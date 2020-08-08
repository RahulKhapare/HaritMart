package grocery.app.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.adoisstudio.helper.H;
import com.squareup.picasso.Picasso;

import java.util.List;

import grocery.app.Fragments.CartFragment;
import grocery.app.R;
import grocery.app.common.P;
import grocery.app.databinding.ActivityCartListBinding;
import grocery.app.model.CartModel;
import grocery.app.util.Click;
import grocery.app.util.ConnectionUtil;

public class CartAdapter extends RecyclerView.Adapter<CartAdapter.ViewHolder> {

    private Context context;
    private List<CartModel> cartModelList;
    private CartFragment cartFragment;
    private boolean isFragment;


    public interface CartInterface{
        void removeCart(int item_id, int position, CardView cardView);
        void updateCart(int item_id, int item_qty, TextView textView);
    }

    public CartAdapter(Context context, List<CartModel> cartModelList, CartFragment cartFragment) {
        this.context = context;
        this.cartModelList = cartModelList;
        this.cartFragment = cartFragment;
        isFragment = true;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ActivityCartListBinding binding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.activity_cart_list, parent, false);
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        CartModel model = cartModelList.get(position);
//        Picasso.get().load(P.imgBaseUrl+model.getCart_image()).placeholder( R.drawable.progress_animation ).error(R.mipmap.ic_launcher_round).into(holder.binding.imgItem);
        Picasso.get().load(model.getCart_image()).placeholder( R.drawable.progress_animation ).error(R.mipmap.ic_launcher_round).into(holder.binding.imgItem);
        if (TextUtils.isEmpty(model.getName()) || model.getName().equals("null")){
            holder.binding.txtTitle.setVisibility(View.GONE);
        }else {
            holder.binding.txtTitle.setText(model.getName());
        }
        holder.binding.txtAmount.setText("₹. "+model.getPrice());
        holder.binding.txtItemCount.setText(model.getQty());
        onClickItem(model,holder,position);
    }

    private void onClickItem(CartModel model,ViewHolder holder,int position) {
        holder.binding.imgMinus.setOnClickListener(v -> {
            Click.preventTwoClick(v);
            int cartValue = Integer.parseInt(holder.binding.txtItemCount.getText().toString());
            if (cartValue>1){
                int itemCount = cartValue-1;
                if (isFragment){
                    if (ConnectionUtil.isOnline(context)){
                        ((CartFragment)cartFragment).updateCart(Integer.parseInt(model.getId()),itemCount,holder.binding.txtItemCount);
                    }else {
                        H.showMessage(context,context.getResources().getString(R.string.internetMessage));
                    }
                }
            }
        });

        holder.binding.imgPlus.setOnClickListener(v -> {
            Click.preventTwoClick(v);
            int itemCount = Integer.parseInt(holder.binding.txtItemCount.getText().toString())+1;
            if (isFragment){
                if (ConnectionUtil.isOnline(context)){
                    ((CartFragment)cartFragment).updateCart(Integer.parseInt(model.getId()),itemCount,holder.binding.txtItemCount);
                }else{
                    H.showMessage(context,context.getResources().getString(R.string.internetMessage));
                }
            }
        });

        holder.binding.imgRemove.setOnClickListener(v -> {
            Click.preventTwoClick(v);
            if (isFragment){
                if (ConnectionUtil.isOnline(context)){
                    ((CartFragment)cartFragment).removeCart(Integer.parseInt(model.getId()),position,holder.binding.cardView);
                }else {
                    H.showMessage(context,context.getResources().getString(R.string.internetMessage));
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return cartModelList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ActivityCartListBinding binding;
        public ViewHolder(@NonNull ActivityCartListBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}
