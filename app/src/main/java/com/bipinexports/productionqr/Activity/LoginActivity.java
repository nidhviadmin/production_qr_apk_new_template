package com.bipinexports.productionqr.Activity;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.os.Build;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputLayout;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;

import com.bipinexports.productionqr.APIClient;
import com.bipinexports.productionqr.GetResult;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.bipinexports.productionqr.R;
import com.bipinexports.productionqr.SessionManagement;

import org.json.JSONException;
import org.json.JSONObject;
import retrofit2.Call;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener, GetResult.MyListener {

    EditText et1, et2;
    private TextInputLayout inputLayoutName, inputLayoutPassword;
    Button btn;
    ProgressBar progress;
    SessionManagement session;
    private static final int MY_REQUEST_CODE = 786;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        session = new SessionManagement(getApplicationContext());
        session.checkLogin();

        et1 = (EditText) findViewById(R.id.et1);
        et2 = (EditText) findViewById(R.id.et2);
        btn = (Button) findViewById(R.id.btn);
        inputLayoutName = (TextInputLayout) findViewById(R.id.input_layout_name);
        inputLayoutPassword = (TextInputLayout) findViewById(R.id.input_layout_password);
        progress = (ProgressBar) findViewById(R.id.progress);
        final String user = et1.getText().toString();
        final String pass = et2.getText().toString();


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
        {
            if (checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED &&
                    checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED &&
                    checkSelfPermission(Manifest.permission.ACCESS_NETWORK_STATE) != PackageManager.PERMISSION_GRANTED)
            {
                requestPermissions(new String[]{Manifest.permission.CAMERA,
                                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                                Manifest.permission.ACCESS_NETWORK_STATE},
                        MY_REQUEST_CODE);
            }
        }
        btn.setOnClickListener(this);
    }

    @Override
    public void onClick(View v)
    {
        if (v.getId() == R.id.btn)
        {
            if (isOnline())
            {
                if (et1.length() == 0)
                {
                    et1.setError("Enter Username");
                    et1.requestFocus();
                }
                else if (et2.length() == 0)
                {
                    et2.setError("Enter Password");
                    et2.requestFocus();
                }
                else
                {
                    progress.setVisibility(View.VISIBLE);

                    JSONObject jsonObject = new JSONObject();
                    try {
                        jsonObject.put("username", et1.getText().toString());
                        jsonObject.put("password", et2.getText().toString());

                        JsonParser jsonParser = new JsonParser();
                        Call<JsonObject> call = APIClient.getInterface().login((JsonObject) jsonParser.parse(jsonObject.toString()));
                        GetResult getResult = new GetResult();
                        getResult.setMyListener(this);
                        getResult.callForLogin(call, "Login");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            } else {
                Snackbar snackbar = Snackbar
                        .make(v, "No internet connection!", Snackbar.LENGTH_LONG);
                snackbar.show();
            }
        }
    }

    public boolean isOnline() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        return cm.getActiveNetworkInfo() != null && cm.getActiveNetworkInfo().isConnectedOrConnecting();
    }

    @Override
    public void callback(JsonObject result, String callNo) {
        try {
            progress.setVisibility(View.GONE);
            if (callNo.equalsIgnoreCase("Login")) {
                JSONObject jsonObject = new JSONObject(result.toString());
                String mStatus = jsonObject.optString("status");
                String message = jsonObject.optString("message");
                if (mStatus.equals("success")) {

                    JSONObject mData = jsonObject.optJSONObject("data");
                    String mID = mData.optString("id");
                    String mUser = mData.optString("username");
                    String mProcessorid = mData.optString("processorid");
                    String isqc = mData.optString("isqc");
                    String unitid = mData.optString("unitid");

                    session.createLoginSession(mUser, et2.getText().toString(), mProcessorid, mID, isqc, unitid);
                    Intent intent = new Intent(LoginActivity.this, Pending_User_Activity.class);
                    intent.putExtra("name", mUser);
                    intent.putExtra("processorid", mProcessorid);
                    intent.putExtra("password", et2.getText().toString());
                    intent.putExtra("userid", mID);
                    intent.putExtra("isqc", isqc);
                    startActivity(intent);
                    finish();
                }
                else {
                    new AlertDialog.Builder(LoginActivity.this)
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
            }
        }
        catch (Exception e) {
        }
    }
}
