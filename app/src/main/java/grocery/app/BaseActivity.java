package grocery.app;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.view.GravityCompat;
import androidx.databinding.DataBindingUtil;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.adoisstudio.helper.Api;
import com.adoisstudio.helper.H;
import com.adoisstudio.helper.Json;
import com.adoisstudio.helper.Session;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import java.util.ArrayList;

import grocery.app.Fragments.CartFragment;
import grocery.app.Fragments.FavouriteFragment;
import grocery.app.Fragments.HomeFragment;
import grocery.app.Fragments.MoreFragment;
import grocery.app.Fragments.ProductListFragment;
import grocery.app.Fragments.SearchFragment;
import grocery.app.common.App;
import grocery.app.common.P;
import grocery.app.databinding.ActivityBaseBinding;
import grocery.app.util.Config;

public class BaseActivity extends AppCompatActivity {

    private HomeFragment homeFragment;
    private FavouriteFragment favouriteFragment;
    private SearchFragment searchFragment;
    private MoreFragment moreFragment;
    private CartFragment cartFragment;
    private FragmentManager fragmentManager;
    private ActivityBaseBinding binding;
    public ProductListFragment productListFragment;
    private BaseActivity activity = this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_base);
        fragmentManager = getSupportFragmentManager();

        hitCategoryApi();
        handleDrawerAnimation();

    }

    private void handleDrawerAnimation() {
        final DrawerLayout.LayoutParams layoutParams = (DrawerLayout.LayoutParams) binding.frameLayout.getLayoutParams();
        binding.drawerLayout.addDrawerListener(new DrawerLayout.DrawerListener() {
            @Override
            public void onDrawerSlide(@NonNull View drawerView, float slideOffset) {
                binding.frameLayout.setTranslationX((((float) drawerView.getWidth() * slideOffset)));
                binding.frameLayout.setLayoutParams(layoutParams);
            }

            @Override
            public void onDrawerOpened(@NonNull View drawerView) {
            }

            @Override
            public void onDrawerClosed(@NonNull View drawerView) {
            }

            @Override
            public void onDrawerStateChanged(int newState) {
            }
        });
    }


    private void hitCategoryApi() {
        Api.newApi(this, P.baseUrl + "categories").addJson(new Json())
                .setMethod(Api.GET)
                //.onHeaderRequest(App::getHeaders)
                .onError(() -> {
                    H.showMessage(this, "On error is called");
                })
                .onSuccess(json ->
                {
                    if (json.getInt(P.status) == 1) {
                        json = json.getJson(P.data);
                        App.categoryImageUrl = json.getString(P.category_image_path);
                        App.categoryJsonList = json.getJsonList(P.category_list);

                        setUpDrawerLayout();
                        homeFragment = HomeFragment.newInstance();
                        fragmentLoader(homeFragment);
                    } else
                        H.showMessage(this, json.getString(P.msg));
                })
                .run("hitCategoryApi");
    }

    private void setUpDrawerLayout() {
        TextView textView;
        for (int i = 0; i < App.categoryJsonList.size(); i++) {
            Json jsonDrawerItem = App.categoryJsonList.get(i);
            View viewItem = getLayoutInflater().inflate(R.layout.drawer_item_layout, null, false);
            viewItem.setTag(i);

            textView = viewItem.findViewById(R.id.txtCategory);
            textView.setText(jsonDrawerItem.getJson(P.category).getString(P.name));
            textView.setOnClickListener(v -> handleVisibilityOfContainer(v));

            for (int j = 0; j < jsonDrawerItem.getJsonList(P.children).size(); j++) {
                Json jsonDrawerSub = jsonDrawerItem.getJsonList(P.children).get(j);
                View viewCategory = getLayoutInflater().inflate(R.layout.drawer_item_category, null, false);
                viewCategory.setTag(j);

                textView = viewCategory.findViewById(R.id.txtSubCategory);
                textView.setText(jsonDrawerSub.getJson(P.category).getString(P.name));
                textView.setOnClickListener(v -> handleVisibilityOfContainer(v));

                for (int k = 0; k < jsonDrawerSub.getJsonList(P.children).size(); k++) {
                    Json jsonDrawerChild = jsonDrawerSub.getJsonList(P.children).get(k);
                    View viewChild = getLayoutInflater().inflate(R.layout.drawer_item_child, null, false);

                    viewChild.setTag(k);
                    textView = viewChild.findViewById(R.id.txtChild);
                    textView.setText(jsonDrawerChild.getJson(P.category).getString(P.name));
                    textView.setOnClickListener(v -> startProductListingFragment(v));

                    ((LinearLayout) viewCategory.findViewById(R.id.thirdContainer)).addView(viewChild);
                }

                ((LinearLayout) viewItem.findViewById(R.id.subCategoryContainer)).addView(viewCategory);
            }

            binding.categoryContainer.addView(viewItem);
        }
    }

    private void handleVisibilityOfContainer(View v) {
        LinearLayout linearLayout = (LinearLayout) v.getParent();
        linearLayout = (LinearLayout) linearLayout.getChildAt(1);

        if (linearLayout.getVisibility() == View.VISIBLE)
            linearLayout.setVisibility(View.GONE);
        else
            linearLayout.setVisibility(View.VISIBLE);
    }


    private void startProductListingFragment(View v) {
        try {

            FrameLayout frameLayout = (FrameLayout) v.getParent();
            int i = H.getInt(frameLayout.getTag() + "");
            ViewParent viewParent = v.getParent().getParent().getParent();
            LinearLayout linearLayout = (LinearLayout) viewParent;
            int j = H.getInt(linearLayout.getTag() + "");
            viewParent = viewParent.getParent().getParent();
            linearLayout = (LinearLayout) viewParent;
            int k = H.getInt(linearLayout.getTag() + "");

            Config.FROM_HOME = false;
            Config.CATEGORY_POSITION = k;
            Config.SUB_CATEGORY_POSITION = j;
            Config.CHILD_CATEGORY_POSITION = i;

            binding.drawerLayout.closeDrawer(GravityCompat.START);
            productListFragment = ProductListFragment.newInstance();
            fragmentLoader(productListFragment);

        }catch (Exception e){

        }

    }

    public void fragmentLoader(Fragment fragment) {
        fragmentManager.beginTransaction()
                .setCustomAnimations(R.anim.anim_enter, R.anim.anim_exit)
                .replace(R.id.frameLayoutChild, fragment)
                .commit();
    }

    public void onDrawerMenuClick(View view){
        try {
            if (homeFragment.isVisible()){
                binding.drawerLayout.openDrawer(GravityCompat.START);
            }
        }catch (Exception e){

        }
    }

    public void onBottomBarClick(View view) {
        int i = view.getId();
        selectBottomNavigation(view);
        switch (i) {
            case R.id.homeLayout: {
                homeFragment = HomeFragment.newInstance();
                fragmentLoader(homeFragment);
                break;
            }
            case R.id.favouriteLayout: {
                favouriteFragment = FavouriteFragment.newInstance();
                fragmentLoader(favouriteFragment);
                break;
            }
            case R.id.searchLayout: {
                searchFragment = SearchFragment.newInstance();
                fragmentLoader(searchFragment);
                break;
            }
            case R.id.cartLayout: {
                cartFragment = cartFragment.newInstance();
                fragmentLoader(cartFragment);
                break;
            }
            case R.id.moreLayout: {
                moreFragment = MoreFragment.newInstance();
                fragmentLoader(moreFragment);
                break;
            }
        }


    }

    private void selectBottomNavigation(View view) {
        LinearLayout parentLayout = findViewById(R.id.parentLinearLayout);
        int j = (int) H.convertDpToPixel(30, this);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(j, j);
        //LinearLayout linearLayout = (LinearLayout) view.getParent();
        LinearLayout childLayout;
        View v;
        ImageView imageView;
        TextView textView;
        for (int i = 0; i < parentLayout.getChildCount(); i++) {

            childLayout = (LinearLayout) parentLayout.getChildAt(i);
            imageView = childLayout.findViewWithTag("imageView");
            imageView.setColorFilter(getResources().getColor(R.color.grey1));

            textView = childLayout.findViewWithTag("textView");
            textView.setTextColor(getResources().getColor(R.color.grey1));


        }
        j = (int) H.convertDpToPixel(32, this);
        layoutParams = new LinearLayout.LayoutParams(j, j);

        imageView = view.findViewWithTag("imageView");
        imageView.setColorFilter(getResources().getColor(R.color.green));

        textView = view.findViewWithTag("textView");
        textView.setTextColor(getResources().getColor(R.color.green));


    }

}