package com.wapazock.solveit.utils;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import java.util.ArrayList;

public class ViewPagerAdapter extends FragmentPagerAdapter {

    /*
    Global variables for the viewPager adapte
     */
    private ArrayList<Fragment> mFragments = new ArrayList<>();

    public ViewPagerAdapter(@NonNull FragmentManager fm, ArrayList<Fragment> fragmentArray) {
        super(fm);
        this.mFragments = fragmentArray ;
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        return mFragments.get(position);
    }

    @Override
    public int getCount() {
        return mFragments.size();
    }
}
