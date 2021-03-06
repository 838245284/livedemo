package com.myylook.live.activity;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.annotation.NonNull;
import android.support.v4.view.PagerAdapter;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.FrameLayout;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.opensource.svgaplayer.SVGAImageView;
import com.myylook.common.CommonAppConfig;
import com.myylook.common.Constants;
import com.myylook.common.HtmlConfig;
import com.myylook.common.bean.GoodsBean;
import com.myylook.common.custom.MyViewPager;
import com.myylook.common.dialog.LiveChargeDialogFragment;
import com.myylook.common.http.CommonHttpConsts;
import com.myylook.common.http.CommonHttpUtil;
import com.myylook.common.http.HttpCallback;
import com.myylook.common.pay.PayCallback;
import com.myylook.common.pay.PayPresenter;
import com.myylook.common.utils.DialogUitl;
import com.myylook.common.utils.L;
import com.myylook.common.utils.RandomUtil;
import com.myylook.common.utils.RouteUtil;
import com.myylook.common.utils.ToastUtil;
import com.myylook.common.utils.WordUtil;
import com.myylook.game.bean.GameParam;
import com.myylook.game.event.GameWindowChangedEvent;
import com.myylook.game.event.OpenGameChargeEvent;
import com.myylook.game.util.GamePresenter;
import com.myylook.live.R;
import com.myylook.live.adapter.LiveRoomScrollAdapter;
import com.myylook.live.bean.LiveBean;
import com.myylook.live.bean.LiveGuardInfo;
import com.myylook.live.bean.LiveUserGiftBean;
import com.myylook.live.dialog.LiveGiftDialogFragment;
import com.myylook.live.dialog.LiveGoodsDialogFragment;
import com.myylook.live.event.LinkMicTxAccEvent;
import com.myylook.live.event.LiveRoomChangeEvent;
import com.myylook.live.http.LiveHttpConsts;
import com.myylook.live.http.LiveHttpUtil;
import com.myylook.live.presenter.LiveLinkMicAnchorPresenter;
import com.myylook.live.presenter.LiveLinkMicPkPresenter;
import com.myylook.live.presenter.LiveLinkMicPresenter;
import com.myylook.live.presenter.LiveRoomCheckLivePresenter2;
import com.myylook.live.socket.GameActionListenerImpl;
import com.myylook.live.socket.SocketChatUtil;
import com.myylook.live.socket.SocketClient;
import com.myylook.live.utils.LiveStorge;
import com.myylook.live.views.LiveAudienceViewHolder;
import com.myylook.live.views.LiveEndViewHolder;
import com.myylook.live.views.LivePlayKsyViewHolder;
import com.myylook.live.views.LivePlayTxViewHolder;
import com.myylook.live.views.LiveRoomPlayViewHolder;
import com.myylook.live.views.LiveRoomViewHolder;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.List;

import pl.droidsonroids.gif.GifImageView;

/**
 * Created by cxf on 2018/10/10.
 */

public class LiveAudienceActivity extends LiveActivity {

    private static final String TAG = "LiveAudienceActivity";

    public static void forward(Context context, LiveBean liveBean, int liveType, int liveTypeVal, String key, int position, int liveSdk) {
        Intent intent = new Intent(context, LiveAudienceActivity.class);
        intent.putExtra(Constants.LIVE_BEAN, liveBean);
        intent.putExtra(Constants.LIVE_TYPE, liveType);
        intent.putExtra(Constants.LIVE_TYPE_VAL, liveTypeVal);
        intent.putExtra(Constants.LIVE_KEY, key);
        intent.putExtra(Constants.LIVE_POSITION, position);
        intent.putExtra(Constants.LIVE_SDK, liveSdk);
        context.startActivity(intent);
    }

    private boolean mUseScroll = true;
    private String mKey;
    private int mPosition;
    private RecyclerView mRecyclerView;
    private LiveRoomScrollAdapter mRoomScrollAdapter;
    private View mMainContentView;
    private MyViewPager mViewPager;
    private ViewGroup mSecondPage;//?????????????????????
    private FrameLayout mContainerWrap;
    private LiveRoomPlayViewHolder mLivePlayViewHolder;
    private LiveAudienceViewHolder mLiveAudienceViewHolder;
    private boolean mEnd;
    private boolean mCoinNotEnough;//????????????
    private LiveRoomCheckLivePresenter2 mCheckLivePresenter;
    private boolean mLighted;
    private PayPresenter mPayPresenter;

