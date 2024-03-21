package com.example.travel_mobile_app.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.viewmodel.CreationExtras;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.example.travel_mobile_app.Adapter.DashboardAdapter;
import com.example.travel_mobile_app.R;
import com.example.travel_mobile_app.models.DashboardModel;

import java.util.ArrayList;

public class SocialSearchPostFragment extends Fragment implements View.OnClickListener{
    private RecyclerView searchPostRv;
    private ArrayList<DashboardModel> searchPostList;
    private ImageView searchPost_btnBack;
    public SocialSearchPostFragment() {
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
        View view = inflater.inflate(R.layout.fragment_social_search_post, container, false);

        searchPostRv = view.findViewById(R.id.searchPostRv);
        searchPostList = new ArrayList<>();
        searchPostList.add(new DashboardModel(R.drawable.avatar_men, R.drawable.avatar_men, R.drawable.bookmark, "ngocvaw", "12 giờ trước", "123", "40", "50", "check in tai: ssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssss...."));
        searchPostList.add(new DashboardModel(R.drawable.avatar_men, R.drawable.avatar_men, R.drawable.bookmark, "ngocvaw", "12 giờ trước", "123", "40", "50", "check in tai: ssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssss...."));
        searchPostList.add(new DashboardModel(R.drawable.favorite, R.drawable.avatar_men, R.drawable.bookmark, "ngocvaw", "12 giờ trước", "123", "40", "50", "check in tai: ssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssss...."));
        searchPostList.add(new DashboardModel(R.drawable.favorite, R.drawable.avatar_men, R.drawable.bookmark, "ngocvaw", "12 giờ trước", "123", "40", "50", "check in tai: ssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssss...."));

        DashboardAdapter dashboardAdapter = new DashboardAdapter(searchPostList, getContext(), requireActivity().getSupportFragmentManager(), getActivity());
        searchPostRv.setHasFixedSize(true);
        searchPostRv.setLayoutManager(new StaggeredGridLayoutManager(1, LinearLayoutManager.VERTICAL));
        searchPostRv.setAdapter(dashboardAdapter);


        searchPost_btnBack = view.findViewById(R.id.searchPost_btnBack);
        searchPost_btnBack.setOnClickListener(this);

        return view;
    }

    @Override
    public void onClick(View v) {
        if(v.getId()==R.id.searchPost_btnBack){
            FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();
            fragmentManager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
        }
    }

    @NonNull
    @Override
    public CreationExtras getDefaultViewModelCreationExtras() {
        return super.getDefaultViewModelCreationExtras();
    }
}