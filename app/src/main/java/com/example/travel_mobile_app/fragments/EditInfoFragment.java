package com.example.travel_mobile_app.fragments;

import android.annotation.SuppressLint;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.travel_mobile_app.R;
public class EditInfoFragment extends Fragment implements View.OnClickListener {


    private EditText editUsername, editName, editEmail, editAddress;
    private ImageView avataImageView;
    private TextView btnUpdate;

    public EditInfoFragment() {
        // Required empty public constructor
    }

    // TODO: Rename and change types and number of parameters
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @SuppressLint("MissingInflatedId")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_edit_info, container, false);
        avataImageView = view.findViewById(R.id.avataImageView);
        avataImageView.setOnClickListener(this);

        editUsername = view.findViewById(R.id.editUsername);
        editUsername.setSingleLine();

        editAddress = view.findViewById(R.id.editAddress);
        editAddress.setSingleLine();

        editName = view.findViewById(R.id.editName);
        editName.setSingleLine();

        editEmail = view.findViewById(R.id.editEmail);
        editEmail.setSingleLine();

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
            fragmentTransaction.replace(R.id.container, new EditInfoFragment());
        }
        else if(v.getId()==R.id.btnUpdate){
            fragmentTransaction.replace(R.id.container, new SettingFragment());
        }
        if(v.getId()==R.id.createPost_btnBack){
            fragmentManager.popBackStack("setting_fragment", FragmentManager.POP_BACK_STACK_INCLUSIVE);
        }
        fragmentTransaction.commit();
    }
}