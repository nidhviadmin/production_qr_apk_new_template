package com.bipinexports.productionqr.Activity;

import android.app.AlertDialog;
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
import androidx.appcompat.app.AppCompatActivity;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
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

import retrofit2.Call;

public class Laywise_Multiple_Bundle_QR_Scanner_Activity extends AppCompatActivity implements View.OnClickListener, GetResult.MyListener {

    EditText txtScanData;
    ImageView imageView;
    TextView txtUser;
    SessionManagement session;

    String processorid;
    String userid;
    String username;
    String User, unitid;
    String Id;

    ProgressDialog mProgressDialog;
    String apkFileName;

    public static CustPrograssbar custPrograssbar;
    String myversionName;
    String apkurl;

    BroadcastReceiver receiver;
    String app_version_name;

    Button btnOk;
    Button btnCancel;
    int index = 1;
    String qrid;

    String curr_qr_id, scanneddate, joborderno, shipcode, colors, size, bundleno, styleno, stylename, partname, total_insert_count, layno, index_no;


    List qr_id_arr = new ArrayList<String>();

    List scan_qr_id_arr = new ArrayList<String>();
    List scanned_qr_id_arr = new ArrayList<String>();
    JSONArray QR_ID_Array = new JSONArray();
    List scan_laywise_qr_arr = new ArrayList<String>();

    TableLayout outprogramtbl;


    TextView txtJobNo, txtShipCode, text_Lay_No, txtStyle, txtStyleRef;
    LinearLayout liner_bundle_details, linear_programdata, linear_layout_btn;

    JSONObject scan_qr_data_details;

    int cick_count =0;
    int total_data =0;
    TextView[] textViewPcQr= new TextView[200];

    HashMap<Integer, Integer> text_view_hashmap = new HashMap<Integer, Integer>();
    HashMap<Integer, String> scan_date_time_hashmap = new HashMap<Integer, String>();
    int totalinsert_count =0;
    HashMap<Integer, String> scanned_qr_st_arr_hashmap = new HashMap<Integer, String>();
    HashMap<Integer, String> bundleno_partno_arr_hashmap = new HashMap<Integer, String>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        setContentView(R.layout.activity_bt_scanner_multiple_qr_laywise);

        imageView = findViewById(R.id.imgd);
        imageView.setOnClickListener(this);
        txtUser = findViewById(R.id.txtUser);

        session = new SessionManagement(getApplicationContext());
        HashMap<String, String> user = session.getUserDetails();
        String name = user.get(SessionManagement.KEY_USER);
        this.Id = user.get(SessionManagement.KEY_PROCESSOR_ID);
        processorid = user.get(SessionManagement.KEY_PROCESSOR_ID);
        userid = user.get(SessionManagement.KEY_USER_ID);
        this.User = user.get(SessionManagement.KEY_USER);
        unitid = user.get(SessionManagement.KEY_UNITID);


        txtUser.setText("Hello " + this.User);
        custPrograssbar = new CustPrograssbar();

        processorid = getIntent().getStringExtra("processorid");
        userid = getIntent().getStringExtra("userid");
        username = getIntent().getStringExtra("name");

        versioncode();

        txtJobNo = findViewById(R.id.txtJobNo);
        txtShipCode = findViewById(R.id.txtShipCode);
        text_Lay_No =findViewById(R.id.text_Lay_No);
        txtStyle = findViewById(R.id.txtStyle);
        txtStyleRef = findViewById(R.id.text_Color);

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

        txtScanData = findViewById(R.id.txtScanData);
        txtScanData.requestFocus();

