package grocery.app;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.MenuItem;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import com.adoisstudio.helper.Api;
import com.adoisstudio.helper.H;
import com.adoisstudio.helper.Json;
import com.adoisstudio.helper.LoadingDialog;
import com.adoisstudio.helper.Session;

import grocery.app.common.P;
import grocery.app.databinding.ActivityLoginScreenBinding;
import grocery.app.util.Click;
import grocery.app.util.Config;
import grocery.app.util.WindowBarColor;

public class LoginScreen extends AppCompatActivity {

    private ActivityLoginScreenBinding binding;
    private LoginScreen activity = this;
    private LoadingDialog loadingDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        WindowBarColor.setColor(this);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_login_screen);

        binding.toolbar.setTitle("");
        setSupportActionBar(binding.toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        loadingDialog = new LoadingDialog(this);
        binding.btnProcess.setOnClickListener(v -> {
            Click.preventTwoClick(v);
            checkValidation();
        });
    }

    public void checkValidation(){
        if (TextUtils.isEmpty(binding.etxNumber.getText().toString())){
            H.showMessage(activity,"Enter mobile number");
        }else if (binding.etxNumber.getText().toString().length()>10 || binding.etxNumber.getText().toString().length()<10){
            H.showMessage(activity,"Enter 10 digit mobile number");
        }else {
            hitLoginWithOpt();
        }
    }

    private void hitLoginWithOpt() {
        showProgress();

        Json j = new Json();
        j.addString(P.phone, binding.etxNumber.getText().toString().trim());
        j.addString(P.cart_token, new Session(activity).getString(P.cart_token));
        j.addString(P.otp, "");

        Api.newApi(activity, P.baseUrl + "login_with_otp").addJson(j)
                .setMethod(Api.POST)
                //.onHeaderRequest(App::getHeaders)
                .onError(() -> {
                    H.showMessage(activity, "On error is called");
                    hideProgress();
                })
                .onSuccess(json ->
                {
                    if (json.getInt(P.status) == 1) {
                        json = json.getJson(P.data);
                        Intent intent = new Intent(LoginScreen.this,OtpActivity.class);
                        intent.putExtra(Config.LOGIN_NUMBER,binding.etxNumber.getText().toString());
                        intent.putExtra(Config.LOGIN_OTP,json.getString(P.otp));
                        startActivity(intent);
                    } else {
                        H.showMessage(activity, json.getString(P.msg));
                    }
                    hideProgress();
                })
                .run("hitLoginUser");
    }

    private void showProgress() {
        loadingDialog.show("Please wait..");
    }

    private void hideProgress() {
        loadingDialog.hide();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
        }
        return false;
    }

}
