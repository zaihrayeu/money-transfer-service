package com.mycompany.server.repository;

import com.mycompany.server.model.Client;

import java.util.HashMap;
import java.util.Map;

/**
 * class that managers the client DB
 */
public class ClientRepository {
    private static Map<Long, Client> idToClient;
    static {
        // static init for the purpose of the demo
        idToClient = new HashMap<>();
        idToClient.put(1L, new Client(1, "Bob", 200, true));
        idToClient.put(2L, new Client(2, "Alice", 150, true));
        idToClient.put(3L, new Client(3, "Jack", 0, false));
    }

    /**
     * returns a client by its ID
     * @param id id if the client
     * @return client if present in the DB and null otherwise
     */
    public static Client getClientByID(Long id){
        return idToClient.get(id);
    }
}
