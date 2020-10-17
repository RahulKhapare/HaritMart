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

import com.adoisstudio.helper.LoadingDialog;
import com.adoisstudio.helper.Session;
import com.squareup.picasso.Picasso;

import grocery.app.common.P;
import grocery.app.databinding.ActivityMyAccountBinding;
import grocery.app.util.Click;
import grocery.app.util.Config;
import grocery.app.util.WindowBarColor;

public class MyAccountActivity extends AppCompatActivity implements View.OnClickListener {

    private ActivityMyAccountBinding binding;
    private MyAccountActivity activity = this;
    private String currentAddress;
    private LoadingDialog loadingDialog;
    private Session session;

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

    private void initView() {

        loadingDialog = new LoadingDialog(activity);
        session = new Session(activity);
        binding.txtTermAndCondition.setText("Term & Conditions");
        binding.txtChange.setOnClickListener(this);
        binding.lnrMyOrder.setOnClickListener(this);
        binding.lnrWallet.setOnClickListener(this);
        binding.lnrMyPayment.setOnClickListener(this);
        binding.txtRatingReview.setOnClickListener(this);
        binding.lnrNotifications.setOnClickListener(this);
        binding.lnrGiftCard.setOnClickListener(this);
        binding.lnrMyAddress.setOnClickListener(this);
        binding.lnrLogOut.setOnClickListener(this);
        binding.imgEdit.setOnClickListener(this);
        binding.lnrImage.setOnClickListener(this);
        binding.lnrChangePass.setOnClickListener(this);
        binding.lnrTermCondition.setOnClickListener(this);

        binding.txtLocation.setText(new Session(activity).getString(P.googleAddress));

    }

    @Override
    protected void onResume() {
        super.onResume();
        currentAddress = getIntent().getStringExtra(Config.ADDRESS_LOCATION);
        setProfileData();
    }

    private void setProfileData() {
        Session session = new Session(activity);

        setUserImage(session.getString(P.profile_image));

        if (!TextUtils.isEmpty(session.getString(P.user_name)) && !session.getString(P.user_name).equals("null")) {
            binding.txtName.setText(session.getString(P.user_name));
        }
        if (!TextUtils.isEmpty(session.getString(P.user_email)) && !session.getString(P.user_email).equals("null")) {
            binding.txtEmail.setText(session.getString(P.user_email));
        }
        if (!TextUtils.isEmpty(session.getString(P.user_number)) && !session.getString(P.user_number).equals("null")) {
            binding.txtNumber.setText(session.getString(P.user_number));
        }
    }

    @Override
    public void onClick(View v) {
        Click.preventTwoClick(v);
        switch (v.getId()) {
            case R.id.imgEdit:
                Intent updateIntent = new Intent(activity, UpdateProfileActivity.class);
                startActivity(updateIntent);
                break;
            case R.id.txtChange:
                Config.FROM_ADDRESS = false;
                Intent locationIntent = new Intent(activity, SetLocationActivity.class);
                locationIntent.putExtra(Config.GET_CURRENT_LOCATION, true);
                startActivity(locationIntent);
                finish();
                break;
            case R.id.lnrMyOrder:
                Intent orderDetailIntent = new Intent(activity, OrderDetailListActivity.class);
                startActivity(orderDetailIntent);
                break;
            case R.id.lnrWallet:
                Intent walletIntent = new Intent(activity, MyWalletActivity.class);
                startActivity(walletIntent);
                break;
            case R.id.lnrMyPayment:
                Intent paymentIntent = new Intent(activity, MyPaymentActivity.class);
                startActivity(paymentIntent);
                break;
            case R.id.txtRatingReview:
                //not using now
                break;
            case R.id.lnrNotifications:
                Intent notificationIntent = new Intent(activity, NotificationActivity.class);
                startActivity(notificationIntent);
                break;
            case R.id.lnrGiftCard:
                Intent giftIntent = new Intent(activity, GiftCardActivity.class);
                startActivity(giftIntent);
                break;
            case R.id.lnrMyAddress:
                Intent addressIntent = new Intent(activity, MyAddressActivity.class);
                startActivity(addressIntent);
                break;
            case R.id.lnrChangePass:
                Intent changePassIntent = new Intent(activity, ChangePasswordActivity.class);
                startActivity(changePassIntent);
                break;
            case R.id.lnrTermCondition:
                Intent termIntent = new Intent(activity, TearmAndConditionActivity.class);
                startActivity(termIntent);
                break;
            case R.id.lnrLogOut:
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
            new Session(this).clear();
            Intent intent = new Intent(activity, OnboardingActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        });
        dialog.findViewById(R.id.txtNo).setOnClickListener(v -> {
            dialog.cancel();
        });
        dialog.setCancelable(false);
        dialog.show();
    }

    private void setUserImage(String imagePath) {
        if (!TextUtils.isEmpty(imagePath)) {
            Picasso.get().load(imagePath).placeholder(R.drawable.progress_animation).error(R.drawable.ic_baseline_account_circle_24).into(binding.imgUser);
        }
    }

    private void showProgress() {
        loadingDialog.show("Please wait..");
    }

    private void hideProgress() {
        loadingDialog.hide();
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

