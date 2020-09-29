package grocery.app.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.List;

import grocery.app.R;
import grocery.app.databinding.ActivityNotificationBgBinding;
import grocery.app.model.NotificationModel;

public class NotificationAdapter extends RecyclerView.Adapter<NotificationAdapter.ViewHolder> {

    private Context context;
    private List<NotificationModel> notificationModelList;

    public NotificationAdapter(Context context, List<NotificationModel> notificationModelList) {
        this.context = context;
        this.notificationModelList = notificationModelList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ActivityNotificationBgBinding binding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.activity_notification_bg, parent, false);
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        NotificationModel model = notificationModelList.get(position);
        holder.binding.txtTitle.setText(model.getTitle());
        holder.binding.txtDescription.setText(model.getDescription());
        Picasso.get().load(model.getImage()).into(holder.binding.imgNotification);
    }

    @Override
    public int getItemCount() {
        return notificationModelList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ActivityNotificationBgBinding binding;
        public ViewHolder(@NonNull ActivityNotificationBgBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}
