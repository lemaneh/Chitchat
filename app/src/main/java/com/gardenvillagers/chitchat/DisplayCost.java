package com.gardenvillagers.chitchat;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class DisplayCost extends AppCompatActivity {
    com.autofit.et.lib.AutoFitEditText logout,accountText, amountMoney, updateInfo;
    ImageView  img;
    UserDetails phoneUser;
    SharedPreferences mPrefs;
    FirebaseUser currentUser;
    DatabaseReference rootDB;
    FirebaseAuth mAuth;
    DatabaseInterface db=null;
    FirebaseDatabase root;
    Date currentTime ;
    String formattedCurrentDate;
    SimpleDateFormat df;
    MyRecyclerViewAdapter adapter;
    RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_cost);
        updateInfo = findViewById(R.id.updateInfo);
        logout = findViewById(R.id.logout);
        logout.setOnClickListener(clickListener);
        accountText = findViewById(R.id.accountText);
        accountText.setOnClickListener(clickListener);
        amountMoney = findViewById(R.id.amountMoney);
        mPrefs= PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        root = FirebaseDatabase.getInstance();
        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        db = new DatabaseInterface(getApplicationContext());
        recyclerView = findViewById(R.id.recyclerView);

        try {
            String json = mPrefs.getString("phoneUser", "");
            if (json.isEmpty())
                phoneUser = new UserDetails();
            else
                phoneUser = new Gson().fromJson(json, UserDetails.class);

        }catch(Exception e){}

        db.addItem(new itemDetails(0,"work", "gas","-25",null,getCurrentDate()));
        db.addItem(new itemDetails(0,"work", "check","+232",null,getCurrentDate()));


        List<itemDetails> items = db.getAllItems();
        ///
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new MyRecyclerViewAdapter(this, items);
        //dapter.setClickListener(this);
        recyclerView.setAdapter(adapter);
        rootDB = root.getReference().child("users").child(currentUser.getUid());
        rootDB.child("money").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                amountMoney.setText(dataSnapshot.getValue().toString());
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) { }
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
                case R.id.logout:
                    mAuth.signOut();
                    Intent i = new Intent(DisplayCost.this, MainActivity.class);
                    startActivity(i);
                    break;

            }
        }
    };

    private String getCurrentDate(){
        currentTime = Calendar.getInstance().getTime();
        System.out.println("Current time => " + currentTime);
        df = new SimpleDateFormat("dd-MMM-yyyy");
        return df.format(currentTime);
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
    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        mAuth = FirebaseAuth.getInstance();
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
                img.setBackgroundResource(0);
                img.setImageBitmap(selectedImage);

                phoneUser.setPhotoURL(saveToInternalStorage(selectedImage));

                Toast.makeText(this, imageUri.getPath(), Toast.LENGTH_LONG).show();

            } catch (FileNotFoundException e) {
                e.printStackTrace();
                Toast.makeText(this, "Something went wrong", Toast.LENGTH_LONG).show();
            }

        }else {
            Toast.makeText(this, "You haven't picked Image",Toast.LENGTH_LONG).show();
        }
    }
}