package edu.ucsb.cs48.lookup;

import java.util.ArrayList;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;



public class DisplayUsersPageActivity extends AppCompatActivity{
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase, userName, userParent;
    private TextView displayName;
    ArrayList<User> userList = new ArrayList<>();
    RecyclerView mRecyclerView;
    RecyclerView.Adapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.display_users_page);

        // Check if User is Authenticated
        mAuth = FirebaseAuth.getInstance();
        final FirebaseUser user = mAuth.getCurrentUser();

        // Variable Set up
        final String userID = user.getUid();
        mDatabase = FirebaseDatabase.getInstance().getReference();
        userName = mDatabase.child("users").child(userID).child("name");

        // Display User's Name - Successful
        userName.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                displayName = findViewById(R.id.userDisplayName);
                displayName.setText(dataSnapshot.getValue(String.class));
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {}
        });


        // Attempt to display all User's names from FireBase
        userParent = mDatabase.child("users"); // The project root node
        mRecyclerView = (RecyclerView) findViewById(R.id.recyclerView); // RecycleView on page
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(DisplayUsersPageActivity.this));

        userParent.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot ds : dataSnapshot.getChildren()){
                    User someUser = ds.getValue(User.class);
                    userList.add(someUser);
                }
                adapter = new DisplayUsersAdapter(DisplayUsersPageActivity.this, userList);
                mRecyclerView.setAdapter(adapter);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {}

        });
    }
}
