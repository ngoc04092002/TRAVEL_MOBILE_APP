package com.example.travel_mobile_app.fragments;

import android.annotation.SuppressLint;
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
import com.example.travel_mobile_app.R;
import com.example.travel_mobile_app.models.UserModel;
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
    private FirebaseAuth mAuth;
    private FirebaseFirestore firestore = FirebaseFirestore.getInstance();

    public AccountFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        View view = LayoutInflater.from(getContext()).inflate(R.layout.fragment_account, null);
        mAuth = FirebaseAuth.getInstance();


//        FirebaseUser user = mAuth.getCurrentUser();
//        getUserDataById(user.getUid());
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

        progressBar = view.findViewById(R.id.progress_bar);
        progressBar.setVisibility(View.VISIBLE);

        imvAvatar = view.findViewById(R.id.profile_image);
        imvAvatar.setOnClickListener(this);

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

        FirebaseUser user = mAuth.getCurrentUser();
        getUserDataById(user.getUid());
        return view;
    }
    @Override
    public void onClick(View v) {
        FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        if(v.getId()==R.id.btnSaved){
            fragmentTransaction.replace(R.id.container, new ProfileSaveFragment());
        }
        else if(v.getId()==R.id.btnSetting){
            fragmentTransaction.replace(R.id.container, new SettingFragment(currentUser));
        }
        else if(v.getId()==R.id.btnManagePost){
            fragmentTransaction.replace(R.id.container, new SocialUserDetailInfoFragment());
        }
        else if(v.getId()==R.id.btnLogout){
//            fragmentTransaction.replace(R.id.container, new SettingFragment());
        } else if(v.getId() == R.id.profile_image) {
            getContent.launch("image/*");
        }

        // Thêm transaction vào back stack (nếu cần)
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
        Glide.with(requireContext()).load(user.getAvatarURL()).into(imvAvatar);
    }
    private void getUserDataById(String userId) {
        DocumentReference docRef = firestore.collection("users").document(userId);
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(Task<DocumentSnapshot> task) {
                progressBar.setVisibility(View.GONE);

                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        // Lấy dữ liệu từ Firestore
                        UserModel userModel = document.toObject(UserModel.class);

//                        String name = document.getString("fullName");
//                        String username = document.getString("username");
//                        String email = document.getString("email");
//                        String address = document.getString("address");
//                        String password = document.getString("password");
//                        String avataURL = document.getString("avatarURL");
//                        String[] following = document.get("following");
//                        String[] followers = document.getLong("followers");

                        // Tạo một đối tượng UserModel
//                        UserModel userModel = new UserModel(userId, name, username, email, address, password, avataURL, (int) followers, (int) following);
                        currentUser = userModel;
                        assert userModel != null;
                        updateUI(userModel);
                    } else {
                        Log.e(TAG, "Document userid not exist!");
                    }
                } else {
                    Log.e(TAG, "Get User Info Error!");
                }
            }
        });
    }

    private static final int PICK_IMAGE_REQUEST = 1;

    private final ActivityResultLauncher<String> getContent = registerForActivityResult(
            new ActivityResultContracts.GetContent(),
            result -> {
                if (result != null) {
                    // Cập nhật ImageView với ảnh đã chọn
//                    imvAvatar.setImageURI(result);
                    progressBar.setVisibility(View.VISIBLE);
                    uploadImageToFirebaseStorage(result);

                }
            });

    // Assuming you have initialized FirebaseApp and FirebaseAuth

    // Step 1: Upload Image to Firebase Storage
    private void uploadImageToFirebaseStorage(Uri imageUri) {
        StorageReference storageRef = FirebaseStorage.getInstance().getReference();
        StorageReference imageRef = storageRef.child("avatars/" + FirebaseAuth.getInstance().getCurrentUser().getUid());

        imageRef.putFile(imageUri)
                .addOnSuccessListener(taskSnapshot -> {
                    // Image uploaded successfully, now get the download URL
                    imageRef.getDownloadUrl().addOnSuccessListener(uri -> {
                        String downloadUrl = uri.toString();
                        // Step 2: Update User Document in Firestore with download URL
                        updateAvatar(downloadUrl, imageUri);
                    });
                })
                .addOnFailureListener(exception -> {
                    // Handle unsuccessful uploads
                    Log.e("TAG", "Upload failed: " + exception.getMessage());
                    Toast.makeText(getContext(), "Upload failed: " + exception.getMessage(), Toast.LENGTH_SHORT).show();
                    progressBar.setVisibility(View.GONE);
                });
    }

    // Step 2: Update User Document in Firestore
    private void updateAvatar(String downloadUrl, Uri imageUri) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference userRef = db.collection("users").document(currentUser.getId());

        userRef.update("avatarURL", downloadUrl)
                .addOnSuccessListener(aVoid -> {
                    // Avatar URL updated successfully
                    Log.d("TAG", "Avatar URL updated successfully");
                    Toast.makeText(getContext(), "Avatar URL updated successfully", Toast.LENGTH_SHORT).show();
                    imvAvatar.setImageURI(imageUri);
                    progressBar.setVisibility(View.GONE);
                })
                .addOnFailureListener(e -> {
                    // Handle errors
                    Log.e("TAG", "Error updating avatar URL", e);
                    Toast.makeText(getContext(), "Error updating avatar URL", Toast.LENGTH_SHORT).show();
                    progressBar.setVisibility(View.GONE);
                });
    }
}