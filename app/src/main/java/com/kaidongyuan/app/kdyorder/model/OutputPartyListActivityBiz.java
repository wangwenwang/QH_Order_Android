package com.kaidongyuan.app.kdyorder.model;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.kaidongyuan.app.kdyorder.R;
import com.kaidongyuan.app.kdyorder.app.MyApplication;
import com.kaidongyuan.app.kdyorder.bean.Address;
import com.kaidongyuan.app.kdyorder.bean.OutPutToAddress;
import com.kaidongyuan.app.kdyorder.bean.Party;
import com.kaidongyuan.app.kdyorder.constants.URLCostant;
import com.kaidongyuan.app.kdyorder.ui.activity.InventoryPartyListActivity;
import com.kaidongyuan.app.kdyorder.ui.activity.OutputInventoryActivity;
import com.kaidongyuan.app.kdyorder.ui.activity.OutputPartyListActivity;
import com.kaidongyuan.app.kdyorder.util.ExceptionUtil;
import com.kaidongyuan.app.kdyorder.util.HttpUtil;
import com.kaidongyuan.app.kdyorder.util.NetworkUtil;
import com.kaidongyuan.app.kdyorder.util.logger.Logger;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by ${tom} on 2017/9/21.
 */
public class OutputPartyListActivityBiz {
    private OutputPartyListActivity mActivity;

    /**
     * 获取客户列表的网络请求标记
     */
    private final String mTagGetCustomerList = "mTagGetCustomerList";
    /**
     * 获取客户地址的网络请求标记
     */
    private final String mTagGetCustomerAddressInfo = "mTagGetCustomerAddressInfo";
    /**
     * 存放显示在 ListView 中的客户数据集合
     */
    private List<OutPutToAddress> mToAddresList;
    /**
     * 存放从后台获取的所有客户数据集合
     */
    private List<OutPutToAddress> mTotalToAddresList;
    /**
     * 存放从后台获取的客户的地址集合
     */
    private List<Address> mCustomerAddressList;
    /**
     * 用户选中的下单客户
     */
    private OutPutToAddress mSelectedParty;

    public OutputPartyListActivityBiz(OutputPartyListActivity activity) {
        try {
            this.mActivity = activity;
            this.mToAddresList = new ArrayList<>();
        } catch (Exception e) {
            ExceptionUtil.handlerException(e);
        }
    }


