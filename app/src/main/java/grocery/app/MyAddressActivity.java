package grocery.app;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.MenuItem;

import com.miguelcatalan.materialsearchview.MaterialSearchView;

import java.util.ArrayList;
import java.util.List;

import grocery.app.adapter.AddressAdapter;
import grocery.app.adapter.ProductListAdapter;
import grocery.app.databinding.ActivityMyAddressBinding;
import grocery.app.model.AddressModel;
import grocery.app.model.ProductModel;
import grocery.app.util.WindowBarColor;

public class MyAddressActivity extends AppCompatActivity {

    private ActivityMyAddressBinding binding;
    private MyAddressActivity activity = this;
    private MaterialSearchView searchView;
    private AddressAdapter adapter;
    private List<AddressModel> addressModelList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        WindowBarColor.setColor(this);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_my_address);

        binding.toolbar.setTitle("My Address");
        setSupportActionBar(binding.toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        initView();

    }

    private void initView(){
        addressModelList = new ArrayList<>();
        binding.recyclerAddress.setHasFixedSize(true);
        binding.recyclerAddress.setLayoutManager(new LinearLayoutManager(activity));
        adapter = new AddressAdapter(activity,addressModelList);
        binding.recyclerAddress.setAdapter(adapter);
        initSearchView();
    }

    private void initSearchView() {
        searchView = (MaterialSearchView) findViewById(R.id.search_view);
        searchView.setHint("Search product here");
        searchView.setTextColor(getResources().getColor(R.color.green));
        searchView.setHintTextColor(getResources().getColor(R.color.grey1));
        searchView.setVoiceSearch(true); //or false
        searchView.setOnQueryTextListener(new MaterialSearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                //Do some magic
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                //Do some magic
//                if (!TextUtils.isEmpty(newText)) {
//                    List<AddressModel> list = new ArrayList<AddressModel>();
//                    for (AddressModel model : addressModelList) {
//                        if (model.getAddress().toLowerCase().contains(newText.toLowerCase())) {
//                            list.add(model);
//                        }
//                    }
//                    adapter = new AddressAdapter(activity, list);
//                    binding.recyclerAddress.setAdapter(adapter);
//                    adapter.notifyDataSetChanged();
//                } else {
//                    adapter = new AddressAdapter(activity, addressModelList);
//                    binding.recyclerAddress.setAdapter(adapter);
//                }
                return false;
            }
        });
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