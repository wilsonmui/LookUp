package edu.ucsb.cs48.lookup;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
//import com.facebook.CallbackManager;
import android.view.View;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

//    CallbackManager callbackManager;

    //==============================================================================================
    // On Create Setup
    //==============================================================================================
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
//        callbackManager = CallbackManager.Factory.create();
        findViewById(R.id.get_started_button).setOnClickListener(this);
        findViewById(R.id.sign_in_button).setOnClickListener(this);
    }
    //==============================================================================================
    // Action Listeners
    //==============================================================================================
    @Override
    public void onClick(View view) {
        switch(view.getId()){
            case R.id.get_started_button:
                startActivity(new Intent(this, SignUpPageActivity.class));
                break;
            case R.id.sign_in_button:
                startActivity(new Intent(this, SignInPageActivity.class));
                break;
        }
    }
}
