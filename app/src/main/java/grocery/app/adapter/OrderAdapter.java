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

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

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

        Picasso.get().load(P.imgBaseUrl+model.getImage()).placeholder( R.drawable.progress_animation ).error(R.mipmap.ic_launcher_round).into(holder.binding.imgProduct);

        holder.binding.txtAmount.setText(rs + model.getPrice());
        holder.binding.txtProductOff.setText(rs + model.getTotal_price());
        holder.binding.txtProductName.setText(model.getName());
        holder.binding.txtWeight.setText(getProductDerails(model));
        holder.binding.txtStatus.setText(model.getStatus());
        holder.binding.txtDate.setText(formatedDate(model.getDate()));

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

    private String getProductDerails(OrderModel model){
        String details = "";

        if (!TextUtils.isEmpty(model.getLabel1()) && !TextUtils.isEmpty(model.getValue1())){
            details = model.getLabel1() + " : " + model.getValue1();
        }

        if (!TextUtils.isEmpty(model.getLabel2()) && !TextUtils.isEmpty(model.getValue2())){
            details = details + "\n" + model.getLabel2() + " : " + model.getValue2();
        }
        return details;
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
}
