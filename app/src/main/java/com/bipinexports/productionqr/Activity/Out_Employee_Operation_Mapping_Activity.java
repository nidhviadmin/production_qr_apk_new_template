package com.bipinexports.productionqr.Activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
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
import com.bipinexports.productionqr.GetResult;
import com.bipinexports.productionqr.ModelClass;
import com.bipinexports.productionqr.Out_Employee_Data_Object;
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


public class Out_Employee_Operation_Mapping_Activity extends AppCompatActivity implements View.OnClickListener, GetResult.MyListener {

    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private static String LOG_TAG = "CardViewActivity";

    SessionManagement session;
    TextView txtUser;
    ImageView imageView;
    String employee_data_obj;
    JSONObject employee_jsonobj;
    String processorid, type, myversionName, selected_type;
    String userid, User, Id;
    ProgressBar progress;

    public static CustPrograssbar_new custPrograssbar_new;
    SwipeRefreshLayout swipeRefreshLayout;

    ArrayList results = new ArrayList<Out_Employee_Data_Object>();
    int i = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_out_employee_operation_mapping);

        txtUser = findViewById(R.id.txtUser);
        imageView = findViewById(R.id.imgd);
        progress = (ProgressBar) findViewById(R.id.progress);
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
        type = getIntent().getStringExtra("type");
        myversionName = getIntent().getStringExtra("myversionName");
        selected_type = getIntent().getStringExtra("selected_type");
        imageView.setOnClickListener(this);


        getvalue();
        fetch_employee_Details();
        swipeRefreshLayout = findViewById(R.id.swiperefresh);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                swipeRefreshLayout.setRefreshing(false);
                results = new ArrayList<Out_Employee_Data_Object>();
                fetch_employee_Details();
            }
        });
        swipeRefreshLayout.setColorSchemeColors(Color.BLACK);
    }

    private ArrayList<Out_Employee_Data_Object> getDataSet() {
//        ArrayList results = new ArrayList<Out_Employee_Data_Object>();
        try {
            JSONObject jsonObj = new JSONObject(employee_data_obj);
            employee_jsonobj = jsonObj.getJSONObject("empdetails");
            Iterator<String> employee_contents = employee_jsonobj.keys();

            while (employee_contents.hasNext()) {
                String key = employee_contents.next();
                if (employee_jsonobj.get(key) instanceof JSONObject) {
                    String empcode = ((JSONObject) employee_jsonobj.get(key)).getString("empcode");
                    String empname = ((JSONObject) employee_jsonobj.get(key)).getString("empname");
                    String imgpath = ((JSONObject) employee_jsonobj.get(key)).getString("imgpath");
                    String hrmsrecid = ((JSONObject) employee_jsonobj.get(key)).getString("hrmsrecid");
                    String operationid = ((JSONObject) employee_jsonobj.get(key)).getString("operationid");
                    String hrmssecid = ((JSONObject) employee_jsonobj.get(key)).getString("hrmssecid");
                    String designation_name = ((JSONObject) employee_jsonobj.get(key)).getString("designation_name");
                    String operation_name = ((JSONObject) employee_jsonobj.get(key)).getString("operation_name");
                    String process_name = ((JSONObject) employee_jsonobj.get(key)).getString("process_name");
                    String section_name = ((JSONObject) employee_jsonobj.get(key)).getString("section_name");
                    String qrcontractorid = ((JSONObject) employee_jsonobj.get(key)).getString("qrcontractorid");
                    String currentstatus = ((JSONObject) employee_jsonobj.get(key)).getString("currentstatus");
                    String current_inoutimte = ((JSONObject) employee_jsonobj.get(key)).getString("current_inoutimte");
                    String noofconsecutiveabsentdays = ((JSONObject) employee_jsonobj.get(key)).getString("noofconsecutiveabsentdays");
                    String mobileno = ((JSONObject) employee_jsonobj.get(key)).getString("mobileno");
                    String selecteddate = ((JSONObject) employee_jsonobj.get(key)).getString("selecteddate");

                    Out_Employee_Data_Object obj = new Out_Employee_Data_Object(empcode, empname, imgpath, hrmsrecid, operationid, hrmssecid,designation_name, operation_name, process_name, section_name, qrcontractorid, currentstatus, current_inoutimte, noofconsecutiveabsentdays, selecteddate, mobileno);
                    results.add(i, obj);
                    i++;
                }
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return results;
    }


    public void clearfunc() {
        Out_Employee_Operation_Mapping_Activity.custPrograssbar_new.closePrograssBar();
        Intent intent = new Intent(Out_Employee_Operation_Mapping_Activity.this, Empty_Employee_Data_Activity.class);
        intent.putExtra("type", type);
        intent.putExtra("processorid", processorid);
        intent.putExtra("myversionName", myversionName);
        intent.putExtra("selected_type", selected_type);
        startActivity(intent);
        finish();
    }
    private void fetch_employee_Details() {

        Out_Employee_Operation_Mapping_Activity.custPrograssbar_new.prograssCreate(this);
        if (mAdapter != null) {
            clearfunc();
        }
        else {

            session = new SessionManagement(getApplicationContext());
            HashMap<String, String> user = session.getUserDetails();
            processorid = user.get(SessionManagement.KEY_PROCESSOR_ID);
            userid = user.get(SessionManagement.KEY_USER_ID);

            JSONObject jsonObject = new JSONObject();
            try {
                jsonObject.put("userid", userid);
                jsonObject.put("processorid", processorid);

                JsonParser jsonParser = new JsonParser();
                Call<JsonObject> call;

                call = APIClient.getInterface().fetch_employee_out_details((JsonObject) jsonParser.parse(jsonObject.toString()));
                GetResult getResult = new GetResult();
                getResult.setMyListener(this);
                getResult.callForLogin(call, "employee_details");
            } catch (JSONException e) {
                e.printStackTrace();
            }
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
                PopupMenu popup = new PopupMenu(Out_Employee_Operation_Mapping_Activity.this, imageView);
                popup.getMenuInflater().inflate(R.menu.menu_main, popup.getMenu());

                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    public boolean onMenuItemClick(MenuItem item) {
                        if (item.getItemId() == R.id.log) {
                            session.logoutUser();
                            finish();
                        }
                        else if (item.getItemId() == R.id.changepassword) {
                            Intent intent = new Intent(Out_Employee_Operation_Mapping_Activity.this, ChangepasswordActivity.class);
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
        Intent intent = new Intent(Out_Employee_Operation_Mapping_Activity.this, Select_Employee_Detail_Activity.class);
        intent.putExtra("selected_type", selected_type);
        intent.putExtra("type", type);
        intent.putExtra("myversionName", myversionName);
        intent.putExtra("name", user.get(SessionManagement.KEY_USER));
        intent.putExtra("userid", user.get(SessionManagement.KEY_USER_ID));
        intent.putExtra("processorid", user.get(SessionManagement.KEY_PROCESSOR_ID));
        startActivity(intent);
        finish();
    }

    @Override
    public void callback(JsonObject result, String callNo) {
        try {

            if (callNo.equalsIgnoreCase("employee_details")) {
                JSONObject jsonObject = new JSONObject(result.toString());
                String mStatus = jsonObject.optString("status");
                String message = jsonObject.optString("message");

                if (mStatus.equals("success"))
                {
                    employee_data_obj = jsonObject.getJSONObject("data").toString();
                    mRecyclerView = (RecyclerView) findViewById(R.id.my_recycler_view);
                    mRecyclerView.setHasFixedSize(true);
                    mLayoutManager = new LinearLayoutManager(Out_Employee_Operation_Mapping_Activity.this);
                    mRecyclerView.setLayoutManager(mLayoutManager);
                    mAdapter = new Out_Employee_Operation_Mapping_View_Adapter(this,getDataSet());

                    mRecyclerView.setAdapter(mAdapter);

                    ((Out_Employee_Operation_Mapping_View_Adapter) mAdapter).setOnItemClickListener(new Out_Employee_Operation_Mapping_View_Adapter.MyClickListener() {
                        @Override
                        public void onItemClick(int position, View v) {

                        String empcode =((Out_Employee_Operation_Mapping_View_Adapter) mAdapter).getEmpcode(new String(String.valueOf(position)));
                        String empname =((Out_Employee_Operation_Mapping_View_Adapter) mAdapter).getEmpname(new String(String.valueOf(position)));
                        String imgpath =((Out_Employee_Operation_Mapping_View_Adapter) mAdapter).getImgpath(new String(String.valueOf(position)));
                        String hrmsrecid =((Out_Employee_Operation_Mapping_View_Adapter) mAdapter).getHrmsrecid(new String(String.valueOf(position)));
                        String operationid =((Out_Employee_Operation_Mapping_View_Adapter) mAdapter).getOperationid(new String(String.valueOf(position)));
                        String hrmssecid =((Out_Employee_Operation_Mapping_View_Adapter) mAdapter).getHrmssecid(new String(String.valueOf(position)));

                        String designation =((Out_Employee_Operation_Mapping_View_Adapter) mAdapter).getDesignation(new String(String.valueOf(position)));
                        String operation =((Out_Employee_Operation_Mapping_View_Adapter) mAdapter).getOperation(new String(String.valueOf(position)));
                        String process =((Out_Employee_Operation_Mapping_View_Adapter) mAdapter).getProcess(new String(String.valueOf(position)));
                        String section_name =((Out_Employee_Operation_Mapping_View_Adapter) mAdapter).getSection_name(new String(String.valueOf(position)));
                        String qrcontractorid =((Out_Employee_Operation_Mapping_View_Adapter) mAdapter).getQrcontractorid(new String(String.valueOf(position)));
                        String currentstatus =((Out_Employee_Operation_Mapping_View_Adapter) mAdapter).getCurrentstatus(new String(String.valueOf(position)));
                        String current_inoutimte =((Out_Employee_Operation_Mapping_View_Adapter) mAdapter).getCurrentstatus(new String(String.valueOf(position)));

//                         && currentstatus.equals("IN") && selected_type.equals("In")

                        if(type.equals("Bundle_mapp"))
                        {
                            Intent intent = new Intent(Out_Employee_Operation_Mapping_Activity.this, Employee_Bundle_Mapping_Activity.class);
                            intent.putExtra("processorid", processorid);
                            intent.putExtra("empcode", empcode);
                            intent.putExtra("empname", empname);
                            intent.putExtra("myversionName", myversionName);
                            intent.putExtra("selected_type", selected_type);
                            intent.putExtra("type", type);
                            startActivity(intent);
                            finish();
                        }
                        else  if(type.equals("New"))
                        {
                            Intent intent = new Intent(Out_Employee_Operation_Mapping_Activity.this, Employee_Operation_Mapping_Details_Activity.class);
                            intent.putExtra("processorid", processorid);
                            intent.putExtra("empcode", empcode);
                            intent.putExtra("empname", empname);
                            intent.putExtra("imgpath", imgpath);
                            intent.putExtra("hrmsrecid", hrmsrecid);
                            intent.putExtra("operationid", operationid);
                            intent.putExtra("hrmssecid", hrmssecid);
                            intent.putExtra("designation", designation);
                            intent.putExtra("operation", operation);
                            intent.putExtra("process", process);
                            intent.putExtra("section_name", section_name);
                            intent.putExtra("qrcontractorid", qrcontractorid);
                            intent.putExtra("type", type);
                            intent.putExtra("selected_type", selected_type);
                            intent.putExtra("myversionName", myversionName);
                            startActivity(intent);
                            finish();
                        }
                        else
                        {
                            // still
                        }
                        }
                    });
                    mAdapter.notifyDataSetChanged();
                    Log.e("Bipin","Success");
                }
                else if (mStatus.equals("nodatafound")) {
                    if(mAdapter == null)
                    {
                        // Do nothing
                    }
                    new AlertDialog.Builder(Out_Employee_Operation_Mapping_Activity.this)
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
                    new AlertDialog.Builder(Out_Employee_Operation_Mapping_Activity.this)
                        .setMessage(message)
                        .setCancelable(false)
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface arg1, int arg0) {
                                arg1.dismiss();
                            }
                        }).show();
                }
                Out_Employee_Operation_Mapping_Activity.custPrograssbar_new.closePrograssBar();
            }
        }
        catch (Exception e) {
        }
    }
}
