package grocery.app;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.viewpager2.widget.ViewPager2;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.adoisstudio.helper.H;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import grocery.app.Fragments.HomeFragment;

public class BaseActivity extends AppCompatActivity {
    private HomeFragment homeFragment;
    private FragmentManager fragmentManager;
    private long l;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_base);
        ViewPager2 viewPager2 = findViewById(R.id.onBoardViewPager);
        //viewPager2.setAdapter(onBoardingAdapter);
        /*TabLayout tabLayout = findViewById(R.id.tabLayout);
        TabLayoutMediator tabLayoutMediator = new TabLayoutMediator(tabLayout, viewPager2, (tab, position) -> {

        });
        tabLayoutMediator.attach();*/

        homeFragment = HomeFragment.newInstance();
        fragmentManager = getSupportFragmentManager();
      //  fragmentLoader(homeFragment, "");
    }

  /*  private void fragmentLoader(Fragment fragment, String string) {
        if (fragment.isVisible())
            return;

        if (System.currentTimeMillis() - l > 321)
            l = System.currentTimeMillis();
        else
            return;

        decideBottomSelection(string);

        if (arrayList.contains(string)) {
            if (fragment instanceof HomeFragment) {
                int count = fragmentManager.getBackStackEntryCount();
                for (int i = 1; i < count; i++)
                    onBackClick(new View(this));
                return;
            }
        }

        fragmentManager.beginTransaction().setCustomAnimations(R.anim.anim_enter, R.anim.anim_exit).replace(R.id.frameLayout, fragment).addToBackStack(string).commit();

    }*/

    public void onBottomBarClick(View view) {
        int j = (int) H.convertDpToPixel(30, this);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(j, j);
        LinearLayout linearLayout = (LinearLayout) view.getParent();
        LinearLayout childLayout;
        View v;
        ImageView imageView;
        TextView textView;
        for (int i = 0; i < linearLayout.getChildCount(); i++) {
            childLayout = (LinearLayout) linearLayout.getChildAt(i);
            imageView = (ImageView) childLayout.getChildAt(0);
                       imageView.setColorFilter(getResources().getColor(R.color.grey1));
                       imageView.setLayoutParams(layoutParams);
         /*   v = linearLayout.getChildAt(i);
            if (v instanceof TextView) {
                textView = (TextView) v;
                textView.setTextSize(14);
                textView.setTextColor(getResources().getColor(R.color.green));
            }*/

        }
        j = (int)H.convertDpToPixel(32,this);
        layoutParams = new LinearLayout.LayoutParams(j,j);
        imageView = (ImageView) ((LinearLayout) view).getChildAt(0);
        imageView.setColorFilter(getResources().getColor(R.color.green));
        imageView.setLayoutParams(layoutParams);
      /*  textView = (TextView) view;
        textView.setTextSize(16);
*/
    }
}