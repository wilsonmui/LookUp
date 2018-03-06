package edu.ucsb.cs48.lookup;
import java.util.ArrayList;
import java.util.List;

import edu.ucsb.cs48.lookup.ContactInfo.ContactInfo;

/**
 * Created by Wilson on 2/7/18.
 */

/*
    This should be used to represent and store information about each user.
 */

public class User {

    //==============================================================================================
    // Fields
    //==============================================================================================
    private String name;
    private String email;
    private String phone;
    private String uid;
    private String facebookURL;
    private String twitterURl;

    //==============================================================================================
    // Constructors
    //==============================================================================================
    public User() {
        // Required empty constructor for Firebase
    }

    public User(String name, String email, String phone, String uid) {
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.uid = uid;
        this.facebookURL = "";
        this.twitterURl = "";
    }

    public User(String name, String email, String phone, String uid, String facebookURL, String twitterUrl) {
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.uid = uid;
        this.facebookURL = facebookURL;
        this.twitterURl = twitterUrl;
    }

    //==============================================================================================
    // Accessor Methods
    //==============================================================================================
    public String getName() { return this.name; }

    public String getEmail() {return this.email; }

    public String getPhone() { return this.phone; }

    public String getUid() { return this.uid; }

    public String getFacebookURL() { return this.uid; }

    public String getTwitterURl() { return this.uid; }


    //==============================================================================================
    // Setter Methods
    //==============================================================================================
    public void setName(String name) { this.name = name; }

    public void setEmail(String email) { this.email = email; }

    public void setPhone(String phone) { this.phone = phone; }

    public void setFacebookURL(String phone) { this.phone = phone; }

    public void setTwitterURl(String phone) { this.phone = phone; }


    //==============================================================================================
    // Helper Functions
    //==============================================================================================
    private int contactInfoExists(List<Boolean> contactInfos, ContactInfo contactInfo) {

        int size = contactInfos.size();

        for(int i = 0; i < size; i++) {
            if (contactInfos.get(i).equals(contactInfo)) {
                return i; // True
            }
        }

        return -1; // False
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof User) {
            User o = (User) obj;
            return this.uid.equals(o.uid);
        }
        return false;
    }
}

