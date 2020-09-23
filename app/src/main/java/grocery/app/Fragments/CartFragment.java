package grocery.app.Fragments;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
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
import grocery.app.util.Config;


public class CartFragment extends Fragment implements View.OnClickListener, CartAdapter.CartInterface {

    private FragmentCartBinding binding;
    private Context context;
    private List<CartModel> cartModelList;
    private CartAdapter cartAdapter;
    private String rs = "₹.";
    private LoadingDialog loadingDialog;

    public static CartFragment newInstance() {
        return new CartFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_cart, container, false);
        context = inflater.getContext();
        loadingDialog = new LoadingDialog(context);
        initView();
        binding.btnProcessToPay.setOnClickListener(this);
        return binding.getRoot();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (getUserVisibleHint()) {
            hitCartListApi();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnProcessToPay:
                Intent addressIntent = new Intent(context, MyAddressActivity.class);
                addressIntent.putExtra(Config.FOR_CHECKOUT,true);
                startActivity(addressIntent);
                break;
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
                        hitToUpdateSummery();
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
                        hitToUpdateSummery();
                    } else {
                        H.showMessage(context, json.getString(P.msg));
                    }
                    hideProgress();
                })
                .run("hitUpdateCartApi");
    }

    private void hitCartListApi() {
        showProgress();
        hideSummary();
        cartModelList.clear();
        Json j = new Json();
        j.addString(P.cart_token, new Session(context).getString(P.cart_token));
        j.addString(P.user_id, "");

        Api.newApi(context, P.baseUrl + "cart").addJson(j)
                .setMethod(Api.POST)
                //.onHeaderRequest(App::getHeaders)
                .onError(() -> {
                    hideProgress();
                    checkError();
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
                                model.setId(jsonObject.getString("id"));
                                model.setTemp_id(jsonObject.getString("temp_id"));
                                model.setProduct_id(jsonObject.getString("product_id"));
                                model.setProducts_variants_id(jsonObject.getString("products_variants_id"));
                                model.setQty(jsonObject.getString("qty"));
                                model.setOption1(jsonObject.getString("option1"));
                                model.setOption2(jsonObject.getString("option2"));
                                model.setOption3(jsonObject.getString("option3"));
                                model.setTotal_price(jsonObject.getString("total_price"));
                                model.setPrice(jsonObject.getString("price"));
                                model.setCoupon_discount_amount(jsonObject.getString("coupon_discount_amount"));
//                                model.setCart_image(jsonObject.getString("image"));
//                                model.setName(jsonObject.getString("name"));
                                cartModelList.add(model);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            cartAdapter.notifyDataSetChanged();

                        }

                        binding.txtSubTotal.setText(rs + json.getString("item_total"));
                        binding.txtTaxName.setText(json.getString("tax_name"));
                        binding.txtTaxCharge.setText(rs + json.getString("tax_amount"));
                        binding.txtDeliverCharge.setText(rs + json.getString("delivery_amount"));
                        binding.txtTotalAMount.setText(rs + json.getString("grand_total"));

                        hideProgress();
                        showSummary();
                    } else {
                        hideProgress();
//                        H.showMessage(context, json.getString(P.msg));
                    }
                    checkError();
                })
                .run("hitCartListApi");
    }

    private void hitToUpdateSummery() {
        Json j = new Json();
        j.addString(P.cart_token, new Session(context).getString(P.cart_token));
        j.addString(P.user_id, "");

        Api.newApi(context, P.baseUrl + "cart").addJson(j)
                .setMethod(Api.POST)
                //.onHeaderRequest(App::getHeaders)
                .onError(() -> {
                    checkError();
                    if (cartModelList.isEmpty()) {
                        hideSummary();
                    }
                    H.showMessage(context, "On error is called");
                })
                .onSuccess(json ->
                {
                    if (json.getInt(P.status) == 1) {
                        json = json.getJson(P.data);
                        Config.CART_JSON = json;
                        binding.txtSubTotal.setText(rs + json.getString("item_total"));
                        binding.txtTaxName.setText(json.getString("tax_name"));
                        binding.txtTaxCharge.setText(json.getString("tax_amount"));
                        binding.txtDeliverCharge.setText(json.getString("delivery_amount"));
                        binding.txtTotalAMount.setText(rs + json.getString("grand_total"));
                    } else {
                        H.showMessage(context, json.getString(P.msg));
                    }
                    if (cartModelList.isEmpty()) {
                        hideSummary();
                    }
                    checkError();
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
        if (binding.lnrError.getVisibility()==View.GONE){
            binding.lnrError.setVisibility(View.VISIBLE);
        }
    }
    private void hideError(){
        if (binding.lnrError.getVisibility()==View.VISIBLE){
            binding.lnrError.setVisibility(View.GONE);
        }
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
