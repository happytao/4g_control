package com.xt.wateringapplication;

import android.graphics.Color;
import android.os.CountDownTimer;
import android.widget.TextView;

import java.lang.ref.WeakReference;

/**
 * @author:DIY
 * @date: 2021/4/14
 */
public class CountDownTimerUtils extends CountDownTimer {
    private WeakReference<TextView> mTextView;
    private OnCountDownTimerFinished onCountDownTimerFinished;
    private boolean isOrder;
    /**
     * @param millisInFuture    The number of millis in the future from the call
     *                          to {@link #start()} until the countdown is done and {@link #onFinish()}
     *                          is called.
     * @param countDownInterval The interval along the way to receive
     *                          {@link #onTick(long)} callbacks.
     */
    public CountDownTimerUtils(TextView textView, long millisInFuture, long countDownInterval,boolean isOrder, OnCountDownTimerFinished onCountDownTimerFinished) {
        super(millisInFuture, countDownInterval);
        mTextView = new WeakReference<>(textView);
        this.onCountDownTimerFinished = onCountDownTimerFinished;
        this.isOrder = isOrder;
    }

    @Override
    public void onTick(long millisUntilFinished) {
        if(mTextView.get() == null) {
            cancle();
            return;
        }
        mTextView.get().setClickable(false);
        mTextView.get().setText(setViewTime(millisUntilFinished));
        onCountDownTimerFinished.onTick(millisUntilFinished);


    }

    @Override
    public void onFinish() {
        if(mTextView.get() == null) {
            cancle();
            return;
        }
        onCountDownTimerFinished.onFinished(isOrder);

    }

    private String setViewTime(long longTimeFinished) {
        StringBuilder sb = new StringBuilder(longTimeFinished / (60 * 60 * 1000) + "时");
        longTimeFinished %= (60 * 60 * 1000);
        sb.append(longTimeFinished / (60 * 1000) + "分");
        longTimeFinished %= (60 * 1000);
        sb.append(longTimeFinished / 1000 + "秒");
        return sb.toString();


    }

    public void cancle() {
        if(this != null) {
            this.cancel();
        }
    }


}
