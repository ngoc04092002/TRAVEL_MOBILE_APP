package com.example.travel_mobile_app.fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.travel_mobile_app.Adapter.NotificationAdapter;
import com.example.travel_mobile_app.R;
import com.example.travel_mobile_app.models.NotificationModel;
import com.github.ybq.android.spinkit.SpinKitView;
import com.github.ybq.android.spinkit.sprite.Sprite;
import com.github.ybq.android.spinkit.style.Circle;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;


public class NotificationFragment extends Fragment {

    private RecyclerView notificationItemRv;
    ArrayList<NotificationModel> list;
    private FirebaseFirestore db;

    private SpinKitView spinKit;

    public NotificationFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        db = FirebaseFirestore.getInstance();

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_notification, container, false);

        spinKit = view.findViewById(R.id.spin_kit);
        notificationItemRv = view.findViewById(R.id.notification_itemsRv);

        list = new ArrayList<>();
        NotificationAdapter notificationAdapter = new NotificationAdapter(list, getContext(), db);
        setNotificationListData(notificationAdapter);


        notificationItemRv.setHasFixedSize(true);
        notificationItemRv.setLayoutManager(new StaggeredGridLayoutManager(1, LinearLayoutManager.VERTICAL));
        notificationItemRv.setAdapter(notificationAdapter);

        return view;
    }

    private void setNotificationListData(NotificationAdapter notificationAdapter) {
        showProgressBar();
        //fix I2cG4PNtPmSCnPSS0BQib3rRxxl2 userId
        db.collection("notifications").whereEqualTo("postedBy", "I2cG4PNtPmSCnPSS0BQib3rRxxl2")
          .get()
          .addOnCompleteListener(task -> {
              if (task.isSuccessful()) {
                  for (QueryDocumentSnapshot document : task.getResult()) {
                      NotificationModel model = document.toObject(NotificationModel.class);
                      model.setNotificationId(document.getId());
                      list.add(model);
                  }
                  dismissProgressBar();
                  notificationAdapter.notifyDataSetChanged();
              } else {
                  dismissProgressBar();
                  Log.d("record", "Error getting documents: ", task.getException());
              }
          });
    }

    private void showProgressBar() {
        spinKit.setVisibility(View.VISIBLE);
        Sprite circle = new Circle();
        spinKit.setIndeterminateDrawable(circle);
    }

    private void dismissProgressBar() {
        spinKit.setVisibility(View.GONE);
    }
}