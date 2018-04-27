package com.afec.bookshelf.Models;

import android.location.Location;



public class BookInstance {

    String isbn;
    String owner;
    Location location;
    int status;

    public void BookInstance(){
        this.isbn = null;
        this.owner = null;
        this.location.setLatitude(0);
        this.location.setLongitude(0);
        this.status=0;
    }

    public BookInstance(String isbn, Location location, String owner, int status) {
        this.isbn = isbn;
        this.owner = owner;
        this.location = location;
        this.status=status;
    }

    public String getIsbn() {
        return isbn;
    }

    public void setIsbn(String isbn) {
        this.isbn = isbn;
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }


    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }


    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }



}
