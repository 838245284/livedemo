package com.yunbao.live.views;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.util.TypedValue;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.meihu.beautylibrary.filter.glfilter.resource.ResourceHelper;
import com.meihu.beautylibrary.manager.StickerDownLoader;
import com.meihu.beautylibrary.utils.DownloadUtil;
import com.meihu.beautylibrary.utils.FileUtil;
import com.tencent.live.TXLivePusher;
import com.tencent.rtmp.ITXLivePushListener;
import com.tencent.rtmp.TXLiveConstants;
import com.tencent.rtmp.TXLivePushConfig;
import com.tencent.rtmp.ui.TXCloudVideoView;
import com.yunbao.beauty.simple.SimpleBeautyEffectListener;
import com.yunbao.beauty.simple.SimpleFilterBean;
import com.yunbao.beauty.ui.bean.FilterBean;
import com.yunbao.beauty.ui.bean.StickerServiceBean;
import com.yunbao.beauty.ui.enums.FilterEnum;
import com.yunbao.beauty.ui.interfaces.DefaultBeautyEffectListener;
import com.yunbao.beauty.ui.views.BeautyDataModel;
import com.yunbao.beauty.views.MHProjectBeautyEffectListener;
import com.yunbao.common.CommonAppConfig;
import com.yunbao.common.Constants;
import com.yunbao.common.http.HttpCallback;
import com.yunbao.common.interfaces.CommonCallback;
import com.yunbao.common.utils.BitmapUtil;
import com.yunbao.common.utils.DpUtil;
import com.yunbao.common.utils.L;
import com.yunbao.common.utils.ToastUtil;
import com.yunbao.common.utils.WordUtil;
import com.yunbao.live.LiveConfig;
import com.yunbao.live.R;
import com.yunbao.live.activity.LiveActivity;
import com.yunbao.live.bean.LiveConfigBean;
import com.yunbao.live.bean.LiveReceiveGiftBean;
import com.yunbao.live.http.LiveHttpConsts;
import com.yunbao.live.http.LiveHttpUtil;

import java.io.File;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * Created by cxf on 2018/10/7.
 * 腾讯云直播推流
 */

public class LivePushTxViewHolder extends AbsLivePushViewHolder implements ITXLivePushListener {

    private TXLivePusher mLivePusher;
    private TXLivePushConfig mLivePushConfig;
    private int mMeiBaiVal;//基础美颜 美白
    private int mMoPiVal;//基础美颜 磨皮
    private int mHongRunVal;//基础美颜 红润
    private Bitmap mFilterBmp;
    //    private String mBgmPath;//背景音乐路径
    private List<StickerServiceBean> mStickerList;
    private boolean mIsPlayGiftSticker;//主播是否在播放道具礼物
    private Handler mHandler;
    private Handler mMixHandler;
    private ConcurrentLinkedQueue<LiveReceiveGiftBean> mQueue;
    private String mCurStickerName;
    private String mNoStickerName;

    protected int[] txBeautyData; //美颜参数数组
    private boolean mMirror;

    private LiveConfigBean mLiveConfigBean;

    public LivePushTxViewHolder(Context context, ViewGroup parentView, LiveConfigBean liveConfigBean) {
        super(context, parentView);
    }

    @Override
    protected void processArguments(Object... args) {
        if (args.length > 0) {
            mLiveConfigBean = (LiveConfigBean) args[0];
        }
    }

    @Override
    protected int getLayoutId() {
        return R.layout.view_live_push_tx;
    }

