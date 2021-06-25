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


    private ImageView mAvatar;
    private TextView mName;
    private ImageView mSex;
    private TextView mID;
    private TextView mFollow;
    private TextView mFans;
    private TextView mCllocet;
    private boolean mPaused;
    private List<UserItemBean> list;

    public MainMeViewHolder(Context context, ViewGroup parentView) {
        super(context, parentView);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.view_me;
    }

    @Override
    public void init() {

        mAvatar = (ImageView) findViewById(R.id.avatar);
        mName = (TextView) findViewById(R.id.name);
        mSex = (ImageView) findViewById(R.id.sex);
        mID = (TextView) findViewById(R.id.id_val);
        mFollow = (TextView) findViewById(R.id.btn_follow);
        mFans = (TextView) findViewById(R.id.btn_fans);
        mCllocet = (TextView) findViewById(R.id.btn_collect);
        findViewById(R.id.fans).setOnClickListener(this);
        findViewById(R.id.follow).setOnClickListener(this);
        findViewById(R.id.collect).setOnClickListener(this);
        findViewById(R.id.edit).setOnClickListener(this);
        findViewById(R.id.msg).setOnClickListener(this);
        findViewById(R.id.wallet).setOnClickListener(this);
        findViewById(R.id.mingxi).setOnClickListener(this);
        findViewById(R.id.daoju).setOnClickListener(this);
        findViewById(R.id.myvideo).setOnClickListener(this);
        findViewById(R.id.my_dongtai).setOnClickListener(this);
        findViewById(R.id.my_shouyi).setOnClickListener(this);
        findViewById(R.id.my_renzheng).setOnClickListener(this);
        findViewById(R.id.my_risk).setOnClickListener(this);
        findViewById(R.id.pay_content).setOnClickListener(this);
        findViewById(R.id.my_xiaodian).setOnClickListener(this);
        findViewById(R.id.room_manage).setOnClickListener(this);
        findViewById(R.id.zhuangbeicenter).setOnClickListener(this);
        findViewById(R.id.mylevel).setOnClickListener(this);
        findViewById(R.id.invite_award).setOnClickListener(this);
        findViewById(R.id.person_setting).setOnClickListener(this);
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
        this.list = list;
        ImgLoader.displayAvatar(mContext, u.getAvatar(), mAvatar);
        mName.setText(u.getUserNiceName());
        mSex.setImageResource(CommonIconUtil.getSexIcon(u.getSex()));
        CommonAppConfig appConfig = CommonAppConfig.getInstance();
        LevelBean anchorLevelBean = appConfig.getAnchorLevel(u.getLevelAnchor());
        /*if (anchorLevelBean != null) {
            ImgLoader.display(mContext, anchorLevelBean.getThumb(), mLevelAnchor);
        }
        LevelBean levelBean = appConfig.getLevel(u.getLevel());
        if (levelBean != null) {
            ImgLoader.display(mContext, levelBean.getThumb(), mLevel);
        }*/
        mID.setText(u.getLiangNameTip());
        mFollow.setText(StringUtil.toWan(u.getFollows()));
        mFans.setText(StringUtil.toWan(u.getFans()));
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

    private void toWeb(int id){
        for (int i = 0; i < list.size(); i++) {
            UserItemBean bean = null;
            if(list.get(i).getId()==id){
                bean = list.get(i);
            }else{
                continue;
            }
            String url = bean.getHref();
            if (!TextUtils.isEmpty(url)) {
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
        if (i == R.id.edit) {
            RouteUtil.forwardUserHome(mContext, CommonAppConfig.getInstance().getUid());
        } else if (i == R.id.follow) {
            forwardFollow();
        } else if (i == R.id.fans) {
            forwardFans();
        } else if (i == R.id.msg) {
            ChatActivity.forward(mContext);
        }else if(i == R.id.collect){
            mContext.startActivity(new Intent(mContext, GoodsCollectActivity.class));
        } else if (i == R.id.wallet) {
            RouteUtil.forwardMyCoin(mContext);
        } else if (i == R.id.mingxi) {
            WebViewActivity.forward(mContext, HtmlConfig.DETAIL);
        } else if (i == R.id.daoju) {
            WebViewActivity.forward(mContext, HtmlConfig.SHOP);
        }else if(i == R.id.my_dongtai){
            ((MainActivity)mContext).getLocation();
            mContext.startActivity(new Intent(mContext, MyActiveActivity.class));
        }else if(i == R.id.myvideo){
            forwardMyVideo();
        }else if(i == R.id.my_shouyi){
            forwardProfit();
        }else if(i == R.id.my_renzheng){
            toWeb(11);
        }else if(i == R.id.my_risk){
            mContext.startActivity(new Intent(mContext, DailyTaskActivity.class));
        }else if(i == R.id.pay_content){
            forwardPayContent();
        }else if(i == R.id.my_xiaodian){
            forwardMall();
        }else if(i == R.id.room_manage){
            forwardRoomManage();
        }else if(i == R.id.zhuangbeicenter){
            toWeb(5);
        }else if(i == R.id.mylevel){
            toWeb(3);
        }else if(i == R.id.invite_award){
            toWeb(8);
        }else if(i == R.id.person_setting){
            forwardSetting();
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
