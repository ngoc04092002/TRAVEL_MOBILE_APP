package com.example.travel_mobile_app.Adapter;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.travel_mobile_app.R;
import com.example.travel_mobile_app.models.CommentModel;
import com.example.travel_mobile_app.models.PostModel;
import com.example.travel_mobile_app.models.UserModel;
import com.example.travel_mobile_app.utils.CustomDateTime;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.auth.User;

import java.util.ArrayList;
import java.util.Arrays;

public class SocialUserDetailInfoAdapter {

    PostModel model;
    Context context;
    FirebaseFirestore db;

    public SocialUserDetailInfoAdapter(PostModel model, Context context, FirebaseFirestore db) {
        this.model = model;
        this.context = context;
        this.db = db;
    }

    ImageView postImage, postimg;
    TextView name, about, des;

    MaterialButton like, comment, share;

    PostModel postModel;

    public View onCreateView(@NonNull View itemView, PostModel postModel) {
        this.postModel = postModel;
        postimg = itemView.findViewById(R.id.postimg);
        postImage = itemView.findViewById(R.id.profile_image);
        name = itemView.findViewById(R.id.username);
        about = itemView.findViewById(R.id.timestamp);
        like = itemView.findViewById(R.id.like);
        comment = itemView.findViewById(R.id.comment);
        share = itemView.findViewById(R.id.share);
        des = itemView.findViewById(R.id.des);

        CollectionReference users = db.collection("users");
        users.document(postModel.getPostedBy())
             .get()
             .addOnCompleteListener(taskUser -> {
                 if (taskUser.isSuccessful() && taskUser.getResult() != null) {
                     UserModel userModel = taskUser.getResult().toObject(UserModel.class);
                     name.setText(userModel.getFullName());
                     if (userModel.getAvatarURL() != null) {
                         Glide.with(context)
                              .load(Uri.parse(userModel.getAvatarURL()))
                              .centerCrop()
                              .placeholder(R.drawable.avatar_men)
                              .into(postimg);
                     }
                 }
             });

        about.setText(CustomDateTime.formatDate(postModel.getPostedAt()));

        if (postModel.getLikes() != null) {
            like.setText(postModel.getLikes().size());
        }
        if (postModel.getComments() != null) {
            comment.setText(postModel.getComments().size());
        }

        CollectionReference posts = db.collection("posts");
        posts.whereEqualTo("share", true)
             .whereNotEqualTo("originPostId", postModel.getOriginPostId())
             .get()
             .addOnCompleteListener(task -> {
                 if (task.isSuccessful() && task.getResult() != null) {
                     share.setText(task.getResult().size());
                 }
             });
        des.setText(postModel.getPostDescription());

        if (postModel.getPostImage() != null) {
            Glide.with(context)
                 .load(Uri.parse(postModel.getPostImage()))
                 .centerCrop()
                 .placeholder(R.drawable.avatar_men)
                 .into(postimg);
        }


        comment.setOnClickListener(v -> {
            showBottomDialog();
        });

        return itemView;
    }

    private void showBottomDialog() {
        final BottomSheetDialog dialog = new BottomSheetDialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.bottomsheet_layout);

        ImageButton bottomsheet_back = dialog.findViewById(R.id.bottomsheet_back);

        bottomsheet_back.setOnClickListener(v -> {
            dialog.dismiss();
        });

        ArrayList<CommentModel> comments;
        RecyclerView commentsRv = dialog.findViewById(R.id.commentsRv);

        comments = new ArrayList<>();
        comments.add(new CommentModel("1", R.drawable.avatar_men, "ngocvan", "Cảnh này đẹp quaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaas", 1710946229849L));
        comments.add(new CommentModel("2", R.drawable.avatar_men, "ngocvan", "Cảnh này đẹp quaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaas", 1710946229849L));
        comments.add(new CommentModel("3", R.drawable.avatar_men, "ngocvan", "Cảnh này đẹp quaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaas", 1710946229849L));

//        CommentAdapter commentAdapter = new CommentAdapter(comments, dialog.getContext());
//        commentsRv.setHasFixedSize(true);
//        commentsRv.setLayoutManager(new StaggeredGridLayoutManager(1, LinearLayoutManager.VERTICAL));
//        commentsRv.setAdapter(commentAdapter);

        dialog.show();
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().setGravity(Gravity.BOTTOM);

    }

}
