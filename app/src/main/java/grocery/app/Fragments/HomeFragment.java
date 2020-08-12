package grocery.app.Fragments;

import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import com.adoisstudio.helper.Api;
import com.adoisstudio.helper.H;
import com.adoisstudio.helper.Json;
import com.adoisstudio.helper.JsonList;
import com.adoisstudio.helper.LoadingDialog;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import grocery.app.BaseActivity;
import grocery.app.R;
import grocery.app.adapter.ProductAdapter;
import grocery.app.common.App;
import grocery.app.common.P;
import grocery.app.databinding.FragmentHomeBinding;
import grocery.app.model.ProductModel;
import grocery.app.util.Config;


public class HomeFragment extends Fragment implements ProductAdapter.ItemClick{

    private Context context;
    private LoadingDialog loadingDialog;
    private ViewPager2 viewPager2;
    private RecyclerView recyclerView;
    private FragmentHomeBinding binding;
    private int spinnerPosition;
    private List<ProductModel> productModelList;
    private ProductAdapter adapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        if (binding == null) {
            binding = DataBindingUtil.inflate(inflater, R.layout.fragment_home, container, false);
            context = getContext();
            loadingDialog = new LoadingDialog(context);
            setUpTopPager();
            initProductView();
            hitHomeApi();
            setCategoryData();
        }

        return binding.getRoot();
    }

    private void initProductView(){
        productModelList = new ArrayList<>();
        binding.recyclerProductItem.setLayoutManager(new LinearLayoutManager(context,LinearLayoutManager.HORIZONTAL,false));
        binding.recyclerProductItem.setHasFixedSize(true);
        adapter = new ProductAdapter(context,productModelList,HomeFragment.this);
        binding.recyclerProductItem.setAdapter(adapter);
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
            Log.e("TAG", "loadProductList: " + json );
            productModelList.add(new ProductModel(json.getString(P.image), json.getString(P.name)));
        }
        adapter.notifyDataSetChanged();
    }

    @Override
    public void itemClick(int position) {
        Config.CATEGORY_POSITION = spinnerPosition;
        Config.SUB_CATEGORY_POSITION = position;
        Config.CHILD_CATEGORY_POSITION = -1;
        ProductListFragment productListFragment = ProductListFragment.newInstance();
        ((BaseActivity) context).fragmentLoader((productListFragment));
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
                    } else
                        H.showMessage(getContext(), json.getString(P.msg));
                })
                .run("hitHomeApi");
    }

    private void setUpNewArrivedList(String string, JsonList jsonList) {

        for (Json json : jsonList)
            if (!TextUtils.isEmpty(json.getString(P.product_image))) {
                json.addString(P.product_image, P.imgBaseUrl + string + json.getString(P.product_image) + "");
            }
        RecyclerAdapter recyclerAdapter = new RecyclerAdapter(jsonList);
        recyclerView = binding.recyclerView1;
        recyclerView.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false));
        recyclerView.setAdapter(recyclerAdapter);

    }


    private class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.ViewHolder> {
        private JsonList jsonList;

        public RecyclerAdapter(JsonList jL) {
            jsonList = jL;
        }

        class ViewHolder extends RecyclerView.ViewHolder {
            ImageView imageView;
            TextView title;

            private ViewHolder(View itemView) {
                super(itemView);
                imageView = itemView.findViewById(R.id.veg);
                title = itemView.findViewById(R.id.textView);
                itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                    }
                });
            }
        }

        @NonNull
        @Override
        public RecyclerAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

            return new RecyclerAdapter.ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.new_arrived_layout, parent, false));

        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerAdapter.ViewHolder holder, int position) {

            String imagePath = jsonList.get(position).getString(P.product_image);
            if (!TextUtils.isEmpty(imagePath)) {
                Picasso.get().load(imagePath).error(R.mipmap.ic_launcher).into(holder.imageView);
            }
            String a = jsonList.get(position).getString(P.category_name);
            holder.title.setText(a);
        }

        @Override
        public int getItemCount() {
            H.log("sizeIS", jsonList + "");
            return jsonList.size();
        }
    }

    private void setUpTopPager() {
        viewPager2 = binding.viewPager2;
        ViewPager2Adapter viewPager2Adapter = new ViewPager2Adapter();
        viewPager2.setAdapter(viewPager2Adapter);

        TabLayout tabLayout = binding.tabLayout;
        //tabLayout.setupWithViewPager(viewPager2);
        TabLayoutMediator tabLayoutMediator = new TabLayoutMediator(tabLayout, viewPager2, (tab, position) -> {

        });
        tabLayoutMediator.attach();
    }

    private class ViewPager2Adapter extends RecyclerView.Adapter<ViewPager2Adapter.InnerClass> {
        private int i;

        private ViewPager2Adapter() {

        }

        @NonNull
        @Override
        public InnerClass onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view;

            view = getLayoutInflater().inflate(R.layout.image_crousal1, parent, false);


            return new InnerClass(view);
        }

        @Override
        public void onBindViewHolder(@NonNull InnerClass holder, int position) {

        }

        @Override
        public int getItemCount() {
            return 3;
        }

        class InnerClass extends RecyclerView.ViewHolder {
            public InnerClass(@NonNull View itemView) {
                super(itemView);
            }
        }
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

   /* @Override
    public void onDestroyView() {
        super.onDestroyView();

        Object object = fragmentView.getParent();
        if (object instanceof FrameLayout)
            ((FrameLayout) object).removeAllViews();
    }*/
}