package com.myylook.main.views;

import android.content.Context;
import android.content.Intent;
import android.support.design.widget.AppBarLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.myylook.common.CommonAppConfig;
import com.myylook.common.HtmlConfig;
import com.myylook.common.activity.WebViewActivity;
import com.myylook.common.bean.LevelBean;
import com.myylook.common.bean.UserBean;
import com.myylook.common.bean.UserItemBean;
import com.myylook.common.glide.ImgLoader;
import com.myylook.common.interfaces.CommonCallback;
import com.myylook.common.interfaces.OnItemClickListener;
import com.myylook.common.utils.CommonIconUtil;
import com.myylook.common.utils.RouteUtil;
import com.myylook.common.utils.StringUtil;
import com.myylook.common.utils.WordUtil;
import com.myylook.im.activity.ChatActivity;
import com.myylook.live.activity.LiveRecordActivity;
import com.myylook.live.activity.RoomManageActivity;
import com.myylook.main.R;
import com.myylook.main.activity.DailyTaskActivity;
import com.myylook.main.activity.EditProfileActivity;
import com.myylook.main.activity.FansActivity;
import com.myylook.main.activity.FollowActivity;
import com.myylook.main.activity.MainActivity;
import com.myylook.main.activity.MyActiveActivity;
import com.myylook.main.activity.MyProfitActivity;
import com.myylook.main.activity.MyVideoActivity;
import com.myylook.main.activity.SettingActivity;
import com.myylook.main.activity.ThreeDistributActivity;
import com.myylook.main.adapter.MainMeAdapter;
import com.myylook.main.http.MainHttpConsts;
import com.myylook.main.http.MainHttpUtil;
import com.myylook.mall.activity.GoodsCollectActivity;
import com.myylook.mall.activity.PayContentActivity1;
import com.myylook.mall.activity.PayContentActivity2;

import java.util.List;

/**
 * Created by cxf on 2018/9/22.
 * 我的
 */

public class MainMeViewHolder extends AbsMainViewHolder implements OnItemClickListener<UserItemBean>, View.OnClickListener {

    private AppBarLayout mAppBarLayout;
    private TextView mTtileView;
    private ImageView mAvatar;
    private TextView mName;
    private ImageView mSex;
    private ImageView mLevelAnchor;
    private ImageView mLevel;
    private TextView mID;
    private TextView mFollow;
    private TextView mFans;
    private boolean mPaused;
    private RecyclerView mRecyclerView;
    private MainMeAdapter mAdapter;

    public MainMeViewHolder(Context context, ViewGroup parentView) {
        super(context, parentView);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.view_main_me;
    }

