package com.dalimao.mytaxi.account.view;

/**
 * Created by Administrator on 2018/4/13 0013.
 */

public interface ISmsCodeDialogView extends IView{
    /**
     * 显示倒计时
     */
    void showCountDownTimer();

    /**
     * 显示验证状态
     * @param b
     */
    void showSmsCodeCheckState(boolean b);

    void showUserExist(boolean b);
}
