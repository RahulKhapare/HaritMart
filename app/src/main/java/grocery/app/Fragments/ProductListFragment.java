package grocery.app.Fragments;

import android.app.ActionBar;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.view.Window;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;

import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.adoisstudio.helper.Api;
import com.adoisstudio.helper.H;
import com.adoisstudio.helper.Json;
import com.adoisstudio.helper.JsonList;
import com.adoisstudio.helper.LoadingDialog;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import grocery.app.R;
import grocery.app.adapter.ProductItemAdapter;
import grocery.app.adapter.ProductViewAdapter;
import grocery.app.common.App;
import grocery.app.common.P;
import grocery.app.databinding.ActivityProductFragmentBinding;
import grocery.app.model.ProductModel;
import grocery.app.util.Config;

public class ProductListFragment extends Fragment implements ProductItemAdapter.itemClick {

    private ActivityProductFragmentBinding binding;
    private Context context;
    private List<ProductModel> categoryModelList;
    private List<ProductModel> subCategoryModelList;
    private ProductItemAdapter categoryAdapter;
    private ProductItemAdapter subCategoryAdapter;
    public boolean clearFlag = true;
    private LinearLayoutManager subLayout;
    private LinearLayoutManager childLayout;
    private int CheckValue;
    private boolean allowSelection = false;
    private List<ProductModel> productModelList;
    private ProductViewAdapter productViewAdapter;
    private LoadingDialog loadingDialog;
    private int previousCategoryID;
    private boolean filterFlag;
    private Json filterJson = new Json();
    private JsonList checkListJson;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (binding == null) {
            context = inflater.getContext();
            binding = DataBindingUtil.inflate(inflater, R.layout.activity_product_fragment, container, false);
            loadingDialog = new LoadingDialog(context);
            initView();
            setSpinnerCategoryData();

        }
        return binding.getRoot();
    }

    private void initView() {
        productModelList = new ArrayList<>();
        categoryModelList = new ArrayList<>();
        subCategoryModelList = new ArrayList<>();

        subLayout = new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false);
        binding.recyclerCategory.setLayoutManager(subLayout);
        binding.recyclerCategory.setHasFixedSize(true);
        childLayout = new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false);
        binding.recyclerSubCategory.setLayoutManager(childLayout);
        binding.recyclerSubCategory.setHasFixedSize(true);

        binding.recyclerProductList.setLayoutManager(new GridLayoutManager(context, 2));
        binding.recyclerProductList.setHasFixedSize(true);
        productViewAdapter = new ProductViewAdapter(context, productModelList);
        binding.recyclerProductList.setAdapter(productViewAdapter);

        onActionClick();

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

        binding.txtSort.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sortDialog();
            }
        });
        binding.txtFilter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onFilterView();
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
        binding.recyclerProductList.addOnScrollListener(new RecyclerView.OnScrollListener() {

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (dy > 0) {
//                    H.showMessage(context,"UP");
                } else {
//                    H.showMessage(context,"DOWN");
                }
            }

            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);

                if (newState == AbsListView.OnScrollListener.SCROLL_STATE_FLING) {
                    // Do something
                } else if (newState == AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL) {
                    // Do something
                } else {
                    // Do something
                }
            }
        });
    }

    private void setSpinnerCategoryData() {

        ArrayList<String> arrayList = new ArrayList<>();
        for (Json json : App.categoryJsonList) {
            arrayList.add(json.getJson(P.category).getString(P.name));
        }
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(context, R.layout.spinner_item_view, arrayList);
        binding.spinnerCategory.setAdapter(arrayAdapter);
        if (clearFlag) {
            binding.spinnerCategory.setSelection(Config.CATEGORY_POSITION);
        }
        binding.spinnerCategory.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String categoryId = App.categoryJsonList.get(position).getJson(P.category).getString(P.id);
                App.selectedCategoryJson = App.categoryJsonList.get(position);
                if (allowSelection) {
                    categoryModelList.clear();
                    subCategoryModelList.clear();
                    if (categoryAdapter != null) {
                        categoryAdapter.notifyDataSetChanged();
                    }
                    if (subCategoryAdapter != null) {
                        subCategoryAdapter.notifyDataSetChanged();
                    }
                    loadCategoryData(false);
                    filterFlag = true;
//                    hitProductListApi(App.selectedCategoryJson.getJson(P.category).getInt(P.id));
                    hitProductListApi(Integer.parseInt(categoryId));
                } else {
                    loadCategoryData(true);
                    allowSelection = true;
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    private void checkProductList() {
        if (Config.FROM_HOME) {
            hitProductListApi(Config.SUB_CATEGORY_POSITION);
        } else {
            hitProductListApi(Config.CHILD_CATEGORY_POSITION);
        }
    }

    private void loadCategoryData(boolean flag) {
        categoryModelList.clear();
        for (int i = 0; i < App.selectedCategoryJson.getJsonList(P.children).size(); i++) {
            Json jsonCategory = App.selectedCategoryJson.getJsonList(P.children).get(i);
            Json json = jsonCategory.getJson(P.category);
            ProductModel model = new ProductModel();
            model.setId(json.getString(P.id));
            model.setParent_id(json.getString(P.parent_id));
            model.setName(json.getString(P.name));
            model.setImage(json.getString(P.image));
            model.setMain_parent_id(json.getString(P.main_parent_id));
            categoryModelList.add(model);
        }
        categoryAdapter = new ProductItemAdapter(context, categoryModelList, ProductListFragment.this, 1, clearFlag, flag);
        binding.recyclerCategory.setAdapter(categoryAdapter);
        checkProductList();
        if (!Config.FROM_HOME) {
            Config.FROM_HOME = true;
            loadSubCategoryData(Config.SUB_CATEGORY_POSITION);
        }
    }

    private void loadSubCategoryData(int position) {
        subCategoryModelList.clear();
        App.selectedSubCategoryJson = App.selectedCategoryJson.getJsonList(P.children).get(position);
        for (int j = 0; j < App.selectedSubCategoryJson.getJsonList(P.children).size(); j++) {
            Json jsonCategory = App.selectedSubCategoryJson.getJsonList(P.children).get(j);
            Json json = jsonCategory.getJson(P.category);
            ProductModel model = new ProductModel();
            model.setId(json.getString(P.id));
            model.setParent_id(json.getString(P.parent_id));
            model.setName(json.getString(P.name));
            model.setImage(json.getString(P.image));
            model.setMain_parent_id(json.getString(P.main_parent_id));
            subCategoryModelList.add(model);
        }
        subCategoryAdapter = new ProductItemAdapter(context, subCategoryModelList, ProductListFragment.this, 2, clearFlag, false);
        binding.recyclerSubCategory.setAdapter(subCategoryAdapter);
        if (clearFlag) {
            try {
                subLayout.scrollToPosition(Config.SUB_CATEGORY_POSITION);
                childLayout.scrollToPosition(Config.CHILD_CATEGORY_POSITION);
            } catch (Exception e) {
            }
        }
        clearFlag = false;
    }


    private void onFilterView() {
        if (checkListJson != null && checkListJson.size() != 0) {
            showView();
        } else {
            H.showMessage(context, "No filter data found");
        }
    }

    private void sortDialog() {
        final Dialog dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setContentView(R.layout.activity_sort_dialog);
        RadioButton radioNew = dialog.findViewById(R.id.radioNew);
        RadioButton radioOld = dialog.findViewById(R.id.radioOld);
        if (CheckValue == 1) {
            radioNew.setChecked(true);
            radioOld.setChecked(false);
        } else if (CheckValue == 2) {
            radioOld.setChecked(true);
            radioNew.setChecked(false);
        }
        radioNew.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    radioOld.setChecked(false);
                    CheckValue = 1;
                }
            }
        });
        radioOld.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    radioNew.setChecked(false);
                    CheckValue = 2;
                }
            }
        });
        dialog.setCancelable(true);
        dialog.show();
        Window window = dialog.getWindow();
        window.setLayout(ActionBar.LayoutParams.MATCH_PARENT, ActionBar.LayoutParams.WRAP_CONTENT);
    }


    private void hitProductListApi(int categoryId) {
        previousCategoryID = categoryId;
        loadingDialog.show("Please wait....");
        productModelList.clear();
        Json j = new Json();
        j.addInt(P.category_id, categoryId);
        j.addString(P.search_tag, "");
        j.addInt(P.limit, 19);
        j.addInt(P.page, 1);

        filterJson = new Json();
        storeDataInFilterJson(binding.lnrFilterView);
        Log.e("TAG", "filterJsonData: " + filterJson);
        j.addJSON(P.filters, filterJson);

        Api.newApi(context, P.baseUrl + "product_list").addJson(j)
                .setMethod(Api.POST)
                //.onHeaderRequest(App::getHeaders)
                .onError(() -> {
                    loadingDialog.hide();
                    H.showMessage(context, "On error is called");
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
                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject jsonObject = jsonArray.getJSONObject(i);
                                ProductModel model = new ProductModel();
                                model.setCategory_name(jsonObject.getString(P.category_name));
                                model.setId(jsonObject.getString(P.id));
                                model.setFilter_id(jsonObject.getString(P.filter_id));
                                model.setName(jsonObject.getString(P.name));
                                model.setProduct_image(jsonObject.getString(P.product_image));
                                productModelList.add(model);
                            }

                            productViewAdapter.notifyDataSetChanged();

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        loadingDialog.hide();

                    } else {
                        loadingDialog.hide();
                        H.showMessage(context, json.getString(P.msg));
                    }

                })
                .run("hitProductListApi");
    }

    private void hitCategoryFilterListApi(int categoryId) {
        Api.newApi(context, P.baseUrl + "category_filters/" + categoryId).addJson(new Json())
                .setMethod(Api.GET)
                //.onHeaderRequest(App::getHeaders)
                .onError(() -> {
                    H.showMessage(context, "On error is called");
                })
                .onSuccess(json ->
                {
                    if (json.getInt(P.status) == 1) {
                        json = json.getJson(P.data);
                        setUpFilterCategory(json.getJsonList(P.category_filter_data));
                        setUpSortByPopUp(json.getJson(P.short_by_data));

                    } else
                        H.showMessage(context, json.getString(P.msg));
                })
                .run("hitCategoryFilterListApi");
    }

    private void setUpFilterCategory(JsonList jsonList) {
        Log.e("TAG", "setUpFilterCategory: " + jsonList);
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

                for (Json j : json.getJsonList(P.lable_values)) {
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
        binding.txtClearFilter.setOnClickListener(v -> unCheckBox(binding.lnrFilterView));
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

        for (int i = 1; i < linearLayout.getChildCount(); i++) {
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

    private void setUpSortByPopUp(Json json) {
        Log.e("TAG", "setUpSortByPopUp: " + json);
    }


    @Override
    public void clickCategory(int position, int id) {
        filterFlag = true;
        hitProductListApi(id);
        loadSubCategoryData(position);
    }

    @Override
    public void clickSubCategory(int position, int id) {
        filterFlag = true;
        hitProductListApi(id);
    }


    public static ProductListFragment newInstance() {
        ProductListFragment fragment = new ProductListFragment();
        return fragment;
    }

}
