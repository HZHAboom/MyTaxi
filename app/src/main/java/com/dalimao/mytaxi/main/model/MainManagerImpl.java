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

}
