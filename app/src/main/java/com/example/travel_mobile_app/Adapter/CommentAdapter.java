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

import java.util.ArrayList;

public class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.viewHolder> {

    private ArrayList<CommentModel> list;
    private Context context;

    public CommentAdapter(ArrayList<CommentModel> list, Context context) {
        this.list = list;
        this.context = context;
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
        holder.content.setText(HtmlCompat.fromHtml("<b>" + model.getCommentBy() + "</b> " + model.getContent(), HtmlCompat.FROM_HTML_MODE_LEGACY));
        holder.createAt.setText(model.getCreateAt());
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
}
