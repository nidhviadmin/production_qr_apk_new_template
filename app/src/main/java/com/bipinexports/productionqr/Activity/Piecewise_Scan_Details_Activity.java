package com.bipinexports.productionqr.Activity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.ProgressBar;
import android.widget.TableLayout;
import android.widget.TableRow;
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
import java.util.Iterator;

import retrofit2.Call;

public class Piecewise_Scan_Details_Activity extends AppCompatActivity implements View.OnClickListener, GetResult.MyListener {

    String Id, User;

    SessionManagement session;
    GridView gridView;
    ProgressBar progress;
    String userid;
    int postListIndex = 0;

    ImageView imageView;
    Button  AddReject;

    TextView text_Job_Refer, text_Shipcode, text_Part_Name, text_Size_Name, text_Bundle_No, text_Bundle_Qty;
    TextView txtUser;

    int fromday = 0;
    int frommonth = 0;
    int fromyear = 0;
    int today = 0;
    int tomonth = 0;
    int toyear = 0;

    String startdate, enddate, type, processorid, username;
    String job_ref, shipcode, sizename, partname, sectionname, orderid, sectionid, sizeid , part_id ,bundleno, bundleqty ;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_piecewise_scan_details);

        imageView = (ImageView) findViewById(R.id.imgd);
        gridView = (GridView) this.findViewById(R.id.grid);
        progress = (ProgressBar) findViewById(R.id.progress);
        progress.setVisibility(View.VISIBLE);

        txtUser = (TextView) findViewById(R.id.txtUser);

        session = new SessionManagement(getApplicationContext());
        HashMap<String, String> user = session.getUserDetails();
        String name = user.get(SessionManagement.KEY_USER);
        this.Id = user.get(SessionManagement.KEY_PROCESSOR_ID);
        processorid = user.get(SessionManagement.KEY_PROCESSOR_ID);
        userid = user.get(SessionManagement.KEY_USER_ID);
        this.User = user.get(SessionManagement.KEY_USER);

        processorid = getIntent().getStringExtra("processorid");
        userid = getIntent().getStringExtra("userid");
        username = getIntent().getStringExtra("name");

        type = getIntent().getStringExtra("type");
        startdate = getIntent().getStringExtra("fromdate");
        enddate = getIntent().getStringExtra("todate");

        fromday = Integer.parseInt(getIntent().getStringExtra("fromday"));
        frommonth = Integer.parseInt(getIntent().getStringExtra("frommonth"));
        fromyear = Integer.parseInt(getIntent().getStringExtra("fromyear"));
        today = Integer.parseInt(getIntent().getStringExtra("today"));
        tomonth = Integer.parseInt(getIntent().getStringExtra("tomonth"));
        toyear = Integer.parseInt(getIntent().getStringExtra("toyear"));

        job_ref = getIntent().getStringExtra("job_ref");
        shipcode = getIntent().getStringExtra("shipcode");
        sizename = getIntent().getStringExtra("sizename");
        partname = getIntent().getStringExtra("partname");
        sectionname = getIntent().getStringExtra("sectionname");
        bundleno = getIntent().getStringExtra("bundleno");
        bundleqty = getIntent().getStringExtra("bundleqty");

        orderid = getIntent().getStringExtra("orderid");
        sectionid = getIntent().getStringExtra("sectionid");
        sizeid = getIntent().getStringExtra("sizeid");
        part_id = getIntent().getStringExtra("part_id");

        imageView.setOnClickListener(this);

        text_Job_Refer = findViewById(R.id.text_Job_Refer);
        text_Shipcode = findViewById(R.id.text_Shipcode);
        text_Part_Name = findViewById(R.id.text_Part_Name);
        text_Size_Name = findViewById(R.id.text_Size_Name);
        text_Bundle_No = findViewById(R.id.text_Bundle_No);
        text_Bundle_Qty = findViewById(R.id.text_Bundle_Qty);

        text_Job_Refer.setText(job_ref);
        text_Shipcode.setText(shipcode);
        text_Part_Name.setText(partname);
        text_Size_Name.setText(sizename);
        text_Bundle_No.setText(bundleno);
        text_Bundle_Qty.setText(bundleqty);

        getvalue();
        get_piecewise_scan_details();

        AddReject = findViewById(R.id.AddReject);
        AddReject.setOnClickListener(this);
        AddReject.setVisibility(View.INVISIBLE);
    }

    public void onClick(View v) {
        if (isOnline()) {
            switch (v.getId()) {
                case R.id.imgd:
                    PopupMenu popup = new PopupMenu(Piecewise_Scan_Details_Activity.this, imageView);
                    HashMap<String, String> user = session.getUserDetails();
                    String username = user.get(SessionManagement.KEY_USER);
                    String userid = user.get(SessionManagement.KEY_USER_ID);

                    Intent intent = new Intent(Piecewise_Scan_Details_Activity.this, HomeActivity.class);
                    intent.putExtra("openDrawer", true);
                    intent.putExtra("username", username);
                    intent.putExtra("userid", userid);
                    intent.putExtra("processorid", processorid);
                    startActivity(intent);
                    popup.show();
                    break;
                case R.id.AddReject:
                    progress.setVisibility(View.VISIBLE);
                    onBackPressed();
                    break;
            }
        }
        else {
            AlertDialog alertDialog = new AlertDialog.Builder(Piecewise_Scan_Details_Activity.this)
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


    private void get_piecewise_scan_details() {
        session = new SessionManagement(getApplicationContext());
        HashMap<String, String> user = session.getUserDetails();
        processorid = user.get(SessionManagement.KEY_PROCESSOR_ID);
        userid = user.get(SessionManagement.KEY_USER_ID);
        progress.setVisibility(View.VISIBLE);

        JSONObject jsonObject = new JSONObject();
        try {

            jsonObject.put("userid", userid);

            jsonObject.put("contractorid", processorid);
            jsonObject.put("from_date", startdate);
            jsonObject.put("orderid",orderid);
            jsonObject.put("shipcode", shipcode);
            jsonObject.put("sizeid", sizeid);
            jsonObject.put("partid", part_id);
            jsonObject.put("partid", part_id);
            jsonObject.put("sectionid", sectionid);
            jsonObject.put("bundleno", bundleno);

            JsonParser jsonParser = new JsonParser();
            Call<JsonObject> call = APIClient.getInterface().fetch_piece_scan_details((JsonObject) jsonParser.parse(jsonObject.toString()));
            GetResult getResult = new GetResult();
            getResult.setMyListener(this);
            getResult.callForLogin(call, "fetch_piece_scan_details");
        }
        catch (JSONException e) {
            e.printStackTrace();
        }
    }


    @Override
    public void onBackPressed() {
        session = new SessionManagement(getApplicationContext());
        HashMap<String, String> user = session.getUserDetails();
        Intent intent = new Intent(Piecewise_Scan_Details_Activity.this, Datewise_Bundle_Scanned_Detail_Activity.class);
        intent.putExtra("name", user.get(SessionManagement.KEY_USER));
        intent.putExtra("userid", user.get(SessionManagement.KEY_USER_ID));
        intent.putExtra("processorid", user.get(SessionManagement.KEY_PROCESSOR_ID));

        intent.putExtra("fromdate", startdate);
        intent.putExtra("todate", enddate);
        intent.putExtra("fromday", String.valueOf(fromday));
        intent.putExtra("frommonth", String.valueOf(frommonth));
        intent.putExtra("fromyear", String.valueOf(fromyear));
        intent.putExtra("today", String.valueOf(today));
        intent.putExtra("tomonth", String.valueOf(tomonth));
        intent.putExtra("toyear", String.valueOf(toyear));

        intent.putExtra("job_ref", job_ref);
        intent.putExtra("shipcode", shipcode);
        intent.putExtra("sizename", sizename);
        intent.putExtra("partname", partname);
        intent.putExtra("sectionname", sectionname);

        intent.putExtra("orderid", String.valueOf(orderid));
        intent.putExtra("sectionid", String.valueOf(sectionid));
        intent.putExtra("sizeid", String.valueOf(sizeid));
        intent.putExtra("part_id", String.valueOf(part_id));

        intent.putExtra("type", "New");

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

            if (callNo.equalsIgnoreCase("fetch_piece_scan_details")) {
                JSONObject jsonObject = new JSONObject(result.toString());
                String mStatus = jsonObject.optString("status");
                String message = jsonObject.optString("message");
                if (mStatus.equals("success")) {
                    JSONObject jsonObj = jsonObject.getJSONObject("data");
                    JSONObject datewise_piece_scan_JsonObj = jsonObj.getJSONObject("datewise_piece_scan_details");
                    try {

                        TableLayout programtbl = findViewById(R.id.addprogramdata);
                        Iterator<String> prog = datewise_piece_scan_JsonObj.keys();
                        while (prog.hasNext()) {
                            String key = prog.next();
                            if (datewise_piece_scan_JsonObj.get(key) instanceof JSONObject) {

                                String index = ((JSONObject) datewise_piece_scan_JsonObj.get(key)).getString("index");
                                String pcno = ((JSONObject) datewise_piece_scan_JsonObj.get(key)).getString("pcno");
                                String start_ts = ((JSONObject) datewise_piece_scan_JsonObj.get(key)).getString("start_ts");
                                String end_ts =((JSONObject) datewise_piece_scan_JsonObj.get(key)).getString("end_ts");

                                /* Create a new row to be added. */
                                TableRow tr = new TableRow(Piecewise_Scan_Details_Activity.this);
                                tr.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT));
                                tr.setPadding(0, 0, 2, 0);

                                TableRow.LayoutParams params = new TableRow.LayoutParams();
                                params.span = 2;
                                params.weight = 1;

                                TextView txt_index = new TextView(Piecewise_Scan_Details_Activity.this);
                                txt_index.setText(index);
                                txt_index.setTextColor(Color.BLACK);
                                txt_index.setTextSize(2, 16);
                                txt_index.setGravity(Gravity.CENTER);
                                tr.addView(txt_index);

                                TextView txt_cpcno = new TextView(Piecewise_Scan_Details_Activity.this);
                                txt_cpcno.setText(pcno);
                                txt_cpcno.setTextColor(Color.BLACK);
                                txt_cpcno.setTextSize(2, 14);
                                txt_cpcno.setGravity(Gravity.RIGHT);
                                tr.addView(txt_cpcno);

                                Log.e("Bipin","start_ts :" +start_ts);
                                if(start_ts.equals("") || start_ts.equals(null) || start_ts.equals("null"))
                                {
                                    TextView txt_start_ts = new TextView(Piecewise_Scan_Details_Activity.this);
                                    txt_start_ts.setText("Not Scanned");
                                    txt_start_ts.setTextColor(Color.RED);
                                    txt_start_ts.setTextSize(2, 16);
                                    txt_start_ts.setGravity(Gravity.CENTER);
                                    tr.addView(txt_start_ts);
                                }
                                else
                                {
                                    TextView txt_start_ts = new TextView(Piecewise_Scan_Details_Activity.this);
                                    txt_start_ts.setText(start_ts);
                                    txt_start_ts.setTextColor(Color.BLACK);
                                    txt_start_ts.setTextSize(2, 16);
                                    txt_start_ts.setGravity(Gravity.CENTER);
                                    tr.addView(txt_start_ts);
                                }


                                programtbl.addView(tr, new TableLayout.LayoutParams(TableLayout.LayoutParams.MATCH_PARENT, TableLayout.LayoutParams.WRAP_CONTENT));
                                tr.setPadding(15, 30, 2, 0);
                                postListIndex++;
                            }
                        }
                        AddReject.setVisibility(View.VISIBLE);
                    }
                    catch (JSONException e) {
                        e.printStackTrace();
                        Log.e("NidhviTec", "JSONException: " + e);
                    }
                }
                else if (mStatus.equals("logout"))
                {
                    new AlertDialog.Builder(Piecewise_Scan_Details_Activity.this)
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
                    new AlertDialog.Builder(Piecewise_Scan_Details_Activity.this)
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
