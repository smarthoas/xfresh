package net.sf.xfresh.jetty;

import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractJUnit4SpringContextTests;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * Date: Nov 24, 2010
 * Time: 10:08:49 PM
 *
 * @author Nikolay Malevanny nmalevanny@yandex-team.ru
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations={"classpath:test-app-context.xml"})
public abstract class AbstractJettyTest extends AbstractJUnit4SpringContextTests
{
    protected HttpClient httpClient;
    private JettyServerInitializer serverInitializer;
    private int port;

    public AbstractJettyTest() {
        super();
    }

    @Autowired
    @Required
    public void setServerInitializer(final JettyServerInitializer serverInitializer) {
        System.out.println("JettyServerInitializerTest.setServerInitializer");
        this.serverInitializer = serverInitializer;
    }

    @Before
    public void onSetUp() throws Exception {
        httpClient = new DefaultHttpClient();
        port = serverInitializer.getPort();
    }

    protected HttpGet buildRequest(final String name) {
        final HttpGet httpGet = new HttpGet("http://localhost:"
                + port + "/" +
                name);
        return httpGet;
    }
}
