package grocery.app;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Paint;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
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
import grocery.app.util.ConnectionUtil;
import grocery.app.util.LoginFlag;
import grocery.app.util.PageUtil;
import grocery.app.util.WindowBarColor;

public class ProductDetailsActivity extends AppCompatActivity implements NewArrivalAdapter.ClickItem, CategoryFilterAdapter.ClickView {

    private ProductDetailsActivity activity = this;
    private ActivityProductDetailsBinding binding;
    private SliderImageAdapter sliderImageAdapter;
    private List<SliderModel> sliderModelList;
    private NewArrivalAdapter specialOfferAdapter;
    private NewArrivalAdapter newProductAdapter;
    private NewArrivalAdapter trendingProductAdapter;
    private CategoryFilterAdapter mainCategoryFilterAdapter;
    private CategoryFilterAdapter subCategoryFilterAdapter;
    private List<ArrivalModel> specialOfferProductList;
    private List<ArrivalModel> newProductList;
    private List<ArrivalModel> trendingProductList;
    private List<CategoryFilterModel> mainCategoryFilterModelList;
    private List<CategoryFilterModel> subCategoryFilterModelList;
    private String filterId;
    private String producId;
    private String description;
    private Json optionJson = new Json();
    private LoadingDialog loadingDialog;
    private String rs = "₹ ";
    private JsonList specialProductJSON;
    private JsonList newProductJSON;
    private JsonList trendingProductJSON;
    private boolean firstCategoryCall = false;
    public static int mainCategoryFilterId ;
    public static int subCategoryFilterId ;
    private String addToCart = "Add To Cart";
    private String goToCart = "Go To Cart";
    private String wishListValue = "";
    private Session session;

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
        session = new Session(activity);
        firstCategoryCall = true;
        binding.btnCart.setText(addToCart);
        initView();

    }

    private void initView(){

        producId = getIntent().getStringExtra(Config.PRODUCT_ID);
        filterId = getIntent().getStringExtra(Config.PRODUCT_FILTER_ID);

        specialOfferProductList = new ArrayList<>();
        newProductList = new ArrayList<>();
        trendingProductList = new ArrayList<>();
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

        binding.recyclerSpecialProduct.setHasFixedSize(true);
        binding.recyclerSpecialProduct.setLayoutManager(new LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL,false));
        specialOfferAdapter = new NewArrivalAdapter(activity,specialOfferProductList,true);
        binding.recyclerSpecialProduct.setAdapter(specialOfferAdapter);

        binding.recyclerNewProduct.setHasFixedSize(true);
        binding.recyclerNewProduct.setLayoutManager(new LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL,false));
        newProductAdapter = new NewArrivalAdapter(activity,newProductList,true);
        binding.recyclerNewProduct.setAdapter(newProductAdapter);

        binding.recyclerTrendingProduct.setHasFixedSize(true);
        binding.recyclerTrendingProduct.setLayoutManager(new LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL,false));
        trendingProductAdapter = new NewArrivalAdapter(activity,trendingProductList,true);
        binding.recyclerTrendingProduct.setAdapter(trendingProductAdapter);

        onClick();
        hitProductDetailsApi(filterId,producId);
        hitHomeApi();

    }

    @Override
    protected void onResume() {
        super.onResume();
        if (Config.Update_Favorite_Home){
//            hitHomeApi();
        }
    }

    private void onClick(){
        binding.txtSpecialNew.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Click.preventTwoClick(v);
                Intent intent = new Intent(activity, ProductChildListActivity.class);
                intent.putExtra(Config.TITLE, Config.SpecialProductArrived);
                intent.putExtra(Config.CHILD_POSITION, 0);
                intent.putExtra(Config.CHILD_JSON, specialProductJSON + "");
                Config.FROM_HOME = true;
                startActivity(intent);
            }
        });

        binding.txtViewNew.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Click.preventTwoClick(v);
                Intent intent = new Intent(activity, ProductChildListActivity.class);
                intent.putExtra(Config.TITLE, Config.NewProductArrived);
                intent.putExtra(Config.CHILD_POSITION, 0);
                intent.putExtra(Config.CHILD_JSON, newProductJSON + "");
                Config.FROM_HOME = true;
                startActivity(intent);
            }
        });

        binding.txtViewTrending.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Click.preventTwoClick(v);
                Intent intent = new Intent(activity, ProductChildListActivity.class);
                intent.putExtra(Config.TITLE, Config.TrendingProductArrived);
                intent.putExtra(Config.CHILD_POSITION, 0);
                intent.putExtra(Config.CHILD_JSON, trendingProductJSON + "");
                Config.FROM_HOME = true;
                startActivity(intent);
            }
        });

        binding.imgLike.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Click.preventTwoClick(v);
                if (ConnectionUtil.isOnline(activity)){
                    if (session.getBool(P.isUserLogin)){
                        Json json = new Json();
                        json.addInt(P.user_id, H.getInt(session.getString(P.user_id)));
                        json.addInt(P.product_filter_id, Integer.parseInt(filterId));
                        hitAddToWishList(json,binding.imgLike,true);
                    }else {
                        LoginFlag.loginProductId = producId;
                        LoginFlag.loginFilterId = filterId;
                        PageUtil.goToLoginPage(activity,LoginFlag.productDetailFlagValue);
                    }
                }else {
                    getResources().getString(R.string.internetMessage);
                }
            }
        });

        binding.imgShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Click.preventTwoClick(v);
                Intent intent = new Intent(Intent.ACTION_SEND);
                intent.setType("text/plain");
                intent.putExtra(Intent.EXTRA_TEXT, Config.PAGE_LINK + filterId);
                startActivity(Intent.createChooser(intent, "Share Using"));
            }
        });

        binding.btnCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Click.preventTwoClick(v);

