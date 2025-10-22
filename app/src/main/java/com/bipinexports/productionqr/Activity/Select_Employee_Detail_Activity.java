package com.bipinexports.productionqr.Activity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.os.Bundle;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.appcompat.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bipinexports.productionqr.APIClient;
import com.bipinexports.productionqr.GetResult;
import com.bipinexports.productionqr.ModelClass;
import com.bipinexports.productionqr.R;
import com.bipinexports.productionqr.SessionManagement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

import retrofit2.Call;

public class Select_Employee_Detail_Activity extends AppCompatActivity implements View.OnClickListener, GetResult.MyListener {

    String Id, User;

    SessionManagement session;
    GridView gridView;
    ProgressBar progress;
    String processorid;
    String userid,  type, myversionName;

    ImageView imageView;

    RelativeLayout btn_indetails, btn_outdetails, btn_absent_details;

    TextView txt_Employee_In_Count, txt_Employee_Out_Count, txt_Employee_Absent_Count;
    TextView txtUser;
    SwipeRefreshLayout swipeRefreshLayout;
    public static CustPrograssbar_new custPrograssbar_new;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_employee_details);

        imageView = (ImageView) findViewById(R.id.imgd);
        gridView = (GridView) this.findViewById(R.id.grid);
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
        type = getIntent().getStringExtra("type");
        myversionName = getIntent().getStringExtra("myversionName");
        imageView.setOnClickListener(this);

        btn_indetails =  findViewById(R.id.btn_indetails);
        btn_outdetails =  findViewById(R.id.btn_outdetails);
        btn_absent_details =  findViewById(R.id.btn_absent_details);

        txt_Employee_In_Count = findViewById(R.id.txt_Employee_In_Count);
        txt_Employee_Out_Count = findViewById(R.id.txt_Employee_Out_Count);
        txt_Employee_Absent_Count = findViewById(R.id.txt_Employee_Absent_Count);

        getvalue();
        get_Emp_Details();
        swipeRefreshLayout = findViewById(R.id.swiperefresh);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                swipeRefreshLayout.setRefreshing(false);
                get_Emp_Details();
            }
        });
        swipeRefreshLayout.setColorSchemeColors(Color.BLACK);

    }

    public void onClick(View v) {
        if (isOnline()) {
            switch (v.getId()) {
                case R.id.imgd:
                    PopupMenu popup = new PopupMenu(Select_Employee_Detail_Activity.this, imageView);
                    popup.getMenuInflater().inflate(R.menu.menu_main, popup.getMenu());

                    popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                        public boolean onMenuItemClick(MenuItem item) {
                            if (item.getItemId() == R.id.log) {
                                session.logoutUser();
                                finish();
                            }
                            else if (item.getItemId() == R.id.changepassword) {
                                Intent intent = new Intent(Select_Employee_Detail_Activity.this, ChangepasswordActivity.class);
                                startActivity(intent);
                                finish();
                            }
                            return true;
                        }
                    });
                    popup.show();
                    break;
                case R.id.btn_indetails:
                    Intent intent = new Intent(Select_Employee_Detail_Activity.this, In_Employee_Operation_Mapping_Activity.class);
                    intent.putExtra("selected_type", "In");
                    intent.putExtra("type", type);
                    intent.putExtra("myversionName", myversionName);
                    intent.putExtra("userid", userid);
                    intent.putExtra("processorid", processorid);
                    startActivity(intent);
                    finish();
                    break;
            case R.id.btn_outdetails:
                intent = new Intent(Select_Employee_Detail_Activity.this, Out_Employee_Operation_Mapping_Activity.class);
                intent.putExtra("selected_type", "Out");
                intent.putExtra("type", type);
                intent.putExtra("myversionName", myversionName);
                intent.putExtra("userid", userid);
                intent.putExtra("processorid", processorid);
                startActivity(intent);
                finish();
                break;
            case R.id.btn_absent_details:
                intent = new Intent(Select_Employee_Detail_Activity.this, Absent_Employee_Operation_Mapping_Activity.class);
                intent.putExtra("selected_type", "Absent");
                intent.putExtra("type", type);
                intent.putExtra("myversionName", myversionName);
                intent.putExtra("userid", userid);
                intent.putExtra("processorid", processorid);
                startActivity(intent);
                finish();
                break;

            }

        }
        else {
            AlertDialog alertDialog = new AlertDialog.Builder(Select_Employee_Detail_Activity.this)
                    .setMessage("Please Check Your Internet Connection")
                    .setCancelable(false)
                    .setNegativeButton("Exit", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface arg1, int arg0) {
                            arg1.dismiss();
                            finish();
                        }
                    }).show();
        }
    }

    private void get_Emp_Details() {

        session = new SessionManagement(getApplicationContext());
        HashMap<String, String> user = session.getUserDetails();
        processorid = user.get(SessionManagement.KEY_PROCESSOR_ID);
        userid = user.get(SessionManagement.KEY_USER_ID);
        Select_Employee_Detail_Activity.custPrograssbar_new.prograssCreate(this);

        JSONObject jsonObject = new JSONObject();
        try {

            jsonObject.put("userid", userid);
            jsonObject.put("processorid", processorid);

            JsonParser jsonParser = new JsonParser();
            Call<JsonObject> call = APIClient.getInterface().get_in_out_count((JsonObject) jsonParser.parse(jsonObject.toString()));
            GetResult getResult = new GetResult();
            getResult.setMyListener(this);
            getResult.callForLogin(call, "get_in_out_count");
        }
        catch (JSONException e) {
            e.printStackTrace();
        }
    }


    @Override
    public void onBackPressed() {
        session = new SessionManagement(getApplicationContext());
        HashMap<String, String> user = session.getUserDetails();
        Intent intent = new Intent(Select_Employee_Detail_Activity.this, MainActivity.class);
        intent.putExtra("name", user.get(SessionManagement.KEY_USER));
        intent.putExtra("userid", user.get(SessionManagement.KEY_USER_ID));
        intent.putExtra("processorid", user.get(SessionManagement.KEY_PROCESSOR_ID));
        startActivity(intent);
        finish();
    }

    public void getvalue() {
        txtUser.setText("Hello " + this.User);
        ModelClass modelClass = new ModelClass();
        modelClass.setmID(userid);
        Select_Employee_Detail_Activity.custPrograssbar_new.closePrograssBar();
    }


    public boolean isOnline() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        return cm.getActiveNetworkInfo() != null && cm.getActiveNetworkInfo().isConnectedOrConnecting();
    }

    @Override
    public void callback(JsonObject result, String callNo) {
        try {

            if (callNo.equalsIgnoreCase("get_in_out_count"))
            {
                JSONObject jsonObject = new JSONObject(result.toString());
                String mStatus = jsonObject.optString("status");
                String message = jsonObject.optString("message");
                Select_Employee_Detail_Activity.custPrograssbar_new.closePrograssBar();
                if (mStatus.equals("success")) {
                    JSONObject jsonObj = jsonObject.getJSONObject("data");
                    String overall_cnt = jsonObj.optString("overall_cnt");
                    String in_empe_cnt = jsonObj.optString("in_cnt");
                    String out_emp_count = jsonObj.optString("out_cnt");
                    String absent_cnt = jsonObj.optString("absent_cnt");

                    txt_Employee_In_Count.setText(" " +in_empe_cnt);
                    txt_Employee_Out_Count.setText(" " +out_emp_count);
                    txt_Employee_Absent_Count.setText(" " +absent_cnt);

                    if(Integer.parseInt(in_empe_cnt) > 0)
                    {
                        btn_indetails.setOnClickListener(this);
                    }
                    if(Integer.parseInt(out_emp_count) > 0)
                    {
                        btn_outdetails.setOnClickListener(this);
                    }
                    if(Integer.parseInt(absent_cnt) > 0)
                    {
                        btn_absent_details.setOnClickListener(this);
                    }
                }
                else if (mStatus.equals("logout"))
                {
                    new AlertDialog.Builder(Select_Employee_Detail_Activity.this)
                            .setMessage(message)
                            .setCancelable(false)
                            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface arg1, int arg0) {
                                    arg1.dismiss();
                                    session.logoutUser();
                                    finishAffinity();
                                    finish();
                                }
                            }).show();
                }
                else {
                    new AlertDialog.Builder(Select_Employee_Detail_Activity.this)
                            .setMessage(message)
                            .setCancelable(false)
                            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface arg1, int arg0) {
                                    arg1.dismiss();
                                    onBackPressed();
                                }
                            }).show();
                }

            }
        }
        catch (Exception e) {
        }
    }
}
