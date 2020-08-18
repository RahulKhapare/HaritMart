package grocery.app;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.WindowManager;

import com.adoisstudio.helper.Api;
import com.adoisstudio.helper.H;
import com.adoisstudio.helper.Json;
import com.adoisstudio.helper.LoadingDialog;
import com.adoisstudio.helper.Session;

import grocery.app.common.P;
import grocery.app.util.WindowBarColor;

public class SplashScreenActivity extends AppCompatActivity {

    private LoadingDialog loadingDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        WindowBarColor.setColor(this);
        setContentView(R.layout.activity_splash_screen);

        loadingDialog = new LoadingDialog(this);
        hitCartTokenApi();
        hitInitApi();
    }
    

    private void hitInitApi() {
        Api.newApi(this, P.baseUrl+"init").addJson(new Json())
                .setMethod(Api.GET)
                .onLoading(isLoading -> {
                    if (!isDestroyed()) {
                        if (isLoading)
                            loadingDialog.show("loading...");
                        else
                            loadingDialog.hide();
                    }
                })
                .onError(() -> {

                })
                .onSuccess(j -> {
                    if (j.getInt(P.status) == 1) {
                        new Handler().postDelayed(()->{
                            Intent intent = new Intent(this,OnboardingActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(intent);
                            finish();
                        },1230);

                    } else {
                        H.showMessage(this, "Something went wrong");
                    }
                })
                .run("hitInitApi");
    }

    private void hitCartTokenApi() {
        Session session = new Session(this);
        Json j = new Json();
        j.addString(P.cart_token, session.getString(P.cart_token));
        j.addJSON(P.option, new Json());
        Api.newApi(this, P.baseUrl + "cart_token").addJson(j)
                .setMethod(Api.POST)
                //.onHeaderRequest(App::getHeaders)
                .onError(() -> {

                })
                .onSuccess(json ->
                {
                    if (json.getInt(P.status) == 1) {
                        json = json.getJson(P.data);
                        session.addString(P.cart_token, json.getString(P.cart_token));
                    }
                })
                .run("hitCartTokenApi");
    }
}