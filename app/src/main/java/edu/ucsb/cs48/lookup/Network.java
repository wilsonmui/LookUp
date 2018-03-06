package edu.ucsb.cs48.lookup;

import android.util.Log;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import static android.content.ContentValues.TAG;
import static com.facebook.FacebookSdk.getApplicationContext;

/**
 * Created by esuarez on 2/23/18.
 */

public class Network {


    //==============================================================================================
    // Fields
    //==============================================================================================
    private static Network instance = null;
    private boolean isContact;

    //==============================================================================================
    // Constructor
    //==============================================================================================

    private Network() {
        this.isContact = false;
    }

    //==============================================================================================
    // Singleton Accessor Methods
    //==============================================================================================

    public static Network getInstance(){
        if (instance == null)
            instance = new Network();

        return instance;
    }

    public boolean getIsContact() {
        return this.isContact;
    }

    public void setIsContact(boolean isContact) { this.isContact = isContact; }

    //==============================================================================================
    // Methods
    //==============================================================================================

//    public void rmUser(String uid) {
//        databasePull();
//        if(this.getNetwork().containsKey(uid)) {
//            this.getNetwork().remove(uid);
//            DatabaseReference networkref = FirebaseDatabase.getInstance().getReference()
//                    .child("network");
//            networkref.setValue(this.getNetwork());
//        } else {
//            System.out.println("Unsuccesfully removed User: " + uid + " from Network; user already exists.");
//        }
//    }

//    public ArrayList<String> getContacts(String uid) {
//        databasePull();
//        if(this.getNetwork().containsKey(uid)) {
//            return this.getNetwork().get(uid);
//        } else {
//            System.out.println("Unsuccesfully obtained User: " + uid + "'s contacts; user does not exist.");
//            return null;
//        }
//    }
    public void addUserContact(final String baseUid, final String targetUid) {

        DatabaseReference networkRef;
        networkRef = FirebaseDatabase.getInstance().getReference()
                .child("network");

        networkRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    boolean userExists = false;
                    for (DataSnapshot networkDs : dataSnapshot.getChildren()) {
                        final String parentKey = networkDs.getKey().toString();

                        if (parentKey.equals(baseUid)) {
                            userExists = true;
                            DatabaseReference keyRef = FirebaseDatabase.getInstance().getReference()
                                    .child("network").child(parentKey);
                            keyRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    boolean isContact = false;
                                    for (DataSnapshot keyDs : dataSnapshot.getChildren()) {
                                        if (keyDs.getValue().toString().equals(targetUid) && parentKey.equals(baseUid)) {
                                            System.out.println("IS INDEED A CONTACT");
                                            isContact = true;
                                            break;
                                        }
                                    }

                                    System.out.println(isContact);
                                    if (!isContact) {
                                        System.out.println("Contact does NOT exist, and isContact is " + isContact);
                                        DatabaseReference networkRef = FirebaseDatabase.getInstance().getReference()
                                                .child("network");
                                        networkRef.child(baseUid).push().setValue(targetUid);
                                        System.out.println("Contact added!");
                                    }
                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {
                                    // handle error
                                }
                            });
                        } else {
                            System.out.println("Else reached, parent key doesnt equal base uid.");
//                            DatabaseReference networkRef = FirebaseDatabase.getInstance().getReference()
//                                    .child("network");
//                            networkRef.child(baseUid).push().setValue(targetUid);
                        }
                    }

                    if(!userExists) {
                        DatabaseReference networkRef = FirebaseDatabase.getInstance().getReference()
                                .child("network");
                        networkRef.child(baseUid).push().setValue(targetUid);
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // handle error
            }
        });
    }

//    public void rmUserContact(String baseUid, String targetUid) {
//        databasePull();
//        //if targetUid is a contact
//        if(this.isContact(baseUid, targetUid)) {
//            int size = this.getNetwork().get(baseUid).size();
//
//            ArrayList<String> contacts = this.getNetwork().get(baseUid);
//
//            for(int i = 0; i < size; i++) {
//                if(contacts.get(i).equals(targetUid)) {
//                    contacts.remove(i);
//
//                    DatabaseReference networkref = FirebaseDatabase.getInstance().getReference()
//                            .child("network");
//                    networkref.setValue(this.getNetwork());
//
//                }
//            }
//        }
//    }
}
