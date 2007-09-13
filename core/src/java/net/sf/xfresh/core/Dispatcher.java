/*
* Copyright (c) 2007, Xfresh Project
* All rights reserved.
*
* Redistribution and use in source and binary forms, with or without
* modification, are permitted provided that the following conditions are met:
*     * Redistributions of source code must retain the above copyright
*       notice, this list of conditions and the following disclaimer.
*     * Redistributions in binary form must reproduce the above copyright
*       notice, this list of conditions and the following disclaimer in the
*       documentation and/or other materials provided with the distribution.
*     * Neither the name of the Xfresh Project nor the
*       names of its contributors may be used to endorse or promote products
*       derived from this software without specific prior written permission.
*
* THIS SOFTWARE IS PROVIDED `AS IS'' AND ANY
* EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
* WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
* DISCLAIMED. IN NO EVENT SHALL Xfresh Project BE LIABLE FOR ANY
* DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
* (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
* LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
* ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
* (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
* SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
*/
package net.sf.xfresh.core;

import org.apache.log4j.Logger;
import org.apache.xalan.xsltc.trax.SmartTransformerFactoryImpl;
import org.apache.xml.serialize.OutputFormat;
import org.apache.xml.serialize.XMLSerializer;
import org.springframework.context.ApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLFilter;
import org.xml.sax.XMLReader;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.sax.SAXSource;
import javax.xml.transform.sax.SAXTransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import java.io.CharArrayWriter;
import java.io.IOException;
import java.io.Writer;

/**
 * Date: 18.04.2007
 * Time: 20:15:05
 *
 * @author Nikolay Malevanny nmalevanny@yandex-team.ru
 */
public class Dispatcher extends HttpServlet {
    private static final Logger log = Logger.getLogger(Dispatcher.class);

    private static final String DEFAULT_ENCODING = "windows-1251";
    private static final OutputFormat DEFAULT_FORMAT = new OutputFormat("XML", DEFAULT_ENCODING, false);

    private static final int INITIAL_SIZE = 1024*64;
    private static final String TEXT_HTML = "text/html";
    private static final String TEXT_XML = "text/xml";

    private YaletSupport yaletSupport;
    private static final String UTF_8_ENCODING = "utf-8";

    public void init(final ServletConfig servletConfig) throws ServletException {
        super.init(servletConfig);
        final ApplicationContext applicationContext = WebApplicationContextUtils.getWebApplicationContext(
                getServletContext());
        setApplicationContext(applicationContext);
    }

    protected void setApplicationContext(final ApplicationContext applicationContext) {
        yaletSupport = (YaletSupport) applicationContext.getBean("yaletSupport");
    }

    @Override
    protected final void service(final HttpServletRequest req,
                                 final HttpServletResponse res) throws ServletException, IOException {
        if (yaletSupport == null) {
            throw new IllegalStateException("yaletSupport is null");
        }

        req.setCharacterEncoding(DEFAULT_ENCODING);
        res.setCharacterEncoding(DEFAULT_ENCODING);

        final String servletPath = req.getServletPath();
        final String realPath = getServletContext().getRealPath(servletPath);
        if (log.isDebugEnabled()) {
            log.debug("===Start process user request, realPath = " + realPath);
            log.debug("method = " + req.getMethod());
        }

        final InternalRequest internalRequest = yaletSupport.createRequest(req, realPath);

        final InternalResponse internalResponse = yaletSupport.createResponse(res);

        if (internalRequest.needTransform()) {
            res.setContentType(TEXT_HTML);
        } else {
            res.setContentType(TEXT_XML);
        }

        process(internalRequest, internalResponse, new RedirHandler(res));
    }

    protected void process(final InternalRequest request,
                           final InternalResponse response,
                           final RedirHandler redirHandler) throws ServletException {
        final String realPath = request.getRealPath();
        try {
            final InputSource inputSource = new InputSource(realPath);

            final XMLReader xmlReader = createReader();

            final XMLFilter yaletFilter = yaletSupport.createFilter(request, response);

            yaletFilter.setParent(xmlReader);

            final CharArrayWriter writer = new CharArrayWriter(INITIAL_SIZE);
            Transformer transformer = null;
            if (request.needTransform()) {
                transformer = createTransformer(realPath);
            }
            if (transformer!=null) {
                final SAXSource saxSource = new SAXSource(yaletFilter, inputSource);
                transformer.transform(saxSource, new StreamResult(writer));
            } else {
                final XMLSerializer serializer = new MyXMLSerializer(writer);
                yaletFilter.setContentHandler(serializer);
                yaletFilter.parse(inputSource);
            }
            final String redir = response.getRedir();
            if (redir == null) {
                writer.close();
                final Writer nativeWriter = response.getWriter();
                nativeWriter.write(writer.toCharArray());
                nativeWriter.flush();
            } else {
                redirHandler.doRedirect(redir);
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new ServletException("Can't process file " + realPath, e);
        }
    }

    private Transformer createTransformer(final String realPath) throws TransformerConfigurationException {
        final SAXTransformerFactory transformerFactory = (SAXTransformerFactory) SmartTransformerFactoryImpl.newInstance();
        final StreamSource streamSource = new StreamSource(realPath);
        final Source associatedStylesheet = transformerFactory.getAssociatedStylesheet(streamSource,
                null, null, null);
        if (associatedStylesheet==null) {
            return null;
        }

        final Transformer transformer = transformerFactory.newTransformer(associatedStylesheet);
        if (log.isDebugEnabled()) {
            log.debug("transformer class = " + transformer.getClass());
        }
//        transformer.setOutputProperty(OutputKeys.ENCODING, UTF_8_ENCODING);
        return transformer;
    }

    private XMLReader createReader() throws ParserConfigurationException, SAXException {
        SAXParserFactory parserFactory = SAXParserFactory.newInstance();
        final SAXParser saxParser = parserFactory.newSAXParser();
        final XMLReader xmlReader = saxParser.getXMLReader();
        xmlReader.setFeature("http://xml.org/sax/features/namespaces", true);
        return xmlReader;
    }

    private static class MyXMLSerializer extends XMLSerializer {
        private static final String XML_STYLESHEET_PI = "xml-stylesheet";

        public MyXMLSerializer(final Writer writer) {
            super(writer, Dispatcher.DEFAULT_FORMAT);
        }

        public void processingInstructionIO(final String target, final String code) throws IOException {
            if (!XML_STYLESHEET_PI.equalsIgnoreCase(target)) {
                super.processingInstructionIO(target, code);
            }
        }
    }
}
