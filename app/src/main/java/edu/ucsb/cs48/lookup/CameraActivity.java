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
    }
}
