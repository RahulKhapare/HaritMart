package grocery.app.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

import grocery.app.CheckOutActivity;
import grocery.app.MyPaymentActivity;
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
    private int value;

    public interface ClickItem{
        void selectedGetway(String id,String name);
    }

    public GetwayAdapter(Context context, List<GetwayModel> getwayModelList, boolean flag, int value) {
        this.context = context;
        this.getwayModelList = getwayModelList;
        this.flag = flag;
        this.value = value;

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
        if (value==2){
            holder.binding.txtLink.setVisibility(View.VISIBLE);
        }
        if (flag){
            flag = false;
            if (value==1){
                ((CheckOutActivity)context).selectedGetway(model.getId(),model.getName());
            }
        }
        holder.binding.radioButton.setOnClickListener(v -> {
            Click.preventTwoClick(v);
            lastCheckPosition = holder.getAdapterPosition();
            notifyItemRangeChanged(0,getwayModelList.size());
            if (value==1){
                ((CheckOutActivity)context).selectedGetway(model.getId(),model.getName());
            }else if (value==2){
                ((MyPaymentActivity)context).selectedGetway(model.getId(),model.getName());
            }
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
