package com.example.travel_mobile_app.fragments;

import android.annotation.SuppressLint;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;

import com.example.travel_mobile_app.R;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link SettingFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SettingFragment extends Fragment implements View.OnClickListener {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private TextView tvEditInfo, tvChangePass;
    @SuppressLint("UseSwitchCompatOrMaterialCode")
    private Switch swNotification, swUpdate;
    public SettingFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment SettingFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static SettingFragment newInstance(String param1, String param2) {
        SettingFragment fragment = new SettingFragment();
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
        View view = inflater.inflate(R.layout.fragment_setting, container, false);
        tvEditInfo = view.findViewById(R.id.tvEditInfo);
        tvEditInfo.setOnClickListener(this);

        tvChangePass = view.findViewById(R.id.tvChangePass);
        tvChangePass.setOnClickListener(this);

        swNotification = view.findViewById(R.id.swNotification);
        swNotification.setOnClickListener(this);

        swUpdate = view.findViewById(R.id.swUpdate);
        swUpdate.setOnClickListener(this);

        ImageView btnBack = view.findViewById(R.id.createPost_btnBack);
        btnBack.setOnClickListener(this);
        return view;
    }

    @Override
    public void onClick(View v) {
        FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        if(v.getId()==R.id.tvEditInfo){
            fragmentTransaction.replace(R.id.container, new EditInfoFragment());
        }
        else if(v.getId()==R.id.tvChangePass){
            fragmentTransaction.replace(R.id.container, new SettingFragment());
        }
        else if(v.getId()==R.id.swNotification){
//            fragmentTransaction.replace(R.id.container, new SettingFragment());
        }
        else if(v.getId()==R.id.swUpdate){
//            fragmentTransaction.replace(R.id.container, new SettingFragment());
        }
        if(v.getId()==R.id.createPost_btnBack){
            fragmentManager.popBackStack("account_fragment", FragmentManager.POP_BACK_STACK_INCLUSIVE);
        }

        // Thêm transaction vào back stack (nếu cần)
        fragmentTransaction.addToBackStack("setting_fragment");

        // Commit transaction
        fragmentTransaction.commit();
    }
}