package net.sf.xfresh.core;

import net.sf.xfresh.core.impl.SimpleInternalRequest;
import net.sf.xfresh.core.impl.SimpleInternalResponse;
import net.sf.xfresh.jetty.AbstractJettyTest;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.util.Assert;

import static org.junit.Assert.assertEquals;
import java.io.ByteArrayOutputStream;

/**
 * Date: 20.04.2007
 * Time: 17:50:41
 *
 * @author Nikolay Malevanny nmalevanny@yandex-team.ru
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations={"classpath:test-app-context.xml"})
public class YaletProcessorTest extends AbstractJettyTest {
    private static final String TEST_CONTENT = "<?xml version=\"1.0\" encoding=\"utf-8\"?>\n<page><a>тест</a><data id=\"addTestInfo\"/></page>";
    private static final String TEST_TRANSFORMED_CONTENT = "<html><head><META http-equiv=\"Content-Type\" content=\"text/html; charset=utf-8\">" +
            "<title>Тест</title></head><body><h1>Проверка111</h1></body></html>";
    private YaletProcessor yaletProcessor;
    private ByteArrayOutputStream baos;
    private SimpleInternalResponse response;
    private SimpleInternalRequest request;

    @Autowired
    @Required
    public void setYaletProcessor(final YaletProcessor yaletProcessor) {
        this.yaletProcessor = yaletProcessor;
    }

    @Before
    public void onSetUp() throws Exception {
        super.onSetUp();

        baos = new ByteArrayOutputStream();
        response = new SimpleInternalResponse(null);
        response.setOutputStream(baos);
    }

    @Test
    public void testProcessFile() throws Exception {
        request = new SimpleInternalRequest(null, "./xfresh-core/src/test/resources/test.xml");
        request.setNeedTransform(false);
        yaletProcessor.process(request, response, new RedirHandler(null));
        assertEquals(TEST_CONTENT,
                new String(baos.toByteArray()).trim());
    }

    @Test
    public void testTransformFile() throws Exception {
        request = new SimpleInternalRequest(null, "./xfresh-core/src/test/resources/test.xml");

        yaletProcessor.process(request, response, new RedirHandler(null));
        assertEquals(TEST_TRANSFORMED_CONTENT.toLowerCase(),
                new String(baos.toByteArray()).trim().toLowerCase());
    }

    @Test
    public void testTransformXFile() throws Exception {
        request = new SimpleInternalRequest(null, "./xfresh-core/src/test/resources/xtest.xml");

        yaletProcessor.process(request, response, new RedirHandler(null));
        assertEquals(TEST_CONTENT,
                new String(baos.toByteArray()).trim());
    }
}
