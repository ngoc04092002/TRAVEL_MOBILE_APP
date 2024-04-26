package com.example.travel_mobile_app.Adapter;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.example.travel_mobile_app.fragments.FriendFollowerFragment;
import com.example.travel_mobile_app.fragments.FriendFollowingFragment;


public class FriendViewPageAdapter extends FragmentPagerAdapter {


    public FriendViewPageAdapter(@NonNull FragmentManager fm) {
        super(fm);
    }

    public FriendViewPageAdapter(@NonNull FragmentManager fm, int behavior) {
        super(fm, behavior);
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        switch (position){
            case 0:
                return new FriendFollowingFragment();
            case 1:
                return new FriendFollowerFragment();
            default:
                return new FriendFollowingFragment();
        }
    }

    @Override
    public int getCount() {
        return 2;
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        String title=null;
        if(position==0){
            title="Bạn bè";
        }else if(position==1){
            title="Gợi ý";
        }

        return title;
    }
}