    @Override
    public void init() {
        super.init();
        if (mLiveConfigBean == null) {
            mLiveConfigBean = LiveConfig.getDefaultTxConfig();
        }
        mLivePusher = new TXLivePusher(mContext);
        mLivePushConfig = new TXLivePushConfig();
        mLivePushConfig.setVideoFPS(mLiveConfigBean.getTargetFps());//视频帧率 15
        mLivePushConfig.setVideoEncodeGop(mLiveConfigBean.getTargetGop());//GOP大小  1
        mLivePushConfig.setVideoBitrate(mLiveConfigBean.getVideoKBitrate());//1200
        mLivePushConfig.setVideoResolution(mLiveConfigBean.getTargetResolution());//分辨率546_960_1
        mLivePushConfig.setHardwareAcceleration(TXLiveConstants.ENCODE_VIDEO_HARDWARE);//硬件加速
        Bitmap bitmap = decodeResource(mContext.getResources(), R.mipmap.bg_live_tx_pause);
        mLivePushConfig.setPauseImg(bitmap);
        mLivePushConfig.setTouchFocus(false);//自动对焦
        //mLivePushConfig.setANS(true);//噪声抑制
        mLivePushConfig.enableAEC(true);//开启回声消除：连麦时必须开启，非连麦时不要开启
//        mLivePushConfig.setAudioSampleRate(mLiveConfigBean.getAudioKBitrate());//48000
//        mLivePushConfig.setAudioChannels(1);//声道数量
        mLivePusher.setConfig(mLivePushConfig);
        mMirror = true;
        mLivePusher.setMirror(mMirror);
        mLivePusher.setPushListener(this);
//        mLivePusher.setBGMVolume(1f);
//        mLivePusher.setMicVolume(4f);
//        mLivePusher.setBGMNofify(new TXLivePusher.OnBGMNotify() {
//            @Override
//            public void onBGMStart() {
//                L.e(TAG,"onBGMStart-----> ");
//            }
//
//            @Override
//            public void onBGMProgress(long l, long l1) {
//                L.e(TAG,"onBGMProgress-----> "+l+" -----> "+l1);
//            }
//
//            @Override
//            public void onBGMComplete(int i) {
//                L.e(TAG,"onBGMComplete----->");
//                if (!TextUtils.isEmpty(mBgmPath) && mLivePusher != null) {
//                    mLivePusher.playBGM(mBgmPath);
//                }
//            }
//        });
        if (CommonAppConfig.getInstance().isTiBeautyEnable()) {
            mLivePusher.setVideoProcessListener(new TXLivePusher.VideoCustomProcessListener() {
                @Override
                public int onTextureCustomProcess(int texture, int width, int height) {
                    int textureId = texture;
                    if (mMhSDKManager != null) {
                        try {
                            //    mMhSDKManager.setAsync(true); //如果觉得卡顿可以设置为异步人脸识别
                            long st = System.currentTimeMillis();
                            int faceScale = 4; //人脸识别对纹理的缩小倍数
                            int textureScale = 2;//图像渲染对纹理的缩小倍数
                            int align = 128; //128字节对齐，修正腾讯纹理返回大小不是该倍数造成PBO失效的问题
                            int newWidth = ((width * 4 + (align - 1)) & ~(align - 1)) / 4;
                            int newHeight = ((height * 4 + (align - 1)) & ~(align - 1)) / 4;
                            textureId = mMhSDKManager.render12(texture, newWidth, newHeight, faceScale, textureScale);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                    return textureId;
                }

                @Override
                public void onDetectFacePoints(float[] floats) {
                }

                @Override
                public void onTextureDestoryed() {
                    if (mMhSDKManager != null) {
                        mMhSDKManager.destroy();
                    }
                    mMhSDKManager = null;
                }
            });

            String beautyKey = CommonAppConfig.getInstance().getBeautyKey();
            if (!TextUtils.isEmpty(beautyKey)) {
                LiveHttpUtil.getLiveStickerList(beautyKey, new CommonCallback<String>() {
                    @Override
                    public void callback(String info) {
                        mStickerList = JSON.parseArray(info, StickerServiceBean.class);
                    }
                });
            }
            mNoStickerName = WordUtil.getString(R.string.filter_no);
            mCurStickerName = mNoStickerName;
        }
        mPreView = findViewById(R.id.camera_preview);
        mLivePusher.startCameraPreview((TXCloudVideoView) mPreView);

        setBeautyByConfig();
    }

    /*
     根据后台参数设置美颜
     */
    private void setBeautyByConfig() {
        if (CommonAppConfig.getInstance().isTiBeautyEnable()) {
            beautyNames = mContext.getResources().getStringArray(R.array.name_beauty_name_array);
            int[] beautyMap = BeautyDataModel.getInstance().getCurrentBeautyMap();
            int skinWhiting = beautyMap[0];
            int skinSmooth = beautyMap[1];
            int skinTenderness = beautyMap[2];
            txBeautyData = new int[3];
            txBeautyData[0] = skinWhiting;
            txBeautyData[1] = skinSmooth;
            txBeautyData[2] = skinTenderness;
            setTxFilter();
        }
    }

    private boolean setTxFilter() {
        return mLivePusher.setBeautyFilter(TXLiveConstants.BEAUTY_STYLE_SMOOTH, txBeautyData[0], txBeautyData[1], txBeautyData[2]);
    }


    @Override
    public DefaultBeautyEffectListener getDefaultEffectListener() {
        return new MHProjectBeautyEffectListener() {
            @Override
            public void onFilterChanged(Bitmap bitmap) {
                mLivePusher.setFilter(bitmap);

            }

            @Override
            public void onFilterChanged(FilterBean filterBean) {
                if (mContext == null || mMhSDKManager == null) return;
                FilterEnum filterEnum = filterBean.getFilterEnum();
                if (filterEnum == FilterEnum.PRO_FILTER) {
                    mMhSDKManager.changeDynamicFilter(filterBean.getmFilterName());
                } else {
                    Resources res = mContext.getResources();
                    Bitmap lookupBitmap = null;
                    switch (filterEnum) {
                        case NO_FILTER:
                            break;
                        case ROMANTIC_FILTER:
                            lookupBitmap = BitmapFactory.decodeResource(res, R.drawable.filter_langman);
                            break;
                        case FRESH_FILTER:
                            lookupBitmap = BitmapFactory.decodeResource(res, R.drawable.filter_qingxin);
                            break;
                        case BEAUTIFUL_FILTER:
                            lookupBitmap = BitmapFactory.decodeResource(res, R.drawable.filter_weimei);
                            break;
                        case PINK_FILTER:
                            lookupBitmap = BitmapFactory.decodeResource(res, R.drawable.filter_fennen);
                            break;
                        case NOSTALGIC_FILTER:
                            lookupBitmap = BitmapFactory.decodeResource(res, R.drawable.filter_huaijiu);
                            break;
                        case COOL_FILTER:
                            lookupBitmap = BitmapFactory.decodeResource(res, R.drawable.filter_qingliang);
                            break;
                        case BLUES_FILTER:
                            lookupBitmap = BitmapFactory.decodeResource(res, R.drawable.filter_landiao);
                            break;
                        case JAPANESE_FILTER:
                            lookupBitmap = BitmapFactory.decodeResource(res, R.drawable.filter_rixi);
                            break;
                    }
                    mMhSDKManager.setFilter(lookupBitmap);
                }
            }


            @Override
            public void onBeautyOrigin() {
                Log.d(TAG, "beauty_onBeautyOrigin");
                BeautyDataModel.getInstance().resetOriginBasicBeautyData();
                txBeautyData = new int[]{0, 0, 0};
                setTxFilter();
            }


            @Override
            public void onMeiBaiChanged(int progress) {
//                MHRender.setSkinWhiting(progress);
                settingMeiBai(progress);

            }

            public void settingMeiBai(int progress) {
                if (CommonAppConfig.getInstance().isTiBeautyEnable()) {
                    BeautyDataModel.getInstance().setBeautyProgress(beautyNames[0], progress);
                }

                txBeautyData[1] = progress;
                setTxFilter();
            }


            @Override
            public void onMoPiChanged(int progress) {
                settingMopi(progress);
//                MHRender.setSkinSmooth(progress);

            }

            public void settingMopi(int progress) {
                if (CommonAppConfig.getInstance().isTiBeautyEnable())
                    BeautyDataModel.getInstance().setBeautyProgress(beautyNames[1], progress);
                txBeautyData[0] = progress;
                setTxFilter();
            }


            @Override
            public void onFengNenChanged(int progress) {
//                MHRender.setSkinTenderness(progress);
                settingFenNeng(progress);
            }


            public void settingFenNeng(int progress) {
                if (CommonAppConfig.getInstance().isTiBeautyEnable())
                    BeautyDataModel.getInstance().setBeautyProgress(beautyNames[2], progress);
                txBeautyData[2] = progress;
                setTxFilter();
            }

        };
    }

    @Override
    public SimpleBeautyEffectListener getSimpleBeautyEffectListener() {
        return new SimpleBeautyEffectListener() {
            @Override
            public void onFilterChanged(SimpleFilterBean bean) {
//                L.e("-----onFilterChanged-----> " + bean.getKsyFilterType());
                if (bean == null || mLivePusher == null) {
                    return;
                }
                if (mFilterBmp != null) {
                    mFilterBmp.recycle();
                }
                int filterSrc = bean.getFilterSrc();
                if (filterSrc != 0) {
                    Bitmap bitmap = BitmapUtil.getInstance().decodeBitmap(filterSrc);
                    if (bitmap != null) {
                        mFilterBmp = bitmap;
                        mLivePusher.setFilter(bitmap);
                    } else {
                        mLivePusher.setFilter(null);
                    }
                } else {
                    mLivePusher.setFilter(null);
                }
            }

            @Override
            public void onMeiBaiChanged(int progress) {
                L.e("-----onMeiBaiChanged-----> " + progress);
                if (mLivePusher != null) {
                    int v = progress / 10;
                    if (mMeiBaiVal != v) {
                        mMeiBaiVal = v;
                        mLivePusher.setBeautyFilter(0, mMoPiVal, mMeiBaiVal, mHongRunVal);
                    }
                }
            }

            @Override
            public void onMoPiChanged(int progress) {
                L.e("-----onMoPiChanged-----> " + progress);
                if (mLivePusher != null) {
                    int v = progress / 10;
                    if (mMoPiVal != v) {
                        mMoPiVal = v;
                        mLivePusher.setBeautyFilter(0, mMoPiVal, mMeiBaiVal, mHongRunVal);
                    }
                }
            }

            @Override
            public void onHongRunChanged(int progress) {
                L.e("-----onHongRunChanged-----> " + progress);
                if (mLivePusher != null) {
                    int v = progress / 10;
                    if (mHongRunVal != v) {
                        mHongRunVal = v;
                        mLivePusher.setBeautyFilter(0, mMoPiVal, mMeiBaiVal, mHongRunVal);
                    }
                }
            }
        };
    }

    @Override
    public void changeToLeft() {
        if (mPreView != null) {
            FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) mPreView.getLayoutParams();
            params.width = mPreView.getWidth() / 2;
            params.height = DpUtil.dp2px(250);
            params.topMargin = DpUtil.dp2px(130);
            mPreView.setLayoutParams(params);
        }
    }

    @Override
    public void changeToBig() {
        if (mPreView != null) {
            FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) mPreView.getLayoutParams();
            params.width = ViewGroup.LayoutParams.MATCH_PARENT;
            params.height = ViewGroup.LayoutParams.MATCH_PARENT;
            params.topMargin = 0;
            mPreView.setLayoutParams(params);
        }
    }

