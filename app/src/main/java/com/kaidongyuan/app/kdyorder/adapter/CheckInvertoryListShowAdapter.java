package com.kaidongyuan.app.kdyorder.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.kaidongyuan.app.kdyorder.R;

import java.util.ArrayList;
import java.util.List;

public class CheckInvertoryListShowAdapter extends BaseAdapter implements View.OnClickListener {
    private Context mContext;
    private List<String> titles;

    public CheckInvertoryListShowAdapter( List<String> titles, Context mContext) {
        this.titles = (titles == null) ? new ArrayList<String>() : titles;
        this.mContext = mContext;
    }

    public void notifyChange(List<String> titles) {
        this.titles = (titles == null) ? new ArrayList<String>() : titles;
        this.notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return titles.size();
    }

    @Override
    public Object getItem(int position) {
        return titles.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        // 重用机制
        final ViewHolder holder;
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = LayoutInflater.from(mContext).inflate(R.layout.item_single_line, null);
            holder.tv_title = (TextView) convertView.findViewById(R.id.tv_title);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        // 数据模型
        String title = titles.get(position);
        holder.tv_title.setText(title);

        return convertView;
    }

    class ViewHolder {
        private TextView tv_title;
    }

    @Override
    public void onClick(View v) {

    }
}