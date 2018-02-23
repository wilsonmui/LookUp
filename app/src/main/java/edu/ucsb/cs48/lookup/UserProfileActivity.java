package edu.ucsb.cs48.lookup;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

/**
 * Created by deni on 2/8/18.
 */

public class UserProfileActivity  extends AppCompatActivity implements View.OnClickListener{

    //==============================================================================================
    // Declare Variables
    //==============================================================================================
    private FirebaseAuth mAuth;
    private TextView textViewUserEmail, textViewUserName;
    private Button buttonEditProfile;
    private User user;

    //==============================================================================================
    // On Create Setup
    //==============================================================================================
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mAuth = FirebaseAuth.getInstance();
        FirebaseUser User = mAuth.getCurrentUser();

        setContentView(R.layout.user_profile_page);

//        buttonEditProfile = (Button) findViewById(R.id.buttonEditProfile);
//        buttonEditProfile.setOnClickListener(this);
        findViewById(R.id.buttonEditProfile).setOnClickListener(this);

        textViewUserEmail = (TextView) findViewById(R.id.textViewUserEmail);
        textViewUserEmail.setText(User.getEmail());
        textViewUserName = (TextView) findViewById(R.id.textViewUserName);
        textViewUserName.setText(User.getDisplayName());

        loadUserInformation();



    }

    //==============================================================================================
    // On Start Setup
    //==============================================================================================
    @Override
    protected void onStart() {
        super.onStart();
        if (mAuth.getCurrentUser() == null) {
            finish();
            startActivity(new Intent(this, SignInPageActivity.class));
        }
        // Check if User is Authenticated
        mAuth = FirebaseAuth.getInstance();
        if(mAuth.getCurrentUser() == null) {
            finish();
            startActivity(new Intent(this, SignInPageActivity.class));
        }
        setContentView(R.layout.user_profile_page);

        FirebaseUser User = mAuth.getCurrentUser();
        textViewUserEmail = (TextView) findViewById(R.id.textViewUserEmail);
        textViewUserName = (TextView) findViewById(R.id.textViewUserName);
        textViewUserName.setText(User.getDisplayName());
        textViewUserEmail.setText(User.getEmail());

        loadUserInformation();
    }

    //==============================================================================================
    // Action Listeners
    //==============================================================================================
    @Override
    public void onClick(View view) {
        switch(view.getId()){
            case R.id.buttonEditProfile:
                finish();
                startActivity(new Intent(this, EditUserProfileActivity.class));
                break;
        }
    }

    //==============================================================================================
    // Helper Functions
    //==============================================================================================
    private void loadUserInformation() {
        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            textViewUserName.setText(user.getDisplayName());
            String displayName = user.getDisplayName();
        }
    }



}