    /**
     * 切换镜像
     */
    @Override
    public void togglePushMirror() {
        if (mLivePusher != null) {
            mMirror = !mMirror;
            mLivePusher.setMirror(mMirror);
            if (mMirror) {
                ToastUtil.show(R.string.live_mirror_1);
            } else {
                ToastUtil.show(R.string.live_mirror_0);
            }
        }
    }


    /**
     * 切换镜头
     */
    @Override
    public void toggleCamera() {
        if (mLivePusher != null) {
            if (mFlashOpen) {
                toggleFlash();
            }
            mLivePusher.switchCamera();
            mCameraFront = !mCameraFront;
            if (!mCameraFront) {
                mLivePusher.setMirror(false);
            } else {
                mLivePusher.setMirror(mMirror);
            }
        }
    }

    /**
     * 打开关闭闪光灯
     */
    @Override
    public void toggleFlash() {
        if (mCameraFront) {
            ToastUtil.show(R.string.live_open_flash);
            return;
        }
        if (mLivePusher != null) {
            boolean open = !mFlashOpen;
            if (mLivePusher.turnOnFlashLight(open)) {
                mFlashOpen = open;
            }
        }
    }

    /**
     * 开始推流
     *
     * @param pushUrl 推流地址
     */
    @Override
    public void startPush(String pushUrl) {
        if (mLivePusher != null) {
            mLivePusher.startPusher(pushUrl);
        }
        startCountDown();
    }


