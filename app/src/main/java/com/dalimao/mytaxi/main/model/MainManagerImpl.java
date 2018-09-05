package com.dalimao.mytaxi.main.model;

import android.os.Handler;
import android.support.annotation.NonNull;
import android.util.Log;

import com.dalimao.mytaxi.MyTaxiApplication;
import com.dalimao.mytaxi.account.model.response.Account;
import com.dalimao.mytaxi.account.model.response.LoginResponse;
import com.dalimao.mytaxi.common.databus.RxBus;
import com.dalimao.mytaxi.common.http.IHttpClient;
import com.dalimao.mytaxi.common.http.IRequest;
import com.dalimao.mytaxi.common.http.IResponse;
import com.dalimao.mytaxi.common.http.api.API;
import com.dalimao.mytaxi.common.http.biz.BaseBizResponse;
import com.dalimao.mytaxi.common.http.impl.BaseRequest;
import com.dalimao.mytaxi.common.lbs.LocationInfo;
import com.dalimao.mytaxi.common.storage.SharedPreferencesDao;
import com.dalimao.mytaxi.common.util.LogUtil;
import com.dalimao.mytaxi.main.model.response.NearDriversResponse;
import com.dalimao.mytaxi.main.model.response.OrderStateOptResponse;
import com.google.gson.Gson;

import io.reactivex.functions.Function;


/**
 * Created by Administrator on 2018/4/14 0014.
 */

public class MainManagerImpl implements IMainManager{

    private static final String TAG = "MainManagerImpl";

    private IHttpClient mHttpClient;
    private SharedPreferencesDao mSharedPreferencesDao;
    private Handler mHandler;

    public MainManagerImpl(IHttpClient httpClient, SharedPreferencesDao sharedPreferencesDao) {
        mHttpClient = httpClient;
        mSharedPreferencesDao = sharedPreferencesDao;
    }

    @Override
    public void setHandler(Handler handler) {
        mHandler = handler;
    }

    @Override
    public void loginByToken() {
        RxBus.getInstance().chainProcess(new Function() {
            @Override
            public Object apply(@NonNull Object o) throws Exception {
                LoginResponse bizRes = new LoginResponse();

                //  获取本地登录信息

                SharedPreferencesDao dao =
                        new SharedPreferencesDao(MyTaxiApplication.getInstance(),SharedPreferencesDao.FILE_ACCOUNT);
                Account account = (Account) dao.get(SharedPreferencesDao.KEY_ACCOUNT,Account.class);

                //  登录是否过期
                boolean tokenValid = false;

                //  检查token是否过期
                if (account!=null){
                    if (account.getExpired() > System.currentTimeMillis()){
                        // token 有效
                        tokenValid = true;
                    }
                }


                if (!tokenValid){
                    bizRes.setCode(TOKEN_INVALID);
                }else{
                    // 请求网络，完成自动登录
                    String url = API.Config.getDomain() + API.LOGIN_BY_TOKEN;
                    IRequest request = new BaseRequest(url);
                    request.setBody("token",account.getToken());

                    IResponse response = mHttpClient.post(request,false);
                    Log.d(TAG,response.getData());
                    if (response.getCode() == BaseBizResponse.STATE_OK){
                        bizRes = new Gson().fromJson(response.getData(),LoginResponse.class);
                        if (bizRes.getCode() == BaseBizResponse.STATE_OK){
                            //保存登录信息
                            account = bizRes.getData();
                            //todo: 加密存储

                            dao = new SharedPreferencesDao(MyTaxiApplication.getInstance(),SharedPreferencesDao.FILE_ACCOUNT);
                            dao.save(SharedPreferencesDao.KEY_ACCOUNT,account);

                            //通知UI
                            bizRes.setCode(LOGIN_SUC);
                        }else if (bizRes.getCode() == BaseBizResponse.STATE_TOKEN_INVALID){
                            bizRes.setCode(TOKEN_INVALID);
                        }
                    }else{
                        bizRes.setCode(SERVER_FAIL);
                    }
                }
                return bizRes;
            }
        });
    }

    @Override
    public void fetchNearDrivers(final double latitude, final double longitude) {
        RxBus.getInstance().chainProcess(new Function() {
            @Override
            public Object apply(Object o) throws Exception {
                IRequest request = new BaseRequest(API.Config.getDomain()
                        + API.GET_NEAR_DRIVERS);
                request.setBody("latitude", new Double(latitude).toString());
                request.setBody("longitude", new Double(longitude).toString());
                IResponse response = mHttpClient.get(request, false);
                if (response.getCode() == BaseBizResponse.STATE_OK){

                    try {
                        NearDriversResponse nearDriversResponse =
                                new Gson().fromJson(response.getData(), NearDriversResponse.class);
                        return nearDriversResponse;
                    }catch (Exception e){
                        return null;
                    }
                }
                return null;
            }
        });
    }

