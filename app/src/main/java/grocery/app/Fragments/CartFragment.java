package grocery.app.Fragments;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;

import androidx.cardview.widget.CardView;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.adoisstudio.helper.Api;
import com.adoisstudio.helper.H;
import com.adoisstudio.helper.Json;
import com.adoisstudio.helper.LoadingDialog;
import com.adoisstudio.helper.Session;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import grocery.app.MyAddressActivity;
import grocery.app.R;
import grocery.app.adapter.CartAdapter;
import grocery.app.common.P;
import grocery.app.databinding.FragmentCartBinding;
import grocery.app.model.CartModel;
import grocery.app.util.AmountFormat;
import grocery.app.util.Config;
import grocery.app.util.LoginFlag;
import grocery.app.util.PageUtil;


public class CartFragment extends Fragment implements View.OnClickListener, CartAdapter.CartInterface {

    private FragmentCartBinding binding;
    private Context context;
    private List<CartModel> cartModelList;
    private CartAdapter cartAdapter;
    private String rs = "₹ ";
    private LoadingDialog loadingDialog;
    public String applyCoupon = "Apply";
    public String removeCoupon = "Remove";
    private Session session;

    public static CartFragment newInstance() {
        return new CartFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_cart, container, false);
        context = inflater.getContext();
        loadingDialog = new LoadingDialog(context);
        session = new Session(context);
        initView();
        binding.btnProcessToPay.setOnClickListener(this);
        binding.txtApplyCoupon.setOnClickListener(this);
        hitCartListApi("");
        return binding.getRoot();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (getUserVisibleHint()) {

        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnProcessToPay:
                if (session.getBool(P.isUserLogin)){
                    H.showMessage(context,"Action Pending");
//                    Intent addressIntent = new Intent(context, MyAddressActivity.class);
//                    addressIntent.putExtra(Config.FOR_CHECKOUT,true);
//                    startActivity(addressIntent);
                }else {
                    PageUtil.goToLoginPage(context, LoginFlag.cartFlagValue);
                }
                break;
            case R.id.txtApplyCoupon:
                onCouponClick();
                break;
        }
    }

    private void onCouponClick(){
        if (binding.txtApplyCoupon.getText().toString().trim().equals(applyCoupon)){
            if (TextUtils.isEmpty(binding.etxCoupon.getText().toString().trim())){
                H.showMessage(context,"Please enter coupon code");
            }else {
                hitToGetCouponCode(binding.etxCoupon.getText().toString().trim());
            }
        }else if (binding.txtApplyCoupon.getText().toString().trim().equals(removeCoupon)){
            removeCouponData(true);
            hitCartListApi("");
        }
    }

    private void hideCouponView(){
        binding.lnrCoupon.setVisibility(View.GONE);
        removeCouponData(true);
    }

    private void showCouponView(){
        binding.lnrCoupon.setVisibility(View.VISIBLE);
        removeCouponData(true);
    }

    private void hitToGetCouponCode(String couponCode) {
        showProgress();
        Json j = new Json();
        j.addString(P.cart_token, new Session(context).getString(P.cart_token));
        j.addString(P.coupon_code, couponCode);
        if (session.getBool(P.isUserLogin)){
            j.addInt(P.user_id, H.getInt(session.getString(P.user_id)));
        }else {
            j.addInt(P.user_id, Config.commonUserID);
        }
        Api.newApi(context, P.baseUrl + "apply_coupon_code").addJson(j)
                .setMethod(Api.POST)
                //.onHeaderRequest(App::getHeaders)
                .onError(() -> {
                    hideProgress();
                    binding.etxCoupon.setText("");
                    H.showMessage(context, "On error is called");
                })
                .onSuccess(json ->
                {
                    if (json.getInt(P.status) == 1) {
                        json = json.getJson(P.data);
                        binding.etxCoupon.setText(couponCode);
                        binding.etxCoupon.setEnabled(false);
                        binding.txtApplyCoupon.setText(removeCoupon);
                        hitCartListApi(couponCode);
                    } else {
                        H.showMessage(context, json.getString(P.msg));
                        binding.etxCoupon.setText("");
                    }
                    hideProgress();
                })
                .run("hitToGetCouponCode");
    }


    private void removeCouponData(boolean removeCoupon){
        if (removeCoupon){
            binding.etxCoupon.setEnabled(true);
            binding.etxCoupon.setText("");
            binding.etxCoupon.setHint("Enter coupon code");
            binding.txtApplyCoupon.setText(applyCoupon);
        }
    }

    private void initView() {
        cartModelList = new ArrayList<>();
        binding.recyclerViewCard.setLayoutManager(new LinearLayoutManager(context));
        binding.recyclerViewCard.setHasFixedSize(true);
        binding.recyclerViewCard.setNestedScrollingEnabled(false);
        cartAdapter = new CartAdapter(context, cartModelList, CartFragment.this);
        binding.recyclerViewCard.setAdapter(cartAdapter);
    }

    @Override
    public void removeCart(int item_id, int position, CardView cardView) {
        hitRemoveCartApi(item_id, position, cardView);
    }

    @Override
    public void updateCart(int item_id, int item_qty, TextView textView) {
        hitUpdateCartApi(item_id, item_qty, textView);
    }

    private void hitRemoveCartApi(int item_id, int position, CardView cardView) {
        showProgress();
        Json j = new Json();
        j.addInt(P.item_id, item_id);

        Api.newApi(context, P.baseUrl + "remove_cart_product").addJson(j)
                .setMethod(Api.POST)
                //.onHeaderRequest(App::getHeaders)
                .onError(() -> {
                    H.showMessage(context, "On error is called");
                    hideProgress();
                })
                .onSuccess(json ->
                {
                    if (json.getInt(P.status) == 1) {
                        cardView.startAnimation(removeItem(position));
                        H.showMessage(context, json.getString(P.msg));
                        hitToUpdateSummery(binding.etxCoupon.getText().toString().trim());
                    } else {
                        H.showMessage(context, json.getString(P.msg));
                    }
                    hideProgress();
                })
                .run("hitRemoveCartApi");
    }

    private void hitUpdateCartApi(int item_id, int item_qty, TextView textView) {
        showProgress();
        Json j = new Json();
        j.addInt(P.item_id, item_id);
        j.addInt(P.item_qty, item_qty);

        Api.newApi(context, P.baseUrl + "update_cart_product").addJson(j)
                .setMethod(Api.POST)
                //.onHeaderRequest(App::getHeaders)
                .onError(() -> {
                    H.showMessage(context, "On error is called");
                    hideProgress();
                })
                .onSuccess(json ->
                {
                    if (json.getInt(P.status) == 1) {
                        textView.setText(item_qty + "");
                        H.showMessage(context, json.getString(P.msg));
                        hitToUpdateSummery(binding.etxCoupon.getText().toString().trim());
                    } else {
                        H.showMessage(context, json.getString(P.msg));
                    }
                    hideProgress();
                })
                .run("hitUpdateCartApi");
    }

    private void hitCartListApi(String couponCode) {
        showProgress();
        hideSummary();
        cartModelList.clear();
        Json j = new Json();
        j.addString(P.cart_token, new Session(context).getString(P.cart_token));
        if (session.getBool(P.isUserLogin)){
            j.addInt(P.user_id, H.getInt(session.getString(P.user_id)));
        }else {
            j.addInt(P.user_id, Config.commonUserID);
        }
        j.addString(P.coupon_code, couponCode);
        Api.newApi(context, P.baseUrl + "cart").addJson(j)
                .setMethod(Api.POST)
                //.onHeaderRequest(App::getHeaders)
                .onError(() -> {
                    hideProgress();
                    checkError();
                    hideCouponView();
                    H.showMessage(context, "On error is called");
                })
                .onSuccess(json ->
                {
                    if (json.getInt(P.status) == 1) {
                        json = json.getJson(P.data);
                        Config.CART_JSON = json;
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
                                model.setTotal_price(jsonObject.getString(P.total_price));
                                model.setCoupon_discount_amount(jsonObject.getString(P.coupon_discount_amount));
                                try {
                                    JSONObject priceJson = jsonObject.getJSONObject(P.price);
                                    model.setPrice(priceJson.getString(P.price));
                                    model.setSaleprice(priceJson.getString(P.saleprice));
                                    model.setDiscount_amount(priceJson.getString(P.discount_amount));
                                    model.setDiscount(priceJson.getString(P.discount));
                                }catch (Exception e){

                                }

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
                        }

                        cartAdapter.notifyDataSetChanged();
                        binding.txtSubTotal.setText(rs + AmountFormat.getFormatedAmount(json.getString(P.item_total)));
                        binding.txtTaxName.setText(json.getString(P.tax_name));
                        binding.txtTaxCharge.setText(rs + AmountFormat.getFormatedAmount(json.getString(P.tax_amount)));
                        binding.txtCouponDiscount.setText(rs + AmountFormat.getFormatedAmount(json.getString(P.coupon_discount_amount)));
                        binding.txtDeliverCharge.setText(rs + AmountFormat.getFormatedAmount(json.getString(P.delivery_amount)));
                        binding.txtTotalAMount.setText(rs + AmountFormat.getFormatedAmount(json.getString(P.grand_total)));

                        hideProgress();
                        showSummary();

                    } else {
                        hideProgress();
                    }
                    checkError();

                    if (TextUtils.isEmpty(couponCode)){
                        if (cartModelList.isEmpty()){
                            hideCouponView();
                        }else {
                            showCouponView();
                        }
                    }
                })
                .run("hitCartListApi");
    }

    private void hitToUpdateSummery(String couponCode) {
        Json j = new Json();
        j.addString(P.cart_token, new Session(context).getString(P.cart_token));
        if (session.getBool(P.isUserLogin)){
            j.addInt(P.user_id, H.getInt(session.getString(P.user_id)));
        }
        j.addString(P.coupon_code, couponCode);

        Api.newApi(context, P.baseUrl + "cart").addJson(j)
                .setMethod(Api.POST)
                //.onHeaderRequest(App::getHeaders)
                .onError(() -> {
                    checkError();
                    if (cartModelList.isEmpty()) {
                        hideSummary();
                    }
                    hideCouponView();
                    H.showMessage(context, "On error is called");
                })
                .onSuccess(json ->
                {
                    if (json.getInt(P.status) == 1) {
                        json = json.getJson(P.data);
                        Config.CART_JSON = json;
                        binding.txtSubTotal.setText(rs + AmountFormat.getFormatedAmount(json.getString(P.item_total)));
                        binding.txtTaxName.setText(json.getString(P.tax_name));
                        binding.txtTaxCharge.setText(rs + AmountFormat.getFormatedAmount(json.getString(P.tax_amount)));
                        binding.txtCouponDiscount.setText(rs + AmountFormat.getFormatedAmount(json.getString(P.coupon_discount_amount)));
                        binding.txtDeliverCharge.setText(rs + AmountFormat.getFormatedAmount(json.getString(P.delivery_amount)));
                        binding.txtTotalAMount.setText(rs + AmountFormat.getFormatedAmount(json.getString(P.grand_total)));
                    } else {
                        H.showMessage(context, json.getString(P.msg));
                    }
                    if (cartModelList.isEmpty()) {
                        hideSummary();
                    }
                    checkError();

                    if (cartModelList.isEmpty()){
                        hideCouponView();
                    }else {
                        showCouponView();
                    }
                })
                .run("hitToUpdateSummery");
    }

    private Animation removeItem(int position) {
        Animation animation = AnimationUtils.loadAnimation(context,
                R.anim.slide_out);
        animation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                cartModelList.remove(position);
                cartAdapter.notifyDataSetChanged();
                if(cartModelList.isEmpty()){
                    hideSummary();
                    checkError();
                }
            }
        });
        return animation;
    }

    private void checkError(){
        if (cartModelList.isEmpty()){
            showError();
        }else {
            hideError();
        }
    }

    private void showError(){
        binding.lnrError.setVisibility(View.VISIBLE);
    }
    private void hideError(){
        binding.lnrError.setVisibility(View.GONE);
    }
    private void showSummary() {
        binding.lnrSummary.setVisibility(View.VISIBLE);
    }

    private void hideSummary() {
        binding.lnrSummary.setVisibility(View.GONE);
    }

    private void showProgress() {
        loadingDialog.show("Please wait..");
    }

    private void hideProgress() {
        loadingDialog.hide();
    }

}

