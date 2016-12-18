package com.heiliuer.fuda_zaoan.api;

import android.util.Log;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;

import java.util.List;

/**
 * Created by Administrator on 2016/12/18 0018.
 */

public class ApiDispatcherImpl implements ApiDispatcher {

    private List<ApiHandlerWrapper> apiHandlerWrappers;

    private static final String TAG = ApiDispatcherImpl.class.getName();

    @Override

    public WebResourceResponse handler(WebResourceRequest request) {
        for (ApiHandlerWrapper apiHandlerWrapper : apiHandlerWrappers) {
            if (apiHandlerWrapper.isAccept(request)) {
                return apiHandlerWrapper.handle(request);
            }
        }
        Log.i(TAG, String.format("handler: can not find handler for request[uri:%s]", request.getUrl().toString()));
        return null;
    }

    public ApiDispatcherImpl(List<ApiHandlerWrapper> apiHandlerWrappers) {
        this.apiHandlerWrappers = apiHandlerWrappers;
    }
}
