package edu.ucsb.cs48.lookup;

import android.app.ActionBar;
import android.content.ActivityNotFoundException;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;
import android.provider.ContactsContract;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.util.Log;
import android.widget.Toast;

import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import org.w3c.dom.Text;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;

import static android.content.ContentValues.TAG;

/**
 * Created by Tina on 2/22/2018.
 */

public class EditUserProfileActivity extends AppCompatActivity implements View.OnClickListener {

    private FirebaseAuth mAuth;
    private EditText editDisplayName, editEmailAddress, editPhoneNumber;
    private TextView facebookLink;
    private Button buttonEditProfilePicture, buttonSaveProfileEdits, buttonCancelProfileEdits;
    private HashMap<String, String> userProfileData;
    private DatabaseReference databaseRef, userRef, photoRef, nameRef, emailRef, phoneRef, facebookRef, profilePicRef;
    private StorageReference storageRef;
    private String userID, fbUserID, userProfilePicURL;
    private LinearLayout mLinearLayout;
    private Context mContext;
    private PopupWindow editProfilePicPopup;
    private ImageView editUserProfilePic;
    private Bitmap userProfilePic;
    private static int IMAGE_REQUEST_CODE = 7, CAMERA_REQUEST = 1888;
    private static String CAMERA = "CAMERA", GALLERY = "GALLERY";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.edit_user_profile_page);

        mAuth = FirebaseAuth.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();

        userID = user.getUid();

        mContext = getApplicationContext();

        editUserProfilePic = (ImageView) findViewById(R.id.editUserProfilePic);
        editUserProfilePic.setDrawingCacheEnabled(true);

        buttonEditProfilePicture = (Button) findViewById(R.id.buttonEditProfilePicture);
        buttonEditProfilePicture.setOnClickListener(this);

        buttonSaveProfileEdits = (Button) findViewById(R.id.buttonSaveProfileEdits);
        buttonSaveProfileEdits.setOnClickListener(this);

        buttonCancelProfileEdits = (Button) findViewById(R.id.buttonCancelProfileEdits);
        buttonCancelProfileEdits.setOnClickListener(this);

        mLinearLayout = (LinearLayout) findViewById(R.id.editProfileLL);

        if (user == null) {
            finish();
            startActivity(new Intent(this, SignInPageActivity.class));
        }

        userProfileData = new HashMap<String, String>();

        databaseRef = FirebaseDatabase.getInstance().getReference();

        storageRef = FirebaseStorage.getInstance().getReference();

        userID = user.getUid();
        userRef = databaseRef.child("users").child(userID);

        photoRef = userRef.child("profilePic");

        nameRef = userRef.child("name");
        nameRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(final DataSnapshot dataSnapshot) {
                editDisplayName= (EditText) findViewById(R.id.editDisplayName);
                editDisplayName.setText(dataSnapshot.getValue(String.class));
                userProfileData.put(dataSnapshot.getKey(), dataSnapshot.getValue(String.class));
                editDisplayName.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

                    @Override
                    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

                    @Override
                    public void afterTextChanged(Editable editable) {
                        String newDisplayName = editable.toString();
                        if (!newDisplayName.equals(""))
                            userProfileData.put("name", newDisplayName);
                        else userProfileData.remove("name");
                        Log.d(TAG, "display name changed!");
                    }
                });
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {}
        });

        emailRef = userRef.child("email");
        emailRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                editEmailAddress = (EditText) findViewById(R.id.editEmailAddress);
                editEmailAddress.setText(dataSnapshot.getValue(String.class));
                userProfileData.put(dataSnapshot.getKey(), dataSnapshot.getValue(String.class));
                editEmailAddress.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                        String oldEmailAddress = charSequence.toString();
                        userProfileData.put("email", oldEmailAddress);
                    }

                    @Override
                    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

                    @Override
                    public void afterTextChanged(Editable editable) {
                        String newEmailAddress = editable.toString();
                        if (!newEmailAddress.equals(""))
                            userProfileData.put("email", newEmailAddress);
                        else
                            userProfileData.remove("email");

                        Log.d(TAG, "email address changed!");
                    }
                });
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {}
        });

        phoneRef = userRef.child("phone");
        phoneRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                editPhoneNumber = (EditText) findViewById(R.id.editPhoneNumber);
                editPhoneNumber.setText(dataSnapshot.getValue(String.class));
                userProfileData.put(dataSnapshot.getKey(), dataSnapshot.getValue(String.class));
                editPhoneNumber.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                        String oldPhoneNumber = charSequence.toString();
                        userProfileData.put("phone", oldPhoneNumber);
                    }

                    @Override
                    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

                    @Override
                    public void afterTextChanged(Editable editable) {
                        String newPhoneNumber = editable.toString();
                        if (!newPhoneNumber.equals(""))
                            userProfileData.put("phone", newPhoneNumber);
                        else
                            userProfileData.remove("phone");
                        Log.d(TAG, "phone number changed!");
                    }
                });
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {}
        });

        facebookRef = userRef.child("facebook");
        facebookRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                fbUserID = dataSnapshot.getValue(String.class);
                facebookLink = (TextView) findViewById(R.id.facebookLink);
                if (fbUserID != null && !fbUserID.isEmpty()) {
                    facebookLink.setText("https://facebook.com" + fbUserID);
                    userProfileData.put(dataSnapshot.getKey(), fbUserID);
                }
                else {
                    facebookLink.setText("");
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {}
        });

        profilePicRef = userRef.child("profilePic");
        profilePicRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(final DataSnapshot dataSnapshot) {
//                editUserProfilePic = (ImageView) findViewById(R.id.editUserProfilePic);
                if (dataSnapshot.getValue(String.class) != null && !dataSnapshot.getValue(String.class).isEmpty()) {
                    Log.d(TAG, "profile pic url: " + dataSnapshot.getValue(String.class));
                    Picasso.with(mContext).load(dataSnapshot.getValue(String.class)).centerCrop().fit().into(editUserProfilePic);
                    userProfileData.put(dataSnapshot.getKey(), dataSnapshot.getValue(String.class));
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {}
        });
    }

    @Override
    protected void onStart() {
        super.onStart();

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.buttonEditProfilePicture:
                Log.d(TAG, "Edit profile picture button clicked");
                LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(LAYOUT_INFLATER_SERVICE);
                View customView = inflater.inflate(R.layout.edit_profile_pic_popup, null);
                editProfilePicPopup = new PopupWindow(customView, LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
                Button buttonImportFromFB = (Button) customView.findViewById(R.id.buttonImportFromFB);
                buttonImportFromFB.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Log.d(TAG, "Import from FB button clicked");

                        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
                        StrictMode.setThreadPolicy(policy);
                        try {
                            URL url = new URL("https://graph.facebook.com/" + fbUserID + "/picture?type=large");
                            editUserProfilePic.setImageBitmap(BitmapFactory.decodeStream((InputStream)url.getContent()));
                            Log.d(TAG, "TEST");
                            userProfilePicURL = getRedirectedURL(url.toString());
                            userProfileData.put("profilePic", userProfilePicURL);

                        } catch (IOException e) {
                            Log.e(TAG, e.getMessage());
                        }

                        editProfilePicPopup.dismiss();

                    }
                });
                Button buttonUploadPhoto = (Button) customView.findViewById(R.id.buttonUploadPhoto);
                buttonUploadPhoto.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent =  new Intent();

                        // set intent type as image to select image from phone storage
                        intent.setType("image/*");
                        intent.setAction(Intent.ACTION_GET_CONTENT);
                        startActivityForResult(Intent.createChooser(intent, "Please select an image"), IMAGE_REQUEST_CODE);

                        editProfilePicPopup.dismiss();
                    }
                });
