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
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AccountFollowAdapter extends RecyclerView.Adapter<AccountFollowAdapter.viewHolder> {

    List<FollowDTO> list;
    Context context;
    private boolean isShowButton;
    private FirebaseFirestore db;
    FragmentManager fragmentManager;

    public AccountFollowAdapter(List<FollowDTO> list, Context context, boolean isShowButton, FirebaseFirestore db,FragmentManager fragmentManager) {
        this.list = list;
        this.context = context;
        this.isShowButton = isShowButton;
        this.db = db;
        this.fragmentManager = fragmentManager;
    }

    public void setData(List<FollowDTO> newData) {
        this.list = newData;
    }

    @NonNull
    @Override
    public viewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.fragment_account_follow, parent, false);
        return new AccountFollowAdapter.viewHolder(view);
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
        holder.btnFollow.setVisibility(isShowButton ? View.VISIBLE : View.GONE);


        holder.btnFollow.setOnClickListener(v -> {
            UserModel user = SharedPreferencesManager.readUserInfo();
            updateFollowingUser(model, user, position);

        });

        holder.infoFriends.setOnClickListener(v->{
            seeInfoDetail(model.getUserId());
        });
        holder.profile.setOnClickListener(v->{
            seeInfoDetail(model.getUserId());
        });
    }


    private void updateFollowingUser(FollowDTO model, UserModel user, int position) {

        DocumentReference userRef = db.collection("users").document(user.getId());
        user.getFollowing().remove(model.getUserId());
        SharedPreferencesManager.writeUserInfo(user);
        userRef.set(user)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        // Xóa tài liệu thành công, loại bỏ mục khỏi danh sách
                        list.remove(position);

                        // Cập nhật giao diện
                        notifyItemRemoved(position);
                        Log.d("TAG", "DocumentSnapshot successfully deleted!");
                    }
                })
                .addOnFailureListener(e -> {
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
