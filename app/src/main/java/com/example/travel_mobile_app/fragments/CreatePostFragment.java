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

import android.provider.MediaStore;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;
import android.widget.VideoView;

import com.example.travel_mobile_app.R;
import com.github.dhaval2404.imagepicker.ImagePicker;
import com.google.android.material.button.MaterialButton;

public class CreatePostFragment extends Fragment implements View.OnClickListener {


    private MaterialButton btnCamera, btnGallery;
    private ImageView postimg;
    private VideoView videoView;

    public CreatePostFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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
        videoView=view.findViewById(R.id.videoView);

        btnBack.setOnClickListener(this);
        btnCamera.setOnClickListener(this);
        btnGallery.setOnClickListener(this);

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
            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(intent,1);
//            ImagePicker.with(this)
//                       .galleryOnly()
//                       .crop()
//                       .start();
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
            Uri uri = data.getData();
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
}