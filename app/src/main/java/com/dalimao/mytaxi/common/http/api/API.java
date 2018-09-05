package com.dalimao.mytaxi.common.http.api;

/**
 * Created by Administrator on 2018/3/13 0013.
 */

public class API {
    public static final String TEST_GET = "/get?uid=${uid}";
    public static final String TEST_POST = "/post";
    //  获取验证码
    public static final String GET_SMS_CODE =
            "/f34e28da5816433d/getMsgCode?phone=${phone}";
    public static final String CHECK_SMS_CODE =
            "/f34e28da5816433d/checkMsgCode?phone=${phone}&code=${code}";
    //  检查用户是否存在
    public static final String CHECK_USER_EXIST = "/f34e28da5816433d/isUserExist?phone=${phone}";
    //  注册
    public static final String REGISTER = "/f34e28da5816433d/register";
    //  登录
    public static final String LOGIN = "/f34e28da5816433d/auth";
    //  token登录
    public static final String LOGIN_BY_TOKEN = "/f34e28da5816433d/login";
    //  获取附近司机
    public static final String GET_NEAR_DRIVERS = "/f34e28da5816433d/getNearDrivers?latitude=${latitude}&longitude=${longitude}";
    //  上报当前位置
    public static final String UPLOAD_LOCATION = "/f34e28da5816433d/updateUserLocation";
    //  呼叫司机
    public static final String CALL_DRIVER = "/f34e28da5816433d/callDriver";
    //  取消订单
    public static final String CANCEL_ORDER = "/f34e28da5816433d/cancelOrder";
    //  支付成功
    public static final String PAY = "/f34e28da5816433d/paySuc";

    /**
     * 配置域名信息
     */
    public static class Config{
        private static final String TEST_DOMAIN = "http://cloud.bmob.cn";
        private static final String RELEASE_DOMAIN = "http://cloud.bmob.cn";
        private static final String TEST_APP_ID = "e90928398db0130b0d6d21da7bde357e";
        private static final String RELEASE_APP_ID = "e90928398db0130b0d6d21da7bde357e";
        private static final String TEST_APP_KEY = "514d8f8a2371bdf1566033f6664a24d2";
        private static final String RELEASE_APP_KEY = "514d8f8a2371bdf1566033f6664a24d2";
        private static String appId = TEST_APP_ID;
        private static String appKey = TEST_APP_KEY;
        private static String domain = TEST_DOMAIN;

        public static void setDebug(boolean debug){
            domain = debug ? TEST_DOMAIN : RELEASE_DOMAIN;
            appId = debug ? TEST_APP_ID : RELEASE_APP_ID;
            appKey = debug ? TEST_APP_KEY : RELEASE_APP_KEY;
        }
        public static String getDomain(){
            return domain;
        }

        public static String getAppId() {
            return appId;
        }

        public static String getAppKey() {
            return appKey;
        }
    }
}
