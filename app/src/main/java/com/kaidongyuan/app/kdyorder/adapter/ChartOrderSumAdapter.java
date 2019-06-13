package com.kaidongyuan.app.kdyorder.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.kaidongyuan.app.kdyorder.R;
import com.kaidongyuan.app.kdyorder.bean.ChartOrderSum;
import com.kaidongyuan.app.kdyorder.interfaces.OnClickListenerStrInterface;

import java.util.ArrayList;
import java.util.List;

/**
 * 客户拜访列表adapter
 */
public class ChartOrderSumAdapter extends BaseAdapter {

    private Context mContext;
    List<ChartOrderSum> mChartOrderSums;
    OnClickListenerStrInterface onClickListenerStr;

    public ChartOrderSumAdapter(Context mContext, List<ChartOrderSum> mChartOrderSums) {
        this.mContext = mContext;
        this.mChartOrderSums = mChartOrderSums == null ? new ArrayList<ChartOrderSum>() : mChartOrderSums;
    }

    public void setOnClickListenerStr(OnClickListenerStrInterface onClickListenerStr) {
        this.onClickListenerStr = onClickListenerStr;
    }

    public void notifyChange(List<ChartOrderSum> mChartOrderSums) {
        this.mChartOrderSums = mChartOrderSums == null ? new ArrayList<ChartOrderSum>() : mChartOrderSums;
        this.notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return mChartOrderSums.size();
    }

    @Override
    public Object getItem(int position) {
        return mChartOrderSums.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @SuppressLint("SetTextI18n")
    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        final ViewHolder holder;
        final ChartOrderSum chartOrderSum = mChartOrderSums.get(position);
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.activity_chart_order_sum_cell, null);
            holder = new ViewHolder();
            holder.PARTY_NAME = (TextView) convertView.findViewById(R.id.PARTY_NAME);
            holder.ORDER_NUMBER = (TextView) convertView.findViewById(R.id.ORDER_NUMBER);
            holder.NUMBER = (TextView) convertView.findViewById(R.id.NUMBER);
            holder.AMOUNT_MONEY = (TextView) convertView.findViewById(R.id.AMOUNT_MONEY);
            holder.ll_cell = (LinearLayout) convertView.findViewById(R.id.ll_cell);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        holder.PARTY_NAME.setText(chartOrderSum.getPARTY_NAME());
        holder.ORDER_NUMBER.setText(chartOrderSum.getORDER_NUMBER());
        holder.NUMBER.setText(chartOrderSum.getNUMBER());
        holder.AMOUNT_MONEY.setText(chartOrderSum.getAMOUNT_MONEY());
        return convertView;
    }

    class ViewHolder {

        TextView PARTY_NAME, ORDER_NUMBER, NUMBER, AMOUNT_MONEY;
        LinearLayout ll_cell;
    }
}