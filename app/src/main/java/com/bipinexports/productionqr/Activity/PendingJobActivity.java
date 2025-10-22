package com.bipinexports.productionqr.Activity;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;

import com.bipinexports.productionqr.PendingDataObject;
import com.bipinexports.productionqr.R;
import com.bipinexports.productionqr.SessionManagement;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;


public class PendingJobActivity extends AppCompatActivity implements View.OnClickListener {

    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private static String LOG_TAG = "CardViewActivity";

    SessionManagement session;
    TextView txtUser;
    ImageView imageView;
    String weekwisedataobj;
    JSONObject pendingjobsweekwisejsonobj;
    String processorid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pendingjob);

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
        mAdapter = new PendingViewAdapter(getDataSet());
        mRecyclerView.setAdapter(mAdapter);

    }
    @Override
    protected void onResume() {
        super.onResume();
        ((PendingViewAdapter) mAdapter).setOnItemClickListener(new PendingViewAdapter.MyClickListener() {
            @Override
            public void onItemClick(int position, View v) {
//                int week =((PendingViewAdapter) mAdapter).getweek(position);
//                int year =((PendingViewAdapter) mAdapter).getYear(position);
//                Intent intent = new Intent(PendingJobActivity.this, PendingJobActivity.class);
//                intent.putExtra("week", week);
//                intent.putExtra("year", year);
//                intent.putExtra("processorid", processorid);
//                startActivity(intent);
//                finish();
            }
        });
    }

    private ArrayList<PendingDataObject> getDataSet() {
        ArrayList results = new ArrayList<PendingDataObject>();
        try {
            JSONObject jsonObj = new JSONObject(weekwisedataobj);
            pendingjobsweekwisejsonobj = jsonObj.getJSONObject("pendingjobs");
            Iterator<String> pendingjobcontents = pendingjobsweekwisejsonobj.keys();
            int i = 0;
            while (pendingjobcontents.hasNext()) {
                String key = pendingjobcontents.next();
                if (pendingjobsweekwisejsonobj.get(key) instanceof JSONObject) {
                    int weekno = Integer.parseInt(((JSONObject) pendingjobsweekwisejsonobj.get(key)).getString("week"));
                    int year = Integer.parseInt(((JSONObject) pendingjobsweekwisejsonobj.get(key)).getString("year"));
                    String weekyearkey = ((JSONObject) pendingjobsweekwisejsonobj.get(key)).getString("weekyearkey");
                    String pendingbundlecnt = ((JSONObject) pendingjobsweekwisejsonobj.get(key)).getString("pendingbundlecnt");
                    String pendingbundleqty = ((JSONObject) pendingjobsweekwisejsonobj.get(key)).getString("pendingbundleqty");
                    String pendingprice = ((JSONObject) pendingjobsweekwisejsonobj.get(key)).getString("pendingprice");
                    PendingDataObject obj = new PendingDataObject(weekno, year, weekyearkey, pendingbundlecnt, pendingbundleqty, pendingprice);
                    results.add(i, obj);
                    i++;
                }
            }
            PendingDataObject obj = new PendingDataObject(0, 0, "Grand Total", jsonObj.optString("grandtotalbundlecnt"), jsonObj.optString("grandtotalbundleqty"), jsonObj.optString("grandtotalprice"));
            results.add(i, obj);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return results;
    }

    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.imgd:
                PopupMenu popup = new PopupMenu(PendingJobActivity.this, imageView);
                popup.getMenuInflater().inflate(R.menu.menu_main, popup.getMenu());

                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    public boolean onMenuItemClick(MenuItem item) {
                        if (item.getItemId() == R.id.log) {
                            session.logoutUser();
                            finish();
                        }
                        else if (item.getItemId() == R.id.changepassword) {
                            Intent intent = new Intent(PendingJobActivity.this, ChangepasswordActivity.class);
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
        Intent intent = new Intent(PendingJobActivity.this, SelectJobSummaryActivity.class);
        intent.putExtra("name", user.get(SessionManagement.KEY_USER));
        intent.putExtra("userid", user.get(SessionManagement.KEY_USER_ID));
        intent.putExtra("processorid", user.get(SessionManagement.KEY_PROCESSOR_ID));
        startActivity(intent);
        finish();
    }
}
