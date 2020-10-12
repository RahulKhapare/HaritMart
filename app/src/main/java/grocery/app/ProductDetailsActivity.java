package grocery.app;

import android.content.Intent;
import android.graphics.Paint;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;

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

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import grocery.app.adapter.CategoryFilterAdapter;
import grocery.app.adapter.NewArrivalAdapter;
import grocery.app.adapter.SliderImageAdapter;
import grocery.app.common.App;
import grocery.app.common.P;
import grocery.app.databinding.ActivityProductDetailsBinding;
import grocery.app.model.ArrivalModel;
import grocery.app.model.CategoryFilterModel;
import grocery.app.model.SliderModel;
import grocery.app.util.Click;
import grocery.app.util.Config;
import grocery.app.util.WindowBarColor;

public class ProductDetailsActivity extends AppCompatActivity implements NewArrivalAdapter.ClickItem, CategoryFilterAdapter.ClickView {

    private ProductDetailsActivity activity = this;
    private ActivityProductDetailsBinding binding;
    private SliderImageAdapter sliderImageAdapter;
    private List<SliderModel> sliderModelList;
    private NewArrivalAdapter trendingAdapter;
    private NewArrivalAdapter popularAdapter;
    private CategoryFilterAdapter mainCategoryFilterAdapter;
    private CategoryFilterAdapter subCategoryFilterAdapter;
    private List<ArrivalModel> trendingDataList;
    private List<ArrivalModel> popularDataList;
    private List<CategoryFilterModel> mainCategoryFilterModelList;
    private List<CategoryFilterModel> subCategoryFilterModelList;
    private String filterId;
    private String producId;
    private Json optionJson = new Json();
    private LoadingDialog loadingDialog;
    private String rs = "₹ ";
    private JsonList arrivalJSON;
    private JsonList trendingJSON;
    private boolean firstCategoryCall = false;
    public static int mainCategoryFilterId ;
    public static int subCategoryFilterId ;
    private String addToCart = "Add To Cart";
    private String goToCart = "Go To Cart";
    private String wishListValue = "";

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
        firstCategoryCall = true;
        binding.btnCart.setText(addToCart);
        initView();

    }

    private void initView(){

        producId = getIntent().getStringExtra(Config.PRODUCT_ID);
        filterId = getIntent().getStringExtra(Config.PRODUCT_FILTER_ID);

        trendingDataList = new ArrayList<>();
        popularDataList = new ArrayList<>();
        mainCategoryFilterModelList = new ArrayList<>();
        subCategoryFilterModelList = new ArrayList<>();


        sliderModelList = new ArrayList<>();
        sliderImageAdapter = new SliderImageAdapter(activity, sliderModelList);
        binding.pager.setAdapter(sliderImageAdapter);
        binding.tabLayout.setupWithViewPager(binding.pager, true);

        binding.recyclerMainCategory.setHasFixedSize(true);
        binding.recyclerMainCategory.setLayoutManager(new LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL,false));
        mainCategoryFilterAdapter = new CategoryFilterAdapter(activity,mainCategoryFilterModelList,1,filterId,true);
        binding.recyclerMainCategory.setAdapter(mainCategoryFilterAdapter);

        binding.recyclerSubCategory.setHasFixedSize(true);
        binding.recyclerSubCategory.setLayoutManager(new LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL,false));
        subCategoryFilterAdapter = new CategoryFilterAdapter(activity,subCategoryFilterModelList,2,filterId,true);
        binding.recyclerSubCategory.setAdapter(subCategoryFilterAdapter);

        binding.recyclerTrending.setHasFixedSize(true);
        binding.recyclerTrending.setLayoutManager(new LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL,false));
        trendingAdapter = new NewArrivalAdapter(activity,trendingDataList,true);
        binding.recyclerTrending.setAdapter(trendingAdapter);

        binding.recyclerPopular.setHasFixedSize(true);
        binding.recyclerPopular.setLayoutManager(new LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL,false));
        popularAdapter = new NewArrivalAdapter(activity,popularDataList,true);
        binding.recyclerPopular.setAdapter(popularAdapter);

        onClick();
        hitProductDetailsApi(filterId,producId);
        hitHomeApi();
    }

    private void onClick(){
        binding.viewMoreTrending.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Click.preventTwoClick(v);
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
                Click.preventTwoClick(v);
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
                Click.preventTwoClick(v);
                if (!TextUtils.isEmpty(wishListValue) && wishListValue.equals("1")){
                    Json json = new Json();
                    json.addInt(P.user_id, Config.dummyID_1);
                    json.addInt(P.wishlist_id, Integer.parseInt(filterId));
                    hitRemoveToWishList(json,binding.imgLike,true);
                }else if (!TextUtils.isEmpty(wishListValue) && wishListValue.equals("0")){
                    Json json = new Json();
                    json.addInt(P.user_id, Config.dummyID_1);
                    json.addInt(P.product_filter_id, Integer.parseInt(filterId));
                    hitAddToWishList(json,binding.imgLike,true);
                }

            }
        });

        binding.imgShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Click.preventTwoClick(v);
            }
        });

        binding.btnCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Click.preventTwoClick(v);
                if (binding.btnCart.getText().toString().equals(addToCart)){
                    checkAddCart();
                }else if (binding.btnCart.getText().toString().equals(goToCart)){
                    Intent cartIntent = new Intent(activity,BaseActivity.class);
                    cartIntent.putExtra(Config.CHECK_CART_DATA,true);
                    startActivity(cartIntent);
                }
            }
        });

    }

    @Override
    public void itemClick(CategoryFilterModel model, int comingValue) {
        if (comingValue == 1){
            if (!TextUtils.isEmpty(model.getId())){
                mainCategoryFilterId = Integer.parseInt(model.getId());
                subCategoryFilterId = 0;
            }
            Json json = model.getValue();
            updateSubFilterData(json);
        }else if (comingValue == 2){
            if (!TextUtils.isEmpty(model.getId())){
                subCategoryFilterId = Integer.parseInt(model.getId());
            }
        }
    }


    @Override
    public void add(int filterId, ImageView imgAction) {
        Json json = new Json();
        json.addInt(P.user_id, Config.dummyID_1);
        json.addInt(P.product_filter_id, filterId);
        hitAddToWishList(json,imgAction,false);
    }

    @Override
    public void remove(int filterId, ImageView imgAction) {
        Json json = new Json();
        json.addInt(P.user_id, Config.dummyID_1);
        json.addInt(P.wishlist_id, filterId);
        hitRemoveToWishList(json,imgAction,false);
    }


    private void hitHomeApi() {
        showLoader();
        try {
            Json j = new Json();
            j.addInt(P.user_id, Config.dummyID_1);
            Api.newApi(activity, P.baseUrl + "home").addJson(j)
                    .setMethod(Api.POST)
                    //.onHeaderRequest(App::getHeaders)
                    .onError(() -> {
                        hideLoader();
                        H.showMessage(activity, "On error is called");
                    })
                    .onSuccess(json -> {
                        if (json.getInt(P.status) == 1) {
                            json = json.getJson(P.data);
                            App.homeJSONDATA = json;
                            App.product_image_path = json.getString(P.product_image_path);
                            setUpNewArrivedList(json.getString(P.product_image_path), json.getJsonList(P.latest_product_list));
                            setUpTrendingProductList(json.getString(P.product_image_path), json.getJsonList(P.trending_product_list));
                        } else {
                            H.showMessage(activity, json.getString(P.msg));
                        }
                        hideLoader();

                    })
                    .run("hitHomeApi");
        } catch (Exception e) {
            hideLoader();
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

        if (popularDataList.isEmpty()){
            binding.lnrNewArrived.setVisibility(View.GONE);
        }else {
            binding.lnrNewArrived.setVisibility(View.VISIBLE);
        }
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

        if (trendingDataList.isEmpty()){
            binding.lnrTrendingArrived.setVisibility(View.GONE);
        }else {
            binding.lnrTrendingArrived.setVisibility(View.VISIBLE);
        }
    }


    private void hitProductDetailsApi(String filterId,String productId) {

        showLoader();
        Json j = new Json();
        j.addInt(P.filter_id,Integer.parseInt(filterId));
        j.addInt(P.id, Integer.parseInt(productId));
        j.addInt(P.user_id, Config.dummyID_1);
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
                        wishListValue = json.getString(P.is_wishlisted);

                        if (wishListValue.equals("1")){
                            binding.imgLike.setImageDrawable(getResources().getDrawable(R.drawable.ic_baseline_favorite_2));
                        }else {
                            binding.imgLike.setImageDrawable(getResources().getDrawable(R.drawable.ic_baseline_favorite_1));
                        }

                        if (cartValue.equals("1")){
                            binding.btnCart.setText(goToCart);
                        }else {
                            binding.btnCart.setText(addToCart);
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
        mainCategoryFilterModelList.clear();
        binding.txtMainCategory.setText("");
        if (json!=null){
            binding.txtMainCategory.setText(json.getString(P.name));
            JSONArray  jsonArray = json.getJsonArray(P.value);
            if (jsonArray!=null && jsonArray.length()!=0){
                try {
                    for (int i=0; i<jsonArray.length(); i++){
                        JSONObject jsonObject = jsonArray.getJSONObject(i);
                        CategoryFilterModel model = new CategoryFilterModel();
                        model.setId(jsonObject.getString(P.id));
                        model.setName(jsonObject.getString(P.name));
                        model.setFilter_id(jsonObject.getString(P.filter_id));
                        try {
                            model.setValue(new Json(jsonObject.getString(P.value)));
                        }catch (Exception e){

                        }
                        mainCategoryFilterModelList.add(model);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }
        mainCategoryFilterAdapter.notifyDataSetChanged();

        if (mainCategoryFilterModelList.isEmpty()){
            binding.lnrMainCategory.setVisibility(View.GONE);
        }else {
            binding.lnrMainCategory.setVisibility(View.VISIBLE);
        }
    }

    private void updateSubFilterData(Json json){
        subCategoryFilterModelList.clear();
        binding.txtSubCategory.setText("");
        if (json!=null){
            binding.txtSubCategory.setText(json.getString(P.name));
            JSONArray jsonArray = json.getJsonArray(P.value);
            if (jsonArray!=null && jsonArray.length()!=0){
                try {
                    for (int i=0; i<jsonArray.length(); i++){
                        JSONObject jsonObject = jsonArray.getJSONObject(i);
                        CategoryFilterModel model = new CategoryFilterModel();
                        model.setId(jsonObject.getString(P.id));
                        model.setName(jsonObject.getString(P.name));
                        model.setFilter_id(jsonObject.getString(P.filter_id));
                        try {
                            model.setValue(new Json(jsonObject.getString(P.value)));
                        }catch (Exception e){

                        }
                        subCategoryFilterModelList.add(model);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }

        if (firstCategoryCall){
            firstCategoryCall = false;
            subCategoryFilterAdapter = new CategoryFilterAdapter(activity,subCategoryFilterModelList,2,filterId,true);
        }else {
            subCategoryFilterAdapter = new CategoryFilterAdapter(activity,subCategoryFilterModelList,2,"",true);
        }

        binding.recyclerSubCategory.setAdapter(subCategoryFilterAdapter);

        if (subCategoryFilterModelList.isEmpty()){
            binding.lnrSubCategory.setVisibility(View.GONE);
        }else {
            binding.lnrSubCategory.setVisibility(View.VISIBLE);
        }
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

    private void checkAddCart(){

        if (binding.lnrMainCategory.getVisibility()==View.VISIBLE && mainCategoryFilterId == 0){
            H.showMessage(activity,"Please Select " + binding.txtMainCategory.getText().toString());
            return;
        }

        if (binding.lnrSubCategory.getVisibility()==View.VISIBLE && subCategoryFilterId == 0){
            H.showMessage(activity,"Please Select " + binding.txtSubCategory.getText().toString());
            return;
        }

        Json j = new Json();
        j.addString(P.cart_token, new Session(this).getString(P.cart_token));
        j.addInt(P.user_id, Config.dummyID_1);
        j.addInt(P.product_filter_id,Integer.parseInt(filterId));
        j.addInt(P.quantity, 1);
        j.addInt(P.option1, mainCategoryFilterId);
        j.addInt(P.option2, subCategoryFilterId);
        j.addInt(P.option3, 0);
        hitAddToCartApi(j);

    }

    private void hitAddToCartApi(Json j) {
        showLoader();
        Api.newApi(activity, P.baseUrl + "add_to_cart").addJson(j)
                .setMethod(Api.POST)
                //.onHeaderRequest(App::getHeaders)
                .onError(() -> {
                    hideLoader();
                    H.showMessage(activity, "On error is called");
                })
                .onSuccess(json ->
                {
                    if (json.getInt(P.status) == 1) {
                        H.showMessage(activity, "Item added into favorite");
                        binding.btnCart.setText(goToCart);
                    } else{
                        H.showMessage(activity, json.getString(P.msg));
                    }
                    hideLoader();
                })
                .run("hitAddToCartApi");
    }

    private void hitAddToWishList(Json j,ImageView imgAction,boolean fivenFlag) {
        showLoader();
        Api.newApi(activity, P.baseUrl + "add_to_wishlist").addJson(j)
                .setMethod(Api.POST)
                //.onHeaderRequest(App::getHeaders)
                .onError(() -> {
                    hideLoader();
                    H.showMessage(activity, "On error is called");
                })
                .onSuccess(json ->
                {
                    if (json.getInt(P.status) == 1) {
                        H.showMessage(activity, "Item added into favorite");
                        if (fivenFlag){
                            imgAction.setImageDrawable(activity.getResources().getDrawable(R.drawable.ic_baseline_favorite_2));
                            wishListValue = "1";
                            hitHomeApi();
                        }else {
                            imgAction.setImageDrawable(activity.getResources().getDrawable(R.drawable.ic_baseline_remove_24));
                        }

                    } else{
                        H.showMessage(activity, json.getString(P.msg));
                    }
                    hideLoader();
                })
                .run("hitAddToWishList");
    }

    private void hitRemoveToWishList(Json j,ImageView imgAction, boolean fivenFlag) {
        showLoader();
        Api.newApi(activity, P.baseUrl + "remove_from_wishlist").addJson(j)
                .setMethod(Api.POST)
                //.onHeaderRequest(App::getHeaders)
                .onError(() -> {
                    hideLoader();
                    H.showMessage(activity, "On error is called");
                })
                .onSuccess(json ->
                {
                    if (json.getInt(P.status) == 1) {
                        H.showMessage(activity, "Item removed from favorite");
                        if (fivenFlag){
                            imgAction.setImageDrawable(activity.getResources().getDrawable(R.drawable.ic_baseline_favorite_1));
                            wishListValue = "0";
                            hitHomeApi();
                        }else {
                            imgAction.setImageDrawable(activity.getResources().getDrawable(R.drawable.ic_baseline_add_24));
                        }

                    } else{
                        H.showMessage(activity, json.getString(P.msg));
                    }
                    hideLoader();

                })
                .run("hitRemoveToWishList");
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