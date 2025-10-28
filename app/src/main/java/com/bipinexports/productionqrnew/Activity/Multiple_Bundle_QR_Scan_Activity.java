package com.bipinexports.productionqrnew.Activity;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DownloadManager;
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
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.bipinexports.productionqrnew.APIClient;
import com.bipinexports.productionqrnew.GetResult;
import com.bipinexports.productionqrnew.ModelClass;
import com.bipinexports.productionqrnew.R;
import com.bipinexports.productionqrnew.SessionManagement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

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

import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;


public class Multiple_Bundle_QR_Scan_Activity extends BaseActivity implements View.OnClickListener, GetResult.MyListener {

    String Id, User;
    SessionManagement session;

    ArrayList<ModelClass> mylist = new ArrayList<>();
    String processorid;
    String userid;
    String username;
    String qrid;
    String unitid;
    ImageView imageView;
    TextView  txtUser;

    Button btnOk;
    Button btnCancel;
    Dialog dialog;

    public static CustPrograssbar custPrograssbar;

    String curr_qr_id, scanneddate, joborderno, shipcode, colors, size, bundleno, styleno, stylename, partname, total_insert_count;

    ImageView FetchData;

    List qr_id_arr = new ArrayList<String>();

    List scan_qr_id_arr = new ArrayList<String>();
    List scanned_qr_id_arr = new ArrayList<String>();
    JSONArray QR_ID_Array = new JSONArray();
    List pending_scan_qr_id_arr = new ArrayList<String>();

    String qrprefix;
    int index = 1;

    String myversionName;
    String apkurl;
    String apkFileName;

    BroadcastReceiver receiver;
    String app_version_name;
    TableLayout outprogramtbl;

    TextView txtJobNo, txtShipCode, text_Part_Name, text_Size_Name, txtStyle, txtStyleRef;
    LinearLayout liner_bundle_details, linear_programdata, linear_layout_btn;

    JSONObject scan_qr_data_details;

    int cick_count =0;
    TextView[] textViewPcQr= new TextView[200];