    @Override
    protected void getInentParams() {
        Intent intent = getIntent();
        mLiveSDK = intent.getIntExtra(Constants.LIVE_SDK, Constants.LIVE_SDK_KSY);
        L.e(TAG, "??????sdk----->" + (mLiveSDK == Constants.LIVE_SDK_KSY ? "?????????" : "?????????"));
        mKey = intent.getStringExtra(Constants.LIVE_KEY);
        if (TextUtils.isEmpty(mKey)) {
            mUseScroll = false;
        }
        mPosition = intent.getIntExtra(Constants.LIVE_POSITION, 0);
        mLiveType = intent.getIntExtra(Constants.LIVE_TYPE, Constants.LIVE_TYPE_NORMAL);
        mLiveTypeVal = intent.getIntExtra(Constants.LIVE_TYPE_VAL, 0);
        mLiveBean = intent.getParcelableExtra(Constants.LIVE_BEAN);
    }

    private boolean isUseScroll() {
        return mUseScroll && CommonAppConfig.LIVE_ROOM_SCROLL;
    }

    @Override
    public <T extends View> T findViewById(@IdRes int id) {
        if (isUseScroll()) {
            if (mMainContentView != null) {
                return mMainContentView.findViewById(id);
            }
        }
        return super.findViewById(id);
    }

    @Override
    protected int getLayoutId() {
        if (isUseScroll()) {
            return R.layout.activity_live_audience_2;
        }
        return R.layout.activity_live_audience;
    }

    public void setScrollFrozen(boolean frozen) {
        if (mRecyclerView != null) {
            mRecyclerView.setLayoutFrozen(frozen);
        }
    }

