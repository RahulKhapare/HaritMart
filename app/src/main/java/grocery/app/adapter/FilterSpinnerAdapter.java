package grocery.app.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import java.util.List;

import grocery.app.R;
import grocery.app.model.FilterProductModel;

public class FilterSpinnerAdapter extends BaseAdapter {
    private Context context;
    private LayoutInflater inflter;
    private List<FilterProductModel> filterProductModelList;

    public FilterSpinnerAdapter(Context context, List<FilterProductModel> filterProductModelList) {
        this.context = context;
        this.filterProductModelList = filterProductModelList;
        inflter = (LayoutInflater.from(context));
    }


    @Override
    public int getCount() {
        return filterProductModelList.size();
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        view = inflter.inflate(R.layout.activity_filter_spinner_bg, null);
        TextView txtFilterName = view.findViewById(R.id.txtFilterName);
        FilterProductModel model = filterProductModelList.get(i);
        txtFilterName.setText(model.getVariants_name());
        return view;
    }

}