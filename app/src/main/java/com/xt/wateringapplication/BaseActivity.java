package com.xt.wateringapplication;

import android.content.Context;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import android.view.Window;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.chinamobile.iot.onenet.OneNetApi;
import com.chinamobile.iot.onenet.http.Config;
import com.gyf.immersionbar.ImmersionBar;

import java.util.concurrent.TimeUnit;

/**
 * @author:DIY
 * @date: 2021/4/12
 */
public abstract class BaseActivity extends AppCompatActivity {
    protected final String TAG = this.getClass().getSimpleName();

    private static Toast toast;
    public Context context;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = this;
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(initLayout());
        initImmersionBar();
        initView();
        initData();


    }

    protected abstract int initLayout();
    protected abstract void initView();
    protected abstract void initData();

    private void initImmersionBar() {
        ImmersionBar.with(this)
                .navigationBarColor(R.color.white)
                .statusBarColor(R.color.white)
                .statusBarDarkFont(true)
                .fitsSystemWindows(true)
                .init();

    }

    public void showToast(String msg) {
        try {
            if(null == toast) {
                toast = Toast.makeText(context,msg,Toast.LENGTH_SHORT);
            }
            else {
                toast.setText(msg);
            }
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    toast.show();
                }
            });
        } catch (Exception e) {
            Log.e(TAG,Log.getStackTraceString(e));
            Looper.prepare();
            Toast.makeText(this,msg,Toast.LENGTH_SHORT);
            Looper.loop();
        }
    }
}
