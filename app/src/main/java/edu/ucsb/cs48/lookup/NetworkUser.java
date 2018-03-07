package edu.ucsb.cs48.lookup;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by esuarez on 3/5/18.
 */

public class NetworkUser {
    //==============================================================================================
    // Fields
    //==============================================================================================

    private String uid;
    private List<String> networkUserContacts;

    //==============================================================================================
    // Constructors
    //==============================================================================================
    public NetworkUser() {
        // Required empty constructor for Firebase
    }

    public NetworkUser(String uid) {
        this.uid = uid;
        this.networkUserContacts = new ArrayList<String>();
    }

    //==============================================================================================
    // Accessor Methods
    //==============================================================================================
    public String getUid() { return this.uid; }

    public List<String> getNetworkUserContactsl() {return this.networkUserContacts; }




    //==============================================================================================
    // Setter Methods
    //==============================================================================================
    public void setUid(String name) { this.uid = name; }

    public void setNetworkUserContacts(List<String> networkUserContacts) { this.networkUserContacts = networkUserContacts; }

    //==============================================================================================
    // Helper Functions
    //==============================================================================================

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof User) {
            User o = (User) obj;
            return this.uid.equals(o.getUid());
        }
        return false;
    }
}
