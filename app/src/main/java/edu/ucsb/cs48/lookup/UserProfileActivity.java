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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

/**
 * Created by deni on 2/8/18.
 */

public class UserProfileActivity  extends AppCompatActivity implements View.OnClickListener{

    //==============================================================================================
    // Declare Variables
    //==============================================================================================
    private FirebaseAuth mAuth;

    final String URL_TWITTER_SIGN_IN = "http://androidsmile.com/lab/twitter/sign_in.php";
    final String URL_TWITTER_GET_USER_TIMELINE = "http://androidsmile.com/lab/twitter/get_user_timeline.php";

    //    private FirebaseDatabase database;
    private TextView textViewUserEmail, textViewUserName;
    private TextView displayName, emailAddress, phoneNumber, facebookLink, textViewTwitter;

    //    private User currentUser;
    private Switch facebookSwitch;
    private Facebook facebook;
    private String userID;
    private DatabaseReference mDatabase;
    private DatabaseReference databaseRef;

    private FirebaseDatabase database;
    private DatabaseReference userRef, emailRef, phoneRef, facebookRef;

    //==============================================================================================
    // On Create Setup
    //==============================================================================================
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.user_profile_page);

        // Check if User is Authenticated
        mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser == null) {
            finish();
            startActivity(new Intent(this, SignInPageActivity.class));
        }

        setContentView(R.layout.user_profile_page);

        userID = currentUser.getUid();
        mDatabase = FirebaseDatabase.getInstance().getReference();

        userRef = mDatabase.child("users").child(userID).child("name");
        userRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                displayName = (TextView) findViewById(R.id.displayName);
                displayName.setText(dataSnapshot.getValue(String.class));
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        emailRef = mDatabase.child("users").child(userID).child("email");
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

        phoneRef = mDatabase.child("users").child(userID).child("phone");
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

        facebookRef = mDatabase.child("users").child(userID).child("facebook");
        facebookRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                facebookLink = (TextView) findViewById(R.id.facebookLink);
                facebookLink.setText(dataSnapshot.getValue(String.class));
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        // Twitter sign in
        textViewTwitter = (TextView) findViewById(R.id.textViewTwitter);
        signIn();


        facebook = new Facebook();
        facebookSwitch = (Switch)findViewById(R.id.switchFacebook);
        facebookSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                if (isChecked) {
                    facebook.connect();
//                    currentUser.addVisibleContactInfo(facebook);
                }
                else {
                    facebook.disconnect();
//                    currentUser.rmVisibleContactInfo(facebook);
                }
            }
        });

    }

    //==============================================================================================
    // Action Listeners
    //==============================================================================================
    @Override
    public void onClick(View view) {
        switch(view.getId()){
//            case R.id.switchFacebook:
//                if ()
//                Facebook facebook = new Facebook();
//                facebook.connect();
//                currentUser.addVisibleContactiInfo(facebook);
//                break;
//            case R.id.switchTwitter:
//                break;
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

    /*
       show dialog with webview to sign in
    */
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


    /*
        this interface is used to get json from webview
    */
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
                textViewTwitter.setText(oauthResult.getScreenName());;
            }
        }
    }
}

