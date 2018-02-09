package edu.ucsb.cs48.lookup;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

/**
 * Created by deni on 2/8/18.
 */

public class UserProfileActivity  extends AppCompatActivity implements View.OnClickListener{
    //==============================================================================================
    // On Create Setup
    //==============================================================================================
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_profile_page);
    }
    //==============================================================================================
    // Action Listeners
    //==============================================================================================
    @Override
    public void onClick(View view) {
//        switch(view.getId()){
//            case R.id.get_started_button:
//                startActivity(new Intent(this, SignUpPageActivity.class));
//                break;
//        }
    }
}
