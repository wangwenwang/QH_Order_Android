package com.kaidongyuan.app.kdyorder.model;

import android.content.Context;
import android.widget.ImageView;

import com.kaidongyuan.app.kdyorder.R;
import com.kaidongyuan.app.kdyorder.constants.BusinessConstants;
import com.kaidongyuan.app.kdyorder.constants.SharedPreferenceConstants;
import com.kaidongyuan.app.kdyorder.ui.activity.WelcomeActivity;
import com.kaidongyuan.app.kdyorder.util.ExceptionUtil;
import com.kaidongyuan.app.kdyorder.util.SharedPreferencesUtil;

/**
 * Created by Administrator on 2016/5/16.
 * WelcomeActivity 的业务类
 */
public class WelcomeActivityBiz {

    private Context mContext;
    private WelcomeActivity mWelcomeActivity;

    public WelcomeActivityBiz(WelcomeActivity activity) {
        this.mContext = activity;
        this.mWelcomeActivity = activity;
    }


    /**
     * 根据业务类型设置欢迎界面的图片
     * @param mImageViewPicture 显示图片的控件
     */
    public void setImageViewBackground(ImageView mImageViewPicture) {

        mImageViewPicture.setImageResource(R.drawable.login_background);
    }
}











