package edu.ucsb.cs48.lookup.ContactInfo;

import edu.ucsb.cs48.lookup.ContactInfo.ContactInfo;

/**
 * Created by Tina on 2/25/2018.
 */

public class Facebook extends ContactInfo {

    String name;
    String profileURL;
    boolean isConnected;

    //==============================================================================================
    // Constructor
    //==============================================================================================
    Facebook() {
        super("idk");
        name = getName();
    }

    public boolean connect() {
        isConnected = true;
        return true;
    }

    public boolean disconnect() {
        isConnected = false;
        return true;
    }

    public boolean isConnected() {
        return isConnected;
    }

    public String getProfileURL() {
        return profileURL;
    }

}
