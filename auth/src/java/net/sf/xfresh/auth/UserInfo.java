package net.sf.xfresh.auth;

/**
 * Author: Olga Bolshakova (obolshakova@yandex-team.ru)
 * Date: 02.01.11 17:24
 */
public class UserInfo {

    private final long userId;
    private final String login;
    private String fio;
    private String passwdHash;
    private String passwdAdd;

    public UserInfo(final long userId, final String login, final String fio) {
        this.userId = userId;
        this.login = login;
        this.fio = fio;
    }

    public UserInfo(final long userId, final String login, final String fio, final String passwdHash, final String passwdAdd) {
        this.userId = userId;
        this.login = login;
        this.fio = fio;
        this.passwdHash = passwdHash;
        this.passwdAdd = passwdAdd;
    }

    public long getUserId() {
        return userId;
    }

    public String getLogin() {
        return login;
    }

    public String getFio() {
        return fio;
    }

    public String getPasswdHash() {
        return passwdHash;
    }

    public String getPasswdAdd() {
        return passwdAdd;
    }
}
