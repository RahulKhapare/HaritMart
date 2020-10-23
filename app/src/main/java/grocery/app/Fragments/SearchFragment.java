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

import com.adoisstudio.helper.Api;
import com.adoisstudio.helper.H;
import com.adoisstudio.helper.Json;
import com.adoisstudio.helper.JsonList;
import com.adoisstudio.helper.LoadingDialog;
import com.adoisstudio.helper.Session;

import java.util.ArrayList;
import java.util.List;

import grocery.app.R;
import grocery.app.adapter.SearchAdapter;
import grocery.app.common.App;
import grocery.app.common.P;
import grocery.app.databinding.FragmentSearchBinding;
import grocery.app.model.SearchModel;
import grocery.app.util.Config;


public class SearchFragment extends Fragment implements SearchAdapter.Click{

    private FragmentSearchBinding binding;
    private Context context;
    private SearchAdapter trendAdapter;
    private List<SearchModel> trendModelList;
    private LoadingDialog loadingDialog;
    private Session session;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_search, container, false);
        context = inflater.getContext();
        loadingDialog = new LoadingDialog(context);
        session = new Session(context);
        initView();
        return binding.getRoot();
    }

    private void initView() {
        trendModelList = new ArrayList<>();

        binding.recyclerTrendingSearches.setHasFixedSize(true);
        binding.recyclerTrendingSearches.setNestedScrollingEnabled(false);
        binding.recyclerTrendingSearches.setLayoutManager(new LinearLayoutManager(context));
        trendAdapter = new SearchAdapter(context, trendModelList, true,SearchFragment.this);
        binding.recyclerTrendingSearches.setAdapter(trendAdapter);

        onSearchView();
        hitHomeApi();

    }

    @Override
    public void onResume() {
        super.onResume();
    }

    private void showLoader() {
        loadingDialog.show("Please wait...");
    }

    private void hideLoader() {
        loadingDialog.hide();
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
                    trendAdapter.notifyDataSetChanged();
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

    private void hitHomeApi() {
        showLoader();
        try {
            Json j = new Json();
            if (session.getBool(P.isUserLogin)){
                j.addInt(P.user_id, H.getInt(session.getString(P.user_id)));
            }else {
                j.addInt(P.user_id, Config.commonUserHomeID);
            }
            Api.newApi(context, P.baseUrl + "home").addJson(j)
                    .setMethod(Api.POST)
                    //.onHeaderRequest(App::getHeaders)
                    .onError(() -> {
                        hideLoader();
                        showError();
                        H.showMessage(context, "On error is called");
                    })
                    .onSuccess(json -> {
                        if (json.getInt(P.status) == 1) {
                            json = json.getJson(P.data);
                            App.homeJSONDATA = json;
                            App.product_image_path = json.getString(P.product_image_path);
                            setUpTrendingProductList(json.getJsonList(P.under_50_product));
                        } else {
                            showError();
                        }
                        hideLoader();
                    })
                    .run("hitHomeApi");
        } catch (Exception e) {
            hideLoader();
        }

    }

    private void setUpTrendingProductList(JsonList jsonList) {
        trendModelList.clear();
        for (Json json : jsonList) {
            SearchModel model = new SearchModel();
            model.setId(json.getString(P.id));
            model.setFilter_id(json.getString(P.filter_id));
            model.setName(json.getString(P.name));
            model.setSlug(json.getString(P.slug));
            model.setVariants_name(json.getString(P.variants_name));
            model.setProduct_image(json.getString(P.product_image));
            try {
                Json priceJson = json.getJson(P.price);
                model.setPrice(priceJson.getString(P.price));
                model.setSaleprice(priceJson.getString(P.saleprice));
                model.setDiscount_amount(priceJson.getString(P.discount_amount));
                model.setDiscount(priceJson.getString(P.discount));
            }catch (Exception e){

            }
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