package net.sf.xfresh.auth;

/**
 * Author: Olga Bolshakova (obolshakova@yandex-team.ru)
 * Date: 02.01.11 17:26
 */
public interface UserService {

    long addUser(String login, String fio, String passwd, String passwdAdd) throws UserCreationException;

    boolean checkUser(long userId, String passwd);

    void updateUserInfo(UserInfo userInfo);

    UserInfo getUser(long userId);

    UserInfo getUser(String login);

}
