package grocery.app;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import grocery.app.databinding.ActivityLoginScreenBinding;

public class LoginScreen extends AppCompatActivity {


    ActivityLoginScreenBinding activityLoginScreenBinding;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_screen);

        activityLoginScreenBinding =  DataBindingUtil.setContentView(this, R.layout.activity_otp);

        Button button = activityLoginScreenBinding.loginBtn;
        button.setOnClickListener(view -> {
            Intent i = new Intent(LoginScreen.this,OtpActivity.class);
            startActivity(i);
        });
    }
}