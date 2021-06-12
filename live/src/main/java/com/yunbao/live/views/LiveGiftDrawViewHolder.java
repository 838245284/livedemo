package com.yunbao.live.views;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.Color;
import android.os.Handler;
import android.os.Message;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Interpolator;
import android.widget.ImageView;
import android.widget.TextView;

import com.yunbao.common.CommonAppConfig;
import com.yunbao.common.bean.LevelBean;
import com.yunbao.common.glide.ImgLoader;
import com.yunbao.common.utils.DpUtil;
import com.yunbao.common.utils.WordUtil;
import com.yunbao.common.views.AbsViewHolder;
import com.yunbao.live.R;
import com.yunbao.live.bean.LiveReceiveGiftBean;

/**
 * 手绘礼物
 */

public class LiveGiftDrawViewHolder extends AbsViewHolder {

    private View mRoot;
    private View mBg;
    private View mStar;
    private ImageView mAvatar;
    private TextView mName;
    private TextView mContent;
    //    private ImageView mGiftIcon;
//    private TextView mGiftCount;
    private TextView mGiftGroupCount;
    //    private TextView mMulSign;//乘号
    private int mDp214;
    private ObjectAnimator mAnimator;
    //    private Animation mAnimation;//礼物数字执行的放大动画
//    private boolean mIdle;//是否空闲
    private boolean mShowed;//展示礼物的控件是否显示出来了
    //    private String mLastGiftKey;//上次送礼物的信息
    private int mLianCount;//连送礼物的个数
    private Handler mHandler;


    public LiveGiftDrawViewHolder(Context context, ViewGroup parentView) {
        super(context, parentView);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.view_live_gift_draw;
    }

    @Override
    public void init() {
        mRoot = findViewById(R.id.root);
        mBg = findViewById(R.id.bg);
        mStar = findViewById(R.id.star);
        mAvatar = (ImageView) findViewById(R.id.avatar);
        mName = (TextView) findViewById(R.id.name);
        mContent = (TextView) findViewById(R.id.content);
//        mGiftIcon = (ImageView) findViewById(R.id.gift_icon);
//        mGiftCount = (TextView) findViewById(R.id.gift_count);
        mGiftGroupCount = (TextView) findViewById(R.id.gift_group_count);
//        mMulSign = (TextView) findViewById(R.id.mul_sign);
        mDp214 = DpUtil.dp2px(214);
        mHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                if (mBg != null) {
                    mBg.setTranslationX(-mDp214);
                }
                if (mStar != null && mStar.getVisibility() == View.VISIBLE) {
                    mStar.setVisibility(View.INVISIBLE);
                }
//                if (mGiftCount != null && mGiftCount.getVisibility() != View.VISIBLE) {
//                    mGiftCount.setVisibility(View.VISIBLE);
//                    mGiftCount.setText(renderGiftCount(mLianCount));
//                }
//                if (mGiftCount != null) {
//                    mGiftCount.clearAnimation();
//                    if (mAnimation != null) {
//                        mGiftCount.startAnimation(mAnimation);
//                    }
//                }
            }
        };
        Interpolator interpolator = new AccelerateDecelerateInterpolator();
        mAnimator = ObjectAnimator.ofFloat(mBg, "translationX", 0);
        mAnimator.setDuration(300);
        mAnimator.setInterpolator(interpolator);
        mAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                if (mStar != null && mStar.getVisibility() != View.VISIBLE) {
                    mStar.setVisibility(View.VISIBLE);
                }
                if (mHandler != null) {
                    mHandler.sendEmptyMessageDelayed(0, 200);
                }
            }
        });
