package com.xt.wateringapplication;

/**
 * @author:DIY
 * @date: 2021/4/14
 */
public interface OnCountDownTimerFinished {
    public void onTick(long millisUntilFinished);
    public void onFinished(boolean isOrder);
}
