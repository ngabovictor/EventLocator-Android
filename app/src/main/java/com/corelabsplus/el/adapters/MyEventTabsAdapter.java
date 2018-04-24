package com.corelabsplus.el.adapters;

import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.corelabsplus.el.Fragments.EventInfoFragment;
import com.corelabsplus.el.Fragments.EventReservationFragment;

public class MyEventTabsAdapter extends FragmentStatePagerAdapter {

    int numOfTabs;

    public MyEventTabsAdapter(FragmentManager fm, int numOfTabs) {
        super(fm);
        this.numOfTabs = numOfTabs;
    }

    @Override
    public Fragment getItem(int position) {
        switch (position){
            case 0:
                return  new EventInfoFragment();

            case 1:
                return new EventReservationFragment();

            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return numOfTabs;
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        if (position == 0){
            return "INFO";
        }

        else{
            return "RESERVATIONS";
        }
    }
}
