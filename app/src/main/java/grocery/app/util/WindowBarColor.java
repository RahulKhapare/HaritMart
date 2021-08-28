package grocery.app.util;

import android.app.Activity;
import android.content.Context;
import android.os.Build;
import android.view.View;

import androidx.core.content.ContextCompat;

import grocery.app.R;

public class WindowBarColor {

    public static void setColor(Activity activity){
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//            activity.getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
//            activity.getWindow().setStatusBarColor(ContextCompat.getColor(activity, R.color.green));
                activity.getWindow().setStatusBarColor(activity.getResources().getColor(R.color.colorPrimaryDark));
                activity.getWindow().setNavigationBarColor(activity.getResources().getColor(R.color.colorPrimaryDark));
            }
        }catch (Exception e){
        }

    }
}
