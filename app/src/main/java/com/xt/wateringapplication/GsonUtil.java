package com.xt.wateringapplication;

import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

/**
 * @author:DIY
 * @date: 2021/4/16
 */
public class GsonUtil {
    private static Gson gson = new Gson();
    public static <T> T fromJson(String result, Class<T> clazz) {
        try {
            if(null == gson) {
                gson = new Gson();
            }
            return gson.fromJson(result,clazz);
        } catch (JsonSyntaxException e) {
            Log.e("TAG",Log.getStackTraceString(e));
            return null;
        }
    }

    public static String toJson(Object obj) {
        try {
            if(null == gson) {
                gson = new Gson();
            }
            return gson.toJson(obj);
        } catch (Exception e) {
            Log.e("TAG",Log.getStackTraceString(e));
            return null;
        }
    }



}
