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

import junit.framework.TestCase;

import java.io.ByteArrayOutputStream;
import java.io.PrintWriter;

import net.sf.xfresh.core.SimpleInternalRequest;
import net.sf.xfresh.core.SimpleInternalResponse;
import net.sf.xfresh.core.Dispatcher;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * Date: 20.04.2007
 * Time: 17:50:41
 *
 * @author Nikolay Malevanny nmalevanny@yandex-team.ru
 */
public class DispatcherTest extends TestCase {
    private static final String TEST_CONTENT = "<?xml version=\"1.0\" encoding=\"windows-1251\"?>\n<page><a>тест</a><data id=\"addTestInfo\"/></page>";
    private static final String TEST_TRANSFORMED_CONTENT = "<h1>Test Data111</h1>";
    private Dispatcher dispatcher;
    private ByteArrayOutputStream baos;
    private SimpleInternalResponse response;
    private SimpleInternalRequest request;

    protected void setUp() throws Exception {
        super.setUp();

        dispatcher = new Dispatcher();
        dispatcher.setApplicationContext(new ClassPathXmlApplicationContext("testApplicationContext.xml"));


        baos = new ByteArrayOutputStream();
        response = new SimpleInternalResponse(null);
        response.setWriter(new PrintWriter(baos));
    }

    public void testProcessFile() throws Exception {
        request = new SimpleInternalRequest(null, "test.xml");
        request.setNeedTransform(false);
        dispatcher.process(request, response, new RedirHandler(null));
        assertEquals(TEST_CONTENT,
                new String(baos.toByteArray()).trim());
    }

    public void testTransformFile() throws Exception {
        request = new SimpleInternalRequest(null, "test.xml");

        dispatcher.process(request, response, new RedirHandler(null));
        assertEquals(TEST_TRANSFORMED_CONTENT,
                new String(baos.toByteArray()).trim());
    }

    public void testTransformXFile() throws Exception {
        request = new SimpleInternalRequest(null, "xtest.xml");

        dispatcher.process(request, response, new RedirHandler(null));
        assertEquals(TEST_CONTENT,
                new String(baos.toByteArray()).trim());
    }
}
