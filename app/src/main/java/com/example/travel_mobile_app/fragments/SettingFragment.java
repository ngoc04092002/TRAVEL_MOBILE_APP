package com.example.travel_mobile_app.fragments;

import android.annotation.SuppressLint;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.travel_mobile_app.R;
import com.example.travel_mobile_app.models.UserModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import de.hdodenhof.circleimageview.CircleImageView;

public class SettingFragment extends Fragment implements View.OnClickListener {
    private TextView tvEditInfo, tvChangePass;
    @SuppressLint("UseSwitchCompatOrMaterialCode")
    private Switch swNotification, swUpdate;
    private UserModel currentUser;
    private CircleImageView imvAvatar;
    private FirebaseFirestore firestore = FirebaseFirestore.getInstance();

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
        swNotification.setChecked(currentUser.isEnableNotification());

        swUpdate = view.findViewById(R.id.swUpdate);
        swUpdate.setChecked(currentUser.isEnableUpdate());

        imvAvatar = view.findViewById(R.id.imv_avatar);
        Glide.with(getContext()).load(currentUser.getAvatarURL()).into(imvAvatar);

        ImageView btnBack = view.findViewById(R.id.createPost_btnBack);
        btnBack.setOnClickListener(this);

        swNotification.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                // Update the isEnableNotification field in Firestore
                updateNotificationPreference(isChecked);
            }
        });

        swUpdate.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                // Update the isEnableNotification field in Firestore
                updatePreference(isChecked);
            }
        });


        return view;
    }

    @Override
    public void onClick(View v) {
        FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.setCustomAnimations(
                R.anim.enter_animation,  // Hoạt ảnh cho fragment mới khi nó xuất hiện
                R.anim.exit_animation,   // Hoạt ảnh cho fragment cũ khi nó biến mất
                R.anim.pop_enter_animation,  // Hoạt ảnh cho fragment mới khi "pop"
                R.anim.pop_exit_animation    // Hoạt ảnh cho fragment cũ khi "pop"
        );
        if (v.getId() == R.id.tvEditInfo) {
            fragmentTransaction.replace(R.id.container, new EditInfoFragment(currentUser));
        } else if (v.getId() == R.id.tvChangePass) {
            fragmentTransaction.replace(R.id.container, new ChangePasswordFragment(currentUser));
        }
        if (v.getId() == R.id.createPost_btnBack) {
            fragmentManager.popBackStack("account_fragment", FragmentManager.POP_BACK_STACK_INCLUSIVE);
        }

        fragmentTransaction.addToBackStack("setting_fragment");

        // Commit transaction
        fragmentTransaction.commit();
    }

    private void updateNotificationPreference(boolean isEnabled) {
        // Get reference to the user document in Firestore
        DocumentReference userRef = firestore.collection("users").document(currentUser.getId());

        // Update the isEnableNotification field
        userRef.update("enableNotification", isEnabled)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(getContext(), "Thông báo: " + (isEnabled ? "BẬT" : "TẮT"), Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(getContext(), "Failed to update notification preference", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void updatePreference(boolean isEnabled) {
        // Get reference to the user document in Firestore
        DocumentReference userRef = firestore.collection("users").document(currentUser.getId());

        // Update the isEnableNotification field
        userRef.update("enableUpdate", isEnabled)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(getContext(), "Cập nhật: " + (isEnabled ? "BẬT" : "TẮT"), Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(getContext(), "Failed to update notification preference", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
}
