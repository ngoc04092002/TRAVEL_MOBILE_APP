package com.example.travel_mobile_app.Adapter;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
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
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.button.MaterialButton;

import java.util.ArrayList;

public class DashboardAdapter extends RecyclerView.Adapter<DashboardAdapter.viewHolder>{

    ArrayList<DashboardModel> list;
    Context context;

    FragmentManager fragmentManager;

    public DashboardAdapter(ArrayList<DashboardModel> list, Context context, FragmentManager fragmentManager) {
        this.list = list;
        this.context = context;
        this.fragmentManager =fragmentManager;
    }

    @NonNull
    @Override
    public viewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.dashboard_rv, parent, false);

        return new viewHolder(view,fragmentManager);
    }

    @Override
    public void onBindViewHolder(@NonNull viewHolder holder, int position) {
        DashboardModel dashboardModel = list.get(position);

        holder.profile.setImageResource(dashboardModel.getProfile());
        holder.postImage.setImageResource(dashboardModel.getPostImage());
        holder.save.setImageResource(dashboardModel.getSave());
        holder.name.setText(dashboardModel.getName());
        holder.about.setText(dashboardModel.getAbout());
        holder.like.setText(dashboardModel.getLike());
        holder.comment.setText(dashboardModel.getComment());
        holder.share.setText(dashboardModel.getShare());
        holder.des.setText(dashboardModel.getDes());
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class viewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        ImageView profile, postImage, save;
        TextView name, about, des;

        MaterialButton like, comment, share;

        LinearLayout postUserName;

        FragmentManager fragmentManager;

        public viewHolder(@NonNull View itemView, FragmentManager fragmentManager) {
            super(itemView);
            this.fragmentManager = fragmentManager;
            profile = itemView.findViewById(R.id.profile_image);
            postImage = itemView.findViewById(R.id.postimg);
            save = itemView.findViewById(R.id.save);
            name = itemView.findViewById(R.id.username);
            about = itemView.findViewById(R.id.followers);
            like = itemView.findViewById(R.id.like);
            comment = itemView.findViewById(R.id.comment);
            share = itemView.findViewById(R.id.share);
            des = itemView.findViewById(R.id.des);
            postUserName = itemView.findViewById(R.id.post_user_name);

            profile.setOnClickListener(this);
            postUserName.setOnClickListener(this);

            comment.setOnClickListener(v -> {
                showBottomDialog();
            });

        }

        @Override
        public void onClick(View v) {
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

            if(v.getId()==R.id.profile_image||v.getId()==R.id.post_user_name){
                fragmentTransaction.replace(R.id.container, new SocialUserDetailInfoFragment(1));
            }
            // Thêm transaction vào back stack (nếu cần)
            fragmentTransaction.addToBackStack("userDetailInfo_fragment");

            // Commit transaction
            fragmentTransaction.commit();
        }
    }

    private void showBottomDialog() {
        final BottomSheetDialog dialog = new BottomSheetDialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.bottomsheet_layout);

        View bottomSheetView = dialog.findViewById(com.google.android.material.R.id.design_bottom_sheet);
        BottomSheetBehavior<View> bottomSheetBehavior = BottomSheetBehavior.from(bottomSheetView);
        bottomSheetBehavior.setPeekHeight(bottomSheetView.getHeight());
        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);


        ImageButton bottomsheet_back = dialog.findViewById(R.id.bottomsheet_back);

        EditText commentEditText = dialog.findViewById(R.id.comment_message);
        commentEditText.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus) {
                commentEditText.postDelayed(() -> {
                    InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.showSoftInput(commentEditText, InputMethodManager.SHOW_IMPLICIT);
                }, 200);
            }
        });

        bottomsheet_back.setOnClickListener(v -> {
            dialog.dismiss();
        });

        ArrayList<CommentModel> comments;
        RecyclerView commentsRv = dialog.findViewById(R.id.commentsRv);

        comments = new ArrayList<>();
        comments.add(new CommentModel(R.drawable.avatar_men, "ngocvan", "1", "Cảnh này đẹp quaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaas", "1 phút trước"));
        comments.add(new CommentModel(R.drawable.avatar_men, "ngocvan", "1", "Cảnh này đẹp quaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaas", "1 phút trước"));
        comments.add(new CommentModel(R.drawable.avatar_men, "ngocvan", "1", "Cảnh này đẹp quaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaas", "1 phút trước"));
        comments.add(new CommentModel(R.drawable.avatar_men, "ngocvan", "1", "Cảnh này đẹp quaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaas", "1 phút trước"));
        comments.add(new CommentModel(R.drawable.avatar_men, "ngocvan", "1", "Cảnh này đẹp quaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaas", "1 phút trước"));
        comments.add(new CommentModel(R.drawable.avatar_men, "ngocvan", "1", "Cảnh này đẹp quaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaas", "1 phút trước"));
        comments.add(new CommentModel(R.drawable.avatar_men, "ngocvan", "1", "Cảnh này đẹp quaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaas", "1 phút trước"));
        comments.add(new CommentModel(R.drawable.avatar_men, "ngocvan", "1", "Cảnh này đẹp quaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaas", "1 phút trước"));
        comments.add(new CommentModel(R.drawable.avatar_men, "ngocvan", "1", "Cảnh này đẹp quaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaas", "1 phút trước"));
        comments.add(new CommentModel(R.drawable.avatar_men, "ngocvan", "1", "Cảnh này đẹp quaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaas", "1 phút trước"));
        comments.add(new CommentModel(R.drawable.avatar_men, "ngocvan", "1", "Cảnh này đẹp quaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaas", "1 phút trước"));
        comments.add(new CommentModel(R.drawable.avatar_men, "ngocvan", "1", "Cảnh này đẹp quaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaas", "1 phút trước"));
        comments.add(new CommentModel(R.drawable.avatar_men, "ngocvan", "1", "Cảnh này đẹp quaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaas", "1 phút trước"));
        comments.add(new CommentModel(R.drawable.avatar_men, "ngocvan", "1", "Cảnh này đẹp quaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaas", "1 phút trước"));

        CommentAdapter commentAdapter = new CommentAdapter(comments, dialog.getContext());
        commentsRv.setHasFixedSize(true);
        commentsRv.setLayoutManager(new StaggeredGridLayoutManager(1, LinearLayoutManager.VERTICAL));
        commentsRv.setAdapter(commentAdapter);

        dialog.show();
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().setGravity(Gravity.BOTTOM);

    }

}
