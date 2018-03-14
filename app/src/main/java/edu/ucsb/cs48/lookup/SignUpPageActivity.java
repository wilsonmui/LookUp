package edu.ucsb.cs48.lookup;

import android.*;
import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Shader;
import android.graphics.drawable.BitmapDrawable;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.StrictMode;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.util.Patterns;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;


import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import static android.content.ContentValues.TAG;

/**
 * Created by esuarez on 2/4/18.
 */

public class SignUpPageActivity extends AppCompatActivity implements View.OnClickListener {

    //==============================================================================================
    // Declare Variables
    //==============================================================================================
    private EditText editTextEmail, editTextPassword, editTextName, editTextPhone;
    private ProgressBar progressBar;
    private TextView textViewSignIn;
    private FirebaseAuth mAuth;
    private static final int SIGN_IN_REQUEST = 0;
    private Button buttonSignUp, buttonTakePhoto, buttonUploadPhoto;
    private CallbackManager callbackManager;
    private String g_username = "";
    private static String GALLERY = "GALLERY", CAMERA = "CAMERA";

    private DatabaseReference db;
    private StorageReference storageRef;

    FirebaseUser user;

    private String fbID, mCurrentPhotoPath;

    private static final String NAME = "public_profile", EMAIL = "email";
    private TextView info;
    private PopupWindow setProfilePicPopup;

    GoogleSignInClient mGoogleSignInClient;

    private static final int IMAGE_REQUEST_CODE = 7, CAMERA_REQUEST = 1888;
    private ImageView user_profile_photo;

    private Bitmap userProfilePic;

    private static final int PERMISSIONS_REQUEST_CAMERA = 1, PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE = 2,
            PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE = 3;

    //==============================================================================================
    // On Create Setup
    //==============================================================================================

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        // Check if User is Authenticated
        mAuth = FirebaseAuth.getInstance();
        FirebaseUser currUser = mAuth.getCurrentUser();
        storageRef = FirebaseStorage.getInstance().getReference();
        updateUI(currUser);

        setContentView(R.layout.sign_up_page);

        initListeners();

        db = FirebaseDatabase.getInstance().getReference();

        //setup Google sign-in options
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken("499747879832-vaojkbm5j81thueo3m2k6miqhrn57rt6.apps.googleusercontent.com")
                .requestEmail()
                .build();

        // Build a GoogleSignInClient with the options specified by gso.
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        findViewById(R.id.sign_in_button).setOnClickListener(this);

        // Facebook sign up
        FacebookSdk.sdkInitialize(getApplicationContext());
        callbackManager = CallbackManager.Factory.create();
        LoginButton loginButton = (LoginButton) findViewById(R.id.fb_sign_up_button); //TODO: why this happen ??!!??!
        info = (TextView)findViewById(R.id.info);
        loginButton.setReadPermissions(Arrays.asList(NAME, EMAIL));

