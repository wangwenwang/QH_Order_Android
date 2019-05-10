package com.kaidongyuan.app.kdyorder.util;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.widget.Toast;

//import com.kaidongyuan.app.kdyorder.ui.activity.CustomerMeetingCreateActivity;
//import com.kongzue.dialog.listener.OnMenuItemClickListener;
//import com.kongzue.dialog.v2.BottomMenu;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.kaidongyuan.app.kdyorder.bean.FatherAddress;
import com.kaidongyuan.app.kdyorder.constants.URLCostant;
import com.kaidongyuan.app.kdyorder.ui.activity.CustomerMeetingCheckInventoryActivity;
import com.kaidongyuan.app.kdyorder.ui.activity.CustomerMeetingRecomOrderActivity;
import com.kaidongyuan.app.kdyorder.ui.activity.CustomerMetHistoryActivity;
import com.kaidongyuan.app.kdyorder.util.logger.Logger;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.Field;
import java.net.URISyntaxException;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import static android.widget.Toast.LENGTH_LONG;
//import static com.kongzue.dialog.v2.TipDialog.SHOW_TIME_SHORT;

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



    /**
     * 跳转手机地图APP
     *
     * @param address 地址
     * @param mContext  上下文
     * @param appName   APP名字
     *
     * @return
     */
    public static void ToNavigation(final String address, final Context mContext, final String appName) {




//        List<String> list = new ArrayList<>();
//        list.add("菜单1");
//        list.add("菜单2");
//        list.add("菜单3");
//        BottomMenu.show((AppCompatActivity) mContext, list, new OnMenuItemClickListener() {
//            @Override
//            public void onClick(String text, int index) {
//                Toast.makeText(mContext,"菜单 " + text + " 被点击了",SHOW_TIME_SHORT).show();
//            }
//        },true);


//        List list = new ArrayList();
//        if (SystemUtil.isInstalled(mContext, "com.autonavi.minimap")) {
//
//            list.add("高德地图");
//        }
//        if (SystemUtil.isInstalled(mContext, "com.baidu.BaiduMap")) {
//
//            list.add("百度地图");
//        }
//
//        PromptDialog promptDialog = new PromptDialog((Activity) CustomerMeetingCreateActivity.mContext);
//        promptDialog.getDefaultBuilder().touchAble(true).round(3).loadingDuration(3000);
//
//        PromptButton cancle = new PromptButton("取消", null);
//        cancle.setTextColor(Color.parseColor("#0076ff"));
//        if (list.size() == 2) {
//            promptDialog.showAlertSheet("请选择地图", true, cancle,
//                    new PromptButton("高德地图", new PromptButtonListener() {
//                        @Override
//                        public void onClick(PromptButton button) {
//
//                            Log.d("LM", "调用高德地图");
//                            minimap(mContext, address, appName);
//                        }
//                    }),
//                    new PromptButton("百度地图", new PromptButtonListener() {
//                        @Override
//                        public void onClick(PromptButton button) {
//
//                            Log.d("LM", "调用百度地图");
//                            BaiduMap(mContext, address);
//                        }
//                    })
//            );
//        } else if (list.size() == 1) {
//
//            if (list.get(0).equals("高德地图")) {
//
//                Log.d("LM", "调用高德地图");
//                minimap(mContext, address, appName);
//            } else if (list.get(0).equals("百度地图")) {
//
//                Log.d("LM", "调用百度地图");
//                BaiduMap(mContext, address);
//            }
//        } else {
//
//            Toast.makeText(mContext, "未检索到本机已安装‘百度地图’或‘高德地图’App", LENGTH_LONG).show();
//        }
    }


    private static void minimap(Context mContext, String address, String appName) {
        //跳转到高德导航
        Intent autoIntent = new Intent();
        try {
            autoIntent.setData(Uri
                    .parse("androidamap://route?" +
                            "sourceApplication=" + appName +
                            "&slat=" + "" +
                            "&slon=" + "" +
                            "&dlat=" + "" +
                            "&dlon=" + "" +
                            "&dname=" + address +
                            "&dev=0" +
                            "&m=2" +
                            "&t=0"
                    ));
        } catch (Exception e) {
            Log.i("LM", "高德地图异常" + e);
        }
        mContext.startActivity(autoIntent);
    }

    private static void BaiduMap(Context mContext, String address) {

        //跳转到百度导航
        try {
            Intent baiduintent = Intent.parseUri("intent://map/direction?" +
                    "origin=" + "" +
                    "&destination=" + address +
                    "&mode=driving" +
                    "&src=Name|AppName" +
                    "#Intent;scheme=bdapp;package=com.baidu.BaiduMap;end", 0);
            mContext.startActivity(baiduintent);
        } catch (URISyntaxException e) {
            Log.d("LM", "URISyntaxException : " + e.getMessage());
            e.printStackTrace();
        }
    }


    public static boolean GetFatherAddress(final String strAddressIdx, final CustomerMetHistoryActivity mActivity, final int position) {

        try {
            StringRequest request = new StringRequest(Request.Method.POST, URLCostant.GetFatherAddress, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    Logger.w(this.getClass() + ".GetFatherAddress:" + response);
                    GetFatherAddressSuccess(response, mActivity, position);
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Logger.w(this.getClass() + ".GetFatherAddress:" + error.toString());
                    if (NetworkUtil.isNetworkAvailable()) {
                        mActivity.GetFatherAddressError("获取上级地址失败!");
                    } else {
                        mActivity.GetFatherAddressError("请检查网络是否正常连接！");
                    }
                }
            }) {
                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    Map<String, String> params = new HashMap<>();
                    params.put("strAddressIdx", strAddressIdx);
                    params.put("strLicense", "");
                    return params;
                }
            };
            request.setTag("fds");
            request.setRetryPolicy(new DefaultRetryPolicy(
                    DefaultRetryPolicy.DEFAULT_TIMEOUT_MS,
                    DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
            HttpUtil.getRequestQueue().add(request);
            return true;
        } catch (Exception e) {
            ExceptionUtil.handlerException(e);
            return false;
        }
    }

    /**
     * 处理网络请求返回数据成功
     *
     * @param response 返回的数据
     */
    private static void GetFatherAddressSuccess(String response, CustomerMetHistoryActivity mActivity, int position) {
        try {
            JSONObject object = JSON.parseObject(response);
            int type = object.getInteger("type");
            FatherAddress fatherAddressM;
            if (type == 1) {
                if (object.containsKey("result")) {
                    List<FatherAddress> fads = JSON.parseArray(object.getString("result"), FatherAddress.class);
                    if(fads.size()>0) {
                        fatherAddressM = fads.get(0);
                        mActivity.GetVisitAppOrderSuccess(fatherAddressM, position);
                    }else {

                        mActivity.GetFatherAddressError("获取上级信息失败!");
                    }
                }
            } else {
                mActivity.GetFatherAddressError(object.getString("msg"));
            }
        } catch (Exception e) {
            ExceptionUtil.handlerException(e);
            mActivity.GetFatherAddressError("获取上级信息失败!");
        }
    }







    public static boolean GetFatherAddress(final String strAddressIdx, final CustomerMeetingCheckInventoryActivity mActivity, final int position) {

        try {
            StringRequest request = new StringRequest(Request.Method.POST, URLCostant.GetFatherAddress, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    Logger.w(this.getClass() + ".GetFatherAddress:" + response);
                    GetFatherAddressSuccess(response, mActivity, position);
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Logger.w(this.getClass() + ".GetFatherAddress:" + error.toString());
                    if (NetworkUtil.isNetworkAvailable()) {
                        mActivity.GetFatherAddressError("获取上级地址失败!");
                    } else {
                        mActivity.GetFatherAddressError("请检查网络是否正常连接！");
                    }
                }
            }) {
                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    Map<String, String> params = new HashMap<>();
                    params.put("strAddressIdx", strAddressIdx);
                    params.put("strLicense", "");
                    return params;
                }
            };
            request.setTag("fds");
            request.setRetryPolicy(new DefaultRetryPolicy(
                    DefaultRetryPolicy.DEFAULT_TIMEOUT_MS,
                    DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
            HttpUtil.getRequestQueue().add(request);
            return true;
        } catch (Exception e) {
            ExceptionUtil.handlerException(e);
            return false;
        }
    }

    /**
     * 处理网络请求返回数据成功
     *
     * @param response 返回的数据
     */
    private static void GetFatherAddressSuccess(String response, CustomerMeetingCheckInventoryActivity mActivity, int position) {
        try {
            JSONObject object = JSON.parseObject(response);
            int type = object.getInteger("type");
            FatherAddress fatherAddressM;
            if (type == 1) {
                if (object.containsKey("result")) {
                    List<FatherAddress> fads = JSON.parseArray(object.getString("result"), FatherAddress.class);
                    if(fads.size()>0) {
                        fatherAddressM = fads.get(0);
                        mActivity.GetVisitAppOrderSuccess(fatherAddressM, position);
                    }else {

                        mActivity.GetFatherAddressError("获取上级信息失败!");
                    }
                }
            } else {
                mActivity.GetFatherAddressError(object.getString("msg"));
            }
        } catch (Exception e) {
            ExceptionUtil.handlerException(e);
            mActivity.GetFatherAddressError("获取上级信息失败!");
        }
    }
}
