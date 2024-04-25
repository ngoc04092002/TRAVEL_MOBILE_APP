package com.example.travel_mobile_app.fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.travel_mobile_app.Adapter.DetailFollowAdapter;
import com.example.travel_mobile_app.R;
import com.example.travel_mobile_app.models.UserModel;
import com.github.ybq.android.spinkit.SpinKitView;
import com.github.ybq.android.spinkit.sprite.Sprite;
import com.github.ybq.android.spinkit.style.Circle;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class DetailFollowFragment extends Fragment {

    private List<String> follow;
    private String title;
    private SpinKitView spinKit;
    private TextView badRequest;
    private TextView txtTitle;
    private List<UserModel> userModels;
    private RecyclerView detailFollowRv;
    private FirebaseFirestore db;

    public DetailFollowFragment(String title, List<String> follow) {
        this.title = title;
        this.follow = follow;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        db = FirebaseFirestore.getInstance();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_detail_follow, container, false);

        ImageView btnBack = view.findViewById(R.id.detailFollow_btnBack);
        txtTitle = view.findViewById(R.id.title);
        badRequest = view.findViewById(R.id.bad_request);
        spinKit = view.findViewById(R.id.spin_kit);
        detailFollowRv = view.findViewById(R.id.detailFollowRv);

        txtTitle.setText(title);

        userModels = new ArrayList<>();
        DetailFollowAdapter detailFollowAdapter = new DetailFollowAdapter(userModels, getContext());
        detailFollowRv.setHasFixedSize(true);
        detailFollowRv.setLayoutManager(new StaggeredGridLayoutManager(1, LinearLayoutManager.VERTICAL));
        detailFollowRv.setAdapter(detailFollowAdapter);

        fetchFollowData(detailFollowAdapter);

        btnBack.setOnClickListener(v -> {
            FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();
            fragmentManager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
        });

        return view;
    }


    private void fetchFollowData(DetailFollowAdapter detailFollowAdapter) {
        showProgressBar();
        CollectionReference users = db.collection("users");
        if (follow != null && follow.size() != 0) {
            users.whereIn("id", follow)
                 .get()
                 .addOnCompleteListener(task -> {
                     if (task.isSuccessful()) {
                         for (QueryDocumentSnapshot document : task.getResult()) {
                             UserModel model = document.toObject(UserModel.class);
                             userModels.add(model);
                         }

                         detailFollowAdapter.notifyDataSetChanged();
                         badRequest.setVisibility(View.GONE);
                     } else {
                         badRequest.setVisibility(View.VISIBLE);
                         Log.d("ERROR::", "Error getting documents: ", task.getException());
                     }

                     dismissProgressBar();
                 }).addOnFailureListener(
                         unused ->
                         {
                             badRequest.setVisibility(View.VISIBLE);
                             dismissProgressBar();
                         });
        } else {
            dismissProgressBar();
        }
    }


    private void showProgressBar() {
        spinKit.setVisibility(View.VISIBLE);
        Sprite circle = new Circle();
        spinKit.setIndeterminateDrawable(circle);
    }

    private void dismissProgressBar() {
        spinKit.setVisibility(View.GONE);
    }
}