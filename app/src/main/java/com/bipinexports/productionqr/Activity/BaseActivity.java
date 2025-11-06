package com.bipinexports.productionqr.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.bipinexports.productionqr.R;
import com.bipinexports.productionqr.SessionManagement;
import com.bipinexports.productionqr.utils.NotificationManager;

import java.util.HashMap;

public abstract class BaseActivity extends AppCompatActivity {

    protected DrawerLayout drawerLayout;
//    protected SessionManagement session;
    protected String username, userid, processorid;
    protected NotificationManager notificationManager;
    protected SessionManagement session;

    /**
     * Setup notifications with FCM token registration
     * Call this in child activity's onCreate()
     */
    protected void setupNotificationsWithFCM() {
        session = new SessionManagement(getApplicationContext());
        HashMap<String, String> user = session.getUserDetails();
        String userid = user.get(SessionManagement.KEY_USER_ID);

        notificationManager = new NotificationManager(this);

        ImageView notifyIcon = findViewById(R.id.notify);
        TextView notificationCount = findViewById(R.id.notificationCount);

        if (notifyIcon != null && notificationCount != null) {
            notificationManager.setupComplete(notifyIcon, notificationCount, userid);
        }
    }

    protected void setupNotifications() {
        notificationManager = new NotificationManager(this);

        ImageView notifyIcon = findViewById(R.id.notify);
        TextView notificationCount = findViewById(R.id.notificationCount);

        if (notifyIcon != null && notificationCount != null) {
            notificationManager.setupNotifications(notifyIcon, notificationCount);
        }
    }

    /**
     * Setup notifications with dot indicator
     */
    protected void setupNotificationsWithDot(View notificationDot) {
        session = new SessionManagement(getApplicationContext());
        HashMap<String, String> user = session.getUserDetails();
        String userid = user.get(SessionManagement.KEY_USER_ID);

        notificationManager = new NotificationManager(this);

        ImageView notifyIcon = findViewById(R.id.notify);
        TextView notificationCount = findViewById(R.id.notificationCount);

        if (notifyIcon != null && notificationCount != null) {
            notificationManager.setupWithDot(notifyIcon, notificationCount, notificationDot, userid);
        }
    }

    /**
     * Handle notification intent
     * Call this in onCreate() and onNewIntent()
     */
    protected void handleNotificationIntent(Intent intent) {
        if (notificationManager != null) {
            notificationManager.handleNotificationIntent(intent, isUserLoggedIn());
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (notificationManager != null) {
            notificationManager.refresh();
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        handleNotificationIntent(intent);
    }

    protected boolean isUserLoggedIn() {
        session = new SessionManagement(getApplicationContext());
        return session.isLoggedIn();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            getWindow().getDecorView().setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
            );
        }

        session = new SessionManagement(getApplicationContext());

        HashMap<String, String> user = session.getUserDetails();
        username = user.get(SessionManagement.KEY_USER);
        userid = user.get(SessionManagement.KEY_USER_ID);
        processorid = user.get(SessionManagement.KEY_PROCESSOR_ID);
    }

    protected void setupDrawer() {
        drawerLayout = findViewById(R.id.drawer_layout);
        LinearLayout lvlHome = findViewById(R.id.lvl_home);
        LinearLayout lvlChangePassword = findViewById(R.id.lvl_change_password);
        LinearLayout logout = findViewById(R.id.logout);
        TextView txtName = findViewById(R.id.txt_name);
        TextView txtVersion = findViewById(R.id.txt_version);
        ImageView imgClose = findViewById(R.id.img_close);

        // Set username
        if (txtName != null)
            txtName.setText("Hello " + username);

        // Set version dynamically
        if (txtVersion != null) {
            try {
                String version = getPackageManager()
                        .getPackageInfo(getPackageName(), 0).versionName;
                txtVersion.setText("Version " + version);
            } catch (Exception e) {
                e.printStackTrace();
                txtVersion.setText("Version");
            }
        }

        // Close drawer
        if (imgClose != null)
            imgClose.setOnClickListener(v -> drawerLayout.closeDrawer(GravityCompat.START));

        // Logout
        if (logout != null)
            logout.setOnClickListener(v -> {
                session.logoutUser();
                finish();
            });

        // Home click
        if (lvlHome != null)
            lvlHome.setOnClickListener(v -> {
                startActivity(new Intent(this, MainActivity.class));
                drawerLayout.closeDrawer(GravityCompat.START);
            });

        // Change Password click
        if (lvlChangePassword != null)
            lvlChangePassword.setOnClickListener(v -> {
                startActivity(new Intent(this, ChangepasswordActivity.class));
                drawerLayout.closeDrawer(GravityCompat.START);
            });
    }

    protected void toggleDrawer() {
        if (drawerLayout != null) {
            if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
                drawerLayout.closeDrawer(GravityCompat.START);
            } else {
                drawerLayout.openDrawer(GravityCompat.START);
            }
        }
    }
}
