package grocery.app.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import grocery.app.R;
import grocery.app.SearchLocationActivity;
import grocery.app.databinding.ActivityLocationListBinding;
import grocery.app.model.LocationModel;

public class LocationAdapter extends RecyclerView.Adapter<LocationAdapter.ViewHolder> {

    private Context context;
    private List<LocationModel> locationModelList;
    public interface ClickInterface{
        void getLocation(double lat, double logn );
    }

    public LocationAdapter(Context context, List<LocationModel> locationModelList) {
        this.context = context;
        this.locationModelList = locationModelList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ActivityLocationListBinding binding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.activity_location_list, parent, false);
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        LocationModel model = locationModelList.get(position);
        holder.binding.txtTitle.setText(model.getTitle());
        holder.binding.txtAddress.setText(model.getAddress());
        holder.binding.lnrSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((SearchLocationActivity)context).getLocation(model.getLatitute(),model.getLognitute());
            }
        });
    }

    @Override
    public int getItemCount() {
        return locationModelList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ActivityLocationListBinding binding;
        public ViewHolder(@NonNull ActivityLocationListBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}
