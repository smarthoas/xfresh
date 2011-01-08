package net.sf.xfresh.ext;

import net.sf.xfresh.core.DefaultYaletSupport;
import net.sf.xfresh.core.InternalRequest;
import net.sf.xfresh.core.InternalResponse;
import org.springframework.beans.factory.annotation.Required;
import org.xml.sax.XMLFilter;

/**
 * Date: Nov 24, 2010
 * Time: 11:14:42 PM
 *
 * @author Nikolay Malevanny nmalevanny@yandex-team.ru
 */
public class ExtYaletSupport extends DefaultYaletSupport {

    private String resourceBase;

    @Required
    public void setResourceBase(final String resourceBase) {
        this.resourceBase = resourceBase;
    }

    @Override
    public XMLFilter createFilter(final InternalRequest request, final InternalResponse response) {
        return new ExtYaletFilter(singleYaletProcessor, authHandler, request, response, resourceBase);
    }
}
