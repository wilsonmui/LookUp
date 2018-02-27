package edu.ucsb.cs48.lookup;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

/**
 * Created by Tina on 2/22/2018.
 */

public class EditUserProfileActivity extends AppCompatActivity implements View.OnClickListener {

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.edit_user_profile_page);

        mAuth = FirebaseAuth.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();

    }

    @Override
    protected void onStart() {
        super.onStart();

        setContentView(R.layout.edit_user_profile_page);


    }

    @Override
    public void onClick(View view) {

    }

}
