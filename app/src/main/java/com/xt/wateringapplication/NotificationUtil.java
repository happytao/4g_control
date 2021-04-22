package com.xt.wateringapplication;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Vibrator;

import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;

import java.io.File;

import static android.content.Context.VIBRATOR_SERVICE;

/**
 * @author:DIY
 * @date: 2021/4/18
 */
public class NotificationUtil {
    private static NotificationManager notificationManager;

    @RequiresApi(api = Build.VERSION_CODES.O)
    public static void initNotificationManager(Context context) {

        notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        String channelId = "myChannel";
        String channelName = "浇灌完成通知";
        int importance = NotificationManager.IMPORTANCE_MAX;
        createNotificationChannel(channelId,channelName,importance);
        createForegroundNotificationChannel("mySecondChannel","前台服务",importance);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private static void createNotificationChannel(String channelId, String channelName, int importance) {
        NotificationChannel notificationChannel = new NotificationChannel(channelId,channelName,importance);
        notificationChannel.setVibrationPattern(new long[] {0,1000,0,1000});
        notificationChannel.enableVibration(true);
        notificationManager.createNotificationChannel(notificationChannel);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private static void createForegroundNotificationChannel(String channelId, String channelName, int importance) {
        NotificationChannel notificationChannel = new NotificationChannel(channelId,channelName,importance);
        notificationChannel.setSound(null,null);
        notificationManager.createNotificationChannel(notificationChannel);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public static void sendNotificationMessage(Context context, String msg) {
        Notification notification = new NotificationCompat.Builder(context, "myChannel")
                .setContentTitle(msg)
                .setContentText(msg)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setLargeIcon(BitmapFactory.decodeResource(context.getResources(), R.mipmap.ic_launcher))
                .setVibrate(new long[] {0,1000,0,1000})
                .build();
        if (notificationManager == null) {
            initNotificationManager(context);
        }
        notificationManager.notify(1, notification);

    }
}
