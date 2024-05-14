package com.example.travel_mobile_app.fragments;

import static android.content.ContentValues.TAG;

import android.annotation.SuppressLint;
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

import com.example.travel_mobile_app.Adapter.AllLocationAdapter;
import com.example.travel_mobile_app.R;
import com.example.travel_mobile_app.models.Location;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link AllLocationFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AllLocationFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private RecyclerView rcvSug;
    private AllLocationAdapter mSugAdapter;
    private ImageButton backbtn;
    private ImageButton addlocbtn;

    public AllLocationFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment AllLocationFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static AllLocationFragment newInstance(String param1, String param2) {
        AllLocationFragment fragment = new AllLocationFragment();
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

    @SuppressLint("MissingInflatedId")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_alllocation, container, false);
        rcvSug =view.findViewById(R.id.rcv_suggestion);
        mSugAdapter = new AllLocationAdapter(this);


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
                            Long closeTime = document.getLong("closetime");
                            String intro = document.getString("introduce");
                            Long openTime = document.getLong("opentime");
                            String imgLink = document.getString("imglink");
                            String number = document.getString("number");
                            String id = document.getId();

                            Location location = new Location(id,address,imgLink,intro,name,number,openTime,closeTime,price);

                            list.add(location);
                            /*System.out.println(list);
                            Collections.shuffle(list);
                            List<Location> randomLocations = list.subList(0, Math.min(list.size(), 6));*/

                            GridLayoutManager gridLayoutManager = new GridLayoutManager(getContext(), 1);

                            rcvSug.setLayoutManager(gridLayoutManager);
                            mSugAdapter.setData(list);
                            rcvSug.setAdapter(mSugAdapter);

                    }
                } else {
                    Log.d(TAG, "get failed with ", task.getException());
                }
            }
        });
        backbtn = view.findViewById(R.id.backbtn4);
        backbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getParentFragmentManager().popBackStack();

            }
        });
         addlocbtn = view.findViewById(R.id.addlocationbtn);
         addlocbtn.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View v) {
                 Fragment otherFragment = new AddLocationFragment();
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