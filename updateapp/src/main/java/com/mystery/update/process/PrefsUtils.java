package com.mystery.update.process;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.support.annotation.NonNull;

/**
 * SharedPreference工具类
 */
class PrefsUtils {

    //<editor-fold desc="New API">
    private final static String SP_NAME = "update";

    private static SharedPreferences sp;

    private static SharedPreferences getSp(Context context) {
        if (sp == null) {
            sp = context.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE);
        }
        return sp;
    }

    /**
     * 获取int 数据
     *
     * @return 如果没有值，返回-1
     */
    static int getInt(Context context, @NonNull String key) {
        SharedPreferences sp = getSp(context);
        return sp.getInt(key, -1);
    }

    /**
     * 存int缓存
     */
    static void setInt(Context context, @NonNull String key, int value) {
        SharedPreferences sp = getSp(context);
        Editor editor = sp.edit();
        editor.putInt(key, value);
        editor.apply();
    }

}