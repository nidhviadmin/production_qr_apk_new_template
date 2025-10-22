package com.bipinexports.productionqr.Activity;

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
import android.os.Handler;
import android.os.Looper;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import com.bipinexports.productionqr.APIClient;
import com.bipinexports.productionqr.GetResult;
import com.bipinexports.productionqr.R;
import com.bipinexports.productionqr.SessionManagement;
import com.google.android.material.checkbox.MaterialCheckBox;
import com.google.android.material.snackbar.Snackbar;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

import retrofit2.Call;

public class Relax_Roll_Scanner_USB_Reader_Activity extends AppCompatActivity implements View.OnClickListener, GetResult.MyListener {

    EditText txtScanData;
    ImageView imageView;
    TextView txtUser;
    SessionManagement session;

    ProgressDialog mProgressDialog;

    String User, unitid;
    String qrid;

    String myversionName;

    String apkFileName;
    BroadcastReceiver receiver;
    String app_version_name;

    String processorid;
    String userid;
    String username;

    TextView txtJobNo, txtShipCode, text_Color, txtStyle, text_partname,text_rackname, text_rollno, text_rollcode, text_weight, txtTitle, text_4point;
    LinearLayout liner_bundle_details, linear_layout_btn, layout_text_4point_check, layout_text_4point;
    JSONObject rollinfo;

    MaterialCheckBox checkYes;
    MaterialCheckBox checkNo;

    Button btnOk;
    Button btnCancel, btn_scan_again;
    int is_checked = 0;
    Boolean multiclick = false;

    public static CustPrograssbar custPrograssbar;

    String rackname, relaxed_by, joborderno, shipcode, colors, rollno, rollcode, weight, styleno, partname, relaxed_on, str_is_checked;

    Handler scanHandler = new Handler(Looper.getMainLooper());
    Runnable scanRunnable;
    long SCAN_DELAY = 300; // time to wait after last character input

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        setContentView(R.layout.activity_relax_roll_usb_scanner);

        imageView = findViewById(R.id.imgd);
        imageView.setOnClickListener(this);
        txtUser = findViewById(R.id.txtUser);
        custPrograssbar = new CustPrograssbar();

        session = new SessionManagement(getApplicationContext());
        HashMap<String, String> user = session.getUserDetails();
        this.User = user.get(SessionManagement.KEY_USER);
        unitid = user.get(SessionManagement.KEY_UNITID);
        txtUser.setText("Hello " + this.User);

        processorid = getIntent().getStringExtra("processorid");
        userid = getIntent().getStringExtra("userid");
        username = getIntent().getStringExtra("name");

        versioncode();
        hideKeyboard();
        imageView.setOnClickListener(this);

        txtJobNo = findViewById(R.id.txtJobNo);
        txtShipCode = findViewById(R.id.txtShipCode);
        text_Color = findViewById(R.id.text_Color);
        txtStyle = findViewById(R.id.txtStyle);
        txtTitle = findViewById(R.id.txtTitle);

        text_partname = findViewById(R.id.text_partname);
        text_rackname = findViewById(R.id.text_rackname);
        text_rollno = findViewById(R.id.text_rollno);
        text_rollcode = findViewById(R.id.text_rollcode);
        text_weight = findViewById(R.id.text_weight);
        text_4point = findViewById(R.id.text_4point);

        liner_bundle_details = findViewById(R.id.liner_bundle_details);

        layout_text_4point_check = findViewById(R.id.layout_text_4point_check);
        layout_text_4point = findViewById(R.id.layout_text_4point);
        linear_layout_btn = findViewById(R.id.linear_layout_btn);

        btnOk = findViewById(R.id.btnOk);
        btnCancel = findViewById(R.id.btnCancel);
        btn_scan_again = findViewById(R.id.btn_scan_again);

        checkYes = findViewById(R.id.Point_Yes);
        checkNo = findViewById(R.id.Point_No);

        is_checked = 0;
        liner_bundle_details.setVisibility(View.GONE);
        linear_layout_btn.setVisibility(View.GONE);
        layout_text_4point.setVisibility(View.GONE);
        txtTitle.setVisibility(View.GONE);

        checkNo.isChecked();
        checkYes.setChecked(false);

