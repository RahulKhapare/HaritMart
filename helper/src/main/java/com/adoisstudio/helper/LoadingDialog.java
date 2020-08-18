package com.adoisstudio.helper;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import android.os.Build;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.TextView;

import com.victor.loading.rotate.RotateLoading;

/**
 * Created by amitkumar on 15/01/18.
 */

public class LoadingDialog extends Dialog {

    private RotateLoading rotateLoading;

    public LoadingDialog(@NonNull Context context) {
        super(context);
//        requestWindowFeature(Window.FEATURE_ACTION_BAR);

        setContentView(R.layout.helper_dialog_loading);
        rotateLoading = findViewById(R.id.rotateLoading);
        rotateLoading.start();
        getWindow().setBackgroundDrawable(new ColorDrawable(Color.argb(5, 0, 0, 0)));
        getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);

    }//initx

    public LoadingDialog(@NonNull Context context, boolean cancelable) {
        super(context);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setCancelable(cancelable);

        setContentView(R.layout.helper_dialog_loading);

        rotateLoading = findViewById(R.id.rotateLoading);
        rotateLoading.start();

        getWindow().setBackgroundDrawable(new ColorDrawable(Color.argb(50, 0, 0, 0)));
        getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
    }//init

    public LoadingDialog(@NonNull Context context, String msg, boolean cancelable) {
        super(context);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setCancelable(cancelable);

        setContentView(R.layout.helper_dialog_loading);
        rotateLoading = findViewById(R.id.rotateLoading);
        rotateLoading.start();

        ((TextView) findViewById(R.id.msg)).setText(msg);

        getWindow().setBackgroundDrawable(new ColorDrawable(Color.argb(50, 0, 0, 0)));
        getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
    }//init

    @Override
    public void show() {
        super.show();
    }

    public void show(String msg) {
        ((TextView) findViewById(R.id.msg)).setText(msg);
        super.show();
    }

    @Override
    public void hide() {
        rotateLoading.stop();
        super.hide();
    }

    public void setVisibility(boolean isVisible) {
        if (isVisible)
            show();
        else
            dismiss();
    }

    public void setVisibility(boolean isVisible, String msg) {
        if (isVisible)
            show(msg);
        else
            dismiss();
    }

}//class