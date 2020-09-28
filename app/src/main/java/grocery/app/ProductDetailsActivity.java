package grocery.app;

import android.os.Bundle;
import android.os.Handler;
import android.view.MenuItem;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;

import java.util.ArrayList;
import java.util.List;

import grocery.app.adapter.NewArrivalAdapter;
import grocery.app.adapter.QuantityAdapter;
import grocery.app.adapter.SliderImageAdapter;
import grocery.app.databinding.ActivityProductDetailsBinding;
import grocery.app.model.ArrivalModel;
import grocery.app.model.QuantityModel;
import grocery.app.model.SliderModel;
import grocery.app.util.WindowBarColor;

public class ProductDetailsActivity extends AppCompatActivity implements NewArrivalAdapter.ClickItem, QuantityAdapter.ClickView {

    private ProductDetailsActivity activity = this;
    private ActivityProductDetailsBinding binding;
    private SliderImageAdapter sliderImageAdapter;
    private List<SliderModel> sliderModelList;
    private NewArrivalAdapter trendingAdapter;
    private NewArrivalAdapter popularAdapter;
    private QuantityAdapter quantityAdapter;
    private List<ArrivalModel> trendingDataList;
    private List<ArrivalModel> popularDataList;
    private List<QuantityModel> quantityModelList;
    String image = "https://i0.wp.com/www.eatthis.com/wp-content/uploads/2020/07/grocery-shopping-2.jpg?resize=640%2C360&ssl=1";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        WindowBarColor.setColor(this);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_product_details);

        binding.toolbar.setTitle("Product Details");
        setSupportActionBar(binding.toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        initView();

    }

    private void initView(){

        trendingDataList = new ArrayList<>();
        popularDataList = new ArrayList<>();
        quantityModelList = new ArrayList<>();

        quantityModelList.add(new QuantityModel("1Kg"));
        quantityModelList.add(new QuantityModel("2Kg"));
        quantityModelList.add(new QuantityModel("3Kg"));
        quantityModelList.add(new QuantityModel("4Kg"));

        binding.recyclerQuantityView.setHasFixedSize(true);
        binding.recyclerQuantityView.setLayoutManager(new LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL,false));
        quantityAdapter = new QuantityAdapter(activity,quantityModelList);
        binding.recyclerQuantityView.setAdapter(quantityAdapter);

        ArrivalModel model = new ArrivalModel();
        model.setFilter_id("1");
        model.setProduct_image(image);
        model.setProduct_image(image);
        model.setCategory_name("Grocery");

        trendingDataList.add(model);
        trendingDataList.add(model);
        trendingDataList.add(model);
        trendingDataList.add(model);
        trendingDataList.add(model);

        binding.recyclerTrending.setHasFixedSize(true);
        binding.recyclerTrending.setLayoutManager(new LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL,false));
        trendingAdapter = new NewArrivalAdapter(activity,trendingDataList,true);
        binding.recyclerTrending.setAdapter(trendingAdapter);

        popularDataList.add(model);
        popularDataList.add(model);
        popularDataList.add(model);
        popularDataList.add(model);
        popularDataList.add(model);

        binding.recyclerPopular.setHasFixedSize(true);
        binding.recyclerPopular.setLayoutManager(new LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL,false));
        popularAdapter = new NewArrivalAdapter(activity,popularDataList,true);
        binding.recyclerPopular.setAdapter(popularAdapter);

        binding.txtAbout.setText("About this given product");
        binding.txtFact.setText("Fact this given product");

        setUpSliderList();
        onClick();

    }

    private void onClick(){
        binding.viewMoreTrending.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        binding.viewMorePopular.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        binding.imgLike.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        binding.imgShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }


    @Override
    public void add(int filterId) {

    }

    @Override
    public void itemClick(int position, String id) {

    }

    private void setUpSliderList(){
        sliderModelList = new ArrayList<>();
        sliderImageAdapter = new SliderImageAdapter(activity, sliderModelList);
        binding.pager.setAdapter(sliderImageAdapter);
        binding.tabLayout.setupWithViewPager(binding.pager, true);
        loadSliderData();
    }

    private void loadSliderData() {
        sliderModelList.clear();
        SliderModel model = new SliderModel();
        model.setImage(image);
        sliderModelList.add(model);
        sliderModelList.add(model);
        sliderModelList.add(model);
        sliderModelList.add(model);
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