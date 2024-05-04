package com.example.travel_mobile_app.Adapter;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.travel_mobile_app.R;
import com.example.travel_mobile_app.databinding.StoryRvDesignBinding;
import com.example.travel_mobile_app.models.StoryModel;
import com.example.travel_mobile_app.models.UserStory;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import omari.hamza.storyview.StoryView;
import omari.hamza.storyview.callback.StoryClickListeners;
import omari.hamza.storyview.model.MyStory;

public class StoryAdapter extends RecyclerView.Adapter<StoryAdapter.viewHolder> {

    private ArrayList<StoryModel> list;
    private Context context;

    String pattern = "MM/dd/yyyy HH:mm:ss";
    SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);

    public StoryAdapter(ArrayList<StoryModel> list, Context context) {
        this.list = list;
        this.context = context;
    }

    @NonNull
    @Override
    public viewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.story_rv_design, parent, false);
        return new viewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull viewHolder holder, int position) {

        StoryModel storyModel = list.get(position);
        // fix add load user image
        //load image
        if (storyModel.getUri() != null) {
            Glide.with(context)
                 .load(Uri.parse(storyModel.getUri()))
                 .centerCrop()
                 .placeholder(R.drawable.image_empty)
                 .into(holder.binding.story);
        }

        if (storyModel.getImage() != null) {
            Glide.with(context)
                 .load(Uri.parse(storyModel.getImage()))
                 .centerCrop()
                 .placeholder(R.drawable.image_empty)
                 .into(holder.binding.profileImage);
        }

        holder.binding.name.setText(storyModel.getFullName());

        holder.binding.story.setOnClickListener(v -> {
            ArrayList<MyStory> myStories = new ArrayList<>();
            for (UserStory userStory : storyModel.getUserStories()) {
                myStories.add(new MyStory(
                        userStory.getUri(),
                        new Date(userStory.getStoryAt())
                ));
            }

            // fix
            new StoryView.Builder(((AppCompatActivity) context).getSupportFragmentManager())
                    .setStoriesList(myStories)
                    .setStoryDuration(5000)
                    .setTitleText(storyModel.getFullName())
                    .setSubtitleText(storyModel.getFullName())
                    .setTitleLogoUrl(storyModel.getImage())
                    .setStoryClickListeners(new StoryClickListeners() {
                        @Override
                        public void onDescriptionClickListener(int position) {
                            //your action
                        }

                        @Override
                        public void onTitleIconClickListener(int position) {
                            //your action
                        }
                    }) // Optional Listeners
                    .build() // Must be called before calling show method
                    .show();
        });


    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class viewHolder extends RecyclerView.ViewHolder {

        StoryRvDesignBinding binding;

        public viewHolder(@NonNull View itemView) {
            super(itemView);
            binding = StoryRvDesignBinding.bind(itemView);

        }
    }
}
