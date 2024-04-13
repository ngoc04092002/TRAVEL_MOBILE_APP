package com.example.travel_mobile_app.fragments;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.IdRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.viewmodel.CreationExtras;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.travel_mobile_app.Adapter.PostAdapter;
import com.example.travel_mobile_app.Adapter.StoryAdapter;
import com.example.travel_mobile_app.MainActivity;
import com.example.travel_mobile_app.R;
import com.example.travel_mobile_app.SocialSearchPost;
import com.example.travel_mobile_app.models.PostModel;
import com.example.travel_mobile_app.models.StoryModel;
import com.example.travel_mobile_app.models.UserStory;
import com.facebook.shimmer.ShimmerFrameLayout;
import com.github.dhaval2404.imagepicker.ImagePicker;
import com.github.ybq.android.spinkit.sprite.Sprite;
import com.github.ybq.android.spinkit.style.WanderingCubes;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;


public class SocialFragment extends Fragment implements View.OnClickListener {

    private RecyclerView storyRv, dashboardRv;
    private ArrayList<StoryModel> list;
    private ArrayList<PostModel> postList;
    private ImageButton btnFriends, btnAdd, btnSearch;
    private FirebaseFirestore db;
    private FirebaseStorage storage;
    private ShimmerFrameLayout shimmerFrameLayout, shimmerFrameLayoutStory;
    private FrameLayout createStory;
    private LinearLayout backdrop;
    private ProgressBar progressBar;

