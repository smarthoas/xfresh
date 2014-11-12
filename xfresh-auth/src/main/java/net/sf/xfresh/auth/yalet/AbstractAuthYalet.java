package net.sf.xfresh.auth.yalet;

import net.sf.xfresh.auth.AuthService;
import net.sf.xfresh.auth.UserInfo;
import net.sf.xfresh.core.Yalet;
import net.sf.xfresh.core.xml.Xmler;
import static net.sf.xfresh.core.xml.Xmler.tag;
import org.springframework.beans.factory.annotation.Required;

/**
 * Author: Olga Bolshakova (obolshakova@yandex-team.ru)
 * Date: 02.01.11 18:52
 */
abstract class AbstractAuthYalet implements Yalet {

    protected AuthService authService;

    @Required
    public void setAuthService(final AuthService authService) {
        this.authService = authService;
    }

    protected static Xmler.Tag getUserTag(final UserInfo userInfo) {
        return tag("user-info",
                tag("uid", Long.toString(userInfo.getUserId())),
                tag("login", userInfo.getLogin()),
                tag("fio", userInfo.getFio())
        );
    }
}
