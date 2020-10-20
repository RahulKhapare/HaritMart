package grocery.app;

import android.Manifest;
import android.app.ActionBar;
import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.StrictMode;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.adoisstudio.helper.Api;
import com.adoisstudio.helper.H;
import com.adoisstudio.helper.Json;
import com.adoisstudio.helper.Session;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import grocery.app.adapter.OrderAdapter;
import grocery.app.common.P;
import grocery.app.databinding.ActivityOrderDetailsBinding;
import grocery.app.model.OrderModel;
import grocery.app.model.OrderStatusMode;
import grocery.app.util.Click;
import grocery.app.util.Config;
import grocery.app.util.PdfDownloader;
import grocery.app.util.WindowBarColor;

public class OrderDetailsActivity extends AppCompatActivity implements View.OnClickListener {

    private OrderDetailsActivity activity = this;
    private ActivityOrderDetailsBinding binding;
    private String userRating;
    public static Integer truePosition;
    private String pdf_url = "https://www.w3.org/WAI/ER/tests/xhtml/testfiles/resources/pdf/dummy.pdf";
    private static final int READ_WRITE = 20;
    private static final String order_number = "009988";
    private int clickInvoice;
    private int shareInvoice = 1;
    private int viewInvoice = 2;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        WindowBarColor.setColor(this);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_order_details);

        binding.toolbar.setTitle("Order Details");
        setSupportActionBar(binding.toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        getAccess();
        initView();
    }

    private void initView(){

        List<OrderModel> orderModelList = new ArrayList<>();
        getOrderData(orderModelList);
        binding.recyclerOrderProduct.setHasFixedSize(true);
        binding.recyclerOrderProduct.setNestedScrollingEnabled(false);
        binding.recyclerOrderProduct.setLayoutManager(new LinearLayoutManager(activity));
        OrderAdapter orderAdapter = new OrderAdapter(activity,orderModelList);
        binding.recyclerOrderProduct.setAdapter(orderAdapter);

        binding.recyclerOrderStatus.setHasFixedSize(true);
        binding.recyclerOrderStatus.setLayoutManager(new LinearLayoutManager(activity,LinearLayoutManager.HORIZONTAL,false));

        binding.imgStar1.setOnClickListener(this);
        binding.imgStar2.setOnClickListener(this);
        binding.imgStar3.setOnClickListener(this);
        binding.imgStar4.setOnClickListener(this);
        binding.imgStar5.setOnClickListener(this);
        binding.txtCancelOrder.setOnClickListener(this);
        binding.btnShareInvoice.setOnClickListener(this);
        binding.btnDownloadInvoice.setOnClickListener(this);

    }

    private List<OrderStatusMode> getOrderTrackData(){
        truePosition = null;
        List<OrderStatusMode> list = new ArrayList<>();
        list.add(new OrderStatusMode("Stage 1"));
        list.add(new OrderStatusMode("Stage 2"));
        list.add(new OrderStatusMode("Stage 3"));
        list.add(new OrderStatusMode("Stage 4"));
        truePosition = 2;
        return list;
    }

    private void getOrderData(List<OrderModel> orderModelList){
        OrderModel model = new OrderModel();
        model.setQty("2");
        model.setTotal_price("100000");
        model.setPrice("500");
        model.setCoupon_discount_amount("50");
        orderModelList.add(model);
        orderModelList.add(model);
        orderModelList.add(model);
    }

    @Override
    public void onClick(View v) {
        Click.preventTwoClick(v);
        switch (v.getId()){
            case R.id.imgStar1:
                updateRating(1);
                break;
            case R.id.imgStar2:
                updateRating(2);
                break;
            case R.id.imgStar3:
                updateRating(3);
                break;
            case R.id.imgStar4:
                updateRating(4);
                break;
            case R.id.imgStar5:
                updateRating(5);
                break;
            case R.id.txtCancelOrder:
                commentDialog("");
                break;
            case R.id.btnShareInvoice:
                clickInvoice = shareInvoice;
                checkInvoice();
                break;
            case R.id.btnDownloadInvoice:
                clickInvoice = viewInvoice;
                checkInvoice();
                break;
        }
    }

    private void updateRating(int value){
        if (value==1){
            userRating = "1";
            binding.imgStar1.setImageDrawable(getResources().getDrawable(R.drawable.ic_baseline_star_green_24));
            binding.imgStar2.setImageDrawable(getResources().getDrawable(R.drawable.ic_baseline_star_gray_24));
            binding.imgStar3.setImageDrawable(getResources().getDrawable(R.drawable.ic_baseline_star_gray_24));
            binding.imgStar4.setImageDrawable(getResources().getDrawable(R.drawable.ic_baseline_star_gray_24));
            binding.imgStar5.setImageDrawable(getResources().getDrawable(R.drawable.ic_baseline_star_gray_24));
        }else if (value==2){
            userRating = "2";
            binding.imgStar1.setImageDrawable(getResources().getDrawable(R.drawable.ic_baseline_star_green_24));
            binding.imgStar2.setImageDrawable(getResources().getDrawable(R.drawable.ic_baseline_star_green_24));
            binding.imgStar3.setImageDrawable(getResources().getDrawable(R.drawable.ic_baseline_star_gray_24));
            binding.imgStar4.setImageDrawable(getResources().getDrawable(R.drawable.ic_baseline_star_gray_24));
            binding.imgStar5.setImageDrawable(getResources().getDrawable(R.drawable.ic_baseline_star_gray_24));
        }else if (value==3){
            userRating = "3";
            binding.imgStar1.setImageDrawable(getResources().getDrawable(R.drawable.ic_baseline_star_green_24));
            binding.imgStar2.setImageDrawable(getResources().getDrawable(R.drawable.ic_baseline_star_green_24));
            binding.imgStar3.setImageDrawable(getResources().getDrawable(R.drawable.ic_baseline_star_green_24));
            binding.imgStar4.setImageDrawable(getResources().getDrawable(R.drawable.ic_baseline_star_gray_24));
            binding.imgStar5.setImageDrawable(getResources().getDrawable(R.drawable.ic_baseline_star_gray_24));
        }else if (value==4){
            userRating = "4";
            binding.imgStar1.setImageDrawable(getResources().getDrawable(R.drawable.ic_baseline_star_green_24));
            binding.imgStar2.setImageDrawable(getResources().getDrawable(R.drawable.ic_baseline_star_green_24));
            binding.imgStar3.setImageDrawable(getResources().getDrawable(R.drawable.ic_baseline_star_green_24));
            binding.imgStar4.setImageDrawable(getResources().getDrawable(R.drawable.ic_baseline_star_green_24));
            binding.imgStar5.setImageDrawable(getResources().getDrawable(R.drawable.ic_baseline_star_gray_24));
        }else if (value==5){
            userRating = "5";
            binding.imgStar1.setImageDrawable(getResources().getDrawable(R.drawable.ic_baseline_star_green_24));
            binding.imgStar2.setImageDrawable(getResources().getDrawable(R.drawable.ic_baseline_star_green_24));
            binding.imgStar3.setImageDrawable(getResources().getDrawable(R.drawable.ic_baseline_star_green_24));
            binding.imgStar4.setImageDrawable(getResources().getDrawable(R.drawable.ic_baseline_star_green_24));
            binding.imgStar5.setImageDrawable(getResources().getDrawable(R.drawable.ic_baseline_star_green_24));
        }
    }



    private void checkInvoice(){
        if (TextUtils.isEmpty(pdf_url) || pdf_url.equals("null")){
            H.showMessage(activity,"No pdf path found");
        }else {
            getPermission();
        }
    }

    private void getAccess(){
        try {
            StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
            StrictMode.setVmPolicy(builder.build());
        }catch (Exception e){
        }
    }

    private void getPermission() {
        ActivityCompat.requestPermissions(activity,
                new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE},
                READ_WRITE);
    }

    public void jumpToSetting() {
        H.showMessage(activity, "Please allow permission from setting.");
        Intent intent = new Intent();
        intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        Uri uri = Uri.fromParts("package", activity.getPackageName(), null);
        intent.setData(uri);
        activity.startActivity(intent);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case READ_WRITE: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    checkDirectory(activity,pdf_url,order_number);
                } else if (grantResults[0] == PackageManager.PERMISSION_DENIED) {
                    jumpToSetting();
                } else {
                    getPermission();
                }
                return;
            }
        }
    }

    private void checkDirectory(Context context, String fileURL, String title){
        try{
            String destination = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS) + "/Grocery/Pdf/";
            String fileName = title+".pdf";
            destination += fileName;
            File direct = new File(destination);
            if (direct.exists()) {
                if (clickInvoice==shareInvoice){
                    sharePdf(context,destination);
                }else if (clickInvoice==viewInvoice){
                    openPdf(context,destination);
                }

            }else {
                if (clickInvoice==shareInvoice){
                    PdfDownloader.download(activity, fileURL, title, Config.SHARE);
                }else if (clickInvoice==viewInvoice){
                    PdfDownloader.download(activity, fileURL, title, Config.OPEN);
                }
            }
        }catch (Exception e){
            Log.e("TAG", "checkDirectory: "+ e.getMessage() );
            H.showMessage(context,"Something went wrong, try again.");
        }

    }

    private void openPdf(Context context, String filepath){
        File pdfFile = new File(filepath);
        Uri path = Uri.fromFile(pdfFile);
        Intent pdfIntent = new Intent(Intent.ACTION_VIEW);
        pdfIntent.setDataAndType(path, "application/pdf");
        pdfIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        try{
            context.startActivity(pdfIntent);
        }catch(ActivityNotFoundException e){
            H.showMessage(context,"No application available to view PDF");
        }
    }

    private void sharePdf(Context context,String destination){

        Intent intentShareFile = new Intent(Intent.ACTION_SEND);
        File fileWithinMyDir = new File(destination);

        if(fileWithinMyDir.exists()) {
            StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
            StrictMode.setVmPolicy(builder.build());
            intentShareFile.setType("application/pdf");
            intentShareFile.putExtra(Intent.EXTRA_STREAM, Uri.parse("file://"+destination));
            intentShareFile.putExtra(Intent.EXTRA_SUBJECT, "Sharing File...");
            intentShareFile.putExtra(Intent.EXTRA_TEXT, "Sharing File...");
            context.startActivity(Intent.createChooser(intentShareFile, "Share File"));
        }
    }


    private String formatedDate(String stringDate){
        String orderDate = stringDate;
        try {
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-mm-dd");
            Date date = dateFormat.parse(orderDate);
            orderDate = dateFormat.format(date);
        }catch (Exception e){
        }
        return orderDate;
    }

    private void commentDialog(String orderNumber) {

        Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.activity_comment_dialog);

        EditText etxComment = dialog.findViewById(R.id.etxComment);

        TextView txtCancel = dialog.findViewById(R.id.txtCancel);
        TextView txtSubmit = dialog.findViewById(R.id.txtSubmit);

        txtSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Click.preventTwoClick(v);
                String comment = etxComment.getText().toString().trim();
                if (!TextUtils.isEmpty(comment)) {
                    if (comment.length() > 10) {
                        dialog.cancel();
                        hitOrderCancelApi(orderNumber, comment);
                    } else {
                        H.showMessage(activity, "Enter minimum 10 character");
                    }
                } else {
                    H.showMessage(activity, "Please enter comment");
                }
            }
        });

        txtCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Click.preventTwoClick(v);
                dialog.cancel();
            }
        });

        dialog.setCancelable(true);
        dialog.show();
        Window window = dialog.getWindow();
        window.setLayout(ActionBar.LayoutParams.MATCH_PARENT, ActionBar.LayoutParams.WRAP_CONTENT);
    }

    private void hitOrderCancelApi(String orderNumber, String comment) {
        Json j = new Json();
        j.addInt(P.user_id, H.getInt(new Session(activity).getString(P.user_id)));
        j.addString(P.order_number, orderNumber);
        j.addString(P.order_status, "Cancelled");
        j.addString(P.order_status_comment, comment);

        Api.newApi(activity, P.baseUrl + "update_order_status").addJson(j)
                .setMethod(Api.POST)
                //.onHeaderRequest(App::getHeaders)
                .onError(() -> {
                    H.showMessage(activity, "On error is called");
                })
                .onSuccess(json ->
                {
                    if (json.getInt(P.status) == 1) {
                        H.showMessage(activity, "Order cancelled successfully");
                    } else {
                        H.showMessage(activity, json.getString(P.msg));
                    }

                })
                .run("hitOrderCancelApi");
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
        }
        return false;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}