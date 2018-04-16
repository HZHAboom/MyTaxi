package com.dalimao.mytaxi.account.model;

import android.os.Handler;

/**
 * Created by Administrator on 2018/4/13 0013.
 * 账号相关业务逻辑抽象
 */

public interface IAccountManager {

    //验证码发送成功
    static final int SMS_SEND_SUC = 1;
    //验证码发送失败
    static final int SMS_SEND_FAIL = -1;
    //验证码校验成功
    static final int SMS_CHECK_SUC = 2;
    //验证码校验失败
    static final int SMS_CHECK_FAIL = -2;
    //用户已存在
    static final int USER_EXIST = 3;
    //用户不存在
    static final int USER_NOT_EXIST = -3;
    //注册成功
    static final int REGISTER_SUC = 4;
    //登录成功
    static final int LOGIN_SUC = 5;
    //密码错误
    static final int PW_ERR = -5;
    //登录失败
    static final int TOKEN_INVALID = -6;
    //服务器错误
    static final int SERVER_FAIL = -999;

    void setHandler(Handler handler);
    /**
     * 下发验证码
     */
    void fetchSMSCode(String code);
    /**
     * 校验验证码
     */
    void checkSmsCode(String phone,String smsCode);
    /**
     * 用户是否注册接口
     */
    void checkUserExist(String phone);
    /**
     * 注册
     */
    void register(String phone,String password);
    /**
     * 登录
     */
    void login(String phone,String password);
    /**
     * token 登录
     */
    void loginByToken();


}
