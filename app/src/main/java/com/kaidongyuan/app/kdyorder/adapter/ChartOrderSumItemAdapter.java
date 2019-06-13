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
import com.kaidongyuan.app.kdyorder.bean.ChartOrderSumItem;
import com.kaidongyuan.app.kdyorder.interfaces.OnClickListenerStrInterface;

import java.util.ArrayList;
import java.util.List;

public class ChartOrderSumItemAdapter  extends BaseAdapter {

    private Context mContext;
    List<ChartOrderSumItem> mChartOrderSumItems;
    OnClickListenerStrInterface onClickListenerStr;

    public ChartOrderSumItemAdapter(Context mContext, List<ChartOrderSumItem> mChartOrderSumItems) {
        this.mContext = mContext;
        this.mChartOrderSumItems = mChartOrderSumItems == null ? new ArrayList<ChartOrderSumItem>() : mChartOrderSumItems;
    }

    public void setOnClickListenerStr(OnClickListenerStrInterface onClickListenerStr) {
        this.onClickListenerStr = onClickListenerStr;
    }

    public void notifyChange(List<ChartOrderSumItem> mChartOrderSumItems) {
        this.mChartOrderSumItems = mChartOrderSumItems == null ? new ArrayList<ChartOrderSumItem>() : mChartOrderSumItems;
        this.notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return mChartOrderSumItems.size();
    }

    @Override
    public Object getItem(int position) {
        return mChartOrderSumItems.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @SuppressLint("SetTextI18n")
    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        final ChartOrderSumItemAdapter.ViewHolder holder;
        final ChartOrderSumItem chartOrderSumItem = mChartOrderSumItems.get(position);
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.activity_chart_order_sum_item_cell, null);
            holder = new ChartOrderSumItemAdapter.ViewHolder();
            holder.PRODUCT_NO = (TextView) convertView.findViewById(R.id.PRODUCT_NO);
            holder.PRODUCT_NAME = (TextView) convertView.findViewById(R.id.PRODUCT_NAME);
            holder.NUMBER = (TextView) convertView.findViewById(R.id.NUMBER);
            holder.AMOUNT_MONEY = (TextView) convertView.findViewById(R.id.AMOUNT_MONEY);
            holder.ll_cell = (LinearLayout) convertView.findViewById(R.id.ll_cell);
            convertView.setTag(holder);
        } else {
            holder = (ChartOrderSumItemAdapter.ViewHolder) convertView.getTag();
        }
        holder.PRODUCT_NAME.setText(chartOrderSumItem.getPRODUCT_NAME());
        holder.NUMBER.setText(chartOrderSumItem.getNUMBER());
        holder.AMOUNT_MONEY.setText(chartOrderSumItem.getAMOUNT_MONEY());
        return convertView;
    }

    class ViewHolder {

        TextView PRODUCT_NO, PRODUCT_NAME, NUMBER, AMOUNT_MONEY;
        LinearLayout ll_cell;
    }
}