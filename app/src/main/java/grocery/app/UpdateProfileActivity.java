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
import android.os.Handler;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.util.Patterns;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.CompoundButton;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.databinding.DataBindingUtil;

import com.adoisstudio.helper.Api;
import com.adoisstudio.helper.H;
import com.adoisstudio.helper.Json;
import com.adoisstudio.helper.LoadingDialog;
import com.adoisstudio.helper.Session;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.regex.Pattern;

import grocery.app.common.P;
import grocery.app.databinding.ActivityUpdateProfileBinding;
import grocery.app.util.Click;
import grocery.app.util.Config;
import grocery.app.util.WindowBarColor;

public class UpdateProfileActivity extends AppCompatActivity {

    private ActivityUpdateProfileBinding binding;
    private UpdateProfileActivity activity = this;
    private LoadingDialog loadingDialog;
    private boolean sendEmail;
    private Session session;
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
        binding = DataBindingUtil.setContentView(this, R.layout.activity_update_profile);

        binding.toolbar.setTitle("Update Profile");
        setSupportActionBar(binding.toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        initView();

    }

    private void initView(){
        session = new Session(activity);
        sendEmail = false;
        loadingDialog = new LoadingDialog(activity);

        binding.checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked){

                }else {

                }
            }
        });

        binding.btnProcess.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Click.preventTwoClick(v);
                if (checkValidation()){
                    hitSaveProfileApi();
                }
            }
        });

        binding.lnrImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Click.preventTwoClick(v);
                onUploadClick();
            }
        });

        onCheckEmail();
        setPreviousData();
    }


    private boolean checkValidation(){
        boolean value = true;
        if (TextUtils.isEmpty(binding.etxName.getText().toString().trim())){
            H.showMessage(activity,"Please Enter Name");
            value = false;
        }else if (TextUtils.isEmpty(binding.etxNumber.getText().toString().trim())){
            H.showMessage(activity,"Please Enter Mobile Number");
            value = false;
        }else if (binding.etxNumber.getText().toString().trim().length()>10 || binding.etxNumber.getText().toString().trim().length()<10){
            H.showMessage(activity,"Please Enter 10 Digit Mobile Number");
            value = false;
        }else if (TextUtils.isEmpty(binding.etxEmail.getText().toString().trim())){
            H.showMessage(activity,"Please Enter Email Id");
            value = false;
        }else if (!validEmail(binding.etxEmail.getText().toString().trim())){
            H.showMessage(activity,"Please Enter Valid Email Id");
            value = false;
        }
        return value;
    }

    private void onCheckEmail(){

        binding.checkEmail.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked){
                    sendEmail = true;
                }else {
                    sendEmail = false;
                }
            }
        });

    }

    private void setPreviousData(){
        Session session = new Session(activity);

        setUserImage(session.getString(P.profile_image));

        if (!TextUtils.isEmpty(session.getString(P.user_name)) && !session.getString(P.user_name).equals("null")){
            binding.etxName.setText(session.getString(P.user_name));
        }
        if (!TextUtils.isEmpty(session.getString(P.user_email)) && !session.getString(P.user_email).equals("null")){
            binding.etxEmail.setText(session.getString(P.user_email));
        }
        if (!TextUtils.isEmpty(session.getString(P.user_number)) && !session.getString(P.user_number).equals("null")){
            binding.etxNumber.setText(session.getString(P.user_number));
        }
    }

    private void setUserImage(String imagePath){
        if (!TextUtils.isEmpty(imagePath)){
            Picasso.get().load(imagePath).placeholder(R.drawable.progress_animation).error(R.drawable.ic_baseline_account_circle_24).into(binding.imgUser);
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
            base64Image = encodeImage(selectedImage);
            hitSaveProfileImageApi(base64Image);
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

    private void hitSaveProfileImageApi(String imagePath) {
        showProgress();
        Json j = new Json();
        j.addInt(P.user_id,H.getInt(session.getString(P.user_id)));
        j.addString(P.image, imagePath);
        j.addString(P.extension, "jpg");
        j.addString(P.image_for, "profile");

        Api.newApi(activity, P.baseUrl + "upload_image").addJson(j)
                .setMethod(Api.POST)
                //.onHeaderRequest(App::getHeaders)
                .onError(() -> {
                    H.showMessage(activity, "On error is called");
                    hideProgress();
                })
                .onSuccess(json ->
                {
                    if (json.getInt(P.status) == 1) {
                        json = json.getJson(P.data);
                        session.addString(P.profile_image,json.getString(P.image_url));
                        setUserImage(json.getString(P.image_url));
                        H.showMessage(activity, "Profile Image Updated Successfully");
                    } else {
                        H.showMessage(activity, json.getString(P.msg));
                    }
                    hideProgress();
                })
                .run("hitSaveProfileImageApi");
    }

    private void hitSaveProfileApi() {
        showProgress();
        Json j = new Json();
        j.addInt(P.user_id,H.getInt(session.getString(P.user_id)));
        j.addString(P.name, binding.etxName.getText().toString().trim());
        j.addString(P.email, binding.etxEmail.getText().toString().trim());
        j.addString(P.phone, binding.etxNumber.getText().toString().trim());
        j.addString(P.profile_image, session.getString(P.profile_image));
        j.addInt(P.subscribe_newsletter, 0);

        Api.newApi(activity, P.baseUrl + "save_profile").addJson(j)
                .setMethod(Api.POST)
                //.onHeaderRequest(App::getHeaders)
                .onError(() -> {
                    H.showMessage(activity, "On error is called");
                    hideProgress();
                })
                .onSuccess(json ->
                {
                    if (json.getInt(P.status) == 1) {

                        H.showMessage(activity, "Profile Updated Successfully");
                        updateUserData();
                        new Handler().postDelayed(() -> {
                            finish();
                        }, 1000);

                    } else {
                        H.showMessage(activity, json.getString(P.msg));
                    }
                    hideProgress();
                })
                .run("hitSaveProfileApi");
    }

    private void updateUserData(){
        Session session = new Session(activity);
        session.addString(P.user_name, binding.etxName.getText().toString().trim());
        session.addString(P.user_email ,binding.etxEmail.getText().toString().trim());
        session.addString(P.user_number,binding.etxNumber.getText().toString().trim());
    }

    private boolean validEmail(String email) {
        Pattern pattern = Patterns.EMAIL_ADDRESS;
        return pattern.matcher(email).matches();
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

