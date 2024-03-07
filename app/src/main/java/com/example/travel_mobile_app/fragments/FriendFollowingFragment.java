package com.example.travel_mobile_app.fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.travel_mobile_app.Adapter.FollowAdapter;
import com.example.travel_mobile_app.Adapter.NotificationAdapter;
import com.example.travel_mobile_app.R;
import com.example.travel_mobile_app.dto.FollowDTO;
import com.example.travel_mobile_app.models.NotificationModel;

import java.util.ArrayList;

public class FriendFollowingFragment extends Fragment {

    private RecyclerView followingRv;
    private ArrayList<FollowDTO> list;

    public FriendFollowingFragment() {
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
        View view = inflater.inflate(R.layout.fragment_friend_following, container, false);

        followingRv = view.findViewById(R.id.followingRv);
        list = new ArrayList<>();
        list.add(new FollowDTO(R.drawable.avatar_men, "ngocvan", 12));
        list.add(new FollowDTO(R.drawable.avatar_men, "ngocvan2", 20));

        FollowAdapter followAdapter = new FollowAdapter(list, getContext(),true);
        followingRv.setHasFixedSize(true);
        followingRv.setLayoutManager(new StaggeredGridLayoutManager(1, LinearLayoutManager.VERTICAL));
        followingRv.setAdapter(followAdapter);
        return view;
    }
}