package com.example.travel_mobile_app;

import static androidx.databinding.DataBindingUtil.setContentView;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import com.example.travel_mobile_app.fragments.suggestion;
import com.google.firebase.FirebaseApp;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link Directions#newInstance} factory method to
 * create an instance of this fragment.
 */
public class Directions extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private ImageButton btnback3;
    private ImageButton btnmaps;
    private ImageButton btnphone;
    private ImageButton btntaxi;



    public Directions() {


        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment Directions.
     */
    // TODO: Rename and change types and number of parameters
    public static Directions newInstance(String param1, String param2) {
        Directions fragment = new Directions();
        Bundle args = new Bundle();


        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_directions, container, false);
        Bundle bundle = getArguments();
        String number = bundle.getString("location_number");
        btnphone = view.findViewById(R.id.phonebtn);
        btnphone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // Tạo Intent với hành động ACTION_DIAL
                Intent intentphone = new Intent(Intent.ACTION_DIAL);
                System.out.println(number);

                // Đặt dữ liệu URI cho số điện thoại mà bạn muốn gọi
                intentphone.setData(Uri.parse("tel:" + number));


                // Khởi chạy Intent
                startActivity(intentphone);}
        });

        btnback3 = view.findViewById(R.id.backbtn2);
        btnback3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getParentFragmentManager().popBackStack();

            }
        });

        btnmaps = view.findViewById(R.id.ggmapsbtn);
        btnmaps.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Thay đổi Uri bằng Uri của ứng dụng bạn muốn mở
                Uri uri = Uri.parse("https://www.google.com/maps");
                Intent intentmap = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(intentmap);
            }
        });

        btntaxi = view.findViewById(R.id.taxibtn);
        btntaxi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Thay đổi Uri bằng Uri của ứng dụng bạn muốn mở
                Uri uri = Uri.parse("https://www.google.com/maps");
                Intent intenttaxi = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(intenttaxi);
            }
        });
        return view;
    }
}