package grocery.app;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.os.Bundle;
import android.text.TextUtils;

import com.miguelcatalan.materialsearchview.MaterialSearchView;

import java.util.ArrayList;
import java.util.List;

import grocery.app.adapter.AddressAdapter;
import grocery.app.databinding.ActivityUpdateProfileBinding;
import grocery.app.model.AddressModel;
import grocery.app.util.WindowBarColor;

public class UpdateProfileActivity extends AppCompatActivity {

    private ActivityUpdateProfileBinding binding;
    private MaterialSearchView searchView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        WindowBarColor.setColor(this);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_update_profile);

        binding.toolbar.setTitle("Update Profile");
        setSupportActionBar(binding.toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        initView();

    }

    private void initView() {
        initSearchView();
    }

    private void initSearchView() {
        searchView = (MaterialSearchView) findViewById(R.id.search_view);
        searchView.setHint("Search product here");
        searchView.setTextColor(getResources().getColor(R.color.green));
        searchView.setHintTextColor(getResources().getColor(R.color.grey1));
        searchView.setVoiceSearch(true);
        searchView.setOnQueryTextListener(new MaterialSearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                //Do some magic
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });
}}

