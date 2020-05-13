package com.mystery.update.process;

import android.app.Dialog;
import android.content.Context;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.mystery.update.R;
import com.mystery.update.UpdateAppBean;


/**
 * 更新对话框
 */
class UpdateDialog extends Dialog {

    private Context context;

    UpdateDialog(@NonNull Context context) {
        super(context, R.style.update_dialog_style);
        this.context = context;
        init();
    }

    private TextView tvVersion;
    private TextView tvContent;
    private CheckBox cbIgnore;
    private LinearLayout llIgnore;
    private TextView tvNext, tvImmediately;
    private ProgressBar pb_downLoad;

    private void init() {
        View dialogView = View.inflate(context, R.layout.update_dialog, null);
        tvVersion = dialogView.findViewById(R.id.tv_versionCode);
        tvContent = dialogView.findViewById(R.id.tv_content);
        llIgnore = dialogView.findViewById(R.id.ll_ignore);
        cbIgnore = dialogView.findViewById(R.id.cb_ignore);
        tvNext = dialogView.findViewById(R.id.tv_next);
        tvImmediately = dialogView.findViewById(R.id.tv_immediately);
        pb_downLoad = dialogView.findViewById(R.id.pb_downLoad);

        setContentView(dialogView);
        setCanceledOnTouchOutside(false);
        setCancelable(true);
    }

    /**
     * 升级可用（主动弹出）
     */
    void onAuthUpdateAvailable(UpdateAppBean appBean) {
        //显示忽略
        llIgnore.setVisibility(View.VISIBLE);
        //显示下次再说和立即更新
        tvNext.setVisibility(View.VISIBLE);
        tvNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
                if (updateListener != null) updateListener.next(cbIgnore.isChecked());
            }
        });
        tvImmediately.setVisibility(View.VISIBLE);
        tvImmediately.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (updateListener != null) updateListener.update();
            }
        });
        //不显示下载进度条
        pb_downLoad.setVisibility(View.GONE);
        tvVersion.setText(String.valueOf(appBean.getNewVersion()));
        tvContent.setText(appBean.getUpdateLog());
    }

    /**
     * 下载中
     */
    void onDownLoad(int percent) {
        pb_downLoad.setVisibility(View.VISIBLE);
        pb_downLoad.setProgress(percent);
        //不显示：下次再说、立即更新、关闭三个按钮
        tvNext.setVisibility(View.GONE);
        tvImmediately.setVisibility(View.GONE);
    }

    //接口回调
    private UpdateListener updateListener;

    public interface UpdateListener {
        void next(boolean checked);

        void update();
    }

    void setUpdateListener(UpdateListener listener) {
        this.updateListener = listener;
    }

}