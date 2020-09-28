package grocery.app.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import grocery.app.ProductDetailsActivity;
import grocery.app.R;
import grocery.app.databinding.ActivityQuantityBgBinding;
import grocery.app.model.QuantityModel;
import grocery.app.util.Click;

public class QuantityAdapter extends RecyclerView.Adapter<QuantityAdapter.ViewHolder> {

    private Context context;
    private List<QuantityModel> quantityModelList;
    private int lastCheckPosition = -1;

    public QuantityAdapter(Context context, List<QuantityModel> quantityModelList) {
        this.context = context;
        this.quantityModelList = quantityModelList;
    }

    public interface ClickView{
        void itemClick(int position, String id);
    }

    @NonNull
    @Override
    public QuantityAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ActivityQuantityBgBinding binding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.activity_quantity_bg, parent, false);
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull QuantityAdapter.ViewHolder holder, int position) {

        QuantityModel model = quantityModelList.get(position);
        holder.binding.txtQuantity.setText(model.getQuantity());
        holder.binding.txtQuantity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Click.preventTwoClick(v);
                lastCheckPosition = position;
                notifyDataSetChanged();
                ((ProductDetailsActivity)context).itemClick(position,model.getId());
            }
        });

        if (position == lastCheckPosition){
            holder.binding.txtQuantity.setTextColor(context.getResources().getColor(R.color.saffron));
            holder.binding.txtQuantity.setBackground(context.getResources().getDrawable(R.drawable.product_quantity_fill));
        }else {
            holder.binding.txtQuantity.setTextColor(context.getResources().getColor(R.color.grey1));
            holder.binding.txtQuantity.setBackground(context.getResources().getDrawable(R.drawable.product_quantity_gray));
        }

    }

    @Override
    public int getItemCount() {
        return quantityModelList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ActivityQuantityBgBinding binding;
        public ViewHolder(@NonNull ActivityQuantityBgBinding binding ) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}
