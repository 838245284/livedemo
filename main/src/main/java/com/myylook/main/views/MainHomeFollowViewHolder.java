package com.myylook.main.views;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.support.v7.widget.GridLayoutManager;
import android.util.Log;
import android.view.ViewGroup;

import com.bytedance.sdk.openadsdk.AdSlot;
import com.bytedance.sdk.openadsdk.TTAdNative;
import com.bytedance.sdk.openadsdk.TTAdSdk;
import com.bytedance.sdk.openadsdk.TTNativeExpressAd;
import com.myylook.common.Constants;
import com.myylook.common.adapter.RefreshAdapter;
import com.myylook.common.custom.CommonRefreshView;
import com.myylook.common.custom.ItemDecoration;
import com.myylook.common.http.HttpCallback;
import com.myylook.common.interfaces.OnItemClickListener;
import com.myylook.common.utils.DensityUtils;
import com.myylook.common.utils.JsonUtil;
import com.myylook.main.R;
import com.myylook.main.adapter.MainHomeVideoAdapter;
import com.myylook.main.http.MainHttpConsts;
import com.myylook.main.http.MainHttpUtil;
import com.myylook.video.activity.VideoPlayActivity;
import com.myylook.video.bean.VideoBean;
import com.myylook.video.bean.VideoWithAds;
import com.myylook.video.http.VideoHttpUtil;
import com.myylook.video.interfaces.VideoScrollDataHelper;
import com.myylook.video.utils.VideoStorge;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by cxf on 2018/9/22.
 * 首页 关注
 */

public class MainHomeFollowViewHolder extends AbsMainHomeChildViewHolder implements OnItemClickListener<VideoWithAds> {

    private CommonRefreshView mRefreshView;
    private MainHomeVideoAdapter mAdapter;
    private VideoScrollDataHelper mVideoScrollDataHelper;
    private List<VideoWithAds> list = new ArrayList<>();
    String key = "gz";
    private Handler mHandler = new Handler(Looper.getMainLooper());
    private TTAdNative mTTAdNative;

    public MainHomeFollowViewHolder(Context context, ViewGroup parentView) {
        super(context, parentView);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.layout_refreshlist;
    }

    @Override
    public void init() {
        initAds();
        mRefreshView = (CommonRefreshView) findViewById(R.id.refreshView);
        mRefreshView.setEmptyLayoutId(R.layout.view_no_data_live_video);
        mRefreshView.setLayoutManager(new GridLayoutManager(mContext, 2, GridLayoutManager.VERTICAL, false));
        ItemDecoration decoration = new ItemDecoration(mContext, 0x00000000, 5, 0);
        decoration.setOnlySetItemOffsetsButNoDraw(true);
        mRefreshView.setItemDecoration(decoration);
        mRefreshView.setDataHelper(new CommonRefreshView.DataHelper<VideoWithAds>() {
            @Override
            public RefreshAdapter<VideoWithAds> getAdapter() {
                if (mAdapter == null) {
                    mAdapter = new MainHomeVideoAdapter(mContext);
                    mAdapter.setOnItemClickListener(MainHomeFollowViewHolder.this);
                }
                return mAdapter;
            }

            @Override
            public void loadData(int p, HttpCallback callback) {
                VideoHttpUtil.getFollowVideoList(p,callback);
            }

            @Override
            public List<VideoWithAds> processData(String[] info) {
                List<VideoBean> infolist = JsonUtil.getJsonToList(Arrays.toString(info), VideoBean.class);
               if (infolist != null && !infolist.isEmpty()) {
                    for (VideoBean videoBean : infolist) {
                        VideoWithAds videoWithAds = new VideoWithAds();
                        videoWithAds.videoBean = videoBean;
                        videoWithAds.itemType = VideoWithAds.ITEM_TYPE_SHORT_VIDEO;;
                        list.add(videoWithAds);
                    }

                   VideoStorge.getInstance().put(key, infolist);
                }
               return list;
            }

            @Override
            public void onRefreshSuccess(List<VideoWithAds> adapterItemList, int allItemCount) {
                if (list == null || list.isEmpty()) {
                    return;
                }
                int space = 10;
                int size = list.size();
                for (int i = 0; i < size; i += space) {
                    if (i != 0 && i % space == 0) {
                        loadListAd(i);
                    }
                }
            }

            @Override
            public void onRefreshFailure() {

            }

            @Override
            public void onLoadMoreSuccess(List<VideoWithAds> loadItemList, int loadItemCount) {

            }

            @Override
            public void onLoadMoreFailure() {

            }
        });
    }

