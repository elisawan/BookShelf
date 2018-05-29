package com.afec.bookshelf.Models;

import com.google.firebase.database.FirebaseDatabase;

public class Review {
    public static final int STATUS_WRITTEN = 1;
    public static final int STATUS_PENDING = 0;

    private int status;
    private String uidfrom;
    private String uidto;
    private String comment;
    private Float score;
    private Long timestamp;


    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getUidfrom() {
        return uidfrom;
    }

    public void setUidfrom(String uidfrom) {
        this.uidfrom = uidfrom;
    }

    public String getUidto() {
        return uidto;
    }

    public void setUidto(String uidto) {
        this.uidto = uidto;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public Float getScore() {
        return score;
    }

    public void setScore(Float score) {
        this.score = score;
    }

    public Long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Long timestamp) {
        this.timestamp = timestamp;
    }

    public static void addPendingReview(String uidfrom, String uidto){
        Review review1 = new Review();
        review1.setStatus(Review.STATUS_PENDING);
        review1.setUidfrom(uidfrom);
        review1.setUidto(uidto);
        FirebaseDatabase.getInstance().getReference().child("users").child(uidfrom).child("myReviews").push().setValue(review1);
    }
}
