package com.myylook.video.adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bytedance.sdk.openadsdk.DislikeInfo;
import com.bytedance.sdk.openadsdk.FilterWord;
import com.bytedance.sdk.openadsdk.PersonalizationPrompt;
import com.bytedance.sdk.openadsdk.TTAdConstant;
import com.bytedance.sdk.openadsdk.TTAdDislike;
import com.bytedance.sdk.openadsdk.TTAppDownloadListener;
import com.bytedance.sdk.openadsdk.TTNativeExpressAd;
import com.myylook.common.adapter.RefreshAdapter;
import com.myylook.common.bean.UserBean;
import com.myylook.common.glide.ImgLoader;
import com.myylook.common.views.DislikeDialog;
import com.myylook.video.R;
import com.myylook.video.bean.VideoBean;
import com.myylook.video.bean.VideoWithAds;

import java.util.List;
import java.util.Map;
import java.util.WeakHashMap;

/**
 * Created by cxf on 2018/9/26.
 */

public class VideoRecommendAdapter extends RefreshAdapter<VideoWithAds> {


    protected View.OnClickListener mOnClickListener;
    private int mTotalY;
    private int mLastTotalY;
    private Map<VideoAdVh, TTAppDownloadListener> mTTAppDownloadListenerMap = new WeakHashMap<>();
    public VideoRecommendAdapter(Context context) {
        super(context);
        mOnClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!canClick()) {
                    return;
                }
                Object tag = v.getTag();
                if (tag != null) {
                    int position = (int) tag;
                    if (mOnItemClickListener != null) {
                        VideoWithAds videoBean = mList.get(position);
                        mOnItemClickListener.onItemClick(videoBean, position);
                    }
                }
            }
        };
    }

    @Override
    public int getItemViewType(int position) {
        return mList != null ? mList.get(position).itemType : 0;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
//        if (viewType == VideoBean.ITEM_TYPE_SHORT_VIDEO) {
//            return new Vh(mInflater.inflate(R.layout.item_main_home_video, parent, false));
//        } else if (viewType == VideoBean.ITEM_TYPE_LONG_VIDEO) {
//            return new VideoLongVh(mInflater.inflate(R.layout.item_main_home_video_long, parent, false));
//        }
//        return new Vh(mInflater.inflate(R.layout.item_main_home_video, parent, false));
        if (viewType == VideoWithAds.ITEM_TYPE_Ads) {
            return new VideoAdVh(mInflater.inflate(R.layout.listitem_ad_native_express, parent, false));
        }
        return new VideoLongVh(mInflater.inflate(R.layout.item_video_recommend, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder vh, int position) {

    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder vh, int position, @NonNull List payloads) {
        Object payload = payloads.size() > 0 ? payloads.get(0) : null;
        if (vh instanceof Vh) {
            ((Vh) vh).setData(mList.get(position).videoBean, position, payload);
        } else if (vh instanceof VideoLongVh) {
            ((VideoLongVh) vh).setData(mList.get(position).videoBean, position, payload);
        }else if (vh instanceof VideoAdVh) {
            ((VideoAdVh) vh).setData(mList.get(position), position);
        }
    }

    class Vh extends RecyclerView.ViewHolder {

        ImageView mCover;
        ImageView mAvatar;
        TextView mName;
        TextView mTitle;
        TextView mNum;

        public Vh(View itemView) {
            super(itemView);
            mCover = (ImageView) itemView.findViewById(R.id.cover);
            mAvatar = (ImageView) itemView.findViewById(R.id.avatar);
            mName = (TextView) itemView.findViewById(R.id.name);
            mTitle = (TextView) itemView.findViewById(R.id.title);
            mNum = (TextView) itemView.findViewById(R.id.num);
            itemView.setOnClickListener(mOnClickListener);
        }

        protected void setData(VideoBean bean, int position, Object payload) {
            itemView.setTag(position);
            ImgLoader.display(mContext, bean.getThumb(), mCover);
            mTitle.setText(bean.getTitle());
            mNum.setText(bean.getViewNum());
            UserBean userBean = bean.getUserBean();
            if (userBean != null) {
                ImgLoader.display(mContext, userBean.getAvatar(), mAvatar);
                mName.setText(userBean.getUserNiceName());
            }
        }
    }


    class VideoLongVh extends RecyclerView.ViewHolder {

        ImageView mCover;
        ImageView mAvatar;
        TextView mName;
        TextView mTitle;
        TextView mNum;
        private final TextView mCollectionNum;
        private final TextView mLikeNum;

        public VideoLongVh(View itemView) {
            super(itemView);
            mCover = (ImageView) itemView.findViewById(R.id.cover);
            mAvatar = (ImageView) itemView.findViewById(R.id.avatar);
            mName = (TextView) itemView.findViewById(R.id.name);
            mTitle = (TextView) itemView.findViewById(R.id.title);
            mNum = (TextView) itemView.findViewById(R.id.num);
            mLikeNum = (TextView) itemView.findViewById(R.id.like_num);
            mCollectionNum = (TextView) itemView.findViewById(R.id.collection_num);

            itemView.setOnClickListener(mOnClickListener);
        }

        void setData(VideoBean bean, int position, Object payload) {
            itemView.setTag(position);
            ImgLoader.display(mContext, bean.getThumb(), mCover);
            mTitle.setText(bean.getTitle());
            mNum.setText(bean.getViewNum());
            UserBean userBean = bean.getUserBean();
            if (userBean != null) {
                ImgLoader.display(mContext, userBean.getAvatar(), mAvatar);
                mName.setText(userBean.getUserNiceName());

            }

            mCollectionNum.setText(bean.getSc_count());
            mLikeNum.setText(bean.getLikeNum());

        }
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
        recyclerView.setOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
            }

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                mTotalY += dy;
                if (mLastTotalY != mTotalY && mActionListener != null) {
                    mLastTotalY = mTotalY;
                    mActionListener.onScrollYChanged(-mLastTotalY);
                }
            }
        });
    }

    public ActionListener mActionListener;

    public void setActionListener(ActionListener actionListener) {
        mActionListener = actionListener;
    }

    public interface ActionListener {
        void onScrollYChanged(int scrollY);
    }

    class VideoAdVh extends RecyclerView.ViewHolder{

        private ViewGroup videoView;

        public VideoAdVh(View itemView) {
            super(itemView);
            videoView =  itemView.findViewById(R.id.iv_listitem_express);
        }

        void setData(VideoWithAds bean, int position) {
            bindData(itemView, this, bean.ad,position);
            if (videoView != null) {
                //获取视频播放view,该view SDK内部渲染，在媒体平台可配置视频是否自动播放等设置。
                View video = bean.ad.getExpressAdView();
                if (video != null) {
                    videoView.removeAllViews();
                    if (video.getParent() == null) {
                        videoView.addView(video);
//                            ad.render();
                    }
                }
            }
        }
    }

    /**
     * 设置广告的不喜欢，注意：强烈建议设置该逻辑，如果不设置dislike处理逻辑，则模板广告中的 dislike区域不响应dislike事件。
     *  @param ad
     * @param customStyle 是否自定义样式，true:样式自定义
     * @param position
     */
    private void bindDislike(final TTNativeExpressAd ad, boolean customStyle, final int position) {
        if (customStyle) {
            //使用自定义样式
            DislikeInfo dislikeInfo = ad.getDislikeInfo();
            if (dislikeInfo == null || dislikeInfo.getFilterWords() == null || dislikeInfo.getFilterWords().isEmpty()) {
                return;
            }
            final DislikeDialog dislikeDialog = new DislikeDialog(mContext, dislikeInfo);
            dislikeDialog.setOnDislikeItemClick(new DislikeDialog.OnDislikeItemClick() {
                @Override
                public void onItemClick(FilterWord filterWord) {
                    //屏蔽广告
                    //用户选择不喜欢原因后，移除广告展示
                    mList.remove(position);
                    notifyDataSetChanged();
                }
            });
            dislikeDialog.setOnPersonalizationPromptClick(new DislikeDialog.OnPersonalizationPromptClick() {
                @Override
                public void onClick(PersonalizationPrompt personalizationPrompt) {
                }
            });
            ad.setDislikeDialog(dislikeDialog);
            return;
        }
        //使用默认模板中默认dislike弹出样式
        ad.setDislikeCallback((Activity) mContext, new TTAdDislike.DislikeInteractionCallback() {

            @Override
            public void onShow() {

            }

            @Override
            public void onSelected(int i, String s) {
                //用户选择不喜欢原因后，移除广告展示
                mList.remove(position);
                notifyDataSetChanged();
            }

            @Override
            public void onCancel() {

            }

            @Override
            public void onRefuse() {

            }
        });
    }

    private void bindDownloadListener(final VideoAdVh adViewHolder, TTNativeExpressAd ad) {
        TTAppDownloadListener downloadListener = new TTAppDownloadListener() {
            private boolean mHasShowDownloadActive = false;

            @Override
            public void onIdle() {
                if (!isValid()) {
                    return;
                }
            }

            @SuppressLint("SetTextI18n")
            @Override
            public void onDownloadActive(long totalBytes, long currBytes, String fileName, String appName) {
                if (!isValid()) {
                    return;
                }
                if (!mHasShowDownloadActive) {
                    mHasShowDownloadActive = true;
//                    TToast.show(mContext, appName + " 下载中，点击暂停", Toast.LENGTH_LONG);
                }
            }

            @SuppressLint("SetTextI18n")
            @Override
            public void onDownloadPaused(long totalBytes, long currBytes, String fileName, String appName) {
                if (!isValid()) {
                    return;
                }
//                TToast.show(mContext, appName + " 下载暂停", Toast.LENGTH_LONG);

            }

            @Override
            public void onDownloadFailed(long totalBytes, long currBytes, String fileName, String appName) {
                if (!isValid()) {
                    return;
                }
//                TToast.show(mContext, appName + " 下载失败，重新下载", Toast.LENGTH_LONG);
            }

            @Override
            public void onInstalled(String fileName, String appName) {
                if (!isValid()) {
                    return;
                }
//                TToast.show(mContext, appName + " 安装完成，点击打开", Toast.LENGTH_LONG);
            }

            @Override
            public void onDownloadFinished(long totalBytes, String fileName, String appName) {
                if (!isValid()) {
                    return;
                }
//                TToast.show(mContext, appName + " 下载成功，点击安装", Toast.LENGTH_LONG);

            }

            @SuppressWarnings("BooleanMethodIsAlwaysInverted")
            private boolean isValid() {
                return mTTAppDownloadListenerMap.get(adViewHolder) == this;
            }
        };
        //一个ViewHolder对应一个downloadListener, isValid判断当前ViewHolder绑定的listener是不是自己
        ad.setDownloadListener(downloadListener); // 注册下载监听器
        mTTAppDownloadListenerMap.put(adViewHolder, downloadListener);
    }

    private void bindData(View convertView, final VideoAdVh adViewHolder, TTNativeExpressAd ad, int position) {
        //设置dislike弹窗
        bindDislike(ad, false,position);
        switch (ad.getInteractionType()) {
            case TTAdConstant.INTERACTION_TYPE_DOWNLOAD:
                bindDownloadListener(adViewHolder, ad);
                break;
        }
    }
}
