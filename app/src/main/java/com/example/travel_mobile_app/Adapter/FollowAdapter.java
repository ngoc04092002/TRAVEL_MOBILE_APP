package com.example.travel_mobile_app.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.travel_mobile_app.R;
import com.example.travel_mobile_app.dto.FollowDTO;

import java.util.ArrayList;

public class FollowAdapter extends RecyclerView.Adapter<FollowAdapter.viewHolder>{

    ArrayList<FollowDTO> list;
    Context context;
    Boolean isFollow;

    public FollowAdapter(ArrayList<FollowDTO> list, Context context, Boolean isFollow) {
        this.list = list;
        this.context = context;
        this.isFollow = isFollow;
    }

    @NonNull
    @Override
    public viewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.friend_rv, parent, false);
        return new FollowAdapter.viewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull viewHolder holder, int position) {
        FollowDTO model = list.get(position);

        holder.profile.setImageResource(model.getProfile());
        holder.username.setText(model.getUsername());
        holder.followers.setText(model.getNumberOfFollowers() +"người theo dõi");
        if(!isFollow){
            holder.btnFollow.setText("Theo dõi");
            int color = ContextCompat.getColor(context, R.color.yellow);
            holder.btnFollow.setBackgroundColor(color);
        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class viewHolder extends RecyclerView.ViewHolder {

        ImageView profile;
        TextView username,followers;
        Button btnFollow;
        public viewHolder(@NonNull View itemView) {
            super(itemView);
            profile = itemView.findViewById(R.id.profile_image);
            username = itemView.findViewById(R.id.username);
            followers = itemView.findViewById(R.id.followers);
            btnFollow = itemView.findViewById(R.id.btnFollow);
        }
    }
}