//                Button buttonRemoveProfilePic = (Button) customView.findViewById(R.id.buttonRemoveProfilePic);
//                buttonRemoveProfilePic.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View view) {
//                        Log.d(TAG, "Remove profile picture button clicked");
//                        userProfileData.remove("profilePic");
//                        editUserProfilePic.setImageResource(R.drawable.blank_profile_picture);
//                        editProfilePicPopup.dismiss();
//                    }
//                });
                Button buttonTakePicture = (Button) customView.findViewById(R.id.buttonTakePhoto);
                buttonTakePicture.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                        startActivityForResult(cameraIntent, CAMERA_REQUEST);
                        editProfilePicPopup.dismiss();
                    }
                });

                Button buttonCancelEditProfilePic = (Button) customView.findViewById(R.id.buttonCancelEditProfilePic);
                buttonCancelEditProfilePic.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        editProfilePicPopup.dismiss();
                    }
                });
                editProfilePicPopup.showAtLocation(mLinearLayout, Gravity.CENTER, 0, 0);
                break;
            case R.id.buttonSaveProfileEdits:
                Log.d(TAG, "Save button clicked!");
                uploadImageFileToFirebaseStorage();
                updateDatabase(); // need this again in case they imported from facebook
                finish();
                startActivity(new Intent(this, UserProfileActivity.class));
                break;
            case R.id.buttonCancelProfileEdits:
                Log.d(TAG, "Cancel");
                finish();
                startActivity(new Intent(this, UserProfileActivity.class));
                break;

        }
    }

    private void updateDatabase() {

        userRef.setValue(userProfileData);

    }


    public static String getRedirectedURL(String url) throws IOException {
        HttpURLConnection con = (HttpURLConnection) new URL(url).openConnection();
        con.setInstanceFollowRedirects(false);
        con.connect();
        con.getInputStream();

        if (con.getResponseCode() == HttpURLConnection.HTTP_MOVED_PERM || con.getResponseCode() == HttpURLConnection.HTTP_MOVED_TEMP) {
            String redirectUrl = con.getHeaderField("Location");
            return getRedirectedURL(redirectUrl);
        }
        return url;
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // for uploading from camera roll
        if (requestCode == IMAGE_REQUEST_CODE && resultCode == RESULT_OK && data.getData() != null) {
            Uri imageFilePathUri = data.getData();
            Log.d(TAG, "gallery uri " + imageFilePathUri.toString());
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), imageFilePathUri);
                userProfilePic = rotateBitmap(mContext, imageFilePathUri, bitmap, GALLERY);
