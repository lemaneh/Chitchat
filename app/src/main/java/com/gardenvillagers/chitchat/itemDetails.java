package com.gardenvillagers.chitchat;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import java.lang.reflect.Type;
import java.sql.Blob;

public class itemDetails {
    private int itemID;
    private String itemType="";
    private String itemNote = "";
    private String itemAmount = "";
    private byte[] itemImage = null;
    private String itemDate = "";

    public itemDetails(int id, String type, String note,String amount, byte[] image, String date){

        itemID=id;
        itemType=type;
        itemNote=note;
        itemAmount=amount;
        itemImage=image;
        itemDate=date;
    }
    public int getItemID(){
        return itemID;
    }
    public String getItemType(){
        return itemType;
    }
    public String getItemNote(){
        return itemNote;
    }
    public byte[] getItemImage(){
        return itemImage;
    }
    public String getItemAmount(){
        return itemAmount;
    }
    public String getItemDate(){
        return itemDate;
    }
    public void setItemID(int i){
        itemID=i;
    }
    public void setItemType(String s){
        itemType=s;
    }
    public void setItemNote(String s){
        itemNote=s;
    }
    public void setItemImage(byte[] b){
        itemImage=b;
    }
    public void setItemAmount(String s){
        itemAmount=s;
    }
    public void setItemDate(String s){
        itemDate=s;
    }
/*
    public static class PersonSerializer implements JsonSerializer<itemDetails> {
        public JsonElement serialize(final itemDetails userDetails, final Type type, final JsonSerializationContext context) {
            JsonObject result = new JsonObject();
            result.add("photoURL", new JsonPrimitive(userDetails.getPhotoURL()));
            result.add("username", new JsonPrimitive(userDetails.getUsername()));
            result.add("userID", new JsonPrimitive(userDetails.getUsername()));
            /*Person parent = person.getParent();
            if (parent != null) {
                result.add("parent", new JsonPrimitive(parent.getId()));
            }
            *//*
            return result;
        }
    }
*/
}
