package grocery.app.Fragments;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.adoisstudio.helper.LoadingDialog;

import java.util.ArrayList;
import java.util.List;

import grocery.app.R;
import grocery.app.adapter.WishListAdapter;
import grocery.app.databinding.FragmentFavouriteBinding;
import grocery.app.model.WishListModel;

public class FavouriteFragment extends Fragment implements WishListAdapter.Click{

    private FragmentFavouriteBinding binding;
    private Context context;
    private WishListAdapter wishListAdapter;
    private List<WishListModel> wishListModelList;
    private LoadingDialog loadingDialog;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_favourite, container, false);
        context = inflater.getContext();
        loadingDialog = new LoadingDialog(context);
        initView();
        return binding.getRoot();
    }

    private void initView(){
        wishListModelList = new ArrayList<>();
        binding.receyclerWishList.setHasFixedSize(true);
        binding.receyclerWishList.setLayoutManager(new LinearLayoutManager(context));
        wishListAdapter = new WishListAdapter(context,wishListModelList,FavouriteFragment.this);
        binding.receyclerWishList.setAdapter(wishListAdapter);
        addWishListData();
    }


    private void addWishListData(){
        showProgress();
        for (int i = 0; i < 5; i++) {
            WishListModel model = new WishListModel();
            model.setId("1");
            model.setTemp_id("1");
            model.setProduct_id("1");
            model.setProducts_variants_id("1");
            model.setQty("0");
            model.setOption1("");
            model.setOption2("");
            model.setOption3("");
            model.setTotal_price("200");
            model.setPrice("50");
            model.setCoupon_discount_amount("20");
//            model.setCart_image(jsonObject.getString("image"));
            model.setName("Tomato");
            wishListModelList.add(model);
        }
        wishListAdapter.notifyDataSetChanged();
        hideProgress();
        checkError();
    }

    @Override
    public void addFavorite() {

    }

    private void checkError(){
        if (wishListModelList.isEmpty()){
            showError();
        }else {
            hideError();
        }
    }

    private void showProgress() {
        loadingDialog.show("Please wait..");
    }

    private void hideProgress() {
        loadingDialog.hide();
    }

    private void showError(){
        if (binding.lnrError.getVisibility()==View.GONE){
            binding.lnrError.setVisibility(View.VISIBLE);
        }
    }
    private void hideError(){
        if (binding.lnrError.getVisibility()==View.VISIBLE){
            binding.lnrError.setVisibility(View.GONE);
        }
    }

    public static FavouriteFragment newInstance() {
        FavouriteFragment fragment = new FavouriteFragment();

        return fragment;
    }


}