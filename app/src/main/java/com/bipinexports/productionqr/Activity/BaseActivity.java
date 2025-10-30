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

import java.util.HashMap;

public abstract class BaseActivity extends AppCompatActivity {

    protected DrawerLayout drawerLayout;
    protected SessionManagement session;
    protected String username, userid, processorid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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
