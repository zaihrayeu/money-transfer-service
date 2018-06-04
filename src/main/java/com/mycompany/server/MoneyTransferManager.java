package com.mycompany.server;

import com.mycompany.server.model.Client;
import com.mycompany.server.model.MoneyTransferRequest;
import com.mycompany.server.model.MoneyTransferRequestStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * class that manages money transfer requests. it has a FIFO queue to keep pending requests. it also has a continuously
 * running money transfer request processor that checks for new requests in the queue and process them one by one of
 * the queue is not empty.
 */
public class MoneyTransferManager extends Thread {
    static private Logger logger = LoggerFactory.getLogger(MoneyTransferManager.class);
    /**
     * indicates of the money transfer request processor should be running (it is used to shut down the service)
     */
    private boolean isRunning = true;
    /**
     * the ID of the next money transfer request (incremented on each new request)
     */
    private static long transferRequestId = 0;
    /**
     * a synchronized FIFO queue to keep pending money transaction requests
     */
    private static Queue<MoneyTransferRequest> requestQueue = new ConcurrentLinkedQueue<>();
    /**
     * map from money transfer request IDs to requests. the map keeps all requests (pending, completed, etc)
     */
    private static Map<Long, MoneyTransferRequest> idToMoneyTransferRequest = new HashMap<>();

    /**
     * requests a money transfer from one client to another.
     *
     * @param fromClient client to send the money from
     * @param toClient   client to send the money to
     * @param amount     the amount of money to be sent
     * @return the ID of the money transfer request
     * @throws WrongAmountException   thrown in case if the requested amount is wrong
     * @throws NotEnoughFundException thrown in case if the sending client does not have enough funds
     */
    public static synchronized Long requestMoneyTransfer(Client fromClient, Client toClient, long amount)
            throws WrongAmountException, NotEnoughFundException {
        if (amount <= 0) {
            throw new WrongAmountException();
        }
        // we sync on fromClient to avoid concurrent access to its account
        synchronized (fromClient) {
            if (fromClient.getBalance() < amount) {
                throw new NotEnoughFundException();
            }
            fromClient.setBalance(fromClient.getBalance() - amount);
        }
        transferRequestId++;
        MoneyTransferRequest request = new MoneyTransferRequest(transferRequestId, fromClient, toClient, amount);
        requestQueue.add(request);
        idToMoneyTransferRequest.put(transferRequestId, request);

        return transferRequestId;
    }

    /**
     * returns money transfer request status given a request ID or null if there is no request with such ID
     *
     * @param moneyTransferRequestId money transfer request ID
     * @return money transfer request status
     */
    public static MoneyTransferRequestStatus getMoneyTransferRequestStatus(long moneyTransferRequestId) {
        MoneyTransferRequest request = idToMoneyTransferRequest.get(moneyTransferRequestId);
        if (request != null) {
            return request.getStatus();
        } else {
            return null;
        }
    }

    public boolean isRunning() {
        return isRunning;
    }

    public void setRunning(boolean running) {
        isRunning = running;
    }

    /**
     * money transfer request processor that runs continuously (stops when isRunning is set to false)
     * <p>
     * after processing a money transfer request, it's put on sleep for demo purposes
     */
    @Override
    public void run() {
        while (isRunning) {
            if (requestQueue.size() > 0) {
                MoneyTransferRequest request = requestQueue.poll();
                Client fromClient = request.getFromClient();
                Client toClient = request.getToClient();
                logger.info("Processing money transfer request from client " + fromClient.getName()
                        + " to client " + toClient.getName() + ". amount: " + request.getAmount());
                // send the money to the destination client if it is active and return the money otherwise
                if (toClient.isActive()) {
                    // synchronize on toClient to avoid concurrent access to its balance
                    synchronized (toClient) {
                        toClient.setBalance(toClient.getBalance() + request.getAmount());
                    }
                    request.setStatus(MoneyTransferRequestStatus.COMPLETED);
                } else { // we assume that fromClient did not become inactive in the meanwhile
                    // synchronize on fromClient to avoid concurrent access to its balance
                    synchronized (fromClient) {
                        fromClient.setBalance(fromClient.getBalance() + request.getAmount());
                    }
                    request.setStatus(MoneyTransferRequestStatus.RETURNED);
                }
                logger.info("Money transfer request processed.. too tired... now I need to sleep");
            }
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}