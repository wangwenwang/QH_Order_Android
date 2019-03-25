package com.kaidongyuan.app.kdyorder.util;

import android.util.Log;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;

@Aspect
public class AspectTest {
    final String TAG = AspectTest.class.getSimpleName();

    @Around("execution(* android.view.View.OnClickListener.onClick(..))")
    public void onClickLitener(ProceedingJoinPoint proceedingJoinPoint) throws Throwable {
        Log.d("LM", "OnClick_LM");
        if (!NoDoubleClickUtils.isDoubleClick()) {
            proceedingJoinPoint.proceed();
        }
    }
}
