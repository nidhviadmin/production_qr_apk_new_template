package com.bipinexports.productionqr.Activity;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;

import com.bipinexports.productionqr.CompletedDataObject;
import com.bipinexports.productionqr.R;
import com.bipinexports.productionqr.SessionManagement;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;


public class CompletedJobActivity extends AppCompatActivity implements View.OnClickListener {

    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private static String LOG_TAG = "CardViewActivity";

    SessionManagement session;
    TextView txtUser;
    ImageView imageView;
    String weekwisedataobj;
    JSONObject completedjobsweekwisejsonobj;
    String processorid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_completedjob);

        weekwisedataobj = getIntent().getStringExtra("weekwisedataobj");

        txtUser = findViewById(R.id.txtUser);
        imageView = findViewById(R.id.imgd);

        session = new SessionManagement(getApplicationContext());
        HashMap<String, String> user = session.getUserDetails();
        String username = user.get(SessionManagement.KEY_USER);
        txtUser.setText("Hello " + username);

        imageView.setOnClickListener(this);
        processorid = getIntent().getStringExtra("processorid");

        mRecyclerView = (RecyclerView) findViewById(R.id.my_recycler_view);
        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mAdapter = new CompletedViewAdapter(getDataSet());
        mRecyclerView.setAdapter(mAdapter);

    }
    @Override
    protected void onResume() {
        super.onResume();
        ((CompletedViewAdapter) mAdapter).setOnItemClickListener(new CompletedViewAdapter.MyClickListener() {
            @Override
            public void onItemClick(int position, View v) {
            }
        });
    }

    private ArrayList<CompletedDataObject> getDataSet() {
        ArrayList results = new ArrayList<CompletedDataObject>();
        try {
            JSONObject jsonObj = new JSONObject(weekwisedataobj);
            completedjobsweekwisejsonobj = jsonObj.getJSONObject("completedjobs");
            Iterator<String> completedjobcontents = completedjobsweekwisejsonobj.keys();
            int i = 0;
            while (completedjobcontents.hasNext()) {
                String key = completedjobcontents.next();
                if (completedjobsweekwisejsonobj.get(key) instanceof JSONObject) {
                    int weekno = Integer.parseInt(((JSONObject) completedjobsweekwisejsonobj.get(key)).getString("week"));
                    int year = Integer.parseInt(((JSONObject) completedjobsweekwisejsonobj.get(key)).getString("year"));
                    String weekyearkey = ((JSONObject) completedjobsweekwisejsonobj.get(key)).getString("weekyearkey");
                    String completedbundlecnt = ((JSONObject) completedjobsweekwisejsonobj.get(key)).getString("completedbundlecnt");
                    String completedbundleqty = ((JSONObject) completedjobsweekwisejsonobj.get(key)).getString("completedbundleqty");
                    String completedprice = ((JSONObject) completedjobsweekwisejsonobj.get(key)).getString("completedprice");
                    CompletedDataObject obj = new CompletedDataObject(weekno, year, weekyearkey, completedbundlecnt, completedbundleqty, completedprice);
                    results.add(i, obj);
                    i++;
                }
            }
            CompletedDataObject obj = new CompletedDataObject(0, 0, "Grand Total", jsonObj.optString("grandtotalbundlecnt"), jsonObj.optString("grandtotalbundleqty"), jsonObj.optString("grandtotalprice"));
            results.add(i, obj);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return results;
    }

    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.imgd:
                PopupMenu popup = new PopupMenu(CompletedJobActivity.this, imageView);
                popup.getMenuInflater().inflate(R.menu.menu_main, popup.getMenu());

                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    public boolean onMenuItemClick(MenuItem item) {
                        if (item.getItemId() == R.id.log) {
                            session.logoutUser();
                            finish();
                        }
                        else if (item.getItemId() == R.id.changepassword) {
                            Intent intent = new Intent(CompletedJobActivity.this, ChangepasswordActivity.class);
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
        Intent intent = new Intent(CompletedJobActivity.this, SelectJobSummaryActivity.class);
        intent.putExtra("name", user.get(SessionManagement.KEY_USER));
        intent.putExtra("userid", user.get(SessionManagement.KEY_USER_ID));
        intent.putExtra("processorid", user.get(SessionManagement.KEY_PROCESSOR_ID));
        startActivity(intent);
        finish();
    }
}
