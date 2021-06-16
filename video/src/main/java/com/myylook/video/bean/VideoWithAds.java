package com.myylook.video.bean;

import com.bytedance.sdk.openadsdk.TTNativeExpressAd;

import java.util.List;

public class VideoWithAds {
    public VideoBean videoBean;
    public TTNativeExpressAd ad;
    public int itemType;
    public static final int ITEM_TYPE_SHORT_VIDEO = 1;
    public static final int ITEM_TYPE_LONG_VIDEO = 2;
    public static final int ITEM_TYPE_Ads = 3;
}
