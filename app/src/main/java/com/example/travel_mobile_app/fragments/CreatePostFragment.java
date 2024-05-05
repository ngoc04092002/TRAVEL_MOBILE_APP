package com.example.travel_mobile_app.fragments;

import android.app.Activity;
import android.app.Dialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.viewmodel.CreationExtras;

import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.MediaController;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Toast;
import android.widget.VideoView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.example.travel_mobile_app.R;
import com.example.travel_mobile_app.models.PostModel;
import com.example.travel_mobile_app.models.UserModel;
import com.example.travel_mobile_app.services.SharedPreferencesManager;
import com.github.dhaval2404.imagepicker.ImagePicker;
import com.github.ybq.android.spinkit.sprite.Sprite;
import com.github.ybq.android.spinkit.style.WanderingCubes;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.MediaItem;
import com.google.android.exoplayer2.ui.StyledPlayerView;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import org.apache.commons.lang3.StringUtils;

import java.io.FileNotFoundException;
import java.io.InputStream;
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
    private FirebaseFirestore db;
    StorageReference reference;
    ImageView btnRefresh;
    private String postId;
    private String oldUrl;
    private Dialog pd;

    public CreatePostFragment() {
        // Required empty public constructor
    }

    public CreatePostFragment(String postId) {
        this.postId = postId;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        storage = FirebaseStorage.getInstance();
        db = FirebaseFirestore.getInstance();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_create_post, container, false);


        ImageView btnBack = view.findViewById(R.id.createPost_btnBack);
        btnRefresh = view.findViewById(R.id.createPost_btnRefresh);
        btnCamera = view.findViewById(R.id.camera);
        btnGallery = view.findViewById(R.id.gallery);
        postimg = view.findViewById(R.id.postimg);
        videoView = view.findViewById(R.id.videoView);
        btnCreatePost = view.findViewById(R.id.btn_create_post);
        des = view.findViewById(R.id.des);

        btnBack.setOnClickListener(this);
        btnRefresh.setOnClickListener(this);
        btnCamera.setOnClickListener(this);
        btnGallery.setOnClickListener(this);
        btnCreatePost.setOnClickListener(this);
        videoView.setOnClickListener(this);
        postimg.setOnClickListener(this);

        if (this.postId != null) {
            fetchPreData();
        }
        return view;
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.createPost_btnBack) {
            FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();
            fragmentManager.popBackStack("social_fragment", FragmentManager.POP_BACK_STACK_INCLUSIVE);
        } else if (v.getId() == R.id.camera || (v.getId() == R.id.postimg && uri == null)) {
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
            UserModel user = SharedPreferencesManager.readUserInfo();
            if (uri != null) {
                String mimeType = requireContext().getContentResolver().getType(uri);

                if (mimeType != null && mimeType.contains("mp4")) {
                    reference = storage.getReference().child("posts")
                                       .child("video")
                                       .child(user.getId())
                                       .child(new Date().getTime() + "");
                } else {
                    reference = storage.getReference().child("posts")
                                       .child(user.getId())
                                       .child(new Date().getTime() + "");
                }
            }

            createNewPost(reference, user);
        } else if (v.getId() == R.id.videoView) {
            showCenterDialog();
        } else if (v.getId() == R.id.postimg) {
            showCenterDialog(postimg.getDrawable());
        } else if (v.getId() == R.id.createPost_btnRefresh) {
            resetValue();
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
            String mimeType = requireContext().getContentResolver().getType(uri);

            if (mimeType == null) {
                mimeType = uri.toString();
            }

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

    private void fetchPreData() {
        CollectionReference posts = db.collection("posts");
        posts.document(this.postId)
             .get()
             .addOnCompleteListener(task -> {
                 if (task.isSuccessful() && task.getResult() != null) {
                     PostModel postModel = task.getResult().toObject(PostModel.class);
                     des.setText(postModel.getPostDescription());
                     String url = postModel.getPostImage();
                     boolean isVideo = url != null && url.contains("video");
                     oldUrl = url;
                     if (url != null) {
                         uri = Uri.parse(url);
                         Glide.with(this)
                              .load(uri)
                              .centerCrop()
                              .placeholder(R.drawable.image_empty)
                              .into(postimg);
                     }

                     if (isVideo) {
                         videoView.setVisibility(View.VISIBLE);
                         postimg.setVisibility(View.GONE);
                         videoView.start();
                     } else {
                         postimg.setVisibility(View.VISIBLE);
                         videoView.setVisibility(View.GONE);
                         videoView.pause();
                     }

                 }
             }).addOnFailureListener(e -> {
                 System.out.println("ERROR-EDIT-POST::" + e.getMessage());
             });
    }

    private void createNewPost(StorageReference reference, UserModel user) {
        if (des.getText().toString().trim().equals("") && uri == null) {
            Toast.makeText(getContext(), "Lỗi khi tạo", Toast.LENGTH_SHORT).show();
            return;
        }

        showProgressBar();
        String postId = UUID.randomUUID().toString().replace("-", "");

        PostModel post = new PostModel();
        if (this.postId != null) {
            post.setPostId(this.postId); //for edit
            post.setPostImage(uri.toString());
        } else {
            post.setPostId(postId); //for create new
        }
        post.setPostDescription(des.getText().toString().trim());
        post.setPostedBy(user.getId());
        post.setPostedAt(new Date().getTime());
        post.setFullname(user.getFullName());

        CollectionReference posts = db.collection("posts");

        if (uri != null && !uri.toString().contains("http")) {
            //remove old image
            if (oldUrl != null && !oldUrl.equals("")) {
                try {
                    StorageReference fileRef = storage.getReferenceFromUrl(oldUrl);
                    fileRef.delete()
                           .addOnSuccessListener(aVoid -> Log.d("REMOVE-SUCCESS", "Xóa tệp tin thành công"))
                           .addOnFailureListener(exception -> Log.d("REMOVE-FAILURE", "Xóa tệp tin thất bại: " + exception.getMessage()));
                } catch (Exception e) {
                }
            }

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

    ExoPlayer exoPlayer;

    private void showCenterDialog() {
        final Dialog dialog = new Dialog(requireActivity());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.centersheet_video);

        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().setGravity(Gravity.BOTTOM);

        StyledPlayerView playerView = dialog.findViewById(R.id.videoView_dialog);
        exoPlayer = new ExoPlayer.Builder(dialog.getContext()).build();
        playerView.setPlayer(exoPlayer);

        MediaItem mediaItem = MediaItem.fromUri(uri);
        exoPlayer.setMediaItem(mediaItem);
        exoPlayer.prepare();
        exoPlayer.setPlayWhenReady(true);

        ImageButton btnCloseDialog = dialog.findViewById(R.id.close_sheet);
        btnCloseDialog.setOnClickListener(v -> {
            exoPlayer.setPlayWhenReady(false);
            exoPlayer.release();
            exoPlayer = null;
            dialog.dismiss();
        });

        dialog.show();
    }


    private ImageView imageView;
    private float x, y, dx, dy, initX, initY, limitCoordinatesY1, limitCoordinatesY2, limitCoordinatesX;

    //hieenr thị ảnh lên toàn màn hình khi click vào ảnh bài post
    private void showCenterDialog(Drawable drawable) {
        final Dialog dialog = new Dialog(requireActivity());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.centersheet_image);

        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().setGravity(Gravity.BOTTOM);

        imageView = dialog.findViewById(R.id.postimg);
        imageView.setImageDrawable(drawable);
        handleOnTouchImageOfPost(dialog, imageView);

        ImageButton btnCloseDialog = dialog.findViewById(R.id.btnClose_dialog);
        btnCloseDialog.setOnClickListener(v -> {
            dialog.dismiss();
        });

        dialog.show();
    }


    private void handleOnTouchImageOfPost(Dialog dialog, ImageView imageView) {
        // Đặt độ mờ ban đầu là 0
        WindowManager.LayoutParams layoutParams = dialog.getWindow().getAttributes();
        layoutParams.dimAmount = 1;
        dialog.getWindow().setAttributes(layoutParams);


        imageView.setOnTouchListener((v, event) -> {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    x = event.getRawX();
                    y = event.getRawY();
                    initX = imageView.getX();
                    initY = imageView.getY();
                    limitCoordinatesY1 = -imageView.getHeight() / 2;
                    limitCoordinatesY2 = initY + imageView.getHeight() / 2;
                    limitCoordinatesX = imageView.getWidth() / 2;
                    break;
                case MotionEvent.ACTION_MOVE:
                    dx = event.getRawX() - x;
                    dy = event.getRawY() - y;

                    float edgeX = imageView.getX() + dx;
                    float edgeY = imageView.getY() + dy;
                    float acreage1 = (Math.abs(edgeY) - initY + (imageView.getHeight() / 2)) * (Math.abs(edgeX) + (imageView.getWidth() / 2));
                    float acreage2 = limitCoordinatesX * 2 * (limitCoordinatesY2 - limitCoordinatesY1);
                    // Áp dụng độ mờ cho dialog
                    layoutParams.dimAmount = (acreage2 - acreage1) / acreage2;
                    dialog.getWindow().setAttributes(layoutParams);

                    imageView.setX(edgeX);
                    imageView.setY(edgeY);
                    x = event.getRawX();
                    y = event.getRawY();
                    break;
                case MotionEvent.ACTION_UP:

                    if (Math.abs(imageView.getX()) >= limitCoordinatesX || imageView.getY() >= limitCoordinatesY2 || imageView.getY() <= limitCoordinatesY1) {
                        dialog.dismiss();
                    } else {
                        imageView.setX(initX);
                        imageView.setY(initY);
                        layoutParams.dimAmount = 1;
                        dialog.getWindow().setAttributes(layoutParams);
                    }
                    break;
            }
            return true;
        });

    }


    private void resetValue() {
        uri = null;
        des.setText("");
        videoView.setVideoURI(null);
        videoView.setVisibility(View.GONE);
        postimg.setVisibility(View.VISIBLE);
        postimg.setImageURI(null);
        postimg.setImageResource(R.drawable.image_empty);
    }

    private void showProgressBar() {
        pd = new Dialog(getActivity(), android.R.style.Theme_Black);
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.progress_bar_loading, null);
        pd.requestWindowFeature(Window.FEATURE_NO_TITLE);
        pd.getWindow().setBackgroundDrawableResource(R.color.transparent);
        pd.setContentView(view);

        Sprite wanderingCubes = new WanderingCubes();
        ((ProgressBar) pd.findViewById(R.id.spin_kit)).setIndeterminateDrawable(wanderingCubes);
        pd.show();
    }

    private void dismissProgressBar() {
        pd.dismiss();
    }
}