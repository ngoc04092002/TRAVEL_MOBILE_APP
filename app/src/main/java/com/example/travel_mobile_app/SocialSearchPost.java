package com.example.travel_mobile_app;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.example.travel_mobile_app.Adapter.PostAdapter;
import com.example.travel_mobile_app.databinding.ActivitySocialSearchPostBinding;
import com.example.travel_mobile_app.fragments.SocialFragment;
import com.example.travel_mobile_app.models.PostModel;
import com.github.ybq.android.spinkit.sprite.Sprite;
import com.github.ybq.android.spinkit.style.Circle;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.schedulers.Schedulers;


public class SocialSearchPost extends AppCompatActivity implements View.OnClickListener {

    ActivitySocialSearchPostBinding binding;
    private ArrayList<PostModel> posts;
    private FirebaseFirestore db;
    private Disposable disposable;
    private PostAdapter postAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySocialSearchPostBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        db = FirebaseFirestore.getInstance();


        posts = new ArrayList<>();
        disposable = getDisposable(binding.search);

        postAdapter = new PostAdapter(posts, this, this.getSupportFragmentManager(), this, db);
        binding.searchPostRv.setHasFixedSize(true);
        binding.searchPostRv.setLayoutManager(new StaggeredGridLayoutManager(1, LinearLayoutManager.VERTICAL));
        binding.searchPostRv.setAdapter(postAdapter);

        binding.searchPostBtnBack.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.searchPost_btnBack) {
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.container, new SocialFragment());
            transaction.commit();
        }
    }

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
                                     if (newText.toString().trim().equals("")) {
                                         binding.notFound.setVisibility(View.GONE);
                                         if(posts.size()!=0){
                                             posts.clear();
                                             postAdapter.notifyDataSetChanged();
                                         }
                                         return;
                                     }
                                     refreshPosts(newText.toString().trim());
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