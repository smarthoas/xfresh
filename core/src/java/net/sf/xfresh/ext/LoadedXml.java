package net.sf.xfresh.ext;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.apache.xpath.NodeSet;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

/**
 * Date: Nov 29, 2010
 * Time: 1:59:13 AM
 *
 * @author Nikolay Malevanny nmalevanny@yandex-team.ru
 */
public class LoadedXml {
    private static final Logger log = Logger.getLogger(LoadedXml.class);

    public static final LoadedXml EMPTY = new LoadedXml(null);

    private final Document document;

    public LoadedXml(final Document document) {
        this.document = document;
    }

    public String evaluateToString(final String expression, final String defaultValue) {
        if (document==null) {
            log.warn("Can't use expression [" + expression + "], document is null");
            return defaultValue;
        }

        try {
            final XPath xpath = XPathFactory.newInstance().newXPath();
            final String result = (String) xpath.evaluate(expression, document, XPathConstants.STRING);
            log.debug("xpath [" + expression +
                    "] evaluate result = " + result);
            if (StringUtils.isEmpty(result)) {
                return defaultValue;
            }
            return result;
        } catch (XPathExpressionException e) {
            log.error("Error while execute expression: " + expression, e); //ignored
            return defaultValue;
        }
    }
}