        checkYes.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                is_checked = 1;
                if (checkNo.isChecked()) checkNo.setChecked(false);
            } else {
                // Prevent unchecking if no other is selected
                if (!checkNo.isChecked()) {
                    is_checked = 1;
                    checkYes.setChecked(true); // force re-check
                }
            }
        });

        checkNo.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                is_checked = 0;
                if (checkYes.isChecked()) checkYes.setChecked(false);
            } else {
                // Prevent unchecking if no other is selected
                if (!checkYes.isChecked()) {
                    is_checked = 0;
                    checkNo.setChecked(true); // force re-check
                }
            }
        });

        txtScanData = findViewById(R.id.txtScanData);
        txtScanData.requestFocus();
        txtScanData.setInputType(InputType.TYPE_NULL); // hardware‑scanner mode

        txtScanData.setOnEditorActionListener((v, actionId, event) -> {
            String data = txtScanData.getText().toString().trim();
            if (data.isEmpty()) return true;

            if (multiclick) {
                multiclick = false;
                UpdatescanData();
            } else {
                handleScanData(data);
                hideExtraViews();
            }

            // Clear for next scan
            txtScanData.setText("");
            txtScanData.requestFocus();

            // (Optional) hide keyboard even though we’re TYPE_NULL
            InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
            if (imm != null) imm.hideSoftInputFromWindow(txtScanData.getWindowToken(), 0);

            return true;  // we consumed the action
        });


