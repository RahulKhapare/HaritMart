package grocery.app.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Paint;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.adoisstudio.helper.H;
import com.adoisstudio.helper.Json;
import com.adoisstudio.helper.JsonList;
import com.squareup.picasso.Picasso;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import grocery.app.ProductChildListActivity;
import grocery.app.ProductDetailsActivity;
import grocery.app.R;
import grocery.app.common.P;
import grocery.app.databinding.ActivityProductItemListBgBinding;
import grocery.app.model.FilterProductModel;
import grocery.app.model.ProductModel;
import grocery.app.util.Click;
import grocery.app.util.Config;
import grocery.app.util.ConnectionUtil;

public class ProductListAdapter extends RecyclerView.Adapter<ProductListAdapter.ViewHolder> {

    private Context context;
    private List<ProductModel> productModelList;
    private String rs = "₹.";
    public interface Click{
        void add(int filterId, ImageView imageView,String value,ProductModel model);
    }

    public ProductListAdapter(Context context, List<ProductModel> productModelList) {
        this.context = context;
        this.productModelList = productModelList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ActivityProductItemListBgBinding binding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.activity_product_item_list_bg, parent, false);
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ProductModel model = productModelList.get(position);

        Picasso.get().load(model.getProduct_image()).error(R.mipmap.ic_launcher).placeholder(R.drawable.progress_animation).into(holder.binding.imgProduct);

        String offValue = "0";
        if (!TextUtils.isEmpty(model.getPrice()) && !TextUtils.isEmpty(model.getSaleprice())){
            int actualValue = Integer.parseInt(model.getPrice());
            int discountValue = Integer.parseInt(model.getSaleprice());
            try {
//                offValue =  ((actualValue - discountValue) / actualValue) * 100;
//                offValue = actualValue - (actualValue * (discountValue / 100));
//                offValue = actualValue - (discountValue * actualValue) / 100;
                DecimalFormat df = new DecimalFormat("0.00");
                offValue = df.format(discountPercentage(discountValue,actualValue));
            }catch (Exception e){
                offValue = "0";
            }
        }

        holder.binding.txtAmount.setText(rs + model.getSaleprice());
        holder.binding.txtProductOff.setText(rs + model.getPrice());
        holder.binding.txtProductOff.setPaintFlags(holder.binding.txtProductOff.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
        holder.binding.txtPercent.setText(offValue + "% OFF");

        holder.binding.txtQuantity.setText("0 KG");

        if(!Config.FROM_HOME){
            holder.binding.txtQuantity.setVisibility(View.VISIBLE);
            holder.binding.txtCategory.setText(model.getCategory_name());
        }else {
            holder.binding.txtQuantity.setVisibility(View.GONE);
            holder.binding.txtCategory.setText(model.getVariants_name());
        }

        setFilterData(model,holder.binding.spinnerFilter,holder.binding.lnrFilter);

        holder.binding.lnrProduct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                grocery.app.util.Click.preventTwoClick(v);
                Intent productIntent = new Intent(context, ProductDetailsActivity.class);
                productIntent.putExtra(Config.PRODUCT_ID,model.getId());
                if (model.getExternalFilterId()==null){
                    productIntent.putExtra(Config.PRODUCT_FILTER_ID,model.getFilter_id());
                }else {
                    productIntent.putExtra(Config.PRODUCT_FILTER_ID,model.getExternalFilterId());
                }
                context.startActivity(productIntent);
            }
        });

        holder.binding.lnrAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                grocery.app.util.Click.preventTwoClick(v);
                if (ConnectionUtil.isOnline(context)){
                    ((ProductChildListActivity)context).add(Integer.parseInt(model.getFilter_id()),holder.binding.imgAction,model.getIs_wishlisted(),model);
                }else {
                    context.getResources().getString(R.string.internetMessage);
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

        if (position==0){
            setMargins(holder.binding.cardMain,20,50,20,10);
        }
    }

    private void setMargins (View view, int left, int top, int right, int bottom) {
        if (view.getLayoutParams() instanceof ViewGroup.MarginLayoutParams) {
            ViewGroup.MarginLayoutParams p = (ViewGroup.MarginLayoutParams) view.getLayoutParams();
            p.setMargins(left, top, right, bottom);
            view.requestLayout();
        }
    }

    @Override
    public int getItemCount() {
        return productModelList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ActivityProductItemListBgBinding binding;
        public ViewHolder(@NonNull ActivityProductItemListBgBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }

    private float discountPercentage(float S, float M)
    {
        // Calculating discount
        float discount = M - S;

        // Calculating discount percentage
        float disPercent = (discount / M) * 100;

        return disPercent;
    }

    private void setFilterData(ProductModel productModel, Spinner spinner, RelativeLayout lnrFilter){

        boolean firstValue = false;

        String filterString = productModel.getFilter_option()+"";

        if (filterString.equals("[]")){
            hideView(lnrFilter);
        }else if(productModel.getFilter_option()!=null){
            try {
                List<FilterProductModel> filterProductModelList = new ArrayList<>();
                for (int i=0; i<productModel.getFilter_option().size(); i++){
                    Json json = productModel.getFilter_option().get(i);
                    FilterProductModel filterModel = new FilterProductModel();
                    filterModel.setFilter_id(json.getString(P.filter_id).trim());
                    filterModel.setVariants_name(json.getString(P.variants_name).trim());
                    filterProductModelList.add(filterModel);
                    if (!firstValue){
                        firstValue = true;
                        productModel.setExternalFilterId(json.getString(P.filter_id).trim());
                        productModel.setExternalFilterName(json.getString(P.variants_name).trim());
                    }
                }

                FilterSpinnerAdapter adapter = new FilterSpinnerAdapter(context,filterProductModelList);
                spinner.setAdapter(adapter);

                spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        FilterProductModel model = filterProductModelList.get(position);
                        productModel.setExternalFilterId(model.getFilter_id());
                        productModel.setExternalFilterName(model.getVariants_name().trim());
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                });

                if (filterProductModelList.size()==1 || filterProductModelList.size()==0){
                    hideView(lnrFilter);
                }

            }catch (Exception e){
                hideView(lnrFilter);
            }
        }else {
            hideView(lnrFilter);
        }
    }


    private void hideView(RelativeLayout lnrFilter){
        lnrFilter.setVisibility(View.GONE);
    }

}
