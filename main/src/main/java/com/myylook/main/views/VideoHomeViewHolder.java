package com.myylook.main.views;

import android.content.Context;
import android.support.v7.widget.GridLayoutManager;
import android.text.TextUtils;
import android.view.ViewGroup;

import com.myylook.common.CommonAppConfig;
import com.myylook.common.Constants;
import com.myylook.common.adapter.RefreshAdapter;
import com.myylook.common.custom.CommonRefreshView;
import com.myylook.common.custom.ItemDecoration;
import com.myylook.common.http.HttpCallback;
import com.myylook.common.interfaces.OnItemClickListener;
import com.myylook.common.utils.JsonUtil;
import com.myylook.live.views.AbsUserHomeViewHolder;
import com.myylook.main.R;
import com.myylook.main.adapter.VideoHomeAdapter;
import com.myylook.video.activity.VideoPlayActivity;
import com.myylook.video.bean.VideoBean;
import com.myylook.video.event.VideoDeleteEvent;
import com.myylook.video.event.VideoScrollPageEvent;
import com.myylook.video.http.VideoHttpConsts;
import com.myylook.video.http.VideoHttpUtil;
import com.myylook.video.interfaces.VideoScrollDataHelper;
import com.myylook.video.utils.VideoStorge;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.Arrays;
import java.util.List;

/**
 * Created by cxf on 2018/12/14.
 * 用户个人中心发布的视频列表
 */

public class VideoHomeViewHolder extends AbsUserHomeViewHolder implements OnItemClickListener<VideoBean> {

    private CommonRefreshView mRefreshView;
    private VideoHomeAdapter mAdapter;
    private String mToUid;
    private VideoScrollDataHelper mVideoScrollDataHelper;
    private ActionListener mActionListener;
    private String mKey;
    private int type;
    public static final int TYPE_USER = 0;
    public static final int TYPE_MINE = 1;

    public VideoHomeViewHolder(Context context, ViewGroup parentView, String toUid,int type) {
        super(context, parentView, toUid);
        this.type = type;
    }

    @Override
    protected void processArguments(Object... args) {
        mToUid = (String) args[0];
    }

    @Override
    protected int getLayoutId() {
        return R.layout.view_video_home;
    }

    @Override
    public void init() {
        if (TextUtils.isEmpty(mToUid)) {
            return;
        }
        mKey = Constants.VIDEO_USER + this.hashCode();
        mRefreshView = (CommonRefreshView) findViewById(R.id.refreshView);
        if (mToUid.equals(CommonAppConfig.getInstance().getUid())) {
            mRefreshView.setEmptyLayoutId(R.layout.view_no_data_video_home);
        } else {
            mRefreshView.setEmptyLayoutId(R.layout.view_no_data_video_home_2);
        }
        mRefreshView.setLayoutManager(new GridLayoutManager(mContext, 3, GridLayoutManager.VERTICAL, false));
        ItemDecoration decoration = new ItemDecoration(mContext, 0x00000000, 2, 0);
        decoration.setOnlySetItemOffsetsButNoDraw(true);
        mRefreshView.setItemDecoration(decoration);
        mRefreshView.setDataHelper(new CommonRefreshView.DataHelper<VideoBean>() {
            @Override
            public RefreshAdapter<VideoBean> getAdapter() {
                if (mAdapter == null) {
                    mAdapter = new VideoHomeAdapter(mContext);
                    mAdapter.setOnItemClickListener(VideoHomeViewHolder.this);
                }
                return mAdapter;
            }

            @Override
            public void loadData(int p, HttpCallback callback) {
                if(type==TYPE_USER){
                    VideoHttpUtil.getHomeVideo(mToUid, p,100, callback);
                }else{
                    VideoHttpUtil.getMyVideo(mToUid, p,100, callback);
                }
            }

            @Override
            public List<VideoBean> processData(String[] info) {
                return JsonUtil.getJsonToList(Arrays.toString(info),VideoBean.class);
            }
            @Override
            public void onRefreshSuccess(List<VideoBean> list, int listCount) {
                VideoStorge.getInstance().put(mKey, list);
            }
            @Override
            public void onRefreshFailure() {

            }
            @Override
            public void onLoadMoreSuccess(List<VideoBean> loadItemList, int loadItemCount) {

            }
            @Override
            public void onLoadMoreFailure() {

            }
        });

        mVideoScrollDataHelper = new VideoScrollDataHelper() {

            @Override
            public void loadData(int p, HttpCallback callback) {
                VideoHttpUtil.getHomeVideo(mToUid, p,100, callback);
            }
        };
        EventBus.getDefault().register(VideoHomeViewHolder.this);
    }


    @Override
    public void loadData() {
        if(isFirstLoadData()){
            mRefreshView.initData();
        }
    }

    public void release() {
        mVideoScrollDataHelper = null;
        mActionListener = null;
        VideoHttpUtil.cancel(VideoHttpConsts.GET_HOME_VIDEO);
        EventBus.getDefault().unregister(this);
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onVideoScrollPageEvent(VideoScrollPageEvent e) {
        if (!TextUtils.isEmpty(mKey) && mKey.equals(e.getKey()) && mRefreshView != null) {
            mRefreshView.setPageCount(e.getPage());
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onVideoDeleteEvent(VideoDeleteEvent e) {
        if (mAdapter != null) {
            mAdapter.deleteVideo(e.getVideoId());
            if (mAdapter.getItemCount() == 0 && mRefreshView != null) {
                mRefreshView.showEmpty();
            }
        }
        if (mActionListener != null) {
            mActionListener.onVideoDelete(1);
        }
    }

    @Override
    public void onItemClick(VideoBean bean, int position) {
        int page = 1;
        if (mRefreshView != null) {
            page = mRefreshView.getPageCount();
        }
        VideoStorge.getInstance().putDataHelper(mKey, mVideoScrollDataHelper);
        VideoPlayActivity.forward(mContext, position, mKey, page);
    }


    public interface ActionListener {
        void onVideoDelete(int deleteCount);
    }

    public void setActionListener(ActionListener actionListener) {
        mActionListener = actionListener;
    }

}
