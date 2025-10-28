package com.bipinexports.productionqrnew.Activity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.bipinexports.productionqrnew.APIClient;
import com.bipinexports.productionqrnew.GetResult;
import com.bipinexports.productionqrnew.UserDataObject;
import com.bipinexports.productionqrnew.ModelClass;
import com.bipinexports.productionqrnew.R;
import com.bipinexports.productionqrnew.SessionManagement;
import com.google.android.material.snackbar.Snackbar;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;


import retrofit2.Call;

public class Switch_User_Activity extends AppCompatActivity implements View.OnClickListener, GetResult.MyListener {

    String Id, User;
    SessionManagement session;

    private RecyclerView.LayoutManager mLayoutManager;
    private Switch_User_Adapter mAdapter;
    GridView gridView;
    ProgressBar progress;
    ArrayList<ModelClass> mylist = new ArrayList<>();
    String processorid;
    String userid;

    ImageView imageView;
    TextView txtUser;
    String username;

    String myversionName;

    RecyclerView recyclerView;
    public static CustPrograssbar_new custPrograssbar_new;

    private String employee_data_obj = "";
    private JSONObject employee_jsonobj;

    String brand, model, androidVersion, uniqueId;

    SwipeRefreshLayout swipeRefreshLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_switch_user);

        txtUser = (TextView) findViewById(R.id.txtUser);

        imageView = (ImageView) findViewById(R.id.imgd);
        gridView = (GridView) this.findViewById(R.id.grid);
        progress = (ProgressBar) findViewById(R.id.progress);
        custPrograssbar_new = new CustPrograssbar_new();
        Switch_User_Activity.custPrograssbar_new.prograssCreate(this);

        session = new SessionManagement(getApplicationContext());
        HashMap<String, String> user = session.getUserDetails();
        String name = user.get(SessionManagement.KEY_USER);
        this.Id = user.get(SessionManagement.KEY_PROCESSOR_ID);
        processorid = user.get(SessionManagement.KEY_PROCESSOR_ID);
        userid = user.get(SessionManagement.KEY_USER_ID);
        this.User = user.get(SessionManagement.KEY_USER);

        processorid = getIntent().getStringExtra("processorid");
        userid = getIntent().getStringExtra("userid");
        username = getIntent().getStringExtra("username");

        recyclerView = findViewById(R.id.recyclerView);
        swipeRefreshLayout = findViewById(R.id.swiperefresh);

        versioncode();
        getvalue();
        imageView.setOnClickListener(this);

        getDeviceDetails();


        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                swipeRefreshLayout.setRefreshing(false);
                fetch_user_details();
            }
        });
        swipeRefreshLayout.setColorSchemeColors(Color.BLACK);
        fetch_user_details();
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

            AlertDialog alertDialog = new AlertDialog.Builder(Switch_User_Activity.this)
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


    @Override
    public void onClick(View v) {
        if (isOnline()) {
            switch (v.getId()) {

                case R.id.imgd:
                    PopupMenu popup = new PopupMenu(Switch_User_Activity.this, imageView);
                    popup.getMenuInflater().inflate(R.menu.menu_main, popup.getMenu());

                    popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                        public boolean onMenuItemClick(MenuItem item) {
                            if (item.getItemId() == R.id.log) {
                                session.logoutUser();
                                finish();
                            } else if (item.getItemId() == R.id.changepassword) {
                                Intent intent = new Intent(Switch_User_Activity.this, ChangepasswordActivity.class);
                                startActivity(intent);
                                finish();
                            }
                            return true;
                        }
                    });
                    popup.show();
                    break;
            }
        }
        else {
            Snackbar snackbar = Snackbar
                    .make(v, "No internet connection!", Snackbar.LENGTH_LONG);
            snackbar.show();
        }
    }

    @Override
    public void onBackPressed() {
        new AlertDialog.Builder(this)
            .setTitle("Exit")
            .setMessage("Are you sure you want to exit?")
            .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialogInterface, int which) {
                    finishAffinity();
                    finish();
                }
            })
            .setNegativeButton("No", null)
            .show();
    }

    public void getvalue() {
        txtUser.setText("Hello " + username);
        ModelClass modelClass = new ModelClass();
        modelClass.setmID(userid);
        mylist.add(modelClass);
        progress.setVisibility(View.GONE);
    }


    private ArrayList<UserDataObject> getDataSet() {
        ArrayList<UserDataObject> results = new ArrayList<>();
        try {
            JSONObject jsonObj = new JSONObject(employee_data_obj);
            employee_jsonobj = jsonObj.getJSONObject("user_details");
            Iterator<String> employee_contents = employee_jsonobj.keys();

            while (employee_contents.hasNext()) {
                String key = employee_contents.next();
                JSONObject userObj = employee_jsonobj.getJSONObject(key);

                String id = userObj.getString("id");
                String username = userObj.getString("qruser");
                String new_code = userObj.getString("new_code");
                String processorid = userObj.getString("processorid");
                String section_name = userObj.getString("section_name");
                String unitid = userObj.getString("unitid");
                String isqc = userObj.getString("isqc");
                String index = userObj.getString("index");
                String accessid = userObj.getString("accessid");
                String status = userObj.getString("status");
                String message = userObj.getString("message");

                UserDataObject obj = new UserDataObject(index, id, username, new_code, processorid, section_name, unitid, isqc, accessid, status, message);
                results.add(obj);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return results;
    }


    public boolean isOnline() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        return cm.getActiveNetworkInfo() != null && cm.getActiveNetworkInfo().isConnectedOrConnecting();
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void callback(JsonObject result, String callNo) {
        try {
            if (callNo.equalsIgnoreCase("userlist")) {
                JSONObject jsonObject = new JSONObject(result.toString());
                String mStatus = jsonObject.optString("status");
                String message = jsonObject.optString("message");
                String user_count = jsonObject.optString("usercount");

                if (mStatus.equals("success") ) {

                    if(Integer.parseInt(user_count) > 1)
                    {
                        JSONObject userDetailsObject = jsonObject.getJSONObject("data").getJSONObject("user_details");
                        employee_data_obj = jsonObject.getJSONObject("data").toString();
                        ArrayList<UserDataObject> dataList = getDataSet();
                        if (mAdapter == null)
                        {
                            recyclerView.setHasFixedSize(true);
                            mLayoutManager = new LinearLayoutManager(Switch_User_Activity.this);
                            recyclerView.setLayoutManager(mLayoutManager);
                            mAdapter = new Switch_User_Adapter(this, dataList);
//                            mAdapter = new Switch_User_Adapter(this,getDataSet());

                            ((Switch_User_Adapter) mAdapter).setOnItemClickListener(new Switch_User_Adapter.MyClickListener() {
                                @Override
                                public void onItemClick(int position, View v) {

                                    String current_id =((Switch_User_Adapter) mAdapter).getId(new String(String.valueOf(position)));
                                    String new_code =((Switch_User_Adapter) mAdapter).getNew_code(new String(String.valueOf(position)));
                                    String current_processor_id =((Switch_User_Adapter) mAdapter).getProcessorid(new String(String.valueOf(position)));
                                    String current_username =((Switch_User_Adapter) mAdapter).getUsername(new String(String.valueOf(position)));
                                    String section_name =((Switch_User_Adapter) mAdapter).getSection_name(new String(String.valueOf(position)));
                                    String current_unitid =((Switch_User_Adapter) mAdapter).getUnitid(new String(String.valueOf(position)));
                                    String current_isqc =((Switch_User_Adapter) mAdapter).getIsqc(new String(String.valueOf(position)));
                                    String current_accessid =((Switch_User_Adapter) mAdapter).getAccessid(new String(String.valueOf(position)));
                                    String current_status =((Switch_User_Adapter) mAdapter).getStatus(new String(String.valueOf(position)));
                                    String current_message =((Switch_User_Adapter) mAdapter).getMessage(new String(String.valueOf(position)));

                                    if(current_accessid.equals("null"))
                                    {
                                        username = current_username;
                                        new AlertDialog.Builder(Switch_User_Activity.this)
                                        .setMessage(current_message)
                                        .setCancelable(false)
                                        .setPositiveButton("YES", new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int which) {
                                                dialog.dismiss();
                                                session.createLoginSession(current_username, "1234", current_processor_id, current_id, current_isqc, current_unitid);
                                                Intent intent = new Intent(Switch_User_Activity.this, Pending_User_Activity.class);
                                                intent.putExtra("name", current_username);
                                                intent.putExtra("processorid", current_processor_id);
                                                intent.putExtra("password", "1234");
                                                intent.putExtra("userid", current_id);
                                                intent.putExtra("isqc", current_isqc);
                                                startActivity(intent);
                                                finish();
                                            }
                                        })
                                        .setNegativeButton("NO", new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int which) {
                                                dialog.dismiss();
                                            }
                                        })
                                        .show();
                                    }
                                    else
                                    {
                                        session.createLoginSession(current_username, "1234", current_processor_id, current_id, current_isqc, current_unitid);

                                        JSONObject jsonObject = new JSONObject();
                                        try {
                                            jsonObject.put("userid", current_id);
                                            jsonObject.put("username", current_username);

                                            jsonObject.put("brand", brand);
                                            jsonObject.put("model", model);
                                            jsonObject.put("android_version", androidVersion);
                                            jsonObject.put("unique_id", uniqueId);

                                            JsonParser jsonParser = new JsonParser();
                                            Call<JsonObject> call = APIClient.getInterface().update_user_lastin((JsonObject) jsonParser.parse(jsonObject.toString()));
                                            GetResult getResult = new GetResult();
                                            getResult.setMyListener(Switch_User_Activity.this);
                                            getResult.callForLogin(call, "update_user_lastin");
                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }

                                        Intent intent = new Intent(Switch_User_Activity.this, MainActivity.class);
                                        intent.putExtra("name", current_username);
                                        intent.putExtra("processorid", current_processor_id);
                                        intent.putExtra("password", "1234");
                                        intent.putExtra("userid", current_id);
                                        intent.putExtra("isqc", current_isqc);
                                        startActivity(intent);
                                        finish();
                                    }
                                }
                            });

                            GridLayoutManager layoutManager = new GridLayoutManager(this, 1);

                            recyclerView.setLayoutManager(layoutManager);
                            recyclerView.setAdapter(mAdapter);
                        }
                        else
                        {
                            mAdapter.updateData(dataList);
                        }
                    }
                    else  if(Integer.parseInt(user_count) == 1)
                    {
                        JSONObject userDetailsObject = jsonObject.getJSONObject("data").getJSONObject("user_details");
                        employee_data_obj = jsonObject.getJSONObject("data").toString();

                        String id = null, username = null, isqc = null, processorid = null, unitid = null;

                        Iterator<String> keys = userDetailsObject.keys();
                        if (keys.hasNext()) {
                            String key = keys.next(); // e.g., "142"
                            JSONObject user = userDetailsObject.getJSONObject(key);

                            id = user.getString("id");
                            username = user.getString("qruser");
                            isqc = user.getString("isqc");
                            processorid = user.getString("processorid");
                            unitid = user.getString("unitid");
                        }

                        session.createLoginSession(username, "1234", processorid, id, isqc, unitid);

                        jsonObject = new JSONObject();
                        try {
                            jsonObject.put("userid", id);
                            jsonObject.put("username", username);

                            jsonObject.put("brand", brand);
                            jsonObject.put("model", model);
                            jsonObject.put("android_version", androidVersion);
                            jsonObject.put("unique_id", uniqueId);

                            JsonParser jsonParser = new JsonParser();
                            Call<JsonObject> call = APIClient.getInterface().update_user_lastin((JsonObject) jsonParser.parse(jsonObject.toString()));
                            GetResult getResult = new GetResult();
                            getResult.setMyListener(Switch_User_Activity.this);
                            getResult.callForLogin(call, "update_user_lastin");
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        Intent intent = new Intent(Switch_User_Activity.this, MainActivity.class);
                        intent.putExtra("name", username);
                        intent.putExtra("processorid", processorid);
                        intent.putExtra("password", "1234");
                        intent.putExtra("userid", id);
                        intent.putExtra("isqc", isqc);
                        startActivity(intent);
                        finish();
                    }
                }
                else if (mStatus.equals("nodatafound")) {
                    if(mAdapter == null)
                    {
                        // Do nothing
                    }
                    new AlertDialog.Builder(Switch_User_Activity.this)
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
                    new AlertDialog.Builder(Switch_User_Activity.this)
                        .setMessage(message)
                        .setCancelable(false)
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface arg1, int arg0) {
                                arg1.dismiss();
                            }
                        }).show();
                }
                Switch_User_Activity.custPrograssbar_new.closePrograssBar();
            }
        }
        catch (Exception e) {
        }
    }

    private void getDeviceDetails() {
        if(isOnline()) {

            session = new SessionManagement(getApplicationContext());
            HashMap<String, String> user = session.getUserDetails();
            processorid = user.get(SessionManagement.KEY_PROCESSOR_ID);
            userid = user.get(SessionManagement.KEY_USER_ID);
            username = user.get(SessionManagement.KEY_USER);

             brand = Build.BRAND;
            // Retrieve the device's model
             model = Build.MODEL;
            // Retrieve the Android version
             androidVersion = Build.VERSION.RELEASE;
            // Retrieve the unique device ID (Android ID)
             uniqueId = Settings.Secure.getString(this.getContentResolver(), Settings.Secure.ANDROID_ID);
        }
        else {
            AlertDialog alertDialog = new AlertDialog.Builder(this)
                    .setMessage("Please Check Your Internet Connection")
                    .setCancelable(false)
                    .setNegativeButton("Exit", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface arg1, int arg0) {
                            arg1.dismiss();
                            onDestroy();
                            finishAffinity();
                        }
                    }).show();
            alertDialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(Color.RED);
        }
    }


    private void fetch_user_details() {

        Switch_User_Activity.custPrograssbar_new.prograssCreate(this);
        session = new SessionManagement(getApplicationContext());
        HashMap<String, String> user = session.getUserDetails();
        processorid = user.get(SessionManagement.KEY_PROCESSOR_ID);
        userid = user.get(SessionManagement.KEY_USER_ID);
        username = user.get(SessionManagement.KEY_USER);

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("userid", userid);
            jsonObject.put("processorid", processorid);
            jsonObject.put("username", username);
            jsonObject.put("sessionid",session);
            jsonObject.put("version_name",myversionName);

            jsonObject.put("brand", brand);
            jsonObject.put("model", model);
            jsonObject.put("android_version", androidVersion);
            jsonObject.put("unique_id", uniqueId);


            JsonParser jsonParser = new JsonParser();
            Call<JsonObject> call;
            call = APIClient.getInterface().get_user_data((JsonObject) jsonParser.parse(jsonObject.toString()));
            GetResult getResult = new GetResult();
            getResult.setMyListener(this);
            getResult.callForLogin(call, "userlist");
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

}
