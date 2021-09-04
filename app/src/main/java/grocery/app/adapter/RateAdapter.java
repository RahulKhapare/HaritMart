package grocery.app.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import grocery.app.R;
import grocery.app.databinding.ActivityAddressListBinding;
import grocery.app.databinding.ActivityRateListBinding;
import grocery.app.model.RateModel;

public class RateAdapter extends RecyclerView.Adapter<RateAdapter.ViewHolder> {

    private Context context;
    private List<RateModel> rateModelList;

    public RateAdapter(Context context, List<RateModel> rateModelList) {
        this.context = context;
        this.rateModelList = rateModelList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ActivityRateListBinding binding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.activity_rate_list, parent, false);
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        RateModel model = rateModelList.get(position);
        holder.binding.txtName.setText(model.getName());
        holder.binding.txtDesc.setText(model.getDescription());
        if (model.getDescription().length()>250){
            holder.binding.txtDesc.setMaxLines(3);
            holder.binding.txtReadMore.setVisibility(View.VISIBLE);
        }
        holder.binding.txtReadMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                holder.binding.txtReadMore.setVisibility(View.GONE);
                holder.binding.txtDesc.setMaxLines(500);
                holder.binding.txtDesc.setText(model.getDescription());
            }
        });
    }

    @Override
    public int getItemCount() {
        return rateModelList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ActivityRateListBinding binding;

        public ViewHolder(@NonNull ActivityRateListBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}
