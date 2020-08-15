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

import grocery.app.common.P;

public class SplashScreenActivity extends AppCompatActivity {

    private LoadingDialog loadingDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_splash_screen);

        loadingDialog = new LoadingDialog(this);
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
                            Intent intent = new Intent(this,LoginScreen.class);
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
}