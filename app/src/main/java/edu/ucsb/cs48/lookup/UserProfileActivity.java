package edu.ucsb.cs48.lookup;

import edu.ucsb.cs48.lookup.ContactInfo.Facebook;
import android.content.Context;
import android.content.Intent;
import android.nfc.Tag;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
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
import com.squareup.picasso.Picasso;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.util.Arrays;
import static android.content.ContentValues.TAG;
import static android.widget.Toast.*;
import static java.lang.System.load;
import static java.lang.System.out;

/**
 * Created by deni on 2/8/18.
 */

public class UserProfileActivity extends AppCompatActivity implements View.OnClickListener{

    //==============================================================================================
    // Declare Variables
    //==============================================================================================
    private FirebaseAuth mAuth;
  
    private TextView displayName, emailAddress, phoneNumber, textViewTwitter, facebookLink;
    private TextView textViewSnapchat, textViewInstagram, textViewGithub, textViewLinkedin;
    
    private LoginButton buttonConnectToFacebook;
    private TwitterLoginButton loginButton;
    private ImageView profilePic;
    private Button buttonEditProfile, buttonDeleteAccount;
    private String facebookID;
    private DatabaseReference mDatabase;
    private Context mContext;
    private CallbackManager callbackManager;

    private DatabaseReference userRef, nameRef, emailRef, phoneRef, facebookRef, profilePicRef, twitterRef;
    private DatabaseReference snapchatRef, instagramRef, githubRef, linkedinRef;

    //==============================================================================================
    // On Create Setup
    //==============================================================================================
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Check if User is Authenticated
        mAuth = FirebaseAuth.getInstance();

