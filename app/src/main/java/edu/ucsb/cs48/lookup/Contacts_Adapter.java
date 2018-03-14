package edu.ucsb.cs48.lookup;

import android.content.Intent;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.lang.reflect.Array;
import java.util.ArrayList;

import static android.support.v4.content.ContextCompat.startActivity;
import static com.facebook.FacebookSdk.getApplicationContext;

/**
 * Created by Wilson on 2/25/18.
 *
 * Combines the RecyclerView in ContactsPageActivity with contact_layout
 */

public class Contacts_Adapter extends RecyclerView.Adapter<Contacts_Adapter.ContactViewHolder> {

    //uuid of people in contactList
    private ArrayList<String> contactList;
    DatabaseReference db;

    public Contacts_Adapter(ArrayList<String> contactList){
        this.contactList = contactList;
    }


    @Override
    public ContactViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.contact_layout,parent,false);

        return new ContactViewHolder(itemView);
    }

    /*
    must implement way to load image on contact card
     */
    @Override
    public void onBindViewHolder(final ContactViewHolder holder, int position) {
        final String userUid = contactList.get(position);

        db = FirebaseDatabase.getInstance().getReference()
                .child("users").child(userUid);
        db.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()) {
                    User user = new User(dataSnapshot.child("name").getValue().toString(),
                            dataSnapshot.child("email").getValue().toString(),
                            dataSnapshot.child("phone").getValue().toString(),
                            dataSnapshot.child("uid").getValue().toString(),
                            dataSnapshot.child("facebook").getValue().toString(),
                            dataSnapshot.child("twitter").getValue().toString(),
                            dataSnapshot.child("snapchat").getValue().toString(),
                            dataSnapshot.child("linkedin").getValue().toString(),
                            dataSnapshot.child("instagram").getValue().toString(),
                            dataSnapshot.child("github").getValue().toString(),
                            dataSnapshot.child("profilePic").getValue().toString());
                    holder.username.setText(user.getName());
                    Glide.with(getApplicationContext())
                            .load(user.getProfilePic())
                            .into(holder.userImg);
                }
                System.out.println("CONTACTS_ADAPTER: Database failure.");
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        //when contact is clicked, show their info and option to remove them
        holder.layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //open a new activity showing information about Contact
                //pass uid onto new activity
                Intent i = new Intent(view.getContext(), ContactProfileActivity.class);
                i.putExtra("uid", userUid);
                view.getContext().startActivity(i);

                /*
                in other activity:
                Bundle b = new Bundle();
                b = getIntent().getExtras();
                String uid = b.getString("uid")
                 */
            }
        });
    }



    @Override
    public int getItemCount() {
        return contactList.size();
    }

    public static class ContactViewHolder extends RecyclerView.ViewHolder{
        protected ConstraintLayout layout;
        protected TextView username;
        protected ImageView userImg;

        public ContactViewHolder(View v){
            super(v);

            username = (TextView) v.findViewById(R.id.user_name);
            layout = (ConstraintLayout) v.findViewById(R.id.holder_layout);
            userImg = (ImageView) v.findViewById(R.id.user_avatar);
        }

    }

}
