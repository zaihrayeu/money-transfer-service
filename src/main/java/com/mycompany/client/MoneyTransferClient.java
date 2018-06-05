package com.mycompany.client;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

import static com.mycompany.client.MoneyTransferServerConnector.requestMoneyTransferSync;

/**
 * class that makes money transfer requests
 */
public class MoneyTransferClient {

    static private Logger logger = LoggerFactory.getLogger(MoneyTransferClient.class);

    public void run() {
        try {
            // print client base and current balances
            String client1Name = MoneyTransferServerConnector.getClientName(1L);
            String client2Name = MoneyTransferServerConnector.getClientName(2L);
            String client3Name = MoneyTransferServerConnector.getClientName(3L);
            Long client1Balance = MoneyTransferServerConnector.getClientBalance(1L);
            Long client2Balance = MoneyTransferServerConnector.getClientBalance(2L);
            Long client3Balance = MoneyTransferServerConnector.getClientBalance(3L);
            logger.info("Client with ID 1: " + client1Name);
            logger.info("Client with ID 2: " + client2Name);
            logger.info("Client with ID 3: " + client3Name);
            logger.info("Current balance of client " + client1Name + ": " + client1Balance);
            logger.info("Current balance of client " + client2Name + ": " + client2Balance);
            logger.info("Current balance of client " + client3Name + ": " + client3Balance);
            logger.info("---------------------------");
            Thread.sleep(1000);

            // transfer money from client 1 to client 2 - transfer ok
            logger.info("Transferring 150 from client " + client1Name + " to client " + client2Name);
            try {
                requestMoneyTransferSync(1, 2, 150);
            } catch (MoneyTransferException e) {
                logger.error("MoneyTransferException: " + e.getMessage());
            }
            logger.info("---------------------------");
            Thread.sleep(1000);

            // transfer money from client 1 to client 2 - not enough funds
            logger.info("Transferring 1500 from client " + client1Name + " to client " + client2Name);
            try {
                requestMoneyTransferSync(1, 2, 1500);
            } catch (MoneyTransferException e) {
                logger.error("MoneyTransferException: " + e.getMessage());
            }
            logger.info("---------------------------");
            Thread.sleep(1000);

            // transfer money from client 1 to client 2 - wrong amount
            logger.info("Transferring -10 from client " + client1Name + " to client " + client2Name);
            try {
                requestMoneyTransferSync(1, 2, -10);
            } catch (MoneyTransferException e) {
                logger.error("MoneyTransferException: " + e.getMessage());
            }
            logger.info("---------------------------");
            Thread.sleep(1000);

            // transfer money from client 2 to client 3 (inactive) - money returned
            logger.info("Transferring 30 from client " + client2Name + " to client " + client3Name);
            try {
                requestMoneyTransferSync(2, 3, 30);
            } catch (MoneyTransferException e) {
                logger.error("MoneyTransferException: " + e.getMessage());
            }
            // transfer money from client 3 to a non existing client - 404 error
            logger.info("Transferring 40 from client " + client3Name + " to a non existent client");
            try {
                requestMoneyTransferSync(3, 5, 40);
            } catch (MoneyTransferException e) {
                logger.error("MoneyTransferException: " + e.getMessage());
            }
        } catch (IOException | InterruptedException | MoneyTransferException e) {
            e.printStackTrace();
        }
    }
}
