package grocery.app.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.adoisstudio.helper.H;
import com.squareup.picasso.Picasso;

import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import grocery.app.OrderDetailListActivity;
import grocery.app.OrderDetailsActivity;
import grocery.app.R;
import grocery.app.common.P;
import grocery.app.databinding.ActivityOrderDetailListBgBinding;
import grocery.app.model.OrderDetailListModel;
import grocery.app.util.Config;

public class OrderDetailListAdapter extends RecyclerView.Adapter<OrderDetailListAdapter.ViewHolder> {

    private Context context;
    private List<OrderDetailListModel> orderDetailListModelList;
    private String rs = "₹ ";

    public interface onClick {
        void cancelOrder(OrderDetailListModel model, TextView txtStatus, TextView txtCancel);
    }


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

//        Picasso.get().load(P.imgBaseUrl).placeholder(R.drawable.progress_animation ).error(R.mipmap.ic_launcher_round).into(holder.binding.imgProduct);

        holder.binding.txtAmount.setText(rs + "0");
        holder.binding.txtProductOff.setText(rs + "0");
        holder.binding.txtProductOff.setPaintFlags(holder.binding.txtProductOff.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
        holder.binding.txtPercent.setText("0" + "% OFF");

        holder.binding.txtCategory.setText("Category Name");
        holder.binding.txtQuantity.setText("0 KG");

        holder.binding.txtOrderStatus.setText(model.getOrder_status());

        if (model.getOrder_status().contains("Cancelled")) {
            holder.binding.txtOrderStatus.setText("Order Cancelled");
            holder.binding.txtOrderStatus.setTextColor(context.getResources().getColor(R.color.red));
            holder.binding.txtCancelOrder.setVisibility(View.GONE);
            holder.binding.txtTrackOrder.setVisibility(View.GONE);
        } else if (model.getOrder_status().contains("Delivered")) {
            holder.binding.txtOrderStatus.setText("Order Delivered");
            holder.binding.txtOrderStatus.setTextColor(context.getResources().getColor(R.color.colorPrimaryDark));
            holder.binding.txtCancelOrder.setVisibility(View.GONE);
        } else {
            holder.binding.txtOrderStatus.setText("Click To Track Order Details >");
            holder.binding.txtOrderStatus.setTextColor(context.getResources().getColor(R.color.saffron));
        }

        holder.binding.txtTrackOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (model.getOrder_status().contains("Cancelled")) {
                    H.showMessage(context, "Order Cancelled");
                } else if (model.getOrder_status().contains("Delivered")) {
                    H.showMessage(context, "Order Delivered");
                } else {
                    Config.orderDetailListModel = model;
                    Intent orderIntent = new Intent(context, OrderDetailsActivity.class);
                    context.startActivity(orderIntent);
                }
            }
        });

        holder.binding.txtCancelOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((OrderDetailListActivity) context).cancelOrder(model, holder.binding.txtOrderStatus, holder.binding.txtCancelOrder);
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

    private String formatedDate(String stringDate) {
        String orderDate = stringDate;
        try {
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-mm-dd");
            Date date = dateFormat.parse(orderDate);
            orderDate = dateFormat.format(date);
        } catch (Exception e) {
        }
        return orderDate;
    }

    private String getFormatedAmount(String amount) {
        String value = "";
        try {
            int givenAmount = Integer.parseInt(amount);
            value = NumberFormat.getNumberInstance(Locale.US).format(givenAmount);
        } catch (Exception e) {
            value = amount + "";
        }
        return value;
    }

}
