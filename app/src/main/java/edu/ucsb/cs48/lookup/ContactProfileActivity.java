package edu.ucsb.cs48.lookup;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Button;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

/**
 * Created by Wilson on 2/26/18.
 */


//shows User information about contact
public class ContactProfileActivity extends AppCompatActivity {
    private TextView username, twitter, facebook, email, userphone;
    private String uid;
    private Button button;
    DatabaseReference db;
    public boolean currentProfileIsContact = false;

    private FirebaseUser user;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.contact_profile_page);
        Bundle b = new Bundle();
        b = getIntent().getExtras();
        uid = b.getString("uid");
//        Toast.makeText(this, "" + uid, Toast.LENGTH_SHORT).show();

        //Firebase Schtuff
        mAuth = FirebaseAuth.getInstance();
//        if(mAuth.getCurrentUser() == null) {
//            finish();
//            startActivity(new Intent(this, SignInPageActivity.class));
//        }
        user = mAuth.getCurrentUser();

        username = (TextView) findViewById(R.id.user_name);
        twitter = (TextView) findViewById(R.id.user_twitter);
        facebook = (TextView) findViewById(R.id.user_facebook);
        email = (TextView) findViewById(R.id.user_email);
        userphone = (TextView) findViewById(R.id.user_phone);
        button = (Button) findViewById(R.id.add_friend_button);

        db = FirebaseDatabase.getInstance().getReference();

        modifyUIIfContact();

        loadUserField(db.child("users").child(uid).child("email"), email);
        loadUserField(db.child("users").child(uid).child("phone"), userphone);
        loadUserField(db.child("users").child(uid).child("name"), username);
        loadUserField(db.child("users").child(uid).child("facebook"), facebook);
        loadUserField(db.child("users").child(uid).child("twitter"), twitter);

        findViewById(R.id.add_friend_button).setOnClickListener(new Button.OnClickListener(){
            public void onClick(View v) {
                addFriend();
            }
        });
    }

    //changes the button look if the user is already a contact (add contact --> remove contact)
    private void modifyUIIfContact(){
        checkIfUserIsContact(user.getUid(), uid);
        if(currentProfileIsContact) {
            button.setText("Remove contact");
        }else{
            button.setText("Add contact");
        }
    }

    public void checkIfUserIsContact(final String baseUid, final String targetUid){
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
                                    System.out.println("");
                                    for (DataSnapshot keyDs : dataSnapshot.getChildren()) {
                                        if (keyDs.getValue().toString().equals(targetUid) && parentKey.equals(baseUid)) {
                                            currentProfileIsContact = true;
                                        }
                                    }
                                    System.out.println("TRU OR FALSE BITCH: " + currentProfileIsContact);
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

    public void addFriend(){
        //if this user already is a contact,
        String yourUid = user.getUid();
        addUserContact(yourUid,uid);
        addUserContact(uid,yourUid); //takes care of if the person is or isnt a contact
    }

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
                                        button.setText("Remove contact");
                                        currentProfileIsContact = true;
                                    }else{
                                        //TODO remove as contact
                                        removeContact();
                                    }
                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {
                                    // handle error
                                }
                            });
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

    public void removeContact(){
        rmUserContact(user.getUid(), uid);
        rmUserContact(uid, user.getUid());
    }

    public void rmUserContact(final String baseUid, final String targetUid) {

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
                                    String targetKey = "";
                                    for (DataSnapshot keyDs : dataSnapshot.getChildren()) {
                                        if (keyDs.getValue().toString().equals(targetUid) && parentKey.equals(baseUid)) {
                                            isContact = true;
                                            targetKey = keyDs.getKey().toString();
                                            break;
                                        }
                                    }

                                    if (isContact) {
                                        DatabaseReference targetRef = FirebaseDatabase.getInstance().getReference()
                                                .child("network").child(baseUid).child(targetKey);
                                        targetRef.removeValue();
                                        System.out.println("User: " + baseUid + " contact: " + targetUid + " successfully removed");

                                        button.setText("Add contact");
                                        currentProfileIsContact = false;
                                    } else {
                                        System.out.println("Removing contact failed, User: " + baseUid + " does not have contact: " + targetUid);
                                    }

                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {
                                    // handle error
                                }
                            });
                        }
                    }

                    if (!userExists) {
                        System.out.println("Removing contact failed, User: " + baseUid + " does not exist");
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // handle error
            }
        });
    }
    public void loadUserField(DatabaseReference databaseReference, final TextView textView) {
        System.out.println("LOAD");
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                textView.setText(dataSnapshot.getValue(String.class));
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e("Database Error:", "Error connecting to database");
            }
        });
    }

}
