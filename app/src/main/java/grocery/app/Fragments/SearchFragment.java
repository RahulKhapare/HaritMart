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

import java.util.ArrayList;
import java.util.List;

import grocery.app.R;
import grocery.app.adapter.ProductListAdapter;
import grocery.app.adapter.SearchAdapter;
import grocery.app.databinding.FragmentSearchBinding;
import grocery.app.model.ProductModel;
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
        searchAdapter = new SearchAdapter(context, searchModelList, false,SearchFragment.this);
        binding.recyclerSearchHistory.setAdapter(searchAdapter);

        binding.recyclerTrendingSearches.setHasFixedSize(true);
        binding.recyclerTrendingSearches.setNestedScrollingEnabled(false);
        binding.recyclerTrendingSearches.setLayoutManager(new LinearLayoutManager(context));
        trendAdapter = new SearchAdapter(context, trendModelList, true,SearchFragment.this);
        binding.recyclerTrendingSearches.setAdapter(trendAdapter);

        updateSearchData();
        updateTrendSearchData();
        onSearchView();

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
                    for (SearchModel model : searchModelList) {
                        if (model.getTitle().toLowerCase().contains(newText.toLowerCase())) {
                            list.add(model);
                        }
                    }
                    searchAdapter = new SearchAdapter(context, list,false,SearchFragment.this);
                    binding.recyclerSearchHistory.setAdapter(searchAdapter);
                    searchAdapter.notifyDataSetChanged();
                } else {
                    searchAdapter = new SearchAdapter(context, searchModelList,false,SearchFragment.this);
                    binding.recyclerSearchHistory.setAdapter(searchAdapter);
                }
            }
            @Override
            public void afterTextChanged(Editable s) {
            }
        });
    }

    private void updateSearchData() {
        searchModelList.clear();
        searchModelList.add(new SearchModel("http://jjjj", "Tomato"));
        searchModelList.add(new SearchModel("http://jjjj", "Banana"));
        searchAdapter.notifyDataSetChanged();
    }

    private void updateTrendSearchData() {
        trendModelList.clear();
        trendModelList.add(new SearchModel("http://jjjj", "Tomato"));
        trendModelList.add(new SearchModel("http://jjjj", "Banana"));
        trendModelList.add(new SearchModel("http://jjjj", "Painapple"));
        trendModelList.add(new SearchModel("http://jjjj", "Peru"));
        trendModelList.add(new SearchModel("http://jjjj", "Orange"));
        trendModelList.add(new SearchModel("http://jjjj", "Graps"));
        trendModelList.add(new SearchModel("http://jjjj", "Lemon"));
        trendAdapter.notifyDataSetChanged();
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
                    binding.txtSearchHistory.setVisibility(View.GONE);
                }
            }
        });
        return animation;
    }

    public static SearchFragment newInstance() {
        SearchFragment fragment = new SearchFragment();
        return fragment;
    }
}