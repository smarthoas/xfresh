package net.sf.xfresh.ext;

import net.sf.xfresh.core.*;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.log4j.Logger;
import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import java.io.IOException;
import java.io.InputStream;

/**
 * Date: Nov 24, 2010
 * Time: 11:13:37 PM
 *
 * @author Nikolay Malevanny nmalevanny@yandex-team.ru
 */
public class ExtYaletFilter extends YaletFilter {
    private static final Logger log = Logger.getLogger(ExtYaletFilter.class);

    private static final String XFRESH_EXT_URI = "http://xfresh.sf.net/ext";
    private static final String HTTP_ELEMENT = "http";
    private static final String JS_ELEMENT = "js";
    private HttpClient httpClient;
    private String httpUrl;

    public ExtYaletFilter(final YaletResolver yaletResolver,
                          final SaxGenerator saxGenerator,
                          final InternalRequest request,
                          final InternalResponse response) {
        super(yaletResolver, saxGenerator, request, response);

        httpClient = new DefaultHttpClient();
    }

    @Override
    public void startElement(final String uri,
                             final String localName,
                             final String qName,
                             final Attributes atts) throws SAXException {
        if (isHttpBlock(uri, localName)) {
            httpUrl = atts.getValue("url");
        } else if (isJsBlock(uri, localName)) {
            // todo
        } else {
            super.startElement(uri, localName, qName, atts);
        }
    }

    @Override
    public void endElement(final String uri, final String localName, final String qName) throws SAXException {
        if (isHttpBlock(uri, localName)) {
            if (checkHttpUrl()) {
                processHttpBlock();
            }
            httpUrl = null;
        } else if (isJsBlock(uri, localName)) {
            // todo
        } else {
            super.endElement(uri, localName, qName);
        }
    }

    private void processHttpBlock() {
        try {
            final HttpResponse httpResponse = httpClient.execute(new HttpGet(httpUrl));
            final InputStream content = httpResponse.getEntity().getContent();
            final SAXParserFactory parserFactory = SAXParserFactory.newInstance();
            parserFactory.setXIncludeAware(true);
            final SAXParser saxParser = parserFactory.newSAXParser();
            final XMLReader xmlReader = saxParser.getXMLReader();
            xmlReader.setFeature("http://xml.org/sax/features/namespaces", true);
            xmlReader.setContentHandler(getContentHandler());
            xmlReader.parse(new InputSource(content));
        } catch (IOException e) {
            log.error("ERROR", e); //ignored
        } catch (ParserConfigurationException e) {
            log.error("ERROR", e); //ignored
        } catch (SAXException e) {
            log.error("ERROR", e); //ignored
        }
    }

    private boolean checkHttpUrl() {
        return httpUrl != null && httpUrl.startsWith("http://");
    }

    private boolean isHttpBlock(final String uri, final String localName) {
        return XFRESH_EXT_URI.equalsIgnoreCase(uri) && HTTP_ELEMENT.equalsIgnoreCase(localName);
    }

    private boolean isJsBlock(final String uri, final String localName) {
        return XFRESH_EXT_URI.equalsIgnoreCase(uri) && JS_ELEMENT.equalsIgnoreCase(localName);
    }
}
