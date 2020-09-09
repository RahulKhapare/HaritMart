package grocery.app.adapter;

import android.content.Context;
import android.graphics.Paint;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.List;

import grocery.app.Fragments.FavouriteFragment;
import grocery.app.R;
import grocery.app.common.P;
import grocery.app.databinding.ActivityWishListItemsBinding;
import grocery.app.model.WishListModel;

public class WishListAdapter extends RecyclerView.Adapter<WishListAdapter.ViewHolder> {

    private Context context;
    private List<WishListModel> wishListModelList;
    private FavouriteFragment favouriteFragment;
    private String rs = "₹.";
    public interface Click{
        void addFavorite();
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
        Picasso.get().load(P.imgBaseUrl+model.getCart_image()).placeholder( R.drawable.progress_animation ).error(R.mipmap.ic_launcher_round).into(holder.binding.imgProduct);

        holder.binding.txtAmount.setText(rs + model.getPrice());
        holder.binding.txtProductOff.setText(rs + model.getCoupon_discount_amount());

        int actualValue = Integer.parseInt(model.getPrice());
        int discountValue = Integer.parseInt(model.getCoupon_discount_amount());
        int offValue = 0;
        try {
            if(actualValue !=0 && discountValue!=0){
                offValue = (actualValue-discountValue)/(discountValue)*100;
            }
        }catch (Exception e){
        }

        holder.binding.txtPercent.setText(offValue+"% OFF");
        if (!TextUtils.isEmpty(model.getName())){
            holder.binding.txtProductName.setText(model.getName());
        }else {
            holder.binding.txtProductName.setText("Product Name");
            holder.binding.txtProductName.setPaintFlags(holder.binding.txtProductName.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
        }

        holder.binding.txtWeight.setText("0 KG");
        holder.binding.txtWeight.setPaintFlags(holder.binding.txtWeight.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
        holder.binding.txtProductOff.setPaintFlags(holder.binding.txtProductOff.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);

        holder.binding.lnrAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((FavouriteFragment)favouriteFragment).addFavorite();
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
}
