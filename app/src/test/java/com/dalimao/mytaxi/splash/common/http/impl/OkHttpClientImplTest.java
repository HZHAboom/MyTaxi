package com.dalimao.mytaxi.splash.common.http.impl;

import com.dalimao.mytaxi.common.http.IHttpClient;
import com.dalimao.mytaxi.common.http.IRequest;
import com.dalimao.mytaxi.common.http.IResponse;
import com.dalimao.mytaxi.common.http.api.API;
import com.dalimao.mytaxi.common.http.impl.BaseRequest;
import com.dalimao.mytaxi.common.http.impl.OkHttpClientImpl;

import org.junit.Before;
import org.junit.Test;

/**
 * Created by Administrator on 2018/3/14 0014.
 */
public class OkHttpClientImplTest {
    IHttpClient httpClient;
    @Before
    public void setUp() throws Exception {
        httpClient = new OkHttpClientImpl();
        API.Config.setDebug(false);
    }

    @Test
    public void get() throws Exception {
        // request 参数
        String url = API.Config.getDomain() + API.TEST_GET;
        IRequest  request = new BaseRequest(url);
        request.setBody("uid","12345");
        request.setBody("name","zhangsan");
        request.setHeader("testHeader","test header");
        IResponse response = httpClient.get(request, false);
        System.out.println("stateCode = " + response.getCode());
        System.out.println("stateData = " + response.getData());
    }

    @Test
    public void post() throws Exception {
        String url = API.Config.getDomain() + API.TEST_POST;
        IRequest  request = new BaseRequest(url);
        request.setBody("uid","12345");
        request.setHeader("testHeader","test header");
        IResponse response = httpClient.post(request, false);
        System.out.println("stateCode = " + response.getCode());
        System.out.println("stateData = " + response.getData());
    }

}