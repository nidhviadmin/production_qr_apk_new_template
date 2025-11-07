package com.bipinexports.productionqr.Activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.icu.text.SimpleDateFormat;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.bipinexports.productionqr.APIClient;
import com.bipinexports.productionqr.GetResult;
import com.bipinexports.productionqr.In_Employee_Bundle_Data_Object;
import com.bipinexports.productionqr.ModelClass;
import com.bipinexports.productionqr.R;
import com.bipinexports.productionqr.SessionManagement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;

import retrofit2.Call;

public class In_Employee_Bundle_Scan_Activity extends BaseActivity implements View.OnClickListener, GetResult.MyListener {

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
    TextView present_emp;

    ArrayList results = new ArrayList<In_Employee_Bundle_Data_Object>();
    int i = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_in_employee_bundle_reports);
        setContentView(R.layout.activity_base);
        setupDrawer();

        View content = getLayoutInflater().inflate(
                R.layout.activity_in_employee_bundle_reports,
                findViewById(R.id.content_frame),
                true
        );

        txtUser = content.findViewById(R.id.txtUser);
        imageView = content.findViewById(R.id.imgd);
        progress = (ProgressBar) content.findViewById(R.id.progress);
        custPrograssbar_new = new CustPrograssbar_new();

        txtUser = (TextView) content.findViewById(R.id.txtUser);

        session = new SessionManagement(getApplicationContext());
        HashMap<String, String> user = session.getUserDetails();
        String name = user.get(SessionManagement.KEY_USER);
        this.Id = user.get(SessionManagement.KEY_PROCESSOR_ID);
        processorid = user.get(SessionManagement.KEY_PROCESSOR_ID);
        userid = user.get(SessionManagement.KEY_USER_ID);
        this.User = user.get(SessionManagement.KEY_USER);
        present_emp = content.findViewById(R.id.present_emp);

        processorid = getIntent().getStringExtra("processorid");
        type = getIntent().getStringExtra("type");
        myversionName = getIntent().getStringExtra("myversionName");
        selected_type = "Bundle_Mapp";
        imageView.setOnClickListener(this);

        setupNotifications();

        handleNotificationIntent(getIntent());
        Date date = new Date(); // or any Date object
        SimpleDateFormat formatter = new SimpleDateFormat("dd-MMM-yyyy", Locale.ENGLISH);
        String formattedDate = formatter.format(date);

        present_emp.setText("" +formattedDate +" - Present Employee Scan Count");

        getvalue();
        fetch_employee_Details();
        swipeRefreshLayout = content.findViewById(R.id.swiperefresh);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                swipeRefreshLayout.setRefreshing(false);
                fetch_employee_Details();
            }
        });
        swipeRefreshLayout.setColorSchemeColors(Color.BLACK);
    }

    private ArrayList<In_Employee_Bundle_Data_Object> getDataSet() {
//        ArrayList results = new ArrayList<In_Employee_Bundle_Data_Object>();
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
                    String rec_code = ((JSONObject) employee_jsonobj.get(key)).getString("rec_code");
                    String operationid = ((JSONObject) employee_jsonobj.get(key)).getString("operationid");
                    String hrmssecid = ((JSONObject) employee_jsonobj.get(key)).getString("hrmssecid");
                    String designation_name = ((JSONObject) employee_jsonobj.get(key)).getString("des_name");
                    String operation_name = ((JSONObject) employee_jsonobj.get(key)).getString("hrms_operation_name");
                    String process_name = ((JSONObject) employee_jsonobj.get(key)).getString("hrms_process_name");
                    String section_name = ((JSONObject) employee_jsonobj.get(key)).getString("sec_name");
                    String qrcontractorid = ((JSONObject) employee_jsonobj.get(key)).getString("qrcontractorid");
                    String mappednt = ((JSONObject) employee_jsonobj.get(key)).getString("mapped_cnt");

                    In_Employee_Bundle_Data_Object obj = new In_Employee_Bundle_Data_Object(empcode, empname, imgpath, rec_code, operationid, hrmssecid,designation_name, process_name, operation_name, section_name, qrcontractorid,mappednt);
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

        Intent intent = new Intent(In_Employee_Bundle_Scan_Activity.this, Empty_Employee_Data_Activity.class);
        intent.putExtra("type", type);
        intent.putExtra("processorid", processorid);
        intent.putExtra("myversionName", myversionName);
        intent.putExtra("selected_type", selected_type);
        In_Employee_Bundle_Scan_Activity.custPrograssbar_new.closePrograssBar();
        startActivity(intent);
        finish();
    }

    private void fetch_employee_Details() {

        In_Employee_Bundle_Scan_Activity.custPrograssbar_new.prograssCreate(this);

        if (mAdapter != null) {
            clearfunc();
        }
        else
        {
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
                call = APIClient.getInterface().get_bundle_qr_mapped_data((JsonObject) jsonParser.parse(jsonObject.toString()));
                GetResult getResult = new GetResult();
                getResult.setMyListener(this);
                getResult.callForLogin(call, "employee_details");
            }
            catch (JSONException e) {
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
                toggleDrawer();
                break;
        }
    }

    @Override
    public void onBackPressed() {
        session = new SessionManagement(getApplicationContext());
        HashMap<String, String> user = session.getUserDetails();
        Intent intent = new Intent(In_Employee_Bundle_Scan_Activity.this, MainActivity.class);
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
            In_Employee_Bundle_Scan_Activity.custPrograssbar_new.closePrograssBar();
            if (callNo.equalsIgnoreCase("employee_details")) {
                JSONObject jsonObject = new JSONObject(result.toString());
                String mStatus = jsonObject.optString("status");
                String message = jsonObject.optString("message");

                if (mStatus.equals("success")) {

                    employee_data_obj = jsonObject.getJSONObject("data").toString();

                    mRecyclerView = (RecyclerView) findViewById(R.id.my_recycler_view);
                    mRecyclerView.setHasFixedSize(true);
                    mLayoutManager = new LinearLayoutManager(In_Employee_Bundle_Scan_Activity.this);
                    mRecyclerView.setLayoutManager(mLayoutManager);
                    mAdapter = new In_Employee_Bundle_Scan_View_Adapter(this,getDataSet());
                    mRecyclerView.setAdapter(mAdapter);

                    ((In_Employee_Bundle_Scan_View_Adapter) mAdapter).setOnItemClickListener(new In_Employee_Bundle_Scan_View_Adapter.MyClickListener()
                    {
                        @Override
                        public void onItemClick(int position, View v) {

                            String empcode = ((In_Employee_Bundle_Scan_View_Adapter) mAdapter).getEmpcode(new String(String.valueOf(position)));
                            String empname = ((In_Employee_Bundle_Scan_View_Adapter) mAdapter).getEmpname(new String(String.valueOf(position)));
                            String imgpath = ((In_Employee_Bundle_Scan_View_Adapter) mAdapter).getImgpath(new String(String.valueOf(position)));
                            String hrmsrecid = ((In_Employee_Bundle_Scan_View_Adapter) mAdapter).getHrmsrecid(new String(String.valueOf(position)));
                            String operationid = ((In_Employee_Bundle_Scan_View_Adapter) mAdapter).getOperationid(new String(String.valueOf(position)));
                            String hrmssecid = ((In_Employee_Bundle_Scan_View_Adapter) mAdapter).getHrmssecid(new String(String.valueOf(position)));

                            String designation = ((In_Employee_Bundle_Scan_View_Adapter) mAdapter).getDesignation(new String(String.valueOf(position)));
                            String operation = ((In_Employee_Bundle_Scan_View_Adapter) mAdapter).getOperation(new String(String.valueOf(position)));
                            String process = ((In_Employee_Bundle_Scan_View_Adapter) mAdapter).getProcess(new String(String.valueOf(position)));
                            String section_name = ((In_Employee_Bundle_Scan_View_Adapter) mAdapter).getSection_name(new String(String.valueOf(position)));
                            String qrcontractorid = ((In_Employee_Bundle_Scan_View_Adapter) mAdapter).getQrcontractorid(new String(String.valueOf(position)));
                            String mapped_cnt = ((In_Employee_Bundle_Scan_View_Adapter) mAdapter).getmapped_cnt(new String(String.valueOf(position)));

//                            if (type.equals("Bundle_mapp")) {
//                                Intent intent = new Intent(In_Employee_Bundle_Scan_Activity.this, Employee_Bundle_Mapping_Activity.class);
//                                intent.putExtra("processorid", processorid);
//                                intent.putExtra("empcode", empcode);
//                                intent.putExtra("empname", empname);
//                                intent.putExtra("myversionName", myversionName);
//                                intent.putExtra("selected_type", selected_type);
//                                intent.putExtra("type", type);
//                                startActivity(intent);
//                                finish();
//                            }
//                            else if (type.equals("New")) {
//                                Intent intent = new Intent(In_Employee_Bundle_Scan_Activity.this, Employee_Operation_Mapping_Details_Activity.class);
//                                intent.putExtra("processorid", processorid);
//                                intent.putExtra("empcode", empcode);
//                                intent.putExtra("empname", empname);
//                                intent.putExtra("imgpath", imgpath);
//                                intent.putExtra("hrmsrecid", hrmsrecid);
//                                intent.putExtra("operationid", operationid);
//                                intent.putExtra("hrmssecid", hrmssecid);
//                                intent.putExtra("designation", designation);
//                                intent.putExtra("operation", operation);
//                                intent.putExtra("process", process);
//                                intent.putExtra("section_name", section_name);
//                                intent.putExtra("qrcontractorid", qrcontractorid);
//                                intent.putExtra("type", type);
//                                intent.putExtra("selected_type", selected_type);
//                                intent.putExtra("myversionName", myversionName);
//                                startActivity(intent);
//                                finish();
//                            }
//                            else {
//                                // still
//                            }
                        }
                    });
                    mAdapter.notifyDataSetChanged();
                }
                else if (mStatus.equals("nodatafound")) {
                    if(mAdapter == null)
                    {
                        // Do nothing
                    }
                    new AlertDialog.Builder(In_Employee_Bundle_Scan_Activity.this)
                        .setMessage(message)
                        .setCancelable(false)
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface arg1, int arg0) {
                                arg1.dismiss();
                                onBackPressed();
                            }
                        }).show();
                }
                else 
                {
                    new AlertDialog.Builder(In_Employee_Bundle_Scan_Activity.this)
                        .setMessage(message)
                        .setCancelable(false)
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface arg1, int arg0) {
                                arg1.dismiss();
                            }
                        }).show();
                }
                In_Employee_Bundle_Scan_Activity.custPrograssbar_new.closePrograssBar();
            }
        }
        catch (Exception e) {
        }
    }
}