    @Override
    public void onPause() {
        mPaused = true;
        if (mStartPush && mLivePusher != null) {
            mLivePusher.pauseBGM();
            mLivePusher.pausePusher();
        }
    }

    @Override
    public void onResume() {
        if (mPaused && mStartPush && mLivePusher != null) {
            mLivePusher.resumePusher();
            mLivePusher.resumeBGM();
        }
        mPaused = false;
    }

    @Override
    public void startBgm(String path) {
        if (mLivePusher != null) {
//            boolean result = mLivePusher.playBGM(path);
//            if (result) {
//                mBgmPath = path;
//            }
            mLivePusher.playBGM(path);
        }
    }

    @Override
    public void pauseBgm() {
        if (mLivePusher != null) {
            mLivePusher.pauseBGM();
        }
    }

    @Override
    public void resumeBgm() {
        if (mLivePusher != null) {
            mLivePusher.resumeBGM();
        }
    }

    @Override
    public void stopBgm() {
        if (mLivePusher != null) {
            mLivePusher.stopBGM();
        }
//        mBgmPath = null;
    }

    @Override
    protected void onCameraRestart() {
        if (mLivePusher != null && mPreView != null) {
            mLivePusher.startCameraPreview((TXCloudVideoView) mPreView);
        }
    }

    @Override
    public void release() {
        super.release();
        if (mHandler != null) {
            mHandler.removeCallbacksAndMessages(null);
        }
        mHandler = null;
        if (mMixHandler != null) {
            mMixHandler.removeCallbacksAndMessages(null);
        }
        mMixHandler = null;
        LiveHttpUtil.cancel(LiveHttpConsts.LINK_MIC_TX_MIX_STREAM);
        if (mLivePusher != null) {
            mLivePusher.stopBGM();
            mLivePusher.stopPusher();
            mLivePusher.stopScreenCapture();
            mLivePusher.stopCameraPreview(false);
            mLivePusher.setVideoProcessListener(null);
            mLivePusher.setBGMNofify(null);
            mLivePusher.setPushListener(null);
        }
        mLivePusher = null;
        if (mLivePushConfig != null) {
            mLivePushConfig.setPauseImg(null);
        }
        mLivePushConfig = null;
    }

