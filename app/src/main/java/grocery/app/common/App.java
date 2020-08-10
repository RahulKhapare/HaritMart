package grocery.app.common;

import android.app.Application;
import android.provider.Settings;

import com.adoisstudio.helper.Json;
import com.adoisstudio.helper.JsonList;
import com.adoisstudio.helper.Static;


public class App extends Application {

    public static String categoryImageUrl = "";
    public static JsonList categoryJsonList = new JsonList();
    public static Json selectedCategoryJson = new Json();

    public static String device_id = "";
    public static String session_id = "";
    public static boolean IS_DEV = true;

    public static String generatedOtp = "";
    public static String api_key="";


    @Override
    public void onCreate() {
        super.onCreate();

        device_id = Settings.Secure.getString(this.getContentResolver(), Settings.Secure.ANDROID_ID);

        Static.show_log = true;


    }//init

}//class