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
    private boolean isCurrentPassVisible = false;
    private boolean isNewPassVisible = false;
    private boolean isConfirmNewPassVisible = false;

    public ChangePasswordFragment() {
        // Required empty public constructor
    }

    public static ChangePasswordFragment newInstance(String param1, String param2) {
        ChangePasswordFragment fragment = new ChangePasswordFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_change_password, container, false);
        avataImageView = view.findViewById(R.id.avataImageView);
        avataImageView.setOnClickListener(this);

        editCurrentPass = view.findViewById(R.id.editCurrentPass);
        editCurrentPass.setSingleLine();
        editCurrentPass.setTransformationMethod(PasswordTransformationMethod.getInstance());

        editNewPass = view.findViewById(R.id.editNewPass);
        editNewPass.setSingleLine();
        editNewPass.setTransformationMethod(PasswordTransformationMethod.getInstance());


        editConfirmNewPass = view.findViewById(R.id.editConfirmNewPass);
        editConfirmNewPass.setSingleLine();
        editConfirmNewPass.setTransformationMethod(PasswordTransformationMethod.getInstance());


        btnUpdate = view.findViewById(R.id.btnUpdate);
        btnUpdate.setOnClickListener(this);

        btnEyeCurrentPass = view.findViewById(R.id.btn_eye_current_pas);
        btnEyeCurrentPass.setOnClickListener(this);

        btnEyeNewPass = view.findViewById(R.id.btn_eye_new_pass);
        btnEyeNewPass.setOnClickListener(this);

        btnEyeConfirmNewPass = view.findViewById(R.id.btn_eye_confirm_new_pass);
        btnEyeConfirmNewPass.setOnClickListener(this);

        ImageView btnBack = view.findViewById(R.id.createPost_btnBack);
        btnBack.setOnClickListener(this);

        return view;
    }

    @Override
    public void onClick(View v) {
        FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        if (v.getId() == R.id.avataImageView) {

        } else if (v.getId() == R.id.btnUpdate) {

        }  else if (v.getId() == R.id.createPost_btnBack) {
            fragmentManager.popBackStack("setting_fragment", FragmentManager.POP_BACK_STACK_INCLUSIVE);

        }  else if (v.getId() == R.id.btn_eye_current_pas) {
            togglePasswordVisibility(editCurrentPass, btnEyeCurrentPass, isCurrentPassVisible);
            isCurrentPassVisible = !isCurrentPassVisible;
        }  else if (v.getId() == R.id.btn_eye_new_pass) {
            togglePasswordVisibility(editNewPass, btnEyeNewPass, isNewPassVisible);
            isNewPassVisible = !isNewPassVisible;
        }  else if (v.getId() == R.id.btn_eye_confirm_new_pass) {
            togglePasswordVisibility(editConfirmNewPass, btnEyeConfirmNewPass, isConfirmNewPassVisible);
            isConfirmNewPassVisible = !isConfirmNewPassVisible;
        }

        fragmentTransaction.commit();
    }

    private void togglePasswordVisibility(EditText editText, ImageButton eyeButton, boolean isVisible) {
        if (isVisible) {
            // Show password
            editText.setTransformationMethod(PasswordTransformationMethod.getInstance());
            eyeButton.setBackgroundResource(R.drawable.icn_hide_pass);
        } else {
            // Hide password
            editText.setTransformationMethod(null);
            eyeButton.setBackgroundResource(R.drawable.icn_show_pass);
        }
    }
}
