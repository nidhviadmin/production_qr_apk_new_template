package com.bipinexports.productionqr.Activity;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
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

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;

import retrofit2.Call;

public class OuputSummaryActivity extends BaseActivity implements View.OnClickListener, GetResult.MyListener {

    String Id, User;

    SessionManagement session;
    GridView gridView;
    ProgressBar progress;
    String processorid;
    String userid;

    EditText txtFromDate, txttoDate;

    ImageView imageView;

    RelativeLayout btn_OutputSummary;
    LinearLayout overallcard;

    TextView txtOutputBundleCount, txtOutputbundleqty, txtOutputPrice;
    TextView txtUser;

    final Calendar fromcldr = Calendar.getInstance();
    DatePickerDialog frompicker;
    int fromday = 0;
    int frommonth = 0;
    int fromyear = 0;
    String selectedfromdate;


    final Calendar tocldr = Calendar.getInstance();
    DatePickerDialog topicker;
    int today = 0;
    int tomonth = 0;
    int toyear = 0;
    String selectedtodate;
    Button FetchData;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_output_summary);
        setContentView(R.layout.activity_base);
        setupDrawer();

        View content = getLayoutInflater().inflate(
                R.layout.activity_output_summary,
                findViewById(R.id.content_frame),
                true
        );

        imageView = (ImageView) content.findViewById(R.id.imgd);
        gridView = (GridView) this.findViewById(R.id.grid);
        progress = (ProgressBar) content.findViewById(R.id.progress);
        progress.setVisibility(View.VISIBLE);

        txtUser = (TextView) content.findViewById(R.id.txtUser);

        session = new SessionManagement(getApplicationContext());
        HashMap<String, String> user = session.getUserDetails();
        String name = user.get(SessionManagement.KEY_USER);
        this.Id = user.get(SessionManagement.KEY_PROCESSOR_ID);
        processorid = user.get(SessionManagement.KEY_PROCESSOR_ID);
        userid = user.get(SessionManagement.KEY_USER_ID);
        this.User = user.get(SessionManagement.KEY_USER);

        processorid = getIntent().getStringExtra("processorid");
        imageView.setOnClickListener(this);


        txtFromDate = content.findViewById(R.id.txtFromDate);
        txttoDate = content.findViewById(R.id.txttoDate);
        FetchData  = content.findViewById(R.id.FetchData);

        txtFromDate.setOnClickListener(this);
        txttoDate.setOnClickListener(this);


        btn_OutputSummary =  content.findViewById(R.id.btn_OutputSummary);

        txtOutputBundleCount = content.findViewById(R.id.txtOutputBundleCount);
        txtOutputbundleqty = content.findViewById(R.id.txtOutputbundleqty);
        txtOutputPrice = content.findViewById(R.id.txtOutputPrice);

        overallcard = (LinearLayout) content.findViewById(R.id.overallcard);
        overallcard.setVisibility(View.INVISIBLE);
        getvalue();

        btn_OutputSummary.setVisibility(View.INVISIBLE);
        //btn_OutputSummary.setOnClickListener(this);
        FetchData.setOnClickListener(this);
    }

    public void onClick(View v) {
        if (isOnline()) {
            switch (v.getId()) {
                case R.id.imgd:
                    PopupMenu popup = new PopupMenu(OuputSummaryActivity.this, imageView);
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

                    frompicker = new DatePickerDialog(OuputSummaryActivity.this, R.style.datepicker,
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
                case R.id.txttoDate:
                    if(today == 0 || tomonth == 0 || toyear == 0)
                    {
                        today = tocldr.get(Calendar.DAY_OF_MONTH);
                        tomonth = tocldr.get(Calendar.MONTH);
                        toyear = tocldr.get(Calendar.YEAR);
                    }

                    topicker = new DatePickerDialog(OuputSummaryActivity.this, R.style.datepicker,
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
                    topicker.show();
                    break;
                case R.id.btn_OutputSummary:
                    if (txtFromDate.length() == 0) {
                        AlertDialog alertDialog = new AlertDialog.Builder(OuputSummaryActivity.this)
                                .setMessage("Select From Date!")
                                .setCancelable(false)
                                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface d, int arg0) {
                                        d.dismiss();
                                    }
                                }).show();

                    }
                    else if (txttoDate.length() == 0) {
                        new AlertDialog.Builder(OuputSummaryActivity.this)
                                .setMessage("Select To Date!")
                                .setCancelable(false)
                                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface d, int arg0) {
                                        d.dismiss();
                                    }
                                }).show();

                    }
                    else
                    {
//                        progress.setVisibility(View.VISIBLE);
//                        fetchDatewiseoutputDetails();
                    }

                    break;
                case R.id.FetchData:
                    if (txtFromDate.length() == 0) {
                        AlertDialog alertDialog = new AlertDialog.Builder(OuputSummaryActivity.this)
                                .setMessage("Select From Date!")
                                .setCancelable(false)
                                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface d, int arg0) {
                                        d.dismiss();
                                    }
                                }).show();

                    }
                    else if (txttoDate.length() == 0) {
                        new AlertDialog.Builder(OuputSummaryActivity.this)
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
                        getProductionOutputDetails();
                    }
            }

        }
        else {
            AlertDialog alertDialog = new AlertDialog.Builder(OuputSummaryActivity.this)
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

    private void getProductionOutputDetails() {

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
            Call<JsonObject> call = APIClient.getInterface().fetchproductionoutputsummary((JsonObject) jsonParser.parse(jsonObject.toString()));
            GetResult getResult = new GetResult();
            getResult.setMyListener(this);
            getResult.callForLogin(call, "fetchproductionoutputsummary");
        }
        catch (JSONException e) {
            e.printStackTrace();
        }
    }


    @Override
    public void onBackPressed() {
        session = new SessionManagement(getApplicationContext());
        HashMap<String, String> user = session.getUserDetails();
        Intent intent = new Intent(OuputSummaryActivity.this, MainActivity.class);
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

           if (callNo.equalsIgnoreCase("fetchproductionoutputsummary")) {
                JSONObject jsonObject = new JSONObject(result.toString());
                String mStatus = jsonObject.optString("status");
                String message = jsonObject.optString("message");
               progress.setVisibility(View.GONE);

               if (mStatus.equals("success")) {
                   JSONObject jsonObj = jsonObject.getJSONObject("data");

                   JSONObject productionoutputsJsonObj = jsonObj.getJSONObject("productionoutputs");

                   String productionoutbundlecnt = productionoutputsJsonObj.optString("productionoutbundlecnt");
                   String productionoutbundleqty = productionoutputsJsonObj.optString("productionoutbundleqty");
                   String productionoutprice = productionoutputsJsonObj.optString("productionoutprice");

                   txtOutputBundleCount.setText(productionoutbundlecnt);
                   txtOutputbundleqty.setText(productionoutbundleqty);
                   txtOutputPrice.setText(productionoutprice);
                   overallcard.setVisibility(View.VISIBLE);
                   btn_OutputSummary.setVisibility(View.VISIBLE);
               }
               else if (mStatus.equals("logout"))
               {
                   new AlertDialog.Builder(OuputSummaryActivity.this)
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
                   new AlertDialog.Builder(OuputSummaryActivity.this)
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
