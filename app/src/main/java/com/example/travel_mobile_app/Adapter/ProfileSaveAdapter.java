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
        View view = LayoutInflater.from(context).inflate(R.layout.fragment_notification_item, parent, false);
        return new ProfileSaveAdapter.viewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull viewHolder holder, int position) {
        SaveItemModel model = list.get(position);

        holder.profile.setImageResource(model.getProfile());
//        holder.title.setText(HtmlCompat.fromHtml(model.getTitle(),HtmlCompat.FROM_HTML_MODE_LEGACY));
        holder.time.setText(model.getTime());
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class viewHolder extends RecyclerView.ViewHolder {

        ImageView profile;
        TextView title, time;

        public viewHolder(@NonNull View itemView) {
            super(itemView);

            profile = itemView.findViewById(R.id.profile_image);
            title = itemView.findViewById(R.id.title);
            time = itemView.findViewById(R.id.time);
        }
    }
}
