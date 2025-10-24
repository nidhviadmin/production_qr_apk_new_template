package com.bipinexports.productionqr.Activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
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
import com.bipinexports.productionqr.Accessory_Receipt_Vendor_Data_Object;
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


public class Accessory_Receipt_Vendorwise_Activity extends AppCompatActivity implements View.OnClickListener, GetResult.MyListener {

    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private static String LOG_TAG = "CardViewActivity";

    SessionManagement session;
    TextView txtUser;
    ImageView imageView;
    String accessory_data_obj, vendors;
    JSONObject acessory_vendor_jsonobj;
    String processorid;
    String userid, User, Id;
    ProgressBar progress;

    int totalItemCount;
    int startindex = 0;
    int endindex = 10;
    String  pendingcount;

    public static CustPrograssbar_new custPrograssbar_new;

    ArrayList results = new ArrayList<Accessory_Receipt_Vendor_Data_Object>();
    int i = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_accessory_receipt_vendorwise);

        txtUser = findViewById(R.id.txtUser);
        imageView = findViewById(R.id.imgd);
        progress = (ProgressBar) findViewById(R.id.progress);
//        progress.setVisibility(View.VISIBLE);

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

        vendors = getIntent().getStringExtra("vendors");
        pendingcount = getIntent().getStringExtra("pendingcount");
        totalItemCount = Integer.parseInt(pendingcount);

        getvalue();
        fetch_accessory_receipt_vendor_Details();
    }


    private ArrayList<Accessory_Receipt_Vendor_Data_Object> getDataSet() {
//        ArrayList results = new ArrayList<Accessory_Receipt_Vendor_Data_Object>();
        try {
            JSONObject jsonObj = new JSONObject(accessory_data_obj);
            acessory_vendor_jsonobj = jsonObj.getJSONObject("vendors");
            Iterator<String> accessory_receipt_contents = acessory_vendor_jsonobj.keys();

            while (accessory_receipt_contents.hasNext()) {
                String key = accessory_receipt_contents.next();
                if (acessory_vendor_jsonobj.get(key) instanceof JSONObject) {
                    int vendorid = Integer.parseInt(((JSONObject) acessory_vendor_jsonobj.get(key)).getString("vendorid"));
                    String vendorname = ((JSONObject) acessory_vendor_jsonobj.get(key)).getString("vendorname");
                    String count = ((JSONObject) acessory_vendor_jsonobj.get(key)).getString("count");
                    String quantity = ((JSONObject) acessory_vendor_jsonobj.get(key)).getString("quantity");
                    int index = Integer.parseInt(((JSONObject) acessory_vendor_jsonobj.get(key)).getString("index"));
                    Accessory_Receipt_Vendor_Data_Object obj = new Accessory_Receipt_Vendor_Data_Object(vendorid, vendorname, count, quantity, index);
                    results.add(i, obj);
                    i++;
                }
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return results;
    }

    private void fetch_accessory_receipt_vendor_Details() {

        Accessory_Receipt_Vendorwise_Activity.custPrograssbar_new.prograssCreate(this);

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

            JsonParser jsonParser = new JsonParser();
            Call<JsonObject> call = APIClient.getInterface().pendingdeliveries_new((JsonObject) jsonParser.parse(jsonObject.toString()));
            GetResult getResult = new GetResult();
            getResult.setMyListener(this);
            getResult.callForLogin(call, "pendingdeliveries");
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
                Intent intent = new Intent(Accessory_Receipt_Vendorwise_Activity.this, HomeActivity.class);
                intent.putExtra("openDrawer", true); //
                intent.putExtra("username", User);
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
        Intent intent = new Intent(Accessory_Receipt_Vendorwise_Activity.this, MainActivity.class);
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
            Accessory_Receipt_Vendorwise_Activity.custPrograssbar_new.closePrograssBar();
            if (callNo.equalsIgnoreCase("pendingdeliveries")) {
                JSONObject jsonObject = new JSONObject(result.toString());
                String mStatus = jsonObject.optString("status");
                String message = jsonObject.optString("message");

                Accessory_Receipt_Vendorwise_Activity.custPrograssbar_new.closePrograssBar();
                if (mStatus.equals("success")) {

                    accessory_data_obj = jsonObject.getJSONObject("data").toString();

                    mRecyclerView = (RecyclerView) findViewById(R.id.my_recycler_view);
                    mRecyclerView.setHasFixedSize(true);
                    mLayoutManager = new LinearLayoutManager(Accessory_Receipt_Vendorwise_Activity.this);
                    mRecyclerView.setLayoutManager(mLayoutManager);
                    mAdapter = new Accessory_Receipt_Vendor_View_Adapter(getDataSet());
                    mRecyclerView.setAdapter(mAdapter);

                    ((Accessory_Receipt_Vendor_View_Adapter) mAdapter).setOnItemClickListener(new Accessory_Receipt_Vendor_View_Adapter.MyClickListener() {
                        @Override
                        public void onItemClick(int position, View v) {
                            String quantity =((Accessory_Receipt_Vendor_View_Adapter) mAdapter).getQuantity(new String(String.valueOf(position)));
                            String vendorname =((Accessory_Receipt_Vendor_View_Adapter) mAdapter).getVendorname(new String(String.valueOf(position)));
                            int vendorid =((Accessory_Receipt_Vendor_View_Adapter) mAdapter).getVendorid(position);
                            String del_count =((Accessory_Receipt_Vendor_View_Adapter) mAdapter).getCount(new String(String.valueOf(position)));

                            Intent intent = new Intent(Accessory_Receipt_Vendorwise_Activity.this, Accessory_Receipt_Jobwise_Activity.class);
                            intent.putExtra("vendors", acessory_vendor_jsonobj.toString());

                            intent.putExtra("pendingcount", pendingcount);
                            intent.putExtra("processorid", processorid);
                            intent.putExtra("vendorname", vendorname);
                            intent.putExtra("vendorid", String.valueOf(vendorid));
                            intent.putExtra("quantity", quantity);
                            intent.putExtra("del_count", del_count);
                            startActivity(intent);
                            finish();
                        }
                    });
                }
                else if (mStatus.equals("nodatafound")) {
                    if(mAdapter == null)
                    {
                        // Do nothing
                    }

                    new AlertDialog.Builder(Accessory_Receipt_Vendorwise_Activity.this)
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
                    new AlertDialog.Builder(Accessory_Receipt_Vendorwise_Activity.this)
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
