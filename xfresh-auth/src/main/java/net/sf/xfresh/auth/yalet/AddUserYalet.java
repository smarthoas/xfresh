package net.sf.xfresh.auth.yalet;

import net.sf.xfresh.auth.UserCreationException;
import net.sf.xfresh.auth.UserInfo;
import net.sf.xfresh.core.InternalRequest;
import net.sf.xfresh.core.InternalResponse;
import net.sf.xfresh.core.xml.Xmler;

/**
 * Author: Olga Bolshakova (obolshakova@yandex-team.ru)
 * Date: 02.01.11 20:23
 */
public class AddUserYalet extends AbstractAuthYalet {

    public void process(final InternalRequest req, final InternalResponse res) {
        final String login = req.getParameter("user-login");
        final String fio = req.getParameter("user-fio");
        final String passwd = req.getParameter("user-passwd");

        if (login == null || passwd == null) {
            res.add(Xmler.tag("bad-params"));
            return;
        }

        try {
            final UserInfo userInfo = authService.addUser(login, fio, passwd);
            res.add(getUserTag(userInfo));
        } catch (UserCreationException e) {
            if (e.getType() == UserCreationException.Type.ALREADY_EXISTS) {
                res.add(Xmler.tag("already-exists"));
            }
        }
    }
}
