package com.afec.bookshelf.Models;

public class ChatMessage {
    String message;
    String uid;
    long timestamp;



    Boolean bookReq;

    public ChatMessage(String message, String UID, long timestamp){
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

    public String getUid() {
        return uid;
    }

    public void setUid(String UID) {
        this.uid = UID;
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
}
