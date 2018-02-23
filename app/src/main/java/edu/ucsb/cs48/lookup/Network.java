package edu.ucsb.cs48.lookup;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by esuarez on 2/23/18.
 */

public class Network {

    public HashMap<String, List<User>> network;
    private static Network network_instance;

    private static Network getInstance() {
        if(network_instance == null) {
            network_instance = new Network();
            return network_instance;
        }
    }
    private Network() {
        network = new HashMap<String, List<User>>();
    }

    public List<User> getContacts(String uid) {
        //TODO: Implement
    }

    public boolean isContact(String baseUid, String targetUid) {
        //TODO: Implement
    }

    public void add(String baseUid, String targetUid) {
        //TODO: Implement
    }

    public void remove(String baseUid, String targetUid) {
        //TODO: Implement
    }

}
