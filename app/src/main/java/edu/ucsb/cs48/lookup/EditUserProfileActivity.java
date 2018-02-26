package edu.ucsb.cs48.lookup;

import android.content.Intent;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.w3c.dom.Text;

import java.util.HashMap;

import static android.content.ContentValues.TAG;

/**
 * Created by Tina on 2/22/2018.
 */

public class EditUserProfileActivity extends AppCompatActivity implements View.OnClickListener {

    private FirebaseAuth mAuth;
    private TextView displayName, emailAddress, phoneNumber;
    private EditText editDisplayName, editEmailAddress, editPhoneNumber;
    private Button buttonSaveProfileEdits;
    private HashMap<String, String> editedProfileData;
    private DatabaseReference databaseRef, userRef, nameRef, emailRef, phoneRef;
    private String userID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.edit_user_profile_page);

        mAuth = FirebaseAuth.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();

        buttonSaveProfileEdits = (Button) findViewById(R.id.buttonSaveProfileEdits);
        buttonSaveProfileEdits.setOnClickListener(this);

        if (user == null) {
            finish();
            startActivity(new Intent(this, SignInPageActivity.class));
        }

        editedProfileData = new HashMap<String, String>();


        databaseRef = FirebaseDatabase.getInstance().getReference();
        userID = user.getUid();
        userRef = databaseRef.child("users").child(userID);

        nameRef = userRef.child("name");
        nameRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                editDisplayName= (EditText) findViewById(R.id.editDisplayName);
                editDisplayName.setText(dataSnapshot.getValue(String.class));
                editDisplayName.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

                    @Override
                    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

                    @Override
                    public void afterTextChanged(Editable editable) {
                        String newDisplayName = editable.toString();
                        editedProfileData.put("name", newDisplayName);
                    }
                });
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {}
        });

        emailRef = userRef.child("email");
        emailRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                emailAddress = (TextView) findViewById(R.id.editEmailAddress);
                emailAddress.setText(dataSnapshot.getValue(String.class));
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {}
        });

        phoneRef = userRef.child("phone");
        phoneRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                phoneNumber = (TextView) findViewById(R.id.editPhoneNumber);
                phoneNumber.setText(dataSnapshot.getValue(String.class));
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {}
        });

    }

    @Override
    protected void onStart() {
        super.onStart();

        setContentView(R.layout.edit_user_profile_page);


    }

    @Override
    public void onClick(View view) {
        Log.d(TAG, "something clicked!");
        switch (view.getId()) {
            case R.id.buttonSaveProfileEdits:
                Log.d(TAG, "Save button clicked!");
                updateDatabase();
                finish();
                startActivity(new Intent(this, UserProfileActivity.class));
                break;
        }
    }

    private void updateDatabase() {

        userRef.setValue(editedProfileData);

    }

}

