package edu.ucsb.cs48.lookup;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;


public class DisplayUsersPageActivity extends AppCompatActivity{
    private FirebaseAuth mAuth;
    protected TextView name;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.display_users_page);
        TextView userDisplayName = (TextView)findViewById(R.id.userDisplayName);

        FirebaseUser user = mAuth.getInstance().getCurrentUser();

        if (user != null) {
            userDisplayName.setText(user.getDisplayName());
        }
        else
            userDisplayName.setText("User is null!");
    }

}
