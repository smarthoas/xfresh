package net.sf.xfresh.jetty;

import org.apache.log4j.Logger;
import org.eclipse.jetty.server.Connector;
import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.HandlerCollection;
import org.eclipse.jetty.server.NetworkTrafficServerConnector;
import org.eclipse.jetty.util.thread.QueuedThreadPool;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Required;

/**
 * Date: Oct 31, 2010
 * Time: 11:03:03 AM
 *
 * @author Nikolay Malevanny nmalevanny@yandex-team.ru
 */
public class JettyServerInitializer implements InitializingBean {
    private static final Logger log = Logger.getLogger(JettyServerInitializer.class);
    private Server server;
    private int port;
    private int maxThreads;
    private Handler[] handlers;

    @Required
    public void setPort(final int port) {
        this.port = port;
    }

    public void setMaxThreads(final int maxThreads) {
        this.maxThreads = maxThreads;
    }

    @Required
    public void setHandlers(final Handler[] handlers) {
        this.handlers = handlers;
    }

    public void afterPropertiesSet() {
        init();
    }

    public final void init() {
        final long st = System.currentTimeMillis();

        try {
            final QueuedThreadPool threadPool = new QueuedThreadPool();
            threadPool.setMaxThreads(maxThreads);
            server = new Server(threadPool);

            final NetworkTrafficServerConnector connector = new NetworkTrafficServerConnector(server);
            connector.setPort(port);

            final HandlerCollection handlerCollection = new HandlerCollection();
            handlerCollection.setHandlers(handlers);

            server.setConnectors(new Connector[]{connector});
            server.setHandler(handlerCollection);

            beforeStart(server, handlerCollection);

            server.start();
//            server.join();
            log.info("Server started: " + (System.currentTimeMillis() - st) + " ms");
        } catch (Exception e) {
            log.error("Could not initialize server: ", e);
        }
    }

    protected void beforeStart(final Server server, final HandlerCollection handlerCollection) {
        // just to extends
    }

    public int getPort() {
        return port;
    }
}
