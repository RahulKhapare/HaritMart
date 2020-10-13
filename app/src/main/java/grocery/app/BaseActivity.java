package grocery.app;

import android.content.Intent;
import android.os.Bundle;
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

import grocery.app.Fragments.CartFragment;
import grocery.app.Fragments.FavouriteFragment;
import grocery.app.Fragments.HomeFragment;
import grocery.app.Fragments.MoreFragment;
import grocery.app.Fragments.SearchFragment;
import grocery.app.common.P;
import grocery.app.databinding.ActivityBaseBinding;
import grocery.app.util.Click;
import grocery.app.util.Config;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        WindowBarColor.setColor(activity);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_base);
        fragmentManager = getSupportFragmentManager();
        binding.txtAddress.setText(new Session(activity).getString(P.locationAddress));
        checkCartData = getIntent().getBooleanExtra(Config.CHECK_CART_DATA,false);

        if (checkCartData){
            onBottomBarClick(binding.cartLayout);
        }else {
            homeFragment = HomeFragment.newInstance();
            fragmentLoader(homeFragment, Config.HOME);
        }

    }

    public void onClickNotification(View view) {
        Intent notificationIntent = new Intent(activity,NotificationActivity.class);
        startActivity(notificationIntent);
    }

    public void onClickProfile(View view) {
        Intent accountIntent = new Intent(activity, MyAccountActivity.class);
        startActivity(accountIntent);
    }


    public void fragmentLoader(Fragment fragment, String tag) {
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
        switch (i) {
            case R.id.homeLayout: {
                homeFragment = HomeFragment.newInstance();
                fragmentLoader(homeFragment, Config.HOME);
                break;
            }
            case R.id.favouriteLayout: {
                favouriteFragment = FavouriteFragment.newInstance();
                fragmentLoader(favouriteFragment, Config.FAVORITE);
                break;
            }
            case R.id.searchLayout: {
                searchFragment = SearchFragment.newInstance();
                fragmentLoader(searchFragment, Config.SEARCH);
                break;
            }
            case R.id.cartLayout: {
                cartFragment = cartFragment.newInstance();
                fragmentLoader(cartFragment, Config.CART);
                break;
            }
            case R.id.moreLayout: {
                moreFragment = MoreFragment.newInstance();
                fragmentLoader(moreFragment, Config.MORE);
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


    public void onBackAction(){
        int count = getSupportFragmentManager().getBackStackEntryCount();
        if (count >0) {
            if (count>1){
                String title = getSupportFragmentManager().getBackStackEntryAt(count - 2).getName();
                getSupportFragmentManager().popBackStack();
                updateBottomIcon(title);
            }else {
                finishAction();
            }
        } else {
            finishAction();
        }
    }

    private void updateBottomIcon(String tag) {
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
    }

    @Override
    public void onBackPressed() {
        onBackAction();
    }

    private void finishAction(){
        if (back_pressed + TIME_DELAY > System.currentTimeMillis()) {
            finish();
        } else {
            H.showMessage(activity,"Press once again to exit!");
        }
        back_pressed = System.currentTimeMillis();
    }

}