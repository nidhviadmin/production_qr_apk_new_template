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
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupMenu;
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

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import retrofit2.Call;


public class ScannerSelectionBarcodeActivity extends BaseActivity implements
        BarcodeReader.BarcodeListener, BarcodeReader.TriggerListener, View.OnClickListener, GetResult.MyListener {

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
    String userid;
    String username;

    public ScannerSelectionBarcodeActivity() {
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_base);  // Changed to base layout
        setupDrawer();  // Setup drawer before using it

        // Inflate your actual content into the content_frame
        View content = getLayoutInflater().inflate(R.layout.activity_selection_barcode,
                findViewById(R.id.content_frame), true);

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
                    PopupMenu popup = new PopupMenu(ScannerSelectionBarcodeActivity.this, imageView);
                    toggleDrawer();
                    popup.show();
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
        if(arrayString.length > 1 && qrprefix.equals("PC"))
        {
            if(isOnline()) {

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
            }
            else {

                AlertDialog alertDialog = new AlertDialog.Builder(ScannerSelectionBarcodeActivity.this)
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
            if (callNo.equalsIgnoreCase("piecewise_scan_and_update")) {
                JSONObject json = new JSONObject(result.toString());
                String status = json.optString("status");
                String mess = json.optString("message");

                if (status.equals("version_check")) {
                    JSONObject jsonObj = json.getJSONObject("data");
                    apkurl = jsonObj.optString("apk_url");
                    app_version_name = jsonObj.optString("app_version_name");

                    AlertDialog alertDialog = new AlertDialog.Builder(ScannerSelectionBarcodeActivity.this)
                            .setMessage(mess)
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

                        final Dialog dialog = new Dialog(ScannerSelectionBarcodeActivity.this);
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
//                            scan_requestcode =1;
//                            PcwiseScanning();
                            }
                        });

                    }
                    else if (mess.equals("Scanned successfully")) {
//                    PcwiseScanning();
                    } else {
                        AlertDialog alertDialog = new AlertDialog.Builder(ScannerSelectionBarcodeActivity.this)
                                .setMessage(mess)
                                .setCancelable(false)
                                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface arg1, int arg0) {
                                        arg1.dismiss();
//                                    scan_requestcode =1;
//                                    PcwiseScanning();
                                    }
                                }).show();
                    }
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

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(ScannerSelectionBarcodeActivity.this, MainActivity.class);
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
