package com.afec.bookshelf.MyJsonParser;

import com.afec.bookshelf.Book;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by elisa on 03/05/18.
 */

public class SearchResultBookJsonParser {

    private BookJsonParser parser = new BookJsonParser();

    public List<Book> parseResults(JSONObject jsonObject)
    {
        if (jsonObject == null)
            return null;
        List<Book> results = new ArrayList<Book>();
        JSONArray hits = jsonObject.optJSONArray("hits");
        if (hits == null)
            return null;
        for (int i = 0; i < hits.length(); ++i) {
            JSONObject hit = hits.optJSONObject(i);
            if (hit == null)
                continue;
            Book book = parser.parse(hit);
            if (book == null)
                continue;
            results.add(book);
        }
        return results;
    }
}