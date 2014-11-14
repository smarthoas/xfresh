package net.sf.xfresh.db;

import net.sf.xfresh.core.InternalRequest;
import net.sf.xfresh.core.InternalResponse;
import org.springframework.beans.factory.annotation.Required;

/**
 * Date: Nov 9, 2010
 * Time: 11:04:13 AM
 *
 * @author Nikolay Malevanny nmalevanny@yandex-team.ru
 */
public class QueryYalet extends AbstractDbYalet {
    protected String query;

    @Required
    public void setQuery(final String query) {
        this.query = query;
    }

    @Override
    public void process(final InternalRequest req, final InternalResponse res) {
        res.add(jdbcTemplate.query(query, new RecordMapper()));
    }
}
