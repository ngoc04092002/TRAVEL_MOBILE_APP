package com.example.travel_mobile_app.Adapter;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.bumptech.glide.Glide;
import com.example.travel_mobile_app.R;
import com.example.travel_mobile_app.databinding.DashboardRvBinding;
import com.example.travel_mobile_app.fragments.SocialUserDetailInfoFragment;
import com.example.travel_mobile_app.models.CommentModel;
import com.example.travel_mobile_app.models.PostModel;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;

import java.util.ArrayList;

public class PostAdapter extends RecyclerView.Adapter<PostAdapter.viewHolder> {

    ArrayList<PostModel> list;
    Context context;

    FragmentManager fragmentManager;
    Activity activity;

    public PostAdapter(ArrayList<PostModel> list, Context context, FragmentManager fragmentManager, Activity activity) {
        this.list = list;
        this.context = context;
        this.fragmentManager = fragmentManager;
        this.activity = activity;
    }

    @NonNull
    @Override
    public viewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.dashboard_rv, parent, false);

        return new viewHolder(view, fragmentManager);
    }

    @Override
    public void onBindViewHolder(@NonNull viewHolder holder, int position) {
        PostModel post = list.get(position);

        //load image
        if (post.getPostImage() == null) {
            Glide.with(context).clear(holder.binding.postimg);
            holder.binding.postimg.setImageURI(null);
            holder.binding.postimg.setVisibility(View.GONE);

        } else {
            Glide.with(context)
                 .load(Uri.parse(post.getPostImage()))
                 .centerCrop()
                 .placeholder(R.drawable.shimmer)
                 .into(holder.binding.postimg);
        }



    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class viewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        FragmentManager fragmentManager;
        DashboardRvBinding binding;

        public viewHolder(@NonNull View itemView, FragmentManager fragmentManager) {
            super(itemView);
            binding = DashboardRvBinding.bind(itemView);
            this.fragmentManager = fragmentManager;

            binding.profileImage.setOnClickListener(this);
            binding.postUserName.setOnClickListener(this);
            binding.postimg.setOnClickListener(v -> {
                showCenterDialog(binding.postimg.getDrawable());
            });

            binding.comment.setOnClickListener(v -> {
                showBottomDialog();
            });

        }

        @Override
        public void onClick(View v) {
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

            if (v.getId() == R.id.profile_image || v.getId() == R.id.post_user_name) {
                fragmentTransaction.replace(R.id.container, new SocialUserDetailInfoFragment(1));
            }
            // Thêm transaction vào back stack (nếu cần)
            fragmentTransaction.addToBackStack("userDetailInfo_fragment");

            // Commit transaction
            fragmentTransaction.commit();
        }
    }

    //comments UI
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

        CommentAdapter commentAdapter = new CommentAdapter(comments, dialog.getContext());
        commentsRv.setHasFixedSize(true);
        commentsRv.setLayoutManager(new StaggeredGridLayoutManager(1, LinearLayoutManager.VERTICAL));
        commentsRv.setAdapter(commentAdapter);

        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().setGravity(Gravity.BOTTOM);
        dialog.show();
    }

    private ImageView imageView;
    private float x, y, dx, dy, initX, initY, limitCoordinatesY1, limitCoordinatesY2, limitCoordinatesX;

    //hieenr thị ảnh lên toàn màn hình khi click vào ảnh bài post
    private void showCenterDialog(Drawable drawable) {
        final Dialog dialog = new Dialog(activity);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.centersheet_image);

        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().setGravity(Gravity.BOTTOM);

        imageView = dialog.findViewById(R.id.postimg);
        imageView.setImageDrawable(drawable);
        handleOnTouchImageOfPost(dialog, imageView);

        ImageButton btnCloseDialog = dialog.findViewById(R.id.btnClose_dialog);
        btnCloseDialog.setOnClickListener(v -> {
            dialog.dismiss();
        });

        dialog.show();
    }

    private void handleOnTouchImageOfPost(Dialog dialog, ImageView imageView) {
        // Đặt độ mờ ban đầu là 0
        WindowManager.LayoutParams layoutParams = dialog.getWindow().getAttributes();
        layoutParams.dimAmount = 1;
        dialog.getWindow().setAttributes(layoutParams);


        imageView.setOnTouchListener((v, event) -> {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    x = event.getRawX();
                    y = event.getRawY();
                    initX = imageView.getX();
                    initY = imageView.getY();
                    limitCoordinatesY1 = -imageView.getHeight() / 2;
                    limitCoordinatesY2 = initY + imageView.getHeight() / 2;
                    limitCoordinatesX = imageView.getWidth() / 2;
                    break;
                case MotionEvent.ACTION_MOVE:
                    dx = event.getRawX() - x;
                    dy = event.getRawY() - y;

                    float edgeX = imageView.getX() + dx;
                    float edgeY = imageView.getY() + dy;
                    float acreage1 = (Math.abs(edgeY) - initY + (imageView.getHeight() / 2)) * (Math.abs(edgeX) + (imageView.getWidth() / 2));
                    float acreage2 = limitCoordinatesX * 2 * (limitCoordinatesY2 - limitCoordinatesY1);
                    // Áp dụng độ mờ cho dialog
                    layoutParams.dimAmount = (acreage2 - acreage1) / acreage2;
                    dialog.getWindow().setAttributes(layoutParams);

                    imageView.setX(edgeX);
                    imageView.setY(edgeY);
                    x = event.getRawX();
                    y = event.getRawY();
                    break;
                case MotionEvent.ACTION_UP:

                    if (Math.abs(imageView.getX()) >= limitCoordinatesX || imageView.getY() >= limitCoordinatesY2 || imageView.getY() <= limitCoordinatesY1) {
                        dialog.dismiss();
                    } else {
                        imageView.setX(initX);
                        imageView.setY(initY);
                        layoutParams.dimAmount = 1;
                        dialog.getWindow().setAttributes(layoutParams);
                    }
                    break;
            }
            return true;
        });

    }


}

