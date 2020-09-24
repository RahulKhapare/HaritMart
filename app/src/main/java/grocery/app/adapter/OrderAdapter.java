package grocery.app.adapter;

import android.content.Context;
import android.graphics.Paint;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.List;

import grocery.app.Fragments.CartFragment;
import grocery.app.R;
import grocery.app.common.P;
import grocery.app.databinding.ActivityOrderListBinding;
import grocery.app.model.OrderModel;

public class OrderAdapter extends RecyclerView.Adapter<OrderAdapter.ViewHolder> {

    private Context context;
    private List<OrderModel> orderModelList;
    private String rs = "₹.";

    public OrderAdapter(Context context, List<OrderModel> orderModelList) {
        this.context = context;
        this.orderModelList = orderModelList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ActivityOrderListBinding binding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.activity_order_list, parent, false);
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        OrderModel model = orderModelList.get(position);

        Picasso.get().load(P.imgBaseUrl+model.getCart_image()).placeholder( R.drawable.progress_animation ).error(R.mipmap.ic_launcher_round).into(holder.binding.imgProduct);

        holder.binding.txtAmount.setText(rs + model.getPrice());
        holder.binding.txtProductOff.setText(rs + model.getCoupon_discount_amount());

        if (!TextUtils.isEmpty(model.getName())){
            holder.binding.txtProductName.setText(model.getName());
        }else {
            holder.binding.txtProductName.setText("Product Name");
            holder.binding.txtProductName.setPaintFlags(holder.binding.txtProductName.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
        }

        holder.binding.txtWeight.setText("0 KG");
        holder.binding.txtWeight.setPaintFlags(holder.binding.txtWeight.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);

        holder.binding.txtProductOff.setPaintFlags(holder.binding.txtProductOff.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);

    }


    @Override
    public int getItemCount() {
        return orderModelList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ActivityOrderListBinding binding;
        public ViewHolder(@NonNull ActivityOrderListBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}
