package net.sf.xfresh.jetty;

import org.apache.http.HttpResponse;
import org.junit.Test;

import java.io.InputStreamReader;

import static org.junit.Assert.assertEquals;
import static org.springframework.util.FileCopyUtils.copyToString;

/**
 * Date: Oct 31, 2010
 * Time: 11:18:11 AM
 *
 * @author Nikolay Malevanny nmalevanny@yandex-team.ru
 */
public class JettyServerInitializerTest extends AbstractJettyTest {

    public JettyServerInitializerTest() {
        super();
//        setAutowireMode(AUTOWIRE_BY_NAME);
    }

    @Test
    public void testLoadFile() throws Throwable {
        final HttpResponse response = httpClient.execute(buildRequest("test.png"));
        assertEquals(200, response.getStatusLine().getStatusCode());
    }

    @Test
    public void testProcessXml() throws Throwable {
        final HttpResponse response = httpClient.execute(buildRequest("test.xml"));
        assertEquals(200, response.getStatusLine().getStatusCode());
        final String content = copyToString(new InputStreamReader(response.getEntity().getContent()));
        assertEquals("<html><head><meta http-equiv=\"Content-Type\" content=\"text/html; charset=utf-8\"><title>Тест</title></head><body><h1>Проверка111</h1></body></html>".toLowerCase(), content.toLowerCase());
    }

    @Test
    public void testRemoteAddr() throws Throwable {
        final HttpResponse response = httpClient.execute(buildRequest("test-remote-addr.xml?_ox"));
        assertEquals(200, response.getStatusLine().getStatusCode());
        final String content = copyToString(new InputStreamReader(response.getEntity().getContent()));
        assertEquals("<?xml version=\"1.0\" encoding=\"utf-8\"?>\n" +
                "<page>\n" +
                "    <data id=\"remoteAddr\"><string>127.0.0.1</string></data>\n" +
                "</page>", content);
    }

    @Test
    public void testRuString() throws Throwable {
        final HttpResponse response = httpClient.execute(buildRequest("test-ru.xml?_ox"));
        assertEquals(200, response.getStatusLine().getStatusCode());
        final String content = copyToString(new InputStreamReader(response.getEntity().getContent()));
        assertEquals("<?xml version=\"1.0\" encoding=\"utf-8\"?>\n" +
                "<page>" +
                "\n    <data id=\"ruString\"><string>Тест</string></data>" +
                "\n    <data id=\"ruString\"><string>Тест</string></data>" +
                "\n    <data id=\"ruString\"><string>Тест</string></data>" +
                "\n</page>", content);
    }

    @Test
    public void testMultiRuStringTransform() throws Throwable {
        final HttpResponse response = httpClient.execute(buildRequest("test-ru.xml"));
        assertEquals(200, response.getStatusLine().getStatusCode());
        final String content = copyToString(new InputStreamReader(response.getEntity().getContent()));
        assertEquals("<html>ТестТестТест</html>", content);
    }

    @Test
    public void testRedirect() throws Throwable {
        final HttpResponse response = httpClient.execute(buildRequest("test-redirect.xml"));
        assertEquals(200, response.getStatusLine().getStatusCode());
        final String content = copyToString(new InputStreamReader(response.getEntity().getContent()));
        assertEquals("<html>ТестТестТест</html>", content);
    }
}
