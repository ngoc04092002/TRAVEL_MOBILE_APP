package com.example.travel_mobile_app;

import static android.content.ContentValues.TAG;

import android.annotation.SuppressLint;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.travel_mobile_app.fragments.SuggestionFragment;
import com.example.travel_mobile_app.fragments.event;
import com.example.travel_mobile_app.fragments.suggestion;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link DetailInfor#newInstance} factory method to
 * create an instance of this fragment.
 */
public class DetailInfor extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private ImageButton btnback;
    private ImageButton btndirect, btnevent;
    private TextView nametv, introducetv, addresstv, opentimetv, pricetv;
    private ImageView imgv;


    public DetailInfor() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment DetailInfor.
     */
    // TODO: Rename and change types and number of parameters
    public static DetailInfor newInstance(String param1, String param2) {
        DetailInfor fragment = new DetailInfor();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
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
        View view = inflater.inflate(R.layout.fragment_detail_infor, container, false);


        Bundle bundle = getArguments();
        if (bundle != null) {
            String name = bundle.getString("location_name");
            String address = bundle.getString("location_address");
            String intro = bundle.getString("location_intro");
            String event = bundle.getString("location_event");
            String imglink = bundle.getString("location_imglink");
            String number = bundle.getString("location_number");
            String price = bundle.getString("location_price");
            String opentime = bundle.getString("location_opentime");

            // Hiển thị thông tin chi tiết trong TextView hoặc các phần tử khác trong Fragment
            TextView nameTextView = view.findViewById(R.id.tenditich);
            TextView addressTextView = view.findViewById(R.id.diachi);
            TextView introTextView = view.findViewById(R.id.gioithieuchitiet);
            TextView priceTextView = view.findViewById(R.id.giave);
            TextView opentimeTextView = view.findViewById(R.id.giomocua);
            ImageView loadimg = view.findViewById(R.id.anhditich);


            nameTextView.setText(name);
            addressTextView.setText(address);
            introTextView.setText(intro);
            priceTextView.setText(price);
            opentimeTextView.setText(opentime);
            Glide.with(loadimg).load(imglink).into(loadimg);

        }

       /* FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference docRef = db.collection("locations").document("loc001");

        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        String name = document.getString("name");
                        String intro = document.getString("introduce");
                        String adrs = document.getString("address");
                        String opnt = document.getString("opentime");
                        String pric = document.getString("price");
                        String link = document.getString("imglink");
                        nametv.setText(name);
                        introducetv.setText(intro);
                        addresstv.setText(adrs);
                        opentimetv.setText(opnt);
                        pricetv.setText(pric);
                        Glide.with(imgv).load(link).into(imgv);
                        // Ở đây bạn có thể sử dụng giá trị của trường "name"
                    } else {
                        Log.d(TAG, "No such document");
                    }
                } else {
                    Log.d(TAG, "get failed with ", task.getException());
                }
            }
        });*/
        btnback = view.findViewById(R.id.backbtn3);
        btnback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Fragment otherFragment = new suggestion();
                FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
                transaction.replace(R.id.container, otherFragment);
                transaction.addToBackStack(null); // Để cho phép người dùng quay lại Fragment trước đó
                transaction.commit();
            }
        });
        btndirect = view.findViewById(R.id.directbtn);
        btndirect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Fragment otherFragment = new Directions();
                FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
                transaction.replace(R.id.container, otherFragment);
                transaction.addToBackStack(null); // Để cho phép người dùng quay lại Fragment trước đó
                transaction.commit();
            }
        });
        btnevent = view.findViewById(R.id.eventbtn);
        btnevent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Fragment otherFragment = new event();
                FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
                transaction.replace(R.id.container, otherFragment);
                transaction.addToBackStack(null); // Để cho phép người dùng quay lại Fragment trước đó
                transaction.commit();
            }
        });
        return view;}}


