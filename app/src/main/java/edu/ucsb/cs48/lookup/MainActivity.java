package edu.ucsb.cs48.lookup;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    //==============================================================================================
    // On Create Setup
    //==============================================================================================
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        findViewById(R.id.get_started_button).setOnClickListener(this);
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
        }
    }
}
