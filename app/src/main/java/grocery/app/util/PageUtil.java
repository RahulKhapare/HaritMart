package grocery.app.util;

import android.content.Context;
import android.content.Intent;

import com.adoisstudio.helper.Session;

import grocery.app.OnboardingActivity;

public class PageUtil {

    public static void goToLoginPage(Context context){
        new Session(context).clear();
        Intent baseIntent = new Intent(context, OnboardingActivity.class);
        baseIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        context.startActivity(baseIntent);
    }
}
