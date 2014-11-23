package net.sf.xfresh.server;

import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.handler.AbstractHandler;
import org.springframework.beans.factory.annotation.Required;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Author: Olga Bolshakova (obolshakova@yandex-team.ru)
 * Date: 02.01.11 15:07
 */
public class UrlRewriteHandler extends AbstractHandler {

    private Map<Pattern, String> pathPatternToPage = new HashMap<Pattern, String>();

    private Handler handler;

    public void setPathPatternToPage(final Map<String, String> pathPatternToPage) {
        for (final Map.Entry<String, String> entry : pathPatternToPage.entrySet()){
            final Pattern pattern = Pattern.compile(entry.getKey());
            this.pathPatternToPage.put(pattern, entry.getValue());
        }
    }

    @Required
    public void setHandler(final Handler handler) {
        this.handler = handler;
    }

    public void handle(final String target,
                       final Request baseRequest,
                       final HttpServletRequest req,
                       final HttpServletResponse res) throws IOException, ServletException {
        for (final Map.Entry<Pattern, String> entry : pathPatternToPage.entrySet()){
            final Pattern pattern = entry.getKey();
            final String page = entry.getValue();
            final Matcher matcher = pattern.matcher(target);
            if (matcher.matches()) {
                handler.handle(page, baseRequest, req, res);
                return;
            }
        }
        handler.handle(target, baseRequest, req, res);
    }
}
