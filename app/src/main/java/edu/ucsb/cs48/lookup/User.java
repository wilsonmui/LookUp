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
    private String uid;
    private List<SocialMedia> connectedSocialMedias;
    private List<SocialMedia> visibleSocialMedias;
    private List<User> contacts;
    
    public User() {
        // Required empty constructor for Firebase
    }

    public User(String name, String email) {
        this.name = name;
        this.email = email;
        this.uid = uid;
    }


    public String getName() { return name; }

    public String getEmail() {
        return email;
    }

    public String getUid() { return uid; }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof User) {
            User o = (User) obj;
            return this.uid.equals(o.uid);
        }
        return false;
    }
}