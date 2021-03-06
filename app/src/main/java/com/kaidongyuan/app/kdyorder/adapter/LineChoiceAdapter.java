package com.kaidongyuan.app.kdyorder.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.kaidongyuan.app.kdyorder.R;
import com.kaidongyuan.app.kdyorder.app.MyApplication;
import com.kaidongyuan.app.kdyorder.bean.CustomerMeetingLine;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2016/5/17.
 * 客户拜访线路业务类型 Adapter
 */
public class LineChoiceAdapter extends BaseAdapter {

    private List<CustomerMeetingLine> mData;
    private Context mContext;

    public LineChoiceAdapter(List<CustomerMeetingLine> data, Context context) {
        this.mData = data==null? new ArrayList<CustomerMeetingLine>():data;
        this.mContext = context;
    }

    /**
     * 刷新 ListView
     * @param data 数据
     */
    public void notifyChange(List<CustomerMeetingLine> data) {
        this.mData = data==null? new ArrayList<CustomerMeetingLine>():data;
    }

    @Override
    public int getCount() {
        return mData.size();
    }

    @Override
    public CustomerMeetingLine getItem(int position) {
        return mData.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView==null) {
            holder = new ViewHolder();
            convertView = View.inflate(mContext, R.layout.item_business_listview, null);
            holder.tvChartName = (TextView) convertView.findViewById(R.id.textView_bussinessName);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        String chartName = mData.get(position).getITEM_NAME();
        if (TextUtils.isEmpty(chartName)) {
            chartName = MyApplication.getmRes().getString(R.string.no_set);
        }
        holder.tvChartName.setText(chartName);

        return convertView;
    }

    private class ViewHolder {
        TextView tvChartName;
    }
}



















