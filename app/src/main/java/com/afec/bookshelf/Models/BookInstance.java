package com.afec.bookshelf.Models;

import android.location.Location;

public class BookInstance {

    private String isbn;
    private String owner;
    private Location location;
    private int status;
    private String currentDateTime;
    private boolean availability;

    public void BookInstance(){
        this.isbn = null;
        this.owner = null;
        this.location.setLatitude(0);
        this.location.setLongitude(0);
        this.status=0;
        this.currentDateTime=null;
        this.availability=false;
    }

    public BookInstance(String isbn, Location location, String owner, int status, String date, boolean availability) {
        this.isbn = isbn;
        this.owner = owner;
        this.location = location;
        this.status=status;
        this.currentDateTime=date;
        this.availability=availability;
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


    public String getCurrentDateTime() {
        return currentDateTime;
    }
    public void setTime(String date) {
        this.currentDateTime=date;
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

    public boolean getAvailability() {
        return availability;
    }
    public void setAvailability(boolean availability) {
        this.availability = availability;
    }




}
