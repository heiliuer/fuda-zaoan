package com.heiliuer.fuda_zaoan.ui;

import android.support.test.runner.AndroidJUnit4;
import android.test.suitebuilder.annotation.SmallTest;

import com.heiliuer.fuda_zaoan.api.ApiHandlerManager;
import com.heiliuer.fuda_zaoan.api.ApiHandlerWrapper;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.List;

/**
 * Created by Administrator on 2016/12/18 0018.
 */
@RunWith(AndroidJUnit4.class)
@SmallTest
public class ApiHandlerManagerTest {
    private ApiHandlerManager apiHandlerManager;

    @Before
    public void initManager() throws Exception {

        apiHandlerManager = new ApiHandlerManager();
    }

    @Test
    public void getApiHandlerWrappers() throws Exception {

        List<ApiHandlerWrapper> apiHandlerWrappers = apiHandlerManager.getApiHandlerWrappers();

        Assert.assertEquals(apiHandlerWrappers.size(), 1);
    }


    @Test
    public void getHandlerBasePackage() throws Exception {

        String handlerBasePackage = apiHandlerManager.getHandlerBasePackage();
        Assert.assertTrue(handlerBasePackage.equals("com.heiliuer.fuda_zaoan.api.handler"));
    }

}