package com.bipinexports.productionqrnew.Activity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.os.Bundle;

import android.view.View;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bipinexports.productionqrnew.APIClient;
import com.bipinexports.productionqrnew.GetResult;
import com.bipinexports.productionqrnew.ModelClass;
import com.bipinexports.productionqrnew.R;
import com.bipinexports.productionqrnew.SessionManagement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

import retrofit2.Call;

public class SelectJobSummaryActivity extends BaseActivity implements View.OnClickListener, GetResult.MyListener {

    String Id, User;

    SessionManagement session;
    GridView gridView;
    ProgressBar progress;
    String processorid;
    String userid;

    ImageView imageView;

    RelativeLayout btn_AssignedJobs, btn_PendingJobs, btn_InprocessJobs, btn_CompletedJobs;

    TextView txtAssignedBundleCount, txtassignedbundleqty, txtAssignedPrice, txtPendingBundleCount, txtapendingbundleqty, txtPedndingPrice,
            txtInprocesscnt, txtinprocessbundleqty, txtInprocessPrice, txtCompletedBundleCount, txtcompletedbundleqty, txtCompletedPrice,
            txtGrandtotalBundleCount, txtGrandtotalbundleqty, txtGrandtotalPrice;
    TextView txtUser;
    public static CustPrograssbar_new custPrograssbar_new;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_base);  // Changed to base layout
        setupDrawer();  // Setup drawer before using it

        // Inflate your actual content into the content_frame
        View content = getLayoutInflater().inflate(R.layout.activity_select_jobsummary,
                findViewById(R.id.content_frame), true);

        // Find views from the inflated content
        progress = content.findViewById(R.id.progress);

        custPrograssbar_new = new CustPrograssbar_new();
        SelectJobSummaryActivity.custPrograssbar_new.prograssCreate(this);

        imageView = content.findViewById(R.id.imgd);
        gridView = content.findViewById(R.id.grid);

        txtUser = content.findViewById(R.id.txtUser);

        session = new SessionManagement(getApplicationContext());
        HashMap<String, String> user = session.getUserDetails();
        String name = user.get(SessionManagement.KEY_USER);
        this.Id = user.get(SessionManagement.KEY_PROCESSOR_ID);
        processorid = user.get(SessionManagement.KEY_PROCESSOR_ID);
        userid = user.get(SessionManagement.KEY_USER_ID);
        this.User = user.get(SessionManagement.KEY_USER);

        processorid = getIntent().getStringExtra("processorid");
        imageView.setOnClickListener(this);

        btn_AssignedJobs = content.findViewById(R.id.btn_AssignedJobs);
        btn_PendingJobs = content.findViewById(R.id.btn_PendingJobs);
        btn_InprocessJobs = content.findViewById(R.id.btn_InprocessJobs);
        btn_CompletedJobs = content.findViewById(R.id.btn_CompletedJobs);

        txtAssignedBundleCount = content.findViewById(R.id.txtAssignedBundleCount);
        txtassignedbundleqty = content.findViewById(R.id.txtassignedbundleqty);
        txtAssignedPrice = content.findViewById(R.id.txtAssignedPrice);

        txtPendingBundleCount = content.findViewById(R.id.txtPendingBundleCount);
        txtapendingbundleqty = content.findViewById(R.id.txtapendingbundleqty);
        txtPedndingPrice = content.findViewById(R.id.txtPedndingPrice);

        txtInprocesscnt = content.findViewById(R.id.txtInprocesscnt);
        txtinprocessbundleqty = content.findViewById(R.id.txtinprocessbundleqty);
        txtInprocessPrice = content.findViewById(R.id.txtInprocessPrice);

        txtCompletedBundleCount = content.findViewById(R.id.txtCompletedBundleCount);
        txtcompletedbundleqty = content.findViewById(R.id.txtcompletedbundleqty);
        txtCompletedPrice = content.findViewById(R.id.txtCompletedPrice);

        txtGrandtotalBundleCount = content.findViewById(R.id.txtGrandtotalBundleCount);
        txtGrandtotalbundleqty = content.findViewById(R.id.txtGrandtotalbundleqty);
        txtGrandtotalPrice = content.findViewById(R.id.txtGrandtotalPrice);

        getvalue();
        getJobDetails();

        btn_AssignedJobs.setOnClickListener(this);
        btn_PendingJobs.setOnClickListener(this);
        btn_InprocessJobs.setOnClickListener(this);
        btn_CompletedJobs.setOnClickListener(this);
    }

    public void onClick(View v) {
        if (isOnline()) {
            switch (v.getId()) {
                case R.id.imgd:
                    PopupMenu popup = new PopupMenu(SelectJobSummaryActivity.this, imageView);
                    toggleDrawer();
                    popup.show();
                    break;
                case R.id.btn_AssignedJobs:
                    SelectJobSummaryActivity.custPrograssbar_new.prograssCreate(this);
                    fetchAssignJobDetails();
                    break;
            case R.id.btn_PendingJobs:
                SelectJobSummaryActivity.custPrograssbar_new.prograssCreate(this);
                fetchPendingJobDetails();
                break;
            case R.id.btn_InprocessJobs:
                SelectJobSummaryActivity.custPrograssbar_new.prograssCreate(this);
                fetchInprocessJobDetails();
                break;

            case R.id.btn_CompletedJobs:
                SelectJobSummaryActivity.custPrograssbar_new.prograssCreate(this);
                fetchCompletedJobDetails();
                break;
            }

        }
        else {
            AlertDialog alertDialog = new AlertDialog.Builder(SelectJobSummaryActivity.this)
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

    private void getJobDetails() {

        session = new SessionManagement(getApplicationContext());
        HashMap<String, String> user = session.getUserDetails();
        processorid = user.get(SessionManagement.KEY_PROCESSOR_ID);
        userid = user.get(SessionManagement.KEY_USER_ID);

        JSONObject jsonObject = new JSONObject();
        try {

            jsonObject.put("userid", userid);
            jsonObject.put("processorid", processorid);

            JsonParser jsonParser = new JsonParser();
            Call<JsonObject> call = APIClient.getInterface().getjobsummary((JsonObject) jsonParser.parse(jsonObject.toString()));
            GetResult getResult = new GetResult();
            getResult.setMyListener(this);
            getResult.callForLogin(call, "getjobsummary");
        }
        catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void fetchAssignJobDetails() {

        session = new SessionManagement(getApplicationContext());
        HashMap<String, String> user = session.getUserDetails();
        processorid = user.get(SessionManagement.KEY_PROCESSOR_ID);
        userid = user.get(SessionManagement.KEY_USER_ID);

        JSONObject jsonObject = new JSONObject();
        try {

            jsonObject.put("userid", userid);
            jsonObject.put("processorid", processorid);

            JsonParser jsonParser = new JsonParser();
            Call<JsonObject> call = APIClient.getInterface().fetchassignjobdetails((JsonObject) jsonParser.parse(jsonObject.toString()));
            GetResult getResult = new GetResult();
            getResult.setMyListener(this);
            getResult.callForLogin(call, "fetchassignjobdetails");
        }
        catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void fetchPendingJobDetails() {

        session = new SessionManagement(getApplicationContext());
        HashMap<String, String> user = session.getUserDetails();
        processorid = user.get(SessionManagement.KEY_PROCESSOR_ID);
        userid = user.get(SessionManagement.KEY_USER_ID);

        JSONObject jsonObject = new JSONObject();
        try {

            jsonObject.put("userid", userid);
            jsonObject.put("processorid", processorid);

            JsonParser jsonParser = new JsonParser();
            Call<JsonObject> call = APIClient.getInterface().fetchpendingjobdetails((JsonObject) jsonParser.parse(jsonObject.toString()));
            GetResult getResult = new GetResult();
            getResult.setMyListener(this);
            getResult.callForLogin(call, "fetchpendingjobdetails");
        }
        catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void fetchInprocessJobDetails() {

        session = new SessionManagement(getApplicationContext());
        HashMap<String, String> user = session.getUserDetails();
        processorid = user.get(SessionManagement.KEY_PROCESSOR_ID);
        userid = user.get(SessionManagement.KEY_USER_ID);

        JSONObject jsonObject = new JSONObject();
        try {

            jsonObject.put("userid", userid);
            jsonObject.put("processorid", processorid);

            JsonParser jsonParser = new JsonParser();
            Call<JsonObject> call = APIClient.getInterface().fetchinprocessjobdetails((JsonObject) jsonParser.parse(jsonObject.toString()));
            GetResult getResult = new GetResult();
            getResult.setMyListener(this);
            getResult.callForLogin(call, "fetchinprocessjobdetails");
        }
        catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void fetchCompletedJobDetails() {

        session = new SessionManagement(getApplicationContext());
        HashMap<String, String> user = session.getUserDetails();
        processorid = user.get(SessionManagement.KEY_PROCESSOR_ID);
        userid = user.get(SessionManagement.KEY_USER_ID);

        JSONObject jsonObject = new JSONObject();
        try {

            jsonObject.put("userid", userid);
            jsonObject.put("processorid", processorid);

            JsonParser jsonParser = new JsonParser();
            Call<JsonObject> call = APIClient.getInterface().fetchcompletedjobdetails((JsonObject) jsonParser.parse(jsonObject.toString()));
            GetResult getResult = new GetResult();
            getResult.setMyListener(this);
            getResult.callForLogin(call, "fetchcompletedjobdetails");
        }
        catch (JSONException e) {
            e.printStackTrace();
        }
    }


    @Override
    public void onBackPressed() {
        session = new SessionManagement(getApplicationContext());
        HashMap<String, String> user = session.getUserDetails();
        Intent intent = new Intent(SelectJobSummaryActivity.this, MainActivity.class);
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
    }


    public boolean isOnline() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        return cm.getActiveNetworkInfo() != null && cm.getActiveNetworkInfo().isConnectedOrConnecting();
    }

    @Override
    public void callback(JsonObject result, String callNo) {
        try {

            if (callNo.equalsIgnoreCase("getjobsummary")) {
                JSONObject jsonObject = new JSONObject(result.toString());
                String mStatus = jsonObject.optString("status");
                String message = jsonObject.optString("message");
                if (mStatus.equals("success")) {
                    JSONObject jsonObj = jsonObject.getJSONObject("data");

                    JSONObject assignedjobsJsonObj = jsonObj.getJSONObject("assignedjobs");
                    JSONObject pendingjobsJsonObj  = jsonObj.getJSONObject("pendingjobs");
                    JSONObject inprocessjobsJsonObj = jsonObj.getJSONObject("inprocessjobs");
                    JSONObject completedjobsJsonObj = jsonObj.getJSONObject("completedjobs");
                    JSONObject grandtotaljobsJsonObj = jsonObj.getJSONObject("grandtotaljobs");

                    String assignedbundlecnt = assignedjobsJsonObj.optString("assignedbundlecnt");
                    String assignedbundleqty = assignedjobsJsonObj.optString("assignedbundleqty");
                    String assignedprice = assignedjobsJsonObj.optString("assignedprice");


                    String pendingbundlecnt = pendingjobsJsonObj.optString("pendingbundlecnt");
                    String pendingbundleqty = pendingjobsJsonObj.optString("pendingbundleqty");
                    String pendingprice = pendingjobsJsonObj.optString("pendingprice");

                    String inprocessbundlecnt = inprocessjobsJsonObj.optString("inprocessbundlecnt");
                    String inprocessbundleqty = inprocessjobsJsonObj.optString("inprocessbundleqty");
                    String inprocessprice = inprocessjobsJsonObj.optString("inprocessprice");

                    String completedbundlecnt = completedjobsJsonObj.optString("completedbundlecnt");
                    String completedbundleqty = completedjobsJsonObj.optString("completedbundleqty");
                    String completedprice = completedjobsJsonObj.optString("completedprice");


                    String grandtotalbundlecnt = grandtotaljobsJsonObj.optString("grandtotalbundlecnt");
                    String grandtotalbundleqty = grandtotaljobsJsonObj.optString("grandtotalbundleqty");
                    String grandtoalprice = grandtotaljobsJsonObj.optString("grandtoalprice");

                    txtAssignedBundleCount.setText(assignedbundlecnt);
                    txtassignedbundleqty.setText(assignedbundleqty);
                    txtAssignedPrice.setText(assignedprice);

                    txtPendingBundleCount.setText(pendingbundlecnt);
                    txtapendingbundleqty.setText(pendingbundleqty);
                    txtPedndingPrice.setText(pendingprice);

                    txtInprocesscnt.setText(inprocessbundlecnt);
                    txtinprocessbundleqty.setText(inprocessbundleqty);
                    txtInprocessPrice.setText(inprocessprice);

                    txtCompletedBundleCount.setText(completedbundlecnt);
                    txtcompletedbundleqty.setText(completedbundleqty);
                    txtCompletedPrice.setText(completedprice);

                    txtGrandtotalBundleCount.setText(grandtotalbundlecnt);
                    txtGrandtotalbundleqty.setText(grandtotalbundleqty);
                    txtGrandtotalPrice.setText(grandtoalprice);

                }
                else if (mStatus.equals("logout"))
                {
                    new AlertDialog.Builder(SelectJobSummaryActivity.this)
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
                    new AlertDialog.Builder(SelectJobSummaryActivity.this)
                            .setMessage(message)
                            .setCancelable(false)
                            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface arg1, int arg0) {
                                    arg1.dismiss();
                                    onBackPressed();
                                }
                            }).show();
                }
                SelectJobSummaryActivity.custPrograssbar_new.closePrograssBar();
            }
            else  if (callNo.equalsIgnoreCase("fetchassignjobdetails")) {
                JSONObject jsonObject = new JSONObject(result.toString());
                String mStatus = jsonObject.optString("status");
                String message = jsonObject.optString("message");
                if (mStatus.equals("success")) {
                    JSONObject weekwisedataobj = jsonObject.getJSONObject("data");
                    Intent intent = new Intent(SelectJobSummaryActivity.this, AssignedJobActivity.class);
                    intent.putExtra("weekwisedataobj", weekwisedataobj.toString());
                    startActivity(intent);
                    finish();
                }
                else if (mStatus.equals("logout"))
                {
                    new AlertDialog.Builder(SelectJobSummaryActivity.this)
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
                    new AlertDialog.Builder(SelectJobSummaryActivity.this)
                            .setMessage(message)
                            .setCancelable(false)
                            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface arg1, int arg0) {
                                    arg1.dismiss();
                                }
                            }).show();
                }
                SelectJobSummaryActivity.custPrograssbar_new.closePrograssBar();
            }
            else  if (callNo.equalsIgnoreCase("fetchpendingjobdetails")) {
                JSONObject jsonObject = new JSONObject(result.toString());
                String mStatus = jsonObject.optString("status");
                String message = jsonObject.optString("message");

                if (mStatus.equals("success")) {
                    JSONObject weekwisedataobj = jsonObject.getJSONObject("data");
                    Intent intent = new Intent(SelectJobSummaryActivity.this, PendingJobActivity.class);
                    intent.putExtra("weekwisedataobj", weekwisedataobj.toString());
                    startActivity(intent);
                    finish();
                }
                else if (mStatus.equals("logout"))
                {
                    new AlertDialog.Builder(SelectJobSummaryActivity.this)
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
                    new AlertDialog.Builder(SelectJobSummaryActivity.this)
                            .setMessage(message)
                            .setCancelable(false)
                            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface arg1, int arg0) {
                                    arg1.dismiss();
                                }
                            }).show();
                }
                SelectJobSummaryActivity.custPrograssbar_new.closePrograssBar();
            }
            else  if (callNo.equalsIgnoreCase("fetchinprocessjobdetails")) {
                JSONObject jsonObject = new JSONObject(result.toString());
                String mStatus = jsonObject.optString("status");
                String message = jsonObject.optString("message");
                if (mStatus.equals("success")) {
                    JSONObject weekwisedataobj = jsonObject.getJSONObject("data");
                    Intent intent = new Intent(SelectJobSummaryActivity.this, InprocessJobActivity.class);
                    intent.putExtra("weekwisedataobj", weekwisedataobj.toString());
                    startActivity(intent);
                    finish();
                }
                else if (mStatus.equals("logout"))
                {
                    new AlertDialog.Builder(SelectJobSummaryActivity.this)
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
                    new AlertDialog.Builder(SelectJobSummaryActivity.this)
                            .setMessage(message)
                            .setCancelable(false)
                            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface arg1, int arg0) {
                                    arg1.dismiss();
                                }
                            }).show();
                }
                SelectJobSummaryActivity.custPrograssbar_new.closePrograssBar();
            }
            else  if (callNo.equalsIgnoreCase("fetchcompletedjobdetails")) {
                JSONObject jsonObject = new JSONObject(result.toString());
                String mStatus = jsonObject.optString("status");
                String message = jsonObject.optString("message");
                if (mStatus.equals("success")) {
                    JSONObject weekwisedataobj = jsonObject.getJSONObject("data");
                    Intent intent = new Intent(SelectJobSummaryActivity.this, CompletedJobActivity.class);
                    intent.putExtra("weekwisedataobj", weekwisedataobj.toString());
                    startActivity(intent);
                    finish();
                }
                else if (mStatus.equals("logout"))
                {
                    new AlertDialog.Builder(SelectJobSummaryActivity.this)
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
                    new AlertDialog.Builder(SelectJobSummaryActivity.this)
                            .setMessage(message)
                            .setCancelable(false)
                            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface arg1, int arg0) {
                                    arg1.dismiss();
                                }
                            }).show();
                }
                SelectJobSummaryActivity.custPrograssbar_new.closePrograssBar();
            }
        }
        catch (Exception e) {
        }
    }
}
