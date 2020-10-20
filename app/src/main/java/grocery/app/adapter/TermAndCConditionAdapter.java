package grocery.app.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.LinkedHashMap;
import java.util.List;

import grocery.app.R;
import grocery.app.model.TermConditionModel;

public class TermAndCConditionAdapter extends BaseExpandableListAdapter {

    private Context context;
    private List<TermConditionModel> expandableListTitle;
    private LinkedHashMap<TermConditionModel, List<TermConditionModel>> expandableListDetail;

    public TermAndCConditionAdapter(Context context, List<TermConditionModel> expandableListTitle,
                                    LinkedHashMap<TermConditionModel, List<TermConditionModel>> expandableListDetail) {
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
        TermConditionModel model = (TermConditionModel) getChild(listPosition, expandedListPosition);
        if (convertView == null) {
            LayoutInflater layoutInflater = (LayoutInflater) this.context.
                    getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = layoutInflater.inflate(R.layout.activity_term_child_view, null);
        }
        TextView txtDescription = convertView.findViewById(R.id.txtDescription);
        TextView txtReadMore = convertView.findViewById(R.id.txtReadMore);
        txtDescription.setText(model.getMessage());
        if (model.getMessage().length()>400){
            txtDescription.setMaxLines(5);
            txtReadMore.setVisibility(View.VISIBLE);
        }
        txtReadMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                txtReadMore.setVisibility(View.GONE);
                txtDescription.setMaxLines(500);
                txtDescription.setText(model.getMessage());
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
        TermConditionModel model = (TermConditionModel) getGroup(listPosition);
        if (convertView == null) {
            LayoutInflater layoutInflater = (LayoutInflater) this.context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = layoutInflater.inflate(R.layout.activity_term_parent_view, null);
        }
        final int[] clickValue = {0};
        TextView txtTitle = convertView.findViewById(R.id.txtTitle);
        ImageView imgArrow =  convertView.findViewById(R.id.imgArrow);

        txtTitle.setText(model.getTitle());
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