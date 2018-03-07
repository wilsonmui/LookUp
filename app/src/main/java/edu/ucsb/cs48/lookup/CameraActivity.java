package edu.ucsb.cs48.lookup;

import android.app.Activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.media.Image;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import com.google.android.gms.vision.Frame;
import com.google.android.gms.vision.barcode.Barcode;
import com.google.android.gms.vision.barcode.BarcodeDetector;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.BitmapFactory;
import android.util.SparseArray;
import android.widget.TextView;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.vision.barcode.BarcodeDetector;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class CameraActivity extends Activity {
    private static final int CAMERA_REQUEST = 1888;
    ImageView imageView;
    Bitmap bitmapPhoto;
    TextView uid;
    String uidGrabbed;

    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.camera_activity_page);

        uid = this.findViewById(R.id.uid_result);
        imageView = (ImageView) this.findViewById(R.id.face_imageview);
        Button photoButton = (Button) this.findViewById(R.id.scan_now);
        Button analyzePhotoButton = (Button) this.findViewById(R.id.analyze_photo);

        photoButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(cameraIntent, CAMERA_REQUEST);
            }
        });
        analyzePhotoButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                analyzeCode();
            }
        });
    }


    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CAMERA_REQUEST) {
            bitmapPhoto = (Bitmap) data.getExtras().get("data");
            imageView.setImageBitmap(bitmapPhoto);
        }
    }

    private void analyzeCode(){
        BarcodeDetector detector =
                new BarcodeDetector.Builder(getApplicationContext())
                        .setBarcodeFormats(Barcode.DATA_MATRIX | Barcode.QR_CODE)
                        .build();
        if(!detector.isOperational()){
            Log.e("ShitDontWork", "Shit dont work");
            return;
        }

        Frame frame = new Frame.Builder().setBitmap(bitmapPhoto).build();
        SparseArray<Barcode> barcodes = detector.detect(frame);

        if(barcodes.size() <= 0){
            Toast.makeText(getApplicationContext(), "Please take a better photo.", Toast.LENGTH_SHORT).show();
            return;
        }

        Barcode thisCode = barcodes.valueAt(0);
        uidGrabbed = thisCode.rawValue;
        uid.setText(uidGrabbed);

        //check if user exists
//        FirebaseUser user;
//        FirebaseAuth mAuth;
//        mAuth = FirebaseAuth.getInstance();
//        if(mAuth.getCurrentUser() == null) {
//            finish();
//            startActivity(new Intent(this, SignInPageActivity.class));
//        }
//        user = mAuth.getCurrentUser();
//        boolean userExists = true;

        DatabaseReference db = FirebaseDatabase.getInstance().getReference().child("users");
        db.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(!dataSnapshot.hasChild(uidGrabbed)){
                    Toast.makeText(getApplicationContext(), "User does not exist.", Toast.LENGTH_SHORT).show();
                }else{
                    //start ContactProfileActivity with uid string
                    Intent intent = new Intent (CameraActivity.this, ContactProfileActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putString("uid", uidGrabbed);
                    intent.putExtras(bundle);
                    startActivity(intent);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}
