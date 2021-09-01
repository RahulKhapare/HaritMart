package grocery.app;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import com.adoisstudio.helper.Api;
import com.adoisstudio.helper.H;
import com.adoisstudio.helper.Json;
import com.adoisstudio.helper.LoadingDialog;
import com.adoisstudio.helper.Session;

import grocery.app.common.P;
import grocery.app.databinding.ActivityCreateAccountBinding;
import grocery.app.databinding.ActivityLoginScreenBinding;
import grocery.app.util.Click;
import grocery.app.util.Config;
import grocery.app.util.Validation;
import grocery.app.util.WindowBarColor;

public class CreateAccountAccount extends AppCompatActivity {

    private ActivityCreateAccountBinding binding;
    private CreateAccountAccount activity = this;
    private LoadingDialog loadingDialog;
    boolean isPasswordVisible1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        WindowBarColor.setColor(this);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_create_account);

        binding.toolbar.setTitle("");
        setSupportActionBar(binding.toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        loadingDialog = new LoadingDialog(this);
        binding.btnProcess.setOnClickListener(v -> {
            Click.preventTwoClick(v);
            checkValidation();
        });

        binding.imgPass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(binding.etxPassword.getTransformationMethod().equals(PasswordTransformationMethod.getInstance())){
                    ((ImageView)(v)).setImageResource(R.drawable.ic_baseline_on_eye_24);
                    //Show Password
                    binding.etxPassword.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                }
                else{
                    ((ImageView)(v)).setImageResource(R.drawable.ic_baseline_off_eye_24);
                    //Hide Password
                    binding.etxPassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
                }
                binding.etxPassword.setSelection(binding.etxPassword.getText().toString().length());
            }
        });

        binding.imgConPass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(binding.etxConPassword.getTransformationMethod().equals(PasswordTransformationMethod.getInstance())){
                    ((ImageView)(v)).setImageResource(R.drawable.ic_baseline_on_eye_24);
                    //Show Password
                    binding.etxConPassword.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                }
                else{
                    ((ImageView)(v)).setImageResource(R.drawable.ic_baseline_off_eye_24);
                    //Hide Password
                    binding.etxConPassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
                }
                binding.etxConPassword.setSelection(binding.etxConPassword.getText().toString().length());
            }
        });

    }

    public void checkValidation(){
        if (TextUtils.isEmpty(binding.etxName.getText().toString().trim())){
            H.showMessage(activity,"Enter full name");
        }else if (TextUtils.isEmpty(binding.etxEmail.getText().toString().trim())){
            H.showMessage(activity,"Enter email id");
        }else if (!Validation.validEmail(binding.etxEmail.getText().toString().trim())){
            H.showMessage(activity,"Enter valid email id");
        }else if (TextUtils.isEmpty(binding.etxNumber.getText().toString())){
            H.showMessage(activity,"Enter mobile number");
        }else if (binding.etxNumber.getText().toString().length()>10 || binding.etxNumber.getText().toString().length()<10){
            H.showMessage(activity,"Enter 10 digit mobile number");
        }else if (TextUtils.isEmpty(binding.etxPassword.getText().toString().trim())){
            H.showMessage(activity,"Enter password");
        }else if (binding.etxPassword.getText().toString().length()<6){
            H.showMessage(activity,"Enter minimum 6 digit password");
        }else if (TextUtils.isEmpty(binding.etxConPassword.getText().toString().trim())){
            H.showMessage(activity,"Enter confirm password");
        }else if (!binding.etxConPassword.getText().toString().trim().equals(binding.etxPassword.getText().toString().trim())){
            H.showMessage(activity,"Confirm password not matched");
        }else {
//            hitLoginWithOpt();
            Config.SIGN_UP_NUMBER = binding.etxNumber.getText().toString().trim();
            Intent intent = new Intent(activity, LoginScreen.class);
            startActivity(intent);
        }
    }

    private void hitLoginWithOpt() {
        showProgress();

        Json j = new Json();
        j.addString(P.phone, binding.etxNumber.getText().toString().trim());
        j.addString(P.cart_token, new Session(activity).getString(P.cart_token));
        j.addString(P.otp, "");
        j.addString(P.fcm_value, new Session(activity).getString(P.fcm_value));

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
                        Intent intent = new Intent(CreateAccountAccount.this,OtpActivity.class);
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
