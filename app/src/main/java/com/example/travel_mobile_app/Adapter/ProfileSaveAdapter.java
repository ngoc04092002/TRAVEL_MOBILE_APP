package com.example.travel_mobile_app.Adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.text.HtmlCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.travel_mobile_app.DetailPostInfo;
import com.example.travel_mobile_app.R;
import com.example.travel_mobile_app.databinding.FragmentNotificationItemBinding;
import com.example.travel_mobile_app.databinding.FragmentPostSavedItemBinding;
import com.example.travel_mobile_app.models.SaveItemModel;
import com.example.travel_mobile_app.utils.CustomDateTime;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

public class ProfileSaveAdapter extends RecyclerView.Adapter<ProfileSaveAdapter.viewHolder> {

    private ArrayList<SaveItemModel> list;
    private Context context;
    FirebaseFirestore db = FirebaseFirestore.getInstance();

    public ProfileSaveAdapter(ArrayList<SaveItemModel> list, Context context) {
        this.list = list;
        this.context = context;
    }

    @NonNull
    @Override
    public viewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.fragment_post_saved_item, parent, false);
        return new ProfileSaveAdapter.viewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull viewHolder holder, int position) {
        SaveItemModel model = list.get(position);


        Glide.with(context)
                .load(model.getImg())
                .placeholder(R.drawable.image_empty)
                .into(holder.binding.imageView);
        holder.binding.title.setText(model.getTitle());
        holder.binding.time.setText(CustomDateTime.formatDate(model.getTime()));

        holder.binding.unSaved.setOnClickListener(v -> {
            unsavePost(model.getId(), position);

        });

        holder.itemView.setOnClickListener(v -> {
            // Khi người dùng nhấp vào mục, điều hướng đến DetailPostInfo
            navigateToDetailPostInfo(model.getPostID());
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }


    public class viewHolder extends RecyclerView.ViewHolder {
        FragmentPostSavedItemBinding binding;

        public viewHolder(@NonNull View itemView) {
            super(itemView);
            binding = FragmentPostSavedItemBinding.bind(itemView);

        }
    }

    public void unsavePost(String documentID, int position) {
        // Xóa tài liệu từ Firestore
        db.collection("save_posts").document(documentID)
                .delete()
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
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // Xóa tài liệu thất bại
                        Log.w("TAG", "Error deleting document", e);
                    }
                });
    }

    private void navigateToDetailPostInfo(String postId) {
        Intent intent = new Intent(context, DetailPostInfo.class);
        intent.putExtra("postId", postId);
        intent.putExtra("activity_type", "profile_save_fragment"); // Truyền thông tin nguồn gọi
        context.startActivity(intent);
    }

}
