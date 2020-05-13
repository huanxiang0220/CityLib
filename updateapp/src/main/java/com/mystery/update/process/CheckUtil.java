package com.mystery.update.process;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Environment;

import java.io.File;


/**
 * 作者:  tang
 * 时间： 2020/3/27 0027 下午 6:28
 * 邮箱： 3349913147@qq.com
 * 描述：
 */
class CheckUtil {

    public static String cachePath = Environment.getExternalStorageDirectory()
            + File.separator
            + "City"
            + File.separator
            + "APKPath";

    public static String getAPKPath() {
        return cachePath;
    }

    /**
     * 删除目录
     */
    public static void deleteDir(String path) {
        File dir = new File(path);
        deleteDirWithFile(dir);
    }

    /**
     * 删除目录下文件
     */
    public static void deleteDirWithFile(File dir) {
        if (dir == null || !dir.exists() || !dir.isDirectory())
            return;
        for (File file : dir.listFiles()) {
            if (file.isFile()) {
                boolean isDeleteSuccess = file.delete(); // 删除所有文件
                Logger.d("isDeleteSuccess>>>: " + isDeleteSuccess);

            } else if (file.isDirectory()) {
                deleteDirWithFile(file); // 递规的方式删除文件夹
            }
        }
//        dir.delete();// 删除目录本身
    }

    public static int getVersionCode(Context context) {
        try {
            //读取版本号
            PackageManager manager = context.getPackageManager();
            PackageInfo info = manager.getPackageInfo(context.getPackageName(), 0);
            return info.versionCode;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

}