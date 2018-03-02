package edu.ucsb.cs48.lookup;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import android.app.ActionBar;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.StrictMode;
import android.provider.ContactsContract;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.util.Log;

import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
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
    private String userID, fbUserID;
    private LinearLayout mLinearLayout;
    private Context mContext;
    private PopupWindow editProfilePicPopup;
    private DatabaseReference mDatabase;
    private ImageView editUserProfilePic;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.edit_user_profile_page);

        mAuth = FirebaseAuth.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();

        userID = user.getUid();

        mDatabase = FirebaseDatabase.getInstance().getReference();

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

        facebookRef = userRef.child("facebookID");
        facebookRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                fbUserID = dataSnapshot.getValue(String.class);
                facebookLink = (TextView) findViewById(R.id.facebookLink);
                facebookLink.setText("https://facebook.com" + dataSnapshot.getValue(String.class));
                userProfileData.put(dataSnapshot.getKey(), dataSnapshot.getValue(String.class));
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {}
        });

        profilePicRef = userRef.child("profilePic");
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
        setContentView(R.layout.edit_user_profile_page);
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
//                        try {
//                            URL imageURL = new URL("http://graph.facebook.com/" + fbUserID + "/picture?type=large");
//                            Bitmap userProfilePic = BitmapFactory.decodeStream(imageURL.openConnection().getInputStream());
//                            editUserProfilePic.setImageBitmap(userProfilePic);
//                            addProfilePicToDatabase(userProfilePic);
//                            String fbProfilePicURL = "http://graph.facebook.com/" + fbUserID + "/picture?type=large";

                        // this works
//                        String url = "https://graph.facebook.com/" + fbUserID + "/picture?type=large";
//                        new AsyncTaskLoadImage(editUserProfilePic).execute(url);

                        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
                        StrictMode.setThreadPolicy(policy);
                        try {
                            URL url = new URL("https://graph.facebook.com/" + fbUserID + "/picture?type=large");
                            editUserProfilePic.setImageBitmap(BitmapFactory.decodeStream((InputStream)url.getContent()));
                            Log.d(TAG, "TEST");
                            String userProfilePicURL = getRedirectedURL(url.toString());
                            userProfileData.put("profilePic", userProfilePicURL);

                        } catch (IOException e) {
                            Log.e(TAG, e.getMessage());
                        }

//                        }
//                        catch (IOException e) {
//                            e.printStackTrace();
//                        }

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
                updateDatabase();
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

//    private Bitmap importProfilePicFromFB() throws IOException {
//        Log.d(TAG, "facebook id:" + fbUserID);
//        URL imageURL = new URL("https://graph.facebook.com/" + fbUserID + "/picture?type=large");
//
//        Bitmap bitmap = BitmapFactory.decodeStream(imageURL.openConnection().getInputStream());
//
//        Log.d(TAG, "success");
//
//        return bitmap;
//
//    }

    private void addProfilePicToDatabase(Bitmap profilePic) {
        userProfileData.put("profilePic", profilePic.toString());
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

    public static Bitmap getBitmapFromURL(String src) {
        try {
//            Log.e("src", src);
            URL url = new URL(src);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.connect();
            InputStream input = connection.getInputStream();
            Bitmap myBitmap = BitmapFactory.decodeStream(input);
//            Log.e("Bitmap", "returned");
            return myBitmap;
        } catch (IOException e) {
            e.printStackTrace();
//            Log.e("Exception", e.getMessage());
            return null;
        }

    }

}