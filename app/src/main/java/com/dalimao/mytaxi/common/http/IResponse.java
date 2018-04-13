package com.dalimao.mytaxi.common.http;

/**
 * Created by Administrator on 2018/3/13 0013.
 */

public interface IResponse {
    //状态码
    int getCode();
    //数据体
    String getData();
}
