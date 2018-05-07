package com.afec.bookshelf;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

/**
 * Created by elisa on 07/05/18.
 */

public class TabPageAdapter extends FragmentPagerAdapter{
    private int numOfTabs;

    public TabPageAdapter(FragmentManager fm, int numOfTabs) {
        super(fm);
        this.numOfTabs = numOfTabs;
    }

    @Override
    public Fragment getItem(int position) {
        switch (position){
            case 0:
                //return new SearchByAllFragment();
            case 1:
                //return new SearchByTitleFragment();
            case 2:
                //return new SearchByAuthorFragment();
            case 3:
                //return new SearchByPublisherFragment();
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return numOfTabs;
    }
}
