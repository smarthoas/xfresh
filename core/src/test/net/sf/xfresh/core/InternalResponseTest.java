package net.sf.xfresh.core;

import junit.framework.TestCase;

/**
 * Date: 21.04.2007
 * Time: 12:23:01
 *
 * @author Nikolay Malevanny nmalevanny@yandex-team.ru
 */
public class InternalResponseTest extends TestCase {

    public void testDoubleRedir() throws Exception {
        InternalResponse response = new SimpleInternalResponse(null);
        final String path = "test.xml";
        response.redirectTo(path); // ok
        try {
            response.redirectTo(path);
            fail("Expected IlleagalStateException");
        } catch (IllegalStateException e) {
            // ok
        }
    }
}
