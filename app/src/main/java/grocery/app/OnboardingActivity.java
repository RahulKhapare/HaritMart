package grocery.app;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.adoisstudio.helper.H;
import com.adoisstudio.helper.Session;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import grocery.app.common.App;
import grocery.app.common.P;

public class OnboardingActivity extends AppCompatActivity {
    private CallbackManager callbackManager;
    private OnBoardingAdapter onBoardingAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_onboarding);
        setupOnBoardingItems();
        setUpFaceBookLogIn();
        ViewPager2 viewPager2 = findViewById(R.id.onBoardViewPager);
        viewPager2.setAdapter(onBoardingAdapter);
        TabLayout tabLayout = findViewById(R.id.tabLayout);
        TabLayoutMediator tabLayoutMediator = new TabLayoutMediator(tabLayout, viewPager2, (tab, position) -> {

        });
        tabLayoutMediator.attach();
        Button button = findViewById(R.id.loginBtn);
//        Button button1 = findViewById(R.id.createAccount);
//        button1.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//
//            }
//        });
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

    public void onFacebookClick(View view) {
        LoginManager.getInstance().logOut();
        LoginManager.getInstance().logInWithReadPermissions(this, Arrays.asList("public_profile"));
    }

    private void setUpFaceBookLogIn() {
        callbackManager = CallbackManager.Factory.create();

        LoginManager.getInstance().registerCallback(callbackManager,
                new FacebookCallback<LoginResult>() {
                    @Override
                    public void onSuccess(LoginResult loginResult) {
                        loadFacebookInfo(loginResult.getAccessToken());
                        Log.e("onSuccess", "isExecuted");
                    }

                    @Override
                    public void onCancel() {
                        Log.e("onCancel", "isExecuted");
                    }

                    @Override
                    public void onError(FacebookException exception) {
                        Log.e("exceptionIs", exception.getMessage());

                        H.showMessage(OnboardingActivity.this, "Could not login. Please try another login method");
                    }
                });
    }


    private void loadFacebookInfo(final AccessToken token) {
        //
        GraphRequest request = GraphRequest.newMeRequest(token,
                (json, response) -> {
                    //socialLogin("facebook", Api.getString(json, "name"),Api.getString(json, "email"), token.getToken());
                    Log.e("usernameIs", json.toString());

                    try {
                        Session session = new Session(OnboardingActivity.this);

                        session.addString(P.full_name, json.getString("name") + "");
                        session.addString(P.email_id, json.getString("email") + "");
                        session.addString(P.id, json.getString("id") + "");
                       App.startHomeActivity(OnboardingActivity.this);

                     //   hitSocialLoginApi(session, 3); Api calling using Apk

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    Log.e("responseIs", response.toString());
                });

        Bundle parameters = new Bundle();
        parameters.putString("fields", "id,email,name");
        request.setParameters(parameters);
        request.executeAsync();
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