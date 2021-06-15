package com.myylook.main.views;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.alibaba.fastjson.JSON;
import com.myylook.common.CommonAppConfig;
import com.myylook.common.bean.ConfigBean;
import com.myylook.common.bean.VideoClassBean;
import com.myylook.common.custom.CommonRefreshView;
import com.myylook.common.interfaces.OnItemClickListener;
import com.myylook.common.utils.DpUtil;
import com.myylook.common.utils.WordUtil;
import com.myylook.main.R;
import com.myylook.main.adapter.MainHomeVideoAdapter;
import com.myylook.main.adapter.MainHomeVideoClassAdapter;
import com.myylook.main.dialog.MainStartDialogFragment;
import com.myylook.main.fragment.TabFragment;
import com.myylook.video.bean.VideoBean;
import com.myylook.video.http.VideoHttpConsts;
import com.myylook.video.http.VideoHttpUtil;
import com.myylook.video.interfaces.VideoScrollDataHelper;

import net.lucode.hackware.magicindicator.MagicIndicator;
import net.lucode.hackware.magicindicator.ViewPagerHelper;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.CommonNavigator;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.CommonNavigatorAdapter;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.IPagerIndicator;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.IPagerTitleView;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.indicators.LinePagerIndicator;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.titles.ColorTransitionPagerTitleView;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.titles.SimplePagerTitleView;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by cxf on 2018/9/22.
 * 首页视频
 */

public class MainHomeVideoViewHolder extends AbsMainHomeChildViewHolder implements OnItemClickListener<VideoBean>,
        View.OnClickListener {

    private CommonRefreshView mRefreshView;
    private MainHomeVideoAdapter mAdapter;
    private VideoScrollDataHelper mVideoScrollDataHelper;
    //    private RecyclerView mClassRecyclerView;
    private MainHomeVideoClassAdapter mClassAdapter;
    private static final int ID_RECOMMEND = -1;
    private static final int ID_SHORT_VIDEO = -2;
    private int mVideoClassId = ID_RECOMMEND;
    private ViewPager viewPager;
    private ArrayList<FrameLayout> mViewList;
    private List<TabFragment> fragments;
    private List<VideoClassBean> videoClassList;
    private MagicIndicator mIndicator;
    private View mBtnMore;


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
        videoClassList.add(new VideoClassBean(ID_SHORT_VIDEO, WordUtil.getString(R.string.short_video), VideoBean.ITEM_TYPE_SHORT_VIDEO, false));
        videoClassList.add(new VideoClassBean(ID_RECOMMEND, WordUtil.getString(R.string.recommend), VideoBean.ITEM_TYPE_LONG_VIDEO, true));
        ConfigBean configBean = CommonAppConfig.getInstance().getConfig();
        if (configBean != null) {
            List<VideoClassBean> list = JSON.parseArray(configBean.getVideoClass(), VideoClassBean.class);
            if (list != null && list.size() > 0) {
                videoClassList.addAll(list);
            }
        }


        mIndicator = (MagicIndicator) findViewById(R.id.indicator);
        mBtnMore = findViewById(R.id.btn_more);
        mBtnMore.setOnClickListener(this);

        initTabData(videoClassList);

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



        initIndicator();


//        tabLayout.setupWithViewPager(viewPager);
//        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
//            @Override
//            public void onTabSelected(TabLayout.Tab tab) {
//                int position = tab.getPosition();
//                viewPager.setCurrentItem(position);
//            }
//
//            @Override
//            public void onTabUnselected(TabLayout.Tab tab) {
//                // 未选中tab
//            }
//
//            @Override
//            public void onTabReselected(TabLayout.Tab tab) {
//                // 再次选中tab
//            }
//        });
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

    private void initIndicator() {
        mBtnMore.setVisibility(videoClassList != null && videoClassList.size() > 6 ? View.VISIBLE : View.GONE);

        CommonNavigator commonNavigator = new CommonNavigator(mContext);
        commonNavigator.setAdapter(new CommonNavigatorAdapter() {

            @Override
            public int getCount() {
                return videoClassList.size();
            }

            @Override
            public IPagerTitleView getTitleView(Context context, final int index) {
                SimplePagerTitleView simplePagerTitleView = new ColorTransitionPagerTitleView(context);
                simplePagerTitleView.setNormalColor(ContextCompat.getColor(context, R.color.tab_unselect));
                simplePagerTitleView.setSelectedColor(ContextCompat.getColor(context, R.color.tab_select));
                simplePagerTitleView.setText(videoClassList.get(index).getName());
                simplePagerTitleView.setTextSize(14);
                simplePagerTitleView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (viewPager != null) {
                            viewPager.setCurrentItem(index);
                        }
                    }
                });
                return simplePagerTitleView;
            }

            @Override
            public IPagerIndicator getIndicator(Context context) {
                LinePagerIndicator linePagerIndicator = new LinePagerIndicator(context);
                linePagerIndicator.setMode(LinePagerIndicator.MODE_WRAP_CONTENT);
                linePagerIndicator.setXOffset(DpUtil.dp2px(13));
                linePagerIndicator.setRoundRadius(DpUtil.dp2px(2));
                linePagerIndicator.setColors(ContextCompat.getColor(mContext, R.color.white));
                return linePagerIndicator;
            }
        });
        mIndicator.setNavigator(commonNavigator);
        ViewPagerHelper.bind(mIndicator, viewPager);
    }

    private static final String TAG = "MainHomeVideoViewHolder";

    private void initTabData(List<VideoClassBean> videoClassList) {
        fragments = new ArrayList<>();
        for (int i = 0; i < videoClassList.size(); i++) {
            VideoClassBean videoClassBean = videoClassList.get(i);
            TabFragment tabFragment = new TabFragment();
            int id = videoClassList.get(i).getId();
            int type = videoClassList.get(i).getType();
//            Log.e(TAG, "initTabData: "+id+", name:"+ videoClassBean.getName() );
            Bundle bundle = new Bundle();
            bundle.putInt("id", id);
            bundle.putInt("type", type);
            bundle.putString("index", String.valueOf(i));
            tabFragment.setArguments(bundle);
            fragments.add(tabFragment);
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

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.btn_more) {
        }
    }
}
