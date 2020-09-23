package grocery.app.adapter;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import grocery.app.CheckOutActivity;
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
        holder.binding.txtDefault.setText(model.getAddressTitle());
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
                deleteDialog(position,holder.binding.lnrAddress);
            }
        });

        holder.binding.lnrAddress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                lastCheckPosition = holder.getAdapterPosition();
                notifyItemRangeChanged(0,addressModelList.size());
                if (forCheckOut){
                    Config.addressModel = model;
                    Intent checkOutIntent = new Intent(context, CheckOutActivity.class);
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
        address = model.getName() +"\n"+model.getApartmentName()+","+model.getStreetAddress()+"\n"+model.getLandMark()+"\n"+"pincode : "+model.getPincode()+"\n"+ "ph : "+model.getContactNumber();
        return address;
    }

    private void deleteDialog(int position, LinearLayout lnrAddress){
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
        alertDialogBuilder.setTitle("DELETE");
        alertDialogBuilder.setMessage("Are you sure, You wanted to delete address.");
        alertDialogBuilder.setPositiveButton("DELETE",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int arg1) {
                        dialog.dismiss();
                        lnrAddress.startAnimation(removeItem(position));
                    }
                });

        alertDialogBuilder.setNegativeButton("CANCEL",new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

    private Animation removeItem(int position) {
        Animation animation = AnimationUtils.loadAnimation(context, R.anim.slide_out);
        animation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                addressModelList.remove(position);
                notifyDataSetChanged();
            }
        });
        return animation;
    }

}
