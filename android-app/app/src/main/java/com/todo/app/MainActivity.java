package com.todo.app;

import android.app.Activity;
import android.os.Bundle;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.webkit.WebSettings;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebResourceError;
import android.graphics.Bitmap;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.os.Build;
import android.content.res.AssetManager;
import java.io.InputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends Activity {

    private WebView webView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // 设置状态栏颜色
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(android.graphics.Color.parseColor("#663399"));
            window.setNavigationBarColor(android.graphics.Color.parseColor("#764ba2"));
        }

        setContentView(R.layout.activity_main);

        webView = findViewById(R.id.webview);
        setupWebView();

        // 从 assets 加载本地 HTML
        webView.loadUrl("file:///android_asset/todo.html");
    }

    private void setupWebView() {
        WebSettings settings = webView.getSettings();
        settings.setJavaScriptEnabled(true);
        settings.setDomStorageEnabled(true);
        settings.setAllowFileAccess(true);
        settings.setAllowContentAccess(true);
        settings.setCacheMode(WebSettings.LOAD_DEFAULT);
        settings.setUseWideViewPort(true);
        settings.setLoadWithOverviewMode(true);
        settings.setSupportZoom(false);
        settings.setBuiltInZoomControls(false);
        settings.setDisplayZoomControls(false);
        settings.setDatabaseEnabled(true);

        webView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                // 显示加载状态
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                // 加载完成
            }

            @Override
            public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
                // 加载本地错误页
                view.loadUrl("file:///android_asset/todo.html");
            }

            @Override
            public WebResourceResponse shouldInterceptRequest(WebView view, WebResourceRequest request) {
                String url = request.getUrl().toString();
                // 拦截本地资源请求
                if (url.startsWith("file:///android_asset/")) {
                    return null; // 让 WebView 自己处理
                }
                // 所有其他请求都从 assets 加载
                try {
                    String path = request.getUrl().getPath();
                    if (path.startsWith("/")) path = path.substring(1);
                    if (path.isEmpty()) path = "todo.html";

                    AssetManager am = getAssets();
                    String[] files = am.list("");
                    for (String f : files) {
                        if (path.endsWith(f)) {
                            InputStream is = am.open(f);
                            String mime = getMimeType(f);
                            return new WebResourceResponse(mime, "UTF-8", is);
                        }
                    }
                } catch (IOException e) {
                    // ignore
                }
                return null;
            }
        });

        webView.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (event.getAction() == KeyEvent.ACTION_DOWN) {
                    if (keyCode == KeyEvent.KEYCODE_BACK && webView.canGoBack()) {
                        webView.goBack();
                        return true;
                    }
                }
                return false;
            }
        });
    }

    private String getMimeType(String fileName) {
        if (fileName.endsWith(".html")) return "text/html";
        if (fileName.endsWith(".js")) return "application/javascript";
        if (fileName.endsWith(".css")) return "text/css";
        if (fileName.endsWith(".json")) return "application/json";
        if (fileName.endsWith(".png")) return "image/png";
        if (fileName.endsWith(".svg")) return "image/svg+xml";
        if (fileName.endsWith(".ico")) return "image/x-icon";
        return "text/plain";
    }

    @Override
    public void onBackPressed() {
        if (webView.canGoBack()) {
            webView.goBack();
        } else {
            super.onBackPressed();
        }
    }
}
