package grocery.app.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import grocery.app.OrderDetailListActivity;
import grocery.app.R;
import grocery.app.databinding.ActivitySortOrderListBinding;
import grocery.app.model.OrderSortModel;
import grocery.app.util.Click;

public class OrderSortAdapter extends RecyclerView.Adapter<OrderSortAdapter.ViewHolder> {

    private Context context;
    private List<OrderSortModel> orderSortModelList;
    private int lastCheckPosition;

    public interface SortClick{
        void onSort(int position, int sortValue, String sortName);
    }

    public OrderSortAdapter(Context context, List<OrderSortModel> orderSortModelList) {
        this.context = context;
        this.orderSortModelList = orderSortModelList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ActivitySortOrderListBinding binding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.activity_sort_order_list, parent, false);
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        OrderSortModel model = orderSortModelList.get(position);
        lastCheckPosition = OrderDetailListActivity.SORT_POSITION;
        String value = model.getValue().substring(0, 1).toUpperCase() + model.getValue().substring(1);
        holder.binding.radioSort.setText(value);
        holder.binding.radioSort.setChecked(position == lastCheckPosition);
        holder.binding.radioSort.setOnClickListener(v -> {
            Click.preventTwoClick(v);
            lastCheckPosition = holder.getAdapterPosition();
            notifyItemRangeChanged(0,orderSortModelList.size());
            ((OrderDetailListActivity)context).onSort(position,model.getKey(),value);
        });

        if (position==0){
            holder.binding.txtTitle.setVisibility(View.VISIBLE);
            holder.binding.txtTitle.setText("Order Type");
        }else if (position==3){
            holder.binding.txtTitle.setVisibility(View.VISIBLE);
            holder.binding.txtTitle.setText("Time Filter");
        }


        if(position==orderSortModelList.size()-1){
            holder.binding.lnrLine.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return orderSortModelList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ActivitySortOrderListBinding binding;
        public ViewHolder(@NonNull ActivitySortOrderListBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}
