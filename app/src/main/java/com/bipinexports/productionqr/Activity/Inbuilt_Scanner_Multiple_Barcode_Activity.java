package com.bipinexports.productionqr.Activity;

import static android.app.ProgressDialog.show;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DownloadManager;
import android.content.ActivityNotFoundException;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import com.bipinexports.productionqr.APIClient;
import com.bipinexports.productionqr.GetResult;
import com.bipinexports.productionqr.R;
import com.bipinexports.productionqr.SessionManagement;
import com.google.android.material.snackbar.Snackbar;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.honeywell.aidc.AidcManager;
import com.honeywell.aidc.AidcManager.BarcodeDeviceListener;
import com.honeywell.aidc.AidcManager.CreatedCallback;
import com.honeywell.aidc.BarcodeDeviceConnectionEvent;
import com.honeywell.aidc.BarcodeFailureEvent;
import com.honeywell.aidc.BarcodeReadEvent;
import com.honeywell.aidc.BarcodeReader;
import com.honeywell.aidc.BarcodeReaderInfo;
import com.honeywell.aidc.InvalidScannerNameException;
import com.honeywell.aidc.ScannerUnavailableException;
import com.honeywell.aidc.TriggerStateChangeEvent;
import com.honeywell.aidc.UnsupportedPropertyException;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import retrofit2.Call;


