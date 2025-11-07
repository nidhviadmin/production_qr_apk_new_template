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
import androidx.annotation.RequiresApi;
import com.google.android.material.snackbar.Snackbar;
import androidx.core.content.FileProvider;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bipinexports.productionqr.APIClient;
import com.bipinexports.productionqr.GetResult;
import com.bipinexports.productionqr.R;
import com.bipinexports.productionqr.SessionManagement;
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

public class Bundle_QR_Scanner_USB_Reader_Activity extends BaseActivity implements View.OnClickListener, GetResult.MyListener {

    EditText txtScanData;
    ImageView imageView;
    TextView txtUser;

    ProgressDialog mProgressDialog;

    String User, unitid;
    String qrid;

    String myversionName;
    String apkurl;

    String apkFileName;
    BroadcastReceiver receiver;
    String app_version_name;

    String processorid;
    String userid;
    String username;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

        setContentView(R.layout.activity_base);
        setupDrawer();

        View content = getLayoutInflater().inflate(R.layout.activity_bundle_qr_usb_scanner,
                findViewById(R.id.content_frame), true);

        imageView = content.findViewById(R.id.imgd);
        imageView.setOnClickListener(this);
        setupNotifications();
        handleNotificationIntent(getIntent());
        txtUser = content.findViewById(R.id.txtUser);

        HashMap<String, String> user = session.getUserDetails();
        this.User = user.get(SessionManagement.KEY_USER);
        unitid = user.get(SessionManagement.KEY_UNITID);
        txtUser.setText("Hello " + this.User);

        processorid = getIntent().getStringExtra("processorid");
        userid = getIntent().getStringExtra("userid");
        username = getIntent().getStringExtra("name");

        versioncode();

        txtScanData = content.findViewById(R.id.txtScanData);
        txtScanData.requestFocus();

        txtScanData.post(new Runnable() {
            @Override
            public void run() {
                getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
                hideKeyboard();
            }
        });

