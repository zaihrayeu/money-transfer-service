package com.mycompany.server.model;

/**
 * enum for money transfer request statuses
 */
public enum MoneyTransferRequestStatus {
    PENDING, // still to be processed
    RETURNED, // returned - the receiving client's account is not active
    COMPLETED // successful completion of the money transfer
}
