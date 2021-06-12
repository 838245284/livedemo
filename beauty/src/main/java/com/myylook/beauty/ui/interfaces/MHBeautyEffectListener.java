package com.myylook.beauty.ui.interfaces;


import com.myylook.beauty.ui.bean.FilterBean;
import com.myylook.beauty.ui.bean.QuickBeautyBean;
import com.myylook.beauty.ui.bean.WatermarkBean;

/**
 * Created by cxf on 2018/10/8.
 * 萌颜美颜回调
 */

public interface MHBeautyEffectListener extends BeautyEffectListener {

    void onFilterChanged(FilterBean tiFilterEnum);

    void onBeautyOrigin();

    void onShapeOrigin();

    void onMeiBaiChanged(int progress);

    void onMoPiChanged(int progress);

    void onFengNenChanged(int progress);

    void onBrightChanged(int progress);

    void onBigEyeChanged(int progress); //大眼

    void onEyeBrowChanged(int progress);   //眉毛

    void onEyeLengthChanged(int progress);//眼距

    void onEyeCornerChanged(int progress);//眼角

    void onFaceChanged(int progress);//瘦脸

    void onFaceShaveChanged(int progress);//削脸

    void onEyeAlatChanged(int progress);//开眼角

    void onMouseChanged(int progress);//嘴形

    void onNoseChanged(int progress);//瘦鼻

    void onChinChanged(int progress);//下巴

    void onForeheadChanged(int progress);//额头

    void onLengthenNoseChanged(int progress);//长鼻

    void onQuickBeautyChanged(QuickBeautyBean beautyBean);

    void onStickerChanged(String tieZhiName);

    void onSpeciallyChanged(String speciallyName);

    void onWaterMarkChanged(WatermarkBean bitmap);

    void onDistortionChanged(String distortionName);
}
