package com.mycompany.client;

import com.mycompany.server.model.MoneyTransferRequestStatus;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.core.Response;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * utility class that connects to the server and calls its API methods to get clients' balances, make
 * money transfer requests, etc
 */
public class MoneyTransferServerConnector {

    static private Logger logger = LoggerFactory.getLogger(MoneyTransferServerConnector.class);

    private static final String BASE_URL = "http://localhost:8080/";
    private static final String BALANCE_URL_PREFIX = BASE_URL + "balance/";
    private static final String NAME_URL_PREFIX = BASE_URL + "name/";
    private static final String MONEY_TRANSFER_REQUEST_STATUS_PREFIX = BASE_URL + "money-transfer-status/";
    private static final String REQUEST_MONEY_TRANSFER_URL = BASE_URL + "request-money-transfer";

    /**
     * returns the client name by client ID
     *
     * @param clientID client ID
     * @return client name
     */
    public static String getClientName(long clientID) throws IOException, MoneyTransferException {
        return makeGetRequest(NAME_URL_PREFIX + clientID);
    }

    /**
     * returns the balance of the client by client ID
     *
     * @param clientID client ID
     * @return current balance
     */
    public static Long getClientBalance(long clientID) throws IOException, MoneyTransferException {
        return Long.valueOf(makeGetRequest(BALANCE_URL_PREFIX + clientID));
    }

    /**
     * makes a money transfer request
     *
     * @param fromClientId the id of the client that sends money
     * @param toClientId   the id of the client that received money
     * @param amount       the amount of money to be sent
     * @return the id of the money transfer request
     */
    public static Long requestMoneyTransfer(long fromClientId, long toClientId, long amount) throws IOException, MoneyTransferException {
        Map<String, String> postData = new HashMap<>();
        postData.put("fromClientId", "" + fromClientId);
        postData.put("toClientId", "" + toClientId);
        postData.put("amount", "" + amount);

        return Long.valueOf(makePostRequest(REQUEST_MONEY_TRANSFER_URL, postData));
    }

    /**
     * checks the status of the money transfer request given a request ID
     *
     * @param moneyTransferRequestId the money transfer request ID
     * @return the status of the request
     */
    public static MoneyTransferRequestStatus getMoneyTransferStatus(long moneyTransferRequestId) throws IOException, MoneyTransferException {
        return MoneyTransferRequestStatus.valueOf(makeGetRequest(MONEY_TRANSFER_REQUEST_STATUS_PREFIX + moneyTransferRequestId));
    }

    /**
     * utility method that makes a money transfer request and waits until the request status changes from PENDING to
     * some exit status (e.g., COMPLETED)
     *
     * @param fromClientId the id of the client that sends money
     * @param toClientId   the id of the client that received money
     * @param amount       the amount of money to be sent
     * @return the exit status for this money transfer request
     */
    public static MoneyTransferRequestStatus requestMoneyTransferSync(long fromClientId, long toClientId, long amount) throws InterruptedException, IOException, MoneyTransferException {
        Long moneyTransferRequestId = MoneyTransferServerConnector.requestMoneyTransfer(fromClientId, toClientId, amount);
        logger.info("Money transfer request ID: " + moneyTransferRequestId);
        logger.info("Checking money transfer status...");
        MoneyTransferRequestStatus moneyTransferStatus = MoneyTransferServerConnector.getMoneyTransferStatus(moneyTransferRequestId);
        do {
            logger.info("Status: " + moneyTransferStatus);
            Thread.sleep(200);
            moneyTransferStatus = MoneyTransferServerConnector.getMoneyTransferStatus(moneyTransferRequestId);
        } while (moneyTransferStatus.equals(MoneyTransferRequestStatus.PENDING));
        logger.info("Exit Status: " + moneyTransferStatus);
        logger.info("Current balance of the sender client: " + MoneyTransferServerConnector.getClientBalance(fromClientId));
        logger.info("Current balance of the receiver client: " + MoneyTransferServerConnector.getClientBalance(toClientId));

        return moneyTransferStatus;
    }


    /**
     * utility method to execute a GET request. for the sake of simplicity we assume the response is a single string
     *
     * @param url URL for the GET request
     * @return string returned from the endpoint
     */
    private static String makeGetRequest(String url) throws IOException, MoneyTransferException {
        // prepare http client
        HttpClient client = new HttpClient();
        // prepare get method
        GetMethod get = new GetMethod(url);
        client.executeMethod(get);
        if (get.getStatusCode() != Response.Status.OK.getStatusCode()) {
            throw new MoneyTransferException(get.getResponseBodyAsString());
        }

        return get.getResponseBodyAsString();
    }

    /**
     * utility method to execute a POST request. for the sake of simplicity we assume the response is a single string
     *
     * @param url    URL for the POST call
     * @param params a key-value map for the parameters to be sent in the POST request
     * @return string returned from the endpoint
     */
    private static String makePostRequest(String url, Map<String, String> params) throws IOException, MoneyTransferException {
        // prepare http client
        HttpClient client = new HttpClient();
        PostMethod post = new PostMethod(url);
        NameValuePair[] data = params.entrySet().stream()
                .map(entry -> new NameValuePair(entry.getKey(), entry.getValue())).toArray(NameValuePair[]::new);
        post.setRequestBody(data);
        client.executeMethod(post);
        if (post.getStatusCode() != Response.Status.OK.getStatusCode()) {
            throw new MoneyTransferException(post.getResponseBodyAsString());
        }

        return post.getResponseBodyAsString();
    }
}
