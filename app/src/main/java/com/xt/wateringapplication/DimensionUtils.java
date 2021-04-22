package com.xt.wateringapplication;

import android.content.Context;

/**
 * @author:DIY
 * @date: 2021/4/18
 */
public class DimensionUtils {
    public static int dip2px(Context context,float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int)(dpValue * scale + 0.5f);
    }
}
