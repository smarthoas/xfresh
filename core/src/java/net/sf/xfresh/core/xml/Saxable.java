package net.sf.xfresh.core.xml;

import net.sf.xfresh.core.SelfWriter;
import org.xml.sax.ContentHandler;

/**
 * Author: Alexander Astakhov (alalast@yandex.ru)
 * Date: 02.01.11 1:35
 */
public interface Saxable extends SelfWriter {
    void writeTo(final ContentHandler target);
}
