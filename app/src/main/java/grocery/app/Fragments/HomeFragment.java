package grocery.app.Fragments;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.adoisstudio.helper.LoadingDialog;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import java.util.ArrayList;
import java.util.List;

import grocery.app.OnBoardingAdapter;
import grocery.app.R;
import grocery.app.onBoardItem;


public class HomeFragment extends Fragment {
    private View fragmentView;
    private Context context;
    private LoadingDialog loadingDialog;
    private ViewPager2 viewPager2;
    private RecyclerView recyclerView;



    public HomeFragment() {

    }


    public static HomeFragment newInstance() {
        HomeFragment fragment = new HomeFragment();

        return fragment;
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        if (fragmentView == null) {
            fragmentView = inflater.inflate(R.layout.fragment_home, container, false);
            context = getContext();
            loadingDialog = new LoadingDialog(context);

            setUpTopPager();
            setUpRecyclerView();

        }

        return fragmentView;
    }

    private void setUpRecyclerView() {
        recyclerView = fragmentView.findViewById(R.id.recyclerView1);
        recyclerView.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false));
        recyclerView.setAdapter(new RecyclerAdapter());
    }

   private class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.ViewHolder>
   {

       class ViewHolder extends RecyclerView.ViewHolder {
           ImageView imageView;
           TextView title;

           private ViewHolder(View itemView) {
               super(itemView);
               imageView = itemView.findViewById(R.id.imageView);
             //  title = itemView.findViewById(R.id.textView);
               itemView.setOnClickListener(new View.OnClickListener() {
                   @Override
                   public void onClick(View v) {

                   }
               });
           }
       }
       @NonNull
       @Override
       public RecyclerAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
           View views = LayoutInflater.from(parent.getContext()).inflate(R.layout.new_arrived_layout, parent, false);
           //ViewHolder viewHolder=new ViewHolder(views);
           return new ViewHolder(views);
       }

       @Override
       public void onBindViewHolder(@NonNull RecyclerAdapter.ViewHolder holder, int position) {

       }

       @Override
       public int getItemCount() {
           return 6;
       }
   }

    private void setUpTopPager() {
        viewPager2 = fragmentView.findViewById(R.id.viewPager2);
        ViewPager2Adapter viewPager2Adapter = new ViewPager2Adapter();
        viewPager2.setAdapter(viewPager2Adapter);

        TabLayout tabLayout = fragmentView.findViewById(R.id.tabLayout);
        //tabLayout.setupWithViewPager(viewPager2);
        TabLayoutMediator tabLayoutMediator = new TabLayoutMediator(tabLayout, viewPager2, (tab, position) -> {

        });
        tabLayoutMediator.attach();
    }

    private class ViewPager2Adapter extends RecyclerView.Adapter<ViewPager2Adapter.InnerClass> {
        private int i;

       private ViewPager2Adapter() {

        }

        @NonNull
        @Override
        public InnerClass onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view;

                view = getLayoutInflater().inflate(R.layout.image_crousal1, parent, false);


            return new InnerClass(view);
        }

        @Override
        public void onBindViewHolder(@NonNull InnerClass holder, int position) {

        }

        @Override
        public int getItemCount() {
            return 3;
        }

        class InnerClass extends RecyclerView.ViewHolder {
            public InnerClass(@NonNull View itemView) {
                super(itemView);
            }
        }
    }





   /* @Override
    public void onDestroyView() {
        super.onDestroyView();

        Object object = fragmentView.getParent();
        if (object instanceof FrameLayout)
            ((FrameLayout) object).removeAllViews();
    }*/
}