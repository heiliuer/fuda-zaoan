package com.heiliuer.fuda_zaoan.api.handler;

/**
 * Created by Administrator on 2016/12/11 0011.
 */

public class HandlerResult {


    public int status;

    public int getStatus() {
        return status;
    }

    public HandlerResult setStatus(int status) {
        this.status = status;
        return this;
    }

    public Object getData() {
        return data;
    }

    public HandlerResult setData(Object data) {
        this.data = data;
        return this;
    }

    public String getMsg() {
        return msg;
    }

    public HandlerResult setMsg(String msg) {
        this.msg = msg;
        return this;
    }

    public String msg;

    public Object data;


}
