package net.sf.xfresh.auth;

import net.sf.xfresh.core.InternalRequest;
import net.sf.xfresh.core.InternalResponse;

/**
 * Author: Olga Bolshakova (obolshakova@yandex-team.ru)
 * Date: 02.01.11 18:25
 */
public interface AuthService {

    UserInfo doAuth(String login, String passwd, InternalResponse res);

    UserInfo addUser(String login, String fio, String passwd) throws UserCreationException;

    UserInfo getUser(InternalRequest req);

}
