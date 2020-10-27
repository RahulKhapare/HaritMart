package grocery.app;

import android.app.ActionBar;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.net.Uri;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import com.adoisstudio.helper.Api;
import com.adoisstudio.helper.H;
import com.adoisstudio.helper.Json;
import com.adoisstudio.helper.LoadingDialog;
import com.adoisstudio.helper.Session;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
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
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.tabs.TabLayoutMediator;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.iid.FirebaseInstanceId;
import com.squareup.picasso.Picasso;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import grocery.app.adapter.OnBoardingAdapter;
import grocery.app.common.P;
import grocery.app.databinding.ActivityOnboardingBinding;
import grocery.app.model.onBoardItem;
import grocery.app.util.Click;
import grocery.app.util.Config;

public class OnboardingActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener {

    private OnBoardingAdapter onBoardingAdapter;
    private GoogleApiClient googleApiClient;
    private GoogleSignInOptions gso;
    private static final int RC_SIGN_IN_GOOGLE = 1;
    private OnboardingActivity activity = this;

    private static final String TAG = "FacebookLogin";
    private static final int RC_SIGN_IN_FB = 12345;
    private CallbackManager mCallbackManager;
    private FirebaseAuth mAuth;
    private ActivityOnboardingBinding binding;

    private int googleFlag = 1;
    private int facebookFlag = 2;
    private LoadingDialog loadingDialog;
    private boolean updateLoginFlag;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(activity,R.layout.activity_onboarding);
        loadingDialog = new LoadingDialog(this);
        updateLoginFlag = getIntent().getBooleanExtra(Config.UPDATE_LOGIN_FLAG,false);
        setupOnBoardingItems();
        hitCartTokenApi();
        initFacebookLogin();
        onInitGoogle();
        binding.onBoardViewPager.setAdapter(onBoardingAdapter);
        TabLayoutMediator tabLayoutMediator = new TabLayoutMediator(binding.tabLayout, binding.onBoardViewPager, (tab, position) -> {

        });
        tabLayoutMediator.attach();
        binding.loginBtn.setOnClickListener(v -> {
            Click.preventTwoClick(v);
            Intent intent = new Intent(OnboardingActivity.this, LoginScreen.class);
            startActivity(intent);
        });
        binding.txtSkip.setOnClickListener(v -> {
            Click.preventTwoClick(v);
            if (updateLoginFlag){
                finish();
            }else {
                new Session(this).addBool(P.isSkipUser,true);
                Intent intent = new Intent(OnboardingActivity.this, SetLocationActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            }
        });

        getFirebaseToken();
        printHashKey(activity);
    }

    private void getFirebaseToken(){
        FirebaseInstanceId.getInstance().getInstanceId().addOnSuccessListener(this, instanceIdResult -> {
            String newToken = instanceIdResult.getToken();
            new Session(this).addString(P.fcm_value,newToken);
            Log.e("TAG", "getFirebaseToken: " + new Session(this).getString(P.fcm_value));
        });
    }

    public static void printHashKey(Context pContext) {
        try {
            PackageInfo info = pContext.getPackageManager().getPackageInfo(pContext.getPackageName(), PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                String hashKey = new String(Base64.encode(md.digest(), 0));
                Log.i("TAG", "printHashKey() Hash Key: " + hashKey);
            }
        } catch (NoSuchAlgorithmException e) {
            Log.e("TAG", "printHashKey()", e);
        } catch (Exception e) {
            Log.e("TAG", "printHashKey()", e);
        }
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
        Click.preventTwoClick(v);
        requestGoogleLogin();
    }

    private void requestGoogleLogin() {
        Intent intent = Auth.GoogleSignInApi.getSignInIntent(googleApiClient);
        startActivityForResult(intent, RC_SIGN_IN_GOOGLE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN_GOOGLE) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            handleSignInResult(result);
        }else {
            mCallbackManager.onActivityResult(requestCode, resultCode, data);
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
            successDialog(googleFlag,account.getDisplayName(),account.getEmail(),account.getPhotoUrl(),account.getId());
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

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        H.showMessage(activity, "Connection fail.");
    }


    // Facebook integration
    public void onFacebookClick(View v) {
        Click.preventTwoClick(v);
        checkForFacebookLogin();
    }