public class Inbuilt_Scanner_Multiple_Barcode_Activity extends BaseActivity implements
        BarcodeReader.BarcodeListener, BarcodeReader.TriggerListener, OnClickListener, GetResult.MyListener {

    private com.honeywell.aidc.AidcManager mAidcManager;
    private com.honeywell.aidc.BarcodeReader mBarcodeReader;
    private final Context mContext = this;
    private String mConnectedScanner = null;
    private boolean mResume = false;

    SessionManagement session;
    String User, unitid;
    ImageView imageView;
    TextView txtUser;

    String myversionName;
    String apkurl;

    String apkFileName;
    BroadcastReceiver receiver;
    String app_version_name;

    String processorid;
    String userid, isqc, qrid;
    String username;

    public static CustPrograssbar_new custPrograssbar_new;


    String curr_qr_id, scanneddate, joborderno, shipcode, colors, size, bundleno, styleno, stylename, partname, total_insert_count;

    List qr_id_arr = new ArrayList<String>();

    List scan_qr_id_arr = new ArrayList<String>();
    List scanned_qr_id_arr = new ArrayList<String>();
    JSONArray QR_ID_Array = new JSONArray();
    List pending_scan_qr_id_arr = new ArrayList<String>();

    TableLayout outprogramtbl;

    TextView txtJobNo, txtShipCode, text_Part_Name, text_Size_Name, txtStyle, txtStyleRef, title_name;
    LinearLayout liner_bundle_details, linear_programdata, linear_layout_btn, linear_badge;

    JSONObject scan_qr_data_details;

    int click_count =0;
    TextView[] textViewPcQr;

    HashMap<Integer, Integer> text_view_hashmap = new HashMap<Integer, Integer>();
    HashMap<Integer, String> scan_date_time_hashmap = new HashMap<Integer, String>();
    int totalinsert_count =0;
    HashMap<Integer, String> scanned_qr_st_arr_hashmap = new HashMap<Integer, String>();
    HashMap<Integer, String> pending_qr_st_arr = new HashMap<Integer, String>();
    HashMap<Integer, String> assign_header_qr_st_arr = new HashMap<Integer, String>();
    HashMap<Integer, String> bundle_no_arr = new HashMap<Integer, String>();

    TextView txt_bundlenos;
    int current_pageNo = 1;

    int maxRowcnt = 25;
    int maxColCnt = 4;

    int from_bundleno = 0;
    int tobundle_no = 0;

    int[][] bundleNosIdxArray;

    String statrt_qrid;

    Button btnOk;
    Button btnCancel;

    public Inbuilt_Scanner_Multiple_Barcode_Activity() {
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_in_employee_operation_mapping);
        setContentView(R.layout.activity_base);
        setupDrawer();
        View content = getLayoutInflater().inflate(
                R.layout.activity_bt_scanner_multiple_qr,   // <-- your current XML
                findViewById(R.id.content_frame),
                true
        );

        versioncode();

        if(Build.MODEL.startsWith("VM1A")) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        }
        else{
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }

        processorid = getIntent().getStringExtra("processorid");
        userid = getIntent().getStringExtra("userid");
        username = getIntent().getStringExtra("name");
        isqc = getIntent().getStringExtra("isqc");

        custPrograssbar_new = new CustPrograssbar_new();

        imageView = content.findViewById(R.id.imgd);
        imageView.setOnClickListener(this);
        setupNotifications();

        handleNotificationIntent(getIntent());
        txtUser = content.findViewById(R.id.txtUser);

        session = new SessionManagement(getApplicationContext());
        HashMap<String, String> user = session.getUserDetails();
        this.User = user.get(SessionManagement.KEY_USER);
        unitid = user.get(SessionManagement.KEY_UNITID);
        txtUser.setText("Hello " + this.User);

        txtJobNo = content.findViewById(R.id.txtJobNo);
        txtShipCode = content.findViewById(R.id.txtShipCode);
        text_Part_Name = content.findViewById(R.id.text_Part_Name);
        text_Size_Name = content.findViewById(R.id.text_Size_Name);
        txtStyle = content.findViewById(R.id.txtStyle);
        txtStyleRef = content.findViewById(R.id.text_Color);

        title_name  = content.findViewById(R.id.title_name);
        title_name.setVisibility(View.GONE);

        liner_bundle_details = content.findViewById(R.id.liner_bundle_details);
        liner_bundle_details.setVisibility(View.GONE);

        linear_programdata = content.findViewById(R.id.linear_programdata);
        linear_programdata.setVisibility(View.GONE);

        linear_layout_btn = content.findViewById(R.id.linear_layout_btn);
        linear_layout_btn.setVisibility(View.GONE);

        linear_badge = content.findViewById(R.id.linear_badge);
        linear_badge.setVisibility(View.GONE);

        outprogramtbl = content.findViewById(R.id.AddProgramData);

        txt_bundlenos = content.findViewById(R.id.txt_bundlenos);
        btnOk = content.findViewById(R.id.btnOk);
        btnCancel = content.findViewById(R.id.btnCancel);

        btnOk.setOnClickListener(this);
        btnCancel.setOnClickListener(this);
        btnOk.setVisibility(View.GONE);


        /*
         * Get new AidcManager
         */
        AidcManager.create(this, new CreatedCallback() {

            @Override
            public void onCreated(AidcManager aidcManager) {
                mAidcManager = aidcManager;
                mAidcManager.addBarcodeDeviceListener(new BarcodeDeviceListener() {

                    @Override
                    public void onBarcodeDeviceConnectionEvent(BarcodeDeviceConnectionEvent event) {
                        // Could use this to call scannerSelection like when
                        // press switch scanner button.
                        // Here we just use it to notify the user when a scanner
                        // is attached or detached and
                        // give a toast.
                        String connected;
                        if (event.getConnectionStatus() == AidcManager.BARCODE_DEVICE_DISCONNECTED) {
                            connected = "Disconnected";
                        } else {
                            connected = "Connected";
                        }

                        // Only act on the event if the app is in the resume state. The app could
                        // store the connection event and BarcodeReaderInfo object if the app is
                        // not in the resume state so the proper imager claim can be made in onResume
                        if (mResume) {
                            final String message = "Scanner: "
                                    + event.getBarcodeReaderInfo().getFriendlyName() + " is "
                                    + connected;
                            ((AppCompatActivity) mContext).runOnUiThread(new Runnable() {

                                @Override
                                public void run() {
                                    Toast.makeText(mContext, message, Toast.LENGTH_SHORT).show();
                                }

                            });
                        }
                    }
                });
                initialize();
            }
        });
    }

    private void versioncode() {
        Context context = this;
        PackageManager manager = context.getPackageManager();
        try {
            PackageInfo info = manager.getPackageInfo(context.getPackageName(), 0);
            myversionName = info.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            myversionName = "Unknown-01";
        }
    }

    @Override
    public void onClick(View v) {
        if (isOnline())
        {
            switch (v.getId()) {
                case R.id.imgd:
                    toggleDrawer();
                    break;
                case R.id.btnOk:
                    UpdatescanData();
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

    public void initialize() {
//        scannerSelection(mAidcManager.listConnectedBarcodeDevices());
        String scanner = "dcs.scanner.imager";
        createBarcodeReaderConnection(scanner);
    }

    @Override
    public void onBarcodeEvent(final BarcodeReadEvent event) {
//        qrid = event.getBarcodeData();
//        String[] arrayString = qrid.split("-");
        // Scanner thread
        runOnUiThread(() -> {
            try {
                qrid = event.getBarcodeData();
                String[] arrayString = qrid.split("-");
//        String qrprefix = null;
                String qrprefix = null;

//        if(arrayString.length > 1) {
//            qrprefix = arrayString[0];
//            qrid = arrayString[1];
//        }
//        if(arrayString.length > 1 && qrprefix.equals("NE"))
//        {
//            if(isOnline()) {
//
//                session = new SessionManagement(getApplicationContext());
//                HashMap<String, String> user = session.getUserDetails();
//                processorid = user.get(SessionManagement.KEY_PROCESSOR_ID);
//                userid = user.get(SessionManagement.KEY_USER_ID);
//                isqc = user.get(SessionManagement.KEY_ISQC);
//
//                if (isqc.equals("N")) {
//                    Inbuilt_Scanner_Multiple_Barcode_Activity.custPrograssbar_new.prograssCreate(this);
//                    if(click_count == 0) {
//                        JSONObject jsonObject = new JSONObject();
//                        try {
//                            jsonObject.put("qrid", qrid);
//                            jsonObject.put("userid", userid);
//                            jsonObject.put("processorid", processorid);
//                            jsonObject.put("version_name", myversionName);
//
//                            statrt_qrid = qrid;
//
//                            JsonParser jsonParser = new JsonParser();
//                            Call<JsonObject> call = APIClient.getInterface().get_new_qr_data((JsonObject) jsonParser.parse(jsonObject.toString()));
//                            GetResult getResult = new GetResult();
//                            getResult.setMyListener(this);
//                            getResult.callForLogin(call, "get_new_qr_data");
//                        } catch (JSONException e) {
//                            e.printStackTrace();
//                        }
//                    }
//                    else {
//                        curr_qr_id =  qrid;
//                        data_added();
//                    }
                if (arrayString.length > 1) {
                    qrprefix = arrayString[0];
                    qrid = arrayString[1];
                }

//            }
//            else {
//
//                AlertDialog alertDialog = new AlertDialog.Builder(Inbuilt_Scanner_Multiple_Barcode_Activity.this)
//                        .setMessage("Please Check Your Internet Connection")
//                        .setCancelable(false)
//                        .setNegativeButton("Exit", new DialogInterface.OnClickListener() {
//                            public void onClick(DialogInterface arg1, int arg0) {
//                                arg1.dismiss();

                if (arrayString.length > 1 && "NE".equals(qrprefix)) {
                    if (isOnline()) {
                        session = new SessionManagement(getApplicationContext());
                        HashMap<String, String> user = session.getUserDetails();
                        processorid = user.get(SessionManagement.KEY_PROCESSOR_ID);
                        userid = user.get(SessionManagement.KEY_USER_ID);
                        isqc = user.get(SessionManagement.KEY_ISQC);

                        if ("N".equals(isqc)) {
                            Inbuilt_Scanner_Multiple_Barcode_Activity.custPrograssbar_new.prograssCreate(this);

                            if (click_count == 0) {
                                JSONObject jsonObject = new JSONObject();
                                jsonObject.put("qrid", qrid);
                                jsonObject.put("userid", userid);
                                jsonObject.put("processorid", processorid);
                                jsonObject.put("version_name", myversionName);

                                statrt_qrid = qrid;

                                JsonParser jsonParser = new JsonParser();
                                Call<JsonObject> call = APIClient.getInterface()
                                        .get_new_qr_data((JsonObject) jsonParser.parse(jsonObject.toString()));
                                GetResult getResult = new GetResult();
                                getResult.setMyListener(this);
                                getResult.callForLogin(call, "get_new_qr_data");
                            } else {
                                curr_qr_id = qrid;
                                data_added();
                            }
//                        }).show();
//                alertDialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(Color.RED);
//            }
//        }
//        else
//        {
//            Handler h = new Handler(Looper.getMainLooper());
//            h.post(new Runnable() {
//                public void run() {
//                    new AlertDialog.Builder(mContext)
                        }
                    } else {
                        AlertDialog alertDialog = new AlertDialog.Builder(Inbuilt_Scanner_Multiple_Barcode_Activity.this)
                                .setMessage("Please Check Your Internet Connection")
                                .setCancelable(false)
                                .setNegativeButton("Exit", (arg1, arg0) -> arg1.dismiss())
                                .show();

                        alertDialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(Color.RED);
                    }
                } else {
                    new AlertDialog.Builder(Inbuilt_Scanner_Multiple_Barcode_Activity.this)
                            .setTitle("Error")
                            .setMessage("Only Piecewise Scanning is supported as of now.\nContact Admin!")
//                            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
//                                @Override
//                                public void onClick(DialogInterface dialog, int which) {
//                                    dialog.dismiss();
//                                }
//                            }).show();
                            .setPositiveButton("OK", (dialog, which) -> dialog.dismiss())
                            .show();
                }
            } catch (Exception e) {
                Log.e("BarcodeEvent", "Error in barcode event", e);
            }
        });
    }

    // When using Automatic Trigger control do not need to implement the
    // onTriggerEvent function
    @Override
    public void onTriggerEvent(TriggerStateChangeEvent event) {
        // TODO Auto-generated method stub
    }

    @Override
    public void onFailureEvent(BarcodeFailureEvent arg0) {
        // TODO Auto-generated method stub
    }

    @Override
    public void onResume() {
        super.onResume();
        mResume = true;
        if (mBarcodeReader != null) {
            try {
                mBarcodeReader.claim();
            } catch (ScannerUnavailableException e) {
                e.printStackTrace();
                Toast.makeText(this, "Scanner unavailable", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        mResume = false;
        if (mBarcodeReader != null) {
            // release the scanner claim so we don't get any scanner
            // notifications while paused.
            mBarcodeReader.release();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mBarcodeReader != null) {
            // unregister barcode event listener
            mBarcodeReader.removeBarcodeListener(this);
            // unregister trigger state change listener
            mBarcodeReader.removeTriggerListener(this);
            mBarcodeReader.close();
        }
        if (mAidcManager != null) {
            mAidcManager.close();
        }
    }

    public void claimBarcodeReader() {
        if (mBarcodeReader != null) {
            try {
                mBarcodeReader.claim();
            } catch (ScannerUnavailableException e) {
                e.printStackTrace();
                Toast.makeText(this, "Scanner unavailable", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void scannerSelection(final List<BarcodeReaderInfo> scanners) {
        Handler h = new Handler(Looper.getMainLooper());
        h.post(new Runnable() {
            @Override
            public void run() {
                final Dialog scannerSelectDialog = new Dialog(mContext);
                scannerSelectDialog.setContentView(R.layout.scanner_select_dialog);
                Button dialogButton = (Button) scannerSelectDialog
                        .findViewById(R.id.dialogButtonOK);

                // If there are scanners, just show the list, must select one
                if (scanners.size() > 0) {
                    scannerSelectDialog.setTitle("Select Scanner");
                    dialogButton.setVisibility(Button.INVISIBLE);
                    final Map<String, String> scannerNames = new HashMap<String, String>();
                    for (BarcodeReaderInfo i : scanners) {
                        scannerNames.put(i.getFriendlyName(), i.getName());
                    }

                    final ListView list = (ListView) scannerSelectDialog
                            .findViewById(R.id.listScanners);
                    ArrayAdapter<String> scannerNameAdapter = new ArrayAdapter<String>(mContext,
                            android.R.layout.simple_list_item_1, android.R.id.text1,
                            new ArrayList<String>(scannerNames.keySet()));
                    list.setAdapter(scannerNameAdapter);

                    list.setOnItemClickListener(new OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> myAdapter, View myView, int pos,
                                                long mylng) {
                            String selectedScanner = (String) list.getItemAtPosition(pos);
                            createBarcodeReaderConnection(scannerNames.get(selectedScanner));
                            scannerSelectDialog.dismiss();
                        }

                    });

                } else { // Show an ok button to close dialog
                    scannerSelectDialog.setTitle("No Scanners Connected");
                    dialogButton.setOnClickListener(new OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            scannerSelectDialog.dismiss();
                        }
                    });
                }

                scannerSelectDialog.setCancelable(false);
                scannerSelectDialog.show();
            }
        });
    }

    private void createBarcodeReaderConnection(String scanner) {
//        Log.e("Bipin", "Scanner " + scanner);
        if (scanner != null && !scanner.equals(mConnectedScanner)) {

            if (mBarcodeReader != null) {
                mBarcodeReader.release();
                mBarcodeReader.close();
            }

            try {
                mBarcodeReader = mAidcManager.createBarcodeReader(scanner);
                mBarcodeReader.setProperty(BarcodeReader.PROPERTY_TRIGGER_CONTROL_MODE,
                        BarcodeReader.TRIGGER_CONTROL_MODE_AUTO_CONTROL);

            } catch (UnsupportedPropertyException e) {
                e.printStackTrace();
                Toast.makeText(this, "Control mode not set", Toast.LENGTH_SHORT).show();
            }
            catch (InvalidScannerNameException e) {
                e.printStackTrace();
                Toast.makeText(this, "Invalid Scanner Name Exception: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
            catch (Exception e){
                e.printStackTrace();
                Toast.makeText(this, "Exception: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }

            mBarcodeReader.addBarcodeListener((BarcodeReader.BarcodeListener) mContext);
            mBarcodeReader.addTriggerListener((BarcodeReader.TriggerListener) mContext);
//			mBarcodeReader.addMenuCommandListener((BarcodeReader.MenuCommandListener) mContext);

            Map<String, Object> properties = new HashMap<String, Object>();
            // Set Symbologies On/Off
            properties.put(BarcodeReader.PROPERTY_CODE_128_ENABLED, true);
            properties.put(BarcodeReader.PROPERTY_GS1_128_ENABLED, true);
            properties.put(BarcodeReader.PROPERTY_QR_CODE_ENABLED, true);
            properties.put(BarcodeReader.PROPERTY_CODE_39_ENABLED, true);
            properties.put(BarcodeReader.PROPERTY_DATAMATRIX_ENABLED, true);
            properties.put(BarcodeReader.PROPERTY_UPC_A_ENABLE, true);
            properties.put(BarcodeReader.PROPERTY_EAN_13_ENABLED, false);
            properties.put(BarcodeReader.PROPERTY_AZTEC_ENABLED, false);
            properties.put(BarcodeReader.PROPERTY_CODABAR_ENABLED, false);
            properties.put(BarcodeReader.PROPERTY_INTERLEAVED_25_ENABLED, false);
            properties.put(BarcodeReader.PROPERTY_PDF_417_ENABLED, false);
            // Set Max Code 39 barcode length
            properties.put(BarcodeReader.PROPERTY_CODE_39_MAXIMUM_LENGTH, 10);
            // Turn on center decoding
            properties.put(BarcodeReader.PROPERTY_CENTER_DECODE, true);
            // Enable bad read response
            properties.put(BarcodeReader.PROPERTY_NOTIFICATION_BAD_READ_ENABLED, true);
            // Sets time period for decoder timeout in any mode
            properties.put(BarcodeReader.PROPERTY_DECODER_TIMEOUT,  400);
            // Apply the settings
            mBarcodeReader.setProperties(properties);

            claimBarcodeReader();
            mConnectedScanner = scanner;
        }
    }

    @Override
    public void callback(JsonObject result, String callNo) {
        try
        {
            btnOk.setOnClickListener(this);
            btnCancel.setOnClickListener(this);

            if(callNo.equalsIgnoreCase("get_new_qr_data"))
            {
                JSONObject jsonObject = new JSONObject(result.toString());
                String mStatus = jsonObject.optString("status");
                String batch_status = jsonObject.optString("batch_status");
                String message = jsonObject.optString("message");

                if(mStatus.equals("version_check"))
                {
                    JSONObject jsonObj = jsonObject.getJSONObject("data");
                    apkurl = jsonObj.optString("apk_url");
                    app_version_name = jsonObj.optString("version_name");
                    Inbuilt_Scanner_Multiple_Barcode_Activity.custPrograssbar_new.closePrograssBar();

                    AlertDialog alertDialog = new AlertDialog.Builder(Inbuilt_Scanner_Multiple_Barcode_Activity.this)
                            .setMessage(message)
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
                    if (mStatus.equalsIgnoreCase("success"))
                    {
                        if(message.equals("TO BE SCANNED"))
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

                            if(batch_status.equals("batches_found"))
                            {
                                linear_badge.setVisibility(View.VISIBLE);
                                linear_badge.removeAllViews(); // clear previous items

                                try {
                                    JSONObject dataObj = jsonObject.getJSONObject("batches_data");
                                    JSONArray batchesArray = dataObj.getJSONArray("batches");
                                    int current_batch_no = dataObj.getInt("current_batch_no");

                                    for (int i = 0; i < batchesArray.length(); i++) {
                                        JSONObject batchObj = batchesArray.getJSONObject(i);

                                        int batch_no = batchObj.getInt("batch_no");
                                        int from_bundle_no = batchObj.getInt("from_bundle_no");
                                        int to_bundle_no = batchObj.getInt("to_bundle_no");


                                        // Create a vertical layout for each page
                                        LinearLayout badgeLayout = new LinearLayout(this);
                                        badgeLayout.setOrientation(LinearLayout.VERTICAL);
                                        badgeLayout.setGravity(Gravity.CENTER);
                                        badgeLayout.setPadding(30, 20, 30, 20);

                                        // Create TextViews for "Page X" and range
                                        TextView pageTitle = new TextView(this);
                                        pageTitle.setText("B " + batch_no);
                                        pageTitle.setGravity(Gravity.CENTER);
                                        pageTitle.setTextSize(16);
                                        pageTitle.setTypeface(null, Typeface.BOLD);

                                        TextView pageRange = new TextView(this);
                                        pageRange.setText("(" + from_bundle_no + " - " + to_bundle_no + ")");
                                        pageRange.setGravity(Gravity.CENTER);
                                        pageRange.setTextSize(14);

                                        // Add to layout
                                        badgeLayout.addView(pageTitle);
                                        badgeLayout.addView(pageRange);

                                        // Set default background style
                                        badgeLayout.setBackgroundResource(android.R.color.darker_gray);
                                        pageTitle.setTextColor(Color.BLACK);
                                        pageRange.setTextColor(Color.BLACK);

                                        // Highlight Page 1 by default
                                        if (batch_no == current_batch_no) {
                                            from_bundleno = from_bundle_no;
                                            tobundle_no = to_bundle_no;

                                            badgeLayout.setBackgroundResource(android.R.color.holo_blue_light);
                                            pageTitle.setTextColor(Color.WHITE);
                                            pageRange.setTextColor(Color.WHITE);
                                        }

                                        // Add margin between pages
                                        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                                                LinearLayout.LayoutParams.WRAP_CONTENT,
                                                LinearLayout.LayoutParams.WRAP_CONTENT);
                                        params.setMargins(15, 10, 15, 10);
                                        badgeLayout.setLayoutParams(params);

                                        // Click listener for each page
                                        badgeLayout.setOnClickListener(v -> {
                                            // Reset all highlights
                                            for (int j = 0; j < linear_badge.getChildCount(); j++) {
                                                LinearLayout layout = (LinearLayout) linear_badge.getChildAt(j);
                                                layout.setBackgroundResource(android.R.color.darker_gray);
                                                for (int k = 0; k < layout.getChildCount(); k++) {
                                                    TextView t = (TextView) layout.getChildAt(k);
                                                    t.setTextColor(Color.BLACK);
                                                }
                                            }
                                            // Highlight selected
                                            v.setBackgroundResource(android.R.color.holo_blue_light);
                                            for (int k = 0; k < ((LinearLayout) v).getChildCount(); k++) {
                                                TextView t = (TextView) ((LinearLayout) v).getChildAt(k);
                                                t.setTextColor(Color.WHITE);
                                            }
                                            txt_bundlenos.setText(from_bundle_no + "-" + to_bundle_no + " Bundle No Details");
                                            outprogramtbl.removeAllViews();
                                            callGetNewQRData(from_bundle_no, batch_no, to_bundle_no);

                                            // 🔹 Call your API for selected page
                                        });

                                        // Add this page to the layout
                                        linear_badge.addView(badgeLayout);
                                    }

                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                            else
                            {
                                Inbuilt_Scanner_Multiple_Barcode_Activity.custPrograssbar_new.closePrograssBar();
                                new AlertDialog.Builder(Inbuilt_Scanner_Multiple_Barcode_Activity.this)
                                        .setMessage(message)
                                        .setCancelable(false)
                                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface arg1, int arg0) {
                                                arg1.dismiss();
                                                onBackPressed();
                                            }
                                        }).show();
                            }

                            JSONObject jsonObj = jsonObject.getJSONObject("data");

                            curr_qr_id = jsonObj.optString("id");
                            scanneddate = jsonObj.optString("scanneddate");
                            joborderno = jsonObj.optString("joborderno");
                            shipcode = jsonObj.optString("shipcode");
                            colors = jsonObj.optString("color");
                            size = jsonObj.optString("size");
                            styleno = jsonObj.optString("styleno");
                            stylename = jsonObj.optString("stylename");
                            partname = jsonObj.optString("partname");
                            bundleno = jsonObj.optString("bundleno");
                            total_insert_count = jsonObj.optString("total_insert_count");
                            scan_qr_data_details = jsonObj.getJSONObject("bundles_array_details");

                            String max_row_cnt = jsonObj.optString("max_row_cnt");
                            String max_col_cnt = jsonObj.optString("max_col_cnt");

                            maxRowcnt = Integer.parseInt(max_row_cnt);
                            maxColCnt = Integer.parseInt(max_col_cnt);

                            txtJobNo.setText(joborderno);
                            txtShipCode.setText(shipcode);
                            text_Part_Name.setText(partname);
                            text_Size_Name.setText(size);
                            txtStyle.setText(styleno);
                            txtStyleRef.setText(stylename);

                            title_name.setVisibility(View.VISIBLE);
                            liner_bundle_details.setVisibility(View.VISIBLE);
                            linear_programdata.setVisibility(View.VISIBLE);
                            linear_layout_btn.setVisibility(View.VISIBLE);

                            add_scan_data();

                            Boolean arraycontains = false;
                            outprogramtbl.removeAllViews();
                            TableRow row;

                            if(scanned_qr_id_arr.size() > 0)
                            {
                                if (scanned_qr_id_arr.contains(Integer.parseInt(String.valueOf(curr_qr_id).trim())))
                                {
                                    arraycontains = true;
                                }
                                else
                                {
                                    qr_id_arr.add(curr_qr_id);
                                    totalinsert_count++;
                                }
                            }
                            else if(pending_scan_qr_id_arr.size() > 0)
                            {
                                if (pending_scan_qr_id_arr.contains(Integer.parseInt(String.valueOf(curr_qr_id).trim())))
                                {
                                    arraycontains = true;
                                }
                                else
                                {
                                    qr_id_arr.add(curr_qr_id);
                                    totalinsert_count++;
                                }
                            }
                            else
                            {
                                qr_id_arr.add(curr_qr_id);
                                totalinsert_count++;
                            }

                            if (qr_id_arr != null && qr_id_arr.size() > 0)
                            {
                                btnOk.setVisibility(View.VISIBLE);
                            }
                            else {
                                btnOk.setVisibility(View.GONE);
                            }

                            click_count = 1;
                            int totalCount = Integer.parseInt(total_insert_count);

                            textViewPcQr = new TextView[totalCount +1];

                            int currindex;
                            int blockStart = 0;
                            int printedBundlecnt = 0;

                            for (int rowCnt = 0; rowCnt < maxRowcnt; rowCnt++) {

                                row = new TableRow(this);
                                row.setLayoutParams(new TableRow.LayoutParams(
                                        TableRow.LayoutParams.MATCH_PARENT,
                                        TableRow.LayoutParams.WRAP_CONTENT));
                                row.setPadding(5, 20, -10, 5);

                                int printedColCnt = 0;

                                for (int colCnt = 0; colCnt < maxColCnt; colCnt++) {
                                    currindex = bundleNosIdxArray[rowCnt][colCnt];
                                    int qr_id = 0;
                                    String bundle_no = "";

                                    try {
                                        if (currindex >= 0 && currindex < scan_qr_id_arr.size()) {
                                            qr_id = Integer.parseInt(String.valueOf(scan_qr_id_arr.get(currindex)));
                                            bundle_no = bundle_no_arr.get(Integer.parseInt(String.valueOf(qr_id).trim()));

                                            textViewPcQr[currindex] = new TextView(this);
                                            TableRow.LayoutParams tvParams = new TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 1f);
                                            tvParams.setMargins(8, 8, 8, 8);
                                            textViewPcQr[currindex].setLayoutParams(tvParams);

                                            textViewPcQr[currindex].setPadding(10, 16, 10, 16);
                                            textViewPcQr[currindex].setText(bundle_no);
                                            textViewPcQr[currindex].setGravity(Gravity.CENTER);
                                            textViewPcQr[currindex].setBackgroundResource(R.drawable.border_white);
                                            textViewPcQr[currindex].setId(qr_id);

                                            try {
                                                text_view_hashmap.put(qr_id, currindex);
                                            } catch (Exception e) {
                                                e.printStackTrace();
                                            }

                                            if (totalinsert_count > 0 && Integer.parseInt(bundleno) == Integer.parseInt(bundle_no)) {
                                                Date c = Calendar.getInstance().getTime();
                                                SimpleDateFormat df = new SimpleDateFormat("dd-MMM-yyyy HH:mm:ss", Locale.getDefault());
                                                String formattedDate = df.format(c);
                                                scan_date_time_hashmap.put(qr_id, formattedDate);

                                                textViewPcQr[currindex].setBackgroundResource(R.drawable.border);
                                                textViewPcQr[currindex].setOnClickListener(v -> {
                                                    String clickedText = ((TextView) v).getText().toString();
                                                    int clicked_id = ((TextView) v).getId();
                                                    new AlertDialog.Builder(Inbuilt_Scanner_Multiple_Barcode_Activity.this)
                                                            .setTitle("Already Scanned")
                                                            .setMessage("Bundle No :" + Integer.parseInt(clickedText)
                                                                    + "\nScanned On : " + scan_date_time_hashmap.get(clicked_id)
                                                                    + "\nScanned By : " + username)
                                                            .setPositiveButton("OK", (dialog, which) -> dialog.dismiss())
                                                            .show();
                                                });
                                            }

                                            int currentIndexId = textViewPcQr[currindex].getId();

                                            if (scanned_qr_id_arr != null && scanned_qr_id_arr.contains(currentIndexId))
                                            {
                                                textViewPcQr[currindex].setBackgroundResource(R.drawable.border_green);
                                                textViewPcQr[currindex].setOnClickListener(v -> {
                                                    int current_click_text_id = v.getId();
                                                    if (scanned_qr_st_arr_hashmap.containsKey(current_click_text_id)) {
                                                        String clickedText = ((TextView) v).getText().toString();
                                                        new AlertDialog.Builder(Inbuilt_Scanner_Multiple_Barcode_Activity.this)
                                                                .setTitle("Already Scanned")
                                                                .setMessage("Bundle No :" + Integer.parseInt(clickedText)
                                                                        + "\n" + String.valueOf(scanned_qr_st_arr_hashmap.get(current_click_text_id)))
                                                                .setPositiveButton("OK", (dialog, which) -> dialog.dismiss())
                                                                .show();
                                                    }
                                                });
                                            }
                                            else if (pending_scan_qr_id_arr != null && pending_scan_qr_id_arr.contains(currentIndexId))
                                            {
                                                textViewPcQr[currindex].setBackgroundResource(R.drawable.border_red);
                                                textViewPcQr[currindex].setOnClickListener(v -> {
                                                    int current_click_text_id = v.getId();
                                                    if (pending_qr_st_arr.containsKey(current_click_text_id)) {
                                                        String clickedText = ((TextView) v).getText().toString();
                                                        new AlertDialog.Builder(Inbuilt_Scanner_Multiple_Barcode_Activity.this)
                                                                .setTitle(String.valueOf(assign_header_qr_st_arr.get(current_click_text_id)))
                                                                .setMessage("Bundle No :" + Integer.parseInt(clickedText)
                                                                        + "\n" + String.valueOf(pending_qr_st_arr.get(current_click_text_id)))
                                                                .setPositiveButton("OK", (dialog, which) -> dialog.dismiss())
                                                                .show();
                                                    }
                                                });
                                            }

                                            row.addView(textViewPcQr[currindex]);
                                            printedBundlecnt++;
                                            printedColCnt++;
                                        }
                                        else
                                        {
                                            TableRow.LayoutParams tvParams = new TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 1f);
                                            tvParams.setMargins(8, 8, 8, 8);
                                            TextView addlTxtView = new TextView(this);
                                            addlTxtView.setLayoutParams(tvParams);
                                            addlTxtView.setPadding(10, 16, 10, 16);
                                            addlTxtView.setText("");
                                            addlTxtView.setGravity(Gravity.CENTER);
                                            addlTxtView.setId(0);
                                            row.addView(addlTxtView);
                                        }
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                        Log.e("Bipin ", "bundleNosIdxArray Exception : " + e.toString());
                                    }
                                }

                                outprogramtbl.addView(row, new TableLayout.LayoutParams(TableLayout.LayoutParams.MATCH_PARENT, TableLayout.LayoutParams.WRAP_CONTENT));

                            }
                            if(scanned_qr_id_arr.size() > 0 ) {
                                if (scanned_qr_id_arr.contains(Integer.parseInt(String.valueOf(curr_qr_id).trim()))) {
                                    arraycontains = true;
                                    String reason = scanned_qr_st_arr_hashmap.get(Integer.parseInt(String.valueOf(curr_qr_id).trim()));
                                    bundleno = bundle_no_arr.get(Integer.parseInt(String.valueOf(curr_qr_id).trim()));
                                    new AlertDialog.Builder(Inbuilt_Scanner_Multiple_Barcode_Activity.this)
                                            .setTitle("Already Scanned")
                                            .setMessage("Bundle No : "+ bundleno +"\n" +reason )
                                            .setCancelable(false)
                                            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                                public void onClick(DialogInterface arg1, int arg0) {
                                                    arg1.dismiss();

                                                }
                                            }).show();
                                }
                            }
                            else if(pending_scan_qr_id_arr.size() > 0 ) {
                                if (pending_scan_qr_id_arr.contains(Integer.parseInt(String.valueOf(curr_qr_id).trim()))) {
                                    arraycontains = true;
                                    new AlertDialog.Builder(Inbuilt_Scanner_Multiple_Barcode_Activity.this)
                                            .setMessage("This Bundle No" + bundleno + assign_header_qr_st_arr.get(curr_qr_id) +"!")
                                            .setCancelable(false)
                                            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                                public void onClick(DialogInterface arg1, int arg0) {
                                                    arg1.dismiss();

                                                }
                                            }).show();
                                }
                            }
                            Inbuilt_Scanner_Multiple_Barcode_Activity.custPrograssbar_new.closePrograssBar();
                        }
                        else
                        {
                            Inbuilt_Scanner_Multiple_Barcode_Activity.custPrograssbar_new.closePrograssBar();
                            new AlertDialog.Builder(Inbuilt_Scanner_Multiple_Barcode_Activity.this)
                                    .setMessage(message)
                                    .setCancelable(false)
                                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface arg1, int arg0) {
                                            arg1.dismiss();
                                        }
                                    }).show();
                        }
                    }
                    else if (mStatus.equalsIgnoreCase("error"))
                    {
                        Inbuilt_Scanner_Multiple_Barcode_Activity.custPrograssbar_new.closePrograssBar();
                        new AlertDialog.Builder(Inbuilt_Scanner_Multiple_Barcode_Activity.this)
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
            else if(callNo.equalsIgnoreCase("update_qr_scan_queue_bundwisewise"))
            {
                JSONObject jsonObject = new JSONObject(result.toString());
                String mStatus = jsonObject.optString("status");
                String message = jsonObject.optString("message");
                Inbuilt_Scanner_Multiple_Barcode_Activity.custPrograssbar_new.closePrograssBar();
                if (mStatus.equals("success")) {
                    new AlertDialog.Builder(Inbuilt_Scanner_Multiple_Barcode_Activity.this)
                            .setMessage(message)
                            .setCancelable(false)
                            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface arg1, int arg0) {
                                    arg1.dismiss();
                                    onBackPressed();
                                }
                            }).show();
                }
                else {
                    Inbuilt_Scanner_Multiple_Barcode_Activity.custPrograssbar_new.closePrograssBar();
                    new AlertDialog.Builder(Inbuilt_Scanner_Multiple_Barcode_Activity.this)
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


    private void callGetNewQRData(int from_bundle_no, int pageNo, int to_bundle_no) {
        try {

            JSONObject jsonObject = new JSONObject();
            jsonObject.put("qrid", statrt_qrid);
            jsonObject.put("userid", userid);
            jsonObject.put("processorid", processorid);
            jsonObject.put("version_name", myversionName);
            jsonObject.put("from_bundle_no", from_bundle_no);
            jsonObject.put("batch_no", pageNo);

            current_pageNo = pageNo;
            from_bundleno = from_bundle_no;
            tobundle_no = to_bundle_no;

            Inbuilt_Scanner_Multiple_Barcode_Activity.custPrograssbar_new.prograssCreate(this);

            JsonParser jsonParser = new JsonParser();
            Call<JsonObject> call = APIClient.getInterface().get_new_qr_data(
                    (JsonObject) jsonParser.parse(jsonObject.toString()));

            GetResult getResult = new GetResult();
            getResult.setMyListener(this);
            getResult.callForLogin(call, "get_new_qr_data");

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
    private void UpdatescanData() {
        // Upload Audio and update qc scan status
        // Create new table to update qc data
        if (isOnline()){
            Inbuilt_Scanner_Multiple_Barcode_Activity.custPrograssbar_new.prograssCreate(this);

            JSONObject jsonObject = new JSONObject();
            try {

                if (qr_id_arr != null && qr_id_arr.size() > 0)
                {
                    for (int i = 0; i < totalinsert_count; i++) {
                        QR_ID_Array.put(qr_id_arr.get(i));
                    }
                }
                jsonObject.put("userid", userid);
                jsonObject.put("processorid", processorid);
                jsonObject.put("bundle_qr_id_array", QR_ID_Array);
                JsonParser jsonParser = new JsonParser();
                Call<JsonObject> call = APIClient.getInterface().update_qr_scan_queue_bundwisewise((JsonObject) jsonParser.parse(jsonObject.toString()));
                GetResult getResult = new GetResult();
                getResult.setMyListener(this);
                getResult.callForLogin(call, "update_qr_scan_queue_bundwisewise");
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        else {
            new AlertDialog.Builder(Inbuilt_Scanner_Multiple_Barcode_Activity.this)
                    .setMessage("No internet connection!")
                    .setCancelable(false)
                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface d, int arg0) {
                            d.dismiss();
                        }
                    }).show();
        }
    }


    public void add_scan_data() {

        totalinsert_count = 0;
        qr_id_arr.clear();
        scan_qr_id_arr.clear();
        bundle_no_arr.clear();
        pending_scan_qr_id_arr.clear();
        pending_qr_st_arr.clear();
        assign_header_qr_st_arr.clear();
        scanned_qr_st_arr_hashmap.clear();

        text_view_hashmap.clear();
//        qr_id_arr.clear();

        if (bundleNosIdxArray != null && bundleNosIdxArray.length > 0) {
            bundleNosIdxArray = null;
        }
        try {
            if (scan_qr_data_details.length() > 0) {
                Iterator<String> sc_data = scan_qr_data_details.keys();
                while (sc_data.hasNext()) {
                    String key = sc_data.next();
                    if (scan_qr_data_details.get(key) instanceof JSONObject) {
                        int qrid = Integer.parseInt(((JSONObject) scan_qr_data_details.get(key)).getString("qrid"));
                        String scan_status = ((JSONObject) scan_qr_data_details.get(key)).getString("scan_status");
                        String bundleno = ((JSONObject) scan_qr_data_details.get(key)).getString("bundleno");
                        String scan_ts = ((JSONObject) scan_qr_data_details.get(key)).getString("scan_ts");
                        String assigned_header = ((JSONObject) scan_qr_data_details.get(key)).getString("assigned_header");
                        String assigned_msg = ((JSONObject) scan_qr_data_details.get(key)).getString("assigned_msg");
                        if(scan_status.equals("N"))
                        {
                            scan_qr_id_arr.add(qrid);
                            bundle_no_arr.put(qrid,bundleno);
                        }
                        else if(scan_status.equals("P"))
                        {
                            scan_qr_id_arr.add(qrid);
                            pending_scan_qr_id_arr.add(qrid);
                            pending_qr_st_arr.put(qrid,assigned_msg);
                            assign_header_qr_st_arr.put(qrid,assigned_header);
                            bundle_no_arr.put(qrid,bundleno);
                        }
                        else if(scan_status.equals("Y"))
                        {
                            scan_qr_id_arr.add(qrid);
                            scanned_qr_id_arr.add(qrid);
                            scanned_qr_st_arr_hashmap.put(qrid,scan_ts);
                            bundle_no_arr.put(qrid,bundleno);
                        }
                    }
                }
            }
        }
        catch (JSONException e) {
            e.printStackTrace();
        }

        bundleNosIdxArray = new int[maxRowcnt][maxColCnt];

        int rowCnt = 0;
        int curIdx = 0;
        int rowIdx = 0;
        for (int colIdx = 0; colIdx < maxColCnt; colIdx++)
        {
            rowCnt = (((colIdx+1)*maxRowcnt) - maxRowcnt);
            for (curIdx = rowCnt, rowIdx=0; rowIdx < maxRowcnt; curIdx++, rowIdx++)
            {
                bundleNosIdxArray[rowIdx][colIdx] = curIdx;
            }
        }
    }


    public void data_added() {

        Boolean arraycontain_data = false;
        Boolean startcontain_data = false;
        Boolean not_allow_contain_data = false;
        String reason = "";
        String header = "";
        String bundleno = "";

        Log.e("Bipin","qr_id_arr : " +qr_id_arr);
        Log.e("Bipin","curr_qr_id : " +curr_qr_id);

//        if (qr_id_arr.contains(curr_qr_id))
        if (qr_id_arr.contains(Integer.parseInt(String.valueOf(curr_qr_id).trim())))
        {
            startcontain_data = true;
            reason = scan_date_time_hashmap.get(Integer.parseInt(String.valueOf(curr_qr_id).trim()));
            bundleno = bundle_no_arr.get(Integer.parseInt(String.valueOf(curr_qr_id).trim()));
        }
        if (pending_scan_qr_id_arr.contains(Integer.parseInt(String.valueOf(curr_qr_id).trim())))
        {
            not_allow_contain_data = true;

            reason = pending_qr_st_arr.get(Integer.parseInt(String.valueOf(curr_qr_id).trim()));
            header = assign_header_qr_st_arr.get(Integer.parseInt(String.valueOf(curr_qr_id).trim()));
            bundleno = bundle_no_arr.get(Integer.parseInt(String.valueOf(curr_qr_id).trim()));
        }
        if (scanned_qr_id_arr.contains(Integer.parseInt(String.valueOf(curr_qr_id).trim())))
        {
            arraycontain_data = true;
            reason = scanned_qr_st_arr_hashmap.get(Integer.parseInt(String.valueOf(curr_qr_id).trim()));
            bundleno = bundle_no_arr.get(Integer.parseInt(String.valueOf(curr_qr_id).trim()));
        }
        if(startcontain_data)
        {
            Log.e("Bipin","startcontain_data : " +startcontain_data);
            Inbuilt_Scanner_Multiple_Barcode_Activity.custPrograssbar_new.closePrograssBar();
            new AlertDialog.Builder(Inbuilt_Scanner_Multiple_Barcode_Activity.this)
                    .setTitle("Already Scanned")
                    .setMessage("Bundle No : "+ bundleno +"\n"+"Scanned On : "+ reason +"\nScanned By : "+username)
                    .setCancelable(false)
                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface arg1, int arg0) {
                            arg1.dismiss();
                        }
                    }).show();
        }
        else if(not_allow_contain_data)
        {
            Log.e("Bipin","arraycontain_data : " +arraycontain_data);
            Inbuilt_Scanner_Multiple_Barcode_Activity.custPrograssbar_new.closePrograssBar();
            new AlertDialog.Builder(Inbuilt_Scanner_Multiple_Barcode_Activity.this)
                    .setTitle(header)
                    .setMessage("Bundle No : "+ bundleno +"\n"+ reason)
                    .setCancelable(false)
                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface arg1, int arg0) {
                            arg1.dismiss();
                        }
                    }).show();
        }
        else if(arraycontain_data)
        {
            Inbuilt_Scanner_Multiple_Barcode_Activity.custPrograssbar_new.closePrograssBar();
            new AlertDialog.Builder(Inbuilt_Scanner_Multiple_Barcode_Activity.this)
                    .setTitle("Already Scanned")
                    .setMessage("Bundle No : "+ bundleno +"\n" +reason )
                    .setCancelable(false)
                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface arg1, int arg0) {
                            arg1.dismiss();
                        }
                    }).show();
        }
        else {


            if(text_view_hashmap.containsKey(Integer.parseInt(String.valueOf(curr_qr_id).trim())))
            {
                int current_index = text_view_hashmap.get(Integer.parseInt(String.valueOf(curr_qr_id).trim()));
                Date c = Calendar.getInstance().getTime();
                SimpleDateFormat df = new SimpleDateFormat("dd-MMM-yyyy HH:mm:ss");
                String formattedDate = df.format(c);
                scan_date_time_hashmap.put(Integer.parseInt(String.valueOf(curr_qr_id).trim()), formattedDate);
                textViewPcQr[current_index].setBackgroundResource(R.drawable.border);
                textViewPcQr[current_index].setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // Show an alert (dialog) when the TextView is clicked
                        String clickedText = ((TextView) v).getText().toString();
                        int clicked_id = ((TextView) v).getId();
                        AlertDialog.Builder builder = new AlertDialog.Builder(Inbuilt_Scanner_Multiple_Barcode_Activity.this);
                        builder.setTitle("Bundle Scanned On")
                                .setMessage("Bundle No : "+Integer.parseInt(clickedText) +"\nScanned On : " + scan_date_time_hashmap.get(clicked_id) +"\nScanned By : " +username)
                                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        // Do something when OK button is clicked
                                        dialog.dismiss();
                                    }
                                })
                                .show();
                    }
                });
                qr_id_arr.add(curr_qr_id);
                totalinsert_count++;
                btnOk.setVisibility(View.VISIBLE);
                Inbuilt_Scanner_Multiple_Barcode_Activity.custPrograssbar_new.closePrograssBar();
            }
            else
            {
//                Log.e("Bipin","text_view_hashmap : " +text_view_hashmap);
                Inbuilt_Scanner_Multiple_Barcode_Activity.custPrograssbar_new.closePrograssBar();
                AlertDialog alertDialog = new AlertDialog.Builder(Inbuilt_Scanner_Multiple_Barcode_Activity.this)
                        .setMessage("This Bundle # Not Avilable in This Size / Please Scan Valid QR!")
                        .setCancelable(false)
                        .setNegativeButton("Exit", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface arg1, int arg0) {
                                arg1.dismiss();
                            }
                        }).show();
                alertDialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(Color.RED);
            }
        }
    }


    @Override
    public void onBackPressed() {
        Intent intent = new Intent(Inbuilt_Scanner_Multiple_Barcode_Activity.this, MainActivity.class);
        intent.putExtra("username", username);
        intent.putExtra("processorid", processorid);
        intent.putExtra("userid", userid);
        startActivity(intent);
        finish();
    }

    public boolean isOnline() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        return cm.getActiveNetworkInfo() != null && cm.getActiveNetworkInfo().isConnectedOrConnecting();
    }

}