package com.bipinexports.productionqr.Activity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.ProgressBar;
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

public class Machie_Service_Verification_Details_Activity extends AppCompatActivity implements View.OnClickListener, GetResult.MyListener {

    String Id, User;

    SessionManagement session;
    GridView gridView;
    ProgressBar progress;
    String processorid, mac_id, service_refno, service_date,vendorname, service_reason, machine_no,notes;
    String userid, mac_service_verification, mac_service_verification_count;

    ImageView imageView;
    Button AddProg, AddReject, GoBack;

    int currcount =0;

    TextView text_MachineNo, text_Machine_Type, text_Line_No, text_Recruiter, text_Service_Date, Service_Refer_no, text_Service_Reason, text_Vendor_Name, text_Notes, text_Requested_By, text_Requested_On;
    EditText text_Approval_Rejection;
    TextView txtUser;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mac_service_verify_details);

        imageView = (ImageView) findViewById(R.id.imgd);
        gridView = (GridView) this.findViewById(R.id.grid);
        progress = (ProgressBar) findViewById(R.id.progress);
        progress.setVisibility(View.VISIBLE);

        mac_service_verification = getIntent().getStringExtra("mac_service_verification");
        mac_service_verification_count = getIntent().getStringExtra("mac_service_verification_count");

        currcount = Integer.parseInt(mac_service_verification_count);

        txtUser = (TextView) findViewById(R.id.txtUser);

        session = new SessionManagement(getApplicationContext());
        HashMap<String, String> user = session.getUserDetails();
        String name = user.get(SessionManagement.KEY_USER);
        this.Id = user.get(SessionManagement.KEY_PROCESSOR_ID);
        processorid = user.get(SessionManagement.KEY_PROCESSOR_ID);
        userid = user.get(SessionManagement.KEY_USER_ID);
        this.User = user.get(SessionManagement.KEY_USER);

        processorid = getIntent().getStringExtra("processorid");

        mac_id = getIntent().getStringExtra("mac_id");

        service_refno = getIntent().getStringExtra("service_refno");
        service_date = getIntent().getStringExtra("service_date");
        vendorname = getIntent().getStringExtra("vendorname");
        service_reason = getIntent().getStringExtra("service_reason");
        machine_no = getIntent().getStringExtra("machine_no");
        notes = getIntent().getStringExtra("notes");

        imageView.setOnClickListener(this);

        text_MachineNo = findViewById(R.id.text_MachineNo);
        text_Machine_Type = findViewById(R.id.text_Machine_Type);
        text_Vendor_Name = findViewById(R.id.text_Vendor_Name);
        text_Line_No = findViewById(R.id.text_Line_No);
        text_Recruiter = findViewById(R.id.text_Recruiter);
        text_Service_Date = findViewById(R.id.text_Service_Date);
        Service_Refer_no = findViewById(R.id.Service_Refer_no);
        text_Service_Reason = findViewById(R.id.text_Service_Reason);
        text_Notes = findViewById(R.id.text_Notes);
        text_Requested_By = findViewById(R.id.text_Requested_By);
        text_Requested_On = findViewById(R.id.text_Requested_On);
        text_Approval_Rejection = findViewById(R.id.text_Approval_Rejection);

        getvalue();
        get_mac_service_details();
        AddProg = findViewById(R.id.AddProg);
        AddProg.setOnClickListener(this);

        AddReject = findViewById(R.id.AddReject);
        AddReject.setOnClickListener(this);

        GoBack = findViewById(R.id.GoBack);
        GoBack.setOnClickListener(this);
    }

    public void onClick(View v) {
        if (isOnline()) {
            switch (v.getId()) {
                case R.id.imgd:
                    PopupMenu popup = new PopupMenu(Machie_Service_Verification_Details_Activity.this, imageView);
                    popup.getMenuInflater().inflate(R.menu.menu_main, popup.getMenu());
                    popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                        public boolean onMenuItemClick(MenuItem item) {
                            if (item.getItemId() == R.id.log) {
                                session.logoutUser();
                                finish();
                            }
                            else if (item.getItemId() == R.id.changepassword) {
                                Intent intent = new Intent(Machie_Service_Verification_Details_Activity.this, ChangepasswordActivity.class);
                                startActivity(intent);
                                finish();
                            }
                            return true;
                        }
                    });
                    popup.show();
                    break;
                case R.id.AddProg:


                    if(text_Approval_Rejection.equals(" ") || text_Approval_Rejection.equals("") || isEmpty(text_Approval_Rejection)){
                        AlertDialog alertDialog = new AlertDialog.Builder(Machie_Service_Verification_Details_Activity.this)
                                .setMessage("Please Enter Approve Reason!")
                                .setCancelable(false)
                                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface d, int arg0) {
                                        d.dismiss();
                                    }
                                }).show();
                    }
                    else if (text_Approval_Rejection.length() == 0) {
                        AlertDialog alertDialog = new AlertDialog.Builder(Machie_Service_Verification_Details_Activity.this)
                            .setMessage("Please Enter Approve Reason!")
                            .setCancelable(false)
                            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface d, int arg0) {
                                    d.dismiss();
                                }
                            }).show();
                    }
                    else {
                        progress.setVisibility(View.VISIBLE);
                        Accept_Service_Details();
                    }
                    break;
                case R.id.AddReject:

                    if(text_Approval_Rejection.equals(" ") || text_Approval_Rejection.equals("") || isEmpty(text_Approval_Rejection)){
                        AlertDialog alertDialog = new AlertDialog.Builder(Machie_Service_Verification_Details_Activity.this)
                                .setMessage("Please Enter Rejection Reason!")
                                .setCancelable(false)
                                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface d, int arg0) {
                                        d.dismiss();
                                    }
                                }).show();
                    }
                    else if (text_Approval_Rejection.length() == 0) {
                        AlertDialog alertDialog = new AlertDialog.Builder(Machie_Service_Verification_Details_Activity.this)
                            .setMessage("Please Enter Rejection Reason!")
                            .setCancelable(false)
                            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface d, int arg0) {
                                    d.dismiss();
                                }
                            }).show();
                    }
                    else
                    {
                        progress.setVisibility(View.VISIBLE);
                        Reject_Service_Details();
                    }

                    break;
                case R.id.GoBack:
                    progress.setVisibility(View.VISIBLE);
                    onBackPressed();
                    break;
            }
        }
        else {
            AlertDialog alertDialog = new AlertDialog.Builder(Machie_Service_Verification_Details_Activity.this)
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

    private boolean isEmpty(EditText etText) {
        return etText.getText().toString().trim().length() == 0;
    }

    private void Accept_Service_Details() {

        session = new SessionManagement(getApplicationContext());
        HashMap<String, String> user = session.getUserDetails();
        processorid = user.get(SessionManagement.KEY_PROCESSOR_ID);
        userid = user.get(SessionManagement.KEY_USER_ID);
        progress.setVisibility(View.VISIBLE);

        JSONObject jsonObject = new JSONObject();
        try {

            jsonObject.put("userid", userid);
            jsonObject.put("processorid", processorid);
            jsonObject.put("mid", mac_id);
            jsonObject.put("approve_reject_comments", text_Approval_Rejection.getText());

            JsonParser jsonParser = new JsonParser();
            Call<JsonObject> call = APIClient.getInterface().approve_mac_service_verification((JsonObject) jsonParser.parse(jsonObject.toString()));
            GetResult getResult = new GetResult();
            getResult.setMyListener(this);
            getResult.callForLogin(call, "approve_mac_service_verification");
        }
        catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void Reject_Service_Details() {

        session = new SessionManagement(getApplicationContext());
        HashMap<String, String> user = session.getUserDetails();
        processorid = user.get(SessionManagement.KEY_PROCESSOR_ID);
        userid = user.get(SessionManagement.KEY_USER_ID);
        progress.setVisibility(View.VISIBLE);

        JSONObject jsonObject = new JSONObject();
        try {

            jsonObject.put("userid", userid);
            jsonObject.put("processorid", processorid);
            jsonObject.put("mid", mac_id);
            jsonObject.put("approve_reject_comments", text_Approval_Rejection.getText());

            JsonParser jsonParser = new JsonParser();
            Call<JsonObject> call = APIClient.getInterface().reject_mac_service_verification((JsonObject) jsonParser.parse(jsonObject.toString()));
            GetResult getResult = new GetResult();
            getResult.setMyListener(this);
            getResult.callForLogin(call, "reject_mac_service_verification");
        }
        catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void get_mac_service_details() {

        session = new SessionManagement(getApplicationContext());
        HashMap<String, String> user = session.getUserDetails();
        processorid = user.get(SessionManagement.KEY_PROCESSOR_ID);
        userid = user.get(SessionManagement.KEY_USER_ID);
        progress.setVisibility(View.VISIBLE);

        JSONObject jsonObject = new JSONObject();
        try {

            jsonObject.put("userid", userid);
            jsonObject.put("processorid", processorid);
            jsonObject.put("mid", mac_id);

            JsonParser jsonParser = new JsonParser();
            Call<JsonObject> call = APIClient.getInterface().machine_service_verification_details((JsonObject) jsonParser.parse(jsonObject.toString()));
            GetResult getResult = new GetResult();
            getResult.setMyListener(this);
            getResult.callForLogin(call, "machine_service_verification_details");
        }
        catch (JSONException e) {
            e.printStackTrace();
        }
    }


    @Override
    public void onBackPressed() {
        session = new SessionManagement(getApplicationContext());
        HashMap<String, String> user = session.getUserDetails();
        Intent intent;

        Log.e("Bipin","currcount2 :" +currcount);
        if(currcount ==0)
        {
             intent = new Intent(Machie_Service_Verification_Details_Activity.this, MainActivity.class);
            intent.putExtra("name", user.get(SessionManagement.KEY_USER));
            intent.putExtra("userid", user.get(SessionManagement.KEY_USER_ID));
            intent.putExtra("processorid", user.get(SessionManagement.KEY_PROCESSOR_ID));
        }
        else
        {
            intent = new Intent(Machie_Service_Verification_Details_Activity.this, Machine_Service_Verification_Activity.class);
            intent.putExtra("name", user.get(SessionManagement.KEY_USER));
            intent.putExtra("userid", user.get(SessionManagement.KEY_USER_ID));
            intent.putExtra("processorid", user.get(SessionManagement.KEY_PROCESSOR_ID));
            intent.putExtra("mac_service_verification", mac_service_verification.toString());
            intent.putExtra("mac_service_verification_count", mac_service_verification_count);
        }


        startActivity(intent);
        finish();
    }

    public void getvalue() {
        txtUser.setText("Hello " + this.User);
        ModelClass modelClass = new ModelClass();
        modelClass.setmID(userid);
        progress.setVisibility(View.INVISIBLE);
    }


    public boolean isOnline() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        return cm.getActiveNetworkInfo() != null && cm.getActiveNetworkInfo().isConnectedOrConnecting();
    }

    @Override
    public void callback(JsonObject result, String callNo) {
        try {
            progress.setVisibility(View.GONE);

            if (callNo.equalsIgnoreCase("machine_service_verification_details")) {
                JSONObject jsonObject = new JSONObject(result.toString());
                String mStatus = jsonObject.optString("status");
                String message = jsonObject.optString("message");
                progress.setVisibility(View.INVISIBLE);
                if (mStatus.equals("success")) {
                    JSONObject jsonObj = jsonObject.getJSONObject("data");

                    JSONObject mac_service_details_Json_Obj = jsonObj.getJSONObject("mac_service_details");

                    String machine_no = mac_service_details_Json_Obj.optString("machine_no");
                    String machine_type = mac_service_details_Json_Obj.optString("machine_type");
                    String line_name = mac_service_details_Json_Obj.optString("line_name");
                    String recruiter_name = mac_service_details_Json_Obj.optString("recruiter_name");
                    String service_date = mac_service_details_Json_Obj.optString("service_date");
                    String service_reason = mac_service_details_Json_Obj.optString("service_reason");
                    String vendorname = mac_service_details_Json_Obj.optString("vendorname");
                    String notes = mac_service_details_Json_Obj.optString("notes");
                    String modified_by = mac_service_details_Json_Obj.optString("modified_by");
                    String modified_on = mac_service_details_Json_Obj.optString("modified_on");

                    text_MachineNo.setText(machine_no);
                    text_Machine_Type.setText(machine_type);
                    text_Line_No.setText(line_name);
                    text_Recruiter.setText(recruiter_name);
                    text_Vendor_Name.setText(vendorname);
                    text_Service_Date.setText(service_date);
                    Service_Refer_no.setText(service_refno);
                    text_Service_Reason.setText(service_reason);
                    text_Notes.setText(notes);
                    text_Requested_By.setText(modified_by);
                    text_Requested_On.setText(modified_on);
                }
                else if (mStatus.equals("logout"))
                {
                    new AlertDialog.Builder(Machie_Service_Verification_Details_Activity.this)
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
                    new AlertDialog.Builder(Machie_Service_Verification_Details_Activity.this)
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
            else if (callNo.equalsIgnoreCase("reject_mac_service_verification")) {
                JSONObject jsonObject = new JSONObject(result.toString());
                String mStatus = jsonObject.optString("status");
                String message = jsonObject.optString("message");
                progress.setVisibility(View.INVISIBLE);
                if (mStatus.equals("success")) {
                    new AlertDialog.Builder(Machie_Service_Verification_Details_Activity.this)
                            .setMessage(message)
                            .setCancelable(false)
                            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface arg1, int arg0) {
                                    arg1.dismiss();
                                    currcount = Integer.parseInt(mac_service_verification_count)-1;
                                    Log.e("Bipin","currcount2 :" +currcount);
                                    onBackPressed();
                                }
                            }).show();
                }
                else {
                    new AlertDialog.Builder(Machie_Service_Verification_Details_Activity.this)
                            .setMessage(message)
                            .setCancelable(false)
                            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface arg1, int arg0) {
                                    arg1.dismiss();
                                }
                            }).show();
                }
            }
            else if (callNo.equalsIgnoreCase("approve_mac_service_verification")) {
                JSONObject jsonObject = new JSONObject(result.toString());
                String mStatus = jsonObject.optString("status");
                String message = jsonObject.optString("message");
                progress.setVisibility(View.GONE);
                if (mStatus.equals("success")) {
                    new AlertDialog.Builder(Machie_Service_Verification_Details_Activity.this)
                            .setMessage(message)
                            .setCancelable(false)
                            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface arg1, int arg0) {
                                    arg1.dismiss();
                                    currcount = Integer.parseInt(mac_service_verification_count)-1;
                                    onBackPressed();
                                }
                            }).show();
                }
                else {
                    new AlertDialog.Builder(Machie_Service_Verification_Details_Activity.this)
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
