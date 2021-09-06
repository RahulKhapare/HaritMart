package grocery.app;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;

import com.squareup.picasso.Picasso;

import grocery.app.databinding.ActivityGiftCardBinding;
import grocery.app.util.WindowBarColor;

public class GiftCardActivity extends AppCompatActivity {

    private GiftCardActivity activity = this;
    private ActivityGiftCardBinding binding;
    String image = "http://assets.myntassets.com/assets/images/2020/12/18/4d7a8a79-7223-4e33-9e08-3db9090ca13c1608270334657-festive-gifting-min.png";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        WindowBarColor.setColor(this);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_gift_card);

        binding.toolbar.setTitle("Gift Card");
        setSupportActionBar(binding.toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        initView();

    }

    private void initView(){
        Picasso.get().load(image).into(binding.imgGift);

        binding.btnRedeem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

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