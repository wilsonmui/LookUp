package edu.ucsb.cs48.lookup;

import android.app.Activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.media.Image;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
//import com.microsoft.projectoxford.face.*;
//import com.microsoft.projectoxford.face.contract.*;

public class CameraActivity extends Activity {
    private static final int CAMERA_REQUEST = 1888;
    ImageView imageView;
//    private FaceServiceClient faceServiceClient =
//            new FaceServiceRestClient("https://westcentralus.api.cognitive.microsoft.com/face/v1.0", "215542e1a26e4fb499ae0404aa68ed38");

    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.camera_activity_page); //TODO

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
                analyzePhotoWithFaceAPI();
            }
        });
    }


    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CAMERA_REQUEST) {
            Bitmap photo = (Bitmap) data.getExtras().get("data");
            imageView.setImageBitmap(photo);
        }
    }

    private void analyzePhotoWithFaceAPI(){
        //TODO stub
    }
}
