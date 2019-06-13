package com.kaidongyuan.app.kdyorder.model;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.kaidongyuan.app.kdyorder.app.MyApplication;
import com.kaidongyuan.app.kdyorder.bean.ChartOrderSum;
import com.kaidongyuan.app.kdyorder.constants.URLCostant;
import com.kaidongyuan.app.kdyorder.ui.activity.ChartOrderSumActivity;
import com.kaidongyuan.app.kdyorder.util.ExceptionUtil;
import com.kaidongyuan.app.kdyorder.util.HttpUtil;
import com.kaidongyuan.app.kdyorder.util.NetworkUtil;
import com.kaidongyuan.app.kdyorder.util.logger.Logger;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ChartOrderSumActivityBiz {

    private ChartOrderSumActivity mActivity;

    /**
     * 保存客户拜访数据集合
     */
    private List<ChartOrderSum> chartOrderSumList;
    private final String mTagChartOrderSum = "mTagChartOrderSum";

    public ChartOrderSumActivityBiz(ChartOrderSumActivity activity) {
        this.mActivity = activity;
        chartOrderSumList = new ArrayList<>();
    }

    public List<ChartOrderSum> getChartOrderSumList() {
        return chartOrderSumList;
    }

    /**
     * 获取报表列表数据
     *
     * @return 发送请求是否成功
     */
    public boolean GetDatas(final String strType, final String strTime) {

        try {
            StringRequest request = new StringRequest(Request.Method.POST, URLCostant.TotalOrderStatement, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    Logger.w(ChartOrderSumActivityBiz.this.getClass() + ".ChartOrderSumActivity:" + response);
                    GetDataSuccess(response);
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Logger.w(ChartOrderSumActivityBiz.this.getClass() + ".ChartOrderSumActivity:" + error.toString());
                    if (NetworkUtil.isNetworkAvailable()) {
                        mActivity.getDataError("获取报表数据失败!");
                    } else {
                        mActivity.getDataError("请检查网络是否正常连接！");
                    }
                }
            }) {
                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    Map<String, String> params = new HashMap<>();
                    params.put("strUserId", MyApplication.getInstance().getUser().getIDX());
                    params.put("strType", strType);
                    params.put("strTime", strTime);
                    params.put("strLicense", "");
                    return params;
                }
            };
            request.setTag(mTagChartOrderSum);
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
    private void GetDataSuccess(String response) {
        try {
            JSONObject object = JSON.parseObject(response);
            int type = object.getInteger("type");
            if (type == 1) {
                List<ChartOrderSum> tmpChartOrderSums = new ArrayList<>();
                if (object.containsKey("result")) {
                    tmpChartOrderSums = JSON.parseArray(object.getString("result"), ChartOrderSum.class);
                }
                chartOrderSumList.clear();
                chartOrderSumList.addAll(tmpChartOrderSums);
                mActivity.getDataSuccess();
            } else {
                mActivity.getDataError(object.getString("msg"));
            }
        } catch (Exception e) {
            ExceptionUtil.handlerException(e);
            mActivity.getDataError("获取客户拜访数据失败!");
        }
    }

    /**
     * 取消网络请求
     */
    public void cancelRequest() {
        HttpUtil.cancelRequest(mTagChartOrderSum);
    }
}