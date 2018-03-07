package edu.ucsb.cs48.lookup;

import android.app.ActionBar;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.StrictMode;
import android.provider.ContactsContract;
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
    private String userID, fbUserID, userProfilePicURL, currentStorageChild;
    private String imageStoragePath = "All image uploads";
    private LinearLayout mLinearLayout;
    private Context mContext;
    private PopupWindow editProfilePicPopup;
    private ImageView editUserProfilePic;
    private Uri imageFilePathUri;
    private static int RESULT_LOAD_IMG = 1, IMAGE_REQUEST_CODE = 7;
    private boolean uploadImageIsComplete = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.edit_user_profile_page);

        mAuth = FirebaseAuth.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();

        userID = user.getUid();

        mContext = getApplicationContext();

//        editUserProfilePic = (ImageView) findViewById(R.id.editUserProfilePic);

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
                if (fbUserID != null) {
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

        profilePicRef = userRef.child("profile_pic");
        profilePicRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(final DataSnapshot dataSnapshot) {
                editUserProfilePic = (ImageView) findViewById(R.id.editUserProfilePic);
                Log.d(TAG, "profile pic url: " + dataSnapshot.getValue(String.class));
                Picasso.with(mContext).load(dataSnapshot.getValue(String.class)).fit().into(editUserProfilePic);
                userProfileData.put(dataSnapshot.getKey(), dataSnapshot.getValue(String.class));
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
                            userProfileData.put("profile_pic", userProfilePicURL);

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

        if (requestCode == IMAGE_REQUEST_CODE && resultCode == RESULT_OK && data.getData() != null) {
            imageFilePathUri = data.getData();

            try {
                // getting selected image into Bitmap
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), imageFilePathUri);
                // setting up bitmap selected image into Image View
                editUserProfilePic.setImageBitmap(bitmap);
            }
            catch (IOException e) {
                e.printStackTrace();
            }
        }

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

        // Checking whether FilePathUri Is empty or not.
        if (imageFilePathUri != null) {

            // Setting progressDialog Title.
//            progressDialog.setTitle("Image is Uploading...");

            // Showing progressDialog.
//            progressDialog.show();

            // Creating second StorageReference.
            StorageReference storageRef2 = storageRef.child(userID + "_" + System.currentTimeMillis() + "." + GetFileExtension(imageFilePathUri));

            // Adding addOnSuccessListener to second StorageReference.
            storageRef2.putFile(imageFilePathUri)
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

                            userProfileData.put("profile_pic", taskSnapshot.getDownloadUrl().toString());
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
                            uploadImageIsComplete = true;
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

}