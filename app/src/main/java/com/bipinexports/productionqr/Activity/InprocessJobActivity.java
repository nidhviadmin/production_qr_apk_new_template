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

import com.bipinexports.productionqr.InprocessDataObject;
import com.bipinexports.productionqr.R;
import com.bipinexports.productionqr.SessionManagement;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;


public class InprocessJobActivity extends BaseActivity implements View.OnClickListener {

    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private static String LOG_TAG = "CardViewActivity";

    SessionManagement session;
    TextView txtUser;
    ImageView imageView;
    String weekwisedataobj;
    JSONObject inprocessjobsweekwisejsonobj;
    String processorid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_inprocessjob);
        setContentView(R.layout.activity_base);
        setupDrawer();

        View content = getLayoutInflater().inflate(
                R.layout.activity_inprocessjob,
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
        processorid = getIntent().getStringExtra("processorid");

        mRecyclerView = (RecyclerView) content.findViewById(R.id.my_recycler_view);
        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mAdapter = new InprocessViewAdapter(getDataSet());
        mRecyclerView.setAdapter(mAdapter);

    }
    @Override
    protected void onResume() {
        super.onResume();
        ((InprocessViewAdapter) mAdapter).setOnItemClickListener(new InprocessViewAdapter.MyClickListener() {
            @Override
            public void onItemClick(int position, View v) {
            }
        });
    }

    private ArrayList<InprocessDataObject> getDataSet() {
        ArrayList results = new ArrayList<InprocessDataObject>();
        try {
            JSONObject jsonObj = new JSONObject(weekwisedataobj);
            inprocessjobsweekwisejsonobj = jsonObj.getJSONObject("inprocessjobs");
            Iterator<String> inprocessjobcontents = inprocessjobsweekwisejsonobj.keys();
            int i = 0;
            while (inprocessjobcontents.hasNext()) {
                String key = inprocessjobcontents.next();
                if (inprocessjobsweekwisejsonobj.get(key) instanceof JSONObject) {
                    int weekno = Integer.parseInt(((JSONObject) inprocessjobsweekwisejsonobj.get(key)).getString("week"));
                    int year = Integer.parseInt(((JSONObject) inprocessjobsweekwisejsonobj.get(key)).getString("year"));
                    String weekyearkey = ((JSONObject) inprocessjobsweekwisejsonobj.get(key)).getString("weekyearkey");
                    String inprocessbundlecnt = ((JSONObject) inprocessjobsweekwisejsonobj.get(key)).getString("inprocessbundlecnt");
                    String inprocessbundleqty = ((JSONObject) inprocessjobsweekwisejsonobj.get(key)).getString("inprocessbundleqty");
                    String inprocessprice = ((JSONObject) inprocessjobsweekwisejsonobj.get(key)).getString("inprocessprice");
                    InprocessDataObject obj = new InprocessDataObject(weekno, year, weekyearkey, inprocessbundlecnt, inprocessbundleqty, inprocessprice);
                    results.add(i, obj);
                    i++;
                }
            }
            InprocessDataObject obj = new InprocessDataObject(0, 0, "Grand Total", jsonObj.optString("grandtotalbundlecnt"), jsonObj.optString("grandtotalbundleqty"), jsonObj.optString("grandtotalprice"));
            results.add(i, obj);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return results;
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
        Intent intent = new Intent(InprocessJobActivity.this, SelectJobSummaryActivity.class);
        intent.putExtra("name", user.get(SessionManagement.KEY_USER));
        intent.putExtra("userid", user.get(SessionManagement.KEY_USER_ID));
        intent.putExtra("processorid", user.get(SessionManagement.KEY_PROCESSOR_ID));
        startActivity(intent);
        finish();
    }
}
