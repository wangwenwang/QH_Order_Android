package com.kaidongyuan.app.kdyorder.ui.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.kaidongyuan.app.kdyorder.R;
import com.kaidongyuan.app.kdyorder.app.MyApplication;
import com.kaidongyuan.app.kdyorder.constants.BroadcastConstants;
import com.kaidongyuan.app.kdyorder.constants.BusinessConstants;
import com.kaidongyuan.app.kdyorder.ui.activity.BusinessInventoryActivity;
import com.kaidongyuan.app.kdyorder.ui.activity.ChartCheckActivity;
import com.kaidongyuan.app.kdyorder.ui.activity.CheckTmsOrderListActivity;
import com.kaidongyuan.app.kdyorder.ui.activity.CustomerMeetingsActivity;
import com.kaidongyuan.app.kdyorder.ui.activity.InventoryPartyListActivity;
import com.kaidongyuan.app.kdyorder.ui.activity.KpiTrackActivity;
import com.kaidongyuan.app.kdyorder.util.ExceptionUtil;
import com.kaidongyuan.app.kdyorder.util.OrderUtil;
import com.kaidongyuan.app.kdyorder.widget.CycleViewpager;
import com.kaidongyuan.app.kdyorder.widget.MoveButton;
import com.zhy.android.percent.support.PercentRelativeLayout;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * Created by Administrator on 2016/4/1.
 * 主页 Fragment
 */
public class HomeFragment extends BaseFragment implements View.OnClickListener {

    private View mParentView;