    private void checkForFacebookLogin(){
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser==null){
            binding.loginButton.performClick();
        }else {
            successDialog(facebookFlag,currentUser.getDisplayName(),currentUser.getEmail(),currentUser.getPhotoUrl(),currentUser.getUid());
        }
    }

    private void initFacebookLogin(){
        FacebookSdk.sdkInitialize(activity);
        mAuth = FirebaseAuth.getInstance();
        mCallbackManager = CallbackManager.Factory.create();
        binding.loginButton.setReadPermissions("email", "public_profile");
        binding.loginButton.registerCallback(mCallbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                handleFacebookAccessToken(loginResult.getAccessToken());
            }

            @Override
            public void onCancel() {
                H.showMessage(activity,"Unable to login.");
            }

            @Override
            public void onError(FacebookException error) {
                H.showMessage(activity,"Unable to login.");
            }

        });
    }

    private void handleFacebookAccessToken(AccessToken token) {
        AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            FirebaseUser currentUser = mAuth.getCurrentUser();
                            successDialog(facebookFlag,currentUser.getDisplayName(),currentUser.getEmail(),currentUser.getPhotoUrl(),currentUser.getUid());
                        } else {
                            H.showMessage(activity,"Authentication failed.");
                        }
                    }
                });
    }

    private void logOutFromFacebook(){
//        LoginManager.getInstance().logOut();
    }

    private void successDialog(int flag,String name, String email, Uri image,String appId) {
        Dialog dialog = new Dialog(activity);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
//        dialog.setTitle("");
        dialog.setContentView(R.layout.activity_success_view);
        CircleImageView imgUser = dialog.findViewById(R.id.imgUser);
        TextView txtTitle = dialog.findViewById(R.id.txtTitle);
        TextView txtUserName = dialog.findViewById(R.id.txtUserName);
        TextView txtUserEmail = dialog.findViewById(R.id.txtUserEmail);
        TextView txtChangeAccount = dialog.findViewById(R.id.txtChangeAccount);
        Picasso.get().load(image).error(R.mipmap.ic_launcher).into(imgUser);

        if (flag==facebookFlag){
            txtTitle.setText("Facebook Login");
            txtChangeAccount.setText("Cancel");
        }else if (flag==googleFlag){
            txtTitle.setText("Google Login");
        }
        txtUserName.setText(name);
        txtUserEmail.setText(email);
        txtChangeAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.cancel();
                if (flag==facebookFlag){
                    logOutFromFacebook();
                }else if (flag==googleFlag){
                    logOutFromGoogle();
                }
            }
        });
        dialog.findViewById(R.id.txtContinue).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.cancel();
                if (flag==facebookFlag){
                    hitLoginSocial(name,image+"",email,appId,"1");
                }else if (flag==googleFlag){
                    hitLoginSocial(name,image+"",email,appId,"2");
                }

            }
        });
        dialog.setCancelable(true);
        dialog.show();
        Window window = dialog.getWindow();
        window.setLayout(ActionBar.LayoutParams.MATCH_PARENT, ActionBar.LayoutParams.WRAP_CONTENT);
    }


    private void hitLoginSocial(String name,String profile_image,String email,String app_id,String app_type) {
        showProgress();

        Json j = new Json();
        j.addString(P.name, name);
        j.addString(P.profile_image, profile_image);
        j.addString(P.email, email);
        j.addString(P.app_id, app_id);
        j.addString(P.app_type, app_type);
        j.addString(P.phone, "");
        j.addString(P.cart_token, new Session(activity).getString(P.cart_token));
        j.addString(P.fcm_value, new Session(activity).getString(P.fcm_value));

        Api.newApi(activity, P.baseUrl + "social_login").addJson(j)
                .setMethod(Api.POST)
                //.onHeaderRequest(App::getHeaders)
                .onError(() -> {
                    H.showMessage(activity, "On error is called");
                    hideProgress();
                })
                .onSuccess(json ->
                {
                    if (json.getInt(P.status) == 1) {
                        json = json.getJson(P.data);

                        Json userJson = json.getJson(P.user_data);
                        Session session = new Session(activity);
                        session.addString(P.login_token,json.getString(P.login_token));
                        session.addString(P.user_id,userJson.getString(P.id));
                        session.addString(P.app_type,userJson.getString(P.app_type));
                        session.addString(P.user_name,userJson.getString(P.name));
                        session.addString(P.user_email,userJson.getString(P.email));
                        session.addString(P.user_number,userJson.getString(P.phone));
                        session.addString(P.wallet,userJson.getString(P.wallet));
                        session.addString(P.referral_code,userJson.getString(P.referral_code));
                        session.addString(P.profile_image,userJson.getString(P.profile_image));

                        new Session(activity).addBool(P.isUserLogin,true);
                        Intent intent = new Intent(activity, SetLocationActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);

                    } else {
                        H.showMessage(activity, json.getString(P.msg));
                    }
                    hideProgress();
                })
                .run("hitLoginSocial");
    }

    private void hitCartTokenApi() {
        Session session = new Session(this);
        Json j = new Json();
        j.addString(P.cart_token, session.getString(P.cart_token));
        j.addJSON(P.option, new Json());
        Api.newApi(this, P.baseUrl + "cart_token").addJson(j)
                .setMethod(Api.POST)
                //.onHeaderRequest(App::getHeaders)
                .onError(() -> {
                })
                .onSuccess(json ->
                {
                    if (json.getInt(P.status) == 1) {
                        json = json.getJson(P.data);
                        session.addString(P.cart_token, json.getString(P.cart_token));
                    }
                })
                .run("hitCartTokenApi");
    }

    private void showProgress() {
        loadingDialog.show("Please wait..");
    }

    private void hideProgress() {
        loadingDialog.hide();
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

}