    @Override
    public void onPushEvent(int e, Bundle bundle) {
        L.e(TAG, "---------->" + e);
        if (e == TXLiveConstants.PUSH_ERR_OPEN_CAMERA_FAIL) {
            ToastUtil.show(R.string.live_push_failed_1);

        } else if (e == TXLiveConstants.PUSH_ERR_OPEN_MIC_FAIL) {
            ToastUtil.show(R.string.live_push_failed_2);

        } else if (e == TXLiveConstants.PUSH_ERR_NET_DISCONNECT || e == TXLiveConstants.PUSH_ERR_INVALID_ADDRESS) {
            L.e(TAG, "网络断开，推流失败------>");

        } else if (e == TXLiveConstants.PUSH_WARNING_HW_ACCELERATION_FAIL) {
            L.e(TAG, "不支持硬件加速------>");
            if (mLivePushConfig != null && mLivePusher != null) {
                mLivePushConfig.setHardwareAcceleration(TXLiveConstants.ENCODE_VIDEO_SOFTWARE);
                mLivePusher.setConfig(mLivePushConfig);
            }
        } else if (e == TXLiveConstants.PUSH_EVT_FIRST_FRAME_AVAILABLE) {//预览成功
            L.e(TAG, "mStearm--->初始化完毕");
            if (mLivePushListener != null) {
                mLivePushListener.onPreviewStart();
            }
        } else if (e == TXLiveConstants.PUSH_EVT_PUSH_BEGIN) {//推流成功
            L.e(TAG, "mStearm--->推流成功");
            if (!mStartPush) {
                mStartPush = true;
                if (mLivePushListener != null) {
                    mLivePushListener.onPushStart();
                }
            }
        }
    }

    @Override
    public void onNetStatus(Bundle bundle) {

    }

    private Bitmap decodeResource(Resources resources, int id) {
        TypedValue value = new TypedValue();
        resources.openRawResource(id, value);
        BitmapFactory.Options opts = new BitmapFactory.Options();
        opts.inTargetDensity = value.density;
        return BitmapFactory.decodeResource(resources, id, opts);
    }

