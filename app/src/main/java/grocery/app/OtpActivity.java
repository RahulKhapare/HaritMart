package grocery.app;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import com.adoisstudio.helper.Api;
import com.adoisstudio.helper.H;
import com.adoisstudio.helper.Json;
import com.adoisstudio.helper.LoadingDialog;
import com.adoisstudio.helper.Session;

import grocery.app.common.P;
import grocery.app.databinding.ActivityOtpBinding;
import grocery.app.util.Click;
import grocery.app.util.Config;
import grocery.app.util.LoginFlag;
import grocery.app.util.WindowBarColor;

public class OtpActivity extends AppCompatActivity {

    private ActivityOtpBinding binding;
    private OtpActivity activity = this;
    private String loginNumber;
    private String loginOTP;
    private LoadingDialog loadingDialog;
    private CountDownTimer timer;
    private String resetText = "Resend OPT";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        WindowBarColor.setColor(this);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_otp);

        binding.toolbar.setTitle("");
        setSupportActionBar(binding.toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        loadingDialog = new LoadingDialog(this);

        loginNumber = getIntent().getStringExtra(Config.LOGIN_NUMBER);
        loginOTP = getIntent().getStringExtra(Config.LOGIN_OTP);

        binding.txtInfo.setText("Please enter the OPT sent to " + loginNumber);
        binding.etxOtp.setText(loginOTP);

        binding.btnProcess.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Click.preventTwoClick(v);
                checkValidation();
            }
        });

        binding.txtTimer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Click.preventTwoClick(v);
                if (binding.txtTimer.getText().toString().equals(resetText)){
                    showProgress();
                    new Handler().postDelayed(() -> {
                        hideProgress();
                        H.showMessage(activity,"OPT send successfully");
                        binding.etxOtp.setText(loginOTP);
                        startTimer();
                    }, 1230);
                }
            }
        });

        startTimer();

    }

    private void startTimer(){
        timer = new CountDownTimer(30000, 1000) {

            public void onTick(long millisUntilFinished) {
                binding.txtTimer.setText("00 : " + millisUntilFinished / 1000);
            }
            public void onFinish() {
                binding.txtTimer.setText(resetText);
                binding.etxOtp.setText("");
            }
        }.start();
    }

    private void closeTimer(){
        try {
            if (timer!=null){
                timer.cancel();
            }
        }catch (Exception e){
        }
    }

    private void checkValidation() {
        if (TextUtils.isEmpty(binding.etxOtp.getText().toString())) {
            H.showMessage(activity, "Enter any OTP");
        } else if (binding.etxOtp.getText().toString().length() < 6) {
            H.showMessage(activity, "Enter 6 digit OTP");
        } else {
            hitVerifyOPT();
        }
    }

    private void hitVerifyOPT() {
        showProgress();

        Json j = new Json();
        j.addString(P.phone, loginNumber);
        j.addString(P.otp, loginOTP);

        Api.newApi(activity, P.baseUrl + "verify_otp").addJson(j)
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
                        hitLoginWithOpt();
                    } else {
                        H.showMessage(activity, json.getString(P.msg));
                    }
                    hideProgress();
                })
                .run("hitVerifyOPT");
    }

    private void hitLoginWithOpt() {
        showProgress();

        Json j = new Json();
        j.addString(P.phone, loginNumber);
        j.addString(P.cart_token, new Session(activity).getString(P.cart_token));
        j.addString(P.otp, loginOTP);

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

                        Json userJson = json.getJson(P.user_data);
                        Session session = new Session(activity);
                        session.addString(P.login_token,json.getString(P.login_token));
                        session.addString(P.user_id,userJson.getString(P.id));
                        session.addString(P.app_type,userJson.getString(P.app_type));
                        session.addString(P.user_name,userJson.getString(P.name));
                        session.addString(P.user_email,userJson.getString(P.email));
                        session.addString(P.user_number,userJson.getString(P.phone));
                        session.addString(P.wallet,userJson.getString(P.wallet));
                        session.addString(P.referral_code,userJson.getString(P.referral_code));
                        session.addString(P.profile_image,userJson.getString(P.profile_image));

                        closeTimer();
                        new Session(activity).addBool(P.isUserLogin,true);
                        Intent intent = new Intent(OtpActivity.this, SetLocationActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);

                    } else {
                        H.showMessage(activity, json.getString(P.msg));
                    }
                    hideProgress();
                })
                .run("hitLoginUser");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        closeTimer();
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