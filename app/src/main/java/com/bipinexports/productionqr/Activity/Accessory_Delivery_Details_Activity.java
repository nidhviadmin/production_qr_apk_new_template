package com.bipinexports.productionqr.Activity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.ProgressBar;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.bipinexports.productionqr.APIClient;
import com.bipinexports.productionqr.GetResult;
import com.bipinexports.productionqr.ModelClass;
import com.bipinexports.productionqr.R;
import com.bipinexports.productionqr.SessionManagement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import retrofit2.Call;

public class Accessory_Delivery_Details_Activity extends AppCompatActivity implements View.OnClickListener, GetResult.MyListener {

    String Id, User;

    SessionManagement session;
    GridView gridView;
    ProgressBar progress;
    String processorid, dcid, poid, pono, podate,vendorname, dcno, dcdate,type, activity;
    String userid, pendingcount;
    int postListIndex = 0;
    List Size_detail_Cntarr = new ArrayList<String>();

    ImageView imageView;
    Button AddProg, AddReject;

    String   vendorid, joborderno, stylename, orderid, del_quantity, vendors, del_count, vendor_del_count;

    TextView text_PO_No, text_PO_Date, text_Vendor_Name, text_DC_No, text_DC_Date, text_Job_Ref, text_Accessory_Name, text_Job_Order_No, text_Style_Name;
    TextView txtUser;

