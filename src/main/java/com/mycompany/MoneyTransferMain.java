package com.mycompany;

import com.mycompany.client.MoneyTransferClient;
import com.mycompany.server.MoneyTransferManager;
import com.mycompany.server.MoneyTransferServer;
import org.eclipse.jetty.server.Server;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * main class that runs MoneyTransferManager, the server, the client with its calls for the demo, shuts down everything
 * at the end
 */
public class MoneyTransferMain {
    static private Logger logger = LoggerFactory.getLogger(MoneyTransferMain.class);

    public static void main(String[] args) {
        logger.info("Money Transfer service is starting..");
        // money transfer manager
        MoneyTransferManager moneyTransferManager = new MoneyTransferManager();
        moneyTransferManager.start();
        // starting the jetty server component
        Server server = new Server(8080);
        MoneyTransferServer moneyTransferServer = new MoneyTransferServer(server);
        moneyTransferServer.start();
        while (!server.isStarted()) {
            logger.info("waiting for the server to start...");
            try {
                Thread.sleep(200);
            } catch (InterruptedException e) {
                e.printStackTrace();
                System.exit(-1);
            }
        }
        logger.info("Server started. Starting the client and running the demo");
        MoneyTransferClient client = new MoneyTransferClient();
        client.run();
        // money transfers done. stopping
        logger.info("Demo finished. Stopping services.");
        logger.info("Stopping money transaction manager");
        moneyTransferManager.setRunning(false);
        logger.info("Stopping the server");
        try {
            server.stop();
            server.destroy();
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(-1);
        }
        logger.info("Thank you for your attention. Hopefully, you liked the demo :))");
    }
}