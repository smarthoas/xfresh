package net.sf.xfresh.auth.yalet;

import net.sf.xfresh.auth.UserInfo;
import net.sf.xfresh.core.InternalRequest;
import net.sf.xfresh.core.InternalResponse;
import net.sf.xfresh.core.xml.Xmler;

/**
 * Author: Olga Bolshakova (obolshakova@yandex-team.ru)
 * Date: 02.01.11 18:52
 */
public class UserIdYalet extends AbstractAuthYalet {

    public void process(final InternalRequest req, final InternalResponse res) {
        final UserInfo user = authService.getUser(req);
        res.add(Xmler.tag("user-id", Long.toString(user.getUserId())));
    }
}
