package com.kaidongyuan.app.kdyorder.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.kaidongyuan.app.kdyorder.R;
import com.kaidongyuan.app.kdyorder.bean.CustomerMeeting;
import com.kaidongyuan.app.kdyorder.bean.Information;
import com.kaidongyuan.app.kdyorder.constants.URLCostant;
import com.kaidongyuan.app.kdyorder.interfaces.OnClickListenerStrInterface;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

/**
 * 客户拜访列表adapter
 */
public class CustomerMeetingAdapter extends BaseAdapter {

    private Context mContext;
    List<CustomerMeeting> mMeetings;
    OnClickListenerStrInterface onClickListenerStr;

    public CustomerMeetingAdapter(Context mContext, List<CustomerMeeting> mMeetings) {
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
            convertView = LayoutInflater.from(mContext).inflate(R.layout.item_customer_meeting, null);
            holder = new ViewHolder();
            holder.tv_name = (TextView) convertView.findViewById(R.id.tv_name);
            holder.tv_phone = (TextView) convertView.findViewById(R.id.tv_phone);
            holder.tv_time = (TextView) convertView.findViewById(R.id.tv_time);
            holder.tv_customer_name = (TextView) convertView.findViewById(R.id.tv_customer_name);
            holder.tv_customer_address = (TextView) convertView.findViewById(R.id.tv_customer_address);
            holder.tv_visit = (TextView) convertView.findViewById(R.id.tv_visit);
            holder.tv_visited = (TextView) convertView.findViewById(R.id.tv_visited);
            holder.tv_history = (TextView) convertView.findViewById(R.id.tv_history);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        holder.tv_time.setText("上次拜访时间：" + customerMeeting.getVISIT_DATE());
        holder.tv_customer_name.setText(customerMeeting.getPARTY_NAME());

        if (customerMeeting.getFREQUENCY().equals("")) {

            holder.tv_visited.setText("已拜访" + customerMeeting.getVISIT_NUMBER() + "次");
        }else {

            holder.tv_visited.setText("本" + customerMeeting.getFREQUENCY() + "已拜访" + customerMeeting.getVISIT_NUMBER() + "次");
        }

        holder.tv_history.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onClickListenerStr.onClick(position, "tv_history");
            }
        });
        holder.tv_visit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onClickListenerStr.onClick(position, "tv_visit");
            }
        });
        return convertView;
    }

    class ViewHolder {

        TextView tv_name, tv_phone, tv_time, tv_customer_name, tv_customer_address, tv_visit, tv_history, tv_visited;
    }
}
