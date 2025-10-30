package com.bipinexports.productionqr.Activity;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bipinexports.productionqr.APIClient;
import com.bipinexports.productionqr.GetResult;
import com.bipinexports.productionqr.R;
import com.bipinexports.productionqr.SessionManagement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

import retrofit2.Call;

public class Employee_Bundle_Mapping_Activity extends BaseActivity implements View.OnClickListener, GetResult.MyListener {

    SessionManagement session;
    ProgressBar progress;
    TextView txtUser;
    ImageView imageView;

    String processorid, type, empcode, myversionName;
    String qrid;
    String qrprefix;
    String userid;

    public static CustPrograssbar custPrograssbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.empty_activity);
        setContentView(R.layout.activity_base);
        setupDrawer();

        View content = getLayoutInflater().inflate(
                R.layout.empty_activity,
                findViewById(R.id.content_frame),
                true
        );
        progress = (ProgressBar) content.findViewById(R.id.progress);
        progress.setVisibility(View.GONE);
        custPrograssbar = new CustPrograssbar();

        txtUser = content.findViewById(R.id.txtUser);
        imageView = content.findViewById(R.id.imgd);

        session = new SessionManagement(getApplicationContext());
        HashMap<String, String> user = session.getUserDetails();
        String username = user.get(SessionManagement.KEY_USER);
        txtUser.setText("Hello " + username);

        imageView.setOnClickListener(this);
        processorid = getIntent().getStringExtra("processorid");
        empcode = getIntent().getStringExtra("empcode");
        type = getIntent().getStringExtra("type");
        myversionName = getIntent().getStringExtra("myversionName");

        Scanning();
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);

        if (result != null) {
            if (result.getContents() == null)
            {
                Toast.makeText(this, "Result Not Found", Toast.LENGTH_LONG).show();
                onBackPressed();
            }
            else
            {
                qrid = result.getContents();
                String[] arrayString = qrid.split("-");
                if(arrayString.length > 1) {
                    qrprefix = arrayString[0];
                    qrid = arrayString[1];
                }
                if(arrayString.length > 1 && qrprefix.equals("NE") )
                {
                    session = new SessionManagement(getApplicationContext());
                    HashMap<String, String> user = session.getUserDetails();
                    processorid = user.get(SessionManagement.KEY_PROCESSOR_ID);
                    userid = user.get(SessionManagement.KEY_USER_ID);
                    JSONObject jsonObject = new JSONObject();
                    try {
                        jsonObject.put("qrid", qrid);
                        jsonObject.put("userid", userid);
                        jsonObject.put("processorid", processorid);
                        jsonObject.put("version_name", myversionName);
                        Log.e("Bipin","myversionName :" +myversionName);
                        Employee_Bundle_Mapping_Activity.custPrograssbar.prograssCreate(this);

                        JsonParser jsonParser = new JsonParser();
                        Call<JsonObject> call = APIClient.getInterface().get_qrdata((JsonObject) jsonParser.parse(jsonObject.toString()));
                        GetResult getResult = new GetResult();
                        getResult.setMyListener(this);
                        getResult.callForLogin(call, "get_bundle_qr_data");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                else
                {
                    String messsage = "Invalid QR. Contact Admin!";
                    AlertDialog alertDialog = new AlertDialog.Builder(Employee_Bundle_Mapping_Activity.this)
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

    @Override
    public void callback(JsonObject result, String callNo) {
        try {
            progress.setVisibility(View.GONE);
            Employee_Bundle_Mapping_Activity.custPrograssbar.closePrograssBar();
            if (callNo.equalsIgnoreCase("get_bundle_qr_data")) {
                JSONObject json = new JSONObject(result.toString());
                String status = json.optString("status");
                String mess = json.optString("message");

                final Dialog dialog = new Dialog(Employee_Bundle_Mapping_Activity.this);
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
                            update_bundle_data();
                        }
                    });
                    dialog.show();
                }
                else
                {
                    AlertDialog alertDialog = new AlertDialog.Builder(Employee_Bundle_Mapping_Activity.this)
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
            else if (callNo.equalsIgnoreCase("update_bundle_data"))
            {
                JSONObject json = new JSONObject(result.toString());
                String status = json.optString("status");
                String mess = json.optString("message");

                if (status.equals("success")) {
                    Employee_Bundle_Mapping_Activity.custPrograssbar.closePrograssBar();
                    AlertDialog alertDialog = new AlertDialog.Builder(Employee_Bundle_Mapping_Activity.this)
                            .setMessage(mess)
                            .setCancelable(false)
                            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface arg1, int arg0) {
                                    onBackPressed();
                                }
                            }).show();
                }
                else {
                    AlertDialog alertDialog = new AlertDialog.Builder(Employee_Bundle_Mapping_Activity.this)
                            .setMessage(mess)
                            .setCancelable(false)
                            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface arg1, int arg0) {
                                    onBackPressed();
                                }
                            }).show();
                }
            }
        }
        catch (Exception e) {
        }
    }

    private void update_bundle_data() {
        Employee_Bundle_Mapping_Activity.custPrograssbar.prograssCreate(this);
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("qrid", qrid);
            jsonObject.put("userid", userid);
            jsonObject.put("processorid", processorid);
            jsonObject.put("empcode", empcode);

            JsonParser jsonParser = new JsonParser();
            Call<JsonObject> call = APIClient.getInterface().update_bundle_data((JsonObject) jsonParser.parse(jsonObject.toString()));
            GetResult getResult = new GetResult();
            getResult.setMyListener(this);
            getResult.callForLogin(call, "update_bundle_data");

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.imgd:
                toggleDrawer();
                break;
        }
    }

    @Override
    public void onBackPressed() {
        session = new SessionManagement(getApplicationContext());
        HashMap<String, String> user = session.getUserDetails();
        Intent intent;
        intent = new Intent(Employee_Bundle_Mapping_Activity.this, In_Employee_Operation_Mapping_Activity.class);
        intent.putExtra("processorid", processorid);
        intent.putExtra("empcode", empcode);
        intent.putExtra("type", type);
        startActivity(intent);
        finish();

    }
}
