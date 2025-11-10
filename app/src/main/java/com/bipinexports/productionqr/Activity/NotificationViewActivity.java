package com.bipinexports.productionqr.Activity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bipinexports.productionqr.R;
import com.bipinexports.productionqr.service.NotificationHelper;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class NotificationViewActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    NotificationAdapter adapter;
    List<NotificationModel> list = new ArrayList<>();
    ImageView clearAllBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification_view);

        recyclerView = findViewById(R.id.recyclerNotifications);
        clearAllBtn = findViewById(R.id.imgClearAll);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        loadNotifications();
        adapter = new NotificationAdapter(this, list);
        recyclerView.setAdapter(adapter);

        clearAllBtn.setOnClickListener(v -> {
            new AlertDialog.Builder(this)
                    .setTitle("Delete All Notifications")
                    .setMessage("Are you sure you want to clear all notifications?")
                    .setPositiveButton("Yes", (dialog, which) -> {
                        NotificationHelper.clearAllNotifications(this);
                        list.clear();
                        adapter.notifyDataSetChanged();
                    })
                    .setNegativeButton("Cancel", null)
                    .show();
        });
    }

    private void loadNotifications() {
        list.clear();
        SharedPreferences prefs = getSharedPreferences("NOTIFICATIONS", MODE_PRIVATE);
        String json = prefs.getString("list", "[]");

        try {
            JSONArray arr = new JSONArray(json);
            for (int i = 0; i < arr.length(); i++) {
                JSONObject obj = arr.getJSONObject(i);
                list.add(new NotificationModel(
                        obj.optString("title"),
                        obj.optString("message"),
                        obj.optString("imageUrl"),
                        obj.optBoolean("isRead", false)
                ));
            }
        } catch (Exception e) {
            Log.e("NotificationView", "Error loading notifications: " + e.getMessage());
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadNotifications();
        if (adapter != null) adapter.notifyDataSetChanged();
    }
}
