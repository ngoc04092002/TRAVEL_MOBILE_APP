package com.example.travel_mobile_app;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;


import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.example.travel_mobile_app.Adapter.PostAdapter;
import com.example.travel_mobile_app.Adapter.StoryAdapter;
import com.example.travel_mobile_app.Manager.FirebaseManager;
import com.example.travel_mobile_app.databinding.ActivityMainBinding;
import com.example.travel_mobile_app.fragments.AccountFragment;
import com.example.travel_mobile_app.fragments.FriendFollowingFragment;
import com.example.travel_mobile_app.fragments.NotificationFragment;
import com.example.travel_mobile_app.fragments.SocialFragment;
import com.example.travel_mobile_app.models.PostModel;
import com.example.travel_mobile_app.models.StoryModel;
import com.example.travel_mobile_app.models.UserModel;
import com.example.travel_mobile_app.models.UserStory;
import com.example.travel_mobile_app.services.SharedPreferencesManager;

import com.google.firebase.auth.FirebaseAuth;
import com.example.travel_mobile_app.models.NotificationModel;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.WriteBatch;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;


public class MainActivity extends AppCompatActivity {

    ActivityMainBinding binding;
    private FirebaseFirestore db;
    private ArrayList<PostModel> postList;
    private ArrayList<StoryModel> storyList;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        db = FirebaseFirestore.getInstance();


        postList = new ArrayList<>();
        storyList = new ArrayList<>();


        //screen change from search screen to social screen
        String previousFragment = getIntent().getStringExtra("previous_fragment");
        if (previousFragment != null && previousFragment.equals("social_screen")) {
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            binding.readableBottomBar.setSelectedItemId(R.id.insta);
            transaction.replace(R.id.container, new SocialFragment(postList,storyList));
            transaction.commit();
        }

        if (previousFragment != null && previousFragment.equals("notification")) {
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            binding.readableBottomBar.setSelectedItemId(R.id.bell);
            transaction.replace(R.id.container, new NotificationFragment());
            transaction.commit();
        }


        binding.readableBottomBar.setOnItemSelectedListener(item -> {
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

            int itemId = item.getItemId();

            if (itemId == R.id.insta) { // Replace this with the correct ID for the social item
                transaction.replace(R.id.container, new SocialFragment(postList,storyList));
            } else if (itemId == R.id.bell) {
                transaction.replace(R.id.container, new NotificationFragment());
                updateNotificationCheckOpen();
            } else if (itemId == R.id.user) {
                transaction.replace(R.id.container, new AccountFragment());
            }

            transaction.commit();
            return true;
        });

        setUserInfoToLocal("qbJW6GgDkqgv6H5tvCPfLty2Bto2");

    }


    private void setUserInfoToLocal(String uId) {
        SharedPreferencesManager.init(this);
        CollectionReference users = db.collection("users");
        users.document(uId)
             .get()
             .addOnCompleteListener(taskUser -> {
                 if (taskUser.isSuccessful() && taskUser.getResult() != null) {
                     UserModel userModel = taskUser.getResult().toObject(UserModel.class);
                     SharedPreferencesManager.writeUserInfo(userModel);
                     fetchPostListData(userModel);
                     fetchStoryListData(userModel);
                     fetchNotificationBadge(userModel);
                 }
             });
    }

    private void fetchPostListData( UserModel user) {
        CollectionReference postsRef = db.collection("posts");

        List<String> following = user.getFollowing();
        //fix
        postsRef
//                .whereIn("postedBy", following)
.orderBy("postedAt", Query.Direction.DESCENDING)
.get()
.addOnCompleteListener(task -> {
    if (task.isSuccessful()) {
        for (QueryDocumentSnapshot document : task.getResult()) {
            PostModel postModel = document.toObject(PostModel.class);
            postList.add(postModel);
        }
    } else {
        Log.d("record", "Error getting documents: ", task.getException());
    }
});
    }

    private void fetchStoryListData(UserModel user) {
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

                            if (currentTime.getTime() - storyModel.getStoryAt() < (24 * 60 * 60 * 1000)) {
                                userStories.add(new UserStory(storyModel.getUri(), storyModel.getStoryAt()));
                            }
                        }
                        if (userStories.size() > 0) {
                            StoryModel storyModel = new StoryModel();
                            storyModel.setUserStories(userStories);
                            storyModel.setStoryBy(user.getId());
                            storyModel.setFullName(user.getFullName());
                            storyModel.setImage(user.getAvatarURL());
                            storyList.add(storyModel);
                        }
                    } else {
                        Log.e("record", "Error getting documents: ", task.getException());
                    }
                }).addOnFailureListener(e -> {
                    Log.e("ERROR-GET-STORY::", e.getMessage());
                });
    }

    private void updateNotificationCheckOpen() {
        UserModel user = SharedPreferencesManager.readUserInfo();

        db.collection("notifications")
          .whereEqualTo("postedBy", user.getId())
          .whereEqualTo("checkOpen", false)
          .get().addOnCompleteListener(task -> {
              if (task.isSuccessful()) {
                  WriteBatch batch = db.batch();
                  for (QueryDocumentSnapshot document : task.getResult()) {
                      NotificationModel model = document.toObject(NotificationModel.class);
                      model.setCheckOpen(true);
                      DocumentReference docRef = db.collection("notifications").document(document.getId());
                      batch.set(docRef, model);
                  }
                  batch.commit().addOnCompleteListener(batchTask -> {
                      if (!batchTask.isSuccessful()) {
                          Log.d("record", "Error committing batched writes: ", batchTask.getException());
                      }
                  });
                  binding.readableBottomBar.removeBadge(R.id.bell);
              } else {
                  Log.d("record", "Error getting documents: ", task.getException());
              }
          });
    }

    private void fetchNotificationBadge( UserModel user) {
        db.collection("notifications")
          .whereEqualTo("postedBy", user.getId())
          .whereEqualTo("checkOpen", false)
          .get().addOnCompleteListener(task -> {
              if (task.isSuccessful()) {
                  int badgeNumber = task.getResult().size();
                  if (badgeNumber > 0) {
                      binding.readableBottomBar.getOrCreateBadge(R.id.bell).setNumber(badgeNumber);
                  }
              } else {
                  Log.d("record", "Error getting documents: ", task.getException());
              }
          });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

    }
}