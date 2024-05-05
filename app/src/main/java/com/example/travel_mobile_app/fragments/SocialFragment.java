package com.example.travel_mobile_app.fragments;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.IdRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.viewmodel.CreationExtras;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.travel_mobile_app.Adapter.PostAdapter;
import com.example.travel_mobile_app.Adapter.StoryAdapter;
import com.example.travel_mobile_app.R;
import com.example.travel_mobile_app.SocialSearchPost;
import com.example.travel_mobile_app.models.PostModel;
import com.example.travel_mobile_app.models.StoryModel;
import com.example.travel_mobile_app.models.UserModel;
import com.example.travel_mobile_app.models.UserStory;
import com.example.travel_mobile_app.services.SharedPreferencesManager;
import com.facebook.shimmer.ShimmerFrameLayout;
import com.github.dhaval2404.imagepicker.ImagePicker;
import com.github.ybq.android.spinkit.sprite.Sprite;
import com.github.ybq.android.spinkit.style.Circle;
import com.github.ybq.android.spinkit.style.WanderingCubes;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;


public class SocialFragment extends Fragment implements View.OnClickListener {

    private final static int A_DAY = 24 * 60 * 60 * 1000;
    private RecyclerView storyRv, dashboardRv;
    private ArrayList<StoryModel> list;
    private ArrayList<PostModel> postList;
    private ImageButton btnFriends, btnAdd, btnSearch;
    private FirebaseFirestore db;
    private FirebaseStorage storage;
    private ShimmerFrameLayout shimmerFrameLayout, shimmerFrameLayoutStory;
    private FrameLayout createStory;
    private ProgressBar progressBarLoadMore;
    private StoryAdapter storyAdapter;
    SwipeRefreshLayout mSwipeRefreshLayout;
    private PostAdapter postAdapter;
    boolean isLoading = false;
    private Dialog pd;
    NestedScrollView nestedScrollView;
    private boolean isLoadMore = false;

    public SocialFragment() {
        // Required empty public constructor
    }

