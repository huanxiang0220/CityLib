package com.tang.privacy;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.preference.PreferenceManager;
import android.util.Base64;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

/**
 * SharedPreference工具类
 */
class RecordUtils {

    private final static String SP_NAME = "PrivacyKeyTool";

    private static SharedPreferences sp;

    private static SharedPreferences getSp(Context context) {
        if (sp == null) {
            sp = context.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE);
        }
        return sp;
    }

    /**
     * 获取boolean 数据
     *
     * @return 如果没有值，返回false
     */
    public static boolean getBoolean(Context context, String key) {
        SharedPreferences sp = getSp(context);
        return sp.getBoolean(key, false);
    }

    /**
     * 获取boolean 数据
     */
    public static boolean getBoolean(Context context, String key,
                                     boolean defValue) {
        SharedPreferences sp = getSp(context);
        return sp.getBoolean(key, defValue);
    }

    /**
     * 存boolean缓存
     */
    public static void setBoolean(Context context, String key, boolean value) {
        SharedPreferences sp = getSp(context);
        Editor editor = sp.edit();
        editor.putBoolean(key, value);
        editor.commit();
    }

}