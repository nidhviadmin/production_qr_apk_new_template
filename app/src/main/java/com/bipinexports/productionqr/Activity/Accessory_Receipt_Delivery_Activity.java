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
import com.bipinexports.productionqr.Accessory_Receipt_Delivery_Data_Object;
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


public class Accessory_Receipt_Delivery_Activity extends BaseActivity implements View.OnClickListener, GetResult.MyListener {
    private RecyclerView mRecyclerView;

    private View content;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private static String LOG_TAG = "CardViewActivity";

    SessionManagement session;
    TextView txtUser;
    ImageView imageView;

    String accessory_data_obj, vendors;
    JSONObject acessory_receipt_jsonobj;
    String processorid;
    String userid, User, Id;
    ProgressBar progress;

    private boolean loading = true;
    int totalItemCount;
    int startindex = 0;
    int endindex = 10;
    int endpos = 100;
    private boolean finalloading = true;
    String pendingcount;

    public static CustPrograssbar_new custPrograssbar_new;

    String  vendorname, vendorid, del_quantity, stylename, joborderno, orderid, vendor_del_count, del_count;
    TextView text_Vendor_Name, text_Job_Order_No, text_Style_Name, text_qty, text_delivery_count;
    private boolean grandtotreach = false;

    ArrayList results = new ArrayList<Accessory_Receipt_Delivery_Data_Object>();
    int i = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_base);
        setupDrawer();

        content = getLayoutInflater().inflate(
                R.layout.activity_accessory_receipt_delivery,
                findViewById(R.id.content_frame),
                true
        );

        txtUser = content.findViewById(R.id.txtUser);
        imageView = content.findViewById(R.id.imgd);
        progress = content.findViewById(R.id.progress);

        custPrograssbar_new = new CustPrograssbar_new();
        Accessory_Receipt_Delivery_Activity.custPrograssbar_new.prograssCreate(this);

        session = new SessionManagement(getApplicationContext());
        HashMap<String, String> user = session.getUserDetails();
        String name = user.get(SessionManagement.KEY_USER);
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
        del_quantity = getIntent().getStringExtra("quantity");
        vendor_del_count = getIntent().getStringExtra("vendor_del_count");
        joborderno = getIntent().getStringExtra("joborderno");
        stylename = getIntent().getStringExtra("stylename");
        orderid = getIntent().getStringExtra("orderid");
        del_count = getIntent().getStringExtra("del_count");

        totalItemCount = Integer.parseInt(del_count);

        text_Vendor_Name = content.findViewById(R.id.text_Vendor_Name);
        text_Job_Order_No = content.findViewById(R.id.text_Job_Order_No);
        text_Style_Name = content.findViewById(R.id.text_Style_Name);
        text_qty = content.findViewById(R.id.text_qty);
        text_delivery_count = content.findViewById(R.id.text_delivery_count);

        text_Vendor_Name.setText(vendorname);
        text_Job_Order_No.setText(joborderno);
        text_Style_Name.setText(stylename);
        text_qty.setText(del_quantity);
        text_delivery_count.setText(del_count);

        if (Integer.parseInt(del_count) == 0) {
            int dd = Integer.parseInt(vendor_del_count) - 1;
            vendor_del_count = String.valueOf(dd);
            onBackPressed();
        } else {
            getvalue();
            fetch_accessory_receipt_Details();
        }
    }

    private ArrayList<Accessory_Receipt_Delivery_Data_Object> getDataSet() {
//        ArrayList results = new ArrayList<Accessory_Receipt_Delivery_Data_Object>();
        try {
            JSONObject jsonObj = new JSONObject(accessory_data_obj);
            acessory_receipt_jsonobj = jsonObj.getJSONObject("deliveries");

            Iterator<String> accessory_receipt_contents = acessory_receipt_jsonobj.keys();

            while (accessory_receipt_contents.hasNext()) {
                String key = accessory_receipt_contents.next();
                if (acessory_receipt_jsonobj.get(key) instanceof JSONObject) {
                    int dcid = Integer.parseInt(((JSONObject) acessory_receipt_jsonobj.get(key)).getString("dcid"));
                    int poid = Integer.parseInt(((JSONObject) acessory_receipt_jsonobj.get(key)).getString("poid"));

                    String dcdate = ((JSONObject) acessory_receipt_jsonobj.get(key)).getString("dcdate");
                    String dcno = ((JSONObject) acessory_receipt_jsonobj.get(key)).getString("dcno");
                    String pono = ((JSONObject) acessory_receipt_jsonobj.get(key)).getString("pono");
                    String podate = ((JSONObject) acessory_receipt_jsonobj.get(key)).getString("podate");
                    String vendorcode = ((JSONObject) acessory_receipt_jsonobj.get(key)).getString("vendorcode");
                    String vendorname = ((JSONObject) acessory_receipt_jsonobj.get(key)).getString("vendorname");
                    String type = ((JSONObject) acessory_receipt_jsonobj.get(key)).getString("type");
                    String quantity = ((JSONObject) acessory_receipt_jsonobj.get(key)).getString("quantity");
                    int index = Integer.parseInt(((JSONObject) acessory_receipt_jsonobj.get(key)).getString("index"));
                    Accessory_Receipt_Delivery_Data_Object obj = new Accessory_Receipt_Delivery_Data_Object(dcid, dcno, dcdate, poid, pono, podate, vendorcode, vendorname, type, quantity, index);
                    results.add(i, obj);
                    i++;
                }
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return results;
    }

    private void fetch_accessory_receipt_Details() {

        session = new SessionManagement(getApplicationContext());
        HashMap<String, String> user = session.getUserDetails();
        processorid = user.get(SessionManagement.KEY_PROCESSOR_ID);
        userid = user.get(SessionManagement.KEY_USER_ID);

        JSONObject jsonObject = new JSONObject();
        try {
            
            jsonObject.put("userid", userid);
            jsonObject.put("contractorid", processorid);
            jsonObject.put("vendorid", vendorid);
            jsonObject.put("orderid", orderid);
            jsonObject.put("limit", endindex);
            jsonObject.put("offset", startindex);

            JsonParser jsonParser = new JsonParser();
            Call<JsonObject> call = APIClient.getInterface().pendingdeliveries_new((JsonObject) jsonParser.parse(jsonObject.toString()));
            GetResult getResult = new GetResult();
            getResult.setMyListener(this);
            getResult.callForLogin(call, "pendingdeliveries_new");
        } 
        catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void fetchscrolldata() {
        Accessory_Receipt_Delivery_Activity.custPrograssbar_new.prograssCreate(this);
        session = new SessionManagement(getApplicationContext());
        HashMap<String, String> user = session.getUserDetails();
        processorid = user.get(SessionManagement.KEY_PROCESSOR_ID);
        userid = user.get(SessionManagement.KEY_USER_ID);

        JSONObject jsonObject = new JSONObject();
        try {

            jsonObject.put("userid", userid);
            jsonObject.put("contractorid", processorid);
            jsonObject.put("vendorid", vendorid);
            jsonObject.put("orderid", orderid);
            jsonObject.put("limit", endindex);
            jsonObject.put("offset", startindex);

            JsonParser jsonParser = new JsonParser();
            Call<JsonObject> call = APIClient.getInterface().pendingdeliveries_new((JsonObject) jsonParser.parse(jsonObject.toString()));
            GetResult getResult = new GetResult();
            getResult.setMyListener(this);
            getResult.callForLogin(call, "pending_deliveries_new");
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
        Intent intent = new Intent(Accessory_Receipt_Delivery_Activity.this, Accessory_Receipt_Jobwise_Activity.class);
        intent.putExtra("name", user.get(SessionManagement.KEY_USER));
        intent.putExtra("userid", user.get(SessionManagement.KEY_USER_ID));
        intent.putExtra("processorid", user.get(SessionManagement.KEY_PROCESSOR_ID));
        Accessory_Receipt_Delivery_Activity.custPrograssbar_new.closePrograssBar();

        intent.putExtra("vendors", vendors);
        intent.putExtra("pendingcount", pendingcount);
        intent.putExtra("vendorname", vendorname);
        intent.putExtra("vendorid", vendorid);
        intent.putExtra("quantity", del_quantity);
        intent.putExtra("del_count", vendor_del_count);
        startActivity(intent);
        finish();
    }

    @Override
    public void callback(JsonObject result, String callNo)
    {
        try {

            if (callNo.equalsIgnoreCase("pendingdeliveries_new"))
            {
                JSONObject jsonObject = new JSONObject(result.toString());
                String mStatus = jsonObject.optString("status");
                String message = jsonObject.optString("message");

                if (mStatus.equals("success")) {

                    accessory_data_obj = jsonObject.getJSONObject("data").toString();

                    JSONObject jsonObj = new JSONObject(accessory_data_obj);

                    String limit = jsonObj.optString("limit");
                    String offset = jsonObj.optString("offset");

                    mRecyclerView = (RecyclerView) findViewById(R.id.my_recycler_view);
                    mRecyclerView.setHasFixedSize(true);
                    mLayoutManager = new LinearLayoutManager(Accessory_Receipt_Delivery_Activity.this);
                    mRecyclerView.setLayoutManager(mLayoutManager);
                    mAdapter = new Accessory_Receipt_Delivery_View_Adapter(getDataSet());
                    mRecyclerView.setAdapter(mAdapter);

                    ((Accessory_Receipt_Delivery_View_Adapter) mAdapter).setOnItemClickListener(new Accessory_Receipt_Delivery_View_Adapter.MyClickListener()
                    {
                        @Override
                        public void onItemClick(int position, View v) {
                            int poid =((Accessory_Receipt_Delivery_View_Adapter) mAdapter).getPoid(position);
                            int dcid =((Accessory_Receipt_Delivery_View_Adapter) mAdapter).getDcid(position);
                            String pono =((Accessory_Receipt_Delivery_View_Adapter) mAdapter).getPono(new String(String.valueOf(position)));
                            String podate =((Accessory_Receipt_Delivery_View_Adapter) mAdapter).getPodate(new String(String.valueOf(position)));
                            String vendorname =((Accessory_Receipt_Delivery_View_Adapter) mAdapter).getVendorname(new String(String.valueOf(position)));
                            String dcno =((Accessory_Receipt_Delivery_View_Adapter) mAdapter).getDcno(new String(String.valueOf(position)));
                            String dcdate =((Accessory_Receipt_Delivery_View_Adapter) mAdapter).getDcdate(new String(String.valueOf(position)));
                            String type =((Accessory_Receipt_Delivery_View_Adapter) mAdapter).getType(new String(String.valueOf(position)));

                            Intent intent = new Intent(Accessory_Receipt_Delivery_Activity.this, Accessory_Delivery_Details_Activity.class);
                            intent.putExtra("pendingcount", pendingcount);
                            intent.putExtra("vendor_del_count", vendor_del_count);
                            intent.putExtra("processorid", processorid);
                            intent.putExtra("poid", new String(String.valueOf(poid)));
                            intent.putExtra("dcid", new String(String.valueOf(dcid)));
                            intent.putExtra("pono", pono);
                            intent.putExtra("podate", podate);
                            intent.putExtra("vendorname", vendorname);
                            intent.putExtra("vendorid", vendorid);
                            intent.putExtra("joborderno", joborderno);
                            intent.putExtra("stylename", stylename);
                            intent.putExtra("orderid", orderid);
                            intent.putExtra("dcno", dcno);
                            intent.putExtra("dcdate", dcdate);
                            intent.putExtra("type", type);
                            intent.putExtra("vendors", vendors);
                            intent.putExtra("del_quantity", del_quantity);
                            intent.putExtra("del_count", del_count);

                            intent.putExtra("activity", "Accessory_Receipt_Delivery_Activity");
                            startActivity(intent);
                            finish();
                        }
                    });

                    if(totalItemCount > 10 && grandtotreach == false)
                    {
                        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
                            @Override
                            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                                if (dy > endpos && loading && finalloading && grandtotreach == false )
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

                    new AlertDialog.Builder(Accessory_Receipt_Delivery_Activity.this)
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
                    new AlertDialog.Builder(Accessory_Receipt_Delivery_Activity.this)
                        .setMessage(message)
                        .setCancelable(false)
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface arg1, int arg0) {
                                arg1.dismiss();
                            }
                        }).show();
                }
                Accessory_Receipt_Delivery_Activity.custPrograssbar_new.closePrograssBar();
            }
            else if (callNo.equalsIgnoreCase("pending_deliveries_new")) {

                JSONObject jsonObject = new JSONObject(result.toString());
                String mStatus = jsonObject.optString("status");
                String message = jsonObject.optString("message");

                if (mStatus.equals("success")) {

                    accessory_data_obj = jsonObject.getJSONObject("data").toString();

                    JSONObject jsonObj = new JSONObject(accessory_data_obj);
                    acessory_receipt_jsonobj = jsonObj.getJSONObject("deliveries");

                    String limit = jsonObj.optString("limit");
                    String offset = jsonObj.optString("offset");

                    Iterator<String> accessory_receipt_contents = acessory_receipt_jsonobj.keys();
                    while (accessory_receipt_contents.hasNext()) {
                        String key = accessory_receipt_contents.next();
                        if (acessory_receipt_jsonobj.get(key) instanceof JSONObject) {
                            int dcid = Integer.parseInt(((JSONObject) acessory_receipt_jsonobj.get(key)).getString("dcid"));
                            int poid = Integer.parseInt(((JSONObject) acessory_receipt_jsonobj.get(key)).getString("poid"));

                            String dcdate = ((JSONObject) acessory_receipt_jsonobj.get(key)).getString("dcdate");
                            String dcno = ((JSONObject) acessory_receipt_jsonobj.get(key)).getString("dcno");
                            String pono = ((JSONObject) acessory_receipt_jsonobj.get(key)).getString("pono");
                            String podate = ((JSONObject) acessory_receipt_jsonobj.get(key)).getString("podate");
                            String vendorcode = ((JSONObject) acessory_receipt_jsonobj.get(key)).getString("vendorcode");
                            String vendorname = ((JSONObject) acessory_receipt_jsonobj.get(key)).getString("vendorname");
                            String type = ((JSONObject) acessory_receipt_jsonobj.get(key)).getString("type");
                            String quantity = ((JSONObject) acessory_receipt_jsonobj.get(key)).getString("quantity");
                            int index = Integer.parseInt(((JSONObject) acessory_receipt_jsonobj.get(key)).getString("index"));

                            Accessory_Receipt_Delivery_Data_Object obj = new Accessory_Receipt_Delivery_Data_Object(dcid, dcno, dcdate, poid, pono, podate, vendorcode, vendorname, type, quantity, index);
                            results.add(i, obj);
                            i++;
                        }
                    }

                    if(totalItemCount == Integer.parseInt(offset) && grandtotreach==false)
                    {
                        grandtotreach = true;
                    }
                    mAdapter.notifyDataSetChanged();
                    Accessory_Receipt_Delivery_Activity.custPrograssbar_new.closePrograssBar();
                }
                else if (mStatus.equals("nodatafound")) {
                    if(mAdapter == null)
                    {
                        // Do nothing
                    }
                    new AlertDialog.Builder(Accessory_Receipt_Delivery_Activity.this)
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
                    new AlertDialog.Builder(Accessory_Receipt_Delivery_Activity.this)
                        .setMessage(message)
                        .setCancelable(false)
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface arg1, int arg0) {
                                arg1.dismiss();
                            }
                        }).show();
                }
                Accessory_Receipt_Delivery_Activity.custPrograssbar_new.closePrograssBar();
            }
        }
        catch (Exception e) {
        }
    }
}
