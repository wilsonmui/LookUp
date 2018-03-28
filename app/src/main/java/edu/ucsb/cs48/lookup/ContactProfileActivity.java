package edu.ucsb.cs48.lookup;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Button;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
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
    private Button button;
    private ImageView userImg;
    DatabaseReference db, profilePicRef;
    public boolean currentProfileIsContact = false;

    private FirebaseUser user;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.contact_profile_page);
        Bundle b = new Bundle();
        b = getIntent().getExtras();
        uid = b.getString("uid");

        mAuth = FirebaseAuth.getInstance();
        if(mAuth.getCurrentUser() == null) {
            finish();
            startActivity(new Intent(this, SignInPageActivity.class));
        }
        user = mAuth.getCurrentUser();

        username = (TextView) findViewById(R.id.user_name);
        twitter = (TextView) findViewById(R.id.user_twitter);
        facebook = (TextView) findViewById(R.id.user_facebook);
        email = (TextView) findViewById(R.id.user_email);
        userphone = (TextView) findViewById(R.id.user_phone);
        button = (Button) findViewById(R.id.add_friend_button);
        userImg = (ImageView) findViewById(R.id.user_img);

        db = FirebaseDatabase.getInstance().getReference();

        isContact(user.getUid(), uid);

        loadUserField(db.child("users").child(uid).child("email"), email);
        loadUserField(db.child("users").child(uid).child("phone"), userphone);
        loadUserField(db.child("users").child(uid).child("name"), username);
        loadUserSocialField(db.child("users").child(uid).child("facebook"), facebook);
        loadUserSocialField(db.child("users").child(uid).child("twitter"), twitter);

        profilePicRef = db.child("users").child(uid).child("profilePic");
        profilePicRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(final DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue(String.class) != null && !dataSnapshot.getValue(String.class).isEmpty()) {
                    Glide.with(getApplicationContext())
                            .load(dataSnapshot.getValue(String.class))
                            .override(100, 100)
                            .into(userImg);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {}
        });

        findViewById(R.id.add_friend_button).setOnClickListener(new Button.OnClickListener(){
            public void onClick(View v) {
                if(currentProfileIsContact){removeContact();}else{addFriend();}
            }
        });
    }


    public void isContact(final String baseUid, final String targetUid){
            mReadDataOnce(baseUid, new OnGetDataListener() {
                @Override
                public void onStart() {
                    //DO SOME THING WHEN START GET DATA HERE
                }

                @Override
                public void onSuccess(DataSnapshot data) {
                    boolean b = false;
                    for (DataSnapshot contactsDs : data.getChildren()) {
                        if(contactsDs.getValue().toString().equals(targetUid)) {
                            System.out.println(contactsDs.getValue());
                            b = true;
                        }
                    }
                    if(b) {
                        button.setText("Remove contact");
                        button.setBackgroundColor(Color.parseColor("#CB8383"));
                        currentProfileIsContact = true;
                    } else {
                        button.setText("Add contact");
                        button.setBackgroundColor(Color.parseColor("#8B9DC3"));
                        currentProfileIsContact = false;
                    }
                }


                @Override
                public void onFailed(DatabaseError databaseError) {
                    System.out.println("-- Get User Contacts Failed --");
                }
            });
        }


    public void addFriend(){
        //if this user already is a contact,
        String yourUid = user.getUid();
        Network.getInstance().addUserContact(yourUid,uid);
        Network.getInstance().addUserContact(uid,yourUid); //takes care of if the person is or isnt a contact
        button.setBackgroundColor(Color.parseColor("#CB8383"));
        button.setText("Remove contact");
        currentProfileIsContact = true;
    }

    public void mReadDataOnce(String uid, final OnGetDataListener listener) {
        listener.onStart();
        FirebaseDatabase.getInstance().getReference().child("users").child(uid).child("contacts").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                listener.onSuccess(dataSnapshot);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                listener.onFailed(databaseError);
            }
        });
    }



    public void removeContact(){
        Network.getInstance().rmUserContact(user.getUid(), uid);
        Network.getInstance().rmUserContact(uid, user.getUid());
        button.setText("Add contact");
        button.setBackgroundColor(Color.parseColor("#8B9DC3"));
        currentProfileIsContact = false;
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

    public void loadUserSocialField(DatabaseReference databaseReference, final TextView textView) {
        System.out.println("LOAD");
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                textView.setText("https://" + dataSnapshot.getKey().toString() + "/" + dataSnapshot.getValue(String.class));
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e("Database Error:", "Error connecting to database");
            }
        });
    }

}
