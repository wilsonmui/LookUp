package edu.ucsb.cs48.lookup;
import android.net.Uri;

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
    private String facebook;
    private String twitter;
    private String snapchat;
    private String linkedin;
    private String instagram;
    private String github;
    private String profilePic;

    //==============================================================================================
    // Constructors
    //==============================================================================================
    public User() {
        // Required empty constructor for Firebase
    }

    public User(String name, String email, String phone, String uid, String profilePic) {
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.uid = uid;
        this.facebook = "";
        this.twitter = "";
        this.snapchat = "";
        this.linkedin = "";
        this.instagram = "";
        this.github = "";
        this.profilePic = profilePic;
    }

    public User(String name, String email, String phone, String uid, String facebook, String twitter, String snapchat, String linkedin, String instagram, String github, String profilePic) {
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.uid = uid;
        this.facebook = facebook;
        this.twitter = twitter;
        this.snapchat = snapchat;
        this.linkedin = linkedin;
        this.instagram = instagram;
        this.github = github;
        this.profilePic = profilePic;
    }

    //==============================================================================================
    // Accessor Methods
    //==============================================================================================
    public String getName() { return this.name; }

    public String getEmail() {return this.email; }

    public String getPhone() { return this.phone; }

    public String getUid() { return this.uid; }

    public String getFacebook() { return this.facebook; }

    public String getTwitter() { return this.twitter; }

    public String getProfilePic() { return this.profilePic; }

    public String getSnapchat() { return this.snapchat; }

    public String getLinkedin() { return this.linkedin; }

    public String getInstagram() { return this.instagram; }

    public String getGithub() { return this.github; }



    //==============================================================================================
    // Setter Methods
    //==============================================================================================
    public void setName(String name) { this.name = name; }

    public void setEmail(String email) { this.email = email; }

    public void setPhone(String phone) { this.phone = phone; }

    public void setFacebook(String facebook) { this.facebook = facebook; }

    public void setSnapchat(String twitter) { this.snapchat = snapchat; }

    public void setLinkedin(String twitter) { this.linkedin = linkedin; }

    public void setInstagram(String twitter) { this.instagram = instagram; }

    public void setGithub(String twitter) { this.github = github; }

    public void setProfilePic(String profilePic) { this.profilePic = profilePic; }

    //==============================================================================================
    // Methods
    //==============================================================================================

//    //==============================================================================================
//    // Methods
//    //==============================================================================================
//
//    public String addVisibleContactInfo(ContactInfo contactInfo) {
//
//        if(contactInfoExists(this.visibleContactInfo, contactInfo) > -1) {
//                return contactInfo.getName() + " already visible.";
//        } else {
//            this.visibleContactInfo.add(contactInfo);
//            return contactInfo.getName() + "is now visible";
//        }
//    }
//
//    public String rmVisibleContactInfo(ContactInfo contactInfo) {
//
//        int contactInfoIndex = contactInfoExists(this.visibleContactInfo, contactInfo);
//
//        if(contactInfoIndex > -1) {
//            visibleContactInfo.remove(contactInfoIndex);
//            return "Successfully made " + contactInfo.getName() + " invisible.";
//        } else {
//            return contactInfo.getName() + " either does not exist or is already invisible";
//        }
//    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof User) {
            User o = (User) obj;
            return this.uid.equals(o.uid);
        }
        return false;
    }
}

