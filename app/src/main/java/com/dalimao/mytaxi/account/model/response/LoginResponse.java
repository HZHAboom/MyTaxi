package com.dalimao.mytaxi.account.model.response;

import com.dalimao.mytaxi.common.http.biz.BaseBizResponse;

/**
 * Created by Administrator on 2018/4/13 0013.
 */

public class LoginResponse extends BaseBizResponse {
    Account data;

    public Account getData() {
        return data;
    }

    public void setData(Account data) {
        this.data = data;
    }
}
