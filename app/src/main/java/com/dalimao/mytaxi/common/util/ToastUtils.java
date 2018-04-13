package com.dalimao.mytaxi.common.util;

import android.content.Context;
import android.widget.Toast;

/**
 * Created by Administrator on 2018/4/10 0010.
 */

public class ToastUtils {
    public static void show(Context context, String s) {
        Toast.makeText(context, s, Toast.LENGTH_SHORT).show();
    }
}
