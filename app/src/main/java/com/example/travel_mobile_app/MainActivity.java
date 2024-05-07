package com.example.travel_mobile_app;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;


import android.os.Bundle;
import android.util.Log;

import com.example.travel_mobile_app.databinding.ActivityMainBinding;
import com.example.travel_mobile_app.fragments.AccountFragment;
import com.example.travel_mobile_app.fragments.NotificationFragment;
import com.example.travel_mobile_app.fragments.SocialFragment;
import com.example.travel_mobile_app.fragments.suggestion;
import com.example.travel_mobile_app.models.NotificationModel;
import com.example.travel_mobile_app.models.PostModel;
import com.example.travel_mobile_app.models.StoryModel;
import com.example.travel_mobile_app.models.UserModel;
import com.example.travel_mobile_app.services.SharedPreferencesManager;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.WriteBatch;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;


public class MainActivity extends AppCompatActivity {

    ActivityMainBinding binding;
    private FirebaseFirestore db;
    private ArrayList<PostModel> postList;
    private ArrayList<StoryModel> storyList;
    private Gson gson = new Gson();
    private Bundle bundle = new Bundle();


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
            Fragment socialFragment = new SocialFragment();
            socialFragment.setArguments(bundle);
            transaction.replace(R.id.container, socialFragment);
            transaction.commit();
        }

        if (previousFragment != null && previousFragment.equals("notification")) {
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            binding.readableBottomBar.setSelectedItemId(R.id.bell);
            transaction.replace(R.id.container, new NotificationFragment());
            transaction.commit();
        }

        if (previousFragment != null && previousFragment.equals("home_screen")) {
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            binding.readableBottomBar.setSelectedItemId(R.id.home);
            transaction.replace(R.id.container, new suggestion());
            transaction.commit();
        }


        binding.readableBottomBar.setOnItemSelectedListener(item -> {
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

            int itemId = item.getItemId();

            if (itemId == R.id.insta) { // Replace this with the correct ID for the social item
                Fragment socialFragment = new SocialFragment();
                socialFragment.setArguments(bundle);
                transaction.replace(R.id.container, socialFragment);
            } else if (itemId == R.id.bell) {
                transaction.replace(R.id.container, new NotificationFragment());
                updateNotificationCheckOpen();
            } else if (itemId == R.id.user) {
                transaction.replace(R.id.container, new AccountFragment());
            } else if (itemId == R.id.home) {
                transaction.replace(R.id.container, new suggestion());
            }

            transaction.commit();
            return true;
        });

        String userId = Optional.ofNullable(getIntent().getStringExtra("userId"))
                                .orElseGet(() -> SharedPreferencesManager.readUserInfo().getId());

        if (userId != null) {
            setUserInfoToLocal(userId);
        }

    }


    private void setUserInfoToLocal(String uId) {
        SharedPreferencesManager.init(this);
        CollectionReference users = db.collection("users");
        users.document(uId)
             .get()
             .addOnCompleteListener(taskUser -> {
                 if (taskUser.isSuccessful() && taskUser.getResult() != null) {
                     UserModel userModel = taskUser.getResult().toObject(UserModel.class);
                     fetchPostListData(userModel);
                     fetchNotificationBadge(userModel);
                 }
             });
    }

    private void fetchPostListData(UserModel user) {
        CollectionReference postsRef = db.collection("posts");

        List<String> following = user.getFollowing();
        //fix
        postsRef
//                .whereIn("postedBy", following)
.orderBy("postedAt", Query.Direction.DESCENDING)
.limit(30)
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

    private void fetchNotificationBadge(UserModel user) {
        Query query = db.collection("notifications")
                        .whereEqualTo("postedBy", user.getId())
                        .whereEqualTo("checkOpen", false);

        query.addSnapshotListener((value, e) -> {
            if (e != null) {
                Log.e("ERROR", "Error getting badge:", e);
                return;
            }
            int badgeNumber = value.getDocuments().size();
            if (badgeNumber > 0) {
                binding.readableBottomBar.getOrCreateBadge(R.id.bell).setNumber(badgeNumber);
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

    }


}
