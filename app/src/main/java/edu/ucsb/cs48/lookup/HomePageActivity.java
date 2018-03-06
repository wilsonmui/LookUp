package edu.ucsb.cs48.lookup;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.provider.MediaStore;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import android.os.Environment;
import android.net.Uri;
import android.support.v4.content.FileProvider;
import android.widget.Button;

import com.google.firebase.auth.FirebaseAuth;

/**
 * Created by deni on 2/5/18.
 */

public class HomePageActivity extends AppCompatActivity implements View.OnClickListener {

    //==============================================================================================
    // Declare Variables
    //==============================================================================================
    private FirebaseAuth mAuth;
    private Button buttonSignOut;

    //==============================================================================================
    // On Create Setup
    //==============================================================================================
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        // Check if User is Authenticated
        mAuth = FirebaseAuth.getInstance();
        if(mAuth.getCurrentUser() == null) {
            finish();
            startActivity(new Intent(this, SignInPageActivity.class));
        }

        // Layout Setup
        setContentView(R.layout.home_page);

        //Add ActionListeners
        buttonSignOut= (Button) findViewById(R.id.buttonSignOut);

        findViewById(R.id.scan_face_button).setOnClickListener(this);
        findViewById(R.id.view_code).setOnClickListener(this);
        findViewById(R.id.user_profile_button).setOnClickListener(this);
        findViewById(R.id.contacts_button).setOnClickListener(this);
        findViewById(R.id.info_button).setOnClickListener(this);
        findViewById(R.id.buttonDisplayUsers).setOnClickListener(this);
        buttonSignOut.setOnClickListener(this);
    }

    //==============================================================================================
    // Action Listeners
    //==============================================================================================
    @Override
    public void onClick(View view) {
        switch(view.getId()) {
            case R.id.scan_face_button:

                scanPerson();
                break;
            case R.id.user_profile_button:

                startActivity(new Intent(this, UserProfileActivity.class));
                break;
            case R.id.buttonDisplayUsers:

                startActivity(new Intent(this, DisplayUsersPageActivity.class));
                break;
            case R.id.contacts_button:
                startActivity(new Intent(this, ContactsPageActivity.class));
                break;
            case R.id.info_button:
                //finish();
                //startActivity(new Intent(this, InfoPageActivity.class));
                Network.getInstance().addUserContact("6DIZtspD3Egw17VUdnxCbnR1pfF2", "3HpDljXnxLObxxliJsL6dZnz4LF2");
                Network.getInstance().addUserContact("6DIZtspD3Egw17VUdnxCbnR1pfF2", "3PANpyEvjcear2ExIa1qygHjCAs2");
                break;
            case R.id.buttonSignOut:
                finish();
                mAuth.getInstance().signOut();
                startActivity(new Intent(this, SignInPageActivity.class));
                break;
            case R.id.view_code:

                startActivity(new Intent(this, GenerateCodeActivity.class));
                break;
        }
    }

    private void scanPerson(){
        startActivity(new Intent(this, CameraActivity.class));
    }
}
