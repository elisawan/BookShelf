package com.afec.bookshelf.Models;

import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.DatabaseReference;

public class BookInstance {

    private String bookId;
    private String isbn;
    private String owner;
    private MyLocation location;
    private int status;
    private String currentDateTime;
    private boolean availability;

    public BookInstance(){
        this.isbn = null;
        this.owner = null;
        this.location = new MyLocation();
        this.status=0;
        this.currentDateTime=null;
        this.availability=false;
    }

    public BookInstance(String isbn, MyLocation location, String owner, int status, String date, boolean availability) {
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


    public MyLocation getLocation() {
        return location;
    }
    public void setLocation(MyLocation location) {
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

    public String getBookId() {
        return bookId;
    }

    public void setBookId(String bookId) {
        this.bookId = bookId;
    }

    public void acceptRequest (String fromUser, String toUser){

        //1.set availability to false in Firebase
        DatabaseReference bookInstRef = BookInstance.getFirebaseRef();
        bookInstRef.child(bookId).child("availability").setValue(false);

        //2. create confirmation message
        String msg = "I acceped your book request";
        ChatMessage message = new ChatMessage(msg, fromUser, System.currentTimeMillis());

        //3.send the confirmation message in chat
        Chat.Companion.sendMsgToChat(message, fromUser, toUser);
    }

    static public DatabaseReference getFirebaseRef (){
        FirebaseDatabase db = FirebaseDatabase.getInstance();
        return db.getReference("bookInstance");
    }
}
