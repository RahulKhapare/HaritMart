package grocery.app.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import grocery.app.Fragments.HomeFragment;
import grocery.app.Fragments.ProductListFragment;
import grocery.app.R;
import grocery.app.databinding.ActivityProductItemBinding;
import grocery.app.model.ProductModel;
import grocery.app.util.Config;

public class ProductItemAdapter extends RecyclerView.Adapter<ProductItemAdapter.ViewHolder> {

    private Context context;
    private List<ProductModel> productModelList;
    private ProductListFragment fragment;
    private int clickItem;
    private int lastCheckPosition = -1;
    private boolean clearFlag;
    private boolean flag;

    public ProductItemAdapter(Context context, List<ProductModel> productModelList, ProductListFragment fragment,int clickItem, boolean clearFlag,boolean flag) {
        this.context = context;
        this.productModelList = productModelList;
        this.fragment = fragment;
        this.clickItem = clickItem;
        this.clearFlag = clearFlag;
        this.flag = flag;
    }

    public interface itemClick{
        void clickCategory(int position,int id);
        void clickSubCategory(int position, int id);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ActivityProductItemBinding binding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.activity_product_item, parent, false);
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ProductModel model = productModelList.get(position);
        holder.binding.txtCategory.setText(model.getName());

        holder.binding.txtCategory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (clickItem==1){
                    ((ProductListFragment)fragment).clickCategory(position,Integer.parseInt(model.getId()));
                }else if (clickItem==2){
                    ((ProductListFragment)fragment).clickSubCategory(position,Integer.parseInt(model.getId()));
                }
                lastCheckPosition = position;
                notifyDataSetChanged();
            }
        });

        updatePosition(position,holder);
    }

    void updatePosition(int position, ViewHolder holder){
        if (position == lastCheckPosition){
            if (clickItem==1){
                holder.binding.txtCategory.setBackground(context.getResources().getDrawable(R.drawable.green_bg));
                holder.binding.txtCategory.setTextColor(context.getResources().getColor(R.color.white));
            }else if (clickItem==2){
                holder.binding.txtCategory.setTextColor(context.getResources().getColor(R.color.green));
            }
        }else {
            if (clickItem==1){
                if (clearFlag ){
                    if (position==Config.SUB_CATEGORY_POSITION){
                        clearFlag = false;
                        if (flag){
                            holder.binding.txtCategory.setBackground(context.getResources().getDrawable(R.drawable.green_bg));
                            holder.binding.txtCategory.setTextColor(context.getResources().getColor(R.color.white));
                        }
                    }
                }else {
                    holder.binding.txtCategory.setBackground(null);
                    holder.binding.txtCategory.setTextColor(context.getResources().getColor(R.color.textColor));
                }

            }else if (clickItem==2){
                if (clearFlag){
                    if (position==Config.CHILD_CATEGORY_POSITION){
                        holder.binding.txtCategory.setTextColor(context.getResources().getColor(R.color.green));
                    }
                }else {
                    holder.binding.txtCategory.setTextColor(context.getResources().getColor(R.color.textColor));
                }
            }
        }
    }
    @Override
    public int getItemCount() {
        return productModelList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ActivityProductItemBinding binding;
        public ViewHolder(@NonNull ActivityProductItemBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}
