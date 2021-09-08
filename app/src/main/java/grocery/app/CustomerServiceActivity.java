package grocery.app;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.adoisstudio.helper.Session;

import java.util.ArrayList;
import java.util.List;

import grocery.app.Fragments.SearchFragment;
import grocery.app.adapter.OrderAdapter;
import grocery.app.adapter.PopularTopicAdapter;
import grocery.app.adapter.SearchAdapter;
import grocery.app.databinding.ActivityCustomerServiceBinding;
import grocery.app.model.OrderModel;
import grocery.app.model.PopularModel;
import grocery.app.model.SearchModel;
import grocery.app.util.WindowBarColor;

public class CustomerServiceActivity extends AppCompatActivity {

    private CustomerServiceActivity activity = this;
    private ActivityCustomerServiceBinding binding;
    private Session session;

    private String rs = "₹ ";
    private List<OrderModel> orderModelList;
    private OrderAdapter orderAdapter;
    private int orderSize = 0;

    private List<PopularModel> popularModelList;
    private PopularTopicAdapter popularTopicAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        WindowBarColor.setColor(this);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_customer_service);

        binding.toolbar.setTitle("Customer Service");
        setSupportActionBar(binding.toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        initView();
    }

    private void initView() {

        session = new Session(activity);

        orderModelList = new ArrayList<>();
        binding.recyclerOrderProduct.setHasFixedSize(true);
        binding.recyclerOrderProduct.setNestedScrollingEnabled(false);
        binding.recyclerOrderProduct.setLayoutManager(new LinearLayoutManager(activity));

        popularModelList = new ArrayList<>();
        binding.recyclerPopularTopic.setHasFixedSize(true);
        binding.recyclerPopularTopic.setNestedScrollingEnabled(false);
        binding.recyclerPopularTopic.setLayoutManager(new LinearLayoutManager(activity));

        setOrderData();
        setPopularData();
        onSearchView();

    }

    private void onSearchView(){
        binding.etxSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,int after) {
            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String newText = s.toString();
//                if (!TextUtils.isEmpty(newText)) {
//                    List<OrderModel> list = new ArrayList<OrderModel>();
//                    for (OrderModel model : orderModelList) {
//                        if (model.getName().toLowerCase().contains(newText.toLowerCase())) {
//                            list.add(model);
//                        }
//                    }
//                    orderAdapter = new OrderAdapter(activity, list,orderSize);
//                    binding.recyclerOrderProduct.setAdapter(orderAdapter);
//                    orderAdapter.notifyDataSetChanged();
//                } else {
//                    orderAdapter = new OrderAdapter(activity, orderModelList,orderSize);
//                    binding.recyclerOrderProduct.setAdapter(orderAdapter);
//                }
            }
            @Override
            public void afterTextChanged(Editable s) {
            }
        });
    }

    private void setOrderData(){

        OrderModel model = new OrderModel();
        model.setPrice("35");
        model.setTotal_price("50");
        model.setName("Onion-Medium");
        model.setStatus("Delivered");
        model.setDate("2020-06-18");
        model.setLabel1("Fruits Seasonal");
        model.setValue1("Winter Veggies");

        orderModelList.add(model);
        orderModelList.add(model);
        orderModelList.add(model);
        orderModelList.add(model);
        orderModelList.add(model);

        if(orderModelList.size()>2){
            orderSize = 2;
            orderAdapter = new OrderAdapter(activity, orderModelList,2);
            binding.recyclerOrderProduct.setAdapter(orderAdapter);
            binding.lnrShowMore.setVisibility(View.VISIBLE);
        }else {
            orderSize = orderModelList.size();
            orderAdapter = new OrderAdapter(activity, orderModelList,orderModelList.size());
            binding.recyclerOrderProduct.setAdapter(orderAdapter);
            binding.lnrShowMore.setVisibility(View.GONE);
        }

        binding.lnrShowMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                binding.lnrShowMore.setVisibility(View.GONE);
                orderSize = orderModelList.size();
                orderAdapter = new OrderAdapter(activity, orderModelList,orderModelList.size());
                binding.recyclerOrderProduct.setAdapter(orderAdapter);
            }
        });
    }

    private void setPopularData(){

        popularModelList.add(new PopularModel("I am not happy with the quality of product"));
        popularModelList.add(new PopularModel("I want to contact customer service"));
        popularModelList.add(new PopularModel("I want to change the delivery slot"));
        popularModelList.add(new PopularModel("I am unable to log in/ sign up"));
        popularModelList.add(new PopularModel("I am not happy with the quality of product"));
        popularModelList.add(new PopularModel("I want to contact customer service"));
        popularModelList.add(new PopularModel("I want to change the delivery slot"));
        popularModelList.add(new PopularModel("I am unable to log in/ sign up"));

        if(popularModelList.size()>4){
            popularTopicAdapter = new PopularTopicAdapter(activity, popularModelList,4);
            binding.recyclerPopularTopic.setAdapter(popularTopicAdapter);
            binding.lnrShowMorePopular.setVisibility(View.VISIBLE);
        }else {
            popularTopicAdapter = new PopularTopicAdapter(activity, popularModelList,popularModelList.size());
            binding.recyclerPopularTopic.setAdapter(popularTopicAdapter);
            binding.lnrShowMorePopular.setVisibility(View.GONE);
        }

        binding.lnrShowMorePopular.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                binding.lnrShowMorePopular.setVisibility(View.GONE);
                popularTopicAdapter = new PopularTopicAdapter(activity, popularModelList,popularModelList.size());
                binding.recyclerPopularTopic.setAdapter(popularTopicAdapter);
            }
        });
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