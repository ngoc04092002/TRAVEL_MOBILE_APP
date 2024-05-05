package com.example.travel_mobile_app.fragments;

import android.annotation.SuppressLint;
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
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.travel_mobile_app.R;
import com.example.travel_mobile_app.models.UserModel;
import com.example.travel_mobile_app.services.SharedPreferencesManager;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class EditInfoFragment extends Fragment implements View.OnClickListener {


    private EditText editUsername, editName, editEmail, editAddress;
    private CircleImageView avataImageView;
    private TextView btnUpdate;
    private UserModel currentUser;
    private ProgressBar progressBar;
    private FirebaseFirestore firestore = FirebaseFirestore.getInstance();
    private Uri newUri;

    public EditInfoFragment(UserModel user) {
        this.currentUser = user;
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
        progressBar = view.findViewById(R.id.progress_bar);
        progressBar.setVisibility(View.GONE);

        avataImageView = view.findViewById(R.id.imv_avatar);
        avataImageView.setOnClickListener(this);

        editUsername = view.findViewById(R.id.editUsername);
        editUsername.setSingleLine();

        editAddress = view.findViewById(R.id.editAddress);
        editAddress.setSingleLine();

        editName = view.findViewById(R.id.editName);
        editName.setSingleLine();

        editEmail = view.findViewById(R.id.editEmail);
        editEmail.setSingleLine();
        editEmail.setFocusable(false);
        editEmail.setOnClickListener(this);

        btnUpdate= view.findViewById(R.id.btnUpdate);
        btnUpdate.setOnClickListener(this);

        ImageView btnBack = view.findViewById(R.id.createPost_btnBack);
        btnBack.setOnClickListener(this);

        setUpUI();
        return view;
    }

    private void setUpUI() {
        editUsername.setText(currentUser.getUsername());
        editAddress.setText(currentUser.getAddress());
        editName.setText(currentUser.getFullName());
        editEmail.setText(currentUser.getEmail());

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

        if(v.getId()==R.id.imv_avatar){
            getContent.launch("image/*");
        }
        else if(v.getId()==R.id.btnUpdate){
            String newUsername = String.valueOf(editUsername.getText());
            String newName = String.valueOf(editName.getText());
            String newAddress = String.valueOf(editAddress.getText());
            currentUser.setUsername(newUsername);
            currentUser.setAddress(newAddress);
            currentUser.setFullName(newName);
            progressBar.setVisibility(View.VISIBLE);
            if (newUri != null) {
                uploadImageToFirebaseStorage(newUri);
            } else {
                updateUserInfo();
            }
        } else if (v.getId() == R.id.editEmail) {
            Toast.makeText(getContext(), "Không thể chỉnh sửa email Email!", Toast.LENGTH_SHORT).show();
        }
        if(v.getId()==R.id.createPost_btnBack){
            fragmentManager.popBackStack("setting_fragment", FragmentManager.POP_BACK_STACK_INCLUSIVE);
        }
        fragmentTransaction.commit();
    }

    private static final int PICK_IMAGE_REQUEST = 1;

    private final ActivityResultLauncher<String> getContent = registerForActivityResult(
            new ActivityResultContracts.GetContent(),
            result -> {
                if (result != null) {

                    newUri = result;
                    Glide.with(requireContext())
                            .load(result)
                            .into(avataImageView);
//                    avataImageView.setImageURI(result);
//                    progressBar.setVisibility(View.VISIBLE);
//                    uploadImageToFirebaseStorage(result);

                }
            });

    private void uploadImageToFirebaseStorage(Uri imageUri) {
        StorageReference storageRef = FirebaseStorage.getInstance().getReference();
        StorageReference imageRef = storageRef.child("avatars/" + FirebaseAuth.getInstance().getCurrentUser().getUid());

        imageRef.putFile(imageUri)
                .addOnSuccessListener(taskSnapshot -> {
                    // Image uploaded successfully, now get the download URL
                    imageRef.getDownloadUrl().addOnSuccessListener(uri -> {
                        String downloadUrl = uri.toString();
                        // Step 2: Update User Document in Firestore with download URL
                        currentUser.setAvatarURL(downloadUrl);
                        updateUserInfo();
                    });
                })
                .addOnFailureListener(exception -> {
                    // Handle unsuccessful uploads
                    Log.e("TAG", "Upload failed: " + exception.getMessage());
                    Toast.makeText(getContext(), "Upload failed: " + exception.getMessage(), Toast.LENGTH_SHORT).show();
                    progressBar.setVisibility(View.GONE);
                });
    }

    private void updateUserInfo() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference userRef = db.collection("users").document(currentUser.getId());

        Map<String, Object> updates = new HashMap<>();
        updates.put("username", currentUser.getUsername());
        updates.put("fullName", currentUser.getFullName());
        updates.put("address", currentUser.getAddress());
        updates.put("avatarURL", currentUser.getAvatarURL());

        userRef.update(updates)
                .addOnSuccessListener(aVoid -> {
                    // Avatar URL updated successfully
                    Log.d("TAG", "Avatar URL updated successfully");
                    Toast.makeText(getContext(), "Cập nhật thông tin thành công!", Toast.LENGTH_SHORT).show();
                    progressBar.setVisibility(View.GONE);
                    SharedPreferencesManager.writeUserInfo(currentUser);
                    FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();
                    fragmentManager.popBackStack("account_fragment", FragmentManager.POP_BACK_STACK_INCLUSIVE);
                })
                .addOnFailureListener(e -> {
                    // Handle errors
                    Log.e("TAG", "Error updating avatar URL", e);
                    Toast.makeText(getContext(), "Error updating", Toast.LENGTH_SHORT).show();
                    progressBar.setVisibility(View.GONE);
                });
    }

}