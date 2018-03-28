package edu.ucsb.cs48.lookup;

import android.util.Log;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.lang.reflect.Array;
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
    private ArrayList<String> contacts;

    //==============================================================================================
    // Constructor
    //==============================================================================================

    private Network() {
        this.isContact = false;
        this.contacts = new ArrayList();
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

    public String getContactsArray(int index) { return this.contacts.get(index); }

    public ArrayList<String> getContactsRef() { return this.contacts;}

    public void addElementToContactsArray(String uid) { this.contacts.add(uid); }

    public int sizeContactsArray() { return this.contacts.size(); }

    public void clearContactsArray() { this.contacts.clear(); }

    //==============================================================================================
    // Methods
    //==============================================================================================

    public void mReadDataOnce(String child, final OnGetDataListener listener) {
        listener.onStart();
        FirebaseDatabase.getInstance().getReference().child(child).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                listener.onSuccess(dataSnapshot);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                listener.onFailed(databaseError);
            }
        });
    }

    public void mReadDataOnce(String child, String innerChild, final OnGetDataListener listener) {
        listener.onStart();
        FirebaseDatabase.getInstance().getReference().child(child).child(innerChild).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                listener.onSuccess(dataSnapshot);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                listener.onFailed(databaseError);
            }
        });
    }

    public void rmUser(final String uid) {

        DatabaseReference userRef;


        userRef = FirebaseDatabase.getInstance().getReference()
                .child("users");
        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    boolean userExists = false;
                    for (DataSnapshot usersDs : dataSnapshot.getChildren()) {

                        rmUserContact(uid, usersDs.getKey());

                        if (usersDs.getKey().equals(uid)) {
                            userExists = true;
                        }
                    }

                    if (!userExists) {
                        System.out.println("Removing User: " + uid + "failed: User does not exist");
                    } else {
                        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference()
                                .child("users").child(uid);
                        userRef.removeValue();
                        
                        System.out.println("User: " + uid + "was successfully removed");                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // handle error
            }
        });
    }


    public void addUserContact(final String baseUid, final String targetUid) {

        DatabaseReference networkRef;
        networkRef = FirebaseDatabase.getInstance().getReference()
                .child("users");

        networkRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                if (dataSnapshot.exists()) {

                    for (DataSnapshot networkDs : dataSnapshot.getChildren()) {
                        final String parentKey = networkDs.getKey().toString();

                        if (parentKey.equals(baseUid)) {
                            DatabaseReference keyRef = FirebaseDatabase.getInstance().getReference()
                                    .child("users").child(parentKey).child("contacts");
                            keyRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    if (dataSnapshot.exists()) {
                                        boolean isContact = false;
                                        for (DataSnapshot keyDs : dataSnapshot.getChildren()) {
                                            if (keyDs.getValue().toString().equals(targetUid)) {
                                                isContact = true;
                                            }
                                        }

                                        if (!isContact) {
                                            FirebaseDatabase.getInstance().getReference()
                                                    .child("users").child(parentKey)
                                                    .child("contacts").push().setValue(targetUid);
                                            System.out.println("Contact added!");
                                        }
                                    } else {
                                        FirebaseDatabase.getInstance().getReference().child("users")
                                                .child(baseUid).child("contacts").push().setValue(targetUid);
                                    }
                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {
                                    // handle error
                                }
                            });
                        }
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // handle error
            }
        });
    }

    public void rmUserContact(final String baseUid, final String targetUid) {

        DatabaseReference contactsRef;
        contactsRef = FirebaseDatabase.getInstance().getReference()
                .child("users").child(baseUid).child("contacts");

        contactsRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    boolean isContact = false;
                    String targetKey = "";
                    for (DataSnapshot keyDs : dataSnapshot.getChildren()) {
                        if (keyDs.getValue().toString().equals(targetUid)) {
                            isContact = true;
                            targetKey = keyDs.getKey().toString();
                            break;
                        }
                    }

                    if (isContact) {
                        DatabaseReference targetRef = FirebaseDatabase.getInstance().getReference()
                                .child("users").child(baseUid).child("contacts").child(targetKey);
                        targetRef.removeValue();
                        System.out.println("User: " + baseUid + " contact: " + targetUid + " successfully removed");
                    } else {
                        System.out.println("Removing contact failed, User: " + baseUid + " does not have contact: " + targetUid);
                    }
                } else {
                    Log.d(TAG, "User: " + baseUid + " does not Exist");
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // handle error
            }
        });
    }

}