        mContext = getApplicationContext();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser == null) {
            finish();
            startActivity(new Intent(this, SignInPageActivity.class));
        }

        initTwitterConfig();

        setContentView(R.layout.user_profile_page);

        initListeners();

        loadUserData();

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Pass the activity result to the login button.
        loginButton.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
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
        textViewTwitter = (TextView) findViewById(R.id.textViewTwitter);
        displayName = (TextView) findViewById(R.id.displayName);
        phoneNumber = (TextView) findViewById(R.id.phoneNumber);
        emailAddress = (TextView) findViewById(R.id.emailAddress);
        loginButton = (TwitterLoginButton) findViewById(R.id.login_button);
        buttonEditProfile =  (Button) findViewById(R.id.buttonEditProfile);
        facebookLink = (TextView) findViewById(R.id.facebookLink);
        buttonDeleteAccount = (Button) findViewById(R.id.buttonDeleteAccount);
        buttonConnectToFacebook = (LoginButton) findViewById(R.id.buttonConnectToFacebook);
        textViewGithub = (TextView) findViewById(R.id.textViewGithub);
        textViewInstagram = (TextView) findViewById(R.id.textViewInstagram);
        textViewSnapchat = (TextView) findViewById(R.id.textViewSnapchat);
        textViewLinkedin = (TextView) findViewById(R.id.textViewLinkedIn);


        buttonEditProfile.setOnClickListener(this);
        buttonDeleteAccount.setOnClickListener(this);

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
                makeText(UserProfileActivity.this, "Authentication Failed!", LENGTH_LONG).show();
            }
        });

    }

    private void loadUserData() {

        FirebaseUser currentUser = mAuth.getCurrentUser();

        try {
            String uid = currentUser.getUid();
            mDatabase = FirebaseDatabase.getInstance().getReference();
            userRef = mDatabase.child("users").child(uid);

            nameRef = userRef.child("name");
            loadUserField(nameRef, displayName);

            emailRef = userRef.child("email");
            loadUserField(emailRef, emailAddress);

            phoneRef = userRef.child("phone");
            loadUserField(phoneRef, phoneNumber);

            snapchatRef = userRef.child("snapchat");
            loadUserField(snapchatRef, textViewSnapchat);

            instagramRef = userRef.child("instagram");
            loadUserField(instagramRef, textViewInstagram, "instagram");

            githubRef = userRef.child("github");
            loadUserField(githubRef, textViewGithub, "github");

            linkedinRef = userRef.child("linkedin");
            loadUserField(linkedinRef, textViewLinkedin, "linkedin");


            profilePicRef = userRef.child("profilePic");
            profilePicRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(final DataSnapshot dataSnapshot) {
                    if (dataSnapshot.getValue(String.class) != null && dataSnapshot.exists()) {
                        profilePic = (ImageView) findViewById(R.id.profilePic);
                        Glide.with(getApplicationContext())
                                .load(dataSnapshot.getValue(String.class))
                                .override(100, 100)
                                .into(profilePic);
                        //Picasso.with(mContext).load(dataSnapshot.getValue(String.class)).centerCrop().fit().into(profilePic);
                    } else {
                        Log.d(TAG, "Database Failure: could not load pic");
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {}
            });

            facebookRef = userRef.child("facebook");
            facebookRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if(dataSnapshot.exists()) {

                        if (!dataSnapshot.getValue(String.class).equals("")) {
                            buttonConnectToFacebook.setVisibility(View.GONE);
                            facebookLink.setText("https://facebook.com/" + dataSnapshot.getValue(String.class));
                        } else {
                            Log.d(TAG, "not connected to fb");
                            facebookLink.setText("");
                            FacebookSdk.sdkInitialize(getApplicationContext());
                            buttonConnectToFacebook.setVisibility(View.VISIBLE);
                            buttonConnectToFacebook.setReadPermissions(Arrays.asList("public_profile", "email"));
                            callbackManager = CallbackManager.Factory.create();
                            buttonConnectToFacebook.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
                                @Override
                                public void onSuccess(LoginResult loginResult) {
                                    try {
                                        Log.d(TAG, "onSuccess started");
                                        GraphRequest request = GraphRequest.newMeRequest(loginResult.getAccessToken(),
                                                new GraphRequest.GraphJSONObjectCallback() {
                                                    @Override
                                                    public void onCompleted(JSONObject object, GraphResponse response) {
                                                        try {
                                                            facebookID = object.getString("id");
                                                            Log.d(TAG, "FB id: " + facebookID);
                                                        } catch (Exception e) {
                                                            e.printStackTrace();
                                                            Log.d(TAG, "FB link unsuccessful");
                                                        }
                                                    }
                                                });
                                        Bundle parameters = new Bundle();
                                        parameters.putString("fields", "link");
                                        request.setParameters(parameters);
                                        request.executeAsync();
                                    } catch (Exception e) {
                                        Log.d("FACEBOOK ERROR", "cancelled");
                                    }
                                    setResult(RESULT_OK);
                                    Log.d(TAG, "facebook:onSuccess:" + loginResult);
                                    handleFacebookAccessToken(loginResult.getAccessToken());
                                }

                                @Override
                                public void onCancel() {
                                    setResult(RESULT_CANCELED);
                                    Log.d(TAG, "facebook:onCancel");
                                    finish();
                                }

                                @Override
                                public void onError(FacebookException exception) {
                                    Log.d(TAG, "facebook:onError", exception);
                                }
                            });
                        }
                    }

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    //...
                }
            });

            twitterRef = userRef.child("twitter");
            twitterRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if(dataSnapshot.exists()) {

                        if (!dataSnapshot.getValue(String.class).equals("")) {
                            loginButton.setVisibility(View.GONE);
                            textViewTwitter.setText("https://twitter.com/" + dataSnapshot.getValue(String.class));
                        } else {
                            textViewTwitter.setText("");
                            loginButton.setVisibility(View.VISIBLE);
                        }
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    //...
                }
            });
        } catch (Exception e) {
            Log.d(TAG, "Could not get user data, user was no found.", e);
        }

    }


    public void loadUserField(DatabaseReference databaseReference, final TextView textView) {
            databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()) {
                    textView.setText(dataSnapshot.getValue(String.class));
                } else {
                    Log.d(TAG, "Database Failure: could not load value(s)");
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.d(TAG, "Database Failure: could not load value(s)");
            }
        });
    }


    public void loadUserField(DatabaseReference databaseReference, final TextView textView, final String domain) {
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()) {
                    if(dataSnapshot.getValue(String.class).equals("")) {
                        textView.setText(dataSnapshot.getValue(String.class));
                    } else {
                        textView.setText("https://" + domain + ".com/" + dataSnapshot.getValue(String.class));
                    }
                } else {
                    Log.d(TAG, "Database Failure: could not load value(s)");
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.d(TAG, "Database Failure: could not load value(s)");
            }
        });
    }
  
    public void onClick(View view) {
        switch(view.getId()){
            case R.id.buttonEditProfile:
                finish();
                startActivity(new Intent(this, EditUserProfileActivity.class));
                break;

            case R.id.buttonDeleteAccount:
                deleteUser();
                break;
        }
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

    private void handleFacebookAccessToken(AccessToken token) {
        Log.d(TAG, "handleFacebookAccessToken:" + token);
        AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());
        String  facebookID = token.getUserId();
        saveFBUserID(facebookID);
    }

    private void saveFBUserID(String fbID) {
        userRef.child("facebook").setValue(fbID);
    }

    public void deleteUser() {
        final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        final String userUid = user.getUid();

        userRef = mDatabase.child("users").child(user.getUid());
        nameRef = userRef.child("name");
        emailRef = userRef.child("email");

        nameRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(final DataSnapshot nameDataSnapshot) {
                emailRef.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot emailDataSnapshot) {
                        if(emailDataSnapshot.exists()) {
                            // Get auth credentials from the user for re-authentication. The example below shows
                            // email and password credentials but there are multiple possible providers,
                            // such as GoogleAuthProvider or FacebookAuthProvider.
                            AuthCredential credential = EmailAuthProvider
                                    .getCredential(emailDataSnapshot.getValue(String.class), nameDataSnapshot.getValue(String.class));

                            // Prompt the user to re-provide their sign-in credentials
                            user.reauthenticate(credential)
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            Network.getInstance().rmUser(userUid);
                                            user.delete()
                                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                        @Override
                                                        public void onComplete(@NonNull Task<Void> task) {
                                                            if (task.isSuccessful()) {
                                                                updateUI();
                                                            }
                                                        }
                                                    });
                                        }
                                    });
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {}

                });

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {}
        });
    }

    public void updateUI() {
        finish();
        startActivity(new Intent(this, MainActivity.class));
    }
}

