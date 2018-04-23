package com.afec.bookshelf;

public class Owner {
    private int color;
    private String pseudo;
    private String text;
    private String distance;

    public Owner(int color, String pseudo, String text, String distance) {
        this.color = color;
        this.pseudo = pseudo;
        this.text = text;
        this.distance = distance;
    }

    public String getDistance() {
        return distance;
    }

    public void setDistance(String distance) {
        this.distance = distance;
    }

    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
    }

    public String getPseudo() {
        return pseudo;
    }

    public void setPseudo(String pseudo) {
        this.pseudo = pseudo;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
}