    @Override
    public void loadData() {
        if (mRefreshView != null) {
            mRefreshView.initData();
        }
    }

    @Override
    public void release() {
        MainHttpUtil.cancel(MainHttpConsts.GET_FOLLOW);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        release();
    }

    private void initAds() {

        mTTAdNative = TTAdSdk.getAdManager().createAdNative(mContext);
        //step3:(可选，强烈建议在合适的时机调用):申请部分权限，如read_phone_state,防止获取不了imei时候，下载类广告没有填充的问题。
        TTAdSdk.getAdManager().requestPermissionIfNecessary(mContext);
    }

    private void loadListAd(final int position) {
        float expressViewWidth;
        float expressViewHeight;
        expressViewWidth = DensityUtils.getScreenWdp(mContext) / 2 - 12;
        expressViewHeight = expressViewWidth * 16f / 9 + 7;
       /* if(type==10){
        }else{
            expressViewWidth = DensityUtils.getScreenWdp(mContext);
            expressViewHeight = expressViewWidth * 3f /4;
        }*/
        //step4:创建feed广告请求类型参数AdSlot,具体参数含义参考文档
        AdSlot adSlot = new AdSlot.Builder()
                .setCodeId("946218632")
                .setExpressViewAcceptedSize(expressViewWidth, expressViewHeight) //期望模板广告view的size,单位dp
                .setAdType(AdSlot.TYPE_FEED)
                .setAdCount(1) //请求广告数量为1到3条
                .build();
        //step5:请求广告，调用feed广告异步请求接口，加载到广告后，拿到广告素材自定义渲染
        mTTAdNative.loadNativeExpressAd(adSlot, new TTAdNative.NativeExpressAdListener() {
            @Override
            public void onError(int code, String message) {
            }

            @Override
            public void onNativeExpressAdLoad(final List<TTNativeExpressAd> ads) {
                if (list != null) {
                    mHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            bindAdListener(ads, position);
                        }
                    });

                }
            }
        });
    }

    private void bindAdListener(final List<TTNativeExpressAd> ads, int position) {
        for (int i = 0; i < ads.size(); i++) {
            VideoWithAds videoWithAds = new VideoWithAds();
            TTNativeExpressAd ad = ads.get(i);
            videoWithAds.ad = ad;
            videoWithAds.itemType = VideoWithAds.ITEM_TYPE_Ads;
            List<VideoWithAds> adapterList = mAdapter.getList();
            adapterList.add(position, videoWithAds);
            ad.render();
        }
        mAdapter.notifyDataSetChanged();

       /* ad.setExpressInteractionListener(new TTNativeExpressAd.ExpressAdInteractionListener() {
            @Override
            public void onAdClicked(View view, int type) {
            }

            @Override
            public void onAdShow(View view, int type) {
            }

            @Override
            public void onRenderFail(View view, String msg, int code) {
            }

            @Override
            public void onRenderSuccess(View view, float width, float height) {
                //返回view的宽高 单位 dp
                Log.e(TAG, "onRenderSuccess: " + width + ":" + height);
            }
        });
        ad.render();*/

    }


    @Override
    public void onItemClick(VideoWithAds bean, int position) {
        int page = 1;
        if (mRefreshView != null) {
            page = mRefreshView.getPageCount();
        }
        if (mVideoScrollDataHelper == null) {
            mVideoScrollDataHelper = new VideoScrollDataHelper() {

                @Override
                public void loadData(int p, HttpCallback callback) {
                    VideoHttpUtil.getFollowVideoList(p,callback);
                }
            };
        }
        VideoStorge.getInstance().putDataHelper(Constants.VIDEO_HOME, mVideoScrollDataHelper);
        VideoPlayActivity.forward(mContext, position, key, page);
    }
}
