package com.kaidongyuan.app.kdyorder.model;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.kaidongyuan.app.kdyorder.R;
import com.kaidongyuan.app.kdyorder.app.MyApplication;
import com.kaidongyuan.app.kdyorder.bean.KpiTrack;
import com.kaidongyuan.app.kdyorder.constants.URLCostant;
import com.kaidongyuan.app.kdyorder.ui.activity.KpiTrackActivity;
import com.kaidongyuan.app.kdyorder.util.ExceptionUtil;
import com.kaidongyuan.app.kdyorder.util.HttpUtil;
import com.kaidongyuan.app.kdyorder.util.NetworkUtil;
import com.kaidongyuan.app.kdyorder.util.logger.Logger;

import java.util.HashMap;
import java.util.Map;

public class KpiTrackActivityBiz {

    private KpiTrackActivity mActivity;

    public KpiTrack kpiTrack;

    /**
     * 获取业绩考核数据
     */
    private final String mTagKpiTrackActivityBiz = "mTagKpiTrackActivityBiz";


    public KpiTrackActivityBiz(KpiTrackActivity activity) {
        this.mActivity = activity;
    }

    public void setKpiTrack(KpiTrack kpiTrack) {
        this.kpiTrack = kpiTrack;
    }

    public KpiTrack getKpiTrack() {
        return kpiTrack;
    }

    /**
     * 获取目标金额目标销量
     * @return 是否成功发送请求
     */
    public boolean GetTargetByUserIdx() {
        try {
            StringRequest request = new StringRequest(Request.Method.POST, URLCostant.GetTargetByUserIdx, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    Logger.w(KpiTrackActivityBiz.this.getClass() + "KpiTrackActivityBizSuccess:" + response);
                    GetTargetByUserIdxSuccess(response);
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Logger.w(KpiTrackActivityBiz.this.getClass() + "KpiTrackActivityBizError:" + error.toString());
                    if (NetworkUtil.isNetworkAvailable()) {
                        mActivity.KpiTrackActivityBizError("获取目标金额目标销量失败！");
                    } else {
                        mActivity.KpiTrackActivityBizError("获取目标金额目标销量失败！" + MyApplication.getmRes().getString(R.string.please_check_net));
                    }
                }
            }) {
                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    Map<String, String> params = new HashMap<>();
                    params.put("strUserId", MyApplication.getInstance().getUser().getIDX());
                    params.put("strBusinessId", MyApplication.getInstance().getBusiness().getBUSINESS_IDX());
                    params.put("strLicense", "");
                    return params;
                }
            };
            request.setTag(mTagKpiTrackActivityBiz);
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
     * 网络请求订单线路数据成功返回数据
     * @param response 返回的信息
     */
    private void GetTargetByUserIdxSuccess(String response) {
        try {
            JSONObject object = JSON.parseObject(response);
            int type = object.getInteger("type");
            if (type == 1) {
                JSONArray array = object.getJSONArray("result");
                JSONObject obj = (JSONObject) array.get(0);
                kpiTrack = new KpiTrack();
                kpiTrack.setSalesVolume(obj.getString("SalesVolume"));
                kpiTrack.setCompleteSalesVolume(obj.getString("CompleteSalesVolume"));
                kpiTrack.setAmountMoney(obj.getString("AmountMoney"));
                kpiTrack.setCompleteAmountMoney(obj.getString("CompleteAmountMoney"));
                mActivity.KpiTrackActivityBizSuccess();
            } else {
                String msg = object.getString("msg");
                mActivity.KpiTrackActivityBizError(msg);
            }
        } catch (Exception e) {
            mActivity.KpiTrackActivityBizError("请求失败" + e.getMessage());
            ExceptionUtil.handlerException(e);
        }
    }


    /**
     * 取消网络请求
     */
    public void cancelRequest() {
        HttpUtil.cancelRequest(mTagKpiTrackActivityBiz);
    }
}
