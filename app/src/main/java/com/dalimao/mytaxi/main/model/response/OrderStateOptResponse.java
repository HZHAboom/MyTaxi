package com.dalimao.mytaxi.main.model.response;

import com.dalimao.mytaxi.common.http.biz.BaseBizResponse;

/**
 * Created by Administrator on 2018/8/31 0031.
 * 订单操作状态
 */

public class OrderStateOptResponse extends BaseBizResponse{
    public final static int ORDER_STATE_CREATE = 0;
    private int state;

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }
}
