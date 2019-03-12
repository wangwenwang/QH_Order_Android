package com.kaidongyuan.app.kdyorder.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.kaidongyuan.app.kdyorder.R;
import com.kaidongyuan.app.kdyorder.app.MyApplication;
import com.kaidongyuan.app.kdyorder.bean.ProductTB;

import java.util.ArrayList;
import java.util.List;

public class BrandChoiceAdapter extends BaseAdapter {

    private List<ProductTB> mData;
    private Context mContext;

    private List<String> mItem;

    public BrandChoiceAdapter(List<ProductTB> data, Context context) {
        this.mData = data == null ? new ArrayList<ProductTB>() : data;
        this.mItem = new ArrayList<>();
        this.mContext = context;
    }

    /**
     * 刷新 ListView
     *
     * @param item 数据
     */
    public void notifyChange(List<String> item) {

        this.mItem = item == null ? new ArrayList<String>() : item;

//        item.add(0, MyApplication.getmRes().getString(R.string.all));
//        for (ProductTB productTB : item) {
//            String brand = productTB.getPRODUCT_TYPE();
//            if (!TextUtils.isEmpty(brand) && !mOrderBrands.contains(brand)) {
//                mOrderBrands.add(brand);
//            }
//        }
        Log.d("LM", "notifyChange: ");
    }

    @Override
    public int getCount() {
        return mItem.size();
    }

    @Override
    public ProductTB getItem(int position) {
        return mData.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = View.inflate(mContext, R.layout.item_business_listview, null);
            holder.tvChartName = (TextView) convertView.findViewById(R.id.textView_bussinessName);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        String chartName = mItem.get(position);
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
