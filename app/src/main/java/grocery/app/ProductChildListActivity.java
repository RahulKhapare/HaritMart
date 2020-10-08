package grocery.app;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.adoisstudio.helper.Api;
import com.adoisstudio.helper.H;
import com.adoisstudio.helper.Json;
import com.adoisstudio.helper.JsonList;
import com.adoisstudio.helper.LoadingDialog;
import com.adoisstudio.helper.Session;
import com.miguelcatalan.materialsearchview.MaterialSearchView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import grocery.app.adapter.ProductChildItemAdapter;
import grocery.app.adapter.ProductListAdapter;
import grocery.app.common.App;
import grocery.app.common.P;
import grocery.app.databinding.ActivityProductChildListBinding;
import grocery.app.model.ProductModel;
import grocery.app.util.Config;
import grocery.app.util.WindowBarColor;

public class ProductChildListActivity extends AppCompatActivity implements ProductChildItemAdapter.ClickView,ProductListAdapter.Click {

    private ProductChildListActivity activity = this;
    private ActivityProductChildListBinding binding;
    private String title;
    private List<ProductModel> productCategoryList;
    private List<ProductModel> productModelList;
    private ProductListAdapter productListAdapter;
    private ProductChildItemAdapter productChildItemAdapter;
    private MaterialSearchView searchView;
    private int categoryPosition;
    private LoadingDialog loadingDialog;
    private Json filterJson = new Json();
    private int previousCategoryID;
    private boolean filterFlag;
    private boolean clearAll;
    private String jsnData;
    private JsonList checkListJson;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        WindowBarColor.setColor(this);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_product_child_list);

        title = getIntent().getStringExtra(Config.TITLE);
        jsnData = getIntent().getStringExtra(Config.CHILD_JSON);
        categoryPosition = getIntent().getIntExtra(Config.CHILD_POSITION, 0);

        binding.toolbar.setTitle(title);
        setSupportActionBar(binding.toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        loadingDialog = new LoadingDialog(activity);

        initData();
        onActionClick();
    }

    private void showLoader(){
        loadingDialog.show("Please wait...");
    }

    private void hideLoader(){
        loadingDialog.hide();
    }

    private void showView() {
        if (binding.lnrFilter.getVisibility() == View.GONE) {
            binding.lnrFilter.setVisibility(View.VISIBLE);
        }

    }

    private void hideView() {
        if (binding.lnrFilter.getVisibility() == View.VISIBLE) {
            binding.lnrFilter.setVisibility(View.GONE);
        }
    }

    private void onActionClick() {
        binding.lnrMain.setOnTouchListener(new View.OnTouchListener() {
            public boolean onTouch(View v, MotionEvent event) {
                hideView();
                return true;
            }
        });

        binding.imgCloseFilter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideView();
            }
        });
        binding.lnrMain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideView();
            }
        });
    }

    private void initData() {

        productModelList = new ArrayList<>();
        productCategoryList = new ArrayList<>();

        binding.recyclerProductCategory.setLayoutManager(new LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false));
        binding.recyclerProductCategory.setHasFixedSize(true);
        productChildItemAdapter = new ProductChildItemAdapter(activity, productCategoryList, true);
        binding.recyclerProductCategory.setAdapter(productChildItemAdapter);

        productListAdapter = new ProductListAdapter(activity, productModelList);
        binding.recyclerProductList.setLayoutManager(new LinearLayoutManager(activity));
        binding.recyclerProductList.setHasFixedSize(true);
        binding.recyclerProductList.setAdapter(productListAdapter);

        initSearchView();
        if(!Config.FROM_HOME){
            getProductCategoryData();
        }else {
            getNewArrivalProduct();
        }

    }

    private void getNewArrivalProduct(){
        productModelList.clear();
        try {
            JSONArray jsonArray = new JSONArray(jsnData);
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                ProductModel model = new ProductModel();
                model.setCategory_name(jsonObject.getString(P.category_name));
                model.setId(jsonObject.getString(P.id));
                model.setFilter_id(jsonObject.getString(P.filter_id));
                model.setName(jsonObject.getString(P.name));
                model.setIs_wishlisted(jsonObject.getString(P.is_wishlisted));
                model.setProduct_image(P.imgBaseUrl + App.product_image_path + jsonObject.getString(P.product_image));
                try {
//                    JSONObject priceJson =  jsonObject.getJSONObject(P.price);
                    model.setPrice("0");
                    model.setSaleprice("0");
                    model.setDiscount_amount("0");
                    model.setDiscount("0");
                }catch (Exception e){
                }
                productModelList.add(model);
            }
            productListAdapter.notifyDataSetChanged();
            if (productModelList.isEmpty()){
                H.showMessage(activity,"No arrived product data found");
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void getProductCategoryData() {
        productCategoryList.clear();
        try {
            JSONArray jsonArray = new JSONArray(jsnData);
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonData = jsonArray.getJSONObject(i);
                JSONObject json = new JSONObject(jsonData.getString(P.category));
                ProductModel model = new ProductModel();
                model.setId(json.getString(P.id));
                model.setParent_id(json.getString(P.parent_id));
                model.setName(json.getString(P.name));
                model.setImage(json.getString(P.image));
                model.setMain_parent_id(json.getString(P.main_parent_id));
                productCategoryList.add(model);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        productChildItemAdapter.notifyDataSetChanged();
        if (productCategoryList.isEmpty()) {
            showError();
        }
    }

    private void updateProductList(int id) {
        filterFlag = true;
        hitProductListApi(id);
    }


    private void hitProductListApi(int categoryId) {
        previousCategoryID = categoryId;
        showLoader();
        productModelList.clear();
        Json j = new Json();
        j.addInt(P.category_id, categoryId);
        j.addString(P.search_tag, "");
        j.addInt(P.limit, 19);
        j.addInt(P.page, 1);

        filterJson = new Json();
        storeDataInFilterJson(binding.lnrFilterView);
        j.addJSON(P.filters, filterJson);

        try {
            Api.newApi(activity, P.baseUrl + "product_list").addJson(j)
                    .setMethod(Api.POST)
                    //.onHeaderRequest(App::getHeaders)
                    .onError(() -> {
                        hideLoader();
                        showError();
                    })
                    .onSuccess(json ->
                    {
                        if (json.getInt(P.status) == 1) {
                            json = json.getJson(P.data);
                            hideView();
                            if (filterFlag) {
                                hitCategoryFilterListApi(categoryId);
                            }
                            Config.PRODUCT_IMAGE_PATH = json.getString(P.product_image_path);
                            try {
                                JSONArray jsonArray = json.getJSONArray(P.product_list);
                                productModelList.clear();
                                for (int i = 0; i < jsonArray.length(); i++) {
                                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                                    ProductModel model = new ProductModel();
                                    model.setCategory_name(jsonObject.getString(P.category_name));
                                    model.setId(jsonObject.getString(P.id));
                                    model.setFilter_id(jsonObject.getString(P.filter_id));
                                    model.setName(jsonObject.getString(P.name));
                                    model.setProduct_image(jsonObject.getString(P.product_image));
                                    try {
                                        JSONObject priceJson =  jsonObject.getJSONObject(P.price);
                                        model.setPrice(priceJson.getString(P.price));
                                        model.setSaleprice(priceJson.getString(P.saleprice));
                                        model.setDiscount_amount(priceJson.getString(P.discount_amount));
                                        model.setDiscount(priceJson.getString(P.discount));
                                    }catch (Exception e){
                                    }
                                    productModelList.add(model);
                                }
                                productListAdapter.notifyDataSetChanged();

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            hideError();
                        } else {
                            hideLoader();
                            showError();
                            H.showMessage(activity, json.getString(P.msg));
                        }

                        hideLoader();
                        if (productModelList.isEmpty()) {
                            showError();
                        }

                    })
                    .run("hitProductListApi");
        }catch (Exception e){

        }


    }

    private void hitCategoryFilterListApi(int categoryId) {
        try {
            Api.newApi(activity, P.baseUrl + "category_filters/" + categoryId).addJson(new Json())
                    .setMethod(Api.GET)
                    //.onHeaderRequest(App::getHeaders)
                    .onError(() -> {
                        H.showMessage(activity, "On error is called");
                    })
                    .onSuccess(json ->
                    {
                        if (json.getInt(P.status) == 1) {
                            json = json.getJson(P.data);
                            setUpFilterCategory(json.getJsonList(P.category_filter_data));
                        } else
                            H.showMessage(activity, json.getString(P.msg));
                    })
                    .run("hitCategoryFilterListApi");
        }catch (Exception e){

        }

    }

    private void setUpFilterCategory(JsonList jsonList) {
        checkListJson = jsonList;
        binding.lnrFilterView.removeAllViews();
        if (jsonList != null) {
            TextView textView;
            LinearLayout linearLayout;
            CheckBox checkBox;
            for (Json json : jsonList) {
                View view = getLayoutInflater().inflate(R.layout.filter_item_view, null, false);
                textView = view.findViewById(R.id.txtFilterTitle);
                textView.setText(json.getString(P.title) + "");
                textView.setOnClickListener(this::handleVisibilityOfFilterParents);

                linearLayout = view.findViewById(R.id.filterContainerView);
                linearLayout.setTag(json.getString(P.id));

                if (json.equals(jsonList.get(0)))
                    linearLayout.setVisibility(View.VISIBLE);

                for (int i=0; i<json.getJsonList(P.lable_values).size(); i++){
                    Json j = json.getJsonList(P.lable_values).get(i);
                    View v = getLayoutInflater().inflate(R.layout.custom_check_box_view, null, false);
                    checkBox = (CheckBox) v;
                    checkBox.setText(j.getString(P.value));
                    checkBox.setTag(j.getString(P.id));

                    linearLayout.addView(v);

                    checkBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
                        filterFlag = false;
                        hitProductListApi(previousCategoryID);
                    });
                }

                binding.lnrFilterView.addView(view);
            }
        }

        binding.txtClearFilter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clearAll = true;
                unCheckBox(binding.lnrFilterView);
            }
        });
    }

    private void unCheckBox(ViewGroup viewGroup) {
        for (int i = 0; i < viewGroup.getChildCount(); i++) {
            View view = viewGroup.getChildAt(i);
            if (view instanceof ViewGroup)
                unCheckBox((ViewGroup) view);
            else if (view instanceof CheckBox)
                ((CheckBox) view).setChecked(false);
        }
    }


    private void handleVisibilityOfFilterParents(View v) {
        ViewParent viewParent = v.getParent().getParent();
        LinearLayout linearLayout = (LinearLayout) viewParent;
        LinearLayout ll;

        for (int i = 0; i < linearLayout.getChildCount(); i++) {
            ll = (LinearLayout) linearLayout.getChildAt(i);
            ll.getChildAt(1).setVisibility(View.GONE);
        }

        ll = (LinearLayout) v.getParent();
        ll.getChildAt(1).setVisibility(View.VISIBLE);
    }

    private void storeDataInFilterJson(ViewGroup viewGroup) {

        for (int i = 0; i < viewGroup.getChildCount(); i++) {
            View view = viewGroup.getChildAt(i);
            if (view instanceof ViewGroup)
                storeDataInFilterJson((ViewGroup) view);
            else if (view instanceof CheckBox) {
                LinearLayout linearLayout = (LinearLayout) view.getParent();
                String string = linearLayout.getTag() + "";
                String str = view.getTag() + "";

                if (((CheckBox) view).isChecked()) {
                    if (filterJson.has(string) && !filterJson.getString(string).contains(str))
                        filterJson.addString(string, filterJson.getString(string) + "," + str);
                    else
                        filterJson.addString(string, str);
                }
            }
        }
    }


    private void initSearchView() {
        searchView = (MaterialSearchView) findViewById(R.id.search_view);
        searchView.setHint("Search product here");
        searchView.setTextColor(getResources().getColor(R.color.green));
        searchView.setHintTextColor(getResources().getColor(R.color.grey1));
        searchView.setVoiceSearch(true); //or false
        searchView.setOnQueryTextListener(new MaterialSearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                //Do some magic
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                //Do some magic
                if (!TextUtils.isEmpty(newText)) {
                    List<ProductModel> list = new ArrayList<ProductModel>();
                    for (ProductModel model : productModelList) {
                        if (model.getName().toLowerCase().contains(newText.toLowerCase())) {
                            list.add(model);
                        }
                    }
                    productListAdapter = new ProductListAdapter(activity, list);
                    binding.recyclerProductList.setAdapter(productListAdapter);
                    productListAdapter.notifyDataSetChanged();
                } else {
                    productListAdapter = new ProductListAdapter(activity, productModelList);
                    binding.recyclerProductList.setAdapter(productListAdapter);
                }
                return false;
            }
        });
    }


    private void onFilterView() {
        if (checkListJson != null && checkListJson.size() != 0) {
            showView();
        } else {
            H.showMessage(activity, "No filter data found");
        }
    }

    private void hitAddToCartApi(Json j) {
        Api.newApi(this, P.baseUrl + "add_to_cart").addJson(j)
                .setMethod(Api.POST)
                //.onHeaderRequest(App::getHeaders)
                .onError(() -> {
                    H.showMessage(this, "On error is called");
                })
                .onSuccess(json ->
                {
                    if (json.getInt(P.status) == 1) {
                        H.showMessage(this, json.getString(P.msg));
                    } else
                        H.showMessage(this, json.getString(P.msg));
                })
                .run("hitAddToCartApi");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.product, menu);
        MenuItem item = menu.findItem(R.id.action_search);
        MenuItem filter = menu.findItem(R.id.action_filter);
        searchView.setMenuItem(item);
        if (Config.FROM_HOME){
            filter.setVisible(false);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
        } else if (item.getItemId() == R.id.action_filter) {
            onFilterView();
        }
        return false;
    }

    @Override
    public void onBackPressed() {
        if (searchView.isSearchOpen()) {
            searchView.closeSearch();
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public void itemClick(int position, int id) {
        updateProductList(id);
    }

    @Override
    public void add(int filterId) {
        Json json = new Json();
        json.addInt(P.product_filter_id,filterId);
        json.addString(P.cart_token, new Session(this).getString(P.cart_token));
        json.addString(P.user_id, "");
        json.addInt(P.quantity, 1);
        json.addInt(P.option1, 0);
        json.addInt(P.option2, 0);
        json.addInt(P.option3, 0);
//        hitAddToCartApi(json);
    }

    private void showError(){
        binding.lnrError.setVisibility(View.VISIBLE);
    }
    private void hideError(){
        binding.lnrError.setVisibility(View.GONE);
    }
}