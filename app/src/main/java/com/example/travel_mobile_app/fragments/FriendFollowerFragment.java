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
import android.widget.Toast;

import com.example.travel_mobile_app.Adapter.FollowAdapter;
import com.example.travel_mobile_app.R;
import com.example.travel_mobile_app.databinding.FragmentFriendFollowerBinding;
import com.example.travel_mobile_app.databinding.FragmentFriendFollowingBinding;
import com.example.travel_mobile_app.dto.FollowDTO;
import com.example.travel_mobile_app.models.UserModel;
import com.github.ybq.android.spinkit.sprite.Sprite;
import com.github.ybq.android.spinkit.style.Circle;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;


public class FriendFollowerFragment extends Fragment {

    private RecyclerView followerRv;
    ArrayList<FollowDTO> list;

    private FirebaseFirestore db;
    FragmentFriendFollowerBinding binding;
    public FriendFollowerFragment() {
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
        View view= inflater.inflate(R.layout.fragment_friend_follower, container, false);
        binding = FragmentFriendFollowerBinding.bind(view);
        followerRv = view.findViewById(R.id.followerRv);

        String userId = "qbJW6GgDkqgv6H5tvCPfLty2Bto2";

        list = new ArrayList<>();
        FollowAdapter followAdapter = new FollowAdapter(list, getContext(),false);
        followerRv.setHasFixedSize(true);
        followerRv.setLayoutManager(new StaggeredGridLayoutManager(1, LinearLayoutManager.VERTICAL));
        followerRv.setAdapter(followAdapter);

        getFollowerData(followAdapter, userId);
        return view;
    }

    public void getFollowerData(FollowAdapter followAdapter, String userId) {
        CollectionReference users = db.collection("users");

        users.document(userId).get().addOnCompleteListener(task -> {
            if(task.isSuccessful()&& task.getResult()!=null){
                showProgressBar();
                UserModel userModel = task.getResult().toObject(UserModel.class);
                getAllFollowerUser(userModel, users, followAdapter);
            }
        }).addOnFailureListener(unused -> {
            binding.badRequest.setVisibility(View.VISIBLE);
            System.out.println("CHECK_ERROR::"+unused.getMessage());
            dismissProgressBar();
        });
    }

    private void getAllFollowerUser(UserModel userModel, CollectionReference users, FollowAdapter followAdapter) {
        if(userModel == null){
            dismissProgressBar();
            Toast.makeText(getContext(), "Người dùng không tồn tại!",
                           Toast.LENGTH_SHORT).show();
            //not found
            return;
        }

        if (userModel != null && userModel.getFollowing() != null) {
            users.whereNotIn("id", userModel.getFollowing())
                 .get()
                 .addOnCompleteListener(task -> {
                     if (task.isSuccessful()) {
                         for (QueryDocumentSnapshot document : task.getResult()) {
                             UserModel model = document.toObject(UserModel.class);
                             if (model.getFollowers() != null) {
                                 list.add(new FollowDTO(model.getAvatarURL(), model.getFullName(), model.getFollowers().size()));
                             }
                         }
                         binding.badRequest.setVisibility(View.GONE);
                     } else {
                         binding.badRequest.setVisibility(View.VISIBLE);
                         Log.d("ERROR::", "Error getting documents: ", task.getException());
                     }
                     followAdapter.notifyDataSetChanged();
                     dismissProgressBar();
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