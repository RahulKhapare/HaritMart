package grocery.app;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

import grocery.app.adapter.WishListAdapter;
import grocery.app.databinding.ActivityWishlistBinding;
import grocery.app.model.WishListModel;

public class WishlistActivity extends AppCompatActivity {

    private ActivityWishlistBinding binding;
    private WishlistActivity activity = this;
    private WishListAdapter adapter;
    private List<WishListModel> wishListModelList;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_wishlist);

        binding.toolbar.setTitle("Wishlist");
        setSupportActionBar(binding.toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        wishListModelList = new ArrayList<>();
        binding.recycelrWishList.setLayoutManager(new LinearLayoutManager(activity));
        binding.recycelrWishList.setHasFixedSize(true);
        adapter = new WishListAdapter(activity,getWishliatData());
        binding.recycelrWishList.setAdapter(adapter);

    }

    private List<WishListModel> getWishliatData()
    {
        List<WishListModel> wishListModels = new ArrayList<>();
        wishListModels.add(new WishListModel("image","200","89","3232","32","33"));
        wishListModels.add(new WishListModel("image","200","89","3232","32","33"));
        wishListModels.add(new WishListModel("image","200","89","3232","32","33"));
        wishListModels.add(new WishListModel("image","200","89","3232","32","33"));
        wishListModels.add(new WishListModel("image","200","89","3232","32","33"));
        wishListModels.add(new WishListModel("image","200","89","3232","32","33"));
        wishListModels.add(new WishListModel("image","200","89","3232","32","33"));
        wishListModels.add(new WishListModel("image","200","89","3232","32","33"));
        return wishListModels;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
        }
        return false;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}