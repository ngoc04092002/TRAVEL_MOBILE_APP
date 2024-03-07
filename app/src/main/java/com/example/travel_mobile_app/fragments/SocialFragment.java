package com.example.travel_mobile_app.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.viewmodel.CreationExtras;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import com.example.travel_mobile_app.Adapter.DashboardAdapter;
import com.example.travel_mobile_app.Adapter.StoryAdapter;
import com.example.travel_mobile_app.R;
import com.example.travel_mobile_app.models.DashboardModel;
import com.example.travel_mobile_app.models.StoryModel;

import java.util.ArrayList;

import maes.tech.intentanim.CustomIntent;

public class SocialFragment extends Fragment implements View.OnClickListener {

    private RecyclerView storyRv, dashboardRv;
    private ArrayList<StoryModel> list;
    private ArrayList<DashboardModel> dashboardList;

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
        list.add(new StoryModel(R.drawable.photo_camera, R.drawable.avatar_men, "wdd"));
        list.add(new StoryModel(R.drawable.bell_fill, R.drawable.avatar_men, "Ngocvan"));
        list.add(new StoryModel(R.drawable.photo_camera, R.drawable.circle, "hhh"));
        list.add(new StoryModel(R.drawable.comment, R.drawable.add_circle_fill, "hhh"));

        StoryAdapter storyAdapter = new StoryAdapter(list, getContext());
        storyRv.setHasFixedSize(true);
        storyRv.setLayoutManager(new StaggeredGridLayoutManager(1, LinearLayoutManager.HORIZONTAL));
        storyRv.setAdapter(storyAdapter);


        dashboardRv = view.findViewById(R.id.dashboardRv);
        dashboardList = new ArrayList<>();
        dashboardList.add(new DashboardModel(R.drawable.avatar_men, R.drawable.avatar_men, R.drawable.bookmark, "ngocvaw", "12 giờ trước", "123", "40", "50", "check in tai: ssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssss...."));
        dashboardList.add(new DashboardModel(R.drawable.avatar_men, R.drawable.avatar_men, R.drawable.bookmark, "ngocvaw", "12 giờ trước", "123", "40", "50", "check in tai: ssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssss...."));
        dashboardList.add(new DashboardModel(R.drawable.favorite, R.drawable.avatar_men, R.drawable.bookmark, "ngocvaw", "12 giờ trước", "123", "40", "50", "check in tai: ssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssss...."));
        dashboardList.add(new DashboardModel(R.drawable.favorite, R.drawable.avatar_men, R.drawable.bookmark, "ngocvaw", "12 giờ trước", "123", "40", "50", "check in tai: ssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssss...."));

        DashboardAdapter dashboardAdapter = new DashboardAdapter(dashboardList, getContext());
        dashboardRv.setHasFixedSize(true);
        dashboardRv.setLayoutManager(new StaggeredGridLayoutManager(1, LinearLayoutManager.VERTICAL));
        dashboardRv.setAdapter(dashboardAdapter);

        ImageButton btnFriends = view.findViewById(R.id.friends);
        btnFriends.setOnClickListener(this);
        ImageButton btnAdd = view.findViewById(R.id.addButton);
        btnAdd.setOnClickListener(this);

        return view;
    }

    @Override
    public void onClick(View v) {
        FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        if(v.getId()==R.id.friends){
            fragmentTransaction.replace(R.id.container, new FriendsFragment());
        }else if(v.getId()==R.id.addButton){
            fragmentTransaction.replace(R.id.container, new CreatePostFragment());

        }
        // Thêm transaction vào back stack (nếu cần)
        fragmentTransaction.addToBackStack("social_fragment");

        // Commit transaction
        fragmentTransaction.commit();
    }

    @NonNull
    @Override
    public CreationExtras getDefaultViewModelCreationExtras() {
        return super.getDefaultViewModelCreationExtras();
    }
}