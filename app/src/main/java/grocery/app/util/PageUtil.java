package grocery.app.util;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.view.Window;

import com.adoisstudio.helper.Session;

import grocery.app.OnboardingActivity;
import grocery.app.R;

public class PageUtil {

    public static void goToLoginPage(Context context,String flagValue){
        Dialog dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.activity_login_dialog);

        dialog.findViewById(R.id.txtYes).setOnClickListener(v ->
        {
            dialog.cancel();
            checkValue(flagValue);
//            new Session(context).clear();
            Intent baseIntent = new Intent(context, OnboardingActivity.class);
            baseIntent.putExtra(Config.UPDATE_LOGIN_FLAG,true);
//            baseIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            context.startActivity(baseIntent);
        });

        dialog.findViewById(R.id.txtNo).setOnClickListener(v -> {
            dialog.cancel();
        });

        dialog.setCancelable(true);
        dialog.show();
    }

    private static void checkValue(String flagValue){
        if (flagValue.equals(LoginFlag.productDetailFlagValue)){
            LoginFlag.productDetailFlag = true;
        }else if (flagValue.equals(LoginFlag.notificationFlagValue)){
            LoginFlag.notificationFlag = true;
        }else if (flagValue.equals(LoginFlag.profileFlagValue)){
            LoginFlag.profileFlag = true;
        }else if (flagValue.equals(LoginFlag.cartFlagValue)){
            LoginFlag.cartFlag = true;
        }else if (flagValue.equals(LoginFlag.favoriteFlagValue)){
            LoginFlag.favoriteFlag = true;
        }
    }

}
