package com.example.travel_mobile_app.fragments;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.viewmodel.CreationExtras;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;
import android.widget.VideoView;

import com.example.travel_mobile_app.R;
import com.example.travel_mobile_app.models.PostModel;
import com.github.dhaval2404.imagepicker.ImagePicker;
import com.github.ybq.android.spinkit.sprite.Sprite;
import com.github.ybq.android.spinkit.style.WanderingCubes;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import org.apache.commons.lang3.StringUtils;

import java.util.Date;
import java.util.UUID;

public class CreatePostFragment extends Fragment implements View.OnClickListener {


    private MaterialButton btnCamera, btnGallery;
    private ImageView postimg;
    private VideoView videoView;
    private Uri uri = null;
    private FirebaseStorage storage;
    private MaterialButton btnCreatePost;
    private EditText des;
    FirebaseAuth mAuth;
    private FirebaseFirestore db;

    ProgressBar progressBar;
    LinearLayout backdrop;

    public CreatePostFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAuth = FirebaseAuth.getInstance();
        storage = FirebaseStorage.getInstance();
        db = FirebaseFirestore.getInstance();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_create_post, container, false);


        ImageView btnBack = view.findViewById(R.id.createPost_btnBack);
        btnCamera = view.findViewById(R.id.camera);
        btnGallery = view.findViewById(R.id.gallery);
        postimg = view.findViewById(R.id.postimg);
        videoView = view.findViewById(R.id.videoView);
        btnCreatePost = view.findViewById(R.id.btn_create_post);
        des = view.findViewById(R.id.des);
        progressBar = view.findViewById(R.id.spin_kit);
        backdrop = view.findViewById(R.id.backdrop);

        btnBack.setOnClickListener(this);
        btnCamera.setOnClickListener(this);
        btnGallery.setOnClickListener(this);
        btnCreatePost.setOnClickListener(this);

        return view;
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.createPost_btnBack) {
            FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();
            fragmentManager.popBackStack("social_fragment", FragmentManager.POP_BACK_STACK_INCLUSIVE);
        } else if (v.getId() == R.id.camera || v.getId() == R.id.postimg) {
            ImagePicker.with(this)
                       .cameraOnly()
                       .crop()
                       .start();
        } else if (v.getId() == R.id.gallery) {
            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.setType("*/*");
            intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
            startActivityForResult(intent, 1);

        } else if (v.getId() == R.id.btn_create_post) {
            String userId = "8c89d98007c54f34b44f2f619a8684b3";
            final StorageReference reference = storage.getReference().child("posts")
                                                      .child(userId)
                                                      .child(new Date().getTime() + "");
            createNewPost(reference, userId);
        }
    }

    @NonNull
    @Override
    public CreationExtras getDefaultViewModelCreationExtras() {
        return super.getDefaultViewModelCreationExtras();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            uri = data.getData();
            String mimeType = getContext().getContentResolver().getType(uri);

            if (mimeType.contains("jpeg") || mimeType.contains("jpg") || mimeType.contains("png")) {
                postimg.setImageURI(uri);
                postimg.setVisibility(View.VISIBLE);

                videoView.setVisibility(View.GONE);
                videoView.pause();
            } else if (mimeType.contains("mp4")) {

                videoView.setVideoURI(uri);
                videoView.setVisibility(View.VISIBLE);
                videoView.start();

                postimg.setVisibility(View.GONE);
            } else {
                Toast.makeText(getContext(), "Định dạng không được hỗ trợ", Toast.LENGTH_SHORT).show();
            }


        } else if (resultCode == ImagePicker.RESULT_ERROR) {
            Toast.makeText(getContext(), ImagePicker.getError(data), Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(getContext(), "Nhiệm vụ đã được hủy", Toast.LENGTH_SHORT).show();
        }
    }

    private void createNewPost(StorageReference reference, String userId) {
        if (des.getText().toString().trim().equals("") && uri == null) {
            Toast.makeText(getContext(), "Lỗi khi tạo", Toast.LENGTH_SHORT).show();
            return;
        }

        showProgressBar();
        String postId = UUID.randomUUID().toString().replace("-", "");

        PostModel post = new PostModel();
        post.setPostId(postId);
        post.setPostDescription(des.getText().toString().trim());
        post.setPostedBy(userId);
        post.setPostedAt(new Date().getTime());

        CollectionReference posts = db.collection("posts");

        if (uri != null) {
            reference.putFile(uri).addOnSuccessListener(taskSnapshot -> {
                reference.getDownloadUrl().addOnSuccessListener(uri -> {
                    post.setPostImage(uri.toString());
                    saveNewPost(posts, post);
                    dismissProgressBar();
                }).addOnFailureListener(e -> {
                    Toast.makeText(getContext(), "Đã có lỗi khi tạo ảnh", Toast.LENGTH_SHORT).show();
                    dismissProgressBar();
                });
            }).addOnFailureListener(e -> {
                Toast.makeText(getContext(), "Đã có lỗi khi tạo ảnh", Toast.LENGTH_SHORT).show();
                dismissProgressBar();
            });
        } else {
            saveNewPost(posts, post);
        }
    }

    private void saveNewPost(CollectionReference posts, PostModel post) {
        posts.document(post.getPostId()).set(post).addOnSuccessListener(unused -> {
            resetValue();
            Toast.makeText(getContext(), "Tạo thành công", Toast.LENGTH_SHORT).show();
            dismissProgressBar();
        }).addOnFailureListener(e -> {
            Log.e("ERROR", "[ERROR-CREATE-POST]", e);
            Toast.makeText(getContext(), "Đã có lỗi xảy ra.", Toast.LENGTH_SHORT).show();
            dismissProgressBar();
        });
    }

    private void resetValue() {
        uri = null;
        des.setText("");
        videoView.setVideoURI(null);
        postimg.setImageURI(null);
    }

    private void showProgressBar() {
        backdrop.setVisibility(View.VISIBLE);

        Sprite wanderingCubes = new WanderingCubes();
        progressBar.setIndeterminateDrawable(wanderingCubes);
    }

    private void dismissProgressBar() {
        backdrop.setVisibility(View.GONE);
    }
}