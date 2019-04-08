package com.gardenvillagers.chitchat;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.gson.Gson;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

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

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_account);
        mAuth = FirebaseAuth.getInstance();
        registerUser = (TextView)findViewById(R.id.register);
        username = (EditText)findViewById(R.id.username);
        nickname = (EditText)findViewById(R.id.nickname);
        password = (EditText)findViewById(R.id.password);
        loginButton = (Button)findViewById(R.id.loginButton);
        img = findViewById(R.id.gif1);
        img.setOnClickListener(clickListener);

        phoneUser = new UserDetails();
        mPrefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        //img = findViewById(R.id.gif1);
        //img.setImageResource(R.drawable.login);

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        registerUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createAccount();
            }
        });

    }

    private View.OnClickListener clickListener = new View.OnClickListener() {
        public void onClick(View v) {
            switch(v.getId()){

                case R.id.gif1:
                    Intent photoSelect = new Intent(Intent.ACTION_PICK);
                    photoSelect.setType("image/*");
                    startActivityForResult(photoSelect,1);

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



                            SharedPreferences.Editor prefsEditor = mPrefs.edit();
                            Gson gson = new Gson();
                            phoneUser.setUsername(email);
                            phoneUser.setPhotoURL(phoneUrlString);

                            String json = gson.toJson(phoneUser);
                            Toast.makeText(getApplicationContext(),json + phoneUser.getPhotoURL(), Toast.LENGTH_LONG).show();
                            prefsEditor.putString("phoneUser", json);
                            prefsEditor.commit();
                            prefsEditor.apply();

                            Intent i = new Intent(CreateAccount.this, DisplayUser.class);
                            startActivity(i);

                        } else {
                            updateUI(null);
                        }
                    }
                });
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

                Toast.makeText(this, imageUri.getPath(), Toast.LENGTH_LONG).show();
                phoneUrlString = imageUri.getPath();

            } catch (FileNotFoundException e) {
                e.printStackTrace();
                Toast.makeText(this, "Something went wrong", Toast.LENGTH_LONG).show();
            }

        }else {
            Toast.makeText(this, "You haven't picked Image",Toast.LENGTH_LONG).show();
        }
    }
}