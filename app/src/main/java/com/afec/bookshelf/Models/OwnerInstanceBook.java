package com.afec.bookshelf.Models;

public class OwnerInstanceBook {
    User owner;
    String ownerID;
    String  ISBN;
    String bookInstanceID;

    public OwnerInstanceBook(String ownerID, String ISBN, String bookInstanceID, User owner) {
        this.ownerID = ownerID;
        this.ISBN = ISBN;
        this.bookInstanceID = bookInstanceID;
        this.owner = owner;
    }

    public String getOwnerID() {
        return ownerID;
    }

    public void setOwnerID(String ownerID) {
        this.ownerID = ownerID;
    }

    public String getISBN() {
        return ISBN;
    }

    public void setISBN(String ISBN) {
        this.ISBN = ISBN;
    }

    public String getBookInstanceID() {
        return bookInstanceID;
    }

    public void setBookInstanceID(String bookInstanceID) {
        this.bookInstanceID = bookInstanceID;
    }

    public User getOwner() {
        return owner;
    }

    public void setOwner(User owner) {
        this.owner = owner;
    }
}
