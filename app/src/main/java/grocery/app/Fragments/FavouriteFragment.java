package grocery.app.Fragments;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import androidx.cardview.widget.CardView;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.adoisstudio.helper.Api;
import com.adoisstudio.helper.H;
import com.adoisstudio.helper.Json;
import com.adoisstudio.helper.LoadingDialog;
import com.adoisstudio.helper.Session;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import grocery.app.R;
import grocery.app.adapter.WishListAdapter;
import grocery.app.common.P;
import grocery.app.databinding.FragmentFavouriteBinding;
import grocery.app.model.WishListModel;
import grocery.app.util.Config;

public class FavouriteFragment extends Fragment implements WishListAdapter.Click{

    private FragmentFavouriteBinding binding;
    private Context context;
    private WishListAdapter wishListAdapter;
    private List<WishListModel> wishListModelList;
    private LoadingDialog loadingDialog;
    private Session session;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_favourite, container, false);
        context = inflater.getContext();
        loadingDialog = new LoadingDialog(context);
        session = new Session(context);
        initView();
        return binding.getRoot();
    }

    private void initView(){
        wishListModelList = new ArrayList<>();
        binding.receyclerWishList.setHasFixedSize(true);
        binding.receyclerWishList.setLayoutManager(new LinearLayoutManager(context));
        wishListAdapter = new WishListAdapter(context,wishListModelList,FavouriteFragment.this);
        binding.receyclerWishList.setAdapter(wishListAdapter);
        hitForWishList();
    }


    @Override
    public void removeFavorite(int filterId, CardView cardView,int position) {
        Json json = new Json();
        if (session.getBool(P.isUserLogin)){
            json.addInt(P.user_id, H.getInt(session.getString(P.user_id)));
        }else {
            json.addInt(P.user_id, Config.commonUserID);
        }
        json.addInt(P.wishlist_id, filterId);
        hitRemoveToWishList(json,cardView,position);
    }

    private void checkError(){
        if (wishListModelList.isEmpty()){
            showError();
        }else {
            hideError();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if(Config.Update_Favorite_Wish){
            Config.Update_Favorite_Wish = false;
            hitForWishList();
        }
    }

    private void hitForWishList() {
        wishListModelList.clear();
        showProgress();
        Json j = new Json();
        if (session.getBool(P.isUserLogin)){
            j.addInt(P.user_id, H.getInt(session.getString(P.user_id)));
        }else {
            j.addInt(P.user_id, Config.commonUserID);
        }
        Api.newApi(context, P.baseUrl + "wishlist").addJson(j)
                .setMethod(Api.POST)
                //.onHeaderRequest(App::getHeaders)
                .onError(() -> {
                    hideProgress();
                    checkError();
                    H.showMessage(context, "On error is called");
                })
                .onSuccess(json ->
                {
                    wishListModelList.clear();
                    if (json.getInt(P.status) == 1) {
                        json = json.getJson(P.data);
                        String imagePath = json.getString(P.product_image_path);
                        JSONArray jsonArray = json.getJsonArray(P.list);
                        for (int i = 0; i < jsonArray.length(); i++) {
                            try {
                                JSONObject jsonObject = jsonArray.getJSONObject(i);
                                WishListModel model = new WishListModel();

                                model.setProduct_image_path(imagePath);
                                model.setWishlist_id(jsonObject.getString(P.wishlist_id));
                                model.setCategory_name(jsonObject.getString(P.category_name));
                                model.setId(jsonObject.getString(P.id));
                                model.setFilter_id(jsonObject.getString(P.filter_id));
                                model.setName(jsonObject.getString(P.name));
                                model.setVariants_name(jsonObject.getString(P.variants_name));
                                model.setProduct_image(jsonObject.getString(P.product_image));

                                wishListModelList.add(model);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                        wishListAdapter.notifyDataSetChanged();
                    } else{

                    }
                    hideProgress();
                    checkError();
                })
                .run("hitForWishList");
    }

    private void hitRemoveToWishList(Json j, CardView cardView,int position) {
        showProgress();
        Api.newApi(context, P.baseUrl + "remove_from_wishlist").addJson(j)
                .setMethod(Api.POST)
                //.onHeaderRequest(App::getHeaders)
                .onError(() -> {
                    hideProgress();
                    H.showMessage(context, "On error is called");
                })
                .onSuccess(json ->
                {
                    if (json.getInt(P.status) == 1) {
                        H.showMessage(context, "Item removed from favorite");
                        cardView.startAnimation(removeItem(position));
                    } else{
                        H.showMessage(context, json.getString(P.msg));
                    }
                    hideProgress();
                })
                .run("hitRemoveToWishList");
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
                wishListModelList.remove(position);
                wishListAdapter.notifyDataSetChanged();
                if (wishListModelList.isEmpty()){
                    showError();
                }
            }
        });
        return animation;
    }

    public static FavouriteFragment newInstance() {
        FavouriteFragment fragment = new FavouriteFragment();

        return fragment;
    }


}