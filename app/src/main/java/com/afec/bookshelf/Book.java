package com.afec.bookshelf;

import android.location.Location;

public class Book {
    private String title;
    private String isbn;
    private String author;
    private String location;
    private int status;
    private String thumbnailUrl;

    public static final int likeNew = 0;
    public static final int veryGood = 1;
    public static final int good = 2;
    public static final int acceptable = 3;

    public Book(){}

    public Book(String name, String isbn, String author, String location) {
        this.title = name;
        this.isbn = isbn;
        this.author = author;
        this.location = location;
    }

    public void setTitle(String name){this.title = name;}
    public void setIsbn(String isbn){this.isbn = isbn;}
    public void setAuthor(String author){this.author = author;}
    public void setLocation(String location){this.location = location;}
    public void setStatus(int status){this.status = status;}
    public void setThumbnailUrl(String thumbnailUrl) {
        this.thumbnailUrl = thumbnailUrl;
    }


    public String getTitle() {
        return title;
    }
    public String getIsbn() {
        return isbn;
    }
    public String getAuthor() {
        return author;
    }
    public int getStatus(){return status; }
    public String getLocation() {
        return location;
    }
    public String getThumbnailUrl() {
        return thumbnailUrl;
    }


}
