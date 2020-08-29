package grocery.app;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import com.adoisstudio.helper.H;

import grocery.app.databinding.ActivityOtpBinding;
import grocery.app.util.Config;
import grocery.app.util.WindowBarColor;

public class OtpActivity extends AppCompatActivity {

    private ActivityOtpBinding binding;
    private OtpActivity activity = this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        WindowBarColor.setColor(this);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_otp);

        binding.toolbar.setTitle("");
        setSupportActionBar(binding.toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        String number = getIntent().getStringExtra(Config.LOGIN_NUMBER);
        binding.txtInfo.setText("Please enter the PIN sent to " + number);

        binding.btnProcess.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkValidation();
            }
        });
    }

    private void checkValidation(){
        if (TextUtils.isEmpty(binding.etxOtp.getText().toString())){
            H.showMessage(activity,"Enter any OTP");
        }else if (binding.etxOtp.getText().toString().length()<4){
            H.showMessage(activity,"Enter 4 digit OTP");
        }else {
            Intent intent = new Intent(OtpActivity.this,SetLocationActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
        }
        return false;
    }


}