package com.cloudpos.utils;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.cloudpos.injectkey.demo.R;


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

        activity.runOnUiThread(() -> {
            Toast toast = Toast.makeText(activity, message, duration);
            //绑定视图
            LinearLayout layout = (LinearLayout)toast.getView();
            Logger.debug("showToast(%s)", layout);
            if(layout != null){
//                    设置背景 我这里设置的是纯颜色 可以设置任何资源文件
                    layout.setBackgroundResource(R.color.colorPrimary);
//                    获取Toast默认文字显示ID
                    TextView tv = (TextView) layout.getChildAt(0);
                    //设置字体大小
                    tv.setTextSize(16);
                    //设置字体颜色
                    tv.setTextColor(Color.WHITE);
                    //Toast显示的位置
                    toast.setGravity(Gravity.BOTTOM, 0, 96);

            }else{
                toast.setGravity(Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 200);
            }
            toast.show();
        });
    }

    public static void showDialogInfo(final Activity activity, final String message) {
        Logger.debug(message);
        activity.runOnUiThread(() -> {
            final AlertDialog.Builder dialog = new AlertDialog.Builder(activity);
            dialog.setMessage(message);
            dialog.setPositiveButton("OK",
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            //...To-do
                        }
                    });
            dialog.show();
        });
    }

}
