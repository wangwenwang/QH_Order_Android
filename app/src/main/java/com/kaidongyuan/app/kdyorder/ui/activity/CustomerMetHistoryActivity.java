package com.kaidongyuan.app.kdyorder.ui.activity;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Parcelable;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.kaidongyuan.app.kdyorder.R;
import com.kaidongyuan.app.kdyorder.adapter.CustomerMetHistoryAdapter;
import com.kaidongyuan.app.kdyorder.adapter.FirstPartyChoiceAdapter;
import com.kaidongyuan.app.kdyorder.adapter.LineChoiceAdapter;
import com.kaidongyuan.app.kdyorder.app.MyApplication;
import com.kaidongyuan.app.kdyorder.bean.CustomerMeeting;
import com.kaidongyuan.app.kdyorder.bean.CustomerMeetingLine;
import com.kaidongyuan.app.kdyorder.bean.FatherAddress;
import com.kaidongyuan.app.kdyorder.bean.OutPutToAddress;
import com.kaidongyuan.app.kdyorder.constants.EXTRAConstants;
import com.kaidongyuan.app.kdyorder.interfaces.OnClickListenerStrInterface;
import com.kaidongyuan.app.kdyorder.model.CustomerMetHistoryActivityBiz;
import com.kaidongyuan.app.kdyorder.util.DateUtil;
import com.kaidongyuan.app.kdyorder.util.DensityUtil;
import com.kaidongyuan.app.kdyorder.util.ExceptionUtil;
import com.kaidongyuan.app.kdyorder.util.ToastUtil;
import com.kaidongyuan.app.kdyorder.util.Tools;
import com.kaidongyuan.app.kdyorder.widget.loadingdialog.MyLoadingDialog;
import com.kaidongyuan.app.kdyorder.widget.xlistview.XListView;

import java.util.List;

public class CustomerMetHistoryActivity extends BaseActivity implements View.OnClickListener, AdapterView.OnItemClickListener, XListView.IXListViewListener, OnClickListenerStrInterface {

    /**
     * 对应业务类
     */
    private CustomerMetHistoryActivityBiz mBiz;