    /**
     * 获取客户列表
     *
     * @return 是否成功发送请求
     */
    public boolean getToAddressList() {
        try {
            StringRequest request = new StringRequest(Request.Method.POST, URLCostant.GetToAddressList, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    Logger.w(this.getClass() + "getCustomerData.Success:" + response);
                    getToAddressListDataResponseSuccess(response);
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Logger.w(this.getClass() + "getCustomerData.VolleyError:" + error.toString());
                    if (NetworkUtil.isNetworkAvailable()) {
                        mActivity.getCustomerDataError("获取客户列表失败!");
                    } else {
                        mActivity.getCustomerDataError("获取客户列表失败!" + MyApplication.getmRes().getString(R.string.please_check_net));
                    }
                }
            }) {
                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    Map<String, String> params = new HashMap<>();
                    params.put("ADDRESS_IDX", mActivity.mPartyAddressIdx);
                    params.put("BUSINESS_IDX", MyApplication.getInstance().getBusiness().getBUSINESS_IDX());
                    params.put("strLicense", "");
                    return params;
                }
            };
            request.setTag(mTagGetCustomerList);
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
     * 网络请求成功返回数据
     *
     * @param response 返回的数据
     */
    private void getToAddressListDataResponseSuccess(String response) {
        try {
            JSONObject object = JSON.parseObject(response);
            int type = object.getInteger("type");
            if (type == 1) {//获取数据成功
                JSONObject jo=object.getJSONObject("result");
                mTotalToAddresList = JSON.parseArray(jo.getString("To"), OutPutToAddress.class);
                mToAddresList = mTotalToAddresList;
                mActivity.getCustomerDataSuccess();
            } else {
                mActivity.getCustomerDataError(object.getString("msg"));
            }
        } catch (Exception e) {
            ExceptionUtil.handlerException(e);
            mActivity.getCustomerDataError("获取客户列表失败!");
        }
    }

    /**
     * 取消网络请求
     */
    public void cancelRequest() {
        try {
            HttpUtil.cancelRequest(mTagGetCustomerList);
            HttpUtil.cancelRequest(mTagGetCustomerAddressInfo);
        } catch (Exception e) {
            ExceptionUtil.handlerException(e);
        }
    }

    /**
     * 获取客户列表
     *
     * @return 客户列表
     */
    public List<OutPutToAddress> getmToAddresList() {
        return mToAddresList;
    }

    /**
     * 根据用户输入查找客户
     *
     * @param msg 输入的信息
     */
    public void searchParty(String msg) {
        try {
            if (msg == null || msg.length() <= 0) {
                mToAddresList = mTotalToAddresList;
            } else {
                ArrayList<OutPutToAddress> repartylist = new ArrayList<>();
                for (OutPutToAddress toAddress : mTotalToAddresList) {
                    if (toAddress.getPARTY_NAME().contains(msg)) {
                        repartylist.add(toAddress);
                    }
                }
                mToAddresList = repartylist;
            }
        } catch (Exception e) {
            ExceptionUtil.handlerException(e);
            mToAddresList.clear();
        }
    }

    /**
     * 获取客户地址
     *
     * @param positionInPartysListView 在客户 ListView 中的 Position
     * @return 发送请求是否成功
     */
    public boolean getPartygetAddressInfo(final int positionInPartysListView) {
        try {
            StringRequest request = new StringRequest(Request.Method.POST, URLCostant.GET_ADDRESS, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    Logger.w(this.getClass() + "getPartygetAddressInfo.Success:" + response);
                    getCustomerAddressResponseSuccess(response);
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Logger.w(this.getClass() + "getPartygetAddressInfo.VolleyError:" + error.toString());
                    if (NetworkUtil.isNetworkAvailable()) {
                        mActivity.getCustomerAdressDataError("获取客户地址失败!");
                    } else {
                        mActivity.getCustomerAdressDataError("获取客户地址失败!" + MyApplication.getmRes().getString(R.string.please_check_net));
                    }
                }
            }) {
                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    mSelectedParty = mToAddresList.get(positionInPartysListView);
                    Map<String, String> params = new HashMap<>();
                    params.put("strUserId", MyApplication.getInstance().getUser().getIDX());
                    params.put("strPartyId", mSelectedParty.getIDX());
                    params.put("strLicense", "");
                    return params;
                }
            };
            request.setTag(mTagGetCustomerAddressInfo);
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
     * 发送获取用户地址请求成功返回数据
     *
     * @param response 返回的消息
     */
    private void getCustomerAddressResponseSuccess(String response) {
        try {
            JSONObject object = JSON.parseObject(response);
            int type = object.getInteger("type");
            if (type == 1) {
                mCustomerAddressList = JSON.parseArray(object.getString("result"), Address.class);
                if (mCustomerAddressList.size() < 1) {
                    mActivity.getCustomerAdressDataError("没有获取到客户有效地址，请联系供应商！");
                } else {
                    mActivity.getCustomerAdressDataSuccess();
                }
            } else {
                mActivity.getCustomerAdressDataError("获取客户地址失败!" + object.getString("msg"));
            }
        } catch (Exception e) {
            ExceptionUtil.handlerException(e);
            mActivity.getCustomerAdressDataError("获取客户地址失败!");
        }
    }

    /**
     * 获取客户地址集合
     *
     * @return 客户地址集合
     */
    public List<Address> getmCustomerAddress() {
        return mCustomerAddressList;
    }

    /**
     * 获取用户选择的客户
     *
     * @return 用户选择的客户
     */
    public OutPutToAddress getmSelectedParty() {
        return mSelectedParty;
    }
}
