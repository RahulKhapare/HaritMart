package grocery.app.firebase;


import android.app.ActivityManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;

import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.List;
import java.util.Map;

import grocery.app.BaseActivity;
import grocery.app.R;
import grocery.app.util.Config;

@RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
public class FCMService extends FirebaseMessagingService {

    Intent resultIntent;
    private static final String TAG = "MyFCMService";

    //server key : AAAAydWqaYw:APA91bEKi_HWx-ihnSlCXllx_0Ni9WsDR23DLmLOFng0x93awGtaTSQiajBjVtM3k0KdJKCBWvTofrvw3ctugnbalHNT0Qg2N_wU4qA0Dibh0G4ARc7NpK7PofhEvN0xIJHNZs2zNQOy

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);

        String msg = null;
        if (remoteMessage.getData() != null && remoteMessage.getData().size() > 0) {
            Log.e(TAG, "onMessageReceived0: " + remoteMessage.getData());
            Map<String, String> data = remoteMessage.getData();
            msg = data.get("body");
        } else if (remoteMessage.getNotification() != null) {
            msg = remoteMessage.getNotification().getBody();
        }

        Log.e(TAG, "onMessageReceived1: "+ msg );
        handleData(msg,this);

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

}
