package com.myylook.video.activity;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.myylook.common.CommonAppConfig;
import com.myylook.common.Constants;
import com.myylook.common.HtmlConfig;
import com.myylook.common.activity.AbsActivity;
import com.myylook.common.adapter.RefreshAdapter;
import com.myylook.common.bean.UserBean;
import com.myylook.common.custom.CommonRefreshView;
import com.myylook.common.glide.ImgLoader;
import com.myylook.common.http.CommonHttpUtil;
import com.myylook.common.http.HttpCallback;
import com.myylook.common.interfaces.CommonCallback;
import com.myylook.common.interfaces.OnItemClickListener;
import com.myylook.common.mob.MobBean;
import com.myylook.common.mob.MobCallback;
import com.myylook.common.mob.MobShareUtil;
import com.myylook.common.mob.ShareData;
import com.myylook.common.utils.JsonUtil;
import com.myylook.common.utils.LogUtil;
import com.myylook.common.utils.RouteUtil;
import com.myylook.common.utils.StringUtil;
import com.myylook.common.utils.TextViewUtils;
import com.myylook.common.utils.ToastUtil;
import com.myylook.video.R;
import com.myylook.video.adapter.VideoRecommendAdapter;
import com.myylook.video.bean.VideoBean;
import com.myylook.video.bean.VideoCommentBean;
import com.myylook.video.bean.VideoWithAds;
import com.myylook.video.dialog.VideoInputDialogFragment;
import com.myylook.video.dialog.VideoShareDialogFragment;
import com.myylook.video.event.VideoLikeEvent;
import com.myylook.video.event.VideoShareEvent;
import com.myylook.video.http.VideoHttpConsts;
import com.myylook.video.http.VideoHttpUtil;
import com.myylook.video.interfaces.VideoScrollDataHelper;
import com.myylook.video.utils.VideoStorge;
import com.shuyu.gsyvideoplayer.GSYVideoManager;
import com.shuyu.gsyvideoplayer.builder.GSYVideoOptionBuilder;
import com.shuyu.gsyvideoplayer.listener.GSYSampleCallBack;
import com.shuyu.gsyvideoplayer.listener.GSYVideoProgressListener;
import com.shuyu.gsyvideoplayer.listener.LockClickListener;
import com.shuyu.gsyvideoplayer.utils.Debuger;
import com.shuyu.gsyvideoplayer.utils.OrientationUtils;
import com.shuyu.gsyvideoplayer.video.StandardGSYVideoPlayer;
import com.shuyu.gsyvideoplayer.video.base.GSYVideoPlayer;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Logger;

