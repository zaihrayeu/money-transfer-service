package com.mycompany.client;

/**
 * class to handle money transfer exceptions
 */
public class MoneyTransferException extends Exception{
    public MoneyTransferException(String message) {
        super(message);
    }
}
