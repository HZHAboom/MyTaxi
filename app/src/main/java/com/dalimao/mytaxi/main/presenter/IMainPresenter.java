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
}
