package com.example.travel_mobile_app.services;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;

import com.example.travel_mobile_app.MainActivity;
import com.example.travel_mobile_app.R;
import com.example.travel_mobile_app.dto.UserToken;
import com.example.travel_mobile_app.models.NotificationModel;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.WriteBatch;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Date;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.MediaType;

public class MyFirebaseMessagingService extends FirebaseMessagingService {

    private static final String TAG = "MyFirebaseMsgService";
    public static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
    public static final String FCM_API = "https://fcm.googleapis.com/fcm/send";
    public static final String SERVER_KEY = "AAAAJTXnBGY:APA91bFw7SU5fptqFRTUg7ywo0mQ8TNSZ6Rqy7LVmOY9yY64uJuC7VsWQzmHf_Bne7KRhlpp2jiSIHNeEre4g34W4CgOCSJPCl641nn5hzvpd6rdpxl3Il_T2oHyxO6uWJKpUu76lEAK";

    private String token = "";
    private FirebaseFirestore db = FirebaseFirestore.getInstance();


    @Override
    public void onNewToken(@NonNull String token) {
        super.onNewToken(token);
        this.token = token;
        Log.d("TOKEN::", token);
        SharedPreferencesManager.init(getApplicationContext());
        String oldToken = SharedPreferencesManager.readDeviceToken();
        if (token.equals(oldToken)) {
            return;
        }
        String userId = "I2cG4PNtPmSCnPSS0BQib3rRxxl2";

        //remove old token
//        removeToken(token, userId);

        //fix user id
        UserToken userToken = new UserToken(userId, token, new Date().getTime());
        FirebaseFirestore.getInstance().collection("tokens")
                         .whereEqualTo("userId", userId)
                         .whereEqualTo("token", token)
                         .get()
                         .addOnCompleteListener(task -> {
                             if (task.isSuccessful() && task.getResult().size() == 0) {
                                 SharedPreferencesManager.writeDeviceToken(token);
                                 FirebaseFirestore.getInstance().collection("tokens").add(userToken);
                             }
                         });

    }

    public String getCurrentDeviceToken() {
        StringBuilder currentToken = new StringBuilder();
        FirebaseMessaging.getInstance().getToken().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                currentToken.append(task.getResult().toString());
            }
        });

        return currentToken.toString();
    }

    private void removeToken(String oldToken, String userId) {
        db.collection("tokens")
          .whereEqualTo("userId", userId)
          .whereEqualTo("token", oldToken)
          .get()
          .addOnCompleteListener(task -> {
              if (task.isSuccessful()) {
                  WriteBatch batch = db.batch();
                  for (QueryDocumentSnapshot document : task.getResult()) {
                      batch.delete(document.getReference());
                  }
                  batch.commit()
                       .addOnSuccessListener(aVoid -> {
                           Log.d(TAG, "Delete success");
                       })
                       .addOnFailureListener(e -> {
                           Log.w(TAG, "Delete error", e);
                       });
              }
          });
    }

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        // TODO(developer): Handle FCM messages here.

        if (remoteMessage.getNotification() != null) {
            // Show the notification
            String notificationBody = remoteMessage.getNotification().getBody();
            String notificationTitle = remoteMessage.getNotification().getTitle();
            sendNotification(notificationTitle, notificationBody);

        }
    }

    private void sendNotification(String title, String body) {
        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0 /* Request code */, intent,
                                                                PendingIntent.FLAG_IMMUTABLE);

        String channelId = "fcm_default_channel";
        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder =
                new NotificationCompat.Builder(this, channelId)
                        .setSmallIcon(R.mipmap.ic_launcher)
                        .setContentTitle(title)
                        .setContentText(body)
                        .setAutoCancel(true)
                        .setSound(defaultSoundUri)
                        .setContentIntent(pendingIntent).addAction(new NotificationCompat.Action(
                                android.R.drawable.sym_call_missed,
                                "Hủy",
                                PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_CANCEL_CURRENT | PendingIntent.FLAG_IMMUTABLE)))
                        .addAction(new NotificationCompat.Action(
                                android.R.drawable.sym_call_outgoing,
                                "Xem",
                                PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_CANCEL_CURRENT | PendingIntent.FLAG_IMMUTABLE)));

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        // Since android Oreo notification channel is needed.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(channelId,
                                                                  "Channel human readable title",
                                                                  NotificationManager.IMPORTANCE_DEFAULT);
            notificationManager.createNotificationChannel(channel);
        }

        notificationManager.notify(0 /* ID of notification */, notificationBuilder.build());
    }

    public static void pushNotification(String to, String title, String body) throws JSONException, IOException {
        if (to.equals("")) {
            throw new JSONException("To is the empty");
        }
        if (title.equals("") || body.equals("")) {
            throw new JSONException("TITLE and BODY is the empty");
        }

        String userId = "I2cG4PNtPmSCnPSS0BQib3rRxxl2";

        OkHttpClient client = new OkHttpClient();
        JSONObject notification = new JSONObject();
        notification.put("title", title);
        notification.put("body", body);

        JSONObject data = new JSONObject();
        data.put("key1", "value1");
        data.put("key2", "value2");

        JSONObject json = new JSONObject();
        json.put("to", to);
        json.put("notification", notification);
        json.put("data", data);

        RequestBody requestBody;
        requestBody = RequestBody.create(json.toString(), JSON);
        Request request = new Request.Builder()
                .url(FCM_API)
                .post(requestBody)
                .addHeader("Authorization", "key=" + SERVER_KEY)
                .addHeader("Content-Type", "application/json")
                .build();

        Response response = client.newCall(request).execute();
        String responseBody = response.body().string();
        System.out.println(responseBody);
    }

}





