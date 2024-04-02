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
import com.example.travel_mobile_app.models.UserModel;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link SettingFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SettingFragment extends Fragment implements View.OnClickListener {
    private TextView tvEditInfo, tvChangePass;
    @SuppressLint("UseSwitchCompatOrMaterialCode")
    private Switch swNotification, swUpdate;
    private UserModel currentUser;
    public SettingFragment(UserModel user) {
        this.currentUser = user;
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
            fragmentTransaction.replace(R.id.container, new EditInfoFragment(currentUser));
        }
        else if(v.getId()==R.id.tvChangePass){
            fragmentTransaction.replace(R.id.container, new ChangePasswordFragment(currentUser));
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