        // Callback registration
        loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                try {
                    GraphRequest request = GraphRequest.newMeRequest(loginResult.getAccessToken(),
                            new GraphRequest.GraphJSONObjectCallback() {
                                @Override
                                public void onCompleted(JSONObject object, GraphResponse response) {
                                    try {
                                        fbID = object.getString("id");
                                        Log.d(TAG, "FB id: " + fbID);
                                        Log.d(TAG, "FB link successful");
                                    }
                                    catch (Exception e) {
                                        e.printStackTrace();
                                        Log.d(TAG, "FB link unsuccessful");
                                    }
                                }
                            });
                    Bundle parameters = new Bundle();
                    parameters.putString("fields","link");
                    request.setParameters(parameters);
                    request.executeAsync();
                }
                catch (Exception e) {
                    Log.d("FACEBOOK ERROR", "cancelled");
                }
                setResult(RESULT_OK);
                Log.d(TAG, "facebook:onSuccess:" + loginResult);
                handleFacebookAccessToken(loginResult.getAccessToken());
//                finish();
            }

            @Override
            public void onCancel() {
                setResult(RESULT_CANCELED);
                Log.d(TAG, "facebook:onCancel");
                finish();
            }

            @Override
            public void onError(FacebookException exception) {
                Log.d(TAG, "facebook:onError", exception);
            }
        });


    }

    //==============================================================================================
    // On Start setup
    //==============================================================================================
    @Override
    public void onStart() {
        super.onStart();

        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        updateUI(currentUser);

    }

    //==============================================================================================
    // Action Listeners
    //==============================================================================================
    @Override
    public void onClick(View view) {
        switch(view.getId()) {
            case R.id.buttonSignUp:
                registerUser();
                break;
            case R.id.textViewSignIn:
                finish();
                startActivity(new Intent(this, SignInPageActivity.class));
                break;
            case R.id.sign_in_button:
                signIn();
                break;
        }
    }

    //==============================================================================================
    // Helper Functions
    //==============================================================================================
    private void updateUI(FirebaseUser currentUser) {

        if (currentUser != null) {
            finish();
            startActivity(new Intent(this, HomePageActivity.class));
        } else {
            Log.d(TAG, "current user is null");
        }

    }

    private void initListeners() {

        editTextName = (EditText) findViewById(R.id.editTextName);
        editTextEmail = (EditText)findViewById(R.id.editTextEmail);
        editTextPassword = (EditText)findViewById(R.id.editTextPassword);
        editTextPhone = (EditText)findViewById(R.id.editTextPhone);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        buttonSignUp = (Button) findViewById(R.id.buttonSignUp);
        buttonTakePhoto = (Button) findViewById(R.id.buttonTakePhoto);
        buttonUploadPhoto = (Button) findViewById(R.id.buttonUploadPhoto);
        textViewSignIn = (TextView) findViewById(R.id.textViewSignIn);
       // user_profile_photo =(ImageView) findViewById(R.id.user_profile_photo);
        user_profile_photo.setDrawingCacheEnabled(true);
       // Button photoButton = (Button) findViewById(R.id.set_photo_button);

//        photoButton.setOnClickListener(new View.OnClickListener() {
//
//            @Override
//            public void onClick(View v) {
//                LayoutInflater inflater = (LayoutInflater) getApplication().getSystemService(LAYOUT_INFLATER_SERVICE);
//                View customView = inflater.inflate(R.layout.edit_profile_pic_popup, null);
//                setProfilePicPopup = new PopupWindow(customView, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
//
//                // remove the import from facebook option
//                ((Button)customView.findViewById(R.id.buttonImportFromFB)).setVisibility(View.GONE);
//
//                buttonUploadPhoto = (Button) customView.findViewById(R.id.buttonUploadPhoto);
//                buttonUploadPhoto.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View view) {
//
//                        if (isGalleryAccessAllowed() == false) {
//                            requestPermissionReadExternalStorage();
//                        }
//                        else {
//                            Intent intent = new Intent();
//
//                            // set intent type as image to select image from phone storage
//                            intent.setType("image/*");
//                            intent.setAction(Intent.ACTION_GET_CONTENT);
//                            startActivityForResult(Intent.createChooser(intent, "Please select an image"), IMAGE_REQUEST_CODE);
//
//                        }
//                        setProfilePicPopup.dismiss();
//                    }
//                });
//
//                buttonTakePhoto = (Button) customView.findViewById(R.id.buttonTakePhoto);
//                buttonTakePhoto.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View view) {
//                        if (isCameraAllowed() == false) {
//                            requestPermissionCamera();
//                        }
//                        else {
//
//                            Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//                            // Ensure that there's a camera activity to handle the intent
//                            if (cameraIntent.resolveActivity(getPackageManager()) != null) {
//                                // Create the File where the photo should go
//                                File photoFile = null;
//                                try {
//                                    photoFile = createImageFile();
//                                } catch (IOException ex) {
//                                    // Error occurred while creating the File
//                                }
//                                // Continue only if the File was successfully created
//                                if (photoFile != null) {
//                                    Uri photoURI = FileProvider.getUriForFile(SignUpPageActivity.this,
//                                            "com.example.android.fileprovider",
//                                            photoFile);
//                                    cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
//                                    startActivityForResult(cameraIntent, CAMERA_REQUEST);
//                                }
//
//                            }
////                            Log.d(TAG, "wy isn't this working ");
////                            Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
////                            startActivityForResult(cameraIntent, CAMERA_REQUEST);
//                            setProfilePicPopup.dismiss();
//                        }
//                    }
//                });
//
//                Button buttonCancelEditProfilePic = (Button) customView.findViewById(R.id.buttonCancelEditProfilePic);
//                buttonCancelEditProfilePic.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View view) {
//                        setProfilePicPopup.dismiss();
//                    }
//                });
//                setProfilePicPopup.showAtLocation((RelativeLayout) findViewById(R.id.signUpPage), Gravity.CENTER, 0, 0);
//
//            }
//        });


        buttonSignUp.setOnClickListener(this);
        textViewSignIn.setOnClickListener(this);
    }

    private void registerUser() {

        // Sanitize Inputs
        String name = editTextName.getText().toString().trim();
        String email = editTextEmail.getText().toString().trim();
        String password = editTextPassword.getText().toString().trim();
        final String phone = editTextPhone.getText().toString().trim();

        if(name.isEmpty()) {
            editTextName.setError("Name is required");
            editTextName.requestFocus();
            return;
        }

        if(email.isEmpty()) {
            editTextEmail.setError("Email is required");
            editTextEmail.requestFocus();
            return;
        }

        if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            editTextEmail.setError("Please enter a valid email");
            editTextEmail.requestFocus();
            return;
        }

        if (phone.isEmpty()) {
            editTextPhone.setError("Phone Number is required");
            editTextPhone.requestFocus();
            return;
        }

        if (phone.length() < 10) {
            editTextPhone.setError("Please enter a valid phone number");
            editTextPhone.requestFocus();
            return;
        }

        if (password.isEmpty()) {
            editTextPassword.setError("Password is required");
            editTextPassword.requestFocus();
            return;
        }

        if(password.length() < 6) {
            editTextPassword.setError("Minimum length of password should be 6");
            editTextPassword.requestFocus();
            return;
        }


        // Show Progress bar
        progressBar.setVisibility(View.VISIBLE);

        // If all fields pass all checks proceed to creation
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {

            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                progressBar.setVisibility(View.GONE);

                if (task.isSuccessful()) {

                    // Variable Set up
                    String name = editTextName.getText().toString().trim();
                    String email = editTextEmail.getText().toString().trim();
                    String phone = editTextPhone.getText().toString().trim();
                    FirebaseUser currUser = mAuth.getCurrentUser();
                    String uid = currUser.getUid();
                    //Bitmap profilePicBitmap = user_profile_photo.getDrawingCache();
                    //Uri profilePicUri = getImageUri(getApplicationContext(), profilePicBitmap);

                    // Save User Data to DataBase
                    saveUserData(name, email, phone, uid);

                    // Sign in success, update UI with the signed in User's Information
                    updateUI(currUser);

                } else {

                    Toast.makeText(SignUpPageActivity.this, "Authentication failed." + task.getException(),
                            Toast.LENGTH_SHORT).show();
                    updateUI(null);
                }
            }
        });
    }

    private void saveUserData(String name, String email, String phone, String userId) {
            User user = new User(name, email, phone, userId, generatePicId());
            db.child("users").child(userId).setValue(user);
        }

    private String generatePicId() {
        Random rn = new Random();

        switch(rn.nextInt(10 - 1 + 1) + 1) {
            case 1:
                return "https://firebasestorage.googleapis.com/v0/b/lookup-43919.appspot.com/o/identicon.png?alt=media&token=b3f3e71f-b674-4c42-8182-a610f18c7982";
            case 2:
                return "https://firebasestorage.googleapis.com/v0/b/lookup-43919.appspot.com/o/identicon_beige.png?alt=media&token=5cc5d24e-5659-483a-bcaa-94256d10218b";
            case 3:
                return "https://firebasestorage.googleapis.com/v0/b/lookup-43919.appspot.com/o/identicon_green.png?alt=media&token=3ed0435f-0981-46ff-973f-ded55382f785";
            case 4:
                return "https://firebasestorage.googleapis.com/v0/b/lookup-43919.appspot.com/o/identicon_lightblue.png?alt=media&token=57c9ccaf-13b3-4e66-a1a7-d875fcd829a6";
            case 5:
                return "https://firebasestorage.googleapis.com/v0/b/lookup-43919.appspot.com/o/identicon_lightgreen.png?alt=media&token=eba53af9-5785-4a33-9c7d-df1319ef4b91";
            case 6:
                return "https://firebasestorage.googleapis.com/v0/b/lookup-43919.appspot.com/o/identicon_pink.png?alt=media&token=3f83d8d4-05ca-4f7c-bd32-8820345665a0";
            case 7:
                return "https://firebasestorage.googleapis.com/v0/b/lookup-43919.appspot.com/o/identicon_purple.png?alt=media&token=4bcf0631-9323-477b-9f03-b2f43ac51dad";
            case 8:
                return "https://firebasestorage.googleapis.com/v0/b/lookup-43919.appspot.com/o/identicon_rose.png?alt=media&token=979e8888-4fb3-4654-99e3-927084755cde";
            default:
                return "https://firebasestorage.googleapis.com/v0/b/lookup-43919.appspot.com/o/identicon.png?alt=media&token=b3f3e71f-b674-4c42-8182-a610f18c7982";
        }
    }

    private void saveFBUserID(String id, String userId) {
        Map<String,String> userFBData = new HashMap<String,String>();
        userFBData.put("facebook", id);
        db.child("users").child(userId).child("facebook").setValue(id);
    }

    // sign-in for Google
    // note sign out: FirebaseAuth.getInstance().signOut();
    private void signIn() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, SIGN_IN_REQUEST);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        callbackManager.onActivityResult(requestCode, resultCode, data);
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInClient.getSignInIntent(...);
        if (requestCode == SIGN_IN_REQUEST) {
            // The Task returned from this call is always completed, no need to attach
            // a listener.
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleSignInResult(task);
        }

        // for uploading from camera roll
        if (requestCode == IMAGE_REQUEST_CODE && resultCode == RESULT_OK && data.getData() != null) {
            Uri imageFilePathUri = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), imageFilePathUri);
                userProfilePic = rotateBitmap(getApplicationContext(), imageFilePathUri, bitmap, GALLERY);
                user_profile_photo.setImageBitmap(userProfilePic);

            }
            catch (IOException e) {
                e.printStackTrace();
            }
        }

        // for uploading from camera
        if (requestCode == CAMERA_REQUEST && resultCode == RESULT_OK) {
//                Log.d(TAG, "UHHHHEHHHELELOOOOOO");
//                Uri imageFilePathUri = data.getData();
//                Log.d(TAG, "suck my DICK " + imageFilePathUri);
//                try {
//                    Bitmap bitmap = BitmapFactory.decodeStream(getContentResolver().openInputStream(imageFilePathUri), null, null);
//                    Log.d(TAG, imageFilePathUri.toString());
//                    userProfilePic = rotateBitmap(getApplicationContext(), imageFilePathUri, bitmap, CAMERA);
//                    user_profile_photo.setImageBitmap(userProfilePic);
//                } catch (IOException e) {
//            }
            galleryAddPic();
            Bundle extras = data.getExtras();
            Bitmap bitmap = (Bitmap) extras.get("data");
//            try {

//    @Override
//    public void onActivityResult(int requestCode, int resultCode, Intent data) {
//        callbackManager.onActivityResult(requestCode, resultCode, data);
//        super.onActivityResult(requestCode, resultCode, data);
//
//        // Result returned from launching the Intent from GoogleSignInClient.getSignInIntent(...);
//        if (requestCode == SIGN_IN_REQUEST) {
//            // The Task returned from this call is always completed, no need to attach
//            // a listener.
//            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
//            handleSignInResult(task);
//        }
//
//        // for uploading from camera roll
//        if (requestCode == IMAGE_REQUEST_CODE && resultCode == RESULT_OK && data.getData() != null) {
//            Uri imageFilePathUri = data.getData();
//            try {
//                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), imageFilePathUri);
//                userProfilePic = rotateBitmap(getApplicationContext(), imageFilePathUri, bitmap, GALLERY);
//                user_profile_photo.setImageBitmap(userProfilePic);
//
//            }
//            catch (IOException e) {
//                e.printStackTrace();
//            }
//        }
//
//        // for uploading from camera
//        if (requestCode == CAMERA_REQUEST) {
//            Uri imageFilePathUri = data.getData();
//            Log.d(TAG, "camera uri " + imageFilePathUri.toString());
//            try {
//                Bitmap bitmap = BitmapFactory.decodeStream(getContentResolver().openInputStream(imageFilePathUri), null, null);
//                Log.d(TAG, imageFilePathUri.toString());

//                userProfilePic = rotateBitmap(getApplicationContext(), imageFilePathUri, bitmap, CAMERA);
//                user_profile_photo.setImageBitmap(userProfilePic);
//            }
//            catch (IOException e) {}
            user_profile_photo.setImageBitmap(bitmap);
        }
    }

    private void handleSignInResult(Task<GoogleSignInAccount> completedTask) {
        try {
            GoogleSignInAccount account = completedTask.getResult(ApiException.class);
            firebaseAuthWithGoogle(account);
            // Signed in successfully, show authenticated UI
        } catch (ApiException e) {
            // The ApiException status code indicates the detailed failure reason.
            // Please refer to the GoogleSignInStatusCodes class reference for more information.
            Log.w(TAG, "Google sign in failed", e);
        }
    }

    //exchange Google ID token for Firebase credential
    private void firebaseAuthWithGoogle(GoogleSignInAccount acct){
        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithCredential:success");
                            user = mAuth.getCurrentUser();

                            saveUserData(user.getDisplayName(), user.getEmail(),user.getPhoneNumber(), user.getUid());
                            updateUI(user);
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithCredential:failure", task.getException());
                            //Snackbar.make(findViewById(R.id.main_layout), "Authentication Failed.", Snackbar.LENGTH_SHORT).show();
                            updateUI(null);
                        }

                        // ...
                    }
                });
    }

    private void handleFacebookAccessToken(AccessToken token) {
        Log.d(TAG, "handleFacebookAccessToken:" + token);
        AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithCredential:success");
                            user = mAuth.getCurrentUser();
                            String name = user.getDisplayName();
                            String email = user.getEmail();
                            String phone = user.getPhoneNumber();
                            String uid = user.getUid();
                            saveUserData(name, email, phone, uid);
                            saveFBUserID(fbID, uid);
                            updateUI(user);
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithCredential:failure", task.getException());
                            Toast.makeText(SignUpPageActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                            updateUI(null);
                        }

                    }
                });
    }

    public String GetFileExtension(Uri uri) {

        ContentResolver contentResolver = getContentResolver();

        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();

        // Returning the file Extension.
        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri)) ;

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
        if (inImage == null)
            return null;
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, "", null);
        return Uri.parse(path);
    }

