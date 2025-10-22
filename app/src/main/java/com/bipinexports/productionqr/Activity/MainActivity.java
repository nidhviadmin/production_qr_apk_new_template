package com.bipinexports.productionqr.Activity;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DownloadManager;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import androidx.annotation.RequiresApi;

import com.bipinexports.productionqr.MainImage_Data_Object;
import com.google.android.material.snackbar.Snackbar;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bipinexports.productionqr.APIClient;
import com.bipinexports.productionqr.GetResult;
import com.bipinexports.productionqr.ModelClass;
import com.bipinexports.productionqr.R;
import com.bipinexports.productionqr.SessionManagement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import retrofit2.Call;

import static java.lang.Integer.parseInt;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, GetResult.MyListener {

    String Id, User;
    SessionManagement session;

    GridView gridView;
    ProgressBar progress;
    ArrayList<ModelClass> mylist = new ArrayList<>();
    String processorid;
    String userid;
    String isqc;
    String qrid;
    String qrprefix;
    String unitid;
    String mac_service_verification_count, fabric_pendingcount, accessory_pendingcount;
    ProgressDialog mProgressDialog;
    private static final int MY_REQUEST_CODE = 786;

    ImageView imageView, homebtn;

    LinearLayout layout_jobsummary, layout_qrscan, layout_output, layout_datewiseoutput, layout_accessory_receipt, layout_mac_scan, layout_mac_service_verification,
            layout_piecewise_qrscan, layout_datewise_pc, layout_piecewise_qrscan_using_usb_barcode_reader, layout_emp_operation_mapp, layout_employee_bundle_mapping, layout_machine_scan, layout_sharptool_scan,
            layout_emp_operation_mapped;
    LinearLayout layout_piecewise_qrscan_using_barcode_reader, layout_usb_qrscan, layout_bundle_scan, layout_multi_bundle_qrscan_using_usb_barcode_reader, layout_accessory_receipt_new, layout_fabric_receipt_new;
    TextView txtScan, txt_piecewise_scan, txt_bundleqr, txt_multiple_bundle_qr, txt_laywise, txt_reports, txt_machine_details, txt_piecewise;
    TextView txtUser, textpendingcount, mac_service_verification, txt_accessory_count, txt_rollwise, txt_bundle_mapping, txt_fabric_count;
    LinearLayout ll_category4, layout_laywise_qrscan, layout_laywise_qrscanner, ll_category8, layout_rollwise_qrscan, layout_rollwise_qrscanner, layout_rollwise_qrscan_using_barcode_reader, ll_category7, ll_category6,
    ll_category2, ll_category1, ll_category3, ll_category5, ll_category9, ll_category10, layout_scan_inward_rolls, layout_scan_relaxed_rolls, ll_category;

    int scan_requestcode =0;

    String username;

    String current_versionName;
    int current_versionCode;

    String myversionName;
    String apkurl;
    String app_version_name;

    String apkFileName;
    BroadcastReceiver receiver;
    RecyclerView recyclerView;
    public static CustPrograssbar_new custPrograssbar_new;

    MainImage_Adapter mAdapter;
    List<MainImage_Data_Object> itemList = new ArrayList<>();

    SwipeRefreshLayout swipeRefreshLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);

        imageView = (ImageView) findViewById(R.id.imgd);
        homebtn = (ImageView) findViewById(R.id.home);
        gridView = (GridView) this.findViewById(R.id.grid);
        progress = (ProgressBar) findViewById(R.id.progress);
        custPrograssbar_new = new CustPrograssbar_new();
        MainActivity.custPrograssbar_new.prograssCreate(this);

        layout_qrscan = (LinearLayout) findViewById(R.id.layout_qrscan);
        layout_mac_scan = (LinearLayout) findViewById(R.id.layout_mac_scan);
        layout_emp_operation_mapp = (LinearLayout) findViewById(R.id.layout_emp_operation_mapp);
        layout_emp_operation_mapped = (LinearLayout) findViewById(R.id.layout_emp_operation_mapped);
        layout_employee_bundle_mapping = (LinearLayout) findViewById(R.id.layout_employee_bundle_mapping);
        layout_piecewise_qrscan = (LinearLayout) findViewById(R.id.layout_piecewise_qrscan);
        layout_piecewise_qrscan_using_barcode_reader = (LinearLayout) findViewById(R.id.layout_piecewise_qrscan_using_barcode_reader);
        layout_piecewise_qrscan_using_usb_barcode_reader = (LinearLayout) findViewById(R.id.layout_piecewise_qrscan_using_usb_barcode_reader);
        layout_usb_qrscan = findViewById(R.id.layout_usb_qrscan);

        layout_bundle_scan = (LinearLayout) findViewById(R.id.layout_bundle_scan);
        layout_multi_bundle_qrscan_using_usb_barcode_reader = (LinearLayout) findViewById(R.id.layout_multi_bundle_qrscan_using_usb_barcode_reader);

        layout_laywise_qrscan = findViewById(R.id.layout_laywise_qrscan);
        layout_laywise_qrscanner = findViewById(R.id.layout_laywise_qrscanner);
        layout_rollwise_qrscan = findViewById(R.id.layout_rollwise_qrscan);
        layout_rollwise_qrscanner = findViewById(R.id.layout_rollwise_qrscanner);
        layout_rollwise_qrscan_using_barcode_reader = findViewById(R.id.layout_rollwise_qrscan_using_barcode_reader);

        layout_scan_inward_rolls = findViewById(R.id.layout_scan_inward_rolls);
        layout_scan_relaxed_rolls = findViewById(R.id.layout_scan_relaxed_rolls);

        ll_category   = (LinearLayout) findViewById(R.id.ll_category);
        ll_category.setVisibility(View.GONE);

        ll_category1   = (LinearLayout) findViewById(R.id.ll_category1);
        ll_category2   = (LinearLayout) findViewById(R.id.ll_category2);
        ll_category3   = (LinearLayout) findViewById(R.id.ll_category3);
        ll_category4   = (LinearLayout) findViewById(R.id.ll_category4);
        ll_category5   = (LinearLayout) findViewById(R.id.ll_category5);
        ll_category6 = findViewById(R.id.ll_category6);
        ll_category7 = findViewById(R.id.ll_category7);
        ll_category8 = findViewById(R.id.ll_category8);
        ll_category9 = findViewById(R.id.ll_category9);
        ll_category10 = findViewById(R.id.ll_category10);

        txtScan = (TextView) findViewById(R.id.txtScan);
        txtUser = (TextView) findViewById(R.id.txtUser);

        txt_piecewise_scan = (TextView) findViewById(R.id.txt_piecewise_scan);
        txt_piecewise = findViewById(R.id.txt_piecewise);
        txt_rollwise = findViewById(R.id.txt_rollwise);
        txt_piecewise.setVisibility(View.GONE);

        session = new SessionManagement(getApplicationContext());
        HashMap<String, String> user = session.getUserDetails();
        String name = user.get(SessionManagement.KEY_USER);
        this.Id = user.get(SessionManagement.KEY_PROCESSOR_ID);
        processorid = user.get(SessionManagement.KEY_PROCESSOR_ID);
        userid = user.get(SessionManagement.KEY_USER_ID);
        this.User = user.get(SessionManagement.KEY_USER);
        unitid = user.get(SessionManagement.KEY_UNITID);

        txt_bundleqr = findViewById(R.id.txt_bundleqr);
        txt_multiple_bundle_qr = findViewById(R.id.txt_multiple_bundle_qr);
        txt_laywise = findViewById(R.id.txt_laywise);
        txt_reports = findViewById(R.id.txt_reports);
        txt_machine_details = findViewById(R.id.txt_machine_details);
        txt_bundle_mapping = findViewById(R.id.txt_bundle_mapping);

        int underlineThickness = 8; // Thickness in pixels

        int color_bundleqr = ContextCompat.getColor(this, R.color.colorAccent);
        SpannableString spannableString = new SpannableString(txt_bundleqr.getText().toString());
        spannableString.setSpan(new ForegroundColorSpan(color_bundleqr), 0, txt_bundleqr.getText().toString().length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        spannableString.setSpan(new CustomUnderlineSpan(underlineThickness), 0, txt_bundleqr.getText().toString().length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        txt_bundleqr.setText(spannableString);

        int color_green = ContextCompat.getColor(this, android.R.color.holo_green_dark);
        spannableString = new SpannableString(txt_multiple_bundle_qr.getText().toString());
        spannableString.setSpan(new ForegroundColorSpan(color_green), 0, txt_multiple_bundle_qr.getText().toString().length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        spannableString.setSpan(new CustomUnderlineSpan(underlineThickness), 0, txt_multiple_bundle_qr.getText().toString().length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        txt_multiple_bundle_qr.setText(spannableString);

        int color_orange = ContextCompat.getColor(this, android.R.color.holo_orange_dark);
        spannableString = new SpannableString(txt_laywise.getText().toString());
        spannableString.setSpan(new ForegroundColorSpan(color_orange), 0, txt_laywise.getText().toString().length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        spannableString.setSpan(new CustomUnderlineSpan(underlineThickness), 0, txt_laywise.getText().toString().length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        txt_laywise.setText(spannableString);

        int color_greens = ContextCompat.getColor(this, R.color.zxing_viewfinder_laser);
        spannableString = new SpannableString(txt_reports.getText().toString());
        spannableString.setSpan(new ForegroundColorSpan(color_greens), 0, txt_reports.getText().toString().length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        spannableString.setSpan(new CustomUnderlineSpan(underlineThickness), 0, txt_reports.getText().toString().length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        txt_reports.setText(spannableString);

        int forgotpwd = ContextCompat.getColor(this, R.color.forgotpwd);
        spannableString = new SpannableString(txt_machine_details.getText().toString());
        spannableString.setSpan(new ForegroundColorSpan(forgotpwd), 0, txt_machine_details.getText().toString().length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        spannableString.setSpan(new CustomUnderlineSpan(underlineThickness), 0, txt_machine_details.getText().toString().length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        txt_machine_details.setText(spannableString);

        int colorPrimaryDark = ContextCompat.getColor(this, R.color.colorPrimaryDark);
        spannableString = new SpannableString(txt_rollwise.getText().toString());
        spannableString.setSpan(new ForegroundColorSpan(colorPrimaryDark), 0, txt_rollwise.getText().toString().length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        spannableString.setSpan(new CustomUnderlineSpan(underlineThickness), 0, txt_rollwise.getText().toString().length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        txt_rollwise.setText(spannableString);

        int red = ContextCompat.getColor(this, R.color.red);
        spannableString = new SpannableString(txt_bundle_mapping.getText().toString());
        spannableString.setSpan(new ForegroundColorSpan(red), 0, txt_bundle_mapping.getText().toString().length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        spannableString.setSpan(new CustomUnderlineSpan(underlineThickness), 0, txt_bundle_mapping.getText().toString().length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        txt_bundle_mapping.setText(spannableString);

        spannableString = new SpannableString(txt_piecewise.getText().toString());
        spannableString.setSpan(new ForegroundColorSpan(forgotpwd), 0, txt_piecewise.getText().toString().length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        spannableString.setSpan(new CustomUnderlineSpan(underlineThickness), 0, txt_piecewise.getText().toString().length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        txt_piecewise.setText(spannableString);

        accessory_pendingcount = "0";
        fabric_pendingcount = "0";
        mac_service_verification_count = "0";

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED &&
                    checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED &&
                    checkSelfPermission(Manifest.permission.ACCESS_NETWORK_STATE) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{Manifest.permission.CAMERA,
                                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                                Manifest.permission.ACCESS_NETWORK_STATE},
                        MY_REQUEST_CODE);
            }

            int permissionCheckREAD_PHONE_STATE = checkSelfPermission(Manifest.permission.READ_PHONE_STATE);
            if(permissionCheckREAD_PHONE_STATE != PackageManager.PERMISSION_GRANTED)
            {
                requestPermissions(new String[]{Manifest.permission.READ_PHONE_STATE}, MY_REQUEST_CODE);
            }
        }

        processorid = getIntent().getStringExtra("processorid");
        userid = getIntent().getStringExtra("userid");
        username = getIntent().getStringExtra("username");
        isqc = getIntent().getStringExtra("isqc");

        versioncode();
        getvalue();
        imageView.setOnClickListener(this);
        homebtn.setOnClickListener(this);

        layout_jobsummary = (LinearLayout) findViewById(R.id.layout_jobsummary);
        layout_output =   (LinearLayout) findViewById(R.id.layout_output);
        layout_datewiseoutput =   (LinearLayout) findViewById(R.id.layout_datewiseoutput);
        layout_accessory_receipt =   (LinearLayout) findViewById(R.id.layout_accessory_receipt);
        layout_mac_service_verification =   (LinearLayout) findViewById(R.id.layout_mac_service_verification);
        layout_datewise_pc = (LinearLayout) findViewById(R.id.layout_datewise_pc);

        layout_accessory_receipt_new =   (LinearLayout) findViewById(R.id.layout_accessory_receipt_new);
        txt_accessory_count = (TextView) findViewById(R.id.txt_accessory_count);

        layout_fabric_receipt_new = findViewById(R.id.layout_fabric_receipt_new);
        txt_fabric_count = (TextView) findViewById(R.id.txt_fabric_count);
        layout_machine_scan = findViewById(R.id.layout_machine_scan);
        layout_sharptool_scan= findViewById(R.id.layout_sharptool_scan);

        layout_qrscan.setOnClickListener(this);
        layout_mac_scan.setOnClickListener(this);
        layout_jobsummary.setOnClickListener(this);
        layout_output.setOnClickListener(this);
        layout_datewiseoutput.setOnClickListener(this);
        layout_accessory_receipt.setOnClickListener(this);
        layout_mac_service_verification.setOnClickListener(this);
        layout_piecewise_qrscan.setOnClickListener(this);
        layout_piecewise_qrscan_using_barcode_reader.setOnClickListener(this);
        layout_datewise_pc.setOnClickListener(this);
        layout_piecewise_qrscan_using_usb_barcode_reader.setOnClickListener(this);
        layout_usb_qrscan.setOnClickListener(this);
        layout_emp_operation_mapp.setOnClickListener(this);
        layout_emp_operation_mapped.setOnClickListener(this);
        layout_employee_bundle_mapping.setOnClickListener(this);
        layout_accessory_receipt_new.setOnClickListener(this);
        layout_laywise_qrscan.setOnClickListener(this);
        layout_laywise_qrscanner.setOnClickListener(this);
        layout_rollwise_qrscan.setOnClickListener(this);
        layout_rollwise_qrscanner.setOnClickListener(this);
        layout_rollwise_qrscan_using_barcode_reader.setOnClickListener(this);
        layout_fabric_receipt_new.setOnClickListener(this);
        layout_machine_scan.setOnClickListener(this);
        layout_sharptool_scan.setOnClickListener(this);
        layout_scan_relaxed_rolls.setOnClickListener(this);
        layout_scan_inward_rolls.setOnClickListener(this);

        layout_bundle_scan.setOnClickListener(this);
        layout_multi_bundle_qrscan_using_usb_barcode_reader.setOnClickListener(this);
//        layout_multi_bundle_qrscan_using_usb_barcode_reader.setVisibility(View.GONE);

        textpendingcount = (TextView) findViewById(R.id.txt_count);
        mac_service_verification = (TextView) findViewById(R.id.txt_mac_service_verification_count);
        fetch_image_cat_Details();

        getpendingdeliverycounts();
        get_mac_service_verification_counts();
        pending_fabric_receipts();
//        fetch_version_details();

        if(processorid.equals("95")|| processorid.equals("96") || processorid.equals("105") || processorid.equals("106"))
        {
            ll_category1.setVisibility(View.GONE);
            ll_category2.setVisibility(View.GONE);
            ll_category3.setVisibility(View.GONE);
            ll_category6.setVisibility(View.GONE);
            ll_category7.setVisibility(View.GONE);
            ll_category8.setVisibility(View.GONE);
            txt_rollwise.setVisibility(View.GONE);
            ll_category9.setVisibility(View.GONE);
            ll_category10.setVisibility(View.GONE);

            ll_category4.setVisibility(View.VISIBLE);
            ll_category5.setVisibility(View.VISIBLE);
            txt_piecewise.setVisibility(View.VISIBLE);

            txt_bundleqr.setVisibility(View.GONE);
            txt_multiple_bundle_qr .setVisibility(View.GONE);
            txt_laywise.setVisibility(View.GONE);
            txt_reports .setVisibility(View.GONE);
            txt_machine_details.setVisibility(View.GONE);
            txt_bundle_mapping .setVisibility(View.GONE);
        }
        else
        {
            ll_category4.setVisibility(View.GONE);
            ll_category5.setVisibility(View.GONE);
            ll_category8.setVisibility(View.GONE);
            txt_rollwise.setVisibility(View.GONE);
            txt_piecewise.setVisibility(View.GONE);
        }
        if(processorid.equals("8"))
        {
            //ll_category8.setVisibility(View.VISIBLE);
            //txt_rollwise.setVisibility(View.VISIBLE);
        }

//        swipeRefreshLayout = findViewById(R.id.swiperefresh);
//        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
//            @Override
//            public void onRefresh() {
//                swipeRefreshLayout.setRefreshing(false);
//                fetch_image_cat_Details();
//            }
//        });
//        swipeRefreshLayout.setColorSchemeColors(Color.BLACK);
    }

    private void versioncode() {
        if(isOnline()) {
            Context context = this;
            PackageManager manager = context.getPackageManager();
            try {
                PackageInfo info = manager.getPackageInfo(context.getPackageName(), 0);
                myversionName = info.versionName;
                Log.e("Bipin","myversionName :" + myversionName);
            }
            catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
                myversionName = "Unknown-01";
            }
        }
        else {

            AlertDialog alertDialog = new AlertDialog.Builder(MainActivity.this)
                    .setMessage("Please Check Your Internet Connection")
                    .setCancelable(false)
                    .setNegativeButton("Exit", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface arg1, int arg0) {
                            arg1.dismiss();
                            onDestroy();
                            finish();
                        }
                    }).show();
            alertDialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(Color.RED);
        }
    }

    private void getpendingdeliverycounts() {

        if(isOnline()) {
            session = new SessionManagement(getApplicationContext());
            HashMap<String, String> user = session.getUserDetails();
            processorid = user.get(SessionManagement.KEY_PROCESSOR_ID);
            userid = user.get(SessionManagement.KEY_USER_ID);
            username = user.get(SessionManagement.KEY_USER);

            JSONObject jsonObject = new JSONObject();
            try {
                jsonObject.put("userid", userid);
                jsonObject.put("username", username);
                jsonObject.put("contractorid", processorid);

                JsonParser jsonParser = new JsonParser();
                Call<JsonObject> call = APIClient.getInterface().pendingdeliverycount((JsonObject) jsonParser.parse(jsonObject.toString()));
                GetResult getResult = new GetResult();
                getResult.setMyListener(this);
                getResult.callForLogin(call, "pendingdeliverycount");
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        else {

            AlertDialog alertDialog = new AlertDialog.Builder(MainActivity.this)
                    .setMessage("Please Check Your Internet Connection")
                    .setCancelable(false)
                    .setNegativeButton("Exit", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface arg1, int arg0) {
                            arg1.dismiss();
                        }
                    }).show();
            alertDialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(Color.RED);
        }
    }

    private void get_mac_service_verification_counts() {

        if(isOnline()) {
            session = new SessionManagement(getApplicationContext());
            HashMap<String, String> user = session.getUserDetails();
            processorid = user.get(SessionManagement.KEY_PROCESSOR_ID);
            userid = user.get(SessionManagement.KEY_USER_ID);
            username = user.get(SessionManagement.KEY_USER);

            JSONObject jsonObject = new JSONObject();
            try {
                jsonObject.put("userid", userid);
                jsonObject.put("username", username);
                jsonObject.put("processorid", processorid);

                JsonParser jsonParser = new JsonParser();
                Call<JsonObject> call = APIClient.getInterface().machine_service_verification_count((JsonObject) jsonParser.parse(jsonObject.toString()));
                GetResult getResult = new GetResult();
                getResult.setMyListener(this);
                getResult.callForLogin(call, "machine_service_verification_count");
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        else {

            AlertDialog alertDialog = new AlertDialog.Builder(MainActivity.this)
                    .setMessage("Please Check Your Internet Connection")
                    .setCancelable(false)
                    .setNegativeButton("Exit", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface arg1, int arg0) {
                            arg1.dismiss();
                        }
                    }).show();
            alertDialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(Color.RED);
        }
    }


    @Override
    public void onClick(View v) {
        if (isOnline()) {
            switch (v.getId()) {
                case R.id.imgd:
                    PopupMenu popup = new PopupMenu(MainActivity.this, imageView);
                    popup.getMenuInflater().inflate(R.menu.menu_main, popup.getMenu());

                    popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                        public boolean onMenuItemClick(MenuItem item) {
                            if (item.getItemId() == R.id.log) {
                                session.logoutUser();
                                finish();
                            } else if (item.getItemId() == R.id.changepassword) {
                                Intent intent = new Intent(MainActivity.this, ChangepasswordActivity.class);
                                startActivity(intent);
                                finish();
                            }
                            return true;
                        }
                    });
                    popup.show();
                    break;
                case R.id.home:
                    Intent intent = new Intent(MainActivity.this, Switch_User_Activity.class);
                    intent.putExtra("username", username);
                    intent.putExtra("processorid", processorid);
                    intent.putExtra("password", "1234");
                    intent.putExtra("userid", userid);
                    startActivity(intent);
                    finish();
                    break;

                case R.id.layout_qrscan:
                    if(processorid.equals("95")|| processorid.equals("96") || processorid.equals("105") || processorid.equals("106"))
                    {
                        AlertDialog alertDialog = new AlertDialog.Builder(MainActivity.this)
                                .setMessage("Permissions not granted to view this page.\n Contact Admin!")
                                .setCancelable(false)
                                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface arg1, int arg0) {
                                    }
                                }).show();
                    }
                    else {
                        Scanning();
                        scan_requestcode = 0;
                    }
                    break;

                case R.id.layout_mac_scan:
                    if(processorid.equals("95")|| processorid.equals("96") || processorid.equals("105") || processorid.equals("106"))
                    {
                        AlertDialog alertDialog = new AlertDialog.Builder(MainActivity.this)
                                .setMessage("Permissions not granted to view this page.\n Contact Admin!")
                                .setCancelable(false)
                                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface arg1, int arg0) {
                                    }
                                }).show();
                    }
                    else {
                        scan_requestcode = 0;
                        Scanning();
                    }
                    break;

                case R.id.layout_piecewise_qrscan:
                    scan_requestcode =1;
                    PcwiseScanning();
                    break;

                case R.id.layout_piecewise_qrscan_using_barcode_reader:
                    scan_requestcode =1;
                    InbuiltReaderScanning();
                    break;

                case R.id.layout_piecewise_qrscan_using_usb_barcode_reader:
                    scan_requestcode =1;
                    UsbReaderScanning();
                    break;
                case R.id.layout_usb_qrscan:
                    if(processorid.equals("95")|| processorid.equals("96") || processorid.equals("105") || processorid.equals("106"))
                    {
                        AlertDialog alertDialog = new AlertDialog.Builder(MainActivity.this)
                                .setMessage("Permissions not granted to view this page.\n Contact Admin!")
                                .setCancelable(false)
                                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface arg1, int arg0) {
                                    }
                                }).show();
                    }
                    else {
                        scan_requestcode = 1;
                        Budnle_QR_Scanning_USB_Reader();
                    }
                    break;
                case R.id.layout_jobsummary:
                    if(processorid.equals("95")|| processorid.equals("96") || processorid.equals("105") || processorid.equals("106"))
                    {
                        AlertDialog alertDialog = new AlertDialog.Builder(MainActivity.this)
                                .setMessage("Permissions not granted to view this page.\n Contact Admin!")
                                .setCancelable(false)
                                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface arg1, int arg0) {
                                    }
                                }).show();
                    }
                    else {
                        if (unitid.equals("N")) {

                        } else {
                            AlertDialog alertDialog = new AlertDialog.Builder(MainActivity.this)
                                    .setMessage("Only Show Nidhvi Tec Job Summary")
                                    .setCancelable(false)
                                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface arg1, int arg0) {
                                        }
                                    }).show();
                        }
                        intent = new Intent(MainActivity.this, SelectJobSummaryActivity.class);
                        startActivity(intent);
                        finish();
                    }
                    break;
                case R.id.layout_output:
                    if(processorid.equals("95")|| processorid.equals("96") || processorid.equals("105") || processorid.equals("106"))
                    {
                        AlertDialog alertDialog = new AlertDialog.Builder(MainActivity.this)
                                .setMessage("Permissions not granted to view this page.\n Contact Admin!")
                                .setCancelable(false)
                                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface arg1, int arg0) {
                                    }
                                }).show();
                    }
                    else {
                        if (unitid.equals("N")) {
                             intent = new Intent(MainActivity.this, OuputSummaryActivity.class);
                            startActivity(intent);
                            finish();
                        } else {
                            AlertDialog alertDialog = new AlertDialog.Builder(MainActivity.this)
                                    .setMessage("Only Show Nidhvi Tec Job Summary")
                                    .setCancelable(false)
                                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface arg1, int arg0) {

                                        }
                                    }).show();
                        }
                    }
                    break;
                case R.id.layout_datewiseoutput:
                    if(processorid.equals("95")|| processorid.equals("96") || processorid.equals("105") || processorid.equals("106"))
                    {
                        AlertDialog alertDialog = new AlertDialog.Builder(MainActivity.this)
                                .setMessage("Permissions not granted to view this page.\n Contact Admin!")
                                .setCancelable(false)
                                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface arg1, int arg0) {
                                    }
                                }).show();
                    }
                    else {
                        if (unitid.equals("N")) {
                             intent = new Intent(MainActivity.this, DatewiseOutputActivity.class);
                            intent.putExtra("type", "New");
                            startActivity(intent);
                            finish();
                        } else {
                            AlertDialog alertDialog = new AlertDialog.Builder(MainActivity.this)
                                    .setMessage("Only Show Nidhvi Tec Job Summary")
                                    .setCancelable(false)
                                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface arg1, int arg0) {

                                        }
                                    }).show();
                        }
                    }
                    break;
                case R.id.layout_accessory_receipt:
                    if(processorid.equals("95")|| processorid.equals("96") || processorid.equals("105") || processorid.equals("106"))
                    {
                        AlertDialog alertDialog = new AlertDialog.Builder(MainActivity.this)
                                .setMessage("Permissions not granted to view this page.\n Contact Admin!")
                                .setCancelable(false)
                                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface arg1, int arg0) {
                                    }
                                }).show();
                    }
                    else
                    {
                        if (parseInt(accessory_pendingcount) > 0) {
                            if (unitid.equals("N")) {
                                MainActivity.custPrograssbar_new.prograssCreate(this);
                                fetch_accessory_receipt_Details();
                            }
                            else {
                                AlertDialog alertDialog = new AlertDialog.Builder(MainActivity.this)
                                        .setMessage("Only Show Nidhvi Tec Job Summary")
                                        .setCancelable(false)
                                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface arg1, int arg0) {
                                            }
                                        }).show();
                            }
                        }
                        else {
                            AlertDialog alertDialog = new AlertDialog.Builder(MainActivity.this)
                                .setMessage("No Data Found!")
                                .setCancelable(false)
                                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface arg1, int arg0) {
                                    }
                                }).show();
                        }
                    }
                    break;
                case R.id.layout_accessory_receipt_new:
                    if(processorid.equals("95")|| processorid.equals("96") || processorid.equals("105") || processorid.equals("106"))
                    {
                        AlertDialog alertDialog = new AlertDialog.Builder(MainActivity.this)
                                .setMessage("Permissions not granted to view this page.\n Contact Admin!")
                                .setCancelable(false)
                                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface arg1, int arg0) {
                                    }
                                }).show();
                    }
                    else
                    {
                        if (parseInt(accessory_pendingcount) > 0)
                        {
                            if (unitid.equals("N"))
                            {
                                MainActivity.custPrograssbar_new.prograssCreate(this);
                                pendingdeliveries_new_Details();
                            }
                            else
                                {
                                AlertDialog alertDialog = new AlertDialog.Builder(MainActivity.this)
                                .setMessage("Only Show Nidhvi Tec Job Summary")
                                .setCancelable(false)
                                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface arg1, int arg0) {
                                    }
                                }).show();
                            }
                        }
                        else {
                            AlertDialog alertDialog = new AlertDialog.Builder(MainActivity.this)
                            .setMessage("No Data Found!")
                            .setCancelable(false)
                            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface arg1, int arg0) {
                                }
                            }).show();
                        }
                    }
                    break;
                case R.id.layout_fabric_receipt_new:
                    if(processorid.equals("95")|| processorid.equals("96") || processorid.equals("105") || processorid.equals("106"))
                    {
                        AlertDialog alertDialog = new AlertDialog.Builder(MainActivity.this)
                        .setMessage("Permissions not granted to view this page.\n Contact Admin!")
                        .setCancelable(false)
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface arg1, int arg0) {
                            }
                        }).show();
                    }
                    else
                    {
                        if (parseInt(fabric_pendingcount) > 0) {
                            if (unitid.equals("N")) {
                                MainActivity.custPrograssbar_new.prograssCreate(this);
                                fabric_pendingdeliveries();
                            } else {
                                AlertDialog alertDialog = new AlertDialog.Builder(MainActivity.this)
                                .setMessage("Only Show Nidhvi Tec Job Summary")
                                .setCancelable(false)
                                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface arg1, int arg0) {
                                    }
                                }).show();
                            }
                        }
                        else {
                            AlertDialog alertDialog = new AlertDialog.Builder(MainActivity.this)
                            .setMessage("No Data Found!")
                            .setCancelable(false)
                            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface arg1, int arg0) {
                                }
                            }).show();
                        }
                    }
                    break;

                case R.id.layout_mac_service_verification:
                    if(processorid.equals("95")|| processorid.equals("96") || processorid.equals("105") || processorid.equals("106"))
                    {
                        AlertDialog alertDialog = new AlertDialog.Builder(MainActivity.this)
                        .setMessage("Permissions not granted to view this page.\n Contact Admin!")
                        .setCancelable(false)
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface arg1, int arg0) {
                            }
                        }).show();
                    }
                    else {
                        if (parseInt(mac_service_verification_count) > 0) {
                            MainActivity.custPrograssbar_new.prograssCreate(this);
                            fetch_machine_service_verification();
                        } else {
                            AlertDialog alertDialog = new AlertDialog.Builder(MainActivity.this)
                            .setMessage("No Data Found!")
                            .setCancelable(false)
                            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface arg1, int arg0) {
                                }
                            }).show();
                        }
                    }
                    break;
                case R.id.layout_datewise_pc:
                    if (unitid.equals("N")) {

                        session = new SessionManagement(getApplicationContext());
                        HashMap<String, String> user = session.getUserDetails();
                        intent = new Intent(MainActivity.this, Overall_Datewise_Piece_Scanned_Activity.class);
                        intent.putExtra("type", "New");
                        intent.putExtra("name", user.get(SessionManagement.KEY_USER));
                        intent.putExtra("userid", user.get(SessionManagement.KEY_USER_ID));
                        intent.putExtra("processorid", user.get(SessionManagement.KEY_PROCESSOR_ID));
                        startActivity(intent);
                        finish();
                    }
                    else {
                        AlertDialog alertDialog = new AlertDialog.Builder(MainActivity.this)
                                .setMessage("Only Show Nidhvi Tec Job Summary")
                                .setCancelable(false)
                                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface arg1, int arg0) {

                                    }
                                }).show();
                    }
                    break;
                case R.id.layout_emp_operation_mapp:
