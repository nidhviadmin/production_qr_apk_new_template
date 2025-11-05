package com.bipinexports.productionqr.service;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class NotificationHelper {
    private static final String TAG = "NotificationHelper";
    private static final String PREFS_NAME = "NOTIFICATIONS";
    private static final String KEY_LIST = "list";
    private static final String KEY_UNREAD_COUNT = "unread_count";

    /**
     * Get the count of unread notifications
     */
    public static int getUnreadCount(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        return prefs.getInt(KEY_UNREAD_COUNT, 0);
    }

    /**
     * Increment unread count when a new notification arrives
     */
    public static void incrementUnreadCount(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        int currentCount = prefs.getInt(KEY_UNREAD_COUNT, 0);
        prefs.edit().putInt(KEY_UNREAD_COUNT, currentCount + 1).apply();
        Log.d(TAG, "Unread count incremented to: " + (currentCount + 1));
    }

    /**
     * Mark a specific notification as read by index
     */
    public static void markAsRead(Context context, int index) {
        try {
            SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
            String existing = prefs.getString(KEY_LIST, "[]");

            JSONArray array = new JSONArray(existing);
            if (index >= 0 && index < array.length()) {
                JSONObject obj = array.getJSONObject(index);
                if (!obj.optBoolean("isRead", false)) {
                    obj.put("isRead", true);
                    array.put(index, obj);
                    prefs.edit().putString(KEY_LIST, array.toString()).apply();

                    // Decrement unread count
                    int unreadCount = prefs.getInt(KEY_UNREAD_COUNT, 0);
                    if (unreadCount > 0) {
                        prefs.edit().putInt(KEY_UNREAD_COUNT, unreadCount - 1).apply();
                    }
                    Log.d(TAG, "Notification marked as read at index: " + index);
                }
            }
        } catch (JSONException e) {
            Log.e(TAG, "Error marking notification as read: " + e.getMessage());
        }
    }

    /**
     * Mark all notifications as read
     */
    public static void markAllAsRead(Context context) {
        try {
            SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
            String existing = prefs.getString(KEY_LIST, "[]");

            JSONArray array = new JSONArray(existing);
            for (int i = 0; i < array.length(); i++) {
                JSONObject obj = array.getJSONObject(i);
                obj.put("isRead", true);
                array.put(i, obj);
            }

            prefs.edit()
                    .putString(KEY_LIST, array.toString())
                    .putInt(KEY_UNREAD_COUNT, 0)
                    .apply();

            Log.d(TAG, "All notifications marked as read");
        } catch (JSONException e) {
            Log.e(TAG, "Error marking all as read: " + e.getMessage());
        }
    }

    /**
     * Get all notifications
     */
    public static JSONArray getAllNotifications(Context context) {
        try {
            SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
            String existing = prefs.getString(KEY_LIST, "[]");
            return new JSONArray(existing);
        } catch (JSONException e) {
            Log.e(TAG, "Error getting notifications: " + e.getMessage());
            return new JSONArray();
        }
    }

    /**
     * Clear all notifications and reset unread count
     */
    public static void clearAllNotifications(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        prefs.edit()
                .putString(KEY_LIST, "[]")
                .putInt(KEY_UNREAD_COUNT, 0)
                .apply();
        Log.d(TAG, "All notifications cleared and unread count reset");
    }

}