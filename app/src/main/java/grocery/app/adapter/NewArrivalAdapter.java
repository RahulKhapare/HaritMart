package grocery.app.adapter;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.List;

import grocery.app.Fragments.HomeFragment;
import grocery.app.ProductChildListActivity;
import grocery.app.ProductDetailsActivity;
import grocery.app.R;
import grocery.app.databinding.NewArrivedLayoutBinding;
import grocery.app.model.ArrivalModel;
import grocery.app.util.Click;
import grocery.app.util.Config;

public class NewArrivalAdapter extends RecyclerView.Adapter<NewArrivalAdapter.ViewHolder> {

    private Context context;
    private List<ArrivalModel> arrivalModelList;
    private HomeFragment fragment;
    private boolean activityClick;

    public interface ClickItem{
        void add(int filterId, ImageView imgAction);
        void remove(int filterId, ImageView imgAction);
    }

    public NewArrivalAdapter(Context context, List<ArrivalModel> arrivalModelList,HomeFragment fragment) {
        this.context = context;
        this.arrivalModelList = arrivalModelList;
        this.fragment = fragment;
        activityClick = false;
    }

    public NewArrivalAdapter(Context context, List<ArrivalModel> arrivalModelList, boolean activityClick) {
        this.context = context;
        this.arrivalModelList = arrivalModelList;
        this.activityClick = activityClick;
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

        holder.binding.lnrProduct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Click.preventTwoClick(v);
                Intent productIntent = new Intent(context,ProductDetailsActivity.class);
                productIntent.putExtra(Config.PRODUCT_ID,model.getId());
                productIntent.putExtra(Config.PRODUCT_FILTER_ID,model.getFilter_id());
                context.startActivity(productIntent);
            }
        });

        holder.binding.lnrAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Click.preventTwoClick(v);
                if (activityClick){
                    if(holder.binding.imgAction.getDrawable().getConstantState() == context.getResources().getDrawable(R.drawable.ic_baseline_add_24).getConstantState()){
                        ((ProductDetailsActivity)context).add(Integer.parseInt(model.getFilter_id()),holder.binding.imgAction);
                    }else if (holder.binding.imgAction.getDrawable().getConstantState() == context.getResources().getDrawable(R.drawable.ic_baseline_remove_24).getConstantState()){
                        ((ProductDetailsActivity)context).remove(Integer.parseInt(model.getFilter_id()),holder.binding.imgAction);
                    }
                }else {
                    if(holder.binding.imgAction.getDrawable().getConstantState() == context.getResources().getDrawable(R.drawable.ic_baseline_add_24).getConstantState()){
                        ((HomeFragment)fragment).add(Integer.parseInt(model.getFilter_id()),holder.binding.imgAction);
                    }else if (holder.binding.imgAction.getDrawable().getConstantState() == context.getResources().getDrawable(R.drawable.ic_baseline_remove_24).getConstantState()){
                        ((HomeFragment)fragment).remove(Integer.parseInt(model.getFilter_id()),holder.binding.imgAction);
                    }
                }
            }
        });

        if (!TextUtils.isEmpty(model.getIs_wishlisted())){
            if (model.getIs_wishlisted().equals("0")){
                holder.binding.imgAction.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_baseline_add_24));
            }else if (model.getIs_wishlisted().equals("1")){
                holder.binding.imgAction.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_baseline_remove_24));
            }
        }

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
