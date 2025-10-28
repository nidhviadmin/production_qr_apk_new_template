package com.bipinexports.productionqrnew;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

import com.bipinexports.productionqrnew.Activity.LoginActivity;
import com.bipinexports.productionqrnew.Activity.Pending_User_Activity;

import java.util.HashMap;

public class SessionManagement
{
    SharedPreferences pref;
    Editor editor;
    Context _context;
    int PRIVATE_MODE = 0;
    private static final String PREF_NAME = "BipinExports";
    private static final String IS_LOGIN = "IsLoggedIn";
    public static final String KEY_USER = "user";
    public static final String KEY_PASSWORD = "password";
    public static final String KEY_PROCESSOR_ID = "processorid";
    public static final String KEY_USER_ID = "id";
    public static final String KEY_ISQC = "isqc";
    public static final String KEY_UNITID = "unitid";

    public SessionManagement(Context context)
    {
        this._context = context;
        pref = _context.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
        editor = pref.edit();
    }

    public void createLoginSession(String user, String password, String processorid, String userid, String isqc, String unitid)
    {
        editor.putBoolean(IS_LOGIN, true);
        editor.putString(KEY_USER, user);
        editor.putString(KEY_PASSWORD, password);
        editor.putString(KEY_PROCESSOR_ID,processorid);
        editor.putString(KEY_USER_ID,userid);
        editor.putString(KEY_ISQC,isqc);
        editor.putString(KEY_UNITID,unitid);
        editor.commit();
    }

    public void updatepassword(String password)
    {
        editor.putString(KEY_PASSWORD, password);
        editor.commit();
    }

    public void checkLogin()
    {
        if(this.isLoggedIn())
        {
            Intent i = new Intent(_context, Pending_User_Activity.class);
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            _context.startActivity(i);
        }
    }

    public HashMap<String, String> getUserDetails(){
        HashMap<String, String> user = new HashMap<String, String>();
        // user name
        user.put(KEY_USER, pref.getString(KEY_USER, null));
        // processor id
        user.put(KEY_PROCESSOR_ID,pref.getString(KEY_PROCESSOR_ID,null));
        // user id
        user.put(KEY_USER_ID,pref.getString(KEY_USER_ID,null));
        // user type id
        user.put(KEY_ISQC,pref.getString(KEY_ISQC,null));
        // password
        user.put(KEY_PASSWORD,pref.getString(KEY_PASSWORD,null));
        // password
        user.put(KEY_UNITID,pref.getString(KEY_UNITID,null));
        // return user

        return user;
    }

    public void logoutUser()
    {
        editor.clear();
        editor.commit();
        Intent i = new Intent(_context, LoginActivity.class);
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        _context.startActivity(i);
    }

    public boolean isLoggedIn()
    {
        return pref.getBoolean(IS_LOGIN, false);
    }
}