    /**
     * 广告轮播控件
     */
    private CycleViewpager mCycleviewpager;
    /**
     * 跳转到 下单界面
     */
    private MoveButton mMoveButtonMakeOrder;
    /**
     * 跳转到 客户拜访
     */
    private PercentRelativeLayout mPercentrlVisitCustomer;
    /**
     * 跳转到 库存管理
     */
    private PercentRelativeLayout mPercentrlPartyInventory;
    /**
     * 跳转到 查看报表
     */
    private PercentRelativeLayout mPercentrlChart;
    /**
     * 跳转到 业绩追踪
     */
    private PercentRelativeLayout mPercentrlKpiTrack;
    /**
     * 跳转到 订单查询
     */
    private PercentRelativeLayout mPercentrlOrderCheck;
    /**
     * 跳转到 客户管理
     */
    private PercentRelativeLayout mPercentrlCustomerMan;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        try {
            mParentView = inflater.inflate(R.layout.fragment_home, null);
            initView();
            initCycleViewData();
            setListener();
            return mParentView;
        } catch (Exception e) {
            ExceptionUtil.handlerException(e);
            return mParentView;
        }
    }

    @Override
    public void onDestroy() {
        try {
            super.onDestroy();
            mCycleviewpager.stopPlay();
        } catch (Exception e) {
            ExceptionUtil.handlerException(e);
        }
    }

    private void initView() {
        try {
            mCycleviewpager = (CycleViewpager) mParentView.findViewById(R.id.cycleViewpager_carousel);
            mMoveButtonMakeOrder = (MoveButton) mParentView.findViewById(R.id.movebutton_makeOrder);
            mPercentrlChart = (PercentRelativeLayout) mParentView.findViewById(R.id.percentrl_chart);
            mPercentrlVisitCustomer = (PercentRelativeLayout) mParentView.findViewById(R.id.percentrl_visit_customer);
            mPercentrlKpiTrack = (PercentRelativeLayout) mParentView.findViewById(R.id.percentrl_kpi_track);
            mPercentrlPartyInventory = (PercentRelativeLayout) mParentView.findViewById(R.id.percentrl_party_inventory);
            mPercentrlOrderCheck = (PercentRelativeLayout) mParentView.findViewById(R.id.percentrl_order_check);
            mPercentrlCustomerMan = (PercentRelativeLayout) mParentView.findViewById(R.id.percentrl_customer_man);

        } catch (Exception e) {
            ExceptionUtil.handlerException(e);
        }
    }

    /**
     * 设置轮播的图片和圆点图片
     */
    private void initCycleViewData() {
        try {
            mCycleviewpager.setImagesData(new CycleViewpager.ICycleViewpager() {
                @Override
                public List<Integer> setImageResourcesId() {
                    int businessType = OrderUtil.getBusinessType();
                    return getImageList(businessType);
                }

                @Override
                public Map<String, Integer> setPointResourcesId() {
                    HashMap<String, Integer> pointMap = new HashMap<>();
                    pointMap.put(CycleViewpager.SELECTED, R.drawable.point_focused);
                    pointMap.put(CycleViewpager.UNSELECTED, R.drawable.point_unfocused);
                    return pointMap;
                }
            });
        } catch (Exception e) {
            ExceptionUtil.handlerException(e);
        }
    }

    private void setListener() {
        try {
            mMoveButtonMakeOrder.setOnMoveToDoSomeThingListener(new MoveButton.MoveToStartDoSomeThingInterface() {
                @Override
                public void doRightSuccess() {//滑动到右侧，跳转到下单 Fragment 界面
                    Intent intent = new Intent(BroadcastConstants.JUMPTO_MAKEORDER_FRAGMENT);
                    MyApplication.getAppContext().sendBroadcast(intent);
                }

                @Override
                public void doLeftSuccess() {//滑动到左侧，跳转到查单 Fragment 界面
                    Intent checkOrderIntent = new Intent(BroadcastConstants.JUMPTO_CHECKORDER_FRAGMENT);
                    MyApplication.getAppContext().sendBroadcast(checkOrderIntent);
                }
            });
            mPercentrlVisitCustomer.setOnClickListener(this);
            mPercentrlPartyInventory.setOnClickListener(this);
            mPercentrlChart.setOnClickListener(this);
            mPercentrlKpiTrack.setOnClickListener(this);
        } catch (Exception e) {
            ExceptionUtil.handlerException(e);
        }
    }

    /**
     * 根据用户登录的业务类型获取图片资源集合
     *
     * @param businessType 用户登录业务类型
     * @return 图片资源集合
     */
    private List<Integer> getImageList(int businessType) {
        List<Integer> imageList = new ArrayList<>();
        try {
            imageList.add(R.drawable.ad_pic_4);
            imageList.add(R.drawable.ad_pic_5);
            imageList.add(R.drawable.ad_pic_0);
            imageList.add(R.drawable.ad_pic_1);
            imageList.add(R.drawable.ad_pic_2);
            imageList.add(R.drawable.ad_pic_3);
            return imageList;
        } catch (Exception e) {
            ExceptionUtil.handlerException(e);
            return imageList;
        }
    }

    @Override
    public void onClick(View v) {
        try {
            switch (v.getId()) {
                case R.id.percentrl_visit_customer:  // 跳转到 查看报表
                    startActivity(new Intent(this.getActivity(), CustomerMeetingsActivity.class));
                    break;
                case R.id.percentrl_party_inventory: // 跳转到 库存管理
                    Intent inventoryPartyListIntent=new Intent(this.getActivity(),InventoryPartyListActivity.class);
                    startActivity(inventoryPartyListIntent);
                    break;
                case R.id.percentrl_chart:           // 跳转到 查看报表
                    Intent chartCheckIntent = new Intent(this.getActivity(), ChartCheckActivity.class);
                    startActivity(chartCheckIntent);
                    break;
                case R.id.percentrl_kpi_track:       // 跳转到 业绩追踪
                    Intent kpiTrackIntent = new Intent(this.getActivity(), KpiTrackActivity.class);
                    startActivity(kpiTrackIntent);
                    break;
                case R.id.percentrl_order_check:     // 跳转到 订单查询
                    break;
                case R.id.percentrl_customer_man:    // 跳转到 客户管理
                    break;
                default:
                    break;
            }
        } catch (Exception e) {
            ExceptionUtil.handlerException(e);
        }
    }
}