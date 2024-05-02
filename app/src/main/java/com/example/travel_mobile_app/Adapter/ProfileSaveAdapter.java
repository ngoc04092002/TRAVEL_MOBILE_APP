package com.example.travel_mobile_app.Adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.drawable.Drawable;
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
import com.example.travel_mobile_app.R;
import com.example.travel_mobile_app.models.SaveItemModel;

import java.util.ArrayList;

public class ProfileSaveAdapter extends RecyclerView.Adapter<ProfileSaveAdapter.viewHolder> {

    private ArrayList<SaveItemModel> list;
    private Context context;

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
                .into(holder.imageView);
//        holder.title.setText(HtmlCompat.fromHtml(model.getTitle(),HtmlCompat.FROM_HTML_MODE_LEGACY));
        holder.time.setText(model.getTime());
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class viewHolder extends RecyclerView.ViewHolder {

        ImageView imageView;
        TextView title, time;
        ImageButton unSavedButton;

        public viewHolder(@NonNull View itemView) {
            super(itemView);

            imageView = itemView.findViewById(R.id.imageView);
            title = itemView.findViewById(R.id.title);
            time = itemView.findViewById(R.id.time);
            unSavedButton = itemView.findViewById(R.id.un_saved);
        }
    }
}
