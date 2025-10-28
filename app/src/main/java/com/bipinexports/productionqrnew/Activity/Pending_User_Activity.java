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
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.bipinexports.productionqrnew.APIClient;
import com.bipinexports.productionqrnew.GetResult;
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

import retrofit2.Call;

public class Pending_User_Activity extends AppCompatActivity implements View.OnClickListener, GetResult.MyListener {

    String Id, User;
    SessionManagement session;

    GridView gridView;
    ProgressBar progress;
    ArrayList<ModelClass> mylist = new ArrayList<>();
    String processorid;
    String userid;

    ImageView imageView;
    TextView txtUser, pending_text;
    String username;

    String myversionName;
    public static CustPrograssbar_new custPrograssbar_new;
    String brand, model, androidVersion, uniqueId;
    SwipeRefreshLayout swipeRefreshLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.pending_user_activity);

        txtUser = (TextView) findViewById(R.id.txtUser);

        imageView = (ImageView) findViewById(R.id.imgd);
        gridView = (GridView) this.findViewById(R.id.grid);
        progress = (ProgressBar) findViewById(R.id.progress);
        custPrograssbar_new = new CustPrograssbar_new();
        Pending_User_Activity.custPrograssbar_new.prograssCreate(this);

        session = new SessionManagement(getApplicationContext());
        HashMap<String, String> user = session.getUserDetails();
        String name = user.get(SessionManagement.KEY_USER);
        this.Id = user.get(SessionManagement.KEY_PROCESSOR_ID);
        processorid = user.get(SessionManagement.KEY_PROCESSOR_ID);
        userid = user.get(SessionManagement.KEY_USER_ID);
        this.User = user.get(SessionManagement.KEY_USER);

        processorid = getIntent().getStringExtra("processorid");
        userid = getIntent().getStringExtra("userid");
        username = getIntent().getStringExtra("name");

        pending_text = findViewById(R.id.pending_text);

        versioncode();
        getvalue();
        imageView.setOnClickListener(this);
        getDeviceDetails();

        swipeRefreshLayout = findViewById(R.id.swiperefresh);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                swipeRefreshLayout.setRefreshing(false);
                getDeviceDetails();
            }
        });
        swipeRefreshLayout.setColorSchemeColors(Color.BLACK);
    }

    private void versioncode() {
        if (isOnline()) {
            Context context = this;
            PackageManager manager = context.getPackageManager();
            try {
                PackageInfo info = manager.getPackageInfo(context.getPackageName(), 0);
                myversionName = info.versionName;
                Log.e("Bipin", "myversionName :" + myversionName);
            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
                myversionName = "Unknown-01";
            }
        } else {

            AlertDialog alertDialog = new AlertDialog.Builder(Pending_User_Activity.this)
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
                    PopupMenu popup = new PopupMenu(Pending_User_Activity.this, imageView);
                    popup.getMenuInflater().inflate(R.menu.menu_chgpswd, popup.getMenu());

                    popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                        public boolean onMenuItemClick(MenuItem item) {
                            if (item.getItemId() == R.id.logout) {
                                session.logoutUser();
                            }
                            return true;
                        }
                    });
                    popup.show();
                    break;
            }
        } else {
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
        if (userid == null || "null".equals(userid)) {
            txtUser.setText("Hello " + this.User);
        }
        else
        {
            txtUser.setText("Hello " + username);
        }
        ModelClass modelClass = new ModelClass();
        modelClass.setmID(userid);
        mylist.add(modelClass);
    }


    public boolean isOnline() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        return cm.getActiveNetworkInfo() != null && cm.getActiveNetworkInfo().isConnectedOrConnecting();
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void callback(JsonObject result, String callNo) {
        try {
            if (callNo.equalsIgnoreCase("device_details")) {
                JSONObject jsonObject = new JSONObject(result.toString());
                String mStatus = jsonObject.optString("status");
                String message = jsonObject.optString("message");
                Pending_User_Activity.custPrograssbar_new.closePrograssBar();
                if (mStatus.equals("success")) {

                    Intent intent = new Intent(Pending_User_Activity.this, Switch_User_Activity.class);
                    intent.putExtra("myversionName", myversionName);
                    intent.putExtra("username", username);
                    intent.putExtra("userid", userid);
                    intent.putExtra("processorid", processorid);
                    startActivity(intent);
                    finish();
                }
                else if (mStatus.equals("pending"))
                {
                    pending_text.setText(message);
                }
                else {
                    new AlertDialog.Builder(Pending_User_Activity.this)
                            .setMessage(message)
                            .setCancelable(false)
                            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface arg1, int arg0) {
                                    arg1.dismiss();
                                }
                            }).show();
                }
            }
        } catch (Exception e) {
        }
    }

    private void getDeviceDetails() {
        if (isOnline()) {

            if (userid == null || "null".equals(userid))
            {
                Pending_User_Activity.custPrograssbar_new.prograssCreate(this);
                session = new SessionManagement(getApplicationContext());
                HashMap<String, String> user = session.getUserDetails();
                processorid = user.get(SessionManagement.KEY_PROCESSOR_ID);
                userid = user.get(SessionManagement.KEY_USER_ID);
                username = user.get(SessionManagement.KEY_USER);
            }

            brand = Build.BRAND;
            // Retrieve the device's model
            model = Build.MODEL;
            // Retrieve the Android version
            androidVersion = Build.VERSION.RELEASE;
            // Retrieve the unique device ID (Android ID)
            uniqueId = Settings.Secure.getString(this.getContentResolver(), Settings.Secure.ANDROID_ID);

            JSONObject jsonObject = new JSONObject();
            try {
                jsonObject.put("userid", userid);
                jsonObject.put("username", username);
                jsonObject.put("brand", brand);
                jsonObject.put("model", model);
                jsonObject.put("android_version", androidVersion);
                jsonObject.put("unique_id", uniqueId);

                JsonParser jsonParser = new JsonParser();
                Call<JsonObject> call = APIClient.getInterface().update_device_details((JsonObject) jsonParser.parse(jsonObject.toString()));
                GetResult getResult = new GetResult();
                getResult.setMyListener(this);
                getResult.callForLogin(call, "device_details");
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } else {
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

}
