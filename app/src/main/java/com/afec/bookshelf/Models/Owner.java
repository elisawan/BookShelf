package com.afec.bookshelf.Models;

public class Owner {

    private String username;
    private String profileImage;
    private String biography;
    private String distance;
    private Integer addedBooks;
    private Integer borrowedBooks;
    private Integer lentBooks;
    private Integer rating;
    private String uid;

    public Owner(){}

    public Owner(String userName, String image) {

        this.username = userName;
        this.profileImage = image;
        this.distance = "-km";
    }

    public String getDistance() {

        return distance;
    }

    public void setDistance(String distance) {

        this.distance = distance;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getProfileImage() {
        return profileImage;
    }

    public void setProfileImage(String profileImage) {
        this.profileImage = profileImage;
    }

    public String getBiography() {
        return biography;
    }

    public void setBiography(String biography) {
        this.biography = biography;
    }

    public int getAddedBooks() {
        return addedBooks;
    }

    public void setAddedBooks(int addedBooks) {
        this.addedBooks = addedBooks;
    }

    public Integer getBorrowedBooks() {
        return borrowedBooks;
    }

    public void setBorrowedBooks(int borrowedBooks) {
        this.borrowedBooks = borrowedBooks;
    }

    public Integer getLentBooks() {
        return lentBooks;
    }

    public void setLentBooks(int lentBooks) {
        this.lentBooks = lentBooks;
    }

    public Integer getRating() {
        return rating;
    }

    public void setRating(int rating) {
        this.rating = rating;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }
}
