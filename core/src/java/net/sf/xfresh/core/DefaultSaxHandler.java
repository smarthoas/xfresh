package net.sf.xfresh.core;

import net.sf.xfresh.util.XmlUtil;
import org.apache.log4j.Logger;
import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.AttributesImpl;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;

import static net.sf.xfresh.util.XmlUtil.*;
import static org.apache.commons.lang.ArrayUtils.contains;

/**
 * User: darl (darl@yandex-team.ru)
 * Date: 3/21/11 8:23 PM
 */
public class DefaultSaxHandler implements SaxHandler {
    private static final Logger log = Logger.getLogger(DefaultSaxHandler.class);


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

    private static final String COLLECTION_ELEMENT = "collection";
    private static final String MAP_ELEMENT = "map";
    private static final String ENTRY_ELEMENT = "entry";
    private static final String KEY_ELEMENT = "key";
    private static final String VALUE_ELEMENT = "value";

    private final ContentHandler handler;

    public DefaultSaxHandler(ContentHandler contentHandler) {
        this.handler = contentHandler;
    }


    public void writeAny(final String externalName, final Object dataItem) throws SAXException {
        if (dataItem instanceof SelfWriter) {
            ((SelfWriter) dataItem).writeTo(handler);
        } else if (dataItem instanceof SelfSaxWriter) {
            ((SelfSaxWriter) dataItem).writeTo(externalName, this);
        } else if (dataItem instanceof Collection) {
            writeCollection(externalName, (Collection<?>) dataItem);
        } else if (dataItem instanceof Map) {
            writeMap(externalName, (Map<?, ?>) dataItem);
        } else {
            writeItem(handler, externalName, dataItem);
        }
    }

    public void writeCollection(final String externalName, final Collection<?> collection) throws SAXException {
        String element = (externalName == null) ? COLLECTION_ELEMENT : externalName;
        start(handler, element);
        for (Object dataItem : collection) {
            writeAny(null, dataItem);
        }
        end(handler, element);
    }

    public void writeMap(final String externalName, final Map<?, ?> map) throws SAXException {
        String element = (externalName == null) ? MAP_ELEMENT : externalName;
        start(handler, element);

        for (final Map.Entry<?, ?> entry : map.entrySet()) {
            String entryElement = entry.getKey().toString();

            start(handler, entryElement);
            writeShortly(entry.getValue());
            end(handler, entryElement);
        }

        end(handler, element);
    }

    public ContentHandler getContentHandler() {
        return handler;
    }


    private void writeShortly(final Object dataItem) throws SAXException {
        if (isPrimitive(dataItem.getClass())) {
            text(handler, encode(dataItem.toString()));
        } else {
            writeAny(null, dataItem);
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
                writeContent(properties);
                end(handler, elementName);
            }
        }
    }

    private void writeContent(final Map<String, ValueInfo> properties) throws SAXException {
        for (Map.Entry<String, ValueInfo> property : properties.entrySet()) {
            final ValueInfo valueInfo = property.getValue();
            if (!isAttribute(valueInfo.getClazz())) {
                writeAny(toStandart(property.getKey()), valueInfo.getValue());
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
                attributes.addAttribute("", name, name, XmlUtil.NULL_TYPE, valueInfo.getValue().toString());
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

    //todo private?
    public Map<String, ValueInfo> extractProperties(Object dataItem) {
        Map<String, ValueInfo> result = Collections.<String, ValueInfo>emptyMap();
        try {
            final Class<?> clazz = dataItem.getClass();
            BeanInfo beanInfo = Introspector.getBeanInfo(clazz);
            PropertyDescriptor[] propertyDescriptors = beanInfo.getPropertyDescriptors();

            result = new HashMap<String, ValueInfo>(propertyDescriptors.length);
            for (PropertyDescriptor propertyDescriptor : propertyDescriptors) {
                String name = propertyDescriptor.getName();
                if (!contains(STOP_NAMES, name)) {
                    Method readMethod = propertyDescriptor.getReadMethod();
                    if (readMethod == null) continue;
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

    private boolean isPrimitive(final Class<?> clazz) {
        return contains(PRIMITIVES, clazz);
    }

    private boolean isAttribute(final Class<?> clazz) {
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
