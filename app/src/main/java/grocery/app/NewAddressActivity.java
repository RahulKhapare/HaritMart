package grocery.app;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.MenuItem;
import android.view.View;
import android.widget.CompoundButton;

import com.adoisstudio.helper.Api;
import com.adoisstudio.helper.H;
import com.adoisstudio.helper.Json;
import com.adoisstudio.helper.LoadingDialog;
import com.adoisstudio.helper.Session;

import java.util.regex.Pattern;

import grocery.app.Fragments.CartFragment;
import grocery.app.common.P;
import grocery.app.databinding.ActivityNewAddressBinding;
import grocery.app.model.AddressModel;
import grocery.app.util.Config;
import grocery.app.util.WindowBarColor;

public class NewAddressActivity extends AppCompatActivity implements View.OnClickListener {

    private NewAddressActivity activity = this;
    private ActivityNewAddressBinding binding;
    private String addressName;
    private boolean ediAddress;
    private boolean forCheckOut;
    private String home = "Home";
    private String office = "Office";
    private String placeOrder = "Place Order";
    private boolean GOOGLE_ADDRESS;
    private LoadingDialog loadingDialog;
    private Session session;
    private int addressID = 0;
    private int mainAddress = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        WindowBarColor.setColor(this);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_new_address);

        binding.toolbar.setTitle("Add New Address");
        setSupportActionBar(binding.toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        initView();

    }

    private void initView(){

        session = new Session(activity);
        loadingDialog = new LoadingDialog(activity);
        Config.IS_DELIVER_ADDRESS = false;
        ediAddress = getIntent().getBooleanExtra(Config.EDIT_ADDRESS,false);
        forCheckOut = getIntent().getBooleanExtra(Config.FOR_CHECKOUT,false);
        GOOGLE_ADDRESS = getIntent().getBooleanExtra(Config.GOOGLE_ADDRESS,false);

        binding.btnProcess.setOnClickListener(this);
        binding.txtHomeAddress.setOnClickListener(this);
        binding.txtOfficeAddress.setOnClickListener(this);

        binding.checkBoxDefault.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked){
                    Config.IS_DELIVER_ADDRESS = true;
                }else {
                    Config.IS_DELIVER_ADDRESS = false;
                }
            }
        });

        if (ediAddress){
            setPreviousAddress();
        }else if (forCheckOut){
            setPreviousAddress();
        }

        binding.etxCountry.setText("India");
        binding.etxCountry.setFocusable(false);

        if (forCheckOut){
            binding.btnProcess.setText(placeOrder);
            binding.checkBoxDefault.setVisibility(View.VISIBLE);
        }

    }

    private void setPreviousAddress(){

        binding.toolbar.setTitle("Edit Address");
        AddressModel model = Config.addressModel;
        binding.etxName.setText(model.getFull_name());
        binding.etxNumber.setText(model.getPhone());
        binding.etxOtherNumber.setText(model.getPhone2());
        binding.etxEmail.setText(model.getEmail());
        binding.etxStreet.setText(model.getAddress());
        binding.etxLandMark.setText(model.getLandmark());
        binding.etxCountry.setText(model.getCountry());
        binding.etxState.setText(model.getState());
        binding.etxcity.setText(model.getCity());
        binding.etxPincode.setText(model.getPincode());

        if (model.getAddress_type().equals(home)){
            updateHomeAddress();
        }else if (model.getAddress_type().equals(office)){
            updateOfficeAddress();
        }

        if (!TextUtils.isEmpty(model.getId())){
            try {
                addressID = Integer.parseInt(model.getId());
            }catch (Exception e){
            }
        }

        if (!TextUtils.isEmpty(model.getMain_address())){
            try {
                mainAddress = Integer.parseInt(model.getMain_address());
            }catch (Exception e){
            }
        }
    }

    private boolean checkvalitation(){
        boolean value = true;

        if (TextUtils.isEmpty(binding.etxName.getText().toString().trim())){
            H.showMessage(activity,"Enter Name");
            value = false;
        }else if (TextUtils.isEmpty(binding.etxNumber.getText().toString().trim())){
            H.showMessage(activity,"Enter Contact Number");
            value = false;
        }else if (binding.etxNumber.getText().toString().trim().length()>10 || binding.etxNumber.getText().toString().trim().length()<10){
            H.showMessage(activity,"Enter 10 Digit Contact Number");
            value = false;
        }else if (TextUtils.isEmpty(binding.etxStreet.getText().toString().trim())){
            H.showMessage(activity,"Enter Street Address");
            value = false;
        }else if(binding.etxStreet.getText().toString().length()<10){
            H.showMessage(activity,"Address Minimum 10 Character");
            value = false;
        }else if (TextUtils.isEmpty(binding.etxLandMark.getText().toString().trim())){
            H.showMessage(activity,"Enter Landmark");
            value = false;
        }else if (TextUtils.isEmpty(binding.etxCountry.getText().toString().trim())){
            H.showMessage(activity,"Enter Country");
            value = false;
        }else if (TextUtils.isEmpty(binding.etxState.getText().toString().trim())){
            H.showMessage(activity,"Enter State");
            value = false;
        } else if (TextUtils.isEmpty(binding.etxcity.getText().toString().trim())){
            H.showMessage(activity,"Enter City");
            value = false;
        }else if (TextUtils.isEmpty(binding.etxPincode.getText().toString().trim())){
            H.showMessage(activity,"Enter Pincode");
            value = false;
        }else if (binding.etxPincode.getText().toString().trim().length()<6 || binding.etxPincode.getText().toString().trim().length()>6){
            H.showMessage(activity,"Enter 6 Digit Pincode");
            value = false;
        } else if (TextUtils.isEmpty(addressName)){
            H.showMessage(activity,"Choose Address Type");
            value = false;
        }else if (!TextUtils.isEmpty(binding.etxOtherNumber.getText().toString().trim())){
            if (binding.etxOtherNumber.getText().toString().trim().length()>10 || binding.etxOtherNumber.getText().toString().trim().length()<10){
                H.showMessage(activity,"Enter 10 Digit Contact Number");
                value = false;
            }
        }else if (!TextUtils.isEmpty(binding.etxEmail.getText().toString().trim())){
            if (!validEmail(binding.etxEmail.getText().toString().trim())){
                H.showMessage(activity,"Enter Valid Email");
                value = false;
            }
        }

        return value;
    }

    private void saveAddress(){
        if (checkvalitation()){
            hitSaveAddressApi();
        }
    }

    private void hitSaveAddressApi() {
        showProgress();
        Json j = new Json();
        j.addInt(P.user_id,H.getInt(session.getString(P.user_id)));
        j.addInt(P.id,addressID);
        j.addString(P.address_type,addressName);
        j.addString(P.full_name,binding.etxName.getText().toString().trim());
        j.addString(P.address,binding.etxStreet.getText().toString().trim());
        j.addString(P.landmark,binding.etxLandMark.getText().toString().trim());
        j.addString(P.country,binding.etxCountry.getText().toString().trim());
        j.addString(P.state,binding.etxState.getText().toString().trim());
        j.addString(P.city,binding.etxcity.getText().toString().trim());
        j.addString(P.pincode,binding.etxPincode.getText().toString().trim());
        j.addString(P.phone,binding.etxNumber.getText().toString().trim());
        j.addString(P.phone2,binding.etxOtherNumber.getText().toString().trim());
        j.addString(P.email,binding.etxEmail.getText().toString().trim());
        j.addInt(P.main_address,mainAddress);

        Api.newApi(activity, P.baseUrl + "save_address").addJson(j)
                .setMethod(Api.POST)
                //.onHeaderRequest(App::getHeaders)
                .onError(() -> {
                    H.showMessage(activity, "On error is called");
                    hideProgress();
                })
                .onSuccess(json ->
                {
                    if (json.getInt(P.status) == 1) {

                        if (ediAddress){
                            H.showMessage(activity, "Address Updated Successfully");
                        }else {
                            H.showMessage(activity, "Address Save Successfully");
                        }
                        MyAddressActivity.checkAddress = true;
                        new Handler().postDelayed(() -> {
                            finish();
                        }, 1000);

                    } else {
                        H.showMessage(activity, json.getString(P.msg));
                    }
                    hideProgress();
                })
                .run("hitSaveAddressApi");
    }

    private void updateHomeAddress(){
        addressName = home;
        binding.txtHomeAddress.setTextColor(getResources().getColor(R.color.white));
        binding.txtHomeAddress.setBackground(getResources().getDrawable(R.drawable.green_bg));
        binding.txtOfficeAddress.setTextColor(getResources().getColor(R.color.green));
        binding.txtOfficeAddress.setBackground(getResources().getDrawable(R.drawable.grey_border2));
    }

    private void updateOfficeAddress(){
        addressName = office;
        binding.txtOfficeAddress.setTextColor(getResources().getColor(R.color.white));
        binding.txtOfficeAddress.setBackground(getResources().getDrawable(R.drawable.green_bg));
        binding.txtHomeAddress.setTextColor(getResources().getColor(R.color.green));
        binding.txtHomeAddress.setBackground(getResources().getDrawable(R.drawable.grey_border2));
    }


    private void checkForPlaceOrder(){

        AddressModel addressModel = new AddressModel();
        addressModel.setId(addressID+"");
        addressModel.setFull_name(binding.etxName.getText().toString().trim());
        addressModel.setAddress_type(addressName);
        addressModel.setAddress(binding.etxStreet.getText().toString().trim());
        addressModel.setLandmark(binding.etxLandMark.getText().toString().trim());
        addressModel.setCountry(binding.etxCountry.getText().toString().trim());
        addressModel.setState(binding.etxState.getText().toString().trim());
        addressModel.setCity(binding.etxcity.getText().toString().trim());
        addressModel.setPincode(binding.etxPincode.getText().toString().trim());
        addressModel.setPhone(binding.etxNumber.getText().toString().trim());
        addressModel.setPhone2(binding.etxOtherNumber.getText().toString().trim());
        addressModel.setEmail(binding.etxEmail.getText().toString().trim());
        addressModel.setMain_address(mainAddress+"");

        Config.addressModel = addressModel;
        Intent placeOrderIntent = new Intent(activity,CheckOutActivity.class);
        startActivity(placeOrderIntent);
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (CartFragment.paymentFail){
            finish();
        }

        if (GOOGLE_ADDRESS){
            Session session = new Session(activity);
            binding.etxStreet.setText(session.getString(P.googleAddress));
            binding.etxLandMark.setText("");
            binding.etxcity.setText(session.getString(P.googleCity));
            binding.etxPincode.setText(session.getString(P.googleCode));
            binding.etxState.setText(session.getString(P.googleState));
            binding.etxState.setText(session.getString(P.googleState));
//            binding.etxCountry.setText(session.getString(P.googleCountry));
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btnProcess:
                if (binding.btnProcess.getText().toString().equals(placeOrder)){
                    if (checkvalitation()){
                        checkForPlaceOrder();
                    }
                }else {
                    saveAddress();
                }
                break;
            case R.id.txtHomeAddress:
                updateHomeAddress();
                break;
            case R.id.txtOfficeAddress:
                updateOfficeAddress();
                break;
        }
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