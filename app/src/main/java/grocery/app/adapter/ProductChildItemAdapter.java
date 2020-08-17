package grocery.app.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import grocery.app.ProductChildListActivity;
import grocery.app.R;
import grocery.app.databinding.ActivityCartListBinding;
import grocery.app.databinding.ActivityProductChildBgBinding;
import grocery.app.model.ProductModel;
import grocery.app.util.Click;

public class ProductChildItemAdapter extends RecyclerView.Adapter<ProductChildItemAdapter.ViewHolder> {

    private Context context;
    private List<ProductModel> productCategoryList;
    private int lastCheckPosition = -1;
    private boolean flag;

    public ProductChildItemAdapter(Context context, List<ProductModel> productCategoryList,boolean flag) {
        this.context = context;
        this.productCategoryList = productCategoryList;
        this.flag = flag;
    }

    public interface ClickView{
        void itemClick(int position, int id);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ActivityProductChildBgBinding binding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.activity_product_child_bg, parent, false);
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ProductModel model = productCategoryList.get(position);
        holder.binding.txtCategoryName.setText(model.getName());
        holder.binding.txtCategoryName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Click.preventTwoClick(v);
                lastCheckPosition = position;
                notifyDataSetChanged();
                ((ProductChildListActivity)context).itemClick(position,Integer.parseInt(model.getId()));
            }
        });

        if (position == lastCheckPosition){
            holder.binding.txtCategoryName.setTextColor(context.getResources().getColor(R.color.white));
            holder.binding.txtCategoryName.setBackground(context.getResources().getDrawable(R.drawable.product_bg_child_fill));
        }else {
            if (flag){
                flag = false;
                holder.binding.txtCategoryName.setTextColor(context.getResources().getColor(R.color.white));
                holder.binding.txtCategoryName.setBackground(context.getResources().getDrawable(R.drawable.product_bg_child_fill));
                ((ProductChildListActivity)context).itemClick(position,Integer.parseInt(model.getId()));
            }else {
                holder.binding.txtCategoryName.setTextColor(context.getResources().getColor(R.color.saffron));
                holder.binding.txtCategoryName.setBackground(context.getResources().getDrawable(R.drawable.product_bg_child_empty));
            }
        }
    }

    @Override
    public int getItemCount() {
        return productCategoryList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ActivityProductChildBgBinding binding;
        public ViewHolder(@NonNull ActivityProductChildBgBinding binding ) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}
