package edu.ucsb.cs48.lookup;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import java.util.ArrayList;

/**
 * Created by Eliza on 3/1/2018.
 * This is an adapter class for dynamic user display.
 */

public class DisplayUsersAdapter extends RecyclerView.Adapter<DisplayUsersAdapter.ViewHolder> {

    Context context;
    ArrayList<User> userArrayList;

    public DisplayUsersAdapter(Context context, ArrayList<User> tempList) {
        this.userArrayList = tempList;
        this.context = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recyclerview_users, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        User user = userArrayList.get(position);
        holder.userNameTextView.setText(user.getName());
    }

    @Override
    public int getItemCount() {
        return userArrayList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        public TextView userNameTextView;

        public ViewHolder(View itemView) {
            super(itemView);
            userNameTextView = (TextView) itemView.findViewById(R.id.displayUserName);
        }
    }
}
