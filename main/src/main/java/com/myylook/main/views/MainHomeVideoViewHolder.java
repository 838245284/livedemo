package com.myylook.main.views;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.alibaba.fastjson.JSON;
import com.myylook.common.CommonAppConfig;
import com.myylook.common.bean.ConfigBean;
import com.myylook.common.bean.VideoClassBean;
import com.myylook.common.custom.CommonRefreshView;
import com.myylook.common.interfaces.OnItemClickListener;
import com.myylook.common.utils.WordUtil;
import com.myylook.main.R;
import com.myylook.main.adapter.MainHomeVideoAdapter;
import com.myylook.main.adapter.MainHomeVideoClassAdapter;
import com.myylook.main.fragment.TabFragment;
import com.myylook.video.bean.VideoBean;
import com.myylook.video.http.VideoHttpConsts;
import com.myylook.video.http.VideoHttpUtil;
import com.myylook.video.interfaces.VideoScrollDataHelper;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by cxf on 2018/9/22.
 * 首页视频
 */

public class MainHomeVideoViewHolder extends AbsMainHomeChildViewHolder implements OnItemClickListener<VideoBean> {

    private CommonRefreshView mRefreshView;
    private MainHomeVideoAdapter mAdapter;
    private VideoScrollDataHelper mVideoScrollDataHelper;
    //    private RecyclerView mClassRecyclerView;
    private MainHomeVideoClassAdapter mClassAdapter;
    private static final int ID_RECOMMEND = -1;
    private int mVideoClassId = ID_RECOMMEND;
    protected TabLayout tabLayout;
    private ViewPager viewPager;
    private ArrayList<FrameLayout> mViewList;
    private List<TabFragment> fragments;
    private List<VideoClassBean> videoClassList;

    public MainHomeVideoViewHolder(Context context, ViewGroup parentView) {
        super(context, parentView);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.view_main_home_video;
    }

    @Override
    public void init() {
        viewPager = findViewById(R.id.vp_video);
        videoClassList = new ArrayList<>();
        videoClassList.add(new VideoClassBean(ID_RECOMMEND, WordUtil.getString(R.string.recommend), true));
        ConfigBean configBean = CommonAppConfig.getInstance().getConfig();
        if (configBean != null) {
            List<VideoClassBean> list = JSON.parseArray(configBean.getVideoClass(), VideoClassBean.class);
            if (list != null && list.size() > 0) {
                videoClassList.addAll(list);
            }
        }
        tabLayout = findViewById(R.id.tabLayout);
        initTabData(videoClassList);
        tabLayout.setTabMode(TabLayout.MODE_SCROLLABLE);
        mViewList = new ArrayList<>();
        for (int i = 0; i < 4; i++) {
            FrameLayout frameLayout = new FrameLayout(mContext);
            frameLayout.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
            mViewList.add(frameLayout);
        }
        FragmentActivity activity = (FragmentActivity) mContext;
        viewPager.setAdapter(new FragmentStatePagerAdapter(activity.getSupportFragmentManager()) {
            @Override
            public Fragment getItem(int i) {
                return fragments.get(i);
            }

            @Override
            public int getCount() {
                return fragments.size();
            }

            @Nullable
            @Override
            public CharSequence getPageTitle(int position) {
                return videoClassList.get(position).getName();
            }
        });

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int i, float v, int i1) {

            }

            @Override
            public void onPageSelected(int i) {

            }

            @Override
            public void onPageScrollStateChanged(int i) {

            }
        });
        tabLayout.setupWithViewPager(viewPager);
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                int position = tab.getPosition();
                viewPager.setCurrentItem(position);
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                // 未选中tab
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
                // 再次选中tab
            }
        });
       /* mClassAdapter = new MainHomeVideoClassAdapter(mContext, videoClassList);
        mClassAdapter.setOnItemClickListener(new OnItemClickListener<VideoClassBean>() {
            @Override
            public void onItemClick(VideoClassBean bean, int position) {
                mVideoClassId = bean.getId();
                if (mRefreshView != null) {
                    mRefreshView.initData();
                }
            }
        });*/
//        EventBus.getDefault().register(this);
    }

    private static final String TAG = "MainHomeVideoViewHolder";
    private void initTabData(List<VideoClassBean> videoClassList) {
        fragments = new ArrayList<>();
        for (int i = 0; i < videoClassList.size(); i++) {
            VideoClassBean videoClassBean = videoClassList.get(i);
            TabFragment tabFragment = new TabFragment();
            int id = videoClassList.get(i).getId();
//            Log.e(TAG, "initTabData: "+id+", name:"+ videoClassBean.getName() );
            Bundle bundle = new Bundle();
            bundle.putInt("id", id);
            tabFragment.setArguments(bundle);
            fragments.add(tabFragment);
            tabLayout.addTab(tabLayout.newTab());
            tabLayout.getTabAt(i).setText(videoClassBean.getName());
        }
    }

    @Override
    public void loadData() {
    }

   /* @Subscribe(threadMode = ThreadMode.MAIN)
    public void onVideoScrollPageEvent(VideoScrollPageEvent e) {
        if (Constants.VIDEO_HOME.equals(e.getKey()) && mRefreshView != null) {
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
    }*/

    @Override
    public void onItemClick(VideoBean bean, int position) {
        /*int page = 1;
        if (mRefreshView != null) {
            page = mRefreshView.getPageCount();
        }
        if (mVideoScrollDataHelper == null) {
            mVideoScrollDataHelper = new VideoScrollDataHelper() {

                @Override
                public void loadData(int p, HttpCallback callback) {
                    if (mVideoClassId == ID_RECOMMEND) {
                        VideoHttpUtil.getHomeVideoList(p, callback);
                    } else {
                        VideoHttpUtil.getHomeVideoClassList(mVideoClassId, p, callback);
                    }
                }
            };
        }
        VideoStorge.getInstance().putDataHelper(Constants.VIDEO_HOME, mVideoScrollDataHelper);
        VideoPlayActivity.forward(mContext, position, Constants.VIDEO_HOME, page);*/
    }

    @Override
    public void release() {
        VideoHttpUtil.cancel(VideoHttpConsts.GET_HOME_VIDEO_LIST);
        VideoHttpUtil.cancel(VideoHttpConsts.GET_HOME_VIDEO_CLASS_LIST);
        EventBus.getDefault().unregister(this);
        mVideoScrollDataHelper = null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
//        release();
    }

}
