package com.bipinexports.productionqr.Activity;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import androidx.annotation.RequiresApi;
import com.google.android.material.snackbar.Snackbar;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.appcompat.app.AppCompatActivity;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.TableLayout;
import android.widget.TableRow;
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
import com.loopj.android.http.AsyncHttpClient;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;

import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;

public class Sharptool_QR_Scanview_Activity extends AppCompatActivity implements View.OnClickListener, GetResult.MyListener {

    String Id, User;
    SessionManagement session;

    ArrayList<ModelClass> mylist = new ArrayList<>();
    String processorid;
    String userid;
    String username;
    String isqc;

    String unitid;
    ImageView imageView;
    TextView  txtUser;

    Button btnCancel;
    Dialog dialog;

    public static CustPrograssbar custPrograssbar;

    String  scanneddate, total_insert_count;

    List scan_qr_id_arr = new ArrayList<String>();
    List scanned_qr_id_arr = new ArrayList<String>();
    JSONArray sharptool_Array = new JSONArray();

    int index = 1;

    String myversionName;
    String apkurl;
    String apkFileName;

    BroadcastReceiver receiver;
    String app_version_name;
    TableLayout outprogramtbl;

    LinearLayout  linear_programdata, linear_layout_btn;

    JSONObject scan_qr_data_details;

    TextView[] textViewPcQr= new TextView[200];

