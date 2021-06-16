package com.myylook.main.views;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.myylook.common.custom.HomeIndicatorTitle;
import com.myylook.common.utils.WordUtil;
import com.myylook.main.R;

import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.IPagerTitleView;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.titles.ColorTransitionPagerTitleView;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.titles.SimplePagerTitleView;

/**
 * Created by cxf on 2018/9/22.
 * MainActivity 首页
 */

public class MainHomeViewHolder extends AbsMainHomeParentViewHolder {

    private MainHomeFollowViewHolder mFollowViewHolder;
    private MainHomeLiveViewHolder mLiveViewHolder;
    private MainHomeVideoViewHolder mVideoViewHolder;
    public int type;
    public static final int TYPE_HOMR = 0;
    public static final int TYPE_TEACH = 1;

    public MainHomeViewHolder(Context context, ViewGroup parentView,int type) {
        super(context, parentView);
        this.type = type;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.view_main_home;
    }

    @Override
    public void init() {
        setStatusHeight();
//        mIcons = new ImageView[4];
        super.init();

    }

    @Override
    protected void loadPageData(int position) {
        if (mViewHolders == null) {
            return;
        }
        AbsMainHomeChildViewHolder vh = mViewHolders[position];
        if (vh == null) {
            if (mViewList != null && position < mViewList.size()) {
                FrameLayout parent = mViewList.get(position);
                if (parent == null) {
                    return;
                }
                if (position == 0) {
                    if(type==0){
                        mVideoViewHolder = new MainHomeVideoViewHolder(mContext, parent);
                        vh = mVideoViewHolder;
                    }else{
                        /*mVideoTeachViewHolder = new MainTeachVideoViewHolder(mContext,parent);
                        vh = mVideoTeachViewHolder;*/
                    }
                } else if (position == 1) {
                    mFollowViewHolder = new MainHomeFollowViewHolder(mContext, parent);
                    vh = mFollowViewHolder;
                } else if (position == 2) {
                    mLiveViewHolder = new MainHomeLiveViewHolder(mContext, parent);
                    vh = mLiveViewHolder;
                } /*else if (position == 3) {
                    mNearViewHolder = new MainHomeNearViewHolder(mContext, parent);
                    vh = mNearViewHolder;
                }*/
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
        /*if (mIcons != null) {
            for (int i = 0, len = mIcons.length; i < len; i++) {
                View v = mIcons[i];
                if (v != null) {
                    if (i == position) {
                        if (v.getVisibility() != View.VISIBLE) {
                            v.setVisibility(View.VISIBLE);
                        }
                    } else {
                        if (v.getVisibility() == View.VISIBLE) {
                            v.setVisibility(View.INVISIBLE);
                        }
                    }
                }
            }
        }*/
    }

    @Override
    protected int getPageCount() {
        return 3;
    }

    @Override
    protected String[] getTitles() {
        return new String[]{
                WordUtil.getString(R.string.video),
                WordUtil.getString(R.string.follow),
                WordUtil.getString(R.string.live),
        };
    }


    @Override
    protected IPagerTitleView getIndicatorTitleView(Context context, String[] titles, final int index) {
        HomeIndicatorTitle indicatorTitle = new HomeIndicatorTitle(mContext);
        SimplePagerTitleView simplePagerTitleView = new ColorTransitionPagerTitleView(context);
        simplePagerTitleView.setLayoutParams(new FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.MATCH_PARENT));
        simplePagerTitleView.setNormalColor(ContextCompat.getColor(mContext, R.color.gray1));
        simplePagerTitleView.setSelectedColor(ContextCompat.getColor(mContext, R.color.textColor));
        simplePagerTitleView.setText(titles[index]);
        simplePagerTitleView.setTextSize(18);
        simplePagerTitleView.getPaint().setFakeBoldText(true);
        indicatorTitle.addView(simplePagerTitleView);
        indicatorTitle.setTitleView(simplePagerTitleView);
        indicatorTitle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mViewPager != null) {
                    mViewPager.setCurrentItem(index);
                }
            }
        });

        /*ImageView imageView = mIcons[index];
        if (imageView == null) {
            imageView = new ImageView(mContext);
            int dp14 = DpUtil.dp2px(14);
            FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(dp14, dp14);
            lp.topMargin = DpUtil.dp2px(5);
            lp.gravity = Gravity.RIGHT;
            imageView.setLayoutParams(lp);
            if (index == 0) {
                imageView.setImageResource(R.mipmap.icon_home_top_follow);
            } else if (index == 1) {
                imageView.setImageResource(R.mipmap.icon_home_top_live);
            } else if (index == 2) {
                imageView.setImageResource(R.mipmap.icon_home_top_video);
            } else if (index == 3) {
                imageView.setImageResource(R.mipmap.icon_home_top_near);
            }
            imageView.setVisibility(View.INVISIBLE);
            mIcons[index] = imageView;
            indicatorTitle.addView(imageView);
        }*/

        return indicatorTitle;
    }

}