    HashMap<Integer, Integer> text_view_hashmap = new HashMap<Integer, Integer>();
    HashMap<Integer, String> scan_date_time_hashmap = new HashMap<Integer, String>();
    int totalinsert_count =0;
    HashMap<Integer, String> scanned_qr_st_arr_hashmap = new HashMap<Integer, String>();
    HashMap<Integer, String> pending_qr_st_arr = new HashMap<Integer, String>();
    HashMap<Integer, String> assign_header_qr_st_arr = new HashMap<Integer, String>();
    HashMap<Integer, String> bundle_no_arr = new HashMap<Integer, String>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_multiple_bundle_qr_scan);
        setContentView(R.layout.activity_base);
        setupDrawer();

        View content = getLayoutInflater().inflate(
                R.layout.activity_multiple_bundle_qr_scan,
                findViewById(R.id.content_frame),
                true
        );

        if ( Build.VERSION.SDK_INT >= 23 && ContextCompat.checkSelfPermission( getApplicationContext(), Manifest.permission.CAMERA ) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.CAMERA}, 212);
        }

        if ( Build.VERSION.SDK_INT >= 23 && ContextCompat.checkSelfPermission( getApplicationContext(), WRITE_EXTERNAL_STORAGE ) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{WRITE_EXTERNAL_STORAGE}, 212);
        }

        if ( Build.VERSION.SDK_INT >= 23 && ContextCompat.checkSelfPermission( getApplicationContext(), Manifest.permission.READ_EXTERNAL_STORAGE ) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 212);
        }

        custPrograssbar = new CustPrograssbar();

        imageView = (ImageView) content.findViewById(R.id.imgd);
        txtUser = (TextView) content.findViewById(R.id.txtUser);

        txtJobNo = content.findViewById(R.id.txtJobNo);
        txtShipCode = content.findViewById(R.id.txtShipCode);
        text_Part_Name =content.findViewById(R.id.text_Part_Name);
        text_Size_Name = content.findViewById(R.id.text_Size_Name);
        txtStyle = content.findViewById(R.id.txtStyle);
        txtStyleRef = content.findViewById(R.id.text_Color);

        liner_bundle_details = content.findViewById(R.id.liner_bundle_details);
        liner_bundle_details.setVisibility(View.GONE);

        linear_programdata = content.findViewById(R.id.linear_programdata);
        linear_programdata.setVisibility(View.GONE);

        linear_layout_btn = content.findViewById(R.id.linear_layout_btn);
        linear_layout_btn.setVisibility(View.GONE);

        btnOk = content.findViewById(R.id.btnOk);
        btnCancel = content.findViewById(R.id.btnCancel);
        session = new SessionManagement(getApplicationContext());
        HashMap<String, String> user = session.getUserDetails();
        String name = user.get(SessionManagement.KEY_USER);
        this.Id = user.get(SessionManagement.KEY_PROCESSOR_ID);
        this.User = user.get(SessionManagement.KEY_USER);
        unitid = user.get(SessionManagement.KEY_UNITID);

        processorid = getIntent().getStringExtra("processorid");
        userid = getIntent().getStringExtra("userid");
        username = getIntent().getStringExtra("name");

        versioncode();
        getvalue();
        hideKeyboard();
        imageView.setOnClickListener(this);
        Scanning();

        FetchData = content.findViewById(R.id.FetchData);

        btnOk.setOnClickListener(this);
        btnCancel.setOnClickListener(this);
        btnOk.setVisibility(View.GONE);
        FetchData.setOnClickListener(this);
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
            AlertDialog alertDialog = new AlertDialog.Builder(Multiple_Bundle_QR_Scan_Activity.this)
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
        // Check if no view has focus:
        View view = this.getCurrentFocus();
        if (view != null)
        {
            InputMethodManager inputManager = (InputMethodManager) this.getSystemService(Context.INPUT_METHOD_SERVICE);
            inputManager.hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }

    public void Scanning() {
        IntentIntegrator qrScan = new IntentIntegrator(this);
        qrScan.setPrompt(" Scan Bundle QR Code");
        qrScan.setOrientationLocked(false);
        qrScan.initiateScan();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if (isOnline()) {

            if (requestCode == 12345) { // Use the same request code as in the previous step
                if (resultCode == RESULT_OK) {
                    // User granted permission, you can proceed with the installation
                } else {
                    // User denied permission, handle accordingly
                }
            } else if (result != null) {
                if (result.getContents() == null) {
                    Toast t = Toast.makeText(Multiple_Bundle_QR_Scan_Activity.this, "\"Result Not Found", Toast.LENGTH_LONG);
                    t.show();
                } else {
                    qrid = result.getContents();
                    String[] arrayString = qrid.split("-");
                    if (arrayString.length > 1) {
                        qrprefix = arrayString[0];
                        qrid = arrayString[1];
                    }
                    if (arrayString.length > 1 && (qrprefix.equals("NE"))) {
                        if (isOnline()) {

                            if (cick_count == 0) {
                                Multiple_Bundle_QR_Scan_Activity.custPrograssbar.prograssCreate(this);

                                JSONObject jsonObject = new JSONObject();
                                try {
                                    jsonObject.put("qrid", qrid);
                                    jsonObject.put("userid", userid);
                                    jsonObject.put("processorid", processorid);
                                    jsonObject.put("version_name", myversionName);

                                    JsonParser jsonParser = new JsonParser();
                                    Call<JsonObject> call = APIClient.getInterface().get_new_qr_data((JsonObject) jsonParser.parse(jsonObject.toString()));
                                    GetResult getResult = new GetResult();
                                    getResult.setMyListener(this);
                                    getResult.callForLogin(call, "get_new_qr_data");
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            } else {
                                curr_qr_id = qrid;
                                data_added();
                            }
                        } else {
                            AlertDialog alertDialog = new AlertDialog.Builder(Multiple_Bundle_QR_Scan_Activity.this)
                                    .setMessage("Please Check Your Internet Connection")
                                    .setCancelable(false)
                                    .setNegativeButton("OK", new DialogInterface.OnClickListener() {
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
                                new AlertDialog.Builder(Multiple_Bundle_QR_Scan_Activity.this)
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
            }
        } else {
            new AlertDialog.Builder(Multiple_Bundle_QR_Scan_Activity.this)
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
    public void onClick(View v) {

        if (isOnline()) {
            dialog = new Dialog(Multiple_Bundle_QR_Scan_Activity.this);
            switch (v.getId()) {
                case R.id.imgd:
                    PopupMenu popup = new PopupMenu(Multiple_Bundle_QR_Scan_Activity.this, imageView);
                    toggleDrawer();
                    popup.show();
                    break;
                case R.id.FetchData:
                    Scanning();
                    break;

                case R.id.btnOk:
                    UpdatescanData();
                    break;

                case R.id.btnCancel:
                    onBackPressed();
                    break;
            }
        } else {
            Snackbar snackbar = Snackbar.make(v, "No internet connection!", Snackbar.LENGTH_LONG);
            snackbar.show();
        }
    }

    private void UpdatescanData() {
        // Upload Audio and update qc scan status
        // Create new table to update qc data
        if (isOnline()){
            Multiple_Bundle_QR_Scan_Activity.custPrograssbar.prograssCreate(this);

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
            new AlertDialog.Builder(Multiple_Bundle_QR_Scan_Activity.this)
                    .setMessage("No internet connection!")
                    .setCancelable(false)
                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface d, int arg0) {
                            d.dismiss();
                        }
                    }).show();
        }
    }

    public void getvalue() {
        txtUser.setText("Hello " + this.User);
        ModelClass modelClass = new ModelClass();
        modelClass.setmID(userid);
        mylist.add(modelClass);
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
        Multiple_Bundle_QR_Scan_Activity.custPrograssbar.prograssCreate(this);
        DownloadManager downloadManager = (DownloadManager) getSystemService(Context.DOWNLOAD_SERVICE);

        DownloadManager.Request request = new DownloadManager.Request(Uri.parse(apkUrl));
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
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
                    Multiple_Bundle_QR_Scan_Activity.custPrograssbar.closePrograssBar();

                    Uri apkUri = FileProvider.getUriForFile(context, "com.nidhvitec.productionqr.fileprovider", new File(context.getExternalFilesDir(null), apkFileName));
                    Intent installIntent = new Intent(Intent.ACTION_VIEW);
                    installIntent.setData(apkUri);
                    installIntent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                    context.startActivity(installIntent);
                    unregisterReceiver(receiver);
                }
            }
        };
        registerReceiver(receiver, new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));
    }

    @Override
    public void callback(JsonObject result, String callNo) {
        try {

            btnOk.setOnClickListener(this);
            btnCancel.setOnClickListener(this);

           if(callNo.equalsIgnoreCase("get_new_qr_data"))
           {
                JSONObject jsonObject = new JSONObject(result.toString());
                String mStatus = jsonObject.optString("status");
                String message = jsonObject.optString("message");

                if(mStatus.equals("version_check"))
                {
                    JSONObject jsonObj = jsonObject.getJSONObject("data");
                    apkurl = jsonObj.optString("apk_url");
                    app_version_name = jsonObj.optString("version_name");
                    Multiple_Bundle_QR_Scan_Activity.custPrograssbar.closePrograssBar();

                    AlertDialog alertDialog = new AlertDialog.Builder(Multiple_Bundle_QR_Scan_Activity.this)
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
                            total_insert_count = jsonObj.optString("total_insert_count");
                            scan_qr_data_details = jsonObj.getJSONObject("bundles_array_details");

                            txtJobNo.setText(joborderno);
                            txtShipCode.setText(shipcode);
                            text_Part_Name.setText(partname);
                            text_Size_Name.setText(size);
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
                                        textViewPcQr[currindex].setText(String.valueOf(currindex));
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

                                        if(totalinsert_count > 0 && Integer.parseInt(bundleno) == currindex)
                                        {
                                            Date c = Calendar.getInstance().getTime();
                                            SimpleDateFormat df = new SimpleDateFormat("dd-MMM-yyyy HH:mm:ss");
                                            String formattedDate = df.format(c);

                                            scan_date_time_hashmap.put(qr_id, formattedDate);

                                            textViewPcQr[currindex].setBackgroundResource(R.drawable.border);
                                            textViewPcQr[currindex].setOnClickListener(new View.OnClickListener() {
                                                @Override
                                                public void onClick(View v) {
                                                    String clickedText = ((TextView) v).getText().toString();
                                                    int clicked_id = ((TextView) v).getId();
                                                    AlertDialog.Builder builder = new AlertDialog.Builder(Multiple_Bundle_QR_Scan_Activity.this);
                                                    builder.setTitle("Already Scanned")
                                                            .setMessage("Bundle No :"+Integer.parseInt(clickedText) +"\nScanned On : "+scan_date_time_hashmap.get(clicked_id) +"\nScanned By : "+username)
                                                            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                                                public void onClick(DialogInterface dialog, int which) {
                                                                    // Do something when OK button is clicked
                                                                    dialog.dismiss();
                                                                }
                                                            })
                                                            .show();
                                                }
                                            });
                                        }int currentIndexId = textViewPcQr[currindex].getId();

                                        if(scanned_qr_id_arr.size() > 0)
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
                                                            AlertDialog.Builder builder = new AlertDialog.Builder(Multiple_Bundle_QR_Scan_Activity.this);
                                                            builder.setTitle("Already Scanned")
                                                                    .setMessage("Bundle No :"+Integer.parseInt(clickedText) +"\n"+String.valueOf(scanned_qr_st_arr_hashmap.get(current_cilck_text_id)))
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


                                        if(pending_scan_qr_id_arr.size() > 0)
                                        {
                                            if (pending_scan_qr_id_arr.contains(currentIndexId))
                                            {
                                                textViewPcQr[currindex].setBackgroundResource(R.drawable.border_red);
                                                textViewPcQr[currindex].setOnClickListener(new View.OnClickListener() {
                                                    @Override
                                                    public void onClick(View v) {
                                                        // Show an alert (dialog) when the TextView is clicked
                                                        int current_cilck_text_id = ((TextView) v).getId();
                                                        if(pending_qr_st_arr.containsKey(current_cilck_text_id)) {
                                                            String clickedText = ((TextView) v).getText().toString();
                                                            AlertDialog.Builder builder = new AlertDialog.Builder(Multiple_Bundle_QR_Scan_Activity.this);
                                                            builder.setTitle(String.valueOf(assign_header_qr_st_arr.get(current_cilck_text_id)))
                                                                    .setMessage("Bundle No :"+Integer.parseInt(clickedText) +"\n"+String.valueOf(pending_qr_st_arr.get(current_cilck_text_id)))
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
                                    String reason = scanned_qr_st_arr_hashmap.get(Integer.parseInt(String.valueOf(curr_qr_id).trim()));
                                    bundleno = bundle_no_arr.get(Integer.parseInt(String.valueOf(curr_qr_id).trim()));

                                    new AlertDialog.Builder(Multiple_Bundle_QR_Scan_Activity.this)
                                        .setTitle("Already Scanned")
                                        .setMessage("Bundle No :"+ bundleno +"\n" +reason )
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
                                    new AlertDialog.Builder(Multiple_Bundle_QR_Scan_Activity.this)
                                            .setMessage("This Bundle No" + bundleno + assign_header_qr_st_arr.get(curr_qr_id) +"!")
                                            .setCancelable(false)
                                            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                                public void onClick(DialogInterface arg1, int arg0) {
                                                    arg1.dismiss();

                                                }
                                            }).show();
                                }
                            }
                            Multiple_Bundle_QR_Scan_Activity.custPrograssbar.closePrograssBar();
                        }
                        else
                        {
                            Multiple_Bundle_QR_Scan_Activity.custPrograssbar.closePrograssBar();
                            new AlertDialog.Builder(Multiple_Bundle_QR_Scan_Activity.this)
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
                        Multiple_Bundle_QR_Scan_Activity.custPrograssbar.closePrograssBar();
                        new AlertDialog.Builder(Multiple_Bundle_QR_Scan_Activity.this)
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
               Multiple_Bundle_QR_Scan_Activity.custPrograssbar.closePrograssBar();
                if (mStatus.equals("success")) {
                    new AlertDialog.Builder(Multiple_Bundle_QR_Scan_Activity.this)
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
                    Multiple_Bundle_QR_Scan_Activity.custPrograssbar.closePrograssBar();
                    new AlertDialog.Builder(Multiple_Bundle_QR_Scan_Activity.this)
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

    public void add_scan_data() {
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
    }


    public void data_added() {

        Boolean arraycontain_data = false;
        Boolean startcontain_data = false;
        Boolean not_allow_contain_data = false;
        String reason = "";
        String header = "";
        String bundleno = "";

        if (qr_id_arr.contains(curr_qr_id))
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

        if(not_allow_contain_data)
        {
            new AlertDialog.Builder(Multiple_Bundle_QR_Scan_Activity.this)
                    .setTitle(header)
                    .setMessage("Bundle No :"+ bundleno +"\n"+ reason)
                    .setCancelable(false)
                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface arg1, int arg0) {
                            arg1.dismiss();

                        }
                    }).show();
        }
        else if(startcontain_data)
        {
            new AlertDialog.Builder(Multiple_Bundle_QR_Scan_Activity.this)
                    .setTitle("Already Scanned")
                    .setMessage("Bundle No :"+ bundleno +"\n"+"Scanned On : "+ reason +"\nScanned By : "+username)
                    .setCancelable(false)
                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface arg1, int arg0) {
                            arg1.dismiss();
                        }
                    }).show();
        }
        else if(arraycontain_data)
        {
            new AlertDialog.Builder(Multiple_Bundle_QR_Scan_Activity.this)
                    .setTitle("Already Scanned")
                    .setMessage("Bundle No :"+ bundleno +"\n" +reason )
                    .setCancelable(false)
                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface arg1, int arg0) {
                            arg1.dismiss();
                        }
                    }).show();
        }
       else {
            if(text_view_hashmap.containsKey(Integer.parseInt(curr_qr_id)))
            {
                int current_index = text_view_hashmap.get(Integer.parseInt(curr_qr_id));
                Date c = Calendar.getInstance().getTime();
                SimpleDateFormat df = new SimpleDateFormat("dd-MMM-yyyy HH:mm:ss");
                String formattedDate = df.format(c);
                scan_date_time_hashmap.put(Integer.parseInt(curr_qr_id), formattedDate);

                textViewPcQr[current_index].setBackgroundResource(R.drawable.border);
                textViewPcQr[current_index].setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // Show an alert (dialog) when the TextView is clicked
                        String clickedText = ((TextView) v).getText().toString();
                        int clicked_id = ((TextView) v).getId();

                        AlertDialog.Builder builder = new AlertDialog.Builder(Multiple_Bundle_QR_Scan_Activity.this);
                        builder.setTitle("Already Scanned")
                                .setMessage("Bundle No :"+Integer.parseInt(clickedText) +"\nScanned On : "+scan_date_time_hashmap.get(clicked_id) +"\nScanned By : "+username)
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
            }
            else
            {
                AlertDialog alertDialog = new AlertDialog.Builder(Multiple_Bundle_QR_Scan_Activity.this)
                        .setMessage("This Bundle No " + bundleno +"Not Avilable in This Size / Please Scan Valid QR!")
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
        Intent intent = new Intent(Multiple_Bundle_QR_Scan_Activity.this, MainActivity.class);
        intent.putExtra("username", username);
        intent.putExtra("processorid", processorid);
        intent.putExtra("userid", userid);
        startActivity(intent);
        finish();
    }
}
