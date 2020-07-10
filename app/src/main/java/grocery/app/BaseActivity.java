package grocery.app;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
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
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.adoisstudio.helper.H;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import java.util.ArrayList;

import grocery.app.Fragments.CartFragment;
import grocery.app.Fragments.FavouriteFragment;
import grocery.app.Fragments.HomeFragment;
import grocery.app.Fragments.MoreFragment;
import grocery.app.Fragments.SearchFragment;

public class BaseActivity extends AppCompatActivity {
    private HomeFragment homeFragment;
    private FavouriteFragment favouriteFragment;
    private SearchFragment searchFragment;
    private MoreFragment moreFragment;
    private CartFragment cartFragment;
    private FragmentManager fragmentManager;
    private long l;
    TextView textView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_base);
        ViewPager2 viewPager2 = findViewById(R.id.onBoardViewPager);
      //  RecyclerView recyclerView = findViewById(R.id.recyclerView1);
        //viewPager2.setAdapter(onBoardingAdapter);
        /*TabLayout tabLayout = findViewById(R.id.tabLayout);
        TabLayoutMediator tabLayoutMediator = new TabLayoutMediator(tabLayout, viewPager2, (tab, position) -> {

        });
        tabLayoutMediator.attach();*/

        homeFragment = HomeFragment.newInstance();
        fragmentManager = getSupportFragmentManager();
        setUpRecyclerView();
        fragmentLoader(homeFragment);
    }

    private void setUpRecyclerView() {
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getBaseContext(),LinearLayoutManager.HORIZONTAL,false);
    }



   /* public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{

        public RecyclerViewAdapter(ArrayList<CardView> cardViewArrayList, ArrayList<ImageView> imageViews, ArrayList<TextView> textViews, Context mContext) {
            this.cardViewArrayList = cardViewArrayList;
            this.imageViews = imageViews;
            this.textViews = textViews;
            this.mContext = mContext;
        }

        private ArrayList<CardView> cardViewArrayList = new ArrayList<>();
        private ArrayList<ImageView> imageViews = new ArrayList<>();
        private ArrayList<TextView> textViews = new ArrayList<>();
        Context mContext;

        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.new_arrived_layout,parent,false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

        }

        @Override
        public int getItemCount() {
            return textViews.size();
        }

        public  class ViewHolder extends RecyclerView.ViewHolder{
            CardView cardView;
            ImageView imageView;

            public ViewHolder(@NonNull View itemView) {
                super(itemView);
                cardView=itemView.findViewById(R.id.cardView);
                imageView=itemView.findViewById(R.id.veg);


            }
        }
    }*/




    private void fragmentLoader(Fragment fragment) {
        fragmentManager.beginTransaction()
                .setCustomAnimations(R.anim.anim_enter, R.anim.anim_exit)
                .replace(R.id.frameLayout, fragment)
                .commit();


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




        }
        j = (int)H.convertDpToPixel(32,this);
        layoutParams = new LinearLayout.LayoutParams(j,j);
        imageView = (ImageView) ((LinearLayout) view).getChildAt(0);
        imageView.setColorFilter(getResources().getColor(R.color.green));
        imageView.setLayoutParams(layoutParams);


    }
}