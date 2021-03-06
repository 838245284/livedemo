package com.myylook.video.views;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.animation.TimeInterpolator;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.myylook.common.CommonAppConfig;
import com.myylook.common.adapter.RefreshAdapter;
import com.myylook.common.bean.UserBean;
import com.myylook.common.custom.CommonRefreshView;
import com.myylook.common.custom.MyLinearLayout3;
import com.myylook.common.http.HttpCallback;
import com.myylook.common.interfaces.OnItemClickListener;
import com.myylook.common.utils.DialogUitl;
import com.myylook.common.utils.L;
import com.myylook.common.utils.StringUtil;
import com.myylook.common.utils.ToastUtil;
import com.myylook.common.utils.WordUtil;
import com.myylook.common.views.AbsViewHolder;
import com.myylook.video.R;
import com.myylook.video.activity.AbsVideoCommentActivity;
import com.myylook.video.adapter.VideoCommentAdapter;
import com.myylook.video.bean.VideoCommentBean;
import com.myylook.video.event.VideoCommentEvent;
import com.myylook.video.http.VideoHttpConsts;
import com.myylook.video.http.VideoHttpUtil;

import org.greenrobot.eventbus.EventBus;

import java.util.Arrays;
import java.util.List;

/**
 * Created by cxf on 2018/12/3.
 * 视频评论相关
 */

public class VideoCommentViewHolder extends AbsViewHolder implements View.OnClickListener, OnItemClickListener<VideoCommentBean>, VideoCommentAdapter.ActionListener {

    private View mRoot;
    private MyLinearLayout3 mBottom;
    private CommonRefreshView mRefreshView;
    private TextView mCommentNum;
    private VideoCommentAdapter mVideoCommentAdapter;
    private String mVideoId;
    private String mVideoUid;
    private String mCommentString;
    private ObjectAnimator mShowAnimator;
    private ObjectAnimator mHideAnimator;
    private boolean mAnimating;
    private boolean mNeedRefresh;//是否需要刷新

    public VideoCommentViewHolder(Context context, ViewGroup parentView) {
        super(context, parentView);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.view_video_comment;
    }

    @Override
    public void init() {
        mRoot = findViewById(R.id.root);
        mBottom = (MyLinearLayout3) findViewById(R.id.bottom);
        int height = mBottom.getHeight2();
        mBottom.setTranslationY(height);
        mShowAnimator = ObjectAnimator.ofFloat(mBottom, "translationY", 0);
        mHideAnimator = ObjectAnimator.ofFloat(mBottom, "translationY", height);
        mShowAnimator.setDuration(200);
        mHideAnimator.setDuration(200);
        TimeInterpolator interpolator = new AccelerateDecelerateInterpolator();
        mShowAnimator.setInterpolator(interpolator);
        mHideAnimator.setInterpolator(interpolator);
        AnimatorListenerAdapter animatorListener = new AnimatorListenerAdapter() {
            @Override
            public void onAnimationStart(Animator animation) {
                mAnimating = true;
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                mAnimating = false;
                if (animation == mHideAnimator) {
                    if (mRoot != null && mRoot.getVisibility() == View.VISIBLE) {
                        mRoot.setVisibility(View.INVISIBLE);
                    }
                } else if (animation == mShowAnimator) {
                    if (mNeedRefresh) {
                        mNeedRefresh = false;
                        if (mRefreshView != null) {
                            mRefreshView.initData();
                        }
                    }
                }
            }
        };
        mShowAnimator.addListener(animatorListener);
        mHideAnimator.addListener(animatorListener);

        findViewById(R.id.root).setOnClickListener(this);
        findViewById(R.id.btn_close).setOnClickListener(this);
        findViewById(R.id.input).setOnClickListener(this);
        findViewById(R.id.btn_face).setOnClickListener(this);
        mCommentString = WordUtil.getString(R.string.video_comment);
        mCommentNum = (TextView) findViewById(R.id.comment_num);
        mRefreshView = (CommonRefreshView) findViewById(R.id.refreshView);
        mRefreshView.setEmptyLayoutId(R.layout.view_no_data_comment);
        mRefreshView.setLayoutManager(new LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false) {
            @Override
            public void onLayoutChildren(RecyclerView.Recycler recycler, RecyclerView.State state) {
                try {
                    super.onLayoutChildren(recycler, state);
                } catch (Exception e) {
                    L.e("onLayoutChildren------>" + e.getMessage());
                }
            }
        });

