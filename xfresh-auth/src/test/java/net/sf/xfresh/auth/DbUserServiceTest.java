package net.sf.xfresh.auth;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.jdbc.core.simple.SimpleJdbcTemplate;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractJUnit4SpringContextTests;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static org.junit.Assert.*;


/**
 * Author: Olga Bolshakova (obolshakova@yandex-team.ru)
 * Date: 02.01.11 18:09
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:test-app-config.xml"})
public class DbUserServiceTest extends AbstractJUnit4SpringContextTests {

    private DbUserService dbUserService;

    private SimpleJdbcTemplate jdbcTemplate;

    @Required
    @Autowired
    public void setDbUserService(final DbUserService dbUserService) {
        this.dbUserService = dbUserService;
    }

    @Required
    @Autowired
    public void setJdbcTemplate(final SimpleJdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Before
    public void makeTable() throws Exception {
        jdbcTemplate.getJdbcOperations().execute("CREATE TABLE IF NOT EXISTS auth_user (\n" +
                "    user_id               INT           AUTO_INCREMENT        PRIMARY KEY\n" +
                "  , login                 VARCHAR(100)\n" +
                "  , fio                   VARCHAR(256)\n" +
                "  , passwd_hash           VARCHAR(256)\n" +
                "  , passwd_add            VARCHAR(256)\n" +
                ")");
    }

    @Test
    public void testExists() {
        assertNotNull(dbUserService);
        assertNotNull(jdbcTemplate);
    }

    @Test
    public void testAddUser() throws Exception {
        final long userId = createUser();
        assertEquals(1, removeUser(userId));
    }

    private long createUser() {
        return dbUserService.addUser("xxx", "x x", "123", "11");
    }

    private int removeUser(final long userId) {
        return jdbcTemplate.update("delete from auth_user where user_id = ?", userId);
    }

    @Test
    public void testGetUser() throws Exception {
        final long userId = createUser();
        final UserInfo userInfo = dbUserService.getUser(userId);
        assertEquals("xxx", userInfo.getLogin());
        assertEquals("x x", userInfo.getFio());
        assertEquals("123", userInfo.getPasswdHash());
        assertEquals("11", userInfo.getPasswdAdd());
        assertEquals(1, removeUser(userId));
    }

    @Test
    public void testCheckUser() throws Exception {
        final long userId = createUser();
        assertTrue(dbUserService.checkUser(userId, "123"));
        assertFalse(dbUserService.checkUser(userId * 2, "11"));
        assertFalse(dbUserService.checkUser(userId * 3, "123"));
        assertFalse(dbUserService.checkUser(userId, null));
        assertFalse(dbUserService.checkUser(userId * 5, null));
        assertEquals(1, removeUser(userId));
    }
}
