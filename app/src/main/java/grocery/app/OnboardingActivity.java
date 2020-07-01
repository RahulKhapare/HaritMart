package grocery.app;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

public class OnboardingActivity extends AppCompatActivity {

    private ViewPager2 viewPager2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_onboarding);
        setUpPager();
    }

    private void setUpPager() {
     viewPager2 = findViewById(R.id.viewPager2);
        ViewPager2Adapter viewPager2Adapter = new ViewPager2Adapter(0);
        viewPager2.setAdapter(viewPager2Adapter);
        TabLayout tabLayout = findViewById(R.id.tabLayout);
        TabLayoutMediator tabLayoutMediator = new TabLayoutMediator(tabLayout, viewPager2, (tab, position) -> {

        });
        tabLayoutMediator.attach();
    }

    private class ViewPager2Adapter extends RecyclerView.Adapter<ViewPager2Adapter.InnerClass> {
        private  int i;

        private  ViewPager2Adapter(int i)
        {
            this.i= i;
        }

        @NonNull
        @Override
        public InnerClass onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view;
            if (i == 0) {
                view = getLayoutInflater().inflate(R.layout.image_crousal, parent, false);
                view = getLayoutInflater().inflate(R.layout.image_crousal1, parent, false);
            }
            else
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

        public class InnerClass extends RecyclerView.ViewHolder {
            public InnerClass(@NonNull View itemView) {
                super(itemView);
            }
        }
    }
}