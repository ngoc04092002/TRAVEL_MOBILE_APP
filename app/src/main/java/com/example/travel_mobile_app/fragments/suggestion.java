package com.example.travel_mobile_app.fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ListView;

import com.example.travel_mobile_app.Adapter.SuggestionAdapter;
import com.example.travel_mobile_app.DetailInfor;
import com.example.travel_mobile_app.R;
import com.google.firebase.firestore.FirebaseFirestore;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link suggestion#newInstance} factory method to
 * create an instance of this fragment.
 */
public class suggestion extends Fragment implements View.OnClickListener {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private ImageButton disbtn;
    private ListView dexuatlv;
    private SuggestionAdapter dexuatadap;
    private FirebaseFirestore db;


    public suggestion() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment suggestion.
     */
    // TODO: Rename and change types and number of parameters
    public static suggestion newInstance(String param1, String param2) {
        suggestion fragment = new suggestion();
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

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_suggestion, container, false);

        disbtn = view.findViewById(R.id.discoverybtn);
        disbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Fragment otherFragment = new DetailInfor();
                FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
                transaction.replace(R.id.container, otherFragment);
                transaction.addToBackStack(null); // Để cho phép người dùng quay lại Fragment trước đó
                transaction.commit();
            }
        });
        return view;
    }


    @Override
    public void onClick(View v) {

    }
}