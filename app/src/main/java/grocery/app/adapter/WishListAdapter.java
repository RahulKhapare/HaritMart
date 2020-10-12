package grocery.app.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Paint;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.text.DecimalFormat;
import java.util.List;

import grocery.app.Fragments.FavouriteFragment;
import grocery.app.ProductDetailsActivity;
import grocery.app.R;
import grocery.app.common.P;
import grocery.app.databinding.ActivityWishListItemsBinding;
import grocery.app.model.WishListModel;
import grocery.app.util.Click;
import grocery.app.util.Config;

public class WishListAdapter extends RecyclerView.Adapter<WishListAdapter.ViewHolder> {

    private Context context;
    private List<WishListModel> wishListModelList;
    private FavouriteFragment favouriteFragment;
    private String rs = "₹ ";
    public interface Click{
        void removeFavorite(int filterId, CardView cardView,int position);
    }

    public WishListAdapter(Context context, List<WishListModel> wishListModelList,FavouriteFragment favouriteFragment) {
        this.context = context;
        this.wishListModelList = wishListModelList;
        this.favouriteFragment = favouriteFragment;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ActivityWishListItemsBinding binding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.activity_wish_list_items, parent, false);
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        WishListModel model = wishListModelList.get(position);
        Picasso.get().load(P.imgBaseUrl+model.getProduct_image_path()+model.getProduct_image()).placeholder( R.drawable.progress_animation ).error(R.mipmap.ic_launcher_round).into(holder.binding.imgProduct);

        holder.binding.txtAmount.setText(rs + "00");
        holder.binding.txtProductOff.setText(rs + "00");

        String offValue = "0";
        if (!TextUtils.isEmpty("00") && !TextUtils.isEmpty("00")){
            int actualValue = Integer.parseInt("00");
            int discountValue = Integer.parseInt("00");
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

        holder.binding.txtPercent.setText(offValue+"% OFF");
        if (!TextUtils.isEmpty(model.getName())){
            holder.binding.txtProductName.setText(model.getName());
        }else {
            holder.binding.txtProductName.setText("Product Name");
            holder.binding.txtProductName.setPaintFlags(holder.binding.txtProductName.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
        }

        holder.binding.txtWeight.setText("0 KG");
        holder.binding.txtProductOff.setPaintFlags(holder.binding.txtProductOff.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);

        holder.binding.lnrAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                grocery.app.util.Click.preventTwoClick(v);
                if (!TextUtils.isEmpty(model.getWishlist_id())){
                    ((FavouriteFragment)favouriteFragment).removeFavorite(Integer.parseInt(model.getWishlist_id()),holder.binding.cardView,position);
                }
            }
        });

        holder.binding.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                grocery.app.util.Click.preventTwoClick(v);
                grocery.app.util.Click.preventTwoClick(v);
                Intent productIntent = new Intent(context, ProductDetailsActivity.class);
                productIntent.putExtra(Config.PRODUCT_ID,model.getId());
                productIntent.putExtra(Config.PRODUCT_FILTER_ID,model.getFilter_id());
                context.startActivity(productIntent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return wishListModelList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ActivityWishListItemsBinding binding;
        public ViewHolder(@NonNull ActivityWishListItemsBinding binding) {
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