    public static CustPrograssbar_new custPrograssbar_new;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_accessory_delivery_details);

        imageView = (ImageView) findViewById(R.id.imgd);
        gridView = (GridView) this.findViewById(R.id.grid);
        progress = (ProgressBar) findViewById(R.id.progress);
        custPrograssbar_new = new CustPrograssbar_new();

        txtUser = (TextView) findViewById(R.id.txtUser);

        session = new SessionManagement(getApplicationContext());
        HashMap<String, String> user = session.getUserDetails();
        String name = user.get(SessionManagement.KEY_USER);
        this.Id = user.get(SessionManagement.KEY_PROCESSOR_ID);
        processorid = user.get(SessionManagement.KEY_PROCESSOR_ID);
        userid = user.get(SessionManagement.KEY_USER_ID);
        this.User = user.get(SessionManagement.KEY_USER);

        activity = getIntent().getStringExtra("activity");
        processorid = getIntent().getStringExtra("processorid");
        dcid = getIntent().getStringExtra("dcid");
        poid = getIntent().getStringExtra("poid");

        pono = getIntent().getStringExtra("pono");
        podate = getIntent().getStringExtra("podate");
        vendorname = getIntent().getStringExtra("vendorname");
        vendorid = getIntent().getStringExtra("vendorid");
        joborderno = getIntent().getStringExtra("joborderno");
        stylename = getIntent().getStringExtra("stylename");
        orderid = getIntent().getStringExtra("orderid");
        del_quantity = getIntent().getStringExtra("del_quantity");
        pendingcount = getIntent().getStringExtra("pendingcount");
        vendor_del_count = getIntent().getStringExtra("vendor_del_count");
        vendors = getIntent().getStringExtra("vendors");
        del_count = getIntent().getStringExtra("del_count");
        dcno = getIntent().getStringExtra("dcno");
        dcdate = getIntent().getStringExtra("dcdate");
        type = getIntent().getStringExtra("type");

        imageView.setOnClickListener(this);

        text_PO_No = findViewById(R.id.text_PO_No);
        text_PO_Date = findViewById(R.id.text_PO_Date);
        text_Vendor_Name = findViewById(R.id.text_Vendor_Name);
        text_DC_No = findViewById(R.id.text_DC_No);
        text_DC_Date = findViewById(R.id.text_DC_Date);
        text_Job_Ref = findViewById(R.id.text_Job_Ref);
        text_Job_Order_No = findViewById(R.id.text_Job_Order_No);
        text_Style_Name = findViewById(R.id.text_Style_Name);
        text_Accessory_Name = findViewById(R.id.text_Accessory_Name);

        text_PO_No.setText(pono);
        text_PO_Date.setText(podate);
        text_Vendor_Name.setText(vendorname);
        text_DC_No.setText(dcno);
        text_Job_Order_No.setText(joborderno);
        text_Style_Name.setText(stylename);
        text_DC_Date.setText(dcdate);
        text_Job_Ref.setText("");

        getvalue();
        get_deliverydetails();
        AddProg = findViewById(R.id.AddProg);
        AddProg.setOnClickListener(this);
        AddReject = findViewById(R.id.AddReject);
        AddReject.setOnClickListener(this);
        AddProg.setVisibility(View.INVISIBLE);
        AddReject.setVisibility(View.INVISIBLE);
    }

    public void onClick(View v) {
        if (isOnline()) {
            switch (v.getId()) {
                case R.id.imgd:
                    Intent intent = new Intent(Accessory_Delivery_Details_Activity.this, HomeActivity.class);
                    intent.putExtra("openDrawer", true); //
                    intent.putExtra("username", User);
                    intent.putExtra("userid", userid);
                    intent.putExtra("processorid", processorid);
                    startActivity(intent);
                    break;
                case R.id.AddProg:
                    Accessory_Delivery_Details_Activity.custPrograssbar_new.prograssCreate(this);
                    AddProgramDetails();
                    break;
                case R.id.AddReject:
                    Accessory_Delivery_Details_Activity.custPrograssbar_new.prograssCreate(this);
                    onBackPressed();
                    break;
            }
        }
        else {
            AlertDialog alertDialog = new AlertDialog.Builder(Accessory_Delivery_Details_Activity.this)
                    .setMessage("Please Check Your Internet Connection")
                    .setCancelable(false)
                    .setNegativeButton("Exit", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface arg1, int arg0) {
                            arg1.dismiss();
                            finish();
                        }
                    }).show();
        }
    }

    private void AddProgramDetails() {

        session = new SessionManagement(getApplicationContext());
        HashMap<String, String> user = session.getUserDetails();
        processorid = user.get(SessionManagement.KEY_PROCESSOR_ID);
        userid = user.get(SessionManagement.KEY_USER_ID);

        JSONObject jsonObject = new JSONObject();
        try {

            jsonObject.put("userid", userid);
            jsonObject.put("contractorid", processorid);
            jsonObject.put("dcid", dcid);
            jsonObject.put("sizes", Size_detail_Cntarr);
            Log.e("Bipin","sizes :" + Size_detail_Cntarr);

            JsonParser jsonParser = new JsonParser();
            Call<JsonObject> call = APIClient.getInterface().receivedelivery((JsonObject) jsonParser.parse(jsonObject.toString()));
            GetResult getResult = new GetResult();
            getResult.setMyListener(this);
            getResult.callForLogin(call, "receivedelivery");
        }
        catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void get_deliverydetails() {
        session = new SessionManagement(getApplicationContext());
        HashMap<String, String> user = session.getUserDetails();
        processorid = user.get(SessionManagement.KEY_PROCESSOR_ID);
        userid = user.get(SessionManagement.KEY_USER_ID);
        Accessory_Delivery_Details_Activity.custPrograssbar_new.prograssCreate(this);

        JSONObject jsonObject = new JSONObject();
        try {

            jsonObject.put("userid", userid);
            jsonObject.put("contractorid", processorid);
            jsonObject.put("dcid", dcid);

            JsonParser jsonParser = new JsonParser();
            Call<JsonObject> call = APIClient.getInterface().deliverydetails((JsonObject) jsonParser.parse(jsonObject.toString()));
            GetResult getResult = new GetResult();
            getResult.setMyListener(this);
            getResult.callForLogin(call, "deliverydetails");
        }
        catch (JSONException e) {
            e.printStackTrace();
        }
    }


    @Override
    public void onBackPressed() {
        session = new SessionManagement(getApplicationContext());
        HashMap<String, String> user = session.getUserDetails();

        Intent intent;
        intent = new Intent(Accessory_Delivery_Details_Activity.this, Accessory_Receipt_Delivery_Activity.class);
        intent.putExtra("name", user.get(SessionManagement.KEY_USER));
        intent.putExtra("userid", user.get(SessionManagement.KEY_USER_ID));
        intent.putExtra("processorid", user.get(SessionManagement.KEY_PROCESSOR_ID));
        intent.putExtra("vendors", vendors);
        intent.putExtra("pendingcount", pendingcount);
        intent.putExtra("vendor_del_count", vendor_del_count);
        intent.putExtra("vendorname", vendorname);
        intent.putExtra("vendorid", vendorid);
        intent.putExtra("quantity", del_quantity);
        intent.putExtra("joborderno", joborderno);
        intent.putExtra("stylename", stylename);
        intent.putExtra("orderid", orderid);
        intent.putExtra("del_count", del_count);

        Accessory_Delivery_Details_Activity.custPrograssbar_new.closePrograssBar();
        startActivity(intent);
        finish();
    }

    public void getvalue() {
        txtUser.setText("Hello " + this.User);
        ModelClass modelClass = new ModelClass();
        modelClass.setmID(userid);
        progress.setVisibility(View.INVISIBLE);
    }


    public boolean isOnline() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        return cm.getActiveNetworkInfo() != null && cm.getActiveNetworkInfo().isConnectedOrConnecting();
    }

    @Override
    public void callback(JsonObject result, String callNo) {
        try {

            if (callNo.equalsIgnoreCase("deliverydetails"))
            {
                JSONObject jsonObject = new JSONObject(result.toString());
                String mStatus = jsonObject.optString("status");
                String message = jsonObject.optString("message");
                if (mStatus.equals("success")) {
                    JSONObject jsonObj = jsonObject.getJSONObject("data");

                    JSONObject dcdetailsJsonObj = jsonObj.getJSONObject("dcdetails");
                    JSONObject vendorJsonObj = dcdetailsJsonObj.getJSONObject("vendor");
                    JSONObject detailsJsonObj = dcdetailsJsonObj.getJSONObject("details");

                    String dcid = dcdetailsJsonObj.optString("dcid");
                    String dcno = dcdetailsJsonObj.optString("dcno");
                    String dcdate = dcdetailsJsonObj.optString("dcdate");
                    String poid = dcdetailsJsonObj.optString("poid");
                    String pono = dcdetailsJsonObj.optString("pono");
                    String podate = dcdetailsJsonObj.optString("podate");
                    String jobref = dcdetailsJsonObj.optString("jobref");
                    text_Job_Ref.setText(jobref);
                    try {

                        TableLayout programtbl = findViewById(R.id.addprogramdata);
                        Iterator<String> prog = detailsJsonObj.keys();
                        while (prog.hasNext()) {
                            String key = prog.next();
                            if (detailsJsonObj.get(key) instanceof JSONObject) {
                                String sizes =  ((JSONObject) detailsJsonObj.get(key)).getString("sizes");
                                String accessory =  ((JSONObject) detailsJsonObj.get(key)).getString("accessory");

                                String o_quantity = ((JSONObject) detailsJsonObj.get(key)).getString("quantity");
                                String o_contractorid = ((JSONObject) detailsJsonObj.get(key)).getString("contractorid");
                                String o_isreceived = ((JSONObject) detailsJsonObj.get(key)).getString("isreceived");
                                String o_contractorcode =((JSONObject) detailsJsonObj.get(key)).getString("contractorcode");

                                JSONObject accessory_obj = new JSONObject(accessory);
                                String category = (String) accessory_obj.get("category");
                                String name = (String) accessory_obj.get("name");
                                text_Accessory_Name.setText(category+"\n"+name);
                                if(type.equals("S")) {
                                    try {

                                        JSONObject size_obj = new JSONObject(sizes);
                                        Iterator<String> accessory_receipt_contents = size_obj.keys();
                                        while (accessory_receipt_contents.hasNext()) {
                                            String keyd = accessory_receipt_contents.next();
                                            if (size_obj.get(keyd) instanceof JSONObject) {

                                                final int sizedetid = Integer.parseInt(((JSONObject) size_obj.get(keyd)).getString("sizedetid"));
                                                String sizename = ((JSONObject) size_obj.get(keyd)).getString("sizename");
                                                String quantity = ((JSONObject) size_obj.get(keyd)).getString("quantity");
                                                String contractorid = ((JSONObject) size_obj.get(keyd)).getString("contractorid");
                                                String isreceived = ((JSONObject) size_obj.get(keyd)).getString("isreceived");
                                                String contractorcode = ((JSONObject) size_obj.get(keyd)).getString("contractorcode");

                                                /* Create a new row to be added. */
                                                TableRow tr = new TableRow(Accessory_Delivery_Details_Activity.this);
                                                tr.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT));
                                                tr.setPadding(0, 0, 2, 0);

                                                TableRow.LayoutParams params = new TableRow.LayoutParams();
                                                params.span = 2;
                                                params.weight = 1;

                                                TextView txtsizename = new TextView(Accessory_Delivery_Details_Activity.this);
                                                txtsizename.setText(sizename);
                                                txtsizename.setTextColor(Color.BLACK);
                                                txtsizename.setTextSize(2, 16);
                                                txtsizename.setGravity(Gravity.CENTER);
                                                tr.addView(txtsizename);

                                                TextView txt_quantity = new TextView(Accessory_Delivery_Details_Activity.this);
                                                txt_quantity.setText(quantity);
                                                txt_quantity.setTextColor(Color.BLACK);
                                                txt_quantity.setTextSize(2, 16);
                                                txt_quantity.setGravity(Gravity.CENTER);
                                                tr.addView(txt_quantity);

                                                if (contractorid.equals(processorid)) {
                                                    if (isreceived.equals("Y")) {
                                                        ImageView img = new ImageView(Accessory_Delivery_Details_Activity.this);
                                                        img.setImageResource(R.drawable.ic_green_ok);
                                                        params.gravity = Gravity.CENTER;
                                                        img.setLayoutParams(params);
                                                        tr.addView(img);
                                                    }
                                                    else {
                                                        final CheckBox singleCheckbox = new CheckBox(Accessory_Delivery_Details_Activity.this);
                                                        singleCheckbox.setOnClickListener(new View.OnClickListener() {
                                                            @Override
                                                            public void onClick(View v) {
                                                                if (singleCheckbox.isChecked()) {
                                                                    singleCheckbox.setChecked(true);
                                                                    Size_detail_Cntarr.add(sizedetid);
                                                                }
                                                                else if (!singleCheckbox.isChecked())
                                                                {
                                                                    singleCheckbox.setChecked(false);
                                                                    for (int i = 0; i < Size_detail_Cntarr.toArray().length; i++) {
                                                                        if (Size_detail_Cntarr.toArray()[i].equals(sizedetid)) {
                                                                            Size_detail_Cntarr.remove(i);
                                                                        }
                                                                    }
                                                                }
                                                            }
                                                        });

                                                        if(quantity.equals("0"))
                                                        {
                                                            singleCheckbox.setChecked(true);
                                                            singleCheckbox.setEnabled(false);
                                                        }
                                                        else{
                                                            singleCheckbox.setChecked(true);
                                                        }

                                                        Size_detail_Cntarr.add(sizedetid);
                                                        singleCheckbox.setMovementMethod(LinkMovementMethod.getInstance());
                                                        params.gravity = Gravity.CENTER;
                                                        singleCheckbox.setLayoutParams(params);
                                                        tr.addView(singleCheckbox);
                                                    }
                                                }
                                                else {
                                                    TextView txt_contractor_code = new TextView(Accessory_Delivery_Details_Activity.this);
                                                    txt_contractor_code.setText(contractorcode);
                                                    txt_contractor_code.setTextColor(Color.BLACK);
                                                    txt_contractor_code.setTextSize(2, 14);
                                                    txt_contractor_code.setGravity(Gravity.RIGHT);
                                                    tr.addView(txt_contractor_code);
                                                }

                                                programtbl.addView(tr, new TableLayout.LayoutParams(TableLayout.LayoutParams.MATCH_PARENT, TableLayout.LayoutParams.WRAP_CONTENT));
                                                tr.setPadding(15, 30, 2, 0);
                                                postListIndex++;
                                            }
                                        }
                                    }
                                    catch (Throwable t) {
                                        Log.e("My App", "Could not parse malformed JSON: \"" + jsonObject + "\"");
                                    }
                                }
                                else
                                {
                                    /* Create a new row to be added. */
                                    TableRow tr = new TableRow(Accessory_Delivery_Details_Activity.this);
                                    tr.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT));
                                    tr.setPadding(0, 0, 2, 0);

                                    TableRow.LayoutParams params = new TableRow.LayoutParams();
                                    params.span = 2;
                                    params.weight = 1;

                                    TextView txtsizename = new TextView(Accessory_Delivery_Details_Activity.this);
                                    txtsizename.setText("All Sizes");
                                    txtsizename.setTextColor(Color.BLACK);
                                    txtsizename.setTextSize(2, 16);
                                    txtsizename.setGravity(Gravity.CENTER);
                                    tr.addView(txtsizename);

                                    TextView txt_quantity = new TextView(Accessory_Delivery_Details_Activity.this);
                                    txt_quantity.setText(o_quantity);
                                    txt_quantity.setTextColor(Color.BLACK);
                                    txt_quantity.setTextSize(2, 16);
                                    txt_quantity.setGravity(Gravity.CENTER);
                                    tr.addView(txt_quantity);

                                    if (o_contractorid.equals(processorid)) {
                                        if (o_isreceived.equals("Y")) {
                                            ImageView img = new ImageView(Accessory_Delivery_Details_Activity.this);
                                            img.setImageResource(R.drawable.ic_green_ok);
                                            AddProg.setEnabled(false);
                                            params.gravity = Gravity.CENTER;
                                            img.setLayoutParams(params);
                                            tr.addView(img);
                                        }
                                        else {

                                            final CheckBox singleCheckbox = new CheckBox(Accessory_Delivery_Details_Activity.this);
                                            singleCheckbox.setOnClickListener(new View.OnClickListener() {
                                                @Override
                                                public void onClick(View v) {
                                                    if (singleCheckbox.isChecked()) {
                                                        singleCheckbox.setChecked(true);
                                                        AddProg.setEnabled(true);

                                                    }
                                                    else if (!singleCheckbox.isChecked()) {
                                                        singleCheckbox.setChecked(false);
                                                        AddProg.setEnabled(false);
                                                    }
                                                }
                                            });
                                            singleCheckbox.setChecked(true);
                                            singleCheckbox.setMovementMethod(LinkMovementMethod.getInstance());
                                            params.gravity = Gravity.CENTER;
                                            singleCheckbox.setLayoutParams(params);
                                            tr.addView(singleCheckbox);
                                        }
                                    }
                                    else {
                                        TextView txt_contractor_code = new TextView(Accessory_Delivery_Details_Activity.this);
                                        txt_contractor_code.setText(o_contractorcode);
                                        txt_contractor_code.setTextColor(Color.BLACK);
                                        txt_contractor_code.setTextSize(2, 14);
                                        txt_contractor_code.setGravity(Gravity.RIGHT);
                                        tr.addView(txt_contractor_code);
                                    }
                                    programtbl.addView(tr, new TableLayout.LayoutParams(TableLayout.LayoutParams.MATCH_PARENT, TableLayout.LayoutParams.WRAP_CONTENT));
                                    tr.setPadding(15, 30, 2, 0);

                                }
                            }
                        }
                        AddProg.setVisibility(View.VISIBLE);
                        AddReject.setVisibility(View.VISIBLE);
                    }
                    catch (JSONException e) {
                        e.printStackTrace();
                        Log.e("NidhviTec", "JSONException: " + e);
                    }
                }
                else if (mStatus.equals("logout"))
                {
                    new AlertDialog.Builder(Accessory_Delivery_Details_Activity.this)
                            .setMessage(message)
                            .setCancelable(false)
                            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface arg1, int arg0) {
                                    arg1.dismiss();
                                    session.logoutUser();
                                    finishAffinity();
                                    finish();
                                }
                            }).show();
                }
                else {
                    new AlertDialog.Builder(Accessory_Delivery_Details_Activity.this)
                            .setMessage(message)
                            .setCancelable(false)
                            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface arg1, int arg0) {
                                    arg1.dismiss();
                                    onBackPressed();
                                }
                            }).show();
                }
                Accessory_Delivery_Details_Activity.custPrograssbar_new.closePrograssBar();

            }
            else  if (callNo.equalsIgnoreCase("receivedelivery")) {
                JSONObject jsonObject = new JSONObject(result.toString());
                String mStatus = jsonObject.optString("status");
                String message = jsonObject.optString("message");
                if (mStatus.equals("success")) {
                    new AlertDialog.Builder(Accessory_Delivery_Details_Activity.this)
                            .setMessage(message)
                            .setCancelable(false)
                            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface arg1, int arg0) {
                                    arg1.dismiss();
                                    int dd = Integer.parseInt(del_count)-1;
                                    del_count = String.valueOf(dd);
                                    onBackPressed();
                                }
                            }).show();
                }
                else {
                    new AlertDialog.Builder(Accessory_Delivery_Details_Activity.this)
                            .setMessage(message)
                            .setCancelable(false)
                            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface arg1, int arg0) {
                                    arg1.dismiss();
                                }
                            }).show();
                }
                Accessory_Delivery_Details_Activity.custPrograssbar_new.closePrograssBar();
            }

        }
        catch (Exception e) {
        }
    }

}
