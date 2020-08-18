package grocery.app;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import grocery.app.databinding.ActivityOtpBinding;
import grocery.app.util.WindowBarColor;

public class OtpActivity extends AppCompatActivity {
    ActivityOtpBinding activityOtpBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        WindowBarColor.setColor(this);
        activityOtpBinding= DataBindingUtil.setContentView(this, R.layout.activity_otp);
        Button button = findViewById(R.id.otpBtn);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(OtpActivity.this,SetLocationActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }
        });
    }

    public void onBackClick(View view) {
        finish();
    }
}