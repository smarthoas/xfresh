package net.sf.xfresh.core;

import java.io.IOException;
import java.io.StringWriter;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import junit.framework.TestCase;

import org.apache.commons.lang.StringUtils;
import org.xml.sax.SAXException;

import com.sun.org.apache.xml.internal.serialize.OutputFormat;
import com.sun.org.apache.xml.internal.serialize.XMLSerializer;

/**
 * Date: 22.04.2007
 * Time: 14:36:20
 *
 * @author Nikolay Malevanny nmalevanny@yandex-team.ru
 */
public class DefaultSAXGeneratorTest extends TestCase {
    private static final OutputFormat DEFAULT_FORMAT = new OutputFormat("XML", "windows-1251", false);

    private XMLSerializer serializer;
    private StringWriter stringWriter;
    private SAXGenerator generator;

    protected void setUp() throws Exception {
        super.setUp();
        stringWriter = new StringWriter();
        serializer = new XMLSerializer(stringWriter, DEFAULT_FORMAT);
        generator = new DefaultSAXGenerator();
    }

    public void testCollection() throws Exception {
        doWrite(Arrays.asList("test1", "test2"));
        checkResult("<collection><string>test1</string><string>test2</string></collection>");
    }

    public void testMap() throws Exception {
        final Map map = new HashMap();
        map.put("k1","v1");
        map.put("k2","v2");
        doWrite(map);
        checkResult("<map>" + "<k1>v1</k1><k2>v2</k2>" + "</map>");
    }

    public void testSimpleObject() throws Exception {
        doWrite(new A("test", 1));
        checkResult("<a c=\"1\"><b>test</b></a>");
    }

    public void testSimpleObjectWithNull() throws Exception {
        doWrite(new A(null, 1));
        checkResult("<a c=\"1\"><b/></a>");
    }

    private static class A {
        private final String b;
        private final int c;

        public A(final String b, final int c) {
            this.b = b;
            this.c = c;
        }

        public String getB() {
            return b;
        }

        public int getC() {
            return c;
        }
    }

    private void checkResult(final String expectedResult) {
        String s = StringUtils.removeStart(
                stringWriter.getBuffer().toString(),
                "<?xml version=\"1.0\" encoding=\"windows-1251\"?>\n");
        assertEquals(expectedResult, s);
    }

    private void doWrite(final Object dataItem) throws SAXException, IOException {
    	Map<Object, String> data = new HashMap<Object, String>();
    	for (Object item : Arrays.asList(dataItem)) {
    		data.put(item, null);
    	}
        generator.writeXml(serializer.asContentHandler(), data);
        stringWriter.close();
    }
}
