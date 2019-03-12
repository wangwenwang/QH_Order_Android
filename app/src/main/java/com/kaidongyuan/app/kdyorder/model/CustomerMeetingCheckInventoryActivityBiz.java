package com.kaidongyuan.app.kdyorder.model;

import android.text.TextUtils;
import android.widget.Toast;

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
import com.kaidongyuan.app.kdyorder.bean.Product;
import com.kaidongyuan.app.kdyorder.bean.ProductTB;
import com.kaidongyuan.app.kdyorder.constants.URLCostant;
import com.kaidongyuan.app.kdyorder.ui.activity.CustomerMeetingCheckInventoryActivity;
import com.kaidongyuan.app.kdyorder.util.ExceptionUtil;
import com.kaidongyuan.app.kdyorder.util.HttpUtil;
import com.kaidongyuan.app.kdyorder.util.NetworkUtil;
import com.kaidongyuan.app.kdyorder.util.ToastUtil;
import com.kaidongyuan.app.kdyorder.util.logger.Logger;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CustomerMeetingCheckInventoryActivityBiz {

    public CustomerMeetingCheckInventoryActivityBiz(CustomerMeetingCheckInventoryActivity activity) {

        this.mActivity = activity;
    }

    private CustomerMeetingCheckInventoryActivity mActivity;

    /**
     * 记录用户请求是的商品品类
     */
    private String mTempCurrentProductType;

    /**
     * 记录用户请求时的品牌
     */
    private String mTempCurrrentOrderBrand;

    /**
     * 网络获取产品数据时的标记
     */
    private final String mTagGetProductsCheckInventory = "mTagGetProductsCheckInventory";

    /**
     * 网络获取产品品牌分类和品类时的标记
     */
    private final String mTagGetProductTypesDataOfCheckInventory = "mTagGetProductTypesDataOfCheckInventory";

    /**
     * 用户选择的当前品牌
     */
    private String mCurrentOrderBrand;

    /**
     * 用户选择的当前商品品类
     */
    private String mCurrentProductType;
    /**
     * 存放产品数据的集合
     */
    private List<Product> mCurrentProductData;

    /**
     * 已检查产品
     */
    private List<Product> mChoiceProducts = new ArrayList<>();

    /**
     * 品牌、类型
     */
    private List<ProductTB> mBrands = new ArrayList<>();

    /**
     * 产品的品牌类型集合
     */
    private List<String> mOrderBrands;

    /**
     * 产品分类集合
     */
    private List<String> mOrderTypes;

    /**
     * 获取产品数据集合
     *
     * @return 产品数据
     */
    public List<Product> getmCurrentProductData() {
        return mCurrentProductData == null ? new ArrayList<Product>() : mCurrentProductData;
    }

    /**
     * 获取用户已选择的商品
     *
     * @return 用户已选的商品
     */
    public List<Product> getChoiceProducts() {
        return mChoiceProducts == null ? new ArrayList<Product>() : mChoiceProducts;
    }

    /**
     * 获取产品品牌、类型
     *
     * @return 产品品牌、类型列表
     */
    public List<ProductTB> getmBrands() {
        return mBrands == null ? new ArrayList<ProductTB>() : mBrands;
    }

    /**
     * 获取用户选择的当前品类
     *
     * @return 当前品类
     */
    public String getCurrentProductType() {
        return mCurrentProductType;
    }

    /**
     * 获取用户选择的当前品牌
     *
     * @return 当前品牌
     */
    public String getCurrentOrderBrand() {
        return mCurrentOrderBrand;
    }

    public void setmCurrentOrderBrand(String mCurrentOrderBrand) {
        this.mCurrentOrderBrand = mCurrentOrderBrand;
    }

    public void setmCurrentProductType(String mCurrentProductType) {
        this.mCurrentProductType = mCurrentProductType;
    }

    /**
     * 获取产品数据
     *
     * @param mOrderPartyId    party id
     * @param mOrderAddressIdx address idx
     * @return 发送请求是否成功
     */
    public boolean getProductsData(final String mOrderPartyId, final String mOrderAddressIdx, final String strProductType, final String strProductClass) {
        try {
            StringRequest request = new StringRequest(Request.Method.POST, URLCostant.GET_PRDUCT_LIST_TYPE, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    Logger.w(CustomerMeetingCheckInventoryActivityBiz.this.getClass() + "CustomerMeetingCheckInventoryActivityBizSuccess:" + response);
                    getProductsDataSuccess(response);
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Logger.w(CustomerMeetingCheckInventoryActivityBiz.this.getClass() + "getProductsDataVolleyError:" + error.toString());
                    if (NetworkUtil.isNetworkAvailable()) {
                        mActivity.getDataError("获取产品数据失败！");
                    } else {
                        mActivity.getDataError("获取产品数据失败！" + MyApplication.getmRes().getString(R.string.please_check_net));
                    }
                }
            }) {
                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    if (MyApplication.getmRes().getString(R.string.all).equals(mTempCurrrentOrderBrand)) {
                        mTempCurrrentOrderBrand = "";
                    }
                    if (MyApplication.getmRes().getString(R.string.all).equals(mTempCurrentProductType)) {
                        mTempCurrentProductType = "";
                    }
                    Map<String, String> params = new HashMap<>();
                    params.put("strBusinessId", MyApplication.getInstance().getBusiness().getBUSINESS_IDX());
                    params.put("strPartyIdx", mOrderPartyId);
                    params.put("strPartyAddressIdx", mOrderAddressIdx);
                    params.put("strLicense", "");
                    params.put("strProductType", strProductType);
                    params.put("strProductClass", strProductClass);
                    return params;
                }
            };
            request.setTag(mTagGetProductsCheckInventory);
            request.setRetryPolicy(new DefaultRetryPolicy(
                    DefaultRetryPolicy.DEFAULT_TIMEOUT_MS,
                    DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
            HttpUtil.getRequestQueue().add(request);
            return true;
        } catch (Exception e) {
            ExceptionUtil.handlerException(e);
            ToastUtil.showToastBottom(MyApplication.getmRes().getString(R.string.sendrequest_fail), Toast.LENGTH_SHORT);
            return false;
        }
    }

    /**
     * 网络获取产品数据成功返回数据
     *
     * @param response 返回的信息
     */
    private void getProductsDataSuccess(String response) {
        try {
            JSONObject object = JSON.parseObject(response);
            int type = object.getInteger("type");
            if (mCurrentProductData != null) mCurrentProductData.clear();
            if (type == 1) {
                mCurrentProductData = JSON.parseArray(object.getString("result"), Product.class);

                // 测试多商品，界面会不会混乱
//                for (int i = 0; i < 12; i++) {
//
//                    mCurrentProductData.add(mCurrentProductData.get(0));
//                }
                if (mCurrentProductData.size() > 0) {
                    mActivity.getProductDataSuccess();
                } else {
                    mActivity.getDataError("获取产品数据失败，产品数据为空！");
                }
            } else {
                String msg = object.getString("msg");
                mActivity.getDataError(msg);
            }
        } catch (Exception e) {
            ExceptionUtil.handlerException(e);
            mActivity.getDataError("获取产品数据失败！");
        }
    }


    /**
     * 获取产品类型列表数据
     *
     * @return 发送请求是否成功
     */
    public boolean getProductTypesData() {
        try {
            StringRequest request = new StringRequest(Request.Method.POST, URLCostant.GET_PRODUCT_TYPE, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    Logger.w(CustomerMeetingCheckInventoryActivityBiz.this.getClass() + "getProductTypesDataSuccess:" + response);
                    getProductTypesDataSuccess(response);
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Logger.w(CustomerMeetingCheckInventoryActivityBiz.this.getClass() + "getProductTypesDataVolleyError:" + error.toString());
                    if (NetworkUtil.isNetworkAvailable()) {
                        mActivity.getDataError("获取品牌分类和产品分类失败！");
                    } else {
                        mActivity.getDataError("获取品牌分类和产品分类失败！" + MyApplication.getmRes().getString(R.string.please_check_net));
                    }
                }
            }) {
                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    Map<String, String> params = new HashMap<>();
                    params.put("strBusinessId", MyApplication.getInstance().getBusiness().getBUSINESS_IDX());
                    params.put("strLicense", "");
                    return params;
                }
            };
            request.setTag(mTagGetProductTypesDataOfCheckInventory);
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
     * 获取品牌和产品分类成功返回数据
     *
     * @param response 返回的信息
     */
    private void getProductTypesDataSuccess(String response) {
        try {
            JSONObject object = JSON.parseObject(response);
            int type = object.getInteger("type");
            if (type == 1) {
                setCurrentBrandAndType();
                mBrands = JSON.parseArray(object.getString("result"), ProductTB.class);
                if (mBrands.size() > 0) {
                    setOrderBrandsAndTypes();
                    mActivity.getProductTypesDataSuccess();
                } else {
                    mActivity.getDataError("品牌和产品分类为空！");
                }
            } else {
                String msg = object.getString("msg");
                mActivity.getDataError(msg);
            }
        } catch (Exception e) {
            ExceptionUtil.handlerException(e);
            mActivity.getDataError("获取品牌分类和产品分类失败！");
        }
    }

    /**
     * 取消网络请求
     */
    public void cancelRequest() {

        HttpUtil.cancelRequest(mTagGetProductsCheckInventory);
    }

    /**
     * 请求成功后设置当前的品牌和品类
     */
    private void setCurrentBrandAndType() {
        mCurrentOrderBrand = TextUtils.isEmpty(mTempCurrrentOrderBrand) ? mActivity.getString(R.string.all) : mTempCurrrentOrderBrand;
        mCurrentProductType = TextUtils.isEmpty(mTempCurrentProductType) ? mActivity.getString(R.string.all) : mTempCurrentProductType;
    }


    /**
     * 设置产品品类和品牌集合
     */
    private void setOrderBrandsAndTypes() {
        try {
            if (mOrderBrands == null) {
                mOrderBrands = new ArrayList<>();
            }
            if (mOrderTypes == null) {
                mOrderTypes = new ArrayList<>();
            }
            mOrderBrands.clear();
            mOrderTypes.clear();
            mOrderTypes.add(0, MyApplication.getmRes().getString(R.string.all));
            mOrderBrands.add(0, MyApplication.getmRes().getString(R.string.all));
            for (ProductTB productTB : mBrands) {
                String brand = productTB.getPRODUCT_CLASS();
                if (!TextUtils.isEmpty(brand) && !mOrderBrands.contains(brand)) {
                    mOrderBrands.add(brand);
                }
                String type = productTB.getPRODUCT_TYPE();
                if (!TextUtils.isEmpty(type) && !mOrderTypes.contains(type)) {
                    mOrderTypes.add(type);
                }
            }
            int orderTypesSize = mOrderTypes.size();
        } catch (Exception e) {
            ExceptionUtil.handlerException(e);
        }
    }
}