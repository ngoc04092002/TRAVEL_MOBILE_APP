package com.example.travel_mobile_app.Adapter;

import android.content.Context;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.travel_mobile_app.R;
import com.example.travel_mobile_app.dto.FollowDTO;
import com.example.travel_mobile_app.models.UserModel;
import com.example.travel_mobile_app.services.SharedPreferencesManager;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FollowAdapter extends RecyclerView.Adapter<FollowAdapter.viewHolder> {

    List<FollowDTO> list;
    Context context;
    final boolean[] isFollow;
    private FirebaseFirestore db;

    public FollowAdapter(List<FollowDTO> list, Context context, boolean[] isFollow, FirebaseFirestore db) {
        this.list = list;
        this.context = context;
        this.isFollow = isFollow;
        this.db = db;
    }

    public void setData(List<FollowDTO> newData) {
        this.list = newData;
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
        if (model.getProfileImage() != null) {
            Glide.with(context)
                 .load(Uri.parse(model.getProfileImage()))
                 .centerCrop()
                 .placeholder(R.drawable.image_empty)
                 .into(holder.profile);
        }

        holder.username.setText(model.getUsername());
        holder.followers.setText(model.getNumberOfFollowers() + " người theo dõi");
        if (!isFollow[0]) {
            holder.btnFollow.setText("Theo dõi");
            int color = ContextCompat.getColor(context, R.color.yellow);
            holder.btnFollow.setBackgroundColor(color);
        }else{
            holder.btnFollow.setText("Đang theo dõi");
            int color = ContextCompat.getColor(context, R.color.gray);
            holder.btnFollow.setBackgroundColor(color);
        }


        holder.btnFollow.setOnClickListener(v -> {
            UserModel user = SharedPreferencesManager.readUserInfo();
            updateFollowMySelf(model, user);
            updateFollowUser(model, user);
            if (!isFollow[0]) {
                holder.btnFollow.setText("Đang theo dõi");
                int color = ContextCompat.getColor(context, R.color.gray);
                holder.btnFollow.setBackgroundColor(color);
            } else {
                holder.btnFollow.setText("Theo dõi");
                int color = ContextCompat.getColor(context, R.color.yellow);
                holder.btnFollow.setBackgroundColor(color);
            }
            isFollow[0] = !isFollow[0];
        });
    }

    private void updateFollowMySelf(FollowDTO model, UserModel user) {

        DocumentReference userRef = db.collection("users").document(user.getId());

        if (!isFollow[0]) {
            user.getFollowing().add(model.getUserId());
        } else {
            user.getFollowing().remove(model.getUserId());
        }

        SharedPreferencesManager.writeUserInfo(user);
        userRef.set(user)
               .addOnFailureListener(e -> {
                   Log.e("ERROR-FOLLOWING::", e.getMessage());
               });
    }

    private void updateFollowUser(FollowDTO model, UserModel user) {
        DocumentReference userRef = db.collection("users").document(model.getUserId());
        userRef.get()
               .addOnCompleteListener(taskUser -> {
                   if (taskUser.isSuccessful() && taskUser.getResult() != null) {
                       UserModel userModel = taskUser.getResult().toObject(UserModel.class);
                       if (!isFollow[0]) {
                           userModel.getFollowers().add(user.getId());
                       } else {
                           userModel.getFollowers().remove(user.getId());
                       }
                       userRef.set(userModel);
                   }
               }).addOnFailureListener(e -> {
                   Log.e("ERROR-FOLLOWING::", e.getMessage());
               });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class viewHolder extends RecyclerView.ViewHolder {

        ImageView profile;
        TextView username, followers;
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
