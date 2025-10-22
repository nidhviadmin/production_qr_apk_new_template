package com.bipinexports.productionqr.Activity;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.view.WindowManager;

import com.bipinexports.productionqr.R;

public class CustPrograssbar_new {
    private static ProgressDialog progressDialog;
    private static Context currentContext;

    public void prograssCreate(Context context) {
        try {
            // Store the current context
            currentContext = context;

            if (progressDialog != null && progressDialog.isShowing()) {
                return;
            } else {
                progressDialog = new ProgressDialog(context, R.style.TransparentProgressDialog);
                if (progressDialog != null && !progressDialog.isShowing()) {
                    progressDialog.setMessage("Progress...");
                    progressDialog.setCancelable(false); // Prevent dismiss on touch outside
                    progressDialog.show();

                    // Disable user interaction with the screen
                    disableUserInteraction();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void closePrograssBar() {
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
            // Enable user interaction with the screen
            enableUserInteraction();
        }
    }

    private static void disableUserInteraction() {
        if (currentContext instanceof Activity) {
            ((Activity) currentContext).getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                    WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
        }
    }

    private static void enableUserInteraction() {
        if (currentContext instanceof Activity) {
            ((Activity) currentContext).getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
        }
    }
}
