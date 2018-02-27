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

    private List<ContactInfo> visibleContactInfo;

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
        this.visibleContactInfo = new ArrayList<ContactInfo>();
        this.facebookURL = "";
        this.twitterURl = "";
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

    public List<ContactInfo> getVisibleContactInfo() { return visibleContactInfo; }

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
    private int contactInfoExists(List<ContactInfo> contactInfos, ContactInfo contactInfo) {

        int size = contactInfos.size();

        for(int i = 0; i < size; i++) {
            if (contactInfos.get(i).equals(contactInfo)) {
                return i; // True
            }
        }

        return -1; // False
    }

    //==============================================================================================
    // Methods
    //==============================================================================================

    public String addVisibleContactiInfo(ContactInfo contactInfo) {

        if(contactInfoExists(this.visibleContactInfo, contactInfo) > -1) {
                return contactInfo.getName() + " already visible.";
        } else {
            this.visibleContactInfo.add(contactInfo);
            return contactInfo.getName() + "is now visible";
        }
    }

    public String rmVisibleContactInfo(ContactInfo contactInfo) {

        int contactInfoIndex = contactInfoExists(this.visibleContactInfo, contactInfo);

        if(contactInfoIndex > -1) {
            visibleContactInfo.remove(contactInfoIndex);
            return "Successfully made " + contactInfo.getName() + " invisible.";
        } else {
            return contactInfo.getName() + " either does not exist or is already invisible";
        }
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

