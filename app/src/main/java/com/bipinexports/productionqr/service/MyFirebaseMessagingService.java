package com.bipinexports.productionqr.service;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.util.Log;

import androidx.core.app.NotificationCompat;

import com.bipinexports.productionqr.APIClient;
import com.bipinexports.productionqr.Activity.MainActivity;
import com.bipinexports.productionqr.Activity.NotificationViewActivity;
import com.bipinexports.productionqr.R;
import com.bipinexports.productionqr.UserService;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.google.gson.JsonObject;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MyFirebaseMessagingService extends FirebaseMessagingService {

    private static final String TAG = "FCM";

    @Override
    public void onNewToken(String token) {
        super.onNewToken(token);
        Log.d(TAG, " New Firebase Token generated: " + token);
        sendTokenToServer(token);
    }

    private void sendTokenToServer(String token) {
        try {
            SharedPreferences prefs = getSharedPreferences("USER_PREFS", MODE_PRIVATE);
            String userId = prefs.getString("user_id", "1");
            String uniqueId = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);

            Log.d(TAG, " Preparing to send token | user_id=" + userId + " | unique_id=" + uniqueId + " | token=" + token);

            JsonObject json = new JsonObject();
            json.addProperty("user_id", userId);
            json.addProperty("unique_id", uniqueId);
            json.addProperty("device_token", token);

            UserService api = APIClient.getInterface();
            Call<ResponseBody> call = api.updateDeviceToken(json);

            call.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    try {
                        if (response.isSuccessful() && response.body() != null) {
                            String result = response.body().string();
                            Log.d(TAG, "Device token updated successfully: " + result);
                        } else {
                            Log.e(TAG, " Token update failed. HTTP Code: " + response.code());
                        }
                    } catch (Exception e) {
                        Log.e(TAG, " Error parsing response: " + e.getMessage());
                    }
                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {
                    Log.e(TAG, " Network error updating token: " + t.getMessage());
                }
            });

        } catch (Exception e) {
            Log.e(TAG, " sendTokenToServer Exception: " + e.getMessage());
        }
    }

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        Log.d(TAG, " Message received from: " + remoteMessage.getFrom());

        if (remoteMessage.getNotification() != null) {
            String title = remoteMessage.getNotification().getTitle();
            String body = remoteMessage.getNotification().getBody();
            String imageUrl = remoteMessage.getNotification().getImageUrl() != null
                    ? remoteMessage.getNotification().getImageUrl().toString()
                    : "";

            Log.d(TAG, " Notification: " + title + " | " + body);

            saveNotificationLocally(title, body, imageUrl);

            showNotification(title, body, imageUrl);
        }
    }

    private void saveNotificationLocally(String title, String message, String imageUrl) {
        try {
            SharedPreferences prefs = getSharedPreferences("NOTIFICATIONS", MODE_PRIVATE);
            String existing = prefs.getString("list", "[]");

            org.json.JSONArray array = new org.json.JSONArray(existing);
            org.json.JSONObject obj = new org.json.JSONObject();
            obj.put("title", title);
            obj.put("message", message);
            obj.put("imageUrl", imageUrl);
            obj.put("time", System.currentTimeMillis());
            obj.put("isRead", false);  // Add this line

            array.put(obj);

            prefs.edit().putString("list", array.toString()).apply();

            // Increment unread count
            NotificationHelper.incrementUnreadCount(this);  // Add this line

            Log.d(TAG, " Notification saved locally (" + title + ")");
        } catch (Exception e) {
            Log.e(TAG, " Failed to save notification: " + e.getMessage());
        }
    }

    private void showNotification(String title, String message, String imageUrl) {
        Intent intent = new Intent(this, NotificationViewActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_SINGLE_TOP);

        // pass notification info
        intent.putExtra("fromNotification", true);
        intent.putExtra("title", title);
        intent.putExtra("message", message);
        intent.putExtra("imageUrl", imageUrl);

        PendingIntent pendingIntent = PendingIntent.getActivity(
                this,
                0,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );

        String channelId = "fcm_default_channel";
        Uri soundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, channelId)
                .setSmallIcon(R.mipmap.ic_appicon)
                .setContentTitle(title != null ? title : "Production QR")
                .setContentText(message)
                .setAutoCancel(true)
                .setSound(soundUri)
                .setContentIntent(pendingIntent)
                .setPriority(NotificationCompat.PRIORITY_HIGH);

        NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    channelId,
                    "Firebase Notifications",
                    NotificationManager.IMPORTANCE_HIGH
            );
            manager.createNotificationChannel(channel);
        }

        manager.notify((int) System.currentTimeMillis(), builder.build());
    }

}
