package grocery.app;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.databinding.DataBindingUtil;

import com.adoisstudio.helper.Api;
import com.adoisstudio.helper.H;
import com.adoisstudio.helper.Json;
import com.adoisstudio.helper.LoadingDialog;
import com.adoisstudio.helper.Session;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;

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
    private static final int REQUEST_GALLARY = 9;
    private static final int REQUEST_CAMERA = 10;
    private static final int READ_WRIRE = 11;
    private Uri cameraURI;
    private int click;
    private int cameraClick = 0;
    private int galleryClick = 1;
    private String base64Image = "";

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

        loadingDialog = new LoadingDialog(activity);

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

    private void setProfileData(){
        Session session = new Session(activity);
        if(!TextUtils.isEmpty(session.getString(P.user_name))){
            binding.txtName.setText(session.getString(P.user_name));
        }
        if (!TextUtils.isEmpty(session.getString(P.user_email))){
            binding.txtEmail.setText(session.getString(P.user_email));
        }
        if (!TextUtils.isEmpty(session.getString(P.user_number))){
            binding.txtNumber.setText(session.getString(P.user_number));
        }
    }

    @Override
    public void onClick(View v) {
        Click.preventTwoClick(v);
        switch (v.getId()) {
            case R.id.lnrImage:
                onUploadClick();
                break;
            case R.id.imgEdit:
                Intent updateIntent = new Intent(activity,UpdateProfileActivity.class);
                startActivity(updateIntent);
                break;
            case R.id.txtChange:
                Config.FROM_ADDRESS = false;
                Intent locationIntent = new Intent(activity,SetLocationActivity.class);
                locationIntent.putExtra(Config.GET_CURRENT_LOCATION,true);
                startActivity(locationIntent);
                finish();
                break;
            case R.id.lnrMyOrder:
                Intent orderDetailIntent = new Intent(activity,OrderDetailListActivity.class);
                startActivity(orderDetailIntent);
                break;
            case R.id.lnrWallet:
                Intent walletIntent = new Intent(activity,MyWalletActivity.class);
                startActivity(walletIntent);
                break;
            case R.id.lnrMyPayment:
                Intent paymentIntent = new Intent(activity,MyPaymentActivity.class);
                startActivity(paymentIntent);
                break;
            case R.id.txtRatingReview:
                //not using now
                break;
            case R.id.lnrNotifications:
                Intent notificationIntent = new Intent(activity,NotificationActivity.class);
                startActivity(notificationIntent);
                break;
            case R.id.lnrGiftCard:
                Intent giftIntent = new Intent(activity,GiftCardActivity.class);
                startActivity(giftIntent);
                break;
            case R.id.lnrMyAddress:
                Intent addressIntent = new Intent(activity,MyAddressActivity.class);
                startActivity(addressIntent);
                break;
            case R.id.lnrChangePass:
                Intent changePassIntent = new Intent(activity,ChangePasswordActivity.class);
                startActivity(changePassIntent);
                break;
            case R.id.lnrTermCondition:
                Intent termIntent = new Intent(activity,TearmAndConditionActivity.class);
                startActivity(termIntent);
                break;
            case R.id.lnrLogOut:
                onLogOutClick();
                break;
        }
    }

    private void onUploadClick() {
        Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.activity_upload_dialog);
        dialog.findViewById(R.id.txtCamera).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Click.preventTwoClick(v);
                dialog.cancel();
                click = cameraClick;
                getPermission();
            }
        });
        dialog.findViewById(R.id.txtGallary).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Click.preventTwoClick(v);
                dialog.cancel();
                click = galleryClick;
                getPermission();
            }
        });
        dialog.setCancelable(true);
        dialog.show();
    }

    private void getPermission() {
        ActivityCompat.requestPermissions(activity,
                new String[]{Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE},
                READ_WRIRE);
    }

    private void jumpToSetting() {
        H.showMessage(activity, "Please allow permission from setting.");
        try {
            Intent intent = new Intent();
            intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
            Uri uri = Uri.fromParts("package", activity.getPackageName(), null);
            intent.setData(uri);
            activity.startActivity(intent);
        } catch (Exception e) {
        }
    }

    private void openCamera() {
        try {
            StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
            StrictMode.setVmPolicy(builder.build());
            String fileName = String.format("%d.jpg", System.currentTimeMillis());
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            File file = new File(Environment.getExternalStorageDirectory(),
                    fileName);
            cameraURI = Uri.fromFile(file);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, cameraURI);
            startActivityForResult(intent, REQUEST_CAMERA);
        } catch (Exception e) {
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case READ_WRIRE: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (click == cameraClick) {
                        openCamera();
                    } else if (click == galleryClick) {
                        openGallery();
                    }
                } else if (grantResults[0] == PackageManager.PERMISSION_DENIED) {
                    jumpToSetting();
                } else {
                    getPermission();
                }
                return;
            }
        }
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case REQUEST_GALLARY:
                if (resultCode == Activity.RESULT_OK && data != null) {
                    try {
                        Uri selectedImage = data.getData();
                        setImageData(selectedImage);
                    } catch (Exception e) {
                    }
                }
                break;
            case REQUEST_CAMERA:
                if (resultCode == Activity.RESULT_OK) {
                    try {
                        setImageData(cameraURI);
                    } catch (Exception e) {
                    }
                }
                break;
        }
    }


    private void setImageData(Uri uri) {
        base64Image = "";
        try {
            InputStream imageStream = getContentResolver().openInputStream(uri);
            Bitmap selectedImage = BitmapFactory.decodeStream(imageStream);
            binding.imgUser.setImageBitmap(selectedImage);
            base64Image = encodeImage(selectedImage);
//            hitSaveProfileImageApi(base64Image);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            Log.e("TAG", "setImageDataEE: "+ e.getMessage() );
            H.showMessage(activity, "Unable to get image, try again.");
        }
    }

    private String encodeImage(Bitmap bm) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bm.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] b = baos.toByteArray();
        String encImage = Base64.encodeToString(b, Base64.DEFAULT);
        return encImage;
    }

    private void openGallery() {
        try {
            Intent i = new Intent(
                    Intent.ACTION_PICK,
                    android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(i, REQUEST_GALLARY);
        } catch (Exception e) {
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

    private void hitSaveProfileImageApi(String imagePath) {
        showProgress();

        Json j = new Json();
        j.addInt(P.user_id, Config.dummyID_1);
        j.addString("", imagePath);

        Api.newApi(activity, P.baseUrl + "").addJson(j)
                .setMethod(Api.POST)
                //.onHeaderRequest(App::getHeaders)
                .onError(() -> {
                    H.showMessage(activity, "On error is called");
                    hideProgress();
                })
                .onSuccess(json ->
                {
                    if (json.getInt(P.status) == 1) {

                        H.showMessage(activity, "Profile Image Updated Successfully");

                    } else {
                        H.showMessage(activity, json.getString(P.msg));
                    }
                    hideProgress();
                })
                .run("hitSaveProfileImageApi");
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

