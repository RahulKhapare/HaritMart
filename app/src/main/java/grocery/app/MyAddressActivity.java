package grocery.app;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.adoisstudio.helper.H;
import com.miguelcatalan.materialsearchview.MaterialSearchView;

import java.util.ArrayList;
import java.util.List;

import grocery.app.adapter.AddressAdapter;
import grocery.app.databinding.ActivityMyAddressBinding;
import grocery.app.model.AddressModel;
import grocery.app.util.Config;
import grocery.app.util.WindowBarColor;

public class MyAddressActivity extends AppCompatActivity implements View.OnClickListener {

    private ActivityMyAddressBinding binding;
    private MyAddressActivity activity = this;
    private MaterialSearchView searchView;
    private AddressAdapter adapter;
    public static List<AddressModel> addressModelList;
    private String currentAddress;
    public static boolean checkAddress;
    public boolean forCheckOut;

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

        forCheckOut = getIntent().getBooleanExtra(Config.FOR_CHECKOUT,false);

        checkAddress = true;
        addressModelList = new ArrayList<>();

        binding.recyclerAddress.setHasFixedSize(true);
        binding.recyclerAddress.setLayoutManager(new LinearLayoutManager(activity));
        adapter = new AddressAdapter(activity,addressModelList,forCheckOut);
        binding.recyclerAddress.setAdapter(adapter);

        binding.lnrAddress.setOnClickListener(this);
        binding.cardLocation.setOnClickListener(this);

        initSearchView();

        if (forCheckOut){
            binding.lnrTitle.setVisibility(View.GONE);
            binding.cardLocation.setVisibility(View.GONE);
//            binding.lnrAddress.setVisibility(View.GONE);
        }
    }

    private void hitAddressData(){
        adapter.notifyDataSetChanged();
    }

    @Override
    protected void onResume() {
        super.onResume();
        currentAddress = getIntent().getStringExtra(Config.ADDRESS_LOCATION);
        if (!TextUtils.isEmpty(currentAddress)){
            H.showMessage(activity,currentAddress);
        }

        if (checkAddress){
            checkAddress = false;
            hitAddressData();
        }

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.lnrAddress:
                Intent addressIntent = new Intent(activity,NewAddressActivity.class);
                startActivity(addressIntent);
                break;
            case R.id.cardLocation:
                Config.FROM_ADDRESS = true;
                Intent locationIntent = new Intent(activity,SetLocationActivity.class);
                locationIntent.putExtra(Config.GET_CURRENT_LOCATION,true);
                startActivity(locationIntent);
                finish();
                break;
        }
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
                if (!TextUtils.isEmpty(newText)) {
                    List<AddressModel> list = new ArrayList<AddressModel>();
                    for (AddressModel model : addressModelList) {
                        if (model.getApartmentName().toLowerCase().contains(newText.toLowerCase()) || model.getStreetAddress().toLowerCase().contains(newText.toLowerCase())
                        || model.getLandMark().toLowerCase().contains(newText.toLowerCase())) {
                            list.add(model);
                        }
                    }
                    adapter = new AddressAdapter(activity, list,forCheckOut);
                    binding.recyclerAddress.setAdapter(adapter);
                    adapter.notifyDataSetChanged();
                } else {
                    adapter = new AddressAdapter(activity, addressModelList,forCheckOut);
                    binding.recyclerAddress.setAdapter(adapter);
                }
                return false;
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.product, menu);
        MenuItem item = menu.findItem(R.id.action_search);
        MenuItem filter = menu.findItem(R.id.action_filter);
        searchView.setMenuItem(item);
        filter.setVisible(false);
        return true;
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
        if (searchView.isSearchOpen()) {
            searchView.closeSearch();
        } else {
            super.onBackPressed();
        }
    }
}