package grocery.app.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import grocery.app.MyAddressActivity;
import grocery.app.R;
import grocery.app.databinding.ActivityAddressListBinding;
import grocery.app.model.AddressModel;

public class AddressAdapter extends RecyclerView.Adapter<AddressAdapter.ViewHolder> {

    private Context context;
    private List<AddressModel> addressModelList;
    private int lastCheckPosition;

    public AddressAdapter(Context context, List<AddressModel> addressModelList) {
        this.context = context;
        this.addressModelList = addressModelList;
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
}
