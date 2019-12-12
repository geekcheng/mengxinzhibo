package com.tongchuang.phonelive.http;

/**
 * Created by cxf on 2017/8/5.
 */

public class Data {
    private int code;
    private String msg;
    private String[] info;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public String[] getInfo() {
        return info;
    }

    public void setInfo(String[] info) {
        this.info = info;
    }
}
