package grocery.app;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.CompoundButton;

import com.adoisstudio.helper.H;
import com.adoisstudio.helper.Session;

import grocery.app.common.P;
import grocery.app.databinding.ActivityNewAddressBinding;
import grocery.app.model.AddressModel;
import grocery.app.util.Config;
import grocery.app.util.WindowBarColor;

public class NewAddressActivity extends AppCompatActivity implements View.OnClickListener {

    private NewAddressActivity activity = this;
    private ActivityNewAddressBinding binding;
    private String addressName;
    private boolean setDeliveryAddress;
    private boolean ediAddress;
    private String home = "Home";
    private String office = "Office";
    private boolean GOOGLE_ADDRESS;

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


        setDeliveryAddress = false;
        ediAddress = getIntent().getBooleanExtra(Config.EDIT_ADDRESS,false);
        GOOGLE_ADDRESS = getIntent().getBooleanExtra(Config.GOOGLE_ADDRESS,false);

        binding.btnProcess.setOnClickListener(this);
        binding.txtHomeAddress.setOnClickListener(this);
        binding.txtOfficeAddress.setOnClickListener(this);

        binding.checkBoxDefault.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked){
                    setDeliveryAddress = true;
                }else {
                    setDeliveryAddress = false;
                }
            }
        });

        if (ediAddress){
            setPreviousAddress();
        }

    }

    private void setPreviousAddress(){
        binding.toolbar.setTitle("Edit Address");
        AddressModel model = Config.addressModel;
        binding.etxName.setText(model.getName());
        binding.etxNumber.setText(model.getContactNumber());
        binding.etxAptName.setText(model.getApartmentName());
        binding.etxStreet.setText(model.getStreetAddress());
        binding.etxLandMark.setText(model.getLandMark());
        binding.etxcity.setText(model.getCity());
        binding.etxPincode.setText(model.getPincode());
        if (model.getAddressTitle().equals(home)){
            updateHomeAddress();
        }else if (model.getAddressTitle().equals(office)){
            updateOfficeAddress();
        }
        if (model.getIsDeliveryAddress().equals("1")){
            binding.checkBoxDefault.setChecked(true);
            setDeliveryAddress = true;
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
        }else if (TextUtils.isEmpty(binding.etxAptName.getText().toString().trim())){
            H.showMessage(activity,"Enter Apartment Name");
            value = false;
        }else if (TextUtils.isEmpty(binding.etxStreet.getText().toString().trim())){
            H.showMessage(activity,"Enter Street Address");
            value = false;
        }else if (TextUtils.isEmpty(binding.etxLandMark.getText().toString().trim())){
            H.showMessage(activity,"Enter Landmark");
            value = false;
        }else if (TextUtils.isEmpty(binding.etxcity.getText().toString().trim())){
            H.showMessage(activity,"Enter City");
            value = false;
        }else if (TextUtils.isEmpty(binding.etxPincode.getText().toString().trim())){
            H.showMessage(activity,"Enter Pincode");
            value = false;
        }else if (binding.etxPincode.getText().toString().trim().length()<6 || binding.etxPincode.getText().toString().trim().length()>6){
            H.showMessage(activity,"Enter 6 Digit Pincode");
            value = false;
        } else if (TextUtils.isEmpty(addressName)){
            H.showMessage(activity,"Choose Address Name");
            value = false;
        }

        return value;
    }

    private void saveAddress(){
        if (checkvalitation()){
            H.showMessage(activity,"API pending");
            MyAddressActivity.checkAddress = true;
            AddressModel model = new AddressModel();
            model.setName(binding.etxName.getText().toString().trim());
            model.setContactNumber(binding.etxNumber.getText().toString().trim());
            model.setApartmentName(binding.etxAptName.getText().toString().trim());
            model.setStreetAddress(binding.etxStreet.getText().toString().trim());
            model.setLandMark(binding.etxLandMark.getText().toString().trim());
            model.setCity(binding.etxcity.getText().toString().trim());
            model.setPincode(binding.etxPincode.getText().toString().trim());
            model.setAddressTitle(addressName);
            if (setDeliveryAddress){
                model.setIsDeliveryAddress("1");
            }else {
                model.setIsDeliveryAddress("0");
            }
            MyAddressActivity.addressModelList.add(model);
            finish();
        }
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

    @Override
    protected void onResume() {
        super.onResume();
        if (GOOGLE_ADDRESS){
            Session session = new Session(activity);
            binding.etxAptName.setText("");
            binding.etxStreet.setText(session.getString(P.googleAddress));
            binding.etxLandMark.setText("");
            binding.etxcity.setText(session.getString(P.googleCity));
            binding.etxPincode.setText(session.getString(P.googleCode));
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btnProcess:
                saveAddress();
                break;
            case R.id.txtHomeAddress:
                updateHomeAddress();
                break;
            case R.id.txtOfficeAddress:
                updateOfficeAddress();
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