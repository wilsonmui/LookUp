package edu.ucsb.cs48.lookup;
import java.util.ArrayList;

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

    private ArrayList<ContactInfo> connectedContactInfo;
    private ArrayList<ContactInfo> visibleContactInfo;

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
        this.connectedContactInfo = new ArrayList<ContactInfo>();
        this.visibleContactInfo = new ArrayList<ContactInfo>();
    }


    //==============================================================================================
    // Accessor Methods
    //==============================================================================================
    public String getName() { return this.name; }

    public String getEmail() {
        return this.email;
    }

    public String getPhone() { return this.phone; }

    public String getUid() { return this.uid; }

    public ArrayList<ContactInfo> getConnectedContactInfo() { return connectedContactInfo; }

    public ArrayList<ContactInfo> getVisibleContactInfo() { return visibleContactInfo; }

    //==============================================================================================
    // Setter Methods
    //==============================================================================================
    public void setName(String name) { this.name = name; }

    public void setEmail(String email) { this.email = email; }

    public void setPhone(String phone) { this.phone = phone; }


    //==============================================================================================
    // Helper Functions
    //==============================================================================================
    private int contactInfoExists(ArrayList<ContactInfo> contactInfos, ContactInfo contactInfo) {

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

    public String addConnectedContactInfo(ContactInfo contactInfo) {

        if(contactInfoExists(this.connectedContactInfo, contactInfo) > -1) {
            return "Already connected through " + contactInfo.getName();
        } else {
            if (contactInfo.connect()) {
                this.connectedContactInfo.add(contactInfo);
                return "Successfully connected" + contactInfo.getName();
            } else {
                return "Disonnected " + contactInfo.getName() + " was unsuccessful.";
            }
        }
    }

    public String rmConnectedContactInfo(ContactInfo contactInfo) {

        int contactInfoIndex = contactInfoExists(this.connectedContactInfo, contactInfo);

        if(contactInfoIndex > -1) {

                // Attempt to disconnect the Contact Info
                if (contactInfo.disconnect()) {
                    connectedContactInfo.remove(contactInfoIndex);
                    return "Successfully disconnected" + contactInfo.getName();
                } else {
                    return "Disconnecting " + contactInfo.getName() + " was unsuccessful.";
                }
        } else {
            return contactInfo.getName() + " does not exist";
        }
    }


    public String addVisibleContactiInfo(ContactInfo contactInfo) {

        if(contactInfoExists(this.visibleContactInfo, contactInfo) > -1) {
                return contactInfo.getName() + " already visible.";
        } else {
            this.visibleContactInfo.add(contactInfo);
            return contactInfo.getName() + "is now visible";
        }
    }

    public String rmVisibleContactInfo(ContactInfo contactInfo) {

        int contactInfoIndex = contactInfoExists(this.connectedContactInfo, contactInfo);

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

