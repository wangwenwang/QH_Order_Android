package com.kaidongyuan.app.kdyorder.util;

/**
 * Created by Administrator on 2019/2/11.
 * Toast 工具类
 */
public class Tools {
    /**
     * 是否需要考虑折算率，BASE_RATE为0或1时需要考虑，否则不用
     * @param BASE_RATE 折算率
     * @return 是否需要考虑折算率
     */
    public static boolean hasBASE_RATE(double BASE_RATE) {
        if((int)BASE_RATE != 0 && (int)BASE_RATE != 1) {
            return true;
        }else {
            return false;
        }
    }
}
