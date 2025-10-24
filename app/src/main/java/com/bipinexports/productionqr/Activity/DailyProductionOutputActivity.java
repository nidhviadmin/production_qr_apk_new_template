package com.bipinexports.productionqr.Activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bipinexports.productionqr.APIClient;
import com.bipinexports.productionqr.DailyOutputDataObject;
import com.bipinexports.productionqr.GetResult;
import com.bipinexports.productionqr.ModelClass;
import com.bipinexports.productionqr.R;
import com.bipinexports.productionqr.SessionManagement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
;
import retrofit2.Call;


public class DailyProductionOutputActivity extends AppCompatActivity implements View.OnClickListener, GetResult.MyListener {

    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private static String LOG_TAG = "CardViewActivity";

    SessionManagement session;
    TextView txtUser;
    ImageView imageView;
    String dailydataobj;
    JSONObject dailyoutputjsonobj;
    String processorid, selecteddate;
    String userid, User, Id;
    ProgressBar progress;
    TextView txtDate;

    private boolean loading = true;
    int totalItemCount;
    int startindex = 0;
    int endindex = 10;

    int endpos = 50;

    private boolean finalloading = true;

    private boolean grandtotreach = false;
    ArrayList results = new ArrayList<DailyOutputDataObject>();
    int i = 0;


