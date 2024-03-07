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
import com.example.travel_mobile_app.R;
import com.example.travel_mobile_app.dto.FollowDTO;

import java.util.ArrayList;


public class FriendFollowerFragment extends Fragment {

    private RecyclerView followerRv;
    ArrayList<FollowDTO> list;
    public FriendFollowerFragment() {
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
        View view= inflater.inflate(R.layout.fragment_friend_follower, container, false);

        followerRv = view.findViewById(R.id.followerRv);
        list = new ArrayList<>();
        list.add(new FollowDTO(R.drawable.avatar_men, "ngocvan", 12));
        list.add(new FollowDTO(R.drawable.avatar_men, "ngocvan2", 20));

        FollowAdapter followAdapter = new FollowAdapter(list, getContext(),false);
        followerRv.setHasFixedSize(true);
        followerRv.setLayoutManager(new StaggeredGridLayoutManager(1, LinearLayoutManager.VERTICAL));
        followerRv.setAdapter(followAdapter);
        return view;
    }
}