package com.mycompany.server.service;

import com.mycompany.server.MoneyTransferManager;
import com.mycompany.server.NotEnoughFundException;
import com.mycompany.server.WrongAmountException;
import com.mycompany.server.model.Client;
import com.mycompany.server.model.MoneyTransferRequestStatus;
import com.mycompany.server.repository.ClientRepository;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;

/**
 * service class that implements server APIs
 */
@Path("/")
public class MoneyTransferService {
    private final static String FROM_CLIENT_ID_KEY = "fromClientId";
    private final static String TO_CLIENT_ID_KEY = "toClientId";
    private final static String AMOUNT_KEY = "amount";

    /**
     * returns client balance by client ID; returns 404 error if the client does not exist
     */
    @GET
    @Path("/balance/{clientId}")
    public Response getClientBalance(@PathParam("clientId") Long clientId) {
        Client clientByID = ClientRepository.getClientByID(clientId);
        if (clientByID != null) {
            return Response.status(Response.Status.OK).entity(clientByID.getBalance()).build();
        } else {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity("client with ID " + clientId + " not found").build();
        }
    }

    /**
     * returns client name by client ID; returns 404 error if the client does not exist
     */
    @GET
    @Path("/name/{clientId}")
    public Response getClientName(@PathParam("clientId") Long clientId) {
        Client clientByID = ClientRepository.getClientByID(clientId);
        if (clientByID != null) {
            return Response.status(Response.Status.OK).entity(clientByID.getName()).build();
        } else {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity("client with ID " + clientId + " not found").build();
        }
    }

    /**
     * handles money transfer POST requests.
     *
     * @param params map with params, fromClientId, toClientId, etc
     * @return the money transfer request ID or an error code - client not found, wrong transfer amount, not enough funds
     */
    @POST
    @Path("/request-money-transfer")
    public Response requestMoneyTransfer(final MultivaluedMap<String, String> params) {
        long fromClientId = Long.valueOf(params.get(FROM_CLIENT_ID_KEY).get(0));
        long toClientId = Long.valueOf(params.get(TO_CLIENT_ID_KEY).get(0));
        long amount = Long.valueOf(params.get(AMOUNT_KEY).get(0));
        Client fromClient = ClientRepository.getClientByID(fromClientId);
        Client toClient = ClientRepository.getClientByID(toClientId);
        if (fromClient == null || toClient == null) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity("Sending or receiving client not found").build();
        }
        try {
            Long requestId = MoneyTransferManager.requestMoneyTransfer(fromClient, toClient, amount);
            return Response.status(Response.Status.OK).entity(requestId).build();
        } catch (WrongAmountException e) {
            return Response.status(Response.Status.BAD_REQUEST).entity("Wrong money transfer amount requested").build();
        } catch (NotEnoughFundException e) {
            return Response.status(Response.Status.BAD_REQUEST).entity("Not enough funds to make a money transfer").build();
        }
    }

    /**
     * returns the money transfer request status given a request ID or an error code if there is no a request with such ID
     *
     * @param moneyTransferRequestId money transfer request ID
     * @return the money transfer request status
     */
    @GET
    @Path("/money-transfer-status/{moneyTransferRequestId}")
    public Response getMoneyTransferStatus(@PathParam("moneyTransferRequestId") Long moneyTransferRequestId) {
        MoneyTransferRequestStatus status = MoneyTransferManager.getMoneyTransferRequestStatus(moneyTransferRequestId);
        if (status != null) {
            return Response.status(Response.Status.OK).entity(status.name()).build();
        } else {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity("Money transfer request not found").build();
        }
    }
}
