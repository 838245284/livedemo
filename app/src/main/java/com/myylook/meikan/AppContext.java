package com.myylook.meikan;

import android.text.TextUtils;

import com.alibaba.android.arouter.launcher.ARouter;
import com.bytedance.sdk.openadsdk.TTAdConfig;
import com.bytedance.sdk.openadsdk.TTAdConstant;
import com.bytedance.sdk.openadsdk.TTAdSdk;
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
        initAd();
    }

    private void initAd() {
        TTAdSdk.init(sInstance,
                new TTAdConfig.Builder()
                        .appId("5179884")
                        .useTextureView(true) //默认使用SurfaceView播放视频广告,当有SurfaceView冲突的场景，可以使用TextureView
                        .appName("APP测试媒体")
                        .titleBarTheme(TTAdConstant.TITLE_BAR_THEME_DARK)//落地页主题
                        .allowShowNotify(true) //是否允许sdk展示通知栏提示
                        .debug(true) //测试阶段打开，可以通过日志排查问题，上线时去除该调用
                        .directDownloadNetworkType(TTAdConstant.NETWORK_STATE_WIFI) //允许直接下载的网络状态集合,没有设置的网络下点击下载apk会有二次确认弹窗，弹窗中会披露应用信息
                        .supportMultiProcess(false) //是否支持多进程，true支持
                    .asyncInit(true) //是否异步初始化sdk,设置为true可以减少SDK初始化耗时。3450版本开始废弃~~
                //.httpStack(new MyOkStack3())//自定义网络库，demo中给出了okhttp3版本的样例，其余请自行开发或者咨询工作人员。
                .build());
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
