package com.bipinexports.productionqr.Activity;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
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
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bipinexports.productionqr.APIClient;
import com.bipinexports.productionqr.AssignedDataObject;
import com.bipinexports.productionqr.Overall_Datewise_Piece_Scan_Data_Object;
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


public class Overall_Datewise_Piece_Scanned_Activity extends BaseActivity implements View.OnClickListener, GetResult.MyListener {

    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private static String LOG_TAG = "CardViewActivity";

    SessionManagement session;
    TextView txtUser;
    ImageView imageView;

    String userid;
    String username;
    String overall_datewise_piece_scan_details_data_obj;
    JSONObject overall_datewise_piece_scan_details_jsonobj;
    String  User;
    ProgressBar progress;

    private boolean loading = true;
    int totalItemCount;
    int startindex = 0;
    int endindex = 10;
    int endpos = 50;
    private boolean finalloading = true;
    int offset = 0;

    public static CustPrograssbar custPrograssbar;
    private boolean grandtotreach = false;
    ArrayList results = new ArrayList<Overall_Datewise_Piece_Scan_Data_Object>();
    int i = 0;

    String Imei;

    TextView txt_empty, txt_Section;
    EditText txtFromDate, txttoDate;
    Button FetchData;

    final Calendar fromcldr = Calendar.getInstance();
    DatePickerDialog frompicker;
    int fromday = 0;
    int frommonth = 0;
    int fromyear = 0;
    String selectedfromdate;
    Boolean searchlist =false;

    final Calendar tocldr = Calendar.getInstance();
    DatePickerDialog topicker;
    int today = 0;
    int tomonth = 0;
    int toyear = 0;
    String selectedtodate;
    String startdate, enddate, type, processorid;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_overall_datewise_piece_scan_list);
        setContentView(R.layout.activity_base);
        setupDrawer();

        View content = getLayoutInflater().inflate(
                R.layout.activity_overall_datewise_piece_scan_list,
                findViewById(R.id.content_frame),
                true
        );

        txtUser = content.findViewById(R.id.txtUser);
        imageView = content.findViewById(R.id.imgd);
        progress = (ProgressBar) content.findViewById(R.id.progress);
//        progress.setVisibility(View.VISIBLE);

        custPrograssbar = new CustPrograssbar();

        txtUser = (TextView) content.findViewById(R.id.txtUser);

        session = new SessionManagement(getApplicationContext());
        HashMap<String, String> user = session.getUserDetails();
        this.User = user.get(SessionManagement.KEY_USER);

        setImei();

        processorid = getIntent().getStringExtra("processorid");
        userid = getIntent().getStringExtra("userid");
        username = getIntent().getStringExtra("name");

        txt_empty = content.findViewById(R.id.txt_empty);

        imageView.setOnClickListener(this);

        Date c = Calendar.getInstance().getTime();
        System.out.println("Current time => " + c);
        SimpleDateFormat df = new SimpleDateFormat("dd-MMM-yyyy", Locale.getDefault());
        String formattedDate = df.format(c);

        txtFromDate = content.findViewById(R.id.txtFromDate);
        txttoDate = content.findViewById(R.id.txttoDate);
        FetchData  = content.findViewById(R.id.FetchData);
        txt_Section = content.findViewById(R.id.txt_Section);

        type = getIntent().getStringExtra("type");
        if (type.equals("Data")) {

            startdate = getIntent().getStringExtra("fromdate");
            enddate = getIntent().getStringExtra("todate");

            fromday = Integer.parseInt(getIntent().getStringExtra("fromday"));
            frommonth = Integer.parseInt(getIntent().getStringExtra("frommonth"));
            fromyear = Integer.parseInt(getIntent().getStringExtra("fromyear"));
            today = Integer.parseInt(getIntent().getStringExtra("today"));
            tomonth = Integer.parseInt(getIntent().getStringExtra("tomonth"));
            toyear = Integer.parseInt(getIntent().getStringExtra("toyear"));

            txtFromDate.setText(startdate);
            txttoDate.setText(enddate);

            selectedfromdate =  startdate;
            selectedtodate =  enddate;

            fetch_overall_scanned_details();
        }
        else
        {
            selectedfromdate = formattedDate;
            selectedtodate = formattedDate;
            txtFromDate.setText(selectedfromdate);
            fetch_overall_scanned_details();
        }

        txtFromDate.setOnClickListener(this);
        txttoDate.setOnClickListener(this);

        getvalue();
        FetchData.setOnClickListener(this);

    }

    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.imgd:
                PopupMenu popup = new PopupMenu(Overall_Datewise_Piece_Scanned_Activity.this, imageView);
                toggleDrawer();
                popup.show();
                break;
            case R.id.txtFromDate:
                if(fromday == 0 || frommonth == 0 || fromyear == 0)
                {
                    fromday = fromcldr.get(Calendar.DAY_OF_MONTH);
                    frommonth = fromcldr.get(Calendar.MONTH);
                    fromyear = fromcldr.get(Calendar.YEAR);
                }

                frompicker = new DatePickerDialog(Overall_Datewise_Piece_Scanned_Activity.this, R.style.datepicker,
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                                SimpleDateFormat sdf = new SimpleDateFormat("dd-MMM-yyyy");
                                fromcldr.set(year, monthOfYear, dayOfMonth);

                                fromday = dayOfMonth;
                                frommonth = monthOfYear;
                                fromyear = year;

                                String dateString = sdf.format(fromcldr.getTime());
                                txtFromDate.setText(dateString);
                                txttoDate.setText("");
                                selectedfromdate = dateString;
                            }
                        }, fromyear, frommonth, fromday);
                frompicker.show();
                break;
