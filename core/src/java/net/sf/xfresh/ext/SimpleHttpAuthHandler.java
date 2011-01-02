package net.sf.xfresh.ext;

import net.sf.xfresh.core.InternalRequest;
import net.sf.xfresh.core.InternalResponse;
import org.springframework.beans.factory.annotation.Required;
import org.xml.sax.ContentHandler;
import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import java.io.InputStream;
import java.util.Collections;
import java.util.Map;

/**
 * Author: Olga Bolshakova (obolshakova@yandex-team.ru)
 * Date: 02.01.11 22:07
 */
public class SimpleHttpAuthHandler implements AuthHandler {

    private final HttpLoader httpLoader = new HttpLoader();

    private String authUrl;

    @Required
    public void setAuthUrl(final String authUrl) {
        this.authUrl = authUrl;
    }

    public void processAuth(final InternalRequest req, final InternalResponse res, final ContentHandler handler) {
        try {
            final InputStream content = httpLoader.loadWithHeaders(authUrl, 300, collectCookiesHeader(req));
            final SAXParserFactory parserFactory = SAXParserFactory.newInstance();
            parserFactory.setXIncludeAware(true);
            final SAXParser saxParser = parserFactory.newSAXParser();
            final XMLReader xmlReader = saxParser.getXMLReader();
            xmlReader.setFeature("http://xml.org/sax/features/namespaces", true);
            xmlReader.setContentHandler(handler);
            xmlReader.parse(new InputSource(content));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private Map<String, String> collectCookiesHeader(final InternalRequest req) {
        final StringBuilder sb = new StringBuilder();
        for (final Map.Entry<String, String> en : req.getCookies().entrySet()) {
            sb.append(en.getKey()).append("=").append(en.getValue()).append(";");
        }

        return Collections.singletonMap("Cookie", sb.substring(0, sb.length() - 1));
    }
}
