package com.example.travel_mobile_app.fragments;

import android.os.Bundle;

import androidx.appcompat.widget.SearchView;
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
import com.example.travel_mobile_app.dto.DataChangeListener;
import com.example.travel_mobile_app.dto.FollowDTO;
import com.example.travel_mobile_app.models.UserModel;
import com.example.travel_mobile_app.services.SharedPreferencesManager;
import com.github.ybq.android.spinkit.sprite.Sprite;
import com.github.ybq.android.spinkit.style.Circle;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;


public class FriendFollowerFragment extends Fragment implements DataChangeListener {

    private RecyclerView followerRv;
    List<FollowDTO> list;

    private FirebaseFirestore db;
    FragmentFriendFollowerBinding binding;
    FollowAdapter followAdapter;

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
        View view = inflater.inflate(R.layout.fragment_friend_follower, container, false);
        binding = FragmentFriendFollowerBinding.bind(view);
        followerRv = view.findViewById(R.id.followerRv);

        UserModel user = SharedPreferencesManager.readUserInfo();

        list = new ArrayList<>();
        final boolean[] isFollow = {false};
        followAdapter = new FollowAdapter(list, getContext(), isFollow, db, requireActivity().getSupportFragmentManager());
        followerRv.setHasFixedSize(true);
        followerRv.setLayoutManager(new StaggeredGridLayoutManager(1, LinearLayoutManager.VERTICAL));
        followerRv.setAdapter(followAdapter);

        getFollowerData(followAdapter, user.getId());


        return view;
    }

    public void getFollowerData(FollowAdapter followAdapter, String userId) {
        CollectionReference users = db.collection("users");

        users.document(userId).get().addOnCompleteListener(task -> {
            if (task.isSuccessful() && task.getResult() != null) {
                showProgressBar();
                UserModel userModel = task.getResult().toObject(UserModel.class);
                getAllFollowerUser(userModel, users, followAdapter);
            }
        }).addOnFailureListener(unused -> {
            binding.badRequest.setVisibility(View.VISIBLE);
            System.out.println("CHECK_ERROR::" + unused.getMessage());
            dismissProgressBar();
        });
    }

    private void getAllFollowerUser(UserModel userModel, CollectionReference users, FollowAdapter followAdapter) {
        if (userModel == null) {
            dismissProgressBar();
            Toast.makeText(getContext(), "Người dùng không tồn tại!",
                           Toast.LENGTH_SHORT).show();
            //not found
            return;
        }

        if (userModel != null && userModel.getFollowing() != null && userModel.getFollowing().size() != 0) {
            users.whereNotIn("id", userModel.getFollowing())
                 .get()
                 .addOnCompleteListener(task -> {
                     if (task.isSuccessful()) {
                         for (QueryDocumentSnapshot document : task.getResult()) {
                             UserModel model = document.toObject(UserModel.class);
                             if (model.getFollowers() != null) {
                                 list.add(new FollowDTO(model.getId(), model.getAvatarURL(), model.getFullName(), model.getFollowers().size()));
                             }
                         }
                         binding.badRequest.setVisibility(View.GONE);
                         showNotFound(list);
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
        } else {
            users.whereNotEqualTo("id", userModel.getId())
                 .get()
                 .addOnCompleteListener(task -> {
                     if (task.isSuccessful()) {
                         for (QueryDocumentSnapshot document : task.getResult()) {
                             UserModel model = document.toObject(UserModel.class);
                             if (model.getFollowers() != null) {
                                 list.add(new FollowDTO(model.getId(), model.getAvatarURL(), model.getFullName(), model.getFollowers().size()));
                             }
                         }
                         binding.badRequest.setVisibility(View.GONE);
                         showNotFound(list);
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

    @Override
    public void onDataChange(String data) {
        if (list != null) {
            List<FollowDTO> searchList = list.stream().filter(item -> item.getUsername().contains(data)).collect(Collectors.toList());
            followAdapter.setData(searchList);
            followAdapter.notifyDataSetChanged();
            showNotFound(searchList);
        }
    }


    private void showNotFound(List<FollowDTO> checkList) {
        if (checkList.size() == 0) {
            binding.notFound.setVisibility(View.VISIBLE);
        } else {
            binding.notFound.setVisibility(View.GONE);
        }
    }
}