package com.bipinexports.productionqr.Activity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.os.Bundle;
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

public class SelectJobSummaryActivity extends AppCompatActivity implements View.OnClickListener, GetResult.MyListener {

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


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_jobsummary);
        progress = (ProgressBar) findViewById(R.id.progress);

        custPrograssbar_new = new CustPrograssbar_new();
        SelectJobSummaryActivity.custPrograssbar_new.prograssCreate(this);


        imageView = (ImageView) findViewById(R.id.imgd);
        gridView = (GridView) this.findViewById(R.id.grid);

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

        btn_AssignedJobs =  findViewById(R.id.btn_AssignedJobs);
        btn_PendingJobs =  findViewById(R.id.btn_PendingJobs);
        btn_InprocessJobs =  findViewById(R.id.btn_InprocessJobs);
        btn_CompletedJobs =  findViewById(R.id.btn_CompletedJobs);

        txtAssignedBundleCount = findViewById(R.id.txtAssignedBundleCount);
        txtassignedbundleqty = findViewById(R.id.txtassignedbundleqty);
        txtAssignedPrice = findViewById(R.id.txtAssignedPrice);

        txtPendingBundleCount = findViewById(R.id.txtPendingBundleCount);
        txtapendingbundleqty = findViewById(R.id.txtapendingbundleqty);
        txtPedndingPrice = findViewById(R.id.txtPedndingPrice);

        txtInprocesscnt = findViewById(R.id.txtInprocesscnt);
        txtinprocessbundleqty = findViewById(R.id.txtinprocessbundleqty);
        txtInprocessPrice = findViewById(R.id.txtInprocessPrice);

        txtCompletedBundleCount = findViewById(R.id.txtCompletedBundleCount);
        txtcompletedbundleqty = findViewById(R.id.txtcompletedbundleqty);
        txtCompletedPrice = findViewById(R.id.txtCompletedPrice);


        txtGrandtotalBundleCount = findViewById(R.id.txtGrandtotalBundleCount);
        txtGrandtotalbundleqty = findViewById(R.id.txtGrandtotalbundleqty);
        txtGrandtotalPrice = findViewById(R.id.txtGrandtotalPrice);

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
                    popup.getMenuInflater().inflate(R.menu.menu_main, popup.getMenu());

                    popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                        public boolean onMenuItemClick(MenuItem item) {
                            if (item.getItemId() == R.id.log) {
                                session.logoutUser();
                                finish();
                            }
                            else if (item.getItemId() == R.id.changepassword) {
                                Intent intent = new Intent(SelectJobSummaryActivity.this, ChangepasswordActivity.class);
                                startActivity(intent);
                                finish();
                            }
                            return true;
                        }
                    });
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
