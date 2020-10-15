package grocery.app.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import grocery.app.OrderDetailsActivity;
import grocery.app.R;
import grocery.app.databinding.ActivityOrderDetailListBgBinding;
import grocery.app.model.OrderDetailListModel;

public class OrderDetailListAdapter extends RecyclerView.Adapter<OrderDetailListAdapter.ViewHolder> {

    private Context context;
    private List<OrderDetailListModel> orderDetailListModelList;
    private String rs = "₹ ";


    public OrderDetailListAdapter(Context context, List<OrderDetailListModel> orderDetailListModelList) {
        this.context = context;
        this.orderDetailListModelList = orderDetailListModelList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ActivityOrderDetailListBgBinding binding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.activity_order_detail_list_bg, parent, false);
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        OrderDetailListModel model = orderDetailListModelList.get(position);

        holder.binding.txtOrderId.setText(model.getOrderId());
        holder.binding.txtOrderNo.setText(model.getOrderNo());
        holder.binding.txtOrderDate.setText(model.getOrderDate());
        holder.binding.txtBillAmount.setText(rs + getFormatedAmount(model.getOrderAmount()));
        holder.binding.txtPaymentBy.setText(model.getPaymentBy());
        holder.binding.txtTotalAmount.setText(rs + getFormatedAmount(model.getTotalAmount()));

        holder.binding.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent orderIntent = new Intent(context, OrderDetailsActivity.class);
                context.startActivity(orderIntent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return orderDetailListModelList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ActivityOrderDetailListBgBinding binding;
        public ViewHolder(@NonNull ActivityOrderDetailListBgBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }

    private String formatedDate(String stringDate){
        String orderDate = stringDate;
        try {
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-mm-dd");
            Date date = dateFormat.parse(orderDate);
            orderDate = dateFormat.format(date);
        }catch (Exception e){
        }
        return orderDate;
    }

    private String getFormatedAmount(String amount){
        String value = "";
        try {
            int givenAmount  = Integer.parseInt(amount);
            value = NumberFormat.getNumberInstance(Locale.US).format(givenAmount);
        }catch (Exception e){
            value = amount + "";
        }
        return value;
    }

}
