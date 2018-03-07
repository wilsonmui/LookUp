package edu.ucsb.cs48.lookup;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Button;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

/**
 * Created by Wilson on 2/26/18.
 */


//shows User information about contact
public class ContactProfileActivity extends AppCompatActivity {
    private TextView username, twitter, facebook, email, userphone;
    private String uid;
    DatabaseReference db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.contact_profile_page);
        Bundle b = new Bundle();
        b = getIntent().getExtras();
        uid = b.getString("uid");
        Toast.makeText(this, "" + uid, Toast.LENGTH_SHORT).show();

        username = (TextView) findViewById(R.id.user_name);
        twitter = (TextView) findViewById(R.id.user_twitter);
        facebook = (TextView) findViewById(R.id.user_facebook);
        email = (TextView) findViewById(R.id.user_email);
        userphone = (TextView) findViewById(R.id.user_phone);

        db = FirebaseDatabase.getInstance().getReference();

        loadUserField(db.child("users").child(uid).child("email"), email);
        loadUserField(db.child("users").child(uid).child("phone"), userphone);
        loadUserField(db.child("users").child(uid).child("name"), username);
        loadUserField(db.child("users").child(uid).child("facebook"), facebook);
        loadUserField(db.child("users").child(uid).child("twitter"), twitter);

        findViewById(R.id.add_friend_button).setOnClickListener(new Button.OnClickListener(){
            public void onClick(View v) {
                sendUID();
            }
        });
    }

    private void sendUID(){
        //TODO send UID to network!!!

    }

    public void loadUserField(DatabaseReference databaseReference, final TextView textView) {
        System.out.println("LOAD");
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                textView.setText(dataSnapshot.getValue(String.class));
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e("Database Error:", "Error connecting to database");
            }
        });
    }

}
