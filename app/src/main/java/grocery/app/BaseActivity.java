package grocery.app;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.adoisstudio.helper.H;
import com.adoisstudio.helper.Session;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;

import grocery.app.Fragments.CartFragment;
import grocery.app.Fragments.FavouriteFragment;
import grocery.app.Fragments.HomeFragment;
import grocery.app.Fragments.MoreFragment;
import grocery.app.Fragments.SearchFragment;
import grocery.app.common.P;
import grocery.app.databinding.ActivityBaseBinding;
import grocery.app.util.Click;
import grocery.app.util.Config;
import grocery.app.util.LoginFlag;
import grocery.app.util.PageUtil;
import grocery.app.util.WindowBarColor;

public class BaseActivity extends AppCompatActivity {

    private HomeFragment homeFragment;
    private FavouriteFragment favouriteFragment;
    private SearchFragment searchFragment;
    private MoreFragment moreFragment;
    private CartFragment cartFragment;
    private FragmentManager fragmentManager;
    private ActivityBaseBinding binding;
    private BaseActivity activity = this;
    private boolean checkCartData = false;
    private final int TIME_DELAY = 2000;
    private long back_pressed;
    private Session session;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        WindowBarColor.setColor(activity);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_base);
        fragmentManager = getSupportFragmentManager();
        checkCartData = getIntent().getBooleanExtra(Config.CHECK_CART_DATA, false);
        session = new Session(activity);

        if (checkCartData) {
            onBottomBarClick(binding.cartLayout);
        } else {
            homeFragment = HomeFragment.newInstance();
            fragmentLoader(homeFragment, Config.HOME);
            checkLoginFlag();
        }

    }

    private void checkLoginFlag() {

        Intent appLinkIntent = getIntent();
        String appLinkAction = appLinkIntent.getAction();
        Uri appLinkData = appLinkIntent.getData();
        String shareUrl = appLinkData+"";
        String filterId  = shareUrl.replaceAll("[^0-9]", "");

        if (!TextUtils.isEmpty(filterId)){
            Intent productIntent = new Intent(activity, ProductDetailsActivity.class);
            productIntent.putExtra(Config.PRODUCT_ID, "0");
            productIntent.putExtra(Config.PRODUCT_FILTER_ID, filterId);
            startActivity(productIntent);
        }else if (LoginFlag.productDetailFlag) {
            LoginFlag.productDetailFlag = false;
            Intent productIntent = new Intent(activity, ProductDetailsActivity.class);
            productIntent.putExtra(Config.PRODUCT_ID, LoginFlag.loginProductId);
            productIntent.putExtra(Config.PRODUCT_FILTER_ID, LoginFlag.loginFilterId);
            startActivity(productIntent);
        } else if (LoginFlag.notificationFlag) {
            LoginFlag.notificationFlag = false;
            Intent notificationIntent = new Intent(activity, NotificationActivity.class);
            startActivity(notificationIntent);
        } else if (LoginFlag.profileFlag) {
            LoginFlag.profileFlag = false;
            Intent notificationIntent = new Intent(activity, MyAccountActivity.class);
            startActivity(notificationIntent);
        } else if (LoginFlag.cartFlag) {
            LoginFlag.cartFlag = false;
            onBottomBarClick(binding.cartLayout);
        } else if (LoginFlag.favoriteFlag) {
            LoginFlag.favoriteFlag = false;
            onBottomBarClick(binding.favouriteLayout);
        }
    }

    public void onClickNotification(View view) {
        if (session.getBool(P.isUserLogin)) {
            Intent notificationIntent = new Intent(activity, NotificationActivity.class);
            startActivity(notificationIntent);
        } else {
            PageUtil.goToLoginPage(activity, LoginFlag.notificationFlagValue);
        }
    }

    public void onClickProfile(View view) {
        if (session.getBool(P.isUserLogin)) {
            Intent accountIntent = new Intent(activity, MyAccountActivity.class);
            startActivity(accountIntent);
        } else {
            PageUtil.goToLoginPage(activity, LoginFlag.profileFlagValue);
        }
    }


    public void fragmentLoader(Fragment fragment, String tag) {
        Config.currentFlag = tag;
        fragmentManager.beginTransaction()
                .setCustomAnimations(R.anim.anim_enter, R.anim.anim_exit)
                .replace(R.id.frameLayoutChild, fragment, tag)
                .addToBackStack(tag)
                .commit();
    }

    public void onBottomBarClick(View view) {
        int i = view.getId();
        selectBottomNavigation(view);
        Click.preventTwoClick(view);
        String currentFlag = Config.currentFlag;
        switch (i) {
            case R.id.homeLayout: {
                if (!TextUtils.isEmpty(currentFlag) && !currentFlag.equals(Config.HOME)) {
                    homeFragment = HomeFragment.newInstance();
                    fragmentLoader(homeFragment, Config.HOME);
                }
                break;
            }
            case R.id.favouriteLayout: {
                if (session.getBool(P.isUserLogin)) {
                    if (!TextUtils.isEmpty(currentFlag) && !currentFlag.equals(Config.FAVORITE)) {
                        favouriteFragment = FavouriteFragment.newInstance();
                        fragmentLoader(favouriteFragment, Config.FAVORITE);
                    }
                } else {
                    PageUtil.goToLoginPage(activity, LoginFlag.favoriteFlagValue);
                }
                break;
            }
            case R.id.searchLayout: {
                if (!TextUtils.isEmpty(currentFlag) && !currentFlag.equals(Config.SEARCH)) {
                    searchFragment = SearchFragment.newInstance();
                    fragmentLoader(searchFragment, Config.SEARCH);
                }
                break;
            }
            case R.id.cartLayout: {
                if (!TextUtils.isEmpty(currentFlag) && !currentFlag.equals(Config.CART)) {
                    cartFragment = CartFragment.newInstance();
                    fragmentLoader(cartFragment, Config.CART);
                }
                break;
            }
            case R.id.moreLayout: {
                if (!TextUtils.isEmpty(currentFlag) && !currentFlag.equals(Config.MORE)) {
                    moreFragment = MoreFragment.newInstance();
                    fragmentLoader(moreFragment, Config.MORE);
                }
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


    public void onBackAction() {

//        int count = getSupportFragmentManager().getBackStackEntryCount();
//        if (count > 0) {
//            if (count > 1){
//                String title = getSupportFragmentManager().getBackStackEntryAt(count - 2).getName();
//                getSupportFragmentManager().popBackStack();
//                updateBottomIcon(title);
//            }else {
//                finishAction();
//            }
//        } else {
//            finishAction();
//        }

        try {
            HomeFragment homeFragment = (HomeFragment) getSupportFragmentManager().findFragmentByTag(Config.HOME);
            if (homeFragment != null && homeFragment.isVisible()) {
                finishAction();
            } else {
                if (getSupportFragmentManager().getBackStackEntryCount() > 0) {
                    if (!(getSupportFragmentManager().getBackStackEntryCount() == 1)) {
                        getSupportFragmentManager().popBackStack();
                        String title = getSupportFragmentManager().getBackStackEntryAt(getSupportFragmentManager().getBackStackEntryCount() - 2).getName();
                        updateBottomIcon(title);
                    } else {
                        if (checkCartData) {
                            onBottomBarClick(binding.homeLayout);
                        } else {
                            finishAction();
                        }
                    }
                } else {
                    finishAction();
                }
            }
        } catch (Exception e) {

        }

    }

    private void updateBottomIcon(String tag) {
        Config.currentFlag = tag;
        if (tag.equals(Config.HOME)) {
            selectBottomNavigation(binding.homeLayout);
        } else if (tag.equals(Config.FAVORITE)) {
            selectBottomNavigation(binding.favouriteLayout);
        } else if (tag.equals(Config.SEARCH)) {
            selectBottomNavigation(binding.searchLayout);
        } else if (tag.equals(Config.CART)) {
            selectBottomNavigation(binding.cartLayout);
        } else if (tag.equals(Config.MORE)) {
            selectBottomNavigation(binding.moreLayout);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        binding.txtAddress.setText(new Session(activity).getString(P.googleAddress));
    }

    @Override
    public void onBackPressed() {
        onBackAction();
    }

    private void finishAction() {
        if (back_pressed + TIME_DELAY > System.currentTimeMillis()) {
            finishAffinity();
            finish();
        } else {
            H.showMessage(activity, "Press once again to exit!");
        }
        back_pressed = System.currentTimeMillis();
    }

}