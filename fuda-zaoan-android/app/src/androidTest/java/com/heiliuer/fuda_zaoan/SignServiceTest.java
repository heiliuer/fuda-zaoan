package com.heiliuer.fuda_zaoan;

import android.support.test.runner.AndroidJUnit4;
import android.test.AndroidTestCase;
import android.test.suitebuilder.annotation.SmallTest;

import com.heiliuer.fuda_zaoan.server.request.SignService;

import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * Created by Administrator on 2016/12/11 0011.
 */
@RunWith(AndroidJUnit4.class)
@SmallTest
public class SignServiceTest extends AndroidTestCase {

    @Test
    public void testMain() throws Exception {
        SignService.directSignForResult("161127054", "221511");
    }

}