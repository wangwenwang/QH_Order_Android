package com.kaidongyuan.app.kdyorder.ui.activity;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.kaidongyuan.app.kdyorder.R;
import com.kaidongyuan.app.kdyorder.adapter.AddressListAdapter;
import com.kaidongyuan.app.kdyorder.adapter.PartyListAdapter;
import com.kaidongyuan.app.kdyorder.app.MyApplication;
import com.kaidongyuan.app.kdyorder.bean.Address;
import com.kaidongyuan.app.kdyorder.bean.Party;
import com.kaidongyuan.app.kdyorder.constants.EXTRAConstants;
import com.kaidongyuan.app.kdyorder.model.InventoryPartyActivityBiz;
import com.kaidongyuan.app.kdyorder.util.ExceptionUtil;
import com.kaidongyuan.app.kdyorder.util.ToastUtil;
import com.kaidongyuan.app.kdyorder.widget.loadingdialog.MyLoadingDialog;
import com.kaidongyuan.app.kdyorder.widget.xlistview.XListView;

import java.util.List;

/**
 * ${PEOJECT_NAME}
 * Created by Administrator on 2017/6/6.
 */
public class InventoryPartyActivity extends BaseActivity implements AdapterView.OnItemClickListener, View.OnClickListener{
    /**
     * 标题栏返回按钮
     */
    private ImageView mImageViewGoBack;
    /**
     * 选择客户业务类
     */
    private InventoryPartyActivityBiz mBiz;
    /**
     * 客户列表 ListView
     */
    private XListView mCustomerListview;
    /**
     * 客户列表 适配器
     */
    private PartyListAdapter mPartyListAdapter;
    /**
     * 搜索客户编辑框
     */
    private EditText mEdittextSearch;
    /**
     * 没有数据时显示的控件，默认为不显示
     */
    private TextView mTextviewNodata;
    /**
     * 网络请求是的 Dialog
     */
    private MyLoadingDialog mLoadingDialog;
    /**
     * 显示客户地址的 Dialog
     */
    private Dialog mCustomerAddressDialog;
    /**
     * 显示客户地址的 ListView
     */
    private ListView mListviewCustomerAddress;
    /**
     * 取消选择客户地址
     */
    private Button mButtonCancelChoiceAddress;
    /**
     * 显示客户地址的 Adapter
     */
    private AddressListAdapter mCustomerAddressAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inventory_party);
        initData();
        initView();
        initListener();
        getCustomerData();
    }

    private void initData() {
        try {

            mBiz = new InventoryPartyActivityBiz(this);
        } catch (Exception e) {
            ExceptionUtil.handlerException(e);
        }
    }
    private void initView() {
        try {
            mImageViewGoBack = (ImageView) this.findViewById(R.id.button_goback);
            mCustomerListview = (XListView) this.findViewById(R.id.lv_select_customer);
            mPartyListAdapter = new PartyListAdapter(InventoryPartyActivity.this, null);
            mCustomerListview.setAdapter(mPartyListAdapter);
            mCustomerListview.setPullLoadEnable(false);
            mCustomerListview.setPullRefreshEnable(true);
            mEdittextSearch = (EditText) this.findViewById(R.id.edittext_headview_content);
            mTextviewNodata = (TextView) this.findViewById(R.id.textview_nodata);
            mTextviewNodata.setVisibility(View.GONE);
        } catch (Exception e) {
            ExceptionUtil.handlerException(e);
        }
    }

    private void initListener() {
        try {
            mImageViewGoBack.setOnClickListener(this);
            mCustomerListview.setXListViewListener(new XListView.IXListViewListener() {
                @Override
                public void onRefresh() {//上拉刷新数据
                    mEdittextSearch.setText("");
                    getCustomerData();
                }

                @Override
                public void onLoadMore() {
                }
            });
            mCustomerListview.setOnItemClickListener(this);
            mEdittextSearch.addTextChangedListener(new InnerTextWatcher());
            mTextviewNodata.setOnClickListener(this);
        } catch (Exception e) {
            ExceptionUtil.handlerException(e);
        }
    }


    /**
     * 网络获取数据
     */
    private void getCustomerData() {
        try {
            boolean isSuccess = mBiz.getCustomerData();
            if (isSuccess) {//发送请求成功
                showDwonLoadingDialog();
            } else {//发送请求失败
                mTextviewNodata.setVisibility(View.VISIBLE);
                ToastUtil.showToastBottom(MyApplication.getmRes().getString(R.string.sendrequest_fail), Toast.LENGTH_SHORT);
            }
        } catch (Exception e) {
            ExceptionUtil.handlerException(e);
        }
    }

    /**
     * 获取数据是显示的 Dialog
     */
    private void showDwonLoadingDialog() {
        if (mLoadingDialog == null) {
            mLoadingDialog = new MyLoadingDialog(InventoryPartyActivity.this);
        }
        mLoadingDialog.showDialog();
    }

    /**
     * 内部监听编辑框变化的类
     */
    private class InnerTextWatcher implements TextWatcher {

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
        }

        @Override
        public void afterTextChanged(Editable s) {//用户输入的文字变化开始搜索，显示结果
            try {
                String msg = s.toString();
                mBiz.searchParty(msg);//搜索客户
                //显示搜索后的客户列表
                List<Party> parties = mBiz.getmCustomerList();
                mPartyListAdapter.notifyChange(parties);
                if (parties.size() <= 0) {
                    mTextviewNodata.setVisibility(View.VISIBLE);
                } else {
                    mTextviewNodata.setVisibility(View.GONE);
                }
            } catch (Exception e) {
                ExceptionUtil.handlerException(e);
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    /**
     * 处理获取客户列表数据结果
     */
    private void handleGetCustomerData() {
        try {
            mLoadingDialog.dismiss();
            List<Party> parties = mBiz.getmCustomerList();
            mCustomerListview.stopRefresh();
            mPartyListAdapter.notifyChange(parties);
            if (parties == null || parties.size() <= 0) {
                mTextviewNodata.setVisibility(View.VISIBLE);
            } else {
                mTextviewNodata.setVisibility(View.GONE);
            }
        } catch (Exception e) {
            ExceptionUtil.handlerException(e);
        }
    }

    /**
     * 获取数据失败
     *
     * @param msg 失败的原因
     */
    public void getCustomerDataError(String msg) {
        try {
            handleGetCustomerData();
            ToastUtil.showToastBottom(String.valueOf(msg), Toast.LENGTH_SHORT);
        } catch (Exception e) {
            ExceptionUtil.handlerException(e);
        }
    }

    /**
     * 获取数据成功
     */
    public void getCustomerDataSuccess() {
        try {
            handleGetCustomerData();
        } catch (Exception e) {
            ExceptionUtil.handlerException(e);
        }
    }

    /**
     * 处理获取客户地址请求失败
     *
     * @param msg 要显示的信息
     */
    public void getCustomerAdressDataError(String msg) {
        try {
            mLoadingDialog.dismiss();
            ToastUtil.showToastBottom(String.valueOf(msg), Toast.LENGTH_SHORT);
        } catch (Exception e) {
            ExceptionUtil.handlerException(e);
        }
    }

    /**
     * 处理获取客户地址成功
     */
    public void getCustomerAdressDataSuccess() {
        try {
            mLoadingDialog.dismiss();
            List<Address> customerAddress = mBiz.getmCustomerAddress();
            int customerAddressSize = customerAddress.size();
            if (customerAddressSize == 1) {
                jumpToMakeOrderActivity(customerAddress.get(0));
            } else if (customerAddressSize > 1) {
                showAddressDialog(customerAddress);
            }
        } catch (Exception e) {
            ExceptionUtil.handlerException(e);
        }
    }
    /**
     * 显示选择客户地址 Dialog
     *
     * @param customerAddress 客户地址集合
     */
    private void showAddressDialog(List<Address> customerAddress) {
        try {
            if (customerAddress.size() <= 0) {
                ToastUtil.showToastBottom("没有有效的客户地址！", Toast.LENGTH_SHORT);
            }
            if (mCustomerAddressDialog == null) {
                createAddressDialog();
            }
            mCustomerAddressDialog.show();
            mCustomerAddressAdapter.notifyChange(customerAddress);
        } catch (Exception e) {
            ExceptionUtil.handlerException(e);
        }
    }

    private void createAddressDialog() {
        try {
            AlertDialog.Builder builder = new AlertDialog.Builder(InventoryPartyActivity.this);
            mCustomerAddressDialog = builder.create();
            mCustomerAddressDialog.setCanceledOnTouchOutside(false);
            mCustomerAddressDialog.show();
            Window window = mCustomerAddressDialog.getWindow();
            window.setContentView(R.layout.dialog_customeraddress_choice);
            mListviewCustomerAddress = (ListView) window.findViewById(R.id.listView_customeraddress);
            mListviewCustomerAddress.setOnItemClickListener(new InnerOnItemClickListener());
            mButtonCancelChoiceAddress = (Button) window.findViewById(R.id.button_cancelchoiceaddress);
            mButtonCancelChoiceAddress.setOnClickListener(this);
            mCustomerAddressAdapter = new AddressListAdapter(InventoryPartyActivity.this, null);
            mListviewCustomerAddress.setAdapter(mCustomerAddressAdapter);
        } catch (Exception e) {
            ExceptionUtil.handlerException(e);
        }
    }
    @Override
    public void finish() {
        super.finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mLoadingDialog != null && mLoadingDialog.isShowing()) {
            mLoadingDialog.dismiss();
        }
        mBiz.cancelRequest();
        mBiz = null;
        mImageViewGoBack = null;

    }

    @Override
    public void onClick(View v) {
        try {
            switch (v.getId()) {
                case R.id.button_cancelchoiceaddress://取消选择用户地址下单,Dialog中按钮
                    mCustomerAddressDialog.dismiss();
                    break;
                case R.id.textview_nodata://重新加载数据
                    getCustomerData();
                    break;
                case R.id.button_goback:
                    this.finish();
                    break;
            }
        } catch (Exception e) {
            ExceptionUtil.handlerException(e);
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        try {
            if (mBiz.getPartygetAddressInfo(position - 1)) {//发送请求成功,集合中数据的 Index 比 ListView 中的Index 小 1，考虑 headerView
                showDwonLoadingDialog();
            } else {//发送请求失败
                ToastUtil.showToastBottom(MyApplication.getmRes().getString(R.string.sendrequest_fail), Toast.LENGTH_SHORT);
            }
        } catch (Exception e) {
            ExceptionUtil.handlerException(e);
        }
    }

    /**
     * 选择用户地址 Dialog 中 ListView 的监听
     */
    private class InnerOnItemClickListener implements AdapterView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            try {
                mCustomerAddressDialog.dismiss();
                Address address = mBiz.getmCustomerAddress().get(position);
                jumpToMakeOrderActivity(address);
            } catch (Exception e) {
                ExceptionUtil.handlerException(e);
            }
        }
    }
    /**
     * 跳转到编辑库存列表页面
     *
     * @param address 下单客户地址
     */
    private void jumpToMakeOrderActivity(Address address) {
        try {
            Intent makeAppStockIntent = new Intent(InventoryPartyActivity.this, MakeAppStockActivity.class);
            Party party = mBiz.getmSelectedParty();
            makeAppStockIntent.putExtra(EXTRAConstants.ORDER_PARTY_ID, party.getIDX());
            makeAppStockIntent.putExtra(EXTRAConstants.ORDER_PARTY_NO,party.getPARTY_CODE());
            makeAppStockIntent.putExtra(EXTRAConstants.ORDER_PARTY_NAME, party.getPARTY_NAME());
            makeAppStockIntent.putExtra(EXTRAConstants.ORDER_ADDRESS_CODE, address.getADDRESS_CODE());
            makeAppStockIntent.putExtra(EXTRAConstants.ORDER_PARTY_ADDRESS_IDX, address.getIDX());
            makeAppStockIntent.putExtra(EXTRAConstants.ORDER_ADDRESS_INFORMATION, address.getADDRESS_INFO());
            startActivity(makeAppStockIntent);
        } catch (Exception e) {
            ExceptionUtil.handlerException(e);
        }
    }

}
