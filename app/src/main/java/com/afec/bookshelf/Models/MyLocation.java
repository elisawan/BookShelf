package com.afec.bookshelf.Models;

public class MyLocation {
    private Double latitude;
    private Double longitude;

    public MyLocation(){
        latitude=0.0;
        longitude=0.0;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    @Override
    public String toString(){
        return "Lat:"+latitude.toString()+" Long"+longitude.toString();
    }
}
