package com.example.travel_mobile_app.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.viewmodel.CreationExtras;
import androidx.viewpager.widget.ViewPager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.example.travel_mobile_app.Adapter.FriendViewPageAdapter;
import com.example.travel_mobile_app.R;
import com.google.android.material.tabs.TabLayout;

public class FriendsFragment extends Fragment implements View.OnClickListener{

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
        viewPager.setAdapter(new FriendViewPageAdapter(getChildFragmentManager()));

        tabLayout = view.findViewById(R.id.friend_tabLayout);
        tabLayout.setupWithViewPager(viewPager);

        ImageView btnBack = view.findViewById(R.id.friend_btnBack);
        btnBack.setOnClickListener(this);
        return view;
    }

    @Override
    public void onClick(View v) {
        if(v.getId()==R.id.friend_btnBack){
            FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();
            fragmentManager.popBackStack("social_fragment", FragmentManager.POP_BACK_STACK_INCLUSIVE);
        }
    }

    @NonNull
    @Override
    public CreationExtras getDefaultViewModelCreationExtras() {
        return super.getDefaultViewModelCreationExtras();
    }
}