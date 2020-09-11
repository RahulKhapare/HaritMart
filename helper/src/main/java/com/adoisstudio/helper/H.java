package com.adoisstudio.helper;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Build;
import android.text.Html;
import android.text.Spanned;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.StringTokenizer;

/**
 * Created by amitkumar on 05/01/18.
 */

public class H {

    public static void showOkDialog(Context context, String title, String msg) {

        final AlertDialog alertDialog = new AlertDialog.Builder(context).create();
        alertDialog.setTitle(title);
        alertDialog.setMessage(msg);
        alertDialog.setCancelable(false);

        alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                alertDialog.dismiss();
            }
        });

        alertDialog.show();

    }//show ok dialog

    public static void showOkDialog(Context context, String title, String msg, final OnOkListener listener) {

        final AlertDialog alertDialog = new AlertDialog.Builder(context).create();
        alertDialog.setTitle(title);
        alertDialog.setMessage(msg);
        alertDialog.setCancelable(false);

        alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //alertDialog.dismiss();
                listener.onOk();
            }
        });

        alertDialog.show();

    }//show ok dialog


    public static void showYesNoDialog(Context context, String title, String msg, final OnYesNoListener listener) {

        final AlertDialog alertDialog = new AlertDialog.Builder(context).create();
        alertDialog.setTitle(title);
        alertDialog.setMessage(msg);
        alertDialog.setCancelable(false);

        alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                alertDialog.dismiss();
                listener.onDecision(true);
            }
        });
        alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                alertDialog.dismiss();
                listener.onDecision(false);
            }
        });

        alertDialog.show();

    }//show yes no dialog

    public static void showYesNoDialog(Context context, String title, String msg,
                                       String yesText, String noText, final OnYesNoListener listener) {

        final AlertDialog alertDialog = new AlertDialog.Builder(context).create();
        alertDialog.setTitle(title);
        alertDialog.setMessage(msg);
        alertDialog.setCancelable(false);

        alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, yesText, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                alertDialog.dismiss();
                listener.onDecision(true);
            }
        });
        alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, noText, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                alertDialog.dismiss();
                listener.onDecision(false);
            }
        });

        alertDialog.show();

    }//show yes no dialog

    public interface OnOkListener {
        void onOk();
    }

    public interface OnYesNoListener {
        void onDecision(boolean isYes);
    }

    public static Spanned convertHtml(String text) {

        if (text == null) text = "";

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N)
            return Html.fromHtml(text, Html.FROM_HTML_MODE_COMPACT);
        else
            return Html.fromHtml(text);
    }

    //Documents on http://www.adoisstudio.com/android/helper/log
    public static void log(String tag, String msg)
    {
        try {
            if (!Static.show_log)
                return;

            StackTraceElement[] stackTraceElement = Thread.currentThread()
                    .getStackTrace();
            int currentIndex = -1;
            for (int i = 0; i < stackTraceElement.length; i++) {
                if (stackTraceElement[i].getMethodName().compareTo("log") == 0) {
                    currentIndex = i + 1;
                    break;
                }
            }

            String fullClassName = stackTraceElement[currentIndex].getClassName();
            String className = fullClassName.substring(fullClassName
                    .lastIndexOf(".") + 1);
            String methodName = stackTraceElement[currentIndex].getMethodName();
            String lineNumber = String
                    .valueOf(stackTraceElement[currentIndex].getLineNumber());

            Log.e(tag, msg + " : " + className + "." + methodName + "(" + className + ".java:" + lineNumber + ")");
        }
        catch (Exception e)
        {
            Log.e("exceptionIs",e+"");
        }

        //Log.e(tag, msg);
    }//log


    public static long getLong(String string) {
        long l = 0;
        try {
            l = Long.parseLong(string);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return l;
    }

    public static double getDouble(String val) {

        try {
            return Double.parseDouble(val);
        } catch (Exception ex) {
            return 0;
        }
    }

    public static float convertDpToPixel(float dp, Context context) {
        return dp * ((float) context.getResources().getDisplayMetrics().densityDpi / DisplayMetrics.DENSITY_DEFAULT);
    }

    public static float convertPixelsToDp(float px, Context context) {
        return px / ((float) context.getResources().getDisplayMetrics().densityDpi / DisplayMetrics.DENSITY_DEFAULT);
    }

    public static void showMessage(Context context, String msg) {
        Toast toast = Toast.makeText(context, msg, Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.TOP | Gravity.CENTER_HORIZONTAL, 0, 200);
        toast.show();
    }

    public static void hideKeyBoard(Context context, View view)
    {
        InputMethodManager imm = (InputMethodManager)context.getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm!=null)
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    public static void showKeyBoard(Context context, View view)
    {
        InputMethodManager imm = (InputMethodManager)context.getSystemService(Context.INPUT_METHOD_SERVICE);
        view.requestFocus();
        if (imm!=null)
            imm.showSoftInput(view, 0);
    }

    public static int getInt(String string)
    {
        int i = -1;
        try {
            i = Integer.parseInt(string);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return i;
    }

    public static String extractNumberFromString(String string)
    {
        if (string == null)
            return "";

        String str = "";

        char ch=' ';

        for (int i=0; i<string.length(); i++)
        {
            ch = string.charAt(i);
            if (ch == 46 || (ch >=48 && ch<=57))
                str = str + ch;
        }

        return str;
    }

    public static JSONArray extractJsonArray(String string, ArrayList<String> nameList, ArrayList<String> idList)
    {
        StringTokenizer stringTokenizer = new StringTokenizer(string, ",");
        int i;
        JSONArray jsonArray = new JSONArray();
        while (stringTokenizer.hasMoreTokens()) {
            string = stringTokenizer.nextToken().trim();
            i = nameList.indexOf(string);
            if (i != -1)
                jsonArray.put(idList.get(i));
        }

        return jsonArray;
    }
}
