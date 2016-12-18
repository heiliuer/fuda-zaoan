package com.heiliuer.fuda_zaoan.ui;

import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import com.heiliuer.fuda_zaoan.R;
import com.heiliuer.fuda_zaoan.Utils;
import com.heiliuer.fuda_zaoan.api.ApiDispatcher;
import com.heiliuer.fuda_zaoan.api.ApiDispatcherImpl;
import com.heiliuer.fuda_zaoan.api.ApiHandlerManager;
import com.heiliuer.fuda_zaoan.service.ServiceMain;

import java.io.IOException;


public class ActWebMain extends AppCompatActivity {

    private WebView webviewMain;

    private ApiDispatcher apiDispatcher;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_web_main);
        webviewMain = (WebView) findViewById(R.id.webview_main);

        initApis();

        initWebViewSettings();


        ServiceMain.startTheServiceIfSwitchOn(this);

        Utils.setStatusBarDarkMode(true, this);
    }

    private void initWebViewSettings() {

        WebSettings webSettings = webviewMain.getSettings();
        webSettings.setJavaScriptEnabled(true);

        webviewMain.getSettings().setDomStorageEnabled(true);
        webviewMain.getSettings().setDatabaseEnabled(true);
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) {
            webviewMain.getSettings().setDatabasePath("/data/data/" + webviewMain.getContext().getPackageName() + "/databases/");
        }


        WebViewClient webClient = new WebViewClient() {
            @Override
            public WebResourceResponse shouldInterceptRequest(WebView view, final WebResourceRequest request) {
                String target = request.getUrl().toString();
                Log.v("data", "get url:" + target);
                if (target.startsWith("http://api")) {
                    WebResourceResponse response = apiDispatcher.handler(request);
                    if (response != null) {
                        return response;
                    }
                } else {
                    if (target.startsWith("file:///static")) {
                        target = target.replace("file:///", "");
                        try {
                            return new WebResourceResponse("text/*", "utf-8", getResources().getAssets().open(target));
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }

                return super.shouldInterceptRequest(view, request);
            }


            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                //防止跳出
//                final String url = request.getUrl().toString();
//                if (!url.startsWith("file://")) {
//                    webviewMain.post(new Runnable() {
//                        @Override
//                        public void run() {
//                            startUri(url);
//                        }
//                    });
//                    return true;
//                }
                return super.shouldOverrideUrlLoading(view, url);
            }

        };
        webviewMain.setWebViewClient(webClient);

//        webviewMain.setWebChromeClient(new WebChromeClient() {
//        });

        webviewMain.loadUrl("file:///android_asset/index.html");
    }


    private void initApis() {
        apiDispatcher = new ApiDispatcherImpl(new ApiHandlerManager().getApiHandlerWrappers());
    }

    private long timeLastBackPressedToExit;

    @Override
    public void onBackPressed() {
        if (webviewMain.canGoBack()) {
            webviewMain.goBack();
        } else {
            long cu = System.currentTimeMillis();
            if (cu - timeLastBackPressedToExit <= 1000) {
                super.onBackPressed();
            } else {
                timeLastBackPressedToExit = cu;
                Toast.makeText(this, "再次点击返回退出", Toast.LENGTH_SHORT).show();
            }
        }
    }

}
