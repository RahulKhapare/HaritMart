package grocery.app;

import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.miguelcatalan.materialsearchview.MaterialSearchView;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import grocery.app.adapter.LocationAdapter;
import grocery.app.databinding.ActivitySearchLocationBinding;
import grocery.app.model.LocationModel;
import grocery.app.util.WindowBarColor;

public class SearchLocationActivity extends AppCompatActivity implements LocationAdapter.ClickInterface {

    private SearchLocationActivity activity = this;
    private ActivitySearchLocationBinding binding;
    private List<LocationModel> locationModelList;
    private MaterialSearchView searchView;
    private LocationAdapter locationAdapter;
    private Geocoder geocoder;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        WindowBarColor.setColor(this);
        binding = DataBindingUtil.setContentView(activity, R.layout.activity_search_location);
        binding.toolbar.setTitle("");
        setSupportActionBar(binding.toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        initView();
        initSearchView();
    }

    private void initView() {
        geocoder = new Geocoder(this, Locale.getDefault());
        binding.txtTitle.setText("Current Location");
        binding.txtAddress.setText("Using GPS");
        locationModelList = new ArrayList<>();
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(activity);
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        binding.recyelrAddress.setLayoutManager(linearLayoutManager);
        binding.recyelrAddress.setHasFixedSize(true);
        binding.recyelrAddress.setNestedScrollingEnabled(false);
        locationAdapter = new LocationAdapter(activity, locationModelList);
        binding.recyelrAddress.setAdapter(locationAdapter);

        binding.lnrCurrentLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SetLocationActivity.isCurrentLocationFromSearch = true;
                finishView();
            }
        });
    }


    private void initSearchView() {
        searchView = (MaterialSearchView) findViewById(R.id.search_view);
        searchView.setHint("Search");
        searchView.setTextColor(getResources().getColor(R.color.grey1));
        searchView.setHintTextColor(getResources().getColor(R.color.grey1));
        searchView.setOnQueryTextListener(new MaterialSearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                //Do some magic
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                //Do some magic
                if (!TextUtils.isEmpty(newText)) {
//                    Geocoder geocoder = new Geocoder(activity);
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            List<Address> addressList = null;
                            try {
                                addressList = geocoder.getFromLocationName(newText, 1);
                                for (int i = 0; i < addressList.size(); i++) {
                                    Address address = addressList.get(0);
                                    addAddress(address, newText);
                                }
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }, 1000);

                } else {
                    locationAdapter = new LocationAdapter(activity, locationModelList);
                    binding.recyelrAddress.setAdapter(locationAdapter);
                }
                return false;
            }
        });

        searchView.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_DEL) {

                }
                return false;
            }
        });
    }

    private void addAddress(Address address, String title) {
        LocationModel model = new LocationModel();
        model.setTitle(title);
        model.setLatitute(address.getLatitude());
        model.setLognitute(address.getLongitude());
        model.setAddress(getAddress(address.getLatitude(), address.getLongitude()));
        locationModelList.add(model);
        locationAdapter.notifyDataSetChanged();
    }

    private String getAddress(double currentLat, double currentLong) {
        String addressData = "";
        List<Address> addresses;
        try {
            addresses = geocoder.getFromLocation(currentLat, currentLong, 1); // Here 1 represent max location result to returned, by documents it recommended 1 to 5
            String address = addresses.get(0).getAddressLine(0); // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()
            addressData = address + "";
        } catch (IOException e) {
            e.printStackTrace();
        }
        return addressData;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.product, menu);
        MenuItem item = menu.findItem(R.id.action_search);
        MenuItem filter = menu.findItem(R.id.action_filter);
        searchView.setMenuItem(item);
        searchView.showSearch();
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
            super.onBackPressed();
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public void getLocation(double lat, double logn) {
        Intent intent = new Intent();
        intent.putExtra("latValue", lat);
        intent.putExtra("langValue", logn);
        setResult(3, intent);
        finishView();
    }

    private void finishView(){
        if (searchView.isSearchOpen()) {
            searchView.closeSearch();
        }
        finish();
    }
}