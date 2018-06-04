package com.mycompany.server.service;

import com.mycompany.client.MoneyTransferException;
import com.mycompany.server.MoneyTransferManager;
import com.mycompany.server.MoneyTransferServer;
import com.mycompany.server.model.MoneyTransferRequestStatus;
import org.eclipse.jetty.server.Server;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

import static com.mycompany.client.MoneyTransferServerConnector.*;
import static org.junit.Assert.assertEquals;

public class MoneyTransferServiceTest {
    static private Logger logger = LoggerFactory.getLogger(MoneyTransferServiceTest.class);

    private Server server;
    private MoneyTransferManager moneyTransferManager;

    @Before
    public void init() {
        // money transfer manager
        moneyTransferManager = new MoneyTransferManager();
        moneyTransferManager.start();
        server = new Server(8080);
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
        logger.info("Server started. running tests");
    }

    @Test
    public void getClientNameTest() throws IOException, MoneyTransferException {
        String client1Name = getClientName(1L);
        assertEquals("Wrong returned client name", "Bob", client1Name);
    }

    @Test
    public void getClientBalanceTest() throws IOException, MoneyTransferException {
        Long client1Balance = getClientBalance(1L);
        assertEquals("Wrong returned client balance", new Long(200), client1Balance);
    }

    @Test(expected = MoneyTransferException.class)
    public void requestMoneyTransferTest1() throws IOException, MoneyTransferException {
        // exception because of wrong amount
        requestMoneyTransfer(1, 2, -10);
    }

    @Test(expected = MoneyTransferException.class)
    public void requestMoneyTransferTest2() throws IOException, MoneyTransferException {
        // exception because of insufficient funds
        requestMoneyTransfer(1, 2, 1000);
    }

    @Test
    public void requestMoneyTransferTest3() throws IOException, MoneyTransferException, InterruptedException {
        MoneyTransferRequestStatus moneyTransferRequestStatus = requestMoneyTransferSync(1, 2, 150);
        assertEquals("Wrong exit status for money transfer request", MoneyTransferRequestStatus.COMPLETED, moneyTransferRequestStatus);
    }

    @Test
    public void requestMoneyTransferTest4() throws IOException, MoneyTransferException, InterruptedException {
        MoneyTransferRequestStatus moneyTransferRequestStatus = requestMoneyTransferSync(1, 3, 1);
        assertEquals("Wrong exit status for money transfer request", MoneyTransferRequestStatus.RETURNED, moneyTransferRequestStatus);
    }

    @After
    public void shutdown() {
        moneyTransferManager.setRunning(false);
        try {
            server.stop();
            server.destroy();
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(-1);
        }
    }
}
