package grocery.app;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.os.Bundle;
import android.view.View;
import android.widget.Toolbar;

import grocery.app.databinding.ActivityMyAccountBinding;
import grocery.app.util.WindowBarColor;

public class MyAccountActivity extends AppCompatActivity {

    private ActivityMyAccountBinding binding;
    private MyAccountActivity activity = this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        WindowBarColor.setColor(this);
        binding = DataBindingUtil.setContentView(activity, R.layout.activity_my_account);
        binding.toolbar.setTitle("My Account");
        setSupportActionBar(binding.toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
    }

    public void onBackClick(View view) {
        finish();
    }
}