    /**
     * 显示最新资讯的 ListView
     */
    private XListView mXlistViewInformations;
    /**
     * 客户拜访列表 ListView 的 Adapter
     */
    private CustomerMetHistoryAdapter mAdapter;
    /**
     * 没有数据是显示的提示框
     */
    private TextView mTextViewNoData;
    /**
     * 网络请求时显示的 Dialog
     */
    private MyLoadingDialog mLoadingDialog;
    /**
     * 返回上一界面按钮
     */
    private ImageView mImageViewGoBack;
//    private LinearLayout llTimeChoice;
//    private EditText edSearch;
//    private ImageView ivSearch;
    public String strSearch = "";
    public String strPartyCode = "";
    public String strFartherPartyID = "";//供货商ID

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_met_history);
        Log.d("LM", "onCreate: ");

        try {
            initData();
            setTop();
            initView();
            setListener();

            strFartherPartyID = getIntent().getStringExtra("strFartherPartyID");
            strPartyCode = getIntent().getStringExtra("strPartyCode");

            if (mBiz.reFreshCustomerMeetingDatas()) {
                showLoadingDialog();
            }

            Log.d("LM", "onCreate: ");
        } catch (Exception e) {
            ExceptionUtil.handlerException(e);
        }
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        Log.d("LM", "onRestart: ");
        mBiz.reFreshCustomerMeetingDatas();
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.d("LM", "onStart: ");
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d("LM", "onResume: ");


    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d("LM", "onPause: ");
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.d("LM", "onStop: ");
    }

    @Override
    protected void onDestroy() {
        Log.d("LM", "onDestroy: ");
        try {
            super.onDestroy();
            if (mLoadingDialog != null && mLoadingDialog.isShowing()) {
                mLoadingDialog.dismiss();
            }
            mLoadingDialog = null;
            mBiz.cancelRequest();
            mBiz = null;
            mXlistViewInformations = null;
            mAdapter = null;
            mTextViewNoData = null;
            mImageViewGoBack = null;
        } catch (Exception e) {
            ExceptionUtil.handlerException(e);
        }
    }

    private void initData() {
        mBiz = new CustomerMetHistoryActivityBiz(this);
    }

    private void setTop() {
        //版本4.4以上设置状态栏透明，界面布满整个界面
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Window window = getWindow();
            window.setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS, WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            View topView = findViewById(R.id.topview);
            ViewGroup.LayoutParams topParams = topView.getLayoutParams();
            topParams.height = DensityUtil.getStatusHeight() * 16 / 30;
            topView.setLayoutParams(topParams);
        }
    }

    private void initView() {
        try {
//            llTimeChoice = (LinearLayout) findViewById(R.id.ll_time_choice);
//            edSearch = (EditText) findViewById(R.id.ed_search);
//            ivSearch = (ImageView) findViewById(R.id.iv_search);
            this.mXlistViewInformations = (XListView) this.findViewById(R.id.lv_customer_met_history);
            mXlistViewInformations.setPullLoadEnable(false);
            mXlistViewInformations.setPullRefreshEnable(false);
            mAdapter = new CustomerMetHistoryAdapter(this, null);
            mXlistViewInformations.setAdapter(mAdapter);
            this.mTextViewNoData = (TextView) this.findViewById(R.id.textview_nodata);
            this.mImageViewGoBack = (ImageView) this.findViewById(R.id.button_goback);
        } catch (Exception e) {
            ExceptionUtil.handlerException(e);
        }
    }

    private void setListener() {
        try {
            mImageViewGoBack.setOnClickListener(this);
            mAdapter.setOnClickListenerStr(this);
//            llTimeChoice.setOnClickListener(this);
//            edSearch.addTextChangedListener(new TextWatcher() {
//                @Override
//                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
//
//                }
//
//                @Override
//                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
//                    strSearch = charSequence.toString().trim();
//                }
//
//                @Override
//                public void afterTextChanged(Editable editable) {
//
//                }
//            });
//            ivSearch.setOnClickListener(this);
            mXlistViewInformations.setPullRefreshEnable(false);
            mXlistViewInformations.setPullLoadEnable(false);
            mXlistViewInformations.setXListViewListener(this);
            mXlistViewInformations.setOnItemClickListener(this);
        } catch (Exception e) {
            ExceptionUtil.handlerException(e);
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
     * 获取客户拜访列表失败
     *
     * @param message 要显示的消息
     */
    public void getMeetingsDataError(String message) {
        try {
            // 不提示 没有数据 4个字
            if (!message.equals("没有数据")) {
                ToastUtil.showToastBottom(String.valueOf(message), Toast.LENGTH_SHORT);
            }
            handleGetCustomerMeeingsData();
        } catch (Exception e) {
            ExceptionUtil.handlerException(e);
        }
    }

    @Override
    public void onRefresh() {
        showLoadingDialog();
        mBiz.reFreshCustomerMeetingDatas();
    }

    @Override
    public void onLoadMore() {
        showLoadingDialog();
        mBiz.loadMoreCustomerMeetingDatas();
    }

    @Override
    public boolean onClick(int position, String... tags) {
        if (tags != null && tags.length > 0) {
            switch (tags[0]) {
                case "tv_read":
                    Intent intent1 = new Intent(this, CustomerMeetingShowStepActivity.class);
                    intent1.putExtra("CustomerMeeting", mBiz.getCustomerMeetingList().get(position));
                    intent1.putExtra("isShowStep", true);
                    startActivity(intent1);
                    break;
                case "tv_edit":
                    visits(position);
                    break;
                case "tv_show":
                    showVisit(position);
                    break;
                default:
                    break;
            }
        }
        return true;
    }

    /**
     * 获取客户拜访列表成功
     */
    public void getMeetingsDataSuccess() {
        handleGetCustomerMeeingsData();
    }

    /**
     * 处理网络请求结果
     */
    private void handleGetCustomerMeeingsData() {
        try {
            mXlistViewInformations.stopRefresh();
            mXlistViewInformations.stopLoadMore();
            mLoadingDialog.dismiss();
            List<CustomerMeeting> customerMeetings = mBiz.getCustomerMeetingList();
            if (customerMeetings == null || customerMeetings.size() <= 0) {

                mTextViewNoData.setVisibility(View.VISIBLE);
            } else {
                mTextViewNoData.setVisibility(View.GONE);
                if (customerMeetings.size() < 20) {
                    mXlistViewInformations.setFooterViewTxt("无更多数据");
                }
            }
            mAdapter.notifyChange(customerMeetings);
        } catch (Exception e) {
            ExceptionUtil.handlerException(e);
        }
    }

    @Override
    public void onClick(View v) {
        try {
            switch (v.getId()) {
                case R.id.button_goback:
                    this.finish();
                    break;
//                case R.id.ll_time_choice:
//                    ToastUtil.showToastBottom(String.valueOf("维护中..."), Toast.LENGTH_SHORT);
//                    break;
//                case R.id.iv_search:
//                    showLoadingDialog();
//                    mBiz.reFreshCustomerMeetingDatas();
//                    break;
                default:
                    break;
            }
        } catch (Exception e) {
            ExceptionUtil.handlerException(e);
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

        showVisit(position - 1);
    }

    private void showVisit(int position) {

        CustomerMeeting customerM = mBiz.getCustomerMeetingList().get(position);
        Intent intent = new Intent(this, CustomerMeetingShowStepActivity.class);
        intent.putExtra("CustomerMeeting", customerM);
        intent.putExtra("isShowStep", true);
        startActivity(intent);
    }


    private void visits(int position) {

        try {
            CustomerMeeting customerM = mBiz.getCustomerMeetingList().get(position);
            String status = customerM.getVISIT_STATES();
            if (status.equals("")) {

                Intent intent = new Intent(CustomerMetHistoryActivity.this, CustomerMeetingCreateActivity.class);
                intent.putExtra("CustomerMeeting", customerM);
                startActivity(intent);
            } else if (status.equals("新建") || status.equals("确认客户信息")) {

                Intent intent = new Intent(CustomerMetHistoryActivity.this, ArrivedStoreActivity.class);
                intent.putExtra("CustomerMeeting", customerM);
                startActivity(intent);
            } else if (status.equals("进店")) {

                Intent intent = new Intent(CustomerMetHistoryActivity.this, CustomerMeetingCheckInventoryActivity.class);
                intent.putExtra("CustomerMeeting", customerM);
                startActivity(intent);
            } else if (status.equals("检查库存")) {

//                Intent intent = new Intent(this, CustomerMeetingRecomOrderActivity.class);
//                intent.putExtra("CustomerMeeting", customerM);
//                startActivity(intent);

                Tools.GetFatherAddress(customerM.getADDRESS_IDX(), this, position);
            } else if (status.equals("建议订单")) {

                Intent intent = new Intent(this, CustomerMeetingDisplayActivity.class);
                intent.putExtra("CustomerMeeting", customerM);
                startActivity(intent);
            } else if (status.equals("生动化陈列")) {

                Intent intent = new Intent(this, CustomerMeetingShowStepActivity.class);
                intent.putExtra("CustomerMeeting", customerM);
                intent.putExtra("isShowStep", false);
                startActivity(intent);
            } else if (status.equals("离店")) {

                Intent intent = new Intent(this, CustomerMeetingShowStepActivity.class);
                intent.putExtra("CustomerMeeting", customerM);
                intent.putExtra("isShowStep", true);
                startActivity(intent);
            }
        } catch (Exception e) {
            ExceptionUtil.handlerException(e);
        }
    }

    public void GetVisitAppOrderSuccess(FatherAddress FM, int position) {

        CustomerMeeting customerM = mBiz.getCustomerMeetingList().get(position);

        // 建议订单
        if (FM != null) {
            // 收货信息
            OutPutToAddress OT = new OutPutToAddress();
            OT.setADDRESS_INFO(customerM.getPARTY_ADDRESS());
            OT.setCONTACT_PERSON(customerM.getCONTACTS());
            OT.setCONTACT_TEL(customerM.getCONTACTS_TEL());
            OT.setIDX(customerM.getADDRESS_IDX());
            OT.setITEM_CODE(customerM.getPARTY_NO());
            OT.setPARTY_NAME(customerM.getPARTY_NAME());

            // 发货信息
            Intent intent4 = new Intent(this, OutputInventoryActivity.class);
            intent4.putExtra(EXTRAConstants.ORDER_PARTY_ID, FM.getIDX());
            intent4.putExtra(EXTRAConstants.ORDER_PARTY_NO, FM.getPARTY_CODE());
            intent4.putExtra(EXTRAConstants.ORDER_PARTY_NAME, FM.getPARTY_NAME());
            intent4.putExtra(EXTRAConstants.INVENTORY_PARTY_CITY, FM.getPARTY_CITY());
            intent4.putExtra(EXTRAConstants.ORDER_PARTY_ADDRESS_IDX, FM.getADDRESS_IDX());
            intent4.putExtra(EXTRAConstants.ORDER_ADDRESS_CODE, FM.getADDRESS_CODE());
            intent4.putExtra(EXTRAConstants.ORDER_ADDRESS_INFORMATION, FM.getADDRESS_INFO());
            intent4.putExtra(EXTRAConstants.ORDER_ADDRESS_ContactPerson, FM.getCONTACT_PERSON());
            intent4.putExtra(EXTRAConstants.ORDER_ADDRESS_ContactTel, FM.getCONTACT_TEL());
            intent4.putExtra(EXTRAConstants.OUTPUT_ORDER_TYPE, "output_visit_sale");
            intent4.putExtra("OutPutToAddress", (Parcelable) OT);
            intent4.putExtra("CustomerMeeting", (Parcelable) customerM);
            intent4.putExtra(EXTRAConstants.OUTPUT_VISIT_IDX, customerM.getVISIT_IDX());

            startActivity(intent4);
        } else {
            ToastUtil.showToastBottom(String.valueOf("找不到上级"), Toast.LENGTH_SHORT);
        }
    }

    public void GetFatherAddressError(String msg) {


    }
}