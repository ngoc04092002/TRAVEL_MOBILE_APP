package com.example.travel_mobile_app;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.example.travel_mobile_app.Manager.FirebaseManager;
import com.example.travel_mobile_app.databinding.ActivityMainBinding;
import com.example.travel_mobile_app.fragments.AccountFragment;
import com.example.travel_mobile_app.fragments.NotificationFragment;
import com.example.travel_mobile_app.fragments.SocialFragment;
import com.example.travel_mobile_app.fragments.SuggestionFragment;
import com.example.travel_mobile_app.fragments.suggestion;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Firebase;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.example.travel_mobile_app.models.NotificationModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.WriteBatch;


public class MainActivity extends AppCompatActivity {

    ActivityMainBinding binding;
    FirebaseManager firebaseManager = FirebaseManager.getInstance();
    private FirebaseFirestore db;
    private FirebaseAuth mAuth;

    private static final String TAG = "Main Activity";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        // Trong Activity hoặc Fragment của bạn
        /*FragmentTransaction newhome = getSupportFragmentManager().beginTransaction();
        newhome.replace(R.id.container, new SuggestionFragment()); // Thay thế R.id.container với id của layout container trong màn hình chính của bạn
        newhome.commit();*/


        //screen change from search screen to social screen
        String previousFragment = getIntent().getStringExtra("previous_fragment");
        if (previousFragment != null && previousFragment.equals("social_screen")) {
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            binding.readableBottomBar.setSelectedItemId(R.id.insta);
            transaction.replace(R.id.container, new SocialFragment());
            transaction.commit();
            return;
        }

        binding.readableBottomBar.setOnItemSelectedListener(item -> {
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

            int itemId = item.getItemId();

            if (itemId == R.id.insta) { // Replace this with the correct ID for the social item
                transaction.replace(R.id.container, new SocialFragment());
            } else if (itemId == R.id.bell) {
                transaction.replace(R.id.container, new NotificationFragment());
                updateNotificationCheckOpen();
            } else if (itemId == R.id.user) {
                transaction.replace(R.id.container, new AccountFragment());
            } else if (itemId == R.id.search) {
                transaction.replace(R.id.container,new SuggestionFragment());

            }

            transaction.commit();
            return true;
        });

        if (FirebaseAuth.getInstance().getCurrentUser() == null) {
            firebaseManager.loginUser("ngocngu@gmail.com", "1234567890", task -> {
                if (task.isSuccessful()) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d(TAG, "signInWithEmail:success");
                    // You can navigate to another activity here if needed
                    Toast.makeText(MainActivity.this, "Login successful.",
                            Toast.LENGTH_SHORT).show();
                } else {
                    // If sign in fails, display a message to the user.
                    Log.w(TAG, "signInWithEmail:failure", task.getException());
                }
                Toast.makeText(MainActivity.this, "Authentication failed.", Toast.LENGTH_SHORT).show();
            });
        }
        fetchNotificationBadge();
    }

    private void updateNotificationCheckOpen() {
        //fix I2cG4PNtPmSCnPSS0BQib3rRxxl2 userId
        db.collection("notifications")
          .whereEqualTo("postedBy", "I2cG4PNtPmSCnPSS0BQib3rRxxl2")
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

    private void fetchNotificationBadge() {
        //fix I2cG4PNtPmSCnPSS0BQib3rRxxl2 userId
        db.collection("notifications")
          .whereEqualTo("postedBy", "I2cG4PNtPmSCnPSS0BQib3rRxxl2")
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
