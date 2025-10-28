package com.bipinexports.productionqrnew.Activity;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.content.ContextCompat;

import com.bipinexports.productionqrnew.APIClient;
import com.bipinexports.productionqrnew.GetResult;
import com.bipinexports.productionqrnew.ModelClass;
import com.bipinexports.productionqrnew.R;
import com.bipinexports.productionqrnew.SessionManagement;
import com.google.android.material.snackbar.Snackbar;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

import retrofit2.Call;

import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;

public class Scan_Relaxed_Rolls_Activity extends BaseActivity implements View.OnClickListener, GetResult.MyListener {

    String Id, User;
    SessionManagement session;

    ArrayList<ModelClass> mylist = new ArrayList<>();
    String processorid;
    String userid;
    String username;
    String isqc;
    String qrid;
    String unitid;
    ImageView imageView;
    TextView  txtUser;

    Button btnOk;
    Button btnCancel, btn_scan_again;
    Dialog dialog;

    public static CustPrograssbar custPrograssbar;

    String rackname, scanned_by, joborderno, shipcode, colors, rollno, rollcode, weight, styleno, partname, scanned_on, str_is_checked, relaxed_by, relaxed_on;

    String qrprefix;
    int is_checked = 0;
    String myversionName;

    TextView txtJobNo, txtShipCode, text_Color, txtStyle, text_partname,text_rackname, text_rollno, text_rollcode, text_weight, txtTitle, text_4point, text_relaxed_by, text_relaxed_on;
    LinearLayout liner_bundle_details, linear_layout_btn, layout_text_4point, layout_text_relaxed_by, layout_text_relaxed_on;
    JSONObject rollinfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_base);  // Changed to base layout
        setupDrawer();  // Setup drawer before using it

        // Inflate your actual content into the content_frame
        View content = getLayoutInflater().inflate(R.layout.activity_scan_relaxed_rollwise,
                findViewById(R.id.content_frame), true);

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

        imageView = content.findViewById(R.id.imgd);
        txtUser = content.findViewById(R.id.txtUser);

        txtJobNo = content.findViewById(R.id.txtJobNo);
        txtShipCode = content.findViewById(R.id.txtShipCode);
        text_Color = content.findViewById(R.id.text_Color);
        txtStyle = content.findViewById(R.id.txtStyle);
        txtTitle = content.findViewById(R.id.txtTitle);
        txtTitle.setVisibility(View.GONE);

        text_partname = content.findViewById(R.id.text_partname);
        text_rackname = content.findViewById(R.id.text_rackname);
        text_rollno = content.findViewById(R.id.text_rollno);
        text_rollcode = content.findViewById(R.id.text_rollcode);
        text_weight = content.findViewById(R.id.text_weight);
        text_4point = content.findViewById(R.id.text_4point);
        text_relaxed_by = content.findViewById(R.id.text_relaxed_by);
        text_relaxed_on = content.findViewById(R.id.text_relaxed_on);

        liner_bundle_details = content.findViewById(R.id.liner_bundle_details);
        liner_bundle_details.setVisibility(View.GONE);

        layout_text_4point = content.findViewById(R.id.layout_text_4point);
        layout_text_4point.setVisibility(View.GONE);

        layout_text_relaxed_by = content.findViewById(R.id.layout_text_relaxed_by);
        layout_text_relaxed_by.setVisibility(View.GONE);

        layout_text_relaxed_on = content.findViewById(R.id.layout_text_relaxed_on);
        layout_text_relaxed_on.setVisibility(View.GONE);

        linear_layout_btn = content.findViewById(R.id.linear_layout_btn);
        linear_layout_btn.setVisibility(View.GONE);

        btnOk = content.findViewById(R.id.btnOk);
        btn_scan_again = content.findViewById(R.id.btn_scan_again);
        btnCancel = content.findViewById(R.id.btnCancel);

        session = new SessionManagement(getApplicationContext());
        HashMap<String, String> user = session.getUserDetails();
        String name = user.get(SessionManagement.KEY_USER);
        this.Id = user.get(SessionManagement.KEY_PROCESSOR_ID);
        processorid = user.get(SessionManagement.KEY_PROCESSOR_ID);
        userid = user.get(SessionManagement.KEY_USER_ID);
        this.User = user.get(SessionManagement.KEY_USER);
        unitid = user.get(SessionManagement.KEY_UNITID);
        isqc = user.get(SessionManagement.KEY_ISQC);

        processorid = getIntent().getStringExtra("processorid");
        userid = getIntent().getStringExtra("userid");
        username = getIntent().getStringExtra("name");

        versioncode();
        getvalue();
        hideKeyboard();
        imageView.setOnClickListener(this);
        Scanning();

        btnOk.setOnClickListener(this);
        btnCancel.setOnClickListener(this);
        btn_scan_again.setOnClickListener(this);
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
            AlertDialog alertDialog = new AlertDialog.Builder(Scan_Relaxed_Rolls_Activity.this)
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

        liner_bundle_details.setVisibility(View.GONE);
        linear_layout_btn.setVisibility(View.GONE);
        layout_text_4point.setVisibility(View.GONE);
        layout_text_relaxed_by.setVisibility(View.GONE);
        layout_text_relaxed_on.setVisibility(View.GONE);
        txtTitle.setVisibility(View.GONE);

        IntentIntegrator qrScan = new IntentIntegrator(this);
        qrScan.setPrompt(" Scan Roll QR Code");
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
                    Toast t = Toast.makeText(Scan_Relaxed_Rolls_Activity.this, "\"Result Not Found", Toast.LENGTH_LONG);
                    t.show();
                    onBackPressed();
                } else {
                    qrid = result.getContents();
                    String[] arrayString = qrid.split("-");
                    if (arrayString.length > 1) {
                        qrprefix = arrayString[0];
                    }
                    if (qrprefix.equals("RLX") || qrprefix.equals("RIN") || qrprefix.equals("RL") )
                    {
                        if (isOnline())
                        {
                            session = new SessionManagement(getApplicationContext());
                            HashMap<String, String> user = session.getUserDetails();
                            processorid = user.get(SessionManagement.KEY_PROCESSOR_ID);
                            userid = user.get(SessionManagement.KEY_USER_ID);

                            //                    progressBar.setVisibility(View.VISIBLE);
                            Scan_Relaxed_Rolls_Activity.custPrograssbar.prograssCreate(this);

                            JSONObject jsonObject = new JSONObject();
                            try {
                                jsonObject.put("rollid", qrid);
                                jsonObject.put("scanned_by", userid);
                                jsonObject.put("processorid", processorid);
                                jsonObject.put("version_name", myversionName);

                                JsonParser jsonParser = new JsonParser();
                                Call<JsonObject> call = APIClient.getInterface().getproductionrolldetails((JsonObject) jsonParser.parse(jsonObject.toString()));
                                GetResult getResult = new GetResult();
                                getResult.setMyListener(this);
                                getResult.callForLogin(call, "getjobdetails");
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        } else {
                            AlertDialog alertDialog = new AlertDialog.Builder(Scan_Relaxed_Rolls_Activity.this)
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
                                new AlertDialog.Builder(Scan_Relaxed_Rolls_Activity.this)
                                        .setTitle("Error")
                                        .setMessage("Invalid Roll QR.\nContact Admin!")
                                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                dialog.dismiss();
                                                Scanning();
                                            }
                                        }).show();
                            }
                        });
                    }
                }
            }
        } else {
            new AlertDialog.Builder(Scan_Relaxed_Rolls_Activity.this)
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
            dialog = new Dialog(Scan_Relaxed_Rolls_Activity.this);
            switch (v.getId()) {
                case R.id.imgd:
                    PopupMenu popup = new PopupMenu(Scan_Relaxed_Rolls_Activity.this, imageView);
                    toggleDrawer();
                    popup.show();
                    break;
                case R.id.btn_scan_again:
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
        if (isOnline()){
            session = new SessionManagement(getApplicationContext());
            HashMap<String, String> user = session.getUserDetails();
            processorid = user.get(SessionManagement.KEY_PROCESSOR_ID);
            userid = user.get(SessionManagement.KEY_USER_ID);
            String userid = user.get(SessionManagement.KEY_USER_ID);

            Scan_Relaxed_Rolls_Activity.custPrograssbar.prograssCreate(this);

            JSONObject jsonObject = new JSONObject();
            try
            {
                jsonObject.put("userid", userid);
                jsonObject.put("processorid", processorid);
                jsonObject.put("scanned_by", userid);
                jsonObject.put("rollid", qrid);

                JsonParser jsonParser = new JsonParser();
                Call<JsonObject> call = APIClient.getInterface(). updateproductionroll((JsonObject) jsonParser.parse(jsonObject.toString()));
                GetResult getResult = new GetResult();
                getResult.setMyListener(this);
                getResult.callForLogin(call, "updateproductionroll");
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        else {
            new AlertDialog.Builder(Scan_Relaxed_Rolls_Activity.this)
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


    @Override
    public void callback(JsonObject result, String callNo) {
        try {

            btnOk.setOnClickListener(this);
            btnCancel.setOnClickListener(this);

            if(callNo.equalsIgnoreCase("getjobdetails"))
            {
                JSONObject jsonObject = new JSONObject(result.toString());
                String mStatus = jsonObject.optString("status");
                String message = jsonObject.optString("message");

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
                    relaxed_by = rollinfo.getString("relaxed_by");
                    relaxed_on = rollinfo.getString("relaxed_on");
                    str_is_checked = rollinfo.getString("is_checked");
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
                    text_relaxed_by.setText(relaxed_by);
                    text_relaxed_on.setText(relaxed_on);
                    text_4point.setText(str_is_checked);

                    liner_bundle_details.setVisibility(View.VISIBLE);
                    linear_layout_btn.setVisibility(View.VISIBLE);
                    btnOk.setVisibility(View.VISIBLE);
                    if(relaxed_on != null)
                    {
                        layout_text_4point.setVisibility(View.VISIBLE);
                        layout_text_relaxed_by.setVisibility(View.VISIBLE);
                        layout_text_relaxed_on.setVisibility(View.VISIBLE);
                    }
                    txtTitle.setVisibility(View.GONE);

                    Scan_Relaxed_Rolls_Activity.custPrograssbar.closePrograssBar();
                }
                else if (mStatus.equalsIgnoreCase("scanned"))
                {
                    btnOk.setVisibility(View.GONE);
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
                    relaxed_by = rollinfo.getString("relaxed_by");
                    relaxed_on = rollinfo.getString("relaxed_on");
                    str_is_checked = rollinfo.getString("is_checked");

                    scanned_by = "0";
                    scanned_on = "0";
                    if (rollinfo.has("scanned_by") && !rollinfo.isNull("scanned_by")) {
                        scanned_by = rollinfo.getString("scanned_by");
                    }
                    if (rollinfo.has("scanned_on") && !rollinfo.isNull("scanned_on")) {
                        scanned_on = rollinfo.getString("scanned_on");
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
                    text_relaxed_by.setText(relaxed_by);
                    text_relaxed_on.setText(relaxed_on);
                    text_4point.setText(str_is_checked);

                    liner_bundle_details.setVisibility(View.VISIBLE);
                    linear_layout_btn.setVisibility(View.VISIBLE);


                    txtTitle.setText(message +"\n"+"Scanned On : " + scanned_on +"\n" +"Scanned By  : " + scanned_by);
                    txtTitle.setVisibility(View.VISIBLE);
                    if(relaxed_on != null)
                    {
                        layout_text_4point.setVisibility(View.VISIBLE);
                        layout_text_relaxed_by.setVisibility(View.VISIBLE);
                        layout_text_relaxed_on.setVisibility(View.VISIBLE);
                    }
                    Scan_Relaxed_Rolls_Activity.custPrograssbar.closePrograssBar();
                }
                else
                {
                    Scan_Relaxed_Rolls_Activity.custPrograssbar.closePrograssBar();
                    new AlertDialog.Builder(Scan_Relaxed_Rolls_Activity.this)
                    .setMessage(message)
                    .setCancelable(false)
                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface arg1, int arg0) {
                            arg1.dismiss();
                            Scanning();
                        }
                    }).show();
                }
            }
            else  if(callNo.equalsIgnoreCase("updateproductionroll"))
            {
                JSONObject jsonObject = new JSONObject(result.toString());
                String mStatus = jsonObject.optString("status");
                String message = jsonObject.optString("message");
                Scan_Relaxed_Rolls_Activity.custPrograssbar.closePrograssBar();
                if (mStatus.equals("success")) {
                    Scanning();
                }
                else {
                    Scan_Relaxed_Rolls_Activity.custPrograssbar.closePrograssBar();
                    new AlertDialog.Builder(Scan_Relaxed_Rolls_Activity.this)
                    .setMessage(message)
                    .setCancelable(false)
                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface arg1, int arg0) {
                            arg1.dismiss();
                            Scanning();
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
        Intent intent = new Intent(Scan_Relaxed_Rolls_Activity.this, MainActivity.class);
        intent.putExtra("processorid", processorid);
        intent.putExtra("userid", userid);
        intent.putExtra("isqc", isqc);
        startActivity(intent);
        finish();
    }
}
