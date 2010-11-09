package net.sf.xfresh.db;

import net.sf.xfresh.core.InternalRequest;
import net.sf.xfresh.core.InternalResponse;
import net.sf.xfresh.core.Yalet;
import org.springframework.jdbc.core.simple.SimpleJdbcTemplate;

/**
 * Date: Nov 9, 2010
 * Time: 11:04:13 AM
 *
 * @author Nikolay Malevanny nmalevanny@yandex-team.ru
 */
public class QueryYalet implements Yalet {
    private SimpleJdbcTemplate jdbcTemplate;
    private String query;

    public void setJdbcTemplate(final SimpleJdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public void setQuery(final String query) {
        this.query = query;
    }

    public void process(final InternalRequest req, final InternalResponse res) {
        res.add(jdbcTemplate.query(query, new RecordMapper()));
    }


}
