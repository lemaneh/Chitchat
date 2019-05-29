package com.gardenvillagers.chitchat;

import android.net.Uri;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import java.lang.reflect.Type;

public class UserDetails {
    private String username = "";
    private String userID="";
    //private DatabaseInterface db=null;
    static String password = "";
    static String chatWith = "";
    private String photoURL="";
    public void setUsername(String un){username=un;}
    public String getUsername(){return username;}
    public void setUserID(String un){userID=un;}
    public String getUserID(){return userID;}
    //public void setDB(DatabaseInterface dbi){db=dbi;}
    //public String getDB(){return userID;}
    public String getPhotoURL(){return photoURL;}
    public void setPhotoURL(String url){photoURL = url;}

    public static class PersonSerializer implements JsonSerializer<UserDetails> {
        public JsonElement serialize(final UserDetails userDetails, final Type type, final JsonSerializationContext context) {
            JsonObject result = new JsonObject();
            result.add("photoURL", new JsonPrimitive(userDetails.getPhotoURL()));
            result.add("username", new JsonPrimitive(userDetails.getUsername()));
            result.add("userID", new JsonPrimitive(userDetails.getUserID()));
            //result.add("db", new JsonPrimitive(userDetails.getDB()));
            /*Person parent = person.getParent();
            if (parent != null) {
                result.add("parent", new JsonPrimitive(parent.getId()));
            }
            */
            return result;
        }
    }

}
