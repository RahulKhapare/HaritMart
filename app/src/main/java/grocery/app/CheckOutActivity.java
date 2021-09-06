package grocery.app;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.adoisstudio.helper.Api;
import com.adoisstudio.helper.H;
import com.adoisstudio.helper.Json;
import com.adoisstudio.helper.JsonList;
import com.adoisstudio.helper.LoadingDialog;
import com.adoisstudio.helper.Session;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import grocery.app.Fragments.CartFragment;
import grocery.app.adapter.CartAdapter;
import grocery.app.adapter.GetwayAdapter;
import grocery.app.common.P;
import grocery.app.databinding.ActivityCheckOutProductBinding;
import grocery.app.model.AddressModel;
import grocery.app.model.CartModel;
import grocery.app.model.GetwayModel;
import grocery.app.util.AmountFormat;
import grocery.app.util.Config;
import grocery.app.util.WindowBarColor;

public class CheckOutActivity extends AppCompatActivity implements GetwayAdapter.ClickItem {

    private CheckOutActivity activity = this;
    private ActivityCheckOutProductBinding binding;
    private LoadingDialog loadingDialog;
    private AddressModel addressModel;
    private String rs = "₹ ";
    private List<GetwayModel> getwayModelList;
    private GetwayAdapter adapter;
    private CartAdapter cartAdapter;
    private String paymentID;
    private List<CartModel> cartModelList;
    private Session session;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        WindowBarColor.setColor(this);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_check_out_product);

        binding.toolbar.setTitle("Place Order");
        setSupportActionBar(binding.toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        initView();
        updateAddress();
        updateCartAndSummery();

    }

    private void initView(){
        session = new Session(activity);
        loadingDialog = new LoadingDialog(activity);
        addressModel = Config.addressModel;
        getwayModelList = new ArrayList<>();
        cartModelList = new ArrayList<>();

        binding.recyclerCartDetail.setHasFixedSize(true);
        binding.recyclerCartDetail.setNestedScrollingEnabled(false);
        LinearLayoutManager linearLayoutManager2 = new LinearLayoutManager(activity);
        binding.recyclerCartDetail.setLayoutManager(linearLayoutManager2);
        cartAdapter = new CartAdapter(activity,cartModelList,true);
        binding.recyclerCartDetail.setAdapter(cartAdapter);

        binding.recyclerPayment.setHasFixedSize(true);
        binding.recyclerPayment.setNestedScrollingEnabled(false);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(activity);
        binding.recyclerPayment.setLayoutManager(linearLayoutManager);
        adapter = new GetwayAdapter(activity,getwayModelList,true,1);
        binding.recyclerPayment.setAdapter(adapter);

        hitForPaymentOption();

        binding.btnBuyNow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hitPlaceOrder();
            }
        });

    }

    private void updateAddress(){
        binding.txtAddressTitle.setText("Billing & Shipping Address");
        binding.txtTitle.setText(addressModel.getAddress_type() + " Address");
        binding.txtName.setText(addressModel.getFull_name());
        binding.txtAddress.setText(getAddress(addressModel));
    }

    private void updateCartAndSummery(){

        Json json = Config.CART_JSON;
        JSONArray jsonArray = json.getJsonArray(P.item_list);

        for (int i = 0; i < jsonArray.length(); i++) {
            try {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                CartModel model = new CartModel();

                model.setId(jsonObject.getString(P.id));
                model.setTemp_id(jsonObject.getString(P.temp_id));
                model.setProduct_id(jsonObject.getString(P.product_id));
                model.setProducts_variants_id(jsonObject.getString(P.products_variants_id));
                model.setQty(jsonObject.getString(P.qty));
                model.setName(jsonObject.getString(P.name));
                model.setSku(jsonObject.getString(P.sku));
                model.setSlug(jsonObject.getString(P.slug));
                model.setImage(jsonObject.getString(P.image));
                model.setPrice(jsonObject.getString(P.price));
                model.setTotal_price(jsonObject.getString(P.total_price));
                model.setCoupon_discount_amount(jsonObject.getString(P.coupon_discount_amount));

//                try {
//                    JSONObject priceJson = jsonObject.getJSONObject(P.price);
//                    model.setPrice(priceJson.getString(P.price));
//                    model.setSaleprice(priceJson.getString(P.saleprice));
//                    model.setDiscount_amount(priceJson.getString(P.discount_amount));
//                    model.setDiscount(priceJson.getString(P.discount));
//                }catch (Exception e){
//
//                }

                try {
                    if(!TextUtils.isEmpty(jsonObject.getString(P.option1)) && !jsonObject.getString(P.option1).equals("0")){
                        JSONObject jsonOption1 = jsonObject.getJSONObject(P.option1);
                        model.setLabel1(jsonOption1.getString(P.lable));
                        model.setValue1(jsonOption1.getString(P.value));
                    }
                }catch (Exception e){

                }

                try {
                    if(!TextUtils.isEmpty(jsonObject.getString(P.option2)) && !jsonObject.getString(P.option2).equals("0")){
                        JSONObject jsonOption2 = jsonObject.getJSONObject(P.option2);
                        model.setLabel2(jsonOption2.getString(P.lable));
                        model.setValue2(jsonOption2.getString(P.value));
                    }
                }catch (Exception e){

                }

                cartModelList.add(model);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            cartAdapter.notifyDataSetChanged();

        }

        binding.txtSubTotal.setText(rs + AmountFormat.getFormatedAmount(json.getString(P.item_total)));
        binding.txtTaxName.setText(json.getString(P.tax_name));
        binding.txtTaxCharge.setText(rs + AmountFormat.getFormatedAmount(json.getString(P.tax_amount)));
        binding.txtCouponDiscount.setText(rs + AmountFormat.getFormatedAmount(json.getString(P.coupon_discount_amount)));
        binding.txtDeliverCharge.setText(rs + AmountFormat.getFormatedAmount(json.getString(P.delivery_amount)));
        binding.txtTotalAMount.setText(rs + AmountFormat.getFormatedAmount(json.getString(P.grand_total)));

    }

    private String getAddress(AddressModel model){

        String address = "";

        if (!TextUtils.isEmpty(model.getPhone())){
            if (!TextUtils.isEmpty(model.getPhone2())){
                address = "Contact : " + model.getPhone() + "/" + model.getPhone2() + "\n";
            }else {
                address = "Contact : " + model.getPhone() + "\n";
            }
        }

        if (!TextUtils.isEmpty(model.getEmail())){
            address = address + "Email : " + model.getEmail() + "\n";
        }

        if (!TextUtils.isEmpty(model.getAddress())){
            if (!TextUtils.isEmpty(model.getLandmark())){
                address = address + model.getAddress() + "," + model.getLandmark() + "\n";
            }else {
                address = address + model.getAddress() + "\n";
            }
        }

        if (!TextUtils.isEmpty(model.getCity()) && !TextUtils.isEmpty(model.getPincode())){
            address = address +  model.getCity() + " - " + model.getPincode() + "\n";
        }

        return address;
    }


    @Override
    public void selectedGetway(String id,String name) {
        paymentID = id;
    }

    private void hitForPaymentOption(){
        showProgress();
        getwayModelList.clear();
        Api.newApi(activity, P.baseUrl + "payment_gateway")
                .setMethod(Api.GET)
                //.onHeaderRequest(App::getHeaders)
                .onError(() -> {
                    hideProgress();
                    H.showMessage(activity, "Unable to detect payment method, please try again.");
                    finish();
                })
                .onSuccess(json ->
                {
                    if (json.getInt(P.status) == 1) {
                        json = json.getJson(P.data);
                        JsonList jsonList = json.getJsonList(P.list);
                        for (int i=0; i<jsonList.size();i++){
                            JSONObject jsonObject = jsonList.get(i);
                            GetwayModel model = new GetwayModel();
                            try {
                                model.setId(jsonObject.getString(P.id));
                                model.setName(jsonObject.getString(P.name));
                                model.setLogo(jsonObject.getString(P.logo));
                                getwayModelList.add(model);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                        adapter.notifyDataSetChanged();

                        if (getwayModelList.size()==1){
                            binding.lnrOtherPaymentView.setVisibility(View.GONE);
                        }

                    } else {
                        H.showMessage(activity, "Unable to detect payment method, please try again.");
                        finish();
                    }

                    hideProgress();
                })
                .run("hitForPaymentOption");
    }

    private void hitPlaceOrder() {
        showProgress();
        Json j = new Json();
        j.addInt(P.user_id, H.getInt(session.getString(P.user_id)));
        j.addString(P.cart_token, session.getString(P.cart_token));
        j.addString(P.coupon_code, Config.COUPON_CODE);
        j.addInt(P.payment_id, Integer.parseInt(paymentID));

        j.addString(P.bill_address_type,addressModel.getAddress_type() );
        j.addInt(P.bill_address_id,H.getInt(addressModel.getId()) );
        j.addString(P.bill_full_name,addressModel.getFull_name() );
        j.addString(P.bill_address,addressModel.getAddress() );
        j.addString(P.bill_landmark,addressModel.getLandmark() );
        j.addString(P.bill_country,addressModel.getCountry() );
        j.addString(P.bill_state, addressModel.getState());
        j.addString(P.bill_city, addressModel.getCity());
        j.addString(P.bill_pincode, addressModel.getPincode());
        j.addString(P.bill_phone, addressModel.getPhone());
        j.addString(P.bill_email,addressModel.getEmail());
        j.addString(P.bill_phone2,addressModel.getPhone2() );

        if (Config.IS_DELIVER_ADDRESS){
            Config.IS_DELIVER_ADDRESS = false;
            j.addString(P.ship_full_name, addressModel.getFull_name());
            j.addString(P.ship_address_type, addressModel.getAddress_type());
            j.addString(P.ship_address,addressModel.getAddress() );
            j.addString(P.ship_landmark,addressModel.getLandmark() );
            j.addString(P.ship_country,addressModel.getCountry() );
            j.addString(P.ship_state,addressModel.getState() );
            j.addString(P.ship_city, addressModel.getCity());
            j.addString(P.ship_pincode, addressModel.getPincode());
            j.addString(P.ship_phone, addressModel.getPhone());
            j.addString(P.ship_email,addressModel.getEmail() );
            j.addString(P.ship_phone2, addressModel.getPhone2());
            j.addInt(P.shiptodifferetadd,1 );
        }else {
            j.addString(P.ship_full_name, "");
            j.addString(P.ship_address_type, "");
            j.addString(P.ship_address,"" );
            j.addString(P.ship_landmark,"" );
            j.addString(P.ship_country,"" );
            j.addString(P.ship_state,"" );
            j.addString(P.ship_city, "");
            j.addString(P.ship_pincode, "");
            j.addString(P.ship_phone, "");
            j.addString(P.ship_email,"" );
            j.addString(P.ship_phone2, "");
            j.addInt(P.shiptodifferetadd,0 );
        }

        Api.newApi(activity, P.baseUrl + "place_order").addJson(j)
                .setMethod(Api.POST)
                //.onHeaderRequest(App::getHeaders)
                .onError(() -> {
                    hideProgress();
                    H.showMessage(activity, "On error is called");
                })
                .onSuccess(json ->
                {
                    if (json.getInt(P.status) == 1) {
                        int errorCode = json.getInt(P.err_code);
                        if (errorCode==0){
                            H.showMessage(activity, json.getString(P.msg));
//                            check if user not login
                        }else if (errorCode==1){
                            H.showMessage(activity, json.getString(P.msg));
                            //cart problem
                            CartFragment.paymentFail = true;
                            finish();
                        }else if (errorCode==2){
                            H.showMessage(activity, json.getString(P.msg));
                            finish();
                        }else if (errorCode==3){
                            H.showMessage(activity, json.getString(P.msg));
                            finish();
                        } else if (errorCode==4){
                            H.showMessage(activity, json.getString(P.msg));
                        }else if (errorCode==5){
                            Config.COUPON_CODE = "";
                            json = json.getJson(P.data);
                            Intent paymentIntent = new Intent(activity,PaymentWebViewActivity.class);
                            paymentIntent.putExtra(Config.ORDER_ID,json.getString(P.order_id));
                            paymentIntent.putExtra(Config.PAYMENT_URL,json.getString(P.payment_url));
                            startActivity(paymentIntent);
                        }
                    } else {
                        H.showMessage(activity, json.getString(P.msg));
                    }
                    hideProgress();
                })
                .run("hitPlaceOrder");
    }

    private void showProgress() {
        loadingDialog.show("Please wait..");
    }

    private void hideProgress() {
        loadingDialog.hide();
    }

    private boolean checkCardDetails() {
        boolean value = true;

        if (TextUtils.isEmpty(binding.etxCardNumber.getText().toString().trim())) {
            value = false;
            H.showMessage(activity, "Please enter card number");
        } else if (binding.etxCardNumber.getText().toString().trim().length() < 16 || binding.etxCardNumber.getText().toString().trim().length() > 16) {
            value = false;
            H.showMessage(activity, "Please enter 16 digit card number");
        } else if (TextUtils.isEmpty(binding.etxCardName.getText().toString().trim())) {
            value = false;
            H.showMessage(activity, "Please enter name on card");
        } else if (TextUtils.isEmpty(binding.etxValidMonth.getText().toString().trim())) {
            value = false;
            H.showMessage(activity, "Please enter month/year");
        } else if (binding.etxValidMonth.getText().toString().trim().length() < 5 || binding.etxValidMonth.getText().toString().trim().length() > 5) {
            value = false;
            H.showMessage(activity, "Please enter valid month/year");
        } else if (!binding.etxValidMonth.getText().toString().matches("(?:0[1-9]|1[0-2])/[0-9]{2}")) {
            value = false;
            H.showMessage(activity, "Please check card expire format");
        }
        else if (TextUtils.isEmpty(binding.etxCvv.getText().toString().trim())) {
            value = false;
            H.showMessage(activity, "Please enter cvv");
        } else if (binding.etxCvv.getText().toString().trim().length() < 3 || binding.etxCvv.getText().toString().trim().length() > 3) {
            value = false;
            H.showMessage(activity, "Please enter valid cvv");
        }

        return value;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
        }
        return false;
    }
}