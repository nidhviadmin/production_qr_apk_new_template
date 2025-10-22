package com.bipinexports.productionqr.Activity;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bipinexports.productionqr.R;
import com.bipinexports.productionqr.SessionManagement;

import java.util.HashMap;

public class Empty_Activity extends AppCompatActivity implements View.OnClickListener{

    SessionManagement session;
    ProgressBar progress;
    TextView txtUser;
    ImageView imageView;

    int fromday = 0;
    int frommonth = 0;
    int fromyear = 0;

    int today = 0;
    int tomonth = 0;
    int toyear = 0;
    String startdate, enddate, app_type;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.empty_activity);
        progress = (ProgressBar) findViewById(R.id.progress);
        progress.setVisibility(View.GONE);

        txtUser = findViewById(R.id.txtUser);
        imageView = findViewById(R.id.imgd);

        session = new SessionManagement(getApplicationContext());
        HashMap<String, String> user = session.getUserDetails();
        String username = user.get(SessionManagement.KEY_USER);
        txtUser.setText("Hello " + username);

        imageView.setOnClickListener(this);
        app_type = getIntent().getStringExtra("empty_type");
        startdate = getIntent().getStringExtra("fromdate");
        enddate = getIntent().getStringExtra("todate");
        fromday = Integer.parseInt(getIntent().getStringExtra("fromday"));
        frommonth = Integer.parseInt(getIntent().getStringExtra("frommonth"));
        fromyear = Integer.parseInt(getIntent().getStringExtra("fromyear"));
        today = Integer.parseInt(getIntent().getStringExtra("today"));
        tomonth = Integer.parseInt(getIntent().getStringExtra("tomonth"));
        toyear = Integer.parseInt(getIntent().getStringExtra("toyear"));

        onBackPressed();
    }


    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.imgd:
                PopupMenu popup = new PopupMenu(com.bipinexports.productionqr.Activity.Empty_Activity.this, imageView);
                popup.getMenuInflater().inflate(R.menu.menu_main, popup.getMenu());

                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    public boolean onMenuItemClick(MenuItem item) {
                        if (item.getItemId() == R.id.log) {
                            session.logoutUser();
                            finish();
                        }
                        else if (item.getItemId() == R.id.changepassword) {
                            Intent intent = new Intent(com.bipinexports.productionqr.Activity.Empty_Activity.this, ChangepasswordActivity.class);
                            startActivity(intent);
                            finish();
                        }
                        return true;
                    }
                });
                popup.show();
                break;
        }
    }

    @Override
    public void onBackPressed() {
        session = new SessionManagement(getApplicationContext());
        HashMap<String, String> user = session.getUserDetails();

        Intent intent;

        intent = new Intent(com.bipinexports.productionqr.Activity.Empty_Activity.this, Overall_Datewise_Piece_Scanned_Activity.class);
        intent.putExtra("type", "Data");

        intent.putExtra("fromdate", startdate);
        intent.putExtra("todate", enddate);
        intent.putExtra("fromday", String.valueOf(fromday));
        intent.putExtra("frommonth", String.valueOf(frommonth));
        intent.putExtra("fromyear", String.valueOf(fromyear));
        intent.putExtra("today", String.valueOf(today));
        intent.putExtra("tomonth", String.valueOf(tomonth));
        intent.putExtra("toyear", String.valueOf(toyear));
        startActivity(intent);
        finish();

    }
}