//            case R.id.txttoDate:
//                if(today == 0 || tomonth == 0 || toyear == 0)
//                {
//                    today = tocldr.get(Calendar.DAY_OF_MONTH);
//                    tomonth = tocldr.get(Calendar.MONTH);
//                    toyear = tocldr.get(Calendar.YEAR);
//                }
//
//                topicker = new DatePickerDialog(Datewise_Piece_Scanned_Detail_Activity.this, R.style.datepicker,
//                        new DatePickerDialog.OnDateSetListener() {
//                            @Override
//                            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
//                                SimpleDateFormat sdf = new SimpleDateFormat("dd-MMM-yyyy");
//                                tocldr.set(year, monthOfYear, dayOfMonth);
//
//                                today = dayOfMonth;
//                                tomonth = monthOfYear;
//                                toyear = year;
//
//                                String dateString = sdf.format(tocldr.getTime());
//                                txttoDate.setText(dateString);
//                                selectedtodate = dateString;
//                            }
//                        }, toyear, tomonth, today);
//                topicker.show();
//                break;

            case R.id.FetchData:
                if (txtFromDate.length() == 0) {
                    AlertDialog alertDialog = new AlertDialog.Builder(Overall_Datewise_Piece_Scanned_Activity.this)
                            .setMessage("Select From Date!")
                            .setCancelable(false)
                            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface d, int arg0) {
                                    d.dismiss();
                                }
                            }).show();

                }
