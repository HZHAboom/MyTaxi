package com.dalimao.mytaxi.main.presenter;

import com.dalimao.mytaxi.account.model.response.LoginResponse;
import com.dalimao.mytaxi.common.databus.RegisterBus;
import com.dalimao.mytaxi.common.http.biz.BaseBizResponse;
import com.dalimao.mytaxi.common.lbs.LocationInfo;
import com.dalimao.mytaxi.main.model.IMainManager;
import com.dalimao.mytaxi.main.model.bean.Order;
import com.dalimao.mytaxi.main.model.response.NearDriversResponse;
import com.dalimao.mytaxi.main.model.response.OrderStateOptResponse;
import com.dalimao.mytaxi.main.view.IMainView;

/**
 * Created by Administrator on 2018/4/14 0014.
 */

public class MainPresenterImpl implements IMainPresenter {

    private IMainView mView;
    private IMainManager mMainManager;
    //  当前的订单
    private Order mCurrentOrder;
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

    /**
     * 取消呼叫
     */
    @Override
    public void cancel() {
        if (mCurrentOrder != null){
            mMainManager.cancelOrder(mCurrentOrder.getOrderId());
        }else{
            mView.showCancelSuc();
        }
    }

    @Override
    public boolean isLogin() {
        return mMainManager.isLogin();
    }

    @Override
    public void pay() {
        if (mCurrentOrder!=null){
            mMainManager.pay(mCurrentOrder.getOrderId());
        }
    }

    /**
     * 获取正在进行中的订单
     */
    @Override
    public void getProcessingOrder() {
        mMainManager.getProcessingOrder();
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
        if (mCurrentOrder!=null &&
                (mCurrentOrder.getState() == OrderStateOptResponse.ORDER_STATE_ACCEPT)){
            //更新司机到上车点的路径信息
            mView.updateDriver2StartRoute(locationInfo,mCurrentOrder);
        }else if (mCurrentOrder!=null &&
                mCurrentOrder.getState() == OrderStateOptResponse.ORDER_STATE_START_DRIVE){
            //更新司机到终点的路径信息
            mView.updateDriver2EndRoute(locationInfo,mCurrentOrder);
        }else{
            mView.showLocationChange(locationInfo);
        }
    }

    //订单状态响应
    @RegisterBus
    public void onOrderOptResponse(OrderStateOptResponse response){
        if (response.getState() == OrderStateOptResponse.ORDER_STATE_CREATE){
            //呼叫司机
            if (response.getCode() == BaseBizResponse.STATE_OK){
                //  保存当前的订单
                mCurrentOrder = response.getData();
                mView.showCallDriverSuc(mCurrentOrder);
            }else{
                mView.showCallDriverFail();
            }
        }else if (response.getState() == OrderStateOptResponse.ORDER_STATE_CANCEL){
            //取消订单
            if (response.getCode() == BaseBizResponse.STATE_OK){
                mView.showCancelSuc();
            }else{
                mView.showCancelFail();
            }
        }else if (response.getState() == OrderStateOptResponse.ORDER_STATE_ACCEPT){
            //司机接单
            mCurrentOrder = response.getData();
            mView.showDriverAcceptOrder(mCurrentOrder);
        }else if (response.getState() == OrderStateOptResponse.ORDER_STATE_ARRIVE_START){
            //司机到达上车点
            mCurrentOrder = response.getData();
            mView.showDriverArriveStart(mCurrentOrder);
        }else if (response.getState() == OrderStateOptResponse.ORDER_STATE_START_DRIVE){
            //开始行程
            mCurrentOrder = response.getData();
            mView.showStartDrive(mCurrentOrder);
        }else if (response.getState() == OrderStateOptResponse.ORDER_STATE_ARRIVE_END){
            //到达终点
            mCurrentOrder = response.getData();
            mView.showArriveEnd(mCurrentOrder);
        }else if (response.getState() == OrderStateOptResponse.PAY){
            //支付
            if (response.getCode() == BaseBizResponse.STATE_OK){
                mView.showPaySuc(mCurrentOrder);
            }else{
                mView.showPayFail();
            }
        }
    }
}
