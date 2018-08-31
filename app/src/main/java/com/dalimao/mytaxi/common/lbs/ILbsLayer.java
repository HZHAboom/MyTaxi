package com.dalimao.mytaxi.common.lbs;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;

import java.util.List;

/**
 * Created by Administrator on 2018/4/26 0026.
 */

public interface ILbsLayer {

    /**
     * 获取地图
     */
    View getMapView();

    /**
     * 设置位置变化监听
     */
    void setLocationChangeListener(CommonLocationChangeListener locationChangeListener);

    /**
     * 设置定位图标
     */
    void setLocationRes(int res);
    /**
     * 添加，更新标记点，包括位置、角度（通过id识别）
     */
    void addOrUpdateMarker(LocationInfo locationInfo, Bitmap bitmap);

    /**
     * 生命周期函数
     */
    void onCreate(Bundle state);
    void onResume();
    void onSaveInstanceState(Bundle outState);
    void onPause();
    void onDestroy();

    /**
     * 获取当前城市
     * @return
     */
    String getCity();

    /**
     * 联动搜索附近的位置
     * @param key
     * @param listener
     */
    void poiSearch(String key,OnSearchedListener listener);

    void clearAllMarkers();

    /**
     * 绘制两点之间行车路径
     * @param start
     * @param end
     * @param color
     * @param listener
     */
    void driverRoute(LocationInfo start,
                     LocationInfo end,
                     int color,
                     OnRouteCompleteListener listener);

    /**
     * 移动相机到两点之间的视野范围
     * @param locationInfo1
     * @param locationInfo2
     */
    void moveCamera(LocationInfo locationInfo1, LocationInfo locationInfo2);

    interface CommonLocationChangeListener{
        void onLocationChanged(LocationInfo locationInfo);
        void onLocation(LocationInfo locationInfo);
    }

    /**
     * POI 搜索结果监听器
     */
    interface OnSearchedListener{
        void onSearched(List<LocationInfo> results);
        void onError(int rCode);
    }

    /**
     * 路径规划完成监听
     */
    interface OnRouteCompleteListener{
        void onComplete(RouteInfo result);
    }
}
