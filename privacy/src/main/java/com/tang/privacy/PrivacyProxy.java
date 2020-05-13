package com.tang.privacy;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.text.Html;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

/**
 * 作者:  tang
 * 时间： 2019/10/30 0030 上午 10:13
 * 邮箱： 3349913147@qq.com
 * 描述： 处理隐私弹框
 */
public class PrivacyProxy {

    /**
     * 同意的缓存标识
     */
    private static final String CacheKey = "Privacy";

    private Activity mActivity;

    public PrivacyProxy(Activity mActivity) {
        this.mActivity = mActivity;
    }

    public void showPop() {
        if (RecordUtils.getBoolean(mActivity, PrivacyProxy.CacheKey)) {
            return;
        }
        View inflate = View.inflate(mActivity, R.layout.privacy_dialog_layout, null);
        TextView tvContent = inflate.findViewById(R.id.tv_content);
        String privacy = mActivity.getString(R.string.privacy);
        Spanned spanned = Html.fromHtml(privacy);
        addClickableSpan(spanned);
        tvContent.setText(spanned);
        tvContent.setMovementMethod(LinkMovementMethod.getInstance());
        AlertDialog dialog = new AlertDialog.Builder(mActivity)
                .setView(inflate)
                .setNegativeButton(R.string.privacy_disagree, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        //杀死应用
                        android.os.Process.killProcess(android.os.Process.myPid());
                        System.exit(0);
                    }
                })
                .setPositiveButton(R.string.privacy_agreeAndContinue, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        RecordUtils.setBoolean(mActivity, PrivacyProxy.CacheKey, true);
                        dialog.dismiss();
                    }
                })
                .setCancelable(false)
                .create();
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();
        processButton(dialog);
    }

    private void addClickableSpan(Spanned spanned) {
        SpannableStringBuilder spanBuilder;
        if (spanned instanceof SpannableStringBuilder) {
            spanBuilder = (SpannableStringBuilder) spanned;
        } else {
            spanBuilder = new SpannableStringBuilder(spanned);
        }
        int queryStart = 0;
        int queryEnd = spanBuilder.length();
        ForegroundColorSpan[] spans = spanBuilder.getSpans(queryStart, queryEnd, ForegroundColorSpan.class);
        for (ForegroundColorSpan textSpan : spans) {
            int start = spanBuilder.getSpanStart(textSpan);
            int end = spanBuilder.getSpanEnd(textSpan);
            ClickableSpan clickableSpan = new ClickableSpan() {
                @Override
                public void updateDrawState(@NonNull TextPaint ds) {
                    ds.setColor(ContextCompat.getColor(mActivity, R.color.privacy_theme_color));
                    ds.setUnderlineText(false);
                }

                @Override
                public void onClick(@NonNull View widget) {
                    mActivity.startActivity(new Intent(mActivity, PrivacyWebActivity.class));
                }
            };
            spanBuilder.setSpan(clickableSpan, start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        }
    }

    /**
     * 处理对话框上相关的系统按钮的属性
     */
    private void processButton(AlertDialog dialog) {
        //需在调用show之后设置
        int color = ContextCompat.getColor(mActivity, R.color.privacy_444);
        Button button = dialog.getButton(AlertDialog.BUTTON_NEGATIVE);
        button.setTypeface(Typeface.defaultFromStyle(Typeface.NORMAL));
        button.setTextColor(color);
        //需在调用show之后设置
        Button buttonPositive = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
        buttonPositive.setTypeface(Typeface.defaultFromStyle(Typeface.NORMAL));
        buttonPositive.setTextColor(ContextCompat.getColor(mActivity, R.color.privacy_theme_color));
    }

}