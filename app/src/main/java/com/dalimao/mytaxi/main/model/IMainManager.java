package com.dalimao.mytaxi.main.model;

import android.os.Handler;

import com.dalimao.mytaxi.common.lbs.LocationInfo;

/**
 * Created by Administrator on 2018/4/14 0014.
 */

public interface IMainManager {

    //登录成功
    static final int LOGIN_SUC = 5;
    //登录失败
    static final int TOKEN_INVALID = -6;
    //服务器错误
    static final int SERVER_FAIL = -999;

    void setHandler(Handler handler);

    void loginByToken();

    void fetchNearDrivers(double latitude,double longitude);

    void updateLocationToServer(LocationInfo locationInfo);

    //呼叫司机
    void callDriver(String key, float cost, LocationInfo startLocation, LocationInfo endLocation);

    /**
     * 取消订单
     * @param orderId
     */
    void cancelOrder(String orderId);

    boolean isLogin();

    /**
     * 支付
     * @param orderId
     */
    void pay(String orderId);

    /**
     * 获取正在进行中的订单
     */
    void getProcessingOrder();
}
