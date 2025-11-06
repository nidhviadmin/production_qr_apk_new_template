package com.bipinexports.productionqr.utils;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bipinexports.productionqr.Activity.NotificationViewActivity;
import com.bipinexports.productionqr.APIClient;
import com.bipinexports.productionqr.SessionManagement;
import com.bipinexports.productionqr.UserService;
import com.bipinexports.productionqr.service.NotificationHelper;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.gson.JsonObject;

import org.json.JSONArray;
import org.json.JSONObject;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Centralized Notification and FCM Manager
 * Use this class across all activities for consistent notification handling
 */
public class NotificationManager {

    private static final String TAG = "NotificationManager";
    private static final String PREFS_NAME = "NOTIFICATIONS";
    private static final String USER_PREFS = "USER_PREFS";

    private Context context;
    private ImageView notifyIcon;
    private TextView notificationCount;
    private View notificationDot;

    public NotificationManager(Context context) {
        this.context = context;
    }

    /**
     * Complete setup for notifications including FCM token registration
     * Call this in onCreate() of any activity
     *
     * @param notifyIcon - The notification bell icon ImageView
     * @param notificationCount - The badge TextView showing count
     * @param userid - Current user ID for FCM token registration
     */
    public void setupComplete(ImageView notifyIcon, TextView notificationCount, String userid) {
        this.notifyIcon = notifyIcon;
        this.notificationCount = notificationCount;

        // Setup FCM token
        setupFirebaseToken(userid);

        // Set click listener for notification icon
        if (notifyIcon != null) {
            notifyIcon.setOnClickListener(v -> openNotificationView());
        }

        // Update badge count
        updateBadge();
    }

    /**
     * Setup notifications without FCM (for activities that don't need FCM registration)
     *
     * @param notifyIcon - The notification bell icon ImageView
     * @param notificationCount - The badge TextView showing count
     */
    public void setupNotifications(ImageView notifyIcon, TextView notificationCount) {
        this.notifyIcon = notifyIcon;
        this.notificationCount = notificationCount;

        // Set click listener for notification icon
        if (notifyIcon != null) {
            notifyIcon.setOnClickListener(v -> openNotificationView());
        }

        // Update badge count
        updateBadge();
    }

    /**
     * Setup with notification dot (for activities using dot indicator)
     *
     * @param notifyIcon - The notification bell icon ImageView
     * @param notificationCount - The badge TextView showing count
     * @param notificationDot - The dot View indicator
     * @param userid - Current user ID for FCM token registration
     */
    public void setupWithDot(ImageView notifyIcon, TextView notificationCount, View notificationDot, String userid) {
        this.notifyIcon = notifyIcon;
        this.notificationCount = notificationCount;
        this.notificationDot = notificationDot;

        // Setup FCM token
        setupFirebaseToken(userid);

        // Set click listener for notification icon
        if (notifyIcon != null) {
            notifyIcon.setOnClickListener(v -> openNotificationView());
        }

        // Update badge count and dot
        updateBadge();
        updateDot();
    }

    /**
     * Setup and register Firebase Cloud Messaging token
     *
     * @param userid - Current user ID
     */
    private void setupFirebaseToken(String userid) {
        if (userid == null || userid.isEmpty()) {
            Log.w(TAG, "User ID is null or empty. Skipping FCM token registration.");
            return;
        }

        FirebaseMessaging.getInstance().getToken()
                .addOnCompleteListener(task -> {
                    if (!task.isSuccessful()) {
                        Log.w(TAG, "Fetching FCM registration token failed", task.getException());
                        return;
                    }

                    String token = task.getResult();
                    Log.d(TAG, "Firebase Token: " + token);

                    // Save token locally
                    SharedPreferences prefs = context.getSharedPreferences(USER_PREFS, Context.MODE_PRIVATE);
                    prefs.edit().putString("device_token", token).apply();

                    // Send token to server
                    sendTokenToServer(userid, token);
                });
    }

