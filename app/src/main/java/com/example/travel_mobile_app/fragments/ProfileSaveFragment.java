package com.example.travel_mobile_app.fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.example.travel_mobile_app.Adapter.NotificationAdapter;
import com.example.travel_mobile_app.Adapter.PostAdapter;
import com.example.travel_mobile_app.Adapter.ProfileSaveAdapter;
import com.example.travel_mobile_app.R;
import com.example.travel_mobile_app.models.NotificationModel;
import com.example.travel_mobile_app.models.SaveItemModel;

import java.util.ArrayList;

public class ProfileSaveFragment extends Fragment implements View.OnClickListener {
    private RecyclerView saveRv;
    ArrayList<SaveItemModel> list;

    public ProfileSaveFragment() {
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
        View view = inflater.inflate(R.layout.fragment_profile_save, container, false);

        saveRv = view.findViewById(R.id.save_itemsRv);
        list = new ArrayList<>();
        list.add(new SaveItemModel("1L", "https://buffer.com/cdn-cgi/image/w=1000,fit=contain,q=90,f=auto/library/content/images/size/w600/2023/10/free-images.jpg", "<b>ngocvan</b> chào bạn", 1L));
        list.add(new SaveItemModel("2L", "https://buffer.com/cdn-cgi/image/w=1000,fit=contain,q=90,f=auto/library/content/images/size/w600/2023/10/free-images.jpg", "<b>ngocvan</b> đã tạo 1 bài viết rất hay", 1L));
        list.add(new SaveItemModel("3L", "https://buffer.com/cdn-cgi/image/w=1000,fit=contain,q=90,f=auto/library/content/images/size/w600/2023/10/free-images.jpg", "<b>ngocvan</b> đã nhắc tới bạn", 1L));

        ProfileSaveAdapter notificationAdapter = new ProfileSaveAdapter(list, getContext());
        saveRv.setHasFixedSize(true);
        saveRv.setLayoutManager(new StaggeredGridLayoutManager(1, LinearLayoutManager.VERTICAL));
        saveRv.setAdapter(notificationAdapter);

        ImageView btnBack = view.findViewById(R.id.friend_btnBack);
        btnBack.setOnClickListener(this);

        return view;
    }

    @Override
    public void onClick(View v) {
        FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        if (v.getId() == R.id.friend_btnBack) {
            fragmentManager.popBackStack("account_fragment", FragmentManager.POP_BACK_STACK_INCLUSIVE);

        }

        fragmentTransaction.commit();
    }
}