package com.dalimao.mytaxi.account.model;

import android.os.Handler;
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
import com.dalimao.mytaxi.common.storage.SharedPreferencesDao;
import com.dalimao.mytaxi.common.util.DevUtil;
import com.google.gson.Gson;

import io.reactivex.functions.Function;

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
     *
     * @param phone
     */
    @Override
    public void fetchSMSCode(final String phone) {
        RxBus.getInstance().chainProcess(new Function() {
            @Override
            public Object apply(Object o) throws Exception {
                String url = API.Config.getDomain() + API.GET_SMS_CODE;
                IRequest request = new BaseRequest(url);
                request.setBody("phone", phone);
                IResponse response = mHttpClient.get(request, false);
                Log.d(TAG, response.getData());
                BaseBizResponse bizRes = new BaseBizResponse();
                if (response.getCode() == BaseBizResponse.STATE_OK) {
                    bizRes = new Gson().fromJson(response.getData(), BaseBizResponse.class);
                    if (bizRes.getCode() == BaseBizResponse.STATE_OK) {
                        bizRes.setCode(SMS_SEND_SUC);
                    } else {
                        bizRes.setCode(SMS_SEND_FAIL);
                    }
                } else {
                    bizRes.setCode(SMS_SEND_FAIL);
                }
                return bizRes;
            }
        });
    }

    /**
     * 校验验证码
     *
     * @param phone
     * @param smsCode
     */
    @Override
    public void checkSmsCode(final String phone, final String smsCode) {
        RxBus.getInstance().chainProcess(new Function() {
            @Override
            public Object apply(Object o) throws Exception {
                String url = API.Config.getDomain() + API.CHECK_SMS_CODE;
                IRequest request = new BaseRequest(url);
                request.setBody("phone", phone);
                request.setBody("code", smsCode);
                IResponse response = mHttpClient.get(request, false);
                Log.d(TAG, response.getData());

                BaseBizResponse bizRes = new BaseBizResponse();
                if (response.getCode() == BaseBizResponse.STATE_OK) {
                    bizRes = new Gson().fromJson(response.getData(), BaseBizResponse.class);
                    if (bizRes.getCode() == BaseBizResponse.STATE_OK) {
                        bizRes.setCode(SMS_CHECK_SUC);
                    } else {
                        bizRes.setCode(SMS_CHECK_FAIL);
                    }
                } else {
                    bizRes.setCode(SMS_CHECK_FAIL);
                }
                return bizRes;
            }
        });
    }

    @Override
    public void checkUserExist(final String phone) {
        RxBus.getInstance().chainProcess(new Function() {
            @Override
            public Object apply(Object o) throws Exception {
                String url = API.Config.getDomain() + API.CHECK_USER_EXIST;
                IRequest request = new BaseRequest(url);
                request.setBody("phone", phone);
                IResponse response = mHttpClient.get(request, false);
                Log.d(TAG, response.getData());

                BaseBizResponse bizRes = new BaseBizResponse();
                if (response.getCode() == BaseBizResponse.STATE_OK) {
                    bizRes = new Gson().fromJson(response.getData(), BaseBizResponse.class);
                    if (bizRes.getCode() == BaseBizResponse.STATE_USER_EXIST) {
                        bizRes.setCode(USER_EXIST);
                    } else if (bizRes.getCode() == BaseBizResponse.STATE_USER_NOT_EXIST) {
                        bizRes.setCode(USER_NOT_EXIST);
                    }
                } else {
                    bizRes.setCode(SERVER_FAIL);
                }
                return bizRes;
            }
        });
    }

    @Override
    public void register(final String phone, final String password) {
        RxBus.getInstance().chainProcess(new Function() {
            @Override
            public Object apply(Object o) throws Exception {
                String url = API.Config.getDomain() + API.REGISTER;
                IRequest request = new BaseRequest(url);
                request.setBody("phone", phone);
                request.setBody("password", password);
                request.setBody("uid", DevUtil.UUID(MyTaxiApplication.getInstance()));
                IResponse response = mHttpClient.post(request, false);
                Log.d(TAG, response.getData());

                BaseBizResponse bizRes = new BaseBizResponse();
                if (response.getCode() == BaseBizResponse.STATE_OK) {
                    bizRes = new Gson().fromJson(response.getData(), BaseBizResponse.class);
                    if (bizRes.getCode() == BaseBizResponse.STATE_OK) {
                        bizRes.setCode(REGISTER_SUC);
                    } else {
                        bizRes.setCode(SERVER_FAIL);
                    }
                } else {
                    bizRes.setCode(SERVER_FAIL);
                }
                return bizRes;
            }
        });
    }

    @Override
    public void login(final String phone, final String password) {
        RxBus.getInstance().chainProcess(new Function() {
            @Override
            public Object apply(Object o) throws Exception {
                String url = API.Config.getDomain() + API.LOGIN;
                IRequest request = new BaseRequest(url);
                request.setBody("phone", phone);
                request.setBody("password", password);
                IResponse response = mHttpClient.post(request, false);
                Log.d(TAG, response.getData());

                LoginResponse bizRes = new LoginResponse();
                if (response.getCode() == BaseBizResponse.STATE_OK) {
                    bizRes = new Gson().fromJson(response.getData(), LoginResponse.class);
                    if (bizRes.getCode() == BaseBizResponse.STATE_OK) {
                        //保存登录信息
                        Account account = bizRes.getData();
                        //todo: 加密存储

                        SharedPreferencesDao dao =
                                new SharedPreferencesDao(MyTaxiApplication.getInstance(), SharedPreferencesDao.FILE_ACCOUNT);
                        dao.save(SharedPreferencesDao.KEY_ACCOUNT, account);

                        //通知UI
                        bizRes.setCode(LOGIN_SUC);
                    } else if (bizRes.getCode() == BaseBizResponse.STATE_PW_ERR) {
                        bizRes.setCode(PW_ERR);
                    } else {
                        bizRes.setCode(SERVER_FAIL);
                    }
                } else {
                    bizRes.setCode(SERVER_FAIL);
                }
                return bizRes;
            }
        });
    }

    @Override
    public void loginByToken() {
        RxBus.getInstance().chainProcess(new Function() {
            @Override
            public Object apply(Object o) throws Exception {
                //  获取本地登录信息
                LoginResponse bizRes = new LoginResponse();
                SharedPreferencesDao dao =
                        new SharedPreferencesDao(MyTaxiApplication.getInstance(), SharedPreferencesDao.FILE_ACCOUNT);
                Account account = (Account) dao.get(SharedPreferencesDao.KEY_ACCOUNT, Account.class);

                //  登录是否过期
                boolean tokenValid = false;

                //  检查token是否过期
                if (account != null) {
                    if (account.getExpired() > System.currentTimeMillis()) {
                        // token 有效
                        tokenValid = true;
                    }
                }


                if (!tokenValid) {
                    bizRes.setCode(TOKEN_INVALID);
                } else {
                    // 请求网络，完成自动登录
                    String url = API.Config.getDomain() + API.LOGIN_BY_TOKEN;
                    IRequest request = new BaseRequest(url);
                    request.setBody("token", account.getToken());

                    IResponse response = mHttpClient.post(request, false);
                    Log.d(TAG, response.getData());


                    if (response.getCode() == BaseBizResponse.STATE_OK) {
                        bizRes = new Gson().fromJson(response.getData(), LoginResponse.class);
                        if (bizRes.getCode() == BaseBizResponse.STATE_OK) {
                            //保存登录信息
                            account = bizRes.getData();
                            //todo: 加密存储

                            dao = new SharedPreferencesDao(MyTaxiApplication.getInstance(), SharedPreferencesDao.FILE_ACCOUNT);
                            dao.save(SharedPreferencesDao.KEY_ACCOUNT, account);

                            //通知UI
                            bizRes.setCode(LOGIN_SUC);
                        } else if (bizRes.getCode() == BaseBizResponse.STATE_TOKEN_INVALID) {
                            bizRes.setCode(TOKEN_INVALID);
                        }
                    } else {
                        bizRes.setCode(SERVER_FAIL);
                    }
                }
                return bizRes;
            }
        });
    }

}
