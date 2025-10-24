package com.bipinexports.productionqr.Activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bipinexports.productionqr.APIClient;
import com.bipinexports.productionqr.Fabric_Receipt_Data_Object;
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

import retrofit2.Call;


public class Fabric_Receipt_Activity extends AppCompatActivity implements View.OnClickListener, GetResult.MyListener {

    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private static String LOG_TAG = "CardViewActivity";

    SessionManagement session;
    TextView txtUser;
    ImageView imageView;
    String fabric_data_obj, delivery;
    JSONObject fabric_receipt_jsonobj;
    String processorid;
    String userid, User, Id;
    ProgressBar progress;

    private boolean loading = true;
    int totalItemCount;
    int startindex = 0;
    int endindex = 10;
    int endpos = 50;
    private boolean finalloading = true;
    String pendingcount;

    public static CustPrograssbar_new custPrograssbar_new;

    ArrayList results = new ArrayList<Fabric_Receipt_Data_Object>();
    int i = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fabric_receipt);

        txtUser = findViewById(R.id.txtUser);
        imageView = findViewById(R.id.imgd);
        progress = (ProgressBar) findViewById(R.id.progress);
        progress.setVisibility(View.GONE);

        custPrograssbar_new = new CustPrograssbar_new();

        txtUser = (TextView) findViewById(R.id.txtUser);

        session = new SessionManagement(getApplicationContext());
        HashMap<String, String> user = session.getUserDetails();
        String name = user.get(SessionManagement.KEY_USER);
        this.Id = user.get(SessionManagement.KEY_PROCESSOR_ID);
        processorid = user.get(SessionManagement.KEY_PROCESSOR_ID);
        userid = user.get(SessionManagement.KEY_USER_ID);
        this.User = user.get(SessionManagement.KEY_USER);

        processorid = getIntent().getStringExtra("processorid");
        imageView.setOnClickListener(this);

        delivery = getIntent().getStringExtra("delivery");
        pendingcount = getIntent().getStringExtra("pendingcount");
        totalItemCount = Integer.parseInt(pendingcount);

        getvalue();
        fetch_fabric_receipt_Details();
    }


    private ArrayList<Fabric_Receipt_Data_Object> getDataSet() {
//        ArrayList results = new ArrayList<Fabric_Receipt_Data_Object>();
        try {
            JSONObject jsonObj = new JSONObject(fabric_data_obj);
            fabric_receipt_jsonobj = jsonObj.getJSONObject("deliveries");
            Iterator<String> accessory_receipt_contents = fabric_receipt_jsonobj.keys();

            while (accessory_receipt_contents.hasNext()) {
                String key = accessory_receipt_contents.next();
                if (fabric_receipt_jsonobj.get(key) instanceof JSONObject) {
                    int dcid = Integer.parseInt(((JSONObject) fabric_receipt_jsonobj.get(key)).getString("dcid"));
                    String dcdate = ((JSONObject) fabric_receipt_jsonobj.get(key)).getString("dcdate");
                    String dcno = ((JSONObject) fabric_receipt_jsonobj.get(key)).getString("dcno");

                    String joborderno = ((JSONObject) fabric_receipt_jsonobj.get(key)).getString("joborderno");
                    String fromvendor = ((JSONObject) fabric_receipt_jsonobj.get(key)).getString("fromvendor");
                    String styleno = ((JSONObject) fabric_receipt_jsonobj.get(key)).getString("styleno");
                    String shipcode = ((JSONObject) fabric_receipt_jsonobj.get(key)).getString("shipcode");
                    String partname = ((JSONObject) fabric_receipt_jsonobj.get(key)).getString("partname");
                    String sentrolls = ((JSONObject) fabric_receipt_jsonobj.get(key)).getString("sentrolls");
                    String sentweight = ((JSONObject) fabric_receipt_jsonobj.get(key)).getString("sentweight");
                    String status = ((JSONObject) fabric_receipt_jsonobj.get(key)).getString("status");
                    int index = Integer.parseInt(((JSONObject) fabric_receipt_jsonobj.get(key)).getString("index"));

                    Fabric_Receipt_Data_Object obj = new Fabric_Receipt_Data_Object(dcid, dcno, dcdate, sentweight, joborderno, fromvendor, styleno, shipcode, partname, sentrolls, status, index);
                    results.add(i, obj);
                    i++;
                }
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return results;
    }

    private void fetch_fabric_receipt_Details() {

        Fabric_Receipt_Activity.custPrograssbar_new.prograssCreate(this);

        session = new SessionManagement(getApplicationContext());
        HashMap<String, String> user = session.getUserDetails();
        processorid = user.get(SessionManagement.KEY_PROCESSOR_ID);
        userid = user.get(SessionManagement.KEY_USER_ID);

        JSONObject jsonObject = new JSONObject();
        try {
            
            jsonObject.put("userid", userid);
            jsonObject.put("contractorid", processorid);
            jsonObject.put("limit", endindex);
            jsonObject.put("offset", startindex);
            Log.e("Bipin","limit : " +endindex);
            Log.e("Bipin","offset : " +startindex);

            JsonParser jsonParser = new JsonParser();
            Call<JsonObject> call = APIClient.getInterface().fabric_pendingdeliveries((JsonObject) jsonParser.parse(jsonObject.toString()));
            GetResult getResult = new GetResult();
            getResult.setMyListener(this);
            getResult.callForLogin(call, "pendingdeliveries");
        } 
        catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void fetchscrolldata() {
        Fabric_Receipt_Activity.custPrograssbar_new.prograssCreate(this);
        session = new SessionManagement(getApplicationContext());
        HashMap<String, String> user = session.getUserDetails();
        processorid = user.get(SessionManagement.KEY_PROCESSOR_ID);
        userid = user.get(SessionManagement.KEY_USER_ID);

        JSONObject jsonObject = new JSONObject();
        try {
            
            jsonObject.put("userid", userid);
            jsonObject.put("contractorid", processorid);
            jsonObject.put("limit", endindex);
            jsonObject.put("offset", startindex);

            Log.e("Bipin","limit : " +endindex);
            Log.e("Bipin","offset : " +startindex);

            JsonParser jsonParser = new JsonParser();
            Call<JsonObject> call = APIClient.getInterface().fabric_pendingdeliveries((JsonObject) jsonParser.parse(jsonObject.toString()));
            GetResult getResult = new GetResult();
            getResult.setMyListener(this);
            getResult.callForLogin(call, "pending_deliveries");
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

    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.imgd:
                HashMap<String, String> user = session.getUserDetails();
                String username = user.get(SessionManagement.KEY_USER);
                String userid = user.get(SessionManagement.KEY_USER_ID);

                Intent intent = new Intent(Fabric_Receipt_Activity.this, HomeActivity.class);
                intent.putExtra("openDrawer", true);
                intent.putExtra("username", username);
                intent.putExtra("userid", userid);
                intent.putExtra("processorid", processorid);
                startActivity(intent);
                break;
        }
    }

    @Override
    public void onBackPressed() {
        session = new SessionManagement(getApplicationContext());
        HashMap<String, String> user = session.getUserDetails();
        Intent intent = new Intent(Fabric_Receipt_Activity.this, MainActivity.class);
        intent.putExtra("name", user.get(SessionManagement.KEY_USER));
        intent.putExtra("userid", user.get(SessionManagement.KEY_USER_ID));
        intent.putExtra("processorid", user.get(SessionManagement.KEY_PROCESSOR_ID));
        startActivity(intent);
        finish();
    }

    @Override
    public void callback(JsonObject result, String callNo) {
        try {
            progress.setVisibility(View.GONE);
            Fabric_Receipt_Activity.custPrograssbar_new.closePrograssBar();
            if (callNo.equalsIgnoreCase("pendingdeliveries")) {
                JSONObject jsonObject = new JSONObject(result.toString());
                String mStatus = jsonObject.optString("status");
                String message = jsonObject.optString("message");

                Fabric_Receipt_Activity.custPrograssbar_new.closePrograssBar();
                if (mStatus.equals("success")) {

                    fabric_data_obj = jsonObject.getJSONObject("data").toString();

                    mRecyclerView = (RecyclerView) findViewById(R.id.my_recycler_view);
                    mRecyclerView.setHasFixedSize(true);
                    mLayoutManager = new LinearLayoutManager(Fabric_Receipt_Activity.this);
                    mRecyclerView.setLayoutManager(mLayoutManager);
                    mAdapter = new Fabric_Receipt_View_Adapter(getDataSet());
                    mRecyclerView.setAdapter(mAdapter);

                    ((Fabric_Receipt_View_Adapter) mAdapter).setOnItemClickListener(new Fabric_Receipt_View_Adapter.MyClickListener() {
                        @Override
                        public void onItemClick(int position, View v) {

                            int dcid =((Fabric_Receipt_View_Adapter) mAdapter).getDcid(position);
                            String dcno =((Fabric_Receipt_View_Adapter) mAdapter).getDcno(new String(String.valueOf(position)));
                            String dcdate =((Fabric_Receipt_View_Adapter) mAdapter).getDcdate(new String(String.valueOf(position)));
                            String vendorname =((Fabric_Receipt_View_Adapter) mAdapter).getVendorname(new String(String.valueOf(position)));

                            String joborderno =((Fabric_Receipt_View_Adapter) mAdapter).getJoborderno(new String(String.valueOf(position)));
                            String styleno =((Fabric_Receipt_View_Adapter) mAdapter).getStyleno(new String(String.valueOf(position)));
                            String partname =((Fabric_Receipt_View_Adapter) mAdapter).getPartname(new String(String.valueOf(position)));

                            String sentrolls =((Fabric_Receipt_View_Adapter) mAdapter).getSentrolls(new String(String.valueOf(position)));
                            String status =((Fabric_Receipt_View_Adapter) mAdapter).getStatus(new String(String.valueOf(position)));
                            String shipcode =((Fabric_Receipt_View_Adapter) mAdapter).getShipcode(new String(String.valueOf(position)));

                            Intent intent = new Intent(Fabric_Receipt_Activity.this, Fabric_Delivery_Details_Activity.class);
                            intent.putExtra("delivery", delivery.toString());
                            intent.putExtra("pendingcount", pendingcount);
                            intent.putExtra("processorid", processorid);

                            intent.putExtra("dcid", new String(String.valueOf(dcid)));
                            intent.putExtra("dcno", dcno);
                            intent.putExtra("dcdate", dcdate);
                            intent.putExtra("vendorname", vendorname);
                            intent.putExtra("joborderno", joborderno);
                            intent.putExtra("shipcode", shipcode);
                            intent.putExtra("styleno", styleno);
                            intent.putExtra("partname", partname);
                            intent.putExtra("sentrolls", sentrolls);
                            intent.putExtra("status", status);
                            intent.putExtra("activity", "Fabric_Receipt_Activity");
                            startActivity(intent);
                            finish();
                        }
                    });

                    if(totalItemCount >10)
                    {
                        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
                            @Override
                            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                                if (dy > endpos && loading && finalloading )
                                { //check for scroll down
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
                                            //progress.setVisibility(View.VISIBLE);
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
                    new AlertDialog.Builder(Fabric_Receipt_Activity.this)
                        .setMessage(message)
                        .setCancelable(false)
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface arg1, int arg0) {
                                arg1.dismiss();
                            }
                        }).show();
                }
                else 
                {
                    new AlertDialog.Builder(Fabric_Receipt_Activity.this)
                        .setMessage(message)
                        .setCancelable(false)
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface arg1, int arg0) {
                                arg1.dismiss();
                            }
                        }).show();
                }
            }
            else if (callNo.equalsIgnoreCase("pending_deliveries")) {
                JSONObject jsonObject = new JSONObject(result.toString());
                String mStatus = jsonObject.optString("status");
                String message = jsonObject.optString("message");

                if (mStatus.equals("success")) {

                    fabric_data_obj = jsonObject.getJSONObject("data").toString();

                    JSONObject jsonObj = new JSONObject(fabric_data_obj);
                    fabric_receipt_jsonobj = jsonObj.getJSONObject("deliveries");
                    Iterator<String> accessory_receipt_contents = fabric_receipt_jsonobj.keys();
                    while (accessory_receipt_contents.hasNext()) {
                        String key = accessory_receipt_contents.next();
                        if (fabric_receipt_jsonobj.get(key) instanceof JSONObject) {
                            int dcid = Integer.parseInt(((JSONObject) fabric_receipt_jsonobj.get(key)).getString("dcid"));
                            String dcdate = ((JSONObject) fabric_receipt_jsonobj.get(key)).getString("dcdate");
                            String dcno = ((JSONObject) fabric_receipt_jsonobj.get(key)).getString("dcno");

                            String joborderno = ((JSONObject) fabric_receipt_jsonobj.get(key)).getString("joborderno");
                            String fromvendor = ((JSONObject) fabric_receipt_jsonobj.get(key)).getString("fromvendor");
                            String styleno = ((JSONObject) fabric_receipt_jsonobj.get(key)).getString("styleno");
                            String shipcode = ((JSONObject) fabric_receipt_jsonobj.get(key)).getString("shipcode");
                            String partname = ((JSONObject) fabric_receipt_jsonobj.get(key)).getString("partname");
                            String sentrolls = ((JSONObject) fabric_receipt_jsonobj.get(key)).getString("sentrolls");
                            String sentweight = ((JSONObject) fabric_receipt_jsonobj.get(key)).getString("sentweight");
                            String status = ((JSONObject) fabric_receipt_jsonobj.get(key)).getString("status");
                            int index = Integer.parseInt(((JSONObject) fabric_receipt_jsonobj.get(key)).getString("index"));

                            Fabric_Receipt_Data_Object obj = new Fabric_Receipt_Data_Object(dcid, dcno, dcdate, sentweight, joborderno, fromvendor, styleno, shipcode, partname, sentrolls, status, index);
                            results.add(i, obj);
                            i++;
                        }
                    }
                    mAdapter.notifyDataSetChanged();
                }
                else if (mStatus.equals("nodatafound")) {
                    if(mAdapter == null)
                    {
                        // Do nothing
                    }
                    new AlertDialog.Builder(Fabric_Receipt_Activity.this)
                        .setMessage(message)
                        .setCancelable(false)
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface arg1, int arg0) {
                                arg1.dismiss();
                            }
                        }).show();
                }
                else 
                {
                    new AlertDialog.Builder(Fabric_Receipt_Activity.this)
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
