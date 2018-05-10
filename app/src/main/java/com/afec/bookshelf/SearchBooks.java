package com.afec.bookshelf;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v7.widget.Toolbar;

import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

public class SearchBooks extends Fragment {

    private static final String TAG = "SearchBooks activity";
    private SectionsPagerAdapter mSectionsPagerAdapter;
    private ViewPager mViewPager;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.activity_search_books, container, false);

        Bundle b = getArguments();
        String q = b.getString("query");

        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getActivity().getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) v.findViewById(R.id.container);
        setupViewPager(mViewPager, q);

        TabLayout tabLayout = (TabLayout) v.findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);

        mViewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.addOnTabSelectedListener(new TabLayout.ViewPagerOnTabSelectedListener(mViewPager));
        return v;
    }

    private void setupViewPager(ViewPager mViewPager, String query){
        SectionsPagerAdapter adapter = new SectionsPagerAdapter(getActivity().getSupportFragmentManager());

        Bundle b_all = new Bundle();
        b_all.putString("query", query);
        Fragment searchAll = new SearchResult();
        searchAll.setArguments(b_all);
        adapter.addFragment(searchAll,"All");

        Bundle b_title = new Bundle();
        b_title.putString("query", query);
        b_title.putString("search_on","title");
        Fragment searchTitle = new SearchResult();
        searchTitle.setArguments(b_title);
        adapter.addFragment(searchTitle,"Title");

        Bundle b_author = new Bundle();
        b_author.putString("query", query);
        b_author.putString("search_on","authors, allAuthors, author");
        Fragment searchAuthor = new SearchResult();
        searchAuthor.setArguments(b_author);
        adapter.addFragment(searchAuthor,"Author");

        Bundle b_pub = new Bundle();
        b_pub.putString("query", query);
        b_pub.putString("search_on","publisher");
        Fragment searchPub = new SearchResult();
        searchPub.setArguments(b_pub);
        adapter.addFragment(searchPub,"Publisher");

        mViewPager.setAdapter(adapter);
    }

    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        @Override
        public String getPageTitle(int position){
            return mFragmentTitleList.get(position);
        }

        public void addFragment(Fragment fragment, String title){
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }
    }
}