        txtScanData.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if ((event.getAction() == KeyEvent.ACTION_UP &&
                        (keyCode == KeyEvent.KEYCODE_ENTER || keyCode == KeyEvent.KEYCODE_TAB))) {

                    String data = txtScanData.getText().toString().trim();

                    if (!data.isEmpty()) {
                        handleScanData(data);
                        txtScanData.setText("");
                    }

                    return true;
                }
                return false;
            }
        });
    }

    public void hideKeyboard() {
        View view = getCurrentFocus();
        if (view == null) {
            view = new View(this);
        }
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    private void handleScanData(String qrdata) {
        Log.e("Bipin", "qrdata: " + qrdata);
        qrid = qrdata;
        String[] arrayString = qrid.split("-");

        String qrprefix = null;

        if(arrayString.length > 1) {
            qrprefix = arrayString[0];
            qrid = arrayString[1];
        }
        if(arrayString.length > 1 && qrprefix.equals("NE")) {
            HashMap<String, String> user = session.getUserDetails();
            String isqc = user.get(SessionManagement.KEY_ISQC);

            if(isOnline()) {
                if (isqc.equals("N")) {
                    JSONObject jsonObject = new JSONObject();
                    try {
                        jsonObject.put("qrid", qrid);
                        jsonObject.put("userid", userid);
                        jsonObject.put("processorid", processorid);
                        jsonObject.put("version_name", myversionName);

                        JsonParser jsonParser = new JsonParser();
                        Call<JsonObject> call = APIClient.getInterface().getqrdata((JsonObject) jsonParser.parse(jsonObject.toString()));
                        GetResult getResult = new GetResult();
                        getResult.setMyListener(this);
                        getResult.callForLogin(call, "getqrdata");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else if (isqc.equals("Y")) {
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
                }
            } else {
                AlertDialog alertDialog = new AlertDialog.Builder(Bundle_QR_Scanner_USB_Reader_Activity.this)
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
            Handler h = new Handler(Looper.getMainLooper());
            h.post(new Runnable() {
                public void run() {
                    new AlertDialog.Builder(Bundle_QR_Scanner_USB_Reader_Activity.this)
                            .setTitle("Error")
                            .setMessage("Only Bundle Scanning is supported as of now.\nContact Admin!")
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
            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
                myversionName = "Unknown-01";
            }
        } else {
            AlertDialog alertDialog = new AlertDialog.Builder(Bundle_QR_Scanner_USB_Reader_Activity.this)
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
        if (isOnline()) {
            int id = v.getId();

            if (id == R.id.imgd) {
                toggleDrawer();
            }
            else if (id == R.id.lvl_home) {
                Intent intent = new Intent(Bundle_QR_Scanner_USB_Reader_Activity.this, MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(intent);
                finish();
            }
            else if (id == R.id.logout) {
                session.logoutUser();
                finish();
            }
        } else {
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
            previousApkFile.delete();
        }

        String apkUrl = sUrl;
        mProgressDialog = new ProgressDialog(Bundle_QR_Scanner_USB_Reader_Activity.this);
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
                    mProgressDialog.hide();
                    unregisterReceiver(receiver);
                    installApkFromStorage(context, apkFileName);
                }
            }
        };
        registerReceiver(receiver, new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));
    }

    public void installApkFromStorage(Context context, String apkFileName) {
        Uri apkUri = FileProvider.getUriForFile(context, "com.bipinexports.productionqr.fileprovider", new File(context.getExternalFilesDir(null), apkFileName));
        Intent installIntent = new Intent(Intent.ACTION_INSTALL_PACKAGE);
        installIntent.setData(apkUri);
        installIntent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

        try {
            context.startActivity(installIntent);
        } catch (ActivityNotFoundException e) {
            Toast.makeText(context, "APK NOT Open", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void callback(JsonObject result, String callNo) {
        try
        {
            if (callNo.equalsIgnoreCase("piecewise_scan_and_update")) {
                JSONObject json = new JSONObject(result.toString());
                String status = json.optString("status");
                String mess = json.optString("message");

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

                    final Dialog dialog = new Dialog(Bundle_QR_Scanner_USB_Reader_Activity.this);
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
                    btnOk.setOnClickListener(new View.OnClickListener()
                    {
                        @Override
                        public void onClick(View v)
                        {
                            dialog.dismiss();
//                            scan_requestcode =1;
//                            PcwiseScanning();
                        }
                    });

                }
                else if (mess.equals("Scanned successfully")) {
//                    PcwiseScanning();
                }
                else
                {
                    AlertDialog alertDialog = new AlertDialog.Builder(Bundle_QR_Scanner_USB_Reader_Activity.this)
                            .setMessage(mess)
                            .setCancelable(false)
                            .setPositiveButton("OK", new DialogInterface.OnClickListener()
                            {
                                public void onClick(DialogInterface arg1, int arg0)
                                {
                                    arg1.dismiss();
//                                    scan_requestcode =1;
//                                    PcwiseScanning();
                                }
                            }).show();
                }
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

                    AlertDialog alertDialog = new AlertDialog.Builder(Bundle_QR_Scanner_USB_Reader_Activity.this)
                            .setMessage(mess)
                            .setCancelable(false)
                            .setPositiveButton("OK", new DialogInterface.OnClickListener()
                            {
                                @RequiresApi(api = Build.VERSION_CODES.M)
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

                    final Dialog dialog = new Dialog(Bundle_QR_Scanner_USB_Reader_Activity.this);
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
                        btnOk.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                dialog.dismiss();
//
//                            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//                            String dateString = formatter.format(new Date(Long.parseLong(String.valueOf(System.currentTimeMillis()).toString())));
//                            Log.e("Bipin","Scan Again  :" +dateString);

                                //Scanning();
                            }
                        });

                    } else if (mess.equals("TO BE SCANNED")) {

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
//                            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//                            String dateString = formatter.format(new Date(Long.parseLong(String.valueOf(System.currentTimeMillis()).toString())));
//                            Log.e("Bipin","Scan Again  :" +dateString);
                                //Scanning();
                            }
                        });
                        btnOk.setText("Confirm");
                        btnOk.setVisibility(View.VISIBLE);
                        btnOk.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                dialog.dismiss();
                                mProgressDialog = new ProgressDialog(Bundle_QR_Scanner_USB_Reader_Activity.this);
                                mProgressDialog.setMessage(getString(R.string.loading));
                                mProgressDialog.setIndeterminate(true);
                                mProgressDialog.show();

                                if(isOnline()) {
                                    JSONObject jsonObject = new JSONObject();
                                    try {
                                        jsonObject.put("qrid", qrid);
                                        jsonObject.put("userid", userid);
                                        jsonObject.put("processorid", processorid);

                                        JsonParser jsonParser = new JsonParser();
                                        Call<JsonObject> call = APIClient.getInterface().updatescanstatus((JsonObject) jsonParser.parse(jsonObject.toString()));
                                        GetResult getResult = new GetResult();
                                        getResult.setMyListener(Bundle_QR_Scanner_USB_Reader_Activity.this);
                                        getResult.callForLogin(call, "updatescanstatus");
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }
                                else {

                                    AlertDialog alertDialog = new AlertDialog.Builder(Bundle_QR_Scanner_USB_Reader_Activity.this)
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
                        AlertDialog alertDialog = new AlertDialog.Builder(Bundle_QR_Scanner_USB_Reader_Activity.this)
                                .setMessage(mess)
                                .setCancelable(false)
                                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface arg1, int arg0) {
                                        //Scanning();
                                    }
                                }).show();
                    }
                }
            }
            else if (callNo.equalsIgnoreCase("updatescanstatus"))
            {

//                SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//                String dateString = formatter.format(new Date(Long.parseLong(String.valueOf(System.currentTimeMillis()).toString())));
//                Log.e("Bipin","Update Data Retrun View  :" +dateString);

                JSONObject json = new JSONObject(result.toString());
                String status = json.optString("status");
                String mess = json.optString("message");

                if (status.equals("success")) {
                    mProgressDialog.hide();
                    // Scanning();
                }
                else {
                    AlertDialog alertDialog = new AlertDialog.Builder(Bundle_QR_Scanner_USB_Reader_Activity.this)
                            .setMessage(mess)
                            .setCancelable(false)
                            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface arg1, int arg0) {
                                    //Scanning();
                                }
                            }).show();
                }
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
                    app_version_name = jsonObj.optString("app_version_name");

                    AlertDialog alertDialog = new AlertDialog.Builder(Bundle_QR_Scanner_USB_Reader_Activity.this)
                            .setMessage(mess)
                            .setCancelable(false)
                            .setPositiveButton("OK", new DialogInterface.OnClickListener()
                            {
                                @RequiresApi(api = Build.VERSION_CODES.M)
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
                    final Dialog dialog = new Dialog(Bundle_QR_Scanner_USB_Reader_Activity.this);
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
                        btnCancel.setOnClickListener(new View.OnClickListener()
                        {
                            @Override
                            public void onClick(View v)
                            {
                                dialog.dismiss();
                                //Scanning();
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
                                    mProgressDialog = new ProgressDialog(Bundle_QR_Scanner_USB_Reader_Activity.this);
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
                                        getResult.setMyListener(Bundle_QR_Scanner_USB_Reader_Activity.this);
                                        getResult.callForLogin(call, "updateqcscanstatus");
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }
                                else {

                                    AlertDialog alertDialog = new AlertDialog.Builder(Bundle_QR_Scanner_USB_Reader_Activity.this)
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
                    else {
                        AlertDialog alertDialog = new AlertDialog.Builder(Bundle_QR_Scanner_USB_Reader_Activity.this)
                                .setMessage(mess)
                                .setCancelable(false)
                                .setPositiveButton("OK", new DialogInterface.OnClickListener()
                                {
                                    public void onClick(DialogInterface arg1, int arg0)
                                    {
                                        //Scanning();
                                    }
                                }).show();
                    }
                }
            }
            else if (callNo.equalsIgnoreCase("updateqcscanstatus"))
            {
                JSONObject json = new JSONObject(result.toString());
                String status = json.optString("status");
                String mess = json.optString("message");
                if (status.equals("success")) {
                    mProgressDialog.hide();
                    AlertDialog alertDialog = new AlertDialog.Builder(Bundle_QR_Scanner_USB_Reader_Activity.this)
                            .setMessage(mess)
                            .setCancelable(false)
                            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface arg1, int arg0) {
                                    //Scanning();
                                }
                            }).show();
                }
                else {
                    AlertDialog alertDialog = new AlertDialog.Builder(Bundle_QR_Scanner_USB_Reader_Activity.this)
                            .setMessage(mess)
                            .setCancelable(false)
                            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface arg1, int arg0) {
                                    //Scanning();
                                }
                            }).show();
                }
            }
        }
        catch (Exception e) {
        }
    }

    @Override
    public void onBackPressed() {
        if (drawerLayout != null && drawerLayout.isDrawerOpen(androidx.core.view.GravityCompat.START)) {
            drawerLayout.closeDrawer(androidx.core.view.GravityCompat.START);
        } else {
            Intent intent = new Intent(Bundle_QR_Scanner_USB_Reader_Activity.this, MainActivity.class);
            intent.putExtra("username", username);
            intent.putExtra("processorid", processorid);
            intent.putExtra("userid", userid);
            startActivity(intent);
            finish();
        }
    }
}