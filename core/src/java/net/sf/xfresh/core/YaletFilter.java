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

import org.xml.sax.helpers.XMLFilterImpl;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.ContentHandler;
import org.apache.log4j.Logger;
import net.sf.xfresh.core.YaletResolver;
import net.sf.xfresh.core.InternalRequest;
import net.sf.xfresh.core.InternalResponse;
import net.sf.xfresh.core.Yalet;
import net.sf.xfresh.util.XmlUtil;

/**
 * Date: 21.04.2007
 * Time: 15:22:05
 *
 * @author Nikolay Malevanny nmalevanny@yandex-team.ru
 */
public class YaletFilter extends XMLFilterImpl {
    private static final Logger log = Logger.getLogger(YaletFilter.class);

    private static final String YALET_ELEMENT = "yalet";
    private static final String DATA_ELEMENT = "data";
    private static final String ERRORS_ELEMENT = "errors";
    private static final String ID_ATTRIBUTE = "id";

    private final YaletResolver yaletResolver;
    private final InternalRequest request;
    private final InternalResponse response;
    private final SAXGenerator saxGenerator;

    private String actionId;
    private boolean doingAction = false;

    public YaletFilter(final YaletResolver yaletResolver,
                       final SAXGenerator saxGenerator,
                       final InternalRequest request,
                       final InternalResponse response) {
        super();
        this.yaletResolver = yaletResolver;
        this.saxGenerator = saxGenerator;
        this.request = request;
        this.response = response;
        actionId = null;
    }

    public void startElement(final String uri, final String localName, final String qName, final Attributes atts) throws SAXException {
        if (!doingAction && YALET_ELEMENT.equalsIgnoreCase(qName)) {
            doingAction = true;
            actionId = atts.getValue(ID_ATTRIBUTE);
        } else {
            super.startElement(uri, localName, qName, atts);
        }
    }

    public void endElement(final String uri, final String localName, final String qName) throws SAXException {
        if (doingAction && YALET_ELEMENT.equalsIgnoreCase(qName)) {
            processYalet(actionId);
            doingAction = false;
        } else {
            super.endElement(uri, localName, qName);
        }
    }

    private void processYalet(final String yaletId) throws SAXException {
        final ContentHandler handler = getContentHandler();
        Yalet yalet = yaletResolver.findYalet(yaletId);
        if (log.isDebugEnabled()) {
            log.debug("Process yalet with id = " + yaletId);
        }
        yalet.process(request, response);
        XmlUtil.start(handler, DATA_ELEMENT, ID_ATTRIBUTE, yaletId);
        saxGenerator.writeXml(handler, response.getData());
        if (!response.getErrors().isEmpty()) {
            XmlUtil.start(handler, ERRORS_ELEMENT, ID_ATTRIBUTE, yaletId);
            saxGenerator.writeXml(handler, response.getErrors());
            XmlUtil.end(handler, ERRORS_ELEMENT);
        }
        XmlUtil.end(handler, DATA_ELEMENT);
    }

}
