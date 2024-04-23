package com.example.travel_mobile_app.fragments;

import static android.content.ContentValues.TAG;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import com.bumptech.glide.Glide;
import com.example.travel_mobile_app.Adapter.SuggestionAdapter;
import com.example.travel_mobile_app.DetailInfor;
import com.example.travel_mobile_app.R;
import com.example.travel_mobile_app.models.Location;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link SuggestionFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SuggestionFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private RecyclerView rcvSug;
    private SuggestionAdapter mSugAdapter;
    private ImageButton reloadbtn;
    private ImageButton disbtn;

    public SuggestionFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment SuggestionFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static SuggestionFragment newInstance(String param1, String param2) {
        SuggestionFragment fragment = new SuggestionFragment();
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
        View view = inflater.inflate(R.layout.fragment_suggestion2, container, false);
        rcvSug =view.findViewById(R.id.rcv_suggestion);
        mSugAdapter = new SuggestionAdapter(this);
        FirebaseFirestore.getInstance().collection("locations")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            List<Location> list = new ArrayList<>();
                            for (QueryDocumentSnapshot document : task.getResult()) {
                            String name = document.getString("name");
                            String address = document.getString("address");
                            String price = document.getString("price");
                            String event = document.getString("event");
                            String intro = document.getString("introduce");
                            String openTime = document.getString("opentime");
                            String imgLink = document.getString("imglink");
                            String number = document.getString("number");

                            Location location = new Location(address,event,imgLink,intro,name,number,openTime,price);

                            list.add(location);
                            System.out.println(list);
                            Collections.shuffle(list);
                            List<Location> randomLocations = list.subList(0, Math.min(list.size(), 6));

                            GridLayoutManager gridLayoutManager = new GridLayoutManager(getContext(), 1);

                            rcvSug.setLayoutManager(gridLayoutManager);
                            mSugAdapter.setData(randomLocations);
                            rcvSug.setAdapter(mSugAdapter);

                    }
                } else {
                    Log.d(TAG, "get failed with ", task.getException());
                }
            }
        });

        reloadbtn = view.findViewById(R.id.reloadbtn);
        reloadbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Fragment otherFragment = new SuggestionFragment();
                FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
                transaction.replace(R.id.container, otherFragment);
                transaction.addToBackStack(null); // Để cho phép người dùng quay lại Fragment trước đó
                transaction.commit();
            }
        });


        return view;
    }
   /* private List<Location> getListLocation() {
        List<Location> list = new ArrayList<>();
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference docRef = db.collection("locations").document("loc001");
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        String name = document.getString("name");
                        String address = document.getString("address");
                        String price = document.getString("price");
                        String event = document.getString("event");
                        String intro = document.getString("intro");
                        String openTime = document.getString("opentime");
                        String imgLink = document.getString("imglink");
                        String number = document.getString("number");

                        Location location = new Location(address,event,imgLink,intro,name,number,openTime,price);

                        list.add(location);

                    } else {
                        Log.d(TAG, "No such document");
                    }
                } else {
                    Log.d(TAG, "get failed with ", task.getException());
                }
            }
        });
        System.out.println("alo");
        System.out.println(list);
        return list;
    }


   /* private List<Location> getListLocation() {
        List<Location> list = new ArrayList<>();
        list.add(new Location("asasas","asasas","asdasda","asdasdas","asdasda","adasdasd","asdasdasd", "sdasdasd"));




        return list;

    }*/
}