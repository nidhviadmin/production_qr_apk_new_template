package com.bipinexports.productionqr.Activity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bipinexports.productionqr.APIClient;
import com.bipinexports.productionqr.Datewise_Bundle_Piece_Data_Object;
import com.bipinexports.productionqr.Datewise_Piece_Scan_List_Data_Object;
import com.bipinexports.productionqr.GetResult;
import com.bipinexports.productionqr.ModelClass;
import com.bipinexports.productionqr.R;
import com.bipinexports.productionqr.SessionManagement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;

import retrofit2.Call;


public class Datewise_Bundle_Scanned_Detail_Activity extends BaseActivity implements View.OnClickListener, GetResult.MyListener {

    private View content;
    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private static String LOG_TAG = "CardViewActivity";

    SessionManagement session;
    TextView txtUser;
    ImageView imageView;

    String userid;
    String username;
    String datewise_piece_scan_details_data_obj;
    JSONObject datewise_piece_scan_details_jsonobj;
    String  User;
    ProgressBar progress;

    private boolean loading = true;
    int totalItemCount;
    int startindex = 0;
    int endindex = 10;
    int endpos = 50;
    private boolean finalloading = true;

    public static CustPrograssbar custPrograssbar;
    private boolean grandtotreach = false;
    ArrayList results = new ArrayList<Datewise_Bundle_Piece_Data_Object>();
    int i = 0;

    String Imei;

    TextView txt_empty;

    int fromday = 0;
    int frommonth = 0;
    int fromyear = 0;

    int today = 0;
    int tomonth = 0;
    int toyear = 0;

    String startdate, enddate, type, processorid;
    String job_ref, shipcode, sizename, partname, sectionname, orderid, sectionid, sizeid , part_id ;

