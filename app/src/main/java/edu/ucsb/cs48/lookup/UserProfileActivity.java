package edu.ucsb.cs48.lookup;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.security.Provider;

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

        setContentView(R.layout.user_profile_page);

        initListeners();
        loadUserData();
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

        facebook = new Facebook();
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

    private void signIn() {

        final Dialog authDialog = new Dialog(this);

        WebView webview = new WebView(this);
        authDialog.setContentView(webview);
        webview.getSettings().setJavaScriptEnabled(true);
        webview.loadUrl(URL_TWITTER_SIGN_IN);

        webview.setWebViewClient(new WebViewClient() {

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                if (url.contains("callback.php")) {
                    view.loadUrl("javascript:JsonViewer.onJsonReceived(document.getElementsByTagName('body')[0].innerHTML);");
                    authDialog.dismiss();
                }
            }
        });
        webview.addJavascriptInterface(new MyJavaScriptInterface(getApplicationContext()), "JsonViewer");
        authDialog.setCancelable(false);
        authDialog.show();

    }

    @Override
    public void onClick(View view) {
        // nothing
    }


    class MyJavaScriptInterface {
        private Context ctx;

        MyJavaScriptInterface(Context ctx) {
            this.ctx = ctx;
        }

        @JavascriptInterface
        public void onJsonReceived(String json) {
            Gson gson = new GsonBuilder().create();
            final TwitterOauthResult oauthResult = gson.fromJson(json, TwitterOauthResult.class);
            if (oauthResult != null && oauthResult.getOauthToken() != null && oauthResult.getOauthTokenSecret() != null) {
                textViewTwitter.setText(oauthResult.getScreenName());
            }
        }
    }
}

