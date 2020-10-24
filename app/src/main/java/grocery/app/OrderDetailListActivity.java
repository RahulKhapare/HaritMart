package grocery.app;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ActionBar;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.TextView;

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

import grocery.app.adapter.OrderDetailListAdapter;
import grocery.app.adapter.OrderSortAdapter;
import grocery.app.common.App;
import grocery.app.common.P;
import grocery.app.databinding.ActivityOrderDetailListBinding;
import grocery.app.model.OrderDetailListModel;
import grocery.app.model.OrderSortModel;
import grocery.app.util.Click;
import grocery.app.util.Config;
import grocery.app.util.WindowBarColor;

public class OrderDetailListActivity extends AppCompatActivity implements OrderSortAdapter.SortClick,OrderDetailListAdapter.onClick{

    private OrderDetailListActivity activity = this;
    private ActivityOrderDetailListBinding binding;
    private LoadingDialog loadingDialog;
    private List<OrderDetailListModel> orderDetailListModelList;
    private OrderDetailListAdapter adapter;
    public static int SORT_POSITION = 0;
    private Dialog dialog;
    private String cancelValue;
    private String sortName = "";
    private int cancelKey;
    private List<OrderSortModel> orderSortModelList;
    private boolean fromSuccessOrder;
    private int DECENDING = 1;
    private Session session;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        WindowBarColor.setColor(this);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_order_detail_list);

        binding.toolbar.setTitle("My Orders");
        setSupportActionBar(binding.toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        initView();

    }

    private void initView(){
        session = new Session(activity);
        fromSuccessOrder = getIntent().getBooleanExtra(Config.FROM_SUCCESS_ORDER, false);
        loadingDialog = new LoadingDialog(activity);
        orderDetailListModelList = new ArrayList<>();
        orderSortModelList = new ArrayList<>();
        binding.recyclerOrderDetailList.setLayoutManager(new LinearLayoutManager(activity));
        binding.recyclerOrderDetailList.setHasFixedSize(true);
        binding.recyclerOrderDetailList.setNestedScrollingEnabled(false);
        adapter = new OrderDetailListAdapter(activity,orderDetailListModelList);
        binding.recyclerOrderDetailList.setAdapter(adapter);
        hitOrderDetailList(DECENDING);
    }

    private void hitOrderDetailList(int order_by) {
        orderDetailListModelList.clear();
        showProgress();
        Json j = new Json();
        j.addInt(P.user_id, H.getInt(session.getString(P.user_id)));
        j.addString(P.cart_token, session.getString(P.cart_token));
        j.addInt(P.order_by, order_by);

        Api.newApi(activity, P.baseUrl + "order_list").addJson(j)
                .setMethod(Api.POST)
                //.onHeaderRequest(App::getHeaders)
                .onError(() -> {
                    hideProgress();
                    checkError();
                    H.showMessage(activity, "On error is called");
                })
                .onSuccess(json ->
                {
                    orderDetailListModelList.clear();
                    if (json.getInt(P.status) == 1) {
                        json = json.getJson(P.data);
                        JSONObject jsonObject = json;
                        try {
                            loadSortData(jsonObject.getJSONArray(P.order_filter));
                            App.order_status_list = jsonObject.getJSONArray(P.order_status_list);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        JsonList jsonOrderList = json.getJsonList(P.order_list);

                        for(Json orderJson : jsonOrderList){

                            OrderDetailListModel model = new OrderDetailListModel();
                            model.setId(orderJson.getString(P.id));
                            model.setOrder_number(orderJson.getString(P.order_number));
                            model.setOrdered_date(orderJson.getString(P.ordered_date));
                            model.setOrdered_amount(orderJson.getString(P.ordered_amount));
                            model.setName(orderJson.getString(P.name));
                            model.setEmail(orderJson.getString(P.email));
                            model.setPhone(orderJson.getString(P.phone));
                            model.setProductItemList(orderJson.getJsonList(P.item_list));
                            model.setGrand_total(orderJson.getString(P.grand_total));
                            model.setTax_name(orderJson.getString(P.tax_name));
                            model.setTax_amount(orderJson.getString(P.tax_amount));
                            model.setDelivery_name(orderJson.getString(P.delivery_name));
                            model.setDelivery_amount(orderJson.getString(P.delivery_amount));
                            model.setCoupon_code_id(orderJson.getString(P.coupon_code_id));
                            model.setCoupon_code(orderJson.getString(P.coupon_code));
                            model.setCoupon_discount_amount(orderJson.getString(P.coupon_discount_amount));
                            model.setItem_total(orderJson.getString(P.item_total));
                            model.setShiptodifferetadd(orderJson.getString(P.shiptodifferetadd));
                            model.setBill_full_name(orderJson.getString(P.bill_full_name));
                            model.setBill_phone(orderJson.getString(P.bill_phone));
                            model.setBill_email(orderJson.getString(P.bill_email));
                            model.setBill_phone2(orderJson.getString(P.bill_phone2));
                            model.setBill_address_type(orderJson.getString(P.bill_address_type));
                            model.setBill_address(orderJson.getString(P.bill_address));
                            model.setBill_address2(orderJson.getString(P.bill_address2));
                            model.setBill_landmark(orderJson.getString(P.bill_landmark));
                            model.setBill_locality(orderJson.getString(P.bill_locality));
                            model.setBill_country(orderJson.getString(P.bill_country));
                            model.setBill_country_id(orderJson.getString(P.bill_country_id));
                            model.setBill_state(orderJson.getString(P.bill_state));
                            model.setBill_state_id(orderJson.getString(P.bill_state_id));
                            model.setBill_city(orderJson.getString(P.bill_city));
                            model.setBill_city_id(orderJson.getString(P.bill_city_id));
                            model.setBill_pincode(orderJson.getString(P.bill_pincode));
                            model.setShip_full_name(orderJson.getString(P.ship_full_name));
                            model.setShip_phone(orderJson.getString(P.ship_phone));
                            model.setShip_email(orderJson.getString(P.ship_email));
                            model.setShip_phone2(orderJson.getString(P.ship_phone2));
                            model.setShip_address_type(orderJson.getString(P.ship_address_type));
                            model.setShip_address(orderJson.getString(P.ship_address));
                            model.setShip_address2(orderJson.getString(P.ship_address2));
                            model.setShip_landmark(orderJson.getString(P.ship_landmark));
                            model.setShip_locality(orderJson.getString(P.ship_locality));
                            model.setShip_country(orderJson.getString(P.ship_country));
                            model.setShip_country_id(orderJson.getString(P.ship_country));
                            model.setShip_state(orderJson.getString(P.ship_state));
                            model.setShip_state_id(orderJson.getString(P.ship_state_id));
                            model.setShip_city(orderJson.getString(P.ship_city));
                            model.setShip_city_id(orderJson.getString(P.ship_city_id));
                            model.setShip_pincode(orderJson.getString(P.ship_pincode));
                            model.setPayment_id(orderJson.getString(P.payment_id));
                            model.setPayment_name(orderJson.getString(P.payment_name));
                            model.setPayment_info(orderJson.getString(P.payment_info));
                            model.setPayment_status(orderJson.getString(P.payment_status));
                            model.setOrder_status(orderJson.getString(P.order_status));
                            model.setOrder_status_comment(orderJson.getString(P.order_status_comment));
                            model.setPdf_url(orderJson.getString(P.pdf_url));

                            orderDetailListModelList.add(model);
                        }

                    }

                    adapter.notifyDataSetChanged();
                    hideProgress();
                    checkError();
                })
                .run("hitOrderDetailList");
    }

    private void onSortClick() {
        try {

            List<OrderSortModel> list = new ArrayList<>();
            for (int i=0;i<orderSortModelList.size();i++){
                if (i==2){
                    list.add(new OrderSortModel(cancelKey,cancelValue));
                    list.add(new OrderSortModel(orderSortModelList.get(i).getKey(),orderSortModelList.get(i).getValue()));
                }else {
                    list.add(new OrderSortModel(orderSortModelList.get(i).getKey(),orderSortModelList.get(i).getValue()));
                }
            }
            dialog = new Dialog(this);
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setContentView(R.layout.activity_order_sort_dialog);
            RecyclerView recyclerSort = dialog.findViewById(R.id.recyclerSort);
            recyclerSort.setHasFixedSize(true);
            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(activity);
            recyclerSort.setLayoutManager(linearLayoutManager);
            OrderSortAdapter adapter = new OrderSortAdapter(activity,list);
            recyclerSort.setAdapter(adapter);
            dialog.setCancelable(true);
            dialog.show();
            Window window = dialog.getWindow();
            window.setLayout(ActionBar.LayoutParams.MATCH_PARENT, ActionBar.LayoutParams.WRAP_CONTENT);

        }catch (Exception e){
            H.showMessage(activity,"No sort data found");
        }

    }

    private void loadSortData(JSONArray jsonArray){
        orderSortModelList.clear();
        if (jsonArray!=null && jsonArray.length()!=0){
            for (int i=0;i<jsonArray.length();i++){
                try {
                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                    OrderSortModel model = new OrderSortModel();
                    model.setKey(jsonObject.getInt(P.key));
                    model.setValue(jsonObject.getString(P.value));
                    if (jsonObject.getString(P.value).equalsIgnoreCase("Cancel Order")){
                        cancelKey = jsonObject.getInt(P.key);
                        cancelValue = jsonObject.getString(P.value);
                    }else {
                        orderSortModelList.add(model);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @Override
    public void onSort(int position, int sortValue, String name) {
        SORT_POSITION = position;
        sortName = name;
        dialog.dismiss();
        hitOrderDetailList(sortValue);
    }

    @Override
    public void cancelOrder(OrderDetailListModel model, TextView txtStatus,TextView txtCancel) {
        commentDialog(model,txtStatus,txtCancel);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.filter, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
        }else if (item.getItemId() == R.id.action_sort) {
            if (!orderSortModelList.isEmpty()){
                onSortClick();
            }else {
                H.showMessage(activity,"No sort data found");
            }
        }
        return false;
    }


    private void commentDialog(OrderDetailListModel model,TextView txtStatus,TextView txtCancelOrder) {

        Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.activity_comment_dialog);

        EditText etxComment = dialog.findViewById(R.id.etxComment);

        TextView txtCancel = dialog.findViewById(R.id.txtCancel);
        TextView txtSubmit = dialog.findViewById(R.id.txtSubmit);

        txtSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Click.preventTwoClick(v);
                String comment = etxComment.getText().toString().trim();
                if (!TextUtils.isEmpty(comment)) {
                    if (comment.length() > 10) {
                        dialog.cancel();
                        hitOrderCancelApi(model, txtStatus,txtCancelOrder, comment);
                    } else {
                        H.showMessage(activity, "Enter minimum 10 character");
                    }
                } else {
                    H.showMessage(activity, "Please enter comment");
                }
            }
        });

        txtCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Click.preventTwoClick(v);
                dialog.cancel();
            }
        });

        dialog.setCancelable(true);
        dialog.show();
        Window window = dialog.getWindow();
        window.setLayout(ActionBar.LayoutParams.MATCH_PARENT, ActionBar.LayoutParams.WRAP_CONTENT);
    }

    private void hitOrderCancelApi(OrderDetailListModel model,TextView txtStatus,TextView txtCancelOrder, String comment) {
        Json j = new Json();
        j.addInt(P.user_id, H.getInt(session.getString(P.user_id)));
        j.addString(P.order_number, model.getOrder_number());
        j.addString(P.order_status, "Cancelled");
        j.addString(P.order_status_comment, comment);

        Api.newApi(activity, P.baseUrl + "update_order_status").addJson(j)
                .setMethod(Api.POST)
                //.onHeaderRequest(App::getHeaders)
                .onError(() -> {
                    H.showMessage(activity, "On error is called");
                })
                .onSuccess(json ->
                {
                    if (json.getInt(P.status) == 1) {
                        H.showMessage(activity, "Order cancelled successfully");
                        model.setOrder_status("Cancelled");
                        txtStatus.setText("Order Cancelled");
                        txtStatus.setTextColor(getResources().getColor(R.color.red));
                        txtCancelOrder.setVisibility(View.GONE);
                    } else {
                        H.showMessage(activity, json.getString(P.msg));
                    }

                })
                .run("hitOrderCancelApi");
    }



    private void checkError(){
        if (orderDetailListModelList.isEmpty()){
            showError();
        }else {
            hideError();
        }
    }

    private void showProgress() {
        loadingDialog.show("Please wait..");
    }

    private void hideProgress() {
        loadingDialog.hide();
    }

    private void showError(){
        binding.lnrError.setVisibility(View.VISIBLE);
        if (sortName.contains("Cancel Order")){
            binding.txtErrorMessage.setText("No Cancel Order Found");
        }else {
            binding.txtErrorMessage.setText("No Order Data Fount");
        }
    }

    private void hideError(){
        binding.lnrError.setVisibility(View.GONE);
    }

    @Override
    public void onBackPressed() {
        if (fromSuccessOrder) {
            Intent intent = new Intent(activity, BaseActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        } else {
            super.onBackPressed();
        }
    }
}