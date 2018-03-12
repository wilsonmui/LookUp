package edu.ucsb.cs48.lookup;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.provider.MediaStore;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import android.os.Environment;
import android.net.Uri;
import android.support.v4.content.FileProvider;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;

import static edu.ucsb.cs48.lookup.GenerateCodeActivity.QRcodeWidth;

/**
 * Created by deni on 2/5/18.
 */

public class HomePageActivity extends AppCompatActivity implements View.OnClickListener {

    //==============================================================================================
    // Declare Variables
    //==============================================================================================
    private FirebaseAuth mAuth;
    private Button buttonSignOut;
    ImageView imageView;
    Button button;
    public final static int QRcodeWidth = 500 ;
    Bitmap bitmap ;
    private FirebaseUser user;

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
        user = mAuth.getCurrentUser();

        // Layout Setup
        setContentView(R.layout.home_page);

        //Add ActionListeners

        findViewById(R.id.scan_face_button).setOnClickListener(this);
        findViewById(R.id.user_profile_button).setOnClickListener(this);
        findViewById(R.id.contacts_button).setOnClickListener(this);
        findViewById(R.id.buttonSignOut).setOnClickListener(this);

        imageView = (ImageView)findViewById(R.id.codeView);
        displayCode();
    }

    //==============================================================================================
    // Action Listeners
    //==============================================================================================
    @Override
    public void onClick(View view) {
        switch(view.getId()) {
            case R.id.scan_face_button:
                scanPerson();
                break;

            case R.id.user_profile_button:
                startActivity(new Intent(this, UserProfileActivity.class));
                break;
            case R.id.contacts_button:
                startActivity(new Intent(this, ContactsPageActivity.class));
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

    private void scanPerson(){
        startActivity(new Intent(this, CameraActivity.class));
    }

    //Displays your QR code on the page
    private void displayCode(){
        String uid = user.getUid();
        try {
            bitmap = TextToImageEncode(uid);
            imageView.setImageBitmap(bitmap);

        } catch (WriterException e) {
            e.printStackTrace();
        }
    }

    Bitmap TextToImageEncode(String Value) throws WriterException {
        BitMatrix bitMatrix;
        try {
            bitMatrix = new MultiFormatWriter().encode(
                    Value,
                    BarcodeFormat.DATA_MATRIX.QR_CODE,
                    QRcodeWidth, QRcodeWidth, null
            );

        } catch (IllegalArgumentException Illegalargumentexception) {

            return null;
        }
        int bitMatrixWidth = bitMatrix.getWidth();

        int bitMatrixHeight = bitMatrix.getHeight();

        int[] pixels = new int[bitMatrixWidth * bitMatrixHeight];

        for (int y = 0; y < bitMatrixHeight; y++) {
            int offset = y * bitMatrixWidth;

            for (int x = 0; x < bitMatrixWidth; x++) {

                pixels[offset + x] = bitMatrix.get(x, y) ?
                        getResources().getColor(android.R.color.black):getResources().getColor(android.R.color.white);
            }
        }
        Bitmap bitmap = Bitmap.createBitmap(bitMatrixWidth, bitMatrixHeight, Bitmap.Config.ARGB_4444);

        bitmap.setPixels(pixels, 0, 500, 0, 0, bitMatrixWidth, bitMatrixHeight);
        return bitmap;
    }
}
