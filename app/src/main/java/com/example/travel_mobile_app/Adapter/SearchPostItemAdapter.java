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
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.travel_mobile_app.R;
import com.example.travel_mobile_app.dto.FollowDTO;
import com.example.travel_mobile_app.models.PostModel;
import com.example.travel_mobile_app.utils.CustomDateTime;

import java.util.List;

public class SearchPostItemAdapter extends RecyclerView.Adapter<SearchPostItemAdapter.viewHolder> {
    List<PostModel> list;
    Context context;
    String type;
    Activity activity;

    public SearchPostItemAdapter(List<PostModel> list, Context context, String type,Activity activity) {
        this.list = list;
        this.context = context;
        this.type = type;
        this.activity = activity;
    }

    @NonNull
    @Override
    public viewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.social_search_post, parent, false);
        return new SearchPostItemAdapter.viewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull viewHolder holder, int position) {
        PostModel post = list.get(position);

        if (post.getPostImage() != null && post.getPostImage() != "") {
            Glide.with(context)
                 .load(Uri.parse(post.getPostImage()))
                 .centerCrop()
                 .placeholder(R.drawable.circle)
                 .into(holder.profile);
        }
        holder.timestamp.setText(CustomDateTime.formatDate(post.getPostedAt()));
        holder.des.setText(post.getPostDescription());
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class viewHolder extends RecyclerView.ViewHolder {

        ImageView profile;
        TextView des, timestamp;

        public viewHolder(@NonNull View itemView) {
            super(itemView);
            profile = itemView.findViewById(R.id.search_item_img);
            des = itemView.findViewById(R.id.search_post_des);
            timestamp = itemView.findViewById(R.id.search_post_timestamp);

            profile.setOnClickListener(v -> {
                showCenterDialog(profile.getDrawable());
            });
        }
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
