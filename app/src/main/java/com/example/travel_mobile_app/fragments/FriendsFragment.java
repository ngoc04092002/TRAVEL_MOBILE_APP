package com.example.travel_mobile_app.fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.travel_mobile_app.Adapter.FriendViewPageAdapter;
import com.example.travel_mobile_app.R;
import com.google.android.material.tabs.TabLayout;

public class FriendsFragment extends Fragment {

    ViewPager viewPager;
    TabLayout tabLayout;

    public FriendsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_friends, container, false);

        viewPager = view.findViewById(R.id.pager);
        viewPager.setAdapter(new FriendViewPageAdapter(getFragmentManager()));

        tabLayout = view.findViewById(R.id.friend_tabLayout);
        tabLayout.setupWithViewPager(viewPager);

        return view;
    }
}