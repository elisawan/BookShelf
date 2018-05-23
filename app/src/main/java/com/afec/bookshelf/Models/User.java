package com.afec.bookshelf.Models;


import android.graphics.Bitmap;

public class User {
    private String uid;
    private int addedBooks;
    private String biography;
    private int borrowedBooks;
    private int lentBooks;
    private float rating;
    private String username;
    private Bitmap userPic;

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
}
