package grocery.app;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;

import com.adoisstudio.helper.H;
import com.adoisstudio.helper.Json;
import com.adoisstudio.helper.LoadingDialog;
import com.adoisstudio.helper.Session;

import grocery.app.common.App;
import grocery.app.common.P;
import grocery.app.databinding.ActivityLoginMobileBinding;

public class LoginMobile extends AppCompatActivity {
    private ActivityLoginMobileBinding binding;
    private LoadingDialog loadingDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_login_mobile);
    }

    public void onBackClick(View view) {
        finish();
    }

    public void onLoginClick(View view) {
        makeJson();
    }
    private void makeJson() {
        Json json = new Json();
        json.addString(P.cart_token, new Session(this).getString(P.cart_token));

        String string = binding.editText.getText().toString().trim();
        string = string.replace("+91", "");
        string = string.replace(" ", "");
        if (string.isEmpty()) {
            H.showMessage(this, "Please enter mobile number");
            binding.editText.requestFocus();
            return;
        }

        long l =H.getLong(string);
        H.log("lIs", l + "");
        if (l > 0 && string.length() == 10)
            json.addString(P.phone, string);
        else if (l > 0) {
            H.showMessage(this, "Please enter valid phone number");
            binding.editText.requestFocus();
            return;
        } else if (!Patterns.EMAIL_ADDRESS.matcher(string).matches()) {
            H.showMessage(this, "Please enter valid email id");
            binding.editText.requestFocus();
            return;
        } else
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
           /* if (getIntent().getBooleanExtra("fromSkip", false)) {
                intent.putExtra("fromSkip", true);
                startActivityForResult(intent, 49);
            } else*/
                startActivity(intent);
        }, this, json);
    }}