        // Delay hiding the keyboard to ensure focus is set
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
                    } else {
                        // Toast.makeText(Scanner_Multiple_Bundle_QR_Activity.this, "Scan data is empty!", Toast.LENGTH_SHORT).show();
                    }

                    return true;
                }
                return false;
            }
        });
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

            AlertDialog alertDialog = new AlertDialog.Builder(Laywise_Multiple_Bundle_QR_Scanner_Activity.this)
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

    public void hideKeyboard() {
        View view = getCurrentFocus();
        if (view == null) {
            view = new View(this); // fallback if no view has focus
        }
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    private void handleScanData(String qrdata)
    {
        qrid = qrdata;
        String[] arrayString = qrid.split("-");

        String qrprefix = null;

        if(arrayString.length > 1) {
            qrprefix = arrayString[0];
            qrid = arrayString[1];
        }
        if(arrayString.length > 1 && qrprefix.equals("NE"))
        {
            SessionManagement session = new SessionManagement(getApplicationContext());
            HashMap<String, String> user = session.getUserDetails();
            String processorid = user.get(SessionManagement.KEY_PROCESSOR_ID);
            String userid = user.get(SessionManagement.KEY_USER_ID);
            String isqc = user.get(SessionManagement.KEY_ISQC);

            if(isOnline()) {
                if (isqc.equals("N")) {
                    Laywise_Multiple_Bundle_QR_Scanner_Activity.custPrograssbar.prograssCreate(this);
                    if(cick_count == 0) {
                        JSONObject jsonObject = new JSONObject();
                        try {
                            jsonObject.put("qrid", qrid);
                            jsonObject.put("userid", userid);
                            jsonObject.put("processorid", processorid);
                            jsonObject.put("version_name", myversionName);

                            JsonParser jsonParser = new JsonParser();
                            Call<JsonObject> call = APIClient.getInterface().get_laywise_qr_data((JsonObject) jsonParser.parse(jsonObject.toString()));
                            GetResult getResult = new GetResult();
                            getResult.setMyListener(this);
                            getResult.callForLogin(call, "get_laywise_qr_data");
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                    else {
                        if(total_data > 199)
                        {
                            AlertDialog alertDialog = new AlertDialog.Builder(Laywise_Multiple_Bundle_QR_Scanner_Activity.this)
                                    .setMessage("Please Update Then Scan")
                                    .setCancelable(false)
                                    .setNegativeButton("Exit", new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface arg1, int arg0) {
                                            arg1.dismiss();
                                        }
                                    }).show();
                            alertDialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(Color.RED);
                        }
                        else {
                            curr_qr_id = qrid;
                            data_added();
                        }
                    }
                }
            }
            else {

                AlertDialog alertDialog = new AlertDialog.Builder(Laywise_Multiple_Bundle_QR_Scanner_Activity.this)
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
                    new AlertDialog.Builder(Laywise_Multiple_Bundle_QR_Scanner_Activity.this)
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


    @Override
    public void onClick(View v) {
        if (isOnline())
        {
            switch (v.getId()) {
                case R.id.imgd:
                    PopupMenu popup = new PopupMenu(Laywise_Multiple_Bundle_QR_Scanner_Activity.this, imageView);
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
        mProgressDialog = new ProgressDialog(Laywise_Multiple_Bundle_QR_Scanner_Activity.this);
        mProgressDialog.setMessage(getString(R.string.loading));
        mProgressDialog.setIndeterminate(true);
        mProgressDialog.show();
        DownloadManager downloadManager = (DownloadManager) getSystemService(Context.DOWNLOAD_SERVICE);

        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());

        DownloadManager.Request request = new DownloadManager.Request(Uri.parse(apkUrl));

        request.setDestinationInExternalFilesDir(this, null, apkFileName);
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
        request.setTitle("Downloading APK");
        request.setDescription("Download Piece QR APK File..");
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

    @Override
    public void callback(JsonObject result, String callNo) {
        try
        {
            btnOk.setOnClickListener(this);
            btnCancel.setOnClickListener(this);

            if(callNo.equalsIgnoreCase("get_laywise_qr_data"))
            {
                JSONObject jsonObject = new JSONObject(result.toString());
                String mStatus = jsonObject.optString("status");
                String message = jsonObject.optString("message");

                if(mStatus.equals("version_check"))
                {
                    JSONObject jsonObj = jsonObject.getJSONObject("data");
                    apkurl = jsonObj.optString("apk_url");
                    app_version_name = jsonObj.optString("version_name");
                    Laywise_Multiple_Bundle_QR_Scanner_Activity.custPrograssbar.closePrograssBar();

                    AlertDialog alertDialog = new AlertDialog.Builder(Laywise_Multiple_Bundle_QR_Scanner_Activity.this)
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
                            layno = jsonObj.optString("layno");
                            index_no = jsonObj.optString("index_no");
                            total_insert_count = jsonObj.optString("total_insert_count");
                            scan_qr_data_details = jsonObj.getJSONObject("bundles_array_details");

                            txtJobNo.setText(joborderno);
                            txtShipCode.setText(shipcode);
                            text_Lay_No.setText(layno);
                            txtStyle.setText(styleno);
                            txtStyleRef.setText(stylename);

                            liner_bundle_details.setVisibility(View.VISIBLE);
                            linear_programdata.setVisibility(View.VISIBLE);
                            linear_layout_btn.setVisibility(View.VISIBLE);

                            add_scan_data();

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
//                                        textViewPcQr[currindex].setText(String.valueOf(scan_laywise_qr_arr.get(currindex - 1)));
                                        textViewPcQr[currindex].setText(currindex +". "+String.valueOf(scan_laywise_qr_arr.get(currindex - 1)));
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
                                                    AlertDialog.Builder builder = new AlertDialog.Builder(Laywise_Multiple_Bundle_QR_Scanner_Activity.this);
                                                    builder.setTitle("Bundle Scanned On")
                                                            .setMessage(bundleno_partno_arr_hashmap.get(clicked_id) + "\n Scanned On : "+ scan_date_time_hashmap.get(clicked_id) + "\n Scanned By : " + username)
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
                                                            AlertDialog.Builder builder = new AlertDialog.Builder(Laywise_Multiple_Bundle_QR_Scanner_Activity.this);
                                                            builder.setTitle("Bundle Scanned On")
                                                                    .setMessage("Bundle No : "+Integer.parseInt(clickedText) +"\n "+String.valueOf(scanned_qr_st_arr_hashmap.get(current_cilck_text_id)))
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
                                    new AlertDialog.Builder(Laywise_Multiple_Bundle_QR_Scanner_Activity.this)
                                            .setMessage("This Bundle No" + bundleno + "Already Scanned!")
                                            .setCancelable(false)
                                            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                                public void onClick(DialogInterface arg1, int arg0) {
                                                    arg1.dismiss();

                                                }
                                            }).show();
                                }
                            }
                            Laywise_Multiple_Bundle_QR_Scanner_Activity.custPrograssbar.closePrograssBar();
                        }
                        else
                        {
                            Laywise_Multiple_Bundle_QR_Scanner_Activity.custPrograssbar.closePrograssBar();
                            new AlertDialog.Builder(Laywise_Multiple_Bundle_QR_Scanner_Activity.this)
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
                        Laywise_Multiple_Bundle_QR_Scanner_Activity.custPrograssbar.closePrograssBar();
                        new AlertDialog.Builder(Laywise_Multiple_Bundle_QR_Scanner_Activity.this)
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
            else  if(callNo.equalsIgnoreCase("update_qr_scan_queue_bundwisewise"))
            {
                JSONObject jsonObject = new JSONObject(result.toString());
                String mStatus = jsonObject.optString("status");
                String message = jsonObject.optString("message");
                Laywise_Multiple_Bundle_QR_Scanner_Activity.custPrograssbar.closePrograssBar();
                if (mStatus.equals("success")) {
                    new AlertDialog.Builder(Laywise_Multiple_Bundle_QR_Scanner_Activity.this)
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
                    Laywise_Multiple_Bundle_QR_Scanner_Activity.custPrograssbar.closePrograssBar();
                    new AlertDialog.Builder(Laywise_Multiple_Bundle_QR_Scanner_Activity.this)
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

            Laywise_Multiple_Bundle_QR_Scanner_Activity.custPrograssbar.prograssCreate(this);

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
            new AlertDialog.Builder(Laywise_Multiple_Bundle_QR_Scanner_Activity.this)
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
        try {
            if (scan_qr_data_details.length() > 0) {
                Iterator<String> sc_data = scan_qr_data_details.keys();
                while (sc_data.hasNext()) {
                    String key = sc_data.next();
                    if (scan_qr_data_details.get(key) instanceof JSONObject) {
                        int qrid = Integer.parseInt(((JSONObject) scan_qr_data_details.get(key)).getString("qrid"));
                        String bundleno = ((JSONObject) scan_qr_data_details.get(key)).getString("bundleno");
                        String scan_status = ((JSONObject) scan_qr_data_details.get(key)).getString("scan_status");
                        String scan_ts = ((JSONObject) scan_qr_data_details.get(key)).getString("scan_ts");
                        String layno = ((JSONObject) scan_qr_data_details.get(key)).getString("layno");
                        String sizename = ((JSONObject) scan_qr_data_details.get(key)).getString("sizename");
                        String partname = ((JSONObject) scan_qr_data_details.get(key)).getString("partname");
                        String layid = ((JSONObject) scan_qr_data_details.get(key)).getString("layid");

                        if(scan_status.equals("N"))
                        {
                            scan_qr_id_arr.add(qrid);
                            if(partname.equals("RIGHT SLEEVE"))
                            {
                                partname = "R SLEEVE";
                            }
                            else  if(partname.equals("LEFT SLEEVE"))
                            {
                                partname = "L SLEEVE";
                            }
                            else
                            {
                                partname = partname;
                            }
                            scan_laywise_qr_arr.add(bundleno+"\n"+sizename+"\n"+partname);
                        }
                        else if(scan_status.equals("Y"))
                        {
                            if(partname.equals("RIGHT SLEEVE"))
                            {
                                partname = "R SLEEVE";
                            }
                            else  if(partname.equals("LEFT SLEEVE"))
                            {
                                partname = "L SLEEVE";
                            }
                            else
                            {
                                partname = partname;
                            }
                            scan_qr_id_arr.add(qrid);
                            scanned_qr_id_arr.add(qrid);
                            scanned_qr_st_arr_hashmap.put(qrid,scan_ts);
                            scan_laywise_qr_arr.add(bundleno+"\n"+sizename+"\n"+partname);
                        }
                        String bud = "Bundle No : " + bundleno +" | Size : " + sizename  +"\n Part : "+ partname;
                        bundleno_partno_arr_hashmap.put(qrid,bud);
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
            Laywise_Multiple_Bundle_QR_Scanner_Activity.custPrograssbar.closePrograssBar();
            new AlertDialog.Builder(Laywise_Multiple_Bundle_QR_Scanner_Activity.this)
                    .setMessage("Already Scanned")
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
                total_data++;
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
                        AlertDialog.Builder builder = new AlertDialog.Builder(Laywise_Multiple_Bundle_QR_Scanner_Activity.this);
                        builder.setTitle("Bundle Scanned On")
                                .setMessage(bundleno_partno_arr_hashmap.get(clicked_id) +"\n Scanned On : "+scan_date_time_hashmap.get(clicked_id) +"\n Scanned By : "+username)
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
                Laywise_Multiple_Bundle_QR_Scanner_Activity.custPrograssbar.closePrograssBar();
            }
            else
            {
                Laywise_Multiple_Bundle_QR_Scanner_Activity.custPrograssbar.closePrograssBar();
                AlertDialog alertDialog = new AlertDialog.Builder(Laywise_Multiple_Bundle_QR_Scanner_Activity.this)
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
        SessionManagement session = new SessionManagement(getApplicationContext());
        HashMap<String, String> user = session.getUserDetails();
        Intent intent = new Intent(Laywise_Multiple_Bundle_QR_Scanner_Activity.this, MainActivity.class);
        intent.putExtra("name", user.get(SessionManagement.KEY_USER));
        intent.putExtra("userid", user.get(SessionManagement.KEY_USER_ID));
        intent.putExtra("processorid", user.get(SessionManagement.KEY_PROCESSOR_ID));
        startActivity(intent);
        finish();
    }
}
