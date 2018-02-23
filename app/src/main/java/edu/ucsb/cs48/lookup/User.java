package edu.ucsb.cs48.lookup;

import java.util.List;

/**
 * Created by Wilson on 2/7/18.
 */

/*
    This should be used to represent and store information about each user.
 */

public class User {

    private String name;
    private String email;
    private String phone;
    private String uid;

    private List<ContactInfo> connectedContactInfo;
    private List<ContactInfo> visibleContactInfo;

    public User() {
        // Required empty constructor for Firebase
    }

    public User(String name, String email, String phone, String uid) {
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.uid = uid;
    }


    public String getName() { return name; }

    public String getEmail() {
        return email;
    }

    public String getPhone() { return phone; }

    public String getUid() { return uid; }

    public String setName(String name) { this.name = name; }

    public String setEmail(String email) { this.email = email; }

    public String setPhone(String phone) { this.phone = phone; }

    public void addConnectedContactInfo() {
        //TODO: Implement
    }

    public void rmConnectedContactInfo() {
        //TODO: Implement
    }

    public void addVisibleContactiInfo() {
        //TODO: Implement
    }
    public void rmVisibleContactInfo() {
        //TODO: Implement
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