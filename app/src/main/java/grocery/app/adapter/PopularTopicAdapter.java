package grocery.app.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import grocery.app.R;
import grocery.app.databinding.ActivityAddressListBinding;
import grocery.app.databinding.ActivityPopularTopicListBinding;
import grocery.app.model.PopularModel;

public class PopularTopicAdapter extends RecyclerView.Adapter<PopularTopicAdapter.ViewHolder> {

    private Context context;
    private List<PopularModel> popularModelList;
    private int size = 0;


    public PopularTopicAdapter(Context context, List<PopularModel> popularModelList,int size) {
        this.context = context;
        this.popularModelList = popularModelList;
        this.size = size;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ActivityPopularTopicListBinding binding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.activity_popular_topic_list, parent, false);
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        PopularModel model = popularModelList.get(position);
        holder.binding.txtTopic.setText(model.getTopic());

    }

    @Override
    public int getItemCount() {
        return size;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ActivityPopularTopicListBinding binding;

        public ViewHolder(@NonNull ActivityPopularTopicListBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }

}