//                    Intent intent = new Intent(MainActivity.this, Employee_Operation_Mapping_Activity.class);
                     intent = new Intent(MainActivity.this, Select_Employee_Detail_Activity.class);
                    intent.putExtra("type", "New");
                    intent.putExtra("myversionName", myversionName);
                    intent.putExtra("name", username);
                    intent.putExtra("userid", userid);
                    intent.putExtra("processorid", processorid);
                    startActivity(intent);
                    finish();
                    break;

                case R.id.layout_emp_operation_mapped:
                    intent = new Intent(MainActivity.this, In_Employee_Bundle_Scan_Activity.class);
                    intent.putExtra("type", "New");
                    intent.putExtra("myversionName", myversionName);
                    intent.putExtra("name", username);
                    intent.putExtra("userid", userid);
                    intent.putExtra("processorid", processorid);
                    startActivity(intent);
                    finish();
                    break;

                case R.id.layout_employee_bundle_mapping:

                    intent = new Intent(MainActivity.this, In_Employee_Operation_Mapping_Activity.class);
                    intent.putExtra("type", "Bundle_mapp");
                    intent.putExtra("myversionName", myversionName);
                    intent.putExtra("name", username);
                    intent.putExtra("userid", userid);
                    intent.putExtra("processorid", processorid);
                    startActivity(intent);
                    finish();
                    break;
                case R.id.layout_bundle_scan:
                    intent = new Intent(MainActivity.this, Multiple_Bundle_QR_Scan_Activity.class);
                    intent.putExtra("myversionName", myversionName);
                    intent.putExtra("name", username);
                    intent.putExtra("userid", userid);
                    intent.putExtra("processorid", processorid);
                    startActivity(intent);
                    finish();
                    break;

                case R.id.layout_multi_bundle_qrscan_using_usb_barcode_reader:
                    intent = new Intent(MainActivity.this, Scanner_Multiple_Bundle_QR_Activity.class);
                    intent.putExtra("myversionName", myversionName);
                    intent.putExtra("name", username);
                    intent.putExtra("userid", userid);
                    intent.putExtra("processorid", processorid);
                    startActivity(intent);
                    finish();
                    break;

                case R.id.layout_laywise_qrscan:
                    intent = new Intent(MainActivity.this, Laywise_Bundle_QR_Scan_Activity.class);
                    intent.putExtra("myversionName", myversionName);
                    intent.putExtra("name", username);
                    intent.putExtra("userid", userid);
                    intent.putExtra("processorid", processorid);
                    startActivity(intent);
                    finish();
                    break;

                case R.id.layout_laywise_qrscanner:
                    intent = new Intent(MainActivity.this, Laywise_Multiple_Bundle_QR_Scanner_Activity.class);
                    intent.putExtra("myversionName", myversionName);
                    intent.putExtra("name", username);
                    intent.putExtra("userid", userid);
                    intent.putExtra("processorid", processorid);
                    startActivity(intent);
                    finish();
                    break;

                case R.id.layout_rollwise_qrscan:
                    intent = new Intent(MainActivity.this, Rollwise_QR_Scan_Activity.class);
                    intent.putExtra("myversionName", myversionName);
                    intent.putExtra("name", username);
                    intent.putExtra("userid", userid);
                    intent.putExtra("processorid", processorid);
                    startActivity(intent);
                    finish();
                    break;
                case R.id.layout_rollwise_qrscanner:
                    intent = new Intent(MainActivity.this, Rollwise_Multiple_QR_Scanner_Activity.class);
                    intent.putExtra("myversionName", myversionName);
                    intent.putExtra("name", username);
                    intent.putExtra("userid", userid);
                    intent.putExtra("processorid", processorid);
                    startActivity(intent);
                    finish();
                    break;
                case R.id.layout_rollwise_qrscan_using_barcode_reader:
                    intent = new Intent(MainActivity.this, Rollwise_Inbuilt_Scanner_Barcode_Activity.class);
                    intent.putExtra("myversionName", myversionName);
                    intent.putExtra("name", username);
                    intent.putExtra("userid", userid);
                    intent.putExtra("processorid", processorid);
                    startActivity(intent);
                    finish();
                    break;

                case R.id.layout_machine_scan:
                    intent = new Intent(MainActivity.this, Machine_QR_Scanview_Activity.class);
                    intent.putExtra("myversionName", myversionName);
                    intent.putExtra("name", username);
                    intent.putExtra("userid", userid);
                    intent.putExtra("processorid", processorid);
                    startActivity(intent);
                    finish();
                    break;

                case R.id.layout_sharptool_scan:
                    intent = new Intent(MainActivity.this, Sharptool_QR_Scanview_Activity.class);
                    intent.putExtra("myversionName", myversionName);
                    intent.putExtra("name", username);
                    intent.putExtra("userid", userid);
                    intent.putExtra("processorid", processorid);
                    startActivity(intent);
                    finish();
                    break;

                case R.id.layout_scan_inward_rolls:
                    intent = new Intent(MainActivity.this, Scan_Inward_Rolls_Activity.class);
                    intent.putExtra("myversionName", myversionName);
                    intent.putExtra("name", username);
                    intent.putExtra("userid", userid);
                    intent.putExtra("processorid", processorid);
                    startActivity(intent);
                    finish();
                    break;

                case R.id.layout_scan_relaxed_rolls:
                    intent = new Intent(MainActivity.this, Scan_Relaxed_Rolls_Activity.class);
                    intent.putExtra("myversionName", myversionName);
                    intent.putExtra("name", username);
                    intent.putExtra("userid", userid);
                    intent.putExtra("processorid", processorid);
                    startActivity(intent);
                    finish();
                    break;
            }
        }
        else {
            Snackbar snackbar = Snackbar
                    .make(v, "No internet connection!", Snackbar.LENGTH_LONG);
            snackbar.show();
        }
    }


    private void fetch_accessory_receipt_Details() {
        if(isOnline()) {
            session = new SessionManagement(getApplicationContext());
            HashMap<String, String> user = session.getUserDetails();
            processorid = user.get(SessionManagement.KEY_PROCESSOR_ID);
            userid = user.get(SessionManagement.KEY_USER_ID);

            JSONObject jsonObject = new JSONObject();
            try {
                jsonObject.put("userid", userid);
                jsonObject.put("contractorid", processorid);
                jsonObject.put("limit", 10);
                jsonObject.put("offset", 0);

                JsonParser jsonParser = new JsonParser();
                Call<JsonObject> call = APIClient.getInterface().fetch_accessory_receipt_Details((JsonObject) jsonParser.parse(jsonObject.toString()));
                GetResult getResult = new GetResult();
                getResult.setMyListener(this);
                getResult.callForLogin(call, "fetch_accessory_receipt_Details");
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        else {

            AlertDialog alertDialog = new AlertDialog.Builder(MainActivity.this)
                    .setMessage("Please Check Your Internet Connection")
                    .setCancelable(false)
                    .setNegativeButton("Exit", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface arg1, int arg0) {
                            arg1.dismiss();
                        }
                    }).show();
            alertDialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(Color.RED);
        }
    }


    private void pendingdeliveries_new_Details() {
        if(isOnline()) {
            session = new SessionManagement(getApplicationContext());
            HashMap<String, String> user = session.getUserDetails();
            processorid = user.get(SessionManagement.KEY_PROCESSOR_ID);
            userid = user.get(SessionManagement.KEY_USER_ID);

            JSONObject jsonObject = new JSONObject();
            try {
                jsonObject.put("userid", userid);
                jsonObject.put("contractorid", processorid);
                jsonObject.put("limit", 10);
                jsonObject.put("offset", 0);

                JsonParser jsonParser = new JsonParser();
                Call<JsonObject> call = APIClient.getInterface().pendingdeliveries_new((JsonObject) jsonParser.parse(jsonObject.toString()));
                GetResult getResult = new GetResult();
                getResult.setMyListener(this);
                getResult.callForLogin(call, "pendingdeliveries_new");
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        else {

            AlertDialog alertDialog = new AlertDialog.Builder(MainActivity.this)
                    .setMessage("Please Check Your Internet Connection")
                    .setCancelable(false)
                    .setNegativeButton("Exit", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface arg1, int arg0) {
                            arg1.dismiss();
                        }
                    }).show();
            alertDialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(Color.RED);
        }
    }

    private void fetch_machine_service_verification() {
        if(isOnline()) {
            session = new SessionManagement(getApplicationContext());
            HashMap<String, String> user = session.getUserDetails();
            processorid = user.get(SessionManagement.KEY_PROCESSOR_ID);
            userid = user.get(SessionManagement.KEY_USER_ID);

            JSONObject jsonObject = new JSONObject();
            try {
                jsonObject.put("userid", userid);
                jsonObject.put("processorid", processorid);

                JsonParser jsonParser = new JsonParser();
                Call<JsonObject> call = APIClient.getInterface().machine_service_verification((JsonObject) jsonParser.parse(jsonObject.toString()));
                GetResult getResult = new GetResult();
                getResult.setMyListener(this);
                getResult.callForLogin(call, "machine_service_verification");
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        else {

            AlertDialog alertDialog = new AlertDialog.Builder(MainActivity.this)
                    .setMessage("Please Check Your Internet Connection")
                    .setCancelable(false)
                    .setNegativeButton("Exit", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface arg1, int arg0) {
                            arg1.dismiss();
                        }
                    }).show();
            alertDialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(Color.RED);
        }

    }


    private void pending_fabric_receipts() {
        if(isOnline()) {
            session = new SessionManagement(getApplicationContext());
            HashMap<String, String> user = session.getUserDetails();
            processorid = user.get(SessionManagement.KEY_PROCESSOR_ID);
            userid = user.get(SessionManagement.KEY_USER_ID);

            JSONObject jsonObject = new JSONObject();
            try {
                jsonObject.put("userid", userid);
                jsonObject.put("contractorid", processorid);

                JsonParser jsonParser = new JsonParser();
                Call<JsonObject> call = APIClient.getInterface().pending_deliverycount((JsonObject) jsonParser.parse(jsonObject.toString()));
                GetResult getResult = new GetResult();
                getResult.setMyListener(this);
                getResult.callForLogin(call, "pending_fabric_receipts");
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        else {

            AlertDialog alertDialog = new AlertDialog.Builder(MainActivity.this)
                    .setMessage("Please Check Your Internet Connection")
                    .setCancelable(false)
                    .setNegativeButton("Exit", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface arg1, int arg0) {
                            arg1.dismiss();
                        }
                    }).show();
            alertDialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(Color.RED);
        }
    }

    private void fabric_pendingdeliveries() {
        if(isOnline()) {
            session = new SessionManagement(getApplicationContext());
            HashMap<String, String> user = session.getUserDetails();
            processorid = user.get(SessionManagement.KEY_PROCESSOR_ID);
            userid = user.get(SessionManagement.KEY_USER_ID);

            JSONObject jsonObject = new JSONObject();
            try {
                jsonObject.put("userid", userid);
                jsonObject.put("contractorid", processorid);
                jsonObject.put("limit", 10);
                jsonObject.put("offset", 0);

                JsonParser jsonParser = new JsonParser();
                Call<JsonObject> call = APIClient.getInterface().fabric_pendingdeliveries((JsonObject) jsonParser.parse(jsonObject.toString()));
                GetResult getResult = new GetResult();
                getResult.setMyListener(this);
                getResult.callForLogin(call, "fabric_pendingdeliveries");
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        else {

            AlertDialog alertDialog = new AlertDialog.Builder(MainActivity.this)
                    .setMessage("Please Check Your Internet Connection")
                    .setCancelable(false)
                    .setNegativeButton("Exit", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface arg1, int arg0) {
                            arg1.dismiss();
                        }
                    }).show();
            alertDialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(Color.RED);
        }
    }

    public void Scanning() {
        IntentIntegrator qrScan = new IntentIntegrator(this);
        qrScan.setDesiredBarcodeFormats(IntentIntegrator.QR_CODE); // Correct way to specify QR_CODE format
        qrScan.setPrompt("Scan a barcode");
        qrScan.setCameraId(0);  // Use front camera, if needed
        qrScan.setBeepEnabled(true);
        qrScan.setBarcodeImageEnabled(true);
        qrScan.initiateScan();

    }

    public void InbuiltReaderScanning() {
        Intent intent = new Intent(MainActivity.this, ScannerSelectionBarcodeActivity.class);
        intent.putExtra("myversionName", myversionName);
        intent.putExtra("name", username);
        intent.putExtra("userid", userid);
        intent.putExtra("processorid", processorid);
        startActivity(intent);
        finish();
    }

    public void UsbReaderScanning() {
        Intent intent = new Intent(MainActivity.this, BTScannerActivity.class);
        intent.putExtra("myversionName", myversionName);
        intent.putExtra("name", username);
        intent.putExtra("userid", userid);
        intent.putExtra("processorid", processorid);
        startActivity(intent);
        finish();
    }

    public void Budnle_QR_Scanning_USB_Reader() {
        Intent intent = new Intent(MainActivity.this, Bundle_QR_Scanner_USB_Reader_Activity.class);
        intent.putExtra("myversionName", myversionName);
        intent.putExtra("name", username);
        intent.putExtra("userid", userid);
        intent.putExtra("processorid", processorid);
        startActivity(intent);
        finish();
    }

    public void Multi_Budnle_QR_Scanning() {
        Intent intent = new Intent(MainActivity.this, Bundle_QR_Scanner_USB_Reader_Activity.class);
        intent.putExtra("myversionName", myversionName);
        intent.putExtra("name", username);
        intent.putExtra("userid", userid);
        intent.putExtra("processorid", processorid);
        startActivity(intent);
        finish();
    }

    public void PcwiseScanning() {
        IntentIntegrator qrScan = new IntentIntegrator(this);
        qrScan.setDesiredBarcodeFormats(IntentIntegrator.QR_CODE); // Correct way to specify QR_CODE format
        qrScan.setPrompt("Scan a barcode");
        qrScan.setCameraId(0);  // Use front camera, if needed
        qrScan.setBeepEnabled(true);
        qrScan.setBarcodeImageEnabled(true);
        qrScan.initiateScan();
    }

    private int getOrientation() {
        return getResources().getConfiguration().orientation;
    }

    @Override
    public void onBackPressed() {
        new AlertDialog.Builder(this)
            .setTitle("Exit")
            .setMessage("Are you sure you want to exit?")
            .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialogInterface, int which) {
                    finishAffinity();
                    finish();
                }
            })
            .setNegativeButton("No", null)
            .show();
    }

    public void getvalue() {
        txtScan.setText("Scan Bundle QR");
        txtUser.setText("Hello " + this.User);

        ModelClass modelClass = new ModelClass();
        modelClass.setmID(userid);
        mylist.add(modelClass);
        progress.setVisibility(View.GONE);
    }


    private void downloadFile(String sUrl) {
        MainActivity.custPrograssbar_new.prograssCreate(this);
        apkFileName = "production_qr_v"+app_version_name+".apk";
        File externalFilesDir = getExternalFilesDir(null);
        String previousApkFilePath = externalFilesDir.getAbsolutePath()+"/"+ apkFileName;
        File previousApkFile = new File(previousApkFilePath);
        if (previousApkFile.exists()) {
            if (previousApkFile.delete()) {
                // The previous APK file has been successfully deleted
            } else {
                // There was an issue deleting the previous APK file
            }
        }
        else {
            // The previous APK file does not exist or has already been deleted
        }

        String apkUrl = sUrl;

        DownloadManager downloadManager = (DownloadManager) getSystemService(Context.DOWNLOAD_SERVICE);

        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());

        DownloadManager.Request request = new DownloadManager.Request(Uri.parse(apkUrl));

        request.setDestinationInExternalFilesDir(this, null, apkFileName);
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
        request.setTitle("Downloading APK");
        request.setDescription("Download Production QR APK File..");
        final long downloadId = downloadManager.enqueue(request);

        receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                long id = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1);
                if (downloadId == id) {
                    // The download has completed, open and install the APK
                    MainActivity.custPrograssbar_new.closePrograssBar();

//                    checkForUpdates();
                    unregisterReceiver(receiver);
                    installApkFromStorage(context, apkFileName);
                }
            }
        };
        registerReceiver(receiver, new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));
    }


    public void installApkFromStorage(Context context, String apkFileName) {
        // Define the authority for your File Provider
        Uri apkUri = FileProvider.getUriForFile(context, "com.bipinexports.productionqr.fileprovider", new File(context.getExternalFilesDir(null), apkFileName));
        Intent installIntent = new Intent(Intent.ACTION_INSTALL_PACKAGE);
        installIntent.setData(apkUri);
        installIntent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        // Start the installation process

        try
        {
            context.startActivity(installIntent);
        }
        catch (ActivityNotFoundException e)
        {
            Toast.makeText(context, "APK NOT Open", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        Log.i("BipinExports", "result.getContents(): " + result.getContents());

        if (result != null) {
            if (result.getContents() == null) {
                Toast.makeText(this, "Result Not Found", Toast.LENGTH_LONG).show();
            } else {
                qrid = result.getContents();
                String[] arrayString = qrid.split("-");
                if (arrayString.length > 1) {
                    qrprefix = arrayString[0];
                    qrid = arrayString[1];
                }
                if (arrayString.length > 1 && qrprefix.equals("NE") && scan_requestcode != 1) {
                    session = new SessionManagement(getApplicationContext());
                    HashMap<String, String> user = session.getUserDetails();
                    processorid = user.get(SessionManagement.KEY_PROCESSOR_ID);
                    userid = user.get(SessionManagement.KEY_USER_ID);
                    isqc = user.get(SessionManagement.KEY_ISQC);

                    if (processorid.equals("95") || processorid.equals("96") || processorid.equals("105") || processorid.equals("106")) {
                        if (isOnline()) {
                            JSONObject jsonObject = new JSONObject();
                            try {
                                jsonObject.put("qrid", qrid);
                                jsonObject.put("userid", userid);
                                jsonObject.put("processorid", processorid);
                                jsonObject.put("version_name", myversionName);

                                JsonParser jsonParser = new JsonParser();
                                Call<JsonObject> call = APIClient.getInterface().piecewise_scan_and_update((JsonObject) jsonParser.parse(jsonObject.toString()));
                                GetResult getResult = new GetResult();
                                getResult.setMyListener(this);
                                getResult.callForLogin(call, "piecewise_scan_and_update");
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        } else {

                            AlertDialog alertDialog = new AlertDialog.Builder(MainActivity.this)
                                    .setMessage("Please Check Your Internet Connection")
                                    .setCancelable(false)
                                    .setNegativeButton("Exit", new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface arg1, int arg0) {
                                            arg1.dismiss();
                                        }
                                    }).show();
                            alertDialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(Color.RED);
                        }
                    } else if (isqc.equals("N")) {
                        if (isOnline()) {
                            JSONObject jsonObject = new JSONObject();
                            try {
                                jsonObject.put("qrid", qrid);
                                jsonObject.put("userid", userid);
                                jsonObject.put("processorid", processorid);
                                jsonObject.put("version_name", myversionName);

                                JsonParser jsonParser = new JsonParser();
                                //Call<JsonObject> call = APIClient.getInterface().getqrdata_queue((JsonObject) jsonParser.parse(jsonObject.toString()));
                                Call<JsonObject> call = APIClient.getInterface().getqrdata((JsonObject) jsonParser.parse(jsonObject.toString()));
                                GetResult getResult = new GetResult();
                                getResult.setMyListener(this);
                                getResult.callForLogin(call, "getqrdata");
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        } else {

                            AlertDialog alertDialog = new AlertDialog.Builder(MainActivity.this)
                                    .setMessage("Please Check Your Internet Connection")
                                    .setCancelable(false)
                                    .setNegativeButton("Exit", new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface arg1, int arg0) {
                                            arg1.dismiss();
                                        }
                                    }).show();
                            alertDialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(Color.RED);
                        }
                    } else if (isqc.equals("Y")) {
                        if (isOnline()) {
                            JSONObject jsonObject = new JSONObject();
                            try {
                                jsonObject.put("qrid", qrid);
                                jsonObject.put("userid", userid);
                                jsonObject.put("processorid", processorid);
                                jsonObject.put("version_name", myversionName);

                                JsonParser jsonParser = new JsonParser();
                                Call<JsonObject> call = APIClient.getInterface().getqcqrdata((JsonObject) jsonParser.parse(jsonObject.toString()));
                                GetResult getResult = new GetResult();
                                getResult.setMyListener(this);
                                getResult.callForLogin(call, "getqcqrdata");
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        } else {

                            AlertDialog alertDialog = new AlertDialog.Builder(MainActivity.this)
                                    .setMessage("Please Check Your Internet Connection")
                                    .setCancelable(false)
                                    .setNegativeButton("Exit", new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface arg1, int arg0) {
                                            arg1.dismiss();
                                        }
                                    }).show();
                            alertDialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(Color.RED);
                        }
                    }
                } else if (arrayString.length > 1 && arrayString[0].equals("MQR") && scan_requestcode != 1) {
                    session = new SessionManagement(getApplicationContext());
                    HashMap<String, String> user = session.getUserDetails();
                    processorid = user.get(SessionManagement.KEY_PROCESSOR_ID);
                    userid = user.get(SessionManagement.KEY_USER_ID);
                    username = user.get(SessionManagement.KEY_USER);
                    isqc = user.get(SessionManagement.KEY_ISQC);
                    if (isOnline()) {

                        JSONObject jsonObject = new JSONObject();
                        try {
                            jsonObject.put("qrid", qrid);
                            jsonObject.put("userid", userid);
                            jsonObject.put("username", username);
                            jsonObject.put("processorid", processorid);
                            jsonObject.put("activity", "ScanMachineQRActivity");

                            JsonParser jsonParser = new JsonParser();
                            Call<JsonObject> call = APIClient.getInterface().get_machine_details((JsonObject) jsonParser.parse(jsonObject.toString()));
                            GetResult getResult = new GetResult();
                            getResult.setMyListener(this);
                            getResult.callForLogin(call, "get_machine_details");
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    } else {

                        AlertDialog alertDialog = new AlertDialog.Builder(MainActivity.this)
                                .setMessage("Please Check Your Internet Connection")
                                .setCancelable(false)
                                .setNegativeButton("Exit", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface arg1, int arg0) {
                                        arg1.dismiss();
                                    }
                                }).show();
                        alertDialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(Color.RED);
                    }
                } else if (arrayString.length > 1 && qrprefix.equals("PC") && scan_requestcode == 1) {
                    session = new SessionManagement(getApplicationContext());
                    HashMap<String, String> user = session.getUserDetails();
                    processorid = user.get(SessionManagement.KEY_PROCESSOR_ID);
                    userid = user.get(SessionManagement.KEY_USER_ID);
                    isqc = user.get(SessionManagement.KEY_ISQC);
                    if (isOnline()) {

                        JSONObject jsonObject = new JSONObject();
                        try {
                            jsonObject.put("qrid", qrid);
                            jsonObject.put("userid", userid);
                            jsonObject.put("processorid", processorid);
                            jsonObject.put("version_name", myversionName);

                            JsonParser jsonParser = new JsonParser();
                            Call<JsonObject> call = APIClient.getInterface().piecewise_scan_and_update((JsonObject) jsonParser.parse(jsonObject.toString()));
                            GetResult getResult = new GetResult();
                            getResult.setMyListener(this);
                            getResult.callForLogin(call, "piecewise_scan_and_update");
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    } else {

                        AlertDialog alertDialog = new AlertDialog.Builder(MainActivity.this)
                                .setMessage("Please Check Your Internet Connection")
                                .setCancelable(false)
                                .setNegativeButton("Exit", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface arg1, int arg0) {
                                        arg1.dismiss();
                                    }
                                }).show();
                        alertDialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(Color.RED);
                    }
                } else {
                    String messsage = "Invalid QR. Contact Admin!";
                    if (scan_requestcode != 1) {
                        messsage = "Please Select Piecewise QR Menu. Contact Admin!";
                    }

                    AlertDialog alertDialog = new AlertDialog.Builder(MainActivity.this)
                            .setMessage(messsage)
                            .setCancelable(false)
                            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface arg1, int arg0) {
                                    Scanning();
                                }
                            }).show();
                }
            }
        }
    }

    public boolean isOnline() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        return cm.getActiveNetworkInfo() != null && cm.getActiveNetworkInfo().isConnectedOrConnecting();
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void callback(JsonObject result, String callNo) {
        try {

            if (callNo.equalsIgnoreCase("pendingdeliverycount")) {
                JSONObject jsonObject = new JSONObject(result.toString());
                String mStatus = jsonObject.optString("status");
                String message = jsonObject.optString("message");
                if (mStatus.equals("success")) {
                    accessory_pendingcount = jsonObject.optString("pendingcount");

                    if(parseInt(accessory_pendingcount)> 0  && mAdapter!=null)
                    {
                        for (MainImage_Data_Object obj : itemList) {
                            if ("Accessory Receipts Vendor Wise".equalsIgnoreCase(obj.getimage_name())) {
                                obj.setCount(accessory_pendingcount);
                                break;
                            }
                        }
                        mAdapter.notifyDataSetChanged();
                    }
                }
                else if (mStatus.equals("logout"))
                {
                    new AlertDialog.Builder(MainActivity.this)
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
                    new AlertDialog.Builder(MainActivity.this)
                        .setMessage(message)
                        .setCancelable(false)
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface arg1, int arg0) {
                                arg1.dismiss();
//                                session.logoutUser();
//                                finishAffinity();
//                                finish();
                            }
                        }).show();
                }
                MainActivity.custPrograssbar_new.closePrograssBar();
            }
            else if (callNo.equalsIgnoreCase("machine_service_verification_count")) {
                JSONObject jsonObject = new JSONObject(result.toString());
                String mStatus = jsonObject.optString("Result");
                String message = jsonObject.optString("ResponseMsg");

                if (mStatus.equals("true")) {
                    mac_service_verification_count = jsonObject.optString("mac_service_verification_count");
                    if(parseInt(mac_service_verification_count)> 0 && mAdapter!=null)
                    {
                        for (MainImage_Data_Object obj : itemList) {
                            if ("Machine Service Verification".equalsIgnoreCase(obj.getimage_name())) {
                                obj.setCount(mac_service_verification_count);
                                break;
                            }
                        }
                        mAdapter.notifyDataSetChanged();
                    }
                }
                MainActivity.custPrograssbar_new.closePrograssBar();
            }
            else if (callNo.equalsIgnoreCase("fetch_accessory_receipt_Details")) {
                JSONObject jsonObject = new JSONObject(result.toString());
                String mStatus = jsonObject.optString("status");
                String message = jsonObject.optString("message");
                if (mStatus.equals("success")) {
                        JSONObject deliveries = jsonObject.getJSONObject("data");
                        Intent intent = new Intent(MainActivity.this, Fabric_Receipt_Activity.class);
                        intent.putExtra("delivery", deliveries.toString());
                        intent.putExtra("processorid", processorid);
                        intent.putExtra("pendingcount", accessory_pendingcount);

                        startActivity(intent);
                        finish();
                }
                else if (mStatus.equals("logout"))
                {
                    new AlertDialog.Builder(MainActivity.this)
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
                    new AlertDialog.Builder(MainActivity.this)
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
                MainActivity.custPrograssbar_new.closePrograssBar();
            }
            else if (callNo.equalsIgnoreCase("pendingdeliveries_new")) {
                JSONObject jsonObject = new JSONObject(result.toString());
                String mStatus = jsonObject.optString("status");
                String message = jsonObject.optString("message");
                if (mStatus.equals("success")) {
                    JSONObject vendors = jsonObject.getJSONObject("data");
                    Intent intent = new Intent(MainActivity.this, Accessory_Receipt_Vendorwise_Activity.class);
                    intent.putExtra("vendors", vendors.toString());
                    intent.putExtra("processorid", processorid);
                    intent.putExtra("pendingcount", accessory_pendingcount);

                    startActivity(intent);
                    finish();
                }
                else if (mStatus.equals("logout"))
                {
                    new AlertDialog.Builder(MainActivity.this)
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
                    new AlertDialog.Builder(MainActivity.this)
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
                MainActivity.custPrograssbar_new.closePrograssBar();
            }
            else if (callNo.equalsIgnoreCase("machine_service_verification")) {
                JSONObject jsonObject = new JSONObject(result.toString());
                String mStatus = jsonObject.optString("status");
                String message = jsonObject.optString("message");
                if (mStatus.equals("success")) {
                        JSONObject mac_service_verifications = jsonObject.getJSONObject("data");
                        Intent intent = new Intent(MainActivity.this, Machine_Service_Verification_Activity.class);
                        intent.putExtra("mac_service_verification", mac_service_verifications.toString());
                        intent.putExtra("mac_service_verification_count", mac_service_verification_count);

                        startActivity(intent);
                        finish();
                }
                else if (mStatus.equals("logout"))
                {
                    new AlertDialog.Builder(MainActivity.this)
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
                    new AlertDialog.Builder(MainActivity.this)
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
                MainActivity.custPrograssbar_new.closePrograssBar();
            }
            else if (callNo.equalsIgnoreCase("get_machine_details")) {
                JSONObject json = new JSONObject(result.toString());
                String status = json.optString("status");
                String mess = json.optString("message");
              
                final Dialog dialog = new Dialog(MainActivity.this);
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);

                dialog.setContentView(R.layout.machine_popup_alert);
                TextView txtTitle = dialog.findViewById(R.id.txtTitle);
                TextView txtMachineno = dialog.findViewById(R.id.txtMachineno);
                TextView txtMcBrand = dialog.findViewById(R.id.txtMcBrand);
                TextView txtUnit = dialog.findViewById(R.id.txtUnit);
                TextView txtLine = dialog.findViewById(R.id.txtLine);

                TextView txtMcType = dialog.findViewById(R.id.txtMcType);
                TextView txtMcModel = dialog.findViewById(R.id.txtMcModel);
                TextView txtMcSpec = dialog.findViewById(R.id.txtMcSpec);
                TextView txtMcModelline = dialog.findViewById(R.id.txtMcModelline);
                TextView txtMcSpecline = dialog.findViewById(R.id.txtMcSpecline);
                TextView txtMcStatus = dialog.findViewById(R.id.txtMcStatus);

                Button btnScanAgain = dialog.findViewById(R.id.btnScanAgain);
                Button btn_Service = dialog.findViewById(R.id.btn_Service);
                Button btnCancel = dialog.findViewById(R.id.btnCancel);
                dialog.setCancelable(false);
                
                if (status.equals("success")  && mess.equals("Machine Details")) {
                    JSONObject jsonObj = json.getJSONObject("data");
                    String machineno = jsonObj.optString("machineno");
                    String machinemodelno = jsonObj.optString("machinemodelno");
                    String machinespec = jsonObj.optString("machinespec");
                    String modelyear = jsonObj.optString("modelyear");
                    String unitname = jsonObj.optString("unitname");
                    String recruitercode = jsonObj.optString("recruitercode");
                    String linename = jsonObj.optString("linename");
                    String machinebrand = jsonObj.optString("machinebrand");
                    String machinetype = jsonObj.optString("machinetype");
                    String machinecondition = jsonObj.optString("machinecondition");

                    txtTitle.setText(mess);
                    txtMachineno.setText(" : " + machineno);
                    txtMcBrand.setText(" : " + machinebrand);
                    txtUnit.setText(" : " + unitname);
                    txtLine.setText(" : " + linename);
                    txtMcType.setText(" : " + machinetype);
                    txtMcModel.setText(" : " + machinemodelno);
                    txtMcSpec.setText(" : " + machinespec);
                    txtMcStatus.setText(" : " + machinecondition);

                    if (machinemodelno != null && !machinemodelno.isEmpty()){
                        //DO WHATEVER YOU WANT OR LEAVE IT EMPTY
                        txtMcModel.setText(" : " + machinemodelno);
                        txtMcModel.setVisibility(View.VISIBLE);
                        txtMcModelline.setVisibility(View.VISIBLE);

                    }else {
                        //DO WHATEVER YOU WANT OR LEAVE IT EMPTY
                        txtMcModel.setVisibility(View.GONE);
                        txtMcModelline.setVisibility(View.GONE);
                    }

                    if (machinespec != null && !machinespec.isEmpty()){
                        //DO WHATEVER YOU WANT OR LEAVE IT EMPTY
                        txtMcSpec.setText(" : " + machinespec);
                        txtMcSpec.setVisibility(View.VISIBLE);
                        txtMcSpecline.setVisibility(View.VISIBLE);

                    }else {
                        //DO WHATEVER YOU WANT OR LEAVE IT EMPTY
                        txtMcSpec.setVisibility(View.GONE);
                        txtMcSpecline.setVisibility(View.GONE);
                    }

                    btnScanAgain.setText("Scan");
                    btnScanAgain.setVisibility(View.VISIBLE);
                    dialog.show();

                    btnScanAgain.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            dialog.dismiss();
                            if(isOnline()) {

                                mProgressDialog = new ProgressDialog(MainActivity.this);
                                mProgressDialog.setMessage(getString(R.string.loading));
                                mProgressDialog.setIndeterminate(true);
                                mProgressDialog.show();
                                JSONObject jsonObject = new JSONObject();
                                try {
                                    jsonObject.put("qrid", qrid);
                                    jsonObject.put("userid", userid);
                                    jsonObject.put("processorid", processorid);

                                    JsonParser jsonParser = new JsonParser();
                                    Call<JsonObject> call = APIClient.getInterface().update_machine_details((JsonObject) jsonParser.parse(jsonObject.toString()));
                                    GetResult getResult = new GetResult();
                                    getResult.setMyListener(MainActivity.this);
                                    getResult.callForLogin(call, "update_machine_details");
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                            else {

                                AlertDialog alertDialog = new AlertDialog.Builder(MainActivity.this)
                                        .setMessage("Please Check Your Internet Connection")
                                        .setCancelable(false)
                                        .setNegativeButton("Exit", new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface arg1, int arg0) {
                                                arg1.dismiss();
                                            }
                                        }).show();
                                alertDialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(Color.RED);
                            }
                        }
                    });

                    btnCancel.setText("Exit");
                    btnCancel.setVisibility(View.VISIBLE);
                    btnCancel.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            dialog.dismiss();
                        }
                    });

                    if(machinecondition.equals("Working"))
                    {
                        btn_Service.setText("Service");
                        btn_Service.setVisibility(View.VISIBLE);
                        dialog.show();
                        btn_Service.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                dialog.dismiss();

                                if(isOnline()) {

                                    mProgressDialog = new ProgressDialog(MainActivity.this);
                                    mProgressDialog.setMessage(getString(R.string.loading));
                                    mProgressDialog.setIndeterminate(true);
                                    mProgressDialog.show();
                                    JSONObject jsonObject = new JSONObject();
                                    try {
                                        jsonObject.put("qrid", qrid);
                                        jsonObject.put("userid", userid);
                                        jsonObject.put("processorid", processorid);

                                        JsonParser jsonParser = new JsonParser();
                                        Call<JsonObject> call = APIClient.getInterface().machine_service_details((JsonObject) jsonParser.parse(jsonObject.toString()));
                                        GetResult getResult = new GetResult();
                                        getResult.setMyListener(MainActivity.this);
                                        getResult.callForLogin(call, "machine_service_details");
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }
                                else {

                                    AlertDialog alertDialog = new AlertDialog.Builder(MainActivity.this)
                                            .setMessage("Please Check Your Internet Connection")
                                            .setCancelable(false)
                                            .setNegativeButton("Exit", new DialogInterface.OnClickListener() {
                                                public void onClick(DialogInterface arg1, int arg0) {
                                                    arg1.dismiss();
                                                }
                                            }).show();
                                    alertDialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(Color.RED);
                                }
                            }
                        });
                    }
                    else if(machinecondition.equals("Inservice"))
                    {
                        btn_Service.setText("InService");
                        btn_Service.setVisibility(View.VISIBLE);
                        dialog.show();
                        btn_Service.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                            dialog.dismiss();
                            AlertDialog alertDialog = new AlertDialog.Builder(MainActivity.this)
                                .setMessage("Pending for Machine Service")
                                .setCancelable(false)
                                .setPositiveButton("OK", new DialogInterface.OnClickListener()
                                {
                                    public void onClick(DialogInterface arg1, int arg0)
                                    {
                                        Scanning();
                                    }
                                }).show();
                            }
                        });
                    }

                }
                
                else if(status.equals("false"))
                {
                    JSONObject jsonObj = json.getJSONObject("data");
                    String machineno = jsonObj.optString("machineno");
                    String machinemodelno = jsonObj.optString("machinemodelno");
                    String machinespec = jsonObj.optString("machinespec");
                    String modelyear = jsonObj.optString("modelyear");
                    String unitname = jsonObj.optString("unitname");
                    String recruitercode = jsonObj.optString("recruitercode");
                    String linename = jsonObj.optString("linename");
                    String machinebrand = jsonObj.optString("machinebrand");
                    String machinetype = jsonObj.optString("machinetype");
                    String machinecondition = jsonObj.optString("machinecondition");

                    txtTitle.setText(mess);
                    txtMachineno.setText(" : " + machineno);
                    txtMcBrand.setText(" : " + machinebrand);
                    txtUnit.setText(" : " + unitname);
                    txtLine.setText(" : " + linename);
                    txtMcType.setText(" : " + machinetype);
                    txtMcModel.setText(" : " + machinemodelno);
                    txtMcSpec.setText(" : " + machinespec);
                    txtMcStatus.setText(" : " + machinecondition);

                    if (machinemodelno != null && !machinemodelno.isEmpty()){
                        //DO WHATEVER YOU WANT OR LEAVE IT EMPTY
                        txtMcModel.setText(" : " + machinemodelno);
                        txtMcModel.setVisibility(View.VISIBLE);
                        txtMcModelline.setVisibility(View.VISIBLE);

                    }else {
                        //DO WHATEVER YOU WANT OR LEAVE IT EMPTY
                        txtMcModel.setVisibility(View.GONE);
                        txtMcModelline.setVisibility(View.GONE);
                    }

                    if (machinespec != null && !machinespec.isEmpty()){
                        //DO WHATEVER YOU WANT OR LEAVE IT EMPTY
                        txtMcSpec.setText(" : " + machinespec);
                        txtMcSpec.setVisibility(View.VISIBLE);
                        txtMcSpecline.setVisibility(View.VISIBLE);

                    }else {
                        //DO WHATEVER YOU WANT OR LEAVE IT EMPTY
                        txtMcSpec.setVisibility(View.GONE);
                        txtMcSpecline.setVisibility(View.GONE);
                    }

                    btnCancel.setText("Exit");
                    btnCancel.setVisibility(View.VISIBLE);
                    btnCancel.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            dialog.dismiss();
                        }
                    });

                    if(machinecondition.equals("Working"))
                    {
                        btn_Service.setText("Service");
                        btn_Service.setVisibility(View.VISIBLE);
                        dialog.show();
                        btn_Service.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                dialog.dismiss();

                                if(isOnline()) {

                                    mProgressDialog = new ProgressDialog(MainActivity.this);
                                    mProgressDialog.setMessage(getString(R.string.loading));
                                    mProgressDialog.setIndeterminate(true);
                                    mProgressDialog.show();
                                    JSONObject jsonObject = new JSONObject();
                                    try {
                                        jsonObject.put("qrid", qrid);
                                        jsonObject.put("userid", userid);
                                        jsonObject.put("processorid", processorid);

                                        JsonParser jsonParser = new JsonParser();
                                        Call<JsonObject> call = APIClient.getInterface().machine_service_details((JsonObject) jsonParser.parse(jsonObject.toString()));
                                        GetResult getResult = new GetResult();
                                        getResult.setMyListener(MainActivity.this);
                                        getResult.callForLogin(call, "machine_service_details");
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }
                                else {

                                    AlertDialog alertDialog = new AlertDialog.Builder(MainActivity.this)
                                            .setMessage("Please Check Your Internet Connection")
                                            .setCancelable(false)
                                            .setNegativeButton("Exit", new DialogInterface.OnClickListener() {
                                                public void onClick(DialogInterface arg1, int arg0) {
                                                    arg1.dismiss();
                                                }
                                            }).show();
                                    alertDialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(Color.RED);
                                }
                            }
                        });
                    }
                    else if(machinecondition.equals("Inservice"))
                    {
                        btn_Service.setText("InService");
                        btn_Service.setVisibility(View.VISIBLE);
                        dialog.show();
                        btn_Service.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                dialog.dismiss();
                                AlertDialog alertDialog = new AlertDialog.Builder(MainActivity.this)
                                        .setMessage("Pending for Machine Service")
                                        .setCancelable(false)
                                        .setPositiveButton("OK", new DialogInterface.OnClickListener()
                                        {
                                            public void onClick(DialogInterface arg1, int arg0)
                                            {
                                                Scanning();
                                            }
                                        }).show();
                            }
                        });
                    }
                }
                else {
                    AlertDialog alertDialog = new AlertDialog.Builder(MainActivity.this)
                            .setMessage(mess)
                            .setCancelable(false)
                            .setPositiveButton("OK", new DialogInterface.OnClickListener()
                            {
                                public void onClick(DialogInterface arg1, int arg0)
                                {
                                    Scanning();
                                }
                            }).show();
                }
                MainActivity.custPrograssbar_new.closePrograssBar();
            }
            else if (callNo.equalsIgnoreCase("update_machine_details")) {
                mProgressDialog.hide();
                JSONObject json = new JSONObject(result.toString());
                String status = json.optString("status");
                String mess = json.optString("message");
                if (status.equals("success")) {
                    mProgressDialog.hide();
                    AlertDialog alertDialog = new AlertDialog.Builder(MainActivity.this)
                            .setMessage(mess)
                            .setCancelable(false)
                            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface arg1, int arg0) {
                                    Scanning();
                                }
                            }).show();
                }
                else {
                    AlertDialog alertDialog = new AlertDialog.Builder(MainActivity.this)
                            .setMessage(mess)
                            .setCancelable(false)
                            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface arg1, int arg0) {
                                    Scanning();
                                }
                            }).show();
                }
                MainActivity.custPrograssbar_new.closePrograssBar();
            }
            else if (callNo.equalsIgnoreCase("machine_service_details")) {
                JSONObject json = new JSONObject(result.toString());
                String status = json.optString("status");
                String mess = json.optString("message");
                if (status.equals("success")) {
                    mProgressDialog.hide();
                    AlertDialog alertDialog = new AlertDialog.Builder(MainActivity.this)
                        .setMessage(mess)
                        .setCancelable(false)
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface arg1, int arg0) {
                                Scanning();
                            }
                        }).show();
                }
                else {
                    AlertDialog alertDialog = new AlertDialog.Builder(MainActivity.this)
                        .setMessage(mess)
                        .setCancelable(false)
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface arg1, int arg0) {
                                Scanning();
                            }
                        }).show();
                }
                MainActivity.custPrograssbar_new.closePrograssBar();
            }
            else if (callNo.equalsIgnoreCase("getqrdata")) {
                JSONObject json = new JSONObject(result.toString());
                String status = json.optString("status");
                String mess = json.optString("message");

                if(status.equals("version_check"))
                {
                    JSONObject jsonObj = json.getJSONObject("data");
                    apkurl = jsonObj.optString("apk_url");
                    app_version_name = jsonObj.optString("app_version_name");

                    AlertDialog alertDialog = new AlertDialog.Builder(MainActivity.this)
                            .setMessage(mess)
                            .setCancelable(false)
                            .setPositiveButton("OK", new DialogInterface.OnClickListener()
                            {
                                public void onClick(DialogInterface arg1, int arg0)
                                {
                                    arg1.dismiss();
                                    downloadFile(apkurl);
                                }
                            }).show();
                }
                else
                {
                    apkFileName = "production_qr_v"+myversionName+".apk";
                    File externalFilesDir = getExternalFilesDir(null);
                    String previousApkFilePath = externalFilesDir.getAbsolutePath()+"/"+ apkFileName;
                    File previousApkFile = new File(previousApkFilePath);
                    if (previousApkFile.exists()) {
                        if (previousApkFile.delete()) {
                            // The previous APK file has been successfully deleted
                        } else {
                            // There was an issue deleting the previous APK file
                        }
                    }
                    else {
                        // The previous APK file does not exist or has already been deleted
                    }

                    final Dialog dialog = new Dialog(MainActivity.this);
                    dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                    dialog.setContentView(R.layout.popup_alert);
                    TextView txtTitle = dialog.findViewById(R.id.txtTitle);
                    TextView txtJobNo = dialog.findViewById(R.id.txtJobNo);
                    TextView txtShipCode = dialog.findViewById(R.id.txtShipCode);
                    TextView txtStyle = dialog.findViewById(R.id.txtStyle);
                    TextView txtStyleRef = dialog.findViewById(R.id.text_Color);
                    TextView txtPart = dialog.findViewById(R.id.txtPart);
                    TextView txtsize = dialog.findViewById(R.id.txtsize);
                    TextView txtBundleQty = dialog.findViewById(R.id.txtBundleQty);
                    TextView txtBunldeNo = dialog.findViewById(R.id.txtBunldeNo);
                    TextView txtColor = dialog.findViewById(R.id.txtColor);
                    Button btnOk = dialog.findViewById(R.id.btnOk);
                    Button btnCancel = dialog.findViewById(R.id.btnCancel);
                    dialog.setCancelable(false);

                    if (mess.equals("Already Scanned")) {
                        JSONObject jsonObj = json.getJSONObject("data");
                        String date = jsonObj.optString("scanneddate");
                        String joborderno = jsonObj.optString("joborderno");
                        String shipcode = jsonObj.optString("shipcode");
                        String size = jsonObj.optString("size");
                        String color = jsonObj.optString("color");
                        String bundleno = jsonObj.optString("bundleno");
                        String bundleqty = jsonObj.optString("bundleqty");
                        String style = jsonObj.optString("stylename");
                        String styleref = jsonObj.optString("styleno");
                        String processorcode = jsonObj.optString("processorcode");
                        String processorname = jsonObj.optString("processorname");
                        String partname = jsonObj.optString("partname");

                        txtTitle.setText(mess + " on " + date + " by " + processorcode + " " + processorname);
                        txtJobNo.setText(" : " + joborderno);
                        txtShipCode.setText(" : " + shipcode);
                        txtStyle.setText(" : " + style);
                        txtStyleRef.setText(" : " + styleref);
                        txtPart.setText(" : " + partname);
                        txtsize.setText(" : " + size);
                        txtBundleQty.setText(" : " + bundleqty);
                        txtBunldeNo.setText(" : " + bundleno);
                        txtColor.setText(" : " + color);
                        btnOk.setText("OK");
                        btnOk.setVisibility(View.VISIBLE);
                        dialog.show();
                        btnOk.setOnClickListener(new View.OnClickListener()
                        {
                            @Override
                            public void onClick(View v)
                            {
                                dialog.dismiss();
                                Scanning();
                            }
                        });

                    }
                    else if (mess.equals("TO BE SCANNED")) {

                        JSONObject jsonObj = json.getJSONObject("data");
                        String joborderno = jsonObj.optString("joborderno");
                        String shipcode = jsonObj.optString("shipcode");
                        String size = jsonObj.optString("size");
                        String color = jsonObj.optString("color");
                        String bundleno = jsonObj.optString("bundleno");
                        String bundleqty = jsonObj.optString("bundleqty");
                        String style = jsonObj.optString("stylename");
                        String styleref = jsonObj.optString("styleno");
                        String partname = jsonObj.optString("partname");

                        txtTitle.setText(mess);
                        txtJobNo.setText(" : " + joborderno);
                        txtShipCode.setText(" : " + shipcode);
                        txtStyle.setText(" : " + style);
                        txtStyleRef.setText(" : " + styleref);
                        txtPart.setText(" : " + partname);
                        txtsize.setText(" : " + size);
                        txtBundleQty.setText(" : " + bundleqty);
                        txtBunldeNo.setText(" : " + bundleno);
                        txtColor.setText(" : " + color);
                        btnCancel.setText("Cancel");
                        btnCancel.setVisibility(View.VISIBLE);
                        btnCancel.setOnClickListener(new View.OnClickListener()
                        {
                            @Override
                            public void onClick(View v)
                            {
                                dialog.dismiss();

                                Scanning();
                            }
                        });
                        btnOk.setText("Confirm");
                        btnOk.setVisibility(View.VISIBLE);
                        btnOk.setOnClickListener(new View.OnClickListener()
                        {
                            @Override
                            public void onClick(View v)
                            {
                                dialog.dismiss();

                                if(isOnline()) {

                                    mProgressDialog = new ProgressDialog(MainActivity.this);
                                    mProgressDialog.setMessage(getString(R.string.loading));
                                    mProgressDialog.setIndeterminate(true);
                                    mProgressDialog.show();

                                    JSONObject jsonObject = new JSONObject();
                                    try {
                                        jsonObject.put("qrid", qrid);
                                        jsonObject.put("userid", userid);
                                        jsonObject.put("processorid", processorid);

                                        JsonParser jsonParser = new JsonParser();
                                        //Call<JsonObject> call = APIClient.getInterface().updatescanstatus_queue((JsonObject) jsonParser.parse(jsonObject.toString()));
                                        Call<JsonObject> call = APIClient.getInterface().updatescanstatus((JsonObject) jsonParser.parse(jsonObject.toString()));
                                        GetResult getResult = new GetResult();
                                        getResult.setMyListener(MainActivity.this);
                                        getResult.callForLogin(call, "updatescanstatus");
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }
                                else {

                                    AlertDialog alertDialog = new AlertDialog.Builder(MainActivity.this)
                                            .setMessage("Please Check Your Internet Connection")
                                            .setCancelable(false)
                                            .setNegativeButton("Exit", new DialogInterface.OnClickListener() {
                                                public void onClick(DialogInterface arg1, int arg0) {
                                                    arg1.dismiss();
                                                }
                                            }).show();
                                    alertDialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(Color.RED);
                                }
                            }
                        });
                        dialog.show();
                    }
                    else
                    {
                        AlertDialog alertDialog = new AlertDialog.Builder(MainActivity.this)
                                .setMessage(mess)
                                .setCancelable(false)
                                .setPositiveButton("OK", new DialogInterface.OnClickListener()
                                {
                                    public void onClick(DialogInterface arg1, int arg0)
                                    {
                                        Scanning();
                                    }
                                }).show();
                    }
                }
                MainActivity.custPrograssbar_new.closePrograssBar();
            }
            else if (callNo.equalsIgnoreCase("updatescanstatus")) 
            {
                JSONObject json = new JSONObject(result.toString());
                String status = json.optString("status");
                String mess = json.optString("message");

                if (status.equals("success")) {
                    mProgressDialog.hide();
                    Scanning();
                }
                else {
                    AlertDialog alertDialog = new AlertDialog.Builder(MainActivity.this)
                        .setMessage(mess)
                        .setCancelable(false)
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface arg1, int arg0) {
                                Scanning();
                            }
                        }).show();
                }
                MainActivity.custPrograssbar_new.closePrograssBar();
            }
            else if (callNo.equalsIgnoreCase("getqcqrdata")) 
            {
                JSONObject json = new JSONObject(result.toString());
                String status = json.optString("status");
                String mess = json.optString("message");

                if(status.equals("version_check"))
                {
                    JSONObject jsonObj = json.getJSONObject("data");
                    apkurl = jsonObj.optString("apk_url");
                    app_version_name = jsonObj.optString("version_name");

                    AlertDialog alertDialog = new AlertDialog.Builder(MainActivity.this)
                            .setMessage(mess)
                            .setCancelable(false)
                            .setPositiveButton("OK", new DialogInterface.OnClickListener()
                            {
                                public void onClick(DialogInterface arg1, int arg0)
                                {
                                    arg1.dismiss();
                                    downloadFile(apkurl);
                                }
                            }).show();
                }
                else {

                    apkFileName = "production_qr_v"+myversionName+".apk";
                    File externalFilesDir = getExternalFilesDir(null);
                    String previousApkFilePath = externalFilesDir.getAbsolutePath()+"/"+ apkFileName;
                    File previousApkFile = new File(previousApkFilePath);
                    if (previousApkFile.exists()) {
                        if (previousApkFile.delete()) {
                            // The previous APK file has been successfully deleted
                        } else {
                            // There was an issue deleting the previous APK file
                        }
                    }
                    else {
                        // The previous APK file does not exist or has already been deleted
                    }

                    final Dialog dialog = new Dialog(MainActivity.this);
                    dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                    dialog.setContentView(R.layout.popup_alert);
                    TextView txtTitle = dialog.findViewById(R.id.txtTitle);
                    TextView txtJobNo = dialog.findViewById(R.id.txtJobNo);
                    TextView txtShipCode = dialog.findViewById(R.id.txtShipCode);
                    TextView txtStyle = dialog.findViewById(R.id.txtStyle);
                    TextView txtStyleRef = dialog.findViewById(R.id.text_Color);
                    TextView txtPart = dialog.findViewById(R.id.txtPart);
                    TextView txtsize = dialog.findViewById(R.id.txtsize);
                    TextView txtBundleQty = dialog.findViewById(R.id.txtBundleQty);
                    TextView txtBunldeNo = dialog.findViewById(R.id.txtBunldeNo);
                    TextView txtColor = dialog.findViewById(R.id.txtColor);
                    Button btnOk = dialog.findViewById(R.id.btnOk);
                    Button btnCancel = dialog.findViewById(R.id.btnCancel);
                    dialog.setCancelable(false);

                    if (mess.equals("TO BE SCANNED")) {

                        JSONObject jsonObj = json.getJSONObject("data");
                        String joborderno = jsonObj.optString("joborderno");
                        String shipcode = jsonObj.optString("shipcode");
                        String size = jsonObj.optString("size");
                        String color = jsonObj.optString("color");
                        String bundleno = jsonObj.optString("bundleno");
                        String bundleqty = jsonObj.optString("bundleqty");
                        String style = jsonObj.optString("stylename");
                        String styleref = jsonObj.optString("styleno");
                        String partname = jsonObj.optString("partname");

                        txtTitle.setText(mess);
                        txtJobNo.setText(" : " + joborderno);
                        txtShipCode.setText(" : " + shipcode);
                        txtStyle.setText(" : " + style);
                        txtStyleRef.setText(" : " + styleref);
                        txtPart.setText(" : " + partname);
                        txtsize.setText(" : " + size);
                        txtBundleQty.setText(" : " + bundleqty);
                        txtBunldeNo.setText(" : " + bundleno);
                        txtColor.setText(" : " + color);
                        btnCancel.setText("Cancel");
                        btnCancel.setVisibility(View.VISIBLE);
                        btnCancel.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                dialog.dismiss();
                                Scanning();
                            }
                        });
                        btnOk.setText("Confirm");
                        btnOk.setVisibility(View.VISIBLE);
                        btnOk.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                dialog.dismiss();

                                if(isOnline()) {

                                    mProgressDialog = new ProgressDialog(MainActivity.this);
                                    mProgressDialog.setMessage(getString(R.string.loading));
                                    mProgressDialog.setIndeterminate(true);
                                    mProgressDialog.show();

                                    JSONObject jsonObject = new JSONObject();
                                    try {
                                        jsonObject.put("qrid", qrid);
                                        jsonObject.put("userid", userid);
                                        jsonObject.put("processorid", processorid);

                                        JsonParser jsonParser = new JsonParser();
                                        Call<JsonObject> call = APIClient.getInterface().updateqcscanstatus((JsonObject) jsonParser.parse(jsonObject.toString()));
                                        GetResult getResult = new GetResult();
                                        getResult.setMyListener(MainActivity.this);
                                        getResult.callForLogin(call, "updateqcscanstatus");
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }
                                else {

                                    AlertDialog alertDialog = new AlertDialog.Builder(MainActivity.this)
                                            .setMessage("Please Check Your Internet Connection")
                                            .setCancelable(false)
                                            .setNegativeButton("Exit", new DialogInterface.OnClickListener() {
                                                public void onClick(DialogInterface arg1, int arg0) {
                                                    arg1.dismiss();
                                                }
                                            }).show();
                                    alertDialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(Color.RED);
                                }
                            }
                        });
                        dialog.show();
                    } else {
                        AlertDialog alertDialog = new AlertDialog.Builder(MainActivity.this)
                                .setMessage(mess)
                                .setCancelable(false)
                                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface arg1, int arg0) {
                                        Scanning();
                                    }
                                }).show();
                    }
                }
                MainActivity.custPrograssbar_new.closePrograssBar();
            }
            else if (callNo.equalsIgnoreCase("updateqcscanstatus")) 
            {
                JSONObject json = new JSONObject(result.toString());
                String status = json.optString("status");
                String mess = json.optString("message");
                if (status.equals("success")) {
                    mProgressDialog.hide();
                    AlertDialog alertDialog = new AlertDialog.Builder(MainActivity.this)
                        .setMessage(mess)
                        .setCancelable(false)
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface arg1, int arg0) {
                                Scanning();
                            }
                        }).show();
                }
                else {
                    AlertDialog alertDialog = new AlertDialog.Builder(MainActivity.this)
                        .setMessage(mess)
                        .setCancelable(false)
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface arg1, int arg0) {
                                Scanning();
                            }
                        }).show();
                }
                MainActivity.custPrograssbar_new.closePrograssBar();
            }
            else if (callNo.equalsIgnoreCase("get_piecewise_qrdata")) {
                JSONObject json = new JSONObject(result.toString());
                String status = json.optString("status");
                String mess = json.optString("message");

                final Dialog dialog = new Dialog(MainActivity.this);
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.setContentView(R.layout.piecewise_popup_alert);
                TextView txtTitle = dialog.findViewById(R.id.txtTitle);
                TextView txtJobNo = dialog.findViewById(R.id.txtJobNo);
                TextView txtShipCode = dialog.findViewById(R.id.txtShipCode);
                TextView txtStyle = dialog.findViewById(R.id.txtStyle);
                TextView txtStyleRef = dialog.findViewById(R.id.text_Color);
                TextView txtPart = dialog.findViewById(R.id.txtPart);
                TextView txtsize = dialog.findViewById(R.id.txtsize);
                TextView txtBundleQty = dialog.findViewById(R.id.txtBundleQty);
                TextView txtBunldeNo = dialog.findViewById(R.id.txtBunldeNo);
                TextView txtPieceNo = dialog.findViewById(R.id.txtPieceNo);

                TextView txtColor = dialog.findViewById(R.id.txtColor);
                Button btnOk = dialog.findViewById(R.id.btnOk);
                Button btnCancel = dialog.findViewById(R.id.btnCancel);
                dialog.setCancelable(false);

                if (mess.equals("Already Scanned")) {
                    JSONObject jsonObj = json.getJSONObject("data");
                    String date = jsonObj.optString("scanneddate");
                    String joborderno = jsonObj.optString("joborderno");
                    String shipcode = jsonObj.optString("shipcode");
                    String size = jsonObj.optString("size");
                    String color = jsonObj.optString("color");
                    String bundleno = jsonObj.optString("bundleno");
                    String bundleqty = jsonObj.optString("bundleqty");
                    String pc_no = jsonObj.optString("pc_no");
                    String style = jsonObj.optString("stylename");
                    String styleref = jsonObj.optString("styleno");
                    String processorcode = jsonObj.optString("processorcode");
                    String processorname = jsonObj.optString("processorname");
                    String partname = jsonObj.optString("partname");

                    txtTitle.setText(mess + " on " + date + " by " + processorcode + " " + processorname);
                    txtJobNo.setText(" : " + joborderno);
                    txtShipCode.setText(" : " + shipcode);
                    txtStyle.setText(" : " + style);
                    txtStyleRef.setText(" : " + styleref);
                    txtPart.setText(" : " + partname);
                    txtsize.setText(" : " + size);
                    txtBundleQty.setText(" : " + bundleqty);
                    txtBunldeNo.setText(" : " + bundleno);
                    txtPieceNo.setText(" : " + pc_no);
                    txtColor.setText(" : " + color);
                    btnOk.setText("OK");
                    btnOk.setVisibility(View.VISIBLE);
                    dialog.show();
                    btnOk.setOnClickListener(new View.OnClickListener()
                    {
                        @Override
                        public void onClick(View v)
                        {
                            dialog.dismiss();
                            scan_requestcode =1;
                            Scanning();
                        }
                    });

                }
                else if (mess.equals("TO BE SCANNED")) {

                    JSONObject jsonObj = json.getJSONObject("data");
                    String joborderno = jsonObj.optString("joborderno");
                    String shipcode = jsonObj.optString("shipcode");
                    String size = jsonObj.optString("size");
                    String color = jsonObj.optString("color");
                    String bundleno = jsonObj.optString("bundleno");
                    String bundleqty = jsonObj.optString("bundleqty");
                    String style = jsonObj.optString("stylename");
                    String styleref = jsonObj.optString("styleno");
                    String partname = jsonObj.optString("partname");

                    txtTitle.setText(mess);
                    txtJobNo.setText(" : " + joborderno);
                    txtShipCode.setText(" : " + shipcode);
                    txtStyle.setText(" : " + style);
                    txtStyleRef.setText(" : " + styleref);
                    txtPart.setText(" : " + partname);
                    txtsize.setText(" : " + size);
                    txtBundleQty.setText(" : " + bundleqty);
                    txtBunldeNo.setText(" : " + bundleno);
                    txtColor.setText(" : " + color);

                    if(isOnline()) {

                        mProgressDialog = new ProgressDialog(MainActivity.this);
                        mProgressDialog.setMessage(getString(R.string.loading));
                        mProgressDialog.setIndeterminate(true);
                        mProgressDialog.show();

                        JSONObject jsonObject = new JSONObject();
                        try {
                            jsonObject.put("qrid", qrid);
                            jsonObject.put("userid", userid);
                            jsonObject.put("processorid", processorid);

                            JsonParser jsonParser = new JsonParser();
                            Call<JsonObject> call = APIClient.getInterface().piecewise_updatescanstatus((JsonObject) jsonParser.parse(jsonObject.toString()));
                            GetResult getResult = new GetResult();
                            getResult.setMyListener(MainActivity.this);
                            getResult.callForLogin(call, "piecewise_updatestatus");
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                    else {

                        AlertDialog alertDialog = new AlertDialog.Builder(MainActivity.this)
                                .setMessage("Please Check Your Internet Connection")
                                .setCancelable(false)
                                .setNegativeButton("Exit", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface arg1, int arg0) {
                                        arg1.dismiss();
                                    }
                                }).show();
                        alertDialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(Color.RED);
                    }
                }
                else
                {
                    AlertDialog alertDialog = new AlertDialog.Builder(MainActivity.this)
                            .setMessage(mess)
                            .setCancelable(false)
                            .setPositiveButton("OK", new DialogInterface.OnClickListener()
                            {
                                public void onClick(DialogInterface arg1, int arg0)
                                {
                                    scan_requestcode =1;
                                    Scanning();
                                }
                            }).show();
                }
                MainActivity.custPrograssbar_new.closePrograssBar();
            }
            else if (callNo.equalsIgnoreCase("piecewise_updatestatus"))
            {
                JSONObject json = new JSONObject(result.toString());
                String status = json.optString("status");
                String mess = json.optString("message");
                if (status.equals("success")) {
                   mProgressDialog.hide();
                    scan_requestcode =1;
                    Scanning();
                }
                else {
                    AlertDialog alertDialog = new AlertDialog.Builder(MainActivity.this)
                            .setMessage(mess)
                            .setCancelable(false)
                            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface arg1, int arg0) {
                                    scan_requestcode =1;
                                    Scanning();
                                }
                            }).show();
                }
                MainActivity.custPrograssbar_new.closePrograssBar();
            }
            else if (callNo.equalsIgnoreCase("piecewise_scan_and_update")) {
                JSONObject json = new JSONObject(result.toString());
                String status = json.optString("status");
                String mess = json.optString("message");

                if(status.equals("version_check"))
                {
                    JSONObject jsonObj = json.getJSONObject("data");
                    apkurl = jsonObj.optString("apk_url");
                    app_version_name = jsonObj.optString("version_name");

                    AlertDialog alertDialog = new AlertDialog.Builder(MainActivity.this)
                            .setMessage(mess)
                            .setCancelable(false)
                            .setPositiveButton("OK", new DialogInterface.OnClickListener()
                            {
                                public void onClick(DialogInterface arg1, int arg0)
                                {
                                    arg1.dismiss();
                                    downloadFile(apkurl);
                                }
                            }).show();
                }
                else {
                    if (mess.equals("Already Scanned"))
                    {

                        apkFileName = "production_qr_v"+myversionName+".apk";
                        File externalFilesDir = getExternalFilesDir(null);
                        String previousApkFilePath = externalFilesDir.getAbsolutePath()+"/"+ apkFileName;
                        File previousApkFile = new File(previousApkFilePath);
                        if (previousApkFile.exists()) {
                            if (previousApkFile.delete()) {
                                // The previous APK file has been successfully deleted
                            } else {
                                // There was an issue deleting the previous APK file
                            }
                        }
                        else {
                            // The previous APK file does not exist or has already been deleted
                        }
                        JSONObject jsonObj = json.getJSONObject("data");
                        String date = jsonObj.optString("scanneddate");
                        String joborderno = jsonObj.optString("joborderno");
                        String shipcode = jsonObj.optString("shipcode");
                        String size = jsonObj.optString("size");
                        String color = jsonObj.optString("color");
                        String bundleno = jsonObj.optString("bundleno");
                        String bundleqty = jsonObj.optString("bundleqty");
                        String pc_no = jsonObj.optString("pc_no");
                        String style = jsonObj.optString("stylename");
                        String styleref = jsonObj.optString("styleno");
                        String processorcode = jsonObj.optString("processorcode");
                        String processorname = jsonObj.optString("processorname");
                        String partname = jsonObj.optString("partname");

                        final Dialog dialog = new Dialog(MainActivity.this);
                        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                        dialog.setContentView(R.layout.piecewise_popup_alert);
                        TextView txtTitle = dialog.findViewById(R.id.txtTitle);
                        TextView txtJobNo = dialog.findViewById(R.id.txtJobNo);
                        TextView txtShipCode = dialog.findViewById(R.id.txtShipCode);
                        TextView txtStyle = dialog.findViewById(R.id.txtStyle);
                        TextView txtStyleRef = dialog.findViewById(R.id.text_Color);
                        TextView txtPart = dialog.findViewById(R.id.txtPart);
                        TextView txtsize = dialog.findViewById(R.id.txtsize);
                        TextView txtBundleQty = dialog.findViewById(R.id.txtBundleQty);
                        TextView txtBunldeNo = dialog.findViewById(R.id.txtBunldeNo);
                        TextView txtPieceNo = dialog.findViewById(R.id.txtPieceNo);

                        TextView txtColor = dialog.findViewById(R.id.txtColor);
                        Button btnOk = dialog.findViewById(R.id.btnOk);
                        Button btnCancel = dialog.findViewById(R.id.btnCancel);
                        dialog.setCancelable(false);

                        txtTitle.setText(mess + " on " + date + " by " + processorcode + " " + processorname);
                        txtJobNo.setText(" : " + joborderno);
                        txtShipCode.setText(" : " + shipcode);
                        txtStyle.setText(" : " + style);
                        txtStyleRef.setText(" : " + styleref);
                        txtPart.setText(" : " + partname);
                        txtsize.setText(" : " + size);
                        txtBundleQty.setText(" : " + bundleqty);
                        txtBunldeNo.setText(" : " + bundleno);
                        txtPieceNo.setText(" : " + pc_no);
                        txtColor.setText(" : " + color);
                        btnOk.setText("OK");
                        btnOk.setVisibility(View.VISIBLE);
                        dialog.show();
                        btnOk.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                dialog.dismiss();
                                scan_requestcode = 1;
                                PcwiseScanning();
                            }
                        });
                    }
                    else if (mess.equals("Scanned successfully")) {
                        PcwiseScanning();
                    }
                    else {
                        AlertDialog alertDialog = new AlertDialog.Builder(MainActivity.this)
                                .setMessage(mess)
                                .setCancelable(false)
                                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface arg1, int arg0) {
                                        scan_requestcode = 1;
                                        PcwiseScanning();
                                    }
                                }).show();
                    }
                }
                MainActivity.custPrograssbar_new.closePrograssBar();
            }
            else if (callNo.equalsIgnoreCase("fetch_version_details"))
            {
                JSONObject json = new JSONObject(result.toString());
                String status = json.optString("status");
                String mess = json.optString("message");
                if (status.equals("success"))
                {
                    JSONObject jsonObj = json.getJSONObject("data");
                    current_versionName = jsonObj.optString("version_name");
                    current_versionCode = parseInt(jsonObj.optString("version_code"));
                    apkurl = jsonObj.optString("apk_url");

                    if(current_versionName.equals(myversionName))
                    {
                        Log.e("Bipin","ok" );
                    }
                    else {
                        Log.e("Bipin"," not ok" );
                        if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE ) != PackageManager.PERMISSION_GRANTED) {
                            requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 212);
                        }
                        else
                        {
                            AlertDialog alertDialog = new AlertDialog.Builder(MainActivity.this)
                                    .setMessage("Your App Version is Low Please Update ")
                                    .setCancelable(false)
                                    .setNegativeButton("OK", new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface arg1, int arg0) {
                                            arg1.dismiss();
                                            downloadFile(apkurl);
                                        }
                                    }).show();
                            alertDialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(Color.RED);
                        }
                    }
                }
                else {
                    AlertDialog alertDialog = new AlertDialog.Builder(MainActivity.this)
                            .setMessage(mess)
                            .setCancelable(false)
                            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface arg1, int arg0) {
                                    Scanning();
                                }
                            }).show();

                }
                MainActivity.custPrograssbar_new.closePrograssBar();
            }
            else if (callNo.equalsIgnoreCase("pending_fabric_receipts")) {
                JSONObject jsonObject = new JSONObject(result.toString());
                String mStatus = jsonObject.optString("status");
                String message = jsonObject.optString("message");
                if (mStatus.equals("success"))
                {
                    fabric_pendingcount = jsonObject.optString("pendingcount");
                    if (fabric_pendingcount == null || fabric_pendingcount.isEmpty())
                    {
                        System.out.println("fabric_pendingcount is null or empty");
                    }
                    else {
                        if(parseInt(fabric_pendingcount)> 0 && mAdapter!=null)
                        {
                            for (MainImage_Data_Object obj : itemList) {
                                if ("Fabric Receipts  Vendor Wise".equalsIgnoreCase(obj.getimage_name())) {
                                    obj.setCount(fabric_pendingcount);
                                    break;
                                }
                            }
                            mAdapter.notifyDataSetChanged();
                        }
                    }
                }
                else if (mStatus.equals("logout"))
                {
                    new AlertDialog.Builder(MainActivity.this)
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
                    new AlertDialog.Builder(MainActivity.this)
                            .setMessage(message)
                            .setCancelable(false)
                            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface arg1, int arg0) {
                                    arg1.dismiss();
                                }
                            }).show();
                }
                MainActivity.custPrograssbar_new.closePrograssBar();
            }
            else if (callNo.equalsIgnoreCase("fabric_pendingdeliveries")) {
                JSONObject jsonObject = new JSONObject(result.toString());
                String mStatus = jsonObject.optString("status");
                String message = jsonObject.optString("message");
                if (mStatus.equals("success")) {
                    JSONObject deliveries = jsonObject.getJSONObject("data");
                    Intent intent = new Intent(MainActivity.this, Fabric_Receipt_Activity.class);
                    intent.putExtra("delivery", deliveries.toString());
                    intent.putExtra("processorid", processorid);
                    intent.putExtra("pendingcount", fabric_pendingcount);
                    startActivity(intent);
                    finish();
                }
                else if (mStatus.equals("logout"))
                {
                    new AlertDialog.Builder(MainActivity.this)
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
                    new AlertDialog.Builder(MainActivity.this)
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
                MainActivity.custPrograssbar_new.closePrograssBar();
            }
            else if (callNo.equalsIgnoreCase("MainImageList")) {
                JSONObject jsonObject = new JSONObject(result.toString());
                String mStatus = jsonObject.optString("status");
                String message = jsonObject.optString("message");
                String image_count = jsonObject.optString("image_count");

                if (mStatus.equals("success") ) {

                    if(Integer.parseInt(image_count) > 0)
                    {
                        recyclerView = findViewById(R.id.recyclerView);
                        itemList.add(new MainImage_Data_Object(MainImage_Data_Object.TYPE_LOGO, "", "", "", ""));

                        JSONObject userDetailsObject = jsonObject.getJSONObject("data").getJSONObject("user_details");

                        Map<String, List<JSONObject>> groupedData = new LinkedHashMap<>();
                        Iterator<String> keys = userDetailsObject.keys();
                        while (keys.hasNext()) {
                            String key = keys.next();
                            JSONObject obj = userDetailsObject.getJSONObject(key);
                            String title = obj.optString("title_name", "");
                            groupedData.putIfAbsent(title, new ArrayList<>());
                            groupedData.get(title).add(obj);
                        }

                        for (String title : groupedData.keySet()) {
                            itemList.add(new MainImage_Data_Object(MainImage_Data_Object.TYPE_TITLE, title, "", "", ""));
                            for (JSONObject obj : groupedData.get(title)) {
                                itemList.add(new MainImage_Data_Object(
                                        MainImage_Data_Object.TYPE_IMAGE,
                                        title,
                                        obj.optString("imgpath", ""),
                                        obj.optString("imagename", ""),
                                        obj.optString("activityname", "")
                                ));
                            }
                        }

                        GridLayoutManager layoutManager = new GridLayoutManager(this, 3);
                        layoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
                            @Override
                            public int getSpanSize(int position) {
                                int type = itemList.get(position).getType();
                                return (type == MainImage_Data_Object.TYPE_IMAGE) ? 1 : 3; // logo and title span full width
                            }
                        });

                        recyclerView.setLayoutManager(layoutManager);
                        mAdapter = new MainImage_Adapter(this, itemList, (item, position) -> {
//                            Toast.makeText(this, "Clicked: " + item.getimage_name(), Toast.LENGTH_SHORT).show();
                            Log.e("Bipin","item.getimage_name() :" +item.getimage_name());
                            if (item.getimage_name().equals("Scan Bundle QR")) {
                                Scanning();
                            }
                            else if (item.getimage_name().equals("Bundle QR Scan USB Reader")) {
                                scan_requestcode = 1;
                                Budnle_QR_Scanning_USB_Reader();
                            }
                            else if (item.getimage_name().equals("Multi Bundle QR Scan")) {
                                Intent intent = new Intent(MainActivity.this, Multiple_Bundle_QR_Scan_Activity.class);
                                intent.putExtra("myversionName", myversionName);
                                intent.putExtra("name", username);
                                intent.putExtra("userid", userid);
                                intent.putExtra("processorid", processorid);
                                startActivity(intent);
                                finish();
                            }
                            else if (item.getimage_name().equals("Multi Bundle QR USB Reader")) {

                                Intent intent = new Intent(MainActivity.this, Scanner_Multiple_Bundle_QR_Activity.class);
                                intent.putExtra("myversionName", myversionName);
                                intent.putExtra("name", username);
                                intent.putExtra("userid", userid);
                                intent.putExtra("processorid", processorid);
                                startActivity(intent);
                                finish();
                            }
                            else if (item.getimage_name().equals("Lay QR Scan")) {

                                Intent intent = new Intent(MainActivity.this, Laywise_Bundle_QR_Scan_Activity.class);
                                intent.putExtra("myversionName", myversionName);
                                intent.putExtra("name", username);
                                intent.putExtra("userid", userid);
                                intent.putExtra("processorid", processorid);
                                startActivity(intent);
                                finish();
                            }
                            else if (item.getimage_name().equals("Lay Wise QR USB Reader")) {
                                Intent intent = new Intent(MainActivity.this, Laywise_Multiple_Bundle_QR_Scanner_Activity.class);
                                intent.putExtra("myversionName", myversionName);
                                intent.putExtra("name", username);
                                intent.putExtra("userid", userid);
                                intent.putExtra("processorid", processorid);
                                startActivity(intent);
                                finish();
                            }
                            else if (item.getimage_name().equals("Relax Rolls Scan"))
                            {
                                Intent intent = new Intent(MainActivity.this, Scan_Inward_Rolls_Activity.class);
                                intent.putExtra("myversionName", myversionName);
                                intent.putExtra("name", username);
                                intent.putExtra("userid", userid);
                                intent.putExtra("processorid", processorid);
                                startActivity(intent);
                                finish();
                            }
                            else if (item.getimage_name().equals("Roll Delivery Scan"))
                            {
                                Intent intent = new Intent(MainActivity.this, Scan_Relaxed_Rolls_Activity.class);
                                intent.putExtra("myversionName", myversionName);
                                intent.putExtra("name", username);
                                intent.putExtra("userid", userid);
                                intent.putExtra("processorid", processorid);
                                startActivity(intent);
                                finish();
                            }
                            else if (item.getimage_name().equals("Machine Service Verification"))
                            {
                                if(processorid.equals("95")|| processorid.equals("96") || processorid.equals("105") || processorid.equals("106"))
                                {
                                    AlertDialog alertDialog = new AlertDialog.Builder(MainActivity.this)
                                            .setMessage("Permissions not granted to view this page.\n Contact Admin!")
                                            .setCancelable(false)
                                            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                                public void onClick(DialogInterface arg1, int arg0) {
                                                }
                                            }).show();
                                }
                                else {
                                    if (parseInt(mac_service_verification_count) > 0) {
                                        MainActivity.custPrograssbar_new.prograssCreate(this);
                                        fetch_machine_service_verification();
                                    } else {
                                        AlertDialog alertDialog = new AlertDialog.Builder(MainActivity.this)
                                                .setMessage("No Data Found!")
                                                .setCancelable(false)
                                                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                                    public void onClick(DialogInterface arg1, int arg0) {
                                                    }
                                                }).show();
                                    }
                                }
                            }
                            else if (item.getimage_name().equals("Machine Scan/Service"))
                            {
                                if(processorid.equals("95")|| processorid.equals("96") || processorid.equals("105") || processorid.equals("106"))
                                {
                                    AlertDialog alertDialog = new AlertDialog.Builder(MainActivity.this)
                                            .setMessage("Permissions not granted to view this page.\n Contact Admin!")
                                            .setCancelable(false)
                                            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                                public void onClick(DialogInterface arg1, int arg0) {
                                                }
                                            }).show();
                                }
                                else {
                                    scan_requestcode = 0;
                                    Scanning();
                                }
                            }
                            else if (item.getimage_name().equals("Employee Operation Mapping"))
                            {
                                Intent intent = new Intent(MainActivity.this, Select_Employee_Detail_Activity.class);
                                intent.putExtra("type", "New");
                                intent.putExtra("myversionName", myversionName);
                                intent.putExtra("name", username);
                                intent.putExtra("userid", userid);
                                intent.putExtra("processorid", processorid);
                                startActivity(intent);
                                finish();
                            }
                            else if (item.getimage_name().equals("Bundle QR Mapping"))
                            {
                                Intent intent = new Intent(MainActivity.this, In_Employee_Operation_Mapping_Activity.class);
                                intent.putExtra("type", "Bundle_mapp");
                                intent.putExtra("myversionName", myversionName);
                                intent.putExtra("name", username);
                                intent.putExtra("userid", userid);
                                intent.putExtra("processorid", processorid);
                                startActivity(intent);
                                finish();
                            }
                            else if (item.getimage_name().equals("Employee Bundle Mapping"))
                            {
                                Intent intent = new Intent(MainActivity.this, In_Employee_Bundle_Scan_Activity.class);
                                intent.putExtra("type", "New");
                                intent.putExtra("myversionName", myversionName);
                                intent.putExtra("name", username);
                                intent.putExtra("userid", userid);
                                intent.putExtra("processorid", processorid);
                                startActivity(intent);
                                finish();
                            }
                            else if (item.getimage_name().equals("Job Summary"))
                            {
                                if(processorid.equals("95")|| processorid.equals("96") || processorid.equals("105") || processorid.equals("106"))
                                {
                                    AlertDialog alertDialog = new AlertDialog.Builder(MainActivity.this)
                                            .setMessage("Permissions not granted to view this page.\n Contact Admin!")
                                            .setCancelable(false)
                                            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                                public void onClick(DialogInterface arg1, int arg0) {
                                                }
                                            }).show();
                                }
                                else {
                                    if (isqc.equals("N")) {
                                        Intent intent = new Intent(MainActivity.this, SelectJobSummaryActivity.class);
                                        startActivity(intent);
                                        finish();
                                    } else {
                                        AlertDialog alertDialog = new AlertDialog.Builder(MainActivity.this)
                                                .setMessage("Only Show Nidhvi Tec Job Summary")
                                                .setCancelable(false)
                                                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                                    public void onClick(DialogInterface arg1, int arg0) {
                                                    }
                                                }).show();
                                    }
                                }
                            }
                            else if (item.getimage_name().equals("Overall Output Summary"))
                            {
                                if (isqc.equals("N")) {
                                    Intent intent = new Intent(MainActivity.this, OuputSummaryActivity.class);
                                    startActivity(intent);
                                    finish();
                                } else {
                                    AlertDialog alertDialog = new AlertDialog.Builder(MainActivity.this)
                                            .setMessage("Only Show Nidhvi Tec Job Summary")
                                            .setCancelable(false)
                                            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                                public void onClick(DialogInterface arg1, int arg0) {

                                                }
                                            }).show();
                                }
                            }
                            else if (item.getimage_name().equals("Date Wise Output Summary"))
                            {
                                if (isqc.equals("N")) {
                                    Intent intent = new Intent(MainActivity.this, DatewiseOutputActivity.class);
                                    intent.putExtra("type", "New");
                                    startActivity(intent);
                                    finish();
                                } else {
                                    AlertDialog alertDialog = new AlertDialog.Builder(MainActivity.this)
                                            .setMessage("Only Show Nidhvi Tec Job Summary")
                                            .setCancelable(false)
                                            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                                public void onClick(DialogInterface arg1, int arg0) {

                                                }
                                            }).show();
                                }
                            }
                            else if (item.getimage_name().equals("Accessory Receipts Vendor Wise"))
                            {
                                if (parseInt(accessory_pendingcount) > 0)
                                {
                                    if (isqc.equals("N"))
                                    {
                                        MainActivity.custPrograssbar_new.prograssCreate(this);
                                        pendingdeliveries_new_Details();
                                    }
                                    else
                                    {
                                        AlertDialog alertDialog = new AlertDialog.Builder(MainActivity.this)
                                                .setMessage("Only Show Nidhvi Tec Job Summary")
                                                .setCancelable(false)
                                                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                                    public void onClick(DialogInterface arg1, int arg0) {
                                                    }
                                                }).show();
                                    }
                                }
                                else {
                                    AlertDialog alertDialog = new AlertDialog.Builder(MainActivity.this)
                                            .setMessage("No Data Found!")
                                            .setCancelable(false)
                                            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                                public void onClick(DialogInterface arg1, int arg0) {
                                                }
                                            }).show();
                                }
                            }
                            else if (item.getimage_name().equals("Fabric Receipts  Vendor Wise"))
                            {
                                if (parseInt(fabric_pendingcount) > 0)
                                {
                                    if (isqc.equals("N"))
                                    {
                                        MainActivity.custPrograssbar_new.prograssCreate(this);
                                        fabric_pendingdeliveries();
                                    }
                                    else
                                    {
                                        AlertDialog alertDialog = new AlertDialog.Builder(MainActivity.this)
                                                .setMessage("Only Show Nidhvi Tec Job Summary")
                                                .setCancelable(false)
                                                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                                    public void onClick(DialogInterface arg1, int arg0) {
                                                    }
                                                }).show();
                                    }
                                }
                                else {
                                    AlertDialog alertDialog = new AlertDialog.Builder(MainActivity.this)
                                            .setMessage("No Data Found!")
                                            .setCancelable(false)
                                            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                                public void onClick(DialogInterface arg1, int arg0) {
                                                }
                                            }).show();
                                }
                            }
                            else if (item.getimage_name().equals("Machine Scan Reports"))
                            {
                                Intent intent = new Intent(MainActivity.this, Machine_QR_Scanview_Activity.class);
                                intent.putExtra("myversionName", myversionName);
                                intent.putExtra("name", username);
                                intent.putExtra("userid", userid);
                                intent.putExtra("processorid", processorid);
                                startActivity(intent);
                                finish();
                            }
                            else if (item.getimage_name().equals("Sharp Tool Scan Reports"))
                            {
                                Intent intent = new Intent(MainActivity.this, Sharptool_QR_Scanview_Activity.class);
                                intent.putExtra("myversionName", myversionName);
                                intent.putExtra("name", username);
                                intent.putExtra("userid", userid);
                                intent.putExtra("processorid", processorid);
                                startActivity(intent);
                                finish();
                            }
                            else if (item.getimage_name().equals("Relax Rolls USB Reader"))
                            {
                                Intent intent = new Intent(MainActivity.this, Relax_Roll_Scanner_USB_Reader_Activity.class);
                                intent.putExtra("myversionName", myversionName);
                                intent.putExtra("name", username);
                                intent.putExtra("userid", userid);
                                intent.putExtra("processorid", processorid);
                                startActivity(intent);
                                finish();
                            }
                            else if (item.getimage_name().equals("Roll Delivery USB Reader"))
                            {
                                Intent intent = new Intent(MainActivity.this,Roll_Delivery_USB_Reader_Activity.class);
                                intent.putExtra("myversionName", myversionName);
                                intent.putExtra("name", username);
                                intent.putExtra("userid", userid);
                                intent.putExtra("processorid", processorid);
                                startActivity(intent);
                                finish();
                            }

                        });
                        recyclerView.setAdapter(mAdapter);

                        if(parseInt(fabric_pendingcount)> 0)
                        {
                            for (MainImage_Data_Object obj : itemList) {
                                if ("Fabric Receipts  Vendor Wise".equalsIgnoreCase(obj.getimage_name())) {
                                    obj.setCount(fabric_pendingcount);
                                    break;
                                }
                            }
                            mAdapter.notifyDataSetChanged();
                        }
                        if(parseInt(accessory_pendingcount)> 0)
                        {
                            for (MainImage_Data_Object obj : itemList) {
                                if ("Accessory Receipts Vendor Wise".equalsIgnoreCase(obj.getimage_name())) {
                                    obj.setCount(accessory_pendingcount);
                                    break;
                                }
                            }
                            mAdapter.notifyDataSetChanged();
                        }
                        if(parseInt(mac_service_verification_count)> 0)
                        {
                            for (MainImage_Data_Object obj : itemList) {
                                if ("Machine Service Verification".equalsIgnoreCase(obj.getimage_name())) {
                                    obj.setCount(mac_service_verification_count);
                                    break;
                                }
                            }
                            mAdapter.notifyDataSetChanged();
                        }
                    }
                }
                else if (mStatus.equals("nodatafound")) {
                    if(mAdapter == null)
                    {
                        // Do nothing
                    }
                    new AlertDialog.Builder(MainActivity.this)
                            .setMessage(message)
                            .setCancelable(false)
                            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface arg1, int arg0) {
                                    arg1.dismiss();
                                }
                            }).show();
                }
                else
                {
                    new AlertDialog.Builder(MainActivity.this)
                            .setMessage(message)
                            .setCancelable(false)
                            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface arg1, int arg0) {
                                    arg1.dismiss();
                                }
                            }).show();
                }
                MainActivity.custPrograssbar_new.closePrograssBar();
            }
        }
        catch (Exception e) {
        }
    }

    private void fetch_image_cat_Details() {

        MainActivity.custPrograssbar_new.prograssCreate(this);
        session = new SessionManagement(getApplicationContext());
        HashMap<String, String> user = session.getUserDetails();
        processorid = user.get(SessionManagement.KEY_PROCESSOR_ID);
        userid = user.get(SessionManagement.KEY_USER_ID);
        username = user.get(SessionManagement.KEY_USER);

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("userid", userid);
            jsonObject.put("processorid", processorid);
            jsonObject.put("username", username);
            jsonObject.put("sessionid",session);
            jsonObject.put("version_name",myversionName);

            JsonParser jsonParser = new JsonParser();
            Call<JsonObject> call;
            call = APIClient.getInterface().fetch_mainimage_details((JsonObject) jsonParser.parse(jsonObject.toString()));
            GetResult getResult = new GetResult();
            getResult.setMyListener(this);
            getResult.callForLogin(call, "MainImageList");
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
