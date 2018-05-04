package com.afec.bookshelf.MyJsonParser;

import com.afec.bookshelf.Models.Book;

import org.json.JSONObject;

/**
 * Created by elisa on 03/05/18.
 */

public class BookJsonParser {


    public Book parse(JSONObject jsonObject)
    {
        if (jsonObject == null)
            return null;
        String authors = jsonObject.optString("allAuthors");
        String title = jsonObject.optString("title");
        String editionYear = jsonObject.optString("editionYear");
        String author = jsonObject.optString("author");
        String image = jsonObject.optString("image");
        String isbn = jsonObject.optString("isbn");
        String publisher = jsonObject.optString("publisher");
        String thumbnailUrl = jsonObject.optString("thumbnailUrl");
        return new Book(title, isbn, authors, thumbnailUrl, publisher, editionYear);
    }
}

