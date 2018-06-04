package com.mycompany.server.model;

/**
 * represents a client in a bank
 */
public class Client {
    /**
     * the ID of the client
     */
    private long id;
    /**
     * client's name
     */
    private String name;
    /**
     * the current balance of the client
     */
    private long balance;
    /**
     * indicates if the account of this client is active
     */
    private boolean isActive;

    public Client(long id, String name, long balance, boolean isActive) {
        this.id = id;
        this.name = name;
        this.balance = balance;
        this.isActive = isActive;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getBalance() {
        return balance;
    }

    public void setBalance(long balance) {
        this.balance = balance;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }
}
