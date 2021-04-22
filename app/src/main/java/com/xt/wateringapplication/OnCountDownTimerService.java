package com.xt.wateringapplication;

/**
 * @author:DIY
 * @date: 2021/4/19
 */
public interface OnCountDownTimerService {
    void onTick(long mill);
    void onFinish();
}
