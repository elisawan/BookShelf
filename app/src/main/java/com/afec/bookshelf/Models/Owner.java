package com.afec.bookshelf.Models;

public class Owner {

    private String username;
    private String profileImage;
    private String distance;

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

    public String getUserName() {
        return username;
    }

    public String getProfileImage() {
        return profileImage;
    }
}
