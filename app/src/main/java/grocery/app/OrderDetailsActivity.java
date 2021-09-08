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
import android.os.Handler;
import android.os.StrictMode;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.adoisstudio.helper.Api;
import com.adoisstudio.helper.H;
import com.adoisstudio.helper.Json;
import com.adoisstudio.helper.JsonList;
import com.adoisstudio.helper.Session;

import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import grocery.app.adapter.OrderAdapter;
import grocery.app.common.P;
import grocery.app.databinding.ActivityOrderDetailsBinding;
import grocery.app.model.OrderDetailListModel;
import grocery.app.model.OrderModel;
import grocery.app.util.AmountFormat;
import grocery.app.util.Click;
import grocery.app.util.Config;
import grocery.app.util.PdfDownloader;
import grocery.app.util.WindowBarColor;

public class OrderDetailsActivity extends AppCompatActivity implements View.OnClickListener {

    private OrderDetailsActivity activity = this;
    private ActivityOrderDetailsBinding binding;
    private String userRating;
    public static Integer truePosition;
    private String pdf_url = "";
    private static final int READ_WRITE = 20;
    private static String order_number = "";
    private int clickInvoice;
    private int shareInvoice = 1;
    private int viewInvoice = 2;
    private OrderDetailListModel orderModel;
    private List<OrderModel> orderModelList;
    private OrderAdapter orderAdapter;
    private String rs = "₹ ";
    private Session session;

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

    private void initView() {

        orderModel = Config.orderDetailListModel;

        session = new Session(activity);

        pdf_url = orderModel.getPdf_url();
        order_number = orderModel.getOrder_number();

        orderModelList = new ArrayList<>();
        binding.recyclerOrderProduct.setHasFixedSize(true);
        binding.recyclerOrderProduct.setNestedScrollingEnabled(false);
        binding.recyclerOrderProduct.setLayoutManager(new LinearLayoutManager(activity));

//        binding.txtDate.setText(orderModel.getOrdered_date());

        binding.imgStar1.setOnClickListener(this);
        binding.imgStar2.setOnClickListener(this);
        binding.imgStar3.setOnClickListener(this);
        binding.imgStar4.setOnClickListener(this);
        binding.imgStar5.setOnClickListener(this);
        binding.btnShareInvoice.setOnClickListener(this);
        binding.btnDownloadInvoice.setOnClickListener(this);
        binding.btnCancelOrder.setOnClickListener(this);

        getOrderData(orderModel.getProductItemList());
        setOrderStatus(orderModel.getOrder_status());
        setAddressData();
        setBillData();

        binding.imgStar3.performClick();
    }

    private void setOrderStatus(String status) {
        float alpha = 0.3f;
        if (status.contains("Pending") || status.contains("Processing") || status.contains("Received")){
            binding.lnrStatus2.setAlpha(alpha);
            binding.lnrStatus3.setAlpha(alpha);
            binding.lnrStatus4.setAlpha(alpha);
        }else if (status.contains("Packed")){
            binding.lnrStatus3.setAlpha(alpha);
            binding.lnrStatus4.setAlpha(alpha);
        }else if (status.contains("Shipped") || status.contains("On Hold")){
            binding.lnrStatus4.setAlpha(alpha);
        }else if (status.contains("Delivered")){

        }
    }

    private void setAddressData() {
        binding.txtAddressName.setText("Billing & Shipping Address");
        binding.txtTitle.setText(orderModel.getBill_address_type() + " Address");
        binding.txtName.setText(orderModel.getName());
        binding.txtAddress.setText(getAddress(orderModel));
    }

    private void setBillData() {
        binding.txtSubTotal.setText(rs + AmountFormat.getFormatedAmount(orderModel.getItem_total()));
        binding.txtTaxName.setText(orderModel.getTax_name());
        binding.txtTaxCharge.setText(rs + AmountFormat.getFormatedAmount(orderModel.getTax_amount()));
        binding.txtCouponDiscount.setText(rs + AmountFormat.getFormatedAmount(orderModel.getCoupon_discount_amount()));
        binding.txtDeliverCharge.setText(rs + AmountFormat.getFormatedAmount(orderModel.getDelivery_amount()));
        binding.txtTotalAMount.setText(rs + AmountFormat.getFormatedAmount(orderModel.getGrand_total()));
    }


