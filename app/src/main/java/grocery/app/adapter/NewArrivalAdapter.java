package grocery.app.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.List;

import grocery.app.Fragments.HomeFragment;
import grocery.app.ProductChildListActivity;
import grocery.app.R;
import grocery.app.databinding.NewArrivedLayoutBinding;
import grocery.app.model.ArrivalModel;
import grocery.app.util.Click;

public class NewArrivalAdapter extends RecyclerView.Adapter<NewArrivalAdapter.ViewHolder> {

    private Context context;
    private List<ArrivalModel> arrivalModelList;
    private HomeFragment fragment;

    public interface ClickItem{
        void add(int filterId);
    }

    public NewArrivalAdapter(Context context, List<ArrivalModel> arrivalModelList,HomeFragment fragment) {
        this.context = context;
        this.arrivalModelList = arrivalModelList;
        this.fragment = fragment;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        NewArrivedLayoutBinding binding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.new_arrived_layout, parent, false);
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ArrivalModel model = arrivalModelList.get(position);
        Picasso.get().load(model.getProduct_image()).placeholder(R.drawable.progress_animation).error(R.mipmap.ic_launcher).into(holder.binding.imgVeg);
        holder.binding.txtName.setText(model.getCategory_name());
        holder.binding.lnrAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Click.preventTwoClick(v);
                ((HomeFragment)fragment).add(Integer.parseInt(model.getFilter_id()));
            }
        });

    }

    @Override
    public int getItemCount() {
        return arrivalModelList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        NewArrivedLayoutBinding binding;
        public ViewHolder(@NonNull NewArrivedLayoutBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}
