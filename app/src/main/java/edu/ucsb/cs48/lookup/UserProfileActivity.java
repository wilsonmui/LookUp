package edu.ucsb.cs48.lookup;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
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
import java.util.HashMap;
import java.util.Map;

import edu.ucsb.cs48.lookup.ContactInfo.Facebook;
/**
 * Created by deni on 2/8/18.
 */

public class UserProfileActivity extends AppCompatActivity implements View.OnClickListener{

    //==============================================================================================
    // Declare Variables
    //==============================================================================================
    private FirebaseAuth mAuth;

    //    private FirebaseDatabase database;
    private TextView displayName, emailAddress, phoneNumber, textViewFacebook, textViewTwitter;

    //    private User currentUser;
    private Switch switchFacebook, switchTwitter;
    private Facebook facebook;
    private DatabaseReference mDatabase;
    private DatabaseReference userRef, emailRef, phoneRef, facebookRef, twitterRef;

    private TwitterLoginButton loginButton;

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

    private void initTwitterConfig() {
        TwitterAuthConfig authConfig = new TwitterAuthConfig(getResources().getString(R.string.consumer_key), getResources().getString(R.string.secret_key));
        TwitterConfig config = new TwitterConfig.Builder(this)
                .logger(new DefaultLogger(Log.DEBUG))
                .twitterAuthConfig(authConfig)
                .debug(true)
                .build();
        Twitter.initialize(config);
    }

    private void initListeners() {
        textViewFacebook = (TextView) findViewById(R.id.textViewFacebook);
        textViewTwitter = (TextView) findViewById(R.id.textViewTwitter);
        switchFacebook = (Switch)findViewById(R.id.switchFacebook);
        displayName = (TextView) findViewById(R.id.displayName);
        phoneNumber = (TextView) findViewById(R.id.phoneNumber);
        emailAddress = (TextView) findViewById(R.id.emailAddress);
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
        loadUserField(userRef, displayName);

        emailRef = mDatabase.child("users").child(uid).child("email");
        loadUserField(emailRef, emailAddress);

        phoneRef = mDatabase.child("users").child(uid).child("phone");
        loadUserField(phoneRef, phoneNumber);

        facebookRef = mDatabase.child("users").child(uid).child("facebook");
        loadUserField(facebookRef, textViewFacebook);

        twitterRef = mDatabase.child("users").child(uid).child("twitter");
        loadUserField(twitterRef, textViewTwitter);
    }


    public void loadUserField(DatabaseReference databaseReference, final TextView textView) {
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                textView.setText(dataSnapshot.getValue(String.class));
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

        FirebaseUser currentUser = mAuth.getCurrentUser();
        String uid = currentUser.getUid();
        mDatabase = FirebaseDatabase.getInstance().getReference();

        Map<String, Object> childUpdates = new HashMap<>();
        childUpdates.put("/users/" + uid + "/twitter", username);
        mDatabase.updateChildren(childUpdates);
    }


}