//                rotateImage(mContext, imageFilePathUri);
                editUserProfilePic.setImageBitmap(userProfilePic);

            }
            catch (IOException e) {
                e.printStackTrace();
            }
        }

        // for taking a picture
        else if (requestCode == CAMERA_REQUEST) {
            Uri imageFilePathUri = data.getData();
            Log.d(TAG, "camera uri " + imageFilePathUri.toString());
            try {
//                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), imageFilePathUri);
//                Bitmap bitmap = (Bitmap) data.getExtras().get("data");
                Bitmap bitmap = BitmapFactory.decodeStream(getContentResolver().openInputStream(imageFilePathUri), null, null);


                Log.d(TAG, imageFilePathUri.toString());

                userProfilePic = rotateBitmap(mContext, imageFilePathUri, bitmap, CAMERA);
//                rotateImage(mContext, imageFilePathUri);
                editUserProfilePic.setImageBitmap(userProfilePic);
            }
            catch (IOException e) {}
        }

//        else if (resultCode == RESULT_OK && requestCode == CAMERA_CAPTURE) {
//
//        }

    }

    // Creating Method to get the selected image file Extension from File Path URI.
    public String GetFileExtension(Uri uri) {

        ContentResolver contentResolver = getContentResolver();

        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();

        // Returning the file Extension.
        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri)) ;

    }

    // Creating uploadImageFileToFirebaseStorage method to upload image on storage.
    public void uploadImageFileToFirebaseStorage() {

//        Bitmap bitmap = editUserProfilePic.getDrawingCache();
        Uri uri = getImageUri(mContext, userProfilePic);

        // Checking whether uri Is empty or not.
        if (uri != null) {

            // Setting progressDialog Title.
//            progressDialog.setTitle("Image is Uploading...");

            // Showing progressDialog.
//            progressDialog.show();

            // Creating second StorageReference.
            StorageReference storageRef2 = storageRef.child(userID + "_" + System.currentTimeMillis() + "." + GetFileExtension(uri));

            // Adding addOnSuccessListener to second StorageReference.
            storageRef2.putFile(uri)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                            // Getting image name from EditText and store into string variable.
//                            String TempImageName = ImageName.getText().toString().trim();

                            // Hiding the progressDialog after done uploading.
//                            progressDialog.dismiss();

                            // Showing toast message after done uploading.
                            Toast.makeText(getApplicationContext(), "Image Uploaded Successfully ", Toast.LENGTH_LONG).show();
//                            @SuppressWarnings("VisibleForTests")
//                            ImageUploadInfo imageUploadInfo = new ImageUploadInfo(userID + "_profile_pic", taskSnapshot.getDownloadUrl().toString());

//                            // Getting image upload ID.
//                            String ImageUploadId = databaseRef.push().getKey();
//
//                            // Adding image upload id s child element into databaseReference.
//                            databaseRef.child(ImageUploadId).setValue(imageUploadInfo);

                            userProfileData.put("profilePic", taskSnapshot.getDownloadUrl().toString());
                            Log.d(TAG, "pic to upload: " + taskSnapshot.getDownloadUrl().toString());
                            userProfilePicURL = taskSnapshot.getDownloadUrl().toString();

                            // put the changes to the database
                            updateDatabase();
                        }
                    })
                    // If something goes wrong .
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception exception) {

                            // Hiding the progressDialog.
//                            progressDialog.dismiss();

                            // Showing exception erro message.
                            Toast.makeText(EditUserProfileActivity.this, exception.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    })

                    // On progress change upload time.
                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {

                            // Setting progressDialog Title.
//                            progressDialog.setTitle("Image is Uploading...");

                        }

                    })

                    .addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                            updateDatabase();
                        }
                    });
        }

    }

    public String getRealPathFromURI(Context context, Uri uri, String source) {

        String path = "";

        if (source.equals(GALLERY)) {

            int currentAPIVersion;
            try {
                currentAPIVersion = Build.VERSION.SDK_INT;
            } catch (NumberFormatException e) {
                currentAPIVersion = 3;
            }

            if (currentAPIVersion >= 19)
                path = getRealPathFromURI_API19(context, uri);
            else if (currentAPIVersion >= 11 && currentAPIVersion <= 18)
                path = getRealPathFromURI_API11to18(context, uri);
            else if (currentAPIVersion < 11)
                path = getRealPathFromURI_BelowAPI11(context, uri);
        }
        else if (source.equals(CAMERA)) {
            Cursor cursor = null;
            try {
                String[] proj = { MediaStore.Images.Media.DATA };
                cursor = getContentResolver().query(uri,  proj, null, null, null);
                int index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
                cursor.moveToFirst();
                path = cursor.getString(index);
                cursor.close();
            }
            catch (NullPointerException e) {}
        }

        return path;

    }

    public static String getRealPathFromURI_API19(Context context, Uri uri){
        String filePath = "";
        String wholeID = DocumentsContract.getDocumentId(uri);

        // Split at colon, use second item in the array
        String id = wholeID.split(":")[1];

        String[] column = { MediaStore.Images.Media.DATA };

        // where id is equal to
        String sel = MediaStore.Images.Media._ID + "=?";

        Cursor cursor = context.getContentResolver().query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                column, sel, new String[]{ id }, null);

        int columnIndex = cursor.getColumnIndex(column[0]);

        if (cursor.moveToFirst()) {
            filePath = cursor.getString(columnIndex);
        }
        cursor.close();
        return filePath;
    }

    public static String getRealPathFromURI_API11to18(Context context, Uri contentUri) {
        String[] proj = { MediaStore.Images.Media.DATA };
        String result = null;

        CursorLoader cursorLoader = new CursorLoader(
                context,
                contentUri, proj, null, null, null);
        Cursor cursor = cursorLoader.loadInBackground();

        if(cursor != null){
            int column_index =
                    cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            result = cursor.getString(column_index);
        }
        return result;
    }

    public static String getRealPathFromURI_BelowAPI11(Context context, Uri contentUri){
        String[] proj = { MediaStore.Images.Media.DATA };
        Cursor cursor = context.getContentResolver().query(contentUri, proj, null, null, null);
        int column_index
                = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        return cursor.getString(column_index);
    }


    private static int exifToDegrees(int exifOrientation) {
        if (exifOrientation == ExifInterface.ORIENTATION_ROTATE_90) { return 90; }
        else if (exifOrientation == ExifInterface.ORIENTATION_ROTATE_180) {  return 180; }
        else if (exifOrientation == ExifInterface.ORIENTATION_ROTATE_270) {  return 270; }
        return 0;
    }

    private Bitmap rotateBitmap(Context context, Uri uri, Bitmap bitmap, String source) throws IOException {
        ExifInterface exif = null;
        if (source.equals("GALLERY"))
            exif = new ExifInterface(getRealPathFromURI(context, uri, GALLERY));
        else if (source.equals("CAMERA")) {
            exif = new ExifInterface(getRealPathFromURI(context, uri, CAMERA));
        }
        int rotation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
        int rotationInDegrees = exifToDegrees(rotation);
        Matrix matrix = new Matrix();
        if (rotation != 0f) {
            matrix.preRotate(rotationInDegrees);
        }
        //    getting selected image into Bitmap
        Bitmap rotatedBitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
        return rotatedBitmap;
    }

    public Uri getImageUri(Context inContext, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, "", null);
        return Uri.parse(path);
    }

}