package com.kaidongyuan.app.kdyorder.util;

import android.content.Context;
import android.content.res.Resources;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.Field;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * Created by Administrator on 2019/2/11.
 * Toast 工具类
 */
public class Tools {
    /**
     * 是否需要考虑折算率，BASE_RATE为0或1时需要考虑，否则不用
     *
     * @param BASE_RATE 折算率
     * @return 是否需要考虑折算率
     */
    public static boolean hasBASE_RATE(double BASE_RATE) {
        if ((int) BASE_RATE != 0 && (int) BASE_RATE != 1) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * 拜访状态转义
     *
     * @param VISIT_STATES 转义前拜访状态
     * @return 转义后的状态
     */
    public static String getVISIT_STATES(String VISIT_STATES) {
        if (VISIT_STATES.equals("")) {
            return "未拜访";
        } else if (VISIT_STATES.equals("离店")) {
            return "已拜访";
        } else {
            return "拜访中";
        }
    }

    public static int dip2px(Context context, float dipValue) {
        Resources r = context.getResources();
        return (int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP, dipValue, r.getDisplayMetrics());
    }

    public static double getStateBar1(Context context) {
        double statusBarHeight = Math.ceil(20 * context.getResources().getDisplayMetrics().density);
        return statusBarHeight; // 40
    }

    public static int getStateBar2(Context context) {
        Class c = null;
        try {
            c = Class.forName("com.android.internal.R$dimen");
            Object obj = c.newInstance();
            Field field = c.getField("status_bar_height");
            int x = Integer.parseInt(field.get(obj).toString());
            int statusBarHeight = context.getResources().getDimensionPixelSize(x);
            return statusBarHeight; //48
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

    public static int getStateBar3(Context context) {
        int result = 0;
        int resourceId = context.getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = context.getResources().getDimensionPixelSize(resourceId); //48
        }
        return result;
    }

    public static int getNavigationBarHeight(Context context) {
        Resources resources = context.getResources();
        int resourceId = resources.getIdentifier("navigation_bar_height", "dimen", "android");
        int height = resources.getDimensionPixelSize(resourceId);
        Log.v("dbw", "Navi height:" + height);
        return height;
    }

    public static int getScreen_w(Context context) {
        DisplayMetrics dm = context.getResources().getDisplayMetrics();
        int w_screen = dm.widthPixels;
        return w_screen;
    }

    public static int getScreen_h(Context context) {
        DisplayMetrics dm = context.getResources().getDisplayMetrics();
        int h_screen = dm.heightPixels;
        return h_screen;
    }

    public static int textLength(String s) {
        byte[] buff = new byte[0];
        try {
            buff = s.getBytes("GBK");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return buff.length;
    }

    public static String oneDecimal(String s) {
        DecimalFormat decimalFormat = new DecimalFormat("0.0");//将double类型保留两位小数，不四舍五入
        String uf = decimalFormat.format(Double.parseDouble(s));
        return uf;
    }

    public static String twoDecimal(String s) {
        DecimalFormat decimalFormat = new DecimalFormat("0.00");//将double类型保留两位小数，不四舍五入
        String uf = decimalFormat.format(Double.parseDouble(s));
        return uf;
    }

    public static String twoDecimal(float f) {
        DecimalFormat decimalFormat = new DecimalFormat("0.00");//将double类型保留两位小数，不四舍五入
        String uf = decimalFormat.format(f);
        return uf;
    }

    // 把String转化为float
    public static float convertToFloat(String number, float defaultValue) {
        if (TextUtils.isEmpty(number)) {
            return defaultValue;
        }
        try {
            return Float.parseFloat(number);
        } catch (Exception e) {
            return defaultValue;
        }
    }

    public static String getCurrDate() {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = new Date(System.currentTimeMillis());
        String time1 = simpleDateFormat.format(date);
        return simpleDateFormat.format(date);
    }

    /**
     * 判断是否为今天
     *
     * @param day 传入的 时间  "2019/4/12 10:10:30"
     * @return true今天 false不是
     * @throws ParseException
     */
    public static boolean isToday(String day) throws ParseException {

        ThreadLocal<SimpleDateFormat> DateLocal = new ThreadLocal<SimpleDateFormat>();
        if (null == DateLocal.get()) {
            DateLocal.set(new SimpleDateFormat("yyyy/MM/dd", Locale.CHINA));
        }

        Calendar pre = Calendar.getInstance();
        Date predate = new Date(System.currentTimeMillis());
        pre.setTime(predate);
        Calendar cal = Calendar.getInstance();
        Date date = DateLocal.get().parse(day);
        cal.setTime(date);
        if (cal.get(Calendar.YEAR) == (pre.get(Calendar.YEAR))) {
            int diffDay = cal.get(Calendar.DAY_OF_YEAR)
                    - pre.get(Calendar.DAY_OF_YEAR);

            if (diffDay == 0) {
                return true;
            }
        }
        return false;
    }
}
