package com.xt.wateringapplication;

import android.app.Application;
import android.content.Context;

import com.chinamobile.iot.onenet.OneNetApi;
import com.chinamobile.iot.onenet.http.Config;

import java.util.concurrent.TimeUnit;

/**
 * @author:DIY
 * @date: 2021/4/16
 */
public class BaseApplication extends Application {
    public static BaseApplication app;

    @Override
    public void onCreate() {
        super.onCreate();
        Config config = Config.newBuilder()
                .connectTimeout(60000, TimeUnit.MILLISECONDS)
                .readTimeout(60000,TimeUnit.MILLISECONDS)
                .writeTimeout(60000,TimeUnit.MILLISECONDS)
                .retryCount(2)
                .build();
        OneNetApi.init(this,true,config);
        app = this;
    }
}
