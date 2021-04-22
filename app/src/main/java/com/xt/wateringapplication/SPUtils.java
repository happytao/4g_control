package com.xt.wateringapplication;

import android.content.Context;
import android.content.SharedPreferences;

import okhttp3.Interceptor;

/**
 * @author:DIY
 * @date: 2021/4/17
 */
public class SPUtils {
    //手机里文件名
    public static final String FILE_NAME = "share_data";

    /**
     * 将数据保存到SharedPreferences
     * @param context
     * @param key
     * @param object
     */
    public static void put(Context context, String key, Object object){
        SharedPreferences sp = context.getSharedPreferences(FILE_NAME,Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();

        if(object instanceof String) {
            editor.putString(key,(String)object);
        }
        else if(object instanceof Integer) {
            editor.putInt(key,(Integer)object);
        }
        else if(object instanceof Boolean) {
            editor.putBoolean(key,(boolean)object);
        }
        else if(object instanceof Float) {
            editor.putFloat(key,(Float)object);
        }
        else if(object instanceof Long) {
            editor.putLong(key,(Long)object);
        }
        else {
            editor.putString(key,object.toString());
        }
        editor.apply();
    }

    public static Object get(Context context, String key, Object defaultObject) {
        SharedPreferences sp = context.getSharedPreferences(FILE_NAME,Context.MODE_PRIVATE);
        if(defaultObject instanceof String) {
            return sp.getString(key,(String)defaultObject);
        }
        else if(defaultObject instanceof Integer) {
            return sp.getInt(key,(Integer)defaultObject);
        }
        else if(defaultObject instanceof Boolean) {
            return sp.getBoolean(key,(boolean)defaultObject);
        }
        else if(defaultObject instanceof Float) {
            return sp.getFloat(key,(Float)defaultObject);
        }
        else if(defaultObject instanceof Long) {
            return sp.getLong(key,(Long)defaultObject);
        }
        else {
            return sp.getString(key,defaultObject.toString());
        }
    }

    public static void clear(Context context){
        SharedPreferences sp = context.getSharedPreferences(FILE_NAME,Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.clear().apply();
    }
}
