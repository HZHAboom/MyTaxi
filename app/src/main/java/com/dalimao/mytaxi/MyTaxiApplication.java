package com.dalimao.mytaxi;

import android.app.Application;

/**
 * Created by Administrator on 2018/4/13 0013.
 */

public class MyTaxiApplication extends Application {
    private static MyTaxiApplication instance;
    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
    }
    public static MyTaxiApplication getInstance(){
        return instance;
    }
}
