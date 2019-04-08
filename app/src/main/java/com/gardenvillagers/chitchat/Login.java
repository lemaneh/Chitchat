package com.gardenvillagers.chitchat;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
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

public class Login extends AppCompatActivity {
    TextView registerUser;
    EditText username, password;
    Button loginButton;
    String userText, pass,email;
    ProgressDialog pd;
    ImageView  img;

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        mAuth = FirebaseAuth.getInstance();
        registerUser = (TextView)findViewById(R.id.register);
        username = (EditText)findViewById(R.id.username);
        password = (EditText)findViewById(R.id.password);
        loginButton = (Button)findViewById(R.id.loginButton);

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
    void createAccount(){
        Intent i = new Intent(Login.this, CreateAccount.class);
        startActivity(i);

        /*
        email= username.getText().toString();
        pass = password.getText().toString();
        mAuth.createUserWithEmailAndPassword(email, pass)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            FirebaseUser user = mAuth.getCurrentUser();

                            updateUI(user);
                            startActivity(new Intent(Login.this, Users.class));

                        } else {
                            updateUI(null);
                        }

                        // ...
                    }
                });*/
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