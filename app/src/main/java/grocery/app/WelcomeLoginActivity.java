package grocery.app;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.app.ActionBar;
import android.app.Dialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.TextView;

import com.adoisstudio.helper.H;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
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
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;
import grocery.app.databinding.ActivityWelcomeLoginBinding;

public class WelcomeLoginActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener {

    private ActivityWelcomeLoginBinding binding;
    private FirebaseAuth mAuth;
    private CallbackManager mCallbackManager;
    private static final int RC_SIGN_IN_GOOGLE = 1;
    private WelcomeLoginActivity activity=this;
    private GoogleApiClient googleApiClient;
    private int googleFlag = 1;
    private int facebookFlag = 2;
    private GoogleSignInOptions gso;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_welcome_login);


        binding.toolbar.setTitle("Welcome");
        setSupportActionBar(binding.toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        initFacebookLogin();
        onInitGoogle();
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

    private void initFacebookLogin(){
//        FacebookSdk.sdkInitialize(activity);
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
                            successDialog(facebookFlag,currentUser.getDisplayName(),currentUser.getEmail(),currentUser.getPhotoUrl());
                        } else {
                            H.showMessage(activity,"Authentication failed.");
                        }
                    }
                });
    }

    private void successDialog(int flag,String name, String email, Uri image) {
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
                Intent intent = new Intent(WelcomeLoginActivity.this, SetLocationActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            }
        });
        dialog.setCancelable(true);
        dialog.show();
        Window window = dialog.getWindow();
        window.setLayout(ActionBar.LayoutParams.MATCH_PARENT, ActionBar.LayoutParams.WRAP_CONTENT);
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

    private void requestGoogleLogin() {
        Intent intent = Auth.GoogleSignInApi.getSignInIntent(googleApiClient);
        startActivityForResult(intent, RC_SIGN_IN_GOOGLE);
    }

    private void logOutFromFacebook(){
//        LoginManager.getInstance().logOut();
    }









    public void onLoginClick(View view) {

    }

    public void onFacebookClick(View view) {
        checkForFacebookLogin();

    }

    private void checkForFacebookLogin() {
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser==null){
            binding.loginButton.performClick();
        }else {
            successDialog(facebookFlag,currentUser.getDisplayName(),currentUser.getEmail(),currentUser.getPhotoUrl());
        }
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        H.showMessage(activity, "Connection fail.");
    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }

    public void onGoogleClick(View view) {
        requestGoogleLogin();
    }
}