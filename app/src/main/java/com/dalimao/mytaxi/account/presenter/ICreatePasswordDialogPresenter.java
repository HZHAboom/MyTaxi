package com.dalimao.mytaxi.account.presenter;

/**
 * Created by Administrator on 2018/4/13 0013.
 */

public interface ICreatePasswordDialogPresenter {
    /**
     * 提交注册
     */
    void requestRegister(String phone,String pw);
    /**
     * 登录
     */
    void requestLogin(String phone,String pw);
}
