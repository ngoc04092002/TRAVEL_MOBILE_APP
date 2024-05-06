package com.example.travel_mobile_app;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.travel_mobile_app.Adapter.NotificationAdapter;
import com.example.travel_mobile_app.Adapter.SearchPostItemAdapter;
import com.example.travel_mobile_app.databinding.ActivitySocialSearchPostBinding;
import com.example.travel_mobile_app.models.NotificationModel;
import com.example.travel_mobile_app.models.PostModel;
import com.example.travel_mobile_app.models.UserModel;
import com.example.travel_mobile_app.services.SharedPreferencesManager;
import com.github.ybq.android.spinkit.sprite.Sprite;
import com.github.ybq.android.spinkit.style.Circle;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.schedulers.Schedulers;


public class SocialSearchPost extends AppCompatActivity implements View.OnClickListener {

    ActivitySocialSearchPostBinding binding;
    private List<PostModel> posts;
    private List<NotificationModel> notifications;
    private FirebaseFirestore db;
    private Disposable disposable;
    private SearchPostItemAdapter postAdapter;
    private NotificationAdapter notificationAdapter;
    private String type = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySocialSearchPostBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        db = FirebaseFirestore.getInstance();

        disposable = getDisposable(binding.search);

        binding.searchPostRv.setHasFixedSize(true);
        binding.searchPostRv.setLayoutManager(new StaggeredGridLayoutManager(1, LinearLayoutManager.VERTICAL));

        type = getIntent().getStringExtra("search_activity");

        if (type != null && type.equals("notification")) {
            notifications = new ArrayList<>();
            notificationAdapter = new NotificationAdapter(notifications, this, db, this,getSupportFragmentManager());
            binding.searchPostRv.setAdapter(notificationAdapter);
        } else {
            posts = new ArrayList<>();
            postAdapter = new SearchPostItemAdapter(posts, this, "search_post", this);
            binding.searchPostRv.setAdapter(postAdapter);
        }


        binding.searchPostBtnBack.setOnClickListener(this);

        LinearLayout searchHistory = binding.searchHistory;
        Set<String> existsHistory = SharedPreferencesManager.readPostSearchHistory();
        searchHistory.removeAllViews();
        for (String h : existsHistory) {
            LayoutInflater post = LayoutInflater.from(this);
            View subLayout = post.inflate(R.layout.post_search_history, null);
            TextView content = subLayout.findViewById(R.id.content);
            content.setText(h);
            content.setOnClickListener(v -> {
                binding.search.setQuery(h, true);
            });
            subLayout.findViewById(R.id.btn_del_history).setOnClickListener(v -> {
                existsHistory.remove(h);
                searchHistory.removeView(subLayout);
            });

            searchHistory.addView(subLayout);
        }

