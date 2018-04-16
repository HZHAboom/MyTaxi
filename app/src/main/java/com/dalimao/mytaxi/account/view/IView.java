package com.dalimao.mytaxi.account.view;

/**
 * Created by Administrator on 2018/4/13 0013.
 */

public interface IView {
    /**
     * 显示loading
     */
    void showLoading();
    /**
     * 显示错误
     * @param code
     * @param msg
     */
    void showError(int code, String msg);
}
