package com.kaidongyuan.app.kdyorder.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.kaidongyuan.app.kdyorder.R;
import com.kaidongyuan.app.kdyorder.bean.Product;
import com.kaidongyuan.app.kdyorder.util.ToastUtil;
import com.kaidongyuan.app.kdyorder.util.Tools;

import java.util.ArrayList;
import java.util.List;

public class CheckInvertoryListAdapter extends BaseAdapter {

    private Context mContext;
    private List<Product> mProducts;
    private List<Product> mChoicedProducts;

    /**
     * 回调接口
     */
    private CheckInvertoryListAdapterInterface mInterfae;

    /**
     * 设置接口
     *
     * @param checkInvertoryListAdapterInterface 回调接口
     */
    public void setInterface(CheckInvertoryListAdapter.CheckInvertoryListAdapterInterface checkInvertoryListAdapterInterface) {
        this.mInterfae = checkInvertoryListAdapterInterface;
    }

    /**
     * Created by Administrator on 2019/2/22.
     * 检查库存 Adapter 的回调接口
     */
    public interface CheckInvertoryListAdapterInterface {

        /**
         * 确认检查此产品
         *
         * @param dataIndex 产品列表中的位置
         */
        void okProduct(int dataIndex);
    }

    public CheckInvertoryListAdapter(Context context, List<Product> products, List<Product> choicedProduct) {
        mContext = context;
        mProducts = products == null ? new ArrayList<Product>() : products;
        mChoicedProducts = choicedProduct == null ? new ArrayList<Product>() : choicedProduct;
    }

    public void notifyChange(List<Product> products, List<Product> choicedProduct) {
        mProducts = products == null ? new ArrayList<Product>() : products;
        mChoicedProducts = choicedProduct == null ? new ArrayList<Product>() : choicedProduct;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return mProducts.size();
    }

    @Override
    public Object getItem(int position) {
        return mProducts.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        final Holder holder;
        if (convertView == null) {
            holder = new Holder();
            convertView = LayoutInflater.from(mContext).inflate(R.layout.activity_check_invertory, null);
            holder.textViewProductName = (TextView) convertView.findViewById(R.id.tv_product_name);
            holder.buttonConfirm = (Button) convertView.findViewById(R.id.btn_confirm);
            holder.ed_product_qty = (EditText) convertView.findViewById(R.id.ed_product_qty);
            holder.tv_product_uom = (TextView) convertView.findViewById(R.id.tv_product_uom);
            convertView.setTag(holder);
        } else {
            holder = (Holder) convertView.getTag();
        }

        // 产品
        Product product = mProducts.get(position);

        holder.textViewProductName.setText(product.getPRODUCT_NAME());

        holder.ed_product_qty.setText("");

        if(Tools.hasBASE_RATE(product.getBASE_RATE())) {

            holder.tv_product_uom.setText(product.getPACK_UOM());
        }else {

            holder.tv_product_uom.setText(product.getPRODUCT_UOM());
        }

        //设置监听
//        holder.buttonConfirm.setTag(position);
//        holder.buttonConfirm.setOnClickListener(this);

        holder.buttonConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                try {

                    int qty=Integer.parseInt(holder.ed_product_qty.getText().toString());
                    confirmOnclick(position, qty);
                } catch (NumberFormatException e) {

                    Toast.makeText(mContext, "请输入数字", Toast.LENGTH_SHORT).show();
                }
            }
        });

        return convertView;
    }

    private class Holder {
        TextView textViewProductName;
        TextView buttonConfirm;
        EditText ed_product_qty;
        TextView tv_product_uom;
    }

//    @Override
//    public void onClick(View v) {
//
//        View convertView = LayoutInflater.from(mContext).inflate(R.layout.activity_check_invertory, null);
//        int tag = (int) v.getTag();
//        Holder holder = new Holder();
//        holder.ed_product_qty = (EditText) convertView.findViewById(R.id.ed_product_qty);
//        String a = String.valueOf(holder.ed_product_qty.getText());
//        switch (v.getId()) {
//            case R.id.btn_confirm:
//                confirmOnclick(tag);
//                break;
//        }
//    }

    private void confirmOnclick(int tag, int qty) {

        Product p = mProducts.get(tag);

        p.setCHOICED_SIZE(qty);

        if(mChoicedProducts.contains(p)) {
            ToastUtil.showToastBottom("此商品已检查！", Toast.LENGTH_SHORT);
            return;
        }
        if(p.getCHOICED_SIZE() <= 0) {
            ToastUtil.showToastBottom("数量不能为空！", Toast.LENGTH_SHORT);
            return;
        }
        mChoicedProducts.add(p);
        mInterfae.okProduct(tag);
    }
}