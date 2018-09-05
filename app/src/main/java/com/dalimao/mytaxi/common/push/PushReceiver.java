package com.dalimao.mytaxi.common.push;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.dalimao.mytaxi.common.databus.RxBus;
import com.dalimao.mytaxi.common.http.biz.BaseBizResponse;
import com.dalimao.mytaxi.common.lbs.LocationInfo;
import com.dalimao.mytaxi.common.util.LogUtil;
import com.dalimao.mytaxi.main.model.bean.Order;
import com.dalimao.mytaxi.main.model.response.OrderStateOptResponse;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import cn.bmob.push.PushConstants;

/**
 * Created by Administrator on 2018/7/12 0012.
 */

public class PushReceiver extends BroadcastReceiver{
    private static final int MSG_TYPE_LOCATION = 1;
    //订单变化
    private static final int MSG_TYPE_ORDER = 2;

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals(PushConstants.ACTION_MESSAGE)){
            String msg = intent.getStringExtra("msg");
            LogUtil.d("bmob","客户端收到推送内容：" + msg);
            // TODO: 2018/7/12 0012 通知业务或UI
            //{"data":{"key":"cdc227a5-7b24-4509-8222-9969c88eec2a","latitude":27.985272,"longitude":120.659486,"rotation":-104.71875},"type":1}
            try {
                JSONObject jsonObject = new JSONObject(msg);
                int type = jsonObject.optInt("type");
                if (type == MSG_TYPE_LOCATION){
                    //位置变化
                    LocationInfo locationInfo =
                            new Gson().fromJson(jsonObject.optString("data"),LocationInfo.class);
                    RxBus.getInstance().send(locationInfo);
                } else if (type == MSG_TYPE_ORDER){
                    //订单变化
                    //解析数据
                    Order order =
                            new Gson().fromJson(jsonObject.optString("data"),Order.class);
                    OrderStateOptResponse stateOptResponse = new OrderStateOptResponse();
                    stateOptResponse.setData(order);
                    //更正错误stateOptResponse.setState(OrderStateOptResponse.ORDER_STATE_ACCEPT);
                    stateOptResponse.setState(order.getState());
                    stateOptResponse.setCode(BaseBizResponse.STATE_OK);
                    //通知UI
                    RxBus.getInstance().send(stateOptResponse);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
    }
}