    HashMap<Integer, Integer> text_view_hashmap = new HashMap<Integer, Integer>();
    HashMap<Integer, String> scanned_qr_st_arr_hashmap = new HashMap<Integer, String>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_multiple_sharptool_qr_scan);

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

        imageView = (ImageView) findViewById(R.id.imgd);
        txtUser = (TextView) findViewById(R.id.txtUser);

        linear_programdata = findViewById(R.id.linear_programdata);
        linear_programdata.setVisibility(View.GONE);

        linear_layout_btn = findViewById(R.id.linear_layout_btn);
        linear_layout_btn.setVisibility(View.GONE);

        btnCancel = findViewById(R.id.btnCancel);
        session = new SessionManagement(getApplicationContext());
        HashMap<String, String> user = session.getUserDetails();
        String name = user.get(SessionManagement.KEY_USER);
        this.Id = user.get(SessionManagement.KEY_PROCESSOR_ID);
        processorid = user.get(SessionManagement.KEY_PROCESSOR_ID);
        userid = user.get(SessionManagement.KEY_USER_ID);
        this.User = user.get(SessionManagement.KEY_USER);
        unitid = user.get(SessionManagement.KEY_UNITID);

        processorid = getIntent().getStringExtra("processorid");
        userid = getIntent().getStringExtra("userid");
        username = getIntent().getStringExtra("name");
        myversionName = getIntent().getStringExtra("myversionName");

        getvalue();
        hideKeyboard();
        get_scan_sharptools();
        imageView.setOnClickListener(this);
        btnCancel.setOnClickListener(this);
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
        qrScan.setPrompt(" Scan Machine QR Code");
        qrScan.setOrientationLocked(false);
        qrScan.initiateScan();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if (isOnline()) {

            if (requestCode == 12345) { // Use the same request code as in the previous step
                if (resultCode == RESULT_OK) {
                    // User granted permission, you can proceed with the installation
                } else {
                    // User denied permission, handle accordingly
                }
            }
            else if (result != null) {
                if (result.getContents() == null) {
                    Toast t = Toast.makeText(Sharptool_QR_Scanview_Activity.this, "\"Result Not Found", Toast.LENGTH_LONG);
                    t.show();
                }
                else {

                }
            }
        }
        else {
            new AlertDialog.Builder(Sharptool_QR_Scanview_Activity.this)
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
            dialog = new Dialog(Sharptool_QR_Scanview_Activity.this);
            switch (v.getId()) {
                case R.id.imgd:
                    Intent intent = new Intent(Sharptool_QR_Scanview_Activity.this, HomeActivity.class);
                    intent.putExtra("openDrawer", true); //
                    intent.putExtra("username", username);
                    intent.putExtra("userid", userid);
                    intent.putExtra("processorid", processorid);
                    startActivity(intent);
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
        Sharptool_QR_Scanview_Activity.custPrograssbar.prograssCreate(this);
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
                    Sharptool_QR_Scanview_Activity.custPrograssbar.closePrograssBar();

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


    private void get_scan_sharptools() {
        if (isOnline()){
            AsyncHttpClient asyncHttpClient = new AsyncHttpClient();
            session = new SessionManagement(getApplicationContext());
            HashMap<String, String> user = session.getUserDetails();
            processorid = user.get(SessionManagement.KEY_PROCESSOR_ID);
            userid = user.get(SessionManagement.KEY_USER_ID);
            String userid = user.get(SessionManagement.KEY_USER_ID);

            Sharptool_QR_Scanview_Activity.custPrograssbar.prograssCreate(this);

            JSONObject jsonObject = new JSONObject();
            try {

                jsonObject.put("userid", userid);
                jsonObject.put("username", username);
                jsonObject.put("processorid", processorid);
                jsonObject.put("selected_date", processorid);

                JsonParser jsonParser = new JsonParser();
                Call<JsonObject> call = APIClient.getInterface().get_scan_sharptools((JsonObject) jsonParser.parse(jsonObject.toString()));
                GetResult getResult = new GetResult();
                getResult.setMyListener(this);
                getResult.callForLogin(call, "get_scan_sharptools");
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        else {
            new AlertDialog.Builder(Sharptool_QR_Scanview_Activity.this)
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
    public void callback(JsonObject result, String callNo) {
        try {

            btnCancel.setOnClickListener(this);

            Sharptool_QR_Scanview_Activity.custPrograssbar.closePrograssBar();
           if(callNo.equalsIgnoreCase("get_scan_sharptools"))
           {
                JSONObject jsonObject = new JSONObject(result.toString());
                String mStatus = jsonObject.optString("status");
                String message = jsonObject.optString("message");

                if(mStatus.equals("version_check"))
                {
                    JSONObject jsonObj = jsonObject.getJSONObject("data");
                    apkurl = jsonObj.optString("apk_url");
                    app_version_name = jsonObj.optString("version_name");
                    Sharptool_QR_Scanview_Activity.custPrograssbar.closePrograssBar();

                    AlertDialog alertDialog = new AlertDialog.Builder(Sharptool_QR_Scanview_Activity.this)
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

                        scanneddate = jsonObj.optString("scanneddate");
                        total_insert_count = jsonObj.optString("total_insert_count");
                        scan_qr_data_details = jsonObj.getJSONObject("sharptools_array_details");

                        linear_programdata.setVisibility(View.VISIBLE);
                        linear_layout_btn.setVisibility(View.VISIBLE);

                        try {
                            if (scan_qr_data_details.length() > 0) {
                                Iterator<String> sc_data = scan_qr_data_details.keys();
                                while (sc_data.hasNext()) {
                                    String key = sc_data.next();
                                    if (scan_qr_data_details.get(key) instanceof JSONObject) {
                                        int qrid = Integer.parseInt(((JSONObject) scan_qr_data_details.get(key)).getString("qrid"));
                                        String scan_status = ((JSONObject) scan_qr_data_details.get(key)).getString("scanstatus");
                                        String scan_ts = ((JSONObject) scan_qr_data_details.get(key)).getString("scan_ts");
                                        String sharptool_category = ((JSONObject) scan_qr_data_details.get(key)).getString("sharptool_category");
                                        String sharptool_name = ((JSONObject) scan_qr_data_details.get(key)).getString("sharptool_name");
                                        String line_no = ((JSONObject) scan_qr_data_details.get(key)).getString("line_no");
                                        String line_sharptool = "";
                                        if(scan_status.equals("null"))
                                        {
                                            line_sharptool = line_no +"-"+ sharptool_category +"-"+ sharptool_name;
                                            scan_qr_id_arr.add(qrid);
                                            sharptool_Array.put(qrid, line_sharptool);
                                        }
                                        else
                                        {
                                            line_sharptool = line_no +"-"+ sharptool_category +"-"+ sharptool_name;
                                            scan_qr_id_arr.add(qrid);
                                            scanned_qr_id_arr.add(qrid);
                                            sharptool_Array.put(qrid, line_sharptool);
                                            scanned_qr_st_arr_hashmap.put(qrid,scan_ts);
                                        }
                                    }
                                }
                            }
                        }
                        catch (JSONException e) {
                            e.printStackTrace();
                        }

                        outprogramtbl = findViewById(R.id.AddProgramData);
                        TableRow row;

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
                            row.setPadding(2, 20, -50, 3);

                            for(colCnt=0;colCnt<maxColCnt;colCnt++,indexIncrementor+=maxRowcnt)
                            {
                                currindex = rowCnt + 1 + indexIncrementor;
                                if(currindex <= Integer.parseInt(total_insert_count))
                                {
                                    int qr_id = Integer.parseInt(String.valueOf(scan_qr_id_arr.get(currindex - 1)));
                                    textViewPcQr[currindex] = new TextView(this);
                                    textViewPcQr[currindex].setLayoutParams(new TableRow.LayoutParams(2, TableRow.LayoutParams.WRAP_CONTENT));
                                    textViewPcQr[currindex].setPadding(3, 10, 0, 10);
                                    textViewPcQr[currindex].setText(String.valueOf(sharptool_Array.get(qr_id)));
                                    textViewPcQr[currindex].setWidth(1);  // Adjust width as needed
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

                                    int currentIndexId = textViewPcQr[currindex].getId();

                                    if(scanned_qr_id_arr.size() > 0)
                                    {
                                        if (scanned_qr_id_arr.contains(currentIndexId))
                                        {
                                            textViewPcQr[currindex].setBackgroundResource(R.drawable.border_green);
                                            textViewPcQr[currindex].setTextColor(getResources().getColor(R.color.white));
                                            textViewPcQr[currindex].setOnClickListener(new View.OnClickListener() {
                                                @Override
                                                public void onClick(View v) {
                                                    // Show an alert (dialog) when the TextView is clicked
                                                    int current_cilck_text_id = ((TextView) v).getId();
                                                    if(scanned_qr_st_arr_hashmap.containsKey(current_cilck_text_id)) {
                                                        String clickedText = ((TextView) v).getText().toString();

                                                        String[] splited = clickedText.split("-");

                                                        AlertDialog.Builder builder = new AlertDialog.Builder(Sharptool_QR_Scanview_Activity.this);
                                                        builder.setTitle("Sharptool Scanned Details")
                                                            .setMessage("Sharptool Name : "+splited[2] +"\n"+String.valueOf(scanned_qr_st_arr_hashmap.get(current_cilck_text_id)) )
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
                                    maxColCnt = 4;
                                    indexIncrementor=100;
                                    printedBundlecnt = 0;
                                }
                            }
                        }
                        Sharptool_QR_Scanview_Activity.custPrograssbar.closePrograssBar();

                        if(Integer.parseInt(total_insert_count) < 15)
                        {
                            ViewGroup.MarginLayoutParams layoutParams = (ViewGroup.MarginLayoutParams) linear_layout_btn.getLayoutParams();
                            layoutParams.setMargins(
                                    layoutParams.leftMargin,
                                    -130,
                                    layoutParams.rightMargin,
                                    layoutParams.bottomMargin
                            );
                            linear_layout_btn.setLayoutParams(layoutParams);
                        }
                    }
                    else if (mStatus.equalsIgnoreCase("error"))
                    {
                        Sharptool_QR_Scanview_Activity.custPrograssbar.closePrograssBar();
                        new AlertDialog.Builder(Sharptool_QR_Scanview_Activity.this)
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
        }
        catch (Exception e) {
        }
    }


    @Override
    public void onBackPressed() {
        Intent intent = new Intent(Sharptool_QR_Scanview_Activity.this, MainActivity.class);
        intent.putExtra("processorid", processorid);
        intent.putExtra("userid", userid);
        intent.putExtra("isqc", isqc);
        startActivity(intent);
        finish();
    }
}
