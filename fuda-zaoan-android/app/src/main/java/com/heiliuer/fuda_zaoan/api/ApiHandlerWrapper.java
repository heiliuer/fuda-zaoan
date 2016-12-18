package com.heiliuer.fuda_zaoan.api;

import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;

/**
 * Created by Administrator on 2016/12/18 0018.
 */
public interface ApiHandlerWrapper {

    boolean isAccept(WebResourceRequest request);

    WebResourceResponse handle(WebResourceRequest request);
}
