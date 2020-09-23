package grocery.app.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

import grocery.app.CheckOutActivity;
import grocery.app.R;
import grocery.app.databinding.ActivityGetwayListBinding;
import grocery.app.model.GetwayModel;
import grocery.app.util.Click;

public class GetwayAdapter extends RecyclerView.Adapter<GetwayAdapter.ViewHolder> {

    private int limit = 10;
    private Context context;
    private List<GetwayModel> getwayModelList;
    private int lastCheckPosition;
    private boolean flag;

    public interface ClickItem{
        void selectedGetway(String id);
    }

    public GetwayAdapter(Context context, List<GetwayModel> getwayModelList, boolean flag) {
        this.context = context;
        this.getwayModelList = getwayModelList;
        this.flag = flag;

    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ActivityGetwayListBinding binding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.activity_getway_list, parent, false);
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        GetwayModel model = getwayModelList.get(position);
        holder.binding.radioButton.setText(model.getName());
        holder.binding.radioButton.setChecked(position == lastCheckPosition);
        if (flag){
            flag = false;
            ((CheckOutActivity)context).selectedGetway(model.getId());
        }
        holder.binding.radioButton.setOnClickListener(v -> {
            Click.preventTwoClick(v);
            lastCheckPosition = holder.getAdapterPosition();
            notifyItemRangeChanged(0,getwayModelList.size());
            ((CheckOutActivity)context).selectedGetway(model.getId());
        });

    }

    @Override
    public int getItemCount() {
        return getwayModelList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ActivityGetwayListBinding binding;
        public ViewHolder(@NonNull ActivityGetwayListBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}
