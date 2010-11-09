package net.sf.xfresh.core;

import org.apache.log4j.Logger;
import org.apache.xalan.xsltc.trax.SmartTransformerFactoryImpl;
import org.apache.xml.serialize.OutputFormat;
import org.apache.xml.serialize.XMLSerializer;
import org.springframework.beans.factory.annotation.Required;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLFilter;
import org.xml.sax.XMLReader;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.sax.SAXSource;
import javax.xml.transform.sax.SAXTransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import java.io.CharArrayWriter;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.io.Writer;

/**
 * Date: Nov 5, 2010
 * Time: 5:56:23 PM
 *
 * @author Nikolay Malevanny nmalevanny@yandex-team.ru
 */
public class YaletProcessor {
    private static final Logger log = Logger.getLogger(YaletProcessor.class);

    private static final String DEFAULT_ENCODING = "windows-1251";
    private static final OutputFormat DEFAULT_FORMAT = new OutputFormat("XML", DEFAULT_ENCODING, false);
    private static final int INITIAL_SIZE = 1024*64;
    private static final String TEXT_HTML = "text/html";
    private static final String TEXT_XML = "text/xml";

    private YaletSupport yaletSupport;
    private String encoding = DEFAULT_ENCODING;

    @Required
    public void setYaletSupport(final YaletSupport yaletSupport) {
        this.yaletSupport = yaletSupport;
    }

    public void setEncoding(final String encoding) {
        this.encoding = encoding;
    }

    public void process(final HttpServletRequest req,
                        final HttpServletResponse res,
                        final String realPath) throws UnsupportedEncodingException, ServletException {
        req.setCharacterEncoding(encoding);
        res.setCharacterEncoding(encoding);

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
            if (transformer != null) {
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
                nativeWriter.close();
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
        parserFactory.setXIncludeAware(true);
        final SAXParser saxParser = parserFactory.newSAXParser();
        final XMLReader xmlReader = saxParser.getXMLReader();
        xmlReader.setFeature("http://xml.org/sax/features/namespaces", true);
        return xmlReader;
    }

    private static class MyXMLSerializer extends XMLSerializer {
        private static final String XML_STYLESHEET_PI = "xml-stylesheet";

        public MyXMLSerializer(final Writer writer) {
            super(writer, DEFAULT_FORMAT);
        }

        public void processingInstructionIO(final String target, final String code) throws IOException {
            if (!XML_STYLESHEET_PI.equalsIgnoreCase(target)) {
                super.processingInstructionIO(target, code);
            }
        }
    }

}
