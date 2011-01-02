package net.sf.xfresh.ext;

import net.sf.xfresh.core.InternalRequest;
import net.sf.xfresh.core.InternalResponse;
import net.sf.xfresh.core.xml.Xmler;
import org.xml.sax.ContentHandler;

/**
 * Author: Olga Bolshakova (obolshakova@yandex-team.ru)
 * Date: 02.01.11 22:04
 */
public class AlwaysNoAuthHandler implements AuthHandler {

    public void processAuth(final InternalRequest req, final InternalResponse res, final ContentHandler handler) {
        Xmler.tag("no-auth").writeTo(handler);
    }
}
