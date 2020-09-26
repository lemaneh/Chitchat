package com.gardenvillagers.chitchat;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
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
import com.google.gson.Gson;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

public class Login extends AppCompatActivity {
    TextView registerUser;
    EditText username, password;
    Button loginButton;
    String userText, pass,email;
    ProgressDialog pd;
    ImageView  img;
    UserDetails phoneUser;
    SharedPreferences mPrefs;

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        mAuth = FirebaseAuth.getInstance();
        registerUser = (TextView)findViewById(R.id.updateInfo);
        username = (EditText)findViewById(R.id.username);
        password = (EditText)findViewById(R.id.password);
        password.setOnEditorActionListener(new EditText.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    loginButton.performClick();
                    return true;
                }
                return false;
            }
        });
        loginButton = (Button)findViewById(R.id.loginButton);

        try {
            String json = mPrefs.getString("phoneUser", "");

            if (json.isEmpty()) {
                phoneUser = new UserDetails();
            } else {

                phoneUser = new Gson().fromJson(json, UserDetails.class);

                if (!phoneUser.getPhotoURL().equals("")) {
                    loadImageFromStorage(phoneUser.getPhotoURL());
                    img.setBackgroundResource(0);
                    Toast.makeText(this, "INSIDE PHOTO URL", Toast.LENGTH_SHORT).show();
                } else
                    Toast.makeText(this, "NO PICTURE", Toast.LENGTH_SHORT).show();
            }

        }catch(Exception e){}


        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        registerUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createAccount();
            }
        });
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                userText = username.getText().toString();
                pass = password.getText().toString();

                if(userText.equals("")){
                    username.setError("can't be blank");
                }
                else if(pass.equals("")){
                    password.setError("can't be blank");
                }
                else{
                    String url = "https://androidchatapp-76776.firebaseio.com/users.json";
                    pd = new ProgressDialog(Login.this);
                    pd.setMessage("Loading...");
                    pd.show();
                    signIn();
                }

            }
        });
    }

    private void loadImageFromStorage(String path)
    {

        try {
            File f=new File(path, "profile.jpg");
            Bitmap b = BitmapFactory.decodeStream(new FileInputStream(f));
            img.setImageBitmap(b);
        }
        catch (FileNotFoundException e)
        {
            e.printStackTrace();
        }

    }
    void createAccount(){
        Intent i = new Intent(Login.this, CreateAccount.class);
        startActivity(i);

    }
    void signIn(){
        email= username.getText().toString();
        pass = password.getText().toString();
        mAuth.signInWithEmailAndPassword(email, pass)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            FirebaseUser user = mAuth.getCurrentUser();
                            pd.dismiss();
                            Intent i = new Intent(Login.this, DisplayUser.class);
                            startActivity(i);
                        } else {
                            pd.dismiss();
                            updateUI(null);
                        }

                        // ...
                    }
                });

    }
    void updateUI(FirebaseUser thisUser){

        if(thisUser == null) {
            Toast.makeText(this,"Please Login",
                    Toast.LENGTH_LONG).show();
            /*Intent i = new Intent(Login.this, Login.class);
            startActivity(i);
            */
        }
        else {
            Toast.makeText(this,"Successful!",
                    Toast.LENGTH_LONG).show();
        }
    }
    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();


        updateUI(currentUser);
    }
}