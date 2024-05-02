package com.example.travel_mobile_app.fragments;

import android.annotation.SuppressLint;
import android.graphics.drawable.Drawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.bumptech.glide.Glide;
import com.example.travel_mobile_app.models.UserModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.travel_mobile_app.R;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import de.hdodenhof.circleimageview.CircleImageView;

public class ChangePasswordFragment extends Fragment implements View.OnClickListener {
    private EditText editCurrentPass, editNewPass, editConfirmNewPass;
    private ImageButton btnEyeCurrentPass, btnEyeNewPass, btnEyeConfirmNewPass;
    private TextView btnUpdate, tvName, tvUsername;
    private CircleImageView avataImageView;
    private ProgressBar progressBar;

    private boolean isCurrentPassVisible = false;
    private boolean isNewPassVisible = false;
    private boolean isConfirmNewPassVisible = false;
    private static final String TAG = "PasswordChangeActivity";

    // Firebase Authentication instance
    private FirebaseAuth mAuth;
    // Firestore instance
    private FirebaseFirestore mFirestore;

    private UserModel currentUser;
    public ChangePasswordFragment(UserModel user) {
        // Required empty public constructor
        this.currentUser = user;
    }

    @SuppressLint("MissingInflatedId")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_change_password, container, false);

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();
// I    //Initialize Firestore
        mFirestore = FirebaseFirestore.getInstance();

        progressBar = view.findViewById(R.id.progress_bar);
        progressBar.setVisibility(View.GONE);

        avataImageView = view.findViewById(R.id.imv_avatar);

        tvName = view.findViewById(R.id.tvName);
        tvUsername = view.findViewById(R.id.tvUsername);

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

        setUpUI();
        return view;
    }

    private void setUpUI() {
        tvName.setText(currentUser.getFullName());
        tvUsername.setText("@" + currentUser.getUsername());
        Drawable defaultAvatar = getResources().getDrawable(R.drawable.avatar_men);

        Glide.with(requireContext())
                .load(currentUser.getAvatarURL())
                .placeholder(defaultAvatar)
                .into(avataImageView);
    }

    @Override
    public void onClick(View v) {
        FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        if (v.getId() == R.id.btnUpdate) {
            changePassword();
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

    private void changePassword() {
        String currentPassword = editCurrentPass.getText().toString().trim();
        String newPassword = editNewPass.getText().toString().trim();
        String confirmPassword = editConfirmNewPass.getText().toString().trim();
        if (currentPassword.equals(currentUser.getPassword())) {
            if (newPassword.equals(confirmPassword)) {
                progressBar.setVisibility(View.VISIBLE);
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                AuthCredential credential = EmailAuthProvider.getCredential(user.getEmail(), currentPassword);

                user.reauthenticate(credential)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    // User re-authenticated successfully, now change the password
                                    user.updatePassword(newPassword)
                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    if (task.isSuccessful()) {
                                                        // Password updated successfully
                                                        updatePasswordInFirestore(user.getUid(), newPassword);
                                                        Toast.makeText(getContext(), "Password updated successfully", Toast.LENGTH_SHORT).show();
//                                                        finish(); // Finish the activity
                                                    } else {
                                                        progressBar.setVisibility(View.GONE);
                                                        Log.e(TAG, "Error updating password", task.getException());
                                                        Toast.makeText(getContext(), "Failed to update password", Toast.LENGTH_SHORT).show();
                                                    }
                                                }
                                            });
                                } else {
                                    progressBar.setVisibility(View.GONE);
                                    Log.e(TAG, "Error re-authenticating", task.getException());
                                    Toast.makeText(getContext(), "Failed to re-authenticate", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
            } else {
                Log.e(TAG, "Confirm Password Not Match!");
                Toast.makeText(getContext(), "Confirm Password Not Match!", Toast.LENGTH_SHORT).show();
            }
        } else {
            Log.e(TAG, "Current Password Not Correct!");
            Toast.makeText(getContext(), "Current Password Not Correct!", Toast.LENGTH_SHORT).show();
        }
    }

    private void updatePasswordInFirestore(String userId, String newPassword) {
        System.out.println("userID: " + userId);
        // Update the password in Firestore
        DocumentReference userRef = mFirestore.collection("users").document(userId);

        userRef.update("password", newPassword)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        progressBar.setVisibility(View.GONE);
                        if (task.isSuccessful()) {
                            Log.d(TAG, "Password updated in Firestore");
                        } else {
                            Log.e(TAG, "Error updating password in Firestore", task.getException());
                        }
                    }
                });
    }
}
