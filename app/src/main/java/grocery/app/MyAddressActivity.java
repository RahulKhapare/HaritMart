package grocery.app;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.adoisstudio.helper.Api;
import com.adoisstudio.helper.H;
import com.adoisstudio.helper.Json;
import com.adoisstudio.helper.JsonList;
import com.adoisstudio.helper.LoadingDialog;
import com.adoisstudio.helper.Session;
import com.miguelcatalan.materialsearchview.MaterialSearchView;

import java.util.ArrayList;
import java.util.List;

import grocery.app.Fragments.CartFragment;
import grocery.app.adapter.AddressAdapter;
import grocery.app.common.P;
import grocery.app.databinding.ActivityMyAddressBinding;
import grocery.app.model.AddressModel;
import grocery.app.util.Config;
import grocery.app.util.WindowBarColor;

public class MyAddressActivity extends AppCompatActivity implements View.OnClickListener,AddressAdapter.onClick{

    private ActivityMyAddressBinding binding;
    private MyAddressActivity activity = this;
    private MaterialSearchView searchView;
    private AddressAdapter adapter;
    private List<AddressModel> addressModelList;
    private String currentAddress;
    public static boolean checkAddress;
    public boolean forCheckOut;
    private Session session;
    private LoadingDialog loadingDialog;

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

        session = new Session(activity);
        loadingDialog = new LoadingDialog(activity);

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
            binding.lnrTitle.setBackground(getResources().getDrawable(R.drawable.custom_button_grey));
            binding.txtTitleMessage.setText("Select address to place order");
            binding.txtTitleMessage.setTextColor(getResources().getColor(R.color.black));
            binding.cardLocation.setVisibility(View.GONE);
        }else {
            binding.lnrAddress.setVisibility(View.VISIBLE);
        }

        currentAddress = getIntent().getStringExtra(Config.ADDRESS_LOCATION);
        if (!TextUtils.isEmpty(currentAddress)){
            Intent addIntent = new Intent(activity,NewAddressActivity.class);
            addIntent.putExtra(Config.GOOGLE_ADDRESS,true);
            startActivity(addIntent);
        }

    }


    @Override
    protected void onResume() {
        super.onResume();

        if (CartFragment.paymentFail){
            finish();
        }else if (checkAddress){
            checkAddress = false;
            hitAddressListApi();
        }

    }


    private void hitAddressListApi() {
        addressModelList.clear();
        showProgress();
        Json j = new Json();
        j.addInt(P.user_id,H.getInt(session.getString(P.user_id)));
        j.addInt(P.id,0);
        j.addInt(P.main_address,0);

        Api.newApi(activity, P.baseUrl + "address_list").addJson(j)
                .setMethod(Api.POST)
                //.onHeaderRequest(App::getHeaders)
                .onError(() -> {
                    H.showMessage(activity, "On error is called");
                    hideProgress();
                })
                .onSuccess(json ->
                {
                    addressModelList.clear();
                    if (json.getInt(P.status) == 1) {
                        json = json.getJson(P.data);
                        JsonList addressList = json.getJsonList(P.list);
                        for (Json jsonData : addressList){
                            AddressModel model = new AddressModel();

                            model.setId(jsonData.getString(P.id));
                            model.setFull_name(jsonData.getString(P.full_name));
                            model.setAddress_type(jsonData.getString(P.address_type));
                            model.setAddress(jsonData.getString(P.address));
                            model.setLandmark(jsonData.getString(P.landmark));
                            model.setCountry(jsonData.getString(P.country));
                            model.setState(jsonData.getString(P.state));
                            model.setCity(jsonData.getString(P.city));
                            model.setPincode(jsonData.getString(P.pincode));
                            model.setPhone(jsonData.getString(P.phone));
                            model.setEmail(jsonData.getString(P.email));
                            model.setPhone2(jsonData.getString(P.phone2));
                            model.setMain_address(jsonData.getString(P.main_address));

                            addressModelList.add(model);
                        }
                    } else {
                        H.showMessage(activity, json.getString(P.msg));
                    }

                    adapter.notifyDataSetChanged();
                    hideProgress();

                    if(forCheckOut){
                        if (addressModelList.isEmpty()){
                            binding.lnrAddress.setVisibility(View.VISIBLE);
                        }else {
                            binding.lnrAddress.setVisibility(View.GONE);
                        }
                    }

                })
                .run("hitAddressListApi");
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

    @Override
    public void deleteAddress(AddressModel model,int position,LinearLayout linearLayout) {
        deleteDialog(model,position,linearLayout);
    }

    private void deleteDialog(AddressModel model,int position, LinearLayout lnrAddress){
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(activity);
        alertDialogBuilder.setTitle("DELETE");
        alertDialogBuilder.setMessage("Are you sure, You wanted to delete address.");
        alertDialogBuilder.setPositiveButton("DELETE",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int arg1) {
                        dialog.dismiss();
                        hitDeleteAddress(lnrAddress,position,model.getId());
                    }
                });

        alertDialogBuilder.setNegativeButton("CANCEL",new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

    private void hitDeleteAddress(LinearLayout lnrAction,int position, String id) {
        showProgress();
        Json j = new Json();
        j.addInt(P.user_id, H.getInt(session.getString(P.user_id)));
        j.addString(P.id, id);

        Api.newApi(activity, P.baseUrl + "remove_address").addJson(j)
                .setMethod(Api.POST)
                //.onHeaderRequest(App::getHeaders)
                .onError(() -> {
                    hideProgress();
                    H.showMessage(activity, "On error is called");
                })
                .onSuccess(json ->
                {
                    if (json.getInt(P.status) == 1) {
                        H.showMessage(activity, "Address delete successfully");
                        lnrAction.startAnimation(removeItem(position));
                    } else {
                        H.showMessage(activity, json.getString(P.msg));
                    }
                    hideProgress();
                })
                .run("hitDeleteAddress");
    }

    private Animation removeItem(int position) {
        Animation animation = AnimationUtils.loadAnimation(activity, R.anim.slide_out);
        animation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                addressModelList.remove(position);
                adapter.notifyDataSetChanged();
            }
        });
        return animation;
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
                        if (model.getLandmark().toLowerCase().contains(newText.toLowerCase()) || model.getAddress().toLowerCase().contains(newText.toLowerCase())
                                || model.getCity().toLowerCase().contains(newText.toLowerCase())) {
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

    private void showProgress() {
        loadingDialog.show("Please wait..");
    }

    private void hideProgress() {
        loadingDialog.hide();
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