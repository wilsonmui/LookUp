package edu.ucsb.cs48.lookup;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;


import static android.content.ContentValues.TAG;

/**
 * Created by esuarez on 2/4/18.
 */

public class SignUpPageActivity extends AppCompatActivity implements View.OnClickListener {

    //==============================================================================================
    // Declare Variables
    //==============================================================================================
    EditText editTextEmail, editTextPassword;
    ProgressBar progressBar;
    private FirebaseAuth mAuth;

    //==============================================================================================
    // On Create Setup
    //==============================================================================================
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.sign_up_page);

        // Set up UI variables and Listeners
        editTextEmail = (EditText)findViewById(R.id.editTextEmail);
        editTextPassword = (EditText)findViewById(R.id.editTextPassword);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        findViewById(R.id.buttonSignUp).setOnClickListener(this);

        // Initialize the FirebaseAuth Instance
        mAuth = FirebaseAuth.getInstance();
    }

    //==============================================================================================
    // On Start setup
    //==============================================================================================
    @Override
    public void onStart() {
        super.onStart();

        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        updateUI(currentUser);
    }

    //==============================================================================================
    // Action Listeners
    //==============================================================================================
    @Override
    public void onClick(View view) {
        switch(view.getId()) {
            case R.id.buttonSignUp:
                registerUser();
                break;
            case R.id.textViewLogin:
                startActivity(new Intent(this, MainActivity.class));
                break;
        }
    }

    //==============================================================================================
    // Helper Functions
    //==============================================================================================
    private void updateUI(FirebaseUser currentUser) {

        if (currentUser != null) {
            // TODO:If user is logged in...
        } else {
            // TODO:If user is not logged in...
        }
    }

    private void registerUser() {

        // Sanitize Inputs
        String email = editTextEmail.getText().toString().trim();
        String password = editTextPassword.getText().toString().trim();

        if(email.isEmpty()) {
            editTextEmail.setError("Email is required");
            editTextEmail.requestFocus();
            return;
        }

        if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            editTextEmail.setError("Please enter a valid email");
            editTextEmail.requestFocus();
            return;
        }

        if (password.isEmpty()) {
            editTextPassword.setError("Password is required");
            editTextPassword.requestFocus();
            return;
        }

        if(password.length() < 6) {
            editTextPassword.setError("Minimum length of password should be 6");
            editTextPassword.requestFocus();
            return;
        }

        // Show Progress bar
        progressBar.setVisibility(View.VISIBLE);

        // If all fields pass all checks proceed to creation
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {

            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                progressBar.setVisibility(View.GONE);

                if (task.isSuccessful()) {

                    // Sign in success, update UI with the signed-in user's information
                    Log.d(TAG, "createUserWithEmail:success");
                    Toast.makeText(getApplicationContext(),"User Registered Successful", Toast.LENGTH_SHORT).show();

                    // Transition to new Activity
                    FirebaseUser user = mAuth.getCurrentUser();
                    updateUI(user);

                    //TODO: Finish home page
                    // startActivity(new Intent(home_page))
                } else {

                    // If sign in fails, display a message to the user.
                    Log.w(TAG, "createUserWithEmail:failure", task.getException());
                    Toast.makeText(SignUpPageActivity.this, "Authentication failed." + task.getException(),
                            Toast.LENGTH_SHORT).show();
                    updateUI(null);
                }
            }
        });
    }
}
