package com.kaidongyuan.app.kdyorder.ui.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Parcelable;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
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
import com.example.mylibrary.SlideDateTimeListener;
import com.example.mylibrary.SlideDateTimePicker;
import com.kaidongyuan.app.kdyorder.R;
import com.kaidongyuan.app.kdyorder.adapter.OrderProductDetailAdapter;
import com.kaidongyuan.app.kdyorder.adapter.OrderPromotionAdapter;
import com.kaidongyuan.app.kdyorder.app.MyApplication;
import com.kaidongyuan.app.kdyorder.bean.CustomerMeeting;
import com.kaidongyuan.app.kdyorder.bean.OrderGift;
import com.kaidongyuan.app.kdyorder.bean.Product;
import com.kaidongyuan.app.kdyorder.bean.PromotionDetail;
import com.kaidongyuan.app.kdyorder.bean.PromotionOrder;
import com.kaidongyuan.app.kdyorder.constants.BusinessConstants;
import com.kaidongyuan.app.kdyorder.constants.EXTRAConstants;
import com.kaidongyuan.app.kdyorder.constants.SharedPreferenceConstants;
import com.kaidongyuan.app.kdyorder.constants.URLCostant;
import com.kaidongyuan.app.kdyorder.model.OrderConfirmActivityBiz;
import com.kaidongyuan.app.kdyorder.model.OutPutOrderConfirmActivityBiz;
import com.kaidongyuan.app.kdyorder.util.DensityUtil;
import com.kaidongyuan.app.kdyorder.util.ExceptionUtil;
import com.kaidongyuan.app.kdyorder.util.HttpUtil;
import com.kaidongyuan.app.kdyorder.util.NetworkUtil;
import com.kaidongyuan.app.kdyorder.util.OrderUtil;
import com.kaidongyuan.app.kdyorder.util.SharedPreferencesUtil;
import com.kaidongyuan.app.kdyorder.util.ToastUtil;
import com.kaidongyuan.app.kdyorder.util.logger.Logger;
import com.kaidongyuan.app.kdyorder.widget.MyListView;
import com.kaidongyuan.app.kdyorder.widget.loadingdialog.MyLoadingDialog;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by ${tom} on 2017/9/23.
 */
public class OutPutOrderConfirmActivity extends BaseFragmentActivity implements View.OnClickListener  {
    /**
     * 跳转到添加赠品界面时用到的常量
     */
    private final int ADDGIFT_REQUESCODE = 12;

    /**
     * 对应的业务类
     */
    private OutPutOrderConfirmActivityBiz mBiz;

    /**
     * 返回上一界面按钮
     */
    private ImageView mImageViewGoBack;

    // 发货方
    private TextView tv_outputfrom_partName;
    private TextView tv_outputfrom_person;
    private TextView tv_outputfrom_tel;
    private TextView tv_outputfrom_address;

    // 收货方
    private TextView tv_outputto_partName;
    private TextView tv_outputto_person;
    private TextView tv_outputto_tel;
    private TextView tv_outputto_address;

    /**
     * 用户选中的商品
     */
    private List<Product> mChoicedProducts;

    /**
     * 送货时间
     */
    private TextView mTextViewTime;
    /**
     * 产品总数
     */
    private TextView mTextViewTotalCount;
    /**
     * 原价
     */
    private TextView mTextViewOrgPrice;
    /**
     * 实际付款
     */
    private TextView mTextViewActPrice;
    /**
     * 促销信息
     */
    private TextView mTextViewPromotionRemark;
    /**
     * 促销减价
     */
    private TextView mTextViewPromotionPrice;
    /**
     * 支付价格
     */
    private TextView mTextViewPayPrice;
    /**
     * 无赠品时显示的提示框
     */
    private TextView mTextViewNoPromotion;
    /**
     * 产品列表
     */
    private MyListView mListViewProduct;
    /**
     * 产品列表适配器
     */
    private OrderProductDetailAdapter mProductAdapter;
    /**
     * 赠品列表
     */
    private MyListView mListViewPromotion;
    /**
     * 赠品列表适配器
     */
    private OrderPromotionAdapter mPromotionAdapter;
    /**
     * 备注信息输入框
     */
    private EditText mEditTextMark;
    /**
     * 网络请求时显示的 Dialog
     */
    private MyLoadingDialog mLoadingDialog;
    /**
     * 添加赠品按钮
     */
    private Button mBtAddGift;
    /**
     * 用户选择的送货时间
     */
    private Date mDate;
    /**
     * 手动添加的赠品
     */
    private ArrayList<PromotionDetail> mGiftData;
    /**
     * 商品总数过度值
     */
    private int mTempTotalQTY = 0;
    /**
     * 添加赠品界面返回的赠品
     */
    private ArrayList<PromotionDetail> mReturnGiftData;
    /**
     * 赠品的品类集合
     */
    private List<OrderGift> mGiftCategoryData;

