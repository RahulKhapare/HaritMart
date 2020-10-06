package grocery.app;

import android.os.Bundle;
import android.view.MenuItem;
import android.widget.ExpandableListView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import com.adoisstudio.helper.Json;
import com.adoisstudio.helper.JsonList;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import grocery.app.adapter.ExpandableProductAdapter;
import grocery.app.common.App;
import grocery.app.common.P;
import grocery.app.databinding.ActivityProductCategoryBinding;
import grocery.app.model.ProductModel;
import grocery.app.util.Config;
import grocery.app.util.WindowBarColor;

public class ProductCategoryActivity extends AppCompatActivity {

    private ActivityProductCategoryBinding binding;
    private ProductCategoryActivity activity = this;
    private int position;
    private boolean fromPosition;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        WindowBarColor.setColor(this);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_product_category);

        binding.toolbar.setTitle("Category");
        setSupportActionBar(binding.toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        position = getIntent().getIntExtra(Config.PARENT_POSITION,0);
        fromPosition = getIntent().getBooleanExtra(Config.FROM_POSITION,false);

        LinkedHashMap<ProductModel, List<ProductModel>> expandableListDetail = new LinkedHashMap<ProductModel, List<ProductModel>>();

        for (int i = 0; i < App.categoryJsonList.size(); i++) {

            Json jsonParentData = App.categoryJsonList.get(i);
            Json jsonParent = jsonParentData.getJson(P.category);

            ProductModel subModel = new ProductModel();
            subModel.setId(jsonParent.getString(P.id));
            subModel.setParent_id(jsonParent.getString(P.parent_id));
            subModel.setName(jsonParent.getString(P.name));
            subModel.setImage(jsonParent.getString(P.image));
            subModel.setMain_parent_id(jsonParent.getString(P.main_parent_id));

            App.selectedCategoryJson = App.categoryJsonList.get(i);
            List<ProductModel> childItem = new ArrayList<ProductModel>();

            for (int j = 0; j < App.selectedCategoryJson.getJsonList(P.children).size(); j++) {

                Json jsonChildData = App.selectedCategoryJson.getJsonList(P.children).get(j);
                Json jsonChild = jsonChildData.getJson(P.category);
                JsonList subChildList = jsonChildData.getJsonList(P.children);

                ProductModel childModel = new ProductModel();
                childModel.setId(jsonChild.getString(P.id));
                childModel.setParent_id(jsonChild.getString(P.parent_id));
                childModel.setName(jsonChild.getString(P.name));
                childModel.setImage(P.imgBaseUrl + App.categoryImageUrl + jsonChild.getString(P.image));
                childModel.setMain_parent_id(jsonChild.getString(P.main_parent_id));
                childModel.setPosition(j);
                childModel.setJsonArrayData(subChildList+"");
                childItem.add(childModel);

            }

            expandableListDetail.put(subModel, childItem);

        }

        List<ProductModel> expandableListTitle = new ArrayList<ProductModel>(expandableListDetail.keySet());
        ExpandableProductAdapter adapter = new ExpandableProductAdapter(activity,expandableListTitle,expandableListDetail);
        binding.productExpandList.setAdapter(adapter);

        if (fromPosition){
            binding.productExpandList.setVerticalScrollbarPosition(position);
            binding.productExpandList.expandGroup(position);
        }

        binding.productExpandList.setOnGroupExpandListener(new ExpandableListView.OnGroupExpandListener() {
            int previousItem = -1;
            @Override
            public void onGroupExpand(int groupPosition) {
                if (groupPosition!=previousItem)
                    binding.productExpandList.collapseGroup(previousItem);
                previousItem = groupPosition;
            }
        });

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