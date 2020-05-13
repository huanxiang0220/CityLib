package com.tang.privacy;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.net.http.SslError;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.webkit.SslErrorHandler;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;


/**
 * 声明
 */
public class PrivacyWebActivity extends AppCompatActivity {

    private WebView mWebView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.privacy_activity_web);
        mWebView = findViewById(R.id.webView);
        initData();
    }

    private void initData() {
        mWebView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                return false;
            }
        });
        mWebView.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onReceivedTitle(WebView view, String title) {
                super.onReceivedTitle(view, title);
            }
        });
        mWebView.setWebViewClient(new WebViewClient() {

            @Override
            public void onReceivedError(final WebView view, int errorCode, String description, String failingUrl) {
                super.onReceivedError(view, errorCode, description, failingUrl);
                view.loadData(failingUrl, "text/html", "UTF-8");
                view.getSettings().setDefaultTextEncodingName("UTF-8");
            }

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
                mWebView.setVisibility(View.GONE);
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                mWebView.setVisibility(View.VISIBLE);
            }

            @Override
            public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
                handlerSslError(PrivacyWebActivity.this, handler, error);
            }
        });
        initSetting();

        mWebView.loadUrl("http://www.cityorder123.com/appclause.html");
    }

    @SuppressLint("SetJavaScriptEnabled")
    private void initSetting() {
        WebSettings webSetting = mWebView.getSettings();
        webSetting.setJavaScriptCanOpenWindowsAutomatically(false);//是否允许弹框
        webSetting.setJavaScriptEnabled(false); //支持js
        //设置自适应屏幕，两者合用
        webSetting.setLoadWithOverviewMode(true);
        webSetting.setUseWideViewPort(true);

        webSetting.setSupportZoom(false);//支持缩放，默认为true。是下面那个的前提。
        webSetting.setAllowFileAccess(true); //设置可以访问文件
        webSetting.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.NARROW_COLUMNS);//支持内容重新布局
        webSetting.setBuiltInZoomControls(false);//设置内置的缩放控件。
        webSetting.setSupportMultipleWindows(false);//多窗口

        webSetting.setAppCacheEnabled(true);//开启 Application Caches 功能
        webSetting.setDatabaseEnabled(true);//开启 database storage API 功能
        webSetting.setDomStorageEnabled(true);//开启 DOM storage API 功能
        webSetting.setCacheMode(WebSettings.LOAD_DEFAULT);
        webSetting.setAppCacheMaxSize(Long.MAX_VALUE);
        webSetting.setAppCachePath(getApplicationContext().getCacheDir().getAbsolutePath());//设置 Application Caches 缓存目录
        webSetting.setPluginState(WebSettings.PluginState.ON_DEMAND);

        //解决混合模式
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            webSetting.setMixedContentMode(0);
        }
    }

    /**
     * 处理SslError
     */
    public void handlerSslError(Context context, final SslErrorHandler handler, SslError error) {
        String msg = "";
        if (error.getPrimaryError() == SslError.SSL_DATE_INVALID
                || error.getPrimaryError() == SslError.SSL_EXPIRED
                || error.getPrimaryError() == SslError.SSL_IDMISMATCH
                || error.getPrimaryError() == SslError.SSL_INVALID
                || error.getPrimaryError() == SslError.SSL_NOTYETVALID
                || error.getPrimaryError() == SslError.SSL_UNTRUSTED) {
            if (error.getPrimaryError() == SslError.SSL_DATE_INVALID) {
                msg = "The date of the certificate is invalid";
            } else if (error.getPrimaryError() == SslError.SSL_INVALID) {
                msg = "A generic error occurred";
            } else if (error.getPrimaryError() == SslError.SSL_EXPIRED) {
                msg = "The certificate has expired";
            } else if (error.getPrimaryError() == SslError.SSL_IDMISMATCH) {
                msg = "Hostname mismatch";
            } else if (error.getPrimaryError() == SslError.SSL_NOTYETVALID) {
                msg = "The certificate is not yet valid";
            } else if (error.getPrimaryError() == SslError.SSL_UNTRUSTED) {
                msg = "The certificate authority is not trusted";
            }
        }
        final AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setMessage(msg);
        builder.setPositiveButton("continue", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                handler.proceed();
            }
        });
        builder.setNegativeButton("cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                handler.cancel();
            }
        });
        final AlertDialog dialog = builder.create();
        dialog.show();
    }

}