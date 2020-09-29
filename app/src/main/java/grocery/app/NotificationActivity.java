package grocery.app;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.os.Bundle;
import android.view.MenuItem;

import java.util.ArrayList;
import java.util.List;

import grocery.app.adapter.NotificationAdapter;
import grocery.app.databinding.ActivityNotificationBinding;
import grocery.app.model.NotificationModel;
import grocery.app.util.WindowBarColor;

public class NotificationActivity extends AppCompatActivity {

    private NotificationActivity activity = this;
    private ActivityNotificationBinding binding;
    private List<NotificationModel> notificationModelList;
    private NotificationAdapter adapter;
    String image = "https://i0.wp.com/www.eatthis.com/wp-content/uploads/2020/07/grocery-shopping-2.jpg?resize=640%2C360&ssl=1";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        WindowBarColor.setColor(this);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_notification);

        binding.toolbar.setTitle("Notifications");
        setSupportActionBar(binding.toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        initView();

    }

    private void initView(){

        notificationModelList = new ArrayList<>();

        NotificationModel model = new NotificationModel();
        model.setTitle("Notification Title");
        model.setDescription("Notification Description");
        model.setImage(image);

        notificationModelList.add(model);
        notificationModelList.add(model);
        notificationModelList.add(model);
        notificationModelList.add(model);
        notificationModelList.add(model);
        notificationModelList.add(model);
        notificationModelList.add(model);
        notificationModelList.add(model);
        notificationModelList.add(model);
        notificationModelList.add(model);
        notificationModelList.add(model);

        binding.recyclerNotification.setLayoutManager(new LinearLayoutManager(activity));
        binding.recyclerNotification.setHasFixedSize(true);
        binding.recyclerNotification.setNestedScrollingEnabled(false);
        adapter = new NotificationAdapter(activity,notificationModelList);
        binding.recyclerNotification.setAdapter(adapter);

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