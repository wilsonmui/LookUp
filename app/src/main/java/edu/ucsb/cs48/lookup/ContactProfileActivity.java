package edu.ucsb.cs48.lookup;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;
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

        db = FirebaseDatabase.getInstance().getReference()
                .child("users").child(uid);
        db.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot userSnapshot : dataSnapshot.getChildren()) {
                    User user = userSnapshot.getValue(User.class);

                    username.setText(user.getName());
                    twitter.setText(user.getTwitterURl());
                    facebook.setText(user.getFacebookURL());
                    email.setText(user.getEmail());
                    userphone.setText(user.getPhone());
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

}
