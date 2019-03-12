package com.kaidongyuan.app.kdyorder.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.kaidongyuan.app.kdyorder.R;
import com.kaidongyuan.app.kdyorder.bean.Product;
import com.kaidongyuan.app.kdyorder.ui.activity.CustomerMeetingCheckInventoryActivity;
import com.kaidongyuan.app.kdyorder.util.CheckStringEmptyUtil;
import com.kaidongyuan.app.kdyorder.util.Tools;

import java.util.ArrayList;
import java.util.List;

public class CheckInvertoryListOKAdapter extends BaseAdapter implements View.OnClickListener {
    private Context mContext;
    private List<Product> products;

    /**
     * 回调接口
     */
    private CheckInvertoryListOKAdapter.CheckInvertoryListOKAdapterInterface mInterfae;

    /**
     * 设置接口
     * @param checkInvertoryListOKAdapterInterface 回调接口
     */
    public void setInterface(CheckInvertoryListOKAdapter.CheckInvertoryListOKAdapterInterface checkInvertoryListOKAdapterInterface) {
        this.mInterfae = checkInvertoryListOKAdapterInterface;
    }

    /**
     * 移除已检查的产品 Adapter 的回调接口
     */
    public interface CheckInvertoryListOKAdapterInterface {
        void delProduct(int dataIndex);
    }

    public CheckInvertoryListOKAdapter(Context mContext, List<Product> products) {
        this.products = products == null ? new ArrayList<Product>() : products;
        this.mContext = mContext;
    }

    public void notifyChange(List<Product> products) {
        this.products = products == null ? new ArrayList<Product>() : products;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return products.size();
    }

    @Override
    public Object getItem(int position) {
        return products.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        // 重用机制
        Holder holder;
        if (convertView == null) {
            holder = new Holder();
            convertView = LayoutInflater.from(mContext).inflate(R.layout.check_invertory_ok, null);
            holder.tv_product_name = (TextView) convertView.findViewById(R.id.tv_product_name);
            holder.iv_delete = (ImageView) convertView.findViewById(R.id.iv_delete);
            convertView.setTag(holder);
        } else {
            holder = (Holder) convertView.getTag();
        }

        // 数据模型
        Product bean = products.get(position);

        // 删除按钮
        holder.iv_delete.setTag(position);
        holder.iv_delete.setOnClickListener(this);

        // 商品名称
        String productName = CheckStringEmptyUtil.checkStringIsEmptyWithNoSet(bean.getPRODUCT_NAME());
        String uom = "";
        // 始终查检大单位
        if(Tools.hasBASE_RATE(bean.getBASE_RATE())) {
            uom = bean.getPACK_UOM();
        }else {
            uom = bean.getPRODUCT_UOM();
        }
        holder.tv_product_name.setText(productName + "(" + bean.getCHOICED_SIZE() + uom + ")");

        return convertView;
    }

    static class Holder {
        private TextView tv_product_name;
        private ImageView iv_delete;
    }

    @Override
    public void onClick(View v) {

        int tag = (int) v.getTag();
        switch (v.getId()) {
            case R.id.iv_delete:
                delOnclick(tag);
                break;
        }
    }

    private void delOnclick(int tag) {

        mInterfae.delProduct(tag);
    }
}