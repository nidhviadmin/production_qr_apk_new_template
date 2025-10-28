package com.bipinexports.productionqrnew.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bipinexports.productionqrnew.R;

public class HomeActivity extends BaseActivity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_base);
        setupDrawer();

        // Inflate only the home content inside the drawer frame
        View content = getLayoutInflater().inflate(R.layout.activity_home,
                findViewById(R.id.content_frame), true);

        ImageView menuButton = content.findViewById(R.id.imgd);
        if (menuButton != null) menuButton.setOnClickListener(v -> toggleDrawer());

        TextView txtUser = content.findViewById(R.id.txtUser);
        if (txtUser != null) txtUser.setText("Hello " + username);

        View forgotPasswordLayout = content.findViewById(R.id.lvl_forgotpassword);
        if (forgotPasswordLayout != null) forgotPasswordLayout.setOnClickListener(this);
    }


    @Override
    public void onClick(View v) {
        int id = v.getId();

        if (id == R.id.lvl_home) {
            Intent intent = new Intent(HomeActivity.this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(intent);
            finish();
        } else if (id == R.id.logout) {
            session.logoutUser();
            finish();
        } else if (id == R.id.lvl_forgotpassword) {
            Intent intent = new Intent(HomeActivity.this, ChangepasswordActivity.class);
            startActivity(intent);
        }
    }

    @Override
    public void onBackPressed() {
        if (drawerLayout != null && drawerLayout.isDrawerOpen(androidx.core.view.GravityCompat.START)) {
            drawerLayout.closeDrawer(androidx.core.view.GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }
}