    String fromdate, todate, fromday, frommonth, fromyear, today, tomonth, toyear;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dailyproductionoutput);


        txtUser = findViewById(R.id.txtUser);
        imageView = findViewById(R.id.imgd);
        progress = (ProgressBar) findViewById(R.id.progress);
        progress.setVisibility(View.VISIBLE);

        txtUser = (TextView) findViewById(R.id.txtUser);

        session = new SessionManagement(getApplicationContext());
        HashMap<String, String> user = session.getUserDetails();
        String name = user.get(SessionManagement.KEY_USER);
        this.Id = user.get(SessionManagement.KEY_PROCESSOR_ID);
        processorid = user.get(SessionManagement.KEY_PROCESSOR_ID);
        userid = user.get(SessionManagement.KEY_USER_ID);
        this.User = user.get(SessionManagement.KEY_USER);

        processorid = getIntent().getStringExtra("processorid");
        selecteddate = getIntent().getStringExtra("selecteddate");

        fromdate = getIntent().getStringExtra("fromdate");
        todate = getIntent().getStringExtra("todate");

        fromday = getIntent().getStringExtra("fromday");
        frommonth = getIntent().getStringExtra("frommonth");
        fromyear = getIntent().getStringExtra("fromyear");
        today = getIntent().getStringExtra("today");
        tomonth = getIntent().getStringExtra("tomonth");
        toyear = getIntent().getStringExtra("toyear");

        txtDate = (TextView) findViewById(R.id.txtDate);
        txtDate.setText("OutputDate  - " +selecteddate);

        imageView.setOnClickListener(this);


        getvalue();
        fetchDailyoutputDetails();

    }

    private ArrayList<DailyOutputDataObject> getDataSet() {
//        ArrayList results = new ArrayList<DailyOutputDataObject>();
        try {
            JSONObject jsonObj = new JSONObject(dailydataobj);
            dailyoutputjsonobj = jsonObj.getJSONObject("dailyoutput");
            Iterator<String> datewiseoutputcontents = dailyoutputjsonobj.keys();

            while (datewiseoutputcontents.hasNext()) {
                String key = datewiseoutputcontents.next();
                if (dailyoutputjsonobj.get(key) instanceof JSONObject) {
                    String date =  ((JSONObject) dailyoutputjsonobj.get(key)).getString("date");
                    String bcnt = ((JSONObject) dailyoutputjsonobj.get(key)).getString("bcnt");
                    String bqty = ((JSONObject) dailyoutputjsonobj.get(key)).getString("bqty");
                    String price = ((JSONObject) dailyoutputjsonobj.get(key)).getString("price");
                    DailyOutputDataObject obj = new DailyOutputDataObject(date, bcnt, bqty, price);
                    results.add(i, obj);
                    i++;
                }
            }
            totalItemCount = Integer.parseInt(jsonObj.optString("totalcount"));
            if(totalItemCount < 10)
            {
                endindex = totalItemCount;
            }
            if(totalItemCount == endindex && grandtotreach==false)
            {
                DailyOutputDataObject obj = new DailyOutputDataObject("Grand Total", jsonObj.optString("grandtotalbundlecnt"), jsonObj.optString("grandtotalbundleqty"), jsonObj.optString("grandtotalprice"));
                results.add(i, obj);
                grandtotreach = true;
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return results;
    }


    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.imgd:
                HashMap<String, String> user = session.getUserDetails();
                String username = user.get(SessionManagement.KEY_USER);
                String userid = user.get(SessionManagement.KEY_USER_ID);

                Intent intent = new Intent(DailyProductionOutputActivity.this, HomeActivity.class);
                intent.putExtra("openDrawer", true);
                intent.putExtra("username", username);
                intent.putExtra("userid", userid);
                intent.putExtra("processorid", processorid);
                startActivity(intent);
                break;
        }
    }

    private void fetchDailyoutputDetails() {

        session = new SessionManagement(getApplicationContext());
        HashMap<String, String> user = session.getUserDetails();
        processorid = user.get(SessionManagement.KEY_PROCESSOR_ID);
        userid = user.get(SessionManagement.KEY_USER_ID);

        JSONObject jsonObject = new JSONObject();
        try {

            jsonObject.put("userid", userid);
            jsonObject.put("processorid", processorid);
            jsonObject.put("currentdate", selecteddate);
            jsonObject.put("startindex", startindex);
            jsonObject.put("endindex", endindex);
            jsonObject.put("limit", endindex);
            jsonObject.put("offset", startindex);

            JsonParser jsonParser = new JsonParser();
            Call<JsonObject> call = APIClient.getInterface().fetchdailyrpoductionoutputdetails((JsonObject) jsonParser.parse(jsonObject.toString()));
            GetResult getResult = new GetResult();
            getResult.setMyListener(this);
            getResult.callForLogin(call, "fetchdailyrpoductionoutputdetails");
        }
        catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void fetchscrolldata() {
        session = new SessionManagement(getApplicationContext());
        HashMap<String, String> user = session.getUserDetails();
        processorid = user.get(SessionManagement.KEY_PROCESSOR_ID);
        userid = user.get(SessionManagement.KEY_USER_ID);

        JSONObject jsonObject = new JSONObject();
        try {

            jsonObject.put("userid", userid);
            jsonObject.put("processorid", processorid);
            jsonObject.put("currentdate", selecteddate);
            jsonObject.put("startindex", startindex);
            jsonObject.put("endindex", endindex);
            jsonObject.put("limit", endindex);
            jsonObject.put("offset", startindex);

            JsonParser jsonParser = new JsonParser();
            Call<JsonObject> call = APIClient.getInterface().fetchdailyrpoductionoutputdetails((JsonObject) jsonParser.parse(jsonObject.toString()));
            GetResult getResult = new GetResult();
            getResult.setMyListener(this);
            getResult.callForLogin(call, "fetchdaily_poduction_output_details");
        }
        catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void getvalue() {
        txtUser.setText("Hello " + this.User);
        ModelClass modelClass = new ModelClass();
        modelClass.setmID(userid);
        progress.setVisibility(View.INVISIBLE);
    }
    @Override
    public void onBackPressed() {
        session = new SessionManagement(getApplicationContext());
        HashMap<String, String> user = session.getUserDetails();
        Intent intent = new Intent(DailyProductionOutputActivity.this, DatewiseOutputActivity.class);
        intent.putExtra("name", user.get(SessionManagement.KEY_USER));
        intent.putExtra("userid", user.get(SessionManagement.KEY_USER_ID));
        intent.putExtra("processorid", user.get(SessionManagement.KEY_PROCESSOR_ID));
        intent.putExtra("startdate", fromdate);
        intent.putExtra("enddate", todate);
        intent.putExtra("fromday", fromday);
        intent.putExtra("frommonth", frommonth);
        intent.putExtra("fromyear", fromyear);
        intent.putExtra("today", today);
        intent.putExtra("tomonth", tomonth);
        intent.putExtra("toyear", toyear);
        intent.putExtra("type", "Data");
        startActivity(intent);
        finish();
    }

    @Override
    public void callback(JsonObject result, String callNo) {
        try {
            progress.setVisibility(View.GONE);

            if (callNo.equalsIgnoreCase("fetchdailyrpoductionoutputdetails")) {
                JSONObject jsonObject = new JSONObject(result.toString());
                String mStatus = jsonObject.optString("status");
                String message = jsonObject.optString("message");
                if (mStatus.equals("success")) {
                    dailydataobj = jsonObject.getJSONObject("data").toString();
                    progress.setVisibility(View.GONE);

                    mRecyclerView = (RecyclerView) findViewById(R.id.my_recycler_view);
                    mRecyclerView.setHasFixedSize(true);
                    mLayoutManager = new LinearLayoutManager(DailyProductionOutputActivity.this);
                    mRecyclerView.setLayoutManager(mLayoutManager);
                    mAdapter = new DailyoutputViewAdapter(getDataSet());
                    mRecyclerView.setAdapter(mAdapter);

                    if(totalItemCount >10)
                    {
                        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
                            @Override
                            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                                if (dy > endpos && loading && finalloading && grandtotreach == false) { //check for scroll down

                                    startindex = endindex;
                                    endindex += 10;
                                    if(endindex > totalItemCount)
                                    {
                                        endindex = totalItemCount;
                                        finalloading = false;
                                    }
                                    if (loading) {
                                        if (endindex <= totalItemCount) {
                                            loading = false;
                                            progress.setVisibility(View.VISIBLE);
                                            new CountDownTimer(1000, 1000) {
                                                public void onTick(long millisUntilFinished) {
                                                }
                                                public void onFinish() {
                                                    fetchscrolldata();
                                                    loading = true;
                                                    if(finalloading) {
                                                        endpos += 10;
                                                    }
                                                }
                                            }.start();

                                        }
                                    }
                                }
                            }
                        });
                    }
                }
                else if (mStatus.equals("nodatafound")) {
                    if(mAdapter == null)
                    {
                        // Do nothing
                    }
                    new AlertDialog.Builder(DailyProductionOutputActivity.this)
                            .setMessage(message)
                            .setCancelable(false)
                            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface arg1, int arg0) {
                                    arg1.dismiss();
                                }
                            }).show();
                }
                else {
                    new AlertDialog.Builder(DailyProductionOutputActivity.this)
                            .setMessage(message)
                            .setCancelable(false)
                            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface arg1, int arg0) {
                                    arg1.dismiss();
                                }
                            }).show();
                }
            }
            else  if (callNo.equalsIgnoreCase("fetchdaily_poduction_output_details")) {
                JSONObject jsonObject = new JSONObject(result.toString());
                String mStatus = jsonObject.optString("status");
                String message = jsonObject.optString("message");

                if (mStatus.equals("success")) {
                    dailydataobj = jsonObject.getJSONObject("data").toString();
                    JSONObject jsonObj = new JSONObject(dailydataobj);
                    dailyoutputjsonobj = jsonObj.getJSONObject("dailyoutput");
                    Iterator<String> datewiseoutputcontents = dailyoutputjsonobj.keys();
                    while (datewiseoutputcontents.hasNext()) {
                        String key = datewiseoutputcontents.next();
                        if (dailyoutputjsonobj.get(key) instanceof JSONObject) {
                            String date =  ((JSONObject) dailyoutputjsonobj.get(key)).getString("date");
                            String bcnt = ((JSONObject) dailyoutputjsonobj.get(key)).getString("bcnt");
                            String bqty = ((JSONObject) dailyoutputjsonobj.get(key)).getString("bqty");
                            String price = ((JSONObject) dailyoutputjsonobj.get(key)).getString("price");
                            DailyOutputDataObject obj = new DailyOutputDataObject(date, bcnt, bqty, price);
                            results.add(i, obj);
                            i++;
                        }
                    }
                    totalItemCount = Integer.parseInt(jsonObj.optString("totalcount"));
                    if(totalItemCount == endindex && grandtotreach==false)
                    {
                        DailyOutputDataObject obj = new DailyOutputDataObject("Grand Total", jsonObj.optString("grandtotalbundlecnt"), jsonObj.optString("grandtotalbundleqty"), jsonObj.optString("grandtotalprice"));
                        results.add(i, obj);
                        grandtotreach = true;
                    }
                    mAdapter.notifyDataSetChanged();
                }
                else if (mStatus.equals("nodatafound")) {
                    if(mAdapter == null)
                    {
                        // Do nothing
                    }
                    new AlertDialog.Builder(DailyProductionOutputActivity.this)
                            .setMessage(message)
                            .setCancelable(false)
                            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface arg1, int arg0) {
                                    arg1.dismiss();
                                }
                            }).show();
                }
                else {
                    new AlertDialog.Builder(DailyProductionOutputActivity.this)
                            .setMessage(message)
                            .setCancelable(false)
                            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface arg1, int arg0) {
                                    arg1.dismiss();
                                }
                            }).show();
                }
            }
        }
        catch (Exception e) {
        }
    }

}
