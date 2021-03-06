package com.myylook.mall.activity;

import android.content.Intent;
import android.support.annotation.Nullable;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.style.AbsoluteSizeSpan;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.myylook.common.CommonAppConfig;
import com.myylook.common.Constants;
import com.myylook.common.activity.AbsActivity;
import com.myylook.common.bean.ConfigBean;
import com.myylook.common.glide.ImgLoader;
import com.myylook.common.http.HttpCallback;
import com.myylook.common.utils.DialogUitl;
import com.myylook.common.utils.RouteUtil;
import com.myylook.common.utils.StringUtil;
import com.myylook.im.activity.ChatActivity;
import com.myylook.im.event.ImUnReadCountEvent;
import com.myylook.im.utils.ImMessageUtil;
import com.myylook.mall.R;
import com.myylook.mall.http.MallHttpConsts;
import com.myylook.mall.http.MallHttpUtil;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

/**
 * 卖家页面
 */
@Route(path = RouteUtil.PATH_MALL_SELLER)
public class SellerActivity extends AbsActivity {

    private ImageView mAvatar;
    private TextView mName;
    private TextView mGoodsDes;//卖家描述
    private TextView mScore;//综合分
    private TextView mAmount;//账户金额
    private TextView mIncome;//累计收入
    private TextView mPay;//待付款
    private TextView mSend;//待发货
    private TextView mRefund;//退款

    private TextView mCountPay;//待付款
    private TextView mCountSend;//待发货
    private TextView mCountRefund;//退款

    private String mCertText;
    private String mCertImgUrl;

    private TextView mRedPoint;//显示未读消息数量的红点

    @Override
    protected boolean isStatusBarWhite() {
        return true;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_seller;
    }

    @Override
    protected void main() {
        ConfigBean configBean = CommonAppConfig.getInstance().getConfig();
        if (configBean != null) {
            String shopName = configBean.getShopSystemName();
            TextView title = findViewById(R.id.title);
            title.setText(shopName);
        }
        mAvatar = findViewById(R.id.avatar);
        mName = findViewById(R.id.name);
        mGoodsDes = findViewById(R.id.goods_des);
        mScore = findViewById(R.id.score);
        mAmount = findViewById(R.id.amount);
        mIncome = findViewById(R.id.income);
        mPay = findViewById(R.id.pay);
        mSend = findViewById(R.id.send);
        mRefund = findViewById(R.id.refund);
        mCountPay = findViewById(R.id.count_pay);
        mCountSend = findViewById(R.id.count_send);
        mCountRefund = findViewById(R.id.count_refund);
        mRedPoint = (TextView) findViewById(R.id.red_point);
        String unReadCount = ImMessageUtil.getInstance().getAllUnReadMsgCount();
        setUnReadCount(unReadCount);
        EventBus.getDefault().register(this);

    }

    @Override
    protected void onResume() {
        super.onResume();
        getData();
    }

    private void getData() {
        MallHttpUtil.getSellerHome(new HttpCallback() {
            @Override
            public void onSuccess(int code, String msg, String[] info) {
                if (code == 0 && info.length > 0) {
                    JSONObject obj = JSON.parseObject(info[0]);
                    JSONObject shopInfo = obj.getJSONObject("shop_info");
                    JSONObject balanceInfo = obj.getJSONObject("balance_info");
                    JSONObject orderInfo = obj.getJSONObject("order_info");
                    if (mAvatar != null) {
                        ImgLoader.display(mContext, shopInfo.getString("avatar"), mAvatar);
                    }
                    if (mName != null) {
                        mName.setText(shopInfo.getString("name"));
                    }
                    if (mGoodsDes != null) {
                        mGoodsDes.setText(obj.getString("seller_desc"));
                    }
                    if (mScore != null) {
                        mScore.setText(shopInfo.getString("composite_points"));
                    }
                    if (mAmount != null) {
                        mAmount.setText(renderBalanceText(balanceInfo.getString("balance")));
                    }
                    if (mIncome != null) {
                        mIncome.setText(renderBalanceText(balanceInfo.getString("balance_total")));
                    }
                    if (mPay != null) {
                        mPay.setText(orderInfo.getString("wait_payment"));
                    }
                    if (mSend != null) {
                        mSend.setText(orderInfo.getString("wait_shipment"));
                    }
                    if (mRefund != null) {
                        mRefund.setText(orderInfo.getString("wait_refund"));
                    }
                    mCertText = shopInfo.getString("certificate_desc");
                    mCertImgUrl = shopInfo.getString("certificate");
                }
            }
        });
    }

