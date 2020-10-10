package grocery.app;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;

import com.adoisstudio.helper.Api;
import com.adoisstudio.helper.H;
import com.adoisstudio.helper.Json;
import com.adoisstudio.helper.JsonList;
import com.adoisstudio.helper.LoadingDialog;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        WindowBarColor.setColor(this);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_check_out_product);

        binding.toolbar.setTitle("Check Out");
        setSupportActionBar(binding.toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        initView();
        updateAddress();
        updateCartAndSummery();

    }

    private void initView(){

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
                H.showMessage(activity,"Hit Payment WebView");
            }
        });

    }

    private void updateAddress(){
        binding.txtTitle.setText(addressModel.getAddressTitle());
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
                model.setOption1(jsonObject.getString(P.option1));
                model.setOption2(jsonObject.getString(P.option2));
                model.setOption3(jsonObject.getString(P.option3));
                model.setName(jsonObject.getString(P.name));
                model.setSku(jsonObject.getString(P.sku));
                model.setSlug(jsonObject.getString(P.slug));
                model.setImage(jsonObject.getString(P.image));
                model.setTotal_price(jsonObject.getString(P.total_price));
                model.setPrice(jsonObject.getString(P.price));
                model.setCoupon_discount_amount(jsonObject.getString(P.coupon_discount_amount));

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
        address = model.getName() +"\n"+model.getApartmentName()+","+model.getStreetAddress()+"\n"+model.getLandMark()+"\n"+"pincode : "+model.getPincode()+"\n"+ "ph : "+model.getContactNumber();
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
                    H.showMessage(activity, "On error is called");
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
                    } else {
                        H.showMessage(activity, json.getString(P.msg));
                    }

                    hideProgress();
                })
                .run("hitForPaymentOption");
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
}