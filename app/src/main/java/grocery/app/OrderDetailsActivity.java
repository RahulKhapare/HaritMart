package grocery.app;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;

import org.json.JSONArray;
import org.json.JSONException;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import grocery.app.adapter.OrderAdapter;
import grocery.app.common.App;
import grocery.app.databinding.ActivityOrderDetailsBinding;
import grocery.app.model.OrderModel;
import grocery.app.model.OrderStatusMode;
import grocery.app.util.WindowBarColor;

public class OrderDetailsActivity extends AppCompatActivity implements View.OnClickListener {

    private OrderDetailsActivity activity = this;
    private ActivityOrderDetailsBinding binding;
    private String userRating;
    public static Integer truePosition;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        WindowBarColor.setColor(this);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_order_details);

        binding.toolbar.setTitle("Order Details");
        setSupportActionBar(binding.toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        initView();

    }

    private void initView(){

        List<OrderModel> orderModelList = new ArrayList<>();
        getOrderData(orderModelList);
        binding.recyclerOrderProduct.setHasFixedSize(true);
        binding.recyclerOrderProduct.setNestedScrollingEnabled(false);
        binding.recyclerOrderProduct.setLayoutManager(new LinearLayoutManager(activity));
        OrderAdapter orderAdapter = new OrderAdapter(activity,orderModelList);
        binding.recyclerOrderProduct.setAdapter(orderAdapter);

        binding.recyclerOrderStatus.setHasFixedSize(true);
        binding.recyclerOrderStatus.setLayoutManager(new LinearLayoutManager(activity,LinearLayoutManager.HORIZONTAL,false));

        binding.imgStar1.setOnClickListener(this);
        binding.imgStar2.setOnClickListener(this);
        binding.imgStar3.setOnClickListener(this);
        binding.imgStar4.setOnClickListener(this);
        binding.imgStar5.setOnClickListener(this);
        binding.txtReturnOrder.setOnClickListener(this);

    }

    private List<OrderStatusMode> getOrderTrackData(){
        truePosition = null;
        List<OrderStatusMode> list = new ArrayList<>();
        list.add(new OrderStatusMode("Stage 1"));
        list.add(new OrderStatusMode("Stage 2"));
        list.add(new OrderStatusMode("Stage 3"));
        list.add(new OrderStatusMode("Stage 4"));
        truePosition = 2;
        return list;
    }

    private void getOrderData(List<OrderModel> orderModelList){
        OrderModel model = new OrderModel();
        model.setQty("2");
        model.setTotal_price("100000");
        model.setPrice("500");
        model.setCoupon_discount_amount("50");
        orderModelList.add(model);
        orderModelList.add(model);
        orderModelList.add(model);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.imgStar1:
                updateRating(1);
                break;
            case R.id.imgStar2:
                updateRating(2);
                break;
            case R.id.imgStar3:
                updateRating(3);
                break;
            case R.id.imgStar4:
                updateRating(4);
                break;
            case R.id.imgStar5:
                updateRating(5);
                break;
            case R.id.txtReturnOrder:
                break;
        }
    }

    private void updateRating(int value){
        if (value==1){
            userRating = "1";
            binding.imgStar1.setImageDrawable(getResources().getDrawable(R.drawable.ic_baseline_star_green_24));
            binding.imgStar2.setImageDrawable(getResources().getDrawable(R.drawable.ic_baseline_star_gray_24));
            binding.imgStar3.setImageDrawable(getResources().getDrawable(R.drawable.ic_baseline_star_gray_24));
            binding.imgStar4.setImageDrawable(getResources().getDrawable(R.drawable.ic_baseline_star_gray_24));
            binding.imgStar5.setImageDrawable(getResources().getDrawable(R.drawable.ic_baseline_star_gray_24));
        }else if (value==2){
            userRating = "2";
            binding.imgStar1.setImageDrawable(getResources().getDrawable(R.drawable.ic_baseline_star_green_24));
            binding.imgStar2.setImageDrawable(getResources().getDrawable(R.drawable.ic_baseline_star_green_24));
            binding.imgStar3.setImageDrawable(getResources().getDrawable(R.drawable.ic_baseline_star_gray_24));
            binding.imgStar4.setImageDrawable(getResources().getDrawable(R.drawable.ic_baseline_star_gray_24));
            binding.imgStar5.setImageDrawable(getResources().getDrawable(R.drawable.ic_baseline_star_gray_24));
        }else if (value==3){
            userRating = "3";
            binding.imgStar1.setImageDrawable(getResources().getDrawable(R.drawable.ic_baseline_star_green_24));
            binding.imgStar2.setImageDrawable(getResources().getDrawable(R.drawable.ic_baseline_star_green_24));
            binding.imgStar3.setImageDrawable(getResources().getDrawable(R.drawable.ic_baseline_star_green_24));
            binding.imgStar4.setImageDrawable(getResources().getDrawable(R.drawable.ic_baseline_star_gray_24));
            binding.imgStar5.setImageDrawable(getResources().getDrawable(R.drawable.ic_baseline_star_gray_24));
        }else if (value==4){
            userRating = "4";
            binding.imgStar1.setImageDrawable(getResources().getDrawable(R.drawable.ic_baseline_star_green_24));
            binding.imgStar2.setImageDrawable(getResources().getDrawable(R.drawable.ic_baseline_star_green_24));
            binding.imgStar3.setImageDrawable(getResources().getDrawable(R.drawable.ic_baseline_star_green_24));
            binding.imgStar4.setImageDrawable(getResources().getDrawable(R.drawable.ic_baseline_star_green_24));
            binding.imgStar5.setImageDrawable(getResources().getDrawable(R.drawable.ic_baseline_star_gray_24));
        }else if (value==5){
            userRating = "5";
            binding.imgStar1.setImageDrawable(getResources().getDrawable(R.drawable.ic_baseline_star_green_24));
            binding.imgStar2.setImageDrawable(getResources().getDrawable(R.drawable.ic_baseline_star_green_24));
            binding.imgStar3.setImageDrawable(getResources().getDrawable(R.drawable.ic_baseline_star_green_24));
            binding.imgStar4.setImageDrawable(getResources().getDrawable(R.drawable.ic_baseline_star_green_24));
            binding.imgStar5.setImageDrawable(getResources().getDrawable(R.drawable.ic_baseline_star_green_24));
        }
    }

    private String formatedDate(String stringDate){
        String orderDate = stringDate;
        try {
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-mm-dd");
            Date date = dateFormat.parse(orderDate);
            orderDate = dateFormat.format(date);
        }catch (Exception e){
        }
        return orderDate;
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