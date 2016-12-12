package com.heiliuer.xiaomi_card;

/**
 * Created by Administrator on 2016/12/11 0011.
 */

public class Result {
    //{"Success":true,"Msg":"\u9000\u51FA\u6210\u529F\uFF01"}


    public static final Result getErrorResult(String msg) {
        return new Result(false, msg);
    }

    public static final Result getSuccessResult(String msg) {
        return new Result(true, msg);
    }

    public Result() {
    }

    public Result(boolean success, String msg) {
        Success = success;
        Msg = msg;
    }

    public boolean Success;
    public String Msg;

    public String getMsg() {
        return Msg;
    }

    public void setMsg(String msg) {
        Msg = msg;
    }

    public boolean isSuccess() {
        return Success;
    }

    public void setSuccess(boolean success) {
        Success = success;
    }
}