//    public String GetFileExtension(Uri uri) {
//
//        ContentResolver contentResolver = getContentResolver();
//
//        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
//
//        // Returning the file Extension.
//        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri)) ;
//
//    }
//
//    public String getRealPathFromURI(Context context, Uri uri, String source) {
//
//        String path = "";
//
//        if (source.equals(GALLERY)) {
//
//            int currentAPIVersion;
//            try {
//                currentAPIVersion = Build.VERSION.SDK_INT;
//            } catch (NumberFormatException e) {
//                currentAPIVersion = 3;
//            }
//
//            if (currentAPIVersion >= 19)
//                path = getRealPathFromURI_API19(context, uri);
//            else if (currentAPIVersion >= 11 && currentAPIVersion <= 18)
//                path = getRealPathFromURI_API11to18(context, uri);
//            else if (currentAPIVersion < 11)
//                path = getRealPathFromURI_BelowAPI11(context, uri);
//        }
//        else if (source.equals(CAMERA)) {
//            Cursor cursor = null;
//            try {
//                String[] proj = { MediaStore.Images.Media.DATA };
//                cursor = getContentResolver().query(uri,  proj, null, null, null);
//                int index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
//                cursor.moveToFirst();
//                path = cursor.getString(index);
//                cursor.close();
//            }
//            catch (NullPointerException e) {}
//        }
//
//        return path;
//
//    }
//
//    public static String getRealPathFromURI_API19(Context context, Uri uri){
//        String filePath = "";
//        String wholeID = DocumentsContract.getDocumentId(uri);
//
//        // Split at colon, use second item in the array
//        String id = wholeID.split(":")[1];
//
//        String[] column = { MediaStore.Images.Media.DATA };
//
//        // where id is equal to
//        String sel = MediaStore.Images.Media._ID + "=?";
//
//        Cursor cursor = context.getContentResolver().query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
//                column, sel, new String[]{ id }, null);
//
//        int columnIndex = cursor.getColumnIndex(column[0]);
//
//        if (cursor.moveToFirst()) {
//            filePath = cursor.getString(columnIndex);
//        }
//        cursor.close();
//        return filePath;
//    }
//
//    public static String getRealPathFromURI_API11to18(Context context, Uri contentUri) {
//        String[] proj = { MediaStore.Images.Media.DATA };
//        String result = null;
//
//        CursorLoader cursorLoader = new CursorLoader(
//                context,
//                contentUri, proj, null, null, null);
//        Cursor cursor = cursorLoader.loadInBackground();
//
//        if(cursor != null){
//            int column_index =
//                    cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
//            cursor.moveToFirst();
//            result = cursor.getString(column_index);
//        }
//        return result;
//    }
//
//    public static String getRealPathFromURI_BelowAPI11(Context context, Uri contentUri){
//        String[] proj = { MediaStore.Images.Media.DATA };
//        Cursor cursor = context.getContentResolver().query(contentUri, proj, null, null, null);
//        int column_index
//                = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
//        cursor.moveToFirst();
//        return cursor.getString(column_index);
//    }
//
//
//    private static int exifToDegrees(int exifOrientation) {
//        if (exifOrientation == ExifInterface.ORIENTATION_ROTATE_90) { return 90; }
//        else if (exifOrientation == ExifInterface.ORIENTATION_ROTATE_180) {  return 180; }
//        else if (exifOrientation == ExifInterface.ORIENTATION_ROTATE_270) {  return 270; }
//        return 0;
//    }
//
//    private Bitmap rotateBitmap(Context context, Uri uri, Bitmap bitmap, String source) throws IOException {
//        ExifInterface exif = null;
//        if (source.equals("GALLERY"))
//            exif = new ExifInterface(getRealPathFromURI(context, uri, GALLERY));
//        else if (source.equals("CAMERA")) {
//            exif = new ExifInterface(getRealPathFromURI(context, uri, CAMERA));
//        }
//        int rotation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
//        int rotationInDegrees = exifToDegrees(rotation);
//        Matrix matrix = new Matrix();
//        if (rotation != 0f) {
//            matrix.preRotate(rotationInDegrees);
//        }
//        //    getting selected image into Bitmap
//        Bitmap rotatedBitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
//        return rotatedBitmap;
//    }

