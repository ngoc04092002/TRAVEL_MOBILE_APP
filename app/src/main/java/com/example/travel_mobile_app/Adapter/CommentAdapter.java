package com.example.travel_mobile_app.Adapter;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Handler;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.text.HtmlCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.travel_mobile_app.R;
import com.example.travel_mobile_app.databinding.CommentItemBinding;
import com.example.travel_mobile_app.models.CommentModel;
import com.example.travel_mobile_app.models.PostModel;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.viewHolder> {

    private ArrayList<CommentModel> list;
    private Context context;
    private FirebaseFirestore db;
    private PostModel post;

    public CommentAdapter(ArrayList<CommentModel> list, Context context) {
        this.list = list;
        this.context = context;
    }

    public CommentAdapter(ArrayList<CommentModel> list, Context context, FirebaseFirestore db, PostModel post) {
        this.list = list;
        this.context = context;
        this.db = db;
        this.post = post;
    }

    @NonNull
    @Override
    public viewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.comment_item, parent, false);
        return new CommentAdapter.viewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull viewHolder holder, int position) {
        CommentModel model = list.get(position);
        holder.binding.profileImage.setImageResource(model.getProfile());
        holder.binding.content.setText(HtmlCompat.fromHtml("<b>" + model.getCommentBy() + "</b><br> " + model.getContent(), HtmlCompat.FROM_HTML_MODE_LEGACY));
        holder.binding.createAt.setText(formatDate(model.getCreateAt()));

        //fix compare if comment yourself
        handleLongPress(holder.binding.commentItem, model.getCommentId());
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class viewHolder extends RecyclerView.ViewHolder {

        CommentItemBinding binding;

        public viewHolder(@NonNull View itemView) {
            super(itemView);
            binding = CommentItemBinding.bind(itemView);

        }
    }

    private void handleLongPress(LinearLayout item, String commentId) {
        Handler handler = new Handler();
        Runnable longPressRunnable = () -> {
            showDialog(commentId);
        };

        item.setOnTouchListener((v, event) -> {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    handler.postDelayed(longPressRunnable, 800);
                    return true;
                case MotionEvent.ACTION_UP:
                case MotionEvent.ACTION_CANCEL:
                    handler.removeCallbacks(longPressRunnable);
                    return true;
            }
            return false;
        });
    }

    private void showDialog(String commentId) {
        final BottomSheetDialog dialog = new BottomSheetDialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.comment_item_option);

        LinearLayout btnDeleteComment = dialog.findViewById(R.id.btnDelete_comment);
        btnDeleteComment.setOnClickListener(v -> {
            List<CommentModel> filterComment = post.getComments().stream()                // convert list to stream
                                                   .filter(line -> !line.getCommentId().equals(commentId))     // we dont like mkyong
                                                   .collect(Collectors.toList());
            post.setComments(filterComment);
            db.collection("posts").document(post.getPostId())
              .set(post).addOnSuccessListener(unused -> {
                  notifyDataSetChanged();
              })
              .addOnFailureListener(e -> {
                  Toast.makeText(context, "Đã có lỗi xảy ra", Toast.LENGTH_SHORT).show();
              });

            dialog.dismiss();
        });

        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().setGravity(Gravity.BOTTOM);
        dialog.show();
    }

    private String formatDate(long previousTimeMillis) {
        long currentTimeMillis = System.currentTimeMillis();

        Instant currentInstant = Instant.ofEpochMilli(currentTimeMillis);
        Instant previousInstant = Instant.ofEpochMilli(previousTimeMillis);

        Duration duration = Duration.between(previousInstant, currentInstant);

        long seconds = duration.getSeconds();
        long minutes = seconds / 60;
        long hours = minutes / 60;
        long days = hours / 24;
        long months = days / 30;
        long years = days / 365;

        if (years > 0) {
            return years + " năm trước";
        } else if (months > 0) {
            return months + " tháng trước";
        } else if (days > 0) {
            return days + " ngày trước";
        } else if (hours > 0) {
            return hours + " giờ trước";
        } else if (minutes > 0) {
            return minutes + " phút trước";
        } else {
            return seconds + " giây trước";
        }
    }
}
