package grocery.app.adapter;

import android.content.Context;
import android.content.Intent;
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
import grocery.app.ProductDetailsActivity;
import grocery.app.R;
import grocery.app.databinding.NewArrivedLayoutBinding;
import grocery.app.model.ArrivalModel;
import grocery.app.util.Click;
import grocery.app.util.Config;
import grocery.app.util.ConnectionUtil;

public class NewArrivalAdapter extends RecyclerView.Adapter<NewArrivalAdapter.ViewHolder> {

    private Context context;
    private List<ArrivalModel> arrivalModelList;
    private HomeFragment fragment;
    private boolean activityClick;
    private int maxItem = 10;

    public interface ClickItem {
        void add(int filterId, ImageView imgAction);
    }

    public NewArrivalAdapter(Context context, List<ArrivalModel> arrivalModelList, HomeFragment fragment) {
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
        holder.binding.txtName.setText(model.getName());

        holder.binding.lnrProduct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Click.preventTwoClick(v);
                Config.Update_Direct_Home = true;
                Intent productIntent = new Intent(context, ProductDetailsActivity.class);
                productIntent.putExtra(Config.PRODUCT_ID, model.getId());
                productIntent.putExtra(Config.PRODUCT_FILTER_ID, model.getFilter_id());
                context.startActivity(productIntent);
            }
        });

        holder.binding.lnrAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Click.preventTwoClick(v);
                if (ConnectionUtil.isOnline(context)) {
                    if (activityClick) {
                        ((ProductDetailsActivity) context).add(Integer.parseInt(model.getFilter_id()), holder.binding.imgAction);
                    } else {
                        ((HomeFragment) fragment).add(Integer.parseInt(model.getFilter_id()), holder.binding.imgAction);
                    }
                } else {
                    context.getResources().getString(R.string.internetMessage);
                }
            }
        });


        if (activityClick) {
            holder.binding.lnrProduct.setBackground(context.getResources().getDrawable(R.drawable.product_bg_dark));
            holder.binding.txtName.setVisibility(View.GONE);
        } else {
            holder.binding.lnrProduct.setBackground(context.getResources().getDrawable(R.drawable.product_bg_two));
            holder.binding.txtName.setVisibility(View.VISIBLE);
        }

    }

    @Override
    public int getItemCount() {
        if (arrivalModelList.size() > maxItem) {
            return maxItem;
        } else {
            return arrivalModelList.size();
        }
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        NewArrivedLayoutBinding binding;

        public ViewHolder(@NonNull NewArrivedLayoutBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}
