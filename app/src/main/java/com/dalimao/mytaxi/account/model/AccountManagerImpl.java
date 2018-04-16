package com.dalimao.mytaxi.account.model;

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
import com.dalimao.mytaxi.common.util.DevUtil;
import com.google.gson.Gson;

/**
 * Created by Administrator on 2018/4/13 0013.
 */

public class AccountManagerImpl implements IAccountManager {

    private static final String TAG = "AccountManagerImpl";

    //网络请求
    private IHttpClient mHttpClient;
    //数据存储
    private SharedPreferencesDao mSharedPreferencesDao;
    //发送消息 handler
    private Handler mHandler;

    public AccountManagerImpl(IHttpClient httpClient, SharedPreferencesDao sharedPreferencesDao) {
        mHttpClient = httpClient;
        mSharedPreferencesDao = sharedPreferencesDao;
    }

    @Override
    public void setHandler(Handler handler) {
        mHandler = handler;
    }

    /**
     * 下发验证码
     * @param phone
     */
    @Override
    public void fetchSMSCode(final String phone) {
        new Thread(){
            @Override
            public void run() {
                String url = API.Config.getDomain() + API.GET_SMS_CODE;
                IRequest request = new BaseRequest(url);
                request.setBody("phone",phone);
                IResponse response = mHttpClient.get(request,false);
                Log.d(TAG,response.getData());
                if (response.getCode() == BaseBizResponse.STATE_OK){
                    BaseBizResponse bizRes = new Gson().fromJson(response.getData(),BaseBizResponse.class);
                    if (bizRes.getCode() == BaseBizResponse.STATE_OK){
                        mHandler.sendEmptyMessage(SMS_SEND_SUC);
                    }else{
                        mHandler.sendEmptyMessage(SMS_SEND_FAIL);
                    }
                }else{
                    mHandler.sendEmptyMessage(SMS_SEND_FAIL);
                }
            }
        }.start();
    }

    /**
     * 校验验证码
     * @param phone
     * @param smsCode
     */
    @Override
    public void checkSmsCode(final String phone, final String smsCode) {
        new Thread(){
            @Override
            public void run() {
                String url = API.Config.getDomain() + API.CHECK_SMS_CODE;
                IRequest request = new BaseRequest(url);
                request.setBody("phone",phone);
                request.setBody("code",smsCode);
                IResponse response = mHttpClient.get(request,false);
                Log.d(TAG,response.getData());
                if (response.getCode() == BaseBizResponse.STATE_OK){
                    BaseBizResponse bizRes = new Gson().fromJson(response.getData(),BaseBizResponse.class);
                    if (bizRes.getCode() == BaseBizResponse.STATE_OK){
                        mHandler.sendEmptyMessage(SMS_CHECK_SUC);
                    }else{
                        mHandler.sendEmptyMessage(SMS_CHECK_FAIL);
                    }
                }else{
                    mHandler.sendEmptyMessage(SMS_CHECK_FAIL);
                }
            }
        }.start();
    }

    @Override
    public void checkUserExist(final String phone) {
        new Thread(){
            @Override
            public void run() {
                String url = API.Config.getDomain() + API.CHECK_USER_EXIST;
                IRequest request = new BaseRequest(url);
                request.setBody("phone",phone);
                IResponse response = mHttpClient.get(request,false);
                Log.d(TAG,response.getData());
                if (response.getCode() == BaseBizResponse.STATE_OK){
                    BaseBizResponse bizRes =
                            new Gson().fromJson(response.getData(),
                                    BaseBizResponse.class);
                    if (bizRes.getCode() == BaseBizResponse.STATE_USER_EXIST){
                        mHandler.sendEmptyMessage(USER_EXIST);
                    }else if (bizRes.getCode() == BaseBizResponse.STATE_USER_NOT_EXIST){
                        mHandler.sendEmptyMessage(USER_NOT_EXIST);
                    }
                }else{
                    mHandler.sendEmptyMessage(SERVER_FAIL);
                }
            }
        }.start();
    }

    @Override
    public void register(final String phone, final String password) {
        new Thread(){
            @Override
            public void run() {
                String url = API.Config.getDomain() + API.REGISTER;
                IRequest request = new BaseRequest(url);
                request.setBody("phone",phone);
                request.setBody("password",password);
                request.setBody("uid", DevUtil.UUID(MyTaxiApplication.getInstance()));
                IResponse response = mHttpClient.post(request,false);
                Log.d(TAG,response.getData());
                if (response.getCode() == BaseBizResponse.STATE_OK){
                    BaseBizResponse bizRes = new Gson().fromJson(response.getData(),BaseBizResponse.class);
                    if (bizRes.getCode() == BaseBizResponse.STATE_OK){
                        mHandler.sendEmptyMessage(REGISTER_SUC);
                    }else {
                        mHandler.sendEmptyMessage(SERVER_FAIL);
                    }
                }else{
                    mHandler.sendEmptyMessage(SERVER_FAIL);
                }
            }
        }.start();
    }

    @Override
    public void login(final String phone, final String password) {
        new Thread(){
            @Override
            public void run() {
                String url = API.Config.getDomain() + API.LOGIN;
                IRequest request = new BaseRequest(url);
                request.setBody("phone",phone);
                request.setBody("password",password);

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
                    }else if (bizRes.getCode() == BaseBizResponse.STATE_PW_ERR){
                        mHandler.sendEmptyMessage(PW_ERR);
                    }
                    else {
                        mHandler.sendEmptyMessage(SERVER_FAIL);
                    }
                }else{
                    mHandler.sendEmptyMessage(SERVER_FAIL);
                }
            }
        }.start();
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
