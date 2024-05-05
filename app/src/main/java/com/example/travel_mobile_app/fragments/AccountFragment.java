package com.example.travel_mobile_app.fragments;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.travel_mobile_app.Login;
import com.example.travel_mobile_app.R;
import com.example.travel_mobile_app.models.UserModel;
import com.example.travel_mobile_app.services.SharedPreferencesManager;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import de.hdodenhof.circleimageview.CircleImageView;


public class AccountFragment extends Fragment implements View.OnClickListener{
    private static final String TAG = "AccountFragment";
    private ProgressBar progressBar;
    private UserModel currentUser;
    private LinearLayout btnSaved, btnSetting, btnManagePost, btnLogout;
    private CircleImageView imvAvatar;
    private TextView tvFullname, tvUsername, tvEmail, tvFollower, tvFollowing;
    private LinearLayout follwing, follower;
    private FirebaseAuth mAuth;
    private FirebaseFirestore firestore = FirebaseFirestore.getInstance();

    public AccountFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }
    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.

    }
    @SuppressLint({"WrongViewCast", "MissingInflatedId"})
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_account, container, false);

        progressBar = view.findViewById(R.id.progress_bar);
        progressBar.setVisibility(View.GONE);

        imvAvatar = view.findViewById(R.id.profile_image);

        tvFullname = view.findViewById(R.id.detail_info_name);
        tvUsername = view.findViewById(R.id.detail_info_username);
        tvEmail = view.findViewById(R.id.detail_info_address);
        tvFollower = view.findViewById(R.id.detail_info_follower_cnt);
        tvFollowing = view.findViewById(R.id.detail_info_following_cnt);

        btnSaved = view.findViewById(R.id.btnSaved);
        btnSaved.setOnClickListener(this);

        btnSetting = view.findViewById(R.id.btnSetting);
        btnSetting.setOnClickListener(this);

        btnManagePost = view.findViewById(R.id.btnManagePost);
        btnManagePost.setOnClickListener(this);

        btnLogout = view.findViewById(R.id.btnLogout);
        btnLogout.setOnClickListener(this);

        follwing = view.findViewById(R.id.liner_following);
        follwing.setOnClickListener(this);

        follower = view.findViewById(R.id.liner_follower);
        follower.setOnClickListener(this);

        UserModel userModel = SharedPreferencesManager.readUserInfo();
        currentUser = userModel;

        updateUI(userModel);

        return view;
    }
    @Override
    public void onClick(View v) {
        FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.setCustomAnimations(
                R.anim.enter_animation,
                R.anim.exit_animation,
                R.anim.pop_enter_animation,
                R.anim.pop_exit_animation
        );
        if(v.getId()==R.id.btnSaved){
            fragmentTransaction.replace(R.id.container, new ProfileSaveFragment());
        }
        else if(v.getId()==R.id.btnSetting){
            fragmentTransaction.replace(R.id.container, new SettingFragment(currentUser));
        }
        else if(v.getId()==R.id.btnManagePost){
            fragmentTransaction.replace(R.id.container, new MyPostsFragment());
        }
        else if(v.getId()==R.id.btnLogout){
            FirebaseAuth.getInstance().signOut();

            Intent intent = new Intent(requireActivity(), Login.class);

            intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);

            startActivity(intent);
            requireActivity().finish();
        }
        else if (v.getId() == R.id.liner_follower) {
            fragmentTransaction.replace(R.id.container, new MyPostsFragment());
        }
        else if (v.getId() == R.id.liner_follower) {
            fragmentTransaction.replace(R.id.container, new MyPostsFragment());

        }

        fragmentTransaction.addToBackStack("account_fragment");
        // Commit transaction
        fragmentTransaction.commit();

    }

    @SuppressLint("SetTextI18n")
    private void  updateUI(UserModel user) {
        System.out.println("NAME: " + user.getFullName());
        tvFullname.setText(user.getFullName());
        tvUsername.setText("@" + user.getUsername());
        tvEmail.setText(user.getEmail());
        tvFollower.setText(String.valueOf(user.getFollowers().size()));
        tvFollowing.setText(String.valueOf(user.getFollowing().size()));
        @SuppressLint("UseCompatLoadingForDrawables") Drawable defaultAvatar = getResources().getDrawable(R.drawable.avatar_men);

        Glide.with(requireContext())
                .load(user.getAvatarURL())
                .placeholder(defaultAvatar)
                .into(imvAvatar);
    }

}