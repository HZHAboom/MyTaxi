package com.dalimao.mytaxi.main.model;

import android.os.Handler;
import android.util.Log;

import com.dalimao.mytaxi.MyTaxiApplication;
import com.dalimao.mytaxi.account.model.response.Account;
import com.dalimao.mytaxi.account.model.response.LoginResponse;
import com.dalimao.mytaxi.common.http.IHttpClient;
import com.dalimao.mytaxi.common.http.IRequest;
import com.dalimao.mytaxi.common.http.IResponse;
import com.dalimao.mytaxi.common.http.api.API;
import com.dalimao.mytaxi.common.http.biz.BaseBizResponse;
import com.dalimao.mytaxi.common.http.impl.BaseRequest;
import com.dalimao.mytaxi.common.storage.SharedPreferencesDao;
import com.google.gson.Gson;


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
        //  获取本地登录信息

        SharedPreferencesDao dao =
                new SharedPreferencesDao(MyTaxiApplication.getInstance(),SharedPreferencesDao.FILE_ACCOUNT);
        final Account account = (Account) dao.get(SharedPreferencesDao.KEY_ACCOUNT,Account.class);

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
            mHandler.sendEmptyMessage(TOKEN_INVALID);
        }else{
            // 请求网络，完成自动登录
            new Thread(){
                @Override
                public void run() {
                    String url = API.Config.getDomain() + API.LOGIN_BY_TOKEN;
                    IRequest request = new BaseRequest(url);
                    request.setBody("token",account.getToken());

                    IResponse response = mHttpClient.post(request,false);
                    Log.d(TAG,response.getData());
                    if (response.getCode() == BaseBizResponse.STATE_OK){
                        LoginResponse bizRes = new Gson().fromJson(response.getData(),LoginResponse.class);
                        if (bizRes.getCode() == BaseBizResponse.STATE_OK){
                            //保存登录信息
                            Account account = bizRes.getData();
                            //todo: 加密存储

                            SharedPreferencesDao dao =
                                    new SharedPreferencesDao(MyTaxiApplication.getInstance(),SharedPreferencesDao.FILE_ACCOUNT);
                            dao.save(SharedPreferencesDao.KEY_ACCOUNT,account);

                            //通知UI
                            mHandler.sendEmptyMessage(LOGIN_SUC);
                        }else if (bizRes.getCode() == BaseBizResponse.STATE_TOKEN_INVALID){
                            mHandler.sendEmptyMessage(TOKEN_INVALID);
                        }
                    }else{
                        mHandler.sendEmptyMessage(SERVER_FAIL);
                    }
                }
            }.start();
        }
    }
}
