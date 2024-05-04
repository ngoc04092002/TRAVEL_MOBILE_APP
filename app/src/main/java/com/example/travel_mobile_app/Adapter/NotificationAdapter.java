package com.example.travel_mobile_app.Adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.core.text.HtmlCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.travel_mobile_app.DetailPostInfo;
import com.example.travel_mobile_app.R;
import com.example.travel_mobile_app.SocialSearchPost;
import com.example.travel_mobile_app.databinding.FragmentNotificationItemBinding;
import com.example.travel_mobile_app.models.CommentModel;
import com.example.travel_mobile_app.models.NotificationModel;
import com.example.travel_mobile_app.models.UserModel;
import com.example.travel_mobile_app.utils.CustomDateTime;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class NotificationAdapter extends RecyclerView.Adapter<NotificationAdapter.viewHolder> {

    private List<NotificationModel> list;
    private Context context;
    private FirebaseFirestore db;
    private Activity activity;

    public NotificationAdapter(List<NotificationModel> list, Context context, FirebaseFirestore db, Activity activity) {
        this.list = list;
        this.context = context;
        this.db = db;
        this.activity = activity;
    }

    @NonNull
    @Override
    public viewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.fragment_notification_item, parent, false);
        return new NotificationAdapter.viewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull viewHolder holder, int position) {
        NotificationModel model = list.get(position);


        //load image
        String userUri = model.getUserImage();
        if (userUri == null) {
            Glide.with(context).clear(holder.binding.profileImage);
            holder.binding.profileImage.setImageURI(null);
            holder.binding.profileImage.setImageResource(R.drawable.avatar_men);

        } else {
            Glide.with(context)
                 .load(Uri.parse(userUri))
                 .centerCrop()
                 .placeholder(R.drawable.image_empty)
                 .into(holder.binding.profileImage);
        }

        //fix 8c89d98007c54f34b44f2f619a8684b3 other userid
        String type = model.getType();
        String content = "<b>" + model.getNotificationBy() + "</b>";
        if (type.equals("like")) {
            content += " Thích bài đăng của bạn";
        } else if (type.equals("comment")) {
            content += " Bình luận bài đăng của bạn";
        }

        holder.binding.notificationContent.setText(HtmlCompat.fromHtml(content, HtmlCompat.FROM_HTML_MODE_LEGACY));

        holder.binding.time.setText(CustomDateTime.formatDate(model.getNotificationAt()));

        int color = ContextCompat.getColor(context, R.color.unread);
        if (model.isCheckOpen()) {
            color = ContextCompat.getColor(context, R.color.white);
        }
        holder.binding.notificationItem.setBackgroundColor(color);

        holder.binding.toolbar.setOnMenuItemClickListener(v -> {
            if (v.getItemId() == R.id.del_notification) {
                db.collection("notifications").document(model.getNotificationId()).delete().addOnSuccessListener(unused -> {
                    List<NotificationModel> assignData = list.stream().filter(item -> !item.getNotificationId().equals(model.getNotificationId())).collect(Collectors.toList());
                    list = assignData;
                    notifyDataSetChanged();
                }).addOnFailureListener(e -> {
                    Toast.makeText(context, "Mạng kém", Toast.LENGTH_SHORT).show();
                });
                return true;
            }
            return false;
        });


        //handle click
        holder.binding.infoNotification.setOnClickListener(v -> {
            navigateToDetailPostInfo(model.getPostId());
        });

        holder.binding.profileImage.setOnClickListener(v -> {
            navigateToDetailPostInfo(model.getPostId());
        });
    }

    private void navigateToDetailPostInfo(String postId) {
        Intent i = new Intent(context, DetailPostInfo.class);
        i.putExtra("detail_post_activity", "notification");
        i.putExtra("postId", postId);
        context.startActivity(i);
        activity.overridePendingTransition(0, android.R.anim.slide_out_right);
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class viewHolder extends RecyclerView.ViewHolder {
        FragmentNotificationItemBinding binding;

        public viewHolder(@NonNull View itemView) {
            super(itemView);
            binding = FragmentNotificationItemBinding.bind(itemView);

        }
    }
}