//    public Uri getImageUri(Context inContext, Bitmap inImage) {
//        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
//        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
//        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, "", null);
//        return Uri.parse(path);
//    }

    private void requestPermissionCamera() {

        ActivityCompat.requestPermissions(SignUpPageActivity.this,
                new String[]{android.Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE},
                PERMISSIONS_REQUEST_CAMERA);

    }


    private void requestPermissionReadExternalStorage() {
        if (ContextCompat.checkSelfPermission(SignUpPageActivity.this, android.Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            // Permission is not granted
            // Permission is not granted
            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(SignUpPageActivity.this,
                    android.Manifest.permission.READ_EXTERNAL_STORAGE)) {

                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.

            } else {

                // No explanation needed; request the permission
                ActivityCompat.requestPermissions(SignUpPageActivity.this,
                        new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE},
                        PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE);

            }
        }

    }

    private void requestPermissionWriteExternalStorage() {

                ActivityCompat.requestPermissions(SignUpPageActivity.this,
                        new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 1: { // cameraAllowed
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.
                    buttonTakePhoto.performClick();
                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                break;
            }
            case 2: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                }
                else {

                }
                break;
            }
            case 3: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                }
                else {

                }
            }

        }
    }

    private boolean isCameraAllowed() {

        return (ContextCompat.checkSelfPermission(SignUpPageActivity.this, android.Manifest.permission.CAMERA)
                == PackageManager.PERMISSION_GRANTED);

    }

    private boolean isWriteExternalStorageAllowed() {
        return ContextCompat.checkSelfPermission(SignUpPageActivity.this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                == PackageManager.PERMISSION_GRANTED;
    }

    private boolean isGalleryAccessAllowed() {
        return ContextCompat.checkSelfPermission(SignUpPageActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE)
                == PackageManager.PERMISSION_GRANTED;

    }

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "LookUp_" + timeStamp;
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

    private void galleryAddPic() {
        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        File f = new File(mCurrentPhotoPath);
        Uri contentUri = Uri.fromFile(f);
        mediaScanIntent.setData(contentUri);
        this.sendBroadcast(mediaScanIntent);
    }

}
