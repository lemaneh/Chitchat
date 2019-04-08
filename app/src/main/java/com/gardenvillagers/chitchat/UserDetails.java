package com.gardenvillagers.chitchat;

import android.net.Uri;

public class UserDetails {
    static String username = "";
    static String password = "";
    static String chatWith = "";
    static String photoURL="";
    static Uri photoURI;
    public void setUsername(String un){username=un;}
    public String getPhotoURL(){return photoURL;}
    public void setPhotoURL(String url){photoURL = url;}
    public Uri getPhotoURI(){return photoURI;}

    public void setPhotoURI(Uri url){photoURI = url;}
}
