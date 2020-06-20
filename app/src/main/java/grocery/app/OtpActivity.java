package grocery.app;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.os.Bundle;

import grocery.app.databinding.ActivityOtpBinding;

public class OtpActivity extends AppCompatActivity {
    ActivityOtpBinding activityOtpBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityOtpBinding= DataBindingUtil.setContentView(this, R.layout.activity_otp);
    }
}