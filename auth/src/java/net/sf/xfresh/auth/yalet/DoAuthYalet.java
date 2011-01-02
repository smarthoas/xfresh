package net.sf.xfresh.auth.yalet;

import net.sf.xfresh.auth.UserInfo;
import net.sf.xfresh.core.InternalRequest;
import net.sf.xfresh.core.InternalResponse;
import net.sf.xfresh.core.xml.Xmler;

/**
 * Author: Olga Bolshakova (obolshakova@yandex-team.ru)
 * Date: 02.01.11 20:30
 */
public class DoAuthYalet extends AbstractAuthYalet {

    public void process(final InternalRequest req, final InternalResponse res) {
        final String login = req.getParameter("user-login");
        final String passwd = req.getParameter("user-passwd");

        final UserInfo info = authService.doAuth(login, passwd, res);

        if (info == null) {
            res.add(Xmler.tag("failed"));
            return;
        }

        res.add(getUserTag(info));
    }
}