//                if (session.getBool(P.isUserLogin)){
//                    if (binding.btnCart.getText().toString().equals(addToCart)){
//                        checkAddCart();
//                    }else if (binding.btnCart.getText().toString().equals(goToCart)){
//                        Intent cartIntent = new Intent(activity,BaseActivity.class);
//                        cartIntent.putExtra(Config.CHECK_CART_DATA,true);
//                        startActivity(cartIntent);
//                    }
//                }else {
//                    LoginFlag.loginProductId = producId;
//                    LoginFlag.loginFilterId = filterId;
//                    PageUtil.goToLoginPage(activity,LoginFlag.productDetailFlagValue);
//                }

                if (binding.btnCart.getText().toString().equals(addToCart)){
                    checkAddCart();
                }else if (binding.btnCart.getText().toString().equals(goToCart)){
                    Intent cartIntent = new Intent(activity,BaseActivity.class);
                    cartIntent.putExtra(Config.CHECK_CART_DATA_FLAG,true);
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
        if (session.getBool(P.isUserLogin)){
            json.addInt(P.user_id, H.getInt(session.getString(P.user_id)));
        }
        json.addInt(P.product_filter_id, filterId);
//        hitAddToWishList(json,imgAction,false);
    }


    private void hitHomeApi() {
        showLoader();
        try {
            Json j = new Json();
            if (session.getBool(P.isUserLogin)){
                j.addInt(P.user_id, H.getInt(session.getString(P.user_id)));
            }else {
                j.addInt(P.user_id, Config.commonUserHomeID);
            }
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
                            setSpecialOfferProductList(json.getJsonList(P.special_offer_product));
                            setNewOfferProductList(json.getJsonList(P.new_product));
                            setTrendingOfferProductList(json.getJsonList(P.under_50_product));
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


    private void setSpecialOfferProductList(JsonList jsonList) {
        specialProductJSON = jsonList;
        specialOfferProductList.clear();
        for (Json json : jsonList) {
            ArrivalModel model = new ArrivalModel();
            model.setId(json.getString(P.id));
            model.setFilter_id(json.getString(P.filter_id));
            model.setName(json.getString(P.name));
            model.setSlug(json.getString(P.slug));
            model.setVariants_name(json.getString(P.variants_name));
            model.setProduct_image(json.getString(P.product_image));
            try {
                Json priceJson = json.getJson(P.price);
                model.setPrice(priceJson.getString(P.price));
                model.setSaleprice(priceJson.getString(P.saleprice));
                model.setDiscount_amount(priceJson.getString(P.discount_amount));
                model.setDiscount(priceJson.getString(P.discount));
            }catch (Exception e){

            }

            if (!model.getFilter_id().equals(filterId)){
                specialOfferProductList.add(model);
            }

        }
        specialOfferAdapter.notifyDataSetChanged();

        if (jsonList.size()==0){
            binding.lnrSpecialProduct.setVisibility(View.GONE);
        }else {
            binding.lnrSpecialProduct.setVisibility(View.VISIBLE);
        }
    }

    private void setNewOfferProductList(JsonList jsonList) {
        newProductJSON = jsonList;
        newProductList.clear();
        for (Json json : jsonList) {
            ArrivalModel model = new ArrivalModel();
            model.setId(json.getString(P.id));
            model.setFilter_id(json.getString(P.filter_id));
            model.setName(json.getString(P.name));
            model.setSlug(json.getString(P.slug));
            model.setVariants_name(json.getString(P.variants_name));
            model.setProduct_image(json.getString(P.product_image));
            try {
                Json priceJson = json.getJson(P.price);
                model.setPrice(priceJson.getString(P.price));
                model.setSaleprice(priceJson.getString(P.saleprice));
                model.setDiscount_amount(priceJson.getString(P.discount_amount));
                model.setDiscount(priceJson.getString(P.discount));
            }catch (Exception e){

            }

            if (!model.getFilter_id().equals(filterId)){
                newProductList.add(model);
            }

        }
        newProductAdapter.notifyDataSetChanged();

        if (jsonList.size()==0){
            binding.lnrNewProduct.setVisibility(View.GONE);
        }else {
            binding.lnrNewProduct.setVisibility(View.VISIBLE);
        }
    }

    private void setTrendingOfferProductList(JsonList jsonList) {
        trendingProductJSON = jsonList;
        trendingProductList.clear();
        for (Json json : jsonList) {
            ArrivalModel model = new ArrivalModel();
            model.setId(json.getString(P.id));
            model.setFilter_id(json.getString(P.filter_id));
            model.setName(json.getString(P.name));
            model.setSlug(json.getString(P.slug));
            model.setVariants_name(json.getString(P.variants_name));
            model.setProduct_image(json.getString(P.product_image));
            try {
                Json priceJson = json.getJson(P.price);
                model.setPrice(priceJson.getString(P.price));
                model.setSaleprice(priceJson.getString(P.saleprice));
                model.setDiscount_amount(priceJson.getString(P.discount_amount));
                model.setDiscount(priceJson.getString(P.discount));
            }catch (Exception e){

            }

            if (!model.getFilter_id().equals(filterId)){
                trendingProductList.add(model);
            }

        }
        trendingProductAdapter.notifyDataSetChanged();

        if (jsonList.size()==0){
            binding.lnrTrendingProduct.setVisibility(View.GONE);
        }else {
            binding.lnrTrendingProduct.setVisibility(View.VISIBLE);
        }
    }

    private void hitProductDetailsApi(String filterId,String productId) {

        showLoader();
        Json j = new Json();
        j.addInt(P.filter_id,Integer.parseInt(filterId));
        j.addInt(P.id, Integer.parseInt(productId));
        if (session.getBool(P.isUserLogin)){
            j.addInt(P.user_id, H.getInt(session.getString(P.user_id)));
        }else {
            j.addInt(P.user_id, Config.commonUserID);
        }
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
        if (session.getBool(P.isUserLogin)){
            j.addInt(P.user_id, H.getInt(session.getString(P.user_id)));
        }else {
            j.addInt(P.user_id, Config.commonUserID);
        }
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
                        H.showMessage(activity, "Item added into cart");
                        binding.btnCart.setText(goToCart);
                    } else{
                        H.showMessage(activity, json.getString(P.msg));
                    }
                    hideLoader();
                })
                .run("hitAddToCartApi");
    }

    private void hitAddToWishList(Json j,ImageView imgAction,boolean givenFlag) {
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
                        if (givenFlag){
                            if (json.getString(P.msg).equals("wishlisted")){
                                H.showMessage(activity, "Item added into favorite");
                                imgAction.setImageDrawable(activity.getResources().getDrawable(R.drawable.ic_baseline_favorite_2));
                            }else if (json.getString(P.msg).equals("notwishlisted")){
                                H.showMessage(activity, "Item removed from favorite");
                                imgAction.setImageDrawable(activity.getResources().getDrawable(R.drawable.ic_baseline_favorite_1));
                            }
                            Config.Update_Favorite_List = true;
//                            hitHomeApi();
                        }else {
                            if (json.getString(P.msg).equals("wishlisted")){
                                H.showMessage(activity, "Item added into favorite");
                                imgAction.setImageDrawable(activity.getResources().getDrawable(R.drawable.ic_baseline_remove_24));
                            }else if (json.getString(P.msg).equals("notwishlisted")){
                                H.showMessage(activity, "Item removed from favorite");
                                imgAction.setImageDrawable(activity.getResources().getDrawable(R.drawable.ic_baseline_add_24));
                            }
                            Config.Update_Favorite_Home = true;
                        }
                        Config.Update_Favorite_Wish = true;

                        if (Config.Update_Direct_Home){
                            Config.Update_Direct_Home = false;
                            Config.Update_Favorite_Home = true;
                        }

                    } else{
                        H.showMessage(activity, json.getString(P.msg));
                    }
                    hideLoader();
                })
                .run("hitAddToWishList");
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