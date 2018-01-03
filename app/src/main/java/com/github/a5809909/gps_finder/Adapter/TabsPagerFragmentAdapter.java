package com.github.a5809909.gps_finder.Adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.github.a5809909.gps_finder.Fragment.DatabaseFragment;

public class TabsPagerFragmentAdapter extends FragmentPagerAdapter {

    private String[] tabs;

    public TabsPagerFragmentAdapter(FragmentManager fm) {
        super(fm);
        tabs= new String[]{
                "Show Point",
                "DataBase",
                "Show Map",
                "Images",
                "Weather"
        };
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return tabs[position];
    }

    @Override
    public Fragment getItem(int position) {
        switch (position){
            case 0:
                return DatabaseFragment.getInstance();
            case 1:
                return DatabaseFragment.getInstance();
            case 2:
                return DatabaseFragment.getInstance();
            case 3:
                return DatabaseFragment.getInstance();
            case 4:
                break;
        }
        return null;
    }

    @Override
    public int getCount() {
        return tabs.length;
    }
}
