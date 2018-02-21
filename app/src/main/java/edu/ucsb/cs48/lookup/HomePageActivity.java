package edu.ucsb.cs48.lookup;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.provider.MediaStore;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import android.os.Environment;
import android.net.Uri;
import android.support.v4.content.FileProvider;
import android.widget.Button;

import com.google.firebase.auth.FirebaseAuth;

/**
 * Created by deni on 2/5/18.
 */

public class HomePageActivity extends AppCompatActivity implements View.OnClickListener {

    //==============================================================================================
    // Declare Variables
    //==============================================================================================
    static final int REQUEST_TAKE_PHOTO = 1;
    String mCurrentPhotoPath; //pathname for photo
    private FirebaseAuth mAuth;
    private Button buttonSignOut;


    //==============================================================================================
    // On Create Setup
    //==============================================================================================
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        // Check if User is Authenticated
        mAuth = FirebaseAuth.getInstance();
        if(mAuth.getCurrentUser() == null) {
            finish();
            startActivity(new Intent(this, SignInPageActivity.class));
        }

        // Layout Setup
        setContentView(R.layout.home_page);

        //Add ActionListeners
        buttonSignOut= (Button) findViewById(R.id.buttonSignOut);

        findViewById(R.id.scan_face_button).setOnClickListener(this);
        findViewById(R.id.user_profile_button).setOnClickListener(this);
        findViewById(R.id.contacts_button).setOnClickListener(this);
        findViewById(R.id.info_button).setOnClickListener(this);
        buttonSignOut.setOnClickListener(this);
    }
    //==============================================================================================
    // Action Listeners
    //==============================================================================================
    @Override
    public void onClick(View view) {
        switch(view.getId()) {
            case R.id.scan_face_button:
                //TODO open camera and do the thang
                dispatchTakePictureIntent();
                break;
            case R.id.user_profile_button:
                finish();
                startActivity(new Intent(this, UserProfileActivity.class));
                break;
            case R.id.contacts_button:
                //TODO contacts button selected
//                startActivity(new Intent(this, ContactsActivity.class)); //TODO implement ContactsActivity.class
                break;
            case R.id.info_button:
                finish();
                startActivity(new Intent(this, InfoPageActivity.class));
                break;
            case R.id.buttonSignOut:
                finish();
                mAuth.getInstance().signOut();
                startActivity(new Intent(this, SignInPageActivity.class));
                break;
        }
    }

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                // Error occurred while creating the File
                //TODO error message?
            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(this,
                        "com.example.android.fileprovider",
                        photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO);
            }
        }
    }


    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        mCurrentPhotoPath = image.getAbsolutePath();
        return image;
    }
}
