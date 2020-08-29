package grocery.app;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.MenuItem;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import com.adoisstudio.helper.H;

import grocery.app.databinding.ActivityLoginScreenBinding;
import grocery.app.util.Config;
import grocery.app.util.WindowBarColor;

public class LoginScreen extends AppCompatActivity {

    private ActivityLoginScreenBinding binding;
    private LoginScreen activity = this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        WindowBarColor.setColor(this);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_login_screen);

        binding.toolbar.setTitle("");
        setSupportActionBar(binding.toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        binding.btnProcess.setOnClickListener(view -> {
            checkValidation();
        });
    }

    public void checkValidation(){
        if (TextUtils.isEmpty(binding.etxNumber.getText().toString())){
            H.showMessage(activity,"Enter mobile number");
        }else if (binding.etxNumber.getText().toString().length()>10 || binding.etxNumber.getText().toString().length()<10){
            H.showMessage(activity,"Enter 10 digit mobile number");
        }else {
            Intent intent = new Intent(LoginScreen.this,OtpActivity.class);
            intent.putExtra(Config.LOGIN_NUMBER,binding.etxNumber.getText().toString());
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