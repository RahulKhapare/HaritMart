package grocery.app;

import android.os.Bundle;
import android.view.MenuItem;
import android.widget.ExpandableListView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import grocery.app.adapter.TermAndCConditionAdapter;
import grocery.app.databinding.ActivityTearmAndConditionBinding;
import grocery.app.model.TermConditionModel;
import grocery.app.util.WindowBarColor;


public class TearmAndConditionActivity extends AppCompatActivity {

    private TearmAndConditionActivity activity = this;
    private ActivityTearmAndConditionBinding binding;
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

        LinkedHashMap<TermConditionModel, List<TermConditionModel>> expandableListDetail = new LinkedHashMap<TermConditionModel, List<TermConditionModel>>();

        for (int i=0; i<3; i++){
            TermConditionModel model = new TermConditionModel();
            model.setTitle("Term of use");

            List<TermConditionModel> childItem = new ArrayList<TermConditionModel>();
            TermConditionModel childModel = new TermConditionModel();
            childModel.setMessage(message);
            childItem.add(childModel);
            expandableListDetail.put(model, childItem);
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