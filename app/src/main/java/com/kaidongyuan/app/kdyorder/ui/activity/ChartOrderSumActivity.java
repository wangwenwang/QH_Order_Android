package com.kaidongyuan.app.kdyorder.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bigkoo.alertview.AlertView;
import com.bigkoo.alertview.OnItemClickListener;
import com.kaidongyuan.app.kdyorder.R;
import com.kaidongyuan.app.kdyorder.adapter.ChartOrderSumAdapter;
import com.kaidongyuan.app.kdyorder.bean.ChartOrderSum;
import com.kaidongyuan.app.kdyorder.interfaces.OnClickListenerStrInterface;
import com.kaidongyuan.app.kdyorder.model.ChartOrderSumActivityBiz;
import com.kaidongyuan.app.kdyorder.util.ExceptionUtil;
import com.kaidongyuan.app.kdyorder.util.ToastUtil;
import com.kaidongyuan.app.kdyorder.widget.loadingdialog.MyLoadingDialog;
import com.kaidongyuan.app.kdyorder.widget.xlistview.XListView;

import java.util.List;

public class ChartOrderSumActivity extends BaseActivity implements View.OnClickListener, AdapterView.OnItemClickListener, OnClickListenerStrInterface {


    private ChartOrderSumActivityBiz mBiz;

    /**
     * 网络请求时显示的 Dialog
     */
    private MyLoadingDialog mLoadingDialog;


    /**
     * 显示最新资讯的 ListView
     */
    private XListView mXlistViewChartOrderSums;

    /**
     * 没有数据是显示的提示框
     */
    private TextView mTextViewNoData;

    /**
     * 报表列表 ListView 的 Adapter
     */
    private ChartOrderSumAdapter mAdapter;

    /**
     * 返回上一界面按钮
     */
    private ImageView mImageViewGoBack;

    private LinearLayout llOrderType, llOrderDate;

    private TextView tvOrderType, tvOrderDate;

    private String[] orderTypes = {"出库", "入库", "取消"};

    private String[] orderTypes_value = {"OUT", "INPUT"};

    private int orderTypes_index = 0;

    private String[] orderDates = {"全部", "今天", "本周", "本月", "本季", "本年", "取消"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chart_order_sum);
        try {
            initData();
            initView();
            setListener();
            showLoadingDialog();
            mBiz.GetDatas("OUT", "全部");
        } catch (Exception e) {
            ExceptionUtil.handlerException(e);
        }
        tvOrderType.setText(orderTypes[0]);
        tvOrderDate.setText(orderDates[0]);
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
            mImageViewGoBack = null;
            llOrderType = null;
            llOrderDate = null;
            tvOrderType = null;
            tvOrderDate = null;
            mAdapter = null;
            mTextViewNoData = null;
            mXlistViewChartOrderSums = null;
        } catch (Exception e) {
            ExceptionUtil.handlerException(e);
        }
    }

    private void initData() {
        mBiz = new ChartOrderSumActivityBiz(this);
    }

    @Override
    public void onClick(View v) {
        try {
            switch (v.getId()) {
                case R.id.button_goback:
                    this.finish();
                    break;
                case R.id.ll_order_type:
                    orderTypeAlertShow();
                    break;
                case R.id.ll_order_date:
                    orderDateAlertShow();
                    break;
            }
        } catch (Exception e) {
            ExceptionUtil.handlerException(e);
        }
    }

    private void initView() {
        try {
            mImageViewGoBack = (ImageView) findViewById(R.id.button_goback);
            llOrderType = (LinearLayout) findViewById(R.id.ll_order_type);
            llOrderDate = (LinearLayout) findViewById(R.id.ll_order_date);
            tvOrderType = (TextView) findViewById(R.id.tv_order_type);
            tvOrderDate = (TextView) findViewById(R.id.tv_order_date);
            this.mXlistViewChartOrderSums = (XListView) this.findViewById(R.id.lv_chart_order_sum);
            mAdapter = new ChartOrderSumAdapter(this, null);
            mXlistViewChartOrderSums.setAdapter(mAdapter);
            mTextViewNoData = (TextView) findViewById(R.id.textview_nodata);
        } catch (Exception e) {
            ExceptionUtil.handlerException(e);
        }
    }

    private void setListener() {
        try {
            mImageViewGoBack.setOnClickListener(this);
            llOrderType.setOnClickListener(this);
            llOrderDate.setOnClickListener(this);
            mAdapter.setOnClickListenerStr(this);
            mXlistViewChartOrderSums.setPullLoadEnable(false);
            mXlistViewChartOrderSums.setPullRefreshEnable(false);
            mXlistViewChartOrderSums.setOnItemClickListener(this);
        } catch (Exception e) {
            ExceptionUtil.handlerException(e);
        }
    }


    public void orderTypeAlertShow() {
        new AlertView("类型", null, null,
                new String[]{},
                orderTypes,
                this, AlertView.Style.Alert, new OnItemClickListener() {
            @Override
            public void onItemClick(Object o, int position) {

                if (!orderTypes[position].equals("取消")) {
                    tvOrderType.setText(orderTypes[position]);
                    orderTypes_index = position;
                    mBiz.GetDatas(orderTypes_value[position], (String) tvOrderDate.getText());
                }
            }
        }).show();
    }

    public void orderDateAlertShow() {
        new AlertView("日期", null, null,
                new String[]{},
                orderDates,
                this, AlertView.Style.Alert, new OnItemClickListener() {
            @Override
            public void onItemClick(Object o, int position) {

                if (!orderDates[position].equals("取消")) {
                    tvOrderDate.setText(orderDates[position]);
                    mBiz.GetDatas(orderTypes_value[orderTypes_index], orderDates[position]);
                }
            }
        }).show();
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

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

        ChartOrderSum chartOrderSum = mBiz.getChartOrderSumList().get(position - 1);
        Intent intent = new Intent(this, ChartOrderSumItemActivity.class);
        intent.putExtra("PARTY_CODE", chartOrderSum.getPARTY_CODE());
        startActivity(intent);
    }

    /**
     * 获取客户拜访列表失败
     *
     * @param message 要显示的消息
     */
    public void getDataError(String message) {
        try {
            mLoadingDialog.dismiss();
            // 不提示 没有数据 4个字
            if (!message.equals("没有数据")) {
                ToastUtil.showToastBottom(String.valueOf(message), Toast.LENGTH_SHORT);
            }
        } catch (Exception e) {
            ExceptionUtil.handlerException(e);
        }
    }

    /**
     * 获取客户拜访列表成功
     */
    public void getDataSuccess() {

        try {

            mLoadingDialog.dismiss();
            List<ChartOrderSum> chartOrderSumList = mBiz.getChartOrderSumList();
            if (chartOrderSumList == null || chartOrderSumList.size() <= 0) {
                mTextViewNoData.setVisibility(View.VISIBLE);
            } else {
                mTextViewNoData.setVisibility(View.GONE);
            }
            mAdapter.notifyChange(chartOrderSumList);
        } catch (Exception e) {
            ExceptionUtil.handlerException(e);
        }
    }

    @Override
    public boolean onClick(int position, String... tags) {
        return true;
    }
}