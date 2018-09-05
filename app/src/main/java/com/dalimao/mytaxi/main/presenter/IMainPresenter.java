package com.dalimao.mytaxi.main.presenter;

import com.dalimao.mytaxi.common.lbs.LocationInfo;

/**
 * Created by Administrator on 2018/4/14 0014.
 */

public interface IMainPresenter {
    void loginByToken();

    /**
     * 获取附近司机
     * @param latitude
     * @param longitude
     */
    void fetchNearDrivers(double latitude, double longitude);

    /**
     * 上报当前位置
     * @param locationInfo
     */
    void updateLocationToServer(LocationInfo locationInfo);

    /**
     * 呼叫司机
     * @param key
     * @param cost
     * @param startLocation
     * @param endLocation
     */
    void callDriver(String key, float cost, LocationInfo startLocation, LocationInfo endLocation);

    /**
     * 取消呼叫
     */
    void cancel();

    boolean isLogin();

    /**
     * 支付
     */
    void pay();
}
