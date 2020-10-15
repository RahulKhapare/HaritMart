package grocery.app.util;

import android.app.DownloadManager;
import android.content.ActivityNotFoundException;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Environment;
import android.os.StrictMode;

import com.adoisstudio.helper.H;

import java.io.File;

public  class PdfDownloader {

    public static void download(Context context,String fileURL,String title,int flag) {
        checkDirectory(context,fileURL,title,flag);
    }

    public static void checkDirectory(Context context,String fileURL,String title,int flag){
        try{
            String destination = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS) + "/JwelleryApp/Pdf/";
            String fileName = title+".pdf";
            destination += fileName;
            File direct = new File(destination);
            if (direct.exists()) {
                if (flag==Config.SHARE){
                    sharePdf(context,destination);
                }else if (flag==Config.OPEN){
                    openPdf(context,destination);
                }
            }else {
                startDownload(context,fileURL,title,destination,flag);
            }
        }catch (Exception e){
            H.showMessage(context,"Something went wrong, try again.");
        }

    }

    public static void openPdf(Context context, String filepath){
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

    public static void startDownload(Context context,String fileURL,String title,String destination,int flag){
        H.showMessage(context,"Downloading Started....");
        final Uri uri = Uri.parse("file://" + destination);
        DownloadManager.Request request = new DownloadManager.Request(Uri.parse(fileURL));
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
        request.setDescription("Downloading....");
        request.setTitle(title);
        request.setDestinationUri(uri);

        final DownloadManager manager = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
        final long downloadId = manager.enqueue(request);
        BroadcastReceiver onComplete = new BroadcastReceiver() {
            public void onReceive(Context ctxt, Intent intent) {
                H.showMessage(ctxt,"Downloading Completed...");
                context.unregisterReceiver(this);
                if (flag==Config.SHARE){
                    sharePdf(context,destination);
                }else if (flag==Config.OPEN){
                    openPdf(context,destination);
                }
            }
        };
        context.registerReceiver(onComplete, new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));
    }

    public static void sharePdf(Context context,String destination){

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

}
