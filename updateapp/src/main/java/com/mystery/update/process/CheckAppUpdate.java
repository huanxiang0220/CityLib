package com.mystery.update.process;

import android.app.Activity;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;

import com.mystery.update.HttpManager;

import java.io.File;

import static android.Manifest.permission.READ_EXTERNAL_STORAGE;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;
import static android.content.pm.PackageManager.PERMISSION_GRANTED;

/**
 * APP升级
 */
public class CheckAppUpdate {

    private HttpManager httpManager;
    private String updateUrl;

    HttpManager getHttpManager() {
        return httpManager;
    }

    String getUpdateUrl() {
        return updateUrl;
    }

    private CheckAppUpdate(Builder builder) {
        this.httpManager = builder.httpManager;
        this.updateUrl = builder.updateUrl;
    }

    public void startCheck(Activity activity) {
        if (ContextCompat.checkSelfPermission(activity, READ_EXTERNAL_STORAGE) != PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(activity, WRITE_EXTERNAL_STORAGE) != PERMISSION_GRANTED) {
            CheckUtil.cachePath = activity.getCacheDir() + File.separator
                    + "City"
                    + File.separator
                    + "APKPath";
        }
        new RequestManager().with(activity, this);
    }

    public static class Builder {

        private HttpManager httpManager;
        private String updateUrl;

        public Builder setHttpManager(HttpManager httpManager) {
            this.httpManager = httpManager;
            return this;
        }

        public Builder setUpdateUrl(String updateUrl) {
            this.updateUrl = updateUrl;
            return this;
        }

        public CheckAppUpdate build() {
            if (httpManager == null) {
                throw new IllegalArgumentException("请设置HttpManager");
            }
            if (updateUrl == null) {
                throw new IllegalArgumentException("请设置updateUrl");
            }
            return new CheckAppUpdate(this);
        }
    }

}