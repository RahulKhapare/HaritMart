package grocery.app.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.List;

import grocery.app.Fragments.SearchFragment;
import grocery.app.ProductDetailsActivity;
import grocery.app.R;
import grocery.app.databinding.ActivitySearchListBinding;
import grocery.app.model.SearchModel;
import grocery.app.util.Config;

public class SearchAdapter extends RecyclerView.Adapter<SearchAdapter.ViewHolder>{

    private Context context;
    private List<SearchModel> searchModelList;
    private boolean flag;
    private SearchFragment fragment;
    public interface Click{
        void remove(LinearLayout linearLayout,int position);
    }

    public SearchAdapter(Context context, List<SearchModel> searchModelList, boolean flag,SearchFragment fragment) {
        this.context = context;
        this.searchModelList = searchModelList;
        this.flag = flag;
        this.fragment = fragment;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ActivitySearchListBinding binding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.activity_search_list, parent, false);
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        SearchModel model = searchModelList.get(position);
        Picasso.get().load(model.getProduct_image()).error(R.mipmap.ic_launcher).placeholder(R.drawable.progress_animation).into(holder.binding.imgProduct);
        holder.binding.txtProduct.setText(model.getName());
        holder.binding.lnrSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                grocery.app.util.Click.preventTwoClick(v);
                Intent productIntent = new Intent(context, ProductDetailsActivity.class);
                productIntent.putExtra(Config.PRODUCT_ID,model.getId());
                productIntent.putExtra(Config.PRODUCT_FILTER_ID,model.getFilter_id());
                context.startActivity(productIntent);
            }
        });
        holder.binding.imgRemove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((SearchFragment)fragment).remove(holder.binding.lnrSearch,position);
            }
        });

        if (flag) {
            holder.binding.imgRemove.setVisibility(View.GONE);
        } else {
            holder.binding.imgRemove.setVisibility(View.VISIBLE);
        }

    }

    @Override
    public int getItemCount() {
        return searchModelList.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder {
        ActivitySearchListBinding binding;

        public ViewHolder(@NonNull ActivitySearchListBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }

}
