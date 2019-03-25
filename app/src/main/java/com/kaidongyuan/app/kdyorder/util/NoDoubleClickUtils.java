package com.kaidongyuan.app.kdyorder.util;

import android.util.Log;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;

/**
 * 防止按钮2连续多次点击
 * Created by wangww on 2019/3/24.
 */
public class NoDoubleClickUtils {
    private final static int SPACE_TIME = 500;//2次点击的间隔时间，单位ms
    private static long lastClickTime;

    public synchronized static boolean isDoubleClick() {
        long currentTime = System.currentTimeMillis();
        boolean isClick;
        if (currentTime - lastClickTime > SPACE_TIME) {
            isClick = false;
        } else {
            isClick = true;
        }
        lastClickTime = currentTime;
        return isClick;
    }

//    @Aspect
//    public class AspectTest {
//        final String TAG = AspectTest.class.getSimpleName();
//
//        @Around("execution(* android.view.View.OnClickListener.onClick(..))")
//        public void onClickLitener(ProceedingJoinPoint proceedingJoinPoint) throws Throwable {
//            Log.d("LM", "OnClick_LM");
//            if (!NoDoubleClickUtils.isDoubleClick()) {
//                proceedingJoinPoint.proceed();
//            }
//        }
//    }
}
