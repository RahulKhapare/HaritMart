package grocery.app.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.List;

import grocery.app.R;
import grocery.app.WishlistActivity;
import grocery.app.databinding.ActivityCartListBinding;
import grocery.app.databinding.WishlistLayoutBinding;
import grocery.app.model.WishListModel;

public class WishListAdapter extends RecyclerView.Adapter<WishListAdapter.ViewHolder> {

    private Context context;
    private List<WishListModel> wishListModelList;

    public WishListAdapter(Context context, List<WishListModel> wishListModelList) {
        this.context = context;
        this.wishListModelList = wishListModelList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        WishlistLayoutBinding binding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.wishlist_layout, parent, false);
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        WishListModel model = wishListModelList.get(position);
        Picasso.get().load(model.getVegImage()).error(R.drawable.tomato).into(holder.binding.vegImage);
        holder.binding.priceTextView.setText(model.getPrice());
        holder.binding.offTextView.setText(model.getDiscountstrike());
        holder.binding.weight.setText(model.getWeight());
        holder.binding.sizeTextView.setText(model.getVegSize());
    }

    @Override
    public int getItemCount() {
        return wishListModelList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        WishlistLayoutBinding binding;
        public ViewHolder(@NonNull WishlistLayoutBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}