    /**
     * Send FCM token to backend server
     */
    private void sendTokenToServer(String userid, String token) {
        try {
            JsonObject json = new JsonObject();
            json.addProperty("user_id", userid);
            json.addProperty("unique_id", android.provider.Settings.Secure.getString(
                    context.getContentResolver(), android.provider.Settings.Secure.ANDROID_ID));
            json.addProperty("device_token", token);

            UserService api = APIClient.getInterface();
            Call<ResponseBody> call = api.updateDeviceToken(json);

            call.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    try {
                        if (response.isSuccessful() && response.body() != null) {
                            String res = response.body().string();
                            Log.d(TAG, "Server response: " + res);
                        } else {
                            Log.e(TAG, "Token update failed. HTTP Code: " + response.code());
                        }
                    } catch (Exception e) {
                        Log.e(TAG, "Error reading response: " + e.getMessage());
                    }
                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {
                    Log.e(TAG, "Network error sending token: " + t.getMessage());
                }
            });

        } catch (Exception e) {
            Log.e(TAG, "Exception sending token: " + e.getMessage());
        }
    }

    /**
     * Update the notification badge count
     */
    public void updateBadge() {
        if (notificationCount == null) return;

        int unreadCount = NotificationHelper.getUnreadCount(context);

        if (unreadCount > 0) {
            if (unreadCount > 99) {
                notificationCount.setText("99+");
            } else {
                notificationCount.setText(String.valueOf(unreadCount));
            }
            notificationCount.setVisibility(View.VISIBLE);
            Log.d(TAG, "Showing badge count: " + unreadCount);
        } else {
            notificationCount.setVisibility(View.GONE);
            Log.d(TAG, "No unread. Badge hidden");
        }
    }

    /**
     * Update notification dot indicator
     */
    public void updateDot() {
        if (notificationDot == null) return;

        int unreadCount = NotificationHelper.getUnreadCount(context);
        notificationDot.setVisibility(unreadCount > 0 ? View.VISIBLE : View.GONE);
        Log.d(TAG, "Notification dot visibility updated. Unread count: " + unreadCount);
    }

    /**
     * Open NotificationViewActivity
     */
    private void openNotificationView() {
        Intent intent = new Intent(context, NotificationViewActivity.class);
        context.startActivity(intent);
    }

    /**
     * Save notification from FCM intent
     */
    public static void saveNotificationFromIntent(Context context, String title, String message, String imageUrl) {
        try {
            SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
            String existing = prefs.getString("list", "[]");

            JSONArray array = new JSONArray(existing);

            // Check if notification already exists
            boolean exists = false;
            for (int i = 0; i < array.length(); i++) {
                JSONObject obj = array.getJSONObject(i);
                if (obj.optString("title").equals(title) &&
                        obj.optString("message").equals(message)) {
                    exists = true;
                    break;
                }
            }

            if (!exists) {
                JSONObject obj = new JSONObject();
                obj.put("title", title);
                obj.put("message", message);
                obj.put("imageUrl", imageUrl != null ? imageUrl : "");
                obj.put("time", System.currentTimeMillis());
                obj.put("isRead", false);

                array.put(obj);
                prefs.edit().putString("list", array.toString()).apply();

                NotificationHelper.incrementUnreadCount(context);

                Log.d(TAG, "Notification saved: " + title);
            }
        } catch (Exception e) {
            Log.e(TAG, "Failed to save notification: " + e.getMessage());
        }
    }

    /**
     * Handle notification intent in activity
     * Call this in onCreate() and onNewIntent()
     *
     * @param intent - The intent to handle
     * @param isUserLoggedIn - Whether user is currently logged in
     */
    public void handleNotificationIntent(Intent intent, boolean isUserLoggedIn) {
        if (intent != null && intent.getBooleanExtra("from_notification", false)) {
            String title = intent.getStringExtra("notification_title");
            String message = intent.getStringExtra("notification_message");
            String imageUrl = intent.getStringExtra("notification_imageUrl");

            Log.d(TAG, "Received notification - Title: " + title + ", Message: " + message);

            if (title != null && message != null) {
                // Save notification locally
                saveNotificationFromIntent(context, title, message, imageUrl != null ? imageUrl : "");

                // Navigate to NotificationViewActivity after a short delay
                new Handler().postDelayed(() -> {
                    if (isUserLoggedIn) {
                        // User is logged in, go directly to notification view
                        Intent notificationIntent = new Intent(context, NotificationViewActivity.class);
                        notificationIntent.putExtra("title", title);
                        notificationIntent.putExtra("message", message);
                        notificationIntent.putExtra("imageUrl", imageUrl);
                        context.startActivity(notificationIntent);
                    } else {
                        // User not logged in, show message
                        Toast.makeText(context, "Please login first", Toast.LENGTH_SHORT).show();
                    }
                }, 1000);
            }
        }
    }

    /**
     * Refresh all notification UI elements
     * Call this in onResume() to update counts
     */
    public void refresh() {
        updateBadge();
        updateDot();
    }

    /**
     * Get saved FCM token from SharedPreferences
     */
    public static String getSavedToken(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(USER_PREFS, Context.MODE_PRIVATE);
        return prefs.getString("device_token", "");
    }

    /**
     * Clear all notifications
     */
    public static void clearAllNotifications(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        prefs.edit().putString("list", "[]").apply();
        NotificationHelper.clearAllNotifications(context);
    }
}