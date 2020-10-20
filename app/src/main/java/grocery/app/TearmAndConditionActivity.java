package grocery.app;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ExpandableListView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import com.adoisstudio.helper.Api;
import com.adoisstudio.helper.H;
import com.adoisstudio.helper.Json;
import com.adoisstudio.helper.LoadingDialog;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import grocery.app.adapter.TermAndCConditionAdapter;
import grocery.app.common.P;
import grocery.app.databinding.ActivityTearmAndConditionBinding;
import grocery.app.model.TermConditionModel;
import grocery.app.util.WindowBarColor;


public class TearmAndConditionActivity extends AppCompatActivity {

    private TearmAndConditionActivity activity = this;
    private ActivityTearmAndConditionBinding binding;
    private LoadingDialog loadingDialog;
    private LinkedHashMap<TermConditionModel, List<TermConditionModel>> expandableListDetail;
    private String message = "Lorem ipsum dolor sit amet, consetetur sadipscing elitr, sed diam nonumy eirmod tempor invidunt ut labore et dolore magna aliquyam erat, sed diam voluptua. At vero eos et accusam et justo duo dolores et ea rebum. Stet clita kasd gubergren, no sea takimata sanctus est Lorem ipsum dolor sit amet. Lorem ipsum dolor sit amet, consetetur sadipscing elitr, sed diam nonumy eirmod tempor invidunt ut labore et dolore magna aliquyam erat, sed diam voluptua. At vero eos et accusam et justo duo dolores et ea rebum. Stet clita kasd gubergren, no sea takimata sanctus est Lorem ipsum dolor sit amet. Lorem ipsum dolor sit amet, consetetur sadipscing elitr, sed diam nonumy eirmod tempor invidunt ut labore et dolore magna aliquyam erat, sed diam voluptua. At vero eos et accusam et justo duo dolores et ea rebum. Stet clita kasd gubergren, no sea takimata sanctus est Lorem ipsum dolor sit amet. Lorem ipsum dolor sit amet, consetetur sadipscing elitr, sed diam nonumy eirmod tempor invidunt ut labore et dolore magna aliquyam erat, sed diam voluptua. At vero eos et accusam et justo duo dolores et ea rebum. Stet clita kasd gubergren, no sea takimata sanctus est Lorem ipsum";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        WindowBarColor.setColor(this);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_tearm_and_condition);

        binding.toolbar.setTitle("Term & Conditions");
        setSupportActionBar(binding.toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        initView();

    }

    private void initView(){

        loadingDialog = new LoadingDialog(activity);
        expandableListDetail = new LinkedHashMap<TermConditionModel, List<TermConditionModel>>();
//        hitTermConditionList();
        loadTermConditionData();

    }

    private void hitTermConditionList() {
        showProgress();
        Json j = new Json();

        Api.newApi(activity, P.baseUrl + "").addJson(j)
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

                    }
                    hideProgress();
                    checkError();
                })
                .run("hitTermConditionList");
    }

    private void loadTermConditionData(){

        for (int i=0; i<3; i++){
            TermConditionModel model = new TermConditionModel();
            model.setTitle("Term of use");

            List<TermConditionModel> childItemList = new ArrayList<TermConditionModel>();
            TermConditionModel childModel = new TermConditionModel();
            childModel.setMessage(message);
            childItemList.add(childModel);
            expandableListDetail.put(model, childItemList);
        }

        List<TermConditionModel> expandableListTitle = new ArrayList<TermConditionModel>(expandableListDetail.keySet());
        TermAndCConditionAdapter adapter = new TermAndCConditionAdapter(activity,expandableListTitle,expandableListDetail);
        binding.tearmConditionList.setAdapter(adapter);

        binding.tearmConditionList.setOnGroupExpandListener(new ExpandableListView.OnGroupExpandListener() {
            int previousItem = -1;
            @Override
            public void onGroupExpand(int groupPosition) {
                if (groupPosition!=previousItem)
                    binding.tearmConditionList.collapseGroup(previousItem);
                previousItem = groupPosition;
            }
        });
    }

    private void checkError(){
        if (expandableListDetail.isEmpty()){
            showError();
        }else {
            hideError();
        }
    }
    private void showError(){
        binding.lnrError.setVisibility(View.VISIBLE);
    }
    private void hideError(){
        binding.lnrError.setVisibility(View.GONE);
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