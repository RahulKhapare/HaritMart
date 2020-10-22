package grocery.app;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import grocery.app.common.P;
import grocery.app.databinding.ActivityPaymentWebViewBinding;
import grocery.app.util.Click;
import grocery.app.util.Config;
import grocery.app.util.WindowBarColor;

public class PaymentWebViewActivity extends AppCompatActivity {

    private String orderId;
    private String paymentUrl;
    private PaymentWebViewActivity activity = this;
    private ActivityPaymentWebViewBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        WindowBarColor.setColor(activity);

        binding = DataBindingUtil.setContentView(this, R.layout.activity_payment_web_view);
        orderId = getIntent().getStringExtra(Config.ORDER_ID);
        paymentUrl = getIntent().getStringExtra(Config.PAYMENT_URL);

        binding.webView.setWebViewClient(new WebClient());
        binding.webView.getSettings().setJavaScriptEnabled(true);
        binding.webView.loadUrl(paymentUrl);

    }

    private class WebClient extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            view.loadUrl(url);

            return super.shouldOverrideUrlLoading(view, url);
        }
        @Override
        public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
            super.onReceivedError(view, errorCode, description, failingUrl);

        }
        @Override
        public void onPageFinished(WebView view, String url) {
            Log.e("TAG", "onPageFinished: " + url );
            if (url.contains("payment_success")){
                continueShoppingIntent();
            }
        }
    }

    private void continueShoppingIntent(){
        binding.webView.setVisibility(View.GONE);
        binding.lnrSuccess.setVisibility(View.VISIBLE);
    }

    public void onOrderClick(View view){
        Click.preventTwoClick(view);
        Intent intent = new Intent(activity, OrderDetailListActivity.class);
        intent.putExtra(Config.FROM_SUCCESS_ORDER,true);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }

    public void onCloseClick(View view){
        Click.preventTwoClick(view);
        onBackPressed();
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(activity,BaseActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }
}