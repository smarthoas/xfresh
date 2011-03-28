package net.sf.xfresh.core;

import net.sf.xfresh.util.XmlUtil;
import org.xml.sax.SAXException;

import java.util.Map;

/**
 * User: darl (darl@yandex-team.ru)
 * Date: 3/21/11 8:37 PM
 */
public class MapSelfSaxWriter<K, V> implements SelfSaxWriter {
    private final Map<K, V> map;
    private final SaxWriter<Map.Entry<K, V>> writer;

    public MapSelfSaxWriter(Map<K, V> map, SaxWriter<Map.Entry<K, V>> writer) {
        this.map = map;
        this.writer = writer;
    }

    public void writeTo(String externalName, SaxHandler saxHandler) throws SAXException {
        XmlUtil.start(saxHandler.getContentHandler(), externalName);
        for (Map.Entry<K, V> entry : map.entrySet()) {
            writer.write(entry, saxHandler);
        }
        XmlUtil.end(saxHandler.getContentHandler(), externalName);
    }
}