    public void onSellerClick(View v) {
        int i = v.getId();
        if (i == R.id.btn_buyer) {
            forwardBuyer();
        } else if (i == R.id.btn_name) {
            shopDetail();
        } else if (i == R.id.btn_amount) {//账户余额
            SellerAccountActivity.forward(mContext);
        } else if (i == R.id.btn_income) {//累计收入
            SellerAccountActivity.forward(mContext);
        } else if (i == R.id.btn_order_manage) {//订单管理
            SellerOrderActivity.forward(mContext, 9);
        } else if (i == R.id.btn_pay) {//待付款
            SellerOrderActivity.forward(mContext, 2);
        } else if (i == R.id.btn_send) {//待发货
            SellerOrderActivity.forward(mContext, 0);
        } else if (i == R.id.btn_refund) {//退款
            SellerOrderActivity.forward(mContext, 1);
        } else if (i == R.id.btn_add_goods) {//添加商品
            addGoods();
        } else if (i == R.id.btn_manage_goods) {//商品管理
            SellerManageGoodsActivity.forward(mContext);
        } else if (i == R.id.btn_address) {//地址管理
            SellerAddressActivity.forward(mContext);
        } else if (i == R.id.btn_msg) {//消息
            ChatActivity.forward(mContext);
        } else if (i == R.id.btn_manage_class) {//经营类目设置
            SetManageClassActivity.forward(mContext);
        }
    }

    private void forwardBuyer() {
        Intent intent = new Intent(mContext, BuyerActivity.class);
        intent.putExtra(Constants.MALL_GOODS_FROM_SHOP, true);
        startActivityForResult(intent, 100);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 100 && resultCode == RESULT_OK) {
            finish();
        }
    }

    private void addGoods() {
        DialogUitl.showStringArrayDialog(mContext, new Integer[]{
                R.string.mall_406, R.string.mall_374, R.string.mall_375,
        }, new DialogUitl.StringArrayDialogCallback() {
            @Override
            public void onItemClick(String text, int tag) {
                if (tag == R.string.mall_374) {
                    SellerAddGoodsActivity.forward(mContext, null);
                } else if (tag == R.string.mall_375) {
                    RouteUtil.forwardGoodsOutSide(null);
                } else if (tag == R.string.mall_406) {
                    SellerAddPlatActivity.forward(mContext);
                }
            }
        });

    }

    /**
     * 小店详情
     */
    private void shopDetail() {
        ShopDetailActivity.forward(mContext, mCertText, mCertImgUrl);
    }


    private CharSequence renderBalanceText(String text) {
        if (TextUtils.isEmpty(text)) {
            return text;
        }
        if (!text.contains(".")) {
            text = StringUtil.contact(text, ".00");
        }
        SpannableStringBuilder builder = new SpannableStringBuilder(text);
        builder.setSpan(new AbsoluteSizeSpan(12, true), text.indexOf("."), builder.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        return builder;
    }

    @Override
    protected void onDestroy() {
        EventBus.getDefault().unregister(this);
        MallHttpUtil.cancel(MallHttpConsts.GET_SELLER_HOME);
        super.onDestroy();
    }


    /**
     * 显示未读消息
     */
    private void setUnReadCount(String unReadCount) {
        if (mRedPoint != null) {
            if ("0".equals(unReadCount)) {
                if (mRedPoint.getVisibility() == View.VISIBLE) {
                    mRedPoint.setVisibility(View.INVISIBLE);
                }
            } else {
                if (mRedPoint.getVisibility() != View.VISIBLE) {
                    mRedPoint.setVisibility(View.VISIBLE);
                }
                mRedPoint.setText(unReadCount);
            }
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onImUnReadCountEvent(ImUnReadCountEvent e) {
        String unReadCount = e.getUnReadCount();
        if (!TextUtils.isEmpty(unReadCount)) {
            setUnReadCount(unReadCount);
        }
    }

}
