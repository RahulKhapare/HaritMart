package grocery.app.Fragments;

import android.app.ActionBar;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.RadioButton;

import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.adoisstudio.helper.H;
import com.adoisstudio.helper.Json;
import com.adoisstudio.helper.Session;

import java.util.ArrayList;
import java.util.List;

import grocery.app.R;
import grocery.app.adapter.ProductItemAdapter;
import grocery.app.common.App;
import grocery.app.common.P;
import grocery.app.databinding.ActivityProductFragmentBinding;
import grocery.app.model.ProductModel;
import grocery.app.util.Config;

public class ProductListFragment extends Fragment implements ProductItemAdapter.itemClick{

    private ActivityProductFragmentBinding binding;
    private Context context;
    private List<ProductModel> categoryModelList;
    private List<ProductModel> subCategoryModelList;
    private ProductItemAdapter categoryAdapter;
    private ProductItemAdapter subCategoryAdapter;
    public boolean clearFlag = true;
    private LinearLayoutManager subLayout;
    private LinearLayoutManager childLayout;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (binding == null) {
            context = inflater.getContext();
            binding = DataBindingUtil.inflate(inflater, R.layout.activity_product_fragment, container, false);
            initView();
            setSpinnerCategoryData();
        }
        return binding.getRoot();
    }

    private void initView(){

        categoryModelList = new ArrayList<>();
        subCategoryModelList = new ArrayList<>();

        subLayout = new LinearLayoutManager(context,LinearLayoutManager.HORIZONTAL,false);
        binding.recyclerCategory.setLayoutManager(subLayout);
        binding.recyclerCategory.setHasFixedSize(true);
        childLayout = new LinearLayoutManager(context,LinearLayoutManager.HORIZONTAL,false);
        binding.recyclerSubCategory.setLayoutManager(childLayout);
        binding.recyclerSubCategory.setHasFixedSize(true);

        binding.txtSort.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sortDialog();
            }
        });
        binding.txtFilter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }

    private void setSpinnerCategoryData(){

        ArrayList<String> arrayList = new ArrayList<>();
        for (Json json : App.categoryJsonList){
            arrayList.add(json.getJson(P.category).getString(P.name));
        }
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(context, R.layout.spinner_item_view, arrayList);
        binding.spinnerCategory.setAdapter(arrayAdapter);
        if (clearFlag){
            binding.spinnerCategory.setSelection(Config.CATEGORY_POSITION);
        }
        binding.spinnerCategory.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                App.selectedCategoryJson = App.categoryJsonList.get(position);
                loadCategoryData();
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    private void loadCategoryData(){
        categoryModelList.clear();
        for (int i=0; i<App.selectedCategoryJson.getJsonList(P.children).size();i++){
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
        categoryAdapter = new ProductItemAdapter(context,categoryModelList,ProductListFragment.this,1,clearFlag);
        binding.recyclerCategory.setAdapter(categoryAdapter);
        loadSubCategoryData(Config.SUB_CATEGORY_POSITION);
    }

    private void loadSubCategoryData(int position){
        subCategoryModelList.clear();
        App.selectedSubCategoryJson = App.selectedCategoryJson.getJsonList(P.children).get(position);
        for (int j=0; j<App.selectedSubCategoryJson.getJsonList(P.children).size();j++){
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
        subCategoryAdapter = new ProductItemAdapter(context,subCategoryModelList,ProductListFragment.this,2,clearFlag);
        binding.recyclerSubCategory.setAdapter(subCategoryAdapter);
        if (clearFlag){
            try {
                subLayout.scrollToPosition(Config.SUB_CATEGORY_POSITION);
                childLayout.scrollToPosition(Config.CHILD_CATEGORY_POSITION);
            }catch (Exception e){
            }
        }
        clearFlag = false;
    }

    private void sortDialog() {
        final Dialog dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.activity_sort_dialog);
        RadioButton radioNew = dialog.findViewById(R.id.radioNew);
        RadioButton radioOld = dialog.findViewById(R.id.radioOld);
        radioNew.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked){
                    radioOld.setChecked(false);
                }
            }
        });
        radioOld.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked){
                    radioNew.setChecked(false);
                }
            }
        });
        dialog.setCancelable(true);
        dialog.show();
        Window window = dialog.getWindow();
        window.setLayout(ActionBar.LayoutParams.MATCH_PARENT, ActionBar.LayoutParams.WRAP_CONTENT);
    }

    @Override
    public void clickCategory(int position) {
        loadSubCategoryData(position);
    }

    @Override
    public void clickSubCategory(int position) {
    }



    public static ProductListFragment newInstance() {
        ProductListFragment fragment = new ProductListFragment();
        return fragment;
    }

}
