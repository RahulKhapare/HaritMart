package grocery.app;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.adoisstudio.helper.Api;
import com.adoisstudio.helper.H;
import com.adoisstudio.helper.Json;
import com.adoisstudio.helper.LoadingDialog;
import com.adoisstudio.helper.Session;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import grocery.app.adapter.NotificationAdapter;
import grocery.app.common.P;
import grocery.app.databinding.ActivityNotificationBinding;
import grocery.app.model.NotificationModel;
import grocery.app.util.Config;
import grocery.app.util.WindowBarColor;

public class NotificationActivity extends AppCompatActivity {

    private NotificationActivity activity = this;
    private ActivityNotificationBinding binding;
    private List<NotificationModel> notificationModelList;
    private NotificationAdapter adapter;
    private LoadingDialog loadingDialog;
    private Session session;

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
        session = new Session(activity);
        loadingDialog = new LoadingDialog(activity);
        notificationModelList = new ArrayList<>();

        binding.recyclerNotification.setLayoutManager(new LinearLayoutManager(activity));
        binding.recyclerNotification.setHasFixedSize(true);
        binding.recyclerNotification.setNestedScrollingEnabled(false);
        adapter = new NotificationAdapter(activity,notificationModelList);
        binding.recyclerNotification.setAdapter(adapter);
//        hitNotificationList();
        setData();
    }

    private void setData(){
        NotificationModel model = new NotificationModel();
        model.setTitle("FLAT 50% off");
        model.setDescription("Over 250+ best sellers on deal today");
        model.setImage("https://img.freepik.com/free-vector/gradient-abstract-horizontal-sale-banner_52683-67806.jpg?size=626&ext=jpg");
        notificationModelList.add(model);
        notificationModelList.add(model);
        notificationModelList.add(model);
        notificationModelList.add(model);
        notificationModelList.add(model);
        adapter.notifyDataSetChanged();

        checkError();
    }


    private void hitNotificationList() {
        showProgress();
        Json j = new Json();
        if (session.getBool(P.isUserLogin)){
            j.addInt(P.user_id, H.getInt(session.getString(P.user_id)));
        }else {
            j.addInt(P.user_id, H.getInt(session.getString(P.user_id)));
        }
        j.addString(P.cart_token, new Session(activity).getString(P.cart_token));
        Api.newApi(activity, P.baseUrl + "notification").addJson(j)
                .setMethod(Api.POST)
                //.onHeaderRequest(App::getHeaders)
                .onError(() -> {
                    hideProgress();
                    checkError();
                    H.showMessage(activity, "On error is called");
                })
                .onSuccess(json ->
                {
                    if (json.getInt(P.status) == 1) {
                        json = json.getJson(P.data);
                        JSONArray jsonArray = json.getJsonArray(P.list);
                        for (int i = 0; i < jsonArray.length(); i++) {
                            try {
                                JSONObject jsonObject = jsonArray.getJSONObject(i);
                                NotificationModel model = new NotificationModel();

                                notificationModelList.add(model);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                        adapter.notifyDataSetChanged();
                    }
                    hideProgress();
                    checkError();
                })
                .run("hitNotificationList");
    }

    private void checkError(){
        if (notificationModelList.isEmpty()){
            showError();
        }else {
            hideError();
        }
    }

    private void showProgress() {
        loadingDialog.show("Please wait..");
    }

    private void hideProgress() {
        loadingDialog.hide();
    }

    private void showError(){
        binding.lnrError.setVisibility(View.VISIBLE);
    }

    private void hideError(){
        binding.lnrError.setVisibility(View.GONE);
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

//    apply_coupon_code
//    {
//        "coupon_code":"ORDER30",
//            "user_id":1,
//            "cart_token":"abdghdghdgh"
//    }

}