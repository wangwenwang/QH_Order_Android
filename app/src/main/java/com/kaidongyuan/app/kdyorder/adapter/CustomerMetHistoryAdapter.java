package com.kaidongyuan.app.kdyorder.adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.kaidongyuan.app.kdyorder.R;
import com.kaidongyuan.app.kdyorder.bean.CustomerMeeting;
import com.kaidongyuan.app.kdyorder.interfaces.OnClickListenerStrInterface;
import com.kaidongyuan.app.kdyorder.util.Tools;

import java.util.ArrayList;
import java.util.List;

/**
 * 客户拜访列表adapter
 */
public class CustomerMetHistoryAdapter extends BaseAdapter {

    private Context mContext;
    List<CustomerMeeting> mMeetings;
    OnClickListenerStrInterface onClickListenerStr;

    public CustomerMetHistoryAdapter(Context mContext, List<CustomerMeeting> mMeetings) {
        this.mContext = mContext;
        this.mMeetings = mMeetings == null ? new ArrayList<CustomerMeeting>() : mMeetings;
    }

    public void setOnClickListenerStr(OnClickListenerStrInterface onClickListenerStr) {
        this.onClickListenerStr = onClickListenerStr;
    }

    public void notifyChange(List<CustomerMeeting> mMeetings) {
        this.mMeetings = mMeetings == null ? new ArrayList<CustomerMeeting>() : mMeetings;
        this.notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return mMeetings.size();
    }

    @Override
    public Object getItem(int position) {
        return mMeetings.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        final ViewHolder holder;
        final CustomerMeeting customerMeeting = mMeetings.get(position);
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.item_customer_met_history, null);
            holder = new ViewHolder();
            holder.tv_name = (TextView) convertView.findViewById(R.id.tv_name);
            holder.tv_phone = (TextView) convertView.findViewById(R.id.tv_phone);
            holder.tv_time = (TextView) convertView.findViewById(R.id.tv_time);
            holder.tv_customer_name = (TextView) convertView.findViewById(R.id.tv_customer_name);
            holder.tv_edit = (TextView) convertView.findViewById(R.id.tv_edit);
            holder.tv_show = (TextView) convertView.findViewById(R.id.tv_show);
            holder.tv_visited = (TextView) convertView.findViewById(R.id.tv_visited);
            holder.tv_visit_status = (TextView) convertView.findViewById(R.id.tv_visit_status);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        holder.tv_time.setText("拜访时间：" + customerMeeting.getVISIT_DATE());
        holder.tv_customer_name.setText(customerMeeting.getPARTY_NAME());

        if (Tools.getVISIT_STATES(customerMeeting.getVISIT_STATES()).equals("未拜访")) {

            holder.tv_visit_status.setText("未拜访");
            holder.tv_visit_status.setTextColor(Color.parseColor("#de443c"));
            holder.tv_visit_status.setVisibility(View.GONE);
            holder.tv_edit.setText("拜访");
            holder.tv_edit.setVisibility(View.VISIBLE);
        } else if (Tools.getVISIT_STATES(customerMeeting.getVISIT_STATES()).equals("拜访中")) {

            holder.tv_visit_status.setText("继续拜访");
            holder.tv_visit_status.setTextColor(Color.parseColor("#717171"));
            holder.tv_visit_status.setVisibility(View.GONE);
            holder.tv_edit.setVisibility(View.VISIBLE);
        } else if (Tools.getVISIT_STATES(customerMeeting.getVISIT_STATES()).equals("已拜访")) {

            holder.tv_visit_status.setText("已拜访");
            holder.tv_visit_status.setTextColor(Color.parseColor("#3c7143"));
            holder.tv_visit_status.setVisibility(View.VISIBLE);
            holder.tv_edit.setVisibility(View.GONE);
        }
        holder.tv_edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onClickListenerStr.onClick(position, "tv_edit");
            }
        });
        holder.tv_show.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onClickListenerStr.onClick(position, "tv_show");
            }
        });
        return convertView;
    }

    class ViewHolder {

        TextView tv_name, tv_phone, tv_time, tv_customer_name, tv_edit, tv_visited, tv_visit_status, tv_show;
    }
}