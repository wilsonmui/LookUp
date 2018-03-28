package edu.ucsb.cs48.lookup;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

/*
This activity displays the current user's list of contacts. Should provide methods to pull up
contact info and remove them from contacts list.
Uses Contacts_Adapter to display RecyclerView
 */
public class ContactsPageActivity extends AppCompatActivity implements View.OnClickListener {

    //==============================================================================================
    // Declare Variables
    //==============================================================================================
    FirebaseAuth mAuth;
    ArrayList<String> filteredContacts = new ArrayList<>();
    ArrayList<String> contacts = new ArrayList<>();
    EditText search;
    RecyclerView contacts_list;
    DatabaseReference db;
    Contacts_Adapter ca;

    //==============================================================================================
    // On Create
    //==============================================================================================

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mAuth = FirebaseAuth.getInstance();
        if(mAuth.getCurrentUser() == null) {
            finish();
            startActivity(new Intent(this, SignInPageActivity.class));
        }

        setContentView(R.layout.activity_contacts_page);

        initListeners();

        try {
            String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
            loadContacts(uid, contacts_list);
        } catch(Exception e) {
            Log.e("Exception", "Load Contacts ", e);
        }

    }

    //==============================================================================================
    // Adapter Functions
    //==============================================================================================

    public void filterContacts(final String query){

        filteredContacts = new ArrayList<>();

        for(int i = 0; i < contacts.size(); i++){

            mReadDataOnce(contacts.get(i), new OnGetDataListener() {
                @Override
                public void onStart() {
                    //DO SOME THING WHEN START GET DATA HERE
                }

                @Override
                public void onSuccess(DataSnapshot data) {
                    if(data.exists()) {
                        String contactName = data.child("name").getValue().toString();

                        if (contactName.toLowerCase().contains(query.toLowerCase())){
                            filteredContacts.add(data.getKey());
                        }

                        ca.contactList = filteredContacts;
                        ca.notifyDataSetChanged();
                    }
                }


                @Override
                public void onFailed(DatabaseError databaseError) {
                    System.out.println("CONTACTS_ADAPTER: Database failure.");
                }
            });
        }
    }

    //==============================================================================================
    // Database Functions
    //==============================================================================================

    public void mReadDataOnce(String child, final OnGetDataListener listener) {
        listener.onStart();
        FirebaseDatabase.getInstance().getReference().child("users").child(child).child("contacts").addListenerForSingleValueEvent(new ValueEventListener() {
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

    public void loadContacts(final String uid, final RecyclerView contacts_list) {

        mReadDataOnce(uid, new OnGetDataListener() {
            @Override
            public void onStart() {
                //DO SOME THING WHEN START GET DATA HERE
            }

            @Override
            public void onSuccess(DataSnapshot data) {

                for (DataSnapshot contactsDs : data.getChildren()) {
                    contacts.add(contactsDs.getValue().toString());
                }

                ca = new Contacts_Adapter(contacts);

                contacts_list.setAdapter(ca);
            }


            @Override
            public void onFailed(DatabaseError databaseError) {
                System.out.println("-- Get User Contacts Failed --");
            }
        });
    }

    //==============================================================================================
    // Helper Functions
    //==============================================================================================

    public void initListeners() {
        findViewById(R.id.back_button).setOnClickListener(this);
        findViewById(R.id.search_button).setOnClickListener(this);

        search = (EditText) findViewById(R.id.search);
        contacts_list = (RecyclerView) findViewById(R.id.contacts_list);

        contacts_list.setHasFixedSize(true);
        LinearLayoutManager llm = new LinearLayoutManager(this);
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        contacts_list.setLayoutManager(llm);
    }

    @Override
    public void onClick(View view) {
        switch(view.getId()) {
            case R.id.back_button:
                startActivity(new Intent(this, HomePageActivity.class));
                break;
            case R.id.search_button:
                filterContacts(search.getText().toString());
                break;
        }
    }


}