    TextView txt_jobref, text_shipcode;
    int offset =0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_base);
        setupDrawer();

        content = getLayoutInflater().inflate(
                R.layout.activity_datewise_bundle_scan_list,
                findViewById(R.id.content_frame),
                true);

        txtUser = content.findViewById(R.id.txtUser);
        imageView = content.findViewById(R.id.imgd);
        progress = content.findViewById(R.id.progress);
        txt_empty = content.findViewById(R.id.txt_empty);
        txt_jobref = content.findViewById(R.id.txt_jobref);
        text_shipcode = content.findViewById(R.id.text_shipcode);

        custPrograssbar = new CustPrograssbar();
        session = new SessionManagement(getApplicationContext());
        HashMap<String, String> user = session.getUserDetails();
        this.User = user.get(SessionManagement.KEY_USER);

        processorid = getIntent().getStringExtra("processorid");
        userid = getIntent().getStringExtra("userid");
        username = getIntent().getStringExtra("name");

        imageView.setOnClickListener(this);

        setupNotifications();
        handleNotificationIntent(getIntent());
        // Set date formatting
        Date c = Calendar.getInstance().getTime();
        SimpleDateFormat df = new SimpleDateFormat("dd-MMM-yyyy", Locale.getDefault());
        String formattedDate = df.format(c);

        loading = true;
        totalItemCount = 0;
        startindex = 0;
        endindex = 10;
        finalloading = true;
        progress.setVisibility(View.VISIBLE);

        startdate = getIntent().getStringExtra("fromdate");
        enddate = getIntent().getStringExtra("todate");

        job_ref = getIntent().getStringExtra("job_ref");
        shipcode = getIntent().getStringExtra("shipcode");
        sizename = getIntent().getStringExtra("sizename");
        partname = getIntent().getStringExtra("partname");
        sectionname = getIntent().getStringExtra("sectionname");

        orderid = getIntent().getStringExtra("orderid");
        sectionid = getIntent().getStringExtra("sectionid");
        sizeid = getIntent().getStringExtra("sizeid");
        part_id = getIntent().getStringExtra("part_id");

        txt_jobref.setText(job_ref);
        text_shipcode.setText(sectionname + " | " + shipcode + " | " + partname + " | " + sizename);

        getvalue();
        fetch_datewise_bundle_piece_scan_details();
    }

    public void onClick(View v) {
        if (v.getId() == R.id.imgd) {
            toggleDrawer();
        }
    }
    private ArrayList<Datewise_Bundle_Piece_Data_Object> getDataSet() {
        try {
            JSONObject jsonObj = new JSONObject(datewise_piece_scan_details_data_obj);
            datewise_piece_scan_details_jsonobj = jsonObj.getJSONObject("datewise_piece_scan_details");
            Iterator<String> datewise_piece_scan_contents = datewise_piece_scan_details_jsonobj.keys();

            while (datewise_piece_scan_contents.hasNext()) {
                String key = datewise_piece_scan_contents.next();
                if (datewise_piece_scan_details_jsonobj.get(key) instanceof JSONObject) {
                    int orderid = Integer.parseInt(((JSONObject) datewise_piece_scan_details_jsonobj.get(key)).getString("orderid"));
                    int index = Integer.parseInt(((JSONObject) datewise_piece_scan_details_jsonobj.get(key)).getString("index"));
                    int sizeid = Integer.parseInt(((JSONObject) datewise_piece_scan_details_jsonobj.get(key)).getString("sizeid"));
                    int sectionid = Integer.parseInt(((JSONObject) datewise_piece_scan_details_jsonobj.get(key)).getString("sectionid"));
                    int partid = Integer.parseInt(((JSONObject) datewise_piece_scan_details_jsonobj.get(key)).getString("partid"));

                    String job_ref = ((JSONObject) datewise_piece_scan_details_jsonobj.get(key)).getString("job_ref");
                    String shipcode = ((JSONObject) datewise_piece_scan_details_jsonobj.get(key)).getString("shipcode");
                    String sectionname = ((JSONObject) datewise_piece_scan_details_jsonobj.get(key)).getString("sectionname");
                    String partname = ((JSONObject) datewise_piece_scan_details_jsonobj.get(key)).getString("partname");
                    String sizename = ((JSONObject) datewise_piece_scan_details_jsonobj.get(key)).getString("sizename");
                    String sizewiseqty = ((JSONObject) datewise_piece_scan_details_jsonobj.get(key)).getString("sizewiseqty");
                    String bundleno =((JSONObject) datewise_piece_scan_details_jsonobj.get(key)).getString("bundleno");
                    String bundle_qty = ((JSONObject) datewise_piece_scan_details_jsonobj.get(key)).getString("bundle_qty");
                    String scanned_pcno = ((JSONObject) datewise_piece_scan_details_jsonobj.get(key)).getString("scanned_pcno");
                    String diff_qty = ((JSONObject) datewise_piece_scan_details_jsonobj.get(key)).getString("diff_qty");

                    Datewise_Bundle_Piece_Data_Object obj = new Datewise_Bundle_Piece_Data_Object(orderid, index, job_ref, sectionname,shipcode, partname, bundle_qty, sizename, sizewiseqty, bundleno,
                            scanned_pcno, sizeid, sectionid, partid, diff_qty);
                    results.add(i, obj);
                    i++;
                }
            }
            offset = Integer.parseInt(jsonObj.optString("offset"));
            if(totalItemCount == offset)
            {
                Datewise_Bundle_Piece_Data_Object obj = new Datewise_Bundle_Piece_Data_Object(0, 0, "", "","", "Grand Total",  jsonObj.optString("total_bqty"), "", "" ,"",
                        jsonObj.optString("total_pcs"), 0, 0, 0, jsonObj.optString("total_diff"));
                results.add(i, obj);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return results;
    }

    public void setImei() {
        if (Imei == null || Imei.isEmpty()) {
            TelephonyManager telephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
            try {
//                Imei = telephonyManager.getDeviceId();
            } catch (SecurityException e) {
                e.printStackTrace();
            }
        }
    }

    private void fetch_datewise_bundle_piece_scan_details() {
        Datewise_Bundle_Scanned_Detail_Activity.custPrograssbar.prograssCreate(this);
        session = new SessionManagement(getApplicationContext());
        HashMap<String, String> user = session.getUserDetails();
        processorid = user.get(SessionManagement.KEY_PROCESSOR_ID);
        userid = user.get(SessionManagement.KEY_USER_ID);
        username = user.get(SessionManagement.KEY_USER);
        JSONObject jsonObject = new JSONObject();
        try {

            jsonObject.put("userid", userid);
            jsonObject.put("contractorid", processorid);

            jsonObject.put("from_date", startdate);
            jsonObject.put("to_date", startdate);
            jsonObject.put("limit", endindex);
            jsonObject.put("offset", startindex);

            jsonObject.put("orderid",orderid);
            jsonObject.put("shipcode", shipcode);
            jsonObject.put("sizeid", sizeid);
            jsonObject.put("partid", part_id);
            jsonObject.put("sectionid", sectionid);

            JsonParser jsonParser = new JsonParser();
            Call<JsonObject> call = APIClient.getInterface().fetch_datewise_bundle_piece_scan_details((JsonObject) jsonParser.parse(jsonObject.toString()));
            GetResult getResult = new GetResult();
            getResult.setMyListener(this);
            getResult.callForLogin(call, "datewise_bundle_pieces");
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void getvalue() {
        txtUser.setText("Hello " + this.User);
        ModelClass modelClass = new ModelClass();
        modelClass.setmID(userid);
        progress.setVisibility(View.INVISIBLE);
    }

    @Override
    public void onBackPressed() {
        session = new SessionManagement(getApplicationContext());
        HashMap<String, String> user = session.getUserDetails();
        Intent intent = new Intent(Datewise_Bundle_Scanned_Detail_Activity.this, Datewise_Piece_Scanned_Detail_Activity.class);
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
        intent.putExtra("searchlist", "true");

        intent.putExtra("job_ref", job_ref);
        intent.putExtra("shipcode", shipcode);
        intent.putExtra("sizename", sizename);
        intent.putExtra("partname", partname);
        intent.putExtra("sectionname", sectionname);

        intent.putExtra("orderid", String.valueOf(orderid));
        intent.putExtra("sectionid", String.valueOf(sectionid));
        intent.putExtra("sizeid", String.valueOf(sizeid));
        intent.putExtra("part_id", String.valueOf(part_id));
        intent.putExtra("type", "Data");

        startActivity(intent);
        finish();
    }

    private void fetchscrolldata() {
        Datewise_Bundle_Scanned_Detail_Activity.custPrograssbar.prograssCreate(this);
        session = new SessionManagement(getApplicationContext());
        HashMap<String, String> user = session.getUserDetails();
        userid = user.get(SessionManagement.KEY_USER_ID);

        JSONObject jsonObject = new JSONObject();
        try {

            jsonObject.put("userid", userid);
            jsonObject.put("contractorid", processorid);

            jsonObject.put("from_date", startdate);
            jsonObject.put("to_date", endindex);
            jsonObject.put("limit", endindex);
            jsonObject.put("offset", startindex);

            jsonObject.put("orderid",orderid);
            jsonObject.put("shipcode", shipcode);
            jsonObject.put("sizeid", sizeid);
            jsonObject.put("partid", part_id);
            jsonObject.put("sectionid", sectionid);

            JsonParser jsonParser = new JsonParser();
            Call<JsonObject> call = APIClient.getInterface().fetch_datewise_bundle_piece_scan_details((JsonObject) jsonParser.parse(jsonObject.toString()));
            GetResult getResult = new GetResult();
            getResult.setMyListener(this);
            getResult.callForLogin(call, "pending_deliveries");
        }
        catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void callback(JsonObject result, String callNo) {
        try {

            Datewise_Bundle_Scanned_Detail_Activity.custPrograssbar.closePrograssBar();
            progress.setVisibility(View.INVISIBLE);
            if (callNo.equalsIgnoreCase("datewise_bundle_pieces")) {
                JSONObject jsonObject = new JSONObject(result.toString());
                String mStatus = jsonObject.optString("status");
                String message = jsonObject.optString("message");
                if (mStatus.equals("success")) {

                    String totalcount = jsonObject.optString("totalcount");
                    totalItemCount = Integer.parseInt(totalcount);
                    txt_empty.setVisibility(View.INVISIBLE);

                    datewise_piece_scan_details_data_obj = jsonObject.getJSONObject("data").toString();

                    mRecyclerView = (RecyclerView) findViewById(R.id.my_recycler_view);
                    mRecyclerView.setHasFixedSize(true);
                    mLayoutManager = new LinearLayoutManager(Datewise_Bundle_Scanned_Detail_Activity.this);
                    mRecyclerView.setLayoutManager(mLayoutManager);
                    mAdapter = new Datewise_Bundle_Piece_Scan_Adapter(getDataSet());
                    mRecyclerView.setAdapter(mAdapter);

                    ((Datewise_Bundle_Piece_Scan_Adapter) mAdapter).setOnItemClickListener(new Datewise_Bundle_Piece_Scan_Adapter.MyClickListener() {
                        @Override
                        public void onItemClick(int position, View v) {
                            int orderid =((Datewise_Bundle_Piece_Scan_Adapter) mAdapter).getOrderid(position);
                            int sectionid =((Datewise_Bundle_Piece_Scan_Adapter) mAdapter).getSectionid(position);
                            int sizeid =((Datewise_Bundle_Piece_Scan_Adapter) mAdapter).getSizeid(position);
                            int part_id =((Datewise_Bundle_Piece_Scan_Adapter) mAdapter).getPart_id(position);
                            String shipcode =((Datewise_Bundle_Piece_Scan_Adapter) mAdapter).getShipcode(new String(String.valueOf(position)));
                            String sizename =((Datewise_Bundle_Piece_Scan_Adapter) mAdapter).getSizename(new String(String.valueOf(position)));
                            String job_ref =((Datewise_Bundle_Piece_Scan_Adapter) mAdapter).getJob_ref(new String(String.valueOf(position)));
                            String partname =((Datewise_Bundle_Piece_Scan_Adapter) mAdapter).getPartname(new String(String.valueOf(position)));
                            String sectionname =((Datewise_Bundle_Piece_Scan_Adapter) mAdapter).getSectionname(new String(String.valueOf(position)));
                            String bundleno =((Datewise_Bundle_Piece_Scan_Adapter) mAdapter).getBundleno(new String(String.valueOf(position)));
                            String bundle_qty =((Datewise_Bundle_Piece_Scan_Adapter) mAdapter).getBundle_qty(new String(String.valueOf(position)));

                            if(partname.equals("Grand Total"))
                            {
                            }
                            else {
                                session = new SessionManagement(getApplicationContext());
                                HashMap<String, String> user = session.getUserDetails();

                                Intent intent = new Intent(Datewise_Bundle_Scanned_Detail_Activity.this, Piecewise_Scan_Details_Activity.class);
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
                                intent.putExtra("searchlist", "true");

                                intent.putExtra("job_ref", job_ref);
                                intent.putExtra("shipcode", shipcode);
                                intent.putExtra("sizename", sizename);
                                intent.putExtra("partname", partname);
                                intent.putExtra("sectionname", sectionname);
                                intent.putExtra("bundleno", bundleno);
                                intent.putExtra("bundleqty", bundle_qty);

                                intent.putExtra("orderid", String.valueOf(orderid));
                                intent.putExtra("sectionid", String.valueOf(sectionid));
                                intent.putExtra("sizeid", String.valueOf(sizeid));
                                intent.putExtra("part_id", String.valueOf(part_id));

                                startActivity(intent);
                                finish();
                            }
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
                    new AlertDialog.Builder(Datewise_Bundle_Scanned_Detail_Activity.this)
                            .setMessage(message)
                            .setCancelable(false)
                            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface arg1, int arg0) {
                                    arg1.dismiss();
                                }
                            }).show();
                }
                else {
                    new AlertDialog.Builder(Datewise_Bundle_Scanned_Detail_Activity.this)
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

                    datewise_piece_scan_details_data_obj = jsonObject.getJSONObject("data").toString();
                    JSONObject jsonObj = new JSONObject(datewise_piece_scan_details_data_obj);
                    datewise_piece_scan_details_jsonobj = jsonObj.getJSONObject("datewise_piece_scan_details");
                    Iterator<String> datewise_piece_scan_contents = datewise_piece_scan_details_jsonobj.keys();

                    while (datewise_piece_scan_contents.hasNext()) {
                        String key = datewise_piece_scan_contents.next();
                        if (datewise_piece_scan_details_jsonobj.get(key) instanceof JSONObject) {
                            int orderid = Integer.parseInt(((JSONObject) datewise_piece_scan_details_jsonobj.get(key)).getString("orderid"));
                            int index = Integer.parseInt(((JSONObject) datewise_piece_scan_details_jsonobj.get(key)).getString("index"));
                            int sizeid = Integer.parseInt(((JSONObject) datewise_piece_scan_details_jsonobj.get(key)).getString("sizeid"));
                            int sectionid = Integer.parseInt(((JSONObject) datewise_piece_scan_details_jsonobj.get(key)).getString("sectionid"));
                            int partid = Integer.parseInt(((JSONObject) datewise_piece_scan_details_jsonobj.get(key)).getString("partid"));

                            String job_ref = ((JSONObject) datewise_piece_scan_details_jsonobj.get(key)).getString("job_ref");
                            String shipcode = ((JSONObject) datewise_piece_scan_details_jsonobj.get(key)).getString("shipcode");
                            String sectionname = ((JSONObject) datewise_piece_scan_details_jsonobj.get(key)).getString("sectionname");
                            String partname = ((JSONObject) datewise_piece_scan_details_jsonobj.get(key)).getString("partname");
                            String sizename = ((JSONObject) datewise_piece_scan_details_jsonobj.get(key)).getString("sizename");
                            String sizewiseqty = ((JSONObject) datewise_piece_scan_details_jsonobj.get(key)).getString("sizewiseqty");
                            String bundleno =((JSONObject) datewise_piece_scan_details_jsonobj.get(key)).getString("bundleno");
                            String bundle_qty = ((JSONObject) datewise_piece_scan_details_jsonobj.get(key)).getString("bundle_qty");
                            String scanned_pcno = ((JSONObject) datewise_piece_scan_details_jsonobj.get(key)).getString("scanned_pcno");
                            String diff_qty = ((JSONObject) datewise_piece_scan_details_jsonobj.get(key)).getString("diff_qty");

                            Datewise_Bundle_Piece_Data_Object obj = new Datewise_Bundle_Piece_Data_Object(orderid, index, job_ref, sectionname,shipcode, partname, bundle_qty, sizename, sizewiseqty, bundleno,
                                    scanned_pcno, sizeid, sectionid, partid, diff_qty);
                            results.add(i, obj);
                            i++;
                        }
                    }
                    totalItemCount = Integer.parseInt(jsonObj.optString("totalcount"));
                    offset = Integer.parseInt(jsonObj.optString("offset"));
                    Log.e("Bipin","offset :" +offset);
                    if(totalItemCount == endindex && grandtotreach==false)
                    {
                        grandtotreach = true;
                    }
                    if(totalItemCount == offset)
                    {
                        Datewise_Bundle_Piece_Data_Object obj = new Datewise_Bundle_Piece_Data_Object(0, 0, "", "","", "Grand Total",  jsonObj.optString("total_bqty"), "", "" ,"",
                                jsonObj.optString("total_pcs"), 0, 0, 0, jsonObj.optString("total_diff"));
                        results.add(i, obj);
                    }
                    mAdapter.notifyDataSetChanged();
                }
                else if (mStatus.equals("nodatafound")) {
                    if(mAdapter == null)
                    {
                        // Do nothing
                    }
                    new AlertDialog.Builder(Datewise_Bundle_Scanned_Detail_Activity.this)
                            .setMessage(message)
                            .setCancelable(false)
                            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface arg1, int arg0) {
                                    arg1.dismiss();
                                }
                            }).show();
                }
                else {
                    new AlertDialog.Builder(Datewise_Bundle_Scanned_Detail_Activity.this)
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
