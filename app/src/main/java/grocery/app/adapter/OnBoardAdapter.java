package grocery.app.adapter;

import android.content.Context;
import android.os.Parcelable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;
import java.util.List;

import grocery.app.R;
import grocery.app.model.OnBoardModel;
import grocery.app.util.LoadImage;


public class OnBoardAdapter extends PagerAdapter {

    private List<OnBoardModel> imageModelList;
    private LayoutInflater inflater;
    private Context context;
    private int inputValue;


    public OnBoardAdapter(Context context, List<OnBoardModel> imageModelList) {
        this.context = context;
        this.imageModelList = imageModelList;
        inflater = LayoutInflater.from(context);
        notifyDataSetChanged();
    }

    public void addData(@NonNull List<OnBoardModel> data) {
        imageModelList.clear();
        imageModelList.addAll(data);
        notifyDataSetChanged();
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
    }

    @Override
    public int getCount() {
        return imageModelList.size();
    }

    @Override
    public Object instantiateItem(ViewGroup view, int position) {
        View imageLayout = null;
        imageLayout = inflater.inflate(R.layout.activity_login_dashboard_slider,view,false);

        assert imageLayout != null;

        OnBoardModel model = imageModelList.get(position);

        ImageView imgBanner = imageLayout.findViewById(R.id.imgBanner);
        TextView txtTitle = imageLayout.findViewById(R.id.txtTitle);
        LoadImage.glide(context,imgBanner,model.getImage());
        txtTitle.setText(model.getTitle());

        view.addView(imageLayout, 0);

        return imageLayout;
    }
    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view.equals(object);
    }

    @Override
    public void restoreState(Parcelable state, ClassLoader loader) {
    }

    @Override
    public Parcelable saveState() {
        return null;
    }

    @Override
    public int getItemPosition(Object object) {
        return POSITION_NONE;
    }

}