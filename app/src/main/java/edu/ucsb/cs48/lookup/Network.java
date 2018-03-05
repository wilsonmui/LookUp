package edu.ucsb.cs48.lookup;

import android.util.Log;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;

import static android.content.ContentValues.TAG;
import static com.facebook.FacebookSdk.getApplicationContext;

/**
 * Created by esuarez on 2/23/18.
 */

public enum Network {

    INSTANCE(new HashMap<String, ArrayList<String>>());

    //==============================================================================================
    // Fields
    //==============================================================================================
    private HashMap<String, ArrayList<String>> network;

    //==============================================================================================
    // Constructor
    //==============================================================================================

    Network(HashMap<String, ArrayList<String>> network) {
        this.network = network;
    }

    //==============================================================================================
    // Singleton Accessor Methods
    //==============================================================================================
    public static Network getInstance() {
        return INSTANCE;
    }

    public HashMap<String, ArrayList<String>> getNetwork() {
        return this.network;
    }

    //==============================================================================================
    // Methods
    //==============================================================================================

    //make sure this is called when a new user is created
    public void addNewUser(String uid) {
        //pullfromfirebase();
        if(this.getNetwork().containsKey(uid)) {
            System.out.println("Unsuccesfully Added User: " + uid + " to Network; user already exists.");
            Log.d(TAG, "Unsuccessfully Added User: " + uid + "to Network, user already exists");

        } else {
            this.getNetwork().put(uid, new ArrayList<String>());

            DatabaseReference networkref = FirebaseDatabase.getInstance().getReference()
                    .child("network");
            networkref.setValue(this.getNetwork());

            Toast.makeText(getApplicationContext(), "added: " + uid,
                    Toast.LENGTH_SHORT).show();
        }
    }

    public void rmUser(String uid) {
        pullfromfirebase();
        if(this.getNetwork().containsKey(uid)) {
            this.getNetwork().remove(uid);
            DatabaseReference networkref = FirebaseDatabase.getInstance().getReference()
                    .child("network");
            networkref.setValue(this.getNetwork());
        } else {
            System.out.println("Unsuccesfully removed User: " + uid + " from Network; user already exists.");
        }
    }

    public ArrayList<String> getContacts(String uid) {
        pullfromfirebase();
        if(this.getNetwork().containsKey(uid)) {
            return this.getNetwork().get(uid);
        } else {
            System.out.println("Unsuccesfully obtained User: " + uid + "'s contacts; user does not exist.");
            return null;
        }
    }

    public boolean isContact(String baseUid, String targetUid) {
        pullfromfirebase();
        if(this.getNetwork().containsKey(baseUid)) {
            int size = this.getNetwork().get(baseUid).size();

            // CHeck if array is empty
            if(size == 0) {
                System.out.println("User: " + baseUid + " has no contacts");
                return false;
            }

            ArrayList<String> contacts = this.getNetwork().get(baseUid);

            for(int i = 0; i < size; i++) {
                if(contacts.get(i).equals(targetUid)) {
                    return true;
                }
            }

            return false;

        } else {
            System.out.println("Could not retrieve User: " + baseUid + "'s contact: " + targetUid + "; origin does not exist.");
            return false;
        }
    }

    public void addUserContact(String baseUid, String targetUid) {
        pullfromfirebase();
        //if not already a contact
        if(!this.isContact(baseUid, targetUid)) {
            this.getNetwork().get(baseUid).add(targetUid);

            //now add in database as well
            DatabaseReference networkref = FirebaseDatabase.getInstance().getReference()
                    .child("network");
            networkref.setValue(this.getNetwork());

        }
    }

    public void rmUserContact(String baseUid, String targetUid) {
        pullfromfirebase();
        //if targetUid is a contact
        if(this.isContact(baseUid, targetUid)) {
            int size = this.getNetwork().get(baseUid).size();

            ArrayList<String> contacts = this.getNetwork().get(baseUid);

            for(int i = 0; i < size; i++) {
                if(contacts.get(i).equals(targetUid)) {
                    contacts.remove(i);

                    DatabaseReference networkref = FirebaseDatabase.getInstance().getReference()
                            .child("network");
                    networkref.setValue(this.getNetwork());

                }
            }
        }
    }

    //update network from firebase
    private void pullfromfirebase(){
        DatabaseReference db;
        db = FirebaseDatabase.getInstance().getReference()
                .child("network");
        db.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Network dbnetwork = dataSnapshot.getValue(Network.class);
                //Log.d(TAG, "size of network: " + dbnetwork.getNetwork().size());

                //network = dbnetwork.getNetwork();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

}