    @Override
    public void init() {
        mAppBarLayout = (AppBarLayout) findViewById(R.id.appBarLayout);
        mAppBarLayout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                float totalScrollRange = appBarLayout.getTotalScrollRange();
                float rate = -1 * verticalOffset / totalScrollRange * 2;
                if (rate >= 1) {
                    rate = 1;
                }
                if (mTtileView != null) {
                    mTtileView.setAlpha(rate);
                }
            }
        });

        mTtileView = (TextView) findViewById(R.id.titleView);
        mAvatar = (ImageView) findViewById(R.id.avatar);
        mName = (TextView) findViewById(R.id.name);
        mSex = (ImageView) findViewById(R.id.sex);
        mLevelAnchor = (ImageView) findViewById(R.id.level_anchor);
        mLevel = (ImageView) findViewById(R.id.level);
        mID = (TextView) findViewById(R.id.id_val);
        mFollow = (TextView) findViewById(R.id.btn_follow);
        mFans = (TextView) findViewById(R.id.btn_fans);
        mFollow.setOnClickListener(this);
        mFans.setOnClickListener(this);
        findViewById(R.id.btn_edit).setOnClickListener(this);
        findViewById(R.id.btn_msg).setOnClickListener(this);
        findViewById(R.id.btn_wallet).setOnClickListener(this);
        findViewById(R.id.btn_detail).setOnClickListener(this);
        findViewById(R.id.btn_shop).setOnClickListener(this);
        mRecyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false));
    }

    @Override
    public void onPause() {
        super.onPause();
        mPaused = true;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (isShowed() && mPaused) {
            loadData();
        }
        mPaused = false;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        MainHttpUtil.cancel(MainHttpConsts.GET_BASE_INFO);
    }

    @Override
    public void loadData() {
        if (isFirstLoadData()) {
            CommonAppConfig appConfig = CommonAppConfig.getInstance();
            UserBean u = appConfig.getUserBean();
            List<UserItemBean> list = appConfig.getUserItemList();
            if (u != null && list != null) {
                showData(u, list);
            }
        }
        MainHttpUtil.getBaseInfo(mCallback);
    }

    private CommonCallback<UserBean> mCallback = new CommonCallback<UserBean>() {
        @Override
        public void callback(UserBean bean) {
            List<UserItemBean> list = CommonAppConfig.getInstance().getUserItemList();
            if (bean != null) {
                showData(bean, list);
            }
        }
    };

    private void showData(UserBean u, List<UserItemBean> list) {
        ImgLoader.displayAvatar(mContext, u.getAvatar(), mAvatar);
        mTtileView.setText(u.getUserNiceName());
        mName.setText(u.getUserNiceName());
        mSex.setImageResource(CommonIconUtil.getSexIcon(u.getSex()));
        CommonAppConfig appConfig = CommonAppConfig.getInstance();
        LevelBean anchorLevelBean = appConfig.getAnchorLevel(u.getLevelAnchor());
        if (anchorLevelBean != null) {
            ImgLoader.display(mContext, anchorLevelBean.getThumb(), mLevelAnchor);
        }
        LevelBean levelBean = appConfig.getLevel(u.getLevel());
        if (levelBean != null) {
            ImgLoader.display(mContext, levelBean.getThumb(), mLevel);
        }
        mID.setText(u.getLiangNameTip());
        mFollow.setText(StringUtil.contact(StringUtil.toWan(u.getFollows()), " ", WordUtil.getString(R.string.follow)));
        mFans.setText(StringUtil.contact(StringUtil.toWan(u.getFans()), " ", WordUtil.getString(R.string.fans)));
        if (list != null && list.size() > 0) {
            if (mAdapter == null) {
                mAdapter = new MainMeAdapter(mContext, list);
                mAdapter.setOnItemClickListener(this);
                mRecyclerView.setAdapter(mAdapter);
            } else {
                mAdapter.setList(list);
            }
        }
    }


    @Override
    public void onItemClick(UserItemBean bean, int position) {
        if (bean.getId() == 22) {//我的小店
            forwardMall();
            return;
        } else if (bean.getId() == 24) {//付费内容
            forwardPayContent();
            return;
        }
        String url = bean.getHref();
        if (TextUtils.isEmpty(url)) {
            switch (bean.getId()) {
                case 1:
                    forwardProfit();
                    break;
                case 2:
                    forwardCoin();
                    break;
                case 13:
                    forwardSetting();
                    break;
                case 19:
                    forwardMyVideo();
                    break;
                case 20:
                    forwardRoomManage();
                    break;
                case 23://我的动态
                    ((MainActivity)mContext).getLocation();
                    mContext.startActivity(new Intent(mContext, MyActiveActivity.class));
                    break;
                case 25://每日任务
                    mContext.startActivity(new Intent(mContext, DailyTaskActivity.class));
                    break;
                case 26://我的收藏
                    mContext.startActivity(new Intent(mContext, GoodsCollectActivity.class));
                    break;
            }
        } else {
            if (!url.contains("?")) {
                url = StringUtil.contact(url, "?");
            }
            if (bean.getId() == 8) {//三级分销
                ThreeDistributActivity.forward(mContext, bean.getName(), url);
            } else {
                WebViewActivity.forward(mContext, url);
            }
        }
    }

    /**
     * 我的小店 商城
     */
    private void forwardMall() {
        UserBean u = CommonAppConfig.getInstance().getUserBean();
        if (u != null) {
            if (u.getIsOpenShop() == 0) {
                RouteUtil.forward(RouteUtil.PATH_MALL_BUYER);
            } else {
                RouteUtil.forward(RouteUtil.PATH_MALL_SELLER);
            }
        }

    }


    /**
     * 付费内容
     */
    private void forwardPayContent() {
        UserBean u = CommonAppConfig.getInstance().getUserBean();
        if (u != null) {
            if (u.getIsOpenPayContent() == 0) {
                PayContentActivity1.forward(mContext);
            } else {
                PayContentActivity2.forward(mContext);
            }
        }
    }

    @Override
    public void onClick(View v) {
        int i = v.getId();
        if (i == R.id.btn_edit) {
            RouteUtil.forwardUserHome(mContext, CommonAppConfig.getInstance().getUid());
        } else if (i == R.id.btn_follow) {
            forwardFollow();

        } else if (i == R.id.btn_fans) {
            forwardFans();

        } else if (i == R.id.btn_msg) {
            ChatActivity.forward(mContext);
        } else if (i == R.id.btn_wallet) {
            RouteUtil.forwardMyCoin(mContext);
        } else if (i == R.id.btn_detail) {
            WebViewActivity.forward(mContext, HtmlConfig.DETAIL);
        } else if (i == R.id.btn_shop) {
            WebViewActivity.forward(mContext, HtmlConfig.SHOP);
        }
    }

    /**
     * 编辑个人资料
     */
    private void forwardEditProfile() {
        mContext.startActivity(new Intent(mContext, EditProfileActivity.class));
    }

    /**
     * 我的关注
     */
    private void forwardFollow() {
        FollowActivity.forward(mContext, CommonAppConfig.getInstance().getUid());
    }

    /**
     * 我的粉丝
     */
    private void forwardFans() {
        FansActivity.forward(mContext, CommonAppConfig.getInstance().getUid());
    }

    /**
     * 直播记录
     */
    private void forwardLiveRecord() {
        LiveRecordActivity.forward(mContext, CommonAppConfig.getInstance().getUserBean());
    }

    /**
     * 我的收益
     */
    private void forwardProfit() {
        mContext.startActivity(new Intent(mContext, MyProfitActivity.class));
    }

    /**
     * 我的钻石
     */
    private void forwardCoin() {
        RouteUtil.forwardMyCoin(mContext);
    }

    /**
     * 设置
     */
    private void forwardSetting() {
        mContext.startActivity(new Intent(mContext, SettingActivity.class));
    }

    /**
     * 我的视频
     */
    private void forwardMyVideo() {
        mContext.startActivity(new Intent(mContext, MyVideoActivity.class));
    }

    /**
     * 房间管理
     */
    private void forwardRoomManage() {
        mContext.startActivity(new Intent(mContext, RoomManageActivity.class));
    }


}