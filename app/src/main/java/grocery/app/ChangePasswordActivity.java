package grocery.app;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;

import com.adoisstudio.helper.Api;
import com.adoisstudio.helper.H;
import com.adoisstudio.helper.Json;
import com.adoisstudio.helper.LoadingDialog;
import com.adoisstudio.helper.Session;

import grocery.app.common.P;
import grocery.app.databinding.ActivityChangePasswordBinding;
import grocery.app.util.Config;
import grocery.app.util.WindowBarColor;

public class ChangePasswordActivity extends AppCompatActivity {

    private ChangePasswordActivity activity = this;
    private ActivityChangePasswordBinding binding;
    private LoadingDialog loadingDialog;
    private Session session;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        WindowBarColor.setColor(this);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_change_password);

        binding.toolbar.setTitle("Change Password");
        setSupportActionBar(binding.toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        initView();
    }

    private void initView(){

        session = new Session(activity);

        loadingDialog = new LoadingDialog(activity);

        binding.btnChangePass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkValidation()){
                    hitUpdatePasswordApi();
                }
            }
        });

    }

    private boolean checkValidation(){
        boolean value =  true;

        if (TextUtils.isEmpty(binding.etxNewPassword.getText().toString().trim())){
            H.showMessage(activity,"Please Enter New Password");
            value = false;
        }else if (TextUtils.isEmpty(binding.etxOldPassword.getText().toString().trim())){
            H.showMessage(activity,"Please Enter Old Password");
            value = false;
        }else if (TextUtils.isEmpty(binding.etxConfirmPassword.getText().toString().trim())){
            H.showMessage(activity,"Please Enter Confirm Password");
            value = false;
        }else if (!binding.etxNewPassword.getText().toString().trim().equals(binding.etxConfirmPassword.getText().toString().trim())){
            H.showMessage(activity,"Confirm password Not Matched With New Password");
            value = false;
        }else if (binding.etxNewPassword.getText().toString().trim().length()<6){
            H.showMessage(activity,"Enter Minimum 6 Digit Password");
            value = false;
        }else if (binding.etxConfirmPassword.getText().toString().trim().length()<6){
            H.showMessage(activity,"Enter Minimum 6 Digit Password");
            value = false;
        }

        return value;
    }

    private void hitUpdatePasswordApi() {
        showProgress();
        Json j = new Json();
        j.addInt(P.user_id, H.getInt(session.getString(P.user_id)));
        j.addString(P.old_password, binding.etxOldPassword.getText().toString().trim());
        j.addString(P.new_password, binding.etxNewPassword.getText().toString().trim());
        j.addString(P.cofirm_new_password, binding.etxConfirmPassword.getText().toString().trim());

        Api.newApi(activity, P.baseUrl + "change_profile_password").addJson(j)
                .setMethod(Api.POST)
                //.onHeaderRequest(App::getHeaders)
                .onError(() -> {
                    H.showMessage(activity, "On error is called");
                    hideProgress();
                })
                .onSuccess(json ->
                {
                    if (json.getInt(P.status) == 1) {

                        H.showMessage(activity, "Password Updated Successfully");

                        new Handler().postDelayed(() -> {
                            finish();
                        }, 1000);

                    } else {
                        H.showMessage(activity, json.getString(P.msg));
                    }
                    hideProgress();
                })
                .run("hitUpdatePasswordApi");
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

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

}
