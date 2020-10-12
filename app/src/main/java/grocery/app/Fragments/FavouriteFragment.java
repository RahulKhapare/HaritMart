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
        //
        wishListModelList = new ArrayList<>();
        binding.receyclerWishList.setHasFixedSize(true);
        binding.receyclerWishList.setLayoutManager(new LinearLayoutManager(context));
        wishListAdapter = new WishListAdapter(context,wishListModelList,FavouriteFragment.this);
        binding.receyclerWishList.setAdapter(wishListAdapter);
        addWishListData();
    }


    private void addWishListData(){
        showProgress();
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
        binding.lnrError.setVisibility(View.VISIBLE);
    }
    private void hideError(){
        binding.lnrError.setVisibility(View.GONE);
    }

    public static FavouriteFragment newInstance() {
        FavouriteFragment fragment = new FavouriteFragment();

        return fragment;
    }


}