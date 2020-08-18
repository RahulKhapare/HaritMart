package grocery.app.Fragments;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;
import androidx.viewpager2.widget.ViewPager2;

import com.adoisstudio.helper.Api;
import com.adoisstudio.helper.H;
import com.adoisstudio.helper.Json;
import com.adoisstudio.helper.JsonList;
import com.adoisstudio.helper.LoadingDialog;
import com.adoisstudio.helper.Session;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

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

public class HomeFragment extends Fragment implements ProductCategoryAdapter.ItemClick,NewArrivalAdapter.ClickItem{

    SliderImageAdapter sliderImageAdapter;
    private Context context;
    private LoadingDialog loadingDialog;
    private ViewPager2 viewPager2;
    private RecyclerView recyclerView;
    private FragmentHomeBinding binding;
    private int spinnerPosition;
    private List<ProductModel> productModelList;
    private List<ArrivalModel> arrivalModelList;
    private ProductCategoryAdapter adapter;
    private NewArrivalAdapter arrivalAdapter;
    private int currentPage = 0;
    private int NUM_PAGES = 0;
    private List<SliderModel> sliderModelList;
    private JsonList arrivalJSON;

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

    private void onClickItemView(){

        binding.viewMoreExplorer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent categoryIntent = new Intent(context, ProductCategoryActivity.class);
                categoryIntent.putExtra(Config.FROM_POSITION,false);
                startActivity(categoryIntent);
            }
        });

        binding.viewMoreArrival.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, ProductChildListActivity.class);
                intent.putExtra(Config.TITLE,"New Arrived");
                intent.putExtra(Config.CHILD_POSITION,0);
                intent.putExtra(Config.CHILD_JSON,arrivalJSON+"");
                Config.FROM_HOME = true;
                startActivity(intent);
            }
        });

    }

    private void initProductView(){

        productModelList = new ArrayList<>();
        binding.recyclerProductItem.setLayoutManager(new LinearLayoutManager(context,LinearLayoutManager.HORIZONTAL,false));
        binding.recyclerProductItem.setHasFixedSize(true);
        adapter = new ProductCategoryAdapter(context,productModelList,HomeFragment.this);
        binding.recyclerProductItem.setAdapter(adapter);

        arrivalModelList = new ArrayList<>();
        binding.recyclerNewArrival.setLayoutManager(new LinearLayoutManager(context,LinearLayoutManager.HORIZONTAL,false));
        binding.recyclerNewArrival.setHasFixedSize(true);
        arrivalAdapter = new NewArrivalAdapter(context,arrivalModelList,HomeFragment.this);
        binding.recyclerNewArrival.setAdapter(arrivalAdapter);

        sliderModelList = new ArrayList<>();
        sliderImageAdapter = new SliderImageAdapter(context, sliderModelList);
        binding.pager.setAdapter(sliderImageAdapter);

    }

    private void loadCategoryProductItem(){
        productModelList.clear();
        try {
            for (Json data : App.categoryJsonList){
                Json json = data.getJson(P.category);
                ProductModel model = new ProductModel();
                model.setId(json.getString(P.id));
                model.setName(json.getString(P.name));
                model.setParent_id(json.getString(P.parent_id));
                model.setImage(json.getString(P.image));
                model.setMain_parent_id(json.getString(P.main_parent_id));
                productModelList.add(model);
            }
        }catch (Exception e){

        }

    }

    @Override
    public void itemClick(int position) {
        Intent categoryIntent = new Intent(context,ProductCategoryActivity.class);
        categoryIntent.putExtra(Config.PARENT_POSITION,position);
        categoryIntent.putExtra(Config.FROM_POSITION,true);
        startActivity(categoryIntent);
    }

    private void hitHomeApi() {
        showLoader();
        try {
            Api.newApi(context, P.baseUrl + "home").addJson(new Json())
                    .setMethod(Api.GET)
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
                            setUpNewArrivedList(json.getString(P.product_image_path), json.getJsonList(P.latest_product_list));
                            setUpSliderList(json.getString(P.slider_image_path), json.getJsonList(P.slider_list));
                        } else{
                            H.showMessage(getContext(), json.getString(P.msg));
                        }
                        hideLoader();

                    })
                    .run("hitHomeApi");
        }catch (Exception e){
            hideLoader();
        }

    }

    private void setUpSliderList(String string,JsonList jsonList){

        sliderModelList.clear();

        for (Json json : jsonList){
            SliderModel model = new SliderModel();
            model.setImage(P.imgBaseUrl + string + json.getString(P.image));
            model.setImage_alt_text(json.getString(P.image_alt_text));
            model.setNew_window(json.getString(P.new_window));
            model.setTitle(json.getString(P.title));
            model.setUrl(json.getString(P.url));
            sliderModelList.add(model);
        }

        currentPage = 0;
        NUM_PAGES = 0;

        sliderImageAdapter.notifyDataSetChanged();
        binding.indicator.setViewPager(binding.pager);

        final float density = getResources().getDisplayMetrics().density;

        //Set circle indicator radius
        binding.indicator.setRadius(5 * density);

        NUM_PAGES = sliderModelList.size();

        // Auto start of viewpager
        final Handler handler = new Handler();
        final Runnable Update = new Runnable() {
            public void run() {
                if (currentPage == NUM_PAGES) {
                    currentPage = 0;
                }
                binding.pager.setCurrentItem(currentPage++, true);
            }
        };
        Timer swipeTimer = new Timer();
        swipeTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                handler.post(Update);
            }
        }, 3000, 3000);

        // Pager listener over indicator
        binding.indicator.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                currentPage = position;
            }
            @Override
            public void onPageScrolled(int pos, float arg1, int arg2) {

            }
            @Override
            public void onPageScrollStateChanged(int pos) {

            }
        });
    }

    private void setUpNewArrivedList(String string, JsonList jsonList) {
        arrivalJSON = jsonList;
        arrivalModelList.clear();
        for (Json json : jsonList){
            ArrivalModel model = new ArrivalModel();
            model.setCategory_name(json.getString(P.category_name));
            model.setFilter_id(json.getString(P.filter_id));
            model.setId(json.getString(P.id));
            model.setName(json.getString(P.name));
            model.setProduct_image(P.imgBaseUrl + string + json.getString(P.product_image));
            arrivalModelList.add(model);
        }
        arrivalAdapter.notifyDataSetChanged();
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

    private void showLoader(){
        loadingDialog.show("Please wait...");
    }

    private void hideLoader(){
        loadingDialog.hide();
    }

    @Override
    public void add(int filterId) {
        Json json = new Json();
        json.addInt(P.product_filter_id,filterId);
        json.addString(P.cart_token, new Session(context).getString(P.cart_token));
        json.addString(P.user_id, "");
        json.addInt(P.quantity, 1);
        json.addInt(P.option1, 0);
        json.addInt(P.option2, 0);
        json.addInt(P.option3, 0);
        hitAddToCartApi(json);
    }
}