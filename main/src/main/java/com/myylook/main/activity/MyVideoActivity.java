package com.myylook.main.activity;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.myylook.common.CommonAppConfig;
import com.myylook.common.activity.AbsActivity;
import com.myylook.common.adapter.ViewPagerAdapter;
import com.myylook.common.bean.UserBean;
import com.myylook.common.utils.DpUtil;
import com.myylook.common.utils.WordUtil;
import com.myylook.live.views.AbsUserHomeViewHolder;
import com.myylook.live.views.LiveRecordViewHolder;
import com.myylook.main.R;
import com.myylook.main.views.ActiveHomeViewHolder;
import com.myylook.main.views.MyLongVideoViewHolder;
import com.myylook.main.views.UserHomeDetailViewHolder;
import com.myylook.main.views.UserLongVideoViewHolder;
import com.myylook.main.views.VideoHomeViewHolder;

import net.lucode.hackware.magicindicator.MagicIndicator;
import net.lucode.hackware.magicindicator.ViewPagerHelper;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.CommonNavigator;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.CommonNavigatorAdapter;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.IPagerIndicator;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.IPagerTitleView;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.indicators.LinePagerIndicator;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.titles.ColorTransitionPagerTitleView;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.titles.SimplePagerTitleView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by cxf on 2018/12/14.
 */

public class MyVideoActivity extends AbsActivity {

    private VideoHomeViewHolder mVideoHomeViewHolder;
    private MyLongVideoViewHolder mVideoLongViewHolder;
    private MyLongVideoViewHolder mVideoTeachViewHolder;

    private List<FrameLayout> mViewList;
    private ViewPager mViewPager;
    private MagicIndicator mIndicator;
    private AbsUserHomeViewHolder[] mViewHolders;
    public static final int TYPE_LONG = 98;
    public static final int TYPE_TEACH = 99;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_my_video;
    }

    @Override
    protected void main() {
        setTitle(WordUtil.getString(R.string.video_my_video));
        mViewPager = (ViewPager) findViewById(R.id.viewPager);
        mViewList = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            FrameLayout frameLayout = new FrameLayout(mContext);
            frameLayout.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
            mViewList.add(frameLayout);
        }
        final String[] titles = new String[]{"短视频","长视频","教学视频"};
        mViewHolders = new AbsUserHomeViewHolder[3];
        mViewPager.setAdapter(new ViewPagerAdapter(mViewList));
        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                loadPageData(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        mIndicator = (MagicIndicator) findViewById(R.id.indicator);
        CommonNavigator commonNavigator = new CommonNavigator(mContext);
        commonNavigator.setAdapter(new CommonNavigatorAdapter() {

            @Override
            public int getCount() {
                return titles.length;
            }

            @Override
            public IPagerTitleView getTitleView(Context context, final int index) {
                SimplePagerTitleView simplePagerTitleView = new ColorTransitionPagerTitleView(context);
                simplePagerTitleView.setNormalColor(ContextCompat.getColor(mContext, R.color.gray1));
                simplePagerTitleView.setSelectedColor(ContextCompat.getColor(mContext, R.color.textColor));
                simplePagerTitleView.setText(titles[index]);
                simplePagerTitleView.setTextSize(14);
                simplePagerTitleView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (mViewPager != null) {
                            mViewPager.setCurrentItem(index);
                        }
                    }
                });
                return simplePagerTitleView;
            }

            @Override
            public IPagerIndicator getIndicator(Context context) {
                LinePagerIndicator linePagerIndicator = new LinePagerIndicator(context);
                linePagerIndicator.setMode(LinePagerIndicator.MODE_EXACTLY);
                linePagerIndicator.setLineWidth(DpUtil.dp2px(20));
                linePagerIndicator.setLineHeight(DpUtil.dp2px(2));
                linePagerIndicator.setRoundRadius(DpUtil.dp2px(1));
                linePagerIndicator.setColors(ContextCompat.getColor(mContext, R.color.global));
                return linePagerIndicator;
            }

        });
        commonNavigator.setAdjustMode(true);
        mIndicator.setNavigator(commonNavigator);
        ViewPagerHelper.bind(mIndicator, mViewPager);
        loadPageData(0);
       /* mVideoHomeViewHolder = new VideoHomeViewHolder(mContext, (ViewGroup) findViewById(R.id.container), CommonAppConfig.getInstance().getUid());
        mVideoHomeViewHolder.addToParent();
        mVideoHomeViewHolder.subscribeActivityLifeCycle();
        mVideoHomeViewHolder.loadData();*/
    }

    private void loadPageData(int position) {
        if (mViewHolders == null) {
            return;
        }
        AbsUserHomeViewHolder vh = mViewHolders[position];
        if (vh == null) {
            if (mViewList != null && position < mViewList.size()) {
                FrameLayout parent = mViewList.get(position);
                if (parent == null) {
                    return;
                }
                String uid = CommonAppConfig.getInstance().getUid();
                if (position == 0) {
                    mVideoHomeViewHolder = new VideoHomeViewHolder(mContext, parent, uid,VideoHomeViewHolder.TYPE_MINE);
                    vh = mVideoHomeViewHolder;

                }else if (position == 1) {
                    mVideoLongViewHolder = new MyLongVideoViewHolder(this, parent, uid,TYPE_LONG);
                    mVideoLongViewHolder.setActionListener(new MyLongVideoViewHolder.ActionListener() {
                        @Override
                        public void onVideoDelete(int deleteCount) {
                        }
                    });
                    vh = mVideoLongViewHolder;
                }else if (position == 2) {
                    mVideoTeachViewHolder = new MyLongVideoViewHolder(mContext, parent, uid,TYPE_TEACH);
                    mVideoTeachViewHolder.setActionListener(new MyLongVideoViewHolder.ActionListener() {
                        @Override
                        public void onVideoDelete(int deleteCount) {
                        }
                    });
                    vh = mVideoTeachViewHolder;
                }
                if (vh == null) {
                    return;
                }
                mViewHolders[position] = vh;
                vh.addToParent();
                vh.subscribeActivityLifeCycle();
            }
        }
        if (vh != null) {
            vh.loadData();
        }
    }

    private void release(){
        if(mVideoHomeViewHolder!=null){
            mVideoHomeViewHolder.release();
        }
        mVideoHomeViewHolder=null;
    }

    @Override
    public void onBackPressed() {
        release();
        super.onBackPressed();
    }

    @Override
    protected void onDestroy() {
        release();
        super.onDestroy();
    }
}
