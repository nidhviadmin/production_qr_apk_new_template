package com.bipinexports.productionqr.Activity;

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
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import androidx.annotation.RequiresApi;
import com.google.android.material.snackbar.Snackbar;
import androidx.core.content.FileProvider;
import androidx.appcompat.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
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

import com.bipinexports.productionqr.APIClient;
import com.bipinexports.productionqr.GetResult;
import com.bipinexports.productionqr.R;
import com.bipinexports.productionqr.SessionManagement;
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
import com.loopj.android.http.AsyncHttpClient;

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

public class Rollwise_Inbuilt_Scanner_Barcode_Activity extends AppCompatActivity implements
        BarcodeReader.BarcodeListener, BarcodeReader.TriggerListener, OnClickListener, GetResult.MyListener {

    private AidcManager mAidcManager;
    private BarcodeReader mBarcodeReader;
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

    String curr_qr_id, parentrollid, rollno, rollcode, weight, status,orderid, styleid, partid , joborderno, shipcode, colors, styleno, partname, total_insert_count, index_no;

    List qr_id_arr = new ArrayList<String>();

    List scan_qr_id_arr = new ArrayList<String>();
    List scanned_qr_id_arr = new ArrayList<String>();
    JSONArray QR_ID_Array = new JSONArray();
    List scan_rollwise_qr_arr = new ArrayList<String>();

    JSONObject scan_qr_data_details;

    int cick_count =0;
    int total_data =0;
    TextView[] textViewPcQr= new TextView[200];

    HashMap<Integer, Integer> text_view_hashmap = new HashMap<Integer, Integer>();
    HashMap<Integer, String> scan_date_time_hashmap = new HashMap<Integer, String>();
    int totalinsert_count =0;
    HashMap<Integer, String> scanned_qr_st_arr_hashmap = new HashMap<Integer, String>();
    HashMap<Integer, String> rollno_arr_hashmap = new HashMap<Integer, String>();

    JSONObject rollinfo;

    LinearLayout linear_rollwise_details;

    TextView txtJobNo, txtShipCode, txtStyle, text_Color;
    LinearLayout liner_bundle_details, linear_programdata, linear_layout_btn;

    Button btnOk;
    Button btnCancel;

    String processorid;
    String userid;
    String username;

    TableLayout outprogramtbl;
    int index = 1;

    public static CustPrograssbar custPrograssbar;

    public Rollwise_Inbuilt_Scanner_Barcode_Activity() {
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rollwise_inbuild_scanner_barcode);

        custPrograssbar = new CustPrograssbar();

        versioncode();

        if(Build.MODEL.startsWith("VM1A")) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        }
        else{
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }

        imageView = findViewById(R.id.imgd);
        imageView.setOnClickListener(this);
        txtUser = findViewById(R.id.txtUser);

        session = new SessionManagement(getApplicationContext());
        HashMap<String, String> user = session.getUserDetails();
        this.User = user.get(SessionManagement.KEY_USER);
        unitid = user.get(SessionManagement.KEY_UNITID);
        txtUser.setText("Hello " + this.User);

        processorid = getIntent().getStringExtra("processorid");
        userid = getIntent().getStringExtra("userid");
        username = getIntent().getStringExtra("name");

        txtJobNo = findViewById(R.id.txtJobNo);
        txtShipCode = findViewById(R.id.txtShipCode);
        text_Color = findViewById(R.id.text_Color);
        txtStyle = findViewById(R.id.txtStyle);

        liner_bundle_details = findViewById(R.id.liner_bundle_details);
        liner_bundle_details.setVisibility(View.GONE);

        linear_programdata = findViewById(R.id.linear_programdata);
        linear_programdata.setVisibility(View.GONE);

        linear_layout_btn = findViewById(R.id.linear_layout_btn);
        linear_layout_btn.setVisibility(View.GONE);
        btnOk = findViewById(R.id.btnOk);
        btnCancel = findViewById(R.id.btnCancel);

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

        linear_rollwise_details = findViewById(R.id.linear_rollwise_details);
        linear_rollwise_details.setVisibility(View.GONE);
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
                    PopupMenu popup = new PopupMenu(Rollwise_Inbuilt_Scanner_Barcode_Activity.this, imageView);
                    HashMap<String, String> user = session.getUserDetails();
                    String username = user.get(SessionManagement.KEY_USER);
                    String userid = user.get(SessionManagement.KEY_USER_ID);

                    Intent intent = new Intent(Rollwise_Inbuilt_Scanner_Barcode_Activity.this, HomeActivity.class);
                    intent.putExtra("openDrawer", true);
                    intent.putExtra("username", username);
                    intent.putExtra("userid", userid);
                    intent.putExtra("processorid", processorid);
                    startActivity(intent);
                    popup.show();
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
        String qrid = event.getBarcodeData();
        String[] arrayString = qrid.split("-");

        String qrprefix = null;

        if(arrayString.length > 1) {
            qrprefix = arrayString[0];
            qrid = arrayString[1];
        }
        if(arrayString.length > 1 && qrprefix.equals("RL"))
        {
            SessionManagement session = new SessionManagement(getApplicationContext());
            HashMap<String, String> user = session.getUserDetails();
            String processorid = user.get(SessionManagement.KEY_PROCESSOR_ID);
            String userid = user.get(SessionManagement.KEY_USER_ID);
            String isqc = user.get(SessionManagement.KEY_ISQC);

            if(isOnline()) {
                if (isqc.equals("N"))
                {
                    Log.e("Bipin","cick_count : " +cick_count);
                    if(cick_count == 0)
                    {
                        JSONObject jsonObject = new JSONObject();
                        try {
                            jsonObject.put("rollid", qrid);
                            jsonObject.put("userid", userid);
                            jsonObject.put("processorid", processorid);
                            jsonObject.put("version_name", myversionName);

                            JsonParser jsonParser = new JsonParser();
                            Call<JsonObject> call = APIClient.getInterface().getjobdetails((JsonObject) jsonParser.parse(jsonObject.toString()));
                            GetResult getResult = new GetResult();
                            getResult.setMyListener(this);
                            getResult.callForLogin(call, "getjobdetails");
                        }
                        catch (JSONException e)
                        {
                            e.printStackTrace();
                        }
                    }
                    else {
                        Log.e("Bipin","cick_count  second: " +cick_count);
                        if(total_data > 199)
                        {
                            AlertDialog alertDialog = new AlertDialog.Builder(Rollwise_Inbuilt_Scanner_Barcode_Activity.this)
                                    .setMessage("Please Submit After Then Scan")
                                    .setCancelable(false)
                                    .setNegativeButton("Exit", new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface arg1, int arg0) {
                                            arg1.dismiss();
                                        }
                                    }).show();
                            alertDialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(Color.RED);
                        }
                        else{

                            Log.e("Bipin","cick_count  qrid: " +qrid);
                            curr_qr_id = qrid;
                            data_added();
                        }
                    }
                }
                else {

                    AlertDialog alertDialog = new AlertDialog.Builder(Rollwise_Inbuilt_Scanner_Barcode_Activity.this)
                            .setMessage("QC Employee Not Allowed Scan")
                            .setCancelable(false)
                            .setNegativeButton("Exit", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface arg1, int arg0) {
                                    arg1.dismiss();
                                }
                            }).show();
                    alertDialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(Color.RED);
                }
            }
            else {

                AlertDialog alertDialog = new AlertDialog.Builder(Rollwise_Inbuilt_Scanner_Barcode_Activity.this)
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
            Handler h = new Handler(Looper.getMainLooper());
            h.post(new Runnable() {
                public void run() {
                    new AlertDialog.Builder(mContext)
                            .setTitle("Error")
                            .setMessage("Only Piecewise Scanning is supported as of now.\nContact Admin!")
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
            if (callNo.equalsIgnoreCase("getjobdetails")) {
                JSONObject json = new JSONObject(result.toString());
                String mStatus = json.optString("status");
                String message = json.optString("message");

                if (mStatus.equals("version_check")) {
                    JSONObject jsonObj = json.getJSONObject("data");
                    apkurl = jsonObj.optString("apk_url");
                    app_version_name = jsonObj.optString("app_version_name");

                    AlertDialog alertDialog = new AlertDialog.Builder(Rollwise_Inbuilt_Scanner_Barcode_Activity.this)
                            .setMessage(message)
                            .setCancelable(false)
                            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                @RequiresApi(api = Build.VERSION_CODES.M)
                                public void onClick(DialogInterface arg1, int arg0) {
                                    arg1.dismiss();
                                    downloadFile(apkurl);
                                }
                            }).show();
                }
                else {

                    apkFileName = "production_qr_v" + myversionName + ".apk";
                    File externalFilesDir = getExternalFilesDir(null);
                    String previousApkFilePath = externalFilesDir.getAbsolutePath() + "/" + apkFileName;
                    File previousApkFile = new File(previousApkFilePath);
                    if (previousApkFile.exists()) {
                        if (previousApkFile.delete()) {
                            // The previous APK file has been successfully deleted
                        } else {
                            // There was an issue deleting the previous APK file
                        }
                    }

                    if (message.equals("Fetched successfully"))
                    {
                        JSONObject jsonObj = json.getJSONObject("data");
                        rollinfo = jsonObj.getJSONObject("rollinfo");
                        scan_qr_data_details = jsonObj.getJSONObject("allrolls");

                        curr_qr_id =  rollinfo.getString("rollid");
                        parentrollid = rollinfo.getString("parentrollid");
                        rollno = rollinfo.getString("rollno");
                        rollcode = rollinfo.getString("rollcode");
                        weight = rollinfo.getString("weight");
                        status = rollinfo.getString("status");
                        orderid = rollinfo.getString("orderid");
                        styleid = rollinfo.getString("styleid");
                        shipcode = rollinfo.getString("shipcode");
                        partid = rollinfo.getString("partid");
                        joborderno = rollinfo.getString("joborderno");
                        styleno = rollinfo.optString("styleno");
                        colors = rollinfo.optString("color");
                        partname = rollinfo.optString("partname");

                        linear_rollwise_details.setVisibility(View.VISIBLE);

                        int insert_count = scan_qr_data_details.length();
                        total_insert_count = String.valueOf(insert_count);
                        add_scan_data();

                        txtJobNo.setText(joborderno);
                        txtShipCode.setText(shipcode);
                        text_Color.setText(colors +"  |  " + partname);
                        txtStyle.setText(styleno);

                        liner_bundle_details.setVisibility(View.VISIBLE);
                        linear_programdata.setVisibility(View.VISIBLE);
                        linear_layout_btn.setVisibility(View.VISIBLE);

                        Boolean arraycontains = false;
                        outprogramtbl = findViewById(R.id.AddProgramData);
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
                        cick_count = 1;

                        int maxRowcnt = 25;
                        int maxColCnt = 4;

                        int currindex = 0;
                        int indexIncrementor = 0;
                        int printedBundlecnt = 0;
                        int rowCnt = 0;
                        int colCnt = 0;
                        for(rowCnt = 0; rowCnt< maxRowcnt;)
                        {
                            row = new TableRow(this);
                            row.setId(index);
                            row.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT));
                            row.setPadding(5, 20, -10, 5);
                            for(colCnt=0;colCnt<maxColCnt;colCnt++,indexIncrementor+=maxRowcnt)
                            {
                                currindex = rowCnt + 1 + indexIncrementor;
                                if(currindex <= Integer.parseInt(total_insert_count))
                                {
                                    int qr_id = Integer.parseInt(String.valueOf(scan_qr_id_arr.get(currindex - 1)));
                                    textViewPcQr[currindex] = new TextView(this);
                                    textViewPcQr[currindex].setLayoutParams(new TableRow.LayoutParams(10, TableRow.LayoutParams.WRAP_CONTENT));
                                    textViewPcQr[currindex].setPadding(5, 20, 5, 5);
                                    textViewPcQr[currindex].setText(currindex +". "+String.valueOf(scan_rollwise_qr_arr.get(currindex - 1)));
                                    textViewPcQr[currindex].setWidth(1);  // Adjust width as needed
                                    // textViewPcQr.setPadding(5, 20, 5, 5);
                                    textViewPcQr[currindex].setBackgroundResource(R.drawable.border_white);
                                    textViewPcQr[currindex].setGravity(Gravity.CENTER);
                                    textViewPcQr[currindex].setId(qr_id);

                                    try
                                    {
                                        text_view_hashmap.put(qr_id, currindex);
                                    }
                                    catch(Exception e)
                                    {
                                        e.printStackTrace();
                                    }

                                    if(totalinsert_count > 0 && Integer.parseInt(index_no) == currindex)
                                    {
                                        Date c = Calendar.getInstance().getTime();
                                        SimpleDateFormat df = new SimpleDateFormat("dd-MMM-yyyy HH:mm:ss");
                                        String formattedDate = df.format(c);

                                        scan_date_time_hashmap.put(qr_id, formattedDate);

                                        textViewPcQr[currindex].setBackgroundResource(R.drawable.border);
                                        textViewPcQr[currindex].setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {
                                                // Show an alert (dialog) when the TextView is clicked

                                                String clickedText = ((TextView) v).getText().toString();
                                                int clicked_id = ((TextView) v).getId();
                                                AlertDialog.Builder builder = new AlertDialog.Builder(Rollwise_Inbuilt_Scanner_Barcode_Activity.this);
                                                builder.setTitle("Roll Scanned Details")
                                                        .setMessage(rollno_arr_hashmap.get(clicked_id) + "\n Scanned On : "+ scan_date_time_hashmap.get(clicked_id) + "\n Scanned By : " + username)
                                                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                                            public void onClick(DialogInterface dialog, int which) {
                                                                // Do something when OK button is clicked
                                                                dialog.dismiss();
                                                            }
                                                        })
                                                        .show();
                                            }
                                        });
                                    }
                                    int currentIndexId = textViewPcQr[currindex].getId();

                                    if(scanned_qr_id_arr.size() > 0 )
                                    {
                                        if (scanned_qr_id_arr.contains(currentIndexId))
                                        {
                                            textViewPcQr[currindex].setBackgroundResource(R.drawable.border_green);
                                            textViewPcQr[currindex].setOnClickListener(new View.OnClickListener() {
                                                @Override
                                                public void onClick(View v) {
                                                    // Show an alert (dialog) when the TextView is clicked
                                                    int current_cilck_text_id = ((TextView) v).getId();
                                                    if(scanned_qr_st_arr_hashmap.containsKey(current_cilck_text_id)) {
                                                        String clickedText = ((TextView) v).getText().toString();
                                                        AlertDialog.Builder builder = new AlertDialog.Builder(Rollwise_Inbuilt_Scanner_Barcode_Activity.this);
                                                        builder.setTitle("Rollwise Scanned Details")
                                                                .setMessage(String.valueOf(scanned_qr_st_arr_hashmap.get(current_cilck_text_id)))
                                                                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                                                    public void onClick(DialogInterface dialog, int which) {
                                                                        // Do something when OK button is clicked
                                                                        dialog.dismiss();
                                                                    }
                                                                })
                                                                .show();
                                                    }
                                                }
                                            });
                                        }
                                    }
                                    row.addView(textViewPcQr[currindex]);
                                    printedBundlecnt++;
                                }
                            }
                            outprogramtbl.addView(row, new TableLayout.LayoutParams(TableLayout.LayoutParams.MATCH_PARENT, TableLayout.LayoutParams.WRAP_CONTENT));
                            if(indexIncrementor > 100)
                            {
                                indexIncrementor = 100;
                            }
                            else
                            {
                                indexIncrementor = 0;
                            }
                            rowCnt++;
                            if(Integer.parseInt(total_insert_count) > 100)
                            {
                                if(printedBundlecnt >= 100 &&  Integer.parseInt(total_insert_count) > printedBundlecnt)
                                {
                                    rowCnt = 0;
                                    maxRowcnt = 25;
                                    colCnt = 0;
                                    maxColCnt = 4;
                                    indexIncrementor=100;
                                    printedBundlecnt = 0;
                                }
                            }
                        }
                        if(scanned_qr_id_arr.size() > 0 ) {
                            if (scanned_qr_id_arr.contains(Integer.parseInt(String.valueOf(curr_qr_id).trim()))) {
                                arraycontains = true;
                                new AlertDialog.Builder(Rollwise_Inbuilt_Scanner_Barcode_Activity.this)
                                        .setMessage("Roll No : " + "R"+rollno + " and Weight : "+ weight +"Kgs "+"\n Already Scanned!")
                                        .setCancelable(false)
                                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface arg1, int arg0) {
                                                arg1.dismiss();

                                            }
                                        }).show();
                            }
                        }
                        Rollwise_Inbuilt_Scanner_Barcode_Activity.custPrograssbar.closePrograssBar();

                    }
                    else if (message.equals("Scanned successfully")) {
                    } else {
                        AlertDialog alertDialog = new AlertDialog.Builder(Rollwise_Inbuilt_Scanner_Barcode_Activity.this)
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
            else  if(callNo.equalsIgnoreCase("updateproductionrolls"))
            {
                JSONObject jsonObject = new JSONObject(result.toString());
                String mStatus = jsonObject.optString("status");
                String message = jsonObject.optString("message");
                Rollwise_Inbuilt_Scanner_Barcode_Activity.custPrograssbar.closePrograssBar();
                if (mStatus.equals("success")) {
                    new AlertDialog.Builder(Rollwise_Inbuilt_Scanner_Barcode_Activity.this)
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
                    Rollwise_Inbuilt_Scanner_Barcode_Activity.custPrograssbar.closePrograssBar();
                    new AlertDialog.Builder(Rollwise_Inbuilt_Scanner_Barcode_Activity.this)
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


    public void add_scan_data() {
        int indexpost = 0 ;
        try {
            if (scan_qr_data_details.length() > 0) {
                Iterator<String> sc_data = scan_qr_data_details.keys();
                while (sc_data.hasNext()) {
                    String key = sc_data.next();
                    if (scan_qr_data_details.get(key) instanceof JSONObject) {
                        int rollid = Integer.parseInt(((JSONObject) scan_qr_data_details.get(key)).getString("rollid"));
                        String parentrollid = ((JSONObject) scan_qr_data_details.get(key)).getString("parentrollid");
                        String rollno = ((JSONObject) scan_qr_data_details.get(key)).getString("rollno");
                        String rollcode = ((JSONObject) scan_qr_data_details.get(key)).getString("rollcode");
                        String status = ((JSONObject) scan_qr_data_details.get(key)).getString("status");
                        String weight = ((JSONObject) scan_qr_data_details.get(key)).getString("weight");

                        indexpost++;
                        if (curr_qr_id.contains(String.valueOf(rollid).trim()))
                        {
                            index_no = String.valueOf(indexpost);
                        }
                        if(status.equals("A"))
                        {
                            scan_qr_id_arr.add(rollid);
                            scan_rollwise_qr_arr.add("R"+rollno+"\n"+weight + " Kgs");
                        }
                        else if(status.equals("S"))
                        {
                            String scanned_by = ((JSONObject) scan_qr_data_details.get(key)).getString("scanned_by");
                            String scanned_on = ((JSONObject) scan_qr_data_details.get(key)).getString("scanned_on");

                            scan_qr_id_arr.add(rollid);
                            scanned_qr_id_arr.add(rollid);
                            scanned_qr_st_arr_hashmap.put(rollid," Roll No : R"+rollno  +"\n Weight : "+ weight + " Kgs" +"\n Scanned On : "+ scanned_on +"\n Scanned By  : "+ scanned_by);
                            scan_rollwise_qr_arr.add("R"+rollno+"\n"+weight + " Kgs");
                        }
                        String bud = "Roll No : "+ "R"+rollno  +"\n Weight : "+ weight + " Kgs";
                        rollno_arr_hashmap.put(rollid,bud);
                    }
                }
            }
        }
        catch (JSONException e) {
            e.printStackTrace();
        }
    }


    public void data_added() {

        Boolean arraycontain_data = false;

        if (qr_id_arr.contains(curr_qr_id))
        {
            arraycontain_data = true;
        }
        if (scanned_qr_id_arr.contains(Integer.parseInt(String.valueOf(curr_qr_id).trim())))
        {
            arraycontain_data = true;
        }
        if(arraycontain_data)
        {
            // Ensure the progress bar is closed on the main thread
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Rollwise_Inbuilt_Scanner_Barcode_Activity.custPrograssbar.closePrograssBar();

                    // Create and show the alert dialog
                    AlertDialog alertDialog = new AlertDialog.Builder(Rollwise_Inbuilt_Scanner_Barcode_Activity.this)
                            .setMessage("Already Scanned!")
                            .setCancelable(false)
                            .setNegativeButton("Exit", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface arg1, int arg0) {
                                    arg1.dismiss();
                                }
                            }).show();

                    // Set the text color for the negative button
                    alertDialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(Color.RED);
                }
            });
        }
        else {
            if(text_view_hashmap.containsKey(Integer.parseInt(curr_qr_id)))
            {
                total_data++;
                final int current_index = text_view_hashmap.get(Integer.parseInt(curr_qr_id));
                Date c = Calendar.getInstance().getTime();
                SimpleDateFormat df = new SimpleDateFormat("dd-MMM-yyyy HH:mm:ss");
                String formattedDate = df.format(c);
                scan_date_time_hashmap.put(Integer.parseInt(String.valueOf(curr_qr_id).trim()), formattedDate);

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        textViewPcQr[current_index].setBackgroundResource(R.drawable.border);
                        textViewPcQr[current_index].setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                // Show an alert (dialog) when the TextView is clicked
                                String clickedText = ((TextView) v).getText().toString();
                                int clicked_id = ((TextView) v).getId();

                                AlertDialog.Builder builder = new AlertDialog.Builder(Rollwise_Inbuilt_Scanner_Barcode_Activity.this);
                                builder.setTitle("Roll Scanned Details")
                                        .setMessage(rollno_arr_hashmap.get(clicked_id) + "\n Scanned On : " + scan_date_time_hashmap.get(clicked_id) + "\n Scanned By : " + username)
                                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int which) {
                                                // Do something when OK button is clicked
                                                dialog.dismiss();
                                            }
                                        })
                                        .show();
                            }
                        });
                    }
                });

                qr_id_arr.add(curr_qr_id);
                totalinsert_count++;
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        btnOk.setVisibility(View.VISIBLE);
                        Rollwise_Inbuilt_Scanner_Barcode_Activity.custPrograssbar.closePrograssBar();
                    }
                });
            }
            else
            {
                Rollwise_Inbuilt_Scanner_Barcode_Activity.custPrograssbar.closePrograssBar();
                AlertDialog alertDialog = new AlertDialog.Builder(Rollwise_Inbuilt_Scanner_Barcode_Activity.this)
                        .setMessage("This Roll No # Not Avilable in This Roll / Please Scan Valid QR!")
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


    private void UpdatescanData() {
        // Upload Audio and update qc scan status
        // Create new table to update qc data
        if (isOnline()){
            AsyncHttpClient asyncHttpClient = new AsyncHttpClient();
            session = new SessionManagement(getApplicationContext());
            HashMap<String, String> user = session.getUserDetails();
            processorid = user.get(SessionManagement.KEY_PROCESSOR_ID);
            userid = user.get(SessionManagement.KEY_USER_ID);
            String userid = user.get(SessionManagement.KEY_USER_ID);

            Rollwise_Inbuilt_Scanner_Barcode_Activity.custPrograssbar.prograssCreate(this);

            JSONObject jsonObject = new JSONObject();
            try {

                if (qr_id_arr != null && qr_id_arr.size() > 0)
                {
                    for (int i = 0; i < totalinsert_count; i++) {
                        QR_ID_Array.put(Integer.parseInt((String) qr_id_arr.get(i)));
                    }
                }
                jsonObject.put("userid", userid);
                jsonObject.put("scanned_by", processorid);
                jsonObject.put("rollids", QR_ID_Array);
                JsonParser jsonParser = new JsonParser();
                Call<JsonObject> call = APIClient.getInterface().updateproductionrolls((JsonObject) jsonParser.parse(jsonObject.toString()));
                GetResult getResult = new GetResult();
                getResult.setMyListener(this);
                getResult.callForLogin(call, "updateproductionrolls");
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        else {
            new AlertDialog.Builder(Rollwise_Inbuilt_Scanner_Barcode_Activity.this)
                    .setMessage("No internet connection!")
                    .setCancelable(false)
                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface d, int arg0) {
                            d.dismiss();
                        }
                    }).show();
        }
    }

    @Override
    public void onBackPressed() {
        SessionManagement session = new SessionManagement(getApplicationContext());
        HashMap<String, String> user = session.getUserDetails();
        Intent intent = new Intent(Rollwise_Inbuilt_Scanner_Barcode_Activity.this, MainActivity.class);
        intent.putExtra("name", user.get(SessionManagement.KEY_USER));
        intent.putExtra("userid", user.get(SessionManagement.KEY_USER_ID));
        intent.putExtra("processorid", user.get(SessionManagement.KEY_PROCESSOR_ID));
        startActivity(intent);
        finish();
    }

    public boolean isOnline() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        return cm.getActiveNetworkInfo() != null && cm.getActiveNetworkInfo().isConnectedOrConnecting();
    }

}
