package com.xt.wateringapplication;

/**
 * @author:DIY
 * @date: 2021/4/16
 */
public interface OnRefresh {
    void onRefresh(GetStatusBean getStatusBean);
    void onError(String errorMsg);
}
