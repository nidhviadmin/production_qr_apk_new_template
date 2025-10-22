package com.bipinexports.productionqr.Activity;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
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
import com.bipinexports.productionqr.DatewiseDataObject;
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
import java.util.HashMap;

import java.util.GregorianCalendar;
import java.util.Iterator;

import retrofit2.Call;


public class DatewiseOutputActivity extends AppCompatActivity implements View.OnClickListener, GetResult.MyListener {

    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private static String LOG_TAG = "CardViewActivity";

    SessionManagement session;
    TextView txtUser;
    ImageView imageView;
    String datewisedataobj;
    JSONObject datewiseoutputjsonobj;
    String processorid;
    String userid, User, Id;
    ProgressBar progress;

    EditText txtFromDate, txttoDate;

    final Calendar fromcldr = Calendar.getInstance();
    DatePickerDialog frompicker;
    int fromday;
    int frommonth;
    int fromyear;
    String selectedfromdate;


    final Calendar tocldr = Calendar.getInstance();
    int today;
    int tomonth;
    int toyear;
    String selectedtodate;
    Button FetchData;
    String startdate, enddate, type;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_datewiseoutput);

        type = getIntent().getStringExtra("type");

        txtUser = findViewById(R.id.txtUser);
        imageView = findViewById(R.id.imgd);
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
        imageView.setOnClickListener(this);

        txtFromDate = findViewById(R.id.txtFromDate);
        txttoDate = findViewById(R.id.txttoDate);
        FetchData  = findViewById(R.id.FetchData);

        txtFromDate.setOnClickListener(this);
        txttoDate.setOnClickListener(this);
        FetchData.setOnClickListener(this);

        getvalue();

        if (type.equals("Data")) {

            startdate = getIntent().getStringExtra("startdate");
            enddate = getIntent().getStringExtra("enddate");

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

            fetchDatewiseoutputDetails();
        }
    }

    private ArrayList<DatewiseDataObject> getDataSet() {
        ArrayList results = new ArrayList<DatewiseDataObject>();
        try {
            JSONObject jsonObj = new JSONObject(datewisedataobj);
            datewiseoutputjsonobj = jsonObj.getJSONObject("datewiseoutput");
            Iterator<String> datewiseoutputcontents = datewiseoutputjsonobj.keys();
            int i = 0;
            while (datewiseoutputcontents.hasNext()) {
                String key = datewiseoutputcontents.next();
                if (datewiseoutputjsonobj.get(key) instanceof JSONObject) {
                    String date =  ((JSONObject) datewiseoutputjsonobj.get(key)).getString("date");
                    String datewise =  ((JSONObject) datewiseoutputjsonobj.get(key)).getString("datewise");
                    String datewisebundlecnt = ((JSONObject) datewiseoutputjsonobj.get(key)).getString("datewisebundlecnt");
                    String datewisebundleqty = ((JSONObject) datewiseoutputjsonobj.get(key)).getString("datewisebundleqty");
                    String datewisebundleprice = ((JSONObject) datewiseoutputjsonobj.get(key)).getString("datewisebundleprice");
                    DatewiseDataObject obj = new DatewiseDataObject(date, datewise,datewisebundlecnt, datewisebundleqty, datewisebundleprice);
                    results.add(i, obj);
                    i++;
                }
            }
            DatewiseDataObject obj = new DatewiseDataObject("Grand Total","", jsonObj.optString("grandtotalbundlecnt"), jsonObj.optString("grandtotalbundleqty"), jsonObj.optString("grandtotalprice"));
            results.add(i, obj);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return results;
    }


    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.imgd:
                PopupMenu popup = new PopupMenu(DatewiseOutputActivity.this, imageView);
                popup.getMenuInflater().inflate(R.menu.menu_main, popup.getMenu());

                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    public boolean onMenuItemClick(MenuItem item) {
                        if (item.getItemId() == R.id.log) {
                            session.logoutUser();
                            finish();
                        }
                        else if (item.getItemId() == R.id.changepassword) {
                            Intent intent = new Intent(DatewiseOutputActivity.this, ChangepasswordActivity.class);
                            startActivity(intent);
                            finish();
                        }
                        return true;
                    }
                });
                popup.show();
                break;
            case R.id.txtFromDate:
                if(fromday == 0 || frommonth == 0 || fromyear == 0)
                {
                    fromday = fromcldr.get(Calendar.DAY_OF_MONTH);
                    frommonth = fromcldr.get(Calendar.MONTH);
                    fromyear = fromcldr.get(Calendar.YEAR);
                }

                frompicker = new DatePickerDialog(DatewiseOutputActivity.this, R.style.datepicker,
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
                                today = 0;
                                tomonth = 0;
                                toyear = 0;
                                selectedfromdate = dateString;
                                selectedtodate = dateString;
                            }
                        }, fromyear, frommonth, fromday);
                frompicker.show();
                break;
            case R.id.txttoDate:

                if (txtFromDate.length() == 0) {
                    AlertDialog alertDialog = new AlertDialog.Builder(DatewiseOutputActivity.this)
                        .setMessage("Select From Date!")
                        .setCancelable(false)
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface d, int arg0) {
                                d.dismiss();
                            }
                        }).show();
                }
                else
                {
                    if(today == 0 || tomonth == 0 || toyear == 0)
                    {
                        today = fromday;
                        tomonth = frommonth;
                        toyear  = fromyear;
                    }
                    // min is today
                    GregorianCalendar minDate = new GregorianCalendar();
                    minDate.set(toyear, tomonth, today);

                    // max is after 1 month
                    GregorianCalendar maxDate = new GregorianCalendar();
                    maxDate.set(toyear, tomonth, today+9);

                    DatePickerDialog dialog = new DatePickerDialog(DatewiseOutputActivity.this, R.style.datepicker,
                            new DatePickerDialog.OnDateSetListener() {
                                @Override
                                public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                                    SimpleDateFormat sdf = new SimpleDateFormat("dd-MMM-yyyy");
                                    tocldr.set(year, monthOfYear, dayOfMonth);

                                    today = dayOfMonth;
                                    tomonth = monthOfYear;
                                    toyear = year;

                                    String dateString = sdf.format(tocldr.getTime());
                                    txttoDate.setText(dateString);
                                    selectedtodate = dateString;
                                }
                            }, toyear, tomonth, today);

                    dialog.getDatePicker().setCalendarViewShown(true);
                    dialog.getDatePicker().setSpinnersShown(false);
                    dialog.getDatePicker().setMaxDate(maxDate.getTimeInMillis());
                    dialog.getDatePicker().setMinDate(minDate.getTimeInMillis());
                    dialog.show();
                }
                break;

            case R.id.FetchData:
                if (txtFromDate.length() == 0) {
                    AlertDialog alertDialog = new AlertDialog.Builder(DatewiseOutputActivity.this)
                            .setMessage("Select From Date!")
                            .setCancelable(false)
                            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface d, int arg0) {
                                    d.dismiss();
                                }
                            }).show();

                }
                else if (txttoDate.length() == 0) {
                    new AlertDialog.Builder(DatewiseOutputActivity.this)
                            .setMessage("Select To Date!")
                            .setCancelable(false)
                            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface d, int arg0) {
                                    d.dismiss();
                                }
                            }).show();

                }
                else {
                    progress.setVisibility(View.VISIBLE);
                    fetchDatewiseoutputDetails();
                }
        }
    }

    private void fetchDatewiseoutputDetails() {

        session = new SessionManagement(getApplicationContext());
        HashMap<String, String> user = session.getUserDetails();
        processorid = user.get(SessionManagement.KEY_PROCESSOR_ID);
        userid = user.get(SessionManagement.KEY_USER_ID);

        JSONObject jsonObject = new JSONObject();
        try {

            jsonObject.put("userid", userid);
            jsonObject.put("processorid", processorid);
            jsonObject.put("fromdate", selectedfromdate);
            jsonObject.put("todate", selectedtodate);

            JsonParser jsonParser = new JsonParser();
            Call<JsonObject> call = APIClient.getInterface().fetchdatewiseoutputdetails((JsonObject) jsonParser.parse(jsonObject.toString()));
            GetResult getResult = new GetResult();
            getResult.setMyListener(this);
            getResult.callForLogin(call, "fetchdatewiseoutputdetails");
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
    @Override
    public void onBackPressed() {
        session = new SessionManagement(getApplicationContext());
        HashMap<String, String> user = session.getUserDetails();
        Intent intent = new Intent(DatewiseOutputActivity.this, MainActivity.class);
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

            if (callNo.equalsIgnoreCase("fetchdatewiseoutputdetails")) {
                JSONObject jsonObject = new JSONObject(result.toString());
                String mStatus = jsonObject.optString("status");
                String message = jsonObject.optString("message");
                if (mStatus.equals("success")) {

                    datewisedataobj = jsonObject.getJSONObject("data").toString();
                    progress.setVisibility(View.GONE);

                    mRecyclerView = (RecyclerView) findViewById(R.id.my_recycler_view);
                    mRecyclerView.setHasFixedSize(true);
                    mLayoutManager = new LinearLayoutManager(DatewiseOutputActivity.this);
                    mRecyclerView.setLayoutManager(mLayoutManager);
                    mAdapter = new DatewiseoutputViewAdapter(getDataSet());
                    mRecyclerView.setAdapter(mAdapter);

                    ((DatewiseoutputViewAdapter) mAdapter).setOnItemClickListener(new DatewiseoutputViewAdapter.MyClickListener() {
                        @Override
                        public void onItemClick(int position, View v) {
                            String date =((DatewiseoutputViewAdapter) mAdapter).getDate(position);
                            Intent intent = new Intent(DatewiseOutputActivity.this, DailyProductionOutputActivity.class);
                            intent.putExtra("selecteddate", date);
                            intent.putExtra("fromdate", selectedfromdate);
                            intent.putExtra("todate", selectedtodate);

                            intent.putExtra("fromday", String.valueOf(fromday));
                            intent.putExtra("frommonth", String.valueOf(frommonth));
                            intent.putExtra("fromyear", String.valueOf(fromyear));
                            intent.putExtra("today", String.valueOf(today));
                            intent.putExtra("tomonth", String.valueOf(tomonth));
                            intent.putExtra("toyear", String.valueOf(toyear));
                            Log.e("test ","firtst" +today);
                            intent.putExtra("processorid", processorid);

                            startActivity(intent);
                            finish();
                        }
                    });
                }
                else if(mStatus.equals("nodatafound"))
                {
                    if(mAdapter == null)
                    {
                        // Do nothing
                    }
                    new AlertDialog.Builder(DatewiseOutputActivity.this)
                        .setMessage(message)
                        .setCancelable(false)
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface arg1, int arg0) {
                                arg1.dismiss();
                            }
                        }).show();
                }
                else {
                    new AlertDialog.Builder(DatewiseOutputActivity.this)
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
