package com.kaidongyuan.app.kdyorder.ui.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.kaidongyuan.app.kdyorder.R;
import com.kaidongyuan.app.kdyorder.bean.KpiTrack;
import com.kaidongyuan.app.kdyorder.model.KpiTrackActivityBiz;
import com.kaidongyuan.app.kdyorder.util.ExceptionUtil;
import com.kaidongyuan.app.kdyorder.util.ToastUtil;
import com.kaidongyuan.app.kdyorder.widget.loadingdialog.MyLoadingDialog;

import java.text.DecimalFormat;

public class KpiTrackActivity extends BaseActivity implements View.OnClickListener {

    /**
     * 返回上一界面按钮
     */
    private ImageView mImageViewGoBack;

    /**
     * 对应业务类
     */
    private KpiTrackActivityBiz mBiz;

    /**
     * 网络请求时显示的 Dialog
     */
    private MyLoadingDialog mLoadingDialog;

    private TextView kpiQtyTotal;
    private TextView kpiQtyFinish;
    private TextView kpiQtyFinishPercent;

    private TextView kpiAmountTotal;
    private TextView kpiAmountFinish;
    private TextView kpiAmountFinishPercent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_kpi_track);

        initData();
        initView();
        setListener();
        showLoadingDialog();
        mBiz.GetTargetByUserIdx();
    }

    private void initData () {

        mBiz = new KpiTrackActivityBiz(this);
    }

    private void initView() {

        mImageViewGoBack = (ImageView) this.findViewById(R.id.button_goback);
        kpiQtyTotal = (TextView) this.findViewById(R.id.kpi_qty_total);
        kpiQtyFinish = (TextView) this.findViewById(R.id.kpi_qty_finish);
        kpiQtyFinishPercent = (TextView) this.findViewById(R.id.kpi_qty_finishPercent);
        kpiAmountTotal = (TextView) this.findViewById(R.id.kpi_amount_total);
        kpiAmountFinish = (TextView) this.findViewById(R.id.kpi_amount_finish);
        kpiAmountFinishPercent = (TextView) this.findViewById(R.id.kpi_amount_finishPercent);

        kpiQtyTotal.setText("");
        kpiQtyFinish.setText("");
        kpiQtyFinishPercent.setText("");
        kpiAmountTotal.setText("");
        kpiAmountFinish.setText("");
        kpiAmountFinishPercent.setText("");
    }

    private void fullData(KpiTrack kpiM) {

        DecimalFormat df = new DecimalFormat("#0.00");
        DecimalFormat df2 = new DecimalFormat("#0");
        float kpiQtyTotal_f = Float.parseFloat(kpiM.getSalesVolume());
        float kpiQtyFinish_f = Float.parseFloat(kpiM.getCompleteSalesVolume());
        String kpiQtyFinishPer = df.format(kpiQtyFinish_f / kpiQtyTotal_f);
        float fQtyPre = Float.parseFloat(kpiQtyFinishPer);
        String sQtyPre = df2.format(fQtyPre * 100) + "%";

        float kpiAmountTotal_f = Float.parseFloat(kpiM.getAmountMoney());
        float kpiAmountFinish_f = Float.parseFloat(kpiM.getCompleteAmountMoney());
        String kpiAmountFinishPer = df.format(kpiAmountFinish_f / kpiAmountTotal_f);
        float fAmountPre = Float.parseFloat(kpiAmountFinishPer);
        String sAmountPre = df2.format(fAmountPre * 100) + "%";

        kpiQtyTotal.setText(kpiM.getSalesVolume() + "件");
        kpiQtyFinish.setText(kpiM.getCompleteSalesVolume() + "件");
        kpiQtyFinishPercent.setText(sQtyPre);

        kpiAmountTotal.setText(kpiM.getAmountMoney() + "元");
        kpiAmountFinish.setText(kpiM.getCompleteAmountMoney() + "元");
        kpiAmountFinishPercent.setText(sAmountPre);
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
    protected void onDestroy() {
        super.onDestroy();
        try {
            super.onDestroy();
            if (mLoadingDialog != null && mLoadingDialog.isShowing()) {
                mLoadingDialog.dismiss();
            }
            mLoadingDialog = null;
            mImageViewGoBack = null;
            mBiz.cancelRequest();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 网络请求成功时调用
     */
    public void KpiTrackActivityBizSuccess() {
        try {
            mLoadingDialog.dismiss();
            fullData(mBiz.getKpiTrack());
        } catch (Exception e) {
            ExceptionUtil.handlerException(e);
        }
    }

    public void KpiTrackActivityBizError(String msg) {

        if (mLoadingDialog != null) mLoadingDialog.dismiss();
        try {
            ToastUtil.showToastBottom(String.valueOf(msg), Toast.LENGTH_SHORT);
        } catch (Exception e) {
            ExceptionUtil.handlerException(e);
        }
    }

    private void setListener() {
        try {
            mImageViewGoBack.setOnClickListener(this);
        } catch (Exception e) {
            ExceptionUtil.handlerException(e);
        }
    }

    @Override
    public void onClick(View v) {
        try {
            switch (v.getId()) {
                case R.id.button_goback://返回上一界面
                    this.finish();
                    break;
            }
        } catch (Exception e) {
            ExceptionUtil.handlerException(e);
        }
    }
}