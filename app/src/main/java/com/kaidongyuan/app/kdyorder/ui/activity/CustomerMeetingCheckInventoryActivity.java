package com.kaidongyuan.app.kdyorder.ui.activity;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.HeaderViewListAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
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
import com.kaidongyuan.app.kdyorder.adapter.BrandChoiceAdapter;
import com.kaidongyuan.app.kdyorder.adapter.CheckInvertoryListAdapter;
import com.kaidongyuan.app.kdyorder.adapter.CheckInvertoryListOKAdapter;
import com.kaidongyuan.app.kdyorder.app.MyApplication;
import com.kaidongyuan.app.kdyorder.bean.CustomerMeeting;
import com.kaidongyuan.app.kdyorder.bean.Product;
import com.kaidongyuan.app.kdyorder.bean.ProductTB;
import com.kaidongyuan.app.kdyorder.constants.URLCostant;
import com.kaidongyuan.app.kdyorder.interfaces.OnClickListenerStrInterface;
import com.kaidongyuan.app.kdyorder.model.CustomerMeetingCheckInventoryActivityBiz;
import com.kaidongyuan.app.kdyorder.util.ExceptionUtil;
import com.kaidongyuan.app.kdyorder.util.HttpUtil;
import com.kaidongyuan.app.kdyorder.util.NetworkUtil;
import com.kaidongyuan.app.kdyorder.util.ToastUtil;
import com.kaidongyuan.app.kdyorder.util.Tools;
import com.kaidongyuan.app.kdyorder.util.logger.Logger;
import com.kaidongyuan.app.kdyorder.widget.loadingdialog.MyLoadingDialog;
import com.kaidongyuan.app.kdyorder.widget.xlistview.XListView;
import com.zhy.android.percent.support.PercentFrameLayout;
import com.zhy.android.percent.support.PercentRelativeLayout;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CustomerMeetingCheckInventoryActivity extends BaseActivity implements View.OnClickListener, AdapterView.OnItemClickListener, OnClickListenerStrInterface {

    /**
     * 网络请求时显示的 Dialog
     */
    private MyLoadingDialog mLoadingDialog;
    /**
     * 返回上一界面按钮
     */
    private ImageView mImageViewGoBack;

    /**
     * 对应的业务类
     */
    private CustomerMeetingCheckInventoryActivityBiz mBiz;

    /**
     * 产品列表
     */
    private CheckInvertoryListAdapter mProductAdapter;

    /**
     * 产品列表
     */
    private XListView mListViewProduct;

    /**
     * 已检查产品列表适配器 提示
     */
    private TextView mTextViewOkListPrompt;

    /**
     * 已检查产品列表适配器
     */
    public CheckInvertoryListOKAdapter mProductOKAdapter;

    /**
     * 已检查产品列表
     */
    private XListView mListViewProductOK;

    // 备注
    private EditText remark;

    private final String mTagCheckInventory = "mTagCheckInventory";

    private CustomerMeeting customerM;

    /**
     * 用户选择的当前品牌
     */
    private TextView mTextViewCurrentOrderBrand;

    /**
     * 用户选择的当前品类
     */
    private TextView mTextViewCurrentOrderType;


    private LinearLayout llProdctBrand, llProductType;

    private PercentRelativeLayout prlTitleView;

    private LinearLayout ll_remark;

    /**
     * 选择品牌的 Dialog
     */
    private Dialog mChoiceBrandDialog;
    private ListView mListViewChoiceBrands;
    private BrandChoiceAdapter mBrandChoiceAdapter;
    private List<String> mBrandList;

    /**
     * 选择类型的 Dialog
     */
    private Dialog mChoiceTypeDialog;
    private ListView mListViewChoiceTypes;
    private BrandChoiceAdapter mTypeChoiceAdapter;
    private List<String> mTypeList;

    private Context mContext;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_check_inventory);

        mContext = this;

        remark = (EditText) findViewById(R.id.check_mark);
        mImageViewGoBack = (ImageView) this.findViewById(R.id.button_goback);

        mBiz = new CustomerMeetingCheckInventoryActivityBiz(this);

        customerM = (CustomerMeeting) getIntent().getParcelableExtra("CustomerMeeting");

        showLoadingDialog();
        mBiz.getProductTypesData();

        initView();
        setListener();
        setListViewHeight(mListViewProduct, mListViewProductOK);
    }

    private void setListViewHeight(XListView listView, XListView okListView) {

        // 屏幕总高度
        int h_screen = Tools.getScreen_h(mContext);
        // 已检查产品列表
        HeaderViewListAdapter hAdapterOk = (HeaderViewListAdapter) okListView.getAdapter();
        CheckInvertoryListOKAdapter listAdapterOk = (CheckInvertoryListOKAdapter) hAdapterOk.getWrappedAdapter();
        if (hAdapterOk == null) {
            return;
        }
        int itemHeightOk = 100;
        if (mBiz.getChoiceProducts().size() > 0) {
            View itemView = listAdapterOk.getView(0, null, listView);
            itemView.measure(0, 0);
            itemHeightOk = itemView.getMeasuredHeight();
        }
        LinearLayout.LayoutParams layoutParamsOk = (LinearLayout.LayoutParams) okListView.getLayoutParams();
        int okItemCount = listAdapterOk.getCount();

        // 产品列表
        HeaderViewListAdapter hAdapter = (HeaderViewListAdapter) listView.getAdapter();
        CheckInvertoryListAdapter listAdapter = (CheckInvertoryListAdapter) hAdapter.getWrappedAdapter();
        if (listAdapter == null) {
            return;
        }
        int itemHeight = 130;
        if (mBiz.getChoiceProducts().size() > 0) {
            View itemView = listAdapter.getView(0, null, listView);
            itemView.measure(0, 0);
            itemHeight = itemView.getMeasuredHeight();
        }
        LinearLayout.LayoutParams layoutParams = null;
//        layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, itemHeight * 4);
        layoutParams = (LinearLayout.LayoutParams) listView.getLayoutParams();

        // 已检查库存，listview显示最大行数
        int okItemCountMax = 3;
        if (okItemCount <= okItemCountMax) {

            // 总高 - (状态栏 + 标题栏 + 品牌高 + 已检查提示 + Cell*已检查数量 + 底部高 + 一些margin)
            int stateBarH = (int) Tools.getStateBar2(mContext);
            int bottomNavBar = Tools.getNavigationBarHeight(mContext); // h_screen 已经减去了 bottomNavBar
            float titleViewH = (float) ((h_screen - stateBarH) / 100.0 * 8);
            int brandH = Tools.dip2px(mContext, 40);
            int checkedH = Tools.dip2px(mContext, 35);
            int cellH = itemHeightOk;
            int bottomH = Tools.dip2px(mContext, (65 + 30));
            Log.d("LM",
                    "状态栏: " + stateBarH + "\n" +
                            "标题栏: " + titleViewH + "\n" +
                            "品牌: " + brandH + "\n" +
                            "提示: " + checkedH + "\n" +
                            "CellH: " + cellH + "\n" +
                            "底部: " + bottomH + "\n");
            if (mBiz.getChoiceProducts().size() > 0) {
                int listHeigth = (int) (h_screen - (stateBarH + titleViewH + brandH + checkedH + cellH * mBiz.getChoiceProducts().size() + bottomH + 20));
                layoutParams.height = listHeigth;
                mTextViewOkListPrompt.setVisibility(View.VISIBLE);
                layoutParamsOk.height = okItemCountMax * cellH + 12;
            } else {
                int listHeigth = (int) (h_screen - (stateBarH + titleViewH + brandH + cellH * mBiz.getChoiceProducts().size() + bottomH + 20));
                layoutParams.height = listHeigth;
                mTextViewOkListPrompt.setVisibility(View.GONE);
            }
        }
        listView.setLayoutParams(layoutParams);

    }

    public void confirmOnclick(View view) {

        String checkContext = "";

        for (int i = 0; i < mBiz.getChoiceProducts().size(); i++) {
            Product p = mBiz.getChoiceProducts().get(i);
            String uom;
            if (Tools.hasBASE_RATE(p.getBASE_RATE())) {
                uom = p.getPACK_UOM();
            } else {
                uom = p.getPRODUCT_UOM();
            }
            if (checkContext.equals("")) {
                checkContext = p.getPRODUCT_NAME() + "（" + p.getCHOICED_SIZE() + uom + "）";
            } else {
                checkContext = checkContext + "^；" + p.getPRODUCT_NAME() + "（" + p.getCHOICED_SIZE() + uom + "）";
            }
        }
        checkContext = checkContext + "^；" + remark.getText().toString();

        showLoadingDialog();
        try {
            final String finalCheckContext = checkContext;
            StringRequest request = new StringRequest(Request.Method.POST, URLCostant.GetVisitCheckInventory, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    Logger.w(this.getClass() + ".CheckInventory:" + response);
                    CheckInventorySuccess(response);
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Logger.w(this.getClass() + ".CheckInventory:" + error.toString());
                    if (NetworkUtil.isNetworkAvailable()) {
                        mLoadingDialog.dismiss();
                        ToastUtil.showToastBottom(String.valueOf("请求失败!"), Toast.LENGTH_SHORT);
                    } else {
                        mLoadingDialog.dismiss();
                        ToastUtil.showToastBottom(String.valueOf("请检查网络是否正常连接！"), Toast.LENGTH_SHORT);
                    }
                }
            }) {
                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    Map<String, String> params = new HashMap<>();
                    params.put("strVisitIdx", customerM.getVISIT_IDX());
                    params.put("strCheckInventory", finalCheckContext);
                    params.put("strLicense", "");
                    return params;
                }
            };
            request.setTag(mTagCheckInventory);
            request.setRetryPolicy(new DefaultRetryPolicy(
                    DefaultRetryPolicy.DEFAULT_TIMEOUT_MS,
                    DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
            HttpUtil.getRequestQueue().add(request);

        } catch (Exception e) {
            ExceptionUtil.handlerException(e);

        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        try {
            super.onDestroy();
            if (mLoadingDialog != null && mLoadingDialog.isShowing()) {
                mLoadingDialog.dismiss();
            }
            mLoadingDialog = null;
            HttpUtil.cancelRequest(mTagCheckInventory);
            mImageViewGoBack = null;
            mListViewProduct = null;
            mListViewProductOK = null;
            mProductAdapter = null;
            mProductOKAdapter = null;
            mBiz.cancelRequest();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 网络请求是显示 Dilaog
     */
    private void showLoadingDialog() {
        try {
            if (mLoadingDialog == null) {
                mLoadingDialog = new MyLoadingDialog(this);
            }
            mLoadingDialog.showDialog();
        } catch (Exception e) {
            ExceptionUtil.handlerException(e);
        }
    }

    /**
     * 处理网络请求返回数据成功
     *
     * @param response 返回的数据
     */
    private void CheckInventorySuccess(String response) {
        try {
            JSONObject object = JSON.parseObject(response);
            int type = object.getInteger("type");
            String msg = object.getString("msg");
            mLoadingDialog.dismiss();
            if (type == 1) {

                Intent intent = new Intent(this, CustomerMeetingRecomOrderActivity.class);
                intent.putExtra("CustomerMeeting", customerM);
                startActivity(intent);
            } else {

                ToastUtil.showToastBottom(String.valueOf(msg), Toast.LENGTH_SHORT);
            }
        } catch (Exception e) {
            ExceptionUtil.handlerException(e);
            ToastUtil.showToastBottom("服务器返回数据异常！", Toast.LENGTH_SHORT);
        }
    }

    @Override
    public void onClick(View v) {
        try {
            switch (v.getId()) {
                case R.id.button_goback:
                    pop();
                    break;
                case R.id.ll_prodct_brand:
                    showChoiceBrandDialog();
                    break;
                case R.id.ll_product_type:
                    showChoiceTypeDialog();
                    break;
                default:
                    break;
            }
        } catch (Exception e) {
            ExceptionUtil.handlerException(e);
        }
    }

    private void pop() {

        MyApplication.getInstance().finishActivity(CustomerMeetingCreateActivity.class);
        MyApplication.getInstance().finishActivity(ArrivedStoreActivity.class);
        this.finish();
    }


    @Override
    public void onBackPressed() {
        try {

            pop();
        } catch (Exception e) {

            ExceptionUtil.handlerException(e);
        }
    }

    private void setListener() {
        try {
            mImageViewGoBack.setOnClickListener(CustomerMeetingCheckInventoryActivity.this);
            mProductAdapter.setInterface(new CheckInvertoryListAdapter.CheckInvertoryListAdapterInterface() {
                @Override
                public void okProduct(int dataIndex) {

                    mProductOKAdapter.notifyChange(mBiz.getChoiceProducts());
                    setListViewHeight(mListViewProduct, mListViewProductOK);
                }
            });
            mProductOKAdapter.setInterface(new CheckInvertoryListOKAdapter.CheckInvertoryListOKAdapterInterface() {
                @Override
                public void delProduct(int dataIndex) {

                    mBiz.getChoiceProducts().remove(dataIndex);
                    mProductOKAdapter.notifyChange(mBiz.getChoiceProducts());
                    setListViewHeight(mListViewProduct, mListViewProductOK);
                }
            });
            llProdctBrand.setOnClickListener(this);
            llProductType.setOnClickListener(this);
        } catch (Exception e) {
            ExceptionUtil.handlerException(e);
        }
    }

    /**
     * 获取产品数据成功时回调的方法
     */
    public void getProductDataSuccess() {
        try {
            mLoadingDialog.dismiss();
            List<Product> products = mBiz.getmCurrentProductData();
            if (products.size() <= 0) {
                ToastUtil.showToastBottom("产品数据为空!", Toast.LENGTH_SHORT);
            }
            notifyDataChange();
            setListViewHeight(mListViewProduct, mListViewProductOK);
        } catch (Exception e) {
            ExceptionUtil.handlerException(e);
        }
    }

    public void getDataError(String msg) {

        try {
            mLoadingDialog.dismiss();
            ToastUtil.showToastBottom(String.valueOf(msg), Toast.LENGTH_SHORT);
            notifyDataChange();
            setListViewHeight(mListViewProduct, mListViewProductOK);
        } catch (Exception e) {
            ExceptionUtil.handlerException(e);
        }
    }

    /**
     * 数据发生变化，刷新每个 ListView 中的数据
     */
    private void notifyDataChange() {
        try {
            mProductAdapter.notifyChange(mBiz.getmCurrentProductData(), mBiz.getChoiceProducts());
            mProductOKAdapter.notifyChange(mBiz.getChoiceProducts());
        } catch (Exception e) {
            ExceptionUtil.handlerException(e);
        }
    }

    private void initView() {
        try {
            mImageViewGoBack = (ImageView) this.findViewById(R.id.button_goback);

            mTextViewCurrentOrderBrand = (TextView) this.findViewById(R.id.tv_brand);
            mTextViewCurrentOrderType = (TextView) this.findViewById(R.id.tv_type);

            llProdctBrand = (LinearLayout) findViewById(R.id.ll_prodct_brand);
            llProductType = (LinearLayout) findViewById(R.id.ll_product_type);

            mProductAdapter = new CheckInvertoryListAdapter(this, null, null);
            mListViewProduct = (XListView) this.findViewById(R.id.lv_product_list);
            mListViewProduct.setAdapter(mProductAdapter);
            mListViewProduct.setPullRefreshEnable(false);

            mTextViewOkListPrompt = (TextView) this.findViewById(R.id.tv_ok_list_prompt);
            mProductOKAdapter = new CheckInvertoryListOKAdapter(this, null);
            mListViewProductOK = (XListView) this.findViewById(R.id.lv_ok_product_list);
            mListViewProductOK.setAdapter(mProductOKAdapter);
            mListViewProductOK.setPullRefreshEnable(false);

            prlTitleView = (PercentRelativeLayout) this.findViewById(R.id.percentRL_title);
            ll_remark = (LinearLayout) findViewById(R.id.ll_remark);
        } catch (Exception e) {
            ExceptionUtil.handlerException(e);
        }
    }

    /**
     * 获取产品类型和品牌类型成功回调方法
     */
    public void getProductTypesDataSuccess() {//获取产品类型和品牌类型成功后获取产品信息
        try {

            mTextViewCurrentOrderBrand.setText(String.valueOf(mBiz.getCurrentOrderBrand()));
            mTextViewCurrentOrderType.setText(String.valueOf(mBiz.getCurrentProductType()));

            if (mBiz.getmBrands().size() > 0) {
                if (!mBiz.getProductsData(customerM.getIDX(), customerM.getADDRESS_IDX(), "", "")) {
                    mLoadingDialog.dismiss();
                    ToastUtil.showToastBottom("获取产品数据" + getString(R.string.sendrequest_fail), Toast.LENGTH_SHORT);
                }
            } else {
                mLoadingDialog.dismiss();
                ToastUtil.showToastBottom("产品品牌和品类为空！", Toast.LENGTH_SHORT);
            }
        } catch (Exception e) {
            ExceptionUtil.handlerException(e);
        }
    }

    /**
     * 显示选择品牌 Dialog
     */
    private void showChoiceBrandDialog() {
        try {
            if (mChoiceBrandDialog == null) {
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.create();
                mChoiceBrandDialog = builder.show();
                mChoiceBrandDialog.setCanceledOnTouchOutside(false);
                Window window = mChoiceBrandDialog.getWindow();
                window.setContentView(R.layout.dialog_brand_choice);
                window.findViewById(R.id.button_cancel).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        mChoiceBrandDialog.dismiss();
                    }
                });
                mListViewChoiceBrands = (ListView) window.findViewById(R.id.listView_chart_choice);
                mBrandChoiceAdapter = new BrandChoiceAdapter(null, this);
                mListViewChoiceBrands.setAdapter(mBrandChoiceAdapter);
                mListViewChoiceBrands.setOnItemClickListener(new CustomerMeetingCheckInventoryActivity.InnerOnItemClickListener());
            }
            mChoiceBrandDialog.show();

            mBrandList = new ArrayList<String>();
            mBrandList.add(0, MyApplication.getmRes().getString(R.string.all));
            for (ProductTB productTB : mBiz.getmBrands()) {
                String brand = productTB.getPRODUCT_TYPE();
                if (!TextUtils.isEmpty(brand) && !mBrandList.contains(brand)) {
                    mBrandList.add(brand);
                }
            }
            mBrandChoiceAdapter.notifyChange(mBrandList);
        } catch (Exception e) {
            ExceptionUtil.handlerException(e);
        }
    }


    /**
     * 显示选择类型 Dialog
     */
    private void showChoiceTypeDialog() {
        try {
            if (mChoiceTypeDialog == null) {
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.create();
                mChoiceTypeDialog = builder.show();
                mChoiceTypeDialog.setCanceledOnTouchOutside(false);
                Window window = mChoiceTypeDialog.getWindow();
                window.setContentView(R.layout.dialog_brand_choice);
                window.findViewById(R.id.button_cancel).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        mChoiceTypeDialog.dismiss();
                    }
                });
                mListViewChoiceTypes = (ListView) window.findViewById(R.id.listView_chart_choice);
                mTypeChoiceAdapter = new BrandChoiceAdapter(null, this);
                mListViewChoiceTypes.setAdapter(mTypeChoiceAdapter);
                mListViewChoiceTypes.setOnItemClickListener(new CustomerMeetingCheckInventoryActivity.InnerOnItemClickListener1());
            }
            mChoiceTypeDialog.show();

            mTypeList = new ArrayList<String>();
            mTypeList.add(0, MyApplication.getmRes().getString(R.string.all));
            for (ProductTB productTB : mBiz.getmBrands()) {
                String brand = productTB.getPRODUCT_CLASS();
                if (!TextUtils.isEmpty(brand) && !mTypeList.contains(brand)) {
                    mTypeList.add(brand);
                }
            }
            mTypeChoiceAdapter.notifyChange(mTypeList);
        } catch (Exception e) {
            ExceptionUtil.handlerException(e);
        }
    }

    /**
     * Dialog 中选择品牌的监听
     */
    private class InnerOnItemClickListener implements AdapterView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            try {
                mChoiceBrandDialog.dismiss();
                String brand = mBrandList.get(position);
                mTextViewCurrentOrderBrand.setText(brand);
                if (brand.equals(MyApplication.getmRes().getString(R.string.all))) {
                    brand = "";
                }
                mBiz.setmCurrentOrderBrand(brand);
                if (mBiz.getmBrands().size() > 0) {
                    if (!mBiz.getProductsData(customerM.getIDX(), customerM.getADDRESS_IDX(), brand, "")) {
                        mLoadingDialog.dismiss();
                        ToastUtil.showToastBottom("获取产品数据" + getString(R.string.sendrequest_fail), Toast.LENGTH_SHORT);
                    }
                } else {
                    mLoadingDialog.dismiss();
                    ToastUtil.showToastBottom("产品品牌和品类为空！", Toast.LENGTH_SHORT);
                }
            } catch (Exception e) {
                ExceptionUtil.handlerException(e);
            }
        }
    }

    /**
     * Dialog 中选择类型的监听
     */
    private class InnerOnItemClickListener1 implements AdapterView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            try {
                mChoiceTypeDialog.dismiss();
                String type = mTypeList.get(position);
                mTextViewCurrentOrderType.setText(type);
                if (type.equals(MyApplication.getmRes().getString(R.string.all))) {
                    type = "";
                }
                String brand = mBiz.getCurrentOrderBrand();
                if (brand.equals(MyApplication.getmRes().getString(R.string.all))) {
                    brand = "";
                }
                mBiz.setmCurrentProductType(type);
                if (mBiz.getmBrands().size() > 0) {
                    if (!mBiz.getProductsData(customerM.getIDX(), customerM.getADDRESS_IDX(), brand, type)) {
                        mLoadingDialog.dismiss();
                        ToastUtil.showToastBottom("获取产品数据" + getString(R.string.sendrequest_fail), Toast.LENGTH_SHORT);
                    }
                } else {
                    mLoadingDialog.dismiss();
                    ToastUtil.showToastBottom("产品品牌和品类为空！", Toast.LENGTH_SHORT);
                }
            } catch (Exception e) {
                ExceptionUtil.handlerException(e);
            }
        }
    }


    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

        Log.d("LM", "onItemClick: + 11");
    }

    @Override
    public boolean onClick(int position, String... tags) {

        Log.d("LM", "onItemClick: + 22");
        return false;
    }
}
