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
import java.io.Writer;
import java.util.*;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

/**
 * Date: 20.04.2007
 * Time: 18:31:03
 *
 * Reused from different yalets.
 *
 * @author Nikolay Malevanny nmalevanny@yandex-team.ru
 */
class SimpleInternalResponse implements InternalResponse {
    private final HttpServletResponse httpResponse;
    private String redir;
    private List<Object> data = new ArrayList<Object>();
    private List<ErrorInfo> errors = new ArrayList<ErrorInfo>();
    private Map<String, Object> attributes = new HashMap<String, Object>();
    private Writer writer;

    protected SimpleInternalResponse(final HttpServletResponse httpResponse) {
        this.httpResponse = httpResponse;
    }

    public void redirectTo(final String path) {
        if (redir != null) {
        	throw new IllegalStateException("Already redirected");
        }
        redir = path;
    }

    public void add(final Object object) {
        data.add(object);
    }
    
    public final List<Object> getData() {
        return data;
    }

    public final List<ErrorInfo> getErrors() {
        return errors;
    }

    public String getRedir() {
        return redir;
    }

    void setWriter(final Writer writer) {
        this.writer = writer;
    }

    public Writer getWriter() throws IOException {
        if (writer == null && httpResponse != null) {
            writer = httpResponse.getWriter();
        }
        return writer;
    }

    public void addError(final ErrorInfo errorInfo) {
        errors.add(errorInfo);
    }
    
    public void setCookies(Map<String, String> cookies) {
        for (Map.Entry<String, String> cookie : cookies.entrySet()) {
            httpResponse.addCookie(new Cookie(cookie.getKey(), cookie.getValue()));
        }
    }

    public void putAttribute(final String name, final Object value) {
        attributes.put(name, value);
    }

    public Object getAttribute(final String name) {
        return attributes.get(name);
    }

    public void clear() {
        data.clear();
        errors.clear();
    }
}
