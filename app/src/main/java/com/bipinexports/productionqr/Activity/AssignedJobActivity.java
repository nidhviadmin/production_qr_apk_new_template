package com.bipinexports.productionqr.Activity;

import android.content.Intent;
import android.os.Bundle;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bipinexports.productionqr.AssignedDataObject;
import com.bipinexports.productionqr.R;
import com.bipinexports.productionqr.SessionManagement;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

public class AssignedJobActivity extends BaseActivity implements View.OnClickListener {

    private View content;
    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    SessionManagement session;
    TextView txtUser;
    ImageView imageView;
    String weekwisedataobj;
    JSONObject assignedjobsweekwisejsonobj;
    String processorid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_base);
        setupDrawer();

        content = getLayoutInflater().inflate(
                R.layout.activity_assignedjob,
                findViewById(R.id.content_frame),
                true
        );

        weekwisedataobj = getIntent().getStringExtra("weekwisedataobj");

        txtUser = content.findViewById(R.id.txtUser);
        imageView = content.findViewById(R.id.imgd);

        session = new SessionManagement(getApplicationContext());
        HashMap<String, String> user = session.getUserDetails();
        String username = user.get(SessionManagement.KEY_USER);
        txtUser.setText("Hello " + username);

        imageView.setOnClickListener(this);

        setupNotifications();

        handleNotificationIntent(getIntent());
        processorid = getIntent().getStringExtra("processorid");

        mRecyclerView = content.findViewById(R.id.my_recycler_view);
        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mAdapter = new AssignedViewAdapter(getDataSet());
        mRecyclerView.setAdapter(mAdapter);

        ((AssignedViewAdapter) mAdapter).setOnItemClickListener((position, v) -> {
            // handle item click if needed
        });
    }

    private ArrayList<AssignedDataObject> getDataSet() {
        ArrayList results = new ArrayList<AssignedDataObject>();
        try {
            JSONObject jsonObj = new JSONObject(weekwisedataobj);
            assignedjobsweekwisejsonobj = jsonObj.getJSONObject("assignedjobs");
            Iterator<String> assignedjobcontents = assignedjobsweekwisejsonobj.keys();
            int i = 0;
            while (assignedjobcontents.hasNext()) {
                String key = assignedjobcontents.next();
                if (assignedjobsweekwisejsonobj.get(key) instanceof JSONObject) {
                    int weekno = Integer.parseInt(((JSONObject) assignedjobsweekwisejsonobj.get(key)).getString("week"));
                    int year = Integer.parseInt(((JSONObject) assignedjobsweekwisejsonobj.get(key)).getString("year"));
                    String weekyearkey = ((JSONObject) assignedjobsweekwisejsonobj.get(key)).getString("weekyearkey");
                    String assignedbundlecnt = ((JSONObject) assignedjobsweekwisejsonobj.get(key)).getString("assignedbundlecnt");
                    String assignedbundleqty = ((JSONObject) assignedjobsweekwisejsonobj.get(key)).getString("assignedbundleqty");
                    String assignedprice = ((JSONObject) assignedjobsweekwisejsonobj.get(key)).getString("assignedprice");
                    AssignedDataObject obj = new AssignedDataObject(weekno, year, weekyearkey, assignedbundlecnt, assignedbundleqty, assignedprice);
                    results.add(i, obj);
                    i++;
                }
            }
            AssignedDataObject obj = new AssignedDataObject(0, 0, "Grand Total",
                    jsonObj.optString("grandtotalbundlecnt"),
                    jsonObj.optString("grandtotalbundleqty"),
                    jsonObj.optString("grandtotalprice"));
            results.add(i, obj);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return results;
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.imgd) {
            toggleDrawer(); // open/close drawer
        }
    }

    @Override
    public void onBackPressed() {
        HashMap<String, String> user = session.getUserDetails();
        Intent intent = new Intent(this, SelectJobSummaryActivity.class);
        intent.putExtra("name", user.get(SessionManagement.KEY_USER));
        intent.putExtra("userid", user.get(SessionManagement.KEY_USER_ID));
        intent.putExtra("processorid", user.get(SessionManagement.KEY_PROCESSOR_ID));
        startActivity(intent);
        finish();
    }
}
