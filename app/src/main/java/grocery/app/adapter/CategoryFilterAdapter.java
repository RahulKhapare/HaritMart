package grocery.app.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import grocery.app.ProductDetailsActivity;
import grocery.app.R;
import grocery.app.databinding.ActivityQuantityBgBinding;
import grocery.app.model.CategoryFilterModel;
import grocery.app.util.Click;

public class CategoryFilterAdapter extends RecyclerView.Adapter<CategoryFilterAdapter.ViewHolder> {

    private Context context;
    private List<CategoryFilterModel> categoryFilterModelList;
    private int lastCheckPosition = -1;
    private int comingValue = 0;
    private String filterId = "";
    private boolean inputValue = false;

    public CategoryFilterAdapter(Context context, List<CategoryFilterModel> categoryFilterModelList, int comingValue,String filterId,boolean inputValue) {
        this.context = context;
        this.categoryFilterModelList = categoryFilterModelList;
        this.comingValue = comingValue;
        this.filterId = filterId;
        this.inputValue = inputValue;
    }

    public interface ClickView{
        void itemClick(CategoryFilterModel model, int comingValue);
    }

    @NonNull
    @Override
    public CategoryFilterAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ActivityQuantityBgBinding binding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.activity_quantity_bg, parent, false);
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull CategoryFilterAdapter.ViewHolder holder, int position) {

        CategoryFilterModel model = categoryFilterModelList.get(position);
        holder.binding.txtFilterValue.setText(model.getName());
        holder.binding.txtFilterValue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Click.preventTwoClick(v);
                lastCheckPosition = position;
                notifyDataSetChanged();
                ((ProductDetailsActivity)context).itemClick(model,comingValue);
            }
        });


        if (!TextUtils.isEmpty(filterId)){
            if (model.getFilter_id().contains(filterId)){
                lastCheckPosition = position;
                if (comingValue==1){
                    if (!TextUtils.isEmpty(model.getId())){

                        ProductDetailsActivity.mainCategoryFilterId = Integer.parseInt(model.getId());

//                        if(model.getFilter_id().contains(",")){
//                            String data = model.getFilter_id();
//                            String[] items = data.split(",");
//                            for (String item : items)
//                            {
//                               if (item.equals(filterId)){
//                                   ProductDetailsActivity.mainCategoryFilterId = Integer.parseInt(item);
//                               }
//                            }
//                        }else {
//                            ProductDetailsActivity.mainCategoryFilterId = Integer.parseInt(model.getFilter_id());
//                        }

                    }
                }else if (comingValue==2){
                    if (!TextUtils.isEmpty(model.getId())){
                        ProductDetailsActivity.subCategoryFilterId = Integer.parseInt(model.getId());
                    }
                }
                ((ProductDetailsActivity)context).itemClick(model,comingValue);
                filterId = "";
            }
        }

        if (position == lastCheckPosition){
            holder.binding.txtFilterValue.setTextColor(context.getResources().getColor(R.color.saffron));
            holder.binding.txtFilterValue.setBackground(context.getResources().getDrawable(R.drawable.product_quantity_fill));
        }else {
            holder.binding.txtFilterValue.setTextColor(context.getResources().getColor(R.color.grey1));
            holder.binding.txtFilterValue.setBackground(context.getResources().getDrawable(R.drawable.product_quantity_gray));
        }

    }

    @Override
    public int getItemCount() {
        return categoryFilterModelList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ActivityQuantityBgBinding binding;
        public ViewHolder(@NonNull ActivityQuantityBgBinding binding ) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }

}


