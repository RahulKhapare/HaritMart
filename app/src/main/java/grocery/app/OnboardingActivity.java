package grocery.app;

import android.app.ActionBar;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import com.adoisstudio.helper.H;
import com.adoisstudio.helper.Session;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.OptionalPendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.squareup.picasso.Picasso;

import org.json.JSONException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import grocery.app.common.App;
import grocery.app.common.P;

public class OnboardingActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener {

    private CallbackManager callbackManager;
    private OnBoardingAdapter onBoardingAdapter;
    private GoogleApiClient googleApiClient;
    private GoogleSignInOptions gso;
    private static final int RC_SIGN_IN = 1;
    private OnboardingActivity activity = this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_onboarding);
        setupOnBoardingItems();
        setUpFaceBookLogIn();
        onInitGoogle();
        ViewPager2 viewPager2 = findViewById(R.id.onBoardViewPager);
        viewPager2.setAdapter(onBoardingAdapter);
        TabLayout tabLayout = findViewById(R.id.tabLayout);
        TabLayoutMediator tabLayoutMediator = new TabLayoutMediator(tabLayout, viewPager2, (tab, position) -> {

        });
        tabLayoutMediator.attach();
        Button button = findViewById(R.id.loginBtn);
        button.setOnClickListener(view -> {
            Intent intent = new Intent(OnboardingActivity.this, LoginScreen.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
        });
    }

    private void onInitGoogle() {
        gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();
        googleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();
    }

    //    Google Integration
    public void onGoogleClick(View v) {
        requestGoogleLogin();
    }

    private void requestGoogleLogin() {
        Intent intent = Auth.GoogleSignInApi.getSignInIntent(googleApiClient);
        startActivityForResult(intent, RC_SIGN_IN);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            handleSignInResult(result);
        }
    }

    private void handleSignInResult(GoogleSignInResult result) {
        try {
            if (result.isSuccess()) {
                OptionalPendingResult<GoogleSignInResult> opr = Auth.GoogleSignInApi.silentSignIn(googleApiClient);
                if (opr.isDone()) {
                    GoogleSignInResult resultData = opr.get();
                    handleUserData(resultData);
                } else {
                    opr.setResultCallback(new ResultCallback<GoogleSignInResult>() {
                        @Override
                        public void onResult(@NonNull GoogleSignInResult googleSignInResult) {
                            handleUserData(googleSignInResult);
                        }
                    });
                }
            } else {
                H.showMessage(activity, "Sign in cancel");
            }
        } catch (Exception e) {
            H.showMessage(activity, "Error to get login, try again.");
        }

    }

    private void handleUserData(GoogleSignInResult result) {
        if (result.isSuccess()) {
            GoogleSignInAccount account = result.getSignInAccount();
            googleDialog(account);
        } else {
            H.showMessage(activity, "User data not found");
        }
    }

    private void logOutFromGoogle() {
        Auth.GoogleSignInApi.signOut(googleApiClient).setResultCallback(
                new ResultCallback<Status>() {
                    @Override
                    public void onResult(Status status) {
                        if (status.isSuccess()) {
                            requestGoogleLogin();
                        } else {
                            H.showMessage(activity, "Session not close");
                        }
                    }
                });
    }


    public void googleDialog(GoogleSignInAccount account) {
        Dialog dialog = new Dialog(activity);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setTitle("Google Login");
        dialog.setContentView(R.layout.activity_google_view);
        CircleImageView imgUser = dialog.findViewById(R.id.imgUser);
        TextView txtUserName = dialog.findViewById(R.id.txtUserName);
        TextView txtUserEmail = dialog.findViewById(R.id.txtUserEmail);
        Picasso.get().load(account.getPhotoUrl()).error(R.mipmap.ic_launcher).into(imgUser);
        txtUserName.setText(account.getDisplayName());
        txtUserEmail.setText(account.getEmail());
        dialog.findViewById(R.id.txtChangeAccount).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.cancel();
                logOutFromGoogle();
            }
        });
        dialog.findViewById(R.id.txtContinue).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.cancel();
            }
        });
        dialog.setCancelable(true);
        dialog.show();
        Window window = dialog.getWindow();
        window.setLayout(ActionBar.LayoutParams.MATCH_PARENT, ActionBar.LayoutParams.WRAP_CONTENT);

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

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }
}