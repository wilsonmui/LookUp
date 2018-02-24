package edu.ucsb.cs48.lookup;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by esuarez on 2/23/18.
 */

public enum Network {

    INSTANCE(new HashMap<String, ArrayList<String>>());

    //==============================================================================================
    // Fields
    //==============================================================================================
    private HashMap<String, ArrayList<String>> network;

    //==============================================================================================
    // Constructor
    //==============================================================================================

    Network(HashMap<String, ArrayList<String>> network) {
        this.network = network;
    }

    //==============================================================================================
    // Singleton Accessor Methods
    //==============================================================================================
    public static Network getInstance() {
        return INSTANCE;
    }

    public HashMap<String, ArrayList<String>> getNetwork() {
        return this.network;
    }

    //==============================================================================================
    // Methods
    //==============================================================================================


    public void addNewUser(String uid) {
        if(this.getNetwork().containsKey(uid)) {
            System.out.println("Unsuccesfully Added User: " + uid + " to Network; user already exists.");
        } else {
            this.getNetwork().put(uid, new ArrayList<String>());
        }
    }

    public void rmUser(String uid) {
        if(this.getNetwork().containsKey(uid)) {
            this.getNetwork().remove(uid);
        } else {
            System.out.println("Unsuccesfully removed User: " + uid + " from Network; user already exists.");
        }
    }

    public ArrayList<String> getContacts(String uid) {
        if(this.getNetwork().containsKey(uid)) {
            return this.getNetwork().get(uid);
        } else {
            System.out.println("Unsuccesfully obtained User: " + uid + "'s contacts; user does not exist.");
            return null;
        }
    }

    public boolean isContact(String baseUid, String targetUid) {
        if(this.getNetwork().containsKey(baseUid)) {
            int size = this.getNetwork().get(baseUid).size();

            // CHeck if array is empty
            if(size == 0) {
                System.out.println("User: " + baseUid + " has no contacts");
                return false;
            }

            ArrayList<String> contacts = this.getNetwork().get(baseUid);

            for(int i = 0; i < size; i++) {
                if(contacts.get(i).equals(targetUid)) {
                    return true;
                }
            }

            return false;

        } else {
            System.out.println("Could not retrieve User: " + baseUid + "'s contact: " + targetUid + "; origin does not exist.");
            return false;
        }
    }

    public void addUserContact(String baseUid, String targetUid) {
        if(!this.isContact(baseUid, targetUid)) {
            this.getNetwork().get(baseUid).add(targetUid);
        }
    }

    public void rmUserContact(String baseUid, String targetUid) {
        if(this.isContact(baseUid, targetUid)) {
            int size = this.getNetwork().get(baseUid).size();

            ArrayList<String> contacts = this.getNetwork().get(baseUid);

            for(int i = 0; i < size; i++) {
                if(contacts.get(i).equals(targetUid)) {
                    contacts.remove(i);
                }
            }
        }
    }

}