//                else if (txttoDate.length() == 0) {
//                    new AlertDialog.Builder(Datewise_Piece_Scanned_Detail_Activity.this)
//                            .setMessage("Select To Date!")
//                            .setCancelable(false)
//                            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
//                                public void onClick(DialogInterface d, int arg0) {
//                                    d.dismiss();
//                                }
//                            }).show();
//
//                }
                else {

                    loading = true;
                    totalItemCount=0;
                    startindex = 0;
                    endindex = 10;
                    endpos = 50;
                    offset =0;
                    finalloading = true;
                    grandtotreach = false;
                    progress.setVisibility(View.VISIBLE);
                    fetch_overall_scanned_details();
                }
        }
    }
    private ArrayList<Overall_Datewise_Piece_Scan_Data_Object> getDataSet() {
//        ArrayList results = new ArrayList<Accessory_Receipt_Data_Object>();
        try {
            JSONObject jsonObj = new JSONObject(overall_datewise_piece_scan_details_data_obj);
            overall_datewise_piece_scan_details_jsonobj = jsonObj.getJSONObject("datewise_piece_scan_details");
            Iterator<String> datewise_piece_scan_contents = overall_datewise_piece_scan_details_jsonobj.keys();

            while (datewise_piece_scan_contents.hasNext()) {
                String key = datewise_piece_scan_contents.next();
                if (overall_datewise_piece_scan_details_jsonobj.get(key) instanceof JSONObject) {
                    int orderid = Integer.parseInt(((JSONObject) overall_datewise_piece_scan_details_jsonobj.get(key)).getString("orderid"));
                    int index = Integer.parseInt(((JSONObject) overall_datewise_piece_scan_details_jsonobj.get(key)).getString("index"));
                    int sectionid = Integer.parseInt(((JSONObject) overall_datewise_piece_scan_details_jsonobj.get(key)).getString("sectionid"));
                    String job_ref = ((JSONObject) overall_datewise_piece_scan_details_jsonobj.get(key)).getString("job_ref");
                    String shipcode = ((JSONObject) overall_datewise_piece_scan_details_jsonobj.get(key)).getString("shipcode");
                    String sectionname = ((JSONObject) overall_datewise_piece_scan_details_jsonobj.get(key)).getString("sectionname");

                    String bundle_qty = ((JSONObject) overall_datewise_piece_scan_details_jsonobj.get(key)).getString("bundle_qty");
                    String scanned_pcno = ((JSONObject) overall_datewise_piece_scan_details_jsonobj.get(key)).getString("scanned_pcno");
                    String diff_qty = ((JSONObject) overall_datewise_piece_scan_details_jsonobj.get(key)).getString("diff_qty");

                    txt_Section.setText(sectionname);

                    Overall_Datewise_Piece_Scan_Data_Object obj = new Overall_Datewise_Piece_Scan_Data_Object(index,orderid, sectionid, job_ref, shipcode, sectionname, bundle_qty, scanned_pcno, diff_qty);
                    results.add(i, obj);
                    i++;
                }
            }
            totalItemCount = Integer.parseInt(jsonObj.optString("totalcount"));
            offset = Integer.parseInt(jsonObj.optString("offset"));
            if(totalItemCount == offset)
            {
                Overall_Datewise_Piece_Scan_Data_Object obj = new Overall_Datewise_Piece_Scan_Data_Object(0, 0, 0, "","","Grand Total", jsonObj.optString("total_pcs"), jsonObj.optString("total_bqty"), jsonObj.optString("total_diff"));
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

    public void clearfunc() {

        Intent intent = new Intent(Overall_Datewise_Piece_Scanned_Activity.this, Empty_Activity.class);
        intent.putExtra("fromdate", selectedfromdate);
        intent.putExtra("todate", selectedtodate);
        intent.putExtra("fromday", String.valueOf(fromday));
        intent.putExtra("frommonth", String.valueOf(frommonth));
        intent.putExtra("fromyear", String.valueOf(fromyear));
        intent.putExtra("today", String.valueOf(today));
        intent.putExtra("tomonth", String.valueOf(tomonth));
        intent.putExtra("toyear", String.valueOf(toyear));
        intent.putExtra("empty_type", "Overall_Datewise_Piece_Scanned_Activity");
        startActivity(intent);
        finish();
    }
    private void fetch_overall_scanned_details() {
        searchlist = true;
        if(mAdapter!= null)
        {
            clearfunc();
        }
        else {
            Overall_Datewise_Piece_Scanned_Activity.custPrograssbar.prograssCreate(this);
            session = new SessionManagement(getApplicationContext());
            HashMap<String, String> user = session.getUserDetails();
            processorid = user.get(SessionManagement.KEY_PROCESSOR_ID);
            userid = user.get(SessionManagement.KEY_USER_ID);
            username = user.get(SessionManagement.KEY_USER);
            JSONObject jsonObject = new JSONObject();
            try {

                jsonObject.put("userid", userid);
                jsonObject.put("contractorid", processorid);

                jsonObject.put("limit", endindex);
                jsonObject.put("offset", startindex);
                jsonObject.put("from_date", selectedfromdate);
                jsonObject.put("to_date", selectedfromdate);

                JsonParser jsonParser = new JsonParser();
                Call<JsonObject> call = APIClient.getInterface().fetch_overall_scanned_details((JsonObject) jsonParser.parse(jsonObject.toString()));
                GetResult getResult = new GetResult();
                getResult.setMyListener(this);
                getResult.callForLogin(call, "fetch_overall_scanned_details");
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


    @Override
    public void onBackPressed() {
        session = new SessionManagement(getApplicationContext());
        HashMap<String, String> user = session.getUserDetails();
        Intent intent = new Intent(Overall_Datewise_Piece_Scanned_Activity.this, MainActivity.class);
        intent.putExtra("name", user.get(SessionManagement.KEY_USER));
        intent.putExtra("userid", user.get(SessionManagement.KEY_USER_ID));
        intent.putExtra("processorid", user.get(SessionManagement.KEY_PROCESSOR_ID));
        startActivity(intent);
        finish();
    }

    private void fetchscrolldata() {
        Overall_Datewise_Piece_Scanned_Activity.custPrograssbar.prograssCreate(this);
        session = new SessionManagement(getApplicationContext());
        HashMap<String, String> user = session.getUserDetails();
        userid = user.get(SessionManagement.KEY_USER_ID);

        JSONObject jsonObject = new JSONObject();
        try {

            jsonObject.put("userid", userid);
            jsonObject.put("contractorid", processorid);
            jsonObject.put("limit", endindex);
            jsonObject.put("offset", startindex);
            jsonObject.put("from_date", selectedfromdate);
            jsonObject.put("to_date", selectedfromdate);

            JsonParser jsonParser = new JsonParser();
            Call<JsonObject> call = APIClient.getInterface().fetch_overall_scanned_details((JsonObject) jsonParser.parse(jsonObject.toString()));
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

            Overall_Datewise_Piece_Scanned_Activity.custPrograssbar.closePrograssBar();
            progress.setVisibility(View.INVISIBLE);
            if (callNo.equalsIgnoreCase("fetch_overall_scanned_details")) {
                JSONObject jsonObject = new JSONObject(result.toString());
                String mStatus = jsonObject.optString("status");
                String message = jsonObject.optString("message");
                if (mStatus.equals("success")) {

                    String totalcount = jsonObject.optString("totalcount");
                    totalItemCount = Integer.parseInt(totalcount);
                    txt_empty.setVisibility(View.INVISIBLE);

                    overall_datewise_piece_scan_details_data_obj = jsonObject.getJSONObject("data").toString();

                    mRecyclerView = (RecyclerView) findViewById(R.id.my_recycler_view);
                    mRecyclerView.setHasFixedSize(true);
                    mLayoutManager = new LinearLayoutManager(Overall_Datewise_Piece_Scanned_Activity.this);
                    mRecyclerView.setLayoutManager(mLayoutManager);
                    mAdapter = new Overall_Datewise_Piece_Scan_Adapter(getDataSet());
                    mRecyclerView.setAdapter(mAdapter);

                    ((Overall_Datewise_Piece_Scan_Adapter) mAdapter).setOnItemClickListener(new Overall_Datewise_Piece_Scan_Adapter.MyClickListener() {
                        @Override
                        public void onItemClick(int position, View v) {
                            int orderid =((Overall_Datewise_Piece_Scan_Adapter) mAdapter).getOrderid(position);
                            int sectionid =((Overall_Datewise_Piece_Scan_Adapter) mAdapter).getSectionid(position);
                            String shipcode =((Overall_Datewise_Piece_Scan_Adapter) mAdapter).getShipcode(new String(String.valueOf(position)));
                            String job_ref =((Overall_Datewise_Piece_Scan_Adapter) mAdapter).getJob_ref(new String(String.valueOf(position)));
                            String sectionname =((Overall_Datewise_Piece_Scan_Adapter) mAdapter).getSectionname(new String(String.valueOf(position)));
                            if(sectionname.equals("Grand Total"))
                            {
                            }
                            else
                            {
                                session = new SessionManagement(getApplicationContext());
                                HashMap<String, String> user = session.getUserDetails();

                                Intent intent = new Intent(Overall_Datewise_Piece_Scanned_Activity.this, Datewise_Piece_Scanned_Detail_Activity.class);
                                intent.putExtra("name", user.get(SessionManagement.KEY_USER));
                                intent.putExtra("userid", user.get(SessionManagement.KEY_USER_ID));
                                intent.putExtra("processorid", user.get(SessionManagement.KEY_PROCESSOR_ID));

                                intent.putExtra("fromdate", selectedfromdate);
                                intent.putExtra("todate", selectedfromdate);
                                intent.putExtra("fromday", String.valueOf(fromday));
                                intent.putExtra("frommonth", String.valueOf(frommonth));
                                intent.putExtra("fromyear", String.valueOf(fromyear));
                                intent.putExtra("today", String.valueOf(today));
                                intent.putExtra("tomonth", String.valueOf(tomonth));
                                intent.putExtra("toyear", String.valueOf(toyear));

                                intent.putExtra("job_ref", job_ref);
                                intent.putExtra("shipcode", shipcode);
                                intent.putExtra("sectionname", sectionname);
                                intent.putExtra("orderid", String.valueOf(orderid));
                                intent.putExtra("sectionid", String.valueOf(sectionid));

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
                    new AlertDialog.Builder(Overall_Datewise_Piece_Scanned_Activity.this)
                            .setMessage(message)
                            .setCancelable(false)
                            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface arg1, int arg0) {
                                    arg1.dismiss();
                                }
                            }).show();
                }
                else {
                    new AlertDialog.Builder(Overall_Datewise_Piece_Scanned_Activity.this)
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

                    overall_datewise_piece_scan_details_data_obj = jsonObject.getJSONObject("data").toString();
                    JSONObject jsonObj = new JSONObject(overall_datewise_piece_scan_details_data_obj);
                    overall_datewise_piece_scan_details_jsonobj = jsonObj.getJSONObject("datewise_piece_scan_details");
                    Iterator<String> datewise_piece_scan_contents = overall_datewise_piece_scan_details_jsonobj.keys();
                    int i = 0;
                    while (datewise_piece_scan_contents.hasNext()) {
                        String key = datewise_piece_scan_contents.next();
                        if (overall_datewise_piece_scan_details_jsonobj.get(key) instanceof JSONObject) {
                            int orderid = Integer.parseInt(((JSONObject) overall_datewise_piece_scan_details_jsonobj.get(key)).getString("orderid"));
                            int index = Integer.parseInt(((JSONObject) overall_datewise_piece_scan_details_jsonobj.get(key)).getString("index"));
                            int sectionid = Integer.parseInt(((JSONObject) overall_datewise_piece_scan_details_jsonobj.get(key)).getString("sectionid"));
                            String job_ref = ((JSONObject) overall_datewise_piece_scan_details_jsonobj.get(key)).getString("job_ref");
                            String shipcode = ((JSONObject) overall_datewise_piece_scan_details_jsonobj.get(key)).getString("shipcode");
                            String sectionname = ((JSONObject) overall_datewise_piece_scan_details_jsonobj.get(key)).getString("sectionname");

                            String bundle_qty = ((JSONObject) overall_datewise_piece_scan_details_jsonobj.get(key)).getString("bundle_qty");
                            String scanned_pcno = ((JSONObject) overall_datewise_piece_scan_details_jsonobj.get(key)).getString("scanned_pcno");
                            String diff_qty = ((JSONObject) overall_datewise_piece_scan_details_jsonobj.get(key)).getString("diff_qty");

                            txt_Section.setText(sectionname);

                            Overall_Datewise_Piece_Scan_Data_Object obj = new Overall_Datewise_Piece_Scan_Data_Object(index,orderid, sectionid, job_ref, shipcode, sectionname, bundle_qty, scanned_pcno, diff_qty);
                            results.add(i, obj);
                            i++;
                        }
                    }
                    totalItemCount = Integer.parseInt(jsonObj.optString("totalcount"));
                    offset = Integer.parseInt(jsonObj.optString("offset"));
                    if(totalItemCount == endindex && grandtotreach==false)
                    {
                        grandtotreach = true;
                        if(totalItemCount == offset && grandtotreach)
                        {
                            Overall_Datewise_Piece_Scan_Data_Object obj = new Overall_Datewise_Piece_Scan_Data_Object(0, 0, 0, "","","Grand Total", jsonObj.optString("total_pcs"), jsonObj.optString("total_bqty"), jsonObj.optString("total_diff"));
                            results.add(i, obj);
                        }
                    }

                    mAdapter.notifyDataSetChanged();
                }
                else if (mStatus.equals("nodatafound")) {
                    if(mAdapter == null)
                    {
                        // Do nothing
                    }
                    new AlertDialog.Builder(Overall_Datewise_Piece_Scanned_Activity.this)
                            .setMessage(message)
                            .setCancelable(false)
                            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface arg1, int arg0) {
                                    arg1.dismiss();
                                }
                            }).show();
                }
                else {
                    new AlertDialog.Builder(Overall_Datewise_Piece_Scanned_Activity.this)
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
