package com.mystery.update.process;

import android.app.Activity;

import com.mystery.update.UpdateAppBean;
import com.mystery.update.UpdateAppManager;
import com.mystery.update.UpdateCallback;
import com.mystery.update.service.DownloadService;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;


/**
 * 作者:  tang
 * 时间： 2020/3/27 0027 下午 6:31
 * 邮箱： 3349913147@qq.com
 * 描述：
 */
class RequestManager {

    private static final String ignoreCodeKey = "ignoreCode";

    void with(final Activity activity, CheckAppUpdate checkAppUpdate) {
        new UpdateAppManager
                .Builder()
                //必须设置，当前Activity
                .setActivity(activity)
                //必须设置，实现httpManager接口的对象
                .setHttpManager(checkAppUpdate.getHttpManager())
                //必须设置，更新地址
                .setUpdateUrl(checkAppUpdate.getUpdateUrl())
                //以下设置，都是可选
                //设置请求方式，默认get
                .setPost(false)
                //设置apk下砸路径，默认是在下载到sd卡下/Download/1.0.0/test.apk
                .setTargetPath(CheckUtil.getAPKPath())
                .build()
                //检测是否有新版本
                .checkNewApp(new UpdateCallback() {

                    @Override
                    protected UpdateAppBean parseJson(String json, Activity mActivity) {
                        Logger.e("parseJson");

                        UpdateAppBean updateAppBean = new UpdateAppBean();
                        try {
                            JSONObject jObj = new JSONObject(json);
                            JSONObject jsonObject = jObj.getJSONObject("data");

                            boolean update = jsonObject.optInt("version_code") > CheckUtil.getVersionCode(mActivity);

                            updateAppBean
                                    //（必须）是否更新Yes,No
                                    .setUpdate(update ? "Yes" : "No")
                                    //（必须）新版本号，
                                    .setVersionName(jsonObject.optString("version"))
                                    .setVersionCode(jsonObject.optInt("version_code"))
                                    //（必须）下载地址
                                    .setApkFileUrl(jsonObject.optString("apk_file_url"))
                                    //（必须）更新内容
                                    .setUpdateLog(jsonObject.optString("content"))
                                    //大小，不设置不显示大小，可以不设置
                                    .setTargetSize(jsonObject.optString("target_size"))
                                    //是否强制更新，可以不设置
                                    .setConstraint(false)
                                    //设置md5，可以不设置
                                    .setNewMd5(jsonObject.optString("new_md51"));

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        return updateAppBean;
                    }

                    /**
                     * 有新版本
                     */
                    @Override
                    protected void hasNewApp(UpdateAppBean appBean, UpdateAppManager updateAppManager) {
                        Logger.e("hasNewApp");
                        if (appBean == null)
                            return;
                        //忽略版本号
                        int ignoreCode = PrefsUtils.getInt(activity, ignoreCodeKey);
                        if (ignoreCode == appBean.getVersionCode())
                            return;
                        if (CheckUtil.getVersionCode(activity) >= appBean.getVersionCode())
                            return;
                        showUpdatePop(activity, appBean, updateAppManager);
                    }

                    @Override
                    protected void noNewApp() {
                        //删除文件（后期换成广播-监听安装完成之后再删除--要考虑进程杀死导致文件不是完整文件）
                        new Runnable() {
                            @Override
                            public void run() {
                                CheckUtil.deleteDir(CheckUtil.getAPKPath());
                            }
                        }.run();
                    }
                });
    }

    private UpdateAppManager updateAppManager;
    private UpdateDialog updateDialog;

    private void showUpdatePop(final Activity activity, final UpdateAppBean appBean, UpdateAppManager updateAppManager) {
        if (appBean == null)
            return;
        this.updateAppManager = updateAppManager;

        updateDialog = new UpdateDialog(activity);
        updateDialog.onAuthUpdateAvailable(appBean);
        updateDialog.setUpdateListener(new UpdateDialog.UpdateListener() {

            @Override
            public void next(boolean checked) {
                //忽略的时候：保存忽略版本号
                if (checked) {
                    PrefsUtils.setInt(activity, ignoreCodeKey, appBean.getVersionCode());
                }
            }

            @Override
            public void update() {
                download();
            }
        });
        updateDialog.show();
    }

    /**
     * 下载
     */
    private void download() {
        if (updateAppManager != null)
            //开始下载
            updateAppManager.download(new DownloadService.DownloadCallback() {
                @Override
                public void onStart() {
                    Logger.e("onStart");
                    updateDialog.onDownLoad(Math.round(0));
                    if (!updateDialog.isShowing()) updateDialog.show();
                }

                /**
                 * 进度
                 *
                 * @param progress  进度 0.00 -1.00 ，总大小
                 * @param totalSize 总大小 单位B
                 */
                @Override
                public void onProgress(float progress, long totalSize) {
                    Logger.e("onProgress" + Math.round(progress * 100));
                    updateDialog.onDownLoad(Math.round(progress * 100));
                }

                /**
                 *
                 * @param total 总大小 单位B
                 */
                @Override
                public void setMax(long total) {

                }

                @Override
                public boolean onFinish(File file) {
                    Logger.e("onFinish" + file.getPath());
                    updateDialog.dismiss();
                    updateDialog = null;
                    return true;
                }

                @Override
                public void onError(String msg) {
                    Logger.e("onError" + msg);
                    updateDialog.dismiss();
                    updateDialog = null;
                    Logger.e("下载错误：" + msg);
                    //删除文件
                    CheckUtil.deleteDir(CheckUtil.getAPKPath());
                }
            });
    }

}