    /**
     * 腾讯sdk连麦时候主播混流
     *
     * @param linkMicType 混流类型 1主播与主播连麦  0 用户与主播连麦
     * @param toStream    对方的stream
     */
    public void onLinkMicTxMixStreamEvent(int linkMicType, String toStream) {
        String txAppId = getTxAppId();
        String selfAnchorStream = getLiveStream();
        if (TextUtils.isEmpty(txAppId) || TextUtils.isEmpty(selfAnchorStream)) {
            return;
        }
        String mixParams = null;
        if (linkMicType == Constants.LINK_MIC_TYPE_NORMAL) {
            mixParams = createMixParams(txAppId, selfAnchorStream, toStream);
        } else if (linkMicType == Constants.LINK_MIC_TYPE_ANCHOR) {
            mixParams = createMixParams2(txAppId, selfAnchorStream, toStream);
        }
        if (TextUtils.isEmpty(mixParams)) {
            return;
        }
        final String finalMixParams = mixParams;
        if (mMixHandler != null) {
            mMixHandler.removeCallbacksAndMessages(null);
        }
        LiveHttpUtil.linkMicTxMixStream(finalMixParams, new HttpCallback() {
            @Override
            public void onSuccess(int code, String msg, String[] info) {
                L.e("linkMicTxMixStream---1---code---> " + code + " ---msg---> " + msg);
                if (true) {//原来这里判断是 code != 0，但还是有混流失败的情况

                    //第2次 5秒后重新请求混流接口
                    if (mMixHandler == null) {
                        mMixHandler = new Handler();
                    }
                    mMixHandler.postDelayed(new Runnable() {
                        @Override
                        public void run() {

                            LiveHttpUtil.linkMicTxMixStream(finalMixParams, new HttpCallback() {
                                @Override
                                public void onSuccess(int code, String msg, String[] info) {
                                    L.e("linkMicTxMixStream---2---code---> " + code + " ---msg---> " + msg);
                                    if (true) {//原来这里判断是 code != 0，但还是有混流失败的情况

                                        //第3次 5秒后重新请求混流接口
                                        if (mMixHandler == null) {
                                            mMixHandler = new Handler();
                                        }
                                        mMixHandler.postDelayed(new Runnable() {
                                            @Override
                                            public void run() {
                                                LiveHttpUtil.linkMicTxMixStream(finalMixParams, new HttpCallback() {
                                                    @Override
                                                    public void onSuccess(int code, String msg, String[] info) {
                                                        L.e("linkMicTxMixStream---3---code---> " + code + " ---msg---> " + msg);
                                                    }
                                                });
                                            }
                                        }, 5000);
                                    }
                                }
                            });


                        }
                    }, 5000);
                }
            }
        });
    }


//    /**
//     * 计算混流参数 观众与主播连麦
//     *
//     * @param txAppId          腾讯云appId
//     * @param selfAnchorStream 自己主播的stream
//     * @param toStream         对方的stream
//     * @return
//     */
//    private String createMixParams(String txAppId, String selfAnchorStream, String toStream) {
//        JSONObject obj = new JSONObject();
//        long timestamp = System.currentTimeMillis() / 1000;
//        obj.put("timestamp", timestamp);
//        obj.put("eventId", timestamp);
//        JSONObject interFace = new JSONObject();
//        interFace.put("interfaceName", "Mix_StreamV2");
//        JSONObject para = new JSONObject();
//        para.put("app_id", txAppId);
//        para.put("interface", "mix_streamv2.start_mix_stream_advanced");
//        para.put("mix_stream_session_id", selfAnchorStream);
//        para.put("output_stream_id", selfAnchorStream);
//        JSONArray array = new JSONArray();
//        JSONObject mainAnchor = new JSONObject();//大主播
//        mainAnchor.put("input_stream_id", selfAnchorStream);
//        JSONObject mainLayoutParams = new JSONObject();
//        mainLayoutParams.put("image_layer", 1);
//        mainAnchor.put("layout_params", mainLayoutParams);
//        array.add(mainAnchor);
//
//        if (!TextUtils.isEmpty(toStream)) {
//            JSONObject smallAnchor = new JSONObject();//小主播
//            smallAnchor.put("input_stream_id", toStream);
//            JSONObject smallLayoutParams = new JSONObject();
//            smallLayoutParams.put("image_layer", 2);
//            smallLayoutParams.put("image_width", 0.25);
//            smallLayoutParams.put("image_height", 0.21);
//            smallLayoutParams.put("location_x", 0.75);
//            smallLayoutParams.put("location_y", 0.6);
//            smallAnchor.put("layout_params", smallLayoutParams);
//            array.add(smallAnchor);
//        }
//
//        para.put("input_stream_list", array);
//        interFace.put("para", para);
//        obj.put("interface", interFace);
//        return obj.toString();
//    }
//
//
//    /**
//     * 计算混流参数 主播与主播连麦
//     *
//     * @param txAppId          腾讯云appId
//     * @param selfAnchorStream 自己主播的stream
//     * @param toStream         对方的stream
//     * @return
//     */
//    private String createMixParams2(String txAppId, String selfAnchorStream, String toStream) {
//        JSONObject obj = new JSONObject();
//        long timestamp = System.currentTimeMillis() / 1000;
//        obj.put("timestamp", timestamp);
//        obj.put("eventId", timestamp);
//        JSONObject interFace = new JSONObject();
//        interFace.put("interfaceName", "Mix_StreamV2");
//        JSONObject para = new JSONObject();
//        para.put("app_id", txAppId);
//        para.put("interface", "mix_streamv2.start_mix_stream_advanced");
//        para.put("mix_stream_session_id", selfAnchorStream);
//        para.put("output_stream_id", selfAnchorStream);
//        JSONArray array = new JSONArray();
//
//
//        if (!TextUtils.isEmpty(toStream)) {
//
//            JSONObject bg = new JSONObject();//背景
//            bg.put("input_stream_id", "canvas1");//背景的id,这个字符串随便写
//            JSONObject bgLayoutParams = new JSONObject();
//            bgLayoutParams.put("image_layer", 1);
//            bgLayoutParams.put("input_type", 3);
//            bg.put("layout_params", bgLayoutParams);
//            array.add(bg);
//
//            JSONObject selfAnchor = new JSONObject();//自己主播
//            selfAnchor.put("input_stream_id", selfAnchorStream);
//            JSONObject selfLayoutParams = new JSONObject();
//            selfLayoutParams.put("image_layer", 2);
//            selfLayoutParams.put("image_width", 0.5);
//            selfLayoutParams.put("image_height", 0.5);
//            selfLayoutParams.put("location_x", 0);
//            selfLayoutParams.put("location_y", 0.25);
//            selfAnchor.put("layout_params", selfLayoutParams);
//            array.add(selfAnchor);
//
//            JSONObject toAnchor = new JSONObject();//对方主播
//            toAnchor.put("input_stream_id", toStream);
//            JSONObject toLayoutParams = new JSONObject();
//            toLayoutParams.put("image_layer", 3);
//            toLayoutParams.put("image_width", 0.5);
//            toLayoutParams.put("image_height", 0.5);
//            toLayoutParams.put("location_x", 0.5);
//            toLayoutParams.put("location_y", 0.25);
//            toAnchor.put("layout_params", toLayoutParams);
//            array.add(toAnchor);
//        } else {
//            JSONObject mainAnchor = new JSONObject();//大主播
//            mainAnchor.put("input_stream_id", selfAnchorStream);
//            JSONObject mainLayoutParams = new JSONObject();
//            mainLayoutParams.put("image_layer", 1);
//            mainAnchor.put("layout_params", mainLayoutParams);
//            array.add(mainAnchor);
//        }
//
//        para.put("input_stream_list", array);
//        interFace.put("para", para);
//        obj.put("interface", interFace);
//        return obj.toString();
//    }


