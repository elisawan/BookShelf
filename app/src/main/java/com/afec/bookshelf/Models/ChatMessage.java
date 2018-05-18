package com.afec.bookshelf.Models;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

public class ChatMessage {
    String message;
    String UID;
    long timestamp;

    public ChatMessage(String message, String UID, long timestamp){
        this.message=message;
        this.UID=UID;
        this.timestamp=timestamp;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getUID() {
        return UID;
    }

    public void setUID(String UID) {
        this.UID = UID;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }
}
