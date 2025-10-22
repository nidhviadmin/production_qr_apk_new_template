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
import com.bipinexports.productionqr.Accessory_Receipt_Data_Object;
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


public class Accessory_Receipt_Activity extends AppCompatActivity implements View.OnClickListener, GetResult.MyListener {

    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private static String LOG_TAG = "CardViewActivity";

    SessionManagement session;
    TextView txtUser;
    ImageView imageView;
    String accessory_data_obj, delivery;
    JSONObject acessory_receipt_jsonobj;
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

    public static CustPrograssbar custPrograssbar;

    ArrayList results = new ArrayList<Accessory_Receipt_Data_Object>();
    int i = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fabric_receipt);

        txtUser = findViewById(R.id.txtUser);
        imageView = findViewById(R.id.imgd);
        progress = (ProgressBar) findViewById(R.id.progress);
//        progress.setVisibility(View.VISIBLE);

        custPrograssbar = new CustPrograssbar();

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
        fetch_accessory_receipt_Details();
    }


    private ArrayList<Accessory_Receipt_Data_Object> getDataSet() {
//        ArrayList results = new ArrayList<Accessory_Receipt_Data_Object>();
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
                    Accessory_Receipt_Data_Object obj = new Accessory_Receipt_Data_Object(dcid, dcno, dcdate, poid, pono, podate, vendorcode, vendorname, type, quantity, index);
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

        Accessory_Receipt_Activity.custPrograssbar.prograssCreate(this);

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
            Call<JsonObject> call = APIClient.getInterface().pendingdeliveries((JsonObject) jsonParser.parse(jsonObject.toString()));
            GetResult getResult = new GetResult();
            getResult.setMyListener(this);
            getResult.callForLogin(call, "pendingdeliveries");
        } 
        catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void fetchscrolldata() {
        Accessory_Receipt_Activity.custPrograssbar.prograssCreate(this);
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
            Call<JsonObject> call = APIClient.getInterface().pendingdeliveries((JsonObject) jsonParser.parse(jsonObject.toString()));
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
                PopupMenu popup = new PopupMenu(Accessory_Receipt_Activity.this, imageView);
                popup.getMenuInflater().inflate(R.menu.menu_main, popup.getMenu());

                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    public boolean onMenuItemClick(MenuItem item) {
                        if (item.getItemId() == R.id.log) {
                            session.logoutUser();
                            finish();
                        }
                        else if (item.getItemId() == R.id.changepassword) {
                            Intent intent = new Intent(Accessory_Receipt_Activity.this, ChangepasswordActivity.class);
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
        Intent intent = new Intent(Accessory_Receipt_Activity.this, MainActivity.class);
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
            Accessory_Receipt_Activity.custPrograssbar.closePrograssBar();
            if (callNo.equalsIgnoreCase("pendingdeliveries")) {
                JSONObject jsonObject = new JSONObject(result.toString());
                String mStatus = jsonObject.optString("status");
                String message = jsonObject.optString("message");

                Accessory_Receipt_Activity.custPrograssbar.closePrograssBar();
                if (mStatus.equals("success")) {

                    accessory_data_obj = jsonObject.getJSONObject("data").toString();

                    mRecyclerView = (RecyclerView) findViewById(R.id.my_recycler_view);
                    mRecyclerView.setHasFixedSize(true);
                    mLayoutManager = new LinearLayoutManager(Accessory_Receipt_Activity.this);
                    mRecyclerView.setLayoutManager(mLayoutManager);
                    mAdapter = new Accessory_Receipt_View_Adapter(getDataSet());
                    mRecyclerView.setAdapter(mAdapter);

                    ((Accessory_Receipt_View_Adapter) mAdapter).setOnItemClickListener(new Accessory_Receipt_View_Adapter.MyClickListener() {
                        @Override
                        public void onItemClick(int position, View v) {
                            int poid =((Accessory_Receipt_View_Adapter) mAdapter).getPoid(position);
                            int dcid =((Accessory_Receipt_View_Adapter) mAdapter).getDcid(position);
                            String pono =((Accessory_Receipt_View_Adapter) mAdapter).getPono(new String(String.valueOf(position)));
                            String podate =((Accessory_Receipt_View_Adapter) mAdapter).getPodate(new String(String.valueOf(position)));
                            String vendorname =((Accessory_Receipt_View_Adapter) mAdapter).getVendorname(new String(String.valueOf(position)));
                            String dcno =((Accessory_Receipt_View_Adapter) mAdapter).getDcno(new String(String.valueOf(position)));
                            String dcdate =((Accessory_Receipt_View_Adapter) mAdapter).getDcdate(new String(String.valueOf(position)));
                            String type =((Accessory_Receipt_View_Adapter) mAdapter).getType(new String(String.valueOf(position)));

                            Intent intent = new Intent(Accessory_Receipt_Activity.this, Delivery_Details_Activity.class);
                            intent.putExtra("delivery", delivery.toString());
                            intent.putExtra("pendingcount", pendingcount);
                            intent.putExtra("processorid", processorid);
                            intent.putExtra("poid", new String(String.valueOf(poid)));
                            intent.putExtra("dcid", new String(String.valueOf(dcid)));
                            intent.putExtra("pono", pono);
                            intent.putExtra("podate", podate);
                            intent.putExtra("vendorname", vendorname);
                            intent.putExtra("dcno", dcno);
                            intent.putExtra("dcdate", dcdate);
                            intent.putExtra("type", type);
                            intent.putExtra("activity", "Accessory_Receipt_Activity");
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

                    new AlertDialog.Builder(Accessory_Receipt_Activity.this)
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
                    new AlertDialog.Builder(Accessory_Receipt_Activity.this)
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

                    accessory_data_obj = jsonObject.getJSONObject("data").toString();

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

                            Accessory_Receipt_Data_Object obj = new Accessory_Receipt_Data_Object(dcid, dcno, dcdate, poid, pono, podate, vendorcode, vendorname, type, quantity, index);
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
                    new AlertDialog.Builder(Accessory_Receipt_Activity.this)
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
                    new AlertDialog.Builder(Accessory_Receipt_Activity.this)
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
