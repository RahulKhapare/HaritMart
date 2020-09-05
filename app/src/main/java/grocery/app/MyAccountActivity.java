package grocery.app;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;

import com.adoisstudio.helper.LoadingDialog;

import grocery.app.databinding.ActivityMyAccountBinding;
import grocery.app.databinding.ActivityMyAddressBinding;
import grocery.app.util.WindowBarColor;

public class MyAccountActivity extends AppCompatActivity implements View.OnClickListener {

    private ActivityMyAccountBinding binding;
    private MyAccountActivity activity = this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        WindowBarColor.setColor(this);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_my_account);

        binding.toolbar.setTitle("My Account");
        setSupportActionBar(binding.toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        binding.txtChange.setOnClickListener(this);
        binding.txtMyOrder.setOnClickListener(this);
        binding.txtWallet.setOnClickListener(this);
        binding.txtMyPayment.setOnClickListener(this);
        binding.txtRatingReview.setOnClickListener(this);
        binding.txtNotification.setOnClickListener(this);
        binding.txtGiftCard.setOnClickListener(this);
        binding.txtDelieveryAddress.setOnClickListener(this);
        binding.txtLogOut.setOnClickListener(this);
        binding.imgEdit.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.imgEdit:
                break;
            case R.id.txtChange:
                break;
            case R.id.txtMyOrder:
                break;
            case R.id.txtWallet:
                break;
            case R.id.txtMyPayment:
                break;
            case R.id.txtRatingReview:
                break;
            case R.id.txtNotification:
                break;
            case R.id.txtGiftCard:
                break;
            case R.id.txtDelieveryAddress:
                Intent addressIntent = new Intent(activity,MyAddressActivity.class);
                startActivity(addressIntent);
                break;
            case R.id.txtLogOut:
                break;
        }
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