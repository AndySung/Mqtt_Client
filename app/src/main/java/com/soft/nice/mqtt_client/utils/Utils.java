package com.soft.nice.mqtt_client.utils;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.widget.Toast;

public class Utils {
    public static Toast toast;
    /**
     * 吐丝的方法，可以避免重复吐丝。当你点击多次按钮的时候，吐丝只出现一次。
     * @param context 上下文对象
     * @param string    吐丝的内容
     */
    public static void showToast(Context context, String string) {
        // TODO Auto-generated method stub
        if(toast == null){
            // 如果Toast对象为空了，那么需要创建一个新的Toast对象
            toast = Toast.makeText(context, string, Toast.LENGTH_LONG);
        }else {
            // 如果toast对象还存在，那么就不再创建新的Toast对象
            toast.setText(string);
        }
        // 最后调用show方法吐丝
        toast.show();
    }

    public static void showAlertDialog(Context context, String title, String message) {
        AlertDialog alertDialog = new AlertDialog.Builder(context).create();
        alertDialog.setTitle(title);
        alertDialog.setMessage(message);
        alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        alertDialog.show();
    }

    /**
     * str 要分割的字符串 比如： "tcp://192.168.1.1"
     * delimeter // 指定分割字符 比如 "-"
     * **/
    public static String SplitTwoPartString(String str, String delimeter, int whichPart) {
        String[] temp;
        temp = str.split(delimeter); // 分割字符串
        return temp[whichPart];
    }
}
