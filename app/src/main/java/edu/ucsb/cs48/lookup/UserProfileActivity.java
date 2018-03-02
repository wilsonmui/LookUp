package edu.ucsb.cs48.lookup;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.twitter.sdk.android.core.Callback;
import com.twitter.sdk.android.core.DefaultLogger;
import com.twitter.sdk.android.core.Result;
import com.twitter.sdk.android.core.Twitter;
import com.twitter.sdk.android.core.TwitterAuthConfig;
import com.twitter.sdk.android.core.TwitterAuthToken;
import com.twitter.sdk.android.core.TwitterConfig;
import com.twitter.sdk.android.core.TwitterCore;
import com.twitter.sdk.android.core.TwitterException;
import com.twitter.sdk.android.core.TwitterSession;
import com.twitter.sdk.android.core.identity.TwitterLoginButton;

import edu.ucsb.cs48.lookup.ContactInfo.Facebook;
/**
 * Created by deni on 2/8/18.
 */

public class UserProfileActivity extends AppCompatActivity implements View.OnClickListener{

    //==============================================================================================
    // Declare Variables
    //==============================================================================================
    private FirebaseAuth mAuth;

    final String URL_TWITTER_SIGN_IN = "http://androidsmile.com/lab/twitter/sign_in.php";

    //    private FirebaseDatabase database;
    private TextView displayName, emailAddress, phoneNumber, textViewFacebook, textViewTwitter;

    //    private User currentUser;
    private Switch switchFacebook, switchTwitter;
    private Facebook facebook;
    private String userID;
    private DatabaseReference mDatabase;

    private DatabaseReference userRef, emailRef, phoneRef, facebookRef, twitterRef;

    TwitterLoginButton loginButton;
    //==============================================================================================
    // On Create Setup
    //==============================================================================================
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Check if User is Authenticated
        mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser == null) {
            finish();
            startActivity(new Intent(this, SignInPageActivity.class));
        }
        TwitterAuthConfig authConfig = new TwitterAuthConfig(getResources().getString(R.string.consumer_key), getResources().getString(R.string.secret_key));
        TwitterConfig config = new TwitterConfig.Builder(this)
                .logger(new DefaultLogger(Log.DEBUG))
                .twitterAuthConfig(authConfig)
                .debug(true)
                .build();
        Twitter.initialize(config);

        setContentView(R.layout.user_profile_page);


        loginButton = (TwitterLoginButton) findViewById(R.id.login_button);
        loginButton.setCallback(new Callback<TwitterSession>() {
            @Override
            public void success(Result<TwitterSession> result) {
                TwitterSession session = TwitterCore.getInstance().getSessionManager().getActiveSession();
                TwitterAuthToken authToken = session.getAuthToken();
                String token = authToken.token;
                String secret = authToken.secret;

                login(session);
            }

            @Override
            public void failure(TwitterException exception) {
                Toast.makeText(UserProfileActivity.this, "Authentication Failed!", Toast.LENGTH_LONG).show();
            }
        });

        initListeners();
        loadUserData();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Pass the activity result to the login button.
        loginButton.onActivityResult(requestCode, resultCode, data);
    }


    //==============================================================================================
    // Helper Functions
    //==============================================================================================
    private void initListeners() {
        textViewFacebook = (TextView) findViewById(R.id.textViewFacebook);
        textViewTwitter = (TextView) findViewById(R.id.textViewTwitter);
        switchFacebook = (Switch)findViewById(R.id.switchFacebook);
        switchTwitter = (Switch)findViewById(R.id.switchTwitter);
        displayName = (TextView) findViewById(R.id.displayName);
        phoneNumber = (TextView) findViewById(R.id.phoneNumber);
        emailAddress = (TextView) findViewById(R.id.emailAddress);

        switchTwitter.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    //facebook.connect();
//                    currentUser.addVisibleContactInfo(facebook);
                }
                else {
                    //signIn();
//                    facebook.disconnect();
//                    currentUser.rmVisibleContactInfo(facebook);
                }
            }
        });

        //facebook = new Facebook();
        switchFacebook.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                if (isChecked) {
                    // facebook.connect();
                }
                else {
                    // facebook.disconnect();
                }
            }
        });

    }

    private void loadUserData() {

        FirebaseUser currentUser = mAuth.getCurrentUser();
        String uid = currentUser.getUid();
        mDatabase = FirebaseDatabase.getInstance().getReference();

        userRef = mDatabase.child("users").child(uid).child("name");
        userRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                displayName.setText(dataSnapshot.getValue(String.class));
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        emailRef = mDatabase.child("users").child(uid).child("email");
        emailRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                emailAddress.setText(dataSnapshot.getValue(String.class));
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        phoneRef = mDatabase.child("users").child(uid).child("phone");
        phoneRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                phoneNumber.setText(dataSnapshot.getValue(String.class));
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        facebookRef = mDatabase.child("users").child(uid).child("facebook");
        facebookRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                textViewFacebook.setText(dataSnapshot.getValue(String.class));
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        twitterRef = mDatabase.child("users").child(uid).child("twitter");
        twitterRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                textViewTwitter.setText(dataSnapshot.getValue(String.class));
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }


    @Override
    public void onClick(View view) {
        // nothing
    }


    public void login(TwitterSession session) {
        String username = session.getUserName();
        textViewTwitter.setText(username);

    }


}

