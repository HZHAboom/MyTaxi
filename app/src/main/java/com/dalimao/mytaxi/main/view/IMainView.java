package com.dalimao.mytaxi.main.view;

import com.dalimao.mytaxi.common.lbs.LocationInfo;
import com.dalimao.mytaxi.main.model.bean.Order;

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
     * @param currentOrder
     */
    void showCallDriverSuc(Order currentOrder);

    /**
     * 显示呼叫未成功发出
     */
    void showCallDriverFail();

    /**
     * 取消订单成功
     */
    void showCancelSuc();

    /**
     * 取消订单失败
     */
    void showCancelFail();

    /**
     * 显示司机接单
     * @param currentOrder
     */
    void showDriverAcceptOrder(Order currentOrder);

    /**
     * 司机到达上车地点
     * @param currentOrder
     */
    void showDriverArriveStart(Order currentOrder);

    /**
     * 更新司机到上车点的路径
     * @param currentOrder
     */
    void showStartDrive(Order currentOrder);

    /**
     * 显示到达终点
     * @param currentOrder
     */
    void showArriveEnd(Order currentOrder);

    /**
     * 更新司机到上车点的路径
     * @param locationInfo
     * @param order
     */
    void updateDriver2StartRoute(LocationInfo locationInfo, Order order);

    /**
     * 更新司机到终点的路径
     * @param locationInfo
     * @param order
     */
    void updateDriver2EndRoute(LocationInfo locationInfo, Order order);

    /**
     * 支付成功
     * @param currentOrder
     */
    void showPaySuc(Order currentOrder);

    /**
     * 显示支付失败
     */
    void showPayFail();
}
