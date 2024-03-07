package com.example.travel_mobile_app.fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.travel_mobile_app.Adapter.NotificationAdapter;
import com.example.travel_mobile_app.Adapter.StoryAdapter;
import com.example.travel_mobile_app.R;
import com.example.travel_mobile_app.models.NotificationModel;

import java.util.ArrayList;

public class NotificationFragment extends Fragment {

    private RecyclerView notificationItemRv;
    ArrayList<NotificationModel> list;

    public NotificationFragment() {
        // Required empty public constructor
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_notification, container, false);

        notificationItemRv = view.findViewById(R.id.notification_itemsRv);
        list = new ArrayList<>();
        list.add(new NotificationModel(R.drawable.avatar_men, "<b>ngocvan</b> chào bạn", "12 giờ trước"));
        list.add(new NotificationModel(R.drawable.favorite, "<b>ngocvan</b> đã tạo 1 bài viết rất hay", "12 giờ trước"));
        list.add(new NotificationModel(R.drawable.bell_fill, "<b>ngocvan</b> đã nhắc tới bạn", "12 giờ trước"));

        NotificationAdapter notificationAdapter = new NotificationAdapter(list, getContext());
        notificationItemRv.setHasFixedSize(true);
        notificationItemRv.setLayoutManager(new StaggeredGridLayoutManager(1, LinearLayoutManager.VERTICAL));
        notificationItemRv.setAdapter(notificationAdapter);

        return view;
    }
}