    private String createMixParams(String txAppId, String selfAnchorStream, String toStream) {
        JSONObject para = new JSONObject();
        para.put("MixStreamSessionId", selfAnchorStream);
        JSONArray array = new JSONArray();
        JSONObject mainAnchor = new JSONObject();//大主播
        mainAnchor.put("InputStreamName", selfAnchorStream);
        JSONObject mainLayoutParams = new JSONObject();
        mainLayoutParams.put("ImageLayer", 1);
        mainLayoutParams.put("InputType", 0);
        mainLayoutParams.put("LocationX", 0);
        mainLayoutParams.put("LocationY", 0);
        mainAnchor.put("LayoutParams", mainLayoutParams);
        array.add(mainAnchor);

        if (!TextUtils.isEmpty(toStream)) {
            JSONObject smallAnchor = new JSONObject();//小主播
            smallAnchor.put("InputStreamName", toStream);
            JSONObject smallLayoutParams = new JSONObject();
            smallLayoutParams.put("ImageLayer", 2);
            smallLayoutParams.put("ImageWidth", 0.25);
            smallLayoutParams.put("ImageHeight", 0.21);
            smallLayoutParams.put("LocationX", 0.75);
            smallLayoutParams.put("LocationY", 0.6);
            smallAnchor.put("LayoutParams", smallLayoutParams);
            array.add(smallAnchor);
        }

        JSONObject outPara = new JSONObject();
        outPara.put("OutputStreamName", selfAnchorStream);

        para.put("InputStreamList", array);

        para.put("OutputParams", outPara);

        JSONObject controlParams = new JSONObject();
        controlParams.put("UseMixCropCenter", 1);
        para.put("ControlParams", controlParams);

        return para.toString();
    }

    private String createMixParams2(String txAppId, String selfAnchorStream, String toStream) {
        JSONObject para = new JSONObject();
        para.put("MixStreamSessionId", selfAnchorStream);
        JSONArray array = new JSONArray();
        if (!TextUtils.isEmpty(toStream)) {
            JSONObject bg = new JSONObject();//背景
            bg.put("InputStreamName", "canvas1");//背景的id,这个字符串随便写
            JSONObject bgLayoutParams = new JSONObject();
            bgLayoutParams.put("ImageLayer", 1);
            bgLayoutParams.put("InputType", 3);
//            bgLayoutParams.put("image_width", ScreenDimenUtil.getInstance().getScreenWdith());
//            bgLayoutParams.put("image_height", ScreenDimenUtil.getInstance().getScreenHeight());
//            bgLayoutParams.put("location_x", 0);
            bg.put("LayoutParams", bgLayoutParams);
            array.add(bg);

            JSONObject selfAnchor = new JSONObject();//自己主播
            selfAnchor.put("InputStreamName", selfAnchorStream);
            JSONObject selfLayoutParams = new JSONObject();
            selfLayoutParams.put("ImageLayer", 2);
            selfLayoutParams.put("ImageWidth", 0.5);
            selfLayoutParams.put("ImageHeight", 0.5);
            selfLayoutParams.put("LocationX", 0);
            selfLayoutParams.put("LocationY", 0.25);
            selfAnchor.put("LayoutParams", selfLayoutParams);
            array.add(selfAnchor);

            JSONObject toAnchor = new JSONObject();//对方主播
            toAnchor.put("InputStreamName", toStream);
            JSONObject toLayoutParams = new JSONObject();
            toLayoutParams.put("ImageLayer", 3);
            toLayoutParams.put("ImageWidth", 0.5);
            toLayoutParams.put("ImageHeight", 0.5);
            toLayoutParams.put("LocationX", 0.5);
            toLayoutParams.put("LocationY", 0.25);
            toAnchor.put("LayoutParams", toLayoutParams);
            array.add(toAnchor);
        } else {
            JSONObject mainAnchor = new JSONObject();//大主播
            mainAnchor.put("InputStreamName", selfAnchorStream);
            JSONObject mainLayoutParams = new JSONObject();
            mainLayoutParams.put("ImageLayer", 1);
            mainAnchor.put("LayoutParams", mainLayoutParams);
            array.add(mainAnchor);
        }
        para.put("InputStreamList", array);

        JSONObject outPara = new JSONObject();
        outPara.put("OutputStreamName", selfAnchorStream);
        para.put("OutputParams", outPara);

        return para.toString();
    }


    private String getLiveStream() {
        return ((LiveActivity) mContext).getStream();
    }

    private String getTxAppId() {
        return ((LiveActivity) mContext).getTxAppId();
    }


    private StickerServiceBean findStickerBean(String stickerId) {
        if (TextUtils.isEmpty(stickerId)) {
            return null;
        }
        for (StickerServiceBean bean : mStickerList) {
            if (stickerId.equals(bean.getId())) {
                return bean;
            }
        }
        return null;
    }

