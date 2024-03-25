package com.example.travel_mobile_app.fragments;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import android.annotation.SuppressLint;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.travel_mobile_app.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.concurrent.Executor;


public class AccountFragment extends Fragment implements View.OnClickListener{
    private static final String TAG = "EmailPassword";

    private LinearLayout btnSaved, btnSetting, btnManagePost, btnLogout;
    private FirebaseAuth mAuth;
    public AccountFragment() {
        // Required empty public constructor
    }

    public static AccountFragment newInstance(String param1, String param2) {
        AccountFragment fragment = new AccountFragment();
        Bundle args = new Bundle();

        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAuth = FirebaseAuth.getInstance();
    }
    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.

    }
    @SuppressLint("WrongViewCast")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_account, container, false);
        btnSaved = view.findViewById(R.id.btnSaved);
        btnSaved.setOnClickListener(this);

        btnSetting = view.findViewById(R.id.btnSetting);
        btnSetting.setOnClickListener(this);

        btnManagePost = view.findViewById(R.id.btnManagePost);
        btnManagePost.setOnClickListener(this);

        btnLogout = view.findViewById(R.id.btnLogout);
        btnLogout.setOnClickListener(this);

//        FirebaseUser currentUser = mAuth.getCurrentUser();
//        if(currentUser != null){
//            Log.d("abc","haha");
//        } else {
//            Log.d("abc","haha");
//        }
//        mAuth.createUserWithEmailAndPassword("abcd@gmail.com", "123456")
//                .addOnCompleteListener(requireActivity(), new OnCompleteListener<AuthResult>() {
//                    @Override
//                    public void onComplete(@NonNull Task<AuthResult> task) {
//                        if (task.isSuccessful()) {
//                            // Sign in success, update UI with the signed-in user's information
//                            Log.d(TAG, "createUserWithEmail:success");
//                            FirebaseUser user = mAuth.getCurrentUser();
//                            updateUI(user);
//                        } else {
//                            // If sign in fails, display a message to the user.
//                            Log.w(TAG, "createUserWithEmail:failure", task.getException());
//                            Toast.makeText(getContext(), "Authentication failed.",
//                                    Toast.LENGTH_SHORT).show();
//                            updateUI(null);
//                        }
//                    }
//                });
        return view;
    }
    private void updateUI(FirebaseUser user) {

    }
    @Override
    public void onClick(View v) {
        FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        if(v.getId()==R.id.btnSaved){
            fragmentTransaction.replace(R.id.container, new ProfileSaveFragment());
        }
        else if(v.getId()==R.id.btnSetting){
            fragmentTransaction.replace(R.id.container, new SettingFragment());
        }
        else if(v.getId()==R.id.btnManagePost){
            fragmentTransaction.replace(R.id.container, new SocialUserDetailInfoFragment());
        }
        else if(v.getId()==R.id.btnLogout){
//            fragmentTransaction.replace(R.id.container, new SettingFragment());
        }

        // Thêm transaction vào back stack (nếu cần)
        fragmentTransaction.addToBackStack("account_fragment");

        // Commit transaction
        fragmentTransaction.commit();
    }
}