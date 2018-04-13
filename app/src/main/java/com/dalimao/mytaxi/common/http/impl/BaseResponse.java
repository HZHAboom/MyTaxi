package com.dalimao.mytaxi.common.http.impl;

import com.dalimao.mytaxi.common.http.IResponse;

/**
 * Created by Administrator on 2018/3/13 0013.
 */
public class BaseResponse implements IResponse{
    public static final int STATE_UNKNOWN_ERROR = 10001;
    //状态码
    private int code;
    //响应数据
    private String data;
    @Override
    public int getCode() {
        return code;
    }

    @Override
    public String getData() {
        return data;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public void setData(String data) {
        this.data = data;
    }
}
