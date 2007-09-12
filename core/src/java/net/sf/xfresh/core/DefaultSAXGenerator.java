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

import static org.apache.commons.lang.ArrayUtils.contains;
import static net.sf.xfresh.util.XmlUtil.empty;
import static net.sf.xfresh.util.XmlUtil.end;
import static net.sf.xfresh.util.XmlUtil.start;
import static net.sf.xfresh.util.XmlUtil.text;
import static net.sf.xfresh.util.XmlUtil.toStandart;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;
import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.AttributesImpl;

/**
 * Date: 21.04.2007
 * Time: 16:06:45
 *
 * @author Nikolay Malevanny nmalevanny@yandex-team.ru
 */
public class DefaultSAXGenerator implements SAXGenerator {
    private static final Logger log = Logger.getLogger(DefaultSAXGenerator.class);

    private static final String COLLECTION_ELEMENT = "collection";
    private static final String MAP_ELEMENT = "map";
    private static final String ENTRY_ELEMENT = "entry";
    private static final String KEY_ELEMENT = "key";
    private static final String VALUE_ELEMENT = "value";

    private static final Class[] PRIMITIVES = new Class[]{
            String.class,
            Long.class,
            Integer.class,
            Double.class,
            Float.class,
            Date.class,
    };

    private static final Class[] ATTRIBUTES = new Class[]{
            Long.class,
            Integer.class,
            Double.class,
            Float.class,
            Date.class,
    };

    private static final String[] STOP_NAMES = new String[]{
            "class",
            "parent",
    };

    private static final Object[] EMPTY_ARGS = null;

    public void writeXml(final ContentHandler handler, final Collection data) throws SAXException {
        for (Object dataItem : data) {
            writeAny(handler, null, dataItem);
        }
    }

    private void writeAny(final ContentHandler handler, final String externalName, final Object dataItem) throws SAXException {
        if (dataItem instanceof SelfWriter) {
            ((SelfWriter) dataItem).writeTo(handler);
        } else if (dataItem instanceof Collection) {
            writeCollection(handler, externalName, (Collection) dataItem);
        } else if (dataItem instanceof Map) {
            writeMap(handler, externalName, (Map) dataItem);
        } else {
            writeItem(handler, externalName, dataItem);
        }
    }

    private void writeCollection(final ContentHandler handler, final String externalName, final Collection collection) throws SAXException {
        String element = (externalName == null) ? COLLECTION_ELEMENT : externalName;
        start(handler, element);
        for (Object dataItem : collection) {
            writeAny(handler, null, dataItem);
        }
        end(handler, element);
    }

    private void writeMap(final ContentHandler handler, final String externalName, final Map map) throws SAXException {
        String element = (externalName == null) ? MAP_ELEMENT : externalName;
        start(handler, element);
        for (Object o : map.entrySet()) {
            Map.Entry entry = (Map.Entry) o;
            start(handler, ENTRY_ELEMENT);
            start(handler, KEY_ELEMENT);
            writeShortly(handler, entry.getKey());
            end(handler, KEY_ELEMENT);
            start(handler, VALUE_ELEMENT);
            writeShortly(handler, entry.getValue());
            end(handler, VALUE_ELEMENT);
            end(handler, ENTRY_ELEMENT);
        }
        end(handler, element);
    }

    private void writeShortly(final ContentHandler handler, final Object dataItem) throws SAXException {
        if (isPrimitive(dataItem.getClass())) {
            text(handler, encode(dataItem.toString()));
        } else {
            writeAny(handler, null, dataItem);
        }
    }

    private void writeItem(final ContentHandler handler, final String externalName, final Object dataItem) throws SAXException {
        if (dataItem == null) {
            empty(handler, externalName);
        } else {
            final String elementName = externalName != null ? externalName : toStandart(dataItem.getClass().getSimpleName());
            if (isPrimitive(dataItem.getClass())) {
                writePrimitive(handler, elementName, dataItem.toString());
            } else {
                final Map<String, ValueInfo> properties = extractProperties(dataItem);
                AttributesImpl attributes = createAttributes(properties);
                start(handler, elementName, attributes);
                writeContent(handler, properties);
                end(handler, elementName);
            }
        }
    }

    private void writeContent(final ContentHandler handler, final Map<String, ValueInfo> properties) throws SAXException {
        for (Map.Entry<String, ValueInfo> property : properties.entrySet()) {
            final ValueInfo valueInfo = property.getValue();
            if (!isAttribute(valueInfo.getClazz())) {
                writeAny(handler, toStandart(property.getKey()), valueInfo.getValue());
            }
        }
    }

    private AttributesImpl createAttributes(final Map<String, ValueInfo> properties) {
        AttributesImpl attributes = new AttributesImpl();
        for (Map.Entry<String, ValueInfo> property : properties.entrySet()) {
            final ValueInfo valueInfo = property.getValue();
            final Class<?> valueClass = valueInfo.getClazz();
            if (isAttribute(valueClass)) {
                final String name = toStandart(property.getKey());
                attributes.addAttribute(null, name, name, null, valueInfo.getValue().toString());
            }
        }
        return attributes;
    }

    private void writePrimitive(final ContentHandler handler, final String xmlName, final String value)
            throws SAXException {
        start(handler, xmlName);
        text(handler, encode(value));
//        text(handler, value);
        end(handler, xmlName);
    }

    public Map<String, ValueInfo> extractProperties(Object dataItem) {
        Map<String, ValueInfo> result = Collections.<String, ValueInfo>emptyMap();
        try {
            final Class<? extends Object> clazz = dataItem.getClass();
            BeanInfo beanInfo = Introspector.getBeanInfo(clazz);
            PropertyDescriptor[] propertyDescriptors = beanInfo.getPropertyDescriptors();

            result = new HashMap<String, ValueInfo>(propertyDescriptors.length);
            for (PropertyDescriptor propertyDescriptor : propertyDescriptors) {
                String name = propertyDescriptor.getName();
                if (!contains(STOP_NAMES, name)) {
                    Method readMethod = propertyDescriptor.getReadMethod();
                    try {
                        final Object value = readMethod.invoke(dataItem, EMPTY_ARGS);
                        if (value != dataItem) {
                            result.put(name, new ValueInfo(propertyDescriptor.getPropertyType(), value));
                        }
                    } catch (IllegalAccessException e) {
                        log.error("Error while reading value of property " + name + " in object " +
                                dataItem, e);// ignored
                    } catch (InvocationTargetException e) {
                        log.error("Error while reading value of property " + name + " in object " +
                                dataItem, e);// ignored
                    }
                }
            }
        } catch (IntrospectionException e) {
            log.error("Error while introspecting object " + dataItem, e);// ignored
        }
        return result;
    }

    private static class ValueInfo {

        private final Class clazz;

        private final Object value;

        public ValueInfo(final Class clazz, final Object value) {
            this.clazz = clazz;
            this.value = value;
        }

        public Class getClazz() {
            return clazz;
        }

        public Object getValue() {
            return value;
        }
    }

    private boolean isPrimitive(final Class<? extends Object> clazz) {
        return contains(PRIMITIVES, clazz);
    }

    private boolean isAttribute(final Class<? extends Object> clazz) {
        return contains(ATTRIBUTES, clazz) || clazz.isPrimitive();
    }

    private String encode(final String value) {
//        String charsetName = "Windows-1251";
//        try {
//            return new String(value.getBytes(charsetName)); // todo kill encoding hack
            return new String(value); // todo kill encoding hack
//        } catch (UnsupportedEncodingException e) {
//            throw new RuntimeException(charsetName + " - unsupported", e);
//        }
    }
}
