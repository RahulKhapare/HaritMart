package grocery.app.adapter;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import grocery.app.CheckOutActivity;
import grocery.app.MyAddressActivity;
import grocery.app.NewAddressActivity;
import grocery.app.R;
import grocery.app.databinding.ActivityAddressListBinding;
import grocery.app.model.AddressModel;
import grocery.app.util.Config;

public class AddressAdapter extends RecyclerView.Adapter<AddressAdapter.ViewHolder> {

    private Context context;
    private List<AddressModel> addressModelList;
    private int lastCheckPosition;
    private boolean forCheckOut;

    public interface onClick{
        void deleteAddress(AddressModel model, int position, LinearLayout linearLayout);
    }

    public AddressAdapter(Context context, List<AddressModel> addressModelList, boolean forCheckOut) {
        this.context = context;
        this.addressModelList = addressModelList;
        this.forCheckOut = forCheckOut;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ActivityAddressListBinding binding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.activity_address_list, parent, false);
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        AddressModel model = addressModelList.get(position);

        holder.binding.radioButton.setChecked(position == lastCheckPosition);
        holder.binding.txtDefault.setText(model.getAddress_type() + " Address");
        holder.binding.txtName.setText(model.getFull_name());
        holder.binding.txtAddress.setText(getAddress(model));

        holder.binding.imgEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent editIntent = new Intent(context, NewAddressActivity.class);
                Config.addressModel = model;
                editIntent.putExtra(Config.EDIT_ADDRESS,true);
                context.startActivity(editIntent);
            }
        });

        holder.binding.imgDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((MyAddressActivity)context).deleteAddress(model,position,holder.binding.lnrAddress);
            }
        });

        holder.binding.lnrAddress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                lastCheckPosition = holder.getAdapterPosition();
                notifyItemRangeChanged(0,addressModelList.size());
                if (forCheckOut){
                    Config.addressModel = model;
                    Intent checkOutIntent = new Intent(context, NewAddressActivity.class);
                    checkOutIntent.putExtra(Config.FOR_CHECKOUT,true);
                    context.startActivity(checkOutIntent);
                }
            }
        });

        if (forCheckOut){
            holder.binding.imgEdit.setVisibility(View.GONE);
            holder.binding.imgDelete.setVisibility(View.GONE);
        }

    }

    @Override
    public int getItemCount() {
        return addressModelList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ActivityAddressListBinding binding;
        public ViewHolder(@NonNull ActivityAddressListBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }

    private String getAddress(AddressModel model){

        String address = "";

        if (!TextUtils.isEmpty(model.getEmail())){
            address = address + "Email : " + model.getEmail() + "\n";
        }

        if (!TextUtils.isEmpty(model.getAddress())){
            if (!TextUtils.isEmpty(model.getLandmark())){
                address = address + model.getAddress() + "," + model.getLandmark() + "\n";
            }else {
                address = address + model.getAddress() + "\n";
            }
        }

        if (!TextUtils.isEmpty(model.getCity()) && !TextUtils.isEmpty(model.getPincode())){
            address = address +  model.getCity() + "\n" + model.getPincode() + "\n";
        }

        if (!TextUtils.isEmpty(model.getPhone())){
            if (!TextUtils.isEmpty(model.getPhone2())){
                address = address + "ph: " + model.getPhone() + "/" + model.getPhone2() + "\n";
            }else {
                address = address + "ph: " + model.getPhone() + "\n";
            }
        }

        return address;
    }

}
