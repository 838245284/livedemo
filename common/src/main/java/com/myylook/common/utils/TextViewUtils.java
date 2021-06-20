package com.myylook.common.utils;

import android.graphics.drawable.Drawable;
import android.widget.TextView;

public class TextViewUtils {
    /**
     * 设置TextView图标
     *
     * @param drawable  图标
     * @param direction 图标方向，0左 1上 2右 3下 默认图标位于左侧0
     */
    public static void setDrawableRes(TextView textView, int drawable, int direction) {
        switch (direction) {
            case 0:
                textView.setCompoundDrawablesRelativeWithIntrinsicBounds(drawable, 0, 0, 0);
                break;
            case 1:
                textView.setCompoundDrawablesRelativeWithIntrinsicBounds(0, drawable, 0, 0);
                break;
            case 2:
                textView.setCompoundDrawablesRelativeWithIntrinsicBounds(0, 0, drawable, 0);
                break;
            case 3:
                textView.setCompoundDrawablesRelativeWithIntrinsicBounds(0, 0, 0, drawable);
                break;
        }
    }

    /**
     * 设置TextView图标
     *
     * @param drawableId 图标
     * @param direction  图标方向，0左 1上 2右 3下 默认图标位于左侧0
     */
    public static void setDrawableRes(TextView textView, int drawableId, int direction, int iconWidth, int iconHeight) {
        Drawable drawable = textView.getContext().getDrawable(drawableId);
        drawable.setBounds(0, 0, iconWidth, iconHeight);

        switch (direction) {
            case 0:
                textView.setCompoundDrawables(drawable, null, null, null);
                break;
            case 1:
                textView.setCompoundDrawables(null, drawable, null, null);
                break;
            case 2:
                textView.setCompoundDrawables(null, null, drawable, null);
                break;
            case 3:
                textView.setCompoundDrawables(null, null, null, drawable);
                break;
        }
    }
}
