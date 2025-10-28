package com.bipinexports.productionqrnew.Activity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ProgressBar;

import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.bipinexports.productionqrnew.APIClient;
import com.bipinexports.productionqrnew.GetResult;
import com.bipinexports.productionqrnew.ModelClass;
import com.bipinexports.productionqrnew.R;
import com.bipinexports.productionqrnew.SessionManagement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import retrofit2.Call;

public class Delivery_Details_Activity extends BaseActivity implements View.OnClickListener, GetResult.MyListener {

    private View content;
    String Id, User;

    SessionManagement session;
    GridView gridView;
    ProgressBar progress;
    String processorid, dcid, poid, pono, podate,vendorname, dcno, dcdate,type;
    String userid, delivery, pendingcount;
    int postListIndex = 0;
    List Size_detail_Cntarr = new ArrayList<String>();

    ImageView imageView;
    Button AddProg, AddReject;


    TextView text_PO_No, text_PO_Date, text_Vendor_Name, text_DC_No, text_DC_Date, text_Job_Ref, text_Accessory_Name;
    TextView txtUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_base);
        setupDrawer();

        content = getLayoutInflater().inflate(
                R.layout.activity_delivery_details,
                findViewById(R.id.content_frame),
                true
        );


        imageView = content.findViewById(R.id.imgd);
        gridView = content.findViewById(R.id.grid);
        progress = content.findViewById(R.id.progress);
        progress.setVisibility(View.VISIBLE);

        delivery = getIntent().getStringExtra("delivery");
        pendingcount = getIntent().getStringExtra("pendingcount");

        txtUser = content.findViewById(R.id.txtUser);

        session = new SessionManagement(getApplicationContext());
        HashMap<String, String> user = session.getUserDetails();
        this.Id = user.get(SessionManagement.KEY_PROCESSOR_ID);
        processorid = user.get(SessionManagement.KEY_PROCESSOR_ID);
        userid = user.get(SessionManagement.KEY_USER_ID);
        this.User = user.get(SessionManagement.KEY_USER);

        processorid = getIntent().getStringExtra("processorid");
        dcid = getIntent().getStringExtra("dcid");
        poid = getIntent().getStringExtra("poid");

        pono = getIntent().getStringExtra("pono");
        podate = getIntent().getStringExtra("podate");
        vendorname = getIntent().getStringExtra("vendorname");
        dcno = getIntent().getStringExtra("dcno");
        dcdate = getIntent().getStringExtra("dcdate");
        type = getIntent().getStringExtra("type");

        imageView.setOnClickListener(this);

        text_PO_No = content.findViewById(R.id.text_PO_No);
        text_PO_Date = content.findViewById(R.id.text_PO_Date);
        text_Vendor_Name = content.findViewById(R.id.text_Vendor_Name);
        text_DC_No = content.findViewById(R.id.text_DC_No);
        text_DC_Date = content.findViewById(R.id.text_DC_Date);
        text_Job_Ref = content.findViewById(R.id.text_Job_Ref);
        text_Accessory_Name = content.findViewById(R.id.text_Accessory_Name);

        text_PO_No.setText(pono);
        text_PO_Date.setText(podate);
        text_Vendor_Name.setText(vendorname);
        text_DC_No.setText(dcno);
        text_DC_Date.setText(dcdate);
        text_Job_Ref.setText("");

        getvalue();
        get_deliverydetails();

        AddProg = content.findViewById(R.id.AddProg);
        AddReject = content.findViewById(R.id.AddReject);
        AddProg.setOnClickListener(this);
        AddReject.setOnClickListener(this);
        AddProg.setVisibility(View.INVISIBLE);
        AddReject.setVisibility(View.INVISIBLE);
    }

    public void onClick(View v) {
        if (isOnline()) {
            switch (v.getId()) {
                case R.id.imgd:
                    toggleDrawer();
                    break;
                case R.id.AddProg:
                    progress.setVisibility(View.VISIBLE);
                    AddProgramDetails();
                    break;
                case R.id.AddReject:
                    progress.setVisibility(View.VISIBLE);
                    onBackPressed();
                    break;
            }
        }
        else {
            AlertDialog alertDialog = new AlertDialog.Builder(Delivery_Details_Activity.this)
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
        progress.setVisibility(View.VISIBLE);

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
        progress.setVisibility(View.VISIBLE);

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
        Intent intent = new Intent(Delivery_Details_Activity.this, Accessory_Receipt_Activity.class);
        intent.putExtra("name", user.get(SessionManagement.KEY_USER));
        intent.putExtra("userid", user.get(SessionManagement.KEY_USER_ID));
        intent.putExtra("processorid", user.get(SessionManagement.KEY_PROCESSOR_ID));
        intent.putExtra("delivery", delivery.toString());
        intent.putExtra("pendingcount", pendingcount);

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
            progress.setVisibility(View.GONE);

            if (callNo.equalsIgnoreCase("deliverydetails")) {
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
                                                TableRow tr = new TableRow(Delivery_Details_Activity.this);
                                                tr.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT));
                                                tr.setPadding(0, 0, 2, 0);

                                                TableRow.LayoutParams params = new TableRow.LayoutParams();
                                                params.span = 2;
                                                params.weight = 1;

                                                TextView txtsizename = new TextView(Delivery_Details_Activity.this);
                                                txtsizename.setText(sizename);
                                                txtsizename.setTextColor(Color.BLACK);
                                                txtsizename.setTextSize(2, 16);
                                                txtsizename.setGravity(Gravity.CENTER);
                                                tr.addView(txtsizename);

                                                TextView txt_quantity = new TextView(Delivery_Details_Activity.this);
                                                txt_quantity.setText(quantity);
                                                txt_quantity.setTextColor(Color.BLACK);
                                                txt_quantity.setTextSize(2, 16);
                                                txt_quantity.setGravity(Gravity.CENTER);
                                                tr.addView(txt_quantity);

                                                if (contractorid.equals(processorid)) {
                                                    if (isreceived.equals("Y")) {
                                                        ImageView img = new ImageView(Delivery_Details_Activity.this);
                                                        img.setImageResource(R.drawable.ic_green_ok);
                                                        params.gravity = Gravity.CENTER;
                                                        img.setLayoutParams(params);
                                                        tr.addView(img);
                                                    }
                                                    else {
                                                        final CheckBox singleCheckbox = new CheckBox(Delivery_Details_Activity.this);
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
                                                    TextView txt_contractor_code = new TextView(Delivery_Details_Activity.this);
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
                                    TableRow tr = new TableRow(Delivery_Details_Activity.this);
                                    tr.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT));
                                    tr.setPadding(0, 0, 2, 0);

                                    TableRow.LayoutParams params = new TableRow.LayoutParams();
                                    params.span = 2;
                                    params.weight = 1;

                                    TextView txtsizename = new TextView(Delivery_Details_Activity.this);
                                    txtsizename.setText("All Sizes");
                                    txtsizename.setTextColor(Color.BLACK);
                                    txtsizename.setTextSize(2, 16);
                                    txtsizename.setGravity(Gravity.CENTER);
                                    tr.addView(txtsizename);

                                    TextView txt_quantity = new TextView(Delivery_Details_Activity.this);
                                    txt_quantity.setText(o_quantity);
                                    txt_quantity.setTextColor(Color.BLACK);
                                    txt_quantity.setTextSize(2, 16);
                                    txt_quantity.setGravity(Gravity.CENTER);
                                    tr.addView(txt_quantity);

                                    if (o_contractorid.equals(processorid)) {
                                        if (o_isreceived.equals("Y")) {
                                            ImageView img = new ImageView(Delivery_Details_Activity.this);
                                            img.setImageResource(R.drawable.ic_green_ok);
                                            AddProg.setEnabled(false);
                                            params.gravity = Gravity.CENTER;
                                            img.setLayoutParams(params);
                                            tr.addView(img);
                                        }
                                        else {

                                            final CheckBox singleCheckbox = new CheckBox(Delivery_Details_Activity.this);
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
                                        TextView txt_contractor_code = new TextView(Delivery_Details_Activity.this);
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
                    new AlertDialog.Builder(Delivery_Details_Activity.this)
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
                    new AlertDialog.Builder(Delivery_Details_Activity.this)
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
            else  if (callNo.equalsIgnoreCase("receivedelivery")) {
                JSONObject jsonObject = new JSONObject(result.toString());
                String mStatus = jsonObject.optString("status");
                String message = jsonObject.optString("message");
                progress.setVisibility(View.GONE);
                if (mStatus.equals("success")) {
                    new AlertDialog.Builder(Delivery_Details_Activity.this)
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
                    new AlertDialog.Builder(Delivery_Details_Activity.this)
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
        catch (Exception e) {
        }
    }

}
