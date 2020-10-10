package grocery.app.adapter;

import android.content.Context;
import android.graphics.Paint;
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

import java.text.DecimalFormat;
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
    private boolean processToPay;
    private String rs = "₹ ";


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

    public CartAdapter(Context context, List<CartModel> cartModelList, boolean processToPay) {
        this.context = context;
        this.cartModelList = cartModelList;
        this.processToPay = processToPay;
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

        Picasso.get().load(P.imgBaseUrl+model.getImage()).placeholder( R.drawable.progress_animation ).error(R.mipmap.ic_launcher_round).into(holder.binding.imgProduct);

        holder.binding.txtAmount.setText(rs + model.getPrice());
        holder.binding.txtProductOff.setText(rs + model.getTotal_price());

        String offValue = "0";
        if (!TextUtils.isEmpty(model.getPrice()) && !TextUtils.isEmpty(model.getTotal_price())){
            int actualValue = Integer.parseInt(model.getTotal_price());
            int discountValue = Integer.parseInt(model.getPrice());
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

        holder.binding.txtItemCount.setText(model.getQty());
        holder.binding.txtProductOff.setPaintFlags(holder.binding.txtProductOff.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);

        onClickItem(model,holder,position);

        if (processToPay){
            holder.binding.lnrUpdate.setVisibility(View.GONE);
        }
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

        holder.binding.lnrRemove.setOnClickListener(v -> {
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

    private float discountPercentage(float S, float M)
    {
        // Calculating discount
        float discount = M - S;

        // Calculating discount percentage
        float disPercent = (discount / M) * 100;

        return disPercent;
    }
}
