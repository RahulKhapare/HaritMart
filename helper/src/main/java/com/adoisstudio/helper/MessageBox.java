package com.adoisstudio.helper;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.TextView;

/**
 * Created by amitkumar on 08/01/18.
 */

public class MessageBox {

    public static void showOkMessage(Context context, String title, String msg) {
        showOkMessage(context, title, msg, new OnOkListener() {
            @Override
            public void onOk() {
            }
        });
    }//

    public static void showOkMessage(Context context, String title, String msg, final OnOkListener listener) {

        final Dialog dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        //dialog.setCancelable(false);
        dialog.setContentView(R.layout.helper_dialog_message_ok);

        ((TextView) dialog.findViewById(R.id.title)).setText(title);
        ((TextView) dialog.findViewById(R.id.message)).setText(msg);

        dialog.findViewById(R.id.btn_ok).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                listener.onOk();
            }
        });

        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.argb(50, 0, 0, 0)));
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        dialog.show();
    }//

    public static void showYesNoMessage(Context context, String title, String msg, String yes, String no,
                                        final OnYesNoListener listener) {

        final Dialog dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.helper_dialog_message_yes_no);

        ((TextView) dialog.findViewById(R.id.title)).setText(title);
        ((TextView) dialog.findViewById(R.id.message)).setText(msg);
        ((TextView) dialog.findViewById(R.id.btn_yes)).setText(yes);
        ((TextView) dialog.findViewById(R.id.btn_no)).setText(no);

        dialog.findViewById(R.id.btn_yes).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                listener.onYesNo(true);
            }
        });

        dialog.findViewById(R.id.btn_no).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                listener.onYesNo(false);
            }
        });

        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.argb(50, 0, 0, 0)));
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        dialog.show();
    }//

    public interface OnOkListener {
        void onOk();
    }

    public interface OnYesNoListener {
        void onYesNo(boolean isYes);
    }

}//class