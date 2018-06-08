package com.afec.bookshelf.Models;

import com.afec.bookshelf.Models.Chat;
import com.afec.bookshelf.R;
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

    private Boolean responded;
    private String messageID;
    private String chatID;

    public ChatMessage(){}

    public ChatMessage(String message, String UID, long timestamp, boolean isRead){
        super();
        this.message=message;
        this.uid=UID;
        this.timestamp=timestamp;
        this.isRead=isRead;
        this.responded=true;
    }

    public void setRead(Boolean read) {
        isRead = read;
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

    public Boolean getResponded() {
        return responded;
    }

    public void setResponded(Boolean responded) {
        this.responded = responded;
    }

    public String getMessageID() {
        return messageID;
    }

    public void setMessageID(String messageID) {
        this.messageID = messageID;
    }

    public String getChatID() {
        if(chatID == null){
            this.chatID = Chat.Companion.chatID(this.uid,this.toUserID);
        }
        return chatID;
    }

    public void setChatID(String chatID) {
        this.chatID = chatID;
    }

    public void acceptRequest(){
        //1.set availability to false
        FirebaseDatabase db = FirebaseDatabase.getInstance();
        DatabaseReference bookInstRef = db.getReference("book_instances").child(bookInstance);
        bookInstRef.child("availability").setValue(false);
        db.getReference("chat").child(chatID).child(messageID).child("responded").setValue(true);
        //2.send confirmation message to the user who made the request
        FirebaseUser me = FirebaseAuth.getInstance().getCurrentUser();
        ChatMessage acceptMessage = new ChatMessage();
        acceptMessage.message = String.valueOf(R.string.book_request_accepted_message);
        acceptMessage.isRead = false;
        acceptMessage.timestamp = System.currentTimeMillis();
        acceptMessage.toUserID = this.uid;
        acceptMessage.uid = this.toUserID;
        acceptMessage.chatID = chatID;
        Chat.Companion.sendMsgToChat(acceptMessage, toUserID, uid);
    }

    public void declineRequest(){
        //1.set availability to true
        FirebaseDatabase db = FirebaseDatabase.getInstance();
        DatabaseReference bookInstRef = db.getReference("book_instances").child(bookInstance);
        bookInstRef.child("availability").setValue(true);
        db.getReference("chat").child(chatID).child(messageID).child("responded").setValue(true);
        //2.send decline message to the user who made the request
        FirebaseUser me = FirebaseAuth.getInstance().getCurrentUser();
        ChatMessage declineMessage = new ChatMessage();
        declineMessage.message = String.valueOf(R.string.book_request_declined_message) ;
        declineMessage.isRead = false;
        declineMessage.timestamp = System.currentTimeMillis();
        declineMessage.toUserID = this.uid;
        declineMessage.uid = this.toUserID;
        declineMessage.chatID = chatID;
        Chat.Companion.sendMsgToChat(declineMessage, toUserID, uid);
    }
}
