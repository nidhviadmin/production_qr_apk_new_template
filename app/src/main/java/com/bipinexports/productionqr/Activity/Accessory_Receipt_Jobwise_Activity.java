package com.bipinexports.productionqr.Activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bipinexports.productionqr.APIClient;
import com.bipinexports.productionqr.Accessory_Receipt_Jobwise_Data_Object;
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

public class Accessory_Receipt_Jobwise_Activity extends BaseActivity implements View.OnClickListener, GetResult.MyListener {

    private View content;
    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    SessionManagement session;
    TextView txtUser;
    ImageView imageView;
    String accessory_data_obj, vendors;
    JSONObject acessory_joborder_jsonobj;
    String processorid;
    String userid, User, Id;
    ProgressBar progress;

    int totalItemCount;
    int startindex = 0;
    int endindex = 10;
    String pendingcount;

    String vendorname, vendorid, quantity, vendor_del_count;
    TextView text_Vendor_Name, text_qty, text_del_count;

    public static CustPrograssbar_new custPrograssbar_new;

    ArrayList results = new ArrayList<Accessory_Receipt_Jobwise_Data_Object>();
    int i = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_base);
        setupDrawer();

        content = getLayoutInflater().inflate(
                R.layout.activity_accessory_receipt_jobwise,
                findViewById(R.id.content_frame),
                true);

        txtUser = content.findViewById(R.id.txtUser);
        imageView = content.findViewById(R.id.imgd);
        progress = content.findViewById(R.id.progress);

        custPrograssbar_new = new CustPrograssbar_new();

        session = new SessionManagement(getApplicationContext());
        HashMap<String, String> user = session.getUserDetails();
        this.Id = user.get(SessionManagement.KEY_PROCESSOR_ID);
        processorid = user.get(SessionManagement.KEY_PROCESSOR_ID);
        userid = user.get(SessionManagement.KEY_USER_ID);
        this.User = user.get(SessionManagement.KEY_USER);

        processorid = getIntent().getStringExtra("processorid");
        imageView.setOnClickListener(this);
        setupNotifications();

        handleNotificationIntent(getIntent());

        vendors = getIntent().getStringExtra("vendors");
        pendingcount = getIntent().getStringExtra("pendingcount");
        vendorname = getIntent().getStringExtra("vendorname");
        vendorid = getIntent().getStringExtra("vendorid");
        quantity = getIntent().getStringExtra("quantity");
        vendor_del_count = getIntent().getStringExtra("del_count");
        totalItemCount = Integer.parseInt(pendingcount);

        text_Vendor_Name = content.findViewById(R.id.text_Vendor_Name);
        text_qty = content.findViewById(R.id.text_qty);
        text_del_count = content.findViewById(R.id.text_del_count);

        text_Vendor_Name.setText(vendorname);
        text_qty.setText(quantity);
        text_del_count.setText(vendor_del_count);

        if (Integer.parseInt(vendor_del_count) == 0) {
            int dd = Integer.parseInt(vendor_del_count) - 1;
            vendor_del_count = String.valueOf(dd);
            onBackPressed();
        } else {
            getvalue();
            fetch_accessory_vendorwise_job_Details();
        }
    }

    private ArrayList<Accessory_Receipt_Jobwise_Data_Object> getDataSet() {
        try {
            JSONObject jsonObj = new JSONObject(accessory_data_obj);
            acessory_joborder_jsonobj = jsonObj.getJSONObject("joborders");
            Iterator<String> accessory_receipt_contents = acessory_joborder_jsonobj.keys();

            while (accessory_receipt_contents.hasNext()) {
                String key = accessory_receipt_contents.next();
                if (acessory_joborder_jsonobj.get(key) instanceof JSONObject) {
                    int orderid = Integer.parseInt(((JSONObject) acessory_joborder_jsonobj.get(key)).getString("orderid"));
                    String joborderno = ((JSONObject) acessory_joborder_jsonobj.get(key)).getString("joborderno");
                    String stylename = ((JSONObject) acessory_joborder_jsonobj.get(key)).getString("stylename");
                    String count = ((JSONObject) acessory_joborder_jsonobj.get(key)).getString("count");
                    String quantity = ((JSONObject) acessory_joborder_jsonobj.get(key)).getString("quantity");
                    int index = Integer.parseInt(((JSONObject) acessory_joborder_jsonobj.get(key)).getString("index"));
                    Accessory_Receipt_Jobwise_Data_Object obj = new Accessory_Receipt_Jobwise_Data_Object(orderid, joborderno, stylename, count, quantity, index);
                    results.add(i, obj);
                    i++;
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return results;
    }

    private void fetch_accessory_vendorwise_job_Details() {

        custPrograssbar_new.prograssCreate(this);

        session = new SessionManagement(getApplicationContext());
        HashMap<String, String> user = session.getUserDetails();
        processorid = user.get(SessionManagement.KEY_PROCESSOR_ID);
        userid = user.get(SessionManagement.KEY_USER_ID);

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("userid", userid);
            jsonObject.put("contractorid", processorid);
            jsonObject.put("vendorid", vendorid);
            jsonObject.put("limit", endindex);
            jsonObject.put("offset", startindex);

            JsonParser jsonParser = new JsonParser();
            Call<JsonObject> call = APIClient.getInterface().pendingdeliveries_new((JsonObject) jsonParser.parse(jsonObject.toString()));
            GetResult getResult = new GetResult();
            getResult.setMyListener(this);
            getResult.callForLogin(call, "pendingdeliveries");
        } catch (JSONException e) {
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
        Intent intent = new Intent(Accessory_Receipt_Jobwise_Activity.this, Accessory_Receipt_Vendorwise_Activity.class);
        intent.putExtra("name", user.get(SessionManagement.KEY_USER));
        intent.putExtra("userid", user.get(SessionManagement.KEY_USER_ID));
        intent.putExtra("processorid", user.get(SessionManagement.KEY_PROCESSOR_ID));
        intent.putExtra("vendors", vendors);
        intent.putExtra("pendingcount", pendingcount);
        startActivity(intent);
        finish();
    }

    @Override
    public void callback(JsonObject result, String callNo) {
        try {
            progress.setVisibility(View.GONE);
            custPrograssbar_new.closePrograssBar();
            if (callNo.equalsIgnoreCase("pendingdeliveries")) {
                JSONObject jsonObject = new JSONObject(result.toString());
                String mStatus = jsonObject.optString("status");
                String message = jsonObject.optString("message");

                if (mStatus.equals("success")) {
                    accessory_data_obj = jsonObject.getJSONObject("data").toString();

                    mRecyclerView = content.findViewById(R.id.my_recycler_view);
                    mRecyclerView.setHasFixedSize(true);
                    mLayoutManager = new LinearLayoutManager(this);
                    mRecyclerView.setLayoutManager(mLayoutManager);
                    mAdapter = new Accessory_Receipt_Jobwise_View_Adapter(getDataSet());
                    mRecyclerView.setAdapter(mAdapter);

                    ((Accessory_Receipt_Jobwise_View_Adapter) mAdapter).setOnItemClickListener((position, v) -> {
                        int orderid = ((Accessory_Receipt_Jobwise_View_Adapter) mAdapter).getOrderid(position);
                        String quantity = ((Accessory_Receipt_Jobwise_View_Adapter) mAdapter).getQuantity(String.valueOf(position));
                        String joborderno = ((Accessory_Receipt_Jobwise_View_Adapter) mAdapter).getJoborderno(String.valueOf(position));
                        String stylename = ((Accessory_Receipt_Jobwise_View_Adapter) mAdapter).getStylename(String.valueOf(position));
                        String del_count = ((Accessory_Receipt_Jobwise_View_Adapter) mAdapter).getCount(String.valueOf(position));

                        Intent intent = new Intent(Accessory_Receipt_Jobwise_Activity.this, Accessory_Receipt_Delivery_Activity.class);
                        intent.putExtra("vendors", vendors);
                        intent.putExtra("pendingcount", pendingcount);
                        intent.putExtra("processorid", processorid);
                        intent.putExtra("vendorname", vendorname);
                        intent.putExtra("vendor_del_count", vendor_del_count);
                        intent.putExtra("joborderno", joborderno);
                        intent.putExtra("stylename", stylename);
                        intent.putExtra("vendorid", vendorid);
                        intent.putExtra("orderid", String.valueOf(orderid));
                        intent.putExtra("quantity", quantity);
                        intent.putExtra("del_count", del_count);
                        startActivity(intent);
                        finish();
                    });

                } else if (mStatus.equals("nodatafound")) {
                    new AlertDialog.Builder(this)
                            .setMessage(message)
                            .setCancelable(false)
                            .setPositiveButton("OK", (arg1, arg0) -> arg1.dismiss())
                            .show();
                } else {
                    new AlertDialog.Builder(this)
                            .setMessage(message)
                            .setCancelable(false)
                            .setPositiveButton("OK", (arg1, arg0) -> arg1.dismiss())
                            .show();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
