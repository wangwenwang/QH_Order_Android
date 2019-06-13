package com.kaidongyuan.app.kdyorder.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.view.menu.MenuBuilder;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.kaidongyuan.app.kdyorder.R;
import com.kaidongyuan.app.kdyorder.adapter.ChartOrderSumItemAdapter;
import com.kaidongyuan.app.kdyorder.bean.ChartOrderSumItem;
import com.kaidongyuan.app.kdyorder.model.ChartOrderSumItemActivityBiz;
import com.kaidongyuan.app.kdyorder.util.ExceptionUtil;
import com.kaidongyuan.app.kdyorder.util.ToastUtil;
import com.kaidongyuan.app.kdyorder.widget.loadingdialog.MyLoadingDialog;
import com.kaidongyuan.app.kdyorder.widget.xlistview.XListView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import me.kareluo.ui.OptionMenu;
import me.kareluo.ui.OptionMenuView;
import me.kareluo.ui.PopupMenuView;

/*
 * 全部出库详情
 * */
public class ChartOrderSumItemActivity extends BaseActivity implements View.OnClickListener {


    private ChartOrderSumItemActivityBiz mBiz;

    /**
     * 网络请求时显示的 Dialog
     */
    private MyLoadingDialog mLoadingDialog;


    /**
     * 显示商品的 ListView
     */
    private XListView mXlistViewChartOrderSumItems;

    /**
     * 没有数据是显示的提示框
     */
    private TextView mTextViewNoData;

    /**
     * 报表列表 ListView 的 Adapter
     */
    private ChartOrderSumItemAdapter mAdapter;

    /**
     * 返回上一界面按钮
     */
    private ImageView mImageViewGoBack, mSort;

    private String PARTY_CODE = "";

    /**
     * 搜索产品名称编辑框
     */
    private EditText mEdittextSearch;

