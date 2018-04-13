package com.dalimao.mytaxi.common.http;

/**
 * Created by Administrator on 2018/3/13 0013.
 */

public interface IHttpClient {
    IResponse get(IRequest request, boolean forceCache);
    IResponse post(IRequest request, boolean forceCache);
}
