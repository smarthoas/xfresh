package net.sf.xfresh.ext;

import net.sf.xfresh.jetty.AbstractJettyTest;
import org.apache.http.HttpResponse;
import org.junit.Test;

import java.io.InputStreamReader;

import static org.springframework.util.FileCopyUtils.copyToString;
import static org.junit.Assert.assertEquals;


/**
 * Date: Oct 31, 2010
 * Time: 11:18:11 AM
 *
 * @author Nikolay Malevanny nmalevanny@yandex-team.ru
 */
public class HttpBlockTest extends AbstractJettyTest {

    public HttpBlockTest() {
        super();
    }

    @Test
    public void testHttp() throws Throwable {
        final HttpResponse response = httpClient.execute(buildRequest("test-http.xml"));
        assertEquals(200, response.getStatusLine().getStatusCode());
        final String content = copyToString(new InputStreamReader(response.getEntity().getContent()));
        assertEquals(
                "<?xml version=\"1.0\" encoding=\"utf-8\"?>\n" +
                        "<page xmlns:x=\"http://xfresh.sf.net/ext\">\n" +
                        "    <page><a>тест</a><data id=\"addTestInfo\"/></page>\n" +
                        "</page>",
                content);
    }
}

