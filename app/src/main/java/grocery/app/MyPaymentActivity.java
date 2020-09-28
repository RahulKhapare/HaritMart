package grocery.app;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.app.Dialog;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.TextView;

import com.adoisstudio.helper.Api;
import com.adoisstudio.helper.H;
import com.adoisstudio.helper.JsonList;
import com.adoisstudio.helper.LoadingDialog;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import grocery.app.adapter.GetwayAdapter;
import grocery.app.common.P;
import grocery.app.databinding.ActivityMyPaymentBinding;
import grocery.app.model.GetwayModel;
import grocery.app.util.WindowBarColor;

public class MyPaymentActivity extends AppCompatActivity implements GetwayAdapter.ClickItem{

    private MyPaymentActivity activity = this;
    private ActivityMyPaymentBinding binding;
    private LoadingDialog loadingDialog;
    private List<GetwayModel> getwayModelList;
    private GetwayAdapter adapter;
    private String paymentID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        WindowBarColor.setColor(this);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_my_payment);

        binding.toolbar.setTitle("My Payment");
        setSupportActionBar(binding.toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        iniView();

    }

    private void iniView(){

        loadingDialog = new LoadingDialog(activity);

        getwayModelList = new ArrayList<>();
        binding.recyclerWallet.setLayoutManager(new LinearLayoutManager(activity));
        binding.recyclerWallet.setNestedScrollingEnabled(false);
        binding.recyclerWallet.setHasFixedSize(true);
        adapter = new GetwayAdapter(activity,getwayModelList,true,2);
        binding.recyclerWallet.setAdapter(adapter);

        hitForPaymentOption();

    }

    @Override
    public void selectedGetway(String id,String name) {
        paymentID = id;
        onPaymentClick(paymentID,name);
    }

    private void hitForPaymentOption(){
        showProgress();
        getwayModelList.clear();
        Api.newApi(activity, P.baseUrl + "payment_gateway")
                .setMethod(Api.GET)
                //.onHeaderRequest(App::getHeaders)
                .onError(() -> {
                    hideProgress();
                    H.showMessage(activity, "On error is called");
                })
                .onSuccess(json ->
                {
                    if (json.getInt(P.status) == 1) {
                        json = json.getJson(P.data);
                        JsonList jsonList = json.getJsonList(P.list);
                        for (int i=0; i<jsonList.size();i++){
                            JSONObject jsonObject = jsonList.get(i);
                            GetwayModel model = new GetwayModel();
                            try {
                                model.setId(jsonObject.getString(P.id));
                                model.setName(jsonObject.getString(P.name));
                                model.setLogo(jsonObject.getString(P.logo));
                                getwayModelList.add(model);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                        adapter.notifyDataSetChanged();
                    } else {
                        H.showMessage(activity, json.getString(P.msg));
                    }

                    hideProgress();
                })
                .run("hitForPaymentOption");
    }

    private void onPaymentClick(String paymentID,String methodName) {
        Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.activity_payment_dialog);

        TextView txtMessage = dialog.findViewById(R.id.txtMessage);
        TextView txtNumber = dialog.findViewById(R.id.txtNumber);
        TextView txtNote = dialog.findViewById(R.id.txtNote);
        TextView txtCancel = dialog.findViewById(R.id.txtCancel);
        TextView txtProcess = dialog.findViewById(R.id.txtProcess);

        txtMessage.setText("Link " + methodName + " associate with this number");
        txtNumber.setText("+91 0000000000");
        txtNote.setText("if you don't have " + methodName + " account, please link your number.");

        txtCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        txtProcess.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        dialog.setCancelable(true);
        dialog.show();
        Window window = dialog.getWindow();
        window.setLayout(ActionBar.LayoutParams.MATCH_PARENT, ActionBar.LayoutParams.WRAP_CONTENT);
    }

    private void showProgress() {
        loadingDialog.show("Please wait..");
    }

    private void hideProgress() {
        loadingDialog.hide();
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