package com.afec.bookshelf.Models;

import com.afec.bookshelf.Models.Chat;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class ChatMessage {

    private String message;
    private long timestamp;
    private Boolean bookReq = false; //is this a special message for book request?
    private String bookInstance;
    private String bookISBN;
    private String uid;
    private String toUserID;
    private Boolean isRead;

    public ChatMessage(){}

    public ChatMessage(String message, String UID, long timestamp, boolean isRead){
        super();
        this.message=message;
        this.uid=UID;
        this.timestamp=timestamp;
        this.isRead=isRead;
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

    public Boolean getRead() {
        return isRead;
    }

    public void setRead(Boolean isRead) {
        this.isRead = isRead;
    }

    public void acceptRequest(){
        //1.set availability to false
        FirebaseDatabase db = FirebaseDatabase.getInstance();
        DatabaseReference bookInstRef = db.getReference("book_instances").child(bookInstance);
        bookInstRef.child("availability").setValue(false);
        //2.send confirmation message to the user who made the request
        FirebaseUser me = FirebaseAuth.getInstance().getCurrentUser();
        ChatMessage message = new ChatMessage("Hi, I accepted your book request", me.getUid(), System.currentTimeMillis(), false);
        Chat.Companion.sendMsgToChat(message, toUserID, uid);
    }
}
