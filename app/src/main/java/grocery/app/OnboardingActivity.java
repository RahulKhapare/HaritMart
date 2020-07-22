package grocery.app;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import java.util.ArrayList;
import java.util.List;

public class OnboardingActivity extends AppCompatActivity {

    private OnBoardingAdapter onBoardingAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_onboarding);
        setupOnBoardingItems();
        ViewPager2 viewPager2 = findViewById(R.id.onBoardViewPager);
        viewPager2.setAdapter(onBoardingAdapter);
        TabLayout tabLayout = findViewById(R.id.tabLayout);
        TabLayoutMediator tabLayoutMediator = new TabLayoutMediator(tabLayout, viewPager2, (tab, position) -> {

        });
        tabLayoutMediator.attach();
        Button button = findViewById(R.id.loginBtn);
        Button button1 = findViewById(R.id.createAccount);
        button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
        button.setOnClickListener(view -> {
            Intent intent = new Intent(OnboardingActivity.this, LoginScreen.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
        });
    }

    private void setupOnBoardingItems() {
        List<onBoardItem> onBoardItems = new ArrayList<>();
        onBoardItem onBoardItem = new onBoardItem();
        onBoardItem.setImage(R.drawable.ic_group_4173);
        onBoardItem.setTitle("Select ipsum dolor sit amet");

        onBoardItem onBoardItem1 = new onBoardItem();
        onBoardItem1.setImage(R.drawable.ic_group_145);
        onBoardItem1.setTitle("Select ipsum dolor sit amet");

        onBoardItem onBoardItem2 = new onBoardItem();
        onBoardItem2.setImage(R.drawable.ic_group_4172);
        onBoardItem2.setTitle("Fast Doorstep Deliveries ");


        onBoardItems.add(onBoardItem);
        onBoardItems.add(onBoardItem1);
        onBoardItems.add(onBoardItem2);

        onBoardingAdapter = new OnBoardingAdapter(onBoardItems);
    }

   /* private void setUpPager() {
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
    }*/
}