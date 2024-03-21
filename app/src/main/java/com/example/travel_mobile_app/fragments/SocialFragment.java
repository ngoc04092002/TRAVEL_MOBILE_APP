package com.example.travel_mobile_app.fragments;

import android.os.Bundle;

import androidx.annotation.IdRes;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.viewmodel.CreationExtras;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageButton;

import com.example.travel_mobile_app.Adapter.DashboardAdapter;
import com.example.travel_mobile_app.Adapter.PostAdapter;
import com.example.travel_mobile_app.Adapter.StoryAdapter;
import com.example.travel_mobile_app.R;
import com.example.travel_mobile_app.models.DashboardModel;
import com.example.travel_mobile_app.models.PostModel;
import com.example.travel_mobile_app.models.StoryModel;
import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

import maes.tech.intentanim.CustomIntent;

public class SocialFragment extends Fragment implements View.OnClickListener {

    private RecyclerView storyRv, dashboardRv;
    private ArrayList<StoryModel> list;
    private ArrayList<PostModel> postList;
    private ImageButton btnFriends, btnAdd, btnSearch;
    private FirebaseFirestore db;
    private ShimmerFrameLayout shimmerFrameLayout, shimmerFrameLayoutStory;
    private FrameLayout createStory;

    public SocialFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        db = FirebaseFirestore.getInstance();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_social, container, false);
        shimmerFrameLayout = view.findViewById(R.id.shimmer_layout);

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
        postList = new ArrayList<>();
        PostAdapter postAdapter = new PostAdapter(postList, getContext(), requireActivity().getSupportFragmentManager(), getActivity());

        setPostListData(postAdapter);

        dashboardRv.setHasFixedSize(true);
        dashboardRv.setLayoutManager(new StaggeredGridLayoutManager(1, LinearLayoutManager.VERTICAL));
        dashboardRv.setAdapter(postAdapter);

        btnFriends = view.findViewById(R.id.friends);
        btnFriends.setOnClickListener(this);
        btnAdd = view.findViewById(R.id.addButton);
        btnAdd.setOnClickListener(this);
        btnSearch = view.findViewById(R.id.btnSearch);
        btnSearch.setOnClickListener(this);
        createStory = view.findViewById(R.id.createStory);
        createStory.setOnClickListener(this);

        return view;
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.friends) {
            replaceScreen(R.id.container, new FriendsFragment(),"social_fragment");
        } else if (v.getId() == R.id.addButton) {
            replaceScreen(R.id.container, new CreatePostFragment(),"social_fragment");
        } else if (v.getId() == R.id.btnSearch) {
            replaceScreen(R.id.container, new SocialSearchPostFragment(),null);
        }else if(v.getId()==R.id.createStory){

        }

    }

    private void replaceScreen(@IdRes int containerViewId, @NonNull Fragment fragment, String backTrackName){
        FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        fragmentTransaction.replace(containerViewId, fragment);
        // Thêm transaction vào back stack (nếu cần)
        fragmentTransaction.addToBackStack(backTrackName);
        // Commit transaction
        fragmentTransaction.commit();
    }

    private void setPostListData(PostAdapter postAdapter) {
        shimmerFrameLayout.setVisibility(View.VISIBLE);
        shimmerFrameLayout.startShimmer();
        db.collection("posts")
          .get()
          .addOnCompleteListener(task -> {
              if (task.isSuccessful()) {
                  for (QueryDocumentSnapshot document : task.getResult()) {
                      PostModel postModel = document.toObject(PostModel.class);
                      postList.add(postModel);
                  }
                  shimmerFrameLayout.showShimmer(false);
                  shimmerFrameLayout.setVisibility(View.GONE);
                  postAdapter.notifyDataSetChanged();
              } else {
                  Log.d("record", "Error getting documents: ", task.getException());
              }
          });
    }

    @NonNull
    @Override
    public CreationExtras getDefaultViewModelCreationExtras() {
        return super.getDefaultViewModelCreationExtras();
    }

}