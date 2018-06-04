package com.mycompany.server.model;

/**
 * class represents transfer request from one client to another
 */
public class MoneyTransferRequest {
    /**
     * the ID of the money transfer request
     */
    private long id;
    /**
     * client that sends the money
     */
    private Client fromClient;
    /**
     * client that receives the money
     */
    private Client toClient;
    /**
     * the amount to be sent
     */
    private long amount;
    /**
     * status of the request, PENDING by default
     */
    private MoneyTransferRequestStatus status = MoneyTransferRequestStatus.PENDING;

    public MoneyTransferRequest(long id, Client fromClient, Client toClient, long amount) {
        this.id = id;
        this.fromClient = fromClient;
        this.toClient = toClient;
        this.amount = amount;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public Client getFromClient() {
        return fromClient;
    }

    public void setFromClient(Client fromClient) {
        this.fromClient = fromClient;
    }

    public Client getToClient() {
        return toClient;
    }

    public void setToClient(Client toClient) {
        this.toClient = toClient;
    }

    public long getAmount() {
        return amount;
    }

    public void setAmount(long amount) {
        this.amount = amount;
    }

    public MoneyTransferRequestStatus getStatus() {
        return status;
    }

    public void setStatus(MoneyTransferRequestStatus status) {
        this.status = status;
    }
}
