package com.gardenvillagers.chitchat;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
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
import java.io.InputStream;

public class DisplayUser extends AppCompatActivity {
    TextView registerUser;
    TextView username, nickname;
    Button loginButton,logout;
    String userText, pass,email;
    ProgressDialog pd;
    ImageView  img;
    UserDetails phoneUser;
    SharedPreferences mPrefs;
    FirebaseUser currentUser;

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_user);
        mAuth = FirebaseAuth.getInstance();
        registerUser = (TextView)findViewById(R.id.register);
        username = findViewById(R.id.username);
        nickname = findViewById(R.id.nickname);
        loginButton = (Button)findViewById(R.id.loginButton);
        img = findViewById(R.id.gif1);
        img.setOnClickListener(clickListener);
        logout = findViewById(R.id.logout);
        logout.setOnClickListener(clickListener);
        mPrefs= PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        currentUser = mAuth.getCurrentUser();

        Gson gson = new Gson();
        String json = mPrefs.getString("phoneUser","");

        Toast.makeText(this,json, Toast.LENGTH_LONG).show();
        if (json.isEmpty()) {
            phoneUser = new UserDetails();
        }

        else {
            phoneUser = gson.fromJson(json, UserDetails.class);
            File imgFile = new File(phoneUser.getPhotoURL());

            if(imgFile.exists()){
                Bitmap myBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
                img.setImageBitmap(myBitmap);
                Toast.makeText(this,"EXISTS", Toast.LENGTH_LONG).show();
            }
            Toast.makeText(this,phoneUser.getPhotoURL(),
                    Toast.LENGTH_LONG).show();
            username.setText(currentUser.getEmail());
            nickname.setText(currentUser.getDisplayName());
            registerUser.setText("Hello "+ currentUser.getDisplayName());
        }

    }

    private View.OnClickListener clickListener = new View.OnClickListener() {
        public void onClick(View v) {
            switch(v.getId()){

                case R.id.gif1:
                    Intent photoSelect = new Intent(Intent.ACTION_PICK);
                    photoSelect.setType("image/*");
                    startActivityForResult(photoSelect,1);

                    break;
                case R.id.logout:
                    mAuth.signOut();
                    Intent i = new Intent(DisplayUser.this, MainActivity.class);
                    startActivity(i);
                    break;

            }
        }
    };


    /*
    void updateUI(FirebaseUser thisUser){
        if(thisUser == null) {
            Toast.makeText(this,"Please try again",
                    Toast.LENGTH_LONG).show();
        }
        else {
            Toast.makeText(this,"Hello "+ thisUser.getEmail(),
                    Toast.LENGTH_LONG).show();
            /*Intent i = new Intent(DisplayUser.this, Users.class);
            startActivity(i);

        }
    }*/
    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        currentUser = mAuth.getCurrentUser();
    }

    @Override
    protected void onActivityResult(int reqCode, int resultCode, Intent data) {
        super.onActivityResult(reqCode, resultCode, data);


        if (resultCode == RESULT_OK) {
            try {
                final Uri imageUri = data.getData();
                final InputStream imageStream = getContentResolver().openInputStream(imageUri);
                final Bitmap selectedImage = BitmapFactory.decodeStream(imageStream);
                img.setImageBitmap(selectedImage);

                SharedPreferences.Editor prefsEditor = mPrefs.edit();
                Gson gson = new Gson();
                phoneUser.setPhotoURL(imageUri.getPath());
                String json = gson.toJson(phoneUser);
                prefsEditor.putString("phoneUser", json);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
                Toast.makeText(this, "Something went wrong", Toast.LENGTH_LONG).show();
            }

        }else {
            Toast.makeText(this, "You haven't picked Image",Toast.LENGTH_LONG).show();
        }
    }
}