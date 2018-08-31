package com.dalimao.mytaxi.main.presenter;

import com.dalimao.mytaxi.account.model.response.LoginResponse;
import com.dalimao.mytaxi.common.databus.RegisterBus;
import com.dalimao.mytaxi.common.http.biz.BaseBizResponse;
import com.dalimao.mytaxi.common.lbs.LocationInfo;
import com.dalimao.mytaxi.main.model.IMainManager;
import com.dalimao.mytaxi.main.model.response.NearDriversResponse;
import com.dalimao.mytaxi.main.model.response.OrderStateOptResponse;
import com.dalimao.mytaxi.main.view.IMainView;

/**
 * Created by Administrator on 2018/4/14 0014.
 */

public class MainPresenterImpl implements IMainPresenter {

    private IMainView mView;
    private IMainManager mMainManager;

//    static class MyHandler extends Handler{
//        private WeakReference<MainPresenterImpl> refContext;
//        public MyHandler(MainPresenterImpl context){
//            refContext = new WeakReference<MainPresenterImpl>(context);
//        }
//
//        @Override
//        public void handleMessage(Message msg) {
//            super.handleMessage(msg);
//            MainPresenterImpl presenter = refContext.get();
//            if (presenter == null){
//                return;
//            }
//            switch (msg.what){
//
//            }
//        }
//    }

    public MainPresenterImpl(IMainView view, IMainManager mainManager) {
        mView = view;
        mMainManager = mainManager;
//        mMainManager.setHandler(new MyHandler(this));
    }

    @Override
    public void loginByToken() {
        mMainManager.loginByToken();
    }

    @Override
    public void fetchNearDrivers(double latitude, double longitude) {
        mMainManager.fetchNearDrivers(latitude,longitude);
    }

    @Override
    public void updateLocationToServer(LocationInfo locationInfo) {
        mMainManager.updateLocationToServer(locationInfo);
    }

    @Override
    public void callDriver(String key, float cost, LocationInfo startLocation, LocationInfo endLocation) {
        mMainManager.callDriver(key, cost, startLocation, endLocation);
    }

    @RegisterBus
    public void loginByTokenResponse(LoginResponse response){
        switch (response.getCode()){
            case IMainManager.LOGIN_SUC:
                mView.showLoginSuc();
                break;
            case IMainManager.TOKEN_INVALID:
                mView.showError(IMainManager.TOKEN_INVALID,"");
                break;
            case IMainManager.SERVER_FAIL:
                mView.showError(IMainManager.SERVER_FAIL,"");
                break;
        }
    }

    @RegisterBus
    public void onNearDriversResponse(NearDriversResponse response){
        if (response.getCode() == BaseBizResponse.STATE_OK){
            mView.showNears(response.getData());
        }
    }

    @RegisterBus
    public void onLocationInfo(LocationInfo locationInfo){
        mView.showLocationChange(locationInfo);
    }

    //订单状态响应
    @RegisterBus
    public void onOrderOptResponse(OrderStateOptResponse response){
        if (response.getState() == OrderStateOptResponse.ORDER_STATE_CREATE){
            //呼叫司机
            if (response.getCode() == BaseBizResponse.STATE_OK){
                mView.showCallDriverSuc();
            }else{
                mView.showCallDriverFail();
            }
        }
    }
}
