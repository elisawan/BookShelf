package com.afec.bookshelf;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.afec.bookshelf.Models.Review;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Andrea on 29/05/2018.
 */

public class ReviewPage extends Fragment {

    FirebaseDatabase database;
    String uid;

    private static final String TAG = "ReviewPage activity";
    private SectionsPagerAdapter mSectionsPagerAdapter;
    private ViewPager mViewPager;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.activity_reviews, container, false);

        Bundle b = getArguments();
        if(b!=null){
            if(b.containsKey("user")){
                uid = b.getString("user");
            }else{
                uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
            }
        }

        database = FirebaseDatabase.getInstance();

        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getActivity().getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) v.findViewById(R.id.rev_container);
        setupViewPager(mViewPager);

        TabLayout tabLayout = (TabLayout) v.findViewById(R.id.rev_tabs);
        tabLayout.setupWithViewPager(mViewPager);

        mViewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.addOnTabSelectedListener(new TabLayout.ViewPagerOnTabSelectedListener(mViewPager));
        return v;
    }

    private void setupViewPager(ViewPager mViewPager){
        SectionsPagerAdapter adapter = new SectionsPagerAdapter(getActivity().getSupportFragmentManager());

        Bundle b_pending = new Bundle();
        b_pending.putInt("query",Review.STATUS_PENDING);
        Fragment pendingReviews = new ReviewList();
        pendingReviews.setArguments(b_pending);
        adapter.addFragment(pendingReviews,getResources().getString(R.string.pending));

        Bundle b_written = new Bundle();
        b_written.putInt("query",Review.STATUS_WRITTEN);
        Fragment writtenReviews = new ReviewList();
        writtenReviews.setArguments(b_written);
        adapter.addFragment(writtenReviews,getResources().getString(R.string.written));

        Bundle b_received = new Bundle();
        b_received.putInt("query",Review.STATUS_RECEIVED);
        Fragment receivedReviews = new ReviewList();
        receivedReviews.setArguments(b_received);
        adapter.addFragment(receivedReviews,getResources().getString(R.string.received));

        mViewPager.setAdapter(adapter);
    }

    public static class SectionsPagerAdapter extends FragmentPagerAdapter {

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