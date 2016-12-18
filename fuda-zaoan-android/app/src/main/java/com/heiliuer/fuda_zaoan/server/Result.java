package com.heiliuer.fuda_zaoan.server;

/**
 * Created by Administrator on 2016/12/11 0011.
 */

public class Result {
    //{"Success":true,"msg":"\u9000\u51FA\u6210\u529F\uFF01"}


    public static final Result getErrorResult(String msg) {
        return new Result(false, msg);
    }

    public static final Result getSuccessResult(String msg) {
        return new Result(true, msg);
    }

    Object _extra;

    public Object get_extra() {
        return _extra;
    }

    public Result set_extra(Object _extra) {
        this._extra = _extra;
        return this;
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

    public Result setSuccess(boolean success) {
        Success = success;
        return this;
    }
}
