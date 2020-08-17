package grocery.app.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import org.w3c.dom.Text;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

import grocery.app.ProductChildListActivity;
import grocery.app.R;
import grocery.app.common.App;
import grocery.app.common.P;
import grocery.app.model.ProductModel;
import grocery.app.util.Click;
import grocery.app.util.Config;

public class ExpandableProductAdapter extends BaseExpandableListAdapter {

    private Context context;
    private List<ProductModel> expandableListTitle;
    private LinkedHashMap<ProductModel, List<ProductModel>> expandableListDetail;

    public ExpandableProductAdapter(Context context, List<ProductModel> expandableListTitle,
                                    LinkedHashMap<ProductModel, List<ProductModel>> expandableListDetail) {
        this.context = context;
        this.expandableListTitle = expandableListTitle;
        this.expandableListDetail = expandableListDetail;
    }

    @Override
    public Object getChild(int listPosition, int expandedListPosition) {
        return this.expandableListDetail.get(this.expandableListTitle.get(listPosition))
                .get(expandedListPosition);
    }

    @Override
    public long getChildId(int listPosition, int expandedListPosition) {
        return expandedListPosition;
    }

    @Override
    public View getChildView(int listPosition, final int expandedListPosition,
                             boolean isLastChild, View convertView, ViewGroup parent) {
        ProductModel model = (ProductModel) getChild(listPosition, expandedListPosition);
        if (convertView == null) {
            LayoutInflater layoutInflater = (LayoutInflater) this.context.
                    getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = layoutInflater.inflate(R.layout.activity_category_child_view, null);
        }
        RelativeLayout lnrSub = convertView.findViewById(R.id.lnrSub);
        TextView txtSubTitle = convertView.findViewById(R.id.txtSubTitle);
        txtSubTitle.setText(model.getName());
        lnrSub.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Click.preventTwoClick(v);
                App.selectedSubCategoryJson = App.selectedCategoryJson.getJsonList(P.children).get(model.getPosition());
                Intent intent = new Intent(context, ProductChildListActivity.class);
                intent.putExtra(Config.TITLE,model.getName());
                intent.putExtra(Config.CHILD_POSITION,model.getPosition());
                intent.putExtra(Config.CHILD_JSON,model.getJsonArrayData());
                context.startActivity(intent);
            }
        });
        return convertView;
    }

    @Override
    public int getChildrenCount(int listPosition) {
        return this.expandableListDetail.get(this.expandableListTitle.get(listPosition))
                .size();
    }

    @Override
    public Object getGroup(int listPosition) {
        return this.expandableListTitle.get(listPosition);
    }

    @Override
    public int getGroupCount() {
        return this.expandableListTitle.size();
    }

    @Override
    public long getGroupId(int listPosition) {
        return listPosition;
    }

    @Override
    public View getGroupView(int listPosition, boolean isExpanded,
                             View convertView, ViewGroup parent) {
        ProductModel model = (ProductModel) getGroup(listPosition);
        if (convertView == null) {
            LayoutInflater layoutInflater = (LayoutInflater) this.context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = layoutInflater.inflate(R.layout.activity_category_parent_view, null);
        }
        ImageView image =  convertView.findViewById(R.id.image);
        TextView txtTitle = convertView.findViewById(R.id.txtTitle);
        TextView txtDescription = convertView.findViewById(R.id.txtDescription);
        Picasso.get().load(P.imgBaseUrl + App.categoryImageUrl + model.getImage())
                .placeholder(R.drawable.progress_animation)
                .error(R.mipmap.ic_launcher).into(image);
        txtTitle.setText(model.getName());
        txtDescription.setText("Description will here.....");
        return convertView;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public boolean isChildSelectable(int listPosition, int expandedListPosition) {
        return true;
    }
}