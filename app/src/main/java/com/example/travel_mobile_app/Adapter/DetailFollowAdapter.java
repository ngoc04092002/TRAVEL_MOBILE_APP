package com.example.travel_mobile_app.Adapter;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.travel_mobile_app.R;
import com.example.travel_mobile_app.models.UserModel;

import java.util.List;

public class DetailFollowAdapter extends RecyclerView.Adapter<DetailFollowAdapter.viewHolder>{

    List<UserModel> userModels;
    Context context;

    public DetailFollowAdapter(List<UserModel> userModels,Context context){
        this.context=context;
        this.userModels=userModels;
    }


    @NonNull
    @Override
    public viewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.detail_follow_rv, parent, false);
        return new DetailFollowAdapter.viewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull viewHolder holder, int position) {
        UserModel user = userModels.get(position);

        if (user.getAvatarURL() != null && user.getAvatarURL() != "") {
            Glide.with(context)
                 .load(Uri.parse(user.getAvatarURL()))
                 .centerCrop()
                 .placeholder(R.drawable.circle)
                 .into(holder.detailFollowImage);
        }
        if (user.getFollowers() != null) {
            int cntFollower = user.getFollowers().size();
            String follower= cntFollower > 120 ? "120+" : String.valueOf(cntFollower);
            holder.detailFollowFollowers.setText(follower+" Đang theo dõi");
        }
        if(user.getFullName()!=null){
            holder.detailFollowUsername.setText(user.getFullName());
        }
    }

    @Override
    public int getItemCount() {
        return userModels.size();
    }

    public class viewHolder extends RecyclerView.ViewHolder {

        ImageView detailFollowImage;
        TextView detailFollowUsername,detailFollowFollowers;
        public viewHolder(@NonNull View itemView) {
            super(itemView);
            detailFollowImage = itemView.findViewById(R.id.detail_follow_profile_image);
            detailFollowUsername = itemView.findViewById(R.id.detail_follow_username);
            detailFollowFollowers = itemView.findViewById(R.id.detail_follow_followers);
        }
    }
}
