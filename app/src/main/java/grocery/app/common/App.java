package grocery.app.common;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.provider.Settings;

import com.adoisstudio.helper.Api;
import com.adoisstudio.helper.H;
import com.adoisstudio.helper.Json;
import com.adoisstudio.helper.JsonList;
import com.adoisstudio.helper.Static;

import grocery.app.BaseActivity;


public class App extends Application {

    public static String categoryImageUrl = "";
    public static JsonList categoryJsonList = new JsonList();
    public static Json selectedCategoryJson = new Json();
    public static Json selectedSubCategoryJson = new Json();

    public static String device_id = "";
    public static String session_id = "";
    public static boolean IS_DEV = true;
    public static App app = new App();

    public static String generatedOtp = "";
    public static String api_key="";

    public static void startHomeActivity(Context context) {
        Intent intent = new Intent(context, BaseActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        context.startActivity(intent);
    }

    public void hitLogInWithOtpApi(OnSuccessCallBack onSuccessCallBack, Context context, Json json) {
        Api.newApi(context, P.baseUrl + "login_with_otp").addJson(json)
                .setMethod(Api.POST)
                //.onHeaderRequest(App::getHeaders)
                .onError(() -> {
                    H.showMessage(context, "On error is called");
                })
                .onSuccess(json1 ->
                {
                    if (json1.getInt(P.status) == 1) {
                        json1 = json1.getJson(P.data);
                        onSuccessCallBack.successCallBack(json1);

                    } else
                        H.showMessage(context, json1.getString(P.msg));
                })
                .run("hitLogInWithOtpApi");
    }

    public interface OnSuccessCallBack {
        void successCallBack(Json json);
    }


    @Override
    public void onCreate() {
        super.onCreate();

        device_id = Settings.Secure.getString(this.getContentResolver(), Settings.Secure.ANDROID_ID);

        Static.show_log = true;


    }//init

}//class