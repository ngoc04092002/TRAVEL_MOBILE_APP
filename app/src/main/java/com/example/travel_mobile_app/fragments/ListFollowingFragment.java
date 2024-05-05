package com.example.travel_mobile_app.fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.travel_mobile_app.R;


public class ListFollowingFragment extends Fragment {


    public ListFollowingFragment() {
        // Required empty public constructor
    }


    public static ListFollowingFragment newInstance(String param1, String param2) {
        ListFollowingFragment fragment = new ListFollowingFragment();

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_list_following, container, false);
    }
}