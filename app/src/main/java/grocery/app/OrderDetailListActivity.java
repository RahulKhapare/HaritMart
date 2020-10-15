package grocery.app;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ActionBar;
import android.app.Dialog;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;

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

import grocery.app.adapter.OrderDetailListAdapter;
import grocery.app.adapter.OrderSortAdapter;
import grocery.app.common.P;
import grocery.app.databinding.ActivityOrderDetailListBinding;
import grocery.app.model.OrderDetailListModel;
import grocery.app.model.OrderSortModel;
import grocery.app.util.Config;
import grocery.app.util.WindowBarColor;

public class OrderDetailListActivity extends AppCompatActivity implements OrderSortAdapter.SortClick{

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
        loadingDialog = new LoadingDialog(activity);
        orderDetailListModelList = new ArrayList<>();
        orderSortModelList = new ArrayList<>();
        binding.recyclerOrderDetailList.setLayoutManager(new LinearLayoutManager(activity));
        binding.recyclerOrderDetailList.setHasFixedSize(true);
        binding.recyclerOrderDetailList.setNestedScrollingEnabled(false);
        adapter = new OrderDetailListAdapter(activity,orderDetailListModelList);
        binding.recyclerOrderDetailList.setAdapter(adapter);
//        hitOrderDetailList();
        setDummyData();
    }

    private void hitOrderDetailList(int sortId) {
        orderDetailListModelList.clear();
        showProgress();
        Json j = new Json();
        j.addInt(P.user_id, Config.dummyID_1);
        j.addString(P.cart_token, new Session(activity).getString(P.cart_token));
        Api.newApi(activity, P.baseUrl + "").addJson(j)
                .setMethod(Api.POST)
                //.onHeaderRequest(App::getHeaders)
                .onError(() -> {
                    hideProgress();
                    checkError();
                    H.showMessage(activity, "On error is called");
                })
                .onSuccess(json ->
                {
                    if (json.getInt(P.status) == 1) {
                        json = json.getJson(P.data);
                    }
                    hideProgress();
                    checkError();
                })
                .run("hitOrderDetailList");
    }

    private void setDummyData(){

        OrderDetailListModel model = new OrderDetailListModel();
        model.setOrderId("001");
        model.setOrderNo("#009933");
        model.setOrderDate("00/00/0000");
        model.setOrderAmount("00000");
        model.setPaymentBy("PhonePe");
        model.setTotalAmount("00000000");

        orderDetailListModelList.add(model);

        adapter.notifyDataSetChanged();

        checkError();
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
//        hitOrderDetailList(sortValue);
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
    }

    private void hideError(){
        binding.lnrError.setVisibility(View.GONE);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

}