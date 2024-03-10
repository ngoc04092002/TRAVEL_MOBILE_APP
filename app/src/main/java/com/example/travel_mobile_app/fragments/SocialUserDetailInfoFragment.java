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
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;

import com.example.travel_mobile_app.Adapter.DashboardAdapter;
import com.example.travel_mobile_app.Adapter.SocialUserDetailInfoAdapter;
import com.example.travel_mobile_app.R;
import com.example.travel_mobile_app.models.DashboardModel;

import java.util.ArrayList;

public class SocialUserDetailInfoFragment extends Fragment implements View.OnClickListener {

    private Integer useId;

    public SocialUserDetailInfoFragment() {
        // Required empty public constructor
    }

    public SocialUserDetailInfoFragment(Integer useId) {
        this.useId = useId;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    private ImageView btnFriendBack;
    private ArrayList<DashboardModel> dashboardList;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view= inflater.inflate(R.layout.fragment_social_user_detail_info, container, false);


        dashboardList = new ArrayList<>();
        dashboardList.add(new DashboardModel(R.drawable.avatar_men, R.drawable.avatar_men, R.drawable.bookmark, "ngocvaw", "12 giờ trước", "123", "40", "50", "check in tai: ssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssss...."));
        dashboardList.add(new DashboardModel(R.drawable.avatar_men, R.drawable.avatar_men, R.drawable.bookmark, "ngocvaw", "12 giờ trước", "123", "40", "50", "check in tai: ssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssss...."));
        dashboardList.add(new DashboardModel(R.drawable.favorite, R.drawable.avatar_men, R.drawable.bookmark, "ngocvaw", "12 giờ trước", "123", "40", "50", "check in tai: ssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssss...."));
        dashboardList.add(new DashboardModel(R.drawable.favorite, R.drawable.avatar_men, R.drawable.bookmark, "ngocvaw", "12 giờ trước", "123", "40", "50", "check in tai: ssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssss...."));

        LinearLayout userInfos = view.findViewById(R.id.uInfo_container);

        for(DashboardModel model:dashboardList){
            LayoutInflater post = LayoutInflater.from(getContext());
            View subLayout = post.inflate(R.layout.dashboard_rv, null);
            SocialUserDetailInfoAdapter socialUserDetailInfoAdapter = new SocialUserDetailInfoAdapter(model,getContext());
            View viewSubLayout = socialUserDetailInfoAdapter.onCreateView(subLayout,model);

            userInfos.addView(viewSubLayout);
        }


        //handle click
        btnFriendBack = view.findViewById(R.id.uInfo_btnBack);
        btnFriendBack.setOnClickListener(this);

        return view;
    }

    @Override
    public void onClick(View v) {
        if(v.getId()==R.id.uInfo_btnBack){
            FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();
            fragmentManager.popBackStack("userDetailInfo_fragment", FragmentManager.POP_BACK_STACK_INCLUSIVE);
        }
    }

    @NonNull
    @Override
    public CreationExtras getDefaultViewModelCreationExtras() {
        return super.getDefaultViewModelCreationExtras();
    }
}