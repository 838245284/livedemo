package com.myylook.common.utils;

import android.content.Context;

import com.myylook.common.CommonAppContext;

/**
 * Created by cxf on 2017/8/9.
 * dp转px工具类
 */

public class DpUtil {

    private static float scale;

    static {
        scale = CommonAppContext.sInstance.getResources().getDisplayMetrics().density;
    }

    public static int dp2px(int dpVal) {
        return (int) (scale * dpVal + 0.5f);
    }

    public static int px2dip( float pxValue) {
        return (int) (pxValue / scale + 0.5f);
    }
}
