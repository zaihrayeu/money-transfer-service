package com.mycompany.server;

import com.mycompany.server.service.MoneyTransferService;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;

/**
 * a jetty server that is run to expose the MoneyTransferService APIs
 */
public class MoneyTransferServer extends Thread {
    private Server server;

    public MoneyTransferServer(Server server) {
        this.server = server;
    }

    @Override
    public void run() {
        // jetty server with MoneyTransferService APIs
        ServletContextHandler context = new ServletContextHandler(ServletContextHandler.SESSIONS);
        context.setContextPath("/");
        server.setHandler(context);
        ServletHolder jerseyServlet = context
                .addServlet(org.glassfish.jersey.servlet.ServletContainer.class, "/*");
        jerseyServlet.setInitOrder(0);
        jerseyServlet.setInitParameter("jersey.config.server.provider.classnames",
                MoneyTransferService.class.getCanonicalName());
        try {
            server.start();
            server.join();
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(-1);
        }
    }
}
