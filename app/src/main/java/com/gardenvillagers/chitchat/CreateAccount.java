package com.gardenvillagers.chitchat;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.client.Firebase;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.gson.GsonBuilder;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class CreateAccount extends AppCompatActivity {
    TextView registerUser;
    EditText username, password,nickname;
    Button loginButton;
    String userText, pass,email;
    ProgressDialog pd;
    ImageView  img;
    UserDetails phoneUser;
    private SharedPreferences mPrefs;
    private String phoneUrlString="";
    DatabaseReference rootDB;

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_account);
        mAuth = FirebaseAuth.getInstance();
        registerUser = (TextView)findViewById(R.id.updateInfo);
        username = (EditText)findViewById(R.id.username);
        nickname = (EditText)findViewById(R.id.nickname);
        password = (EditText)findViewById(R.id.password);
        password.setOnEditorActionListener(new EditText.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    registerUser.performClick();
                    return true;
                }
                return false;
            }
        });
        loginButton = (Button)findViewById(R.id.loginButton);
        img = findViewById(R.id.gif1);
        img.setOnClickListener(clickListener);

        phoneUser = new UserDetails();
        mPrefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        registerUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createAccount();
            }
        });
        rootDB = FirebaseDatabase.getInstance().getReference();
        Firebase.setAndroidContext(this);

    }

    private View.OnClickListener clickListener = new View.OnClickListener() {
        public void onClick(View v) {
            switch(v.getId()){

                case R.id.gif1:
                    Intent photoSelect = new Intent(Intent.ACTION_PICK);
                    photoSelect.setType("image/*");
                    startActivityForResult(photoSelect,1);

                    break;

                case R.id.login:
                    mAuth.signOut();
                    Intent i = new Intent(CreateAccount.this, Login.class);
                    startActivity(i);
                    break;

            }
        }
    };
    void createAccount(){
        email= username.getText().toString();
        pass = password.getText().toString();
        mAuth.createUserWithEmailAndPassword(email, pass)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            FirebaseUser user = mAuth.getCurrentUser();
                            UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                                    .setDisplayName(nickname.getText().toString()).build();
                            user.updateProfile(profileUpdates);


                            phoneUser.setUsername(email);
                            phoneUser.setPhotoURL(phoneUrlString);
                            com.google.gson.Gson gson = new GsonBuilder().registerTypeAdapter(UserDetails.class, new UserDetails.PersonSerializer())
                                    .create();
                            //System.out.println(gson.toJson(phoneUser));


                            SharedPreferences.Editor prefsEditor = mPrefs.edit();

                            String json = gson.toJson(phoneUser);
                            prefsEditor.putString("phoneUser", json);
                            prefsEditor.commit();
                            prefsEditor.apply();

                            DatabaseReference userNode = rootDB.child("users").child(user.getUid());
                            userNode.child("nickname").setValue(nickname.getText().toString());
                            userNode.child("email").setValue(email);
                            userNode.child("money").setValue("$0.00");
                            userNode.child("location").setValue("-,-");

                            Intent i = new Intent(CreateAccount.this, DisplayUser.class);
                            startActivity(i);

                        } else {
                            updateUI(null);
                        }
                    }
                });
    }


    private String saveToInternalStorage(Bitmap bitmapImage){
        ContextWrapper cw = new ContextWrapper(getApplicationContext());
        // path to /data/data/yourapp/app_data/imageDir
        File directory = cw.getDir("imageDir", Context.MODE_PRIVATE);
        // Create imageDir
        File mypath=new File(directory,"profile.jpg");

        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(mypath);
            // Use the compress method on the BitMap object to write image to the OutputStream
            bitmapImage.compress(Bitmap.CompressFormat.PNG, 100, fos);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                fos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return directory.getAbsolutePath();
    }
    void updateUI(FirebaseUser thisUser){
        if(thisUser == null) {
        }
        else {
            Intent i = new Intent(CreateAccount.this, DisplayUser.class);
            startActivity(i);
        }
    }
    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        updateUI(currentUser);
    }

    @Override
    protected void onActivityResult(int reqCode, int resultCode, Intent data) {
        super.onActivityResult(reqCode, resultCode, data);


        if (resultCode == RESULT_OK) {
            try {
                final Uri imageUri = data.getData();
                final InputStream imageStream = getContentResolver().openInputStream(imageUri);
                final Bitmap selectedImage = BitmapFactory.decodeStream(imageStream);
                img.setBackgroundResource(0);
                img.setImageBitmap(selectedImage);

                phoneUrlString = saveToInternalStorage(selectedImage);

                com.google.gson.Gson gson = new GsonBuilder().registerTypeAdapter(UserDetails.class, new UserDetails.PersonSerializer())
                        .create();
                //System.out.println(gson.toJson(phoneUser));


                SharedPreferences.Editor prefsEditor = mPrefs.edit();

                String json = gson.toJson(phoneUser);
                //Toast.makeText(getApplicationContext(),json + gson.toJson(phoneUser), Toast.LENGTH_LONG).show();
                prefsEditor.putString("phoneUser", json);
                prefsEditor.commit();
                prefsEditor.apply();

                //Toast.makeText(this, imageUri.getPath(), Toast.LENGTH_LONG).show();

            } catch (FileNotFoundException e) {
                e.printStackTrace();
                Toast.makeText(this, "Something went wrong", Toast.LENGTH_LONG).show();
            }

        }else {
            Toast.makeText(this, "You haven't picked Image",Toast.LENGTH_LONG).show();
        }
    }
}