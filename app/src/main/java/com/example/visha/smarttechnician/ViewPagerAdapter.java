package com.example.visha.smarttechnician;

import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by visha on 26 Jan 2018.
 */

public class ViewPagerAdapter extends FragmentPagerAdapter {

    private List<Fragment> fragmentList=new ArrayList<>();
    private List<String> fragmentTitleList=new ArrayList<>();

    ViewPagerAdapter(FragmentManager fragmentManager){
        super(fragmentManager);
    }

    @Override
    public Fragment getItem(int position) {
        return fragmentList.get(position);
    }

    public void addFragment(Fragment fragment,String title){

        fragmentList.add(fragment);
        fragmentTitleList.add(title);

    }

    @Override
    public int getCount() {
        return fragmentList.size();
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        return fragmentTitleList.get(position);
    }
}
