package com.dalimao.mytaxi.common.util;

import java.util.regex.Pattern;

/**
 * Created by Administrator on 2018/4/10 0010.
 */

public class FormatUtil {
    public static boolean checkMobile(String phone) {
        String regex = "(\\+\\d+)?1[3458]\\d{9}$";
        return Pattern.matches(regex,phone);
    }
}
