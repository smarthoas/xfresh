package net.sf.xfresh.auth;

import org.springframework.beans.factory.annotation.Required;
import org.springframework.jdbc.core.simple.SimpleJdbcTemplate;
import org.springframework.test.AbstractDependencyInjectionSpringContextTests;

/**
 * Author: Olga Bolshakova (obolshakova@yandex-team.ru)
 * Date: 02.01.11 18:09
 */
public class DbUserServiceTest extends AbstractDependencyInjectionSpringContextTests {
    public DbUserServiceTest() {
        super();
        setAutowireMode(AUTOWIRE_BY_NAME);
    }

    @Override
    protected String[] getConfigLocations() {
        return new String[]{
                "classpath:test-app-config.xml"
        };
    }

    private DbUserService dbUserService;

    private SimpleJdbcTemplate jdbcTemplate;

    @Required
    public void setDbUserService(final DbUserService dbUserService) {
        this.dbUserService = dbUserService;
    }

    @Required
    public void setJdbcTemplate(final SimpleJdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public void testExists() {
        assertNotNull(dbUserService);
        assertNotNull(jdbcTemplate);
    }

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

    public void testGetUser() throws Exception {
        final long userId = createUser();
        final UserInfo userInfo = dbUserService.getUser(userId);
        assertEquals("xxx", userInfo.getLogin());
        assertEquals("x x", userInfo.getFio());
        assertEquals("123", userInfo.getPasswdHash());
        assertEquals("11", userInfo.getPasswdAdd());
        assertEquals(1, removeUser(userId));
    }

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
