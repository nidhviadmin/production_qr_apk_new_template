package com.bipinexports.productionqrnew.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import androidx.core.content.ContextCompat;

import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;

import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
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
import com.loopj.android.http.AsyncHttpClient;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;

import retrofit2.Call;

import static java.lang.Boolean.FALSE;

public class Employee_Operation_Mapping_Details_Activity extends BaseActivity implements View.OnClickListener, GetResult.MyListener {


    private static String LOG_TAG = "CardViewActivity";

    SessionManagement session;
    TextView txtUser;
    ImageView imageView;
    String style_data_obj;
    String processorid;
    String userid, User, Id;
    ProgressBar progress;
    TextView text_Style_Name;
    EditText txt_operation;

    String empcode, empname, imgpath, hrmsrecid, operationid, hrmssecid, designation, operation, process, section_name, style_count,qrcontractorid, operation_defined,
            type, myversionName, selected_type;
    int qrsection_id;

    public static CustPrograssbar_new custPrograssbar_new;

    int i = 0;
    TextView EmpCode, txtEmpName, txtDesignation, txtProcess, txtOperation, txtSection;
    ImageView emp_Imge;
    HashMap<Integer, String> operations = new HashMap<Integer, String>();
    int selectedoperation = 0;
    int posoperation = 1;

    ListView listView;
    ArrayAdapter<String> adapter;

    LayoutInflater inflater1;
    ViewGroup header;
    String txtTitle;
    Dialog dialog;
    TextView txtView;
    JSONObject operationnames;

    Button btnUpdate,btnCancel;

    int postListIndex = 0;


    List Style_master_detail_arr = new ArrayList<String>();
    List Employee_operation_arr = new ArrayList<String>();
    List<String> mOperation = new ArrayList<String>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_employee_operation_mapping_details);

        setContentView(R.layout.activity_base);
        setupDrawer();

        View content = getLayoutInflater().inflate(
                R.layout.activity_employee_operation_mapping_details,
                findViewById(R.id.content_frame),
                true
        );

        txtUser = content.findViewById(R.id.txtUser);
        imageView = content.findViewById(R.id.imgd);
        progress = (ProgressBar) content.findViewById(R.id.progress);
