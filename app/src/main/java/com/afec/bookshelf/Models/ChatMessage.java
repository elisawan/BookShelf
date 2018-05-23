package com.afec.bookshelf.Models;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class ChatMessage {

    String message;
    long timestamp;
    Boolean bookReq = false; //is this a special message for book request?
    String bookInstance;
    String bookISBN;
    String uid;
    String toUserID;

    public ChatMessage(){}

    public ChatMessage(String message, String UID, long timestamp){
        super();
        this.message=message;
        this.uid=UID;
        this.timestamp=timestamp;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public Boolean getBookReq() {
        return bookReq;
    }

    public void setBookReq(Boolean bookReq) {
        this.bookReq = bookReq;
    }

    public String getBookInstance() {
        return bookInstance;
    }

    public void setBookInstance(String bookInstance) {
        this.bookInstance = bookInstance;
    }

    public String getBookISBN() {
        return bookISBN;
    }

    public void setBookISBN(String bookISBN) {
        this.bookISBN = bookISBN;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getToUserID() {
        return toUserID;
    }

    public void setToUserID(String toUserID) {
        this.toUserID = toUserID;
    }
}
