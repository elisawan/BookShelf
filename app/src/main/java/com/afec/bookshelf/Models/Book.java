package com.afec.bookshelf.Models;

import android.content.Context;
import android.location.Location;

import java.util.ArrayList;
import java.util.List;

public class Book {
    private String title;
    private String isbn;
    private List<String> authors;
    private String thumbnailUrl;
    private String publisher;
    private String editionYear;

    public static final int likeNew = 0;
    public static final int veryGood = 1;
    public static final int good = 2;
    public static final int acceptable = 3;

    public Book(){
        this.authors = new ArrayList<String>();
    }

    public Book(String name, String isbn, List<String> authors) {
        super();
        this.authors = authors;
        this.title = name;
        this.isbn = isbn;
        this.publisher = null;
        this.editionYear = null;
    }

    public void setTitle(String name){this.title = name;}
    public void setIsbn(String isbn){this.isbn = isbn;}
    public void setAuthor(String author){
        this.authors.add(author);
    }
    public void setThumbnailUrl(String thumbnailUrl) {this.thumbnailUrl = thumbnailUrl;}
    public void setEditionYear(String editionYear) {this.editionYear = editionYear;}
    public void setPublisher(String publisher) {this.publisher = publisher;}

    public String getTitle() {
        return title;
    }
    public String getIsbn() {
        return isbn;
    }
    public List<String> getAuthors() {
        return authors;
    }
    public String getThumbnailUrl() {
        return thumbnailUrl;
    }
    public String getEditionYear() {return editionYear;}
    public String getPublisher() {return publisher;}

    public String getAllAuthors(){
        String all = "";
        if(authors == null){
            return null;
        }
        for(String a : authors){
            all = all+", "+a;
        }
        return all;
    }

}
