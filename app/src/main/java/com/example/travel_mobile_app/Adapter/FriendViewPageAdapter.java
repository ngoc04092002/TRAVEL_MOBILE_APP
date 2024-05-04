package com.example.travel_mobile_app.Adapter;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.example.travel_mobile_app.dto.DataChangeListener;

import java.util.List;


public class FriendViewPageAdapter extends FragmentPagerAdapter {

    private String searchedText="";
    private List<Fragment> fragments;
    public FriendViewPageAdapter(@NonNull FragmentManager fm,List<Fragment> fragments) {
        super(fm);
        this.fragments = fragments;
    }

    public FriendViewPageAdapter(@NonNull FragmentManager fm, int behavior) {
        super(fm, behavior);
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        return fragments.get(position);
    }

    @Override
    public int getCount() {
        return fragments.size();
    }

    public void updateData(String data) {
        this.searchedText = data;
        for (Fragment fragment : fragments) {
            if (fragment instanceof DataChangeListener) {
                ((DataChangeListener) fragment).onDataChange(data);
            }
        }
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