    @Override
    public void updateLocationToServer(final LocationInfo locationInfo) {
        RxBus.getInstance().chainProcess(new Function() {
            @Override
            public Object apply(Object o) throws Exception {
                IRequest request = new BaseRequest(API.Config.getDomain()
                        + API.UPLOAD_LOCATION);
                request.setBody("latitude",
                        new Double(locationInfo.getLatitude()).toString());
                request.setBody("longitude",
                        new Double(locationInfo.getLongitude()).toString());
                request.setBody("key",locationInfo.getKey());
                request.setBody("rotation",
                        new Float(locationInfo.getRotation()).toString());
                IResponse response = mHttpClient.post(request, false);
                if (response.getCode() == BaseBizResponse.STATE_OK){
                    LogUtil.d(TAG, "位置上报成功");
                }else {
                    LogUtil.d(TAG, "位置上报失败");
                }
                return "";
            }
        });
    }

    /**
     * 呼叫司机
     * @param key
     * @param cost
     * @param startLocation
     * @param endLocation
     */
    @Override
    public void callDriver(final String key,
                           final float cost,
                           final LocationInfo startLocation,
                           final LocationInfo endLocation) {
        RxBus.getInstance().chainProcess(new Function() {
            @Override
            public Object apply(Object o) throws Exception {
                /**
                 * 获取uid,phone
                 */
                SharedPreferencesDao sharedPreferencesDao =
                        new SharedPreferencesDao(MyTaxiApplication.getInstance(),
                                SharedPreferencesDao.FILE_ACCOUNT);
                Account account =
                        (Account) sharedPreferencesDao.get(SharedPreferencesDao.KEY_ACCOUNT,
                                Account.class);
                String uid = account.getUid();
                String phone = account.getAccount();
                IRequest request = new BaseRequest(API.Config.getDomain()
                            + API.CALL_DRIVER);
                request.setBody("key",key);
                request.setBody("uid",uid);
                request.setBody("phone",phone);
                request.setBody("startLatitude",
                        new Double(startLocation.getLatitude()).toString());
                request.setBody("startLongitude",
                        new Double(startLocation.getLongitude()).toString());
                request.setBody("endLatitude",
                        new Double(endLocation.getLatitude()).toString());
                request.setBody("endLongitude",
                        new Double(endLocation.getLongitude()).toString());
                request.setBody("cost",new Float(cost).toString());

                IResponse response = mHttpClient.post(request,false);
                OrderStateOptResponse orderStateOptResponse = new OrderStateOptResponse();
                if (response.getCode() == BaseBizResponse.STATE_OK){
                    //解析订单信息
                    orderStateOptResponse =
                            new Gson().fromJson(response.getData(),
                                    OrderStateOptResponse.class);
                }
                orderStateOptResponse.setCode(response.getCode());
                orderStateOptResponse.setState(OrderStateOptResponse.ORDER_STATE_CREATE);
                LogUtil.d(TAG,"call driver: " + orderStateOptResponse.getData());
                LogUtil.d(TAG,"call driver phone: " + phone);
                return orderStateOptResponse;
            }
        });
    }

    @Override
    public void cancelOrder(final String orderId) {
        RxBus.getInstance().chainProcess(new Function() {
            @Override
            public Object apply(Object o) throws Exception {
                IRequest request = new BaseRequest(API.Config.getDomain()
                        + API.CANCEL_ORDER);
                request.setBody("id",orderId);
                IResponse response = mHttpClient.post(request,false);
                OrderStateOptResponse orderStateOptResponse = new OrderStateOptResponse();
                orderStateOptResponse.setCode(response.getCode());
                orderStateOptResponse.setState(OrderStateOptResponse.ORDER_STATE_CANCEL);

                LogUtil.d(TAG,"cancel order: " + response.getData());
                return orderStateOptResponse;
            }
        });
    }

    @Override
    public boolean isLogin() {
        //获取本地登录信息
        Account account =
                (Account) mSharedPreferencesDao.get(SharedPreferencesDao.KEY_ACCOUNT,
                        Account.class);

        //登录是否过期
        boolean tokenValid = false;

        //检查token是否过期
        if (account!=null){
            if (account.getExpired() > System.currentTimeMillis()){
                //token 有效
                tokenValid = true;
            }
        }
        return tokenValid;
    }

}
