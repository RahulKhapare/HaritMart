package grocery.app;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.google.firebase.crashlytics.FirebaseCrashlytics;

import grocery.app.databinding.ActivityLoginScreenBinding;
import grocery.app.util.WindowBarColor;

public class LoginScreen extends AppCompatActivity {

    private ActivityLoginScreenBinding activityLoginScreenBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        WindowBarColor.setColor(this);
        activityLoginScreenBinding =  DataBindingUtil.setContentView(this, R.layout.activity_login_screen);

        activityLoginScreenBinding.loginBtn.setOnClickListener(view -> {
            Intent intent = new Intent(LoginScreen.this,OtpActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
        });
    }

    public void onBackClick(View view) {
        finish();
    }
}