//        txtScanData = findViewById(R.id.txtScanData);
//        txtScanData.setFocusable(true);
//        txtScanData.setFocusableInTouchMode(true);
//        txtScanData.requestFocus();
//
//
//        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
//        hideKeyboard();
//
//        txtScanData.setOnKeyListener((v, keyCode, event) -> {
//            if (event.getAction() == KeyEvent.ACTION_DOWN &&
//                    (keyCode == KeyEvent.KEYCODE_ENTER || keyCode == KeyEvent.KEYCODE_TAB)) {
//
//                String data = txtScanData.getText().toString().trim();
//                boolean isFocusable = txtScanData.isFocusable();
//                boolean hasFocus = txtScanData.hasFocus();
//
//                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
//                if (imm != null) {
//                    imm.hideSoftInputFromWindow(txtScanData.getWindowToken(), 0);
//                }
//
//                if (multiclick) {
//                    txtScanData.setText("");
//                    txtScanData.postDelayed(() -> {
//                        txtScanData.requestFocus();  // re-focus
//                        txtScanData.setFocusable(true);
//                    }, 100);
//                    multiclick = false;
//                    UpdatescanData();
//                } else {
//                    if (!data.isEmpty()) {
//                        handleScanData(data);
//                        txtScanData.setText("");
//                        is_checked = 0;
//                        liner_bundle_details.setVisibility(View.GONE);
//                        linear_layout_btn.setVisibility(View.GONE);
//                        layout_text_4point.setVisibility(View.GONE);
//                        txtTitle.setVisibility(View.GONE);
//                        checkNo.setChecked(false);
//                        checkYes.setChecked(false);
//                    } else {
//                        txtScanData.setFocusable(true);
//                        txtScanData.setText("");
//                    }
//
//                    txtScanData.postDelayed(() -> {
//                        txtScanData.setText("");
//                        txtScanData.requestFocus(); // retain focus for next scan
//                        txtScanData.setFocusable(true);
//                    }, 100);
//                }
//
//                return true;
//            }
//            return false;
//        });

        btnOk.setOnClickListener(this);
        btnCancel.setOnClickListener(this);
        btn_scan_again.setOnClickListener(this);
    }

    private void hideExtraViews() {
        is_checked = 0;
        liner_bundle_details.setVisibility(View.GONE);
        linear_layout_btn.setVisibility(View.GONE);
        layout_text_4point.setVisibility(View.GONE);
        txtTitle.setVisibility(View.GONE);
        checkNo.setChecked(false);
        checkYes.setChecked(false);
    }

    public void hideKeyboard() {
        View view = getCurrentFocus();
        if (view == null) {
            view = new View(this); // fallback if no view has focus
        }
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
    }

    private void handleScanData(String qrdata)
    {
        qrid = qrdata;
        String[] arrayString = qrid.split("-");

        String qrprefix = null;

        if(arrayString.length > 1) {
            qrprefix = arrayString[0];
        }
        if (qrprefix.equals("RIN") || qrprefix.equals("RL"))
        {
            if (isOnline())
            {
                session = new SessionManagement(getApplicationContext());
                HashMap<String, String> user = session.getUserDetails();
                processorid = user.get(SessionManagement.KEY_PROCESSOR_ID);
                userid = user.get(SessionManagement.KEY_USER_ID);

                //                    progressBar.setVisibility(View.VISIBLE);
                Relax_Roll_Scanner_USB_Reader_Activity.custPrograssbar.prograssCreate(this);

                JSONObject jsonObject = new JSONObject();
                try {
                    jsonObject.put("rollid", qrid);
                    jsonObject.put("scanned_by", userid);
                    jsonObject.put("processorid", processorid);
                    jsonObject.put("version_name", myversionName);


                    JsonParser jsonParser = new JsonParser();
                    Call<JsonObject> call = APIClient.getInterface().getinwardrolldetails((JsonObject) jsonParser.parse(jsonObject.toString()));
                    GetResult getResult = new GetResult();
                    getResult.setMyListener(this);
                    getResult.callForLogin(call, "getjobdetails");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } else {
                AlertDialog alertDialog = new AlertDialog.Builder(Relax_Roll_Scanner_USB_Reader_Activity.this)
                        .setMessage("Please Check Your Internet Connection")
                        .setCancelable(false)
                        .setNegativeButton("OK", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface arg1, int arg0) {
                                arg1.dismiss();

                            }
                        }).show();
                alertDialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(Color.RED);
            }
        }
        else {
            Handler h = new Handler(Looper.getMainLooper());
            h.post(new Runnable() {
                public void run() {
                    new AlertDialog.Builder(Relax_Roll_Scanner_USB_Reader_Activity.this)
                            .setTitle("Error")
                            .setMessage("Invalid Roll QR.\nContact Admin!")
                            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            }).show();
                }
            });
        }
    }


    private void versioncode() {
        if(isOnline()) {
            Context context = this;
            PackageManager manager = context.getPackageManager();
            try {
                PackageInfo info = manager.getPackageInfo(context.getPackageName(), 0);
                myversionName = info.versionName;
            }
            catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
                myversionName = "Unknown-01";
            }
        }
        else {

            AlertDialog alertDialog = new AlertDialog.Builder(Relax_Roll_Scanner_USB_Reader_Activity.this)
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

    @Override
    public void onClick(View v) {
        if (isOnline())
        {
            switch (v.getId()) {
                case R.id.imgd:
                    PopupMenu popup = new PopupMenu(Relax_Roll_Scanner_USB_Reader_Activity.this, imageView);
                    popup.getMenuInflater().inflate(R.menu.menu_chgpswd, popup.getMenu());
                    popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                        public boolean onMenuItemClick(MenuItem item) {
                            if (item.getItemId() == R.id.logout) {
                                session.logoutUser();
                                finish();
                            }
                            return true;
                        }
                    });
                    popup.show();
                    break;
                case R.id.btnOk:
                    UpdatescanData();
                    break;
                case R.id.btn_scan_again:
                    Scan_again();
                    break;
                case R.id.btnCancel:
                    onBackPressed();
                    break;
                default:
                    break;
            }
        }
        else {
            Snackbar snackbar = Snackbar
                    .make(v, "No internet connection!", Snackbar.LENGTH_LONG);
            snackbar.show();
        }
    }

    public boolean isOnline() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        return cm.getActiveNetworkInfo() != null && cm.getActiveNetworkInfo().isConnectedOrConnecting();
    }


    private void downloadFile(String sUrl) {

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
        mProgressDialog = new ProgressDialog(Relax_Roll_Scanner_USB_Reader_Activity.this);
        mProgressDialog.setMessage(getString(R.string.loading));
        mProgressDialog.setIndeterminate(true);
        mProgressDialog.show();
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
                    mProgressDialog.hide();

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

    private void UpdatescanData() {
        if (isOnline()){
            session = new SessionManagement(getApplicationContext());
            HashMap<String, String> user = session.getUserDetails();
            processorid = user.get(SessionManagement.KEY_PROCESSOR_ID);
            userid = user.get(SessionManagement.KEY_USER_ID);
            String userid = user.get(SessionManagement.KEY_USER_ID);

            Relax_Roll_Scanner_USB_Reader_Activity.custPrograssbar.prograssCreate(this);

            JSONObject jsonObject = new JSONObject();
            try
            {
                jsonObject.put("userid", userid);
                jsonObject.put("processorid", processorid);
                jsonObject.put("scanned_by", userid);
                jsonObject.put("rollid", qrid);
                jsonObject.put("is_checked", is_checked);

                JsonParser jsonParser = new JsonParser();
                Call<JsonObject> call = APIClient.getInterface(). updaterelaxedroll((JsonObject) jsonParser.parse(jsonObject.toString()));
                GetResult getResult = new GetResult();
                getResult.setMyListener(this);
                getResult.callForLogin(call, "updateproductionrolls");
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        else {
            new AlertDialog.Builder(Relax_Roll_Scanner_USB_Reader_Activity.this)
                    .setMessage("No internet connection!")
                    .setCancelable(false)
                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface d, int arg0) {
                            d.dismiss();
                        }
                    }).show();
        }
    }

    public void Scan_again() {
        is_checked = 0;
        liner_bundle_details.setVisibility(View.GONE);
        linear_layout_btn.setVisibility(View.GONE);
        layout_text_4point.setVisibility(View.GONE);
        txtTitle.setVisibility(View.GONE);
        checkNo.isChecked();
        checkYes.setChecked(false);
        txtScanData.setText("");
        txtScanData.setFocusable(true);
        multiclick = false;
    }

    @Override
    public void callback(JsonObject result, String callNo) {
        try
        {
            btnOk.setOnClickListener(this);
            btnCancel.setOnClickListener(this);
            txtScanData.setFocusable(true);
            if(callNo.equalsIgnoreCase("getjobdetails"))
            {
                JSONObject jsonObject = new JSONObject(result.toString());
                String mStatus = jsonObject.optString("status");
                String message = jsonObject.optString("message");
                txtScanData.setFocusable(true);
                if (mStatus.equalsIgnoreCase("new"))
                {
                    JSONObject jsonObj = jsonObject.getJSONObject("data");

                    rollinfo = jsonObj.getJSONObject("rollinfo");

                    joborderno = rollinfo.getString("joborderno");
                    styleno = rollinfo.optString("styleno");
                    colors = rollinfo.optString("color");
                    shipcode = rollinfo.getString("shipcode");
                    partname = rollinfo.optString("partname");
                    rackname =  rollinfo.getString("rackname");
                    rollno = rollinfo.getString("rollno");
                    rollcode = rollinfo.getString("rollcode");
                    weight = rollinfo.getString("weight");

                    txtJobNo.setText(joborderno);
                    txtShipCode.setText(shipcode);
                    text_Color.setText(colors);
                    txtStyle.setText(styleno);

                    text_partname.setText(partname);
                    text_rackname.setText(rackname);
                    text_rollno.setText(rollno);
                    text_rollcode.setText(rollcode);
                    text_weight.setText(weight);

                    liner_bundle_details.setVisibility(View.VISIBLE);
                    linear_layout_btn.setVisibility(View.VISIBLE);
                    btnOk.setVisibility(View.VISIBLE);
                    layout_text_4point_check.setVisibility(View.VISIBLE);
                    layout_text_4point.setVisibility(View.GONE);
                    txtTitle.setVisibility(View.GONE);

                    Relax_Roll_Scanner_USB_Reader_Activity.custPrograssbar.closePrograssBar();
                    multiclick = true;
                }
                else if (mStatus.equalsIgnoreCase("scanned"))
                {
                    btnOk.setVisibility(View.GONE);
                    layout_text_4point_check.setVisibility(View.GONE);
                    JSONObject jsonObj = jsonObject.getJSONObject("data");

                    rollinfo = jsonObj.getJSONObject("rollinfo");

                    joborderno = rollinfo.getString("joborderno");
                    styleno = rollinfo.optString("styleno");
                    colors = rollinfo.optString("color");
                    shipcode = rollinfo.getString("shipcode");
                    partname = rollinfo.optString("partname");
                    rackname =  rollinfo.getString("rackname");
                    rollno = rollinfo.getString("rollno");
                    rollcode = rollinfo.getString("rollcode");
                    weight = rollinfo.getString("weight");
                    str_is_checked = rollinfo.getString("is_checked");

                    relaxed_by = "0";
                    relaxed_on = "0";
                    if (rollinfo.has("relaxed_by") && !rollinfo.isNull("relaxed_by")) {
                        relaxed_by = rollinfo.getString("relaxed_by");
                    }
                    if (rollinfo.has("relaxed_on") && !rollinfo.isNull("relaxed_on")) {
                        relaxed_on = rollinfo.getString("relaxed_on");
                    }
                    if(Integer.parseInt(str_is_checked) == 1)
                    {
                        str_is_checked = "YES";
                    }
                    else
                    {
                        str_is_checked = "NO";
                    }

                    txtJobNo.setText(joborderno);
                    txtShipCode.setText(shipcode);
                    text_Color.setText(colors);
                    txtStyle.setText(styleno);

                    text_partname.setText(partname);
                    text_rackname.setText(rackname);
                    text_rollno.setText(rollno);
                    text_rollcode.setText(rollcode);
                    text_weight.setText(weight);
                    text_4point.setText(str_is_checked);

                    liner_bundle_details.setVisibility(View.VISIBLE);
                    linear_layout_btn.setVisibility(View.VISIBLE);

                    layout_text_4point.setVisibility(View.VISIBLE);

                    txtTitle.setText(message +"\n"+"Scanned On : " + relaxed_on +"\n" +"Scanned By  : " + relaxed_by);
                    txtTitle.setVisibility(View.VISIBLE);

                    Relax_Roll_Scanner_USB_Reader_Activity.custPrograssbar.closePrograssBar();
                }
                else
                {
                    Relax_Roll_Scanner_USB_Reader_Activity.custPrograssbar.closePrograssBar();
                    new AlertDialog.Builder(Relax_Roll_Scanner_USB_Reader_Activity.this)
                        .setMessage(message)
                        .setCancelable(false)
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface arg1, int arg0) {
                                arg1.dismiss();
                            }
                        }).show();
                }

            }
            else  if(callNo.equalsIgnoreCase("updateproductionrolls"))
            {
                JSONObject jsonObject = new JSONObject(result.toString());
                String mStatus = jsonObject.optString("status");
                String message = jsonObject.optString("message");
                Relax_Roll_Scanner_USB_Reader_Activity.custPrograssbar.closePrograssBar();
                if (mStatus.equals("success")) {

                    is_checked = 0;
                    liner_bundle_details.setVisibility(View.GONE);
                    linear_layout_btn.setVisibility(View.GONE);
                    layout_text_4point.setVisibility(View.GONE);
                    txtTitle.setVisibility(View.GONE);

                    checkNo.isChecked();
                    checkYes.setChecked(false);
                }
                else {
                    Relax_Roll_Scanner_USB_Reader_Activity.custPrograssbar.closePrograssBar();
                    new AlertDialog.Builder(Relax_Roll_Scanner_USB_Reader_Activity.this)
                        .setMessage(message)
                        .setCancelable(false)
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface arg1, int arg0) {
                                arg1.dismiss();

                                is_checked = 0;
                                liner_bundle_details.setVisibility(View.GONE);
                                linear_layout_btn.setVisibility(View.GONE);
                                layout_text_4point.setVisibility(View.GONE);
                                txtTitle.setVisibility(View.GONE);

                                checkNo.isChecked();
                                checkYes.setChecked(false);

                            }
                        }).show();
                }
            }
            txtScanData.requestFocus();
        }
        catch (Exception e) {
        }
    }

    @Override
    public void onBackPressed() {

        Intent intent = new Intent(Relax_Roll_Scanner_USB_Reader_Activity.this, MainActivity.class);
        intent.putExtra("username", username);
        intent.putExtra("processorid", processorid);
        intent.putExtra("userid", userid);
        startActivity(intent);
        finish();
    }
}
