package grocery.app.Fragments;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

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
    private List<ArrivalModel> arrivalModelList;
    private List<ArrivalModel> trendingModelList;
    private ProductCategoryAdapter adapter;
    private NewArrivalAdapter arrivalAdapter;
    private NewArrivalAdapter trendingAdapter;
    private List<SliderModel> sliderModelList;
    private JsonList arrivalJSON;
    private JsonList trendingJSON;

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
            initProductView();
            hitHomeApi();
            loadCategoryProductItem();
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

        binding.viewMoreArrival.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, ProductChildListActivity.class);
                intent.putExtra(Config.TITLE, "New Arrived");
                intent.putExtra(Config.CHILD_POSITION, 0);
                intent.putExtra(Config.CHILD_JSON, arrivalJSON + "");
                Config.FROM_HOME = true;
                startActivity(intent);
            }
        });

        binding.viewMoreTrending.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, ProductChildListActivity.class);
                intent.putExtra(Config.TITLE, "Trending Arrived");
                intent.putExtra(Config.CHILD_POSITION, 0);
                intent.putExtra(Config.CHILD_JSON, trendingJSON + "");
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

        arrivalModelList = new ArrayList<>();
        binding.recyclerNewArrival.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false));
        binding.recyclerNewArrival.setHasFixedSize(true);
        arrivalAdapter = new NewArrivalAdapter(context, arrivalModelList, HomeFragment.this);
        binding.recyclerNewArrival.setAdapter(arrivalAdapter);

        trendingModelList = new ArrayList<>();
        binding.recyclerTrendingProduct.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false));
        binding.recyclerTrendingProduct.setHasFixedSize(true);
        trendingAdapter = new NewArrivalAdapter(context, trendingModelList, HomeFragment.this);
        binding.recyclerTrendingProduct.setAdapter(trendingAdapter);

        sliderModelList = new ArrayList<>();
        sliderImageAdapter = new SliderImageAdapter(context, sliderModelList);
        binding.pager.setAdapter(sliderImageAdapter);
        binding.tabLayout.setupWithViewPager(binding.pager, true);

//        TabLayoutMediator tabLayoutMediator = new TabLayoutMediator(binding.tL, binding.vP2, (tab, position) -> {
//        });
//        tabLayoutMediator.attach();

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

    private void hitHomeApi() {
        showLoader();
        try {
            Json j = new Json();
            j.addInt(P.user_id, Config.dummyID);
            Api.newApi(context, P.baseUrl + "home").addJson(j)
                    .setMethod(Api.POST)
                    //.onHeaderRequest(App::getHeaders)
                    .onError(() -> {
                        hideLoader();
                        H.showMessage(context, "On error is called");
                    })
                    .onSuccess(json -> {
                        if (json.getInt(P.status) == 1) {
                            if (((BaseActivity) context).isDestroyed())
                                return;
                            json = json.getJson(P.data);
                            App.homeJSONDATA = json;
                            App.product_image_path = json.getString(P.product_image_path);
                            setUpSliderList(json.getString(P.slider_image_path), json.getJsonList(P.slider_list));
                            setUpNewArrivedList(json.getString(P.product_image_path), json.getJsonList(P.latest_product_list));
                            setUpTrendingProductList(json.getString(P.product_image_path), json.getJsonList(P.trending_product_list));
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

    private void setUpNewArrivedList(String string, JsonList jsonList) {
        arrivalJSON = jsonList;
        arrivalModelList.clear();
        for (Json json : jsonList) {
            ArrivalModel model = new ArrivalModel();
            model.setCategory_name(json.getString(P.category_name));
            model.setFilter_id(json.getString(P.filter_id));
            model.setId(json.getString(P.id));
            model.setName(json.getString(P.name));
            model.setIs_wishlisted(json.getString(P.is_wishlisted));
            model.setProduct_image(P.imgBaseUrl + string + json.getString(P.product_image));
            arrivalModelList.add(model);
        }
        arrivalAdapter.notifyDataSetChanged();

        if (jsonList.size()==0){
            binding.lnrNewArrived.setVisibility(View.GONE);
        }else {
            binding.lnrNewArrived.setVisibility(View.VISIBLE);
        }
    }

    private void setUpTrendingProductList(String string, JsonList jsonList) {
        trendingJSON = jsonList;
        trendingModelList.clear();
        for (Json json : jsonList) {
            ArrivalModel model = new ArrivalModel();
            model.setCategory_name(json.getString(P.category_name));
            model.setFilter_id(json.getString(P.filter_id));
            model.setId(json.getString(P.id));
            model.setName(json.getString(P.name));
            model.setIs_wishlisted(json.getString(P.is_wishlisted));
            model.setProduct_image(P.imgBaseUrl + string + json.getString(P.product_image));
            trendingModelList.add(model);
        }
        trendingAdapter.notifyDataSetChanged();

        if (jsonList.size()==0){
            binding.lnrTrendingArrived.setVisibility(View.GONE);
        }else {
            binding.lnrTrendingArrived.setVisibility(View.VISIBLE);
        }
    }

    private void hitAddToCartApi(Json j) {
        Api.newApi(context, P.baseUrl + "add_to_cart").addJson(j)
                .setMethod(Api.POST)
                //.onHeaderRequest(App::getHeaders)
                .onError(() -> {
                    H.showMessage(context, "On error is called");
                })
                .onSuccess(json ->
                {
                    if (json.getInt(P.status) == 1) {
                        H.showMessage(context, json.getString(P.msg));
                    } else
                        H.showMessage(context, json.getString(P.msg));
                })
                .run("hitAddToCartApi");
    }

    private void showLoader() {
        loadingDialog.show("Please wait...");
    }

    private void hideLoader() {
        loadingDialog.hide();
    }

    @Override
    public void add(int filterId) {
        Json json = new Json();
        json.addInt(P.product_filter_id, filterId);
        json.addString(P.cart_token, new Session(context).getString(P.cart_token));
        json.addString(P.user_id, "");
        json.addInt(P.quantity, 1);
        json.addInt(P.option1, 0);
        json.addInt(P.option2, 0);
        json.addInt(P.option3, 0);
//        hitAddToCartApi(json);
    }
}