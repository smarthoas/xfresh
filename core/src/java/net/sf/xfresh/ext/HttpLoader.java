package net.sf.xfresh.ext;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.io.InputStream;

/**
 * Date: Nov 28, 2010
 * Time: 1:41:37 AM
 *
 * @author Nikolay Malevanny nmalevanny@yandex-team.ru
 */
public class HttpLoader {
    private static final Logger log = Logger.getLogger(HttpLoader.class);

    private static final DocumentBuilderFactory DOCUMENT_BUILDER_FACTORY =
            DocumentBuilderFactory.newInstance();

    private HttpClient httpClient;

    public HttpLoader() {
        httpClient = new DefaultHttpClient();
    }

    public InputStream loadAsStream(final String url, final int timeout) throws IOException {
        // todo use timeout
        final HttpGet httpGet = new HttpGet(url);
        final HttpResponse httpResponse = httpClient.execute(httpGet);
        return httpResponse.getEntity().getContent();
    }

    public LoadedXml load(final String url, final int timeout) {
        try {
            final InputStream stream = loadAsStream(url, timeout);
            final DocumentBuilder builder = DOCUMENT_BUILDER_FACTORY.newDocumentBuilder();
            final Document document = builder.parse(stream);
            return new LoadedXml(document);
        } catch (IOException e) {
            log.error("Error while processing url: " + url, e); //ignored
        } catch (ParserConfigurationException e) {
            log.error("Error while processing url: " + url, e); //ignored
        } catch (SAXException e) {
            log.error("Error while processing url: " + url, e); //ignored
        }
        return LoadedXml.EMPTY_XML;
    }
}

