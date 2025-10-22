package com.bipinexports.productionqr.Activity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.os.Bundle;
import com.google.android.material.snackbar.Snackbar;
import androidx.appcompat.app.AppCompatActivity;

import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bipinexports.productionqr.APIClient;
import com.bipinexports.productionqr.GetResult;
import com.bipinexports.productionqr.R;
import com.bipinexports.productionqr.SessionManagement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

import retrofit2.Call;

public class ChangepasswordActivity extends AppCompatActivity implements View.OnClickListener, GetResult.MyListener {

    String CurrentPassword;
    EditText txtCurrentPassword, txtNewPassword, txtRetypeNewPassword;
    Button btnSubmitPassword;
    SessionManagement session;
    ProgressBar progress;

    String User, unitid;
    ImageView imageView;
    TextView txtUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);

        txtCurrentPassword = findViewById(R.id.txtCurrentPassword);
        txtNewPassword = findViewById(R.id.txtNewPassword);
        txtRetypeNewPassword = findViewById(R.id.txtRetypeNewPassword);
        btnSubmitPassword = findViewById(R.id.btnSubmitPassword);

        btnSubmitPassword.setOnClickListener(this);
        progress = findViewById(R.id.progress);

        imageView = findViewById(R.id.imgd);
        imageView.setOnClickListener(this);
        txtUser = findViewById(R.id.txtUser);

        session = new SessionManagement(getApplicationContext());
        HashMap<String, String> user = session.getUserDetails();
        this.User = user.get(SessionManagement.KEY_USER);
        unitid = user.get(SessionManagement.KEY_UNITID);
        txtUser.setText("Hello " + this.User);
    }

    @Override
    public void onClick(View v) {
        if (isOnline())
        {
            switch (v.getId()) {
                case R.id.imgd:
                    PopupMenu popup = new PopupMenu(ChangepasswordActivity.this, imageView);
                    popup.getMenuInflater().inflate(R.menu.menu_chgpswd, popup.getMenu());
                    popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                        public boolean onMenuItemClick(MenuItem item) {
                            if (item.getItemId() == R.id.logout) {
                                session.logoutUser();
                                finish();
                            }
                            return true;
                        }
                    });
                    popup.show();
                    break;
                case R.id.btnSubmitPassword:
                    session = new SessionManagement(getApplicationContext());
                    HashMap<String, String> user = session.getUserDetails();

                    this.CurrentPassword = user.get(SessionManagement.KEY_PASSWORD);

                    if (txtCurrentPassword.length() == 0)
                    {
                        txtCurrentPassword.setError("Enter Current Password");
                        txtCurrentPassword.requestFocus();
                    }
                    else if (txtNewPassword.length() == 0)
                    {
                        txtNewPassword.setError("Enter New Password");
                        txtNewPassword.requestFocus();
                    }
                    else if (txtRetypeNewPassword.length() == 0)
                    {
                        txtRetypeNewPassword.setError("Retype New Password");
                        txtRetypeNewPassword.requestFocus();
                    }
                    // else if (!txtCurrentPassword.getText().toString().equals(this.CurrentPassword))
                    // {
                    //     txtCurrentPassword.setError("Current Password doesn't match");
                    //     txtCurrentPassword.requestFocus();
                    // }
                    // else if (txtCurrentPassword.getText().toString().equals(txtNewPassword.getText().toString()))
                    // {
                    //     txtNewPassword.setError("Current password and new password cannot be same.");
                    //     txtNewPassword.requestFocus();
                    // }
                    else if (!txtRetypeNewPassword.getText().toString().equals(txtNewPassword.getText().toString()))
                    {
                        txtNewPassword.setError("New and retyped passwords doesn't match");
                        txtNewPassword.requestFocus();
                    }
                    else
                    {
                        progress.setVisibility(View.VISIBLE);

                        JSONObject jsonObject = new JSONObject();
                        try {

                            jsonObject.put("userid", user.get(SessionManagement.KEY_USER_ID));
                            jsonObject.put("currentpassword", txtCurrentPassword.getText().toString());
                            jsonObject.put("newpassword", txtNewPassword.getText().toString());

                            JsonParser jsonParser = new JsonParser();
                            Call<JsonObject> call = APIClient.getInterface().changepassword((JsonObject) jsonParser.parse(jsonObject.toString()));
                            GetResult getResult = new GetResult();
                            getResult.setMyListener(ChangepasswordActivity.this);
                            getResult.callForLogin(call, "changepassword");
                        }
                        catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
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
        session = new SessionManagement(getApplicationContext());
        HashMap<String, String> user = session.getUserDetails();
        Intent intent = new Intent(ChangepasswordActivity.this, MainActivity.class);
        intent.putExtra("name", user.get(SessionManagement.KEY_USER));
        intent.putExtra("userid", user.get(SessionManagement.KEY_USER_ID));
        startActivity(intent);
        finish();
    }

    public boolean isOnline() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        return cm.getActiveNetworkInfo() != null && cm.getActiveNetworkInfo().isConnectedOrConnecting();
    }

    @Override
    public void callback(JsonObject result, String callNo) {
        try {
            progress.setVisibility(View.GONE);

            if (callNo.equalsIgnoreCase("changepassword")) {
                JSONObject jsonObject = new JSONObject(result.toString());
                String mStatus = jsonObject.optString("status");
                String message = jsonObject.optString("message");
                if (mStatus.equalsIgnoreCase("success")) {
                    AlertDialog alertDialog = new AlertDialog.Builder(ChangepasswordActivity.this)
                            .setMessage(message)
                            .setCancelable(false)
                            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface arg1, int arg0) {
                                    session = new SessionManagement(getApplicationContext());
                                    session.updatepassword(txtNewPassword.getText().toString());
                                    HashMap<String, String> user = session.getUserDetails();
                                    Intent intent = new Intent(ChangepasswordActivity.this, MainActivity.class);
                                    intent.putExtra("name", user.get(SessionManagement.KEY_USER));
                                    intent.putExtra("userid", user.get(SessionManagement.KEY_USER_ID));
                                    startActivity(intent);
                                    finish();
                                }
                            }).show();
                }
                else if (mStatus.equalsIgnoreCase("error")) {
                    AlertDialog alertDialog = new AlertDialog.Builder(ChangepasswordActivity.this)
                        .setMessage(message)
                        .setCancelable(false)
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface arg1, int arg0) {
                                session = new SessionManagement(getApplicationContext());
                                HashMap<String, String> user = session.getUserDetails();
                                Intent intent = new Intent(ChangepasswordActivity.this, MainActivity.class);
                                intent.putExtra("name", user.get(SessionManagement.KEY_USER));
                                intent.putExtra("userid", user.get(SessionManagement.KEY_USER_ID));
                                startActivity(intent);
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