//        progress.setVisibility(View.VISIBLE);
        custPrograssbar_new = new CustPrograssbar_new();

        txtUser = (TextView) content.findViewById(R.id.txtUser);

        session = new SessionManagement(getApplicationContext());
        HashMap<String, String> user = session.getUserDetails();
        String name = user.get(SessionManagement.KEY_USER);
        this.Id = user.get(SessionManagement.KEY_PROCESSOR_ID);
        processorid = user.get(SessionManagement.KEY_PROCESSOR_ID);
        userid = user.get(SessionManagement.KEY_USER_ID);
        this.User = user.get(SessionManagement.KEY_USER);

        processorid = getIntent().getStringExtra("processorid");
        empcode = getIntent().getStringExtra("empcode");
        type = getIntent().getStringExtra("type");
        selected_type = getIntent().getStringExtra("selected_type");
        myversionName = getIntent().getStringExtra("myversionName");

        empcode = getIntent().getStringExtra("empcode");
        empname = getIntent().getStringExtra("empname");
        imgpath = getIntent().getStringExtra("imgpath");
        hrmsrecid = getIntent().getStringExtra("hrmsrecid");
        operationid = getIntent().getStringExtra("operationid");
        hrmssecid = getIntent().getStringExtra("hrmssecid");
        designation = getIntent().getStringExtra("designation");
        operation = getIntent().getStringExtra("operation");
        process = getIntent().getStringExtra("process");
        section_name = getIntent().getStringExtra("section_name");
        qrcontractorid = getIntent().getStringExtra("qrcontractorid");
        style_count = "0";

        imageView.setOnClickListener(this);

        EmpCode = content.findViewById(R.id.txtEmpCode);
        txtEmpName = content.findViewById(R.id.txtEmpName);
        txtSection =content.findViewById(R.id.txtSection);
        txtDesignation = content.findViewById(R.id.txtDesignation);
        txtProcess = content.findViewById(R.id.txtProcess);
        txtOperation = content.findViewById(R.id.txtOperation);

        EmpCode.setText(empcode);
        txtEmpName.setText(empname);
        txtSection.setText(section_name);
        txtDesignation.setText(designation);
        txtProcess.setText(process);
        txtOperation.setText(operation);

        btnUpdate = content.findViewById(R.id.btnUpdate);
        btnCancel = content.findViewById(R.id.btnCancel);

        btnUpdate.setVisibility(View.GONE);
        btnCancel.setVisibility(View.GONE);

        btnUpdate.setOnClickListener(this);
        btnCancel.setOnClickListener(this);

        emp_Imge = content.findViewById(R.id.emp_Imge);

        if (imgpath == null || imgpath.equals("")) {

        }
        else {
            Picasso.get()
                    .load(imgpath)
                    .into(emp_Imge);
        }


        getvalue();
        get_style_operation_data();
    }


    private void get_style_operation_data() {
        Employee_Operation_Mapping_Details_Activity.custPrograssbar_new.prograssCreate(this);
        session = new SessionManagement(getApplicationContext());
        HashMap<String, String> user = session.getUserDetails();
        processorid = user.get(SessionManagement.KEY_PROCESSOR_ID);
        userid = user.get(SessionManagement.KEY_USER_ID);

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("userid", userid);
            jsonObject.put("processorid", processorid);
            jsonObject.put("empcode", empcode);

            JsonParser jsonParser = new JsonParser();
            Call<JsonObject> call = APIClient.getInterface().get_style_operation_data((JsonObject) jsonParser.parse(jsonObject.toString()));
            GetResult getResult = new GetResult();
            getResult.setMyListener(this);
            getResult.callForLogin(call, "style_operation_details");
        } 
        catch (JSONException e) {
            e.printStackTrace();
        }
    }


    public void getvalue() {
        txtUser.setText("Hello " + this.User);
        ModelClass modelClass = new ModelClass();
        modelClass.setmID(userid);
        progress.setVisibility(View.INVISIBLE);
    }

    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.imgd:
                toggleDrawer();
                break;
            case R.id.btnUpdate:
                Update_employee_operation();
                break;
            case R.id.btnCancel:
                onBackPressed();
                break;
        }
    }

    private void Update_employee_operation() {
        AsyncHttpClient asyncHttpClient = new AsyncHttpClient();
        session = new SessionManagement(getApplicationContext());
        HashMap<String, String> user = session.getUserDetails();
        processorid = user.get(SessionManagement.KEY_PROCESSOR_ID);
        String userid = user.get(SessionManagement.KEY_USER_ID);

        Employee_Operation_Mapping_Details_Activity.custPrograssbar_new.prograssCreate(this);

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("userid", userid);
            jsonObject.put("processorid", processorid);

            jsonObject.put("empcode", empcode);
            jsonObject.put("hrmsrecid",hrmsrecid);
            jsonObject.put("qrcontractorid",qrcontractorid);
            jsonObject.put("hrmssecid",hrmssecid);
            jsonObject.put("qrsection_id",qrsection_id);
            jsonObject.put("style_master_ids", Style_master_detail_arr);
            jsonObject.put("qr_operation_ids", Employee_operation_arr);

            JsonParser jsonParser = new JsonParser();
            Call<JsonObject> call = APIClient.getInterface().update_emp_style_operation_mapping((JsonObject) jsonParser.parse(jsonObject.toString()));
            GetResult getResult = new GetResult();
            getResult.setMyListener(this);
            getResult.callForLogin(call, "update_emp_style_operation_mapping");
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onBackPressed() {
        session = new SessionManagement(getApplicationContext());
        HashMap<String, String> user = session.getUserDetails();
        Intent intent = null;
        if(selected_type.equals("In"))
        {
            intent = new Intent(Employee_Operation_Mapping_Details_Activity.this, In_Employee_Operation_Mapping_Activity.class);
        }
        else  if(selected_type.equals("Out"))
        {
            intent = new Intent(Employee_Operation_Mapping_Details_Activity.this, Out_Employee_Operation_Mapping_Activity.class);
        }
        else  if(selected_type.equals("Absent"))
        {
            intent = new Intent(Employee_Operation_Mapping_Details_Activity.this, Absent_Employee_Operation_Mapping_Activity.class);
        }
        intent.putExtra("name", user.get(SessionManagement.KEY_USER));
        intent.putExtra("userid", user.get(SessionManagement.KEY_USER_ID));
        intent.putExtra("processorid", user.get(SessionManagement.KEY_PROCESSOR_ID));
        intent.putExtra("myversionName", myversionName);
        intent.putExtra("type", type);
        intent.putExtra("selected_type", selected_type);
        startActivity(intent);
        finish();
    }

    private TableRow findTableRow(View view) {
        // Loop through the parent views until a TableRow is found
        while (!(view instanceof TableRow) && view.getParent() != null) {
            view = (View) view.getParent();
        }

        // If a TableRow is found, return it
        if (view instanceof TableRow) {
            return (TableRow) view;
        } else {
            return null;
        }
    }

    private EditText findEditText(ViewGroup viewGroup) {
        // Loop through the children of the ViewGroup to find an EditText
        for (int i = 0; i < viewGroup.getChildCount(); i++) {
            View child = viewGroup.getChildAt(i);

            if (child instanceof EditText) {
                return (EditText) child;
            } else if (child instanceof ViewGroup) {
                // Recursively search in nested ViewGroups
                EditText foundEditText = findEditText((ViewGroup) child);
                if (foundEditText != null) {
                    return foundEditText;
                }
            }
        }

        // Return null if no EditText is found
        return null;
    }

    @Override
    public void callback(JsonObject result, String callNo) {
        try {
            progress.setVisibility(View.GONE);
            Employee_Operation_Mapping_Details_Activity.custPrograssbar_new.closePrograssBar();
            if(callNo.equalsIgnoreCase("style_operation_details"))
            {
                JSONObject jsonObject = new JSONObject(result.toString());
                String mStatus = jsonObject.optString("status");
                String message = jsonObject.optString("message");

                if (mStatus.equals("success")) {

//                    style_data_obj = jsonObject.getJSONObject("data")
                    JSONObject jsonObj = jsonObject.getJSONObject("data");

                    JSONObject data = jsonObject.getJSONObject("data");
                    JSONObject style_masters = data.getJSONObject("style_masters");

                    style_count = data.optString("style_count");
                    operation_defined = data.optString("operation_defined");
                    operationnames = data.getJSONObject("section_operations");

                    if(Integer.parseInt(style_count) > 0) {
                        postListIndex = 0;
                        int inexd = 1;
                        TableLayout programtbl = findViewById(R.id.addprogramdata2);
                        Iterator<String> keys = style_masters.keys();
                        while (keys.hasNext()) {
                            String key = keys.next();
                            JSONObject item = style_masters.getJSONObject(key);

                            String stylemaster_id = item.optString("stylemasterid");
                            String style_name = item.optString("style_name");
                            String operation_name = item.optString("operation_name");
                            String qr_operationid = item.optString("operation_id");

                            /* Create a new row to be added. */
                            TableRow tr = new TableRow(Employee_Operation_Mapping_Details_Activity.this);
                            tr.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT));
                            tr.setPadding(0, 0, 2, 0);

                            TableRow.LayoutParams params = new TableRow.LayoutParams();

                            LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                            View newRow = inflater.inflate(R.layout.emp_style_operation_card_view_row, null);
                            newRow.setId(postListIndex);

                            text_Style_Name = newRow.findViewById(R.id.text_Style_Name);
                            txt_operation = newRow.findViewById(R.id.txt_operation);

                            Log.e("Bipin","stylemaster_id : " +stylemaster_id);
                            text_Style_Name.setId(Integer.parseInt(stylemaster_id));
                            text_Style_Name.setText( inexd+"." + style_name);

                            int count_oper = 0;
                            try {
                                Iterator<String> operation2 = operationnames.keys();
                                while (operation2.hasNext()) {
                                    String key2 = operation2.next();
                                    if (operationnames.get(key2) instanceof JSONObject) {
                                        int id = Integer.parseInt(((JSONObject) operationnames.get(key2)).getString("id"));
                                        int stylemasterid = Integer.parseInt(((JSONObject) operationnames.get(key2)).getString("stylemasterid"));
                                        int section_id = Integer.parseInt(((JSONObject) operationnames.get(key2)).getString("section_id"));
                                        int hrms_section_id = Integer.parseInt(((JSONObject) operationnames.get(key2)).getString("hrms_section_id"));

                                        if(Integer.parseInt(stylemaster_id) == stylemasterid && Integer.parseInt(hrmssecid) == hrms_section_id)
                                        {
                                            count_oper++;
                                        }
                                    }
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                            if(count_oper ==0)
                            {
                                txt_operation.setText(operation_defined);
                                txt_operation.setTextColor(ContextCompat.getColor(this, R.color.red));
                            }
                            else
                            {
                                txt_operation.setText("Select Operation");
                            }
                            if(Integer.parseInt(qr_operationid) > 0)
                            {
                                txt_operation.setText(operation_name);
                                txt_operation.setTextColor(ContextCompat.getColor(this, R.color.green));
                            }

                            txt_operation.setId(Integer.parseInt(stylemaster_id));
                            txt_operation.setEnabled(true);
//                            txt_operation.setSelection(postListIndex);

                            txt_operation.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    dialog = new Dialog(Employee_Operation_Mapping_Details_Activity.this);
                                    final int clickedTextViewId = v.getId();

                                    TextView clickedTextView = (TextView) v;
                                    // Get the text value of the clicked TextView
                                    final String current_operation = clickedTextView.getText().toString();

                                    TableRow currentRow = findTableRow(v);
                                    final EditText editText = findEditText(currentRow);

                                    int posoper = 0;
                                    mOperation = new ArrayList<String>();
                                    operations = new HashMap<Integer, String>();
                                    try {
                                        Iterator<String> operation = operationnames.keys();
                                        while (operation.hasNext()) {
                                            String key = operation.next();
                                            if (operationnames.get(key) instanceof JSONObject) {
                                                int id = Integer.parseInt(((JSONObject) operationnames.get(key)).getString("id"));
                                                int stylemasterid = Integer.parseInt(((JSONObject) operationnames.get(key)).getString("stylemasterid"));
                                                int section_id = Integer.parseInt(((JSONObject) operationnames.get(key)).getString("section_id"));
                                                int hrms_section_id = Integer.parseInt(((JSONObject) operationnames.get(key)).getString("hrms_section_id"));

                                                if(Objects.equals(clickedTextViewId, stylemasterid) && Integer.parseInt(hrmssecid) == hrms_section_id)
                                                {
                                                    String operationname = ((JSONObject) operationnames.get(key)).getString("operation");
                                                    posoper++;
                                                    if((operationname.toUpperCase()).equals(current_operation.toUpperCase()))
                                                    {
                                                        posoperation = posoper;
                                                    }
                                                    qrsection_id = section_id;
                                                    operations.put(id, operationname);
                                                    mOperation.add(operationname);
                                                }
                                            }
                                        }
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }

                                    if (!mOperation.isEmpty()) {
                                        dialog.setContentView(R.layout.list_items);
                                        listView = dialog.findViewById(R.id.list_view);
                                        listView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
                                        listView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
                                        dialog.show();
                                        inflater1 = getLayoutInflater();
                                        header = (ViewGroup) inflater1.inflate(R.layout.list_item_heading, listView, false);
                                        txtView = header.findViewById(R.id.txtView);
                                        txtTitle = "SELECT OPERATION";
                                        txtView.setText(txtTitle);
                                        listView.addHeaderView(header, txtView, FALSE);

                                        adapter = new ArrayAdapter<String>(Employee_Operation_Mapping_Details_Activity.this, android.R.layout.simple_list_item_single_choice,
                                                android.R.id.text1, mOperation);
                                        listView.setAdapter(adapter);
                                        listView.setItemChecked(posoperation, true);

                                        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                            @Override
                                            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                                String itemValue = (String) listView.getItemAtPosition(position);
                                                posoperation = position;

                                                for (HashMap.Entry entry : operations.entrySet()) {
                                                    if (itemValue.equals(entry.getValue())) {
                                                        selectedoperation = Integer.parseInt(entry.getKey().toString());

                                                        if(Style_master_detail_arr.toArray().length > 0)
                                                        {
                                                            for (int i = 0; i < Style_master_detail_arr.toArray().length; i++)
                                                            {
                                                                if (Style_master_detail_arr.toArray()[i].equals(clickedTextViewId))
                                                                {
                                                                    Style_master_detail_arr.remove(i);
                                                                    Employee_operation_arr.remove(i);
                                                                }
                                                            }
                                                        }
                                                        Style_master_detail_arr.add(clickedTextViewId);
                                                        Employee_operation_arr.add(selectedoperation);
                                                        btnUpdate.setVisibility(View.VISIBLE);
                                                        break;
                                                    }
                                                }
                                                if (editText != null) {
                                                    editText.setText(itemValue);
                                                }
                                                dialog.dismiss();
                                                dialog = null;
                                                mOperation.clear();
                                                selectedoperation =0;
                                                posoperation = 0;
                                            }
                                        });
                                    }
                                }
                            });

                            params.gravity = Gravity.LEFT;
                            tr.addView(newRow);

                            programtbl.addView(tr, new TableLayout.LayoutParams(TableLayout.LayoutParams.MATCH_PARENT, TableLayout.LayoutParams.WRAP_CONTENT));
                            tr.setPadding(10, 30, 2, 0);
                            postListIndex++;
                            inexd++;

                            View v = new View(this);
                            v.setLayoutParams(new LinearLayout.LayoutParams(
                                    LinearLayout.LayoutParams.MATCH_PARENT,
                                    5
                            ));
                            v.setBackgroundColor(Color.parseColor("#917c1f"));
                            programtbl.addView(v);
                            Log.e("Bipin ","postListIndex :" +postListIndex);
                            // Use in your row inflater logic
                        }
                    }

                    btnCancel.setVisibility(View.VISIBLE);
                    Employee_Operation_Mapping_Details_Activity.custPrograssbar_new.closePrograssBar();

                }
                else if (mStatus.equals("nodatafound")) {
                    Employee_Operation_Mapping_Details_Activity.custPrograssbar_new.closePrograssBar();
                    btnCancel.setVisibility(View.VISIBLE);
                    new AlertDialog.Builder(Employee_Operation_Mapping_Details_Activity.this)
                        .setMessage(message)
                        .setCancelable(false)
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface arg1, int arg0) {
                                arg1.dismiss();
                            }
                        }).show();
                }
                else 
                {
                    Employee_Operation_Mapping_Details_Activity.custPrograssbar_new.closePrograssBar();
                    btnCancel.setVisibility(View.VISIBLE);
                    new AlertDialog.Builder(Employee_Operation_Mapping_Details_Activity.this)
                        .setMessage(message)
                        .setCancelable(false)
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface arg1, int arg0) {
                                arg1.dismiss();
                            }
                        }).show();
                }
            }
            else if(callNo.equalsIgnoreCase("update_emp_style_operation_mapping")) {
                JSONObject jsonObject = new JSONObject(result.toString());
                String mStatus = jsonObject.optString("status");
                String message = jsonObject.optString("message");
                Employee_Operation_Mapping_Details_Activity.custPrograssbar_new.closePrograssBar();
                btnCancel.setVisibility(View.VISIBLE);
                new AlertDialog.Builder(Employee_Operation_Mapping_Details_Activity.this)
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
        catch (Exception e) {
        }
    }
}
