package grocery.app.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import grocery.app.OrderDetailsActivity;
import grocery.app.R;
import grocery.app.databinding.ActivityStatusOrderListBinding;
import grocery.app.model.OrderStatusMode;

public class OrderStatusAdapter extends RecyclerView.Adapter<OrderStatusAdapter.ViewHolder> {

    private Context context;
    private List<OrderStatusMode> trackOrderModelList;
    private float alpha = 0.3f;

    public OrderStatusAdapter(Context context, List<OrderStatusMode> trackOrderModelList) {
        this.context = context;
        this.trackOrderModelList = trackOrderModelList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ActivityStatusOrderListBinding binding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.activity_status_order_list, parent, false);
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        OrderStatusMode model = trackOrderModelList.get(position);
        holder.binding.txtMessage.setText(model.getMessage());
        updateView(model,holder,position);
    }

    private void updateView(OrderStatusMode model,ViewHolder holder,int position){
        if (OrderDetailsActivity.truePosition == null){
            updateAlpha(model,holder);
        }else {
            if (OrderDetailsActivity.truePosition==0){
                if (position>0){
                    updateAlpha(model,holder);
                }
            }else {
                if (position > OrderDetailsActivity.truePosition){
                    updateAlpha(model,holder);
                }
            }
        }
    }

    @Override
    public int getItemCount() {
        return trackOrderModelList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ActivityStatusOrderListBinding binding;
        public ViewHolder(@NonNull ActivityStatusOrderListBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }

    private void updateAlpha(OrderStatusMode model ,ViewHolder holder){
        holder.binding.view.setAlpha(alpha);
        holder.binding.imgCircle.setAlpha(alpha);
    }
}
