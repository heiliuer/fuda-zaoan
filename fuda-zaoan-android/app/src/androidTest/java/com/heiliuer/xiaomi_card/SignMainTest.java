package com.heiliuer.xiaomi_card;

import android.support.test.runner.AndroidJUnit4;
import android.test.AndroidTestCase;
import android.test.suitebuilder.annotation.SmallTest;

import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * Created by Administrator on 2016/12/11 0011.
 */
@RunWith(AndroidJUnit4.class)
@SmallTest
public class SignMainTest extends AndroidTestCase {

    @Test
    public void testMain() throws Exception {
        SignMain.main(null);
    }

}