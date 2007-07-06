/*
* Copyright (c) 2007, Xfresh Project
* All rights reserved.
*
* Redistribution and use in source and binary forms, with or without
* modification, are permitted provided that the following conditions are met:
*     * Redistributions of source code must retain the above copyright
*       notice, this list of conditions and the following disclaimer.
*     * Redistributions in binary form must reproduce the above copyright
*       notice, this list of conditions and the following disclaimer in the
*       documentation and/or other materials provided with the distribution.
*     * Neither the name of the Xfresh Project nor the
*       names of its contributors may be used to endorse or promote products
*       derived from this software without specific prior written permission.
*
* THIS SOFTWARE IS PROVIDED `AS IS'' AND ANY
* EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
* WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
* DISCLAIMED. IN NO EVENT SHALL Xfresh Project BE LIABLE FOR ANY
* DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
* (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
* LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
* ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
* (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
* SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
*/
package net.sf.xfresh.core;

import java.io.IOException;
import java.io.StringWriter;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
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
    
    public void testObjectWithCollectionOfOblects() throws Exception {
    	List<A> list = new LinkedList<A>();
    	list.add(new A("2", 3));
    	list.add(new A("4", 5));
    	list.add(new A("6", 7));
    	doWrite(new SimpleClassWithCollection<A>(list));
    	checkResult("<simple-class-with-collection><parts><a c=\"3\"><b>2</b></a><a c=\"5\"><b>4</b></a><a c=\"7\"><b>6</b></a></parts><id>10</id></simple-class-with-collection>");
    }
    
    public void testMap() throws Exception {
        final Map<String, String> map = new HashMap<String, String>();
        map.put("k1","v1");
        map.put("k2","v2");
        doWrite(map);
        checkResult("<map>" +
                "<entry><key>k1</key><value>v1</value></entry>" +
                "<entry><key>k2</key><value>v2</value></entry>" +
                "</map>");
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
        generator.writeXml(serializer.asContentHandler(), Arrays.asList(dataItem));
        stringWriter.close();
    }
    
    private static class SimpleClassWithCollection<T> {
    	private String id = "10";
    	private List<T> parts;
    	
    	public SimpleClassWithCollection(List<T> list) {
    		this.parts = list;
    	}

		public String getId() {
			return id;
		}

		public List<T> getParts() {
			return parts;
		}
    }
}
