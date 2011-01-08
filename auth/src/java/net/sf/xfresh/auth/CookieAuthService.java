package net.sf.xfresh.auth;

import net.sf.xfresh.core.InternalRequest;
import net.sf.xfresh.core.InternalResponse;
import net.sf.xfresh.util.DigestUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Required;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

/**
 * Author: Olga Bolshakova (obolshakova@yandex-team.ru)
 * Date: 02.01.11 18:31
 */
public class CookieAuthService implements AuthService {
    private static final Logger log = Logger.getLogger(CookieAuthService.class);

    private UserService userService;

    private String cookieName = "xfreshauth";

    @Required
    public void setUserService(final UserService userService) {
        this.userService = userService;
    }

    public void setCookieName(final String cookieName) {
        this.cookieName = cookieName;
    }

    public UserInfo doAuth(final String login, final String passwd, final InternalResponse res) {
        final UserInfo user = userService.getUser(login);
        if (user == null) {
            res.removeCookie(cookieName);
            return null;
        }
        final String passwdAdd = user.getPasswdAdd();

        final String hash = calculateHash(passwd, passwdAdd);
        if (!user.getPasswdHash().equals(hash)) {
            res.removeCookie(cookieName);
            return null;
        }

        setCookie(user.getUserId(), hash, res);
        return user;
    }

    private void setCookie(final long userId, final String value, final InternalResponse res) {
        final Map<String, String> cookies = new HashMap<String, String>();
        cookies.put(cookieName, userId + "|" + value);
        res.setCookies(cookies);
    }

    private String calculateHash(final String passwd, final String passwdAdd) {
        return DigestUtils.md5String(DigestUtils.md5String(passwd) + passwdAdd);
    }

    public UserInfo addUser(final String login, final String fio, final String passwd) {
        final String passwdAdd = generatePasswdAdd();

        final String hash = calculateHash(passwd, passwdAdd);

        final long userId = userService.addUser(login, fio, hash, passwdAdd);
        return new UserInfo(userId, login, fio, hash, passwdAdd);
    }

    private String generatePasswdAdd() {
        final Random random = new Random();
        return Integer.toString(random.nextInt(100));
    }

    public UserInfo getUser(final InternalRequest req) {
        final String hash = req.getCookies().get(cookieName);
        if (hash == null) {
            log.info("no auth cookie for ip " + req.getRemoteAddr());
            return null;
        }
        final String[] cookie = hash.split("\\|");
        if (cookie.length != 2) {
            log.info("bad cookie for ip " + req.getRemoteAddr() + " => {" + hash + "}");
            return null;
        }
        final long userId = Long.parseLong(cookie[0]);
        if (userService.checkUser(userId, cookie[1])) {
            return userService.getUser(userId);
        }

        log.info("bad cookie owner for " + req.getRemoteAddr() + " => {" + hash + "} user-id=" + cookie[0] + " hash=" + cookie[1]);
        return null;
    }
}
