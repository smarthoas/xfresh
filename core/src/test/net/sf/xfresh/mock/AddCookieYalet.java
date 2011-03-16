package net.sf.xfresh.mock;

import net.sf.xfresh.core.InternalRequest;
import net.sf.xfresh.core.InternalResponse;
import net.sf.xfresh.core.Yalet;

/**
 * User: Vladislav Dolbilov (darl@yandex-team.ru)
 * Date: 3/16/11 3:20 PM
 */
public class AddCookieYalet implements Yalet {
    public void process(InternalRequest req, InternalResponse res) {
        res.addCookie("key", "value");
        res.addCookie("key", "value", 15);
        res.addCookie("key", "value", 16, ".domain.com");
        res.addCookie("key", "value", 16, ".domain.com", "/test.html");
        res.addCookie("key", "value", 16, ".domain.com", "/test.html", true);
    }
}
