package com.example.travel_mobile_app.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.text.HtmlCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.travel_mobile_app.R;
import com.example.travel_mobile_app.models.CommentModel;
import com.example.travel_mobile_app.models.PostModel;
import com.google.firebase.firestore.FirebaseFirestore;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;

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
        holder.profile.setImageResource(model.getProfile());
        holder.content.setText(HtmlCompat.fromHtml("<b>" + model.getCommentBy() + "</b><br> " + model.getContent(), HtmlCompat.FROM_HTML_MODE_LEGACY));
        holder.createAt.setText(formatDate(model.getCreateAt()));
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class viewHolder extends RecyclerView.ViewHolder {

        ImageView profile;
        TextView content, createAt;

        public viewHolder(@NonNull View itemView) {
            super(itemView);

            profile = itemView.findViewById(R.id.profile_image);
            content = itemView.findViewById(R.id.content);
            createAt = itemView.findViewById(R.id.createAt);
        }
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
