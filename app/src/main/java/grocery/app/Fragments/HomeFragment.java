package grocery.app.Fragments;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;

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

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import grocery.app.BaseActivity;
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


public class HomeFragment extends Fragment implements ProductCategoryAdapter.ItemClick{

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
    SliderImageAdapter sliderImageAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        if (binding == null) {
            binding = DataBindingUtil.inflate(inflater, R.layout.fragment_home, container, false);
            context = getContext();
            loadingDialog = new LoadingDialog(context);
            initProductView();
            hitHomeApi();
//            setCategoryData();
            loadCategoryProductItem();
            onClickItemView();

        }

        return binding.getRoot();
    }

    private void onClickItemView(){

        binding.viewMoreArrival.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        binding.viewMoreExplorer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

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
        arrivalAdapter = new NewArrivalAdapter(context,arrivalModelList);
        binding.recyclerNewArrival.setAdapter(arrivalAdapter);

        sliderModelList = new ArrayList<>();
        sliderImageAdapter = new SliderImageAdapter(context, sliderModelList);
        binding.pager.setAdapter(sliderImageAdapter);

    }

    private void loadCategoryProductItem(){
        productModelList.clear();
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
    }

    private void setCategoryData(){
        ArrayList<String> arrayList = new ArrayList<>();
        for (Json json : App.categoryJsonList){
            arrayList.add(json.getJson(P.category).getString(P.name));
        }
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(context, R.layout.spinner_item_view, arrayList);
        binding.spinnerCategory.setAdapter(arrayAdapter);
        binding.spinnerCategory.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                App.selectedCategoryJson = App.categoryJsonList.get(position);
                spinnerPosition = position;
                loadProductList();
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    private void loadProductList(){
        productModelList.clear();
        for (Json data : App.selectedCategoryJson.getJsonList(P.children)) {
            Json json = data.getJson(P.category);
            productModelList.add(new ProductModel(json.getString(P.image), json.getString(P.name)));
        }
        adapter.notifyDataSetChanged();
    }

    @Override
    public void itemClick(int position) {
//        Config.FROM_HOME = true;
//        Config.CATEGORY_POSITION = spinnerPosition;
//        Config.SUB_CATEGORY_POSITION = position;
//        Config.CHILD_CATEGORY_POSITION = -1;
//        ProductListFragment productListFragment = ProductListFragment.newInstance();
//        ((BaseActivity) context).fragmentLoader((productListFragment));
    }

    private void hitHomeApi() {
        Api.newApi(context, P.baseUrl + "home").addJson(new Json())
                .setMethod(Api.GET)
                //.onHeaderRequest(App::getHeaders)
                .onError(() -> {
                    H.showMessage(context, "On error is called");
                })
                .onSuccess(json -> {
                    if (json.getInt(P.status) == 1) {
                        if (((BaseActivity) context).isDestroyed())
                            return;
                        json = json.getJson(P.data);
                        setUpNewArrivedList(json.getString(P.product_image_path), json.getJsonList(P.latest_product_list));
                        setUpSliderList(json.getString(P.slider_image_path), json.getJsonList(P.slider_list));
                    } else
                        H.showMessage(getContext(), json.getString(P.msg));
                })
                .run("hitHomeApi");
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

    public static HomeFragment newInstance() {
        HomeFragment fragment = new HomeFragment();

        return fragment;
    }

    private void showLoader(){
        loadingDialog.show("loading...");
    }

    private void hideLoader(){
        loadingDialog.hide();
    }

}