//        mAnimation = new ScaleAnimation(1.5f, 0.7f, 1.5f, 0.7f, Animation.RELATIVE_TO_SELF, 0, Animation.RELATIVE_TO_SELF, 1f);
//        mAnimation.setDuration(200);
//        mAnimation.setInterpolator(interpolator);
//        mIdle = true;
    }


    /**
     * 显示礼物动画
     */
    public void show(LiveReceiveGiftBean bean) {
//        mIdle = false;
//        boolean lian = true;
        if (!mShowed) {
            mShowed = true;
            if (mRoot != null && mRoot.getVisibility() != View.VISIBLE) {
                mRoot.setVisibility(View.VISIBLE);
            }
        }
//        if (!isSameUser) {
        ImgLoader.displayAvatar(mContext, bean.getAvatar(), mAvatar);
        LevelBean levelBean = CommonAppConfig.getInstance().getLevel(bean.getLevel());
        if (levelBean != null) {
            mName.setTextColor(Color.parseColor(levelBean.getColor()));
        } else {
            mName.setTextColor(0xffffffff);
        }
        mName.setText(bean.getUserNiceName());
//            lian = false;
//        }
//        if (TextUtils.isEmpty(mLastGiftKey) || !mLastGiftKey.equals(bean.getKey())) {
//            ImgLoader.display(mContext, bean.getGiftIcon(), mGiftIcon);
        mContent.setText(renderGiftInfo2(bean.getGiftName()));
//            if (bean.getGiftCount() > 1) {
        mGiftGroupCount.setText("x" + bean.getGiftCount());
//                mMulSign.setText(R.string.live_gift_send_lian_3);
//            } else {
//                mGiftGroupCount.setText("");
//                mMulSign.setText(R.string.live_gift_send_lian_2);
//            }
//            lian = false;
//            if (mGiftCount != null && mGiftCount.getVisibility() == View.VISIBLE) {
//                mGiftCount.setVisibility(View.INVISIBLE);
//            }
        if (mAnimator != null) {
            mAnimator.start();
        }
//        }
//        if (lian) {
//            mLianCount++;
//        } else {
//            mLianCount = bean.getLianCount();
//        }
//        if (mGiftCount != null && mGiftCount.getVisibility() == View.VISIBLE) {
//            mGiftCount.setText(renderGiftCount(mLianCount));
//        }
//        mLastGiftKey = bean.getKey();
//        if (lian && mGiftCount != null && mAnimation != null) {
//            mGiftCount.startAnimation(mAnimation);
//        }
    }


    public void hideNormal() {
        if (mBg != null) {
            mBg.setTranslationX(-mDp214);
        }
//        if (mGiftCount != null && mGiftCount.getVisibility() == View.VISIBLE) {
//            mGiftCount.setVisibility(View.INVISIBLE);
//        }
        if (mRoot != null && mRoot.getVisibility() == View.VISIBLE) {
            mRoot.setVisibility(View.INVISIBLE);
        }
        mAvatar.setImageDrawable(null);
//        mGiftIcon.setImageDrawable(null);
//        mIdle = true;
        mShowed = false;
        mLianCount = 0;
//        mLastGiftKey = null;

    }

    public void hide() {
        if (mBg != null) {
            mBg.setTranslationX(-mDp214);
        }
//        if (mGiftCount != null && mGiftCount.getVisibility() == View.VISIBLE) {
//            mGiftCount.setVisibility(View.INVISIBLE);
//        }
        if (mRoot != null && mRoot.getVisibility() == View.VISIBLE) {
            mRoot.setVisibility(View.INVISIBLE);
        }
        mAvatar.setImageDrawable(null);
//        mGiftIcon.setImageDrawable(null);
//        mIdle = true;
        mShowed = false;
        mLianCount = 0;
//        mLastGiftKey = null;

    }

    /**
     * 是否是空闲的
     */
    public boolean isIdle() {
//        return mIdle ;
        return false;
    }

    /**
     * 是否是同一种礼物，人的uid,礼物的id，礼物的个数都相同
     */
    public boolean isSameGift(LiveReceiveGiftBean bean) {
//        return !TextUtils.isEmpty(mLastGiftKey) && mLastGiftKey.equals(bean.getKey())
//        ;
        return false;
    }


    public void cancelAnimAndHide() {
        cancelAnim();
        hide();
    }

    private void cancelAnim() {
        if (mHandler != null) {
            mHandler.removeCallbacksAndMessages(null);
        }
        if (mAnimator != null) {
            mAnimator.cancel();
        }
//        if (mGiftCount != null) {
//            mGiftCount.clearAnimation();
//        }

    }

    private static SpannableStringBuilder renderGiftInfo2(String giftName) {
        SpannableStringBuilder builder = new SpannableStringBuilder();
        String s1 = WordUtil.getString(R.string.live_send_gift_1);
        String content = s1 + " " + giftName;
        int index1 = s1.length();
        builder.append(content);
        builder.setSpan(new ForegroundColorSpan(0xffff6131), index1, content.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        return builder;
    }

    public void release() {
        cancelAnim();
        mContext = null;
        mParentView = null;
//        mLastGiftKey = null;
        mHandler = null;

    }
}
