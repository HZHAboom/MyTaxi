package com.dalimao.mytaxi.main.view;

import com.dalimao.mytaxi.common.lbs.LocationInfo;

import java.util.List;

/**
 * Created by Administrator on 2018/4/14 0014.
 */

public interface IMainView{
    void showLoginSuc();

    void showError(int code,String msg);

    /**
     * 附近司机显示
     * @param data
     */
    void showNears(List<LocationInfo> data);

    /**
     * 显示位置变化
     * @param locationInfo
     */
    void showLocationChange(LocationInfo locationInfo);

    /**
     * 显示呼叫成功发出
     */
    void showCallDriverSuc();

    /**
     * 显示呼叫未成功发出
     */
    void showCallDriverFail();
}
