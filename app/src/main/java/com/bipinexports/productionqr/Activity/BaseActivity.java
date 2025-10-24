package com.bipinexports.productionqr.Activity;

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
        LinearLayout logout = findViewById(R.id.logout);
        ImageView imgClose = findViewById(R.id.img_close);
        TextView txtName = findViewById(R.id.txt_name);

        if (txtName != null)
            txtName.setText("Hello " + username);

        if (imgClose != null)
            imgClose.setOnClickListener(v -> drawerLayout.closeDrawer(GravityCompat.START));

        if (logout != null)
            logout.setOnClickListener(v -> {
                session.logoutUser();
                finish();
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
