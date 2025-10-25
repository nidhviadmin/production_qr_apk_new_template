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
import com.bipinexports.productionqr.GetResult;
import com.bipinexports.productionqr.Machine_Service_Verification_Data_Object;
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

public class Machine_Service_Verification_Activity extends BaseActivity implements View.OnClickListener, GetResult.MyListener {

    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private static String LOG_TAG = "CardViewActivity";

    SessionManagement session;
    TextView txtUser;
    ImageView imageView;
    String mac_service_data_obj, mac_service_verification;
    JSONObject mac_service_verification_jsonobj;
    String processorid;
    String userid, User, Id;
    ProgressBar progress;

    int totalItemCount;
    String mac_service_verification_count;

    public static CustPrograssbar custPrograssbar;

    ArrayList results = new ArrayList<Machine_Service_Verification_Data_Object>();
    int i = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_mac_service_verification);
        setContentView(R.layout.activity_base);
        setupDrawer();

        View content = getLayoutInflater().inflate(
                R.layout.activity_mac_service_verification,
                findViewById(R.id.content_frame),
                true
        );

        txtUser = content.findViewById(R.id.txtUser);
        imageView = content.findViewById(R.id.imgd);
        progress = (ProgressBar) content.findViewById(R.id.progress);

        custPrograssbar = new CustPrograssbar();

        txtUser = (TextView) content.findViewById(R.id.txtUser);

        session = new SessionManagement(getApplicationContext());
        HashMap<String, String> user = session.getUserDetails();
        String name = user.get(SessionManagement.KEY_USER);
        this.Id = user.get(SessionManagement.KEY_PROCESSOR_ID);
        processorid = user.get(SessionManagement.KEY_PROCESSOR_ID);
        userid = user.get(SessionManagement.KEY_USER_ID);
        this.User = user.get(SessionManagement.KEY_USER);

        processorid = getIntent().getStringExtra("processorid");
        imageView.setOnClickListener(this);

        mac_service_verification = getIntent().getStringExtra("mac_service_verification");
        mac_service_verification_count = getIntent().getStringExtra("mac_service_verification_count");
        totalItemCount = Integer.parseInt(mac_service_verification_count);

        getvalue();
        fetch_machine_service_verification();
    }


    private ArrayList<Machine_Service_Verification_Data_Object> getDataSet() {
//        ArrayList results = new ArrayList<Machine_Service_Verification_Data_Object>();
        try {
            JSONObject jsonObj = new JSONObject(mac_service_data_obj);
            mac_service_verification_jsonobj = jsonObj.getJSONObject("mac_service_verifications");
            Iterator<String> accessory_receipt_contents = mac_service_verification_jsonobj.keys();

            while (accessory_receipt_contents.hasNext()) {
                String key = accessory_receipt_contents.next();
                if (mac_service_verification_jsonobj.get(key) instanceof JSONObject) {
                    int mac_id = Integer.parseInt(((JSONObject) mac_service_verification_jsonobj.get(key)).getString("id"));

                    String dservice_refno = ((JSONObject) mac_service_verification_jsonobj.get(key)).getString("service_refno");
                    String service_date = ((JSONObject) mac_service_verification_jsonobj.get(key)).getString("service_date");
                    String service_reason = ((JSONObject) mac_service_verification_jsonobj.get(key)).getString("service_reason");
                    String machine_no = ((JSONObject) mac_service_verification_jsonobj.get(key)).getString("machine_no");
                    String vendorname = ((JSONObject) mac_service_verification_jsonobj.get(key)).getString("vendorname");
                    String notes = ((JSONObject) mac_service_verification_jsonobj.get(key)).getString("notes");
                    int index = Integer.parseInt(((JSONObject) mac_service_verification_jsonobj.get(key)).getString("index"));
                    Machine_Service_Verification_Data_Object obj = new Machine_Service_Verification_Data_Object(mac_id, dservice_refno, service_date, service_reason, machine_no, vendorname, notes, index);
                    results.add(i, obj);
                    i++;
                }
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return results;
    }

    private void fetch_machine_service_verification() {

        Machine_Service_Verification_Activity.custPrograssbar.prograssCreate(this);

        session = new SessionManagement(getApplicationContext());
        HashMap<String, String> user = session.getUserDetails();
        processorid = user.get(SessionManagement.KEY_PROCESSOR_ID);
        userid = user.get(SessionManagement.KEY_USER_ID);

        JSONObject jsonObject = new JSONObject();
        try {

            jsonObject.put("userid", userid);
            jsonObject.put("processorid", processorid);

            JsonParser jsonParser = new JsonParser();
            Call<JsonObject> call = APIClient.getInterface().machine_service_verification((JsonObject) jsonParser.parse(jsonObject.toString()));
            GetResult getResult = new GetResult();
            getResult.setMyListener(this);
            getResult.callForLogin(call, "machine_service_verification");
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
                PopupMenu popup = new PopupMenu(Machine_Service_Verification_Activity.this, imageView);
                toggleDrawer();
                popup.show();
                break;
        }
    }

    @Override
    public void onBackPressed() {
        session = new SessionManagement(getApplicationContext());
        HashMap<String, String> user = session.getUserDetails();
        Intent intent = new Intent(Machine_Service_Verification_Activity.this, MainActivity.class);
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
            Machine_Service_Verification_Activity.custPrograssbar.closePrograssBar();
            if (callNo.equalsIgnoreCase("machine_service_verification")) {
                JSONObject jsonObject = new JSONObject(result.toString());
                String mStatus = jsonObject.optString("status");
                String message = jsonObject.optString("message");

                Machine_Service_Verification_Activity.custPrograssbar.closePrograssBar();
                if (mStatus.equals("success")) {
                    mac_service_data_obj = jsonObject.getJSONObject("data").toString();
//                        progress.setVisibility(View.GONE);

                    mRecyclerView = (RecyclerView) findViewById(R.id.my_recycler_view);
                    mRecyclerView.setHasFixedSize(true);
                    mLayoutManager = new LinearLayoutManager(Machine_Service_Verification_Activity.this);
                    mRecyclerView.setLayoutManager(mLayoutManager);
                    mAdapter = new Machine_Service_View_Adapter(getDataSet());
                    mRecyclerView.setAdapter(mAdapter);

                    ((Machine_Service_View_Adapter) mAdapter).setOnItemClickListener(new Machine_Service_View_Adapter.MyClickListener() {
                        @Override
                        public void onItemClick(int position, View v) {

                            int mac_id =((Machine_Service_View_Adapter) mAdapter).getMac_id(position);

                            String service_refno =((Machine_Service_View_Adapter) mAdapter).getService_refno(new String(String.valueOf(position)));
                            String service_date =((Machine_Service_View_Adapter) mAdapter).getService_date(new String(String.valueOf(position)));
                            String vendorname =((Machine_Service_View_Adapter) mAdapter).getVendorname(new String(String.valueOf(position)));
                            String service_reason =((Machine_Service_View_Adapter) mAdapter).getService_reason(new String(String.valueOf(position)));
                            String machine_no =((Machine_Service_View_Adapter) mAdapter).getMachine_no(new String(String.valueOf(position)));
                            String notes =((Machine_Service_View_Adapter) mAdapter).getNotes(new String(String.valueOf(position)));

                            Intent intent = new Intent(Machine_Service_Verification_Activity.this, Machie_Service_Verification_Details_Activity.class);
                            intent.putExtra("mac_service_verification", mac_service_verification.toString());
                            intent.putExtra("mac_service_verification_count", mac_service_verification_count);
                            intent.putExtra("processorid", processorid);
                            intent.putExtra("mac_id", new String(String.valueOf(mac_id)));
                            intent.putExtra("service_refno", service_refno);
                            intent.putExtra("service_date", service_date);
                            intent.putExtra("vendorname", vendorname);
                            intent.putExtra("service_reason", service_reason);
                            intent.putExtra("machine_no", machine_no);
                            intent.putExtra("notes", notes);
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
                    new AlertDialog.Builder(Machine_Service_Verification_Activity.this)
                            .setMessage(message)
                            .setCancelable(false)
                            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface arg1, int arg0) {
                                    arg1.dismiss();
                                }
                            }).show();
                }
                else {
                    new AlertDialog.Builder(Machine_Service_Verification_Activity.this)
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
