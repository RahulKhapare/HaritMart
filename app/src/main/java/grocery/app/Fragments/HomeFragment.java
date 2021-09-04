package grocery.app.Fragments;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.adoisstudio.helper.Api;
import com.adoisstudio.helper.H;
import com.adoisstudio.helper.Json;
import com.adoisstudio.helper.JsonList;
import com.adoisstudio.helper.LoadingDialog;
import com.adoisstudio.helper.Session;

import java.util.ArrayList;
import java.util.List;

import grocery.app.BaseActivity;
import grocery.app.ProductCategoryActivity;
import grocery.app.ProductChildListActivity;
import grocery.app.R;
import grocery.app.adapter.NewArrivalAdapter;
import grocery.app.adapter.ProductCategoryAdapter;
import grocery.app.adapter.SliderImageAdapter;
import grocery.app.common.App;
import grocery.app.common.P;
import grocery.app.databinding.FragmentHomeBinding;
import grocery.app.model.ArrivalModel;
import grocery.app.model.ProductModel;
import grocery.app.model.SliderModel;
import grocery.app.util.Config;

public class HomeFragment extends Fragment implements ProductCategoryAdapter.ItemClick, NewArrivalAdapter.ClickItem {

    SliderImageAdapter sliderImageAdapter;
    private Context context;
    private LoadingDialog loadingDialog;
    private FragmentHomeBinding binding;
    private List<ProductModel> productModelList;
    private List<ArrivalModel> specialOfferProductList;
    private List<ArrivalModel> newProductList;
    private List<ArrivalModel> trendingProductList;
    private ProductCategoryAdapter adapter;
    private NewArrivalAdapter specialOfferAdapter;
    private NewArrivalAdapter newProductAdapter;
    private NewArrivalAdapter trendingProductAdapter;
    private List<SliderModel> sliderModelList;
    private JsonList specialProductJSON;
    private JsonList newProductJSON;
    private JsonList trendingProductJSON;
    private Session session;

    public static HomeFragment newInstance() {
        HomeFragment fragment = new HomeFragment();

        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        if (binding == null) {
            binding = DataBindingUtil.inflate(inflater, R.layout.fragment_home, container, false);
            context = getContext();
            loadingDialog = new LoadingDialog(context);
            session = new Session(context);
            initProductView();
            hitCategoryApi();
            hitHomeApi();
            onClickItemView();
        }

        return binding.getRoot();
    }

