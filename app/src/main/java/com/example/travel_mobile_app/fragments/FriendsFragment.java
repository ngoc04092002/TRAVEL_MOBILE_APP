package com.example.travel_mobile_app.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.SearchView;
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
import com.example.travel_mobile_app.models.PostModel;
import com.google.android.material.tabs.TabLayout;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;


public class FriendsFragment extends Fragment implements View.OnClickListener {

    ViewPager viewPager;
    TabLayout tabLayout;

    public FriendsFragment() {
        // Required empty public constructor
    }

    private SearchView searchView;
    FriendViewPageAdapter adapter;
    private Bundle bundle = new Bundle();


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_friends, container, false);

        List<Fragment> fragments = new ArrayList<>();
        fragments.add(new FriendFollowingFragment());
        fragments.add(new FriendFollowerFragment());

        adapter = new FriendViewPageAdapter(getChildFragmentManager(), fragments);
        viewPager = view.findViewById(R.id.viewpager);
        tabLayout = view.findViewById(R.id.friend_tabLayout);

        searchView = view.findViewById(R.id.search_friends);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                adapter.updateData(query);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
               adapter.updateData(newText);
                return true;
            }
        });

        viewPager.setAdapter(adapter);
        tabLayout.setupWithViewPager(viewPager);

        ImageView btnBack = view.findViewById(R.id.friend_btnBack);
        btnBack.setOnClickListener(this);


        if(getArguments()!=null){
            bundle = getArguments();
        }

        return view;
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.friend_btnBack) {

            FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.setCustomAnimations(
                    R.anim.pop_enter_animation,
                    R.anim.pop_exit_animation,
                    R.anim.enter_animation,
                    R.anim.exit_animation);

            Fragment fragment = new SocialFragment();
            fragment.setArguments(bundle);
            fragmentTransaction.replace(R.id.container, fragment);
            fragmentTransaction.commit();
        }
    }

    @NonNull
    @Override
    public CreationExtras getDefaultViewModelCreationExtras() {
        return super.getDefaultViewModelCreationExtras();
    }
}