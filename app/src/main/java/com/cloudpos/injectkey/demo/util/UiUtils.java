package com.cloudpos.injectkey.demo.util;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.widget.Toast;


/**
 * Created by lizhou on 18-3-20.
 */

public class UiUtils {

    public static void showToastLong(final Activity activity, final String message) {
        showToast(activity, message, Toast.LENGTH_LONG);
    }

    public static void showToastShort(final Activity activity, final String message) {
        showToast(activity, message, Toast.LENGTH_SHORT);
    }

    public static void showToast(final Activity activity, final String message, final int duration) {
        Logger.debug(message);
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(activity, message, duration).show();
            }
        });
    }

    public static void showDialogInfo(final Activity activity, final String message) {
        Logger.debug(message);
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                final AlertDialog.Builder normalDialog = new AlertDialog.Builder(activity);
                normalDialog.setMessage(message);
                normalDialog.setPositiveButton("OK",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                //...To-do
                            }
                        });
                normalDialog.show();
            }
        });
    }

}
