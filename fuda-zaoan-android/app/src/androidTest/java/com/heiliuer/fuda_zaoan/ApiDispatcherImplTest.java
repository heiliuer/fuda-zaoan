package com.heiliuer.fuda_zaoan;

import android.net.Uri;
import android.support.test.runner.AndroidJUnit4;
import android.test.suitebuilder.annotation.SmallTest;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;

import com.google.gson.Gson;
import com.heiliuer.fuda_zaoan.api.ApiDispatcherImpl;
import com.heiliuer.fuda_zaoan.api.handler.HandlerResult;
import com.heiliuer.fuda_zaoan.api.ApiHandlerManager;

import org.apache.commons.io.IOUtils;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Map;

/**
 * Created by Administrator on 2016/12/18 0018.
 */
@RunWith(AndroidJUnit4.class)
@SmallTest
public class ApiDispatcherImplTest {

    private ApiHandlerManager apiHandlerManager;
    private ApiDispatcherImpl apiDispatcher;

    @Before
    public void setUp() throws Exception {
        apiHandlerManager = new ApiHandlerManager();
        apiDispatcher = new ApiDispatcherImpl(new ApiHandlerManager().getApiHandlerWrappers());
    }

    @Test
    public void handler() throws Exception {
        WebResourceResponse response = apiDispatcher.handler(new WebResourceRequest() {
            @Override
            public Uri getUrl() {
                return Uri.parse("http://api/login");
            }

            @Override
            public boolean isForMainFrame() {
                return false;
            }

            @Override
            public boolean hasGesture() {
                return false;
            }

            @Override
            public String getMethod() {
                return null;
            }

            @Override
            public Map<String, String> getRequestHeaders() {
                return null;
            }
        });
        String result = IOUtils.toString(response.getData(), "utf-8");
        HandlerResult handlerResult = new Gson().fromJson(result, HandlerResult.class);
        Assert.assertEquals(handlerResult.getStatus(), 0);
        Assert.assertEquals(handlerResult.getMsg(), "success");

    }

}