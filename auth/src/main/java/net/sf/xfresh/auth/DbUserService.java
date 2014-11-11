package net.sf.xfresh.auth;

import org.springframework.beans.factory.annotation.Required;
import org.springframework.jdbc.core.RowCallbackHandler;
import org.springframework.jdbc.core.simple.ParameterizedRowMapper;
import org.springframework.jdbc.core.simple.SimpleJdbcTemplate;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Author: Olga Bolshakova (obolshakova@yandex-team.ru)
 * Date: 02.01.11 17:22
 */
public class DbUserService implements UserService {

    private SimpleJdbcTemplate jdbcTemplate;

    @Required
    public void setJdbcTemplate(final SimpleJdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public long addUser(final String login, final String fio, final String passwd, final String passwdAdd) throws UserCreationException {

        final UserInfo user = getUser(login);
        if (user != null) {
            throw new UserCreationException(UserCreationException.Type.ALREADY_EXISTS);
        }

        try {
            jdbcTemplate.update(
                    "insert into auth_user (login, fio, passwd_hash, passwd_add) " +
                            "values (?,?,?,?)",
                    login, fio, passwd, passwdAdd
            );
            return getLastInsertId();
        } catch (Exception e) {
            throw new UserCreationException(UserCreationException.Type.INTERNAL_ERROR);
        }
    }

    private Long getLastInsertId() {
        final List<Long> id = new ArrayList<Long>(1);
        jdbcTemplate.getJdbcOperations().query("select last_insert_id()", new RowCallbackHandler() {
            public void processRow(final ResultSet rs) throws SQLException {
                id.add(rs.getLong(1));
            }
        });
        if (id.isEmpty()) {
            throw new IllegalStateException("No inserted id");
        }
        return id.get(0);
    }

    public void updateUserInfo(final UserInfo userInfo) {
        jdbcTemplate.update(
                "update auth_user set fio = ? where user_id = ?",
                userInfo.getFio(), userInfo.getUserId()
        );
    }

    public boolean checkUser(final long userId, final String passwd) {
        return 0 < jdbcTemplate.queryForInt(
                "select count(*) from auth_user where user_id = ? and passwd_hash = ?",
                userId, passwd
        );
    }

    public UserInfo getUser(final long userId) {
        final List<UserInfo> users = jdbcTemplate.query(
                "select * from auth_user where user_id = ?", USER_INFO_MAPPER, userId);
        if (users.isEmpty()) {
            return null;
        }
        return users.get(0);
    }

    public UserInfo getUser(final String login) {
        final List<UserInfo> users = jdbcTemplate.query(
                "select user_id, login, fio, passwd_hash, passwd_add from auth_user where login = ?", USER_INFO_MAPPER, login);
        if (users.isEmpty()) {
            return null;
        }
        return users.get(0);
    }

    private static ParameterizedRowMapper<UserInfo> USER_INFO_MAPPER = new ParameterizedRowMapper<UserInfo>() {
        public UserInfo mapRow(final ResultSet rs, final int i) throws SQLException {
            return new UserInfo(
                    rs.getLong("user_id"),
                    rs.getString("login"),
                    rs.getString("fio"),
                    rs.getString("passwd_hash"),
                    rs.getString("passwd_add")
            );
        }
    };
}
