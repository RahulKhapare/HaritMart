package grocery.app;

import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.MenuItem;
import android.view.View;
import android.widget.CompoundButton;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import com.adoisstudio.helper.Api;
import com.adoisstudio.helper.H;
import com.adoisstudio.helper.Json;
import com.adoisstudio.helper.LoadingDialog;
import com.adoisstudio.helper.Session;

import java.util.regex.Pattern;

import grocery.app.common.P;
import grocery.app.databinding.ActivityUpdateProfileBinding;
import grocery.app.util.Config;
import grocery.app.util.WindowBarColor;

public class UpdateProfileActivity extends AppCompatActivity {

    private ActivityUpdateProfileBinding binding;
    private UpdateProfileActivity activity = this;
    private LoadingDialog loadingDialog;
    private boolean sendEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        WindowBarColor.setColor(this);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_update_profile);

        binding.toolbar.setTitle("Update Profile");
        setSupportActionBar(binding.toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        initView();

    }

    private void initView(){

        sendEmail = false;
        loadingDialog = new LoadingDialog(activity);

        binding.btnProcess.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkValidation()){
                    hitSaveProfileApi();
                }
            }
        });

        onCheckEmail();
        setPreviousData();
    }


    private boolean checkValidation(){
        boolean value = true;
        if (TextUtils.isEmpty(binding.etxName.getText().toString().trim())){
            H.showMessage(activity,"Please Enter Name");
            value = false;
        }else if (TextUtils.isEmpty(binding.etxNumber.getText().toString().trim())){
            H.showMessage(activity,"Please Enter Mobile Number");
            value = false;
        }else if (binding.etxNumber.getText().toString().trim().length()>10 || binding.etxNumber.getText().toString().trim().length()<10){
            H.showMessage(activity,"Please Enter 10 Digit Mobile Number");
            value = false;
        }else if (TextUtils.isEmpty(binding.etxEmail.getText().toString().trim())){
            H.showMessage(activity,"Please Enter Email Id");
            value = false;
        }else if (!validEmail(binding.etxEmail.getText().toString().trim())){
            H.showMessage(activity,"Please Enter Valid Email Id");
            value = false;
        }
        return value;
    }

    private void onCheckEmail(){

        binding.checkEmail.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked){
                    sendEmail = true;
                }else {
                    sendEmail = false;
                }
            }
        });

    }

    private void setPreviousData(){
        Session session = new Session(activity);
        binding.etxName.setText(session.getString(P.user_name));
        binding.etxEmail.setText(session.getString(P.user_email));
        binding.etxNumber.setText(session.getString(P.user_number));
    }

    private void hitSaveProfileApi() {
        showProgress();
        Json j = new Json();
        j.addInt(P.user_id, Config.dummyID_1);
        j.addString(P.name, binding.etxName.getText().toString().trim());
        j.addString(P.email, binding.etxEmail.getText().toString().trim());
        j.addString(P.phone, binding.etxNumber.getText().toString().trim());

        Api.newApi(activity, P.baseUrl + "save_profile").addJson(j)
                .setMethod(Api.POST)
                //.onHeaderRequest(App::getHeaders)
                .onError(() -> {
                    H.showMessage(activity, "On error is called");
                    hideProgress();
                })
                .onSuccess(json ->
                {
                    if (json.getInt(P.status) == 1) {

                        H.showMessage(activity, "Profile Updated Successfully");
                        updateUserData();
                        new Handler().postDelayed(() -> {
                            finish();
                        }, 1000);

                    } else {
                        H.showMessage(activity, json.getString(P.msg));
                    }
                    hideProgress();
                })
                .run("hitSaveProfileApi");
    }

    private void updateUserData(){
        Session session = new Session(activity);
        session.addString(P.user_name, binding.etxName.getText().toString().trim());
        session.addString(P.user_email ,binding.etxEmail.getText().toString().trim());
        session.addString(P.user_number,binding.etxNumber.getText().toString().trim());
    }

    private boolean validEmail(String email) {
        Pattern pattern = Patterns.EMAIL_ADDRESS;
        return pattern.matcher(email).matches();
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