    @Override
    protected void main() {
        setVolumeControlStream(AudioManager.STREAM_MUSIC);
        if (isUseScroll()) {
            mRecyclerView = super.findViewById(R.id.recyclerView);
            mRecyclerView.setHasFixedSize(true);
            mRecyclerView.setLayoutManager(new LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false));
            mMainContentView = LayoutInflater.from(mContext).inflate(R.layout.activity_live_audience, null, false);
        }
        super.main();
        if (mLiveSDK == Constants.LIVE_SDK_TX || isUseScroll()) {
            //?????????????????????
            mLivePlayViewHolder = new LivePlayTxViewHolder(mContext, (ViewGroup) findViewById(R.id.play_container));
        } else {
            //??????????????????
            mLivePlayViewHolder = new LivePlayKsyViewHolder(mContext, (ViewGroup) findViewById(R.id.play_container));
        }
        mLivePlayViewHolder.addToParent();
        mLivePlayViewHolder.subscribeActivityLifeCycle();
        mViewPager = (MyViewPager) findViewById(R.id.viewPager);
        mSecondPage = (ViewGroup) LayoutInflater.from(mContext).inflate(R.layout.view_audience_page, mViewPager, false);
        mContainerWrap = mSecondPage.findViewById(R.id.container_wrap);
        mContainer = mSecondPage.findViewById(R.id.container);
        mLiveRoomViewHolder = new LiveRoomViewHolder(mContext, mContainer, (GifImageView) mSecondPage.findViewById(R.id.gift_gif), (SVGAImageView) mSecondPage.findViewById(R.id.gift_svga), mContainerWrap);
        mLiveRoomViewHolder.addToParent();
        mLiveRoomViewHolder.subscribeActivityLifeCycle();
        mLiveAudienceViewHolder = new LiveAudienceViewHolder(mContext, mContainer);
        mLiveAudienceViewHolder.addToParent();
        mLiveAudienceViewHolder.setUnReadCount(getImUnReadCount());
        mLiveBottomViewHolder = mLiveAudienceViewHolder;
        mViewPager.setAdapter(new PagerAdapter() {

            @Override
            public int getCount() {
                return 2;
            }

            @Override
            public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
                return view == object;
            }

            @NonNull
            @Override
            public Object instantiateItem(@NonNull ViewGroup container, int position) {
                if (position == 0) {
                    View view = new View(mContext);
                    view.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
                    container.addView(view);
                    return view;
                } else {
                    container.addView(mSecondPage);
                    return mSecondPage;
                }
            }

            @Override
            public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
            }
        });
        mViewPager.setCurrentItem(1);
        mLiveLinkMicPresenter = new LiveLinkMicPresenter(mContext, mLivePlayViewHolder, false, mLiveSDK, mLiveAudienceViewHolder.getContentView());
        mLiveLinkMicAnchorPresenter = new LiveLinkMicAnchorPresenter(mContext, mLivePlayViewHolder, false, mLiveSDK, null);
        mLiveLinkMicPkPresenter = new LiveLinkMicPkPresenter(mContext, mLivePlayViewHolder, false, null);
        if (isUseScroll()) {
            List<LiveBean> list = LiveStorge.getInstance().get(mKey);
            mRoomScrollAdapter = new LiveRoomScrollAdapter(mContext, list, mPosition);
            mRoomScrollAdapter.setActionListener(new LiveRoomScrollAdapter.ActionListener() {
                @Override
                public void onPageSelected(LiveBean liveBean, ViewGroup container, boolean first) {
                    L.e(TAG, "onPageSelected----->" + liveBean);
                    if (mMainContentView != null && container != null) {
                        ViewParent parent = mMainContentView.getParent();
                        if (parent != null) {
                            ViewGroup viewGroup = (ViewGroup) parent;
                            if (viewGroup != container) {
                                viewGroup.removeView(mMainContentView);
                                container.addView(mMainContentView);
                            }
                        } else {
                            container.addView(mMainContentView);
                        }
                    }
                    if (!first) {
                        checkLive(liveBean);
                    }
                }

                @Override
                public void onPageOutWindow(String liveUid) {
                    L.e(TAG, "onPageOutWindow----->" + liveUid);
                    if (TextUtils.isEmpty(mLiveUid) || mLiveUid.equals(liveUid)) {
                        LiveHttpUtil.cancel(LiveHttpConsts.CHECK_LIVE);
                        LiveHttpUtil.cancel(LiveHttpConsts.ENTER_ROOM);
                        LiveHttpUtil.cancel(LiveHttpConsts.ROOM_CHARGE);
                        clearRoomData();
                    }
                }

                @Override
                public void onPageInWindow(String liveThumb) {
                    if (mLivePlayViewHolder != null) {
                        mLivePlayViewHolder.setCover(liveThumb);
                    }
                }
            });
            mRecyclerView.setAdapter(mRoomScrollAdapter);
        }
        setLiveRoomData(mLiveBean);
        enterRoom();
    }


    public void scrollNextPosition() {
        if (mRoomScrollAdapter != null) {
            mRoomScrollAdapter.scrollNextPosition();
        }
    }


    private void setLiveRoomData(LiveBean liveBean) {
        mLiveBean = liveBean;
        mLiveUid = liveBean.getUid();
        mStream = liveBean.getStream();
        mLivePlayViewHolder.setCover(liveBean.getThumb());
//        mLivePlayViewHolder.play(liveBean.getPull());
        mLiveAudienceViewHolder.setLiveInfo(mLiveUid, mStream);
        mLiveRoomViewHolder.setAvatar(liveBean.getAvatar());
        mLiveRoomViewHolder.setAnchorLevel(liveBean.getLevelAnchor());
        mLiveRoomViewHolder.setName(liveBean.getUserNiceName());
        mLiveRoomViewHolder.setRoomNum(liveBean.getLiangNameTip());
        mLiveRoomViewHolder.setTitle(liveBean.getTitle());
        mLiveLinkMicPkPresenter.setLiveUid(mLiveUid);
        mLiveLinkMicPresenter.setLiveUid(mLiveUid);
        mLiveAudienceViewHolder.setShopOpen(liveBean.getIsshop() == 1);
    }

    private void clearRoomData() {
        if (mSocketClient != null) {
            mSocketClient.disConnect();
        }
        mSocketClient = null;
        if (mLivePlayViewHolder != null) {
            mLivePlayViewHolder.stopPlay();
        }
        if (mLiveRoomViewHolder != null) {
            mLiveRoomViewHolder.clearData();
        }
        if (mGamePresenter != null) {
            mGamePresenter.clearGame();
        }
        if (mLiveEndViewHolder != null) {
            mLiveEndViewHolder.removeFromParent();
        }
        if (mLiveLinkMicPresenter != null) {
            mLiveLinkMicPresenter.clearData();
        }
        if (mLiveLinkMicAnchorPresenter != null) {
            mLiveLinkMicAnchorPresenter.clearData();
        }
        if (mLiveLinkMicPkPresenter != null) {
            mLiveLinkMicPkPresenter.clearData();
        }
        setPkBgVisible(false);
        mLighted = false;
    }

    private void checkLive(LiveBean bean) {
        if (mCheckLivePresenter == null) {
            mCheckLivePresenter = new LiveRoomCheckLivePresenter2(mContext, new LiveRoomCheckLivePresenter2.ActionListener() {
                @Override
                public void onLiveRoomChanged(LiveBean liveBean, int liveType, int liveTypeVal, int liveSdk) {
                    if (liveBean == null) {
                        return;
                    }
                    setLiveRoomData(liveBean);
                    mLiveType = liveType;
                    mLiveTypeVal = liveTypeVal;
                    if (mRoomScrollAdapter != null) {
                        mRoomScrollAdapter.hideCover();
                    }
                    enterRoom();
                }
            });
        }
        mCheckLivePresenter.checkLive(bean);
    }


    private void enterRoom() {
        LiveHttpUtil.enterRoom(mLiveUid, mStream, new HttpCallback() {
            @Override
            public void onSuccess(int code, String msg, String[] info) {
                if (code == 0 && info.length > 0) {
                    JSONObject obj = JSON.parseObject(info[0]);
                    if (mLivePlayViewHolder != null) {
                        mLivePlayViewHolder.play(obj.getString("pull"));
                    }
                    mDanmuPrice = obj.getString("barrage_fee");
                    mSocketUserType = obj.getIntValue("usertype");
                    mChatLevel = obj.getIntValue("speak_limit");
                    mDanMuLevel = obj.getIntValue("barrage_limit");
                    //??????socket
                    mSocketClient = new SocketClient(obj.getString("chatserver"), LiveAudienceActivity.this);
                    if (mLiveLinkMicPresenter != null) {
                        mLiveLinkMicPresenter.setSocketClient(mSocketClient);
                    }
                    mSocketClient.connect(mLiveUid, mStream);
                    if (mLiveRoomViewHolder != null) {
                        mLiveRoomViewHolder.setLiveInfo(mLiveUid, mStream, obj.getIntValue("userlist_time") * 1000);
                        mLiveRoomViewHolder.setVotes(obj.getString("votestotal"));
                        mLiveRoomViewHolder.setAttention(obj.getIntValue("isattention"));
                        List<LiveUserGiftBean> list = JSON.parseArray(obj.getString("userlists"), LiveUserGiftBean.class);
                        mLiveRoomViewHolder.setUserList(list);

                        mLiveRoomViewHolder.startRefreshUserList();
                        if (mLiveType == Constants.LIVE_TYPE_TIME) {//????????????
                            mLiveRoomViewHolder.startRequestTimeCharge();
                        }
                    }
                    //?????????????????????????????????????????????
                    String linkMicUid = obj.getString("linkmic_uid");
                    String linkMicPull = obj.getString("linkmic_pull");
                    if (!TextUtils.isEmpty(linkMicUid) && !"0".equals(linkMicUid) && !TextUtils.isEmpty(linkMicPull)) {
                        if (mLiveSDK != Constants.LIVE_SDK_TX && mLiveLinkMicPresenter != null) {
                            mLiveLinkMicPresenter.onLinkMicPlay(linkMicUid, linkMicPull);
                        }
                    }
                    //???????????????????????????
                    JSONObject pkInfo = JSON.parseObject(obj.getString("pkinfo"));
                    if (pkInfo != null) {
                        String pkUid = pkInfo.getString("pkuid");
                        if (!TextUtils.isEmpty(pkUid) && !"0".equals(pkUid)) {
                            if (mLiveSDK != Constants.LIVE_SDK_TX) {
                                String pkPull = pkInfo.getString("pkpull");
                                if (!TextUtils.isEmpty(pkPull) && mLiveLinkMicAnchorPresenter != null) {
                                    mLiveLinkMicAnchorPresenter.onLinkMicAnchorPlayUrl(pkUid, pkPull);
                                }
                            } else {
                                if (mLivePlayViewHolder instanceof LivePlayTxViewHolder) {
                                    ((LivePlayTxViewHolder) mLivePlayViewHolder).setAnchorLinkMic(true, 0);
                                }
                            }
                            setPkBgVisible(true);
                        }
                        if (pkInfo.getIntValue("ifpk") == 1 && mLiveLinkMicPkPresenter != null) {//pk?????????
                            mLiveLinkMicPkPresenter.onEnterRoomPkStart(pkUid, pkInfo.getLongValue("pk_gift_liveuid"), pkInfo.getLongValue("pk_gift_pkuid"), pkInfo.getIntValue("pk_time"));
                        }
                    }

                    //????????????
                    mLiveGuardInfo = new LiveGuardInfo();
                    int guardNum = obj.getIntValue("guard_nums");
                    mLiveGuardInfo.setGuardNum(guardNum);
                    JSONObject guardObj = obj.getJSONObject("guard");
                    if (guardObj != null) {
                        mLiveGuardInfo.setMyGuardType(guardObj.getIntValue("type"));
                        mLiveGuardInfo.setMyGuardEndTime(guardObj.getString("endtime"));
                    }
                    if (mLiveRoomViewHolder != null) {
                        mLiveRoomViewHolder.setGuardNum(guardNum);
                        //????????????
                        mLiveRoomViewHolder.setRedPackBtnVisible(obj.getIntValue("isred") == 1);
                    }
                    //??????????????????
                    boolean showPan = obj.getIntValue("turntable_switch") == 1;
                    //????????????
                    int giftPrizePoolLevel = obj.getIntValue("jackpot_level");
                    if (mLiveRoomViewHolder != null) {
                        mLiveRoomViewHolder.showBtn(showPan, giftPrizePoolLevel);
                    }

                    //???????????????
                    JSONObject showGoodsInfo = obj.getJSONObject("show_goods");
                    String goodsId = showGoodsInfo.getString("goodsid");
                    if (!"0".equals(goodsId)) {
                        GoodsBean goodsBean = new GoodsBean();
                        goodsBean.setId(goodsId);
                        goodsBean.setThumb(showGoodsInfo.getString("goods_thumb"));
                        goodsBean.setName(showGoodsInfo.getString("goods_name"));
                        goodsBean.setPriceNow(showGoodsInfo.getString("goods_price"));
                        goodsBean.setType(showGoodsInfo.getIntValue("goods_type"));
                        if (mLiveRoomViewHolder != null) {
                            mLiveRoomViewHolder.setShowGoodsBean(goodsBean);
                        }
                    }

                    //????????????
                    if (CommonAppConfig.GAME_ENABLE && mLiveRoomViewHolder != null) {
                        GameParam param = new GameParam();
                        param.setContext(mContext);
                        param.setParentView(mContainerWrap);
                        param.setTopView(mContainer);
                        param.setInnerContainer(mLiveRoomViewHolder.getInnerContainer());
                        param.setGameActionListener(new GameActionListenerImpl(LiveAudienceActivity.this, mSocketClient));
                        param.setLiveUid(mLiveUid);
                        param.setStream(mStream);
                        param.setAnchor(false);
                        param.setCoinName(CommonAppConfig.getInstance().getScoreName());
                        param.setObj(obj);
                        if (mGamePresenter == null) {
                            mGamePresenter = new GamePresenter();
                        }
                        mGamePresenter.setGameParam(param);
                    }
                }
            }
        });
    }

    /**
     * ??????????????????
     */
    public void openGiftWindow() {
        if (TextUtils.isEmpty(mLiveUid) || TextUtils.isEmpty(mStream)) {
            return;
        }
        LiveGiftDialogFragment fragment = new LiveGiftDialogFragment();
        fragment.setLifeCycleListener(this);
        fragment.setLiveGuardInfo(mLiveGuardInfo);
        Bundle bundle = new Bundle();
        bundle.putString(Constants.LIVE_UID, mLiveUid);
        bundle.putString(Constants.LIVE_STREAM, mStream);
        fragment.setArguments(bundle);
        fragment.show(((LiveAudienceActivity) mContext).getSupportFragmentManager(), "LiveGiftDialogFragment");
    }

    /**
     * ????????????
     */
    private void endPlay() {
        if (mEnd) {
            return;
        }
        mEnd = true;
        //??????socket
        if (mSocketClient != null) {
            mSocketClient.disConnect();
        }
        mSocketClient = null;
        //????????????
        if (mLivePlayViewHolder != null) {
            mLivePlayViewHolder.release();
        }
        mLivePlayViewHolder = null;
        release();
    }

    @Override
    protected void release() {
        if (mSocketClient != null) {
            mSocketClient.disConnect();
        }
        if (mPayPresenter != null) {
            mPayPresenter.release();
        }
        mPayPresenter = null;
        LiveHttpUtil.cancel(LiveHttpConsts.CHECK_LIVE);
        LiveHttpUtil.cancel(LiveHttpConsts.ENTER_ROOM);
        LiveHttpUtil.cancel(LiveHttpConsts.ROOM_CHARGE);
        CommonHttpUtil.cancel(CommonHttpConsts.GET_BALANCE);
        super.release();
        if (mRoomScrollAdapter != null) {
            mRoomScrollAdapter.release();
        }
        mRoomScrollAdapter = null;
    }

    /**
     * ??????????????????????????????
     */
    @Override
    public void onLiveEnd() {
        super.onLiveEnd();
        endPlay();
        if (mViewPager != null) {
            if (mViewPager.getCurrentItem() != 1) {
                mViewPager.setCurrentItem(1, false);
            }
            mViewPager.setCanScroll(false);
        }
        if (mLiveEndViewHolder == null) {
            mLiveEndViewHolder = new LiveEndViewHolder(mContext, mSecondPage);
            mLiveEndViewHolder.subscribeActivityLifeCycle();
            mLiveEndViewHolder.addToParent();
        }
        mLiveEndViewHolder.showData(mLiveBean, mStream);
        if (isUseScroll()) {
            if (mRecyclerView != null) {
                mRecyclerView.setLayoutFrozen(true);
            }
        }
    }


    /**
     * ????????????????????????
     */
    @Override
    public void onKick(String touid) {
        if (!TextUtils.isEmpty(touid) && touid.equals(CommonAppConfig.getInstance().getUid())) {//??????????????????
            exitLiveRoom();
            ToastUtil.show(WordUtil.getString(R.string.live_kicked_2));
        }
    }

    /**
     * ????????????????????????
     */
    @Override
    public void onShutUp(String touid, String content) {
        if (!TextUtils.isEmpty(touid) && touid.equals(CommonAppConfig.getInstance().getUid())) {
            DialogUitl.showSimpleTipDialog(mContext, content);
        }
    }

    @Override
    public void onBackPressed() {
        if (!mEnd && !canBackPressed()) {
            return;
        }
        exitLiveRoom();
    }

    /**
     * ???????????????
     */
    public void exitLiveRoom() {
        endPlay();
        super.onBackPressed();
    }


    @Override
    protected void onDestroy() {
        if (mLiveAudienceViewHolder != null) {
            mLiveAudienceViewHolder.clearAnim();
        }
        super.onDestroy();
        L.e("LiveAudienceActivity-------onDestroy------->");
    }

    /**
     * ??????
     */
    public void light() {
        if (!mLighted) {
            mLighted = true;
            int guardType = mLiveGuardInfo != null ? mLiveGuardInfo.getMyGuardType() : Constants.GUARD_TYPE_NONE;
            SocketChatUtil.sendLightMessage(mSocketClient, 1 + RandomUtil.nextInt(6), guardType);
        }
        if (mLiveRoomViewHolder != null) {
            mLiveRoomViewHolder.playLightAnim();
        }
    }


    /**
     * ?????????????????????????????????
     */
    public void roomChargeUpdateVotes() {
        sendUpdateVotesMessage(mLiveTypeVal);
    }

    /**
     * ????????????
     */
    public void pausePlay() {
        if (mLivePlayViewHolder != null) {
            mLivePlayViewHolder.pausePlay();
        }
    }

    /**
     * ????????????
     */
    public void resumePlay() {
        if (mLivePlayViewHolder != null) {
            mLivePlayViewHolder.resumePlay();
        }
    }

    /**
     * ????????????
     */
    public void onChargeSuccess() {
        if (mLiveType == Constants.LIVE_TYPE_TIME) {
            if (mCoinNotEnough) {
                mCoinNotEnough = false;
                LiveHttpUtil.roomCharge(mLiveUid, mStream, new HttpCallback() {
                    @Override
                    public void onSuccess(int code, String msg, String[] info) {
                        if (code == 0) {
                            roomChargeUpdateVotes();
                            if (mLiveRoomViewHolder != null) {
                                resumePlay();
                                mLiveRoomViewHolder.startRequestTimeCharge();
                            }
                        } else {
                            if (code == 1008) {//????????????
                                mCoinNotEnough = true;
                                DialogUitl.showSimpleDialog(mContext, WordUtil.getString(R.string.live_coin_not_enough), false,
                                        new DialogUitl.SimpleCallback2() {
                                            @Override
                                            public void onConfirmClick(Dialog dialog, String content) {
                                                RouteUtil.forwardMyCoin(mContext);
                                            }

                                            @Override
                                            public void onCancelClick() {
                                                exitLiveRoom();
                                            }
                                        });
                            }
                        }
                    }
                });
            }
        }
    }

    public void setCoinNotEnough(boolean coinNotEnough) {
        mCoinNotEnough = coinNotEnough;
    }

    /**
     * ????????????????????????
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onGameWindowChangedEvent(GameWindowChangedEvent e) {
        if (mLiveRoomViewHolder != null) {
            mLiveRoomViewHolder.setOffsetY(e.getGameViewHeight());
        }
    }

    /**
     * ??????????????????
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onOpenGameChargeEvent(OpenGameChargeEvent e) {
        openChargeWindow();
    }

    /**
     * ??????sdk??????????????????????????????
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onLinkMicTxAccEvent(LinkMicTxAccEvent e) {
        if (mLivePlayViewHolder != null && mLivePlayViewHolder instanceof LivePlayTxViewHolder) {
            ((LivePlayTxViewHolder) mLivePlayViewHolder).onLinkMicTxAccEvent(e.isLinkMic());
        }
    }

    /**
     * ??????sdk????????????????????????
     *
     * @param linkMic true???????????? false????????????
     */
    public void onLinkMicTxAnchor(boolean linkMic) {
        if (mLivePlayViewHolder != null && mLivePlayViewHolder instanceof LivePlayTxViewHolder) {
            ((LivePlayTxViewHolder) mLivePlayViewHolder).setAnchorLinkMic(linkMic, 5000);
        }
    }


    /**
     * ??????????????????
     */
    public void openChargeWindow() {
        if (mPayPresenter == null) {
            mPayPresenter = new PayPresenter(this);
            mPayPresenter.setServiceNameAli(Constants.PAY_BUY_COIN_ALI);
            mPayPresenter.setServiceNameWx(Constants.PAY_BUY_COIN_WX);
            mPayPresenter.setAliCallbackUrl(HtmlConfig.ALI_PAY_COIN_URL);
            mPayPresenter.setPayCallback(new PayCallback() {
                @Override
                public void onSuccess() {
                    if (mPayPresenter != null) {
                        mPayPresenter.checkPayResult();
                    }
                }

                @Override
                public void onFailed() {

                }
            });
        }
        LiveChargeDialogFragment fragment = new LiveChargeDialogFragment();
        fragment.setLifeCycleListener(this);
        fragment.setPayPresenter(mPayPresenter);
        fragment.show(getSupportFragmentManager(), "ChatChargeDialogFragment");
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onLiveRoomChangeEvent(LiveRoomChangeEvent e) {
        LiveBean liveBean = e.getLiveBean();
        if (liveBean != null) {
            LiveHttpUtil.cancel(LiveHttpConsts.CHECK_LIVE);
            LiveHttpUtil.cancel(LiveHttpConsts.ENTER_ROOM);
            LiveHttpUtil.cancel(LiveHttpConsts.ROOM_CHARGE);
            clearRoomData();

            setLiveRoomData(liveBean);
            mLiveType = e.getLiveType();
            mLiveTypeVal = e.getLiveTypeVal();
            enterRoom();
        }
    }

    /**
     * ??????????????????
     */
    public void openGoodsWindow() {
        SocketChatUtil.liveGoodsFloat(mSocketClient);
        LiveGoodsDialogFragment fragment = new LiveGoodsDialogFragment();
        fragment.setLifeCycleListener(this);
        Bundle bundle = new Bundle();
        bundle.putString(Constants.LIVE_UID, mLiveUid);
        fragment.setArguments(bundle);
        fragment.show(getSupportFragmentManager(), "LiveGoodsDialogFragment");
    }

    public void liveGoodsFloat() {
        SocketChatUtil.liveGoodsFloat(mSocketClient);
    }

}
