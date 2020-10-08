package grocery.app.Fragments;

import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;

import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.adoisstudio.helper.Json;
import com.adoisstudio.helper.JsonList;

import java.util.ArrayList;
import java.util.List;

import grocery.app.R;
import grocery.app.adapter.SearchAdapter;
import grocery.app.common.App;
import grocery.app.common.P;
import grocery.app.databinding.FragmentSearchBinding;
import grocery.app.model.SearchModel;


public class SearchFragment extends Fragment implements SearchAdapter.Click{

    private FragmentSearchBinding binding;
    private Context context;
    private SearchAdapter searchAdapter;
    private SearchAdapter trendAdapter;
    private List<SearchModel> searchModelList;
    private List<SearchModel> trendModelList;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_search, container, false);
        context = inflater.getContext();
        initView();
        return binding.getRoot();
    }

    private void initView() {
        searchModelList = new ArrayList<>();
        trendModelList = new ArrayList<>();

        binding.recyclerSearchHistory.setHasFixedSize(true);
        binding.recyclerSearchHistory.setNestedScrollingEnabled(false);
        binding.recyclerSearchHistory.setLayoutManager(new LinearLayoutManager(context));
        searchAdapter = new SearchAdapter(context, searchModelList, true,SearchFragment.this);
        binding.recyclerSearchHistory.setAdapter(searchAdapter);

        binding.recyclerTrendingSearches.setHasFixedSize(true);
        binding.recyclerTrendingSearches.setNestedScrollingEnabled(false);
        binding.recyclerTrendingSearches.setLayoutManager(new LinearLayoutManager(context));
        trendAdapter = new SearchAdapter(context, trendModelList, true,SearchFragment.this);
        binding.recyclerTrendingSearches.setAdapter(trendAdapter);

        loadProductData();
        onSearchView();

    }

    @Override
    public void onResume() {
        super.onResume();
        if (searchModelList!=null && searchModelList.isEmpty()){
            binding.txtSearch.setVisibility(View.GONE);
        }
    }

    private void onSearchView(){
        binding.etxSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,int after) {
            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String newText = s.toString();
                if (!TextUtils.isEmpty(newText)) {
                    List<SearchModel> list = new ArrayList<SearchModel>();
                    for (SearchModel model : trendModelList) {
                        if (model.getName().toLowerCase().contains(newText.toLowerCase())) {
                            list.add(model);
                        }
                    }
                    trendAdapter = new SearchAdapter(context, list,true,SearchFragment.this);
                    binding.recyclerTrendingSearches.setAdapter(trendAdapter);
                    searchAdapter.notifyDataSetChanged();
                } else {
                    trendAdapter = new SearchAdapter(context, trendModelList,true,SearchFragment.this);
                    binding.recyclerTrendingSearches.setAdapter(trendAdapter);
                }
            }
            @Override
            public void afterTextChanged(Editable s) {
            }
        });
    }

    private void loadProductData(){
        Json json = App.homeJSONDATA;
        setUpTrendingProductList(json.getString(P.product_image_path), json.getJsonList(P.trending_product_list));
    }

    private void setUpTrendingProductList(String string, JsonList jsonList) {
        trendModelList.clear();
        for (Json json : jsonList) {
            SearchModel model = new SearchModel();
            model.setCategory_name(json.getString(P.category_name));
            model.setFilter_id(json.getString(P.filter_id));
            model.setId(json.getString(P.id));
            model.setName(json.getString(P.name));
            model.setIs_wishlisted(json.getString(P.is_wishlisted));
            model.setProduct_image(P.imgBaseUrl + string + json.getString(P.product_image));
            trendModelList.add(model);
        }
        trendAdapter.notifyDataSetChanged();

        if (trendModelList.isEmpty()){
            binding.txtTendingSearch.setVisibility(View.GONE);
            showError();
        }else {
            binding.txtTendingSearch.setVisibility(View.VISIBLE);
            hideError();
        }
    }

    @Override
    public void remove(LinearLayout linearLayout, int position) {
        linearLayout.startAnimation(removeItem(position));
    }

    private Animation removeItem(int position) {
        Animation animation = AnimationUtils.loadAnimation(context,
                R.anim.slide_out);
        animation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                searchModelList.remove(position);
                searchAdapter.notifyDataSetChanged();
                if (searchModelList.isEmpty()){
                    binding.txtSearch.setVisibility(View.GONE);
                }
            }
        });
        return animation;
    }

    public static SearchFragment newInstance() {
        SearchFragment fragment = new SearchFragment();
        return fragment;
    }

    private void showError(){
        binding.lnrError.setVisibility(View.VISIBLE);
    }
    private void hideError(){
        binding.lnrError.setVisibility(View.GONE);
    }
}