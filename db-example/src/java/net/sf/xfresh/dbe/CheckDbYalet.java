package net.sf.xfresh.dbe;

import net.sf.xfresh.core.InternalRequest;
import net.sf.xfresh.core.InternalResponse;
import net.sf.xfresh.db.AbstractDbYalet;
import org.springframework.jdbc.core.RowCallbackHandler;
import org.springframework.jdbc.core.simple.ParameterizedRowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Date: Nov 10, 2010
 * Time: 9:15:15 PM
 *
 * @author Nikolay Malevanny nmalevanny@yandex-team.ru
 */
public class CheckDbYalet extends AbstractDbYalet {
    @Override
    public void process(final InternalRequest req, final InternalResponse res) {
        jdbcTemplate.getJdbcOperations().query("select query, dsc from db_check order by query",
                new RowCallbackHandler() {
                    public void processRow(final ResultSet rs) throws SQLException {
                        res.add(jdbcTemplate.queryForObject(rs.getString("query"),
                                new CheckDbResultMapper(rs.getString("dsc"))));
                    }
                });
    }

    private static class CheckDbResultMapper implements ParameterizedRowMapper<CheckDbResult> {
        private String checkDescription;

        private CheckDbResultMapper(final String checkDescription) {
            this.checkDescription = checkDescription;
        }

        public CheckDbResult mapRow(final ResultSet rs, final int i) throws SQLException {
            return new CheckDbResult(rs.getInt("result"), rs.getString("dsc"), checkDescription);
        }
    }
}
