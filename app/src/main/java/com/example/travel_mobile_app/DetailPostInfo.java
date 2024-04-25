package com.example.travel_mobile_app;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;

import com.example.travel_mobile_app.Adapter.PostAdapter;
import com.example.travel_mobile_app.Adapter.SocialUserDetailInfoAdapter;
import com.example.travel_mobile_app.databinding.ActivityDetailPostInfoBinding;
import com.example.travel_mobile_app.models.PostModel;
import com.github.ybq.android.spinkit.sprite.Sprite;
import com.github.ybq.android.spinkit.style.Circle;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.Filter;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class DetailPostInfo extends AppCompatActivity {

    ActivityDetailPostInfoBinding binding;
    private FirebaseFirestore db;
    private RecyclerView dashboardRv;
    private List<PostModel> postList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityDetailPostInfoBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        db = FirebaseFirestore.getInstance();

        //handle click
        binding.detailPostBtnBack.setOnClickListener(v -> {
            Intent intent = new Intent(this, MainActivity.class);

            String type = getIntent().getStringExtra("detail_post_activity");
            if (type != null && type.equals("notification")) {
                intent.putExtra("previous_fragment", "notification");
            } else {
                intent.putExtra("previous_fragment", "social_screen");
            }

            startActivity(intent);
            overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
        });


        String postId = getIntent().getStringExtra("postId");

        showProgressBar();
        postList = new ArrayList<>();
        dashboardRv = binding.dashboardRv;
        PostAdapter postAdapter = new PostAdapter(postList, this, getSupportFragmentManager(), this, db);
        dashboardRv.setHasFixedSize(true);
        dashboardRv.setLayoutManager(new StaggeredGridLayoutManager(1, LinearLayoutManager.VERTICAL));
        setPostData(postAdapter, postId);

        dashboardRv.setAdapter(postAdapter);

    }


    private void setPostData(PostAdapter postAdapter, String postId) {

        CollectionReference posts = db.collection("posts");
        posts.document(postId)
             .get()
             .addOnCompleteListener(task ->
                                    {
                                        if (task.isSuccessful()) {
                                            PostModel model = task.getResult().toObject(PostModel.class);
                                            postList.add(model);

                                            binding.badRequest.setVisibility(View.GONE);
                                            postAdapter.notifyDataSetChanged();
                                        } else {
                                            binding.badRequest.setVisibility(View.VISIBLE);
                                        }
                                        dismissProgressBar();
                                    }).
             addOnFailureListener(e ->

                                  {
                                      binding.badRequest.setVisibility(View.VISIBLE);
                                      Log.e("ERROR-USER-DETAIL-INFOR", e.getMessage());
                                      dismissProgressBar();
                                  });
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