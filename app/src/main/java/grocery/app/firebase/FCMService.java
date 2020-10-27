package grocery.app.firebase;


import android.app.ActivityManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.text.Html;
import android.text.Spanned;
import android.util.Log;
import android.widget.RemoteViews;

import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.adoisstudio.helper.H;
import com.adoisstudio.helper.Session;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import grocery.app.BaseActivity;
import grocery.app.R;
import grocery.app.common.P;
import grocery.app.util.Config;

@RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
public class FCMService extends FirebaseMessagingService {

    private Intent resultIntent;
    private static final String TAG = "MyFCMService";
    private static ArrayList<Map<String, String>> arrayList = new ArrayList<>();
    private Bitmap bitmap;

    //server key : AAAAydWqaYw:APA91bEKi_HWx-ihnSlCXllx_0Ni9WsDR23DLmLOFng0x93awGtaTSQiajBjVtM3k0KdJKCBWvTofrvw3ctugnbalHNT0Qg2N_wU4qA0Dibh0G4ARc7NpK7PofhEvN0xIJHNZs2zNQOy

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);

//        String msg = null;
//        if (remoteMessage.getData() != null && remoteMessage.getData().size() > 0) {
//            Log.e(TAG, "onMessageReceived0: " + remoteMessage.getData());
//            Map<String, String> data = remoteMessage.getData();
//            msg = data.get("body");
//        } else if (remoteMessage.getNotification() != null) {
//            msg = remoteMessage.getNotification().getBody();
//        }
//
//        Log.e(TAG, "onMessageReceived1: "+ msg );
//        handleData(msg,this);

        if (remoteMessage == null)
            return;

        if (remoteMessage.getData().size() > 0) {

            Map data = remoteMessage.getData();
            arrayList.add(data);
            H.log("arrayListIs", arrayList + "");
            for (Map map : arrayList)
            {
                Object action = map.get(P.action);
                Object title = map.get(P.title);
                Object description = map.get(P.description);
                Object actionData = map.get(P.action_data);
                Object imageUrl = map.get(P.icon);

                if (title == null || description == null)
                    return;

                if (action != null && actionData != null)
                {
                    if (action.toString().equalsIgnoreCase("CATEGORY"))
                        sendDefaultNotification(action.toString(), title.toString(), description.toString(), actionData.toString(),0);
                    else if (action.toString().equalsIgnoreCase("OFFER"))
                        sendDefaultNotification(action.toString(), title.toString(), description.toString(), actionData.toString(),1);
                    else if (imageUrl != null)
                    {
                        bitmap = getBitmapfromUrl(imageUrl.toString());
                        sendCustomNotification(action.toString(), title.toString(), description.toString(), actionData.toString(),2);
                    }

                }
            }
        }

    }

    private void handleData(String message , Context context){
        String title = context.getResources().getString(R.string.app_name);
        if (!isAppIsInBackground(getApplicationContext())) {
            Intent pushNotification = new Intent(Config.PUSH_NOTIFICATION);
            pushNotification.putExtra("message", message);
            LocalBroadcastManager.getInstance(this).sendBroadcast(pushNotification);
            resultIntent = new Intent(getApplicationContext(), BaseActivity.class);
            showNotification(getApplicationContext(), title, message,resultIntent);
        } else {
            Intent pushNotification = new Intent(Config.PUSH_NOTIFICATION);
            pushNotification.putExtra("message", message);
            LocalBroadcastManager.getInstance(this).sendBroadcast(pushNotification);
            resultIntent = new Intent(getApplicationContext(), BaseActivity.class);
            showNotification(getApplicationContext(), title, message, resultIntent);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    public void showNotification(Context context, String title, String body, Intent intent) {

        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        int notificationId = 1;
        String channelId = "channel-01";
        String channelName = "Channel Name";
        int importance = NotificationManager.IMPORTANCE_HIGH;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel mChannel = new NotificationChannel(
                    channelId, channelName, importance);
            notificationManager.createNotificationChannel(mChannel);
        }

        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context, channelId)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle(title)
                .setContentText(body);

        TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
        stackBuilder.addNextIntent(intent);
        PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(
                0,
                PendingIntent.FLAG_UPDATE_CURRENT
        );
        mBuilder.setContentIntent(resultPendingIntent);

        notificationManager.notify(notificationId, mBuilder.build());
    }

    private void sendDefaultNotification(String action, String title, String messageBody, String action_data, int requestCode) {

        Intent intent = new Intent(this, BaseActivity.class);
        intent.putExtra(P.action, action);
        intent.putExtra(P.action_data, action_data);
        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher);

        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, requestCode, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        String channelId = getString(R.string.app_name);
        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder =
                new NotificationCompat.Builder(this, channelId)
                        .setSmallIcon(R.mipmap.ic_launcher)
                        .setLargeIcon(bitmap)
                        //.setColor(Color.argb(100, 183, 0, 0))
                        .setContentTitle(convertFromHtml(title))
                        .setContentText(convertFromHtml(messageBody))
                        .setAutoCancel(true)
                        .setSound(defaultSoundUri)
                        .setContentIntent(pendingIntent);

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        // Since android Oreo notification channel is needed.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(channelId,
                    "Channel human readable title",
                    NotificationManager.IMPORTANCE_DEFAULT);
            notificationManager.createNotificationChannel(channel);
        }

        notificationManager.notify((int) System.currentTimeMillis(), notificationBuilder.build());
    }

    private void sendCustomNotification(String action, String title, String description, String actionData,int requestCode) {
        RemoteViews remoteViews = new RemoteViews(getPackageName(), R.layout.activity_notification_custome);
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        remoteViews.setImageViewBitmap(R.id.imageView, bitmap);
        remoteViews.setTextViewText(R.id.titleTextView, title);
        remoteViews.setTextViewText(R.id.descriptionTextView, description);

        Intent intent = new Intent(this, BaseActivity.class);
        intent.putExtra(P.action, action);
        intent.putExtra(P.action_data, actionData);

        PendingIntent pendingIntent = PendingIntent.getActivity(this, requestCode, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        String channelId = getString(R.string.app_name);

        NotificationCompat.Builder notificationCompactBuilder = new NotificationCompat.Builder(this,channelId);
        notificationCompactBuilder.setSmallIcon(R.mipmap.ic_launcher);
        notificationCompactBuilder.setAutoCancel(true);
        notificationCompactBuilder.setSound(defaultSoundUri);
        if (Build.VERSION.SDK_INT >= 24)
            notificationCompactBuilder.setCustomBigContentView(remoteViews);

        // Since android Oreo notification channel is needed.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
        {
            NotificationChannel channel = new NotificationChannel(channelId, "Channel human readable title", NotificationManager.IMPORTANCE_DEFAULT);
            notificationManager.createNotificationChannel(channel);
        }
        notificationCompactBuilder.setContentIntent(pendingIntent);

        showIconCount(notificationCompactBuilder);
        notificationManager.notify(0, notificationCompactBuilder.build());
    }

    public boolean isAppIsInBackground(Context context) {
        boolean isInBackground = true;
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.KITKAT_WATCH) {
            List<ActivityManager.RunningAppProcessInfo> runningProcesses = am.getRunningAppProcesses();
            for (ActivityManager.RunningAppProcessInfo processInfo : runningProcesses) {
                if (processInfo.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND) {
                    for (String activeProcess : processInfo.pkgList) {
                        if (activeProcess.equals(context.getPackageName())) {
                            isInBackground = false;
                        }
                    }
                }
            }
        } else {
            List<ActivityManager.RunningTaskInfo> taskInfo = am.getRunningTasks(1);
            ComponentName componentInfo = taskInfo.get(0).topActivity;
            if (componentInfo.getPackageName().equals(context.getPackageName())) {
                isInBackground = false;
            }
        }
        return isInBackground;
    }

    private void showIconCount(NotificationCompat.Builder notificationCompactBuilder) {

        try {
            Notification notification = notificationCompactBuilder.build();
            Session session = new Session(getBaseContext());

            int i =  session.getInt("iconCount");
            i = i + arrayList.size();
            session.addInt("iconCount",i);

            //  .applyNotification(getApplicationContext(), notification, i);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    public Bitmap getBitmapfromUrl(String imageUrl) {
        try {
            URL url = new URL(imageUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.connect();
            InputStream input = connection.getInputStream();
            Bitmap bitmap = BitmapFactory.decodeStream(input);
            return bitmap;

        } catch (Exception e) {
            e.printStackTrace();
            return null;

        }
    }

    private Spanned convertFromHtml(String text) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N)
            return Html.fromHtml(text, Html.FROM_HTML_MODE_COMPACT);
        else
            return Html.fromHtml(text);
    }

}
