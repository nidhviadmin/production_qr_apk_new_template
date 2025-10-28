package com.bipinexports.productionqrnew.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bipinexports.productionqrnew.R;
import com.bipinexports.productionqrnew.SessionManagement;

import java.util.HashMap;

public class Empty_Employee_Data_Activity extends BaseActivity implements View.OnClickListener{

    SessionManagement session;
    ProgressBar progress;
    TextView txtUser;
    ImageView imageView;
    String processorid, type, myversionName, selected_type;
    public static CustPrograssbar_new custPrograssbar_new;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.empty_activity);
        setContentView(R.layout.activity_base);
        setupDrawer();

        View content = getLayoutInflater().inflate(
                R.layout.empty_activity,
                findViewById(R.id.content_frame),
                true
        );
        progress = (ProgressBar) content.findViewById(R.id.progress);
        progress.setVisibility(View.GONE);
        custPrograssbar_new = new CustPrograssbar_new();
        Empty_Employee_Data_Activity.custPrograssbar_new.prograssCreate(this);

        txtUser = content.findViewById(R.id.txtUser);
        imageView = content.findViewById(R.id.imgd);

        session = new SessionManagement(getApplicationContext());
        HashMap<String, String> user = session.getUserDetails();
        String username = user.get(SessionManagement.KEY_USER);
        txtUser.setText("Hello " + username);

        imageView.setOnClickListener(this);
        processorid = getIntent().getStringExtra("processorid");
        type = getIntent().getStringExtra("type");
        myversionName = getIntent().getStringExtra("myversionName");
        selected_type = getIntent().getStringExtra("selected_type");

        onBackPressed();
    }


    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.imgd:
                toggleDrawer();
                break;
        }
    }

    @Override
    public void onBackPressed() {
        session = new SessionManagement(getApplicationContext());
        HashMap<String, String> user = session.getUserDetails();

        Intent intent = null;

        if(selected_type.equals("In"))
        {
            intent = new Intent(Empty_Employee_Data_Activity.this, In_Employee_Operation_Mapping_Activity.class);
        }
        else  if(selected_type.equals("Out"))
        {
            intent = new Intent(Empty_Employee_Data_Activity.this, Out_Employee_Operation_Mapping_Activity.class);
        }
        else  if(selected_type.equals("Absent"))
        {
            intent = new Intent(Empty_Employee_Data_Activity.this, Absent_Employee_Operation_Mapping_Activity.class);
        }
        else  if(selected_type.equals("Bundle_Mapp"))
        {
            intent = new Intent(Empty_Employee_Data_Activity.this, In_Employee_Bundle_Scan_Activity.class);
        }
        intent.putExtra("type", type);
        intent.putExtra("processorid", processorid);
        intent.putExtra("myversionName", myversionName);
        intent.putExtra("selected_type", selected_type);
        startActivity(intent);
        Empty_Employee_Data_Activity.custPrograssbar_new.closePrograssBar();
        finish();

    }
}
