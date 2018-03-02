package edu.ucsb.cs48.lookup;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import edu.ucsb.cs48.lookup.ContactInfo.Facebook;

import static android.content.ContentValues.TAG;

/**
 * Created by deni on 2/8/18.
 */

public class UserProfileActivity  extends AppCompatActivity implements View.OnClickListener{

    //==============================================================================================
    // Declare Variables
    //==============================================================================================
    private FirebaseAuth mAuth;
    private TextView displayName, emailAddress, phoneNumber, facebookLink;
    private ImageView profilePic;
//    private User currentUser;
    private Button buttonEditProfile;
    private Switch facebookSwitch;
    private Facebook facebook;
    private String userID;
    private DatabaseReference mDatabase;
    private Context mContext;

    private DatabaseReference userRef, nameRef, emailRef, phoneRef, facebookRef, profilePicRef;


    //==============================================================================================
    // On Create Setup
    //==============================================================================================
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.user_profile_page);

        buttonEditProfile =  (Button) findViewById(R.id.buttonEditProfile);
        buttonEditProfile.setOnClickListener(this);

        // Check if User is Authenticated
        mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser == null) {
            finish();
            startActivity(new Intent(this, SignInPageActivity.class));
        }

        mContext = getApplicationContext();

        userID = currentUser.getUid();
        mDatabase = FirebaseDatabase.getInstance().getReference();

        userRef = mDatabase.child("users").child(userID);

        nameRef= userRef.child("name");
        nameRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                displayName = (TextView) findViewById(R.id.displayName);
                displayName.setText(dataSnapshot.getValue(String.class));
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        emailRef = userRef.child("email");
        emailRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                emailAddress = (TextView) findViewById(R.id.emailAddress);
                emailAddress.setText(dataSnapshot.getValue(String.class));
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        phoneRef = userRef.child("phone");
        phoneRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                phoneNumber = (TextView) findViewById(R.id.phoneNumber);
                phoneNumber.setText(dataSnapshot.getValue(String.class));
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        facebookRef = userRef.child("facebookID");
        facebookRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                facebookLink = (TextView) findViewById(R.id.facebookLink);
                facebookLink.setText("https://facebook.com/" + dataSnapshot.getValue(String.class));
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        profilePicRef = userRef.child("profilePic");
        profilePicRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(final DataSnapshot dataSnapshot) {
                profilePic = (ImageView) findViewById(R.id.profilePic);
                Picasso.with(mContext).load(dataSnapshot.getValue(String.class)).fit().into(profilePic);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {}
        });

//        loadUserData();

//        facebook = new Facebook();
//        facebookSwitch = (Switch)findViewById(R.id.switchFacebook);
//        facebookSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
//            @Override
//            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
//                if (isChecked) {
//                    facebook.connect();
//                    currentUser.addVisibleContactInfo(facebook);
//                }
//                else {
//                    facebook.disconnect();
//                    currentUser.rmVisibleContactInfo(facebook);
//                }
//            }
//        });

    }

    //==============================================================================================
    // Action Listeners
    //==============================================================================================
    @Override
    public void onClick(View view) {
        switch(view.getId()){
            case R.id.buttonEditProfile:
                Log.d(TAG, "Edit profile button clicked!");
                finish();
                startActivity(new Intent(this, EditUserProfileActivity.class));
                break;
        }
    }

    //==============================================================================================
    // Helper Functions
    //==============================================================================================
    private void loadUserData() {
        FirebaseUser user = mAuth.getCurrentUser();

        String name = user.getDisplayName();
    }

    private void loadContactInfoObjects() {

    }

}