public class VideoLongDetailsActivity extends AbsVideoPlayActivity implements View.OnClickListener,
        OnItemClickListener<VideoWithAds> {


    private View mLayoutHeadView;
    private TextView mTvTitle;
    private TextView mTvContent;
    private TextView mBtnLike;
    private TextView mBtnDownload;
    private TextView mBtnReward;
    private TextView mBtnForward;
    private TextView mBtnCollection;
    private ImageView mAvatarView;
    private TextView mTvUserName;
    private TextView mTvCount;
    private TextView mBtnFollow;

    public static void forward(Context context, VideoBean videoBean) {
        Intent intent = new Intent(context, VideoLongDetailsActivity.class);
        intent.putExtra(Constants.VIDEO_BEAN, videoBean);
        context.startActivity(intent);
    }

    private VideoBean mVideoBean;

    private StandardGSYVideoPlayer mVideoView;
    private CommonRefreshView mRefreshView;
    private VideoRecommendAdapter mAdapter;
    private boolean isFirstLoadData = true;

    private OrientationUtils orientationUtils;

    private boolean isPlay;
    private boolean isPause;

    private List<VideoWithAds> list = new ArrayList<>();


    @Override
    protected int getLayoutId() {
        return R.layout.activity_video_long_details;
    }


    @Override
    protected void main() {

        Intent intent = getIntent();
        mVideoBean = intent.getParcelableExtra(Constants.VIDEO_BEAN);

        if (mVideoBean == null) {
            ToastUtil.show("参数错误");
            finish();
            return;
        }

        mVideoView = findViewById(R.id.videoView);
        mRefreshView = findViewById(R.id.refreshView);
        findViewById(R.id.input_tip).setOnClickListener(this);
        findViewById(R.id.btn_face).setOnClickListener(this);

        initVideo();

        initHeadView();

        initList();

        initListener();

        play();

        getVideoDetails();
    }


    private void initHeadView() {
        mLayoutHeadView = findViewById(R.id.layout_head);
        mTvTitle = mLayoutHeadView.findViewById(R.id.tv_title);
        mTvContent = mLayoutHeadView.findViewById(R.id.tv_content);
        mBtnLike = mLayoutHeadView.findViewById(R.id.btn_like);
        mBtnDownload = mLayoutHeadView.findViewById(R.id.btn_download);
        mBtnReward = mLayoutHeadView.findViewById(R.id.btn_reward);
        mBtnForward = mLayoutHeadView.findViewById(R.id.btn_forward);
        mBtnCollection = mLayoutHeadView.findViewById(R.id.btn_collection);
        mAvatarView = mLayoutHeadView.findViewById(R.id.avatar);
        mTvUserName = mLayoutHeadView.findViewById(R.id.name);
        mTvCount = mLayoutHeadView.findViewById(R.id.count);
        mBtnFollow = mLayoutHeadView.findViewById(R.id.btn_follow);

    }

    private void initListener() {
        mVideoView.getBackButton().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });


        mBtnLike.setOnClickListener(this);
        mBtnDownload.setOnClickListener(this);
        mBtnReward.setOnClickListener(this);
        mBtnForward.setOnClickListener(this);
        mBtnCollection.setOnClickListener(this);

        mTvUserName.setOnClickListener(this);
        mAvatarView.setOnClickListener(this);

        mBtnFollow.setOnClickListener(this);
    }

    private void initList() {
        mRefreshView.setEmptyLayoutId(R.layout.view_no_data_video_long_details);
        mRefreshView.setLayoutManager(new LinearLayoutManager(mContext));
        mRefreshView.setDataHelper(new CommonRefreshView.DataHelper<VideoWithAds>() {
            @Override
            public RefreshAdapter<VideoWithAds> getAdapter() {
                if (mAdapter == null) {
                    mAdapter = new VideoRecommendAdapter(mContext);
                    mAdapter.setOnItemClickListener(VideoLongDetailsActivity.this);
                }
                return mAdapter;
            }

            @Override
            public void loadData(int p, HttpCallback callback) {
                VideoHttpUtil.getHomeVideoList(p, callback);
            }

            @Override
            public List<VideoWithAds> processData(String[] info) {
                List<VideoBean> infolist = JsonUtil.getJsonToList(Arrays.toString(info), VideoBean.class);
                if (infolist != null && !infolist.isEmpty()) {
                    for (VideoBean videoBean : infolist) {
                        VideoWithAds videoWithAds = new VideoWithAds();
                        videoWithAds.videoBean = videoBean;
                        videoWithAds.itemType = VideoWithAds.ITEM_TYPE_SHORT_VIDEO;
                        list.add(videoWithAds);
                    }
                    VideoStorge.getInstance().put("TAG", infolist);
                }

                return list;

            }

            @Override
            public void onRefreshSuccess(List<VideoWithAds> list, int listCount) {
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

    private void play() {
        mVideoView.startPlayLogic();
    }

    private void initVideo() {

        if (mVideoBean == null) return;

        ImageView videoCoverView = new ImageView(mContext);
        videoCoverView.setScaleType(ImageView.ScaleType.CENTER_CROP);
        ImgLoader.display(mContext, mVideoBean.getThumb(), videoCoverView);


        //外部辅助的旋转，帮助全屏
        orientationUtils = new OrientationUtils(this, mVideoView);
        //初始化不打开外部的旋转
        orientationUtils.setEnable(false);

        GSYVideoOptionBuilder gsyVideoOption = new GSYVideoOptionBuilder();
        gsyVideoOption.setThumbImageView(videoCoverView)
                .setIsTouchWiget(true)
                .setRotateViewAuto(false)
                //仅仅横屏旋转，不变直
                //.setOnlyRotateLand(true)
                .setRotateWithSystem(true)
                .setLockLand(true)
                .setAutoFullWithSize(true)
                .setShowFullAnimation(false)
                .setNeedLockFull(true)
                .setUrl(mVideoBean.getHref())
//                .setMapHeadData(header)
                .setCacheWithPlay(false)
//                .setVideoTitle("测试视频")
                .setVideoAllCallBack(new GSYSampleCallBack() {
                    @Override
                    public void onPrepared(String url, Object... objects) {
                        Debuger.printfError("***** onPrepared **** " + objects[0]);
                        Debuger.printfError("***** onPrepared **** " + objects[1]);
                        super.onPrepared(url, objects);
                        //开始播放了才能旋转和全屏
                        orientationUtils.setEnable(mVideoView.isRotateWithSystem());
                        isPlay = true;

                    }

                    @Override
                    public void onEnterFullscreen(String url, Object... objects) {
                        super.onEnterFullscreen(url, objects);
                        Debuger.printfError("***** onEnterFullscreen **** " + objects[0]);//title
                        Debuger.printfError("***** onEnterFullscreen **** " + objects[1]);//当前全屏player
                    }

                    @Override
                    public void onAutoComplete(String url, Object... objects) {
                        super.onAutoComplete(url, objects);
                    }

                    @Override
                    public void onClickStartError(String url, Object... objects) {
                        super.onClickStartError(url, objects);
                    }

                    @Override
                    public void onQuitFullscreen(String url, Object... objects) {
                        super.onQuitFullscreen(url, objects);
                        Debuger.printfError("***** onQuitFullscreen **** " + objects[0]);//title
                        Debuger.printfError("***** onQuitFullscreen **** " + objects[1]);//当前非全屏player
                        if (orientationUtils != null) {
                            orientationUtils.backToProtVideo();
                        }
                    }
                })
                .setLockClickListener(new LockClickListener() {
                    @Override
                    public void onClick(View view, boolean lock) {
                        if (orientationUtils != null) {
                            //配合下方的onConfigurationChanged
                            orientationUtils.setEnable(!lock);
                        }
                    }
                })
                .setGSYVideoProgressListener(new GSYVideoProgressListener() {
                    @Override
                    public void onProgress(int progress, int secProgress, int currentPosition, int duration) {
                        Debuger.printfLog(" progress " + progress + " secProgress " + secProgress + " currentPosition " + currentPosition + " duration " + duration);
                    }
                })
                .build(mVideoView);

        mVideoView.getFullscreenButton().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //直接横屏
                orientationUtils.resolveByClick();

                //第一个true是否需要隐藏actionbar，第二个true是否需要隐藏statusbar
                mVideoView.startWindowFullscreen(mContext, true, true);
            }
        });


    }

    private void getVideoDetails() {
        VideoHttpUtil.getVideoDetails(mVideoBean.getId(), new HttpCallback() {
            @Override
            public void onSuccess(int code, String msg, String[] info) {
                if (code == 0 && info.length > 0) {
//                    JSONObject obj = JSON.parseObject(info[0]);
                    mVideoBean = JsonUtil.getJsonToBean(info[0], VideoBean.class);
                    updateVidoeHeadUI();
                } else {
                    ToastUtil.show(msg);
                }
            }
        });
    }

    private void updateVidoeHeadUI() {
        if (mVideoBean == null) return;
        mTvTitle.setText(mVideoBean.getTitle());

        StringBuilder contentSB = new StringBuilder();
        contentSB.append("非原创")//
                .append("  ·  ")
                .append(mVideoBean.getViewNum())
                .append("次观看")
                .append("  ·  ")
                .append(mVideoBean.getDatetime())
                .append("发布");
        mTvContent.setText(contentSB);

        boolean isLike = 1 == mVideoBean.getLike();
        boolean isFollow = "1".equals(mVideoBean.getIsattent());


        updateFollowBtn(isFollow);

        TextViewUtils.setDrawableRes(mBtnLike, isLike ? R.mipmap.ic_video_d_like_s : R.mipmap.ic_video_d_like_n, 1);
        mBtnLike.setTextColor(ContextCompat.getColor(mContext, isFollow ? R.color.global : R.color.textColor2));

        //
        TextViewUtils.setDrawableRes(mBtnCollection, isLike ? R.mipmap.ic_video_d_collection_s : R.mipmap.ic_video_d_collection_n, 1);

        UserBean userBean = mVideoBean.getUserBean();
        if (userBean != null) {
            ImgLoader.display(mContext, mVideoBean.getUserBean().getAvatar(), mAvatarView);

            mTvUserName.setText(userBean.getUserNiceName());
            int fans = userBean.getFans();
            if (fans > 1000) {
                mTvCount.setText((((float) fans) / 1000) + "k订阅者");
            } else {
                mTvCount.setText(fans + "订阅者");
            }
        }

    }

    private void updateFollowBtn(boolean isFollow) {
        mBtnFollow.setText(isFollow ? "已关注" : "关注");
        mBtnFollow.setBackgroundResource(isFollow ? R.drawable.btn_follow_vidoe_recommend : R.drawable.btn_follow_vidoe_recommend_n);
    }


    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        //如果旋转了就全屏
        if (isPlay && !isPause) {
            mVideoView.onConfigurationChanged(this, newConfig, orientationUtils, true, true);
        }
    }


    @Override
    public void onBackPressed() {

        if (orientationUtils != null) {
            orientationUtils.backToProtVideo();
        }

        if (GSYVideoManager.backFromWindowFull(this)) {
            return;
        }
        super.onBackPressed();
    }


    @Override
    protected void onPause() {
        getCurPlay().onVideoPause();
        super.onPause();
        isPause = true;
    }

    @Override
    protected void onResume() {
        getCurPlay().onVideoResume(false);
        super.onResume();
        isPause = false;
        loadData();

    }


    public void loadData() {
        if (!isFirstLoadData) {
            return;
        }
        if (mRefreshView != null) {
            mRefreshView.initData();
            isFirstLoadData = false;
        }
    }

    @Override
    public void onItemClick(VideoWithAds bean, int position) {
        VideoLongDetailsActivity.forward(mContext, bean.videoBean);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (isPlay) {
            getCurPlay().release();
        }
        //GSYPreViewManager.instance().releaseMediaPlayer();
        if (orientationUtils != null)
            orientationUtils.releaseListener();

        isFirstLoadData = true;
        VideoHttpUtil.cancel(VideoHttpConsts.GET_HOME_VIDEO_LIST);
        VideoHttpUtil.cancel(VideoHttpConsts.GET_HOME_VIDEO_CLASS_LIST);
        EventBus.getDefault().unregister(this);
    }


    private GSYVideoPlayer getCurPlay() {
        if (mVideoView.getFullWindowPlayer() != null) {
            return mVideoView.getFullWindowPlayer();
        }
        return mVideoView;
    }


    @Override
    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.btn_like) {
            clickLike();
        } else if (id == R.id.btn_download) {
        } else if (id == R.id.btn_reward) {
        } else if (id == R.id.btn_forward) {
            clickShare();
        } else if (id == R.id.btn_collection) {
        } else if (id == R.id.avatar || id == R.id.name) {
            clickAvatar();
        } else if (id == R.id.btn_follow) {
            clickFollow();
        } else if (id == R.id.input_tip) {
            openCommentInputWindow(false);
        } else if (id == R.id.btn_face) {
            openCommentInputWindow(true);

        }
    }


    /**
     * 打开评论输入框
     */
    private void openCommentInputWindow(boolean openFace) {
        if (mVideoBean != null) {
            openCommentInputWindow(openFace, mVideoBean.getId(), mVideoBean.getUid(), null);
        }
    }


    /**
     * 点击头像
     */
    public void clickAvatar() {
        if (mVideoBean != null) {
            RouteUtil.forwardUserHome(mContext, mVideoBean.getUid());
        }
    }


    /**
     * 点赞,取消点赞
     */
    private void clickLike() {
        if (mVideoBean == null) {
            return;
        }
        VideoHttpUtil.setVideoLike(mTag, mVideoBean.getId(), new HttpCallback() {
            @Override
            public void onSuccess(int code, String msg, String[] info) {
                if (code == 0 && info.length > 0) {
                    JSONObject obj = JSON.parseObject(info[0]);
                    String likeNum = obj.getString("likes");
                    int like = obj.getIntValue("islike");
                    if (mVideoBean != null) {
                        mVideoBean.setLikeNum(likeNum);
                        mVideoBean.setLike(like);
                        EventBus.getDefault().post(new VideoLikeEvent(mVideoBean.getId(), like, likeNum));
                    }
                    if (mBtnLike != null) {
                        TextViewUtils.setDrawableRes(mBtnLike, like == 1 ? R.mipmap.ic_video_d_like_s : R.mipmap.ic_video_d_like_n, 1);
                        mBtnLike.setTextColor(ContextCompat.getColor(mContext, like == 1 ? R.color.global : R.color.textColor2));

                    }

                } else {
                    ToastUtil.show(msg);
                }
            }
        });
    }

    /**
     * 点击关注按钮
     */
    private void clickFollow() {
        if (mVideoBean == null) {
            return;
        }
        final UserBean u = mVideoBean.getUserBean();
        if (u == null) {
            return;
        }
        CommonHttpUtil.setAttention(mTag, u.getId(), new CommonCallback<Integer>() {
            @Override
            public void callback(Integer attent) {
                mVideoBean.setIsattent(attent.toString());
                updateFollowBtn("1".equals(mVideoBean.getIsattent()));
            }
        });
    }


    /**
     * 点击分享按钮
     */
    private void clickShare() {
        if (mVideoBean == null) {
            return;
        }
        VideoShareDialogFragment fragment = new VideoShareDialogFragment();
        Bundle bundle = new Bundle();
        bundle.putParcelable(Constants.VIDEO_BEAN, mVideoBean);
        fragment.setArguments(bundle);
        fragment.show(((VideoLongDetailsActivity) mContext).getSupportFragmentManager(), "VideoShareDialogFragment");
    }


}
