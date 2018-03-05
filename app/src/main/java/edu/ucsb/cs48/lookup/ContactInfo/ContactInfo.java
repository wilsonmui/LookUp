package edu.ucsb.cs48.lookup.ContactInfo;

import edu.ucsb.cs48.lookup.User;

/**
 * Created by esuarez on 2/23/18.
 */

public abstract class ContactInfo {

    //==============================================================================================
    // Fields
    //==============================================================================================

    private String name;

    //==============================================================================================
    // Constructor
    //==============================================================================================

    public ContactInfo(String name) {
        this.name = name;
    }

    //==============================================================================================
    // Accessor Methods
    //==============================================================================================

    public String getName() { return this.name; }

    //==============================================================================================
    // Setter Methods
    //==============================================================================================

    public void setName(String name) { this.name = name; }

    //==============================================================================================
    // Methods
    //==============================================================================================
    public boolean connect() {
        // Must be Overriden by children
        return false;
    }

    public boolean disconnect() {
        // Must be Overriden by children
        return false;
    }

    public boolean isConnected() {
        return false;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof User) {
            User o = (User) obj;
            return this.getName().equals(o.getName());
        }
        return false;
    }
}
