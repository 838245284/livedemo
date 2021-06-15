package com.myylook.main.fragment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.google.gson.jpush.Gson;
import com.google.gson.jpush.reflect.TypeToken;
import com.myylook.common.Constants;
import com.myylook.common.R;
import com.myylook.common.adapter.RefreshAdapter;
import com.myylook.common.custom.CommonRefreshView;
import com.myylook.common.custom.ItemDecoration;
import com.myylook.common.http.HttpCallback;
import com.myylook.common.interfaces.OnItemClickListener;
import com.myylook.common.utils.DpUtil;
import com.myylook.common.utils.JsonUtil;
import com.myylook.common.utils.LogUtil;
import com.myylook.main.adapter.MainHomeVideoAdapter;
import com.myylook.video.activity.VideoPlayActivity;
import com.myylook.video.bean.VideoBean;
import com.myylook.video.http.VideoHttpConsts;
import com.myylook.video.http.VideoHttpUtil;
import com.myylook.video.interfaces.VideoScrollDataHelper;
import com.myylook.video.utils.VideoStorge;

import org.greenrobot.eventbus.EventBus;

import java.util.Arrays;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link TabFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class TabFragment extends Fragment implements OnItemClickListener<VideoBean> {

    private CommonRefreshView mRefreshView;
    private MainHomeVideoAdapter mAdapter;
    private static final int ID_RECOMMEND = -1;
    private static final int ID_SHORT_VIDEO = -2;

    private int mVideoClassId = ID_RECOMMEND;
    /**
     * 视频类型 1：短视频 2：长视频
     */
    private int mItemType = VideoBean.ITEM_TYPE_SHORT_VIDEO;

    private VideoScrollDataHelper mVideoScrollDataHelper;
    private boolean isFirstLoadData = true;

    private static final String TAG = "TabFragment";
    private String index;
    private List<VideoBean> list;

    public static TabFragment newInstance(String label) {
        Bundle args = new Bundle();
        args.putString("label", label);
        TabFragment fragment = new TabFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.layout_refreshlist, container, false);
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mVideoClassId = getArguments().getInt("id");
        index = getArguments().getString("index");
        mItemType = getArguments().getInt("type", VideoBean.ITEM_TYPE_SHORT_VIDEO);
        mRefreshView = view.findViewById(R.id.refreshView);
        mRefreshView.setEmptyLayoutId(R.layout.view_no_data_live_video);

        if (mItemType == VideoBean.ITEM_TYPE_SHORT_VIDEO) {
            mRefreshView.setLayoutManager(new GridLayoutManager(getContext(), 2, GridLayoutManager.VERTICAL, false));
            ItemDecoration decoration = new ItemDecoration(getContext(), 0x00000000, 5, 0);
            decoration.setOnlySetItemOffsetsButNoDraw(true);
            mRefreshView.setItemDecoration(decoration);
            FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            layoutParams.leftMargin = DpUtil.dp2px(5);
            layoutParams.rightMargin = DpUtil.dp2px(5);
            mRefreshView.setLayoutParams(layoutParams);
        } else if (mItemType == VideoBean.ITEM_TYPE_LONG_VIDEO) {
            mRefreshView.setLayoutManager(new LinearLayoutManager(getContext()));
            mRefreshView.setLayoutParams(new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        }
        mRefreshView.setDataHelper(new CommonRefreshView.DataHelper<VideoBean>() {
            @Override
            public RefreshAdapter<VideoBean> getAdapter() {
                if (mAdapter == null) {
                    mAdapter = new MainHomeVideoAdapter(getContext());
                    mAdapter.setOnItemClickListener(TabFragment.this);
                }
                return mAdapter;
            }

            @Override
            public void loadData(int p, HttpCallback callback) {
                if (mVideoClassId == ID_RECOMMEND) {
                    VideoHttpUtil.getHomeVideoList(p, callback);
                } else if (mVideoClassId == ID_SHORT_VIDEO) {
                    VideoHttpUtil.getHomeShortVideoList(p, callback);
                } else {
                    VideoHttpUtil.getHomeVideoClassList(mVideoClassId, p, callback);
                }
            }

            @Override
            public List<VideoBean> processData(String[] info) {
                list = JsonUtil.getJsonToList(Arrays.toString(info), VideoBean.class);
                Gson gson=new Gson();
                if (list != null && !list.isEmpty()) {
                    for (VideoBean videoBean : list) {
                        videoBean.setItemType(mItemType);
                    }
                    VideoStorge.getInstance().put(String.valueOf(index), list);
                }
                return list;

            }

            @Override
            public void onRefreshSuccess(List<VideoBean> list, int listCount) {

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
    }

    @Override
    public void onResume() {
        super.onResume();
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
    public void onItemClick(VideoBean bean, int position) {
        int page = 1;
        if (mRefreshView != null) {
            page = mRefreshView.getPageCount();
        }
        if (mVideoScrollDataHelper == null) {
            mVideoScrollDataHelper = new VideoScrollDataHelper() {

                @Override
                public void loadData(int p, HttpCallback callback) {
                    if (mVideoClassId == ID_RECOMMEND) {
                        VideoHttpUtil.getHomeVideoList(p, callback);
                    } else if (mVideoClassId == ID_SHORT_VIDEO) {
                        VideoHttpUtil.getHomeShortVideoList(p, callback);
                    } else {
                        VideoHttpUtil.getHomeVideoClassList(mVideoClassId, p, callback);
                    }
                }
            };
        }

        VideoStorge.getInstance().putDataHelper(Constants.VIDEO_HOME, mVideoScrollDataHelper);
        VideoPlayActivity.forward(getContext(), position, index, page);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        isFirstLoadData = true;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        VideoHttpUtil.cancel(VideoHttpConsts.GET_HOME_VIDEO_LIST);
        VideoHttpUtil.cancel(VideoHttpConsts.GET_HOME_VIDEO_CLASS_LIST);
        EventBus.getDefault().unregister(this);
        mVideoScrollDataHelper = null;
    }
}