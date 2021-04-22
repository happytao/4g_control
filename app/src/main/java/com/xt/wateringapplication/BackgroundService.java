package com.xt.wateringapplication;

import android.app.Notification;
import android.app.Service;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Binder;
import android.os.Build;
import android.os.CountDownTimer;
import android.os.IBinder;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;

/**
 * @author:DIY
 * @date: 2021/4/19
 */
public class BackgroundService extends Service {
    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onCreate() {
        super.onCreate();
        NotificationUtil.initNotificationManager(this);
        Notification notification = new NotificationCompat.Builder(this, "mySecondChannel")
                .setContentTitle("远程浇灌软件")
                .setContentText("远程浇灌软件")
                .setSmallIcon(R.mipmap.ic_launcher)
                .setLargeIcon(BitmapFactory.decodeResource(this.getResources(), R.mipmap.ic_launcher))
                .build();
        startForeground(0,notification);
    }

    private OnCountDownTimerService onCountDownTimerService;
    private OnCountDownTimerFinished onCountDownTimerFinished = new OnCountDownTimerFinished() {
        @Override
        public void onTick(long millisUntilFinished) {
            onCountDownTimerService.onTick(millisUntilFinished);

        }

        @RequiresApi(api = Build.VERSION_CODES.O)
        @Override
        public void onFinished(boolean isOrder) {
            if(isOrder) {
                NotificationUtil.sendNotificationMessage(BackgroundService.this,"预约时间已到，开始浇灌");
            }
            else {
                NotificationUtil.sendNotificationMessage(BackgroundService.this,"浇灌完成！");
            }
            onCountDownTimerService.onFinish();

        }

    };

    private CountDownTimerBinder mBinder = new CountDownTimerBinder();
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    class CountDownTimerBinder extends Binder {
        private CountDownTimerUtils countDownTimerUtils;
        public void startTiming(TextView textView, long millsInFuture, long countDownInterval,boolean isOrder,OnCountDownTimerService onCountDownTimerService) {
            BackgroundService.this.onCountDownTimerService = onCountDownTimerService;
            countDownTimerUtils = new CountDownTimerUtils(textView,millsInFuture,countDownInterval,isOrder,onCountDownTimerFinished);
            countDownTimerUtils.start();
        }
        public void cancelTiming() {
            if(countDownTimerUtils != null) {
                countDownTimerUtils.cancle();
                countDownTimerUtils = null;
            }
        }

    }
}