    private List<ChartOrderSumItem> mChartOrderSumItemList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chart_order_sum_item);
        try {
            initData();
            initView();
            setListener();
            showLoadingDialog();
            mBiz.GetDatas("OUT", "全部", PARTY_CODE);
        } catch (Exception e) {
            ExceptionUtil.handlerException(e);
        }
    }


    @Override
    protected void onDestroy() {
        Log.d("LM", "onDestroy: ");
        try {
            super.onDestroy();
            if (mLoadingDialog != null && mLoadingDialog.isShowing()) {
                mLoadingDialog.dismiss();
            }
            mLoadingDialog = null;
            mImageViewGoBack = null;
            mAdapter = null;
            mTextViewNoData = null;
            mXlistViewChartOrderSumItems = null;
            mEdittextSearch = null;
            mSort = null;
        } catch (Exception e) {
            ExceptionUtil.handlerException(e);
        }
    }

    private void initData() {
        mBiz = new ChartOrderSumItemActivityBiz(this);
        Intent intent = getIntent();
        if (intent.hasExtra("PARTY_CODE")) {
            PARTY_CODE = intent.getStringExtra("PARTY_CODE");
        }
    }

    @Override
    protected Object clone() {
        try {
            ChartOrderSumItem cosi = (ChartOrderSumItem) super.clone();
            cosi = (ChartOrderSumItem) this.clone();
            return cosi;
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public void onClick(View v) {
        try {
            switch (v.getId()) {
                case R.id.button_goback:
                    this.finish();
                    break;
                case R.id.button_sort:
                    // 根据menu资源文件创建
                    PopupMenuView menuView = new PopupMenuView(this, R.menu.menu_pop, new MenuBuilder(this));
                    // 设置点击监听事件
                    menuView.setOnMenuClickListener(new OptionMenuView.OnOptionMenuClickListener() {
                        @Override
                        public boolean onOptionMenuClick(int position, OptionMenu menu) {
                            Log.d("LM", "onOptionMenuClick: " + position + "   " + menu.getTitle());
                            if (position == 0) {
                                mAdapter.notifyChange(mChartOrderSumItemList);
                            } else if (position == 1) {
                                ArrayList<ChartOrderSumItem> list = new ArrayList<>();
                                for (int i = 0; i < mChartOrderSumItemList.size(); i++) {
                                    ChartOrderSumItem cosi = mChartOrderSumItemList.get(i);
                                    list.add(cosi);
                                }
                                Collections.sort(list, new Comparator<ChartOrderSumItem>() {
                                    @Override
                                    public int compare(ChartOrderSumItem lhs, ChartOrderSumItem rhs) {
                                        int i = Integer.parseInt(rhs.getNUMBER()) - Integer.parseInt(lhs.getNUMBER());
                                        if (i == 0) {
                                            return Integer.parseInt(rhs.getNUMBER()) - Integer.parseInt(lhs.getNUMBER());
                                        }
                                        return i;
                                    }
                                });
                                mAdapter.notifyChange(list);
                            } else if (position == 2) {
                                ArrayList<ChartOrderSumItem> list = new ArrayList<>();
                                for (int i = 0; i < mChartOrderSumItemList.size(); i++) {
                                    ChartOrderSumItem cosi = mChartOrderSumItemList.get(i);
                                    list.add(cosi);
                                }
                                Collections.sort(list, new Comparator<ChartOrderSumItem>() {
                                    @Override
                                    public int compare(ChartOrderSumItem lhs, ChartOrderSumItem rhs) {
                                        int i = Integer.parseInt(rhs.getAMOUNT_MONEY()) - Integer.parseInt(lhs.getAMOUNT_MONEY());
                                        if (i == 0) {
                                            return Integer.parseInt(rhs.getAMOUNT_MONEY()) - Integer.parseInt(lhs.getAMOUNT_MONEY());
                                        }
                                        return i;
                                    }
                                });
                                mAdapter.notifyChange(list);
                            }
                            return true;
                        }
                    });
                    menuView.setMenuItems(Arrays.asList(
                            new OptionMenu("按默认排序"),
                            new OptionMenu("按数量降序"),
                            new OptionMenu("按金额降序")));

                    menuView.setOrientation(LinearLayout.VERTICAL);
                    // 显示在mButtom控件的周围
                    menuView.show(mSort);
                    break;
            }
        } catch (Exception e) {
            ExceptionUtil.handlerException(e);
        }
    }

    private void initView() {
        try {
            mImageViewGoBack = (ImageView) findViewById(R.id.button_goback);
            mXlistViewChartOrderSumItems = (XListView) findViewById(R.id.lv_chart_order_sum);
            mAdapter = new ChartOrderSumItemAdapter(this, null);
            mXlistViewChartOrderSumItems.setAdapter(mAdapter);
            mTextViewNoData = (TextView) findViewById(R.id.textview_nodata);
            mEdittextSearch = (EditText) findViewById(R.id.edittext_headview_content);
            mSort = (ImageView) findViewById(R.id.button_sort);

            mXlistViewChartOrderSumItems.setXListViewListener(new XListView.IXListViewListener() {
                @Override
                public void onRefresh() {//上拉刷新数据
                    mEdittextSearch.setText("");
                }

                @Override
                public void onLoadMore() {
                }
            });
            mEdittextSearch.addTextChangedListener(new InnerTextWatcher());
        } catch (Exception e) {
            ExceptionUtil.handlerException(e);
        }
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
                //显示搜索后的客户列表
                List<ChartOrderSumItem> chartOrderSumItemList = mBiz.getChartOrderSumList();
                if (chartOrderSumItemList.size() <= 0) {
                    mTextViewNoData.setVisibility(View.VISIBLE);
                } else {
                    mTextViewNoData.setVisibility(View.GONE);
                    ArrayList<ChartOrderSumItem> repartylist = new ArrayList<>();
                    for (ChartOrderSumItem sumItem : chartOrderSumItemList) {
                        if (sumItem.getPRODUCT_NAME().contains(msg)) {
                            repartylist.add(sumItem);
                        }
                    }
                    mChartOrderSumItemList = repartylist;
                    mAdapter.notifyChange(mChartOrderSumItemList);
                }
            } catch (Exception e) {
                ExceptionUtil.handlerException(e);
            }
        }
    }

    private void setListener() {
        try {
            mImageViewGoBack.setOnClickListener(this);
            mSort.setOnClickListener(this);
        } catch (Exception e) {
            ExceptionUtil.handlerException(e);
        }
    }


    /**
     * 网络请求是显示 Dilaog
     */
    private void showLoadingDialog() {
        try {
            if (mLoadingDialog == null) {
                mLoadingDialog = new MyLoadingDialog(this);
            }
            mLoadingDialog.showDialog();
        } catch (Exception e) {
            ExceptionUtil.handlerException(e);
        }
    }

    /**
     * 获取客户拜访列表失败
     *
     * @param message 要显示的消息
     */
    public void getDataError(String message) {
        try {
            mLoadingDialog.dismiss();
            // 不提示 没有数据 4个字
            if (!message.equals("没有数据")) {
                ToastUtil.showToastBottom(String.valueOf(message), Toast.LENGTH_SHORT);
            }
        } catch (Exception e) {
            ExceptionUtil.handlerException(e);
        }
    }

    /**
     * 获取客户拜访列表成功
     */
    public void getDataSuccess() {
        try {

            mLoadingDialog.dismiss();
            mChartOrderSumItemList = mBiz.getChartOrderSumList();
            if (mChartOrderSumItemList == null || mChartOrderSumItemList.size() <= 0) {
                mTextViewNoData.setVisibility(View.VISIBLE);
            } else {
                mTextViewNoData.setVisibility(View.GONE);
            }
            mAdapter.notifyChange(mChartOrderSumItemList);
        } catch (Exception e) {
            ExceptionUtil.handlerException(e);
        }
    }
}