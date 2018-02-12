package edu.ucsb.cs48.lookup;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.Parcelable;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.File;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by deni on 2/5/18.
 */

public class HomePageActivity extends AppCompatActivity implements View.OnClickListener {

    private Button mBtn;
    private Context context;
    private static final int SELECT_PICTURE_CAMARA = 101, SELECT_PICTURE = 201, CROP_IMAGE = 301;
    private Uri outputFileUri;
    String mCurrentPhotoPath;
    private Uri selectedImageUri;
    private File finalFile = null;
    private ImageView imageView;
//    private PermissionUtil permissionUtil;
    private int REQUEST_CAMERA = 401;

    public final String APP_TAG = "MyCustomApp";

    //==============================================================================================
    // On Create Setup
    //==============================================================================================
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home_page);
        //Add ActionListeners
        findViewById(R.id.scan_face_button).setOnClickListener(this);
        findViewById(R.id.user_profile_button).setOnClickListener(this);
        findViewById(R.id.contacts_button).setOnClickListener(this);
        findViewById(R.id.info_button).setOnClickListener(this);
    }
    //==============================================================================================
    // On Start setup
    //==============================================================================================
    @Override
    public void onStart() {
        super.onStart();

    }
    //==============================================================================================
    // Action Listeners
    //==============================================================================================
    @Override
    public void onClick(View view) {
        switch(view.getId()) {
            case R.id.scan_face_button:
                //TODO open camera and do the thing
                launchCamera();
                break;
            case R.id.user_profile_button:
                startActivity(new Intent(this, UserProfileActivity.class)); //TODO implement UserProfileActivity.class
                break;
            case R.id.contacts_button:
                //TODO contacts button selected
//                startActivity(new Intent(this, ContactsActivity.class)); //TODO implement ContactsActivity.class
                break;
            case R.id.info_button:
                //TODO info button selected, go to info page
                break;

        }
    }

    public void launchCamera() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N) {
            intent.putExtra(MediaStore.EXTRA_OUTPUT, getPhotoFileUri());
        } else {
            File file = new File(getPhotoFileUri().getPath());
            Uri photoUri = FileProvider.getUriForFile(getApplicationContext(), getApplicationContext().getPackageName() + ".provider", file);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
        }
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        if (intent.resolveActivity(getApplicationContext().getPackageManager()) != null) {
            startActivityForResult(intent, REQUEST_CAMERA);
        }
    }

    // Returns the Uri for a photo stored on disk given the fileName
    public Uri getPhotoFileUri(String fileName) {
        // Get safe storage directory for photos
        File mediaStorageDir = new File(
                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), APP_TAG);

        // Create the storage directory if it does not exist
        if (!mediaStorageDir.exists() && !mediaStorageDir.mkdirs()){
            Log.d(APP_TAG, "failed to create directory");
        }

        // Return the file target for the photo based on filename
        return Uri.fromFile(new File(mediaStorageDir.getPath() + File.separator + fileName));
    }

}
