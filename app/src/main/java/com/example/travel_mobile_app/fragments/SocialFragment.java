package com.example.travel_mobile_app.fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.travel_mobile_app.Adapter.StoryAdapter;
import com.example.travel_mobile_app.R;
import com.example.travel_mobile_app.models.StoryModel;

import java.util.ArrayList;

public class SocialFragment extends Fragment {

    RecyclerView storyRv;
    ArrayList<StoryModel> list;

    public SocialFragment() {
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
        View view = inflater.inflate(R.layout.fragment_social, container, false);

        storyRv = view.findViewById(R.id.storyRv);
        list = new ArrayList<>();
        list.add(new StoryModel(R.drawable.add_circle, R.drawable.add_circle_fill, "hhh"));
        list.add(new StoryModel(R.drawable.circle, R.drawable.avatar_men, "Ngocvan"));
        list.add(new StoryModel(R.drawable.circle, R.drawable.avatar_men, "wdd"));
        list.add(new StoryModel(R.drawable.circle, R.drawable.avatar_men, "Ngocvan"));
        list.add(new StoryModel(R.drawable.photo_camera, R.drawable.circle, "hhh"));
        list.add(new StoryModel(R.drawable.add_circle, R.drawable.add_circle_fill, "hhh"));

        StoryAdapter storyAdapter = new StoryAdapter(list, getContext());
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        storyRv.setAdapter(storyAdapter);
        storyRv.setLayoutManager(linearLayoutManager);
        storyRv.setNestedScrollingEnabled(false);

        return view;
    }
}