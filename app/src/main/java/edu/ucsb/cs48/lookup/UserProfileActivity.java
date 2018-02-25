package edu.ucsb.cs48.lookup;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.ToggleButton;

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
    private User currentUser;
    private Switch facebookSwitch;
    private Facebook facebook;

    //==============================================================================================
    // On Create Setup
    //==============================================================================================
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.user_profile_page);

        // Check if User is Authenticated
        mAuth = FirebaseAuth.getInstance();
        if(mAuth.getCurrentUser() == null) {
            finish();
            startActivity(new Intent(this, SignInPageActivity.class));
        }
        setContentView(R.layout.user_profile_page);
//        textViewUserEmail = (TextView) findViewById(R.id.textViewUserEmail);
//        textViewUserName = (TextView) findViewById(R.id.textViewUserName);
//        textViewUserName.setText(User.getDisplayName());
//        textViewUserEmail.setText(User.getEmail());
//
//        loadUserData();

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


}