    public String party_idx;

    public String strOutputOrderType;

    // 发货
    public String strPartyName;
    public String strPartyCode;
    public String strContactPerson;
    public String strContactTel;
    public String strAddressCode;
    public String strAddressInfo;
    public String strAddressIDX;
    // 收货
    public String strToPartyCode;
    public String strToPartyName;
    public String strToPerson;
    public String strToTel;
    public String strToAddress;

    /**
     * 客户拜访，拜访ID
     */
    public String VISIT_IDX;

    // 拜访模型
    CustomerMeeting customerM;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        try {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_outputorder_confirm);
            initData();
            setTop();
            initView();
            setListener();
            getPromotionData();
        } catch (Exception e) {
            ExceptionUtil.handlerException(e);
        }
    }

    @Override
    protected void onDestroy() {
        try {
            super.onDestroy();
            mBiz.cancelRequest();
            mBiz = null;
            if (mLoadingDialog != null && mLoadingDialog.isShowing()) {
                mLoadingDialog.dismiss();
            }
            mLoadingDialog = null;
            // 发货
            tv_outputfrom_partName=null;
            tv_outputfrom_person=null;
            tv_outputfrom_tel=null;
            tv_outputfrom_address=null;
            // 收货
            tv_outputto_partName=null;
            tv_outputto_person=null;
            tv_outputto_tel=null;
            tv_outputto_address=null;
            // 返回
            mImageViewGoBack = null;
            mChoicedProducts = null;
            mTextViewTime = null;
            mTextViewTotalCount = null;
            mTextViewOrgPrice = null;
            mTextViewActPrice = null;
            mTextViewPromotionRemark = null;
            mTextViewPromotionPrice = null;
            mTextViewPayPrice = null;
            mTextViewNoPromotion = null;
            mListViewProduct = null;
            mProductAdapter = null;
            mListViewPromotion = null;
            mPromotionAdapter = null;
            mEditTextMark = null;
            mBtAddGift = null;
            mDate = null;
            mGiftData = null;
            mReturnGiftData = null;
            mGiftCategoryData = null;
            VISIT_IDX = null;
        } catch (Exception e) {
            ExceptionUtil.handlerException(e);
        }
    }

    private void initData() {
        try {

            mBiz = new OutPutOrderConfirmActivityBiz(this);
            Intent intent = getIntent();
            //出库人信息
            if (intent.hasExtra(EXTRAConstants.ORDER_PARTY_ID)) {
                party_idx = intent.getStringExtra(EXTRAConstants.ORDER_PARTY_ID);
            }
            if (intent.hasExtra(EXTRAConstants.ORDER_ADDRESS_CODE)){
                strAddressCode = intent.getStringExtra(EXTRAConstants.ORDER_ADDRESS_CODE);
            }
            if (intent.hasExtra(EXTRAConstants.ORDER_ADDRESS_INFORMATION)){
                strAddressInfo = intent.getStringExtra(EXTRAConstants.ORDER_ADDRESS_INFORMATION);
            }
            if (intent.hasExtra(EXTRAConstants.ORDER_ADDRESS_ContactPerson)){
                strContactPerson = intent.getStringExtra(EXTRAConstants.ORDER_ADDRESS_ContactPerson);
            }
            if (intent.hasExtra(EXTRAConstants.ORDER_ADDRESS_ContactTel)){
                strContactTel = intent.getStringExtra(EXTRAConstants.ORDER_ADDRESS_ContactTel);
            }
            if (intent.hasExtra(EXTRAConstants.ORDER_PARTY_ADDRESS_IDX)) {
                strAddressIDX = intent.getStringExtra(EXTRAConstants.ORDER_PARTY_ADDRESS_IDX);
            }
            if (intent.hasExtra(EXTRAConstants.ORDER_PARTY_NO)){
                strPartyCode=intent.getStringExtra(EXTRAConstants.ORDER_PARTY_NO);
            }


            if (intent.hasExtra(EXTRAConstants.ORDER_PARTY_NAME)){
                strPartyName = intent.getStringExtra(EXTRAConstants.ORDER_PARTY_NAME);
            }
            //收货人信息
            if (intent.hasExtra(EXTRAConstants.OUTPUT_TOPARTYCODE)){
                strToPartyCode = intent.getStringExtra(EXTRAConstants.OUTPUT_TOPARTYCODE);
            }
            if (intent.hasExtra(EXTRAConstants.OUTPUT_TOPARTYNAME)){
                strToPartyName = intent.getStringExtra(EXTRAConstants.OUTPUT_TOPARTYNAME);
            }
            if (intent.hasExtra(EXTRAConstants.OUTPUT_TOPERSON)){
                strToPerson = intent.getStringExtra(EXTRAConstants.OUTPUT_TOPERSON);
            }
            if (intent.hasExtra(EXTRAConstants.OUTPUT_TOTEL)){
                strToTel = intent.getStringExtra(EXTRAConstants.OUTPUT_TOTEL);
            }
            if (intent.hasExtra(EXTRAConstants.OUTPUT_TOADDRESS)){
                strToAddress = intent.getStringExtra(EXTRAConstants.OUTPUT_TOADDRESS);
            }
            if (intent.hasExtra(EXTRAConstants.OUTPUT_ORDER_TYPE)){
                strOutputOrderType=intent.getStringExtra(EXTRAConstants.OUTPUT_ORDER_TYPE);
            }
            if (intent.hasExtra("VISIT_IDX")){
                VISIT_IDX=intent.getStringExtra("VISIT_IDX");
            }

            if (intent.hasExtra("CustomerMeeting")) {
                customerM = intent.getParcelableExtra("CustomerMeeting");
            }

            //所选出库产品
            mChoicedProducts = intent.getParcelableArrayListExtra(EXTRAConstants.CHOICED_PRODUCT_LIST);

        } catch (Exception e) {
            ExceptionUtil.handlerException(e);
        }
    }

    private void setTop () {
        //版本4.4以上设置状态栏透明，界面布满整个界面
        if (Build.VERSION.SDK_INT>=Build.VERSION_CODES.KITKAT) {
            Window window = getWindow();
            window.setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS, WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            View topView = findViewById(R.id.topview);
            ViewGroup.LayoutParams topParams = topView.getLayoutParams();
            topParams.height = DensityUtil.getStatusHeight()*16/30;
            topView.setLayoutParams(topParams);
        }
    }

    private void initView() {
        try {
            setTieltLayoutHeight();
            mImageViewGoBack = (ImageView) this.findViewById(R.id.button_goback);
            mTextViewTime = (TextView) this.findViewById(R.id.tv_time);
            // 收货
            tv_outputfrom_partName= (TextView) this.findViewById(R.id.tv_outputfrom_party_name);
            tv_outputfrom_person= (TextView) this.findViewById(R.id.tv_outputfrom_person);
            tv_outputfrom_tel= (TextView) this.findViewById(R.id.tv_outputfrom_tel);
            tv_outputfrom_address= (TextView) this.findViewById(R.id.tv_outputfrom_address);
            // 发货
            tv_outputto_partName= (TextView) this.findViewById(R.id.tv_outputto_party_name);
            tv_outputto_person= (TextView) this.findViewById(R.id.tv_outputto_person);
            tv_outputto_tel= (TextView) this.findViewById(R.id.tv_outputto_tel);
            tv_outputto_address= (TextView) this.findViewById(R.id.tv_outputto_address);
            // 总计
            mTextViewTotalCount = (TextView) this.findViewById(R.id.tv_total_count);
            mTextViewOrgPrice = (TextView) this.findViewById(R.id.tv_org_price);
            mTextViewActPrice = (TextView) this.findViewById(R.id.tv_act_price);
            mTextViewPromotionRemark = (TextView) this.findViewById(R.id.tv_promotion_remark);
            mTextViewPromotionPrice = (TextView) this.findViewById(R.id.tv_promotion_price);
            mTextViewPayPrice = (TextView) this.findViewById(R.id.tv_pay_price);
            mTextViewNoPromotion = (TextView) this.findViewById(R.id.tv_no_promotion);
            mListViewProduct = (MyListView) this.findViewById(R.id.lv_product);
            mProductAdapter = new OrderProductDetailAdapter(this, null);
            mListViewProduct.setAdapter(mProductAdapter);
            mListViewPromotion = (MyListView) this.findViewById(R.id.lv_promotion);
            mPromotionAdapter = new OrderPromotionAdapter(this, null);
            mListViewPromotion.setAdapter(mPromotionAdapter);
            mEditTextMark = (EditText) this.findViewById(R.id.et_mark);
            mBtAddGift = (Button) this.findViewById(R.id.bt_addgift);
            if (strOutputOrderType!=null&&strOutputOrderType.equals("output_return")){
                tv_outputfrom_partName.setText(strToPartyName);
                tv_outputfrom_person.setText(strToPerson);
                tv_outputfrom_tel.setText(strToTel);
                tv_outputfrom_address.setText(strToAddress);

                tv_outputto_partName.setText(strPartyName);
                tv_outputto_person.setText(strContactPerson);
                tv_outputto_tel.setText(strContactTel);
                tv_outputto_address.setText(strAddressInfo);
            }else {
                tv_outputfrom_partName.setText(strPartyName);
                tv_outputfrom_person.setText(strContactPerson);
                tv_outputfrom_tel.setText(strContactTel);
                tv_outputfrom_address.setText(strAddressInfo);

                tv_outputto_partName.setText(strToPartyName);
                tv_outputto_person.setText(strToPerson);
                tv_outputto_tel.setText(strToTel);
                tv_outputto_address.setText(strToAddress);
            }

        } catch (Exception e) {
            ExceptionUtil.handlerException(e);
        }
    }

    /**
     * 输入框在底部，所以要动态调整标题栏的高度
     * 设置标题栏高度，默认是50dp，根据百分百设置，统一标题栏高度
     */
    private void setTieltLayoutHeight() {
        try {
            String strHeight = getString(R.string.title_height);//百分百布局格式 如 1%h
            int index = strHeight.indexOf("%");
            if (index != -1) {
                int height = Integer.parseInt(strHeight.substring(0, index));
                int titleLayoutHeight = DensityUtil.getHeight() * height / 100;
                RelativeLayout relativeLayoutTitle = (RelativeLayout) findViewById(R.id.percentRL_title);
                ViewGroup.LayoutParams params = relativeLayoutTitle.getLayoutParams();
                params.height = titleLayoutHeight;
                relativeLayoutTitle.setLayoutParams(params);
            }
        } catch (Exception e) {
            ExceptionUtil.handlerException(e);
        }
    }

    private void setListener() {
        try {
            mImageViewGoBack.setOnClickListener(this);
            findViewById(R.id.btn_confirm).setOnClickListener(this);
            mBtAddGift.setOnClickListener(this);
            findViewById(R.id.rl_select_time).setOnClickListener(this);
            mProductAdapter.setInterface(new OrderProductDetailAdapter.OrderProductDetailAdapterModifyPriceInterface() {
                @Override
                public void raisePrice(int dataIndex) {//上调价格 0.1元
                    mBiz.raisePrice(dataIndex);
                    notifyDataChange();
                }

                @Override
                public void cutPrice(int dataIndex) {//下调价格 0.1元
                    mBiz.cutPrice(dataIndex);
                    notifyDataChange();
                }

                @Override
                public void setProductPrice(int dataIndex, double inputPirce) {//手动设置价格
                    mBiz.setPrice(dataIndex, inputPirce);
                    notifyDataChange();
                }
            });
        } catch (Exception e) {
            ExceptionUtil.handlerException(e);
        }
    }

    /**
     * 发送订单信息到后台，获取后台返回的订单信息
     */
    private void getPromotionData() {
        try {
            if (mBiz.getPromotionData(mBiz.getSubmitString(mChoicedProducts))) {
                showLoadingDialog();
            } else {
                ToastUtil.showToastBottom(getString(R.string.sendrequest_fail), Toast.LENGTH_SHORT);
            }
        } catch (Exception e) {
            ExceptionUtil.handlerException(e);
        }
    }

    /**
     * 网络请求是显示的 Dialog
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

    @Override
    public void onClick(View v) {
        try {
            switch (v.getId()) {
                case R.id.button_goback://返回上一界面
                    finish();
                    break;
                case R.id.btn_confirm://提交订单
                    confirm();
                    break;
                case R.id.bt_addgift://添加赠品
                    choiceGift();
                    break;
                case R.id.rl_select_time://选择送货时间
                    new SlideDateTimePicker.Builder((getSupportFragmentManager()))
                            .setListener(new DateHandler(0))
                            .setInitialDate(new Date())
                            .setMinDate(new Date())
                            .build()
                            .show();
                    break;
            }
        } catch (Exception e) {
            ExceptionUtil.handlerException(e);
        }
    }

    /**
     * 获取促销信息失败时回调的方法
     *
     * @param msg 显示的信息
     */
    public void getDataError(String msg) {
        try {
            mLoadingDialog.dismiss();
            ToastUtil.showToastBottom(String.valueOf(msg), Toast.LENGTH_SHORT);
        } catch (Exception e) {
            ExceptionUtil.handlerException(e);
        }
    }

    /**
     * 获取促销信息成功时回调的方法
     */
    public void getPromotionDataSuccess() {
        try {
            mLoadingDialog.dismiss();
            PromotionOrder promotionOrder = mBiz.getOrder();
            if (promotionOrder != null) {
                setViewData();
                //设置添加赠品按钮是否可见
                String bussinessName = SharedPreferencesUtil.getValueByName(SharedPreferenceConstants.BUSSINESS_CODE, SharedPreferenceConstants.NAME, 0);
//                if (!(bussinessName.contains(BusinessConstants.TYPE_YIBAO)||bussinessName.contains(BusinessConstants.TYPE_DIKUI))&& BusinessConstants.IS_HAVE_GIFT.equals(mBiz.getOrder().HAVE_GIFT)) {
                if (!(bussinessName.contains(BusinessConstants.TYPE_YIBAO)||bussinessName.contains(BusinessConstants.TYPE_DIKUI))&& BusinessConstants.IS_HAVE_GIFT.equals(mBiz.getOrder().HAVE_GIFT)) {
                    mBtAddGift.setVisibility(View.VISIBLE);
                } else {
                    mBtAddGift.setVisibility(View.GONE);
                }
            } else {
                ToastUtil.showToastBottom("确认订单信息失败！", Toast.LENGTH_SHORT);
            }
        } catch (Exception e) {
            ExceptionUtil.handlerException(e);
        }
    }

    /**
     * 成功提交订单时回调的方法
     */
    public void confirmSuccess() {
        try {
            mLoadingDialog.dismiss();
            ToastUtil.showToastBottom("提交订单成功！", Toast.LENGTH_SHORT);

            if (strOutputOrderType!=null&&strOutputOrderType.equals("output_visit_sale")){

                nextOnclick();
            }else {

                MyApplication.getInstance().finishActivity(OutputInventoryActivity.class);
                this.finish();
            }

        } catch (Exception e) {
            ExceptionUtil.handlerException(e);
        }
    }

    /**
     * 设置空间的信息
     */
    private void setViewData() {
        try {
            PromotionOrder order = mBiz.getOrder();
            List<PromotionDetail> products = mBiz.getProducts();
            mProductAdapter.notifyChange(products);
            List<PromotionDetail> promotions = mBiz.getPromotions();
            mPromotionAdapter.notifyChange(promotions);
            if (products == null || promotions.size() == 0) {
                mTextViewNoPromotion.setVisibility(View.VISIBLE);
                mListViewPromotion.setVisibility(View.GONE);
            } else {
                mTextViewNoPromotion.setVisibility(View.GONE);
                mListViewPromotion.setVisibility(View.VISIBLE);
            }
            mTextViewTotalCount.setText(String.valueOf(order.TOTAL_QTY));
            if (promotions.size() == 0) {
                mTextViewOrgPrice.setText("￥" + order.ORG_PRICE + "");
            } else {
                mTextViewOrgPrice.setText("￥" + (order.ORG_PRICE - mBiz.getPromotionPrice(promotions)));
            }
            // 不需要显示赠品价格
            mTextViewPayPrice.setText(String.valueOf(OrderUtil.getPaymentType(order.PAYMENT_TYPE)));
            if (order.MJ_REMARK == null || order.MJ_REMARK.equals("") || order.MJ_REMARK.equals("+|+")) {
                mTextViewPromotionRemark.setVisibility(View.GONE);
                this.findViewById(R.id.textview_promotion_remark_head).setVisibility(View.GONE);
                mTextViewPromotionPrice.setVisibility(View.GONE);
                this.findViewById(R.id.textview_promotion_price_head).setVisibility(View.GONE);
                mTextViewActPrice.setText("￥" + order.ACT_PRICE);
            } else {
                mTextViewPromotionRemark.setText(String.valueOf(OrderUtil.getPromotionRemark(order.MJ_REMARK, false)));
                mTextViewPromotionPrice.setText("- ￥" + order.MJ_PRICE + "");
                mTextViewActPrice.setText("￥" + (order.ACT_PRICE - order.MJ_PRICE));
            }
        } catch (Exception e) {
            ExceptionUtil.handlerException(e);
        }
    }

    /**
     * 选择送货时间的监听
     */
    class DateHandler extends SlideDateTimeListener {
        int which;

        DateHandler(int which) {
            this.which = which;
        }

        @Override
        public void onDateTimeCancel() {//不设置送货时间
            try {
                super.onDateTimeCancel();
                mDate = null;
                mTextViewTime.setText(null);
            } catch (Exception e) {
                ExceptionUtil.handlerException(e);
            }
        }

        @Override
        public void onDateTimeSet(Date date) {//设置送货时间
            try {
                mDate = date;
                if (date != null) {
                    SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
                    mTextViewTime.setText(df.format(date));
                }
            } catch (Exception e) {
                ExceptionUtil.handlerException(e);
            }
        }
    }

    /**
     * 确认订单 最后一步提交到服务器
     */
    private void confirm() {
        try {
            String remark = mEditTextMark.getText().toString().trim();
            mBiz.setConfirmData(mReturnGiftData, mChoicedProducts, mTempTotalQTY, mDate, remark);
            if (mBiz.confirm()) {
                showLoadingDialog();
            } else {
                ToastUtil.showToastBottom(getString(R.string.sendrequest_fail), Toast.LENGTH_SHORT);
            }
        } catch (Exception e) {
            ExceptionUtil.handlerException(e);
        }
    }

    /**
     * 跳转到选择赠品界面
     */
    private void choiceGift() {
        try {
            PromotionOrder order = mBiz.getOrder();
            if (mReturnGiftData!=null){
                order.OrderDetails.removeAll(mReturnGiftData);
            }
            mTempTotalQTY = 0;
            mReturnGiftData = null;
            mGiftCategoryData = order.GiftClasses;
            if (mGiftCategoryData != null && mGiftCategoryData.size() > 0) {
                if (mGiftData != null) {//将已选赠品集合清空
                    mGiftData = null;
                }
                Intent intent1 = new Intent(OutPutOrderConfirmActivity.this, AddGiftActivity.class);
                intent1.putExtra(AddGiftActivity.EXTRA_STR_PARTYID, party_idx);
                intent1.putExtra(AddGiftActivity.EXTRA_STR_PARTY_ADDRESSID, strAddressIDX);
                intent1.putExtra(AddGiftActivity.EXTRA_PROMOTION_START_LINE_NO, mBiz.getPromotionNumber());
                intent1.putParcelableArrayListExtra(AddGiftActivity.EXTRA_GIFT_DATA, (ArrayList<? extends Parcelable>) mGiftCategoryData);

                if (BusinessConstants.IS_HAVE_GIFT.equals(order.HAVE_GIFT) && order.OrderDetails == null) {
                    order.OrderDetails = new ArrayList<>();
                }
                intent1.putParcelableArrayListExtra(AddGiftActivity.EXTRA_ORDER_DETAIL, (ArrayList<? extends Parcelable>) order.OrderDetails);
                startActivityForResult(intent1, ADDGIFT_REQUESCODE);
                mBiz.setmOrder(order);
            } else {
                ToastUtil.showToastBottom("无赠品可添加！", Toast.LENGTH_SHORT);
            }
        } catch (Exception e) {
            ExceptionUtil.handlerException(e);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        try {
            super.onActivityResult(requestCode, resultCode, data);
            if (requestCode == ADDGIFT_REQUESCODE) {
                if (mGiftData == null) {
                    mGiftData = new ArrayList<>();
                }
                List<PromotionDetail> promotions = mBiz.getPromotions();
                if (promotions != null && promotions.size() > 0) {
                    mGiftData.addAll(promotions);
                }
                if (resultCode == Activity.RESULT_OK) {
                    Toast.makeText(OutPutOrderConfirmActivity.this, "添加赠品成功", Toast.LENGTH_SHORT).show();
                    if (data.hasExtra(AddGiftActivity.EXTRA_RETURN_GIFT_DATA)) {
                        mReturnGiftData = data.getParcelableArrayListExtra(AddGiftActivity.EXTRA_RETURN_GIFT_DATA);
                        if (mReturnGiftData != null && mReturnGiftData.size() > 0) {
                            mGiftData.addAll(mReturnGiftData);
                        }
                        Logger.w("OrderConfirmActivity.选择的赠品：" + mGiftData.toString());
                    }
                }
                mPromotionAdapter.notifyChange(mGiftData);
                mTempTotalQTY = mBiz.getOrder().TOTAL_QTY;
                if (mReturnGiftData != null && mReturnGiftData.size() > 0) {
                    for (PromotionDetail promotionDetail : mReturnGiftData) {
                        mTempTotalQTY += promotionDetail.PO_QTY;
                    }
                }
                mTextViewTotalCount.setText(String.valueOf(mTempTotalQTY));
                if (mGiftData.size() > 0) {
                    mTextViewNoPromotion.setVisibility(View.GONE);
                    mListViewPromotion.setVisibility(View.VISIBLE);
                    mBtAddGift.setText("重新添加");
                } else {
                    mTextViewNoPromotion.setVisibility(View.VISIBLE);
                    mListViewPromotion.setVisibility(View.GONE);
                    mBtAddGift.setText("重新添加");
                }
            }
        } catch (Exception e) {
            ExceptionUtil.handlerException(e);
        }
    }

    /**
     * 产品数据变动是刷新 listView
     */
    private void notifyDataChange() {
        try {
            mProductAdapter.notifyChange(mBiz.getProducts());
            mPromotionAdapter.notifyChange(mBiz.getPromotions());
            mTextViewActPrice.setText(mBiz.getActPrice());
        } catch (Exception e) {
            ExceptionUtil.handlerException(e);
        }
    }


    public void nextOnclick() {
        showLoadingDialog();
        try {
            StringRequest request = new StringRequest(Request.Method.POST, URLCostant.GetVisitRecommendedOrder, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    Logger.w(this.getClass() + ".RecomOrder:" + response);
                    RecomOrderSuccess(response, customerM);
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Logger.w(this.getClass() + ".RecomOrder:" + error.toString());
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
                    params.put("strRecommendedOrder", "");
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

        } catch (Exception e) {
            ExceptionUtil.handlerException(e);
        }
    }

    /**
     * 处理网络请求返回数据成功
     *
     * @param response 返回的数据
     */
    private void RecomOrderSuccess(String response, CustomerMeeting customerM) {
        try {
            JSONObject object = JSON.parseObject(response);
            int type = object.getInteger("type");
            String msg = object.getString("msg");
            mLoadingDialog.dismiss();
            if (type == 1) {

                Intent intent = new Intent(this, CustomerMeetingDisplayActivity.class);
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
}
