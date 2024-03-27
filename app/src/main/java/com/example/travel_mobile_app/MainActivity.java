package com.example.travel_mobile_app;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;
import android.util.Log;

import com.example.travel_mobile_app.databinding.ActivityMainBinding;
import com.example.travel_mobile_app.fragments.AccountFragment;
import com.example.travel_mobile_app.fragments.NotificationFragment;
import com.example.travel_mobile_app.fragments.SocialFragment;
import com.example.travel_mobile_app.models.NotificationModel;
import com.example.travel_mobile_app.services.MyFirebaseMessagingService;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.WriteBatch;

import org.json.JSONException;

import java.io.IOException;


public class MainActivity extends AppCompatActivity {

    ActivityMainBinding binding;
    private FirebaseFirestore db;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();


        binding.readableBottomBar.setOnItemSelectedListener(item -> {
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

            int itemId = item.getItemId();

            if (itemId == R.id.insta) { // Replace this with the correct ID for the social item
                transaction.replace(R.id.container, new SocialFragment());
            } else if (itemId == R.id.bell) {
                new Thread(()->{
                    try {
                        //fix
                        MyFirebaseMessagingService.pushNotification("fbYprQZSRB-ImtzTv3sKEO:APA91bFLFI96HDbL3rqkegPcRodHzwk65XOoBKvfxdui9w2FcbiS355gawCxz3mTp24xPaCUPBw-UC3wFVRIWg1nHOecYy_9Lc6Dchv_2aqBszDR8ShtWFWcplR4QFLSVs-gpZz0L1OU","test1", "test2", "", "like");
                    } catch (JSONException e) {
                        throw new RuntimeException(e);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }).start();

                transaction.replace(R.id.container, new NotificationFragment());
                updateNotificationCheckOpen();
            } else if (itemId == R.id.user) {
                transaction.replace(R.id.container, new AccountFragment());
            }

            transaction.commit();
            return true;
        });
        String email = "example@example.com";
        String password = "password123";
        // Sign in success, update UI with the signed-in user's information
        FirebaseUser user = mAuth.getCurrentUser();
        user.getIdToken(true)
            .addOnCompleteListener(t -> {
                System.out.println("LOG::" + 3);
                if (t.isSuccessful()) {
                    String token = t.getResult().getToken();
                    System.out.println("TOKEN::" + token);
                }
            });

//
//
//        mAuth.createUserWithEmailAndPassword(email, password)
//             .addOnCompleteListener(task -> {
//                 if (task.isSuccessful()) {
//                     FirebaseUser user = mAuth.getCurrentUser();
//                     System.out.println("TOKEN::"+user.getIdToken(true));
//                 }
//             });


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

}