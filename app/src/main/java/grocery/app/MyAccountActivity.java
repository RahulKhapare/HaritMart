package grocery.app;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.databinding.DataBindingUtil;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.TextView;

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
                Intent orderDetailIntent = new Intent(activity,OrderDetailsActivity.class);
                startActivity(orderDetailIntent);
                break;
            case R.id.txtWallet:
                Intent walletIntent = new Intent(activity,MyWalletActivity.class);
                startActivity(walletIntent);
                break;
            case R.id.txtMyPayment:
                Intent paymentIntent = new Intent(activity,MyPaymentActivity.class);
                startActivity(paymentIntent);
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
                onLogOutClick();
                break;
        }
    }

    public void onLogOutClick() {
        Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.activity_log_out_dialog);
        dialog.findViewById(R.id.txtYes).setOnClickListener(v ->
        {
            dialog.cancel();
        });
        dialog.findViewById(R.id.txtNo).setOnClickListener(v -> {
            dialog.cancel();
        });
        dialog.setCancelable(false);
        dialog.show();
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