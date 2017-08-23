package com.exequiel.redditor.ui.fragment.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.exequiel.redditor.ui.fragment.SubRedditPostListFragment;

public class SubrreditOrderPagerAdapter extends FragmentPagerAdapter {
    public SubrreditOrderPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        // getItem is called to instantiate the fragment for the given page.
        // Return a PlaceholderFragment (defined as a static inner class below).
        String order = getPageTitle(position).toString().toLowerCase();
        return SubRedditPostListFragment.newInstance(order);
    }

    @Override
    public int getCount() {
        // Show 3 total pages.
        return 4;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        switch (position) {
            case 0:
                return "HOT";
            case 1:
                return "NEW";
            case 2:
                return "TOP";
            case 3:
                return "CONTROVERSIAL";
        }
        return null;
    }

    public interface SubRedditOrder {
        int HOT = 0;
        int NEW = 1;
        int TOP = 2;
        int CONTROVERSIAL = 3;
    }
}