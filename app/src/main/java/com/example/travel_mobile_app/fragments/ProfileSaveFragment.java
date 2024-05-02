package com.example.travel_mobile_app.fragments;

import android.annotation.SuppressLint;
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

import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileSaveFragment extends Fragment implements View.OnClickListener {
    private RecyclerView saveRv;
    private CircleImageView btnBack;
    ArrayList<SaveItemModel> list;

    public ProfileSaveFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @SuppressLint("MissingInflatedId")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_profile_save, container, false);

        saveRv = view.findViewById(R.id.save_itemsRv);
        list = new ArrayList<>();
        list.add(new SaveItemModel(1L,
                "02523938371644009ade322b38b4e166",
                "qbJW6GgDkqgv6H5tvCPfLty2Bto2",
                "Đã lưu 12 giờ trước",
                "",
                "https://firebasestorage.googleapis.com/v0/b/travel-app-130de.appspot.com/o/posts%2FqbJW6GgDkqgv6H5tvCPfLty2Bto2%2F1713536813871?alt=media&token=d99e10ef-f022-4504-9e9e-50517fbddf00"));
//        list.add(new SaveItemModel(2L, R.drawable.favorite, "<b>ngocvan</b> đã tạo 1 bài viết rất hay", "Đã lưu 2 phút trước"));
//        list.add(new SaveItemModel(3L, R.drawable.bell_fill, "<b>ngocvan</b> đã nhắc tới bạn", "Đã lưu 1 phút trước"));

        ProfileSaveAdapter notificationAdapter = new ProfileSaveAdapter(list, getContext());
        saveRv.setHasFixedSize(true);
        saveRv.setLayoutManager(new StaggeredGridLayoutManager(1, LinearLayoutManager.VERTICAL));
        saveRv.setAdapter(notificationAdapter);

        btnBack = view.findViewById(R.id.btn_back);
        btnBack.setOnClickListener(this);

        return view;
    }

    @Override
    public void onClick(View v) {
        FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        if (v.getId() == R.id.btn_back) {
            fragmentManager.popBackStack("account_fragment", FragmentManager.POP_BACK_STACK_INCLUSIVE);

        }

        fragmentTransaction.commit();
    }
}