package grocery.app;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import com.adoisstudio.helper.H;

import grocery.app.databinding.ActivityMyAccountBinding;
import grocery.app.util.Config;
import grocery.app.util.WindowBarColor;

public class MyAccountActivity extends AppCompatActivity implements View.OnClickListener {

    private ActivityMyAccountBinding binding;
    private MyAccountActivity activity = this;
    private String currentAddress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        WindowBarColor.setColor(this);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_my_account);

        binding.toolbar.setTitle("My Account");
        setSupportActionBar(binding.toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        initView();

    }

    private void initView(){
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
    protected void onResume() {
        super.onResume();
        currentAddress = getIntent().getStringExtra(Config.ADDRESS_LOCATION);
        if (!TextUtils.isEmpty(currentAddress)){
            H.showMessage(activity,currentAddress);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.imgEdit:
                Intent updateIntent = new Intent(activity,UpdateProfileActivity.class);
                startActivity(updateIntent);
                break;
            case R.id.txtChange:
                Config.FROM_ADDRESS = false;
                Intent locationIntent = new Intent(activity,SetLocationActivity.class);
                locationIntent.putExtra(Config.GET_CURRENT_LOCATION,true);
                startActivity(locationIntent);
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
                //not using now
                break;
            case R.id.txtNotification:
                Intent notificationIntent = new Intent(activity,NotificationActivity.class);
                startActivity(notificationIntent);
                break;
            case R.id.txtGiftCard:
                Intent giftIntent = new Intent(activity,GiftCardActivity.class);
                startActivity(giftIntent);
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