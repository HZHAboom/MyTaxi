package com.dalimao.mytaxi;

import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import okhttp3.Cache;
import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by Administrator on 2018/3/13 0013.
 */

public class TestOkhttp3 {
    /**
     * 测试 OKHttp Get 方法
     */
    @Test
    public void testGet(){
        //创建 OkHttpClient对象
        OkHttpClient client = new OkHttpClient();
        //创建 Request对象
        Request request = new Request.Builder()
                .url("http://httpbin.org/get?id=id")
                .build();
        //OkHttpClient 执行 Request
        try {
            Response response = client.newCall(request).execute();
            System.out.println("response:" + response.body().string());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 测试 OKHttp Post 方法
     */
    @Test
    public void testPost(){
        //创建 OkHttpClient对象
        OkHttpClient client = new OkHttpClient();
        //创建 Request对象
        RequestBody body = RequestBody.create(MediaType.parse("application/json;charset=utf-8"),"{\"name\":\"dalimao\"}");
        Request request = new Request.Builder()
                .url("http://httpbin.org/post")
                .post(body)
                .build();
        //OkHttpClient 执行 Request
        try {
            Response response = client.newCall(request).execute();
            System.out.println("response:" + response.body().string());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 测试拦截器
     */
    @Test
    public void testInterceptor(){
        //定义拦截器
        Interceptor interceptor = new Interceptor() {
            @Override
            public Response intercept(Chain chain) throws IOException {

                long start = System.currentTimeMillis();
                Request request = chain.request();
                Response response = chain.proceed(request);
                long end = System.currentTimeMillis();
                System.out.println("interceptor: cost time = "+ (end - start));
                return response;
            }
        };
        //创建 OkHttpClient对象
        OkHttpClient client = new OkHttpClient.Builder()
                .addInterceptor(interceptor)
                .build();
        //创建 Request对象
        Request request = new Request.Builder()
                .url("http://httpbin.org/get?id=id")
                .build();
        //OkHttpClient 执行 Request
        try {
            Response response = client.newCall(request).execute();
            System.out.println("response:" + response.body().string());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 测试缓存
     */
    @Test
    public void testCache(){
        //创建缓存对象
        Cache cache = new Cache(new File("cache.cache"),1024 * 1024);


        //创建 OkHttpClient对象
        OkHttpClient client = new OkHttpClient.Builder()
                .cache(cache)
                .build();
        //创建 Request对象
        Request request = new Request.Builder()
                .url("http://httpbin.org/get?id=id")
//                .cacheControl(CacheControl.FORCE_NETWORK)
                .build();
        //OkHttpClient 执行 Request
        try {
            Response response = client.newCall(request).execute();
            Response responseCache = response.cacheResponse();
            Response responseNet = response.networkResponse();
            if (responseCache != null){
                //从缓存响应
                System.out.println("response from cache");
            }
            if (responseNet != null){
                System.out.println("response from net");
            }
            System.out.println("response:" + response.body().string());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testMapPut(){
        Map<String, String> map = new HashMap<>();
        map.put("uid","123456");
        map.put("name","zhangsan");
        for (String key : map.keySet()){
            System.out.println("key = "+ key + ",value = " + map.get(key));
        }
    }
}
