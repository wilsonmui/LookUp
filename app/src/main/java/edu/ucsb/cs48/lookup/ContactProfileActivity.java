package edu.ucsb.cs48.lookup;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Button;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import static android.content.ContentValues.TAG;

/**
 * Created by Wilson on 2/26/18.
 */


//shows User information about contact
public class ContactProfileActivity extends AppCompatActivity {

    //==============================================================================================
    // Declare Variables
    //==============================================================================================
    private TextView username, twitter, facebook, email, userphone;
    private TextView textViewSnapchat, textViewInstagram, textViewGithub, textViewLinkedin;
    private String uid;
    private Button button;
    private ImageView userImg;
    DatabaseReference db, profilePicRef, userRef;
    public boolean currentProfileIsContact = false;
    private DatabaseReference snapchatRef, instagramRef, githubRef, linkedinRef, contactRef;
    private FirebaseUser user;
    private FirebaseAuth mAuth;

    //==============================================================================================
    // On Create
    //==============================================================================================
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.contact_profile_page);

        Bundle b;
        b = getIntent().getExtras();
        uid = b.getString("uid");

        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();
        if(user == null) {
            finish();
            startActivity(new Intent(this, SignInPageActivity.class));
        }

        String uid = user.getUid();
        db = FirebaseDatabase.getInstance().getReference();
        userRef = db.child("users").child(uid);

        isContact(user.getUid(), uid);

        initListeners();

        loadUserData();

    }
    //==============================================================================================
    // Database Functions
    //==============================================================================================

    private void mReadDataOnce(String uid, final OnGetDataListener listener) {
        listener.onStart();
        FirebaseDatabase.getInstance().getReference().child("users").child(uid).child("contacts").addListenerForSingleValueEvent(new ValueEventListener() {
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

    public void loadUserField(DatabaseReference databaseReference, final TextView textView) {
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

    public void loadUserSocialField(DatabaseReference databaseReference, final TextView textView) {
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                textView.setText("https://" + dataSnapshot.getKey().toString() + "/" + dataSnapshot.getValue(String.class));
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e("Database Error:", "Error connecting to database");
            }
        });
    }

    private void loadUserData() {
        contactRef = FirebaseDatabase.getInstance().getReference().child("users").child(uid);

        loadUserField(contactRef.child("email"), email);
        loadUserField(contactRef.child("phone"), userphone);
        loadUserField(contactRef.child("name"), username);
        loadUserSocialField(contactRef.child("facebook"), facebook);
        loadUserSocialField(contactRef.child("twitter"), twitter);
        loadUserSocialField(contactRef.child("snapchat"), textViewSnapchat);
        loadUserSocialField(contactRef.child("instagram"), textViewInstagram);
        loadUserSocialField(contactRef.child("github"), textViewGithub);
        loadUserSocialField(contactRef.child("linkedin"), textViewLinkedin);

        profilePicRef = contactRef.child("profilePic");
        userImg = (ImageView) findViewById(R.id.user_img);
        profilePicRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(final DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue(String.class) != null && !dataSnapshot.getValue(String.class).isEmpty()) {
                    Glide.with(getApplicationContext())
                            .load(dataSnapshot.getValue(String.class))
                            .override(100, 100)
                            .into(userImg);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {}
        });
    }

    //==============================================================================================
    // Contact Functions
    //==============================================================================================

    private void isContact(final String baseUid, final String targetUid){
        mReadDataOnce(baseUid, new OnGetDataListener() {
            @Override
            public void onStart() {
                //DO SOME THING WHEN START GET DATA HERE
            }

            @Override
            public void onSuccess(DataSnapshot data) {
                boolean b = false;
                for (DataSnapshot contactsDs : data.getChildren()) {
                    if(contactsDs.getValue().toString().equals(targetUid)) {
                        System.out.println(contactsDs.getValue());
                        b = true;
                    }
                }
                if(b) {
                    button.setText("Remove contact");
                    button.setBackgroundColor(Color.parseColor("#CB8383"));
                    currentProfileIsContact = true;
                } else {
                    button.setText("Add contact");
                    button.setBackgroundColor(Color.parseColor("#8B9DC3"));
                    currentProfileIsContact = false;
                }
            }


            @Override
            public void onFailed(DatabaseError databaseError) {
                System.out.println("-- Get User Contacts Failed --");
            }
        });
    }

    public void addUserContact(final String baseUid, final String targetUid) {

        mReadDataOnce("users", baseUid, new OnGetDataListener() {
            @Override
            public void onStart() {
                //DO SOME THING WHEN START GET DATA HERE
            }

            @Override
            public void onSuccess(DataSnapshot data) {
                boolean hasContacts = false;

                for (DataSnapshot userField : data.getChildren()) {
                    if (userField.getKey().toString().equals("contacts")) {
                        hasContacts = true;
                    }
                }

                if(hasContacts) {
                    boolean isContact = false;

                    for (DataSnapshot contact : data.child("contacts").getChildren()) {
                        if (contact.getValue().toString().equals(targetUid)) {
                            isContact = true;
                        }
                    }

                    if (!isContact) {
                        FirebaseDatabase.getInstance().getReference()
                                .child("users").child(baseUid)
                                .child("contacts").push().setValue(targetUid);
                        System.out.println("Contact added!");
                    }
                } else {
                    FirebaseDatabase.getInstance().getReference().child("users")
                            .child(baseUid).child("contacts").push().setValue(targetUid);
                }
            }

            @Override
            public void onFailed(DatabaseError databaseError) {
                System.out.println("Removing Contact from User: " + baseUid + "failed: User DNE");
            }
        });
    }

    public void rmUserContact(final String baseUid, final String targetUid) {

        mReadDataOnce(baseUid, new OnGetDataListener() {
            @Override
            public void onStart() {
                //DO SOME THING WHEN START GET DATA HERE
            }

            @Override
            public void onSuccess(DataSnapshot data) {

                if (data.exists()) {
                    boolean isContact = false;
                    String targetKey = "";

                    for (DataSnapshot keyDs : data.getChildren()) {

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
                    currentProfileIsContact = false;
                } else {
                    Log.d(TAG, "User: " + baseUid + " does not Exist");
                }
            }

            @Override
            public void onFailed(DatabaseError databaseError) {
                System.out.println("-- Remove User Contact Failed --");
            }
        });
    }

    //==============================================================================================
    // Helper Functions
    //==============================================================================================

    private void initListeners() {
        username = (TextView) findViewById(R.id.user_name);
        twitter = (TextView) findViewById(R.id.user_twitter);
        facebook = (TextView) findViewById(R.id.user_facebook);
        email = (TextView) findViewById(R.id.user_email);
        userphone = (TextView) findViewById(R.id.user_phone);
        button = (Button) findViewById(R.id.add_friend_button);
        textViewGithub = (TextView) findViewById(R.id.textViewGithub);
        textViewInstagram = (TextView) findViewById(R.id.textViewInstagram);
        textViewSnapchat = (TextView) findViewById(R.id.textViewSnapchat);
        textViewLinkedin = (TextView) findViewById(R.id.textViewLinkedIn);

        findViewById(R.id.add_friend_button).setOnClickListener(new Button.OnClickListener(){
            public void onClick(View v) {
                if(currentProfileIsContact){removeContact();}else{
                    addContact();}
            }
        });
    }

    public void addContact(){
        String yourUid = user.getUid();
        addUserContact(yourUid,uid);
        addUserContact(uid,yourUid); //takes care of if the person is or isnt a contact
        button.setBackgroundColor(Color.parseColor("#CB8383"));
        button.setText("Remove contact");
        currentProfileIsContact = true;
    }

    public void removeContact(){
        rmUserContact(user.getUid(), uid);
        rmUserContact(uid, user.getUid());
        button.setText("Add contact");
        button.setBackgroundColor(Color.parseColor("#8B9DC3"));
        currentProfileIsContact = false;
    }
}
