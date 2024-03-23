package com.example.travel_mobile_app.Adapter;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.example.travel_mobile_app.R;
import com.example.travel_mobile_app.fragments.SocialUserDetailInfoFragment;
import com.example.travel_mobile_app.models.CommentModel;
import com.example.travel_mobile_app.models.DashboardModel;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.button.MaterialButton;

import java.util.ArrayList;

public class SocialUserDetailInfoAdapter {

    DashboardModel model;
    Context context;

    public SocialUserDetailInfoAdapter(DashboardModel model, Context context) {
        this.model = model;
        this.context = context;
    }

    ImageView profile, postImage, save;
    TextView name, about, des;

    MaterialButton like, comment, share;

    DashboardModel dashboardModel;

    public View onCreateView(@NonNull View itemView, DashboardModel dashboardModel) {
        this.dashboardModel = dashboardModel;
        profile = itemView.findViewById(R.id.profile_image);
        postImage = itemView.findViewById(R.id.postimg);
        name = itemView.findViewById(R.id.username);
        about = itemView.findViewById(R.id.followers);
        like = itemView.findViewById(R.id.like);
        comment = itemView.findViewById(R.id.comment);
        share = itemView.findViewById(R.id.share);
        des = itemView.findViewById(R.id.des);

        profile.setImageResource(dashboardModel.getProfile());
        postImage.setImageResource(dashboardModel.getPostImage());
        save.setImageResource(dashboardModel.getSave());
        name.setText(dashboardModel.getName());
        about.setText(dashboardModel.getAbout());
        like.setText(dashboardModel.getLike());
        comment.setText(dashboardModel.getComment());
        share.setText(dashboardModel.getShare());
        des.setText(dashboardModel.getDes());

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