    public SocialFragment(ArrayList<PostModel> postList) {
        this.postList = postList;
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

        mSwipeRefreshLayout = view.findViewById(R.id.fragment_social);
        mSwipeRefreshLayout.setColorSchemeResources(R.color.colorPrimary,
                                                    android.R.color.holo_green_dark,
                                                    android.R.color.holo_orange_dark,
                                                    android.R.color.holo_blue_dark);


        progressBarLoadMore = view.findViewById(R.id.spin_kit_load_more);
        nestedScrollView = view.findViewById(R.id.NestedScrollView);

        storyRv = view.findViewById(R.id.storyRv);
        list = new ArrayList<>();
        storyAdapter = new StoryAdapter(list, getContext());
        storyRv.setHasFixedSize(true);
        storyRv.setLayoutManager(new StaggeredGridLayoutManager(1, LinearLayoutManager.HORIZONTAL));

        dashboardRv = view.findViewById(R.id.dashboardRv);
        postAdapter = new PostAdapter(postList, getContext(), requireActivity().getSupportFragmentManager(), getActivity(), db);
        dashboardRv.setHasFixedSize(true);
        dashboardRv.setLayoutManager(new StaggeredGridLayoutManager(1, LinearLayoutManager.VERTICAL));


        if (postList.size() != 0) {
            postAdapter = new PostAdapter(postList, getContext(), requireActivity().getSupportFragmentManager(), getActivity(), db);
        } else {
            loadRecyclerViewData(0L);
        }
        initScrollListener();

        shimmerFrameLayoutStory.setVisibility(View.VISIBLE);
        shimmerFrameLayoutStory.startShimmer();
        setStoryListData(storyAdapter);


        storyRv.setAdapter(storyAdapter);
        dashboardRv.setAdapter(postAdapter);

        mSwipeRefreshLayout.setOnRefreshListener(() -> {
            mSwipeRefreshLayout.setRefreshing(false);
            loadRecyclerViewData(0L);
        });

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
            getActivity().overridePendingTransition(0, android.R.anim.slide_out_right);
        } else if (v.getId() == R.id.createStory) {
            ImagePicker.with(this)
                       .galleryOnly()
                       .crop()
                       .start();
        }

    }


    private void initScrollListener() {
        nestedScrollView.setOnScrollChangeListener((NestedScrollView.OnScrollChangeListener) (v, scrollX, scrollY, oldScrollX, oldScrollY) -> {
            if (scrollY == v.getChildAt(0).getMeasuredHeight() - v.getMeasuredHeight()) {
                if (!isLoadMore) {
                    isLoadMore = true;
                    loadMore();
                }
            }
        });
    }

    private void loadMore() {
        showProgressBarLoadMore();
        Handler handler = new Handler();
        handler.postDelayed(() -> {
            long startAt = 0;
            if (postList.size() != 0) {
                startAt = postList.get(0).getPostedAt();
            }
            setPostListData(postAdapter, startAt);

            isLoading = false;
        }, 300);


    }

    private void loadRecyclerViewData(Long startAt) {
        shimmerFrameLayout.setVisibility(View.VISIBLE);
        shimmerFrameLayout.startShimmer();
        setPostListData(postAdapter, startAt);
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

    private void setPostListData(PostAdapter postAdapter, Long startAt) {
        CollectionReference postsRef = db.collection("posts");

        UserModel user = SharedPreferencesManager.readUserInfo();
        List<String> following = user.getFollowing();

        if (startAt == 0L) {
            postList.clear();
        }

        postsRef
//                .whereIn("postedBy", following)
.orderBy("postedAt", Query.Direction.DESCENDING)
.whereGreaterThan("postedAt", startAt)
.limit(30)
.get()
.addOnCompleteListener(task -> {
    if (task.isSuccessful()) {
        for (QueryDocumentSnapshot document : task.getResult()) {
            PostModel postModel = document.toObject(PostModel.class);
            postList.add(postModel);
        }

        shimmerFrameLayout.showShimmer(false);
        shimmerFrameLayout.setVisibility(View.GONE);
        dismissProgressBarLoadMore();
        isLoadMore = false;
        postAdapter.notifyDataSetChanged();
    } else {
        Log.d("record", "Error getting documents: ", task.getException());
    }
});
    }

    private void setStoryListData(StoryAdapter storyAdapter) {
        UserModel user = SharedPreferencesManager.readUserInfo();
        CollectionReference storiesRef = db.collection("stories");
        CollectionReference usersRef = db.collection("users");


        usersRef
//                .whereIn("id", user.getFollowing())
.get()
.addOnCompleteListener(task -> {
    if (task.isSuccessful() && task.getResult() != null) {
        for (QueryDocumentSnapshot document : task.getResult()) {
            UserModel userModel = document.toObject(UserModel.class);
            if (userModel.getId() != null) {
                getAllUserStory(userModel, storiesRef);
            }
        }

    }
});

    }

    private void getAllUserStory(UserModel user, CollectionReference storiesRef) {
        storiesRef
                .whereEqualTo("storyBy", user.getId())
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult() != null) {
                        ArrayList<UserStory> userStories = new ArrayList<>();
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            Date currentTime = new Date();
                            StoryModel storyModel = document.toObject(StoryModel.class);

                            if (currentTime.getTime() - storyModel.getStoryAt() < A_DAY) {
                                userStories.add(new UserStory(storyModel.getUri(), storyModel.getStoryAt()));
                            }
                        }
                        List<DocumentSnapshot> documents = task.getResult().getDocuments();
                        if (userStories.size() > 0) {
                            StoryModel firstStoryModel = documents.get(documents.size() - 1).toObject(StoryModel.class);
                            StoryModel storyModel = new StoryModel();
                            storyModel.setUserStories(userStories);
                            storyModel.setUri(firstStoryModel.getUri());
                            storyModel.setStoryBy(user.getId());
                            storyModel.setFullName(user.getFullName());
                            storyModel.setImage(user.getAvatarURL());
                            list.add(storyModel);
                        }
                        shimmerFrameLayoutStory.showShimmer(false);
                        shimmerFrameLayoutStory.setVisibility(View.GONE);
                        storyAdapter.notifyDataSetChanged();
                    } else {
                        Log.e("record", "Error getting documents: ", task.getException());
                    }
                }).addOnFailureListener(e -> {
                    Log.e("ERROR-GET-STORY::", e.getMessage());
                });
    }

    private void uploadImages(Uri imageUri, UserModel user) {
        final StorageReference reference = storage.getReference().child("stories")
                                                  .child(user.getId())
                                                  .child(new Date().getTime() + "");

        reference.putFile(imageUri).addOnSuccessListener(taskSnapshot -> {
            reference.getDownloadUrl().addOnSuccessListener(uri -> {
                StoryModel story = new StoryModel();
                story.setUri(uri.toString());
                saveNewStory(user, story);
            }).addOnFailureListener(e -> {
                Toast.makeText(getContext(), "Đã có lỗi khi tạo ảnh", Toast.LENGTH_SHORT).show();
                dismissProgressBar();
            });
        }).addOnFailureListener(e -> {
            Toast.makeText(getContext(), "Đã có lỗi khi tạo ảnh", Toast.LENGTH_SHORT).show();
            dismissProgressBar();
        });
    }

    private void saveNewStory(UserModel user, StoryModel story) {
        CollectionReference stories = db.collection("stories");
        String storyId = UUID.randomUUID().toString().replace("-", "");

        story.setStoryId(storyId);
        story.setImage(user.getAvatarURL());
        story.setFullName(user.getFullName());
        story.setStoryBy(user.getId()); // get name and set fix
        story.setStoryAt(new Date().getTime());

        stories.document(storyId).set(story).addOnSuccessListener(unused -> {
            Toast.makeText(getContext(), "Tạo thành công", Toast.LENGTH_SHORT).show();
            dismissProgressBar();
            storyAdapter.notifyDataSetChanged();
        }).addOnFailureListener(e -> {
            Log.e("ERROR", "[ERROR-CREATE-POST]", e);
            Toast.makeText(getContext(), "Đã có lỗi xảy ra.", Toast.LENGTH_SHORT).show();
            dismissProgressBar();
        });
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

    private void showProgressBarLoadMore() {
        progressBarLoadMore.setVisibility(View.VISIBLE);
        Sprite circle = new Circle();
        progressBarLoadMore.setIndeterminateDrawable(circle);
    }

    private void dismissProgressBarLoadMore() {
        progressBarLoadMore.setVisibility(View.GONE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            Uri uri = data.getData();
            showProgressBar();
            UserModel user = SharedPreferencesManager.readUserInfo();
            uploadImages(uri, user);
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