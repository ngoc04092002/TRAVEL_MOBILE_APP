package com.example.travel_mobile_app.fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.travel_mobile_app.Adapter.FollowAdapter;
import com.example.travel_mobile_app.R;
import com.example.travel_mobile_app.databinding.FragmentFriendFollowingBinding;
import com.example.travel_mobile_app.dto.FollowDTO;
import com.example.travel_mobile_app.models.UserModel;
import com.github.ybq.android.spinkit.sprite.Sprite;
import com.github.ybq.android.spinkit.style.Circle;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;

public class FriendFollowingFragment extends Fragment {

    private RecyclerView followingRv;
    private ArrayList<FollowDTO> list;
    private FirebaseFirestore db;

    FragmentFriendFollowingBinding binding;

    public FriendFollowingFragment() {
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
        View view = inflater.inflate(R.layout.fragment_friend_following, container, false);
        binding = FragmentFriendFollowingBinding.bind(view);
        followingRv = view.findViewById(R.id.followingRv);

        String userId = "qbJW6GgDkqgv6H5tvCPfLty2Bto2";

        list = new ArrayList<>();
        FollowAdapter followAdapter = new FollowAdapter(list, getContext(), true);
        followingRv.setHasFixedSize(true);
        followingRv.setLayoutManager(new StaggeredGridLayoutManager(1, LinearLayoutManager.VERTICAL));
        followingRv.setAdapter(followAdapter);
        getFollowingData(followAdapter, userId);

        return view;
    }


    public void getFollowingData(FollowAdapter followAdapter, String userId) {
        showProgressBar();
        CollectionReference users = db.collection("users");

        users.document(userId).get().addOnSuccessListener(task -> {
            UserModel userModel = task.toObject(UserModel.class);
            getAllFollowingUser(userModel, users, followAdapter);
            binding.badRequest.setVisibility(View.GONE);
        }).addOnFailureListener(unused -> {
            binding.badRequest.setVisibility(View.VISIBLE);
            System.out.println("CHECK_ERROR::" + unused.getMessage());
            dismissProgressBar();
        });
    }

    private void getAllFollowingUser(UserModel userModel, CollectionReference users, FollowAdapter followAdapter) {
        if (userModel != null && userModel.getFollowing() != null) {
            users.whereIn("following", userModel.getFollowing())
                 .get()
                 .addOnCompleteListener(task -> {
                     if (task.isSuccessful()) {
                         for (QueryDocumentSnapshot document : task.getResult()) {
                             UserModel model = document.toObject(UserModel.class);
                             if (model.getFollowers() != null) {
                                 list.add(new FollowDTO(model.getAvatarURL(), model.getUsername(), model.getFollowers().size()));
                             }
                         }
                         binding.badRequest.setVisibility(View.GONE);
                     } else {
                         binding.badRequest.setVisibility(View.VISIBLE);
                         Log.d("ERROR::", "Error getting documents: ", task.getException());
                     }
                     dismissProgressBar();
                     followAdapter.notifyDataSetChanged();
                 }).addOnFailureListener(
                         unused ->
                         {
                             binding.badRequest.setVisibility(View.VISIBLE);
                             dismissProgressBar();
                         });

        }

    }

    private void showProgressBar() {
        binding.spinKit.setVisibility(View.VISIBLE);
        Sprite circle = new Circle();
        binding.spinKit.setIndeterminateDrawable(circle);
    }

    private void dismissProgressBar() {
        binding.spinKit.setVisibility(View.GONE);
    }
}