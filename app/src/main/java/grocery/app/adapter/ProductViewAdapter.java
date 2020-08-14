package grocery.app.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.List;

import grocery.app.R;
import grocery.app.common.P;
import grocery.app.databinding.ActivityProductItemBinding;
import grocery.app.databinding.ActivityProductViewBinding;
import grocery.app.model.ProductModel;
import grocery.app.util.Config;

public class ProductViewAdapter extends RecyclerView.Adapter<ProductViewAdapter.ViewHolder> {

    private Context context;
    private List<ProductModel> productModelList;

    public ProductViewAdapter(Context context, List<ProductModel> productModelList) {
        this.context = context;
        this.productModelList = productModelList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ActivityProductViewBinding binding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.activity_product_view, parent, false);
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ProductModel model = productModelList.get(position);
        Picasso.get().load(P.imgBaseUrl + Config.PRODUCT_IMAGE_PATH + model.getProduct_image()).error(R.mipmap.ic_launcher).placeholder(R.drawable.progress_animation).into(holder.binding.imgJwellery);

    }

    @Override
    public int getItemCount() {
        return productModelList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ActivityProductViewBinding binding;
        public ViewHolder(@NonNull ActivityProductViewBinding binding ) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}
