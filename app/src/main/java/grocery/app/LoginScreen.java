package grocery.app;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import com.adoisstudio.helper.H;
import com.adoisstudio.helper.Json;
import com.adoisstudio.helper.LoadingDialog;
import com.adoisstudio.helper.Session;

import grocery.app.common.App;
import grocery.app.common.P;
import grocery.app.databinding.ActivityLoginScreenBinding;
import grocery.app.util.WindowBarColor;

public class LoginScreen extends AppCompatActivity {

    private ActivityLoginScreenBinding binding;
    private LoginScreen activity = this;
    LoadingDialog loadingDialog;

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
        binding.btnProcess.setOnClickListener(view -> {
           // checkValidation();
            makeJson();
        });
    }

    private void makeJson() {
        Json json = new Json();
        json.addString(P.cart_token, new Session(this).getString(P.cart_token));

        String string = binding.etxNumber.getText().toString().trim();
        string = string.replace("+91", "");
        string = string.replace(" ", "");
        if (string.isEmpty()) {
            H.showMessage(this, "Please enter mobile number");
            binding.etxNumber.requestFocus();
            return;
        }

        long l =H.getLong(string);
        H.log("lIs", l + "");
        if (l > 0 && string.length() == 10)
            json.addString(P.phone, string);
        else if (l > 0) {
            H.showMessage(this, "Please enter valid phone number");
            binding.etxNumber.requestFocus();
            return;
        } /*else if (!Patterns.EMAIL_ADDRESS.matcher(string).matches()) {
            H.showMessage(this, "Please enter valid email id");
            binding.etxNumber.requestFocus();
            return;
        }*/ else
            json.addString(P.phone, string);

        json.addString(P.otp, "");

        hitLogInWithOtpApi(json);
    }

    private void hitLogInWithOtpApi(Json json) {
        loadingDialog.show();
        App.app.hitLogInWithOtpApi(json1 ->
        {
            json.addString(P.otp, json1.getString(P.otp));
            if (!isDestroyed())
                loadingDialog.hide();
            Intent intent = new Intent(this, OtpActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            intent.putExtra(P.json, json.toString());
            if (getIntent().getBooleanExtra("fromSkip", false)) {
                intent.putExtra("fromSkip", true);
                startActivityForResult(intent, 49);
            } else
            startActivity(intent);
        }, this, json);
    }

    /*public void checkValidation(){
        if (TextUtils.isEmpty(binding.etxNumber.getText().toString())){
            H.showMessage(activity,"Enter mobile number");
        }else if (binding.etxNumber.getText().toString().length()>10 || binding.etxNumber.getText().toString().length()<10){
            H.showMessage(activity,"Enter 10 digit mobile number");
        }else {
            Intent intent = new Intent(LoginScreen.this,OtpActivity.class);
            intent.putExtra(Config.LOGIN_NUMBER,binding.etxNumber.getText().toString());
            startActivity(intent);
        }
    }*/

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
        }
        return false;
    }



}