    private void onClickItemView() {

        binding.viewMoreExplorer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent categoryIntent = new Intent(context, ProductCategoryActivity.class);
                categoryIntent.putExtra(Config.FROM_POSITION, false);
                startActivity(categoryIntent);
            }
        });

        binding.txtSpecialNew.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, ProductChildListActivity.class);
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
                Intent intent = new Intent(context, ProductChildListActivity.class);
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
                Intent intent = new Intent(context, ProductChildListActivity.class);
                intent.putExtra(Config.TITLE, Config.TrendingProductArrived);
                intent.putExtra(Config.CHILD_POSITION, 0);
                intent.putExtra(Config.CHILD_JSON, trendingProductJSON + "");
                Config.FROM_HOME = true;
                startActivity(intent);
            }
        });
    }

    private void initProductView() {

        productModelList = new ArrayList<>();
        binding.recyclerProductItem.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false));
        binding.recyclerProductItem.setHasFixedSize(true);
        adapter = new ProductCategoryAdapter(context, productModelList, HomeFragment.this);
        binding.recyclerProductItem.setAdapter(adapter);

        specialOfferProductList = new ArrayList<>();
        binding.recyclerSpecialProduct.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false));
        binding.recyclerSpecialProduct.setHasFixedSize(true);
        specialOfferAdapter = new NewArrivalAdapter(context, specialOfferProductList, HomeFragment.this);
        binding.recyclerSpecialProduct.setAdapter(specialOfferAdapter);

        newProductList = new ArrayList<>();
        binding.recyclerNewProduct.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false));
        binding.recyclerNewProduct.setHasFixedSize(true);
        newProductAdapter = new NewArrivalAdapter(context, newProductList, HomeFragment.this);
        binding.recyclerNewProduct.setAdapter(newProductAdapter);

        trendingProductList = new ArrayList<>();
        binding.recyclerTrendingProduct.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false));
        binding.recyclerTrendingProduct.setHasFixedSize(true);
        trendingProductAdapter = new NewArrivalAdapter(context, trendingProductList, HomeFragment.this);
        binding.recyclerTrendingProduct.setAdapter(trendingProductAdapter);

        sliderModelList = new ArrayList<>();
        sliderImageAdapter = new SliderImageAdapter(context, sliderModelList,1);
        binding.pager.setAdapter(sliderImageAdapter);
        binding.indicator.attachToPager(binding.pager);

    }

    private void hitCategoryApi() {
        showLoader();
        Api.newApi(context, P.baseUrl + "categories").addJson(new Json())
                .setMethod(Api.GET)
                //.onHeaderRequest(App::getHeaders)
                .onError(() -> {
                    hideLoader();
                    H.showMessage(context, "On error is called");
                })
                .onSuccess(json ->
                {
                    if (json.getInt(P.status) == 1) {
                        json = json.getJson(P.data);
                        App.categoryImageUrl = json.getString(P.category_image_path);
                        App.categoryJsonList = json.getJsonList(P.category_list);
                        loadCategoryProductItem();
                    } else{
                        H.showMessage(context, json.getString(P.msg));
                    }
                    hideLoader();
                })
                .run("hitCategoryApi");

    }

    private void loadCategoryProductItem() {
        productModelList.clear();
        try {
            for (Json data : App.categoryJsonList) {
                Json json = data.getJson(P.category);
                ProductModel model = new ProductModel();
                model.setId(json.getString(P.id));
                model.setName(json.getString(P.name));
                model.setParent_id(json.getString(P.parent_id));
                model.setImage(P.imgBaseUrl + App.categoryImageUrl + json.getString(P.image));
                model.setMain_parent_id(json.getString(P.main_parent_id));
                productModelList.add(model);
            }
        } catch (Exception e) {
        }

        if (productModelList.isEmpty()){
            binding.lnrExploreCategory.setVisibility(View.GONE);
        }else {
            binding.lnrExploreCategory.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void itemClick(int position) {
        Intent categoryIntent = new Intent(context, ProductCategoryActivity.class);
        categoryIntent.putExtra(Config.PARENT_POSITION, position);
        categoryIntent.putExtra(Config.FROM_POSITION, true);
        startActivity(categoryIntent);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (Config.Update_Favorite_Home){
            Config.Update_Favorite_Home = false;
//            hitHomeApi();
        }
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
            Api.newApi(context, P.baseUrl + "home").addJson(j)
                    .setMethod(Api.POST)
                    //.onHeaderRequest(App::getHeaders)
                    .onError(() -> {
                        hideLoader();
                        H.showMessage(context, "On error is called");
                    })
                    .onSuccess(json -> {
                        if (json.getInt(P.status) == 1) {
                            json = json.getJson(P.data);
                            App.homeJSONDATA = json;
                            App.product_image_path = json.getString(P.product_image_path);
                            setUpSliderList(json.getString(P.slider_image_path), json.getJsonList(P.slider_list));
                            setSpecialOfferProductList(json.getJsonList(P.special_offer_product));
                            setNewOfferProductList(json.getJsonList(P.new_product));
                            setTrendingOfferProductList(json.getJsonList(P.under_50_product));
                        } else {
                            H.showMessage(getContext(), json.getString(P.msg));
                        }
                        hideLoader();

                    })
                    .run("hitHomeApi");
        } catch (Exception e) {
            hideLoader();
        }

    }

    private void setUpSliderList(String string, JsonList jsonList) {
        sliderModelList.clear();
        for (Json json : jsonList) {
            SliderModel model = new SliderModel();
            model.setImage(P.imgBaseUrl + string + json.getString(P.image));
            model.setImage_alt_text(json.getString(P.image_alt_text));
            model.setNew_window(json.getString(P.new_window));
            model.setTitle(json.getString(P.title));
            model.setUrl(json.getString(P.url));
            sliderModelList.add(model);
        }
        sliderImageAdapter.notifyDataSetChanged();

        if (sliderModelList.isEmpty()){
            binding.lnrSlider.setVisibility(View.GONE);
        }else {
            binding.lnrSlider.setVisibility(View.VISIBLE);
        }

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
            specialOfferProductList.add(model);
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
            newProductList.add(model);
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
            trendingProductList.add(model);
        }
        trendingProductAdapter.notifyDataSetChanged();

        if (jsonList.size()==0){
            binding.lnrTrendingProduct.setVisibility(View.GONE);
        }else {
            binding.lnrTrendingProduct.setVisibility(View.VISIBLE);
        }
    }


    private void hitAddToWishList(Json j,ImageView imgAction) {
        showLoader();
        Api.newApi(context, P.baseUrl + "add_to_wishlist").addJson(j)
                .setMethod(Api.POST)
                //.onHeaderRequest(App::getHeaders)
                .onError(() -> {
                    hideLoader();
                    H.showMessage(context, "On error is called");
                })
                .onSuccess(json ->
                {
                    if (json.getInt(P.status) == 1) {

                        if (json.getString(P.msg).equals("wishlisted")){
                            H.showMessage(context, "Item added into favorite");
                            imgAction.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_baseline_remove_24));
                        }else if (json.getString(P.msg).equals("notwishlisted")){
                            H.showMessage(context, "Item removed from favorite");
                            imgAction.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_baseline_add_24));
                        }

                    } else{
                        H.showMessage(context, json.getString(P.msg));
                    }
                       hideLoader();
                })
                .run("hitAddToWishList");
    }

    private void showLoader() {
        loadingDialog.show("Please wait...");
    }

    private void hideLoader() {
        loadingDialog.hide();
    }

    @Override
    public void add(int filterId, ImageView imgAction) {
        Json json = new Json();
        if (session.getBool(P.isUserLogin)){
            json.addInt(P.user_id, H.getInt(session.getString(P.user_id)));
        }else {
            json.addInt(P.user_id, Config.commonUserID);
        }
        json.addInt(P.product_filter_id, filterId);
        hitAddToWishList(json,imgAction);
    }

}