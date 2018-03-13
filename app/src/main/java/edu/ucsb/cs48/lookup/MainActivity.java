package edu.ucsb.cs48.lookup;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.nfc.Tag;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
//import edu.ucsb.cs48.lookup.Manifest.permission;
//import com.facebook.CallbackManager;
import android.util.Log;
import android.view.View;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.FirebaseApp;


import static android.content.ContentValues.TAG;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    //==============================================================================================
    // Declare Variables
    //==============================================================================================

    private FirebaseAuth mAuth;

    private static int PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE = 1, PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE = 2,
        PERMISSIONS_REQUEST_CAMERA = 3;

    //==============================================================================================
    // On Create Setup
    //==============================================================================================

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Check if User is Authenticated
        FirebaseApp.initializeApp(this);
        mAuth = FirebaseAuth.getInstance();
        if(mAuth.getCurrentUser() != null) {
            finish();
            startActivity(new Intent(this, HomePageActivity.class));
        }

        setContentView(R.layout.activity_main);

        initListeners();

    }

    @Override
    public void onClick(View view) {
        switch(view.getId()) {
            case R.id.buttonGetStarted:
                startActivity(new Intent(this, SignUpPageActivity.class));
                break;
            case R.id.buttonSignIn:
                startActivity(new Intent(this, SignInPageActivity.class));
                break;
            case R.id.info_button:
                finish();
                startActivity(new Intent(this, InfoPageActivity.class));
        }
    }

    //==============================================================================================
    // Helper Functions
    //==============================================================================================

    private void initListeners() {
        findViewById(R.id.buttonGetStarted).setOnClickListener(this);
        findViewById(R.id.buttonSignIn).setOnClickListener(this);
        findViewById(R.id.info_button).setOnClickListener(this);
    }

}
