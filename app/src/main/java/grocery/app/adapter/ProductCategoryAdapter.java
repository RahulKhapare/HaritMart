package grocery.app.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TableLayout;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.List;

import grocery.app.Fragments.HomeFragment;
import grocery.app.R;
import grocery.app.common.App;
import grocery.app.common.P;
import grocery.app.databinding.ActivityProductListBinding;
import grocery.app.model.ProductModel;
import grocery.app.util.Click;

public class ProductCategoryAdapter extends RecyclerView.Adapter<ProductCategoryAdapter.ViewHolder> {

    private Context context;
    private List<ProductModel> productModelList;
    private HomeFragment homeFragment;
    private int maxItem = 10;
    public interface ItemClick{
        void itemClick(int position);
    }

    public ProductCategoryAdapter(Context context, List<ProductModel> productModelList, HomeFragment homeFragment) {
        this.context = context;
        this.productModelList = productModelList;
        this.homeFragment = homeFragment;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ActivityProductListBinding binding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.activity_product_list, parent, false);
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ProductModel model = productModelList.get(position);
        Log.e("TAG", "onBindViewHolderUrl: " + model.getImage() );
        Picasso.get().load(model.getImage())
                .error(R.mipmap.ic_launcher)
                .placeholder(R.drawable.progress_animation).into(holder.binding.image);
        holder.binding.txtName.setText(model.getName());
        holder.binding.lnrItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Click.preventTwoClick(v);
                ((HomeFragment)homeFragment).itemClick(position);
            }
        });
    }

    @Override
    public int getItemCount() {
        if (productModelList.size()>maxItem){
            return maxItem;
        }else {
            return productModelList.size();
        }
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ActivityProductListBinding binding;
        public ViewHolder(@NonNull ActivityProductListBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}
