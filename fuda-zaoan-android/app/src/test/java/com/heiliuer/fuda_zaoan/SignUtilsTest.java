package com.heiliuer.fuda_zaoan;

import junit.framework.Assert;

import org.junit.Test;

/**
 * Created by Administrator on 2016/12/18 0018.
 */
public class SignUtilsTest {
    @Test
    public void getDateStr() throws Exception {

        Assert.assertEquals(SignUtils.getDateStr(), "2016-12-18");
    }

    @Test
    public void getDateStr1() throws Exception {
//        Date date = new Date();
//        date.setYear(2016);
//        date.setMonth(7);
//        date.setDate(2);
//        Assert.assertEquals(SignUtils.getDateStr(date), "2016-07-02");
    }

    @Test
    public void getDateStr2() throws Exception {

    }

}