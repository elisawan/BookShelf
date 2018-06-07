package com.afec.bookshelf.Models;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.FileInputStream;
import java.io.ObjectInputStream;
import java.io.Serializable;

public class User implements Serializable{

    private String uid;
    private int addedBooks;
    private String biography;
    private int borrowedBooks;
    private int lentBooks;
    private String username;
    private String email;
    private Long timestamp = (long)0;
    private int ratingCount = 0;
    private float ratingSum = 0;
    private int credit=0;

    public User() {}

    public User (String uid, String username){
        this.uid = uid;
        this.username = username;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public int getAddedBooks() {
        return addedBooks;
    }

    public void setAddedBooks(int addedBooks) {
        this.addedBooks = addedBooks;
    }

    public String getBiography() {
        return biography;
    }

    public void setBiography(String biography) {
        this.biography = biography;
    }

    public int getBorrowedBooks() {
        return borrowedBooks;
    }

    public void setBorrowedBooks(int borrowedBooks) {
        this.borrowedBooks = borrowedBooks;
    }

    public int getLentBooks() {
        return lentBooks;
    }

    public void setLentBooks(int lentBooks) {
        this.lentBooks = lentBooks;
    }

    public int getRatingCount() {
        return ratingCount;
    }

    public void setRatingCount(int ratingCount) {
        this.ratingCount = ratingCount;
    }

    public float getRatingSum() {
        return ratingSum;
    }

    public void setRatingSum(float ratingSum) {
        this.ratingSum = ratingSum;
    }

    public float getRating() {
        if(ratingCount!=0)
            return ratingSum/ratingCount;
        else
            return 0;
    }

    public void setRating(int sum, int count){
        this.ratingCount = count;
        this.ratingSum = sum;
    }

    public void setCredit(){
        this.credit = 0;
    }

    public int getCredit(){
        return this.credit;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Long timestamp) {
        this.timestamp = timestamp;
    }

    public boolean getUserLocalData(Context context, String uid){
        SharedPreferences sharedPref = context.getSharedPreferences(uid, Context.MODE_PRIVATE);

        this.uid = uid;
        this.username = sharedPref.getString("username", null);
        if(this.username==null) //a user cannot be without username
            return false;
        this.biography=sharedPref.getString("biography","");
        this.ratingSum=sharedPref.getInt("ratingSum",0);
        this.ratingCount=sharedPref.getInt("ratingCount",0);
        this.email=sharedPref.getString("emailUtente", null);
        if(this.email==null) //a user cannot be without email
            return false;
        this.addedBooks=sharedPref.getInt("addedBooks",0);
        this.borrowedBooks=sharedPref.getInt("borrowedBooks",0);
        this.lentBooks=sharedPref.getInt("lentBooks",0);
        this.timestamp=sharedPref.getLong("timestamp",0);

        return true;
    }

    public void updateSharedPrefContent(Context context){
        SharedPreferences sharedPref = context.getSharedPreferences(uid, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString("uid", uid);
        editor.putString("username", username);
        editor.putString("biography", biography);
        editor.putInt("ratingCount",ratingCount);
        editor.putFloat("ratingSum",ratingSum);
        editor.putString("email",email);
        editor.putInt("addedBooks",addedBooks);
        editor.putInt("borrowedBooks",borrowedBooks);
        editor.putInt("lentBooks",lentBooks);
        editor.putLong("timestamp",timestamp);
        editor.commit();
    }
}
