package com.afec.bookshelf;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.TextView;

import com.afec.bookshelf.Models.Book;
import com.afec.bookshelf.MyJsonParser.SearchResultBookJsonParser;
import com.algolia.search.saas.AlgoliaException;
import com.algolia.search.saas.Client;
import com.algolia.search.saas.CompletionHandler;
import com.algolia.search.saas.Index;
import com.algolia.search.saas.Query;

import org.json.JSONObject;

import java.util.List;

public class SearchResult extends Fragment {

    List<Book> booksList;
    GridView gv;

    // Algolia search
    Client client;
    Query query;
    Index index;

    public SearchResult() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_search_res_list, container, false);

        gv = v.findViewById(R.id.book_list_grid);
        gv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //
            }
        });

        String q = null;
        String attr = "*";
        Bundle b = getArguments();
        if(b.containsKey("query"))
            q = b.getString("query");
        if(b.containsKey("search_on"))
            attr = b.getString("search_on");

        // Algolia setup
        client = new Client("BDPR8QJ6ZZ", "57b47a26838971583fcb026954731774");
        query = new Query();
        query.setAttributesToRetrieve(attr);
        query.setHitsPerPage(10);
        index = client.getIndex("bookShelf");

        //show search result
        query.setQuery(q);
        index.searchAsync(query, new CompletionHandler() {
            @Override
            public void requestCompleted(JSONObject jsonObject, AlgoliaException e) {
                Log.d("msg","requestCompleted");
                Log.d("msg",jsonObject.toString());
                SearchResultBookJsonParser parser = new SearchResultBookJsonParser();
                booksList = parser.parseResults(jsonObject);
                gv.setAdapter(new BaseAdapter() {

                    @Override
                    public int getCount() {
                        return booksList.size();
                    }

                    @Override
                    public Object getItem(int position) {
                        return booksList.get(position);
                    }

                    @Override
                    public long getItemId(int position) {
                        return position;
                    }

                    @Override
                    public View getView(int position, View convertView, ViewGroup parent) {
                        if (convertView == null) {
                            convertView = getLayoutInflater().inflate(R.layout.book_preview, parent, false);
                        }
                        TextView title_tv = (TextView) convertView.findViewById(R.id.book_title_preview);
                        title_tv.setText(booksList.get(position).getTitle());
                        TextView author_tv = (TextView) convertView.findViewById(R.id.book_autor_preview);
                        author_tv.setText(booksList.get(position).getAllAuthors());
                        return convertView;
                    }
                });
            }
        });
        return v;
    }
}
