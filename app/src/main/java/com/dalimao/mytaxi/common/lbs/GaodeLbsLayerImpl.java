package com.dalimao.mytaxi.common.lbs;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.maps2d.AMap;
import com.amap.api.maps2d.CameraUpdate;
import com.amap.api.maps2d.CameraUpdateFactory;
import com.amap.api.maps2d.LocationSource;
import com.amap.api.maps2d.MapView;
import com.amap.api.maps2d.model.BitmapDescriptor;
import com.amap.api.maps2d.model.BitmapDescriptorFactory;
import com.amap.api.maps2d.model.CameraPosition;
import com.amap.api.maps2d.model.LatLng;
import com.amap.api.maps2d.model.Marker;
import com.amap.api.maps2d.model.MarkerOptions;
import com.amap.api.maps2d.model.MyLocationStyle;
import com.dalimao.mytaxi.common.util.SensorEventHelper;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Administrator on 2018/4/26 0026.
 */

public class GaodeLbsLayerImpl implements ILbsLayer{
    private static final String TAG = "GaodeLbsLayerImpl";
    private static final String KEY_MY_MARKER = "1000";
    //地图视图对象
    private MapView mapView;
    //地图管理对象
    private AMap aMap;
    //地图位置变化回调对象
    private LocationSource.OnLocationChangedListener mMapLocationChangedListener;
    //位置定位对象
    private AMapLocationClient mlocationClient;
    private AMapLocationClientOption mLocationOption;
    private SensorEventHelper mSensorHelper;
    private CommonLocationChangeListener mLocationChangeListener;
    private boolean mFirstLocation = true;
    private Context mContext;
    private MyLocationStyle mMyLocationStyle;
    //管理地图标记集合
    private Map<String,Marker> mMarkerMap = new HashMap<>();

    public GaodeLbsLayerImpl(Context context) {
        mContext = context;
        mapView = new MapView(context);
        //  获取地图管理器
        aMap = mapView.getMap();
        //  创建定位对象
        mlocationClient = new AMapLocationClient(mContext);
        mLocationOption = new AMapLocationClientOption();
        //设置为高精度定位模式
        mLocationOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);
        //设置定位参数
        mlocationClient.setLocationOption(mLocationOption);
        //传感器对象
        mSensorHelper = new SensorEventHelper(context);
        mSensorHelper.registerSensorListener();
    }

    /**
     * 设置一些amap的属性
     */
    private void setUpMap() {
        if (mMyLocationStyle!=null){
            aMap.setMyLocationStyle(mMyLocationStyle);
        }
        // 设置地图激活（加载监听）
        aMap.setLocationSource(new LocationSource() {
            @Override
            public void activate(OnLocationChangedListener onLocationChangedListener) {
                mMapLocationChangedListener = onLocationChangedListener;
                Log.d(TAG,"activate");
            }

            @Override
            public void deactivate() {
                if (mlocationClient != null) {
                    mlocationClient.stopLocation();
                    mlocationClient.onDestroy();
                }
                mlocationClient = null;
            }
        });
        aMap.getUiSettings().setMyLocationButtonEnabled(true);// 设置默认定位按钮是否显示
        aMap.setMyLocationEnabled(true);// 设置为true表示显示定位层并可触发定位，false表示隐藏定位层并不可触发定位，默认是false
        // aMap.setMyLocationType()
    }


    @Override
    public View getMapView() {
        return mapView;
    }

    @Override
    public void setLocationChangeListener(CommonLocationChangeListener locationChangeListener) {
        mLocationChangeListener = locationChangeListener;
    }

    @Override
    public void setLocationRes(int res) {
        mMyLocationStyle = new MyLocationStyle();
        mMyLocationStyle.myLocationIcon(BitmapDescriptorFactory
                .fromResource(res));// 设置小蓝点的图标
        mMyLocationStyle.strokeColor(Color.BLACK);// 设置圆形的边框颜色
        mMyLocationStyle.radiusFillColor(Color.argb(100, 0, 0, 180));// 设置圆形的填充颜色
        // myLocationStyle.anchor(int,int)//设置小蓝点的锚点
        mMyLocationStyle.strokeWidth(1.0f);// 设置圆形的边框粗细

    }

    @Override
    public void addOrUpdateMarker(LocationInfo locationInfo, Bitmap bitmap) {
        Marker storedMarker = mMarkerMap.get(locationInfo.getKey());
        LatLng latLng = new LatLng(locationInfo.getLatitude(),locationInfo.getLongitude());
        if (storedMarker != null) {
            storedMarker.setPosition(latLng);
            storedMarker.setRotateAngle(locationInfo.getRotation());
        }else{
            MarkerOptions options = new MarkerOptions();
            BitmapDescriptor des = BitmapDescriptorFactory.fromBitmap(bitmap);
            options.icon(des);
            options.anchor(0.5f, 0.5f);
            options.position(latLng);
            Marker marker = aMap.addMarker(options);
            marker.setRotateAngle(android.R.attr.rotation);
            mMarkerMap.put(locationInfo.getKey(),marker);
            if (KEY_MY_MARKER.equals(locationInfo.getKey())){
                //传感器控制我的位置标记的旋转角度
                mSensorHelper.setCurrentMarker(marker);
            }
        }
    }

    @Override
    public void onCreate(Bundle state) {
        mapView.onCreate(state);
        setUpMap();
    }

    @Override
    public void onResume() {
        mapView.onResume();
        setUpLocation();
    }

    private void setUpLocation() {
        mlocationClient.setLocationListener(new AMapLocationListener() {
            @Override
            public void onLocationChanged(AMapLocation aMapLocation) {
                //定位变化位置
                if (mMapLocationChangedListener!=null){
                    //地图已经激活，通知蓝点实时更新
                    mMapLocationChangedListener.onLocationChanged(aMapLocation);
                    Log.d(TAG, "onLocationChanged");
                    LocationInfo locationInfo = new LocationInfo(aMapLocation.getLatitude(),aMapLocation.getLongitude());
                    locationInfo.setName(aMapLocation.getPoiName());
                    locationInfo.setKey(KEY_MY_MARKER);
                    if (mFirstLocation){
                        mFirstLocation = false;
                        LatLng latLng = new LatLng(aMapLocation.getLatitude(),aMapLocation.getLongitude());
                        CameraUpdate  up = CameraUpdateFactory.newCameraPosition(new CameraPosition(latLng,18,30,30));
                        aMap.moveCamera(up);
                        if (mLocationChangeListener!=null){
                            mLocationChangeListener.onLocation(locationInfo);
                        }
                    }
                    if (mLocationChangeListener!=null){
                        mLocationChangeListener.onLocationChanged(locationInfo);
                    }
                }
            }
        });
        mlocationClient.startLocation();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        mapView.onSaveInstanceState(outState);
    }

    @Override
    public void onPause() {
        mapView.onPause();
        mlocationClient.stopLocation();
    }

    @Override
    public void onDestroy() {
        mapView.onDestroy();
        mlocationClient.onDestroy();
    }
}
