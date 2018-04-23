package com.afec.bookshelf;

import android.location.Location;

public class Book {
    private String title;
    private String isbn;
    private String author;
    private Location location;

    public Book(){}

    public Book(String name, String isbn, String author, Location location) {
        this.title = name;
        this.isbn = isbn;
        this.author = author;
        this.location = location;
    }

    public void setTitle(String name){this.title = name;}
    public void setIsbn(String isbn){this.isbn = isbn;}
    public void setAuthor(String author){this.author = author;}
    public void setLocation(Location location){this.location = location;}

    public String getTitle() {
        return title;
    }
    public String getIsbn() {
        return isbn;
    }
    public String getAuthor() {
        return author;
    }
    public Location getLocation() {
        return location;
    }


}
