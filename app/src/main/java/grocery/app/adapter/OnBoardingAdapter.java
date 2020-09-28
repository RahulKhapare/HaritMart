package grocery.app.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import grocery.app.R;
import grocery.app.onBoardItem;

public class OnBoardingAdapter extends  RecyclerView.Adapter<OnBoardingAdapter.onBoardViewHolder>{

    private List<onBoardItem> onBoardItems;

    public OnBoardingAdapter(List<onBoardItem> onBoardItems) {
        this.onBoardItems = onBoardItems;
    }

    @NonNull
    @Override
    public onBoardViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new onBoardViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.image_crousal,parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull onBoardViewHolder holder, int position) {
            holder.setOnBoardingData(onBoardItems.get(position));
    }

    @Override
    public int getItemCount() {
        return onBoardItems.size();
    }

    class onBoardViewHolder extends RecyclerView.ViewHolder{
        private TextView textView;
        private ImageView imageView;

         onBoardViewHolder(@NonNull View itemView) {
            super(itemView);
            textView = itemView.findViewById(R.id.textBoarding);
            imageView = itemView.findViewById(R.id.imageViewBoarding);
        }
        void setOnBoardingData(onBoardItem onBoardItem){
             textView.setText(onBoardItem.getTitle());
             imageView.setImageResource(onBoardItem.getImage());
        }

    }
}
