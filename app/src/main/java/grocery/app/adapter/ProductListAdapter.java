package grocery.app.adapter;

import android.content.Context;
import android.graphics.Paint;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.text.DecimalFormat;
import java.util.List;

import grocery.app.ProductChildListActivity;
import grocery.app.R;
import grocery.app.common.P;
import grocery.app.databinding.ActivityProductItemListBgBinding;
import grocery.app.model.ProductModel;
import grocery.app.util.Click;
import grocery.app.util.Config;
import grocery.app.util.ConnectionUtil;

public class ProductListAdapter extends RecyclerView.Adapter<ProductListAdapter.ViewHolder> {

    private Context context;
    private List<ProductModel> productModelList;
    private String rs = "₹.";
    public interface Click{
        void add(int filterId);
    }

    public ProductListAdapter(Context context, List<ProductModel> productModelList) {
        this.context = context;
        this.productModelList = productModelList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ActivityProductItemListBgBinding binding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.activity_product_item_list_bg, parent, false);
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ProductModel model = productModelList.get(position);
        Picasso.get().load(model.getProduct_image()).error(R.mipmap.ic_launcher).placeholder(R.drawable.progress_animation).into(holder.binding.imgProduct);
        holder.binding.txtProductName.setText(model.getName());
        holder.binding.txtProductWeight.setText("0Kg");
        holder.binding.txtProductOff.setText(rs + model.getPrice());
        holder.binding.txtProductPrice.setText(rs + model.getSaleprice());

        String offValue = "0";
        if (!TextUtils.isEmpty(model.getPrice()) && !TextUtils.isEmpty(model.getPrice())){
            int actualValue = Integer.parseInt(model.getPrice());
            int discountValue = Integer.parseInt(model.getSaleprice());
            try {
//                offValue =  ((actualValue - discountValue) / actualValue) * 100;
//                offValue = actualValue - (actualValue * (discountValue / 100));
//                offValue = actualValue - (discountValue * actualValue) / 100;
                DecimalFormat df = new DecimalFormat("0.00");
                offValue = df.format(discountPercentage(discountValue,actualValue));
            }catch (Exception e){
                offValue = "0";
            }
        }


        holder.binding.txtPercent.setText(offValue + "% OFF");

        holder.binding.txtProductOff.setPaintFlags(holder.binding.txtProductOff.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);

        holder.binding.lnrAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                grocery.app.util.Click.preventTwoClick(v);
                if (ConnectionUtil.isOnline(context)){
                    ((ProductChildListActivity)context).add(Integer.parseInt(model.getFilter_id()));
                }else {
                    context.getResources().getString(R.string.internetMessage);
                }
            }
        });

        if (!TextUtils.isEmpty(model.getIs_wishlisted())){
            if (model.getIs_wishlisted().equals("0")){
                holder.binding.imgAction.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_baseline_add_24));
            }else if (model.getIs_wishlisted().equals("1")){
                holder.binding.imgAction.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_baseline_remove_24));
            }
        }
    }

    @Override
    public int getItemCount() {
        return productModelList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ActivityProductItemListBgBinding binding;
        public ViewHolder(@NonNull ActivityProductItemListBgBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }

    private float discountPercentage(float S, float M)
    {
        // Calculating discount
        float discount = M - S;

        // Calculating discount percentage
        float disPercent = (discount / M) * 100;

        return disPercent;
    }
}