    /**
     * 道具礼物贴纸
     */
    public void setLiveStickerGift(LiveReceiveGiftBean receiveGiftBean) {
        if (receiveGiftBean == null
                || receiveGiftBean.getPlayTime() <= 0
                || mMhSDKManager == null
                || mStickerList == null
                || mStickerList.size() == 0) {
            return;
        }
        StickerServiceBean targetBean = findStickerBean(receiveGiftBean.getStickerId());
        if (targetBean == null) {
            return;
        }
        if (mIsPlayGiftSticker) {
            if (mQueue == null) {
                mQueue = new ConcurrentLinkedQueue<>();
            }
            mQueue.offer(receiveGiftBean);
        } else {
            showStickerBean(targetBean, (long) (receiveGiftBean.getPlayTime() * 1000));
        }
    }

    /**
     * 道具礼物贴纸
     */
    private void showStickerBean(StickerServiceBean targetBean, final long playTime) {
        mIsPlayGiftSticker = true;
        final String name = targetBean.getName();
        final String thumb = targetBean.getThumb();
        boolean fileExist = StickerDownLoader.getStickerIsExist(mContext, name);
        if (fileExist) {
            String zipPath = com.meihu.beautylibrary.constant.Constants.VIDEO_TIE_ZHI_RESOURCE_ZIP_PATH + File.separator + name;
            ResourceHelper.addStickerSource(name, zipPath, name, thumb);
            setStickerTimeout(playTime);
            setSticker(name);
        } else {
            StickerDownLoader.downloadSticker(name, targetBean.getResource(), new DownloadUtil.Callback() {
                @Override
                public void onSuccess(File file) {
                    if (file == null) {
                        return;
                    }
                    L.e("下载贴纸---onSuccess-----> " + file.getAbsolutePath());
                    File targetDir = new File(ResourceHelper.getStickerResourceDirectory(mContext));
                    try {
                        //解压到贴纸目录
                        FileUtil.unzip(file, targetDir);
                        ResourceHelper.addStickerSource(name, file.getAbsolutePath(), name, thumb);
                        setStickerTimeout(playTime);
                        setSticker(name);
                    } catch (Exception e) {
                        getNextStickerGift();
                    }
                }

                @Override
                public void onProgress(int i) {
                    L.e("下载贴纸---onProgress-----> " + i);
                }

                @Override
                public void onError(Throwable throwable) {
                    getNextStickerGift();
                }
            });
        }
    }


    /**
     * 道具礼物贴纸
     */
    private void setStickerTimeout(long timeout) {
        if (mHandler == null) {
            mHandler = new Handler() {
                @Override
                public void handleMessage(Message msg) {
                    getNextStickerGift();
                }
            };
        }
        mHandler.sendEmptyMessageDelayed(0, timeout);
    }


    /**
     * 获取下一个道具礼物贴纸
     */
    private void getNextStickerGift() {
        if (mQueue != null) {
            LiveReceiveGiftBean nextGiftBean = mQueue.poll();
            if (nextGiftBean == null) {
                endSticker();
            } else {
                if (nextGiftBean.getPlayTime() <= 0) {
                    getNextStickerGift();
                } else {
                    StickerServiceBean nextStickerBean = findStickerBean(nextGiftBean.getStickerId());
                    long playTime = (long) (nextGiftBean.getPlayTime() * 1000);
                    if (nextStickerBean == null) {
                        getNextStickerGift();
                    } else {
                        String nextStickerName = nextStickerBean.getName();
                        if (TextUtils.isEmpty(nextStickerName)) {
                            getNextStickerGift();
                        } else {
                            if (!nextStickerName.equals(mCurStickerName)) {
                                showStickerBean(nextStickerBean, playTime);
                            } else {
                                mIsPlayGiftSticker = true;
                                setStickerTimeout(playTime);
                            }
                        }
                    }
                }
            }
        } else {
            endSticker();
        }
    }


    private void endSticker() {
        if (mIsPlayGiftSticker) {
            mIsPlayGiftSticker = false;
            String orignStickerName = BeautyDataModel.getInstance().getStickerName();
            if (TextUtils.isEmpty(orignStickerName)) {
                setSticker(mNoStickerName);
            } else {
                setSticker(orignStickerName);
            }
        }
    }


    /**
     * 道具礼物贴纸
     */
    private void setSticker(String stickerName) {
        if (!TextUtils.isEmpty(stickerName) && !stickerName.equals(mCurStickerName)) {
            mCurStickerName = stickerName;
            if (mMhSDKManager != null) {
                mMhSDKManager.setSticker(stickerName);
            }
        }
    }


    public boolean isPlayGiftSticker() {
        return mIsPlayGiftSticker;
    }
}