    public SocialFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        db = FirebaseFirestore.getInstance();
        storage = FirebaseStorage.getInstance();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_social, container, false);
        shimmerFrameLayout = view.findViewById(R.id.shimmer_layout);
        shimmerFrameLayoutStory = view.findViewById(R.id.shimmer_layout_story);
        shimmerFrameLayout.setVisibility(View.VISIBLE);
        shimmerFrameLayout.startShimmer();
        shimmerFrameLayoutStory.setVisibility(View.VISIBLE);
        shimmerFrameLayoutStory.startShimmer();

        progressBar = view.findViewById(R.id.spin_kit);
        backdrop = view.findViewById(R.id.backdrop);

        storyRv = view.findViewById(R.id.storyRv);
        list = new ArrayList<>();
        StoryAdapter storyAdapter = new StoryAdapter(list, getContext());
        setStoryListData(storyAdapter);
        storyRv.setHasFixedSize(true);
        storyRv.setLayoutManager(new StaggeredGridLayoutManager(1, LinearLayoutManager.HORIZONTAL));
        storyRv.setAdapter(storyAdapter);


        dashboardRv = view.findViewById(R.id.dashboardRv);
        postList = new ArrayList<>();
        PostAdapter postAdapter = new PostAdapter(postList, getContext(), requireActivity().getSupportFragmentManager(), getActivity(), db);
        setPostListData(postAdapter);
        dashboardRv.setHasFixedSize(true);
        dashboardRv.setLayoutManager(new StaggeredGridLayoutManager(1, LinearLayoutManager.VERTICAL));
        dashboardRv.setAdapter(postAdapter);

        btnFriends = view.findViewById(R.id.friends);
        btnFriends.setOnClickListener(this);
        btnAdd = view.findViewById(R.id.addButton);
        btnAdd.setOnClickListener(this);
        btnSearch = view.findViewById(R.id.btnSearch);
        btnSearch.setOnClickListener(this);
        createStory = view.findViewById(R.id.createStory);
        createStory.setOnClickListener(this);

        return view;
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.friends) {
            replaceScreen(R.id.container, new FriendsFragment(), "social_fragment");
        } else if (v.getId() == R.id.addButton) {
            replaceScreen(R.id.container, new CreatePostFragment(), "social_fragment");
        } else if (v.getId() == R.id.btnSearch) {
            Intent i = new Intent(getActivity(), SocialSearchPost.class);
            startActivity(i);
            getActivity().overridePendingTransition(0, 0);
        } else if (v.getId() == R.id.createStory) {
            ImagePicker.with(this)
                       .galleryOnly()
                       .crop()
                       .start();
        }

    }

    private void replaceScreen(@IdRes int containerViewId, @NonNull Fragment fragment, String backTrackName) {
        FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        fragmentTransaction.replace(containerViewId, fragment);
        // Thêm transaction vào back stack (nếu cần)
        fragmentTransaction.addToBackStack(backTrackName);
        // Commit transaction
        fragmentTransaction.commit();
    }

    private void setPostListData(PostAdapter postAdapter) {
        db.collection("posts")
          .get()
          .addOnCompleteListener(task -> {
              if (task.isSuccessful()) {
                  for (QueryDocumentSnapshot document : task.getResult()) {
                      PostModel postModel = document.toObject(PostModel.class);
                      postList.add(postModel);
                  }
                  shimmerFrameLayout.showShimmer(false);
                  shimmerFrameLayout.setVisibility(View.GONE);
                  postAdapter.notifyDataSetChanged();
              } else {
                  Log.d("record", "Error getting documents: ", task.getException());
              }
          });
    }

    private void setStoryListData(StoryAdapter storyAdapter) {
        //fix 8c89d98007c54f34b44f2f619a8684b3 is userID and add filter table user
        db.collection("stories")
          .whereEqualTo("storyBy", "8c89d98007c54f34b44f2f619a8684b3")
          .get()
          .addOnCompleteListener(task -> {
              if (task.isSuccessful() && task.getResult() != null) {
                  ArrayList<UserStory> userStories = new ArrayList<>();
                  for (QueryDocumentSnapshot document : task.getResult()) {
                      Date currentTime = new Date();
                      StoryModel storyModel = document.toObject(StoryModel.class);

                      if (currentTime.getTime() - storyModel.getStoryAt() < (24 * 60 * 60 * 1000)) {
                          userStories.add(new UserStory(storyModel.getUri(), storyModel.getStoryAt()));
                      }
                  }
                  if(userStories.size()>0){
                      StoryModel storyModel = new StoryModel();
                      storyModel.setUserStories(userStories);
                      storyModel.setStoryBy("8c89d98007c54f34b44f2f619a8684b3");
                      list.add(storyModel);
                  }

                  shimmerFrameLayoutStory.showShimmer(false);
                  shimmerFrameLayoutStory.setVisibility(View.GONE);
                  storyAdapter.notifyDataSetChanged();
              } else {
                  Log.d("record", "Error getting documents: ", task.getException());
              }
          });
    }

    private void uploadImages(Uri imageUri, String userId) {
        final StorageReference reference = storage.getReference().child("stories")
                                                  .child(userId)
                                                  .child(new Date().getTime() + "");

        reference.putFile(imageUri).addOnSuccessListener(taskSnapshot -> {
            reference.getDownloadUrl().addOnSuccessListener(uri -> {
                StoryModel story = new StoryModel();
                story.setUri(uri.toString());
                saveNewStory(userId, story);
            }).addOnFailureListener(e -> {
                Toast.makeText(getContext(), "Đã có lỗi khi tạo ảnh", Toast.LENGTH_SHORT).show();
                dismissProgressBar();
            });
        }).addOnFailureListener(e -> {
            Toast.makeText(getContext(), "Đã có lỗi khi tạo ảnh", Toast.LENGTH_SHORT).show();
            dismissProgressBar();
        });
    }

    private void saveNewStory(String userId, StoryModel story) {
        CollectionReference stories = db.collection("stories");
        String storyId = UUID.randomUUID().toString().replace("-", "");

        story.setStoryId(storyId);
        story.setStoryBy("8c89d98007c54f34b44f2f619a8684b3"); // get name and set fix
        story.setStoryAt(new Date().getTime());

        stories.document(storyId).set(story).addOnSuccessListener(unused -> {
            Toast.makeText(getContext(), "Tạo thành công", Toast.LENGTH_SHORT).show();
            dismissProgressBar();
        }).addOnFailureListener(e -> {
            Log.e("ERROR", "[ERROR-CREATE-POST]", e);
            Toast.makeText(getContext(), "Đã có lỗi xảy ra.", Toast.LENGTH_SHORT).show();
            dismissProgressBar();
        });
    }

    private void showProgressBar() {
        backdrop.setVisibility(View.VISIBLE);

        Sprite wanderingCubes = new WanderingCubes();
        progressBar.setIndeterminateDrawable(wanderingCubes);
    }

    private void dismissProgressBar() {
        backdrop.setVisibility(View.GONE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            Uri uri = data.getData();
            showProgressBar();
            String userId = UUID.randomUUID().toString().replace("-", "");
            uploadImages(uri, userId);
        } else {
            Toast.makeText(getContext(), "Nhiệm vụ đã được hủy", Toast.LENGTH_SHORT).show();
        }

    }

    @NonNull
    @Override
    public CreationExtras getDefaultViewModelCreationExtras() {
        return super.getDefaultViewModelCreationExtras();
    }

}