        SharedPreferencesManager.init(this);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.searchPost_btnBack) {
            Intent intent = new Intent(this, MainActivity.class);

            String type = getIntent().getStringExtra("search_activity");
            if (type != null && type.equals("notification")) {
                intent.putExtra("previous_fragment", "notification");
            } else {
                intent.putExtra("previous_fragment", "social_screen");
            }

            overridePendingTransition(android.R.anim.fade_in,R.anim.pop_exit_animation);
            startActivity(intent);
        }
    }


    //debounce
    private Disposable getDisposable(SearchView search) {
        return Observable.create(emitter -> {
                             search.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                                 @Override
                                 public boolean onQueryTextSubmit(String query) {
                                     return false;
                                 }

                                 @Override
                                 public boolean onQueryTextChange(String newText) {
                                     if (!emitter.isDisposed()) {
                                         emitter.onNext(newText);
                                     }
                                     return false;
                                 }
                             });
                         }).debounce(500, TimeUnit.MILLISECONDS)
                         .subscribeOn(Schedulers.io())
                         .observeOn(AndroidSchedulers.mainThread())
                         .subscribe(
                                 newText -> {
                                     LinearLayout searchHistory = binding.searchHistory;
                                     Set<String> existsHistory = SharedPreferencesManager.readPostSearchHistory();

                                     searchHistory.removeAllViews();

                                     if (newText.toString().trim().equals("")) {
                                         binding.notFound.setVisibility(View.GONE);
                                         if (type != null && type.equals("notification") && notifications.size() != 0) {
                                             notifications.clear();
                                             notificationAdapter.notifyDataSetChanged();
                                         } else {
                                             posts.clear();
                                             postAdapter.notifyDataSetChanged();
                                         }

                                         searchHistory.setVisibility(View.VISIBLE);
                                         for (String h : existsHistory) {
                                             LayoutInflater post = LayoutInflater.from(this);
                                             View subLayout = post.inflate(R.layout.post_search_history, null);
                                             TextView content = subLayout.findViewById(R.id.content);
                                             content.setText(h);
                                             content.setOnClickListener(v -> {
                                                 binding.search.setQuery(h, true);
                                             });
                                             subLayout.findViewById(R.id.btn_del_history).setOnClickListener(v -> {
                                                 existsHistory.remove(h);
                                                 searchHistory.removeView(subLayout);
                                             });
                                             searchHistory.addView(subLayout);
                                         }
                                     }else{
                                         searchHistory.setVisibility(View.GONE);
                                         existsHistory.add(newText.toString().trim());
                                         SharedPreferencesManager.writePostSearchHistory(existsHistory);
                                         System.out.println("type::" + type);
                                         System.out.println("newText::" + newText.toString().trim());
                                         if (type != null && type.equals("notification")) {
                                             refreshNotifications(newText.toString().trim());
                                         } else {
                                             refreshPosts(newText.toString().trim());
                                         }
                                     }

                                 },
                                 error -> {
                                     // handle error
                                     Log.e("ERROR::", error.getMessage());
                                 },
                                 () -> {
                                     // handle complete
                                 });
    }

    private void refreshPosts(String newText) {
        posts.clear();
        showProgressBar();
        db.collection("posts")
          .whereGreaterThanOrEqualTo("postDescription", newText)
          .whereLessThanOrEqualTo("postDescription", newText + "\uf8ff")
          .get()
          .addOnSuccessListener(documentSnapshots -> {
              if (documentSnapshots.getDocuments().size() == 0) {
                  binding.notFound.setVisibility(View.VISIBLE);
              } else {
                  binding.notFound.setVisibility(View.GONE);
                  for (DocumentSnapshot documentSnapshot : documentSnapshots.getDocuments()) {
                      PostModel post = documentSnapshot.toObject(PostModel.class);
                      posts.add(post);
                  }
                  postAdapter.notifyDataSetChanged();
              }
              dismissProgressBar();
          }).addOnFailureListener(e -> {
              dismissProgressBar();
              binding.notFound.setVisibility(View.VISIBLE);
              binding.notFound.setText("Đã có lỗi xảy ra");
          });
    }

    private void refreshNotifications(String newText) {
        notifications.clear();
        showProgressBar();
        UserModel user = SharedPreferencesManager.readUserInfo();

        db.collection("notifications")
          .whereEqualTo("postedBy", user.getId())
          .whereGreaterThanOrEqualTo("notificationBy", newText)
          .whereLessThanOrEqualTo("notificationBy", newText + "\uf8ff")
          .get()
          .addOnSuccessListener(documentSnapshots -> {
              if (documentSnapshots.getDocuments().size() == 0) {
                  binding.notFound.setVisibility(View.VISIBLE);
              } else {
                  binding.notFound.setVisibility(View.GONE);
                  for (DocumentSnapshot documentSnapshot : documentSnapshots.getDocuments()) {
                      NotificationModel notification = documentSnapshot.toObject(NotificationModel.class);
                      notifications.add(notification);
                  }
                  notificationAdapter.notifyDataSetChanged();
              }
              dismissProgressBar();
          }).addOnFailureListener(e -> {
              Log.e("ERROR-FILTER-NOTIFICATION::", e.getMessage());
              dismissProgressBar();
              binding.notFound.setVisibility(View.VISIBLE);
              binding.notFound.setText("Đã có lỗi xảy ra");
          });
    }

    private void showProgressBar() {
        binding.spinKit.setVisibility(View.VISIBLE);
        Sprite circle = new Circle();
        binding.spinKit.setIndeterminateDrawable(circle);
    }

    private void dismissProgressBar() {
        binding.spinKit.setVisibility(View.GONE);
    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {
        super.onPointerCaptureChanged(hasCapture);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        disposable.dispose();
    }
}