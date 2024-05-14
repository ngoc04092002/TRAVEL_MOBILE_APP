package com.example.travel_mobile_app.Adapter;

import android.content.Context;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.travel_mobile_app.R;
import com.example.travel_mobile_app.dto.FollowDTO;
import com.example.travel_mobile_app.fragments.SocialUserDetailInfoFragment;
import com.example.travel_mobile_app.models.PostModel;
import com.example.travel_mobile_app.models.UserModel;
import com.example.travel_mobile_app.services.SharedPreferencesManager;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class FollowAdapter extends RecyclerView.Adapter<FollowAdapter.viewHolder> {

    List<FollowDTO> list;
    Context context;
    final boolean[] isFollow;
    Map<Integer, Boolean> checkFollow = new TreeMap<>();
    private FirebaseFirestore db;
    FragmentManager fragmentManager;

    public FollowAdapter(List<FollowDTO> list, Context context, boolean[] isFollow, FirebaseFirestore db,FragmentManager fragmentManager) {
        this.list = list;
        this.context = context;
        this.isFollow = isFollow;
        this.db = db;
        this.fragmentManager = fragmentManager;
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
        if(!checkFollow.containsKey(position)){
            checkFollow.put(position, isFollow[0]);
        }
        holder.username.setText(model.getUsername());
        holder.followers.setText(model.getNumberOfFollowers() + " người theo dõi");
        if (!checkFollow.get(position)) {
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
            updateFollowMySelf(model, user, checkFollow.get(position));
            updateFollowUser(model, user, checkFollow.get(position));
            if (!checkFollow.get(position)) {
                holder.btnFollow.setText("Đang theo dõi");
                int color = ContextCompat.getColor(context, R.color.gray);
                holder.btnFollow.setBackgroundColor(color);
            } else {
                holder.btnFollow.setText("Theo dõi");
                int color = ContextCompat.getColor(context, R.color.yellow);
                holder.btnFollow.setBackgroundColor(color);
            }
            checkFollow.put(position,!checkFollow.get(position));
        });

        holder.infoFriends.setOnClickListener(v->{
            seeInfoDetail(model.getUserId());
        });
        holder.profile.setOnClickListener(v->{
            seeInfoDetail(model.getUserId());
        });
    }


    private void updateFollowMySelf(FollowDTO model, UserModel user,Boolean checkFollow) {
        DocumentReference userRef = db.collection("users").document(user.getId());
        if (!checkFollow) {
            user.getFollowing().add(model.getUserId());
        } else {
            user.getFollowing().remove(model.getUserId());
        }
        SharedPreferencesManager.writeUserInfo(user);
        userRef.set(user)
                .addOnCompleteListener(task -> {
                    System.out.println("ok");
                })
               .addOnFailureListener(e -> {
                   Log.e("ERROR-FOLLOWING::", e.getMessage());
               });
    }

    private void updateFollowUser(FollowDTO model, UserModel user,Boolean checkFollow) {
        DocumentReference userRef = db.collection("users").document(model.getUserId());
        userRef.get()
               .addOnCompleteListener(taskUser -> {
                   if (taskUser.isSuccessful() && taskUser.getResult() != null) {
                       UserModel userModel = taskUser.getResult().toObject(UserModel.class);
                       if (!checkFollow) {
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


    private void seeInfoDetail(String postedBy) {
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.container, new SocialUserDetailInfoFragment(postedBy));
        fragmentTransaction.addToBackStack("userDetailInfo_fragment");
        // Commit transaction
        fragmentTransaction.commit();
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class viewHolder extends RecyclerView.ViewHolder {

        ImageView profile;
        TextView username, followers;
        Button btnFollow;
        LinearLayout infoFriends;

        public viewHolder(@NonNull View itemView) {
            super(itemView);
            profile = itemView.findViewById(R.id.profile_image);
            username = itemView.findViewById(R.id.username);
            followers = itemView.findViewById(R.id.followers);
            btnFollow = itemView.findViewById(R.id.btnFollow);
            infoFriends = itemView.findViewById(R.id.info_friends);
        }
    }


}