        mRefreshView.setDataHelper(new CommonRefreshView.DataHelper<VideoCommentBean>() {
            @Override
            public RefreshAdapter<VideoCommentBean> getAdapter() {
                if (mVideoCommentAdapter == null) {
                    mVideoCommentAdapter = new VideoCommentAdapter(mContext);
                    mVideoCommentAdapter.setOnItemClickListener(VideoCommentViewHolder.this);
                    mVideoCommentAdapter.setActionListener(VideoCommentViewHolder.this);
                }
                return mVideoCommentAdapter;
            }

            @Override
            public void loadData(int p, HttpCallback callback) {
                if (!TextUtils.isEmpty(mVideoId)) {
                    VideoHttpUtil.getVideoCommentList(mVideoId, p, callback);
                }
            }

            @Override
            public List<VideoCommentBean> processData(String[] info) {
                JSONObject obj = JSON.parseObject(info[0]);
                String commentNum = obj.getString("comments");
                EventBus.getDefault().post(new VideoCommentEvent(mVideoId, commentNum));
                if (mCommentNum != null) {
                    mCommentNum.setText(commentNum + " " + mCommentString);
                }
                List<VideoCommentBean> list = JSON.parseArray(obj.getString("commentlist"), VideoCommentBean.class);
                for (VideoCommentBean bean : list) {
                    if (bean != null) {
                        bean.setParentNode(true);
                    }
                }
                return list;
            }

            @Override
            public void onRefreshSuccess(List<VideoCommentBean> list, int listCount) {

            }

            @Override
            public void onRefreshFailure() {

            }

            @Override
            public void onLoadMoreSuccess(List<VideoCommentBean> loadItemList, int loadItemCount) {

            }

            @Override
            public void onLoadMoreFailure() {

            }
        });
    }

    public void setVideoInfo(String videoId, String videoUid) {
        if (!TextUtils.isEmpty(videoId) && !TextUtils.isEmpty(videoUid)) {
            if (!TextUtils.isEmpty(mVideoId) && !mVideoId.equals(videoId)) {
                if (mVideoCommentAdapter != null) {
                    mVideoCommentAdapter.clearData();
                }
            }
            if (!videoId.equals(mVideoId)) {
                mNeedRefresh = true;
            }
            mVideoId = videoId;
            mVideoUid = videoUid;
        }

    }

    public void showBottom() {
        if (mAnimating) {
            return;
        }
        if (mRoot != null && mRoot.getVisibility() != View.VISIBLE) {
            mRoot.setVisibility(View.VISIBLE);
        }
        if (mShowAnimator != null) {
            mShowAnimator.start();
        }
    }

    public void hideBottom() {
        if (mAnimating) {
            return;
        }
        if (mHideAnimator != null) {
            mHideAnimator.start();
        }
    }

    public void needRefresh() {
        mNeedRefresh = true;
    }

    @Override
    public void onClick(View v) {
        int i = v.getId();
        if (i == R.id.root || i == R.id.btn_close) {
            hideBottom();
        } else if (i == R.id.input) {
            if (!TextUtils.isEmpty(mVideoId) && !TextUtils.isEmpty(mVideoUid)) {
                ((AbsVideoCommentActivity) mContext).openCommentInputWindow(false, mVideoId, mVideoUid, null);
            }
        } else if (i == R.id.btn_face) {
            if (!TextUtils.isEmpty(mVideoId) && !TextUtils.isEmpty(mVideoUid)) {
                ((AbsVideoCommentActivity) mContext).openCommentInputWindow(true, mVideoId, mVideoUid, null);
            }
        }
    }


    public void release() {
        if (mShowAnimator != null) {
            mShowAnimator.cancel();
        }
        mShowAnimator = null;
        if (mHideAnimator != null) {
            mHideAnimator.cancel();
        }
        mHideAnimator = null;
        VideoHttpUtil.cancel(VideoHttpConsts.GET_VIDEO_COMMENT_LIST);
        VideoHttpUtil.cancel(VideoHttpConsts.SET_COMMENT_LIKE);
        VideoHttpUtil.cancel(VideoHttpConsts.GET_COMMENT_REPLY);
        VideoHttpUtil.cancel(VideoHttpConsts.DELETE_COMMENT);
    }

    @Override
    public void onItemClick(VideoCommentBean bean, int position) {
        if (!TextUtils.isEmpty(mVideoId) && !TextUtils.isEmpty(mVideoUid)) {
            ((AbsVideoCommentActivity) mContext).openCommentInputWindow(false, mVideoId, mVideoUid, bean);
        }
    }

    @Override
    public void onExpandClicked(final VideoCommentBean commentBean) {
        final VideoCommentBean parentNodeBean = commentBean.getParentNodeBean();
        if (parentNodeBean == null) {
            return;
        }
        VideoHttpUtil.getCommentReply(parentNodeBean.getId(), parentNodeBean.getChildPage(), new HttpCallback() {
            @Override
            public void onSuccess(int code, String msg, String[] info) {
                if (code == 0) {
                    List<VideoCommentBean> list = JSON.parseArray(Arrays.toString(info), VideoCommentBean.class);
                    if (list == null || list.size() == 0) {
                        return;
                    }
                    if (parentNodeBean.getChildPage() == 1) {
                        if (list.size() > 1) {
                            list = list.subList(1, list.size());
                        }
                    }
                    for (VideoCommentBean bean : list) {
                        bean.setParentNodeBean(parentNodeBean);
                    }
                    List<VideoCommentBean> childList = parentNodeBean.getChildList();
                    if (childList != null) {
                        childList.addAll(list);
                        if (childList.size() < parentNodeBean.getReplyNum()) {
                            parentNodeBean.setChildPage(parentNodeBean.getChildPage() + 1);
                        }
                        if (mVideoCommentAdapter != null) {
                            mVideoCommentAdapter.insertReplyList(commentBean, list.size());
                        }
                    }
                }
            }
        });
    }

    @Override
    public void onCollapsedClicked(VideoCommentBean commentBean) {
        VideoCommentBean parentNodeBean = commentBean.getParentNodeBean();
        if (parentNodeBean == null) {
            return;
        }
        List<VideoCommentBean> childList = parentNodeBean.getChildList();
        VideoCommentBean node0 = childList.get(0);
        int orignSize = childList.size();
        parentNodeBean.removeChild();
        parentNodeBean.setChildPage(1);
        if (mVideoCommentAdapter != null) {
            mVideoCommentAdapter.removeReplyList(node0, orignSize - childList.size());
        }
    }

    /**
     * 长按事件
     */
    @Override
    public void onItemLongClick(final VideoCommentBean bean) {
        String uid = CommonAppConfig.getInstance().getUid();
        Integer[] arr = null;
        //是否是自己的视频
        boolean isSelfVideo = !TextUtils.isEmpty(mVideoUid) && mVideoUid.equals(uid);
        //是否是自己的评论
        String commentUid = bean.getUid();
        boolean isSelfComment = !TextUtils.isEmpty(commentUid) && commentUid.equals(uid);
        if (isSelfVideo || isSelfComment) {
            arr = new Integer[]{R.string.copy, R.string.delete};
        } else {
            arr = new Integer[]{R.string.copy};
        }
        if (arr == null) {
            return;
        }
        DialogUitl.showStringArrayDialog(mContext, arr, new DialogUitl.StringArrayDialogCallback() {
            @Override
            public void onItemClick(String text, int tag) {
                if (tag == R.string.copy) {//复制评论
                    UserBean u = bean.getUserBean();
                    if (u != null) {
                        String content = StringUtil.contact("@", u.getUserNiceName(), ": ", bean.getContent());
                        ClipboardManager cm = (ClipboardManager) mContext.getSystemService(Context.CLIPBOARD_SERVICE);
                        ClipData clipData = ClipData.newPlainText("text", content);
                        cm.setPrimaryClip(clipData);
                        ToastUtil.show(R.string.copy_success);
                    }
                } else if (tag == R.string.delete) {
                    //删除评论
                    VideoHttpUtil.deleteComment(bean.getVideoId(), bean.getId(), bean.getUid(), new HttpCallback() {

                        @Override
                        public void onSuccess(int code, String msg, String[] info) {
                            if (code == 0) {
                                if (mRefreshView != null) {
                                    mRefreshView.initData();
                                }
                            }
                            ToastUtil.show(msg);
                        }
                    });

                }
            }
        });
    }

}