    private void getOrderData(JsonList jsonList) {
        Log.e("TAG", "getOrderData: " + jsonList);
        if (!jsonList.isEmpty()) {

            for (Json json : jsonList) {

                OrderModel model = new OrderModel();

                model.setId(json.getString(P.id));
                model.setTemp_id(json.getString(P.temp_id));
                model.setProduct_id(json.getString(P.product_id));
                model.setProducts_variants_id(json.getString(P.products_variants_id));
                model.setQty(json.getString(P.qty));
                model.setCategory_name(json.getString(P.category_name));
                model.setCategory_id(json.getString(P.category_id));
                model.setTax_per(json.getString(P.tax_per));
                model.setName(json.getString(P.name));
                model.setSku(json.getString(P.sku));
                model.setSlug(json.getString(P.slug));
                model.setPrice(json.getString(P.price));
                model.setImage(json.getString(P.image));
                model.setTotal_price(json.getString(P.total_price));
                model.setCoupon_discount_amount(json.getString(P.coupon_discount_amount));
                model.setTax_amount(json.getString(P.tax_amount));
                model.setDate(orderModel.getOrdered_date());
                model.setStatus(orderModel.getOrder_status());

                try {
                    if (!TextUtils.isEmpty(json.getString(P.option1)) && !json.getString(P.option1).equals("0")) {
                        JSONObject jsonOption1 = json.getJSONObject(P.option1);
                        model.setLabel1(jsonOption1.getString(P.lable));
                        model.setValue1(jsonOption1.getString(P.value));
                    }
                } catch (Exception e) {

                }

                try {
                    if (!TextUtils.isEmpty(json.getString(P.option2)) && !json.getString(P.option2).equals("0")) {
                        JSONObject jsonOption2 = json.getJSONObject(P.option2);
                        model.setLabel2(jsonOption2.getString(P.lable));
                        model.setValue2(jsonOption2.getString(P.value));
                    }
                } catch (Exception e) {

                }

                orderModelList.add(model);

            }

            if(orderModelList.size()>2){
                orderAdapter = new OrderAdapter(activity, orderModelList,2);
                binding.recyclerOrderProduct.setAdapter(orderAdapter);
                binding.lnrShowMore.setVisibility(View.VISIBLE);
            }else {
                orderAdapter = new OrderAdapter(activity, orderModelList,orderModelList.size());
                binding.recyclerOrderProduct.setAdapter(orderAdapter);
                binding.lnrShowMore.setVisibility(View.GONE);
            }

            binding.lnrShowMore.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    binding.lnrShowMore.setVisibility(View.GONE);
                    orderAdapter = new OrderAdapter(activity, orderModelList,orderModelList.size());
                    binding.recyclerOrderProduct.setAdapter(orderAdapter);
                }
            });

        }


    }

    private String getAddress(OrderDetailListModel model) {

        String address = "";

        if (!TextUtils.isEmpty(model.getPhone())) {
            if (!TextUtils.isEmpty(model.getBill_phone2())) {
                address = "Contact : " + model.getPhone() + "/" + model.getBill_phone2() + "\n";
            } else {
                address = "Contact : " + model.getPhone() + "\n";
            }
        }

        if (!TextUtils.isEmpty(model.getEmail())) {
            address = address + "Email : " + model.getEmail() + "\n";
        }

        if (!TextUtils.isEmpty(model.getBill_address())) {
            if (!TextUtils.isEmpty(model.getBill_landmark())) {
                address = address + model.getBill_address() + "," + model.getBill_landmark() + "\n";
            } else {
                address = address + model.getBill_address() + "\n";
            }
        }

        if (!TextUtils.isEmpty(model.getBill_city()) && !TextUtils.isEmpty(model.getBill_pincode())) {
            address = address + model.getBill_city() + " - " + model.getBill_pincode() + "";
        }

        return address;
    }


    @Override
    public void onClick(View v) {
        Click.preventTwoClick(v);
        switch (v.getId()) {
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
            case R.id.btnShareInvoice:
                clickInvoice = shareInvoice;
                checkInvoice();
                break;
            case R.id.btnDownloadInvoice:
                clickInvoice = viewInvoice;
                checkInvoice();
                break;
            case R.id.btnCancelOrder:
                commentDialog(orderModel);
                break;
        }
    }

    private void commentDialog(OrderDetailListModel model) {

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
                        hitOrderCancelApi(model,comment);
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

    private void hitOrderCancelApi(OrderDetailListModel model,String comment) {
        Json j = new Json();
        j.addInt(P.user_id, H.getInt(session.getString(P.user_id)));
        j.addString(P.order_number, model.getOrder_number());
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
                        model.setOrder_status("Cancelled");
                        new Handler().postDelayed(() -> {
                            OrderDetailListActivity.isCancelOrder = true;
                            finish();
                        }, 1500);
                    } else {
                        H.showMessage(activity, json.getString(P.msg));
                    }

                })
                .run("hitOrderCancelApi");
    }


    private void updateRating(int value) {
        if (value == 1) {
            userRating = "1";
            binding.imgStar1.setImageDrawable(getResources().getDrawable(R.drawable.ic_baseline_star_green_24));
            binding.imgStar2.setImageDrawable(getResources().getDrawable(R.drawable.ic_baseline_star_24));
            binding.imgStar3.setImageDrawable(getResources().getDrawable(R.drawable.ic_baseline_star_24));
            binding.imgStar4.setImageDrawable(getResources().getDrawable(R.drawable.ic_baseline_star_24));
            binding.imgStar5.setImageDrawable(getResources().getDrawable(R.drawable.ic_baseline_star_24));
        } else if (value == 2) {
            userRating = "2";
            binding.imgStar1.setImageDrawable(getResources().getDrawable(R.drawable.ic_baseline_star_green_24));
            binding.imgStar2.setImageDrawable(getResources().getDrawable(R.drawable.ic_baseline_star_green_24));
            binding.imgStar3.setImageDrawable(getResources().getDrawable(R.drawable.ic_baseline_star_24));
            binding.imgStar4.setImageDrawable(getResources().getDrawable(R.drawable.ic_baseline_star_24));
            binding.imgStar5.setImageDrawable(getResources().getDrawable(R.drawable.ic_baseline_star_24));
        } else if (value == 3) {
            userRating = "3";
            binding.imgStar1.setImageDrawable(getResources().getDrawable(R.drawable.ic_baseline_star_green_24));
            binding.imgStar2.setImageDrawable(getResources().getDrawable(R.drawable.ic_baseline_star_green_24));
            binding.imgStar3.setImageDrawable(getResources().getDrawable(R.drawable.ic_baseline_star_green_24));
            binding.imgStar4.setImageDrawable(getResources().getDrawable(R.drawable.ic_baseline_star_24));
            binding.imgStar5.setImageDrawable(getResources().getDrawable(R.drawable.ic_baseline_star_24));
        } else if (value == 4) {
            userRating = "4";
            binding.imgStar1.setImageDrawable(getResources().getDrawable(R.drawable.ic_baseline_star_green_24));
            binding.imgStar2.setImageDrawable(getResources().getDrawable(R.drawable.ic_baseline_star_green_24));
            binding.imgStar3.setImageDrawable(getResources().getDrawable(R.drawable.ic_baseline_star_green_24));
            binding.imgStar4.setImageDrawable(getResources().getDrawable(R.drawable.ic_baseline_star_green_24));
            binding.imgStar5.setImageDrawable(getResources().getDrawable(R.drawable.ic_baseline_star_24));
        } else if (value == 5) {
            userRating = "5";
            binding.imgStar1.setImageDrawable(getResources().getDrawable(R.drawable.ic_baseline_star_green_24));
            binding.imgStar2.setImageDrawable(getResources().getDrawable(R.drawable.ic_baseline_star_green_24));
            binding.imgStar3.setImageDrawable(getResources().getDrawable(R.drawable.ic_baseline_star_green_24));
            binding.imgStar4.setImageDrawable(getResources().getDrawable(R.drawable.ic_baseline_star_green_24));
            binding.imgStar5.setImageDrawable(getResources().getDrawable(R.drawable.ic_baseline_star_green_24));
        }
    }


    private void checkInvoice() {
        if (TextUtils.isEmpty(pdf_url) || pdf_url.equals("null")) {
            H.showMessage(activity, "No pdf path found");
        } else {
            getPermission();
        }
    }

    private void getAccess() {
        try {
            StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
            StrictMode.setVmPolicy(builder.build());
        } catch (Exception e) {
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
                    checkDirectory(activity, pdf_url, order_number);
                } else if (grantResults[0] == PackageManager.PERMISSION_DENIED) {
                    jumpToSetting();
                } else {
                    getPermission();
                }
                return;
            }
        }
    }

    private void checkDirectory(Context context, String fileURL, String title) {
        try {
            String destination = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS) + "/Grocery/Pdf/";
            String fileName = title + ".pdf";
            destination += fileName;
            File direct = new File(destination);
            if (direct.exists()) {
                if (clickInvoice == shareInvoice) {
                    sharePdf(context, destination);
                } else if (clickInvoice == viewInvoice) {
                    openPdf(context, destination);
                }

            } else {
                if (clickInvoice == shareInvoice) {
                    PdfDownloader.download(activity, fileURL, title, Config.SHARE);
                } else if (clickInvoice == viewInvoice) {
                    PdfDownloader.download(activity, fileURL, title, Config.OPEN);
                }
            }
        } catch (Exception e) {
            Log.e("TAG", "checkDirectory: " + e.getMessage());
            H.showMessage(context, "Something went wrong, try again.");
        }

    }

    private void openPdf(Context context, String filepath) {
        File pdfFile = new File(filepath);
        Uri path = Uri.fromFile(pdfFile);
        Intent pdfIntent = new Intent(Intent.ACTION_VIEW);
        pdfIntent.setDataAndType(path, "application/pdf");
        pdfIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        try {
            context.startActivity(pdfIntent);
        } catch (ActivityNotFoundException e) {
            H.showMessage(context, "No application available to view PDF");
        }
    }

    private void sharePdf(Context context, String destination) {

        Intent intentShareFile = new Intent(Intent.ACTION_SEND);
        File fileWithinMyDir = new File(destination);

        if (fileWithinMyDir.exists()) {
            StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
            StrictMode.setVmPolicy(builder.build());
            intentShareFile.setType("application/pdf");
            intentShareFile.putExtra(Intent.EXTRA_STREAM, Uri.parse("file://" + destination));
            intentShareFile.putExtra(Intent.EXTRA_SUBJECT, "Sharing File...");
            intentShareFile.putExtra(Intent.EXTRA_TEXT, "Sharing File...");
            context.startActivity(Intent.createChooser(intentShareFile, "Share File"));
        }
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