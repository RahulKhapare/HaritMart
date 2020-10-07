package grocery.app;

import android.content.Intent;
import android.graphics.Paint;
import android.os.Bundle;
import android.os.Handler;
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

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import grocery.app.adapter.NewArrivalAdapter;
import grocery.app.adapter.QuantityAdapter;
import grocery.app.adapter.SliderImageAdapter;
import grocery.app.common.App;
import grocery.app.common.P;
import grocery.app.databinding.ActivityProductDetailsBinding;
import grocery.app.model.ArrivalModel;
import grocery.app.model.QuantityModel;
import grocery.app.model.SliderModel;
import grocery.app.util.Config;
import grocery.app.util.WindowBarColor;

public class ProductDetailsActivity extends AppCompatActivity implements NewArrivalAdapter.ClickItem, QuantityAdapter.ClickView {

    private ProductDetailsActivity activity = this;
    private ActivityProductDetailsBinding binding;
    private SliderImageAdapter sliderImageAdapter;
    private List<SliderModel> sliderModelList;
    private NewArrivalAdapter trendingAdapter;
    private NewArrivalAdapter popularAdapter;
    private QuantityAdapter quantityAdapter;
    private List<ArrivalModel> trendingDataList;
    private List<ArrivalModel> popularDataList;
    private List<QuantityModel> quantityModelList;
    private String filterId;
    private String producId;
    private Json optionJson = new Json();
    private LoadingDialog loadingDialog;
    private String rs = "₹ ";
    private JsonList arrivalJSON;
    private JsonList trendingJSON;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        WindowBarColor.setColor(this);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_product_details);

        binding.toolbar.setTitle("Product Details");
        setSupportActionBar(binding.toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        loadingDialog = new LoadingDialog(activity);
        initView();

    }

    private void initView(){

        producId = getIntent().getStringExtra(Config.PRODUCT_ID);
        filterId = getIntent().getStringExtra(Config.PRODUCT_FILTER_ID);

        trendingDataList = new ArrayList<>();
        popularDataList = new ArrayList<>();
        quantityModelList = new ArrayList<>();

        quantityModelList.add(new QuantityModel("1Kg"));
        quantityModelList.add(new QuantityModel("2Kg"));
        quantityModelList.add(new QuantityModel("3Kg"));
        quantityModelList.add(new QuantityModel("4Kg"));

        sliderModelList = new ArrayList<>();
        sliderImageAdapter = new SliderImageAdapter(activity, sliderModelList);
        binding.pager.setAdapter(sliderImageAdapter);
        binding.tabLayout.setupWithViewPager(binding.pager, true);

        binding.recyclerQuantityView.setHasFixedSize(true);
        binding.recyclerQuantityView.setLayoutManager(new LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL,false));
        quantityAdapter = new QuantityAdapter(activity,quantityModelList);
        binding.recyclerQuantityView.setAdapter(quantityAdapter);

        binding.recyclerTrending.setHasFixedSize(true);
        binding.recyclerTrending.setLayoutManager(new LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL,false));
        trendingAdapter = new NewArrivalAdapter(activity,trendingDataList,true);
        binding.recyclerTrending.setAdapter(trendingAdapter);

        binding.recyclerPopular.setHasFixedSize(true);
        binding.recyclerPopular.setLayoutManager(new LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL,false));
        popularAdapter = new NewArrivalAdapter(activity,popularDataList,true);
        binding.recyclerPopular.setAdapter(popularAdapter);

        onClick();
        setupProductListData();
        hitProductDetailsApi(filterId,producId);

    }

    private void onClick(){
        binding.viewMoreTrending.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(activity, ProductChildListActivity.class);
                intent.putExtra(Config.TITLE, "Trending Arrived");
                intent.putExtra(Config.CHILD_POSITION, 0);
                intent.putExtra(Config.CHILD_JSON, trendingJSON + "");
                Config.FROM_HOME = true;
                startActivity(intent);
            }
        });

        binding.viewMorePopular.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(activity, ProductChildListActivity.class);
                intent.putExtra(Config.TITLE, "New Arrived");
                intent.putExtra(Config.CHILD_POSITION, 0);
                intent.putExtra(Config.CHILD_JSON, arrivalJSON + "");
                Config.FROM_HOME = true;
                startActivity(intent);
            }
        });

        binding.imgLike.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        binding.imgShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

    }

    @Override
    public void add(int filterId) {

    }

    @Override
    public void itemClick(int position, String id) {

    }

    private void setupProductListData(){
        try {
            Json json = App.homeJSONDATA;
            setUpNewArrivedList(json.getString(P.product_image_path), json.getJsonList(P.latest_product_list));
            setUpTrendingProductList(json.getString(P.product_image_path), json.getJsonList(P.trending_product_list));
        }catch (Exception e){
        }
    }

    private void setUpNewArrivedList(String string, JsonList jsonList) {
        arrivalJSON = jsonList;
        popularDataList.clear();
        for (Json json : jsonList) {
            ArrivalModel model = new ArrivalModel();
            model.setCategory_name(json.getString(P.category_name));
            model.setFilter_id(json.getString(P.filter_id));
            model.setId(json.getString(P.id));
            model.setName(json.getString(P.name));
            model.setIs_wishlisted(json.getString(P.is_wishlisted));
            model.setProduct_image(P.imgBaseUrl + string + json.getString(P.product_image));
            popularDataList.add(model);
        }
        popularAdapter.notifyDataSetChanged();
    }

    private void setUpTrendingProductList(String string, JsonList jsonList) {
        trendingJSON = jsonList;
        trendingDataList.clear();
        for (Json json : jsonList) {
            ArrivalModel model = new ArrivalModel();
            model.setCategory_name(json.getString(P.category_name));
            model.setFilter_id(json.getString(P.filter_id));
            model.setId(json.getString(P.id));
            model.setName(json.getString(P.name));
            model.setIs_wishlisted(json.getString(P.is_wishlisted));
            model.setProduct_image(P.imgBaseUrl + string + json.getString(P.product_image));
            trendingDataList.add(model);
        }
        trendingAdapter.notifyDataSetChanged();
    }


    private void hitProductDetailsApi(String filterId,String productId) {

        showLoader();
        Json j = new Json();
        j.addInt(P.filter_id,Integer.parseInt(filterId));
        j.addInt(P.id, Integer.parseInt(productId));
        j.addInt(P.user_id, 1);
        j.addString(P.cart_token, new Session(this).getString(P.cart_token));
        j.addJSON(P.option, optionJson);

        Api.newApi(this, P.baseUrl + "product_detail").addJson(j)
                .setMethod(Api.POST)
                //.onHeaderRequest(App::getHeaders)
                .onError(() -> {
                    hideLoader();
                    H.showMessage(this, "On error is called");
                })
                .onSuccess(json ->
                {
                    if (json.getInt(P.status) == 1) {
                        json = json.getJson(P.data);
                        setUpTopSlider(json);
                        setUpDetailsFilter(json.getJson(P.option));

                        binding.txtTitle.setText(json.getString(P.name));
                        binding.txtAmount.setText(json.getString(P.name));

                        if (!TextUtils.isEmpty(json.getString(P.description))){
                            binding.txtAbout.setText(json.getString(P.description));
                        }else {
                            binding.txtAbout.setText("No details found");
                        }

                        if (!TextUtils.isEmpty(json.getString(P.description1))){
                            binding.txtFact.setText(json.getString(P.description1));
                        }else {
                            binding.txtFact.setText("No details found");
                        }

                        try {
                            Json priceJson = json.getJson(P.price);
                            priceJson.getString(P.saleprice);
                            priceJson.getString(P.discount_amount);
                            priceJson.getString(P.discount);

                            binding.txtAmount.setText(rs + priceJson.getString(P.saleprice));
                            binding.txtProductOff.setText(rs + priceJson.getString(P.price));
                            binding.txtProductOff.setPaintFlags(binding.txtProductOff.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);

                            String offValue = "";
                            if (!TextUtils.isEmpty(priceJson.getString(P.price)) && !TextUtils.isEmpty(priceJson.getString(P.saleprice))){
                                int actualValue = Integer.parseInt(priceJson.getString(P.price));
                                int discountValue = Integer.parseInt(priceJson.getString(P.saleprice));
                                try {
                                    DecimalFormat df = new DecimalFormat("0.00");
                                    offValue = df.format(discountPercentage(discountValue,actualValue));
                                }catch (Exception e){
                                    offValue = "0";
                                }
                            }
                            binding.txtPercent.setText(offValue + "% OFF");
                        }catch (Exception e){

                        }

                        String cartValue = json.getString(P.is_added_in_cart);
                        String wishListValue = json.getString(P.is_wishlisted);

                        if (wishListValue.equals("1")){
                            binding.imgLike.setImageDrawable(getResources().getDrawable(R.drawable.ic_baseline_favorite_2));
                        }else {
                            binding.imgLike.setImageDrawable(getResources().getDrawable(R.drawable.ic_baseline_favorite_1));
                        }

                        if (cartValue.equals("1")){
                            binding.btnCart.setText("Go to cart");
                        }else {
                            binding.btnCart.setText("Add to cart");
                        }

                    } else{
                        H.showMessage(this, json.getString(P.msg));
                    }
                    hideLoader();
                })
                .run("hitProductDetailsApi");

    }

    private void setUpTopSlider(Json json) {
        sliderModelList.clear();
        String string = json.getString(P.product_image_path);
        JSONArray jsonArray = json.getJsonArray(P.all_images);
        for (int i = 0; i < jsonArray.length(); i++) {
            try {
                SliderModel model = new SliderModel();
                model.setImage(P.imgBaseUrl + string + jsonArray.get(i));
                sliderModelList.add(model);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        loadSliderData(sliderModelList);
    }

    private void setUpDetailsFilter(Json json){
        H.showMessage(activity,json + "");
    }


    private void loadSliderData(List<SliderModel> sliderModelList) {
        sliderImageAdapter.notifyDataSetChanged();

        Handler handler = new Handler();
        Runnable runnable = null;

        if (runnable != null)
            handler.removeCallbacks(runnable);
        runnable = new Runnable() {
            @Override
            public void run() {
                handler.postDelayed(this, 3000);
                if (binding.pager.getCurrentItem() == sliderImageAdapter.getCount() - 1)
                    binding.pager.setCurrentItem(0);
                else
                    binding.pager.setCurrentItem(binding.pager.getCurrentItem() + 1);
            }
        };
        runnable.run();
    }

    private float discountPercentage(float S, float M)
    {
        // Calculating discount
        float discount = M - S;

        // Calculating discount percentage
        float disPercent = (discount / M) * 100;

        return disPercent;
    }

    private void showLoader(){
        loadingDialog.show("Please wait...");
    }

    private void hideLoader(){
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