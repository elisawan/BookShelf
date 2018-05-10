package com.afec.bookshelf;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.afec.bookshelf.Models.Book;
import com.afec.bookshelf.MyJsonParser.SearchResultBookJsonParser;
import com.algolia.search.saas.AlgoliaException;
import com.algolia.search.saas.Client;
import com.algolia.search.saas.CompletionHandler;
import com.algolia.search.saas.Index;
import com.algolia.search.saas.Query;
import com.squareup.picasso.Picasso;

import org.json.JSONObject;

import java.util.List;

public class SearchResult extends Fragment {

    List<Book> booksList;
    GridView gv;

    // Algolia search
    Client client;
    Query query;
    Index index;
    SearchResultBookJsonParser parser = new SearchResultBookJsonParser();
    TextView tv;
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

        tv = (TextView) v.findViewById(R.id.search_msg);

        String q = null;
        Bundle b = getArguments();
        if(b.containsKey("query")) {
            q = b.getString("query");
            tv.setText(q);
        }


        // Algolia setup
        client = new Client("BDPR8QJ6ZZ", "57b47a26838971583fcb026954731774");
        query = new Query();
        if(b.containsKey("search_on")) {
            String attr = b.getString("search_on");
            query.setRestrictSearchableAttributes(attr);
        }
        query.setAttributesToRetrieve("title","authors","thumbnailUrl","publisher");
        query.setHitsPerPage(10);
        index = client.getIndex("bookShelf");

        //show search result
        query.setQuery(q);
        Log.d("query",query.toString());
        index.searchAsync(query, new CompletionHandler() {
            @Override
            public void requestCompleted(JSONObject jsonObject, AlgoliaException e) {
                Log.d("msg","requestCompleted");
                //Log.d("msg",jsonObject.toString());
                booksList = parser.parseResults(jsonObject);
                if(booksList == null){
                    tv = (TextView) getActivity().findViewById(R.id.search_msg);
                    tv.setText("Nothing found");
                    return;
                }
                gv.setAdapter(bookGridAdapter);
            }
        });

        gv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Fragment newFragment = new ShowBook();
                Bundle b = new Bundle();
                b.putString("isbn", booksList.get(position).getIsbn());
                newFragment.setArguments(b);
                FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
                // Replace whatever is in the fragment_container view with this fragment,
                // and add the transaction to the back stack
                transaction.replace(R.id.content_frame, newFragment);
                transaction.addToBackStack(null);
                // Commit the transaction
                transaction.commit();
            }
        });
        return v;
    }

    private BaseAdapter bookGridAdapter = new BaseAdapter() {
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

            Log.d("title",booksList.get(position).getTitle());
            title_tv.setText(booksList.get(position).getTitle());
            TextView author_tv = (TextView) convertView.findViewById(R.id.book_autor_preview);
            author_tv.setText(booksList.get(position).getAllAuthors());

            // add book image
            if(booksList.get(position).getThumbnailUrl() != null){
                ImageView iv = (ImageView) convertView.findViewById(R.id.book_image_preview);
                Picasso.with(getContext()).load(booksList.get(position).getThumbnailUrl()).placeholder(R.drawable.book_image_placeholder).into(iv);
            }
            return convertView;
        }
    };
}
