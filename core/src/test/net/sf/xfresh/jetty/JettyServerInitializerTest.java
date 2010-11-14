package net.sf.xfresh.jetty;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.test.AbstractDependencyInjectionSpringContextTests;

import java.io.*;

import static org.springframework.util.FileCopyUtils.copyToString;

/**
 * Date: Oct 31, 2010
 * Time: 11:18:11 AM
 *
 * @author Nikolay Malevanny nmalevanny@yandex-team.ru
 */
public class JettyServerInitializerTest extends AbstractDependencyInjectionSpringContextTests {
    private HttpClient httpClient;
    private JettyServerInitializer serverInitializer;
    private int port;

    public JettyServerInitializerTest() {
        super();
//        setAutowireMode(AUTOWIRE_BY_NAME);
    }

    @Override
    protected String[] getConfigLocations() {
        return new String[]{
                "classpath:test-app-context.xml"
        };
    }

    @Required
    public void setServerInitializer(final JettyServerInitializer serverInitializer) {
        System.out.println("JettyServerInitializerTest.setServerInitializer");
        this.serverInitializer = serverInitializer;
    }

    @Override
    protected void onSetUp() throws Exception {
        super.onSetUp();
        httpClient = new DefaultHttpClient();
        port = serverInitializer.getPort();
    }

    public void testLoadFile() throws Throwable {
        final HttpResponse response = httpClient.execute(buildRequest("test.png"));
        assertEquals(200, response.getStatusLine().getStatusCode());
    }

    public void testProcessXml() throws Throwable {
        final HttpResponse response = httpClient.execute(buildRequest("test.xml"));
        assertEquals(200, response.getStatusLine().getStatusCode());
        final String content = copyToString(new InputStreamReader(response.getEntity().getContent()));
        assertEquals("<html><head><META http-equiv=\"Content-Type\" content=\"text/html; charset=utf-8\"><title>Тест</title></head><body><h1>Проверка111</h1></body></html>", content);
    }

    public void testRemoteAddr() throws Throwable {
        final HttpResponse response = httpClient.execute(buildRequest("test-remote-addr.xml?_ox"));
        assertEquals(200, response.getStatusLine().getStatusCode());
        final String content = copyToString(new InputStreamReader(response.getEntity().getContent()));
        assertEquals("<?xml version=\"1.0\" encoding=\"utf-8\"?>\n" +
                "<page>\n" +
                "    <data id=\"remoteAddr\"><string>127.0.0.1</string></data>\n" +
                "    <data id=\"remoteAddr\"><string>127.0.0.1</string></data>\n" +
                "</page>", content);
    }

    private HttpGet buildRequest(final String name) {
        final HttpGet httpGet = new HttpGet("http://localhost:" 
                + port + "/" +
                name);
        return httpGet;
    }
}
