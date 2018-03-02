package edu.ucsb.cs48.lookup;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.lang.reflect.Array;
import java.util.ArrayList;

import static android.support.v4.content.ContextCompat.startActivity;

/**
 * Created by Wilson on 2/25/18.
 *
 * Combines the RecyclerView in ContactsPageActivity with contact_layout
 */

public class Contacts_Adapter extends RecyclerView.Adapter<Contacts_Adapter.ContactViewHolder> {

    //uuid of people in contactList
    private ArrayList<String> contactList;

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
    public void onBindViewHolder(ContactViewHolder holder, int position) {
        final String UserUid = contactList.get(position);
        holder.username.setText(findUsername(UserUid));
        //holder.userImg.

        //when contact is clicked, show their info and option to remove them
        holder.username.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //open a new activity showing information about Contact
                //pass uid onto new activity

                Intent i = new Intent(view.getContext(), ContactsPageActivity.class);
                i.putExtra("uid", UserUid);
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
        protected TextView username;
        protected ImageView userImg;

        public ContactViewHolder(View v){
            super(v);

            username = (TextView) v.findViewById(R.id.user_name);
            userImg = (ImageView) v.findViewById(R.id.user_avatar);
        }

    }

    //find username given uuid
    public String findUsername(String uuid){
        return "null";
    }
    //find userImg given uuid
    public void findUserImg(String uuid){
        return;
    }
}
