package edu.ucsb.cs48.lookup;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.Profile;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.facebook.login.widget.ProfilePictureView;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Arrays;

import static android.content.ContentValues.TAG;

public class SignInPageActivity extends Activity {

    private CallbackManager callbackManager;
    private static final String EMAIL = "email";
    private static final String USER_POSTS = "user_posts";

    private TextView info;

    private FirebaseAuth mAuth;

    private ProfilePictureView profilePictureView;
    private TextView userNameView;


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        callbackManager.onActivityResult(requestCode, resultCode, data);
        super.onActivityResult(requestCode, resultCode, data);
    }


    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        FacebookSdk.sdkInitialize(getApplicationContext());

        setContentView(R.layout.sign_in_page);

        mAuth = FirebaseAuth.getInstance();

        callbackManager = CallbackManager.Factory.create();



        LoginButton loginButton = findViewById(R.id.fb_login_button);
        info = (TextView)findViewById(R.id.info);
        loginButton.setReadPermissions(Arrays.asList(EMAIL));


        // Callback registration
        loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                setResult(RESULT_OK);
                Log.d(TAG, "facebook:onSuccess:" + loginResult);
                handleFacebookAccessToken(loginResult.getAccessToken());
//                finish();
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


    private void updateUI(FirebaseUser currentUser) {
        Profile profile = Profile.getCurrentProfile();
        if (profile != null) {
//            profilePictureView.setProfileId(profile.getId());
//            userNameView
//                    .setText(String.format("%s %s",profile.getFirstName(), profile.getLastName()));
        } else {
//            profilePictureView.setProfileId(null);
//            userNameView.setText(getString(R.string.welcome));
        }
    }

    private void handleFacebookAccessToken(AccessToken token) {
        Log.d(TAG, "handleFacebookAccessToken:" + token);
        AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                       if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithCredential:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            updateUI(user);
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithCredential:failure", task.getException());
                            Toast.makeText(SignInPageActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                            updateUI(null);
                        }
                    }
                });
    }

    //==============================================================================================
    // Action Listeners
    //==============================================================================================
    @Override
    public void onClick(View view) {
        switch(view.getId()) {
            case R.id.buttonSignIn:
                userLogin();
                break;
            case R.id.textViewSignUp:
                startActivity(new Intent(this, SignUpPageActivity.class));
                break;
        }
    }
}