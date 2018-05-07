package com.afec.bookshelf;


import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.TabItem;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


public class SearchResult extends Fragment {

    Toolbar toolbar;
    TabLayout tabLayout;
    ViewPager viewPager;
    TabPageAdapter tabPageAdapter;
    TabItem tabSearchByAll;
    TabItem tabSearchByTitle;
    TabItem tabSearchByAuthor;
    TabItem tabSearchByPublisher;

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

        toolbar = v.findViewById(R.id.toolbar);
        toolbar.setTitle(getResources().getString(R.string.app_name));

        tabSearchByAll = v.findViewById(R.id.tabSearchByAll);
        tabSearchByTitle = v.findViewById(R.id.tabSearchByTitle);
        tabSearchByAuthor = v.findViewById(R.id.tabSearchByAuthor);
        tabSearchByPublisher = v.findViewById(R.id.tabSearchByPublisher);
        tabLayout = v.findViewById(R.id.tablayout);
        viewPager = v.findViewById(R.id.SearchViewPager);

        tabPageAdapter = new TabPageAdapter(getActivity().getSupportFragmentManager(), tabLayout.getTabCount());
        viewPager.setAdapter(tabPageAdapter);

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
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
        });
        return v;
    }
}
