package com.afec.bookshelf.Models;

import android.content.Context;
import android.content.SharedPreferences;

public class User {
    private String uid;
    private int addedBooks;
    private String biography;
    private int borrowedBooks;
    private int lentBooks;
    private float rating;
    private String username;
    private String email;
    private Long timestamp;

    public User() {
    }

    public User(String uid, int addedBooks, String biography, int borrowedBooks, int lentBooks, float rating, String username) {
        this.uid = uid;
        this.addedBooks = addedBooks;
        this.biography = biography;
        this.borrowedBooks = borrowedBooks;
        this.lentBooks = lentBooks;
        this.rating = rating;
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

    public float getRating() {
        return rating;
    }

    public void setRating(float rating) {
        this.rating = rating;
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
        this.rating=sharedPref.getFloat("rating",-1);
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
        editor.putFloat("rating",rating);
        editor.putString("email",email);
        editor.putInt("addedBooks",addedBooks);
        editor.putInt("borrowedBooks",borrowedBooks);
        editor.putInt("lentBooks",lentBooks);
        editor.putLong("timestamp",timestamp);
        editor.commit();
    }

}
