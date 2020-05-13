package com.mystery.update;

import android.app.Activity;

import com.mystery.update.utils.AppUpdateUtils;

import org.json.JSONObject;

/**
 * 新版本版本检测回调
 */
public class UpdateCallback {

    /**
     * 解析json,自定义协议
     *
     * @param json 服务器返回的json
     * @return UpdateAppBean
     */
    protected UpdateAppBean parseJson(String json, Activity mActivity) {
        UpdateAppBean updateAppBean = new UpdateAppBean();
        try {
            JSONObject jObj = new JSONObject(json);
            JSONObject jsonObject = jObj.getJSONObject("data");

            boolean update = jsonObject.optInt("version_code") > AppUpdateUtils.getVersionCode(mActivity);
            updateAppBean
                    .setUpdate(update ? "Yes" : "No")
                    .setVersionName(jsonObject.optString("version"))
                    .setVersionCode(jsonObject.optInt("version_code"))
                    .setApkFileUrl(jsonObject.optString("apk_file_url"))
                    .setTargetSize(jsonObject.optString("target_size"))
                    .setUpdateLog(jsonObject.optString("content"))
                    .setConstraint(jsonObject.optBoolean("constraint"))
                    .setNewMd5(jsonObject.optString("new_md5"));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return updateAppBean;
    }

    /**
     * 有新版本
     *
     * @param updateApp        新版本信息
     * @param updateAppManager app更新管理器
     */
    protected void hasNewApp(UpdateAppBean updateApp, UpdateAppManager updateAppManager) {

    }

    /**
     * 网路请求之后
     */
    protected void onAfter() {
    }


    /**
     * 没有新版本
     */
    protected void noNewApp() {
    }

    /**
     * 网络请求之前
     */
    protected void onBefore() {
    }

}
