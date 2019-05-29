package com.gardenvillagers.chitchat;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import java.util.ArrayList;
import java.util.List;

public class DatabaseInterface extends SQLiteOpenHelper {
    private static int DATABASE_VERSION = 1;
    private static String DATABASE_NAME = "chichat";
    private static String TABLE_ = "items";
    private static final String KEY_ID = "id";
    private static final String KEY_TYPE = "type";
    private static final String KEY_AMOUNT= "amount";
    private static final String KEY_NOTE = "note";
    private static final String KEY_IMAGE = "image";
    private static final String KEY_DATE = "phone_number";
    private FirebaseUser currentUser;
    private FirebaseAuth mAuth;

    public DatabaseInterface(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        //3rd argument to be passed is CursorFactory instance
    }
    // Creating Tables
    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_TABLE = "CREATE TABLE " + TABLE_ + " ("
                + KEY_ID + " INTEGER PRIMARY KEY," + KEY_TYPE + " TEXT,"
                + KEY_AMOUNT + " TEXT," + KEY_NOTE + " TEXT," + KEY_IMAGE
                + " BLOB," + KEY_DATE + " TEXT" + ")";
        db.execSQL(CREATE_TABLE);
    }

    // Upgrading database
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older table if existed
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_);

        // Create tables again
        onCreate(db);
    }

    // code to add the new contact
    void addItem(itemDetails item) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_TYPE, item.getItemType());
        values.put(KEY_AMOUNT, item.getItemAmount());
        values.put(KEY_NOTE, item.getItemNote());
        values.put(KEY_IMAGE, item.getItemImage());
        values.put(KEY_DATE, item.getItemDate());

        // Inserting Row
        db.insert(TABLE_, null, values);
        //2nd argument is String containing nullColumnHack
        db.close(); // Closing database connection
    }

    itemDetails getItem(int id){

        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(TABLE_, new String[] { KEY_ID,
                        KEY_TYPE, KEY_AMOUNT, KEY_NOTE, KEY_IMAGE,KEY_DATE }, KEY_ID + "=?",
                new String[] { String.valueOf(id) }, null, null, null, null);
        if (cursor != null)
            cursor.moveToFirst();

        itemDetails item = new itemDetails(Integer.parseInt(cursor.getString(0)),
                cursor.getString(1), cursor.getString(2),
                cursor.getString(3), cursor.getBlob(4),
                cursor.getString(5));
        // return contact
        return item;


    }

    // code to get all contacts in a list view
    public List<itemDetails> getAllItems() {
        List<itemDetails> itemList = new ArrayList<itemDetails>();
        // Select All Query
        String selectQuery = "SELECT  * FROM " + TABLE_;
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor;
        try {
            cursor = db.rawQuery(selectQuery, null);
        }catch(Exception e){
            onCreate(db);
            cursor = db.rawQuery(selectQuery, null);
        }
        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                itemDetails item = new itemDetails(Integer.parseInt(cursor.getString(0)),
                        cursor.getString(1), cursor.getString(2),
                        cursor.getString(3), cursor.getBlob(4),
                        cursor.getString(5));
                // Adding contact to list
                itemList.add(item);
            } while (cursor.moveToNext());
        }
        // return contact list
        return itemList;
    }




    // Getting contacts Count
    public int getItemsCount() {
        String countQuery = "SELECT  * FROM " + TABLE_;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);
        cursor.close();

        // return count
        return cursor.getCount();
    }


    /*

    // code to update the single contact
    public int updateContact(Contact contact) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_NAME, contact.getName());
        values.put(KEY_PH_NO, contact.getPhoneNumber());

        // updating row
        return db.update(TABLE_CONTACTS, values, KEY_ID + " = ?",
                new String[] { String.valueOf(contact.getID()) });
    }

    // Deleting single contact
    public void deleteContact(Contact contact) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_CONTACTS, KEY_ID + " = ?",
                new String[] { String.valueOf(contact.getID()) });
        db.close();
    }


    // Getting contacts Count
    public int getContactsCount() {
        String countQuery = "SELECT  * FROM " + TABLE_CONTACTS;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);
        cursor.close();

        // return count
        return cursor.getCount();
    }
    */

}