package com.afec.bookshelf;


import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.TabItem;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

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

    Toolbar toolbar;
    TabLayout tabLayout;
    ViewPager viewPager;
    TabPageAdapter tabPageAdapter;
    TabItem tabSearchByAll;
    TabItem tabSearchByTitle;
    TabItem tabSearchByAuthor;
    TabItem tabSearchByPublisher;
    List<Book> booksList;

    // Algolia search
    Client client;
    Query query;
    Index index;
    SearchView searchView;

    public SearchResult() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_search_result, container, false);

        /*tabSearchByAll = v.findViewById(R.id.tabSearchByAll);
        tabSearchByTitle = v.findViewById(R.id.tabSearchByTitle);
        tabSearchByAuthor = v.findViewById(R.id.tabSearchByAuthor);
        tabSearchByPublisher = v.findViewById(R.id.tabSearchByPublisher);
        tabLayout = v.findViewById(R.id.tablayout);
        viewPager = v.findViewById(R.id.SearchViewPager);*/

        // Algolia setup
        client = new Client("BDPR8QJ6ZZ", "57b47a26838971583fcb026954731774");
        query = new Query();
        query.setAttributesToRetrieve("title","authors");
        query.setHitsPerPage(10);
        index = client.getIndex("bookShelf");

        Bundle b = getArguments();

        if(b.containsKey("query")){ //show search result
            String q = b.getString("query");
            query.setQuery(q);
            index.searchAsync(query, new CompletionHandler() {
                @Override
                public void requestCompleted(JSONObject jsonObject, AlgoliaException e) {
                    Log.d("msg","requestCompleted");
                    Log.d("msg",jsonObject.toString());
                    SearchResultBookJsonParser parser = new SearchResultBookJsonParser();
                    booksList = parser.parseResults(jsonObject);
                }
            });
        }

        //tabPageAdapter = new TabPageAdapter(getActivity().getSupportFragmentManager(), tabLayout.getTabCount());
        //viewPager.setAdapter(tabPageAdapter);

        /*tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
                if(tab.getPosition()==1){
                    toolbar.setBackgroundColor(ContextCompat.getColor(
                            getActivity(), R.color.colorTabSearchAll));
                    tabLayout.setBackgroundColor(ContextCompat.getColor(
                            getActivity(), R.color.colorTabSearchAll));
                    if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP){
                        getActivity().getWindow().setStatusBarColor(ContextCompat.getColor(
                                getActivity(), R.color.colorTabSearchAll));
                    }
                }else if (tab.getPosition()==2){
                    toolbar.setBackgroundColor(ContextCompat.getColor(
                            getActivity(), R.color.colorTabSearchTitle));
                    tabLayout.setBackgroundColor(ContextCompat.getColor(
                            getActivity(), R.color.colorTabSearchTitle));
                    if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP){
                        getActivity().getWindow().setStatusBarColor(ContextCompat.getColor(
                                getActivity(), R.color.colorTabSearchTitle));
                    }
                }else if (tab.getPosition()==3){
                    toolbar.setBackgroundColor(ContextCompat.getColor(
                            getActivity(), R.color.colorTabSearchAuthor));
                    tabLayout.setBackgroundColor(ContextCompat.getColor(
                            getActivity(), R.color.colorTabSearchAuthor));
                    if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP){
                        getActivity().getWindow().setStatusBarColor(ContextCompat.getColor(
                                getActivity(), R.color.colorTabSearchAuthor));
                    }
                }else{
                    toolbar.setBackgroundColor(ContextCompat.getColor(
                            getActivity(), R.color.colorTabSearchPublisher));
                    tabLayout.setBackgroundColor(ContextCompat.getColor(
                            getActivity(), R.color.colorTabSearchPublisher));
                    if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP){
                        getActivity().getWindow().setStatusBarColor(ContextCompat.getColor(
                                getActivity(), R.color.colorTabSearchPublisher));
                    }
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });*/
        return v;
    }
}
