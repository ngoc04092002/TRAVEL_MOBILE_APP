package com.example.travel_mobile_app.fragments;

import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.IdRes;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.viewmodel.CreationExtras;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.bumptech.glide.Glide;
import com.example.travel_mobile_app.Adapter.SocialUserDetailInfoAdapter;
import com.example.travel_mobile_app.R;
import com.example.travel_mobile_app.databinding.FragmentSocialUserDetailInfoBinding;
import com.example.travel_mobile_app.models.PostModel;
import com.example.travel_mobile_app.models.UserModel;
import com.github.ybq.android.spinkit.SpinKitView;
import com.github.ybq.android.spinkit.sprite.Sprite;
import com.github.ybq.android.spinkit.style.Circle;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.Filter;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class SocialUserDetailInfoFragment extends Fragment implements View.OnClickListener {
    private String userId;
    private FirebaseFirestore db;
    FragmentSocialUserDetailInfoBinding binding;


    public SocialUserDetailInfoFragment() {
        // Required empty public constructor
    }

    public SocialUserDetailInfoFragment(String userId) {
        this.userId = userId;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        db = FirebaseFirestore.getInstance();
    }

    private ImageView btnFriendBack;
    private List<PostModel> postList;
    private SpinKitView spinKit;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_social_user_detail_info, container, false);
        binding = FragmentSocialUserDetailInfoBinding.bind(view);
        spinKit = view.findViewById(R.id.spin_kit);

        postList = new ArrayList<>();
        showProgressBar();

        CollectionReference users = db.collection("users");
        users.document(userId)
             .get().addOnSuccessListener(documentSnapshot -> {
                 UserModel userModel = documentSnapshot.toObject(UserModel.class);
                 setUserInfo(userModel);
                 binding.badRequest.setVisibility(View.GONE);
                 binding.uInfoScroll.setVisibility(View.VISIBLE);
             })
             .addOnFailureListener(
                     unused ->
                     {
                         binding.badRequest.setVisibility(View.VISIBLE);
                         binding.uInfoScroll.setVisibility(View.GONE);
                     });

        CollectionReference posts = db.collection("posts");
        posts.where(Filter.or(
                     Filter.equalTo("postedBy", userId),
                     Filter.equalTo("shareBy", userId)))
             .get()
             .addOnCompleteListener(task ->
                                    {
                                        if (task.isSuccessful()) {
                                            for (QueryDocumentSnapshot document : task.getResult()) {
                                                PostModel model = document.toObject(PostModel.class);
                                                postList.add(model);
                                            }

                                            LinearLayout userInfos = view.findViewById(R.id.uInfo_container);

                                            for (PostModel model : postList) {
                                                LayoutInflater post = LayoutInflater.from(getContext());
                                                View subLayout = post.inflate(R.layout.dashboard_rv, null);
                                                SocialUserDetailInfoAdapter socialUserDetailInfoAdapter = new SocialUserDetailInfoAdapter(model, getContext(), db, getActivity(), userInfos);
                                                View viewSubLayout = socialUserDetailInfoAdapter.onCreateView(subLayout, model);

                                                userInfos.addView(viewSubLayout);
                                            }
                                        }
                                        dismissProgressBar();

                                    }).

             addOnFailureListener(e ->

                                  {
                                      Log.e("ERROR-USER-DETAIL-INFOR", e.getMessage());
                                      dismissProgressBar();
                                  });


        //handle click
        btnFriendBack = view.findViewById(R.id.uInfo_btnBack);
        btnFriendBack.setOnClickListener(this);


        return view;
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.uInfo_btnBack) {
            FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();
            fragmentManager.popBackStack("userDetailInfo_fragment", FragmentManager.POP_BACK_STACK_INCLUSIVE);
            fragmentManager.popBackStack("account_fragment", FragmentManager.POP_BACK_STACK_INCLUSIVE);
        }
    }

    private void setUserInfo(UserModel userModel) {
        if (userModel == null || this == null) {
            return;
        }

        if (userModel.getFollowing() != null) {
            int cntFollowing = userModel.getFollowing().size();
            String following = cntFollowing > 120 ? "120+" : String.valueOf(cntFollowing);
            binding.detailInfoFollowingCnt.setText(following);
        }
        if (userModel.getFollowers() != null) {
            int cntFollower = userModel.getFollowers().size();
            String follower = cntFollower > 120 ? "120+" : String.valueOf(cntFollower);
            binding.detailInfoFollowerCnt.setText(follower);
        }

        if (userModel.getFullName() != null) {
            binding.detailInfoName.setText(userModel.getFullName());
            binding.uInfoName.setText(userModel.getFullName());
        }

        if (userModel.getUsername() != null) {
            binding.detailInfoUsername.setText(userModel.getUsername());
            binding.uInfoUsername.setText(userModel.getUsername());
        } else {
            binding.detailInfoUsername.setText(userModel.getFullName());
            binding.uInfoUsername.setText(userModel.getFullName());
        }

        if (userModel.getAddress() != null) {
            binding.detailInfoAddress.setText(userModel.getAddress());
            binding.uInfoAddress.setText(userModel.getAddress());
        }
        if (userModel.getEmail() != null) {
            binding.uInfoEmail.setText(userModel.getEmail());
        }

        if (userModel.getAvatarURL() != null) {
            Glide.with(this)
                 .load(Uri.parse(userModel.getAvatarURL()))
                 .centerCrop()
                 .placeholder(R.drawable.avatar_men)
                 .into(binding.profileImage);
        }

        binding.infoFollower.setOnClickListener(v -> {
            replaceScreen(R.id.container, new DetailFollowFragment("Theo dõi", userModel.getFollowers()), null);
        });
        binding.infoFollowing.setOnClickListener(v -> {
            replaceScreen(R.id.container, new DetailFollowFragment("Đang theo dõi", userModel.getFollowing()), null);
        });
    }

    private void showProgressBar() {
        spinKit.setVisibility(View.VISIBLE);
        binding.uInfoScroll.setVisibility(View.GONE);
        Sprite circle = new Circle();
        spinKit.setIndeterminateDrawable(circle);
    }

    private void dismissProgressBar() {
        binding.uInfoScroll.setVisibility(View.VISIBLE);
        spinKit.setVisibility(View.GONE);
    }

    private void replaceScreen(@IdRes int containerViewId, @NonNull Fragment fragment, String backTrackName) {
        FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        fragmentTransaction.replace(containerViewId, fragment);
        fragmentTransaction.addToBackStack(backTrackName);
        // Commit transaction
        fragmentTransaction.commit();
    }

    @NonNull
    @Override
    public CreationExtras getDefaultViewModelCreationExtras() {
        return super.getDefaultViewModelCreationExtras();
    }


}