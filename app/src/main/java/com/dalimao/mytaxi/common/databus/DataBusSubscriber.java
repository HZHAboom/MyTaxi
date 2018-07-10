package com.dalimao.mytaxi.common.databus;

/**
 * Created by Administrator on 2018/4/17 0017.
 */

public interface DataBusSubscriber {
    void onEvent(Object data);
}
