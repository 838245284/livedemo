package com.myylook.meikan;

import android.text.TextUtils;

import com.alibaba.android.arouter.launcher.ARouter;
import com.meihu.beautylibrary.MHSDK;
import com.mob.MobSDK;
import com.qiniu.pili.droid.shortvideo.PLShortVideoEnv;
import com.tencent.bugly.crashreport.CrashReport;
import com.tencent.live.TXLiveBase;
import com.myylook.beauty.ui.views.BeautyDataModel;
import com.myylook.common.CommonAppConfig;
import com.myylook.common.CommonAppContext;
import com.myylook.common.bean.MeiyanConfig;
import com.myylook.common.utils.DecryptUtil;
import com.myylook.common.utils.L;
import com.myylook.im.utils.ImMessageUtil;
import com.myylook.im.utils.ImPushUtil;


/**
 * Created by cxf on 2017/8/3.
 */

public class AppContext extends CommonAppContext {

    public static AppContext sInstance;
    private boolean mBeautyInited;

    @Override
    public void onCreate() {
        super.onCreate();
        sInstance = this;
        //腾讯云直播鉴权url
        String liveLicenceUrl = "http://license.vod2.myqcloud.com/license/v1/1ebb5a4157a9a818802d468d603bee65/TXUgcSDK.licence";
        //腾讯云直播鉴权key
        String liveKey = "826969b36cd7fee009f3d74eb5b6d888";
        //腾讯云视频鉴权url
        String ugcLicenceUrl = "http://license.vod2.myqcloud.com/license/v1/26a8cf861fa26e80ca417516c/TXUgcSDK.licence";
        //腾讯云视频鉴权key
        String ugcKey = "512e9149eccf4ed21531ed2c8";
        TXLiveBase.getInstance().setLicence(this, liveLicenceUrl, liveKey, ugcLicenceUrl, ugcKey);
        L.setDeBug(BuildConfig.DEBUG);
        //初始化腾讯bugly
        CrashReport.initCrashReport(this);
        CrashReport.setAppVersion(this, CommonAppConfig.getInstance().getVersion());
        //初始化ShareSdk
        MobSDK.init(this);
        //初始化极光推送
        ImPushUtil.getInstance().init(this);
        //初始化极光IM
        ImMessageUtil.getInstance().init();

        //初始化 ARouter
        if (BuildConfig.DEBUG) {
            ARouter.openLog();
            ARouter.openDebug();
        }
        ARouter.init(this);
//        if (!LeakCanary.isInAnalyzerProcess(this)) {
//            LeakCanary.install(this);
//        }
        PLShortVideoEnv.init(this);
    }

    /**
     * 初始化美狐
     */

    public void initBeautySdk(String beautyKey) {
        if (CommonAppConfig.isYunBaoApp()) {
            beautyKey = DecryptUtil.decrypt(beautyKey);
        }
        CommonAppConfig.getInstance().setBeautyKey(beautyKey);
        if (!TextUtils.isEmpty(beautyKey)) {
            if (!mBeautyInited) {
                mBeautyInited = true;
                MHSDK.getInstance().init(this, beautyKey);
                CommonAppConfig.getInstance().setTiBeautyEnable(true);

                //根据后台配置设置美颜参数
                MeiyanConfig meiyanConfig = CommonAppConfig.getInstance().getConfig().parseMeiyanConfig();
                int[] dataArray = meiyanConfig.getDataArray();
                BeautyDataModel.getInstance().setBeautyDataMap(dataArray);

                L.e("美狐初始化------->" + beautyKey);
            }
        } else {
            CommonAppConfig.getInstance().setTiBeautyEnable(false);
        }
    }

}
