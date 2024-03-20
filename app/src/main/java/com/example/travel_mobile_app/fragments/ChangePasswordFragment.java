package com.example.travel_mobile_app.fragments;

import android.annotation.SuppressLint;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.text.method.PasswordTransformationMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.travel_mobile_app.R;

public class ChangePasswordFragment extends Fragment implements View.OnClickListener {
    private EditText editCurrentPass, editNewPass, editConfirmNewPass;
    private ImageButton btnEyeCurrentPass, btnEyeNewPass, btnEyeConfirmNewPass;
    private TextView btnUpdate;
    private ImageView avataImageView;
    public ChangePasswordFragment() {
        // Required empty public constructor
    }

    // TODO: Rename and change types and number of parameters
    public static ChangePasswordFragment newInstance(String param1, String param2) {
        ChangePasswordFragment fragment = new ChangePasswordFragment();
        Bundle args = new Bundle();
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
        View view = inflater.inflate(R.layout.fragment_change_password, container, false);
        avataImageView = view.findViewById(R.id.avataImageView);
        avataImageView.setOnClickListener(this);

        editCurrentPass = view.findViewById(R.id.editCurrentPass);
        editCurrentPass.setSingleLine();
        editCurrentPass.setTransformationMethod(PasswordTransformationMethod.getInstance());
        editNewPass = view.findViewById(R.id.editNewPass);
        editNewPass.setSingleLine();

        editConfirmNewPass = view.findViewById(R.id.editConfirmNewPass);
        editConfirmNewPass.setSingleLine();

        btnUpdate= view.findViewById(R.id.btnUpdate);
        btnUpdate.setOnClickListener(this);

        ImageView btnBack = view.findViewById(R.id.createPost_btnBack);
        btnBack.setOnClickListener(this);
        return view;
    }

    @Override
    public void onClick(View v) {
        FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        if(v.getId()==R.id.imv_avatar){
//            fragmentTransaction.replace(R.id.container, new EditInfoFragment());
        }
        else if(v.getId()==R.id.btnUpdate){
//            fragmentTransaction.replace(R.id.container, new SettingFragment());
        }
        if(v.getId()==R.id.createPost_btnBack){
            fragmentManager.popBackStack("setting_fragment", FragmentManager.POP_BACK_STACK_INCLUSIVE);
        }
        fragmentTransaction.commit();
    }
}