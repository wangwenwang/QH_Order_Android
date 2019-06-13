package com.kaidongyuan.app.kdyorder.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.kaidongyuan.app.kdyorder.R;
import com.kaidongyuan.app.kdyorder.util.ExceptionUtil;
import com.kaidongyuan.app.kdyorder.util.ToastUtil;
import com.zhy.android.percent.support.PercentLinearLayout;

public class ChartCenterActivity extends BaseActivity implements View.OnClickListener {

    /**
     * 标题返回上一个界面按钮
     */
    private ImageView mImageViewGoBack;

    /**
     * 客户、销量报表
     */
    private PercentLinearLayout mPercentllCustomerSale;

    /**
     * 订单总计报表
     */
    private PercentLinearLayout mPercentllOrderSum;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chart_center);
        try {
            initView();
            setListener();
        } catch (Exception e) {
            ExceptionUtil.handlerException(e);
        }
    }

    @Override
    protected void onDestroy() {
        try {
            super.onDestroy();
            mPercentllCustomerSale = null;
            mPercentllOrderSum = null;
            mImageViewGoBack = null;
        } catch (Exception e) {
            ExceptionUtil.handlerException(e);
        }
    }

    private void initView() {
        try {
            this.mImageViewGoBack = (ImageView) this.findViewById(R.id.button_goback);
            this.mPercentllCustomerSale = (PercentLinearLayout) this.findViewById(R.id.rl_customer_sale);
            this.mPercentllOrderSum = (PercentLinearLayout) this.findViewById(R.id.rl_order_sum);
        } catch (Exception e) {
            ExceptionUtil.handlerException(e);
        }
    }

    private void setListener() {
        try {
            mImageViewGoBack.setOnClickListener(this);
            mPercentllCustomerSale.setOnClickListener(this);
            mPercentllOrderSum.setOnClickListener(this);
        } catch (Exception e) {
            ExceptionUtil.handlerException(e);
        }
    }

    @Override
    public void onClick(View v) {
        try {
            switch (v.getId()) {
                case R.id.button_goback:     // 返回上一个界面
                    this.finish();
                    break;
                case R.id.rl_customer_sale:  // 客户、销量报表
                    Intent chartCheckIntent = new Intent(this, ChartCheckActivity.class);
                    startActivity(chartCheckIntent);
                    break;
                case R.id.rl_order_sum:  // 客户订单总计
                    Intent chartOrderSumIntent = new Intent(this, ChartOrderSumActivity.class);
                    startActivity(chartOrderSumIntent);
                    break;
            }
        } catch (Exception e) {
            ExceptionUtil.handlerException(e);
        }
    }
}